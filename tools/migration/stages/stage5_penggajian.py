"""Stage 5: Historical Payroll (Penggajian) Migration Runner.

Complies with ADR-0050: Rekonsiliasi Gap Pengkodean Komponen Gaji via Translation Map dan Passthrough Historis.
- Supports windowing parameter (default: last 12 months, or full history if payroll_all is True).
- Migrates batch headers:
    * smartoffice.salary_batch_process -> kepegawaian_dev_new.gaji_batch_root
    * smartoffice.salary_process_master -> kepegawaian_dev_new.gaji_batch_master
- Migrates process component details:
    * smartoffice.salary_process_detail -> kepegawaian_dev_new.gaji_batch_master_proses
- Converts ctype:
    * '+' -> 'PEMASUKAN'
    * '-' -> 'POTONGAN'
    * other / null -> 'NONE'
- Standardizes component codes via centralized COMPONENT_CODE_MAP.
- Safe Passthrough Mechanism:
    * Incidental/ad-hoc components are preserved with their original code and name
      without strict foreign key checks to gaji_komponen.
- Baseline Envers revision injection for gaji_batch_root_aud if present.
"""

from __future__ import annotations

import logging
from datetime import date, datetime
from typing import Any, Sequence

from tools.migration.core.db import (
    batch_insert,
    batch_upsert,
    execute_query,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.envers import (
    REVTYPE_ADD,
    create_revision,
    get_latest_revision,
    snapshot_table_to_audit,
)
from tools.migration.core.state import (
    batch_set_mappings,
    get_all_mappings,
    init_state_table,
)

logger = logging.getLogger(__name__)

DOMAIN_NAME = "penggajian"

# Central translation map for common legacy components to target standard codes
COMPONENT_CODE_MAP: dict[str, str] = {
    # Basic / Income Components
    "gp": "GP",
    "gaji_pokok": "GP",
    "t_si": "TUNJ_SI",
    "tunj_si": "TUNJ_SI",
    "t_anak": "TUNJ_ANAK",
    "tunj_anak": "TUNJ_ANAK",
    "t_jab": "TUNJ_JABATAN",
    "t_jabatan": "TUNJ_JABATAN",
    "tunj_jab": "TUNJ_JABATAN",
    "tunj_jabatan": "TUNJ_JABATAN",
    "t_beras": "TUNJ_BERAS",
    "tunj_beras": "TUNJ_BERAS",
    "t_kk": "TUNJ_KK",
    "tunj_kk": "TUNJ_KK",
    "t_air": "TUNJ_AIR",
    "tunj_air": "TUNJ_AIR",
    "t_kes": "TUNJ_KESEHATAN",
    "t_kesehatan": "TUNJ_KESEHATAN",
    "tunj_kesehatan": "TUNJ_KESEHATAN",
    "t_kpi": "TUNJ_KPI",
    "tunj_kpi": "TUNJ_KPI",
    "tunkin": "TUNJ_KINERJA",
    "tunj_kinerja": "TUNJ_KINERJA",
    "t_jkn": "TUNJ_JKN",
    "tunj_jkn": "TUNJ_JKN",
    "t_ter": "TUNJ_TER",
    "tunj_ter": "TUNJ_TER",
    "t_pph21": "TUNJ_PPH21",
    "tunj_pph21": "TUNJ_PPH21",
    "astek": "ASTEK",
    "pkp": "PKP",
    "phdp": "PHDP",
    "pk": "PENGHASILAN_KOTOR",
    "bruto": "PENGHASILAN_KOTOR",
    "penghasilan_kotor": "PENGHASILAN_KOTOR",
    # Deductions
    "p_pen": "POT_PENSIUN",
    "p_pensiun": "POT_PENSIUN",
    "pot_pensiun": "POT_PENSIUN",
    "p_astek": "POT_ASTEK",
    "pot_astek": "POT_ASTEK",
    "p_rudin": "POT_RUDIN",
    "pot_rudin": "POT_RUDIN",
    "p_jp": "POT_JP",
    "pot_jp": "POT_JP",
    "p_askes": "POT_ASKES",
    "pot_askes": "POT_ASKES",
    "p_tkk": "POT_TKK",
    "pot_tkk": "POT_TKK",
    "p_pph21": "POT_PPH21",
    "pot_pph21": "POT_PPH21",
    "potongan": "POTONGAN",
    "total_potongan": "POTONGAN",
    # Net & Final Summaries
    "pb": "PENGHASILAN_BERSIH",
    "netto": "PENGHASILAN_BERSIH",
    "penghasilan_bersih": "PENGHASILAN_BERSIH",
    "pembulatan": "PEMBULATAN",
    "pb_final": "PENGHASILAN_BERSIH_FINAL",
    "final": "PENGHASILAN_BERSIH_FINAL",
    "penghasilan_bersih_final": "PENGHASILAN_BERSIH_FINAL",
    # Reference / Counts
    "jml_anak": "JML_ANAK",
    "jml_jiwa": "JML_JIWA",
}
# Mapping emp_flag legacy -> EStatusPegawai ordinal (sama dengan stage2_pegawai.py)
_STATUS_PEGAWAI_MAP_GAJI: dict[int, int] = {
    1: 2,  # Pegawai Tetap       -> EStatusPegawai.PEGAWAI (ordinal 2)
    2: 0,  # Pegawai Kontrak     -> EStatusPegawai.KONTRAK (ordinal 0)
    3: 5,  # Non Pegawai         -> EStatusPegawai.NON_PEGAWAI (ordinal 5)
    4: 1,  # Calon Pegawai       -> EStatusPegawai.CAPEG (ordinal 1)
    5: 4,  # Honorer Tetap       -> EStatusPegawai.HONORER (ordinal 4)
    6: 3,  # Calon Honorer Tetap -> EStatusPegawai.CALON_HONORER (ordinal 3)
}


def normalize_ctype(ctype_val: Any) -> str:
    """Converts legacy ctype symbol to target EJenisGaji string enum value."""
    if ctype_val == "+":
        return "PEMASUKAN"
    elif ctype_val == "-":
        return "POTONGAN"
    return "NONE"


def normalize_component_code(legacy_code: str | None) -> str:
    """Maps legacy component codes to target standard codes or passes through safely."""
    if not legacy_code:
        return "NONE"
    cleaned = legacy_code.strip().lower()
    if cleaned in COMPONENT_CODE_MAP:
        return COMPONENT_CODE_MAP[cleaned]
    # Safe Passthrough: preserve ad-hoc or incidental component code as-is
    return legacy_code.strip().upper()


def parse_jml_jiwa(val: Any) -> tuple[int, int]:
    """Parses legacy jml_jiwa string (e.g. '1/3' or '0/2') into (jml_tanggungan, jml_jiwa)."""
    if not val:
        return 0, 0
    val_str = str(val).strip()
    if "/" in val_str:
        parts = val_str.split("/", 1)
        try:
            tanggungan = int(parts[0].strip())
        except ValueError:
            tanggungan = 0
        try:
            jiwa = int(parts[1].strip())
        except ValueError:
            jiwa = 0
        return tanggungan, jiwa
    try:
        num = int(val_str)
        return 0, num
    except ValueError:
        return 0, 0


def migrate_salary_batches(
    target_conn: Any,
    legacy_conn: Any,
    pegawai_info: dict[str, dict[str, Any]],
    payroll_all: bool = False,
    months_window: int = 12,
    dry_run: bool = False,
) -> tuple[int, int, int, list[str]]:
    """Migrates salary batches, per-employee master, and detailed component rows.

    Returns:
        tuple of (root_batch_count, master_employee_count, detail_component_count, list_of_batch_root_ids)
    """
    # 1. Fetch batches from smartoffice.salary_batch_process
    where_clauses = ["status < 99"]
    params: list[Any] = []

    if not payroll_all:
        # Default window: last N months (e.g. 12 months from first of current month)
        where_clauses.append("period >= DATE_SUB(DATE_FORMAT(CURDATE(), '%%Y-%%m-01'), INTERVAL %s MONTH)")
        params.append(months_window)

    sql_batches = f"""
    SELECT
        id,
        batch_no,
        period,
        status,
        process_date,
        verified_by_name,
        verified_by_position,
        verification_date,
        approved_by_name,
        approved_by_position,
        approval_date,
        note,
        total_record
    FROM salary_batch_process
    WHERE {' AND '.join(where_clauses)}
    ORDER BY period ASC, batch_no ASC
    """
    legacy_batches = execute_query(legacy_conn, sql_batches, params)
    logger.info(
        "Found %d salary batches to migrate (payroll_all=%s, window=%d months)",
        len(legacy_batches),
        payroll_all,
        months_window,
    )

    if not legacy_batches:
        return 0, 0, 0, []

    root_count = 0
    master_count = 0
    detail_count = 0
    batch_root_ids: list[str] = []

    # Valid master IDs cache
    valid_org_ids = {
        row["id"] for row in execute_query(target_conn, "SELECT id FROM organisasi WHERE is_deleted = 0")
    }

    for b_row in legacy_batches:
        batch_no = str(b_row.get("batch_no") or "").strip()
        if not batch_no:
            continue

        period_val = b_row.get("period")
        if isinstance(period_val, (date, datetime)):
            period = period_val.strftime("%Y%m")
        elif isinstance(period_val, str) and len(period_val) >= 7:
            period = period_val.replace("-", "")[:6]
        else:
            period = str(period_val or "")

        batch_root_ids.append(batch_no)

        # 1. Insert/Upsert into gaji_batch_root
        if not dry_run:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO gaji_batch_root (
                        id, periode, status, total_pegawai,
                        tanggal_proses, di_proses_oleh, jabatan_pemroses,
                        tanggal_verifikasi_tahap1, di_verifikasi_oleh_tahap1, jabatan_verifikasi_tahap1,
                        tanggal_persetujuan, di_setujui_oleh, jabatan_penyetuju,
                        notes, is_deleted, created_at, created_by
                    ) VALUES (
                        %s, %s, %s, %s,
                        %s, 'SYSTEM', 'Admin',
                        %s, %s, %s,
                        %s, %s, %s,
                        %s, 0, NOW(), 'MIGRATION'
                    )
                    ON DUPLICATE KEY UPDATE
                        periode = VALUES(periode),
                        status = VALUES(status),
                        total_pegawai = VALUES(total_pegawai),
                        tanggal_proses = VALUES(tanggal_proses),
                        tanggal_verifikasi_tahap1 = VALUES(tanggal_verifikasi_tahap1),
                        di_verifikasi_oleh_tahap1 = VALUES(di_verifikasi_oleh_tahap1),
                        jabatan_verifikasi_tahap1 = VALUES(jabatan_verifikasi_tahap1),
                        tanggal_persetujuan = VALUES(tanggal_persetujuan),
                        di_setujui_oleh = VALUES(di_setujui_oleh),
                        jabatan_penyetuju = VALUES(jabatan_penyetuju),
                        notes = VALUES(notes)
                    """,
                    (
                        batch_no,
                        period,
                        b_row.get("status") or 0,
                        b_row.get("total_record") or 0,
                        b_row.get("process_date"),
                        b_row.get("verification_date"),
                        b_row.get("verified_by_name") or "",
                        b_row.get("verified_by_position") or "",
                        b_row.get("approval_date"),
                        b_row.get("approved_by_name") or "",
                        b_row.get("approved_by_position") or "",
                        b_row.get("note") or "",
                    ),
                )
        root_count += 1

        # 2. Extract per-employee master for this batch
        sql_master = """
        SELECT *
        FROM salary_process_master
        WHERE batch_code = %s
        ORDER BY id ASC
        """
        legacy_masters = execute_query(legacy_conn, sql_master, (batch_no,))
        logger.debug("Batch %s has %d employee master records", batch_no, len(legacy_masters))

        if not legacy_masters:
            continue

        # 3. Extract process details for this batch in one efficient query
        sql_all_details = """
        SELECT d.id, d.pm_id, d.code, d.description, d.ctype, d.value, d.seq, d.formula, d.v_formula
        FROM salary_process_detail d
        JOIN salary_process_master m ON d.pm_id = m.id
        WHERE m.batch_code = %s
        ORDER BY d.pm_id ASC, d.ctype ASC, d.seq ASC
        """
        legacy_details_rows = execute_query(legacy_conn, sql_all_details, (batch_no,))
        details_by_pm: dict[int, list[dict[str, Any]]] = {}
        for d in legacy_details_rows:
            details_by_pm.setdefault(d["pm_id"], []).append(d)

        # 4. Clean existing data for idempotency if re-running
        if not dry_run:
            existing_target_masters = execute_query(
                target_conn,
                "SELECT id FROM gaji_batch_master WHERE batch_root_id = %s",
                (batch_no,),
            )
            if existing_target_masters:
                existing_m_ids = [r["id"] for r in existing_target_masters]
                for i in range(0, len(existing_m_ids), 500):
                    chunk_m_ids = existing_m_ids[i : i + 500]
                    placeholders = ", ".join(["%s"] * len(chunk_m_ids))
                    with target_conn.cursor() as cursor:
                        cursor.execute(
                            f"DELETE FROM gaji_batch_master_proses WHERE batch_master_id IN ({placeholders})",
                            chunk_m_ids,
                        )
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        "DELETE FROM gaji_batch_master WHERE batch_root_id = %s",
                        (batch_no,),
                    )

        # 5. Insert master records and collect details
        batch_detail_records: list[dict[str, Any]] = []

        for m_row in legacy_masters:
            legacy_pm_id = m_row.get("id")
            emp_code = str(m_row.get("emp_code") or "").strip()
            peg_info = pegawai_info.get(emp_code, {})
            pegawai_id = peg_info.get("id")

            org_id = m_row.get("org_group_id")
            if org_id not in valid_org_ids:
                org_id = None

            tanggungan, jiwa = parse_jml_jiwa(m_row.get("jml_jiwa"))

            emp_details = details_by_pm.get(legacy_pm_id, [])
            gp_val = 0.0
            phdp_val = 0.0
            for d in emp_details:
                c = str(d.get("code") or "").strip().lower()
                if c in ("gp", "gaji_pokok"):
                    gp_val = float(d.get("value") or 0.0)
                elif c == "phdp":
                    phdp_val = float(d.get("value") or 0.0)

            sk = peg_info.get("status_kawin")
            if sk is None:
                tax_code = str(m_row.get("tax_code") or "").strip().upper()
                sk = 1 if tax_code.startswith("K") else 0

            sp_raw = m_row.get("emp_flag")
            if sp_raw is not None:
                # Peta emp_flag legacy ke ordinal EStatusPegawai Java (bukan passthrough langsung)
                sp = _STATUS_PEGAWAI_MAP_GAJI.get(sp_raw, peg_info.get("status_pegawai", 0))
            else:
                sp = peg_info.get("status_pegawai", 0)

            target_master_id = None
            if not dry_run:
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        """
                        INSERT INTO gaji_batch_master (
                            batch_root_id, periode, pegawai_id, nipam, nama,
                            jabatan_id, nama_jabatan, level_id,
                            organisasi_id, nama_organisasi,
                            golongan_id, golongan, pangkat,
                            status_pegawai, kode_pajak,
                            gaji_pokok, phdp, status_kawin,
                            jml_tanggungan, jml_jiwa,
                            penghasilan_kotor, total_potongan,
                            total_add_tambahan, total_add_potongan,
                            penghasilan_bersih, penghasilan_bersih2,
                            pembulatan, pembulatan2,
                            penghasilan_bersih_final, penghasilan_bersih_final2,
                            pajak, is_different
                        ) VALUES (
                            %s, %s, %s, %s, %s,
                            %s, %s, %s,
                            %s, %s,
                            %s, %s, %s,
                            %s, %s,
                            %s, %s, %s,
                            %s, %s,
                            %s, %s,
                            %s, %s,
                            %s, %s,
                            %s, %s,
                            %s, %s,
                            %s, 0
                        )
                        """,
                        (
                            batch_no,
                            period,
                            pegawai_id,
                            emp_code,
                            m_row.get("emp_name") or "",
                            peg_info.get("jabatan_id"),
                            m_row.get("emp_position") or "",
                            m_row.get("emp_pos_level"),
                            org_id,
                            m_row.get("org_group_name") or "",
                            peg_info.get("golongan_id"),
                            m_row.get("emp_golongan") or "",
                            m_row.get("emp_pangkat") or "",
                            sp,
                            m_row.get("tax_code") or "",
                            gp_val,
                            phdp_val,
                            sk,
                            tanggungan,
                            jiwa,
                            float(m_row.get("total_income") or 0.0),
                            float(m_row.get("total_deduction") or 0.0),
                            float(m_row.get("total_adhoc_income") or 0.0),
                            float(m_row.get("total_adhoc_deduction") or 0.0),
                            float(m_row.get("nett_income") or 0.0),
                            float(m_row.get("nett_income_2") or 0.0),
                            float(m_row.get("rounding") or 0.0),
                            float(m_row.get("rounding_2") or 0.0),
                            float(m_row.get("final_income") or 0.0),
                            float(m_row.get("final_income_2") or 0.0),
                            float(m_row.get("tax") or 0.0),
                        ),
                    )
                    target_master_id = cursor.lastrowid
            master_count += 1

            for d_row in emp_details:
                raw_code = str(d_row.get("code") or "").strip()
                normalized_code = normalize_component_code(raw_code)
                jenis_gaji = normalize_ctype(d_row.get("ctype"))
                val = float(d_row.get("value") or 0.0)

                batch_detail_records.append({
                    "batch_master_id": target_master_id or 0,
                    "kode": normalized_code,
                    "nama": d_row.get("description") or raw_code,
                    "jenis_gaji": jenis_gaji,
                    "nilai": val,
                    "formula": d_row.get("formula") or "",
                    "nilai_formula": str(d_row.get("v_formula") or ""),
                    "urut": int(d_row.get("seq") or 0),
                })

        if not dry_run and batch_detail_records:
            batch_insert(target_conn, "gaji_batch_master_proses", batch_detail_records, chunk_size=1000)
            target_conn.commit()

        detail_count += len(batch_detail_records)

    logger.info(
        "Migrated %d batch roots, %d batch master employees, and %d component details",
        root_count,
        master_count,
        detail_count,
    )
    return root_count, master_count, detail_count, batch_root_ids


def run_stage5_penggajian(
    target_conn: Any | None = None,
    legacy_conn: Any | None = None,
    payroll_all: bool = False,
    months_window: int = 12,
    dry_run: bool = False,
    console: Any | None = None,
) -> dict[str, Any]:
    """Entrypoint for Stage 5: Historical Payroll (Penggajian) Migration.

    Args:
        target_conn: Connection to kepegawaian_dev_new database. If None, acquires one.
        legacy_conn: Connection to smartoffice database. If None, acquires one.
        payroll_all: If True, migrates all historical payroll years. Default: False (12 months).
        months_window: Number of months to migrate when payroll_all is False. Default: 12.
        dry_run: If True, parses without writing to target database.
        console: Optional rich console for display.

    Returns:
        Summary metrics dictionary.
    """
    logger.info("============================================================")
    logger.info("Executing Stage 5: Payroll Migration (ADR-0050)")
    logger.info(
        "Configuration: payroll_all=%s, window=%d months, dry_run=%s",
        payroll_all,
        months_window,
        dry_run,
    )
    logger.info("============================================================")

    if target_conn is None:
        with get_target_connection(autocommit=False) as managed_conn:
            return run_stage5_penggajian(
                target_conn=managed_conn,
                legacy_conn=legacy_conn,
                payroll_all=payroll_all,
                months_window=months_window,
                dry_run=dry_run,
                console=console,
            )

    init_state_table(target_conn)

    # Pre-fetch Pegawai metadata and NIPAM mappings from target database
    pegawai_rows = execute_query(
        target_conn,
        """
        SELECT p.id, p.nipam, p.jabatan_id, p.golongan_id, p.status_pegawai, b.status_kawin
        FROM pegawai p
        LEFT JOIN biodata b ON p.biodata_id = b.nik
        WHERE p.is_deleted = 0
        """,
    )
    pegawai_info: dict[str, dict[str, Any]] = {
        str(r["nipam"]).strip(): r for r in pegawai_rows if r.get("nipam")
    }
    logger.info("Loaded %d active pegawai records from target database", len(pegawai_info))

    def _execute(leg_conn: Any) -> dict[str, Any]:
        root_count, master_count, detail_count, batch_root_ids = migrate_salary_batches(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            pegawai_info=pegawai_info,
            payroll_all=payroll_all,
            months_window=months_window,
            dry_run=dry_run,
        )

        # Baseline Envers injection for gaji_batch_root_aud if table exists
        envers_rev = None
        if not dry_run and batch_root_ids:
            aud_check = execute_query(
                target_conn,
                "SHOW TABLES LIKE 'gaji_batch_root_aud'",
            )
            if aud_check:
                logger.info("Injecting Envers baseline audit for gaji_batch_root_aud...")
                existing_aud = {
                    r["id"]
                    for r in execute_query(
                        target_conn,
                        "SELECT id FROM gaji_batch_root_aud WHERE revtype = %s",
                        (REVTYPE_ADD,),
                    )
                }
                batches_to_audit = [b for b in batch_root_ids if b not in existing_aud]
                if batches_to_audit:
                    envers_rev = create_revision(target_conn)
                    snapshot_table_to_audit(
                        conn=target_conn,
                        source_table="gaji_batch_root",
                        aud_table="gaji_batch_root_aud",
                        rev=envers_rev,
                        id_column="id",
                        ids=batches_to_audit,
                        revtype=REVTYPE_ADD,
                    )
                    target_conn.commit()
                else:
                    envers_rev = get_latest_revision(target_conn)

        summary = {
            "stage": 5,
            "domain": DOMAIN_NAME,
            "payroll_all": payroll_all,
            "months_window": months_window,
            "dry_run": dry_run,
            "batch_root_migrated": root_count,
            "batch_master_migrated": master_count,
            "batch_proses_detail_migrated": detail_count,
            "envers_revision": envers_rev,
            "status": "COMPLETED",
        }
        logger.info("Stage 5 completed successfully: %s", summary)
        return summary

    if legacy_conn is not None:
        return _execute(legacy_conn)
    with get_legacy_connection() as managed_legacy_conn:
        return _execute(managed_legacy_conn)

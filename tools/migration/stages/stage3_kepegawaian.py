"""Stage 3: Kepegawaian, SK, Mutasi, SP, dan Kontrak.

Memigrasikan rekam jejak surat keputusan, mutasi kerja, surat peringatan, dan kontrak:
1. `smartoffice.emp_sk`           -> `kepegawaian_dev_new.riwayat_sk`:
   - Pemetaan `jenis_sk`: 1:1 match (`EJenisSk` ordinal = `legacy_jenis_sk - 1`).
   - Mengizinkan nomor SK duplikat (ADR-0034).
2. `smartoffice.emp_work_history` -> `kepegawaian_dev_new.riwayat_mutasi` dengan **Delta Matching** (ADR-0049):
   - Jika unit kerja berubah dan jabatan sama -> `MUTASI_LOKER`
   - Jika jabatan berubah dan unit kerja sama -> `MUTASI_JABATAN`
   - Jika keduanya berubah -> prioritaskan `MUTASI_LOKER` (preferensi HR), simpan jabatan baru pada baris yang sama.
   - Relasi ke `riwayat_sk` via nomor SK (`ewh_sk_no`).
3. `smartoffice.emp_notice` (228 baris) -> `kepegawaian_dev_new.riwayat_sp`.
4. `smartoffice.emp_contract`           -> `kepegawaian_dev_new.riwayat_kontrak`.
5. Injeksi Baseline Revision Hibernate Envers (ADR-0051) untuk:
   `riwayat_sk_aud`, `riwayat_mutasi_aud`, `riwayat_sp_aud`, `riwayat_kontrak_aud`.
"""

from __future__ import annotations

from collections import defaultdict
from datetime import date
import logging
from typing import Any, Optional

try:
    from rich.console import Console
    from rich.table import Table
except ImportError:
    Console = None  # type: ignore[misc,assignment]
    Table = None  # type: ignore[misc,assignment]

from tools.migration.config import config
from tools.migration.core.db import (
    batch_upsert,
    execute_query,
    get_target_connection,
)
from tools.migration.core.envers import (
    create_revision,
    snapshot_table_to_audit,
)
from tools.migration.core.state import (
    batch_set_mappings,
    compute_record_hash,
    get_all_mappings,
)
from tools.migration.stages.common import StageResult

logger = logging.getLogger(__name__)


def _clean_date(val: Any) -> Optional[Any]:
    """Sanitizes date values, mapping 0000-00-00 and empty strings to None."""
    if not val:
        return None
    val_str = str(val).strip()
    if val_str.startswith("0000-00-00") or val_str in ("", "-", "--", "None", "NULL"):
        return None
    return val


def sync_riwayat_sk(conn: Any) -> tuple[int, int, list[int]]:
    """Migrates smartoffice.emp_sk to kepegawaian_dev_new.riwayat_sk.

    Permits duplicate SK numbers compliant with ADR-0034.

    Returns:
        tuple of (extracted_count, upserted_count, touched_ids).
    """
    legacy_schema = config.legacy_db.schema

    # Pre-fetch Pegawai details for denormalized columns (nipam, nama)
    peg_rows = execute_query(conn, "SELECT p.id, p.nipam, b.nama FROM pegawai p LEFT JOIN biodata b ON p.biodata_id = b.nik")
    peg_info = {r["id"]: (r["nipam"], r["nama"] or "") for r in peg_rows}

    # Map legacy emp_id -> target pegawai.id
    emp_map = get_all_mappings(conn, domain="pegawai", legacy_table="employee")
    valid_gols = {r["id"] for r in execute_query(conn, "SELECT id FROM golongan")}

    query = f"""
    SELECT
      id,
      emp_id,
      ref_id,
      jenis_sk,
      no_sk,
      tgl_sk,
      tmt_sk,
      golongan_id,
      gaji_pokok,
      mkg_tahun,
      mkg_bulan,
      kenaikan_berikutnya,
      mkgb_tahun,
      mkgb_bulan,
      flag_update_master,
      keterangan,
      status
    FROM `{legacy_schema}`.`emp_sk`
    ORDER BY id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0, []

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []
    touched_ids: list[int] = []

    for r in rows:
        sk_id = r["id"]
        legacy_emp_id = str(r["emp_id"])
        peg_id_str = emp_map.get(legacy_emp_id)
        if not peg_id_str:
            continue
        peg_id = int(peg_id_str)
        nipam, nama = peg_info.get(peg_id, ("", ""))

        # Jenis SK mapping: 1:1 match where EJenisSk ordinal = legacy_jenis_sk - 1
        legacy_jenis = r.get("jenis_sk")
        if legacy_jenis is not None:
            try:
                jenis_sk = max(0, min(8, int(legacy_jenis) - 1))
            except (ValueError, TypeError):
                jenis_sk = 6  # SK_LAINNYA
        else:
            jenis_sk = 6

        gol_id = r.get("golongan_id")
        golongan_id = gol_id if gol_id in valid_gols else None

        is_del = 1 if r.get("status") is not None and int(r.get("status")) != 1 else 0
        up_master = 1 if r.get("flag_update_master") == 1 else 0

        target_row = {
            "id": sk_id,
            "pegawai_id": peg_id,
            "nipam": nipam,
            "nama": nama,
            "nomor_sk": r.get("no_sk") or "",
            "jenis_sk": jenis_sk,
            "tanggal_sk": _clean_date(r.get("tgl_sk")),
            "tmt_berlaku": _clean_date(r.get("tmt_sk")),
            "golongan_id": golongan_id,
            "gaji_pokok": float(r.get("gaji_pokok") or 0.0),
            "mkg_tahun": int(r.get("mkg_tahun") or 0),
            "mkg_bulan": int(r.get("mkg_bulan") or 0),
            "kenaikan_berikutnya": _clean_date(r.get("kenaikan_berikutnya")),
            "mkgb_tahun": int(r.get("mkgb_tahun") or 0),
            "mkgb_bulan": int(r.get("mkgb_bulan") or 0),
            "update_master": up_master,
            "notes": r.get("keterangan"),
            "is_deleted": is_del,
            "changed_status": 0,
            "version": 0,
        }
        records.append(target_row)
        touched_ids.append(sk_id)

        mappings.append({
            "domain": "kepegawaian",
            "legacy_table": "emp_sk",
            "legacy_id": sk_id,
            "new_table": "riwayat_sk",
            "new_id": sk_id,
            "record_hash": compute_record_hash(target_row),
        })

    upsert_cols = [
        "pegawai_id",
        "nipam",
        "nama",
        "nomor_sk",
        "jenis_sk",
        "tanggal_sk",
        "tmt_berlaku",
        "golongan_id",
        "gaji_pokok",
        "mkg_tahun",
        "mkg_bulan",
        "kenaikan_berikutnya",
        "mkgb_tahun",
        "mkgb_bulan",
        "update_master",
        "notes",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="riwayat_sk",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d riwayat_sk records", len(records))
    return len(rows), len(records), touched_ids


def sync_riwayat_mutasi(conn: Any) -> tuple[int, int, list[int]]:
    """Migrates smartoffice.emp_work_history to riwayat_mutasi using Delta Matching (ADR-0049).

    Delta Matching Logic:
      - Unit kerja changed & jabatan same -> MUTASI_LOKER (1)
      - Jabatan changed & unit kerja same -> MUTASI_JABATAN (2)
      - Both changed                      -> MUTASI_LOKER (1) (HR preference)
      - Initial/neither                   -> PENGANGKATAN_PERTAMA (0) or MUTASI_LOKER (1)
      - ewh_type == 4                     -> TERMINASI (6)
      - Relates to riwayat_sk via nomor SK.

    Returns:
        tuple of (extracted_count, upserted_count, touched_ids).
    """
    legacy_schema = config.legacy_db.schema

    # Lookup mapping for employee code -> pegawai_id, nipam, nama
    emp_rows = execute_query(conn, "SELECT p.id, p.nipam, b.nama FROM pegawai p LEFT JOIN biodata b ON p.biodata_id = b.nik")
    code_to_peg = {r["nipam"]: (r["id"], r["nama"] or "") for r in emp_rows}

    # Pre-index riwayat_sk by (pegawai_id, clean_nomor_sk)
    sk_rows = execute_query(conn, "SELECT id, pegawai_id, nomor_sk FROM riwayat_sk WHERE is_deleted = 0")
    sk_index: dict[tuple[int, str], int] = {}
    for sr in sk_rows:
        n_sk = (sr.get("nomor_sk") or "").strip()
        if n_sk:
            sk_index[(sr["pegawai_id"], n_sk)] = sr["id"]

    valid_orgs = {r["id"] for r in execute_query(conn, "SELECT id FROM organisasi")}
    valid_jabs = {r["id"] for r in execute_query(conn, "SELECT id FROM jabatan")}

    # Query emp_work_history ordered by employee and effective date
    query = f"""
    SELECT
      ewh_id,
      emp_code,
      ewh_type,
      ewh_org_id,
      ewh_org_name,
      ewh_pos_id,
      ewh_pos_name,
      ewh_old_org_id,
      ewh_old_org_name,
      ewh_old_pos_id,
      ewh_old_pos_name,
      ewh_sdate,
      ewh_edate,
      ewh_note,
      ewh_status,
      ewh_sk_no
    FROM `{legacy_schema}`.`emp_work_history`
    ORDER BY emp_code ASC, ewh_sdate ASC, ewh_id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0, []

    # Constraint handling: purge stale legacy prototype seed records with mismatched auto-increment IDs
    execute_query(conn, "DELETE FROM riwayat_mutasi WHERE created_by = 'SYSTEM'")

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []
    touched_ids: list[int] = []

    # Track delta state per employee
    current_emp_code: Optional[str] = None
    prev_org_id: Optional[int] = None
    prev_org_name: Optional[str] = None
    prev_pos_id: Optional[int] = None
    prev_pos_name: Optional[str] = None

    # Track used (pegawai_id, riwayat_sk_id) pairs to satisfy UK constraint
    used_sk_links: set[tuple[int, int]] = set()

    for r in rows:
        ewh_id = r["ewh_id"]
        emp_code = r["emp_code"]
        if emp_code not in code_to_peg:
            continue

        peg_id, emp_name = code_to_peg[emp_code]

        # Current org and pos
        raw_org_id = r.get("ewh_org_id")
        curr_org_id = int(raw_org_id) if raw_org_id is not None and str(raw_org_id).isdigit() else None
        curr_org_name = r.get("ewh_org_name")

        raw_pos_id = r.get("ewh_pos_id")
        curr_pos_id = int(raw_pos_id) if raw_pos_id is not None and str(raw_pos_id).isdigit() else None
        curr_pos_name = r.get("ewh_pos_name")

        # Row old org and pos
        raw_old_org_id = r.get("ewh_old_org_id")
        row_old_org_id = int(raw_old_org_id) if raw_old_org_id is not None and str(raw_old_org_id).isdigit() else None
        row_old_org_name = r.get("ewh_old_org_name")

        raw_old_pos_id = r.get("ewh_old_pos_id")
        row_old_pos_id = int(raw_old_pos_id) if raw_old_pos_id is not None and str(raw_old_pos_id).isdigit() else None
        row_old_pos_name = r.get("ewh_old_pos_name")

        # Reset tracker on employee change
        if emp_code != current_emp_code:
            current_emp_code = emp_code
            prev_org_id = None
            prev_org_name = None
            prev_pos_id = None
            prev_pos_name = None

        eff_old_org_id = row_old_org_id if row_old_org_id is not None else prev_org_id
        eff_old_org_name = row_old_org_name if row_old_org_name is not None else prev_org_name
        eff_old_pos_id = row_old_pos_id if row_old_pos_id is not None else prev_pos_id
        eff_old_pos_name = row_old_pos_name if row_old_pos_name is not None else prev_pos_name

        # Apply Delta Matching (ADR-0049)
        ewh_type = r.get("ewh_type")
        if ewh_type == 4:
            jenis_mutasi = 6  # TERMINASI
        elif ewh_type == 1 and eff_old_org_id is None and eff_old_pos_id is None:
            jenis_mutasi = 0  # PENGANGKATAN_PERTAMA
        else:
            org_changed = (curr_org_id != eff_old_org_id) if curr_org_id and eff_old_org_id else False
            pos_changed = (curr_pos_id != eff_old_pos_id) if curr_pos_id and eff_old_pos_id else False

            if org_changed and not pos_changed:
                jenis_mutasi = 1  # MUTASI_LOKER
            elif pos_changed and not org_changed:
                jenis_mutasi = 2  # MUTASI_JABATAN
            elif org_changed and pos_changed:
                jenis_mutasi = 1  # MUTASI_LOKER (prioritize unit per HR preference)
            else:
                if ewh_type == 1:
                    jenis_mutasi = 0  # PENGANGKATAN_PERTAMA
                else:
                    jenis_mutasi = 1  # MUTASI_LOKER

        # Resolve riwayat_sk_id via nomor SK
        sk_no_clean = (r.get("ewh_sk_no") or "").strip()
        matched_sk_id = sk_index.get((peg_id, sk_no_clean))

        # Guard against UniqueConstraint(pegawai_id, riwayat_sk_id) violation
        if matched_sk_id and (peg_id, matched_sk_id) not in used_sk_links:
            riwayat_sk_id = matched_sk_id
            used_sk_links.add((peg_id, matched_sk_id))
        else:
            riwayat_sk_id = None

        org_fk = curr_org_id if curr_org_id in valid_orgs else None
        prev_org_fk = eff_old_org_id if eff_old_org_id in valid_orgs else None
        pos_fk = curr_pos_id if curr_pos_id in valid_jabs else None
        prev_pos_fk = eff_old_pos_id if eff_old_pos_id in valid_jabs else None
        is_del = 1 if r.get("ewh_status") is not None and int(r.get("ewh_status")) != 1 else 0

        target_row = {
            "id": ewh_id,
            "pegawai_id": peg_id,
            "nipam": emp_code,
            "nama": emp_name,
            "riwayat_sk_id": riwayat_sk_id,
            "tmt_berlaku": _clean_date(r.get("ewh_sdate")),
            "tanggal_berakhir": _clean_date(r.get("ewh_edate")),
            "jenis_mutasi": jenis_mutasi,
            "organisasi_id": org_fk,
            "nama_organisasi": curr_org_name,
            "organisasi_lama_id": prev_org_fk,
            "nama_organisasi_lama": eff_old_org_name,
            "jabatan_id": pos_fk,
            "nama_jabatan": curr_pos_name,
            "jabatan_lama_id": prev_pos_fk,
            "nama_jabatan_lama": eff_old_pos_name,
            "notes": (r.get("ewh_note") or "")[:255] if r.get("ewh_note") else None,
            "is_deleted": is_del,
            "changed_status": 0,
            "version": 0,
        }
        records.append(target_row)
        touched_ids.append(ewh_id)

        mappings.append({
            "domain": "kepegawaian",
            "legacy_table": "emp_work_history",
            "legacy_id": ewh_id,
            "new_table": "riwayat_mutasi",
            "new_id": ewh_id,
            "record_hash": compute_record_hash(target_row),
        })

        # Advance delta pointers
        if curr_org_id is not None:
            prev_org_id = curr_org_id
            prev_org_name = curr_org_name
        if curr_pos_id is not None:
            prev_pos_id = curr_pos_id
            prev_pos_name = curr_pos_name

    upsert_cols = [
        "pegawai_id",
        "nipam",
        "nama",
        "riwayat_sk_id",
        "tmt_berlaku",
        "tanggal_berakhir",
        "jenis_mutasi",
        "organisasi_id",
        "nama_organisasi",
        "organisasi_lama_id",
        "nama_organisasi_lama",
        "jabatan_id",
        "nama_jabatan",
        "jabatan_lama_id",
        "nama_jabatan_lama",
        "notes",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="riwayat_mutasi",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d riwayat_mutasi records with Delta Matching", len(records))
    return len(rows), len(records), touched_ids


def sync_riwayat_sp(conn: Any) -> tuple[int, int, list[int]]:
    """Migrates smartoffice.emp_notice (228 records) to kepegawaian_dev_new.riwayat_sp.

    Returns:
        tuple of (extracted_count, upserted_count, touched_ids).
    """
    legacy_schema = config.legacy_db.schema

    peg_rows = execute_query(conn, "SELECT p.id, p.nipam, b.nama FROM pegawai p LEFT JOIN biodata b ON p.biodata_id = b.nik")
    peg_info = {r["id"]: (r["nipam"], r["nama"] or "") for r in peg_rows}

    emp_map = get_all_mappings(conn, domain="pegawai", legacy_table="employee")
    valid_sp_types = {r["id"] for r in execute_query(conn, "SELECT id FROM jenis_sp")}

    query = f"""
    SELECT
      id,
      emp_id,
      emp_org_name,
      emp_pos_name,
      notice_no,
      notice_date,
      notice_type,
      notice_start_date,
      notice_end_date,
      notice_content,
      sign_by_name,
      sign_by_title,
      notice_status
    FROM `{legacy_schema}`.`emp_notice`
    ORDER BY id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0, []

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []
    touched_ids: list[int] = []

    for r in rows:
        sp_id = r["id"]
        legacy_emp_id = str(r["emp_id"])
        peg_id_str = emp_map.get(legacy_emp_id)
        if not peg_id_str:
            continue
        peg_id = int(peg_id_str)
        nipam, nama = peg_info.get(peg_id, ("", ""))

        raw_type = r.get("notice_type")
        jenis_sp_id = raw_type if raw_type in valid_sp_types else 1

        is_del = 1 if r.get("notice_status") == 3 else 0

        target_row = {
            "id": sp_id,
            "pegawai_id": peg_id,
            "nipam": nipam,
            "nama": nama,
            "nomor_sp": r.get("notice_no") or "",
            "tanggal_sp": _clean_date(r.get("notice_date")),
            "jenis_sp_id": jenis_sp_id,
            "tanggal_mulai": _clean_date(r.get("notice_start_date")),
            "tanggal_selesai": _clean_date(r.get("notice_end_date")),
            "notes": r.get("notice_content"),
            "penanda_tangan": r.get("sign_by_name"),
            "jabatan_penanda_tangan": r.get("sign_by_title"),
            "nama_organisasi": r.get("emp_org_name"),
            "nama_jabatan": r.get("emp_pos_name"),
            "is_deleted": is_del,
            "changed_status": 0,
            "version": 0,
        }
        records.append(target_row)
        touched_ids.append(sp_id)

        mappings.append({
            "domain": "kepegawaian",
            "legacy_table": "emp_notice",
            "legacy_id": sp_id,
            "new_table": "riwayat_sp",
            "new_id": sp_id,
            "record_hash": compute_record_hash(target_row),
        })

    upsert_cols = [
        "pegawai_id",
        "nipam",
        "nama",
        "nomor_sp",
        "tanggal_sp",
        "jenis_sp_id",
        "tanggal_mulai",
        "tanggal_selesai",
        "notes",
        "penanda_tangan",
        "jabatan_penanda_tangan",
        "nama_organisasi",
        "nama_jabatan",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="riwayat_sp",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d riwayat_sp records", len(records))
    return len(rows), len(records), touched_ids


def sync_riwayat_kontrak(conn: Any) -> tuple[int, int, list[int]]:
    """Migrates smartoffice.emp_contract to kepegawaian_dev_new.riwayat_kontrak.

    Returns:
        tuple of (extracted_count, upserted_count, touched_ids).
    """
    legacy_schema = config.legacy_db.schema

    peg_rows = execute_query(conn, "SELECT p.id, p.nipam, b.nama FROM pegawai p LEFT JOIN biodata b ON p.biodata_id = b.nik")
    code_to_peg = {r["nipam"]: (r["id"], r["nama"] or "") for r in peg_rows}

    # Index riwayat_sk by (pegawai_id, nomor_sk)
    sk_rows = execute_query(conn, "SELECT id, pegawai_id, nomor_sk FROM riwayat_sk WHERE is_deleted = 0")
    sk_index: dict[tuple[int, str], int] = {}
    for sr in sk_rows:
        n_sk = (sr.get("nomor_sk") or "").strip()
        if n_sk:
            sk_index[(sr["pegawai_id"], n_sk)] = sr["id"]

    query = f"""
    SELECT
      ec_id,
      emp_code,
      contract_no,
      contract_start_date,
      contract_exp_date,
      ec_description,
      ec_status
    FROM `{legacy_schema}`.`emp_contract`
    ORDER BY ec_id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0, []

    # Constraint handling: purge stale legacy prototype seed records with mismatched auto-increment IDs
    execute_query(conn, "DELETE FROM riwayat_kontrak WHERE created_by = 'SYSTEM'")

    # First pass: collect all contract rows per emp_code
    kontrak_by_emp: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for r in rows:
        emp_code = r.get("emp_code")
        if emp_code not in code_to_peg:
            continue
        peg_id, nama = code_to_peg[emp_code]

        ec_id = r.get("ec_id")
        ec_status_raw = r.get("ec_status")
        is_del = 1 if ec_status_raw is not None and int(ec_status_raw) != 1 else 0

        start_date_raw = _clean_date(r.get("contract_start_date"))
        if isinstance(start_date_raw, date):
            start_date_obj = start_date_raw
        elif isinstance(start_date_raw, str):
            try:
                start_date_obj = date.fromisoformat(start_date_raw[:10])
            except Exception:
                start_date_obj = None
        else:
            start_date_obj = None

        kontrak_by_emp[emp_code].append({
            "ec_id": ec_id,
            "emp_code": emp_code,
            "peg_id": peg_id,
            "nama": nama,
            "contract_no": (r.get("contract_no") or "").strip(),
            "ec_status": ec_status_raw,
            "is_del": is_del,
            "contract_start_date": start_date_obj,
            "tanggal_mulai": start_date_raw,
            "tanggal_selesai": _clean_date(r.get("contract_exp_date")),
            "tanggal_sk": start_date_raw,
            "notes": (r.get("ec_description") or "")[:255] if r.get("ec_description") else None,
        })

    # Second pass: derive jenis_kontrak and is_latest per emp_code
    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []
    touched_ids: list[int] = []
    used_sk_links: set[tuple[int, int]] = set()

    for emp_code, contracts in kontrak_by_emp.items():
        sorted_contracts = sorted(
            contracts,
            key=lambda c: (c["contract_start_date"] or date.min, c["ec_id"] or 0),
        )
        active_contracts = [c for c in sorted_contracts if c["is_del"] == 0]
        latest_ec_id = active_contracts[-1]["ec_id"] if active_contracts else None

        for i, c in enumerate(sorted_contracts):
            jenis_kontrak = 1 if i == 0 else 0  # PENGANGKATAN for first, PERPANJANGAN for rest
            is_latest = 1 if c["ec_id"] == latest_ec_id else 0

            peg_id = c["peg_id"]
            contract_no = c["contract_no"]
            matched_sk = sk_index.get((peg_id, contract_no))

            # Check unique constraint on (pegawai_id, riwayat_sk_id)
            if matched_sk and (peg_id, matched_sk) not in used_sk_links:
                riwayat_sk_id = matched_sk
                used_sk_links.add((peg_id, matched_sk))
            else:
                riwayat_sk_id = None

            target_row = {
                "id": c["ec_id"],
                "pegawai_id": peg_id,
                "nipam": c["emp_code"],
                "nama": c["nama"],
                "nomor_kontrak": contract_no,
                "jenis_kontrak": jenis_kontrak,
                "tanggal_mulai": c["tanggal_mulai"],
                "tanggal_selesai": c["tanggal_selesai"],
                "tanggal_sk": c["tanggal_sk"],
                "riwayat_sk_id": riwayat_sk_id,
                "is_latest": is_latest,
                "notes": c["notes"],
                "is_deleted": c["is_del"],
                "changed_status": 0,
                "version": 0,
            }
            records.append(target_row)
            touched_ids.append(c["ec_id"])

            mappings.append({
                "domain": "kepegawaian",
                "legacy_table": "emp_contract",
                "legacy_id": c["ec_id"],
                "new_table": "riwayat_kontrak",
                "new_id": c["ec_id"],
                "record_hash": compute_record_hash(target_row),
            })

    upsert_cols = [
        "pegawai_id",
        "nipam",
        "nama",
        "nomor_kontrak",
        "jenis_kontrak",
        "tanggal_mulai",
        "tanggal_selesai",
        "tanggal_sk",
        "riwayat_sk_id",
        "is_latest",
        "notes",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="riwayat_kontrak",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d riwayat_kontrak records", len(records))
    return len(rows), len(records), touched_ids


def run_stage3(console: Optional[Any] = None) -> StageResult:
    """Executes Stage 3 Kepegawaian (SK, Mutasi, SP, Kontrak) and Envers baseline injection.

    Returns:
        StageResult indicating overall counts and status.
    """
    result = StageResult(stage_name="Stage 3: Kepegawaian & SK", success=True)
    details: dict[str, Any] = {}

    try:
        with get_target_connection(autocommit=False) as conn:
            # 1. Riwayat SK
            ext_sk, up_sk, sk_ids = sync_riwayat_sk(conn)
            result.records_extracted += ext_sk
            result.records_upserted += up_sk
            details["riwayat_sk"] = {"extracted": ext_sk, "upserted": up_sk}

            # 2. Riwayat Mutasi (Delta Matching)
            ext_mut, up_mut, mut_ids = sync_riwayat_mutasi(conn)
            result.records_extracted += ext_mut
            result.records_upserted += up_mut
            details["riwayat_mutasi"] = {"extracted": ext_mut, "upserted": up_mut}

            # 3. Riwayat SP
            ext_sp, up_sp, sp_ids = sync_riwayat_sp(conn)
            result.records_extracted += ext_sp
            result.records_upserted += up_sp
            details["riwayat_sp"] = {"extracted": ext_sp, "upserted": up_sp}

            # 4. Riwayat Kontrak
            ext_kon, up_kon, kon_ids = sync_riwayat_kontrak(conn)
            result.records_extracted += ext_kon
            result.records_upserted += up_kon
            details["riwayat_kontrak"] = {"extracted": ext_kon, "upserted": up_kon}

            # 5. Injeksi Baseline Revision Hibernate Envers (ADR-0051)
            rev_id = create_revision(conn)
            details["envers_rev"] = rev_id

            if sk_ids:
                snapshot_table_to_audit(conn, source_table="riwayat_sk", aud_table="riwayat_sk_aud", rev=rev_id, id_column="id", ids=sk_ids)
            if mut_ids:
                snapshot_table_to_audit(conn, source_table="riwayat_mutasi", aud_table="riwayat_mutasi_aud", rev=rev_id, id_column="id", ids=mut_ids)
            if sp_ids:
                snapshot_table_to_audit(conn, source_table="riwayat_sp", aud_table="riwayat_sp_aud", rev=rev_id, id_column="id", ids=sp_ids)
            if kon_ids:
                snapshot_table_to_audit(conn, source_table="riwayat_kontrak", aud_table="riwayat_kontrak_aud", rev=rev_id, id_column="id", ids=kon_ids)

            logger.info("Stage 3 Envers revision %d baseline injected successfully", rev_id)
            result.details = details

    except Exception as exc:
        logger.error("Stage 3 failed: %s", exc, exc_info=True)
        result.add_error(f"Stage 3 execution error: {exc}")

    if console and Table:
        table = Table(title="Stage 3: Kepegawaian & SK Sync Summary", show_header=True)
        table.add_column("Entity", style="cyan")
        table.add_column("Extracted", justify="right")
        table.add_column("Upserted", justify="right")

        for k, v in details.items():
            if isinstance(v, dict) and "extracted" in v:
                table.add_row(k, str(v["extracted"]), str(v["upserted"]))
        console.print(table)
        console.print(f"[bold]Envers Baseline Rev:[/bold] {details.get('envers_rev', 'N/A')}")

    return result


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    c = Console() if Console else None
    res = run_stage3(console=c)
    if not res.success:
        import sys
        sys.exit(1)

"""Stage 4: Leave Management (Cuti) Migration Runner.

Complies with ADR-0054: Rekonsiliasi Snapshot Kuota Cuti dan Ingesti Delta Transaksi.
- Ignores obsolete 2016 legacy tables (emp_leave, emp_leave_history) and skips dead schema riwayat_cuti.
- 1:1 Snapshot reconciliation of cuti_kuota for 2026 from smartoffice.cuti_kuota.
- Inserts 2026 baseline quota for active employees missing quota records (31 new employees).
- Ingests delta 2025-2026 transactions for cuti_pegawai (247 records) along with:
    * cuti_pegawai_approval -> cuti_approval
    * cuti_pegawai_approval_chain -> cuti_approval_chain
    * cuti_pegawai_detail -> cuti_klaim_detail
- Resolves foreign keys to pegawai_id, cuti_jenis_id, organisasi_id, jabatan_id.
- Injects baseline revisions into Hibernate Envers audit tables:
    * cuti_pegawai_aud
    * cuti_kuota_aud
    * cuti_approval_aud
"""

from __future__ import annotations

import logging
from typing import Any, Sequence

from tools.migration.core.db import (
    batch_insert,
    execute_query,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.envers import (
    REVTYPE_ADD,
    create_revision,
    inject_audit_snapshot,
)
from tools.migration.core.state import (
    batch_set_mappings,
    get_all_mappings,
    get_mapping,
    init_state_table,
    set_mapping,
)

logger = logging.getLogger(__name__)

DOMAIN_NAME = "cuti"


def _map_approval_status(legacy_status: int | None) -> int:
    """Maps legacy cuti approval status to target EApprovalCutiStatus ordinal.

    Legacy:
        1: Proses / Menunggu Persetujuan -> 0 (PENDING)
        2: Disetujui -> 1 (APPROVED)
        3: Dikonfirmasi -> 2 (CONFIRMED)
        4: Ditolak -> 3 (REJECTED)
        5: Dibatalkan -> 4 (CANCELED)
        6: Dikembalikan -> 5 (RETURNED)
    """
    mapping = {
        1: 0,  # PENDING
        2: 1,  # APPROVED
        3: 2,  # CONFIRMED
        4: 3,  # REJECTED
        5: 4,  # CANCELED
        6: 5,  # RETURNED
    }
    return mapping.get(legacy_status or 1, 0)


def reconcile_cuti_kuota_2026(
    target_conn: Any,
    legacy_conn: Any,
    nipam_to_pegawai_id: dict[str, int],
    dry_run: bool = False,
) -> tuple[int, int, list[int]]:
    """Reconciles 2026 leave quota from smartoffice.cuti_kuota into target cuti_kuota.

    Returns:
        tuple of (updated_count, inserted_count, list_of_affected_target_ids)
    """
    logger.info("Starting Stage 4.1: 2026 Leave Quota Snapshot Reconciliation...")

    # 1. Fetch legacy 2026 quota snapshot
    sql_legacy_kuota = """
    SELECT
        ck_id,
        emp_code,
        ck_pyear,
        ck_kuota,
        ck_diambil,
        ck_sisa,
        ck_expired
    FROM cuti_kuota
    WHERE ck_pyear = 2026
    ORDER BY ck_id ASC
    """
    legacy_rows = execute_query(legacy_conn, sql_legacy_kuota)
    logger.info("Found %d quota rows for year 2026 in legacy database", len(legacy_rows))

    # 2. Fetch existing target 2026 quota records
    sql_target_kuota = """
    SELECT id, pegawai_id, tahun, kuota, kuota_terpakai, sisa_kuota, expired
    FROM cuti_kuota
    WHERE tahun = 2026 AND is_deleted = 0
    """
    target_rows = execute_query(target_conn, sql_target_kuota)
    target_kuota_by_pegawai: dict[int, dict[str, Any]] = {
        row["pegawai_id"]: row for row in target_rows
    }

    updated_count = 0
    inserted_count = 0
    affected_ids: list[int] = []
    id_mappings: list[dict[str, Any]] = []
    processed_pegawai_ids: set[int] = set()

    for leg in legacy_rows:
        emp_code = str(leg.get("emp_code", "")).strip()
        pegawai_id = nipam_to_pegawai_id.get(emp_code)
        if not pegawai_id:
            logger.debug("Pegawai not found for emp_code '%s' in cuti_kuota, skipping", emp_code)
            continue

        processed_pegawai_ids.add(pegawai_id)
        kuota = int(leg.get("ck_kuota") or 12)
        terpakai = int(leg.get("ck_diambil") or 0)
        sisa = int(leg.get("ck_sisa") if leg.get("ck_sisa") is not None else (kuota - terpakai))
        expired = leg.get("ck_expired") or "2027-06-30"

        existing = target_kuota_by_pegawai.get(pegawai_id)
        if existing:
            target_id = existing["id"]
            if not dry_run:
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        """
                        UPDATE cuti_kuota
                        SET kuota = %s,
                            kuota_terpakai = %s,
                            sisa_kuota = %s,
                            expired = %s,
                            updated_at = NOW(),
                            updated_by = 'MIGRATION'
                        WHERE id = %s
                        """,
                        (kuota, terpakai, sisa, expired, target_id),
                    )
            updated_count += 1
            affected_ids.append(target_id)
        else:
            target_id = 0
            if not dry_run:
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        """
                        INSERT INTO cuti_kuota (
                            pegawai_id, tahun, kuota, kuota_terpakai,
                            kuota_tambahan, sisa_kuota, expired,
                            is_deleted, created_at, created_by, updated_at, updated_by
                        ) VALUES (%s, 2026, %s, %s, 0, %s, %s, 0, NOW(), 'MIGRATION', NOW(), 'MIGRATION')
                        """,
                        (pegawai_id, kuota, terpakai, sisa, expired),
                    )
                    target_id = cursor.lastrowid
            inserted_count += 1
            affected_ids.append(target_id)

        id_mappings.append({
            "domain": DOMAIN_NAME,
            "legacy_table": "cuti_kuota",
            "legacy_id": leg["ck_id"],
            "new_table": "cuti_kuota",
            "new_id": target_id,
        })

    # 3. Handle active employees missing from 2026 snapshot (insert 31 new employee quotas)
    sql_active_pegawai = """
    SELECT id, nipam FROM pegawai
    WHERE status_kerja IN (1, 2) AND is_deleted = 0
    """
    active_pegawai = execute_query(target_conn, sql_active_pegawai)
    new_employee_quota_count = 0

    for pgw in active_pegawai:
        p_id = pgw["id"]
        if p_id not in processed_pegawai_ids and p_id not in target_kuota_by_pegawai:
            target_id = 0
            if not dry_run:
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        """
                        INSERT INTO cuti_kuota (
                            pegawai_id, tahun, kuota, kuota_terpakai,
                            kuota_tambahan, sisa_kuota, expired,
                            is_deleted, created_at, created_by, updated_at, updated_by
                        ) VALUES (%s, 2026, 12, 0, 0, 12, '2027-06-30', 0, NOW(), 'MIGRATION', NOW(), 'MIGRATION')
                        """,
                        (p_id,),
                    )
                    target_id = cursor.lastrowid
            new_employee_quota_count += 1
            inserted_count += 1
            affected_ids.append(target_id)

    logger.info(
        "Cuti kuota reconciliation completed: %d updated, %d inserted (including %d new employees)",
        updated_count,
        inserted_count,
        new_employee_quota_count,
    )

    if not dry_run and id_mappings:
        batch_set_mappings(target_conn, id_mappings)

    return updated_count, inserted_count, affected_ids


def migrate_cuti_transaksi_delta(
    target_conn: Any,
    legacy_conn: Any,
    nipam_to_pegawai_id: dict[str, int],
    dry_run: bool = False,
) -> tuple[int, list[int], dict[int, int]]:
    """Migrates delta 2025-2026 transactions from smartoffice.cuti_pegawai to cuti_pegawai.

    Returns:
        tuple of (migrated_count, list_of_target_ids, legacy_to_target_cp_id_map)
    """
    logger.info("Starting Stage 4.2: Delta Leave Transactions Migration (2025-2026)...")

    sql_delta = """
    SELECT *
    FROM cuti_pegawai
    WHERE (cp_year >= 2025 OR YEAR(cp_sdate) >= 2025 OR YEAR(cp_request_at) >= 2025)
    ORDER BY cp_id ASC
    """
    legacy_txs = execute_query(legacy_conn, sql_delta)
    logger.info("Extracted %d delta leave transactions from legacy database", len(legacy_txs))

    # Pre-fetch existing cuti_jenis IDs to guarantee FK validity
    valid_cuti_jenis_ids = {
        row["id"]
        for row in execute_query(target_conn, "SELECT id FROM cuti_jenis WHERE is_deleted = 0")
    }

    # Pre-fetch pegawai details (jabatan_id, organisasi_id) for reliable FK resolution
    pegawai_info_rows = execute_query(
        target_conn,
        "SELECT id, nipam, organisasi_id, jabatan_id FROM pegawai WHERE is_deleted = 0",
    )
    pegawai_info: dict[int, dict[str, Any]] = {row["id"]: row for row in pegawai_info_rows}

    # Existing ID mappings
    existing_mappings = get_all_mappings(target_conn, domain=DOMAIN_NAME, legacy_table="cuti_pegawai")

    cp_id_map: dict[int, int] = {}
    target_ids: list[int] = []
    id_mappings_to_save: list[dict[str, Any]] = []

    # First pass: insert all transactions and record target IDs
    for tx in legacy_txs:
        legacy_cp_id = int(tx["cp_id"])
        emp_code = str(tx.get("emp_code") or "").strip()
        pegawai_id = nipam_to_pegawai_id.get(emp_code)

        if not pegawai_id:
            logger.warning("Pegawai not found for delta cuti transaction cp_id=%d (emp_code=%s), skipping", legacy_cp_id, emp_code)
            continue

        p_info = pegawai_info.get(pegawai_id, {})
        org_id = p_info.get("organisasi_id")
        jabatan_id = p_info.get("jabatan_id")

        # Cuti jenis resolution
        jenis_id = tx.get("cp_jenis")
        if jenis_id not in valid_cuti_jenis_ids:
            jenis_id = 1  # Default fallback to Cuti Tahunan (ID: 1)

        sub_jenis_id = tx.get("cp_sub_jenis")
        if sub_jenis_id not in valid_cuti_jenis_ids:
            sub_jenis_id = None

        cp_type = int(tx.get("cp_type") or 1)
        jenis_pengajuan = 1 if cp_type == 2 else 0  # 0: PENGAJUAN_CUTI, 1: KLAIM_CUTI
        approval_status = _map_approval_status(tx.get("cp_approval_status"))

        k0 = int(tx.get("cp_k0") or 0)
        k1 = int(tx.get("cp_k1") or 0)
        n0 = int(tx.get("cp_n0") or 0)
        n1 = int(tx.get("cp_n1") or 0)
        sisa0 = max(0, k0 - n0)
        sisa1 = max(0, k1 - n1)

        is_claimed = 1 if cp_type == 2 or tx.get("cp_is_claim") else 0
        existing_new_id = existing_mappings.get(str(legacy_cp_id))

        if existing_new_id:
            target_cp_id = int(existing_new_id)
            cp_id_map[legacy_cp_id] = target_cp_id
            target_ids.append(target_cp_id)
            continue

        target_cp_id = 0
        if not dry_run:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO cuti_pegawai (
                        pegawai_id, nipam, nama, pangkat_golongan,
                        organisasi_id, jabatan_id, jenis_pengajuan_cuti,
                        jenis_cuti_id, sub_jenis_cuti_id,
                        tanggal_mulai, tanggal_selesai,
                        jumlah_hari, jumlah_hari_kerja,
                        kuota_awal, kuota_akhir, alasan,
                        approval_cuti_status, approval_level,
                        riwayat_kuota0, riwayat_kuota1,
                        riwayat_pakai0, riwayat_pakai1,
                        riwayat_sisa0, riwayat_sisa1,
                        is_claimed, is_deleted,
                        created_at, created_by, updated_at, updated_by
                    ) VALUES (
                        %s, %s, %s, %s,
                        %s, %s, %s,
                        %s, %s,
                        %s, %s,
                        %s, %s,
                        %s, %s, %s,
                        %s, %s,
                        %s, %s,
                        %s, %s,
                        %s, %s,
                        %s, 0,
                        COALESCE(%s, NOW()), 'MIGRATION', NOW(), 'MIGRATION'
                    )
                    """,
                    (
                        pegawai_id,
                        emp_code,
                        tx.get("emp_name") or "",
                        tx.get("pangkat") or tx.get("gol_name"),
                        org_id,
                        jabatan_id,
                        jenis_pengajuan,
                        jenis_id,
                        sub_jenis_id,
                        tx.get("cp_sdate"),
                        tx.get("cp_edate"),
                        tx.get("cp_days") or 0,
                        tx.get("cp_work_days") or 0,
                        tx.get("cp_before") or 0,
                        tx.get("cp_after") or 0,
                        tx.get("cp_alasan") or "",
                        approval_status,
                        tx.get("cp_approval_level"),
                        k0,
                        k1,
                        n0,
                        n1,
                        sisa0,
                        sisa1,
                        is_claimed,
                        tx.get("cp_request_at"),
                    ),
                )
                target_cp_id = cursor.lastrowid

        cp_id_map[legacy_cp_id] = target_cp_id
        target_ids.append(target_cp_id)

        id_mappings_to_save.append({
            "domain": DOMAIN_NAME,
            "legacy_table": "cuti_pegawai",
            "legacy_id": legacy_cp_id,
            "new_table": "cuti_pegawai",
            "new_id": target_cp_id,
        })

    if not dry_run and id_mappings_to_save:
        batch_set_mappings(target_conn, id_mappings_to_save)

    # Second pass: resolve ref_cuti_id for klaim cuti transactions
    if not dry_run:
        for tx in legacy_txs:
            legacy_cp_id = int(tx["cp_id"])
            legacy_ref_id = tx.get("cp_ref_id")
            if legacy_ref_id and int(legacy_ref_id) in cp_id_map:
                target_cp_id = cp_id_map[legacy_cp_id]
                target_ref_id = cp_id_map[int(legacy_ref_id)]
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        "UPDATE cuti_pegawai SET ref_cuti_id = %s WHERE id = %s",
                        (target_ref_id, target_cp_id),
                    )

    logger.info("Migrated %d delta leave transactions to cuti_pegawai", len(target_ids))
    return len(target_ids), target_ids, cp_id_map


def migrate_cuti_supporting_tables(
    target_conn: Any,
    legacy_conn: Any,
    cp_id_map: dict[int, int],
    nipam_to_pegawai_id: dict[str, int],
    dry_run: bool = False,
) -> tuple[int, int, int, list[int]]:
    """Migrates approval, approval chain, and claim detail tables.

    Returns:
        tuple of (approval_count, chain_count, detail_count, list_of_target_approval_ids)
    """
    if not cp_id_map:
        return 0, 0, 0, []

    legacy_cp_ids = list(cp_id_map.keys())
    placeholders = ", ".join(["%s"] * len(legacy_cp_ids))

    # Pre-fetch valid jabatan IDs for approver jabatan FK
    valid_jabatan_ids = {
        row["id"]
        for row in execute_query(target_conn, "SELECT id FROM jabatan WHERE is_deleted = 0")
    }

    # 1. cuti_pegawai_approval -> cuti_approval
    sql_approval = f"""
    SELECT cp_id, cpa_emp_code, cpa_pos_id, cpa_approval_level,
           cpa_approval_status, cpa_note, cpa_date
    FROM cuti_pegawai_approval
    WHERE cp_id IN ({placeholders})
    ORDER BY cpa_id ASC
    """
    legacy_approvals = execute_query(legacy_conn, sql_approval, legacy_cp_ids)

    approval_rows: list[dict[str, Any]] = []
    target_approval_ids: list[int] = []

    for item in legacy_approvals:
        target_cp_id = cp_id_map.get(int(item["cp_id"]))
        if not target_cp_id:
            continue

        approver_nipam = str(item.get("cpa_emp_code") or "").strip()
        approver_id = nipam_to_pegawai_id.get(approver_nipam)
        jabatan_id = item.get("cpa_pos_id")
        if jabatan_id not in valid_jabatan_ids:
            jabatan_id = None

        approval_rows.append({
            "cuti_pegawai_id": target_cp_id,
            "approver_id": approver_id,
            "jabatan_id": jabatan_id,
            "approval_level": item.get("cpa_approval_level") or 1,
            "approval_status": _map_approval_status(item.get("cpa_approval_status")),
            "notes": item.get("cpa_note") or "",
            "is_deleted": 0,
            "created_at": item.get("cpa_date"),
            "created_by": "MIGRATION",
            "updated_at": item.get("cpa_date"),
            "updated_by": "MIGRATION",
        })

    approval_count = 0
    if not dry_run and approval_rows:
        for app_row in approval_rows:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO cuti_approval (
                        cuti_pegawai_id, approver_id, jabatan_id,
                        approval_level, approval_status, notes,
                        is_deleted, created_at, created_by, updated_at, updated_by
                    ) VALUES (%s, %s, %s, %s, %s, %s, 0, COALESCE(%s, NOW()), 'MIGRATION', NOW(), 'MIGRATION')
                    """,
                    (
                        app_row["cuti_pegawai_id"],
                        app_row["approver_id"],
                        app_row["jabatan_id"],
                        app_row["approval_level"],
                        app_row["approval_status"],
                        app_row["notes"],
                        app_row["created_at"],
                    ),
                )
                target_approval_ids.append(cursor.lastrowid)
        approval_count = len(target_approval_ids)
    elif dry_run:
        approval_count = len(approval_rows)

    # 2. cuti_pegawai_approval_chain -> cuti_approval_chain
    sql_chain = f"""
    SELECT cp_id, cpc_pos_id, cpc_pos_name, cpc_approval_level
    FROM cuti_pegawai_approval_chain
    WHERE cp_id IN ({placeholders})
    ORDER BY cpc_id ASC
    """
    legacy_chains = execute_query(legacy_conn, sql_chain, legacy_cp_ids)

    chain_rows: list[dict[str, Any]] = []
    for ch in legacy_chains:
        target_cp_id = cp_id_map.get(int(ch["cp_id"]))
        if not target_cp_id:
            continue
        chain_rows.append({
            "ref_cuti_id": target_cp_id,
            "jabatan_id": ch.get("cpc_pos_id"),
            "jabatan_nama": ch.get("cpc_pos_name") or "",
            "approval_level": ch.get("cpc_approval_level") or 1,
            "approval_status": 0,  # PENDING
            "read_write_status": 0,  # NONE
        })

    chain_count = 0
    if not dry_run and chain_rows:
        chain_count = batch_insert(target_conn, "cuti_approval_chain", chain_rows)
    elif dry_run:
        chain_count = len(chain_rows)

    # 3. cuti_pegawai_detail -> cuti_klaim_detail
    sql_detail = f"""
    SELECT cp_id, cpd_date_1
    FROM cuti_pegawai_detail
    WHERE cp_id IN ({placeholders}) AND cpd_date_1 IS NOT NULL
    ORDER BY cpd_id ASC
    """
    legacy_details = execute_query(legacy_conn, sql_detail, legacy_cp_ids)

    detail_rows: list[dict[str, Any]] = []
    for dt in legacy_details:
        target_cp_id = cp_id_map.get(int(dt["cp_id"]))
        if not target_cp_id:
            continue
        detail_rows.append({
            "ref_cuti_id": target_cp_id,
            "tanggal": dt["cpd_date_1"],
        })

    detail_count = 0
    if not dry_run and detail_rows:
        detail_count = batch_insert(target_conn, "cuti_klaim_detail", detail_rows)
    elif dry_run:
        detail_count = len(detail_rows)

    logger.info(
        "Supporting leave tables migrated: %d approvals, %d chains, %d claim details",
        approval_count,
        chain_count,
        detail_count,
    )
    return approval_count, chain_count, detail_count, target_approval_ids


def run_stage4_cuti(
    target_conn: Any | None = None,
    legacy_conn: Any | None = None,
    dry_run: bool = False,
    console: Any | None = None,
) -> dict[str, Any]:
    """Entrypoint for Stage 4: Leave Management (Cuti) Migration.

    Args:
        target_conn: Connection to kepegawaian_dev_new database. If None, acquires one.
        legacy_conn: Connection to smartoffice database. If None, acquires one.
        dry_run: If True, reads and parses data without writing to target database.
        console: Optional rich console for display.

    Returns:
        Summary metrics dictionary.
    """
    logger.info("============================================================")
    logger.info("Executing Stage 4: Leave Management Migration (ADR-0054)")
    logger.info("============================================================")

    if target_conn is None:
        with get_target_connection(autocommit=False) as managed_conn:
            return run_stage4_cuti(
                target_conn=managed_conn,
                legacy_conn=legacy_conn,
                dry_run=dry_run,
                console=console,
            )

    init_state_table(target_conn)

    # Pre-fetch NIPAM to Pegawai ID mapping from target database
    pegawai_rows = execute_query(
        target_conn,
        "SELECT id, nipam FROM pegawai WHERE is_deleted = 0",
    )
    nipam_to_pegawai_id: dict[str, int] = {
        str(r["nipam"]).strip(): int(r["id"]) for r in pegawai_rows if r.get("nipam")
    }
    logger.info("Loaded %d active pegawai records from target database", len(nipam_to_pegawai_id))

    def _execute(leg_conn: Any) -> dict[str, Any]:
        # Step 4.1: Kuota Snapshot Reconciliation 2026
        kuota_updated, kuota_inserted, affected_kuota_ids = reconcile_cuti_kuota_2026(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            nipam_to_pegawai_id=nipam_to_pegawai_id,
            dry_run=dry_run,
        )

        # Step 4.2: Delta Transaksi 2025-2026
        tx_count, target_tx_ids, cp_id_map = migrate_cuti_transaksi_delta(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            nipam_to_pegawai_id=nipam_to_pegawai_id,
            dry_run=dry_run,
        )

        # Step 4.3: Supporting Approval, Chain, and Detail Tables
        app_count, chain_count, detail_count, target_app_ids = migrate_cuti_supporting_tables(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            cp_id_map=cp_id_map,
            nipam_to_pegawai_id=nipam_to_pegawai_id,
            dry_run=dry_run,
        )

        # Step 4.4: Hibernate Envers Baseline Injection
        envers_rev = None
        if not dry_run and (affected_kuota_ids or target_tx_ids or target_app_ids):
            logger.info("Injecting Envers baseline audit snapshots for Stage 4...")
            envers_rev = create_revision(target_conn)

            # Audit cuti_kuota_aud
            if affected_kuota_ids:
                from tools.migration.core.envers import snapshot_table_to_audit
                snapshot_table_to_audit(
                    conn=target_conn,
                    source_table="cuti_kuota",
                    aud_table="cuti_kuota_aud",
                    rev=envers_rev,
                    ids=affected_kuota_ids,
                    revtype=REVTYPE_ADD,
                )

            # Audit cuti_pegawai_aud
            if target_tx_ids:
                from tools.migration.core.envers import snapshot_table_to_audit
                snapshot_table_to_audit(
                    conn=target_conn,
                    source_table="cuti_pegawai",
                    aud_table="cuti_pegawai_aud",
                    rev=envers_rev,
                    ids=target_tx_ids,
                    revtype=REVTYPE_ADD,
                )

            # Audit cuti_approval_aud
            if target_app_ids:
                from tools.migration.core.envers import snapshot_table_to_audit
                snapshot_table_to_audit(
                    conn=target_conn,
                    source_table="cuti_approval",
                    aud_table="cuti_approval_aud",
                    rev=envers_rev,
                    ids=target_app_ids,
                    revtype=REVTYPE_ADD,
                )

        summary = {
            "stage": 4,
            "domain": DOMAIN_NAME,
            "dry_run": dry_run,
            "kuota_2026_updated": kuota_updated,
            "kuota_2026_inserted": kuota_inserted,
            "transaksi_delta_migrated": tx_count,
            "approval_migrated": app_count,
            "approval_chain_migrated": chain_count,
            "klaim_detail_migrated": detail_count,
            "envers_revision": envers_rev,
            "status": "COMPLETED",
        }
        logger.info("Stage 4 completed successfully: %s", summary)
        return summary

    if legacy_conn is not None:
        return _execute(legacy_conn)
    with get_legacy_connection() as managed_legacy_conn:
        return _execute(managed_legacy_conn)

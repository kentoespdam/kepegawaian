"""Stage 1: Master Reference Synchronization.

Menyelaraskan master lookups yang dibutuhkan:
1. `smartoffice.organization` -> `kepegawaian_dev_new.organisasi`
2. `smartoffice.position`     -> `kepegawaian_dev_new.jabatan`
3. `smartoffice.emp_golongan` (atau `golongan`) -> `kepegawaian_dev_new.golongan`
4. `smartoffice.jenis_cuti`   -> `kepegawaian_dev_new.cuti_jenis`

Menerapkan prinsip Safe Upsert (ADR-0047) dan mencatat seluruh pemetaan ID
ke tabel `migration_id_map` (domain='master').
"""

from __future__ import annotations

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
from tools.migration.core.state import (
    batch_set_mappings,
    compute_record_hash,
)
from tools.migration.stages.common import StageResult

logger = logging.getLogger(__name__)


def sync_organisasi(conn: Any) -> tuple[int, int]:
    """Synchronizes smartoffice.organization to kepegawaian_dev_new.organisasi.

    Returns:
        tuple of (extracted_count, upserted_count).
    """
    legacy_schema = config.legacy_db.schema
    query = f"""
    SELECT
      org_id,
      org_code,
      org_name,
      org_level,
      org_parent,
      org_status,
      mail_code,
      office_code,
      `group`,
      category
    FROM `{legacy_schema}`.`organization`
    ORDER BY org_level ASC, org_id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []

    for r in rows:
        org_id = r["org_id"]
        parent_id = r.get("org_parent")
        if parent_id is not None and int(parent_id) <= 0:
            parent_id = None

        is_deleted = 1 if r.get("org_status") == "Deleted" else 0
        short_name = r.get("mail_code") or r.get("office_code") or None
        org_group = r.get("group") or ""

        target_row = {
            "id": org_id,
            "kode": r.get("org_code") or str(org_id),
            "nama": r.get("org_name"),
            "level_org": r.get("org_level"),
            "parent_id": parent_id,
            "short_name": short_name,
            "category": r.get("category"),
            "org_group": org_group,
            "is_deleted": is_deleted,
        }
        records.append(target_row)
        rec_hash = compute_record_hash(target_row)

        mappings.append({
            "domain": "master",
            "legacy_table": "organization",
            "legacy_id": org_id,
            "new_table": "organisasi",
            "new_id": org_id,
            "record_hash": rec_hash,
        })

    upsert_cols = [
        "kode",
        "nama",
        "level_org",
        "parent_id",
        "short_name",
        "category",
        "org_group",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="organisasi",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d organisasi master records", len(records))
    return len(rows), len(records)


def sync_jabatan(conn: Any) -> tuple[int, int]:
    """Synchronizes smartoffice.position to kepegawaian_dev_new.jabatan.

    Returns:
        tuple of (extracted_count, upserted_count).
    """
    legacy_schema = config.legacy_db.schema
    query = f"""
    SELECT
      pos_id,
      pos_code,
      pos_name,
      pos_parent,
      pos_level,
      pos_org_id,
      pos_status
    FROM `{legacy_schema}`.`position`
    ORDER BY pos_level ASC, pos_id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0

    # Fetch valid level and organisasi IDs for referential integrity
    valid_levels = {
        row["id"] for row in execute_query(conn, "SELECT id FROM level WHERE is_deleted = 0")
    }
    valid_orgs = {row["id"] for row in execute_query(conn, "SELECT id FROM organisasi")}

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []

    for r in rows:
        pos_id = r["pos_id"]
        parent_id = r.get("pos_parent")
        if parent_id is not None and int(parent_id) <= 0:
            parent_id = None

        pos_level = r.get("pos_level")
        level_id = pos_level if pos_level in valid_levels else 7  # Fallback to STAF (7)

        pos_org_id = r.get("pos_org_id")
        organisasi_id = pos_org_id if pos_org_id in valid_orgs else None

        is_deleted = 1 if r.get("pos_status") == "Deleted" else 0

        target_row = {
            "id": pos_id,
            "kode": r.get("pos_code") or str(pos_id),
            "nama": r.get("pos_name"),
            "parent_id": parent_id,
            "level_id": level_id,
            "organisasi_id": organisasi_id,
            "is_deleted": is_deleted,
        }
        records.append(target_row)
        rec_hash = compute_record_hash(target_row)

        mappings.append({
            "domain": "master",
            "legacy_table": "position",
            "legacy_id": pos_id,
            "new_table": "jabatan",
            "new_id": pos_id,
            "record_hash": rec_hash,
        })

    upsert_cols = [
        "kode",
        "nama",
        "parent_id",
        "level_id",
        "organisasi_id",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="jabatan",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d jabatan master records", len(records))
    return len(rows), len(records)


def sync_golongan(conn: Any) -> tuple[int, int]:
    """Synchronizes smartoffice.emp_golongan / golongan to kepegawaian_dev_new.golongan.

    Returns:
        tuple of (extracted_count, upserted_count).
    """
    legacy_schema = config.legacy_db.schema

    # Determine whether legacy table is named 'emp_golongan' or 'golongan'
    check_sql = f"""
    SELECT TABLE_NAME
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = '{legacy_schema}' AND TABLE_NAME IN ('emp_golongan', 'golongan')
    ORDER BY TABLE_NAME = 'emp_golongan' DESC
    LIMIT 1
    """
    tbl_rows = execute_query(conn, check_sql)
    tbl_name = tbl_rows[0]["TABLE_NAME"] if tbl_rows else "golongan"

    query = f"SELECT id, golongan, pangkat FROM `{legacy_schema}`.`{tbl_name}` ORDER BY id ASC"
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []

    for r in rows:
        gol_id = r["id"]
        target_row = {
            "id": gol_id,
            "golongan": r.get("golongan"),
            "pangkat": r.get("pangkat"),
            "is_deleted": 0,
        }
        records.append(target_row)
        rec_hash = compute_record_hash(target_row)

        mappings.append({
            "domain": "master",
            "legacy_table": tbl_name,
            "legacy_id": gol_id,
            "new_table": "golongan",
            "new_id": gol_id,
            "record_hash": rec_hash,
        })

    upsert_cols = ["golongan", "pangkat", "is_deleted"]
    batch_upsert(
        conn=conn,
        table_name="golongan",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d golongan master records from %s", len(records), tbl_name)
    return len(rows), len(records)


def sync_jenis_cuti(conn: Any) -> tuple[int, int]:
    """Synchronizes smartoffice.jenis_cuti to kepegawaian_dev_new.cuti_jenis.

    Returns:
        tuple of (extracted_count, upserted_count).
    """
    legacy_schema = config.legacy_db.schema

    # Inspect columns in legacy jenis_cuti
    cols_query = f"SHOW COLUMNS FROM `{legacy_schema}`.`jenis_cuti`"
    col_rows = execute_query(conn, cols_query)
    avail_cols = {c["Field"] for c in col_rows}

    parent_col = "parent" if "parent" in avail_cols else "parent_id" if "parent_id" in avail_cols else "NULL"
    name_col = "nama_cuti" if "nama_cuti" in avail_cols else "nama"
    max_col = "max_hari" if "max_hari" in avail_cols else "jml_hari" if "jml_hari" in avail_cols else "0"
    potong_col = (
        "potong_cuti"
        if "potong_cuti" in avail_cols
        else "potong_kuota_tahunan"
        if "potong_kuota_tahunan" in avail_cols
        else "0"
    )
    status_col = "status" if "status" in avail_cols else "1"

    query = f"""
    SELECT
      id,
      {name_col} AS nama,
      {parent_col} AS parent,
      {max_col} AS max_hari,
      {potong_col} AS potong_cuti,
      {status_col} AS status
    FROM `{legacy_schema}`.`jenis_cuti`
    ORDER BY parent ASC, id ASC
    """
    rows = execute_query(conn, query)
    if not rows:
        return 0, 0

    records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []

    for r in rows:
        cuti_id = r["id"]
        parent_id = r.get("parent")
        if parent_id is not None and int(parent_id) <= 0:
            parent_id = None

        is_deleted = 1 if r.get("status") is not None and int(r.get("status")) != 1 else 0
        potong = 1 if r.get("potong_cuti") and int(r.get("potong_cuti")) == 1 else 0

        target_row = {
            "id": cuti_id,
            "nama": r.get("nama"),
            "parent_id": parent_id,
            "max_hari": r.get("max_hari") or 0,
            "potong_kuota_tahunan": potong,
            "is_deleted": is_deleted,
            "changed_status": 0,
            "version": 0,
        }
        records.append(target_row)
        rec_hash = compute_record_hash(target_row)

        mappings.append({
            "domain": "master",
            "legacy_table": "jenis_cuti",
            "legacy_id": cuti_id,
            "new_table": "cuti_jenis",
            "new_id": cuti_id,
            "record_hash": rec_hash,
        })

    upsert_cols = [
        "nama",
        "parent_id",
        "max_hari",
        "potong_kuota_tahunan",
        "is_deleted",
    ]
    batch_upsert(
        conn=conn,
        table_name="cuti_jenis",
        records=records,
        update_columns=upsert_cols,
        chunk_size=500,
    )
    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    logger.info("Synchronized %d cuti_jenis master records", len(records))
    return len(rows), len(records)


def run_stage1(console: Optional[Any] = None) -> StageResult:
    """Executes Stage 1 Master Synchronization.

    Syncs:
      - organisasi
      - jabatan
      - golongan
      - cuti_jenis

    Returns:
        StageResult with counts and status.
    """
    result = StageResult(stage_name="Stage 1: Master Reference Sync", success=True)
    details: dict[str, Any] = {}

    try:
        with get_target_connection(autocommit=False) as conn:
            # 1. Organisasi
            ext_org, up_org = sync_organisasi(conn)
            result.records_extracted += ext_org
            result.records_upserted += up_org
            details["organisasi"] = {"extracted": ext_org, "upserted": up_org}

            # 2. Jabatan
            ext_jab, up_jab = sync_jabatan(conn)
            result.records_extracted += ext_jab
            result.records_upserted += up_jab
            details["jabatan"] = {"extracted": ext_jab, "upserted": up_jab}

            # 3. Golongan
            ext_gol, up_gol = sync_golongan(conn)
            result.records_extracted += ext_gol
            result.records_upserted += up_gol
            details["golongan"] = {"extracted": ext_gol, "upserted": up_gol}

            # 4. Jenis Cuti
            ext_cuti, up_cuti = sync_jenis_cuti(conn)
            result.records_extracted += ext_cuti
            result.records_upserted += up_cuti
            details["cuti_jenis"] = {"extracted": ext_cuti, "upserted": up_cuti}

            result.details = details

    except Exception as exc:
        logger.error("Stage 1 failed: %s", exc, exc_info=True)
        result.add_error(f"Stage 1 execution error: {exc}")

    if console and Table:
        table = Table(title="Stage 1: Master Sync Summary", show_header=True)
        table.add_column("Table", style="cyan")
        table.add_column("Extracted", justify="right")
        table.add_column("Upserted", justify="right")

        for tbl, stat in details.items():
            table.add_row(tbl, str(stat["extracted"]), str(stat["upserted"]))
        console.print(table)

    return result


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    c = Console() if Console else None
    res = run_stage1(console=c)
    if not res.success:
        import sys
        sys.exit(1)

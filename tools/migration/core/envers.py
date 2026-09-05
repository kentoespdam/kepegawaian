"""Hibernate Envers baseline audit injection helpers.

Complies with ADR-0051: Injeksi Baseline Revision Hibernate Envers.
Ensures that all initial data inserted via the migration runner receives a global
revision in `revinfo` and baseline audit snapshots with `revtype = 0` (ADD) across
all corresponding `*_aud` audit tables.
"""

from __future__ import annotations

import logging
import time
from typing import Any, Sequence

from tools.migration.core.db import batch_insert, execute_query

logger = logging.getLogger(__name__)

# Envers Revision Types
REVTYPE_ADD = 0
REVTYPE_MOD = 1
REVTYPE_DEL = 2


def create_revision(conn: Any, timestamp_ms: int | None = None) -> int:
    """Creates a global baseline revision entry in `revinfo`.

    Args:
        conn: Active database connection to the target database.
        timestamp_ms: Epoch milliseconds timestamp. Defaults to current time.

    Returns:
        The generated revision identifier (rev).
    """
    if timestamp_ms is None:
        timestamp_ms = int(time.time() * 1000)

    sql = "INSERT INTO revinfo (revtstmp) VALUES (%s)"
    with conn.cursor() as cursor:
        cursor.execute(sql, (timestamp_ms,))
        rev_id = cursor.lastrowid

    logger.info("Created Envers revision %d (timestamp=%d)", rev_id, timestamp_ms)
    return rev_id


def get_latest_revision(conn: Any) -> int | None:
    """Retrieves the latest revision ID from `revinfo`."""
    sql = "SELECT rev FROM revinfo ORDER BY rev DESC LIMIT 1"
    rows = execute_query(conn, sql)
    return rows[0]["rev"] if rows else None


def get_table_columns(conn: Any, table_name: str) -> list[str]:
    """Retrieves column names for a given table in order."""
    if "." in table_name:
        schema, tbl = table_name.split(".", 1)
        sql = f"SHOW COLUMNS FROM `{schema}`.`{tbl}`"
    else:
        sql = f"SHOW COLUMNS FROM `{table_name}`"

    rows = execute_query(conn, sql)
    return [row["Field"] for row in rows]


def inject_audit_snapshot(
    conn: Any,
    aud_table: str,
    rev: int,
    records: list[dict[str, Any]],
    revtype: int = REVTYPE_ADD,
    chunk_size: int = 1000,
) -> int:
    """Injects in-memory record dictionaries into the Envers audit table.

    Each record is augmented with `rev` and `revtype`.

    Args:
        conn: Active database connection to the target database.
        aud_table: Name of the audit table (e.g. `pegawai_aud`).
        rev: Envers revision ID from `revinfo`.
        records: List of entity row dictionaries.
        revtype: Envers revision type (0=ADD, 1=MOD, 2=DEL). Default: 0 (ADD).
        chunk_size: Batch insert chunk size.

    Returns:
        Number of audit records inserted.
    """
    if not records:
        return 0

    # Introspect target audit table columns to safely match available fields
    aud_columns = set(get_table_columns(conn, aud_table))

    audit_records: list[dict[str, Any]] = []
    for rec in records:
        audit_row = {
            col: val for col, val in rec.items() if col in aud_columns
        }
        audit_row["rev"] = rev
        audit_row["revtype"] = revtype
        audit_records.append(audit_row)

    inserted = batch_insert(
        conn=conn,
        table_name=aud_table,
        records=audit_records,
        chunk_size=chunk_size,
    )
    logger.debug(
        "Injected %d audit snapshots into %s (rev=%d, revtype=%d)",
        inserted,
        aud_table,
        rev,
        revtype,
    )
    return inserted


def snapshot_table_to_audit(
    conn: Any,
    source_table: str,
    aud_table: str,
    rev: int,
    id_column: str = "id",
    ids: Sequence[Any] | None = None,
    revtype: int = REVTYPE_ADD,
) -> int:
    """Copies rows directly from the source table to the audit table in SQL.

    Matches shared column names between `source_table` and `aud_table`,
    injecting `rev` and `revtype`.

    Args:
        conn: Active database connection to the target database.
        source_table: Main entity table name (e.g. `pegawai`).
        aud_table: Audit table name (e.g. `pegawai_aud`).
        rev: Envers revision ID from `revinfo`.
        id_column: Primary key column in source table.
        ids: Optional list of IDs to filter. If None, snapshots all rows.
        revtype: Envers revision type (0=ADD).

    Returns:
        Number of audit rows copied.
    """
    source_cols = set(get_table_columns(conn, source_table))
    aud_cols = set(get_table_columns(conn, aud_table))

    # Shared columns between source and aud (excluding rev, revtype)
    common_cols = [c for c in source_cols if c in aud_cols and c not in ("rev", "revtype")]

    if not common_cols:
        raise ValueError(f"No shared columns found between {source_table} and {aud_table}")

    escaped_source_cols = ", ".join(f"`{c}`" for c in common_cols)
    insert_cols = f"{escaped_source_cols}, `rev`, `revtype`"
    select_cols = f"{escaped_source_cols}, %s AS `rev`, %s AS `revtype`"

    where_clause = ""
    params: list[Any] = [rev, revtype]

    if ids is not None:
        if not ids:
            return 0
        total_rowcount = 0
        with conn.cursor() as cursor:
            for i in range(0, len(ids), 1000):
                chunk = ids[i : i + 1000]
                placeholders = ", ".join(["%s"] * len(chunk))
                where_clause = f"WHERE `{id_column}` IN ({placeholders})"
                chunk_params = [rev, revtype] + list(chunk)
                sql = (
                    f"INSERT INTO `{aud_table}` ({insert_cols}) "
                    f"SELECT {select_cols} FROM `{source_table}` {where_clause}"
                )
                cursor.execute(sql, chunk_params)
                total_rowcount += cursor.rowcount

        logger.debug(
            "Copied %d rows from %s to %s (rev=%d, revtype=%d)",
            total_rowcount,
            source_table,
            aud_table,
            rev,
            revtype,
        )
        return total_rowcount

    sql = (
        f"INSERT INTO `{aud_table}` ({insert_cols}) "
        f"SELECT {select_cols} FROM `{source_table}`"
    )

    with conn.cursor() as cursor:
        cursor.execute(sql, [rev, revtype])
        rowcount = cursor.rowcount

    logger.debug(
        "Copied %d rows from %s to %s (rev=%d, revtype=%d)",
        rowcount,
        source_table,
        aud_table,
        rev,
        revtype,
    )
    return rowcount

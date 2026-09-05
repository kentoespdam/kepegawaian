"""State management and ID mapping for data migration.

Maintains the `migration_id_map` table in the target database to preserve
traceability between legacy system primary keys and new system generated IDs.
"""

from __future__ import annotations

import hashlib
import json
import logging
from typing import Any, Sequence

from tools.migration.core.db import batch_upsert, execute_query

logger = logging.getLogger(__name__)

CREATE_STATE_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS migration_id_map (
  domain VARCHAR(64) NOT NULL,
  legacy_table VARCHAR(64) NOT NULL,
  legacy_id VARCHAR(64) NOT NULL,
  new_table VARCHAR(64) NOT NULL,
  new_id VARCHAR(64) NOT NULL,
  record_hash VARCHAR(64) NULL,
  migrated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (domain, legacy_table, legacy_id),
  KEY idx_mig_map_target (domain, new_table, new_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
"""


def init_state_table(conn: Any) -> None:
    """Creates the migration_id_map table if it does not already exist."""
    with conn.cursor() as cursor:
        cursor.execute(CREATE_STATE_TABLE_SQL)
    logger.debug("migration_id_map table initialized.")


def compute_record_hash(record: dict[str, Any]) -> str:
    """Computes a deterministic SHA-256 hash (truncated to 32 hex chars) of record contents."""
    serialized = json.dumps(record, sort_keys=True, default=str)
    return hashlib.sha256(serialized.encode("utf-8")).hexdigest()[:32]


def set_mapping(
    conn: Any,
    domain: str,
    legacy_table: str,
    legacy_id: str | int,
    new_table: str,
    new_id: str | int,
    record_hash: str | None = None,
) -> None:
    """Saves or updates a single ID mapping from legacy system to new system."""
    sql = """
    INSERT INTO migration_id_map (domain, legacy_table, legacy_id, new_table, new_id, record_hash)
    VALUES (%s, %s, %s, %s, %s, %s)
    ON DUPLICATE KEY UPDATE
      new_table = VALUES(new_table),
      new_id = VALUES(new_id),
      record_hash = VALUES(record_hash)
    """
    with conn.cursor() as cursor:
        cursor.execute(
            sql,
            (
                domain,
                legacy_table,
                str(legacy_id),
                new_table,
                str(new_id),
                record_hash,
            ),
        )


def batch_set_mappings(
    conn: Any,
    mappings: list[dict[str, Any]],
    chunk_size: int = 1000,
) -> int:
    """Batch inserts or updates ID mappings.

    Expected dict keys: 'domain', 'legacy_table', 'legacy_id', 'new_table', 'new_id', optional 'record_hash'.
    """
    if not mappings:
        return 0

    sanitized = []
    for item in mappings:
        sanitized.append({
            "domain": item["domain"],
            "legacy_table": item["legacy_table"],
            "legacy_id": str(item["legacy_id"]),
            "new_table": item["new_table"],
            "new_id": str(item["new_id"]),
            "record_hash": item.get("record_hash"),
        })

    return batch_upsert(
        conn=conn,
        table_name="migration_id_map",
        records=sanitized,
        update_columns=["new_table", "new_id", "record_hash"],
        chunk_size=chunk_size,
    )


def get_mapping(
    conn: Any,
    domain: str,
    legacy_table: str,
    legacy_id: str | int,
) -> str | None:
    """Retrieves the new_id corresponding to a legacy record."""
    sql = """
    SELECT new_id FROM migration_id_map
    WHERE domain = %s AND legacy_table = %s AND legacy_id = %s
    LIMIT 1
    """
    rows = execute_query(conn, sql, (domain, legacy_table, str(legacy_id)))
    if rows:
        return str(rows[0]["new_id"])
    return None


def get_mapping_record(
    conn: Any,
    domain: str,
    legacy_table: str,
    legacy_id: str | int,
) -> dict[str, Any] | None:
    """Retrieves the entire mapping row for a legacy record."""
    sql = """
    SELECT domain, legacy_table, legacy_id, new_table, new_id, record_hash, migrated_at
    FROM migration_id_map
    WHERE domain = %s AND legacy_table = %s AND legacy_id = %s
    LIMIT 1
    """
    rows = execute_query(conn, sql, (domain, legacy_table, str(legacy_id)))
    return rows[0] if rows else None


def get_all_mappings(
    conn: Any,
    domain: str | None = None,
    legacy_table: str | None = None,
) -> dict[str, str]:
    """Retrieves all ID mappings as a dictionary mapping legacy_id -> new_id.

    Can be filtered by domain and/or legacy_table.
    """
    conditions: list[str] = []
    params: list[Any] = []

    if domain is not None:
        conditions.append("domain = %s")
        params.append(domain)
    if legacy_table is not None:
        conditions.append("legacy_table = %s")
        params.append(legacy_table)

    where_clause = f"WHERE {' AND '.join(conditions)}" if conditions else ""
    sql = f"SELECT legacy_id, new_id FROM migration_id_map {where_clause}"

    rows = execute_query(conn, sql, params)
    return {str(row["legacy_id"]): str(row["new_id"]) for row in rows}


def has_mapping(
    conn: Any,
    domain: str,
    legacy_table: str,
    legacy_id: str | int,
) -> bool:
    """Checks whether an ID mapping already exists."""
    return get_mapping(conn, domain, legacy_table, legacy_id) is not None

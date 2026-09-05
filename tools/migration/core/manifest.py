"""SQLite File Sync Manifest manager.

Complies with ADR-0052: Two-Phase File Attachment Migration.
Tracks polymorphic legacy attachments to their target hashed paths, enabling
fast metadata migration in Phase 1 followed by resumable, multithreaded
physical file copy in Phase 2.
"""

from __future__ import annotations

import logging
import sqlite3
from contextlib import contextmanager
from datetime import datetime
from pathlib import Path
from typing import Any, Generator, Sequence

from tools.migration.config import config

logger = logging.getLogger(__name__)

# File sync status constants
STATUS_PENDING = "PENDING"
STATUS_SYNCED = "SYNCED"
STATUS_FAILED = "FAILED"
STATUS_SKIPPED = "SKIPPED"

CREATE_MANIFEST_TABLE_SQL = """
CREATE TABLE IF NOT EXISTS file_manifest (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  legacy_rel_path TEXT NOT NULL UNIQUE,
  target_full_path TEXT NOT NULL,
  file_size INTEGER DEFAULT 0,
  mime_type TEXT,
  ref_type TEXT,
  ref_id TEXT,
  status TEXT NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  synced_at TIMESTAMP NULL,
  error_msg TEXT NULL
);

CREATE INDEX IF NOT EXISTS idx_file_manifest_status ON file_manifest (status);
CREATE INDEX IF NOT EXISTS idx_file_manifest_ref ON file_manifest (ref_type, ref_id);
"""


class ManifestManager:
    """Manages reading and writing to the SQLite file sync manifest."""

    def __init__(self, db_path: Path | str | None = None) -> None:
        if db_path is None:
            self.db_path = config.storage.manifest_db_path
        else:
            self.db_path = Path(db_path).resolve()
        self._initialized = False

    def ensure_initialized(self) -> None:
        """Ensures the SQLite schema is created before execution."""
        if not self._initialized:
            self._initialized = True
            self.init_db()

    @contextmanager
    def get_connection(self) -> Generator[sqlite3.Connection, None, None]:
        """Provides a managed SQLite connection with Row factory and WAL mode."""
        self.ensure_initialized()
        self.db_path.parent.mkdir(parents=True, exist_ok=True)
        conn = sqlite3.connect(
            str(self.db_path),
            timeout=30.0,
            detect_types=sqlite3.PARSE_DECLTYPES | sqlite3.PARSE_COLNAMES,
        )
        conn.row_factory = sqlite3.Row
        try:
            conn.execute("PRAGMA journal_mode = WAL;")
            conn.execute("PRAGMA busy_timeout = 5000;")
            yield conn
            conn.commit()
        except Exception:
            conn.rollback()
            raise
        finally:
            conn.close()

    def init_db(self) -> None:
        """Initializes the manifest table and indexes if not already present."""
        with self.get_connection() as conn:
            conn.executescript(CREATE_MANIFEST_TABLE_SQL)
        logger.debug("Manifest SQLite database initialized at %s", self.db_path)

    def add_entry(
        self,
        legacy_rel_path: str,
        target_full_path: str,
        file_size: int = 0,
        mime_type: str | None = None,
        ref_type: str | None = None,
        ref_id: str | None = None,
        status: str = STATUS_PENDING,
    ) -> int:
        """Adds or updates a single file manifest entry."""
        sql = """
        INSERT INTO file_manifest (
          legacy_rel_path, target_full_path, file_size, mime_type, ref_type, ref_id, status
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(legacy_rel_path) DO UPDATE SET
          target_full_path = excluded.target_full_path,
          file_size = excluded.file_size,
          mime_type = excluded.mime_type,
          ref_type = excluded.ref_type,
          ref_id = excluded.ref_id,
          status = excluded.status;
        """
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                sql,
                (
                    legacy_rel_path,
                    target_full_path,
                    file_size,
                    mime_type,
                    ref_type,
                    ref_id,
                    status,
                ),
            )
            return cursor.lastrowid or 0

    def batch_add_entries(
        self,
        entries: list[dict[str, Any]],
        chunk_size: int = 1000,
    ) -> int:
        """Batch inserts or upserts file manifest entries."""
        if not entries:
            return 0

        sql = """
        INSERT INTO file_manifest (
          legacy_rel_path, target_full_path, file_size, mime_type, ref_type, ref_id, status
        ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ON CONFLICT(legacy_rel_path) DO UPDATE SET
          target_full_path = excluded.target_full_path,
          file_size = excluded.file_size,
          mime_type = excluded.mime_type,
          ref_type = excluded.ref_type,
          ref_id = excluded.ref_id;
        """

        total_saved = 0
        with self.get_connection() as conn:
            cursor = conn.cursor()
            for i in range(0, len(entries), chunk_size):
                chunk = entries[i : i + chunk_size]
                params = [
                    (
                        item["legacy_rel_path"],
                        item["target_full_path"],
                        item.get("file_size", 0),
                        item.get("mime_type"),
                        item.get("ref_type"),
                        item.get("ref_id"),
                        item.get("status", STATUS_PENDING),
                    )
                    for item in chunk
                ]
                cursor.executemany(sql, params)
                total_saved += cursor.rowcount

        return total_saved

    def get_pending_entries(
        self,
        limit: int | None = None,
        ref_type: str | None = None,
    ) -> list[dict[str, Any]]:
        """Retrieves list of files waiting to be physically synchronized."""
        conditions = ["status = ?"]
        params: list[Any] = [STATUS_PENDING]

        if ref_type is not None:
            conditions.append("ref_type = ?")
            params.append(ref_type)

        where_clause = f"WHERE {' AND '.join(conditions)}"
        limit_clause = f"LIMIT {int(limit)}" if limit is not None else ""
        sql = f"SELECT * FROM file_manifest {where_clause} ORDER BY id ASC {limit_clause}"

        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(sql, params)
            return [dict(row) for row in cursor.fetchall()]

    def get_entries_by_statuses(
        self,
        statuses: Sequence[str] = (STATUS_PENDING, STATUS_FAILED),
        limit: int | None = None,
        ref_type: str | None = None,
    ) -> list[dict[str, Any]]:
        """Retrieves list of file records matching the specified statuses."""
        if not statuses:
            return []
        placeholders = ", ".join("?" for _ in statuses)
        conditions = [f"status IN ({placeholders})"]
        params: list[Any] = list(statuses)

        if ref_type is not None:
            conditions.append("ref_type = ?")
            params.append(ref_type)

        where_clause = f"WHERE {' AND '.join(conditions)}"
        limit_clause = f"LIMIT {int(limit)}" if limit is not None else ""
        sql = f"SELECT * FROM file_manifest {where_clause} ORDER BY id ASC {limit_clause}"

        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(sql, params)
            return [dict(row) for row in cursor.fetchall()]

    def mark_synced(self, entry_id: int, file_size: int | None = None) -> None:
        """Marks a manifest entry as successfully synchronized."""
        now = datetime.now()
        with self.get_connection() as conn:
            cursor = conn.cursor()
            if file_size is not None:
                cursor.execute(
                    "UPDATE file_manifest SET status = ?, synced_at = ?, file_size = ?, error_msg = NULL WHERE id = ?",
                    (STATUS_SYNCED, now, file_size, entry_id),
                )
            else:
                cursor.execute(
                    "UPDATE file_manifest SET status = ?, synced_at = ?, error_msg = NULL WHERE id = ?",
                    (STATUS_SYNCED, now, entry_id),
                )

    def mark_failed(self, entry_id: int, error_msg: str) -> None:
        """Marks a manifest entry as failed with an error message."""
        with self.get_connection() as conn:
            conn.cursor().execute(
                "UPDATE file_manifest SET status = ?, error_msg = ? WHERE id = ?",
                (STATUS_FAILED, str(error_msg), entry_id),
            )

    def mark_skipped(self, entry_id: int, reason: str = "") -> None:
        """Marks a manifest entry as skipped."""
        with self.get_connection() as conn:
            conn.cursor().execute(
                "UPDATE file_manifest SET status = ?, error_msg = ? WHERE id = ?",
                (STATUS_SKIPPED, reason, entry_id),
            )

    def get_entry_by_legacy_path(self, legacy_rel_path: str) -> dict[str, Any] | None:
        """Looks up a manifest entry by legacy relative path."""
        sql = "SELECT * FROM file_manifest WHERE legacy_rel_path = ? LIMIT 1"
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(sql, (legacy_rel_path,))
            row = cursor.fetchone()
            return dict(row) if row else None

    def reset_failed_entries(self) -> int:
        """Resets all FAILED entries back to PENDING for retry."""
        sql = "UPDATE file_manifest SET status = ?, error_msg = NULL WHERE status = ?"
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(sql, (STATUS_PENDING, STATUS_FAILED))
            return cursor.rowcount

    def get_stats(self) -> dict[str, Any]:
        """Calculates synchronization statistics summary."""
        sql = """
        SELECT
          COUNT(*) AS total_files,
          SUM(file_size) AS total_bytes,
          SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count,
          SUM(CASE WHEN status = 'SYNCED' THEN 1 ELSE 0 END) AS synced_count,
          SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count,
          SUM(CASE WHEN status = 'SKIPPED' THEN 1 ELSE 0 END) AS skipped_count,
          SUM(CASE WHEN status = 'SYNCED' THEN file_size ELSE 0 END) AS synced_bytes
        FROM file_manifest;
        """
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(sql)
            row = cursor.fetchone()
            if not row:
                return {}
            data = dict(row)
            # Replace None with 0 for byte sums
            for k, v in data.items():
                if v is None:
                    data[k] = 0
            return data


# Default manifest instance
manifest_manager = ManifestManager()

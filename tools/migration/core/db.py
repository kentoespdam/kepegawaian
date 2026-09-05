"""Database connection and execution utilities for data migration.

Provides connection context managers for legacy (smartoffice) and target
(kepegawaian_dev_new) MariaDB databases, cross-database query execution,
and high-performance chunked batch insert/upsert helpers.
"""

from __future__ import annotations

import logging
from contextlib import contextmanager
from typing import Any, Generator, Iterable, Sequence

try:
    import pymysql
    from pymysql.connections import Connection
    from pymysql.cursors import DictCursor
except ImportError:
    # Allow importing modules during setup/linting before dependencies are installed
    pymysql = None  # type: ignore[assignment]
    Connection = Any  # type: ignore[misc,assignment]
    DictCursor = Any  # type: ignore[misc,assignment]

from tools.migration.config import DatabaseConfig, config

logger = logging.getLogger(__name__)


def _require_pymysql() -> None:
    """Validates that pymysql is installed."""
    if pymysql is None:
        raise ImportError(
            "pymysql is required for database operations. "
            "Install it via: pip install -r tools/migration/requirements.txt"
        )


def create_connection(
    cfg: DatabaseConfig,
    autocommit: bool = False,
    connect_timeout: int = 30,
) -> Connection:
    """Creates a new MariaDB/MySQL connection with DictCursor."""
    _require_pymysql()
    return pymysql.connect(
        host=cfg.host,
        port=cfg.port,
        user=cfg.user,
        password=cfg.password,
        database=cfg.schema,
        charset=cfg.charset,
        cursorclass=DictCursor,
        autocommit=autocommit,
        connect_timeout=connect_timeout,
    )


@contextmanager
def get_db_connection(
    cfg: DatabaseConfig,
    autocommit: bool = False,
) -> Generator[Connection, None, None]:
    """Context manager yielding a database connection.

    Automatically handles transaction commit or rollback on error,
    and guarantees the connection is properly closed.
    """
    conn = create_connection(cfg, autocommit=autocommit)
    try:
        yield conn
        if not autocommit:
            conn.commit()
    except Exception as exc:
        if not autocommit:
            try:
                conn.rollback()
            except Exception as rollback_err:
                logger.warning("Rollback failed: %s", rollback_err)
        logger.error("Database operation failed on %s: %s", cfg.schema, exc)
        raise
    finally:
        try:
            conn.close()
        except Exception:
            pass


@contextmanager
def get_legacy_connection(
    autocommit: bool = False,
) -> Generator[Connection, None, None]:
    """Context manager for the legacy (smartoffice) database connection."""
    with get_db_connection(config.legacy_db, autocommit=autocommit) as conn:
        yield conn


@contextmanager
def get_target_connection(
    autocommit: bool = False,
) -> Generator[Connection, None, None]:
    """Context manager for the target (kepegawaian_dev_new) database connection."""
    with get_db_connection(config.target_db, autocommit=autocommit) as conn:
        yield conn


@contextmanager
def get_cross_db_connection(
    autocommit: bool = False,
) -> Generator[Connection, None, None]:
    """Context manager for cross-database operations.

    Since user 'dev' has access to both 'smartoffice' and 'kepegawaian_dev_new'
    on the same MariaDB instance, connecting to either allows querying both
    schemas directly (e.g. SELECT ... FROM smartoffice.table JOIN kepegawaian_dev_new.table).
    """
    with get_db_connection(config.target_db, autocommit=autocommit) as conn:
        yield conn


def execute_query(
    conn: Connection,
    query: str,
    params: Sequence[Any] | dict[str, Any] | None = None,
) -> list[dict[str, Any]]:
    """Executes a SELECT query on an active connection and returns all rows as dictionaries."""
    with conn.cursor() as cursor:
        cursor.execute(query, params)
        return cursor.fetchall()


def execute_cross_db_query(
    query: str,
    params: Sequence[Any] | dict[str, Any] | None = None,
    conn: Connection | None = None,
) -> list[dict[str, Any]]:
    """Executes a cross-database query.

    If an active connection is provided, it is used. Otherwise, a temporary
    cross-database connection is acquired and closed automatically.
    """
    if conn is not None:
        return execute_query(conn, query, params)

    with get_cross_db_connection(autocommit=True) as new_conn:
        return execute_query(new_conn, query, params)


def _chunk_iterable(items: Sequence[Any], chunk_size: int) -> Iterable[Sequence[Any]]:
    """Splits a sequence into chunks of maximum size chunk_size."""
    for i in range(0, len(items), chunk_size):
        yield items[i : i + chunk_size]


def batch_insert(
    conn: Connection,
    table_name: str,
    records: list[dict[str, Any]],
    chunk_size: int = 1000,
    ignore_duplicates: bool = False,
) -> int:
    """Inserts a batch of records into a table using multi-row chunked INSERTs.

    Args:
        conn: Active database connection.
        table_name: Target table name (can be schema-qualified e.g. `schema.table`).
        records: List of dictionaries mapping column name -> value.
        chunk_size: Number of records per multi-row INSERT statement.
        ignore_duplicates: If True, uses INSERT IGNORE.

    Returns:
        Total number of rows affected.
    """
    if not records:
        return 0

    columns = list(records[0].keys())
    escaped_cols = ", ".join(f"`{col}`" for col in columns)
    ignore_clause = "IGNORE " if ignore_duplicates else ""

    # Qualifying schema and table safely
    if "." in table_name:
        schema, tbl = table_name.split(".", 1)
        qualified_table = f"`{schema}`.`{tbl}`"
    else:
        qualified_table = f"`{table_name}`"

    row_placeholder = f"({', '.join(['%s'] * len(columns))})"
    total_affected = 0

    with conn.cursor() as cursor:
        for chunk in _chunk_iterable(records, chunk_size):
            placeholders = ", ".join([row_placeholder] * len(chunk))
            sql = f"INSERT {ignore_clause}INTO {qualified_table} ({escaped_cols}) VALUES {placeholders}"
            flat_params = [row.get(col) for row in chunk for col in columns]
            cursor.execute(sql, flat_params)
            total_affected += cursor.rowcount

    return total_affected


def batch_upsert(
    conn: Connection,
    table_name: str,
    records: list[dict[str, Any]],
    update_columns: list[str] | None = None,
    chunk_size: int = 1000,
) -> int:
    """Inserts or updates a batch of records using ON DUPLICATE KEY UPDATE.

    Args:
        conn: Active database connection.
        table_name: Target table name.
        records: List of dictionaries mapping column name -> value.
        update_columns: List of columns to update on duplicate key. If None,
                        updates all columns except those in primary/unique key.
        chunk_size: Number of records per chunk.

    Returns:
        Total number of rows affected.
    """
    if not records:
        return 0

    columns = list(records[0].keys())
    escaped_cols = ", ".join(f"`{col}`" for col in columns)

    if update_columns is None:
        update_columns = columns

    update_clause = ", ".join(f"`{col}` = VALUES(`{col}`)" for col in update_columns)

    if "." in table_name:
        schema, tbl = table_name.split(".", 1)
        qualified_table = f"`{schema}`.`{tbl}`"
    else:
        qualified_table = f"`{table_name}`"

    row_placeholder = f"({', '.join(['%s'] * len(columns))})"
    total_affected = 0

    with conn.cursor() as cursor:
        for chunk in _chunk_iterable(records, chunk_size):
            placeholders = ", ".join([row_placeholder] * len(chunk))
            sql = (
                f"INSERT INTO {qualified_table} ({escaped_cols}) VALUES {placeholders} "
                f"ON DUPLICATE KEY UPDATE {update_clause}"
            )
            flat_params = [row.get(col) for row in chunk for col in columns]
            cursor.execute(sql, flat_params)
            total_affected += cursor.rowcount

    return total_affected

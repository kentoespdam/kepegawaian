"""Stage 0: Preflight & Environment Check.

Mengecek konektivitas dan kesiapan seluruh subsistem sebelum pipeline migrasi berjalan:
1. Pengecekan koneksi ke database legacy (smartoffice) dan database target (kepegawaian_dev_new).
2. Pengecekan konektivitas HTTP ke Appwrite endpoint.
3. Memastikan user 'dev' memiliki hak akses cross-database query.
4. Menginisialisasi tabel `migration_id_map` di database target melalui `core.state.init_state_table`.
5. Mengembalikan status preflight (sukses / gagal beserta detail error).
"""

from __future__ import annotations

import logging
import sys
from dataclasses import dataclass, field
from typing import Any, Optional

try:
    import requests
except ImportError:
    requests = None  # type: ignore[assignment]

try:
    from rich.console import Console
    from rich.panel import Panel
    from rich.table import Table
except ImportError:
    Console = None  # type: ignore[misc,assignment]
    Panel = None  # type: ignore[misc,assignment]
    Table = None  # type: ignore[misc,assignment]

from tools.migration.config import config
from tools.migration.core.db import (
    execute_query,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.state import init_state_table

logger = logging.getLogger(__name__)


@dataclass
class PreflightCheckResult:
    """Represents the outcome of a single preflight verification check."""

    name: str
    passed: bool
    message: str
    details: dict[str, Any] = field(default_factory=dict)
    is_warning: bool = False


@dataclass
class PreflightSummary:
    """Aggregated outcome of all preflight checks."""

    success: bool
    checks: list[PreflightCheckResult] = field(default_factory=list)
    errors: list[str] = field(default_factory=list)

    def add_check(self, check: PreflightCheckResult) -> None:
        self.checks.append(check)
        if not check.passed and not check.is_warning:
            self.success = False
            self.errors.append(f"[{check.name}] {check.message}")


def check_legacy_db_connection() -> PreflightCheckResult:
    """Verifies connectivity to the legacy (smartoffice) MariaDB database."""
    cfg = config.legacy_db
    try:
        with get_legacy_connection(autocommit=True) as conn:
            rows = execute_query(
                conn,
                "SELECT 1 AS ok, DATABASE() AS db_name, VERSION() AS db_version",
            )
            if not rows or rows[0].get("ok") != 1:
                return PreflightCheckResult(
                    name="Legacy DB Connection",
                    passed=False,
                    message="Query validation failed on legacy database",
                    details={"host": cfg.host, "port": cfg.port, "schema": cfg.schema},
                )

            db_name = rows[0].get("db_name")
            db_ver = rows[0].get("db_version")

            # Check table counts
            tbl_rows = execute_query(conn, "SHOW TABLES")
            table_count = len(tbl_rows)

            return PreflightCheckResult(
                name="Legacy DB Connection",
                passed=True,
                message=f"Connected to legacy DB '{db_name}' ({db_ver}) with {table_count} tables",
                details={
                    "host": cfg.host,
                    "port": cfg.port,
                    "schema": db_name,
                    "version": db_ver,
                    "table_count": table_count,
                },
            )
    except Exception as exc:
        logger.error("Legacy DB connection failed: %s", exc)
        return PreflightCheckResult(
            name="Legacy DB Connection",
            passed=False,
            message=f"Cannot connect to legacy DB: {exc}",
            details={"host": cfg.host, "port": cfg.port, "schema": cfg.schema, "error": str(exc)},
        )


def check_target_db_connection() -> PreflightCheckResult:
    """Verifies connectivity to the target (kepegawaian_dev_new) MariaDB database."""
    cfg = config.target_db
    try:
        with get_target_connection(autocommit=True) as conn:
            rows = execute_query(
                conn,
                "SELECT 1 AS ok, DATABASE() AS db_name, VERSION() AS db_version",
            )
            if not rows or rows[0].get("ok") != 1:
                return PreflightCheckResult(
                    name="Target DB Connection",
                    passed=False,
                    message="Query validation failed on target database",
                    details={"host": cfg.host, "port": cfg.port, "schema": cfg.schema},
                )

            db_name = rows[0].get("db_name")
            db_ver = rows[0].get("db_version")

            tbl_rows = execute_query(conn, "SHOW TABLES")
            table_count = len(tbl_rows)

            return PreflightCheckResult(
                name="Target DB Connection",
                passed=True,
                message=f"Connected to target DB '{db_name}' ({db_ver}) with {table_count} tables",
                details={
                    "host": cfg.host,
                    "port": cfg.port,
                    "schema": db_name,
                    "version": db_ver,
                    "table_count": table_count,
                },
            )
    except Exception as exc:
        logger.error("Target DB connection failed: %s", exc)
        return PreflightCheckResult(
            name="Target DB Connection",
            passed=False,
            message=f"Cannot connect to target DB: {exc}",
            details={"host": cfg.host, "port": cfg.port, "schema": cfg.schema, "error": str(exc)},
        )


def check_cross_db_permissions() -> PreflightCheckResult:
    """Verifies that user 'dev' has cross-database SELECT privileges.

    Tests querying the legacy database schema (`smartoffice`) directly from
    a connection established to the target database (`kepegawaian_dev_new`).
    """
    legacy_schema = config.legacy_db.schema
    try:
        with get_target_connection(autocommit=True) as conn:
            # Query a known table in legacy schema
            query = f"SELECT COUNT(*) AS total_org FROM `{legacy_schema}`.`organization`"
            rows = execute_query(conn, query)
            count = rows[0]["total_org"] if rows else 0

            return PreflightCheckResult(
                name="Cross-Database Query Permissions",
                passed=True,
                message=f"Cross-database access verified ({count} rows read from {legacy_schema}.organization)",
                details={"legacy_schema": legacy_schema, "sample_count": count},
            )
    except Exception as exc:
        logger.error("Cross-database query permission check failed: %s", exc)
        return PreflightCheckResult(
            name="Cross-Database Query Permissions",
            passed=False,
            message=(
                f"User '{config.target_db.user}' cannot query `{legacy_schema}` "
                f"from `{config.target_db.schema}` connection: {exc}. "
                f"Ensure GRANT SELECT ON `{legacy_schema}`.* TO '{config.target_db.user}'@'%' is applied."
            ),
            details={"target_user": config.target_db.user, "error": str(exc)},
        )


def check_appwrite_connectivity() -> PreflightCheckResult:
    """Verifies HTTP connectivity to Appwrite Auth REST API endpoint."""
    if requests is None:
        return PreflightCheckResult(
            name="Appwrite HTTP Connectivity",
            passed=False,
            message="Python package 'requests' is not installed",
            is_warning=False,
        )

    appwrite = config.appwrite
    endpoint = appwrite.endpoint.rstrip("/")
    health_url = f"{endpoint}/health"
    users_url = f"{endpoint}/users"

    # Attempt health endpoint first, then users endpoint
    try:
        resp = requests.get(health_url, timeout=5)
        if resp.status_code == 200:
            return PreflightCheckResult(
                name="Appwrite HTTP Connectivity",
                passed=True,
                message=f"Appwrite health OK (status 200) at {endpoint}",
                details={"endpoint": endpoint, "status_code": resp.status_code},
            )
    except Exception:
        pass

    # Fallback to users endpoint with auth headers
    try:
        resp = requests.get(
            f"{users_url}?limit=1",
            headers=appwrite.headers,
            timeout=5,
        )
        if resp.status_code in (200, 401, 403):
            passed = resp.status_code == 200
            msg = (
                f"Appwrite endpoint reachable (status {resp.status_code})"
                if passed
                else f"Appwrite reachable but auth returned {resp.status_code} (check APPWRITE_API_KEY)"
            )
            return PreflightCheckResult(
                name="Appwrite HTTP Connectivity",
                passed=passed,
                message=msg,
                details={"endpoint": endpoint, "status_code": resp.status_code},
                is_warning=not passed,
            )
        else:
            return PreflightCheckResult(
                name="Appwrite HTTP Connectivity",
                passed=False,
                message=f"Appwrite returned unexpected status {resp.status_code}",
                details={"endpoint": endpoint, "status_code": resp.status_code},
                is_warning=True,
            )
    except Exception as exc:
        logger.warning("Appwrite connectivity check failed: %s", exc)
        return PreflightCheckResult(
            name="Appwrite HTTP Connectivity",
            passed=False,
            message=f"Cannot reach Appwrite endpoint {endpoint}: {exc}",
            details={"endpoint": endpoint, "error": str(exc)},
            is_warning=True,  # Non-blocking warning for early stages 0-3
        )


def check_and_init_state_table() -> PreflightCheckResult:
    """Initializes the migration_id_map tracking table in the target database."""
    try:
        with get_target_connection(autocommit=True) as conn:
            init_state_table(conn)
            rows = execute_query(conn, "SELECT COUNT(*) AS total FROM migration_id_map")
            existing_mappings = rows[0]["total"] if rows else 0

            return PreflightCheckResult(
                name="Migration ID Map Initialization",
                passed=True,
                message=f"Table 'migration_id_map' ready with {existing_mappings} existing mappings",
                details={"table": "migration_id_map", "existing_mappings": existing_mappings},
            )
    except Exception as exc:
        logger.error("Failed to initialize migration_id_map table: %s", exc)
        return PreflightCheckResult(
            name="Migration ID Map Initialization",
            passed=False,
            message=f"Failed to create/verify migration_id_map table: {exc}",
            details={"error": str(exc)},
        )


def run_stage0(console: Optional[Any] = None) -> PreflightSummary:
    """Executes all Stage 0 preflight checks and returns aggregated summary.

    Args:
        console: Optional rich.console.Console instance for pretty terminal rendering.

    Returns:
        PreflightSummary indicating overall success and individual check details.
    """
    summary = PreflightSummary(success=True)

    checks = [
        check_legacy_db_connection(),
        check_target_db_connection(),
        check_cross_db_permissions(),
        check_appwrite_connectivity(),
        check_and_init_state_table(),
    ]

    for check in checks:
        summary.add_check(check)

    if console and Table and Panel:
        table = Table(title="Stage 0: Preflight & Environment Verification", show_header=True)
        table.add_column("Check", style="cyan", width=35)
        table.add_column("Status", width=12)
        table.add_column("Details", style="white")

        for c in summary.checks:
            if c.passed:
                status_str = "[green]PASSED[/green]"
            elif c.is_warning:
                status_str = "[yellow]WARNING[/yellow]"
            else:
                status_str = "[red]FAILED[/red]"
            table.add_row(c.name, status_str, c.message)

        console.print(table)
        if summary.success:
            console.print(Panel("[bold green]Preflight checks completed successfully![/bold green]"))
        else:
            console.print(
                Panel(
                    f"[bold red]Preflight checks failed with {len(summary.errors)} critical errors.[/bold red]\n"
                    + "\n".join(f"- {e}" for e in summary.errors),
                    title="Preflight Error",
                )
            )

    return summary


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    c = Console() if Console else None
    res = run_stage0(console=c)
    if not res.success:
        sys.exit(1)

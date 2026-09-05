"""Stage 7: Appwrite Auth Provisioning Migration Runner.

Complies with ADR-0053: Sinkronisasi Terautomasi Akun Appwrite Auth dan Pengelolaan Lifecycle Pengguna.
- Reads employees from kepegawaian_dev_new.pegawai joined with biodata.
- For active employees (status_kerja IN (1, 2)):
    * If account does not exist in Appwrite:
        - userId = str(pegawai.id)
        - email = f"{nipam}@perumdamts.com"
        - name = biodata.nama
        - password = "tirtasatria"
        - prefs = {"roles": ["USER"]}
    * If account already exists: preserves credentials and does not reset password (idempotent).
- For inactive / retired / terminated employees (status_kerja == 0):
    * If account exists in Appwrite and status is active (True):
        - sets status = False (blocked) pursuant to ADR-0039.
- Uses REST API calls via requests with error handling, timeout, and rate-limit backoff.
"""

from __future__ import annotations

import csv
import logging
import time
from dataclasses import dataclass
from pathlib import Path
from typing import Any

try:
    import requests
    from requests.adapters import HTTPAdapter
    from urllib3.util.retry import Retry
except ImportError:
    requests = None  # type: ignore[assignment]
    HTTPAdapter = None  # type: ignore[assignment,misc]
    Retry = None  # type: ignore[assignment,misc]

from tools.migration.config import AppwriteConfig, config
from tools.migration.core.db import execute_query, get_target_connection

logger = logging.getLogger(__name__)

DOMAIN_NAME = "auth"
DEFAULT_PASSWORD = "tirtasatria"
DEFAULT_ROLES = ["USER"]


@dataclass
class AppwriteUserSummary:
    """Statistics and audit log container for Appwrite synchronization."""

    total_evaluated: int = 0
    active_created: int = 0
    active_preserved: int = 0
    retired_blocked: int = 0
    retired_skipped: int = 0
    errors: int = 0


class AppwriteClient:
    """High-level REST API client for Appwrite Users service."""

    def __init__(self, cfg: AppwriteConfig, timeout: float = 10.0) -> None:
        self.endpoint = cfg.endpoint.rstrip("/")
        self.headers = cfg.headers
        self.timeout = timeout

        self.session = requests.Session()
        retries = Retry(
            total=3,
            backoff_factor=1.0,
            status_forcelist=[429, 500, 502, 503, 504],
            allowed_methods=["GET", "POST", "PATCH"],
        )
        self.session.mount("http://", HTTPAdapter(max_retries=retries))
        self.session.mount("https://", HTTPAdapter(max_retries=retries))

    def get_user(self, user_id: str) -> dict[str, Any] | None:
        """Retrieves a single user by ID. Returns None if 404 (not found)."""
        url = f"{self.endpoint}/users/{user_id}"
        try:
            resp = self.session.get(url, headers=self.headers, timeout=self.timeout)
            if resp.status_code == 200:
                return resp.json()
            elif resp.status_code == 404:
                return None
            else:
                resp.raise_for_status()
                return None
        except requests.exceptions.HTTPError as err:
            if err.response is not None and err.response.status_code == 404:
                return None
            raise

    def list_all_users(self, page_size: int = 100) -> dict[str, dict[str, Any]]:
        """Pre-fetches all existing users from Appwrite to optimize batch checks.

        Returns:
            Dictionary mapping user_id -> user_dict.
        """
        user_map: dict[str, dict[str, Any]] = {}
        offset = 0

        while True:
            url = f"{self.endpoint}/users"
            # Appwrite pagination query parameters
            params = {
                "queries[]": [f"limit({page_size})", f"offset({offset})"]
            }
            try:
                resp = self.session.get(
                    url,
                    headers=self.headers,
                    params=params,
                    timeout=self.timeout,
                )
                if resp.status_code != 200:
                    logger.debug("Failed listing users via queries, falling back to individual checks")
                    break

                data = resp.json()
                users = data.get("users", [])
                if not users:
                    break

                for u in users:
                    u_id = str(u.get("$id") or u.get("id"))
                    user_map[u_id] = u

                if len(users) < page_size:
                    break

                offset += page_size
            except Exception as exc:
                logger.debug("Listing users from Appwrite failed (%s), will check per-user", exc)
                break

        return user_map

    def create_user(
        self,
        user_id: str,
        email: str,
        name: str,
        password: str = DEFAULT_PASSWORD,
    ) -> dict[str, Any]:
        """Creates a new user in Appwrite."""
        url = f"{self.endpoint}/users"
        payload = {
            "userId": user_id,
            "email": email,
            "password": password,
            "name": name,
        }
        resp = self.session.post(url, headers=self.headers, json=payload, timeout=self.timeout)
        resp.raise_for_status()
        return resp.json()

    def update_user_prefs(self, user_id: str, prefs: dict[str, Any]) -> dict[str, Any]:
        """Updates user preferences (e.g. roles)."""
        url = f"{self.endpoint}/users/{user_id}/prefs"
        resp = self.session.patch(
            url,
            headers=self.headers,
            json={"prefs": prefs},
            timeout=self.timeout,
        )
        resp.raise_for_status()
        return resp.json()

    def update_user_status(self, user_id: str, status: bool) -> dict[str, Any]:
        """Updates account active/blocked status."""
        url = f"{self.endpoint}/users/{user_id}/status"
        resp = self.session.patch(
            url,
            headers=self.headers,
            json={"status": status},
            timeout=self.timeout,
        )
        resp.raise_for_status()
        return resp.json()

    # Backwards compatibility alias
    set_user_status = update_user_status


def sync_appwrite_accounts(
    target_conn: Any,
    client: AppwriteClient,
    dry_run: bool = False,
    export_csv_path: Path | str | None = None,
) -> AppwriteUserSummary:
    """Performs the full Appwrite Auth account synchronization.

    1. Reads active (status_kerja IN (1, 2)) and retired/terminated (status_kerja == 0) employees.
    2. Provisions missing active accounts with default password and USER role.
    3. Blocks retired employees who have active accounts in Appwrite.
    """
    logger.info("Fetching employee data from target database...")

    sql_pegawai = """
    SELECT
        p.id AS pegawai_id,
        p.nipam,
        p.status_kerja,
        COALESCE(b.nama, '') AS nama_lengkap
    FROM pegawai p
    LEFT JOIN biodata b ON p.biodata_id = b.nik
    WHERE p.is_deleted = 0
    ORDER BY p.id ASC
    """
    employees = execute_query(target_conn, sql_pegawai)
    logger.info("Loaded %d employees from target database for Appwrite Auth sync", len(employees))

    summary = AppwriteUserSummary(total_evaluated=len(employees))
    audit_log: list[dict[str, Any]] = []

    # Attempt pre-fetching existing Appwrite users in bulk
    existing_appwrite_users = client.list_all_users()
    logger.info("Pre-fetched %d existing users from Appwrite", len(existing_appwrite_users))

    for emp in employees:
        p_id = str(emp["pegawai_id"])
        nipam = str(emp.get("nipam") or "").strip()
        raw_status = emp.get("status_kerja")
        status_kerja = int(raw_status) if raw_status is not None else 2
        nama = str(emp.get("nama_lengkap") or "").strip() or f"Pegawai {nipam}"
        email = f"{nipam}@perumdamts.com"

        action = "NONE"
        status_note = ""

        try:
            # Check existing user
            existing_user = existing_appwrite_users.get(p_id)
            if existing_user is None and not existing_appwrite_users:
                # If bulk list wasn't populated, do individual GET
                existing_user = client.get_user(p_id)

            # Retired / Terminated Employee: status_kerja == 0
            if status_kerja == 0:
                if existing_user is not None:
                    is_currently_active = existing_user.get("status", True)
                    if is_currently_active:
                        if not dry_run:
                            client.update_user_status(p_id, status=False)
                        summary.retired_blocked += 1
                        action = "BLOCKED"
                        status_note = "Retired employee account blocked (ADR-0039)"
                    else:
                        summary.retired_skipped += 1
                        action = "ALREADY_BLOCKED"
                        status_note = "Retired account already disabled"
                else:
                    summary.retired_skipped += 1
                    action = "SKIPPED_NOT_FOUND"
                    status_note = "Retired employee has no Appwrite account"

            # Active Employee: status_kerja IN (1, 2)
            elif status_kerja in (1, 2):
                if existing_user is not None:
                    # Idempotent: preserve credentials and existing password
                    if not dry_run and not (existing_user.get("prefs") or {}).get("roles"):
                        try:
                            client.update_user_prefs(p_id, {"roles": DEFAULT_ROLES})
                        except Exception as pref_err:
                            logger.warning("Failed setting prefs for user %s: %s", p_id, pref_err)

                    summary.active_preserved += 1
                    action = "PRESERVED"
                    status_note = "User already exists; password unchanged"
                else:
                    if not dry_run:
                        client.create_user(
                            user_id=p_id,
                            email=email,
                            name=nama,
                            password=DEFAULT_PASSWORD,
                        )
                        # Set default preferences/roles
                        try:
                            client.update_user_prefs(p_id, {"roles": DEFAULT_ROLES})
                        except Exception as pref_err:
                            logger.warning("Failed setting prefs for user %s: %s", p_id, pref_err)

                    summary.active_created += 1
                    action = "CREATED"
                    status_note = "Provisioned new active user with default password"

            else:
                # Other non-active status (e.g. candidates/rejected)
                if existing_user is not None:
                    is_currently_active = existing_user.get("status", True)
                    if is_currently_active:
                        if not dry_run:
                            client.update_user_status(p_id, status=False)
                        summary.retired_blocked += 1
                        action = "BLOCKED"
                        status_note = f"Non-active employee account blocked (status_kerja={status_kerja})"
                    else:
                        summary.retired_skipped += 1
                        action = "ALREADY_BLOCKED"
                        status_note = "Non-active account already disabled"
                else:
                    summary.retired_skipped += 1
                    action = "SKIPPED_NOT_FOUND"
                    status_note = "Non-active employee has no Appwrite account"

        except Exception as exc:
            summary.errors += 1
            action = "ERROR"
            status_note = str(exc)
            logger.error("Error processing Appwrite user %s (%s): %s", p_id, nipam, exc)

        audit_log.append({
            "pegawai_id": p_id,
            "nipam": nipam,
            "nama": nama,
            "status_kerja": status_kerja,
            "action": action,
            "notes": status_note,
        })

    # Export audit summary to CSV if requested
    if export_csv_path:
        csv_file = Path(export_csv_path).resolve()
        csv_file.parent.mkdir(parents=True, exist_ok=True)
        try:
            with open(csv_file, "w", newline="", encoding="utf-8") as f:
                writer = csv.DictWriter(
                    f,
                    fieldnames=["pegawai_id", "nipam", "nama", "status_kerja", "action", "notes"],
                )
                writer.writeheader()
                writer.writerows(audit_log)
            logger.info("Saved Appwrite sync audit log to %s", csv_file)
        except Exception as csv_err:
            logger.warning("Failed writing CSV audit log: %s", csv_err)

    return summary


def run_stage7_auth(
    target_conn: Any | None = None,
    client: AppwriteClient | None = None,
    dry_run: bool = False,
    export_csv: bool = True,
    console: Any | None = None,
) -> dict[str, Any]:
    """Entrypoint for Stage 7: Appwrite Auth Provisioning Migration (ADR-0053).

    Args:
        target_conn: Connection to kepegawaian_dev_new database. If None, acquires one.
        client: AppwriteClient instance. If None, initializes from MigrationConfig.
        dry_run: If True, evaluates employees without calling mutating Appwrite endpoints.
        export_csv: If True, exports audit_auth_sync.csv log.
        console: Optional rich console for display.

    Returns:
        Summary metrics dictionary.
    """
    logger.info("============================================================")
    logger.info("Executing Stage 7: Appwrite Auth Provisioning (ADR-0053)")
    logger.info("============================================================")

    if target_conn is None:
        with get_target_connection(autocommit=False) as managed_conn:
            return run_stage7_auth(
                target_conn=managed_conn,
                client=client,
                dry_run=dry_run,
                export_csv=export_csv,
                console=console,
            )

    if client is None:
        client = AppwriteClient(config.appwrite)

    csv_path = Path("audit_auth_sync.csv") if export_csv else None

    summary = sync_appwrite_accounts(
        target_conn=target_conn,
        client=client,
        dry_run=dry_run,
        export_csv_path=csv_path,
    )

    result = {
        "stage": 7,
        "domain": DOMAIN_NAME,
        "dry_run": dry_run,
        "total_evaluated": summary.total_evaluated,
        "active_created": summary.active_created,
        "active_preserved": summary.active_preserved,
        "retired_blocked": summary.retired_blocked,
        "retired_skipped": summary.retired_skipped,
        "errors": summary.errors,
        "status": "COMPLETED",
    }
    logger.info("Stage 7 completed successfully: %s", result)
    return result

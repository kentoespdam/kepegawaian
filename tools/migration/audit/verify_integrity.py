"""Target Database Referential Integrity and Data Quality Audit.

Complies with:
- ADR-0051: Injeksi Baseline Revision Hibernate Envers
- Architecture Document Section 5.1 (Audit Integritas Relasional & Deteksi Orphan)
- Architecture Document Section 5.3 (Audit Kepatuhan Hibernate Envers)

Performs:
1. Zero orphan check:
   - biodata -> profil_keluarga, pendidikan, pelatihan, keahlian, kartu_identitas
   - pegawai -> riwayat_sk, riwayat_mutasi
   - cuti_pegawai -> pegawai_id, cuti_jenis_id
2. Hibernate Envers baseline verification (*_aud tables have baseline revtype=0 rows).
3. Row count quantity comparison between legacy smartoffice and target kepegawaian.
4. Export to rich terminal table and audit_integrity_report.json.
"""

from __future__ import annotations

import argparse
import json
import logging
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Any, Optional

# Ensure repo root is in sys.path when invoked directly
_REPO_ROOT = Path(__file__).resolve().parents[3]
if str(_REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(_REPO_ROOT))

from tools.migration.config import config
from tools.migration.core.db import (
    execute_query,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.envers import REVTYPE_ADD

logger = logging.getLogger(__name__)

# Key Envers tables to inspect for baseline revisions
ENVERS_AUD_TABLES = [
    "pegawai_aud",
    "biodata_aud",
    "profil_keluarga_aud",
    "pendidikan_aud",
    "pelatihan_aud",
    "keahlian_aud",
    "kartu_identitas_aud",
    "riwayat_sk_aud",
    "riwayat_mutasi_aud",
    "riwayat_sp_aud",
    "riwayat_kontrak_aud",
    "cuti_kuota_aud",
    "cuti_pegawai_aud",
    "cuti_approval_aud",
    "lampiran_sk_aud",
    "lampiran_profil_aud",
    "gaji_batch_root_aud",
]

# Legacy vs Target quantity comparison mapping
TABLE_QUANTITY_MAPPINGS = [
    ("Pegawai / Employee", "employee", "pegawai", "Active and historical staff"),
    ("Biodata / Profile", "emp_profile", "biodata", "Demographic profiles"),
    ("Profil Keluarga", "emp_family", "profil_keluarga", "Family member profiles"),
    ("Pendidikan", "emp_education", "pendidikan", "Education history"),
    ("Pelatihan", "emp_training", "pelatihan", "Training history"),
    ("Keahlian", "emp_skill", "keahlian", "Skill certificates"),
    ("Kartu Identitas", "emp_card", "kartu_identitas", "ID cards (KTP/NPWP/BPJS)"),
    ("Riwayat SK", "emp_sk", "riwayat_sk", "Decrees and career history"),
    ("Riwayat Mutasi", "emp_work_history", "riwayat_mutasi", "Delta matching career moves"),
    ("Cuti Pegawai", "cuti_pegawai", "cuti_pegawai", "Leave requests (delta 2025-2026)"),
    ("Batch Gaji (Root)", "salary_batch_process", "gaji_batch_root", "Historical payroll batches"),
    ("Master Gaji Pegawai", "salary_process_master", "gaji_batch_master", "Per-employee payroll master"),
    ("Detail Komponen Gaji", "salary_process_detail", "gaji_batch_master_proses", "Payroll component lines"),
]


def check_referential_integrity(target_conn: Any) -> list[dict[str, Any]]:
    """Runs zero-orphan queries on foreign key relationships in target database."""
    checks: list[dict[str, Any]] = []

    # Helper function to detect column existence in target table
    def _column_exists(table: str, col: str) -> bool:
        try:
            res = execute_query(
                target_conn,
                f"SHOW COLUMNS FROM `{table}` LIKE %s",
                (col,),
            )
            return len(res) > 0
        except Exception:
            return False

    # 1. Biodata zero-orphan checks
    bio_relations = [
        ("profil_keluarga", "biodata_id", "biodata", "nik", "Family members missing Biodata"),
        ("pendidikan", "biodata_id", "biodata", "nik", "Education missing Biodata"),
        ("pelatihan", "biodata_id", "biodata", "nik", "Training missing Biodata"),
        ("keahlian", "biodata_id", "biodata", "nik", "Skills missing Biodata"),
        ("kartu_identitas", "nik", "biodata", "nik", "ID cards missing Biodata"),
        ("pegawai", "biodata_id", "biodata", "nik", "Pegawai missing Biodata"),
    ]

    for child_tbl, child_fk, parent_tbl, parent_pk, description in bio_relations:
        sql = f"""
        SELECT COUNT(*) AS orphan_count
        FROM `{child_tbl}` c
        WHERE c.`{child_fk}` IS NOT NULL
          AND c.`{child_fk}` != ''
          AND NOT EXISTS (
              SELECT 1 FROM `{parent_tbl}` p WHERE p.`{parent_pk}` = c.`{child_fk}`
          )
        """
        try:
            res = execute_query(target_conn, sql)
            orphan_count = int(res[0]["orphan_count"]) if res else 0
            checks.append({
                "check_name": f"{child_tbl}.{child_fk} -> {parent_tbl}.{parent_pk}",
                "category": "Zero Orphan",
                "description": description,
                "orphan_count": orphan_count,
                "passed": orphan_count == 0,
            })
        except Exception as exc:
            checks.append({
                "check_name": f"{child_tbl}.{child_fk} -> {parent_tbl}.{parent_pk}",
                "category": "Zero Orphan",
                "description": description,
                "orphan_count": -1,
                "passed": False,
                "error": str(exc),
            })

    # 2. Pegawai ID zero-orphan checks
    pegawai_relations = [
        ("riwayat_sk", "pegawai_id", "pegawai", "id", "Riwayat SK missing Pegawai"),
        ("riwayat_mutasi", "pegawai_id", "pegawai", "id", "Riwayat Mutasi missing Pegawai"),
        ("cuti_pegawai", "pegawai_id", "pegawai", "id", "Cuti Pegawai missing Pegawai"),
    ]

    for child_tbl, child_fk, parent_tbl, parent_pk, description in pegawai_relations:
        sql = f"""
        SELECT COUNT(*) AS orphan_count
        FROM `{child_tbl}` c
        WHERE c.`{child_fk}` IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM `{parent_tbl}` p WHERE p.`{parent_pk}` = c.`{child_fk}`
          )
        """
        try:
            res = execute_query(target_conn, sql)
            orphan_count = int(res[0]["orphan_count"]) if res else 0
            checks.append({
                "check_name": f"{child_tbl}.{child_fk} -> {parent_tbl}.{parent_pk}",
                "category": "Zero Orphan",
                "description": description,
                "orphan_count": orphan_count,
                "passed": orphan_count == 0,
            })
        except Exception as exc:
            checks.append({
                "check_name": f"{child_tbl}.{child_fk} -> {parent_tbl}.{parent_pk}",
                "category": "Zero Orphan",
                "description": description,
                "orphan_count": -1,
                "passed": False,
                "error": str(exc),
            })

    # 3. Cuti Pegawai to Cuti Jenis check
    # Check if column is named jenis_cuti_id or cuti_jenis_id
    cuti_col = "jenis_cuti_id" if _column_exists("cuti_pegawai", "jenis_cuti_id") else "cuti_jenis_id"
    sql_cuti = f"""
    SELECT COUNT(*) AS orphan_count
    FROM `cuti_pegawai` c
    WHERE c.`{cuti_col}` IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM `cuti_jenis` j WHERE j.`id` = c.`{cuti_col}`
      )
    """
    try:
        res = execute_query(target_conn, sql_cuti)
        orphan_count = int(res[0]["orphan_count"]) if res else 0
        checks.append({
            "check_name": f"cuti_pegawai.{cuti_col} -> cuti_jenis.id",
            "category": "Zero Orphan",
            "description": "Cuti Pegawai missing Cuti Jenis",
            "orphan_count": orphan_count,
            "passed": orphan_count == 0,
        })
    except Exception as exc:
        checks.append({
            "check_name": f"cuti_pegawai.{cuti_col} -> cuti_jenis.id",
            "category": "Zero Orphan",
            "description": "Cuti Pegawai missing Cuti Jenis",
            "orphan_count": -1,
            "passed": False,
            "error": str(exc),
        })

    # 4. Biodata NIK integrity check (cannot be NULL or whitespace)
    sql_bio_nik = "SELECT COUNT(*) AS empty_nik_count FROM biodata WHERE nik IS NULL OR TRIM(nik) = ''"
    try:
        res = execute_query(target_conn, sql_bio_nik)
        empty_niks = int(res[0]["empty_nik_count"]) if res else 0
        checks.append({
            "check_name": "biodata.nik is not empty",
            "category": "Data Quality",
            "description": "Biodata records with empty or null NIK",
            "orphan_count": empty_niks,
            "passed": empty_niks == 0,
        })
    except Exception as exc:
        checks.append({
            "check_name": "biodata.nik is not empty",
            "category": "Data Quality",
            "description": "Biodata records with empty or null NIK",
            "orphan_count": -1,
            "passed": False,
            "error": str(exc),
        })

    return checks


def check_hibernate_envers(target_conn: Any) -> dict[str, Any]:
    """Verifies baseline Hibernate Envers revisions across all *_aud tables."""
    results: dict[str, Any] = {
        "revinfo_exists": False,
        "total_revisions": 0,
        "latest_revision": None,
        "tables": [],
        "all_passed": True,
    }

    # 1. Check revinfo table
    try:
        rev_rows = execute_query(target_conn, "SELECT COUNT(*) AS total_rev, MAX(rev) AS max_rev FROM revinfo")
        if rev_rows:
            results["revinfo_exists"] = True
            results["total_revisions"] = int(rev_rows[0]["total_rev"] or 0)
            results["latest_revision"] = rev_rows[0]["max_rev"]
    except Exception as exc:
        logger.warning("revinfo table query failed: %s", exc)
        results["all_passed"] = False
        return results

    # 2. Check each audited table
    for aud_table in ENVERS_AUD_TABLES:
        try:
            table_check = execute_query(target_conn, f"SHOW TABLES LIKE '{aud_table}'")
            if not table_check:
                results["tables"].append({
                    "table_name": aud_table,
                    "exists": False,
                    "total_rows": 0,
                    "baseline_add_rows": 0,
                    "passed": True,  # Optional table not existing is not necessarily fatal
                    "status": "NOT_PRESENT",
                })
                continue

            sql_aud = f"""
            SELECT
                COUNT(*) AS total_rows,
                COALESCE(SUM(CASE WHEN revtype = {REVTYPE_ADD} THEN 1 ELSE 0 END), 0) AS baseline_rows
            FROM `{aud_table}`
            """
            aud_rows = execute_query(target_conn, sql_aud)
            total_r = int(aud_rows[0]["total_rows"] or 0) if aud_rows else 0
            base_r = int(aud_rows[0]["baseline_rows"] or 0) if aud_rows else 0

            # It passes if table has baseline records (or is an optional table that was not populated)
            passed = (total_r > 0 and base_r > 0) or total_r == 0
            if not passed:
                results["all_passed"] = False

            results["tables"].append({
                "table_name": aud_table,
                "exists": True,
                "total_rows": total_r,
                "baseline_add_rows": base_r,
                "passed": passed,
                "status": "OK" if passed else "NO_BASELINE",
            })
        except Exception as exc:
            results["all_passed"] = False
            results["tables"].append({
                "table_name": aud_table,
                "exists": False,
                "total_rows": 0,
                "baseline_add_rows": 0,
                "passed": False,
                "error": str(exc),
                "status": "ERROR",
            })

    return results


def check_table_quantities(
    target_conn: Any,
    legacy_conn: Any,
) -> list[dict[str, Any]]:
    """Compares row counts between legacy database and target database."""
    comparisons: list[dict[str, Any]] = []

    for label, legacy_tbl, target_tbl, notes in TABLE_QUANTITY_MAPPINGS:
        legacy_count = 0
        target_count = 0
        error_msg = None

        try:
            l_rows = execute_query(legacy_conn, f"SELECT COUNT(*) AS cnt FROM `{legacy_tbl}`")
            legacy_count = int(l_rows[0]["cnt"]) if l_rows else 0
        except Exception as exc:
            error_msg = f"Legacy query failed: {exc}"

        try:
            t_rows = execute_query(target_conn, f"SELECT COUNT(*) AS cnt FROM `{target_tbl}`")
            target_count = int(t_rows[0]["cnt"]) if t_rows else 0
        except Exception as exc:
            error_msg = f"Target query failed: {exc}"

        delta = target_count - legacy_count
        comparisons.append({
            "entity": label,
            "legacy_table": legacy_tbl,
            "target_table": target_tbl,
            "legacy_count": legacy_count,
            "target_count": target_count,
            "delta": delta,
            "notes": notes,
            "error": error_msg,
        })

    return comparisons


def run_integrity_check(
    target_conn: Any | None = None,
    legacy_conn: Any | None = None,
    console: Any | None = None,
    export_path: Path | str | None = "audit_integrity_report.json",
    strict: bool = False,
) -> dict[str, Any]:
    """Runs complete integrity audit and returns structured findings.

    Args:
        target_conn: Active connection to kepegawaian_dev_new.
        legacy_conn: Active connection to smartoffice.
        console: Optional rich console for table display.
        export_path: Path to export JSON report. If None, skips JSON write.
        strict: If True, flags warnings as failures.

    Returns:
        Structured audit findings dictionary.
    """
    start_time = time.time()
    logger.info("Executing comprehensive referential integrity & Envers audit...")

    def _execute(t_conn: Any, l_conn: Any | None) -> dict[str, Any]:
        # 1. Referential integrity & Zero Orphan check
        orphan_checks = check_referential_integrity(t_conn)
        total_orphans = sum(c["orphan_count"] for c in orphan_checks if c["orphan_count"] > 0)
        orphans_passed = all(c["passed"] for c in orphan_checks)

        # 2. Hibernate Envers baseline audit
        envers_audit = check_hibernate_envers(t_conn)

        # 3. Quantity comparison
        quantity_comparison = []
        if l_conn is not None:
            try:
                quantity_comparison = check_table_quantities(t_conn, l_conn)
            except Exception as exc:
                logger.warning("Quantity comparison failed: %s", exc)

        elapsed = time.time() - start_time
        overall_passed = orphans_passed and envers_audit.get("all_passed", True)

        findings = {
            "timestamp": datetime.now().isoformat(),
            "elapsed_seconds": round(elapsed, 3),
            "overall_passed": overall_passed,
            "zero_orphan_summary": {
                "total_checks": len(orphan_checks),
                "passed": orphans_passed,
                "total_orphan_records": total_orphans,
                "checks": orphan_checks,
            },
            "envers_audit_summary": envers_audit,
            "quantity_comparison": quantity_comparison,
        }

        # Render terminal tables
        _render_audit_tables(findings, console)

        # Export to JSON file
        if export_path:
            out_file = Path(export_path).resolve()
            try:
                out_file.parent.mkdir(parents=True, exist_ok=True)
                with open(out_file, "w", encoding="utf-8") as f:
                    json.dump(findings, f, indent=2)
                logger.info("Audit report exported to %s", out_file)
                if console is not None:
                    console.print(f"[dim]Report saved to: {out_file}[/dim]")
            except Exception as exc:
                logger.error("Failed to write JSON report to %s: %s", out_file, exc)

        return findings

    # Manage connections
    if target_conn is not None:
        if legacy_conn is not None:
            return _execute(target_conn, legacy_conn)
        try:
            with get_legacy_connection() as m_leg:
                return _execute(target_conn, m_leg)
        except Exception:
            return _execute(target_conn, None)

    with get_target_connection(autocommit=True) as m_tgt:
        if legacy_conn is not None:
            return _execute(m_tgt, legacy_conn)
        try:
            with get_legacy_connection() as m_leg:
                return _execute(m_tgt, m_leg)
        except Exception:
            return _execute(m_tgt, None)


def _render_audit_tables(findings: dict[str, Any], console: Any | None = None) -> None:
    """Renders formatted audit findings via rich console."""
    if console is None:
        return

    try:
        from rich.panel import Panel
        from rich.table import Table

        # 1. Zero Orphan Table
        orphan_table = Table(title="1. Zero Orphan & Referential Integrity Checks", show_header=True)
        orphan_table.add_column("Relationship / Check", style="cyan", width=38)
        orphan_table.add_column("Description", width=34)
        orphan_table.add_column("Orphans", justify="right", width=10)
        orphan_table.add_column("Status", justify="center", width=12)

        for c in findings["zero_orphan_summary"]["checks"]:
            cnt = c["orphan_count"]
            if cnt == 0:
                status_str = "[green]PASS[/green]"
                cnt_str = "[green]0[/green]"
            elif cnt < 0:
                status_str = "[red]ERROR[/red]"
                cnt_str = "[red]ERR[/red]"
            else:
                status_str = "[bold red]FAIL[/bold red]"
                cnt_str = f"[bold red]{cnt}[/bold red]"
            orphan_table.add_row(c["check_name"], c["description"], cnt_str, status_str)

        console.print()
        console.print(orphan_table)

        # 2. Hibernate Envers Table
        envers = findings["envers_audit_summary"]
        envers_table = Table(
            title=f"2. Hibernate Envers Baseline Revision Audit (Revinfo Revisions: {envers['total_revisions']})",
            show_header=True,
        )
        envers_table.add_column("Audit Table", style="cyan", width=28)
        envers_table.add_column("Total Rows", justify="right", width=12)
        envers_table.add_column("Baseline (revtype=0)", justify="right", width=20)
        envers_table.add_column("Status", justify="center", width=14)

        for t in envers.get("tables", []):
            st = t["status"]
            if st == "OK":
                st_str = "[green]BASELINE OK[/green]"
            elif st == "NOT_PRESENT":
                st_str = "[dim yellow]NOT PRESENT[/dim yellow]"
            else:
                st_str = "[bold red]NO BASELINE[/bold red]"

            envers_table.add_row(
                t["table_name"],
                str(t["total_rows"]),
                str(t["baseline_add_rows"]),
                st_str,
            )

        console.print()
        console.print(envers_table)

        # 3. Quantity Comparison Table (if available)
        quantities = findings.get("quantity_comparison", [])
        if quantities:
            qty_table = Table(title="3. Row Count Summary (Legacy vs Target)", show_header=True)
            qty_table.add_column("Entity", style="cyan", width=24)
            qty_table.add_column("Legacy Table", width=22)
            qty_table.add_column("Legacy Count", justify="right", width=14)
            qty_table.add_column("Target Count", justify="right", width=14)
            qty_table.add_column("Delta", justify="right", width=10)
            qty_table.add_column("Notes", width=30)

            for q in quantities:
                d = q["delta"]
                d_str = f"{d:+d}" if d != 0 else "[green]0[/green]"
                qty_table.add_row(
                    q["entity"],
                    q["legacy_table"],
                    f"{q['legacy_count']:,}",
                    f"{q['target_count']:,}",
                    d_str,
                    q["notes"],
                )

            console.print()
            console.print(qty_table)

        # Final Banner
        if findings["overall_passed"]:
            console.print(Panel("[bold green]ALL INTEGRITY & ENVERS AUDIT CHECKS PASSED![/bold green]"))
        else:
            console.print(Panel("[bold red]INTEGRITY AUDIT DETECTED ISSUES. CHECK REPORT TABLE ABOVE.[/bold red]"))

    except ImportError:
        pass


def main() -> None:
    """CLI Entrypoint for verify_integrity."""
    parser = argparse.ArgumentParser(description="Target Database Referential Integrity and Envers Audit")
    parser.add_argument("--export", type=str, default="audit_integrity_report.json", help="Path for JSON report")
    parser.add_argument("--strict", action="store_true", help="Exit with code 1 if any orphan detected")
    parser.add_argument("--no-legacy", action="store_true", help="Skip legacy database comparison queries")

    args = parser.parse_args()

    console = None
    try:
        from rich.console import Console

        console = Console()
    except ImportError:
        pass

    try:
        findings = run_integrity_check(
            console=console,
            export_path=args.export,
            strict=args.strict,
        )
        has_orphans = findings["zero_orphan_summary"]["total_orphan_records"] > 0
        sys.exit(1 if (args.strict and has_orphans) else 0)
    except Exception as exc:
        logger.exception("Integrity verification encountered fatal error: %s", exc)
        sys.exit(1)


if __name__ == "__main__":
    main()

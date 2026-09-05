#!/usr/bin/env python3
"""Main CLI Entrypoint for Data Migration Microapp.

Supports:
- run-all: Sequential execution of Stage 0 through Stage 7.
- stage: Independent execution of an individual migration stage.
- sync-files: Physical file attachment copy worker (Phase 2).
- sync-auth: Standalone Appwrite auth provisioning migration.
- audit: Target database referential integrity & payroll reconciliation audits.
"""

from __future__ import annotations

import argparse
import logging
import sys
import time
from pathlib import Path
from typing import Any, Optional

# Ensure repo root is in sys.path when invoked directly
_REPO_ROOT = Path(__file__).resolve().parents[2]
if str(_REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(_REPO_ROOT))

from tools.migration.audit.reconcile_payroll import run_payroll_reconciliation
from tools.migration.audit.verify_integrity import run_integrity_check
from tools.migration.config import config
from tools.migration.core.db import execute_query, get_target_connection
from tools.migration.core.manifest import manifest_manager
from tools.migration.stages import (
    StageResult,
    run_stage0,
    run_stage1,
    run_stage2,
    run_stage3,
    run_stage4_cuti,
    run_stage5_penggajian,
    run_stage6_lampiran,
    run_stage7_auth,
)
from tools.migration.workers.file_sync_worker import run_file_sync

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger("migration.cli")


def _get_console():
    """Initializes Rich console if available."""
    try:
        from rich.console import Console

        return Console()
    except ImportError:
        return None


def print_banner(console=None) -> None:
    """Renders visual banner in terminal."""
    banner_text = (
        "[bold cyan]========================================================================[/bold cyan]\n"
        "[bold white]  KEPEGAWAIAN DATA MIGRATION MICROAPP (ADR-0049 - ADR-0054)[/bold white]\n"
        "[bold white]  Smartoffice Legacy -> Kepegawaian (CQRS / MariaDB / Appwrite)[/bold white]\n"
        "[bold cyan]========================================================================[/bold cyan]"
    )
    if console is not None:
        try:
            from rich.panel import Panel

            console.print(Panel(banner_text, border_style="cyan"))
            return
        except ImportError:
            pass

    print("=" * 72)
    print("  KEPEGAWAIAN DATA MIGRATION MICROAPP (ADR-0049 - ADR-0054)")
    print("  Smartoffice Legacy -> Kepegawaian (CQRS / MariaDB / Appwrite)")
    print("=" * 72)


def handle_fresh_mode(console=None) -> None:
    """Cleans migration state and ID mappings for fresh run."""
    logger.info("Initializing FRESH migration mode: clearing ID mappings and manifest...")
    try:
        with get_target_connection(autocommit=True) as conn:
            check_state = execute_query(conn, "SHOW TABLES LIKE 'migration_id_map'")
            if check_state:
                with conn.cursor() as cursor:
                    cursor.execute("TRUNCATE TABLE migration_id_map")
                logger.info("Truncated migration_id_map table in target database.")

        # Re-initialize manifest DB
        manifest_manager.ensure_initialized()
        with manifest_manager.get_connection() as m_conn:
            m_conn.execute("DELETE FROM file_manifest")
        logger.info("Cleared SQLite file sync manifest.")

        if console is not None:
            console.print("[bold yellow]>> Fresh mode: Cleared migration_id_map and file manifest.[/bold yellow]")
    except Exception as exc:
        logger.warning("Fresh mode cleanup warning: %s", exc)


def cmd_run_all(args: argparse.Namespace, console=None) -> int:
    """Executes Stage 0 through Stage 7 sequentially."""
    print_banner(console)
    start_total = time.time()

    if args.fresh and not args.dry_run:
        handle_fresh_mode(console)

    stages_results: list[dict[str, Any]] = []
    overall_success = True

    # 1. Stage 0: Preflight
    logger.info("Starting Stage 0: Preflight & Environment Verification...")
    try:
        s0_summary = run_stage0(console=console)
        stages_results.append({
            "stage": "Stage 0: Preflight",
            "status": "PASSED" if s0_summary.success else "FAILED",
            "details": f"{len(s0_summary.checks)} checks completed",
            "success": s0_summary.success,
        })
        if not s0_summary.success and not args.force:
            logger.error("Stage 0 Preflight failed. Aborting pipeline execution. Use --force to override.")
            _render_run_all_summary(stages_results, console, time.time() - start_total)
            return 1
    except Exception as exc:
        logger.exception("Stage 0 encountered critical error: %s", exc)
        stages_results.append({"stage": "Stage 0: Preflight", "status": "ERROR", "details": str(exc), "success": False})
        _render_run_all_summary(stages_results, console, time.time() - start_total)
        return 1

    # 2. Stage 1: Master Reference Sync
    logger.info("Starting Stage 1: Master Reference Sync...")
    try:
        s1_res = run_stage1(console=console)
        stages_results.append({
            "stage": "Stage 1: Master Sync",
            "status": "PASSED" if s1_res.success else "FAILED",
            "details": f"Extracted: {s1_res.records_extracted}, Upserted: {s1_res.records_upserted}",
            "success": s1_res.success,
        })
        if not s1_res.success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 1 failed: %s", exc)
        stages_results.append({"stage": "Stage 1: Master Sync", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 3. Stage 2: Pegawai & Biodata
    logger.info("Starting Stage 2: Pegawai & Biodata Migration...")
    try:
        s2_res = run_stage2(console=console)
        stages_results.append({
            "stage": "Stage 2: Pegawai & Biodata",
            "status": "PASSED" if s2_res.success else "FAILED",
            "details": f"Extracted: {s2_res.records_extracted}, Upserted: {s2_res.records_upserted}",
            "success": s2_res.success,
        })
        if not s2_res.success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 2 failed: %s", exc)
        stages_results.append({"stage": "Stage 2: Pegawai & Biodata", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 4. Stage 3: Kepegawaian & SK
    logger.info("Starting Stage 3: Kepegawaian & SK Migration...")
    try:
        s3_res = run_stage3(console=console)
        stages_results.append({
            "stage": "Stage 3: Kepegawaian & SK",
            "status": "PASSED" if s3_res.success else "FAILED",
            "details": f"Extracted: {s3_res.records_extracted}, Upserted: {s3_res.records_upserted}",
            "success": s3_res.success,
        })
        if not s3_res.success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 3 failed: %s", exc)
        stages_results.append({"stage": "Stage 3: Kepegawaian & SK", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 5. Stage 4: Leave Management (Cuti)
    logger.info("Starting Stage 4: Leave Management (Cuti) Migration...")
    try:
        s4_res = run_stage4_cuti(dry_run=args.dry_run, console=console)
        s4_success = s4_res.get("status") == "COMPLETED"
        stages_results.append({
            "stage": "Stage 4: Cuti & Quota",
            "status": "PASSED" if s4_success else "FAILED",
            "details": f"Delta Tx: {s4_res.get('transaksi_delta_migrated', 0)}, Quota 2026: {s4_res.get('kuota_2026_updated', 0)}",
            "success": s4_success,
        })
        if not s4_success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 4 failed: %s", exc)
        stages_results.append({"stage": "Stage 4: Cuti & Quota", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 6. Stage 5: Historical Payroll (Penggajian)
    logger.info("Starting Stage 5: Historical Payroll Migration (payroll_all=%s)...", args.payroll_all)
    try:
        s5_res = run_stage5_penggajian(
            payroll_all=args.payroll_all,
            dry_run=args.dry_run,
            console=console,
        )
        s5_success = s5_res.get("status") == "COMPLETED"
        stages_results.append({
            "stage": "Stage 5: Penggajian",
            "status": "PASSED" if s5_success else "FAILED",
            "details": f"Batches: {s5_res.get('batch_root_migrated', 0)}, Details: {s5_res.get('batch_proses_detail_migrated', 0)}",
            "success": s5_success,
        })
        if not s5_success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 5 failed: %s", exc)
        stages_results.append({"stage": "Stage 5: Penggajian", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 7. Stage 6: Attachments & Manifest (Phase 1)
    logger.info("Starting Stage 6: Attachments & Manifest Migration...")
    try:
        s6_res = run_stage6_lampiran(dry_run=args.dry_run, console=console)
        s6_success = s6_res.get("status") == "COMPLETED"
        stages_results.append({
            "stage": "Stage 6: Lampiran & Manifest",
            "status": "PASSED" if s6_success else "FAILED",
            "details": f"SK: {s6_res.get('lampiran_sk_migrated', 0)}, Profil: {s6_res.get('lampiran_profil_migrated', 0)}, Manifest: {s6_res.get('manifest_total_entries', 0)}",
            "success": s6_success,
        })
        if not s6_success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 6 failed: %s", exc)
        stages_results.append({"stage": "Stage 6: Lampiran & Manifest", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    # 8. Stage 7: Appwrite Auth Provisioning
    logger.info("Starting Stage 7: Appwrite Auth Provisioning...")
    try:
        s7_res = run_stage7_auth(dry_run=args.dry_run, console=console)
        s7_success = s7_res.get("status") == "COMPLETED"
        stages_results.append({
            "stage": "Stage 7: Appwrite Auth",
            "status": "PASSED" if s7_success else "FAILED",
            "details": f"Created: {s7_res.get('active_created', 0)}, Blocked: {s7_res.get('retired_blocked', 0)}",
            "success": s7_success,
        })
        if not s7_success:
            overall_success = False
    except Exception as exc:
        logger.exception("Stage 7 failed: %s", exc)
        stages_results.append({"stage": "Stage 7: Appwrite Auth", "status": "ERROR", "details": str(exc), "success": False})
        overall_success = False

    elapsed = time.time() - start_total
    _render_run_all_summary(stages_results, console, elapsed)
    return 0 if overall_success else 1


def _render_run_all_summary(stages_results: list[dict[str, Any]], console=None, elapsed: float = 0.0) -> None:
    """Renders final summary table for run-all pipeline."""
    if console is not None:
        try:
            from rich.panel import Panel
            from rich.table import Table

            table = Table(title="Migration Pipeline (Run-All) Execution Summary", show_header=True)
            table.add_column("Stage", style="cyan", width=30)
            table.add_column("Status", justify="center", width=12)
            table.add_column("Metrics & Notes", style="white")

            all_ok = True
            for sr in stages_results:
                st = sr["status"]
                if st == "PASSED":
                    st_str = "[green]PASSED[/green]"
                else:
                    st_str = "[bold red]FAILED[/bold red]"
                    all_ok = False
                table.add_row(sr["stage"], st_str, sr["details"])

            console.print()
            console.print(table)
            if all_ok:
                console.print(
                    Panel(f"[bold green]FULL MIGRATION PIPELINE COMPLETED SUCCESSFULLY IN {elapsed:.2f}s![/bold green]")
                )
            else:
                console.print(
                    Panel(f"[bold red]MIGRATION PIPELINE FINISHED WITH FAILURES (Total time: {elapsed:.2f}s).[/bold red]")
                )
            return
        except ImportError:
            pass

    print(f"\nPipeline finished in {elapsed:.2f}s. Summary:")
    for sr in stages_results:
        print(f"  {sr['stage']:<28}: {sr['status']} ({sr['details']})")


def cmd_stage(args: argparse.Namespace, console=None) -> int:
    """Executes a single specified migration stage."""
    print_banner(console)
    stage_name = args.name.lower().strip()

    # Stage alias resolution
    stage_map = {
        "stage0": lambda: run_stage0(console=console),
        "stage1": lambda: run_stage1(console=console),
        "stage2": lambda: run_stage2(console=console),
        "stage3": lambda: run_stage3(console=console),
        "stage4": lambda: run_stage4_cuti(dry_run=args.dry_run, console=console),
        "stage4_cuti": lambda: run_stage4_cuti(dry_run=args.dry_run, console=console),
        "stage5": lambda: run_stage5_penggajian(payroll_all=args.payroll_all, dry_run=args.dry_run, console=console),
        "stage5_penggajian": lambda: run_stage5_penggajian(payroll_all=args.payroll_all, dry_run=args.dry_run, console=console),
        "stage6": lambda: run_stage6_lampiran(dry_run=args.dry_run, console=console),
        "stage6_lampiran": lambda: run_stage6_lampiran(dry_run=args.dry_run, console=console),
        "stage7": lambda: run_stage7_auth(dry_run=args.dry_run, console=console),
        "stage7_auth": lambda: run_stage7_auth(dry_run=args.dry_run, console=console),
    }

    runner = stage_map.get(stage_name)
    if not runner:
        logger.error("Unknown stage name: '%s'. Choose from: %s", stage_name, list(stage_map.keys()))
        return 1

    logger.info("Executing %s...", stage_name)
    try:
        res = runner()
        if isinstance(res, StageResult):
            return 0 if res.success else 1
        elif isinstance(res, dict):
            return 0 if res.get("status") == "COMPLETED" else 1
        elif hasattr(res, "success"):
            return 0 if res.success else 1
        return 0
    except Exception as exc:
        logger.exception("Stage '%s' execution failed: %s", stage_name, exc)
        return 1


def cmd_sync_files(args: argparse.Namespace, console=None) -> int:
    """Executes physical file attachment sync worker."""
    print_banner(console)
    stats = run_file_sync(
        source_dir=args.source,
        target_dir=args.target,
        workers=args.workers,
        limit=args.limit,
        retry_failed=args.retry_failed,
        dry_run=args.dry_run,
        verify_checksum=not args.no_checksum,
        console=console,
    )
    return 0 if stats.failed_count == 0 else 1


def cmd_sync_auth(args: argparse.Namespace, console=None) -> int:
    """Executes standalone Stage 7 Appwrite auth sync."""
    print_banner(console)
    res = run_stage7_auth(
        dry_run=args.dry_run,
        export_csv=not args.no_csv,
        console=console,
    )
    return 0 if res.get("status") == "COMPLETED" else 1


def cmd_audit(args: argparse.Namespace, console=None) -> int:
    """Executes integrity verification and payroll reconciliation audits."""
    print_banner(console)
    exit_code = 0

    # 1. Integrity check
    if not args.payroll_only:
        try:
            logger.info("Running Target Database Referential Integrity & Envers Audit...")
            integrity_findings = run_integrity_check(
                console=console,
                export_path=args.export_integrity,
                strict=args.strict,
            )
            has_orphans = integrity_findings["zero_orphan_summary"]["total_orphan_records"] > 0
            if args.strict and has_orphans:
                exit_code = 1
        except Exception as exc:
            logger.exception("Integrity audit error: %s", exc)
            exit_code = 1

    # 2. Payroll reconciliation
    if not args.integrity_only:
        try:
            logger.info("Running Historical Payroll Reconciliation Audit...")
            payroll_findings = run_payroll_reconciliation(
                console=console,
                export_path=args.export_payroll,
                tolerance=args.tolerance,
                payroll_all=getattr(args, "payroll_all", False),
            )
            if not payroll_findings["summary"]["all_passed"]:
                exit_code = 1
        except Exception as exc:
            logger.exception("Payroll reconciliation audit error: %s", exc)
            exit_code = 1

    return exit_code


def build_parser() -> argparse.ArgumentParser:
    """Constructs command line argument parser."""
    parser = argparse.ArgumentParser(
        prog="python3 tools/migration/run.py",
        description="Kepegawaian Data Migration Microapp CLI Entrypoint",
    )
    subparsers = parser.add_subparsers(dest="subcommand", help="Available subcommands")

    # Command: run-all
    p_run_all = subparsers.add_parser("run-all", help="Execute complete Stage 0 to Stage 7 migration pipeline")
    p_run_all.add_argument("--fresh", action="store_true", help="Clear migration ID map and manifest before running")
    p_run_all.add_argument("--payroll-all", action="store_true", help="Migrate all historical payroll years (default: 12 months)")
    p_run_all.add_argument("--dry-run", action="store_true", help="Parse without mutating target database")
    p_run_all.add_argument("--force", action="store_true", help="Continue pipeline even if Stage 0 preflight fails")
    p_run_all.add_argument("--limit", type=int, default=None, help="Record limit for stages where supported")

    # Command: stage
    p_stage = subparsers.add_parser("stage", help="Execute a specific migration stage independently")
    p_stage.add_argument(
        "--name",
        type=str,
        required=True,
        help="Stage identifier: stage0, stage1, stage2, stage3, stage4_cuti, stage5_penggajian, stage6_lampiran, stage7_auth",
    )
    p_stage.add_argument("--payroll-all", action="store_true", help="Migrate all historical payroll years (Stage 5)")
    p_stage.add_argument("--dry-run", action="store_true", help="Parse without mutating target database")
    p_stage.add_argument("--limit", type=int, default=None, help="Record limit where supported")

    # Command: sync-files
    p_sync = subparsers.add_parser("sync-files", help="Run Phase 2 Physical File Attachment Synchronization Worker")
    p_sync.add_argument("--source", type=str, default=None, help="Legacy attachments source directory")
    p_sync.add_argument("--target", type=str, default=None, help="Target attachments destination directory")
    p_sync.add_argument("--workers", type=int, default=4, help="Worker thread concurrency (default: 4)")
    p_sync.add_argument("--limit", type=int, default=None, help="Limit number of files to process")
    p_sync.add_argument("--retry-failed", action="store_true", help="Include FAILED manifest records in sync")
    p_sync.add_argument("--dry-run", action="store_true", help="Check file existence without physical copy")
    p_sync.add_argument("--no-checksum", action="store_true", help="Skip SHA-256 verification after copy")

    # Command: sync-auth
    p_auth = subparsers.add_parser("sync-auth", help="Run Stage 7 Appwrite Auth Provisioning independently")
    p_auth.add_argument("--dry-run", action="store_true", help="Evaluate accounts without calling Appwrite APIs")
    p_auth.add_argument("--no-csv", action="store_true", help="Do not write audit_auth_sync.csv log")

    # Command: audit
    p_audit = subparsers.add_parser("audit", help="Run target referential integrity and payroll reconciliation audits")
    p_audit.add_argument("--export-integrity", type=str, default="audit_integrity_report.json", help="Path for integrity JSON report")
    p_audit.add_argument("--export-payroll", type=str, default="reconcile_payroll_report.json", help="Path for payroll JSON report")
    p_audit.add_argument("--strict", action="store_true", help="Fail with exit code 1 if any orphan detected")
    p_audit.add_argument("--tolerance", type=float, default=0.0, help="Payroll deviation tolerance (default: Rp 0,-)")
    p_audit.add_argument("--payroll-all", action="store_true", help="Audit all historical payroll batches including older historical batches")
    p_audit.add_argument("--payroll-only", action="store_true", help="Run only payroll reconciliation audit")
    p_audit.add_argument("--integrity-only", action="store_true", help="Run only referential integrity audit")

    return parser


def main() -> None:
    """Main program entrypoint."""
    parser = build_parser()
    args = parser.parse_args()

    console = _get_console()

    if not args.subcommand:
        parser.print_help()
        sys.exit(0)

    if args.subcommand == "run-all":
        sys.exit(cmd_run_all(args, console=console))
    elif args.subcommand == "stage":
        sys.exit(cmd_stage(args, console=console))
    elif args.subcommand == "sync-files":
        sys.exit(cmd_sync_files(args, console=console))
    elif args.subcommand == "sync-auth":
        sys.exit(cmd_sync_auth(args, console=console))
    elif args.subcommand == "audit":
        sys.exit(cmd_audit(args, console=console))
    else:
        parser.print_help()
        sys.exit(1)


if __name__ == "__main__":
    main()

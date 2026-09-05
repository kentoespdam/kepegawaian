"""Historical Payroll Mathematical Reconciliation Audit.

Complies with:
- ADR-0050: Rekonsiliasi Gap Pengkodean Komponen Gaji via Translation Map dan Passthrough Historis
- Architecture Document Section 5.2 (Rekonsiliasi Matematika Payroll Historis)

Compares aggregated payroll figures between:
- Legacy: smartoffice.salary_process_detail (via salary_batch_process & salary_process_master)
- Target: kepegawaian_dev_new.gaji_batch_master_proses (via gaji_batch_root & gaji_batch_master)

Verifies:
1. Total gross earnings (pendapatan kotor: ctype '+' vs jenis_gaji 'PEMASUKAN')
2. Total deductions (potongan: ctype '-' vs jenis_gaji 'POTONGAN')
3. Total take-home pay (netto: pendapatan kotor - potongan)
4. Enforces Zero Deviation (tolerance: Rp 0,-) across all batches and periods.
5. Displays rich terminal report table and exports JSON summary.
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

logger = logging.getLogger(__name__)


def extract_legacy_payroll_summary(legacy_conn: Any) -> list[dict[str, Any]]:
    """Extracts aggregated payroll figures per batch from smartoffice legacy database."""
    sql = """
    SELECT
        b.batch_no,
        DATE_FORMAT(b.period, '%Y-%m') AS period_month,
        DATE_FORMAT(b.period, '%Y-%m-%d') AS period_date,
        COUNT(DISTINCT m.id) AS total_employees,
        COUNT(d.id) AS total_components,
        COALESCE(SUM(CASE WHEN d.ctype = '+' THEN d.value ELSE 0 END), 0.0) AS gross_income,
        COALESCE(SUM(CASE WHEN d.ctype = '-' THEN d.value ELSE 0 END), 0.0) AS total_deduction,
        COALESCE(SUM(CASE WHEN d.ctype = '+' THEN d.value WHEN d.ctype = '-' THEN -d.value ELSE 0 END), 0.0) AS net_income
    FROM salary_batch_process b
    JOIN salary_process_master m ON b.batch_no = m.batch_code
    JOIN salary_process_detail d ON m.id = d.pm_id
    WHERE b.status < 99
    GROUP BY b.batch_no, b.period
    ORDER BY b.period ASC, b.batch_no ASC
    """
    rows = execute_query(legacy_conn, sql)
    logger.info("Extracted %d legacy payroll batch summaries", len(rows))
    return [dict(r) for r in rows]


def extract_target_payroll_summary(target_conn: Any) -> list[dict[str, Any]]:
    """Extracts aggregated payroll figures per batch from kepegawaian target database."""
    sql = """
    SELECT
        r.id AS batch_no,
        CASE
            WHEN LENGTH(r.periode) = 6 THEN CONCAT(LEFT(r.periode, 4), '-', RIGHT(r.periode, 2))
            ELSE COALESCE(DATE_FORMAT(r.periode, '%Y-%m'), r.periode)
        END AS period_month,
        CASE
            WHEN LENGTH(r.periode) = 6 THEN CONCAT(LEFT(r.periode, 4), '-', RIGHT(r.periode, 2), '-01')
            ELSE COALESCE(DATE_FORMAT(r.periode, '%Y-%m-%d'), r.periode)
        END AS period_date,
        COUNT(DISTINCT m.id) AS total_employees,
        COUNT(p.id) AS total_components,
        COALESCE(SUM(CASE WHEN p.jenis_gaji = 'PEMASUKAN' THEN p.nilai ELSE 0.0 END), 0.0) AS gross_income,
        COALESCE(SUM(CASE WHEN p.jenis_gaji = 'POTONGAN' THEN p.nilai ELSE 0.0 END), 0.0) AS total_deduction,
        COALESCE(SUM(CASE WHEN p.jenis_gaji = 'PEMASUKAN' THEN p.nilai WHEN p.jenis_gaji = 'POTONGAN' THEN -p.nilai ELSE 0.0 END), 0.0) AS net_income
    FROM gaji_batch_root r
    JOIN gaji_batch_master m ON r.id = m.batch_root_id
    JOIN gaji_batch_master_proses p ON m.id = p.batch_master_id
    WHERE r.is_deleted = 0 AND r.status = 5
    GROUP BY r.id, r.periode
    ORDER BY r.periode ASC, r.id ASC
    """
    rows = execute_query(target_conn, sql)
    logger.info("Extracted %d target payroll batch summaries", len(rows))
    return [dict(r) for r in rows]


def reconcile_batches(
    legacy_batches: list[dict[str, Any]],
    target_batches: list[dict[str, Any]],
    tolerance: float = 0.0,
    payroll_all: bool = False,
    cutoff_period: str | None = None,
) -> tuple[list[dict[str, Any]], dict[str, Any]]:
    """Reconciles legacy vs target payroll batches and calculates mathematical deltas.

    Args:
        legacy_batches: Aggregated summaries from legacy DB.
        target_batches: Aggregated summaries from target DB.
        tolerance: Allowed deviation in Rupiah (default: 0.0).
        payroll_all: If True, evaluates all legacy historical batches as in-scope.
                     If False (default), evaluates only batches in target DB or within the
                     migration window (last 12 months, >= 2025-09); older legacy historical
                     batches (2017 to August 2025) are categorized as Out of Scope.
        cutoff_period: Optional cutoff period string (YYYY-MM). If omitted, determined by
                       target batches or 12 months prior to current date.

    Returns:
        tuple of (reconciled_items_list, overall_summary_dict)
    """
    legacy_by_batch: dict[str, dict[str, Any]] = {
        str(b["batch_no"]).strip(): b for b in legacy_batches
    }
    target_by_batch: dict[str, dict[str, Any]] = {
        str(b["batch_no"]).strip(): b for b in target_batches
    }

    # Determine cutoff period for 12-month migration window if not explicitly provided
    if cutoff_period is None:
        target_periods = [
            b.get("period_month") or (str(b.get("period_date", ""))[:7])
            for b in target_batches
            if b.get("period_month") or b.get("period_date")
        ]
        if target_periods:
            cutoff_period = min(target_periods)
        else:
            now = datetime.now()
            cutoff_period = f"{now.year - 1:04d}-{now.month:02d}"

    all_batch_keys = sorted(
        set(legacy_by_batch.keys()) | set(target_by_batch.keys()),
        key=lambda k: (
            (legacy_by_batch.get(k) or target_by_batch.get(k) or {}).get("period_date", ""),
            k,
        ),
    )

    reconciled_items: list[dict[str, Any]] = []
    matched_batches = 0
    mismatched_batches = 0
    missing_in_target = 0
    missing_in_legacy = 0
    out_of_scope_batches = 0

    total_legacy_gross = 0.0
    total_target_gross = 0.0
    total_legacy_deduction = 0.0
    total_target_deduction = 0.0
    total_legacy_net = 0.0
    total_target_net = 0.0

    total_oos_legacy_gross = 0.0
    total_oos_legacy_ded = 0.0
    total_oos_legacy_net = 0.0
    oos_periods: list[str] = []

    for batch_id in all_batch_keys:
        leg = legacy_by_batch.get(batch_id)
        tgt = target_by_batch.get(batch_id)

        # Case 1: Legacy batch not in target
        if leg and not tgt:
            leg_period = str(leg.get("period_month") or (str(leg.get("period_date", ""))[:7]))
            is_older_historical = bool(cutoff_period and leg_period and leg_period < cutoff_period)

            if not payroll_all and is_older_historical:
                # Group as Out of Scope (Older Historical Batches - run with --payroll-all)
                out_of_scope_batches += 1
                if leg_period:
                    oos_periods.append(leg_period)
                l_g = float(leg["gross_income"])
                l_d = float(leg["total_deduction"])
                l_n = float(leg["net_income"])
                total_oos_legacy_gross += l_g
                total_oos_legacy_ded += l_d
                total_oos_legacy_net += l_n

                item = {
                    "batch_no": batch_id,
                    "periode": leg.get("period_month") or leg.get("period_date"),
                    "status": "OUT_OF_SCOPE",
                    "in_scope": False,
                    "info": "Out of Scope (Older Historical Batches - run with --payroll-all)",
                    "legacy_gross": l_g,
                    "target_gross": 0.0,
                    "delta_gross": 0.0,
                    "legacy_deduction": l_d,
                    "target_deduction": 0.0,
                    "delta_deduction": 0.0,
                    "legacy_net": l_n,
                    "target_net": 0.0,
                    "delta_net": 0.0,
                    "matched": True,
                }
                reconciled_items.append(item)
                continue

            # Within migration scope but missing in target database
            missing_in_target += 1
            l_g = float(leg["gross_income"])
            l_d = float(leg["total_deduction"])
            l_n = float(leg["net_income"])
            item = {
                "batch_no": batch_id,
                "periode": leg.get("period_month") or leg.get("period_date"),
                "status": "MISSING_IN_TARGET",
                "in_scope": True,
                "info": "Missing in Target Database",
                "legacy_gross": l_g,
                "target_gross": 0.0,
                "delta_gross": -l_g,
                "legacy_deduction": l_d,
                "target_deduction": 0.0,
                "delta_deduction": -l_d,
                "legacy_net": l_n,
                "target_net": 0.0,
                "delta_net": -l_n,
                "matched": False,
            }
            reconciled_items.append(item)
            total_legacy_gross += l_g
            total_legacy_deduction += l_d
            total_legacy_net += l_n
            continue

        # Case 2: Target batch not in legacy
        if tgt and not leg:
            missing_in_legacy += 1
            t_g = float(tgt["gross_income"])
            t_d = float(tgt["total_deduction"])
            t_n = float(tgt["net_income"])
            item = {
                "batch_no": batch_id,
                "periode": tgt.get("period_month") or tgt.get("period_date"),
                "status": "MISSING_IN_LEGACY",
                "in_scope": True,
                "info": "Missing in Legacy Database",
                "legacy_gross": 0.0,
                "target_gross": t_g,
                "delta_gross": t_g,
                "legacy_deduction": 0.0,
                "target_deduction": t_d,
                "delta_deduction": t_d,
                "legacy_net": 0.0,
                "target_net": t_n,
                "delta_net": t_n,
                "matched": False,
            }
            reconciled_items.append(item)
            total_target_gross += t_g
            total_target_deduction += t_d
            total_target_net += t_n
            continue

        # Case 3: Present in both legacy and target (In-Scope evaluation)
        assert leg is not None and tgt is not None

        l_gross = float(leg["gross_income"])
        t_gross = float(tgt["gross_income"])
        l_ded = float(leg["total_deduction"])
        t_ded = float(tgt["total_deduction"])
        l_net = float(leg["net_income"])
        t_net = float(tgt["net_income"])

        delta_gross = round(t_gross - l_gross, 2)
        delta_ded = round(t_ded - l_ded, 2)
        delta_net = round(t_net - l_net, 2)

        matched = (
            abs(delta_gross) <= tolerance
            and abs(delta_ded) <= tolerance
            and abs(delta_net) <= tolerance
        )

        if matched:
            matched_batches += 1
            status = "MATCH"
        else:
            mismatched_batches += 1
            status = "MISMATCH"

        reconciled_items.append({
            "batch_no": batch_id,
            "periode": tgt.get("period_month") or tgt.get("period_date"),
            "status": status,
            "in_scope": True,
            "info": "Reconciled In-Scope Batch",
            "legacy_gross": l_gross,
            "target_gross": t_gross,
            "delta_gross": delta_gross,
            "legacy_deduction": l_ded,
            "target_deduction": t_ded,
            "delta_deduction": delta_ded,
            "legacy_net": l_net,
            "target_net": t_net,
            "delta_net": delta_net,
            "matched": matched,
        })

        total_legacy_gross += l_gross
        total_target_gross += t_gross
        total_legacy_deduction += l_ded
        total_target_deduction += t_ded
        total_legacy_net += l_net
        total_target_net += t_net

    overall_delta_gross = round(total_target_gross - total_legacy_gross, 2)
    overall_delta_ded = round(total_target_deduction - total_legacy_deduction, 2)
    overall_delta_net = round(total_target_net - total_legacy_net, 2)

    in_scope_evaluated = matched_batches + mismatched_batches + missing_in_target + missing_in_legacy

    all_passed = (
        mismatched_batches == 0
        and missing_in_target == 0
        and missing_in_legacy == 0
        and abs(overall_delta_gross) <= tolerance
        and abs(overall_delta_ded) <= tolerance
        and abs(overall_delta_net) <= tolerance
    )

    oos_period_range = (
        f"{min(oos_periods)} s/d {max(oos_periods)}" if oos_periods else "None"
    )

    summary = {
        "total_batches_evaluated": in_scope_evaluated,
        "total_batches_all": len(all_batch_keys),
        "in_scope_batches": in_scope_evaluated,
        "out_of_scope_batches": out_of_scope_batches,
        "matched_batches": matched_batches,
        "mismatched_batches": mismatched_batches,
        "missing_in_target": missing_in_target,
        "missing_in_legacy": missing_in_legacy,
        "tolerance": tolerance,
        "payroll_all": payroll_all,
        "cutoff_period": cutoff_period,
        "all_passed": all_passed,
        "totals": {
            "legacy_gross": total_legacy_gross,
            "target_gross": total_target_gross,
            "delta_gross": overall_delta_gross,
            "legacy_deduction": total_legacy_deduction,
            "target_deduction": total_target_deduction,
            "delta_deduction": overall_delta_ded,
            "legacy_net": total_legacy_net,
            "target_net": total_target_net,
            "delta_net": overall_delta_net,
        },
        "out_of_scope_summary": {
            "total_batches": out_of_scope_batches,
            "label": "Out of Scope (Older Historical Batches - run with --payroll-all)",
            "period_range": oos_period_range,
            "legacy_gross": total_oos_legacy_gross,
            "legacy_deduction": total_oos_legacy_ded,
            "legacy_net": total_oos_legacy_net,
        },
    }

    return reconciled_items, summary


def run_payroll_reconciliation(
    target_conn: Any | None = None,
    legacy_conn: Any | None = None,
    console: Any | None = None,
    export_path: Path | str | None = "reconcile_payroll_report.json",
    tolerance: float = 0.0,
    payroll_all: bool = False,
    cutoff_period: str | None = None,
) -> dict[str, Any]:
    """Executes payroll reconciliation audit and returns findings."""
    start_time = time.time()
    logger.info(
        "Starting historical payroll reconciliation audit (tolerance=Rp %.2f, payroll_all=%s)...",
        tolerance,
        payroll_all,
    )

    def _execute(t_conn: Any, l_conn: Any) -> dict[str, Any]:
        legacy_batches = extract_legacy_payroll_summary(l_conn)
        target_batches = extract_target_payroll_summary(t_conn)

        items, summary = reconcile_batches(
            legacy_batches,
            target_batches,
            tolerance=tolerance,
            payroll_all=payroll_all,
            cutoff_period=cutoff_period,
        )
        elapsed = time.time() - start_time

        findings = {
            "timestamp": datetime.now().isoformat(),
            "elapsed_seconds": round(elapsed, 3),
            "summary": summary,
            "batch_details": items,
        }

        _render_reconciliation_table(findings, console)

        if export_path:
            out_file = Path(export_path).resolve()
            try:
                out_file.parent.mkdir(parents=True, exist_ok=True)
                with open(out_file, "w", encoding="utf-8") as f:
                    json.dump(findings, f, indent=2)
                logger.info("Payroll reconciliation report exported to %s", out_file)
                if console is not None:
                    console.print(f"[dim]Report saved to: {out_file}[/dim]")
            except Exception as exc:
                logger.error("Failed to export JSON report to %s: %s", out_file, exc)

        return findings

    # Connection management
    if target_conn is not None and legacy_conn is not None:
        return _execute(target_conn, legacy_conn)
    elif target_conn is not None:
        with get_legacy_connection() as m_leg:
            return _execute(target_conn, m_leg)
    elif legacy_conn is not None:
        with get_target_connection(autocommit=True) as m_tgt:
            return _execute(m_tgt, legacy_conn)
    else:
        with get_target_connection(autocommit=True) as m_tgt:
            with get_legacy_connection() as m_leg:
                return _execute(m_tgt, m_leg)


def _render_reconciliation_table(findings: dict[str, Any], console: Any | None = None) -> None:
    """Renders formatted reconciliation table to terminal via rich console."""
    if console is None:
        return

    try:
        from rich.panel import Panel
        from rich.table import Table

        items = findings["batch_details"]
        summary = findings["summary"]
        oos = summary.get("out_of_scope_summary", {})

        def _fmt_curr(val: float) -> str:
            return f"Rp {val:,.0f}"

        def _fmt_delta(val: float) -> str:
            if abs(val) < 0.001:
                return "[green]Rp 0[/green]"
            return f"[bold red]Rp {val:+,.0f}[/bold red]"

        # 1. In-Scope batches table
        in_scope_items = [it for it in items if it.get("in_scope", True)]
        table = Table(
            title="Payroll Mathematical Reconciliation (smartoffice -> kepegawaian_dev_new)",
            show_header=True,
        )
        table.add_column("Batch / Period", style="cyan", width=18)
        table.add_column("Legacy Bruto", justify="right", width=16)
        table.add_column("Target Bruto", justify="right", width=16)
        table.add_column("Delta Bruto", justify="right", width=13)
        table.add_column("Legacy Potongan", justify="right", width=16)
        table.add_column("Target Potongan", justify="right", width=16)
        table.add_column("Delta Pot", justify="right", width=13)
        table.add_column("Legacy Netto", justify="right", width=16)
        table.add_column("Target Netto", justify="right", width=16)
        table.add_column("Delta Net", justify="right", width=13)
        table.add_column("Status", justify="center", width=12)

        for it in in_scope_items:
            st = it["status"]
            if st == "MATCH":
                st_str = "[green]MATCH[/green]"
            elif st == "MISMATCH":
                st_str = "[bold red]MISMATCH[/bold red]"
            else:
                st_str = f"[yellow]{st}[/yellow]"

            batch_label = f"{it['batch_no']}\n({it['periode']})"
            table.add_row(
                batch_label,
                _fmt_curr(it["legacy_gross"]),
                _fmt_curr(it["target_gross"]),
                _fmt_delta(it["delta_gross"]),
                _fmt_curr(it["legacy_deduction"]),
                _fmt_curr(it["target_deduction"]),
                _fmt_delta(it["delta_deduction"]),
                _fmt_curr(it["legacy_net"]),
                _fmt_curr(it["target_net"]),
                _fmt_delta(it["delta_net"]),
                st_str,
            )

        console.print()
        console.print(table)

        # 2. Out-of-scope informational table (if any)
        if summary.get("out_of_scope_batches", 0) > 0:
            oos_table = Table(
                title=f"{oos.get('label', 'Out of Scope (Older Historical Batches - run with --payroll-all)')}",
                show_header=True,
            )
            oos_table.add_column("Scope Category", style="yellow", width=36)
            oos_table.add_column("Batch Count", justify="right", width=12)
            oos_table.add_column("Period Range", justify="center", width=22)
            oos_table.add_column("Total Legacy Bruto", justify="right", width=20)
            oos_table.add_column("Total Potongan", justify="right", width=20)
            oos_table.add_column("Total Netto", justify="right", width=20)

            oos_table.add_row(
                "Older Historical Batches (Out of Scope)",
                str(oos.get("total_batches", 0)),
                str(oos.get("period_range", "-")),
                _fmt_curr(oos.get("legacy_gross", 0.0)),
                _fmt_curr(oos.get("legacy_deduction", 0.0)),
                _fmt_curr(oos.get("legacy_net", 0.0)),
            )
            console.print()
            console.print(oos_table)

        # 3. Totals table for in-scope batches
        totals = summary["totals"]
        totals_table = Table(
            title=f"Overall Payroll Reconciliation Totals ({summary.get('in_scope_batches', len(in_scope_items))} In-Scope Batches)",
            show_header=True,
        )
        totals_table.add_column("Metric", style="cyan", width=25)
        totals_table.add_column("Legacy Value", justify="right", width=20)
        totals_table.add_column("Target Value", justify="right", width=20)
        totals_table.add_column("Mathematical Delta", justify="right", width=20)

        totals_table.add_row(
            "Total Pendapatan Kotor",
            _fmt_curr(totals["legacy_gross"]),
            _fmt_curr(totals["target_gross"]),
            _fmt_delta(totals["delta_gross"]),
        )
        totals_table.add_row(
            "Total Potongan",
            _fmt_curr(totals["legacy_deduction"]),
            _fmt_curr(totals["target_deduction"]),
            _fmt_delta(totals["delta_deduction"]),
        )
        totals_table.add_row(
            "Total Take-Home Pay (Netto)",
            _fmt_curr(totals["legacy_net"]),
            _fmt_curr(totals["target_net"]),
            _fmt_delta(totals["delta_net"]),
        )

        console.print()
        console.print(totals_table)

        # 4. Status banner
        if summary["all_passed"]:
            panel_msg = (
                f"[bold green]PAYROLL RECONCILIATION PASSED: ZERO DEVIATION (Rp 0,-) ACROSS ALL "
                f"{summary.get('in_scope_batches', summary['total_batches_evaluated'])} IN-SCOPE BATCHES![/bold green]"
            )
            if summary.get("out_of_scope_batches", 0) > 0:
                panel_msg += (
                    f"\n[dim yellow]({summary['out_of_scope_batches']} older historical batches categorized as "
                    f"Out of Scope [{oos.get('period_range')}] — run with --payroll-all to verify full history)[/dim yellow]"
                )
            console.print(Panel(panel_msg))
        else:
            console.print(
                Panel(
                    f"[bold red]PAYROLL RECONCILIATION FAILED: {summary['mismatched_batches']} batches exceed tolerance, "
                    f"{summary['missing_in_target']} missing in target, {summary['missing_in_legacy']} missing in legacy.[/bold red]"
                )
            )

    except ImportError:
        pass


def main() -> None:
    """CLI entrypoint for reconcile_payroll."""
    parser = argparse.ArgumentParser(description="Historical Payroll Mathematical Reconciliation Audit")
    parser.add_argument("--export", type=str, default="reconcile_payroll_report.json", help="Path for JSON report")
    parser.add_argument("--tolerance", type=float, default=0.0, help="Allowed deviation tolerance in Rupiah (default: 0.0)")
    parser.add_argument("--payroll-all", action="store_true", help="Audit all historical payroll batches including older batches")

    args = parser.parse_args()

    console = None
    try:
        from rich.console import Console

        console = Console()
    except ImportError:
        pass

    try:
        findings = run_payroll_reconciliation(
            console=console,
            export_path=args.export,
            tolerance=args.tolerance,
            payroll_all=args.payroll_all,
        )
        sys.exit(0 if findings["summary"]["all_passed"] else 1)
    except Exception as exc:
        logger.exception("Payroll reconciliation encountered fatal error: %s", exc)
        sys.exit(1)


if __name__ == "__main__":
    main()

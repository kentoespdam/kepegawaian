"""Two-Phase File Attachment Migration Worker (Phase 2: Physical File Copy).

Complies with ADR-0052: Two-Phase File Attachment Migration.
- Reads pending/failed entries from SQLite file_sync_manifest.sqlite via ManifestManager.
- Verifies physical legacy file existence in source directory.
- Copies physical files to target attachments path (<ENUM>/<refId>/<hashedFileName>).
- Multithreaded execution using concurrent.futures.ThreadPoolExecutor (default: 4 workers).
- Verifies file integrity using SHA-256 / file size check.
- Updates SQLite manifest statuses: SYNCED, FAILED, or SKIPPED.
- Supports --dry-run, progress tracking, and detailed statistical reporting.
"""

from __future__ import annotations

import argparse
import hashlib
import logging
import os
import shutil
import sys
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Optional, Sequence

# Ensure repo root is in sys.path when invoked directly
_REPO_ROOT = Path(__file__).resolve().parents[3]
if str(_REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(_REPO_ROOT))

from tools.migration.config import config
from tools.migration.core.manifest import (
    STATUS_FAILED,
    STATUS_PENDING,
    STATUS_SKIPPED,
    STATUS_SYNCED,
    ManifestManager,
    manifest_manager,
)

logger = logging.getLogger(__name__)


@dataclass
class FileSyncStats:
    """Statistics accumulator for file synchronization operations."""

    total_records: int = 0
    synced_count: int = 0
    skipped_count: int = 0
    failed_count: int = 0
    total_bytes: int = 0
    start_time: float = field(default_factory=time.time)
    end_time: float = 0.0
    errors: list[dict[str, Any]] = field(default_factory=list)

    @property
    def elapsed_seconds(self) -> float:
        end = self.end_time if self.end_time > 0 else time.time()
        return max(0.001, end - self.start_time)

    @property
    def megabytes_transferred(self) -> float:
        return self.total_bytes / (1024 * 1024)

    @property
    def transfer_rate_mb_per_sec(self) -> float:
        return self.megabytes_transferred / self.elapsed_seconds


def compute_sha256(filepath: Path, chunk_size: int = 65536) -> str:
    """Computes SHA-256 hash digest of a file."""
    hasher = hashlib.sha256()
    with open(filepath, "rb") as f:
        for chunk in iter(lambda: f.read(chunk_size), b""):
            hasher.update(chunk)
    return hasher.hexdigest()


def sync_single_file(
    entry: dict[str, Any],
    source_base_dir: Path,
    target_base_dir: Path,
    manifest: ManifestManager,
    dry_run: bool = False,
    verify_checksum: bool = True,
) -> tuple[str, int, Optional[str]]:
    """Synchronizes a single file entry from legacy storage to target storage.

    Args:
        entry: Manifest record dict.
        source_base_dir: Base directory containing legacy attachments.
        target_base_dir: Base directory for target attachments.
        manifest: ManifestManager instance.
        dry_run: If True, checks files without copying or updating DB.
        verify_checksum: If True, validates SHA-256 between source and copy.

    Returns:
        tuple of (status, bytes_transferred, error_message_or_none)
    """
    entry_id = entry["id"]
    legacy_rel = entry["legacy_rel_path"]
    target_full = entry["target_full_path"]

    source_path = source_base_dir / legacy_rel

    # Normalize target path: if target_full starts with 'attachments/', strip prefix
    clean_target_rel = target_full
    if clean_target_rel.startswith("attachments/"):
        clean_target_rel = clean_target_rel[len("attachments/") :]
    elif clean_target_rel.startswith("/attachments/"):
        clean_target_rel = clean_target_rel[len("/attachments/") :]

    target_path = target_base_dir / clean_target_rel

    # 1. Verify existence of legacy physical file
    if not source_path.is_file():
        reason = f"Legacy file not found: {source_path}"
        logger.warning("Manifest ID %d SKIPPED: %s", entry_id, reason)
        if not dry_run:
            manifest.mark_skipped(entry_id, reason=reason)
        return STATUS_SKIPPED, 0, reason

    source_size = source_path.stat().st_size

    if dry_run:
        logger.debug("[DRY-RUN] Would copy: %s -> %s (%d bytes)", source_path, target_path, source_size)
        return STATUS_SYNCED, source_size, None

    # 2. Perform copy
    try:
        target_path.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source_path, target_path)

        # 3. Size verification
        target_size = target_path.stat().st_size
        if source_size != target_size:
            err = f"Size mismatch: expected {source_size} bytes, got {target_size} bytes"
            manifest.mark_failed(entry_id, error_msg=err)
            return STATUS_FAILED, 0, err

        # 4. Checksum verification
        if verify_checksum:
            src_sha = compute_sha256(source_path)
            tgt_sha = compute_sha256(target_path)
            if src_sha != tgt_sha:
                err = f"SHA-256 mismatch (src={src_sha}, tgt={tgt_sha})"
                manifest.mark_failed(entry_id, error_msg=err)
                return STATUS_FAILED, 0, err

        # 5. Success: update manifest
        manifest.mark_synced(entry_id, file_size=target_size)
        logger.debug("Successfully synced entry ID %d: %s -> %s", entry_id, source_path.name, target_path)
        return STATUS_SYNCED, target_size, None

    except Exception as exc:
        err = f"Copy error: {exc}"
        logger.error("Failed to copy entry ID %d (%s): %s", entry_id, source_path, exc)
        manifest.mark_failed(entry_id, error_msg=err)
        return STATUS_FAILED, 0, err


def run_file_sync(
    source_dir: Path | str | None = None,
    target_dir: Path | str | None = None,
    manifest: ManifestManager | None = None,
    workers: int = 4,
    limit: int | None = None,
    retry_failed: bool = False,
    dry_run: bool = False,
    verify_checksum: bool = True,
    console: Any | None = None,
) -> FileSyncStats:
    """Executes Phase 2 Physical File Attachment Synchronization.

    Args:
        source_dir: Legacy attachments directory root.
        target_dir: Target attachments directory root.
        manifest: ManifestManager instance. Defaults to singleton.
        workers: Number of worker threads (default: 4).
        limit: Max number of files to process in this run.
        retry_failed: If True, processes FAILED entries in addition to PENDING.
        dry_run: If True, scans without writing physical files or manifest.
        verify_checksum: If True, computes and verifies SHA-256 for copied files.
        console: Optional rich.console.Console instance for terminal rendering.

    Returns:
        FileSyncStats containing summary metrics.
    """
    manifest_mgr = manifest or manifest_manager
    manifest_mgr.ensure_initialized()

    src_path = Path(source_dir).resolve() if source_dir else config.storage.legacy_attachments_path
    tgt_path = Path(target_dir).resolve() if target_dir else config.storage.target_attachments_path

    if not dry_run:
        tgt_path.mkdir(parents=True, exist_ok=True)

    statuses = [STATUS_PENDING, STATUS_FAILED] if retry_failed else [STATUS_PENDING]
    entries = manifest_mgr.get_entries_by_statuses(statuses=statuses, limit=limit)

    stats = FileSyncStats(total_records=len(entries))

    logger.info("============================================================")
    logger.info("Starting Two-Phase File Synchronization Worker (Phase 2)")
    logger.info("Source Directory : %s", src_path)
    logger.info("Target Directory : %s", tgt_path)
    logger.info("Total Files Found: %d (Statuses: %s, Limit: %s)", len(entries), statuses, limit)
    logger.info("Workers: %d | Dry Run: %s | Verify Checksum: %s", workers, dry_run, verify_checksum)
    logger.info("============================================================")

    if not entries:
        logger.info("No files pending synchronization in manifest.")
        stats.end_time = time.time()
        _render_summary(stats, console)
        return stats

    # Initialize rich progress or fallback
    has_rich = False
    progress = None
    task_id = None
    if console is not None:
        try:
            from rich.progress import (
                BarColumn,
                DownloadColumn,
                Progress,
                SpinnerColumn,
                TextColumn,
                TimeElapsedColumn,
                TimeRemainingColumn,
                TransferSpeedColumn,
            )

            progress = Progress(
                SpinnerColumn(),
                TextColumn("[bold blue]{task.description}"),
                BarColumn(),
                TextColumn("[progress.percentage]{task.percentage:>3.0f}%"),
                TextColumn("({task.completed}/{task.total})"),
                TimeElapsedColumn(),
                TimeRemainingColumn(),
                console=console,
            )
            has_rich = True
        except ImportError:
            has_rich = False

    def _process_task():
        with ThreadPoolExecutor(max_workers=max(1, workers)) as executor:
            future_to_entry = {
                executor.submit(
                    sync_single_file,
                    entry=item,
                    source_base_dir=src_path,
                    target_base_dir=tgt_path,
                    manifest=manifest_mgr,
                    dry_run=dry_run,
                    verify_checksum=verify_checksum,
                ): item
                for item in entries
            }

            for future in as_completed(future_to_entry):
                item = future_to_entry[future]
                try:
                    status, transferred_bytes, err = future.result()
                    if status == STATUS_SYNCED:
                        stats.synced_count += 1
                        stats.total_bytes += transferred_bytes
                    elif status == STATUS_SKIPPED:
                        stats.skipped_count += 1
                    else:
                        stats.failed_count += 1
                        stats.errors.append({"id": item["id"], "file": item["legacy_rel_path"], "error": err})
                except Exception as exc:
                    stats.failed_count += 1
                    stats.errors.append({"id": item["id"], "file": item["legacy_rel_path"], "error": str(exc)})

                if progress and task_id is not None:
                    progress.advance(task_id)

    if has_rich and progress:
        with progress:
            task_id = progress.add_task("Syncing files...", total=len(entries))
            _process_task()
    else:
        _process_task()

    stats.end_time = time.time()
    _render_summary(stats, console)
    return stats


def _render_summary(stats: FileSyncStats, console: Any | None = None) -> None:
    """Renders formatted summary table to console or logger."""
    if console is not None:
        try:
            from rich.panel import Panel
            from rich.table import Table

            table = Table(title="File Synchronization Summary (Phase 2)", show_header=True)
            table.add_column("Metric", style="cyan", width=28)
            table.add_column("Value", style="bold white")

            table.add_row("Total Files Evaluated", str(stats.total_records))
            table.add_row("Successfully Synced", f"[green]{stats.synced_count}[/green]")
            table.add_row("Skipped (Not Found)", f"[yellow]{stats.skipped_count}[/yellow]")
            table.add_row("Failed (Errors)", f"[red]{stats.failed_count}[/red]")
            table.add_row("Total Data Transferred", f"{stats.megabytes_transferred:.2f} MB ({stats.total_bytes:,} bytes)")
            table.add_row("Elapsed Time", f"{stats.elapsed_seconds:.2f} s")
            table.add_row("Average Throughput", f"{stats.transfer_rate_mb_per_sec:.2f} MB/s")

            console.print()
            console.print(table)

            if stats.failed_count == 0:
                console.print(Panel("[bold green]All eligible files synchronized successfully![/bold green]"))
            else:
                console.print(Panel(f"[bold red]Completed with {stats.failed_count} errors. Check logs.[/bold red]"))
            return
        except ImportError:
            pass

    logger.info(
        "File Sync Summary: Total=%d, Synced=%d, Skipped=%d, Failed=%d, Transferred=%.2f MB, Time=%.2fs",
        stats.total_records,
        stats.synced_count,
        stats.skipped_count,
        stats.failed_count,
        stats.megabytes_transferred,
        stats.elapsed_seconds,
    )


def main() -> None:
    """CLI entrypoint for standalone file sync worker execution."""
    parser = argparse.ArgumentParser(description="Physical File Attachment Synchronization Worker (ADR-0052 Phase 2)")
    parser.add_argument("--source", type=str, default=None, help="Legacy attachments root directory")
    parser.add_argument("--target", type=str, default=None, help="Target attachments root directory")
    parser.add_argument("--manifest", type=str, default=None, help="Path to SQLite manifest database")
    parser.add_argument("--workers", type=int, default=4, help="Number of concurrent copy threads (default: 4)")
    parser.add_argument("--limit", type=int, default=None, help="Limit number of files to synchronize")
    parser.add_argument("--retry-failed", action="store_true", help="Retry previously FAILED files")
    parser.add_argument("--dry-run", action="store_true", help="Scan files without physical copying")
    parser.add_argument("--no-checksum", action="store_true", help="Skip SHA-256 checksum verification")

    args = parser.parse_args()

    console = None
    try:
        from rich.console import Console

        console = Console()
    except ImportError:
        pass

    custom_manifest = ManifestManager(args.manifest) if args.manifest else None

    stats = run_file_sync(
        source_dir=args.source,
        target_dir=args.target,
        manifest=custom_manifest,
        workers=args.workers,
        limit=args.limit,
        retry_failed=args.retry_failed,
        dry_run=args.dry_run,
        verify_checksum=not args.no_checksum,
        console=console,
    )

    sys.exit(0 if stats.failed_count == 0 else 1)


if __name__ == "__main__":
    main()

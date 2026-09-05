"""Unit tests for file sync worker and audit modules."""

import hashlib
import os
import shutil
import tempfile
import unittest
from pathlib import Path
from unittest.mock import MagicMock

from tools.migration.audit.reconcile_payroll import reconcile_batches
from tools.migration.audit.verify_integrity import (
    check_hibernate_envers,
    check_referential_integrity,
    check_table_quantities,
)
from tools.migration.core.manifest import (
    STATUS_FAILED,
    STATUS_PENDING,
    STATUS_SKIPPED,
    STATUS_SYNCED,
    ManifestManager,
)
from tools.migration.workers.file_sync_worker import (
    compute_sha256,
    run_file_sync,
    sync_single_file,
)


class TestFileSyncWorker(unittest.TestCase):
    """Test suite for file_sync_worker."""

    def setUp(self):
        self.temp_dir = tempfile.mkdtemp()
        self.source_dir = Path(self.temp_dir) / "source"
        self.target_dir = Path(self.temp_dir) / "target"
        self.source_dir.mkdir()
        self.target_dir.mkdir()

        self.db_path = Path(self.temp_dir) / "manifest.sqlite"
        self.manifest = ManifestManager(self.db_path)
        self.manifest.init_db()

    def tearDown(self):
        shutil.rmtree(self.temp_dir, ignore_errors=True)

    def test_compute_sha256(self):
        sample_file = self.source_dir / "sample.txt"
        content = b"Hello Kepegawaian Migration!"
        sample_file.write_bytes(content)

        expected = hashlib.sha256(content).hexdigest()
        actual = compute_sha256(sample_file)
        self.assertEqual(expected, actual)

    def test_sync_single_file_success(self):
        # Create dummy source file
        rel_path = "202305/test_doc.pdf"
        src_file = self.source_dir / rel_path
        src_file.parent.mkdir(parents=True, exist_ok=True)
        src_file.write_bytes(b"%PDF-1.4 Mock PDF Content")

        entry_id = self.manifest.add_entry(
            legacy_rel_path=rel_path,
            target_full_path="SK/101/hashed_doc.pdf",
            file_size=len(b"%PDF-1.4 Mock PDF Content"),
        )
        entry = self.manifest.get_entry_by_legacy_path(rel_path)
        self.assertIsNotNone(entry)

        status, size, err = sync_single_file(
            entry=entry,
            source_base_dir=self.source_dir,
            target_base_dir=self.target_dir,
            manifest=self.manifest,
            dry_run=False,
            verify_checksum=True,
        )

        self.assertEqual(status, STATUS_SYNCED)
        self.assertIsNone(err)
        self.assertTrue((self.target_dir / "SK/101/hashed_doc.pdf").exists())

        # Verify manifest updated
        updated_entry = self.manifest.get_entry_by_legacy_path(rel_path)
        self.assertEqual(updated_entry["status"], STATUS_SYNCED)

    def test_sync_single_file_skipped_when_missing(self):
        rel_path = "missing/ghost.pdf"
        entry_id = self.manifest.add_entry(
            legacy_rel_path=rel_path,
            target_full_path="SK/102/ghost.pdf",
        )
        entry = self.manifest.get_entry_by_legacy_path(rel_path)

        status, size, err = sync_single_file(
            entry=entry,
            source_base_dir=self.source_dir,
            target_base_dir=self.target_dir,
            manifest=self.manifest,
            dry_run=False,
        )

        self.assertEqual(status, STATUS_SKIPPED)
        self.assertIn("Legacy file not found", err)

        updated_entry = self.manifest.get_entry_by_legacy_path(rel_path)
        self.assertEqual(updated_entry["status"], STATUS_SKIPPED)

    def test_run_file_sync_multithreaded(self):
        # Create 5 mock files
        for i in range(5):
            rel = f"20230{i}/doc_{i}.pdf"
            src = self.source_dir / rel
            src.parent.mkdir(parents=True, exist_ok=True)
            src.write_bytes(f"Content for doc {i}".encode("utf-8"))

            self.manifest.add_entry(
                legacy_rel_path=rel,
                target_full_path=f"SK/{i}/hashed_{i}.pdf",
            )

        stats = run_file_sync(
            source_dir=self.source_dir,
            target_dir=self.target_dir,
            manifest=self.manifest,
            workers=2,
            dry_run=False,
        )

        self.assertEqual(stats.total_records, 5)
        self.assertEqual(stats.synced_count, 5)
        self.assertEqual(stats.failed_count, 0)
        self.assertEqual(stats.skipped_count, 0)


class TestAuditIntegrity(unittest.TestCase):
    """Test suite for verify_integrity logic."""

    def test_check_referential_integrity(self):
        mock_conn = MagicMock()
        # Mock SHOW COLUMNS and SELECT queries
        def fake_execute_query(conn, sql, params=None):
            if "SHOW COLUMNS" in sql:
                return [{"Field": "jenis_cuti_id"}]
            if "SELECT COUNT(*)" in sql:
                return [{"orphan_count": 0, "empty_nik_count": 0}]
            return []

        with unittest.mock.patch("tools.migration.audit.verify_integrity.execute_query", side_effect=fake_execute_query):
            checks = check_referential_integrity(mock_conn)
            self.assertTrue(len(checks) > 0)
            self.assertTrue(all(c["passed"] for c in checks))

    def test_check_hibernate_envers(self):
        mock_conn = MagicMock()

        def fake_execute_query(conn, sql, params=None):
            if "revinfo" in sql:
                return [{"total_rev": 1, "max_rev": 1}]
            if "SHOW TABLES" in sql:
                return [{"Table": "pegawai_aud"}]
            if "revtype" in sql:
                return [{"total_rows": 100, "baseline_rows": 100}]
            return []

        with unittest.mock.patch("tools.migration.audit.verify_integrity.execute_query", side_effect=fake_execute_query):
            res = check_hibernate_envers(mock_conn)
            self.assertTrue(res["revinfo_exists"])
            self.assertEqual(res["total_revisions"], 1)
            self.assertTrue(res["all_passed"])

    def test_check_table_quantities(self):
        mock_tgt = MagicMock()
        mock_leg = MagicMock()

        def fake_execute_query(conn, sql, params=None):
            return [{"cnt": 42}]

        with unittest.mock.patch("tools.migration.audit.verify_integrity.execute_query", side_effect=fake_execute_query):
            comp = check_table_quantities(mock_tgt, mock_leg)
            self.assertTrue(len(comp) > 0)
            self.assertEqual(comp[0]["delta"], 0)


class TestPayrollReconciliation(unittest.TestCase):
    """Test suite for reconcile_payroll logic."""

    def test_reconcile_batches_perfect_match(self):
        legacy = [
            {
                "batch_no": "BATCH-001",
                "period_month": "2025-01",
                "period_date": "2025-01-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            }
        ]
        target = [
            {
                "batch_no": "BATCH-001",
                "period_month": "2025-01",
                "period_date": "2025-01-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            }
        ]

        items, summary = reconcile_batches(legacy, target, tolerance=0.0)
        self.assertEqual(len(items), 1)
        self.assertEqual(items[0]["status"], "MATCH")
        self.assertEqual(items[0]["delta_gross"], 0.0)
        self.assertEqual(items[0]["delta_deduction"], 0.0)
        self.assertEqual(items[0]["delta_net"], 0.0)
        self.assertTrue(summary["all_passed"])

    def test_reconcile_batches_mismatch(self):
        legacy = [
            {
                "batch_no": "BATCH-001",
                "period_month": "2025-01",
                "period_date": "2025-01-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            }
        ]
        target = [
            {
                "batch_no": "BATCH-001",
                "period_month": "2025-01",
                "period_date": "2025-01-01",
                "gross_income": 100_000_500.0,  # Rp 500,- discrepancy
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_500.0,
            }
        ]

        items, summary = reconcile_batches(legacy, target, tolerance=0.0)
        self.assertEqual(items[0]["status"], "MISMATCH")
        self.assertEqual(items[0]["delta_gross"], 500.0)
        self.assertFalse(summary["all_passed"])

    def test_reconcile_batches_out_of_scope_historical(self):
        legacy = [
            {
                "batch_no": "202401-001",
                "period_month": "2024-01",
                "period_date": "2024-01-01",
                "gross_income": 50_000_000.0,
                "total_deduction": 5_000_000.0,
                "net_income": 45_000_000.0,
            },
            {
                "batch_no": "202510-001",
                "period_month": "2025-10",
                "period_date": "2025-10-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            },
        ]
        target = [
            {
                "batch_no": "202510-001",
                "period_month": "2025-10",
                "period_date": "2025-10-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            }
        ]

        items, summary = reconcile_batches(
            legacy, target, tolerance=0.0, payroll_all=False, cutoff_period="2025-09"
        )
        self.assertEqual(summary["in_scope_batches"], 1)
        self.assertEqual(summary["out_of_scope_batches"], 1)
        self.assertEqual(summary["matched_batches"], 1)
        self.assertEqual(summary["mismatched_batches"], 0)
        self.assertEqual(summary["missing_in_target"], 0)
        self.assertTrue(summary["all_passed"])

        oos_items = [it for it in items if not it["in_scope"]]
        self.assertEqual(len(oos_items), 1)
        self.assertEqual(oos_items[0]["status"], "OUT_OF_SCOPE")
        self.assertIn("Out of Scope", oos_items[0]["info"])

    def test_reconcile_batches_payroll_all_flag(self):
        legacy = [
            {
                "batch_no": "202401-001",
                "period_month": "2024-01",
                "period_date": "2024-01-01",
                "gross_income": 50_000_000.0,
                "total_deduction": 5_000_000.0,
                "net_income": 45_000_000.0,
            },
            {
                "batch_no": "202510-001",
                "period_month": "2025-10",
                "period_date": "2025-10-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            },
        ]
        target = [
            {
                "batch_no": "202510-001",
                "period_month": "2025-10",
                "period_date": "2025-10-01",
                "gross_income": 100_000_000.0,
                "total_deduction": 10_000_000.0,
                "net_income": 90_000_000.0,
            }
        ]

        items, summary = reconcile_batches(
            legacy, target, tolerance=0.0, payroll_all=True, cutoff_period="2025-09"
        )
        self.assertEqual(summary["in_scope_batches"], 2)
        self.assertEqual(summary["out_of_scope_batches"], 0)
        self.assertEqual(summary["missing_in_target"], 1)
        self.assertFalse(summary["all_passed"])


if __name__ == "__main__":
    unittest.main()

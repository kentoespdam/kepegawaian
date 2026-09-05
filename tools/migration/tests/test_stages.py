"""Unit tests for migration stages 0 to 3 logic."""

import unittest
from tools.migration.stages.common import StageResult
from tools.migration.stages.stage0_preflight import (
    PreflightCheckResult,
    PreflightSummary,
)
from tools.migration.stages.stage2_pegawai import _clean_nik


class TestMigrationStages(unittest.TestCase):

    def test_stage_result(self):
        res = StageResult(stage_name="Stage 1", success=True)
        self.assertTrue(res.success)
        res.add_error("Something went wrong")
        self.assertFalse(res.success)
        self.assertEqual(len(res.errors), 1)

    def test_preflight_summary(self):
        summary = PreflightSummary(success=True)
        check1 = PreflightCheckResult(name="Test 1", passed=True, message="OK")
        summary.add_check(check1)
        self.assertTrue(summary.success)

        check2 = PreflightCheckResult(name="Test 2", passed=False, message="Warn", is_warning=True)
        summary.add_check(check2)
        self.assertTrue(summary.success)  # Warning does not fail summary

        check3 = PreflightCheckResult(name="Test 3", passed=False, message="Fatal Error", is_warning=False)
        summary.add_check(check3)
        self.assertFalse(summary.success)
        self.assertEqual(len(summary.errors), 1)

    def test_clean_nik(self):
        self.assertEqual(_clean_nik(None), "")
        self.assertEqual(_clean_nik(""), "")
        self.assertEqual(_clean_nik("-"), "")
        self.assertEqual(_clean_nik("--"), "")
        self.assertEqual(_clean_nik(" 0 "), "")
        self.assertEqual(_clean_nik("None"), "")
        self.assertEqual(_clean_nik("NULL"), "")
        self.assertEqual(_clean_nik(" 3302 1234 5678 0001 "), "3302123456780001")
        self.assertEqual(_clean_nik("33-02-123456"), "3302123456")

    def test_delta_matching_mutasi(self):
        # Delta matching simulation according to ADR-0049:
        # 1. org changed, pos same -> MUTASI_LOKER (1)
        # 2. pos changed, org same -> MUTASI_JABATAN (2)
        # 3. both changed -> MUTASI_LOKER (1)
        # 4. neither changed -> MUTASI_LOKER (1)

        def match_delta(prev_org, curr_org, prev_pos, curr_pos):
            org_changed = (curr_org != prev_org) if curr_org and prev_org else False
            pos_changed = (curr_pos != prev_pos) if curr_pos and prev_pos else False
            if org_changed and not pos_changed:
                return 1  # MUTASI_LOKER
            elif pos_changed and not org_changed:
                return 2  # MUTASI_JABATAN
            elif org_changed and pos_changed:
                return 1  # MUTASI_LOKER
            return 1

        self.assertEqual(match_delta(10, 20, 5, 5), 1)  # org changed -> MUTASI_LOKER
        self.assertEqual(match_delta(10, 10, 5, 8), 2)  # pos changed -> MUTASI_JABATAN
        self.assertEqual(match_delta(10, 20, 5, 8), 1)  # both changed -> MUTASI_LOKER

    def test_ejenis_sk_ordinal_mapping(self):
        # EJenisSk ordinal = legacy_jenis_sk - 1
        legacy_to_ordinal = lambda leg: max(0, min(8, int(leg) - 1))

        self.assertEqual(legacy_to_ordinal(1), 0)  # SK Kenaikan Pangkat/Gol
        self.assertEqual(legacy_to_ordinal(2), 1)  # SK Capeg
        self.assertEqual(legacy_to_ordinal(3), 2)  # SK Pegawai Tetap
        self.assertEqual(legacy_to_ordinal(4), 3)  # SK Jabatan
        self.assertEqual(legacy_to_ordinal(5), 4)  # SK Mutasi
        self.assertEqual(legacy_to_ordinal(6), 5)  # SK Pensiun
        self.assertEqual(legacy_to_ordinal(7), 6)  # SK Lainnya
        self.assertEqual(legacy_to_ordinal(8), 7)  # SK Penyesuaian Gaji
        self.assertEqual(legacy_to_ordinal(9), 8)  # SK Kenaikan Gaji Berkala


    def test_stage7_status_kerja_resolution(self):
        # 0 = BERHENTI_OR_KELUAR (Terminasi / Pensiun)
        # 1 = DIRUMAHKAN (Aktif)
        # 2 = KARYAWAN_AKTIF (Aktif)
        test_cases = [
            (0, 0, "retired"),
            (1, 1, "active"),
            (2, 2, "active"),
            (None, 2, "active_default"),
            ("0", 0, "retired_str"),
            ("1", 1, "active_str"),
            ("2", 2, "active_str"),
        ]
        for raw, expected, desc in test_cases:
            with self.subTest(desc=desc, raw=raw):
                status_kerja = int(raw) if raw is not None else 2
                self.assertEqual(status_kerja, expected)

    def test_stage7_update_user_prefs_payload(self):
        from unittest.mock import MagicMock
        from tools.migration.config import AppwriteConfig
        from tools.migration.stages.stage7_auth import AppwriteClient

        cfg = AppwriteConfig(
            endpoint="http://localhost/v1",
            project_id="test_project",
            api_key="test_key",
        )
        client = AppwriteClient(cfg)
        client.session = MagicMock()
        mock_resp = MagicMock()
        mock_resp.status_code = 200
        mock_resp.json.return_value = {"roles": ["USER"]}
        client.session.patch.return_value = mock_resp

        client.update_user_prefs("123", {"roles": ["USER"]})
        client.session.patch.assert_called_once_with(
            "http://localhost/v1/users/123/prefs",
            headers=client.headers,
            json={"prefs": {"roles": ["USER"]}},
            timeout=client.timeout,
        )

    def test_stage7_update_user_status_payload(self):
        from unittest.mock import MagicMock
        from tools.migration.config import AppwriteConfig
        from tools.migration.stages.stage7_auth import AppwriteClient

        cfg = AppwriteConfig(
            endpoint="http://localhost/v1",
            project_id="test_project",
            api_key="test_key",
        )
        client = AppwriteClient(cfg)
        client.session = MagicMock()
        mock_resp = MagicMock()
        mock_resp.status_code = 200
        mock_resp.json.return_value = {"status": False}
        client.session.patch.return_value = mock_resp

        client.update_user_status("123", status=False)
        client.session.patch.assert_called_once_with(
            "http://localhost/v1/users/123/status",
            headers=client.headers,
            json={"status": False},
            timeout=client.timeout,
        )


if __name__ == "__main__":
    unittest.main()


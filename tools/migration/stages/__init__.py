"""Migration stages package."""

from __future__ import annotations

from tools.migration.stages.common import StageResult
from tools.migration.stages.stage0_preflight import (
    PreflightCheckResult,
    PreflightSummary,
    run_stage0,
)
from tools.migration.stages.stage1_master import run_stage1
from tools.migration.stages.stage2_pegawai import run_stage2
from tools.migration.stages.stage3_kepegawaian import run_stage3

try:
    from tools.migration.stages.stage4_cuti import run_stage4_cuti
except ImportError:
    run_stage4_cuti = None  # type: ignore[assignment]

try:
    from tools.migration.stages.stage5_penggajian import run_stage5_penggajian
except ImportError:
    run_stage5_penggajian = None  # type: ignore[assignment]

try:
    from tools.migration.stages.stage6_lampiran import run_stage6_lampiran
except ImportError:
    run_stage6_lampiran = None  # type: ignore[assignment]

try:
    from tools.migration.stages.stage7_auth import run_stage7_auth
except ImportError:
    run_stage7_auth = None  # type: ignore[assignment]

# Aliases for convenience
run_stage4 = run_stage4_cuti
run_stage5 = run_stage5_penggajian
run_stage6 = run_stage6_lampiran
run_stage7 = run_stage7_auth

__all__ = [
    "StageResult",
    "PreflightCheckResult",
    "PreflightSummary",
    "run_stage0",
    "run_stage1",
    "run_stage2",
    "run_stage3",
    "run_stage4_cuti",
    "run_stage5_penggajian",
    "run_stage6_lampiran",
    "run_stage7_auth",
    "run_stage4",
    "run_stage5",
    "run_stage6",
    "run_stage7",
]

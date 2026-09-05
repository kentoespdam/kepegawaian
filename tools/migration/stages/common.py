"""Common types and utilities for migration stages."""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any


@dataclass
class StageResult:
    """Standardized result container returned by all migration stages."""

    stage_name: str
    success: bool
    records_extracted: int = 0
    records_upserted: int = 0
    details: dict[str, Any] = field(default_factory=dict)
    errors: list[str] = field(default_factory=list)

    def add_error(self, err: str) -> None:
        self.success = False
        self.errors.append(err)

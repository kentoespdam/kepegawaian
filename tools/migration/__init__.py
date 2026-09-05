"""Data migration microapp package for kepegawaian.

Provides ETL migration stages, Envers baseline injection, ID mapping,
and physical attachment synchronization.
"""

from tools.migration.config import config

__all__ = ["config"]

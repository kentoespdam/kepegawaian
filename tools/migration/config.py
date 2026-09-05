"""Configuration management for data migration microapp.

Loads settings from environment variables with fallback defaults matching
the project's database, Appwrite, and physical file storage environments.
"""

from __future__ import annotations

import os
from dataclasses import dataclass
from pathlib import Path
from typing import Optional


def _load_env_file(dotenv_path: Optional[Path] = None) -> None:
    """Lightweight .env loader without external dependency.

    Loads KEY=VALUE pairs into os.environ if they are not already set.
    """
    if dotenv_path is None:
        # Search current working dir, repo root, and migration dir
        candidates = [
            Path.cwd() / ".env",
            Path(__file__).resolve().parent.parent.parent / ".env",
            Path(__file__).resolve().parent / ".env",
        ]
        for candidate in candidates:
            if candidate.is_file():
                dotenv_path = candidate
                break

    if dotenv_path and dotenv_path.is_file():
        try:
            with open(dotenv_path, "r", encoding="utf-8") as f:
                for line in f:
                    line = line.strip()
                    if not line or line.startswith("#"):
                        continue
                    if "=" in line:
                        key, _, value = line.partition("=")
                        key = key.strip()
                        value = value.strip().strip("'\"")
                        if key and key not in os.environ:
                            os.environ[key] = value
        except Exception:
            pass


# Automatically load .env on module import
_load_env_file()


@dataclass(frozen=True)
class DatabaseConfig:
    """Database connection configuration parameters."""

    host: str
    port: int
    user: str
    password: str
    schema: str
    charset: str = "utf8mb4"

    @property
    def connection_params(self) -> dict:
        """Returns parameters dictionary suitable for pymysql.connect()."""
        return {
            "host": self.host,
            "port": self.port,
            "user": self.user,
            "password": self.password,
            "database": self.schema,
            "charset": self.charset,
        }


@dataclass(frozen=True)
class AppwriteConfig:
    """Appwrite authentication and API configuration parameters."""

    endpoint: str
    project_id: str
    api_key: str

    @property
    def headers(self) -> dict[str, str]:
        """HTTP headers for authenticated Appwrite REST API calls."""
        return {
            "X-Appwrite-Project": self.project_id,
            "X-Appwrite-Key": self.api_key,
            "Content-Type": "application/json",
        }


@dataclass(frozen=True)
class StorageConfig:
    """File storage path configuration for physical attachments and sync manifest."""

    legacy_attachments_path: Path
    target_attachments_path: Path
    manifest_db_path: Path

    def ensure_target_dir(self) -> Path:
        """Ensures the target attachments directory exists on disk."""
        self.target_attachments_path.mkdir(parents=True, exist_ok=True)
        return self.target_attachments_path


@dataclass(frozen=True)
class MigrationConfig:
    """Unified configuration container for migration stages and workers."""

    legacy_db: DatabaseConfig
    target_db: DatabaseConfig
    appwrite: AppwriteConfig
    storage: StorageConfig


def load_config() -> MigrationConfig:
    """Constructs and returns MigrationConfig from environment variables or defaults."""
    # DB Legacy
    legacy_host = os.getenv("LEGACY_DB_HOST", "192.168.230.84")
    legacy_port = int(os.getenv("LEGACY_DB_PORT", "3307"))
    legacy_user = os.getenv("LEGACY_DB_USER", "dev")
    legacy_password = os.getenv("LEGACY_DB_PASSWORD", "password")
    legacy_schema = os.getenv("LEGACY_DB_SCHEMA", "smartoffice")

    legacy_db = DatabaseConfig(
        host=legacy_host,
        port=legacy_port,
        user=legacy_user,
        password=legacy_password,
        schema=legacy_schema,
    )

    # DB Target (defaults can fall back to standard DB_* variables if present)
    target_host = os.getenv("TARGET_DB_HOST", os.getenv("DB_HOST", "192.168.230.84"))
    target_port = int(os.getenv("TARGET_DB_PORT", os.getenv("DB_PORT", "3307")))
    target_user = os.getenv("TARGET_DB_USER", os.getenv("DB_USER", "dev"))
    target_password = os.getenv("TARGET_DB_PASSWORD", os.getenv("DB_PASSWORD", "password"))
    target_schema = os.getenv("TARGET_DB_SCHEMA", os.getenv("DB_SCHEMA", "kepegawaian_dev_new"))

    target_db = DatabaseConfig(
        host=target_host,
        port=target_port,
        user=target_user,
        password=target_password,
        schema=target_schema,
    )

    # Appwrite config
    appwrite_endpoint = os.getenv("APPWRITE_ENDPOINT", "http://192.168.230.254:82/v1")
    appwrite_project_id = os.getenv("APPWRITE_PROJECT_ID", "65cd62cc3385d8434a53")
    default_api_key = (
        "061b4abb7743ecc570cc693483b36bc0f50616b2631c5f7cec3825e15cd196d7"
        "03434b9c7d6a9bb0d44ef7d8ca9eb9d570a916c2e4867993b37fc29d9579278a"
        "cdba9d2ad485eca0381e975aedf5f3217cf6653f4234265975c38186aa53ef57"
        "2702a298e16576843d7dfd47cb77a649fff0f4460876c52b4c7d84c0b2c74706"
    )
    appwrite_api_key = os.getenv("APPWRITE_API_KEY", default_api_key)

    appwrite = AppwriteConfig(
        endpoint=appwrite_endpoint,
        project_id=appwrite_project_id,
        api_key=appwrite_api_key,
    )

    # Storage and file paths
    legacy_att_path_str = os.getenv(
        "LEGACY_ATTACHMENTS_PATH",
        "/home/dev/php/smartoffice/server/attachments",
    )
    # Default target attachment path: /app/attachments if running in container, else ./attachments
    target_att_path_env = os.getenv("TARGET_ATTACHMENTS_PATH")
    if target_att_path_env:
        target_att_path = Path(target_att_path_env)
    elif Path("/app/attachments").exists():
        target_att_path = Path("/app/attachments")
    else:
        target_att_path = Path("./attachments").resolve()

    manifest_db_path = Path(
        os.getenv("MANIFEST_DB_PATH", "file_sync_manifest.sqlite")
    ).resolve()

    storage = StorageConfig(
        legacy_attachments_path=Path(legacy_att_path_str),
        target_attachments_path=target_att_path,
        manifest_db_path=manifest_db_path,
    )

    return MigrationConfig(
        legacy_db=legacy_db,
        target_db=target_db,
        appwrite=appwrite,
        storage=storage,
    )


# Default singleton instance for direct import
config = load_config()

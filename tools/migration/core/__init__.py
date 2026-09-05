"""Core modules for data migration microapp."""

from tools.migration.core.db import (
    batch_insert,
    batch_upsert,
    create_connection,
    execute_cross_db_query,
    execute_query,
    get_cross_db_connection,
    get_db_connection,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.envers import (
    REVTYPE_ADD,
    REVTYPE_DEL,
    REVTYPE_MOD,
    create_revision,
    get_latest_revision,
    inject_audit_snapshot,
    snapshot_table_to_audit,
)
from tools.migration.core.manifest import (
    STATUS_FAILED,
    STATUS_PENDING,
    STATUS_SKIPPED,
    STATUS_SYNCED,
    ManifestManager,
    manifest_manager,
)
from tools.migration.core.state import (
    batch_set_mappings,
    compute_record_hash,
    get_all_mappings,
    get_mapping,
    get_mapping_record,
    has_mapping,
    init_state_table,
    set_mapping,
)

__all__ = [
    # DB
    "create_connection",
    "get_db_connection",
    "get_legacy_connection",
    "get_target_connection",
    "get_cross_db_connection",
    "execute_query",
    "execute_cross_db_query",
    "batch_insert",
    "batch_upsert",
    # State
    "init_state_table",
    "compute_record_hash",
    "set_mapping",
    "batch_set_mappings",
    "get_mapping",
    "get_mapping_record",
    "get_all_mappings",
    "has_mapping",
    # Envers
    "REVTYPE_ADD",
    "REVTYPE_MOD",
    "REVTYPE_DEL",
    "create_revision",
    "get_latest_revision",
    "inject_audit_snapshot",
    "snapshot_table_to_audit",
    # Manifest
    "STATUS_PENDING",
    "STATUS_SYNCED",
    "STATUS_FAILED",
    "STATUS_SKIPPED",
    "ManifestManager",
    "manifest_manager",
]

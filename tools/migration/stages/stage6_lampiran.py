"""Stage 6: Attachments & File Sync Manifest (Lampiran) Migration Runner.

Complies with ADR-0052: Two-Phase File Attachment Migration.
- Reads smartoffice.attachments for kepegawaian categories:
    * ref_type 18 -> lampiran_sk (with resolved EJenisSk)
    * ref_type 6, 8, 11, 12, 13, 7 -> lampiran_profil (with EJenisLampiranProfil enum)
    * ref_type 17 -> riwayat_sp (embedded file_name & hashed_file_name)
    * ref_type 9, 15, 16 -> manifest tracking
    * Profile photos (202 records) in emp_profile -> biodata.foto_profil
- Generates 32-character hexadecimal UUID without hyphens (uuid.uuid4().hex).
- Records every file operation to SQLite manifest (core.manifest.ManifestManager):
    * legacy_rel_path, target_full_path, file_size, mime_type, status='PENDING'
- Injects baseline revisions into Hibernate Envers audit tables:
    * lampiran_sk_aud
    * lampiran_profil_aud
"""

from __future__ import annotations

import logging
import mimetypes
import uuid
from datetime import date, datetime
from typing import Any, Sequence

from tools.migration.core.db import (
    batch_insert,
    execute_query,
    get_legacy_connection,
    get_target_connection,
)
from tools.migration.core.envers import (
    REVTYPE_ADD,
    create_revision,
    snapshot_table_to_audit,
)
from tools.migration.core.manifest import (
    STATUS_PENDING,
    ManifestManager,
    manifest_manager,
)
from tools.migration.core.state import (
    batch_set_mappings,
    get_all_mappings,
    get_mapping,
    init_state_table,
)

logger = logging.getLogger(__name__)

DOMAIN_NAME = "lampiran"

# Mapping of smartoffice ref_type to target EJenisLampiranProfil ordinal
# 0: PROFIL_KELUARGA, 1: PROFIL_PENDIDIKAN, 2: PROFIL_PELATIHAN,
# 3: PROFIL_KEAHLIAN, 4: FOTO_PROFIL, 5: KARTU_IDENTITAS, 6: PROFIL_PENGALAMAN_KERJA
REF_TYPE_TO_LAMPIRAN_PROFIL: dict[int, tuple[int, str, str]] = {
    6: (1, "PROFIL_PENDIDIKAN", "pendidikan"),          # REF_TYPE_EDUCATION
    8: (3, "PROFIL_KEAHLIAN", "keahlian"),              # REF_TYPE_SKILL
    11: (2, "PROFIL_PELATIHAN", "pelatihan"),           # REF_TYPE_TRAINING
    12: (5, "KARTU_IDENTITAS", "kartu_identitas"),      # REF_TYPE_CARD
    13: (0, "PROFIL_KELUARGA", "profil_keluarga"),      # REF_TYPE_FAMILY
    7: (6, "PROFIL_PENGALAMAN_KERJA", "pengalaman_kerja"), # REF_TYPE_WORK
}

# General MIME type fallback
EXT_TO_MIME: dict[str, str] = {
    "pdf": "application/pdf",
    "jpg": "image/jpeg",
    "jpeg": "image/jpeg",
    "png": "image/png",
    "doc": "application/msword",
    "docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "xls": "application/vnd.ms-excel",
    "xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
}


def _resolve_mime_type(file_ext: str | None, filename: str | None) -> str:
    """Infers MIME type from extension or filename."""
    ext = (file_ext or "").lstrip(".").lower()
    if not ext and filename and "." in filename:
        ext = filename.rsplit(".", 1)[-1].lower()
    if ext in EXT_TO_MIME:
        return EXT_TO_MIME[ext]
    guessed, _ = mimetypes.guess_type(filename or f"file.{ext}")
    return guessed or "application/octet-stream"


def _format_legacy_rel_path(upload_date: Any, system_filename: str) -> str:
    """Constructs the relative path within the legacy attachments directory."""
    fn = system_filename.strip()
    # If system_filename already contains folder prefix (e.g. '202305/file.pdf')
    if "/" in fn:
        return fn

    if isinstance(upload_date, (datetime, date)):
        folder = upload_date.strftime("%Y%m")
    elif isinstance(upload_date, str) and len(upload_date) >= 7:
        # e.g. '2023-05-12' -> '202305'
        clean_date = upload_date.replace("-", "")[:6]
        folder = clean_date if clean_date.isdigit() else "general"
    else:
        folder = "general"

    return f"{folder}/{fn}"


def migrate_attachments_metadata(
    target_conn: Any,
    legacy_conn: Any,
    manifest: ManifestManager,
    dry_run: bool = False,
) -> tuple[int, int, int, list[int], list[int]]:
    """Migrates attachments metadata into lampiran_sk, lampiran_profil, and riwayat_sp.

    Returns:
        tuple of (sk_count, profil_count, sp_count, target_sk_ids, target_profil_ids)
    """
    logger.info("Extracting kepegawaian attachments from legacy database...")

    sql_att = """
    SELECT
        id,
        ref_type,
        ref_id,
        file_ext,
        file_size,
        original_filename,
        system_filename,
        doc_notes,
        upload_date,
        upload_by_name,
        status
    FROM attachments
    WHERE ref_type IN (6, 7, 8, 9, 11, 12, 13, 15, 16, 17, 18)
      AND status = 1
    ORDER BY id ASC
    """
    legacy_attachments = execute_query(legacy_conn, sql_att)
    logger.info("Found %d kepegawaian attachment records in legacy database", len(legacy_attachments))

    # Pre-fetch ID mappings for referenced entities
    sk_id_map = get_all_mappings(target_conn, domain="kepegawaian", legacy_table="riwayat_sk")
    sp_id_map = get_all_mappings(target_conn, domain="kepegawaian", legacy_table="riwayat_sp")

    # Mapping cache for profil tables
    profil_maps: dict[str, dict[str, str]] = {
        tbl: get_all_mappings(target_conn, domain="pegawai", legacy_table=tbl)
        for tbl in ("pendidikan", "keahlian", "pelatihan", "kartu_identitas", "profil_keluarga", "pengalaman_kerja")
    }

    # Pre-fetch riwayat_sk jenis_sk ordinals
    sk_info_rows = execute_query(target_conn, "SELECT id, jenis_sk FROM riwayat_sk WHERE is_deleted = 0")
    sk_jenis_map: dict[int, int] = {
        row["id"]: (row["jenis_sk"] if row.get("jenis_sk") is not None else 6)
        for row in sk_info_rows
    }

    lampiran_sk_rows: list[dict[str, Any]] = []
    lampiran_profil_rows: list[dict[str, Any]] = []
    manifest_entries: list[dict[str, Any]] = []

    target_sk_ids: list[int] = []
    target_profil_ids: list[int] = []
    sp_updated_count = 0

    for att in legacy_attachments:
        ref_type = int(att["ref_type"])
        legacy_ref_id = str(att["ref_id"])
        sys_fn = str(att.get("system_filename") or "").strip()
        orig_fn = str(att.get("original_filename") or sys_fn).strip()
        file_ext = str(att.get("file_ext") or "").lstrip(".").lower()

        if not sys_fn:
            continue

        if not file_ext and "." in sys_fn:
            file_ext = sys_fn.rsplit(".", 1)[-1].lower()

        # UUID-v4 32-hex character without hyphens (ADR-0052)
        hashed_name = uuid.uuid4().hex
        mime_type = _resolve_mime_type(file_ext, orig_fn)
        file_size = int(att.get("file_size") or 0)
        upload_date = att.get("upload_date") or datetime.now()
        legacy_rel_path = _format_legacy_rel_path(upload_date, sys_fn)

        # 1. Ref 18: Surat Keputusan (SK)
        if ref_type == 18:
            target_sk_id = int(sk_id_map.get(legacy_ref_id, legacy_ref_id))
            ref_jenis_sk = sk_jenis_map.get(target_sk_id, 6)  # Default: 6 (SK_LAINNYA)
            target_full_path = f"SK/{target_sk_id}/{hashed_name}.{file_ext}" if file_ext else f"SK/{target_sk_id}/{hashed_name}"

            lampiran_sk_rows.append({
                "ref": ref_jenis_sk,
                "ref_id": target_sk_id,
                "file_name": orig_fn,
                "hashed_file_name": hashed_name,
                "mime_type": mime_type,
                "notes": att.get("doc_notes") or "",
                "disetujui": 1,
                "tanggal_pengajuan": upload_date,
                "tanggal_disetujui": upload_date,
                "disetujui_oleh": att.get("upload_by_name") or "SYSTEM",
                "is_deleted": 0,
                "created_at": upload_date,
                "created_by": "MIGRATION",
            })

            manifest_entries.append({
                "legacy_rel_path": legacy_rel_path,
                "target_full_path": target_full_path,
                "file_size": file_size,
                "mime_type": mime_type,
                "ref_type": "SK",
                "ref_id": str(target_sk_id),
                "status": STATUS_PENDING,
            })

        # 2. Ref 6, 8, 11, 12, 13, 7: Profile Attachments (lampiran_profil)
        elif ref_type in REF_TYPE_TO_LAMPIRAN_PROFIL:
            enum_ord, enum_name, target_tbl = REF_TYPE_TO_LAMPIRAN_PROFIL[ref_type]
            table_map = profil_maps.get(target_tbl, {})
            target_ref_id = int(table_map.get(legacy_ref_id, legacy_ref_id))
            target_full_path = f"{enum_name}/{target_ref_id}/{hashed_name}.{file_ext}" if file_ext else f"{enum_name}/{target_ref_id}/{hashed_name}"

            lampiran_profil_rows.append({
                "ref": enum_ord,
                "ref_id": target_ref_id,
                "file_name": orig_fn,
                "hashed_file_name": hashed_name,
                "mime_type": mime_type,
                "notes": att.get("doc_notes") or "",
                "disetujui": 1,
                "tanggal_pengajuan": upload_date,
                "tanggal_disetujui": upload_date,
                "disetujui_oleh": att.get("upload_by_name") or "SYSTEM",
                "is_deleted": 0,
                "created_at": upload_date,
                "created_by": "MIGRATION",
            })

            manifest_entries.append({
                "legacy_rel_path": legacy_rel_path,
                "target_full_path": target_full_path,
                "file_size": file_size,
                "mime_type": mime_type,
                "ref_type": enum_name,
                "ref_id": str(target_ref_id),
                "status": STATUS_PENDING,
            })

        # 3. Ref 17: Surat Peringatan (riwayat_sp embedded)
        elif ref_type == 17:
            target_sp_id = int(sp_id_map.get(legacy_ref_id, legacy_ref_id))
            target_full_path = f"SP/{target_sp_id}/{hashed_name}.{file_ext}" if file_ext else f"SP/{target_sp_id}/{hashed_name}"

            if not dry_run:
                with target_conn.cursor() as cursor:
                    cursor.execute(
                        """
                        UPDATE riwayat_sp
                        SET file_name = %s,
                            hashed_file_name = %s,
                            mime_type = %s,
                            updated_at = NOW(),
                            updated_by = 'MIGRATION'
                        WHERE id = %s
                        """,
                        (orig_fn, hashed_name, mime_type, target_sp_id),
                    )
            sp_updated_count += 1

            manifest_entries.append({
                "legacy_rel_path": legacy_rel_path,
                "target_full_path": target_full_path,
                "file_size": file_size,
                "mime_type": mime_type,
                "ref_type": "SP",
                "ref_id": str(target_sp_id),
                "status": STATUS_PENDING,
            })

        # 4. Other Kepegawaian Attachments (Ref 9: Bank, 15: Mutasi, 16: Contract)
        else:
            category_name = f"REF_{ref_type}"
            target_full_path = f"{category_name}/{legacy_ref_id}/{hashed_name}.{file_ext}" if file_ext else f"{category_name}/{legacy_ref_id}/{hashed_name}"
            manifest_entries.append({
                "legacy_rel_path": legacy_rel_path,
                "target_full_path": target_full_path,
                "file_size": file_size,
                "mime_type": mime_type,
                "ref_type": category_name,
                "ref_id": str(legacy_ref_id),
                "status": STATUS_PENDING,
            })

    # Batch insert lampiran_sk
    sk_count = 0
    if not dry_run and lampiran_sk_rows:
        for sk_row in lampiran_sk_rows:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO lampiran_sk (
                        ref, ref_id, file_name, hashed_file_name, mime_type, notes,
                        disetujui, tanggal_pengajuan, tanggal_disetujui, disetujui_oleh,
                        is_deleted, created_at, created_by, updated_at, updated_by
                    ) VALUES (
                        %s, %s, %s, %s, %s, %s,
                        %s, %s, %s, %s,
                        0, %s, 'MIGRATION', NOW(), 'MIGRATION'
                    )
                    """,
                    (
                        sk_row["ref"],
                        sk_row["ref_id"],
                        sk_row["file_name"],
                        sk_row["hashed_file_name"],
                        sk_row["mime_type"],
                        sk_row["notes"],
                        sk_row["disetujui"],
                        sk_row["tanggal_pengajuan"],
                        sk_row["tanggal_disetujui"],
                        sk_row["disetujui_oleh"],
                        sk_row["created_at"],
                    ),
                )
                target_sk_ids.append(cursor.lastrowid)
        sk_count = len(target_sk_ids)
    elif dry_run:
        sk_count = len(lampiran_sk_rows)

    # Batch insert lampiran_profil
    profil_count = 0
    if not dry_run and lampiran_profil_rows:
        for pr_row in lampiran_profil_rows:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    INSERT INTO lampiran_profil (
                        ref, ref_id, file_name, hashed_file_name, mime_type, notes,
                        disetujui, tanggal_pengajuan, tanggal_disetujui, disetujui_oleh,
                        is_deleted, created_at, created_by, updated_at, updated_by
                    ) VALUES (
                        %s, %s, %s, %s, %s, %s,
                        %s, %s, %s, %s,
                        0, %s, 'MIGRATION', NOW(), 'MIGRATION'
                    )
                    """,
                    (
                        pr_row["ref"],
                        pr_row["ref_id"],
                        pr_row["file_name"],
                        pr_row["hashed_file_name"],
                        pr_row["mime_type"],
                        pr_row["notes"],
                        pr_row["disetujui"],
                        pr_row["tanggal_pengajuan"],
                        pr_row["tanggal_disetujui"],
                        pr_row["disetujui_oleh"],
                        pr_row["created_at"],
                    ),
                )
                target_profil_ids.append(cursor.lastrowid)
        profil_count = len(target_profil_ids)
    elif dry_run:
        profil_count = len(lampiran_profil_rows)

    # Record all files into SQLite sync manifest
    if not dry_run and manifest_entries:
        manifest.batch_add_entries(manifest_entries)
        logger.info("Recorded %d file copy operations in SQLite manifest", len(manifest_entries))

    logger.info(
        "Attachment metadata migration: %d lampiran_sk, %d lampiran_profil, %d riwayat_sp updated",
        sk_count,
        profil_count,
        sp_updated_count,
    )
    return sk_count, profil_count, sp_updated_count, target_sk_ids, target_profil_ids


def migrate_profile_photos(
    target_conn: Any,
    legacy_conn: Any,
    manifest: ManifestManager,
    dry_run: bool = False,
) -> int:
    """Migrates profile photos from legacy emp_profile to target biodata.foto_profil.

    Returns:
        Number of profile photos processed.
    """
    logger.info("Extracting employee profile photos from smartoffice.emp_profile...")

    sql_photo = """
    SELECT
        ep.emp_profile_id,
        e.emp_code,
        ep.emp_photo
    FROM emp_profile ep
    JOIN employee e ON e.emp_profile_id = ep.emp_profile_id
    WHERE ep.emp_photo IS NOT NULL
      AND TRIM(ep.emp_photo) != ''
      AND ep.emp_photo NOT IN ('male.png', 'female.png')
    ORDER BY ep.emp_profile_id ASC
    """
    legacy_photos = execute_query(legacy_conn, sql_photo)
    logger.info("Found %d legacy employee profile photos to migrate", len(legacy_photos))

    # Pre-fetch NIPAM -> NIK mapping (from biodata NIPAM fallback or pegawai join)
    sql_nipam_nik = """
    SELECT p.nipam, p.biodata_id AS nik
    FROM pegawai p
    WHERE p.is_deleted = 0
    """
    mapping_rows = execute_query(target_conn, sql_nipam_nik)
    nipam_to_nik: dict[str, str] = {
        str(r["nipam"]).strip(): str(r["nik"]).strip() for r in mapping_rows if r.get("nipam") and r.get("nik")
    }

    manifest_entries: list[dict[str, Any]] = []
    updated_count = 0

    for item in legacy_photos:
        emp_code = str(item.get("emp_code") or "").strip()
        photo_fn = str(item.get("emp_photo") or "").strip()
        nik = nipam_to_nik.get(emp_code)

        if not photo_fn or not nik:
            continue

        file_ext = photo_fn.rsplit(".", 1)[-1].lower() if "." in photo_fn else "jpg"
        hashed_name = uuid.uuid4().hex
        final_fn = f"{hashed_name}.{file_ext}"

        legacy_rel_path = f"employee/{photo_fn}"
        target_full_path = f"FOTO_PROFIL/{nik}/{final_fn}"
        mime_type = _resolve_mime_type(file_ext, photo_fn)

        if not dry_run:
            with target_conn.cursor() as cursor:
                cursor.execute(
                    """
                    UPDATE biodata
                    SET foto_profil = %s,
                        updated_at = NOW(),
                        updated_by = 'MIGRATION'
                    WHERE nik = %s
                    """,
                    (final_fn, nik),
                )

        manifest_entries.append({
            "legacy_rel_path": legacy_rel_path,
            "target_full_path": target_full_path,
            "file_size": 0,
            "mime_type": mime_type,
            "ref_type": "FOTO_PROFIL",
            "ref_id": nik,
            "status": STATUS_PENDING,
        })
        updated_count += 1

    if not dry_run and manifest_entries:
        manifest.batch_add_entries(manifest_entries)

    logger.info("Migrated %d profile photo references to biodata.foto_profil", updated_count)
    return updated_count


def run_stage6_lampiran(
    target_conn: Any | None = None,
    legacy_conn: Any | None = None,
    manifest: ManifestManager | None = None,
    dry_run: bool = False,
    console: Any | None = None,
) -> dict[str, Any]:
    """Entrypoint for Stage 6: Attachments & Manifest Migration (ADR-0052 Phase 1).

    Args:
        target_conn: Connection to kepegawaian_dev_new database. If None, acquires one.
        legacy_conn: Connection to smartoffice database. If None, acquires one.
        manifest: SQLite ManifestManager instance. Defaults to singleton.
        dry_run: If True, parses without writing to target database or manifest.
        console: Optional rich console for display.

    Returns:
        Summary metrics dictionary.
    """
    logger.info("============================================================")
    logger.info("Executing Stage 6: Attachments & File Sync Manifest (ADR-0052)")
    logger.info("============================================================")

    if target_conn is None:
        with get_target_connection(autocommit=False) as managed_conn:
            return run_stage6_lampiran(
                target_conn=managed_conn,
                legacy_conn=legacy_conn,
                manifest=manifest,
                dry_run=dry_run,
                console=console,
            )

    init_state_table(target_conn)
    manifest_mgr = manifest or manifest_manager
    manifest_mgr.ensure_initialized()

    def _execute(leg_conn: Any) -> dict[str, Any]:
        # Step 6.1: Attachments Metadata Migration
        sk_count, profil_count, sp_count, target_sk_ids, target_profil_ids = migrate_attachments_metadata(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            manifest=manifest_mgr,
            dry_run=dry_run,
        )

        # Step 6.2: Profile Photos Migration
        photos_count = migrate_profile_photos(
            target_conn=target_conn,
            legacy_conn=leg_conn,
            manifest=manifest_mgr,
            dry_run=dry_run,
        )

        # Step 6.3: Hibernate Envers Baseline Injection
        envers_rev = None
        if not dry_run and (target_sk_ids or target_profil_ids):
            logger.info("Injecting Envers baseline audit snapshots for Stage 6...")
            envers_rev = create_revision(target_conn)

            # Audit lampiran_sk_aud
            if target_sk_ids:
                snapshot_table_to_audit(
                    conn=target_conn,
                    source_table="lampiran_sk",
                    aud_table="lampiran_sk_aud",
                    rev=envers_rev,
                    ids=target_sk_ids,
                    revtype=REVTYPE_ADD,
                )

            # Audit lampiran_profil_aud
            if target_profil_ids:
                snapshot_table_to_audit(
                    conn=target_conn,
                    source_table="lampiran_profil",
                    aud_table="lampiran_profil_aud",
                    rev=envers_rev,
                    ids=target_profil_ids,
                    revtype=REVTYPE_ADD,
                )

        manifest_stats = manifest_mgr.get_stats() if not dry_run else {}

        summary = {
            "stage": 6,
            "domain": DOMAIN_NAME,
            "dry_run": dry_run,
            "lampiran_sk_migrated": sk_count,
            "lampiran_profil_migrated": profil_count,
            "riwayat_sp_updated": sp_count,
            "profile_photos_migrated": photos_count,
            "manifest_total_entries": manifest_stats.get("total_files", 0),
            "envers_revision": envers_rev,
            "status": "COMPLETED",
        }
        logger.info("Stage 6 completed successfully: %s", summary)
        return summary

    if legacy_conn is not None:
        return _execute(legacy_conn)
    with get_legacy_connection() as managed_legacy_conn:
        return _execute(managed_legacy_conn)

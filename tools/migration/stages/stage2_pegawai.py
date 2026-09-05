"""Stage 2: Pegawai & Biodata (Core Identity & Profile).

Memigrasikan data kepegawaian dan profil personal:
1. `smartoffice.employee`    -> `kepegawaian_dev_new.pegawai`
2. `smartoffice.emp_profile` -> `kepegawaian_dev_new.biodata` dengan **NIK Fallback ke NIPAM** (ADR-0048)
   jika NIK KTP kosong/null/strip, dan mengekspor daftar unresolved ke `audit_unresolved_nik.csv`.
3. Relasi data anak profil:
   - `emp_family`            -> `profil_keluarga`
   - `emp_education`         -> `pendidikan`
   - `emp_training`          -> `pelatihan`
   - `emp_skill`             -> `keahlian`
   - `emp_card`              -> `kartu_identitas`
   - `emp_work_experience`   -> `pengalaman_kerja`
4. Injeksi Baseline Revision Hibernate Envers (ADR-0051) untuk:
   `pegawai_aud`, `biodata_aud`, `profil_keluarga_aud`, `pendidikan_aud`,
   `pelatihan_aud`, `keahlian_aud`, `kartu_identitas_aud`, `pengalaman_kerja_aud`.
5. Pencatatan pemetaan ID ke `migration_id_map`:
   `(emp_id -> pegawai_id)`, `(emp_profile_id -> nik)`, dan anak profil.
"""

from __future__ import annotations

import csv
import logging
from pathlib import Path
from typing import Any, Optional

try:
    from rich.console import Console
    from rich.table import Table
except ImportError:
    Console = None  # type: ignore[misc,assignment]
    Table = None  # type: ignore[misc,assignment]

from tools.migration.config import config
from tools.migration.core.db import (
    batch_insert,
    batch_upsert,
    execute_query,
    get_target_connection,
)
from tools.migration.core.envers import (
    create_revision,
    snapshot_table_to_audit,
)
from tools.migration.core.state import (
    batch_set_mappings,
    compute_record_hash,
    get_all_mappings,
)
from tools.migration.stages.common import StageResult

logger = logging.getLogger(__name__)


def _clean_nik(val: Any) -> str:
    """Sanitizes raw NIK string by stripping spaces and common placeholders."""
    if val is None:
        return ""
    cleaned = str(val).strip()
    if cleaned in ("", "-", "--", "0", "None", "NULL"):
        return ""
    # Remove interior dashes or spaces
    cleaned = cleaned.replace("-", "").replace(" ", "")
    return cleaned


def _extract_year(val: Any) -> Optional[int]:
    """Extracts integer year from a date, datetime, string, or int."""
    if val is None:
        return None
    if hasattr(val, "year"):
        return val.year
    try:
        val_str = str(val).strip()
        if len(val_str) >= 4:
            y = int(val_str[:4])
            if 1900 <= y <= 2100:
                return y
        y = int(val_str)
        if 1900 <= y <= 2100:
            return y
    except (ValueError, TypeError):
        pass
    return None


def _clean_float(val: Any) -> Optional[float]:
    """Safely converts string/number to float, handling dashes and commas."""
    if val is None:
        return None
    cleaned = str(val).strip().replace(",", ".")
    if cleaned in ("", "-", "--", "None", "NULL"):
        return None
    try:
        return float(cleaned)
    except (ValueError, TypeError):
        return None


def _export_unresolved_nik_audit(audit_rows: list[dict[str, Any]]) -> None:
    """Exports unresolved NIK audit list to CSV files for HR reconciliation."""
    if not audit_rows:
        return

    output_paths = [
        Path("audit_unresolved_nik.csv"),
        Path("tools/migration/audit/audit_unresolved_nik.csv"),
    ]

    for p in output_paths:
        try:
            p.parent.mkdir(parents=True, exist_ok=True)
            with open(p, "w", newline="", encoding="utf-8") as f:
                writer = csv.DictWriter(
                    f,
                    fieldnames=["nipam", "nama", "legacy_profile_id", "fallback_nik", "reason"],
                )
                writer.writeheader()
                writer.writerows(audit_rows)
            logger.info("Exported %d unresolved NIK records to %s", len(audit_rows), p)
        except Exception as exc:
            logger.warning("Could not write audit CSV to %s: %s", p, exc)


def sync_biodata_and_pegawai(
    conn: Any,
) -> tuple[int, int, int, int, list[str], list[int], list[dict[str, Any]]]:
    """Migrates smartoffice.employee and emp_profile into biodata and pegawai.

    Returns:
        tuple of (
            ext_peg, up_peg, ext_bio, up_bio,
            touched_niks, touched_pegawai_ids, audit_rows
        ).
    """
    legacy_schema = config.legacy_db.schema

    # Load emp_card KTPs (ei_type=4) as secondary lookup (active status preferred)
    ktp_map: dict[str, str] = {}
    card_query = f"""
    SELECT emp_code, ei_number
    FROM `{legacy_schema}`.`emp_card`
    WHERE ei_type = 4 AND ei_number IS NOT NULL AND TRIM(ei_number) != ''
    ORDER BY CASE WHEN ei_status = 1 THEN 1 ELSE 2 END ASC, ei_id DESC
    """
    try:
        card_rows = execute_query(conn, card_query)
        for cr in card_rows:
            cleaned = _clean_nik(cr["ei_number"])
            if len(cleaned) >= 8 and cr["emp_code"] not in ktp_map:
                ktp_map[cr["emp_code"]] = cleaned
    except Exception as e:
        logger.warning("Could not query emp_card for KTP fallback: %s", e)

    # Query employee joined with emp_profile and position
    # Sorting ensures active records (emp_work_status=6) take precedence over exited/contract records
    emp_query = f"""
    SELECT
      e.emp_id,
      e.emp_code,
      e.emp_profile_id,
      e.emp_pos_id,
      e.emp_gol_id,
      e.emp_flag,
      e.emp_work_status,
      e.emp_status,
      e.emp_start,
      e.tmt_sk_gol,
      e.tmt_sk_jabatan,
      e.tgl_pengangkatan,
      e.tmt_pensiun,
      e.jml_tanggungan,
      e.emp_gp,
      e.emp_phdp,
      p.askes_flag,
      e.absensi_id,
      e.mkg_tahun,
      e.mkg_bulan,
      p.emp_note,
      p.emp_type,
      p.emp_name,
      p.emp_gender,
      p.emp_birth_place,
      p.emp_birth_date,
      p.emp_religion,
      p.id_marital_status,
      p.emp_address,
      p.emp_phone,
      p.emp_mobile,
      p.emp_mother_name,
      p.emp_photo,
      p.emp_identity_number,
      p.emp_email,
      pos.pos_org_id
    FROM `{legacy_schema}`.`employee` e
    LEFT JOIN `{legacy_schema}`.`emp_profile` p ON e.emp_profile_id = p.emp_profile_id
    LEFT JOIN `{legacy_schema}`.`position` pos ON e.emp_pos_id = pos.pos_id
    ORDER BY CASE WHEN e.emp_work_status = 6 THEN 2 ELSE 1 END ASC, e.emp_id ASC
    """
    rows = execute_query(conn, emp_query)
    if not rows:
        return 0, 0, 0, 0, [], [], []

    # Introspect valid master foreign keys
    valid_orgs = {row["id"] for row in execute_query(conn, "SELECT id FROM organisasi")}
    valid_jabs = {row["id"] for row in execute_query(conn, "SELECT id FROM jabatan")}
    valid_gols = {row["id"] for row in execute_query(conn, "SELECT id FROM golongan")}

    biodata_records: list[dict[str, Any]] = []
    pegawai_records: list[dict[str, Any]] = []
    mappings: list[dict[str, Any]] = []
    unresolved_nik_audit: list[dict[str, Any]] = []
    touched_niks: list[str] = []

    for r in rows:
        emp_id = r["emp_id"]
        emp_code = r["emp_code"]
        profile_id = r.get("emp_profile_id")
        emp_name = r.get("emp_name") or f"Pegawai {emp_code}"

        # 1. Resolve NIK with ADR-0048 Fallback to NIPAM
        raw_nik = _clean_nik(r.get("emp_identity_number"))
        if not raw_nik and emp_code in ktp_map:
            raw_nik = ktp_map[emp_code]

        if not raw_nik or len(raw_nik) < 8:
            # Fallback to NIPAM
            nik = str(emp_code).strip()
            unresolved_nik_audit.append({
                "nipam": emp_code,
                "nama": emp_name,
                "legacy_profile_id": profile_id,
                "fallback_nik": nik,
                "reason": "Missing or invalid KTP NIK in emp_profile / emp_card",
            })
        else:
            nik = raw_nik

        touched_niks.append(nik)

        # 2. Map Biodata fields
        # Gender: 'Wanita' or '2' -> 1 (PEREMPUAN), else 0 (LAKI_LAKI)
        gender_str = str(r.get("emp_gender") or "").strip().lower()
        gender_id = 1 if gender_str in ("wanita", "perempuan", "2") else 0

        # Agama: 1..8 in EAgama (1=ISLAM, 2=KRISTEN, etc.), 0=TIDAK_TAHU
        rel_raw = r.get("emp_religion")
        try:
            agama_id = int(rel_raw) if rel_raw is not None and 1 <= int(rel_raw) <= 8 else 0
        except (ValueError, TypeError):
            agama_id = 0

        # Mapping berdasarkan smartoffice.sys_reference (code='status_kawin')
        # ke ordinal EStatusKawin Java (@Enumerated(EnumType.ORDINAL))
        _STATUS_KAWIN_MAP = {
            1:  0,   # Belum Menikah    -> EStatusKawin.BELUM_KAWIN (ordinal 0)
            2:  1,   # Sudah Menikah    -> EStatusKawin.KAWIN (ordinal 1)
            3:  2,   # Janda/Duda       -> EStatusKawin.JANDA_DUDA (ordinal 2)
            4:  3,   # Menikah Sekantor -> EStatusKawin.MENIKAH_SEKANTOR (ordinal 3)
            99: 4,   # Tidak Tahu       -> EStatusKawin.TIDAK_TAHU (ordinal 4)
        }
        mar_raw = r.get("id_marital_status")
        try:
            mar_int = int(mar_raw) if mar_raw is not None else 99
            status_kawin = _STATUS_KAWIN_MAP.get(mar_int, 4)  # default: TIDAK_TAHU
        except (ValueError, TypeError):
            status_kawin = 4

        telp = r.get("emp_mobile") or r.get("emp_phone") or None
        is_deleted_val = 1 if r.get("emp_status") == 3 else 0

        # Tentukan apakah ini adalah pelamar rekrutmen (bukan karyawan)
        is_recruiter = r.get("emp_type") == 1 or str(emp_code).startswith("REC/")

        bio_row = {
            "nik": nik,
            "nama": emp_name,
            "tempat_lahir": r.get("emp_birth_place"),
            "tanggal_lahir": r.get("emp_birth_date"),
            "jenis_kelamin": gender_id,
            "agama": agama_id,
            "status_kawin": status_kawin,
            "alamat": r.get("emp_address"),
            "telp": telp,
            "ibu_kandung": r.get("emp_mother_name"),
            "foto_profil": r.get("emp_photo"),
            "is_pegawai": 0 if is_recruiter else 1,  # Pelamar bukan pegawai
            "is_deleted": is_deleted_val,
            "version": 0,
            "changed_status": 0,
        }
        biodata_records.append(bio_row)

        if profile_id:
            mappings.append({
                "domain": "pegawai",
                "legacy_table": "emp_profile",
                "legacy_id": profile_id,
                "new_table": "biodata",
                "new_id": nik,
                "record_hash": compute_record_hash(bio_row),
            })

        # 3. Map Pegawai fields
        # Mapping lengkap berdasarkan smartoffice.sys_reference (code='emp_work_status')
        # ke ordinal EStatusKerja Java (@Enumerated(EnumType.ORDINAL))
        _STATUS_KERJA_MAP = {
            1: 3,  # Lamaran Baru        -> EStatusKerja.LAMARAN_BARU (ordinal 3)
            2: 4,  # Tahap Seleksi       -> EStatusKerja.TAHAP_SELEKSI (ordinal 4)
            3: 5,  # Diterima            -> EStatusKerja.DITERIMA (ordinal 5)
            4: 6,  # Direkomendasikan    -> EStatusKerja.DIREKOMENDASIKAN (ordinal 6)
            5: 7,  # Ditolak             -> EStatusKerja.DITOLAK (ordinal 7)
            6: 2,  # Karyawan Aktif      -> EStatusKerja.KARYAWAN_AKTIF (ordinal 2)
            7: 1,  # Dirumahkan          -> EStatusKerja.DIRUMAHKAN (ordinal 1)
            8: 0,  # Berhenti / Keluar   -> EStatusKerja.BERHENTI_OR_KELUAR (ordinal 0)
        }
        work_status_raw = r.get("emp_work_status")
        status_kerja = _STATUS_KERJA_MAP.get(work_status_raw, 0)  # default: BERHENTI_OR_KELUAR

        # Koreksi data legacy kadaluarsa: pegawai dengan tmt_pensiun sudah lewat tapi
        # emp_work_status tidak diperbarui ke 8 oleh admin legacy
        tmt_pensiun_val = r.get("tmt_pensiun")
        if tmt_pensiun_val is not None and status_kerja == 2:  # saat ini dianggap Aktif
            import datetime as _dt
            if hasattr(tmt_pensiun_val, "date"):
                tmt_date = tmt_pensiun_val.date()
            elif isinstance(tmt_pensiun_val, _dt.date):
                tmt_date = tmt_pensiun_val
            else:
                try:
                    tmt_date = _dt.date.fromisoformat(str(tmt_pensiun_val)[:10])
                except ValueError:
                    tmt_date = None
            if tmt_date and tmt_date <= _dt.date.today():
                status_kerja = 0  # BERHENTI_OR_KELUAR — pensiun terlewat di data legacy

        # Mapping lengkap berdasarkan smartoffice.sys_reference (code='emp_flag')
        # ke ordinal EStatusPegawai Java (@Enumerated(EnumType.ORDINAL))
        _STATUS_PEGAWAI_MAP = {
            1: 2,  # Pegawai Tetap       -> EStatusPegawai.PEGAWAI (ordinal 2)
            2: 0,  # Pegawai Kontrak     -> EStatusPegawai.KONTRAK (ordinal 0)
            3: 5,  # Non Pegawai         -> EStatusPegawai.NON_PEGAWAI (ordinal 5)
            4: 1,  # Calon Pegawai       -> EStatusPegawai.CAPEG (ordinal 1)
            5: 4,  # Honorer Tetap       -> EStatusPegawai.HONORER (ordinal 4)
            6: 3,  # Calon Honorer Tetap -> EStatusPegawai.CALON_HONORER (ordinal 3)
        }
        flag_raw = r.get("emp_flag")
        status_pegawai = _STATUS_PEGAWAI_MAP.get(flag_raw, 5)  # default: NON_PEGAWAI (5)

        pos_id = r.get("emp_pos_id")
        jabatan_id = pos_id if pos_id in valid_jabs else None

        pos_org_id = r.get("pos_org_id")
        organisasi_id = pos_org_id if pos_org_id in valid_orgs else None

        gol_id = r.get("emp_gol_id")
        golongan_id = gol_id if gol_id in valid_gols else None

        askes_bit = 1 if str(r.get("askes_flag") or "").strip() in ("1", "Y", "y", "true", "True") else 0
        abs_raw = r.get("absensi_id")
        absensi_id = int(abs_raw) if abs_raw is not None and str(abs_raw).strip().isdigit() else None

        peg_row = {
            "nipam": emp_code,
            "biodata_id": nik,
            "organisasi_id": organisasi_id,
            "jabatan_id": jabatan_id,
            "golongan_id": golongan_id,
            "status_kerja": status_kerja,
            "status_pegawai": status_pegawai,
            "tmt_kerja": r.get("emp_start"),
            "tmt_golongan": r.get("tmt_sk_gol"),
            "tmt_jabatan": r.get("tmt_sk_jabatan"),
            "tanggal_pengangkatan": r.get("tgl_pengangkatan"),
            "tmt_pensiun": r.get("tmt_pensiun"),
            "gaji_pokok": float(r.get("emp_gp") or 0.0),
            "phdp": float(r.get("emp_phdp") or 0.0),
            "jml_tanggungan": int(r.get("jml_tanggungan") or 0),
            "mkg_tahun": int(r.get("mkg_tahun") or 0),
            "mkg_bulan": int(r.get("mkg_bulan") or 0),
            "absensi_id": absensi_id,
            "email": r.get("emp_email"),
            "is_askes": askes_bit,
            "is_deleted": is_deleted_val,
            "notes": r.get("emp_note"),
            "version": 0,
            "changed_status": 0,
        }
        # Pelamar rekrutmen TIDAK dimasukkan ke tabel pegawai — hanya biodata
        if not is_recruiter:
            pegawai_records.append(peg_row)

    # Deduplicate biodata records by NIK
    unique_biodata: dict[str, dict[str, Any]] = {}
    for b in biodata_records:
        unique_biodata[b["nik"]] = b
    deduped_biodata = list(unique_biodata.values())

    # Safe Upsert Biodata
    bio_update_cols = [
        "nama",
        "tempat_lahir",
        "tanggal_lahir",
        "jenis_kelamin",
        "agama",
        "status_kawin",
        "alamat",
        "telp",
        "ibu_kandung",
        "foto_profil",
        "is_pegawai",
        "is_deleted",
    ]
    up_bio = batch_upsert(
        conn=conn,
        table_name="biodata",
        records=deduped_biodata,
        update_columns=bio_update_cols,
        chunk_size=500,
    )

    # Safe Upsert Pegawai
    peg_update_cols = [
        "biodata_id",
        "organisasi_id",
        "jabatan_id",
        "golongan_id",
        "status_kerja",
        "status_pegawai",
        "tmt_kerja",
        "tmt_golongan",
        "tmt_jabatan",
        "tanggal_pengangkatan",
        "tmt_pensiun",
        "gaji_pokok",
        "phdp",
        "jml_tanggungan",
        "mkg_tahun",
        "mkg_bulan",
        "absensi_id",
        "email",
        "is_askes",
        "is_deleted",
        "notes",
    ]
    up_peg = batch_upsert(
        conn=conn,
        table_name="pegawai",
        records=pegawai_records,
        update_columns=peg_update_cols,
        chunk_size=500,
    )

    # Retrieve generated/existing Pegawai IDs to update migration_id_map
    peg_id_rows = execute_query(conn, "SELECT id, nipam FROM pegawai")
    nipam_to_id = {row["nipam"]: row["id"] for row in peg_id_rows}
    touched_pegawai_ids = []

    for r in rows:
        emp_id = r["emp_id"]
        emp_code = r["emp_code"]
        peg_id = nipam_to_id.get(emp_code)
        if peg_id:
            touched_pegawai_ids.append(peg_id)
            mappings.append({
                "domain": "pegawai",
                "legacy_table": "employee",
                "legacy_id": emp_id,
                "new_table": "pegawai",
                "new_id": peg_id,
                "record_hash": compute_record_hash({"nipam": emp_code, "pegawai_id": peg_id}),
            })
            mappings.append({
                "domain": "pegawai",
                "legacy_table": "employee_code",
                "legacy_id": emp_code,
                "new_table": "pegawai",
                "new_id": peg_id,
            })

    batch_set_mappings(conn=conn, mappings=mappings, chunk_size=500)
    _export_unresolved_nik_audit(unresolved_nik_audit)

    return (
        len(rows),
        len(pegawai_records),
        len(deduped_biodata),
        len(deduped_biodata),
        list(unique_biodata.keys()),
        touched_pegawai_ids,
        unresolved_nik_audit,
    )


def sync_child_profiles(
    conn: Any,
    profile_map: dict[str, str],
    emp_code_to_nik: dict[str, str],
) -> tuple[dict[str, tuple[int, int, list[int]]], list[dict[str, Any]]]:
    """Migrates personal child entities referencing biodata.nik and captures ID mappings.

    Entities:
      - emp_family          -> profil_keluarga
      - emp_education       -> pendidikan
      - emp_training        -> pelatihan
      - emp_skill           -> keahlian
      - emp_card            -> kartu_identitas
      - emp_work_experience -> pengalaman_kerja

    Returns:
        tuple of (results_dict, child_mappings)
    """
    legacy_schema = config.legacy_db.schema
    results: dict[str, tuple[int, int, list[int]]] = {}
    child_mappings: list[dict[str, Any]] = []

    # 1. Family: emp_family -> profil_keluarga
    try:
        fam_query = f"""
        SELECT
          fam_id, emp_profile_id, fam_name, fam_gender, fam_relation,
          fam_birth_place, fam_birth_date, fam_tanggungan, fam_description,
          fam_status, fam_sts_nikah, fam_pendidikan
        FROM `{legacy_schema}`.`emp_family`
        ORDER BY fam_id ASC
        """
        fam_rows = execute_query(conn, fam_query)
        valid_jenjang = {row["id"] for row in execute_query(conn, "SELECT id FROM jenjang_pendidikan")}
        FAM_RELATION_MAP = {1: 0, 2: 1, 3: 2, 4: 3, 5: 4, 7: 5}

        fam_records = []
        fam_key_to_legacy: dict[tuple[str, str, str], list[int]] = {}

        for r in fam_rows:
            prof_id = str(r["emp_profile_id"])
            bio_nik = profile_map.get(prof_id)
            if not bio_nik:
                continue

            gender = 1 if str(r.get("fam_gender") or "").strip().lower() in ("wanita", "perempuan", "2") else 0
            is_del = 1 if r.get("fam_status") != 1 else 0
            tanggungan = 1 if r.get("fam_tanggungan") == 1 else 0
            rel_raw = r.get("fam_relation")
            hubungan_keluarga = FAM_RELATION_MAP.get(rel_raw, 4) if rel_raw is not None else None
            status_kawin = 1 if r.get("fam_sts_nikah") == 1 else 0
            fam_pend = r.get("fam_pendidikan")
            pend_id = fam_pend if fam_pend in valid_jenjang else None

            fam_name = str(r.get("fam_name") or "").strip()
            bdate_str = str(r.get("fam_birth_date") or "")
            key = (bio_nik, fam_name, bdate_str)
            fam_key_to_legacy.setdefault(key, []).append(r["fam_id"])

            fam_records.append({
                "biodata_id": bio_nik,
                "nama": fam_name,
                "jenis_kelamin": gender,
                "hubungan_keluarga": hubungan_keluarga,
                "tempat_lahir": r.get("fam_birth_place"),
                "tanggal_lahir": r.get("fam_birth_date"),
                "tanggungan": tanggungan,
                "status_kawin": status_kawin,
                "pendidikan_id": pend_id,
                "notes": r.get("fam_description"),
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        # Deduplicate in-memory by unique constraint key
        dedup_fam: dict[tuple[str, int, str, str, int], dict[str, Any]] = {}
        for rec in fam_records:
            k = (rec["biodata_id"], 0, rec["nama"], str(rec["tanggal_lahir"]), rec["is_deleted"])
            dedup_fam[k] = rec

        batch_upsert(
            conn=conn,
            table_name="profil_keluarga",
            records=list(dedup_fam.values()),
            update_columns=["jenis_kelamin", "hubungan_keluarga", "tempat_lahir", "tanggungan", "status_kawin", "pendidikan_id", "notes", "is_deleted"],
            chunk_size=500,
        )
        fam_target_rows = execute_query(conn, "SELECT id, biodata_id, nama, tanggal_lahir FROM profil_keluarga")
        fam_ids = [row["id"] for row in fam_target_rows]
        results["profil_keluarga"] = (len(fam_rows), len(dedup_fam), fam_ids)

        for tr in fam_target_rows:
            k = (tr["biodata_id"], str(tr["nama"] or "").strip(), str(tr["tanggal_lahir"] or ""))
            if k in fam_key_to_legacy:
                for leg_id in fam_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_family",
                        "legacy_id": leg_id,
                        "new_table": "profil_keluarga",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_family: %s", exc, exc_info=True)
        results["profil_keluarga"] = (0, 0, [])

    # 2. Education: emp_education -> pendidikan
    try:
        edu_query = f"""
        SELECT
          edu_id, emp_profile_id, edu_level, edu_institution, edu_major,
          edu_sdate, edu_edate, edu_lulus, edu_gpa, edu_last_edu_flag, edu_status
        FROM `{legacy_schema}`.`emp_education`
        ORDER BY edu_id ASC
        """
        edu_rows = execute_query(conn, edu_query)
        edu_records = []
        valid_jenjang = {row["id"] for row in execute_query(conn, "SELECT id FROM jenjang_pendidikan")}
        edu_key_to_legacy: dict[tuple[str, Any, Any], list[int]] = {}

        for r in edu_rows:
            prof_id = str(r["emp_profile_id"])
            bio_nik = profile_map.get(prof_id)
            if not bio_nik:
                continue

            lvl = r.get("edu_level")
            jenjang_id = lvl if lvl in valid_jenjang else None
            is_del = 1 if r.get("edu_status") != 1 else 0
            is_lulus = 1 if r.get("edu_lulus") == 1 else 0
            is_latest = 1 if r.get("edu_last_edu_flag") == 1 else 0

            thn_masuk = _extract_year(r.get("edu_sdate"))
            thn_lulus = _extract_year(r.get("edu_edate"))
            gpa = _clean_float(r.get("edu_gpa"))

            key = (bio_nik, jenjang_id, thn_masuk)
            edu_key_to_legacy.setdefault(key, []).append(r["edu_id"])

            edu_records.append({
                "biodata_id": bio_nik,
                "jenjang_id": jenjang_id,
                "institusi": r.get("edu_institution"),
                "jurusan": r.get("edu_major"),
                "kota": None,
                "tahun_masuk": thn_masuk,
                "tahun_lulus": thn_lulus,
                "is_lulus": is_lulus,
                "gpa": gpa,
                "is_latest": is_latest,
                "disetujui": 1,
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        # Deduplicate in-memory
        dedup_edu: dict[tuple[str, Any, Any], dict[str, Any]] = {}
        for rec in edu_records:
            k = (rec["biodata_id"], rec["jenjang_id"], rec["tahun_masuk"])
            dedup_edu[k] = rec

        batch_upsert(
            conn=conn,
            table_name="pendidikan",
            records=list(dedup_edu.values()),
            update_columns=["institusi", "jurusan", "kota", "tahun_lulus", "is_lulus", "gpa", "is_latest", "is_deleted"],
            chunk_size=500,
        )
        edu_target_rows = execute_query(conn, "SELECT id, biodata_id, jenjang_id, tahun_masuk FROM pendidikan")
        edu_ids = [row["id"] for row in edu_target_rows]
        results["pendidikan"] = (len(edu_rows), len(dedup_edu), edu_ids)

        for tr in edu_target_rows:
            k = (tr["biodata_id"], tr["jenjang_id"], tr["tahun_masuk"])
            if k in edu_key_to_legacy:
                for leg_id in edu_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_education",
                        "legacy_id": leg_id,
                        "new_table": "pendidikan",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_education: %s", exc, exc_info=True)
        results["pendidikan"] = (0, 0, [])

    # 3. Training: emp_training -> pelatihan
    try:
        train_query = f"""
        SELECT
          id, emp_profile_id, training_id, lembaga, nama_pelatihan,
          tgl_mulai, tgl_selesai, lulus, nilai, ikatan_dinas, tgl_akhir_ikatan, keterangan, status
        FROM `{legacy_schema}`.`emp_training`
        ORDER BY id ASC
        """
        train_rows = execute_query(conn, train_query)
        train_records = []
        valid_train_types = {row["id"] for row in execute_query(conn, "SELECT id FROM jenis_pelatihan")}
        train_key_to_legacy: dict[tuple[str, str, str], list[int]] = {}

        for r in train_rows:
            prof_id = str(r["emp_profile_id"])
            bio_nik = profile_map.get(prof_id)
            if not bio_nik:
                continue

            t_id = r.get("training_id")
            jenis_id = t_id if t_id in valid_train_types else None
            is_del = 1 if r.get("status") != 1 else 0
            nama = str(r.get("nama_pelatihan") or "").strip()
            tmulai_str = str(r.get("tgl_mulai") or "")

            key = (bio_nik, nama, tmulai_str)
            train_key_to_legacy.setdefault(key, []).append(r["id"])

            train_records.append({
                "biodata_id": bio_nik,
                "jenis_pelatihan_id": jenis_id,
                "lembaga": r.get("lembaga"),
                "nama": nama,
                "tanggal_mulai": r.get("tgl_mulai"),
                "tanggal_selesai": r.get("tgl_selesai"),
                "lulus": 1 if r.get("lulus") == 1 else 0,
                "nilai": str(r.get("nilai") or ""),
                "ikatan_dinas": 1 if r.get("ikatan_dinas") == 1 else 0,
                "tanggal_akhir_ikatan": r.get("tgl_akhir_ikatan"),
                "notes": r.get("keterangan"),
                "disetujui": 1,
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        batch_upsert(
            conn=conn,
            table_name="pelatihan",
            records=train_records,
            update_columns=["lembaga", "nama", "tanggal_mulai", "tanggal_selesai", "lulus", "nilai", "is_deleted"],
            chunk_size=500,
        )
        train_target_rows = execute_query(conn, "SELECT id, biodata_id, nama, tanggal_mulai FROM pelatihan")
        train_ids = [row["id"] for row in train_target_rows]
        results["pelatihan"] = (len(train_rows), len(train_records), train_ids)

        for tr in train_target_rows:
            k = (tr["biodata_id"], str(tr["nama"] or "").strip(), str(tr["tanggal_mulai"] or ""))
            if k in train_key_to_legacy:
                for leg_id in train_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_training",
                        "legacy_id": leg_id,
                        "new_table": "pelatihan",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_training: %s", exc, exc_info=True)
        results["pelatihan"] = (0, 0, [])

    # 4. Skill: emp_skill -> keahlian
    try:
        skill_query = f"""
        SELECT
          id, emp_profile_id, jenis_id, kualifikasi_id, sertifikat, institusi, tahun, status
        FROM `{legacy_schema}`.`emp_skill`
        ORDER BY id ASC
        """
        skill_rows = execute_query(conn, skill_query)
        skill_records = []
        valid_skills = {row["id"] for row in execute_query(conn, "SELECT id FROM jenis_keahlian")}
        skill_key_to_legacy: dict[tuple[str, Any, Any], list[int]] = {}

        for r in skill_rows:
            prof_id = str(r["emp_profile_id"])
            bio_nik = profile_map.get(prof_id)
            if not bio_nik:
                continue

            j_id = r.get("jenis_id")
            jenis_keahlian_id = j_id if j_id in valid_skills else None
            is_del = 1 if r.get("status") != 1 else 0
            thn = _extract_year(r.get("tahun"))

            key = (bio_nik, jenis_keahlian_id, thn)
            skill_key_to_legacy.setdefault(key, []).append(r["id"])

            skill_records.append({
                "biodata_id": bio_nik,
                "jenis_keahlian_id": jenis_keahlian_id,
                "kualifikasi": r.get("kualifikasi_id"),
                "sertifikasi": 1 if r.get("sertifikat") == 1 else 0,
                "institusi": r.get("institusi"),
                "tahun": thn,
                "disetujui": 1,
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        batch_upsert(
            conn=conn,
            table_name="keahlian",
            records=skill_records,
            update_columns=["kualifikasi", "sertifikasi", "institusi", "tahun", "is_deleted"],
            chunk_size=500,
        )
        skill_target_rows = execute_query(conn, "SELECT id, biodata_id, jenis_keahlian_id, tahun FROM keahlian")
        skill_ids = [row["id"] for row in skill_target_rows]
        results["keahlian"] = (len(skill_rows), len(skill_records), skill_ids)

        for tr in skill_target_rows:
            k = (tr["biodata_id"], tr["jenis_keahlian_id"], tr["tahun"])
            if k in skill_key_to_legacy:
                for leg_id in skill_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_skill",
                        "legacy_id": leg_id,
                        "new_table": "keahlian",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_skill: %s", exc, exc_info=True)
        results["keahlian"] = (0, 0, [])

    # 5. ID Card: emp_card -> kartu_identitas
    try:
        card_query = f"""
        SELECT
          ei_id, emp_code, ei_type, ei_number, ei_description, ei_status,
          ei_received_date, ei_exp_date
        FROM `{legacy_schema}`.`emp_card`
        ORDER BY ei_id ASC
        """
        card_rows = execute_query(conn, card_query)
        card_records = []
        valid_kitas = {row["id"] for row in execute_query(conn, "SELECT id FROM jenis_kitas")}
        card_key_to_legacy: dict[tuple[str, int], list[int]] = {}

        for r in card_rows:
            code = r["emp_code"]
            bio_nik = emp_code_to_nik.get(code)
            if not bio_nik:
                continue

            raw_type = r.get("ei_type")
            target_type_id = raw_type if raw_type in valid_kitas else 4
            is_del = 1 if r.get("ei_status") != 1 else 0

            key = (bio_nik, target_type_id)
            card_key_to_legacy.setdefault(key, []).append(r["ei_id"])

            card_records.append({
                "nik": bio_nik,
                "jenis_kitas_id": target_type_id,
                "nomor_kartu": r.get("ei_number") or "",
                "notes": r.get("ei_description"),
                "tanggal_terima": r.get("ei_received_date"),
                "tanggal_expired": r.get("ei_exp_date"),
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        # Deduplicate in-memory by unique key (nik, jenis_kitas_id)
        dedup_cards: dict[tuple[str, int], dict[str, Any]] = {}
        for rec in card_records:
            dedup_cards[(rec["nik"], rec["jenis_kitas_id"])] = rec

        batch_upsert(
            conn=conn,
            table_name="kartu_identitas",
            records=list(dedup_cards.values()),
            update_columns=["nomor_kartu", "notes", "tanggal_terima", "tanggal_expired", "is_deleted"],
            chunk_size=500,
        )
        card_target_rows = execute_query(conn, "SELECT id, nik, jenis_kitas_id FROM kartu_identitas")
        card_ids = [row["id"] for row in card_target_rows]
        results["kartu_identitas"] = (len(card_rows), len(dedup_cards), card_ids)

        for tr in card_target_rows:
            k = (tr["nik"], tr["jenis_kitas_id"])
            if k in card_key_to_legacy:
                for leg_id in card_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_card",
                        "legacy_id": leg_id,
                        "new_table": "kartu_identitas",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_card: %s", exc, exc_info=True)
        results["kartu_identitas"] = (0, 0, [])

    # 6. Work Experience: emp_work_experience -> pengalaman_kerja
    try:
        exp_query = f"""
        SELECT
          ewe_id, emp_profile_id, ewe_company_name, ewe_company_type, ewe_job_title,
          ewe_location, ewe_start, ewe_end, ewe_job_description, ewe_status
        FROM `{legacy_schema}`.`emp_work_experience`
        ORDER BY ewe_id ASC
        """
        exp_rows = execute_query(conn, exp_query)
        exp_records = []
        exp_key_to_legacy: dict[tuple[str, str], list[int]] = {}

        for r in exp_rows:
            prof_id = str(r["emp_profile_id"])
            bio_nik = profile_map.get(prof_id)
            if not bio_nik:
                continue

            thn_masuk = _extract_year(r.get("ewe_start"))
            thn_keluar = _extract_year(r.get("ewe_end"))
            is_del = 1 if r.get("ewe_status") != 1 else 0
            comp_name = str(r.get("ewe_company_name") or "").strip()

            key = (bio_nik, comp_name)
            exp_key_to_legacy.setdefault(key, []).append(r["ewe_id"])

            exp_records.append({
                "biodata_id": bio_nik,
                "nama_perusahaan": comp_name,
                "type_perusahaan": str(r.get("ewe_company_type") or ""),
                "jabatan": r.get("ewe_job_title"),
                "lokasi": r.get("ewe_location"),
                "tahun_masuk": thn_masuk,
                "tahun_keluar": thn_keluar,
                "notes": r.get("ewe_job_description"),
                "disetujui": 1,
                "is_deleted": is_del,
                "changed_status": 0,
                "version": 0,
            })

        batch_upsert(
            conn=conn,
            table_name="pengalaman_kerja",
            records=exp_records,
            update_columns=["nama_perusahaan", "jabatan", "lokasi", "tahun_masuk", "tahun_keluar", "notes", "is_deleted"],
            chunk_size=500,
        )
        exp_target_rows = execute_query(conn, "SELECT id, biodata_id, nama_perusahaan FROM pengalaman_kerja")
        exp_ids = [row["id"] for row in exp_target_rows]
        results["pengalaman_kerja"] = (len(exp_rows), len(exp_records), exp_ids)

        for tr in exp_target_rows:
            k = (tr["biodata_id"], str(tr["nama_perusahaan"] or "").strip())
            if k in exp_key_to_legacy:
                for leg_id in exp_key_to_legacy[k]:
                    child_mappings.append({
                        "domain": "pegawai",
                        "legacy_table": "emp_work_experience",
                        "legacy_id": leg_id,
                        "new_table": "pengalaman_kerja",
                        "new_id": tr["id"],
                    })
    except Exception as exc:
        logger.error("Failed to sync emp_work_experience: %s", exc, exc_info=True)
        results["pengalaman_kerja"] = (0, 0, [])

    return results, child_mappings


def run_stage2(console: Optional[Any] = None) -> StageResult:
    """Executes Stage 2 Pegawai & Biodata migration and Envers baseline injection.

    Returns:
        StageResult indicating overall counts and status.
    """
    result = StageResult(stage_name="Stage 2: Pegawai & Biodata", success=True)
    details: dict[str, Any] = {}

    try:
        with get_target_connection(autocommit=False) as conn:
            # 1. Migrate Biodata & Pegawai
            (
                ext_peg, up_peg, ext_bio, up_bio,
                touched_niks, touched_peg_ids, unresolved_niks
            ) = sync_biodata_and_pegawai(conn)

            result.records_extracted += (ext_peg + ext_bio)
            result.records_upserted += (up_peg + up_bio)
            details["biodata"] = {"extracted": ext_bio, "upserted": up_bio}
            details["pegawai"] = {"extracted": ext_peg, "upserted": up_peg}
            details["unresolved_niks"] = len(unresolved_niks)

            # Build in-memory profile mappings for child entities
            profile_map = get_all_mappings(conn, domain="pegawai", legacy_table="emp_profile")
            emp_code_rows = execute_query(conn, "SELECT nipam, biodata_id FROM pegawai")
            emp_code_to_nik = {row["nipam"]: row["biodata_id"] for row in emp_code_rows}

            # 2. Migrate Child Entities
            child_results, child_mappings = sync_child_profiles(conn, profile_map, emp_code_to_nik)
            for child_name, (ext_c, up_c, _) in child_results.items():
                result.records_extracted += ext_c
                result.records_upserted += up_c
                details[child_name] = {"extracted": ext_c, "upserted": up_c}

            # Record child entity mappings into migration_id_map
            if child_mappings:
                batch_set_mappings(conn=conn, mappings=child_mappings, chunk_size=500)
                logger.info("Recorded %d child profile mappings in migration_id_map", len(child_mappings))

            # Update biodata.pendidikan_id from latest education
            execute_query(
                conn,
                """
                UPDATE biodata b
                JOIN (
                    SELECT biodata_id, jenjang_id
                    FROM pendidikan
                    WHERE is_latest = 1 AND is_deleted = 0 AND jenjang_id IS NOT NULL
                ) p ON b.nik = p.biodata_id
                SET b.pendidikan_id = p.jenjang_id
                """,
            )

            # 3. Hibernate Envers Baseline Revision Injection (ADR-0051)
            rev_id = create_revision(conn)
            details["envers_rev"] = rev_id

            # Snapshot main entities
            snapshot_table_to_audit(
                conn, source_table="biodata", aud_table="biodata_aud", rev=rev_id, id_column="nik", ids=touched_niks
            )
            snapshot_table_to_audit(
                conn, source_table="pegawai", aud_table="pegawai_aud", rev=rev_id, id_column="id", ids=touched_peg_ids
            )

            # Snapshot child audited tables
            audited_child_tables = [
                ("profil_keluarga", "profil_keluarga_aud"),
                ("pendidikan", "pendidikan_aud"),
                ("pelatihan", "pelatihan_aud"),
                ("keahlian", "keahlian_aud"),
                ("kartu_identitas", "kartu_identitas_aud"),
                ("pengalaman_kerja", "pengalaman_kerja_aud"),
            ]
            for src_tbl, aud_tbl in audited_child_tables:
                ids = child_results.get(src_tbl, (0, 0, []))[2]
                if ids:
                    snapshot_table_to_audit(conn, source_table=src_tbl, aud_table=aud_tbl, rev=rev_id, id_column="id", ids=ids)

            logger.info("Stage 2 Envers revision %d baseline injected successfully", rev_id)
            result.details = details

    except Exception as exc:
        logger.error("Stage 2 failed: %s", exc, exc_info=True)
        result.add_error(f"Stage 2 execution error: {exc}")

    if console and Table:
        table = Table(title="Stage 2: Pegawai & Biodata Sync Summary", show_header=True)
        table.add_column("Entity", style="cyan")
        table.add_column("Extracted", justify="right")
        table.add_column("Upserted", justify="right")

        for k, v in details.items():
            if isinstance(v, dict) and "extracted" in v:
                table.add_row(k, str(v["extracted"]), str(v["upserted"]))
        console.print(table)
        console.print(f"[bold]Unresolved NIKs (ADR-0048):[/bold] {details.get('unresolved_niks', 0)}")
        console.print(f"[bold]Envers Baseline Rev:[/bold] {details.get('envers_rev', 'N/A')}")

    return result


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    c = Console() if Console else None
    res = run_stage2(console=c)
    if not res.success:
        import sys
        sys.exit(1)

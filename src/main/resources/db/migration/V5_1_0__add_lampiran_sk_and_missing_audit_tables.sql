-- V5_1_0__add_lampiran_sk_and_missing_audit_tables.sql
--
-- Adds tables that were previously created by Hibernate ddl-auto=update
-- but not defined in Flyway migrations:
--   1. lampiran_sk + lampiran_sk_aud (entity is @Entity + @Audited)
--   2. Master module _AUD tables (created by Envers at runtime)
--   3. Convert VARCHAR columns → ENUM to match JOOQ-generated enums
--      that the application code depends on (BiodataGolonganDarah,
--      GajiBatchMasterProsesJenisGaji, GajiKomponenJenisGaji,
--      GajiKomponenAudJenisGaji)
--   4. v_pegawai view (adapted for biodata_id FK)

-- ======================================================================
-- 1. lampiran_sk (entity is @Entity + @Audited)
-- ======================================================================
CREATE TABLE lampiran_sk (
    id BIGINT NOT NULL AUTO_INCREMENT,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    version INT(11) DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    file_name VARCHAR(255) DEFAULT NULL,
    hashed_file_name VARCHAR(255) DEFAULT NULL,
    mime_type VARCHAR(255) DEFAULT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    ref TINYINT(4) NOT NULL,
    ref_id BIGINT NOT NULL,
    tanggal_disetujui DATETIME(6) DEFAULT NULL,
    tanggal_pengajuan DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_lampiran_sk_is_deleted (is_deleted),
    KEY idx_lampiran_sk_ref (ref),
    KEY idx_lampiran_sk_ref_id (ref_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lampiran_sk_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    file_name VARCHAR(255) DEFAULT NULL,
    hashed_file_name VARCHAR(255) DEFAULT NULL,
    mime_type VARCHAR(255) DEFAULT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    ref TINYINT(4) DEFAULT NULL,
    ref_id BIGINT DEFAULT NULL,
    tanggal_disetujui DATETIME(6) DEFAULT NULL,
    tanggal_pengajuan DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_lampiran_sk_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- 2. Master module Audit tables (Envers)
-- ======================================================================

-- alasan_berhenti_aud
CREATE TABLE alasan_berhenti_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_alasan_berhenti_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cuti_jenis_aud
CREATE TABLE cuti_jenis_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    max_hari INT(11) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    potong_kuota_tahunan BIT(1) DEFAULT NULL,
    parent_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_cuti_jenis_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- golongan_aud
CREATE TABLE golongan_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    golongan VARCHAR(255) DEFAULT NULL,
    pangkat VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_golongan_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- grade_aud
CREATE TABLE grade_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    grade INT(11) DEFAULT NULL,
    tukin DOUBLE DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_grade_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- hari_libur_aud
CREATE TABLE hari_libur_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    jenis_libur TINYINT(4) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    tanggal DATE DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_hari_libur_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- jabatan_aud
CREATE TABLE jabatan_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    parent_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_jabatan_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- jenis_kitas_aud
CREATE TABLE jenis_kitas_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_jenis_kitas_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- jenis_sp_aud
CREATE TABLE jenis_sp_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    kode VARCHAR(10) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_jenis_sp_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- level_aud
CREATE TABLE level_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id, rev),
    KEY idx_level_aud_rev (rev),
    CONSTRAINT fk_level_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- organisasi_aud
CREATE TABLE organisasi_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    category VARCHAR(255) DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    level_org INT(11) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    short_name VARCHAR(255) DEFAULT NULL,
    parent_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_organisasi_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- profesi_aud
CREATE TABLE profesi_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    detail VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    resiko VARCHAR(255) DEFAULT NULL,
    grade_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_profesi_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- rumah_dinas_aud
CREATE TABLE rumah_dinas_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    nilai DOUBLE DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_rumah_dinas_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- sanksi_sp_aud
CREATE TABLE sanksi_sp_aud (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL,
    revtype TINYINT(4) DEFAULT NULL,
    changed_status TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP NULL DEFAULT current_timestamp(),
    created_by VARCHAR(255) DEFAULT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    updated_at TIMESTAMP NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    updated_by VARCHAR(255) DEFAULT NULL,
    is_pending_gaji BIT(1) DEFAULT NULL,
    is_pending_pangkat BIT(1) DEFAULT NULL,
    is_suspension BIT(1) DEFAULT NULL,
    is_terminate_dh BIT(1) DEFAULT NULL,
    is_terminate_th BIT(1) DEFAULT NULL,
    is_turun_jabatan BIT(1) DEFAULT NULL,
    is_turun_pangkat BIT(1) DEFAULT NULL,
    jml_pot_tkk INT(11) DEFAULT NULL,
    keterangan TEXT DEFAULT NULL,
    kode VARCHAR(10) DEFAULT NULL,
    pot_tkk BIT(1) DEFAULT NULL,
    jenis_sp_id BIGINT DEFAULT NULL,
    PRIMARY KEY (rev, id),
    CONSTRAINT fk_sanksi_sp_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- 3. Convert VARCHAR → ENUM for JOOQ enum generation
-- ======================================================================

-- biodata.golongan_darah: VARCHAR(31) → ENUM('A','AB','B','O')
ALTER TABLE biodata
    MODIFY golongan_darah ENUM('A','AB','B','O') DEFAULT NULL;

ALTER TABLE biodata_aud
    MODIFY golongan_darah ENUM('A','AB','B','O') DEFAULT NULL;

-- gaji_batch_master_proses.jenis_gaji: VARCHAR(255) → ENUM('NONE','PEMASUKAN','POTONGAN')
ALTER TABLE gaji_batch_master_proses
    MODIFY jenis_gaji ENUM('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL;

-- gaji_komponen.jenis_gaji: VARCHAR(31) → ENUM('NONE','PEMASUKAN','POTONGAN')
ALTER TABLE gaji_komponen
    MODIFY jenis_gaji ENUM('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL;

ALTER TABLE gaji_komponen_aud
    MODIFY jenis_gaji ENUM('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL;

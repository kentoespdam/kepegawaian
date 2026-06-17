-- V5_0_5__create_penggajian.sql
-- Penggajian domain: 7 entity tables (gaji_batch_root, gaji_batch_master,
-- gaji_batch_master_proses, gaji_batch_potongan_tkk, gaji_batch_root_error_logs,
-- gaji_batch_root_lampiran, gaji_phdp) + 10 _AUD tables (8 V2-ref backfill +
-- gaji_batch_root_AUD partial + gaji_phdp_AUD).
-- gaji_batch_root.status is VARCHAR(32) NOT NULL (folds the V4 fix into this
-- migration). gaji_batch_root uses String PK (id).

-- ----------------------------------------------------------------------
-- gaji_batch_root (String PK; status VARCHAR(32) NOT NULL)
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_root (
    id VARCHAR(64) NOT NULL,
    periode VARCHAR(255) DEFAULT NULL,
    status VARCHAR(32) NOT NULL,
    total_pegawai INT DEFAULT NULL,
    tanggal_proses DATETIME DEFAULT NULL,
    di_proses_oleh VARCHAR(255) DEFAULT NULL,
    jabatan_pemroses VARCHAR(255) DEFAULT NULL,
    tanggal_verifikasi_tahap1 DATETIME DEFAULT NULL,
    di_verifikasi_oleh_tahap1 VARCHAR(255) DEFAULT NULL,
    jabatan_verifikasi_tahap1 VARCHAR(255) DEFAULT NULL,
    tanggal_verifikasi_tahap2 DATETIME DEFAULT NULL,
    di_verifikasi_oleh_tahap2 VARCHAR(255) DEFAULT NULL,
    jabatan_verifikasi_tahap2 VARCHAR(255) DEFAULT NULL,
    tanggal_persetujuan DATETIME DEFAULT NULL,
    di_setujui_oleh VARCHAR(255) DEFAULT NULL,
    jabatan_penyetuju VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_root_is_deleted (is_deleted),
    KEY idx_gaji_batch_root_status (status),
    KEY idx_gaji_batch_root_periode (periode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_batch_master
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_master (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_root_id VARCHAR(64) NOT NULL,
    periode VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    golongan VARCHAR(255) DEFAULT NULL,
    pangkat VARCHAR(255) DEFAULT NULL,
    status_pegawai INT DEFAULT NULL,
    gaji_profil_id BIGINT DEFAULT NULL,
    gaji_pendapatan_non_pajak_id BIGINT DEFAULT NULL,
    kode_pajak VARCHAR(255) DEFAULT NULL,
    gaji_pokok DOUBLE DEFAULT NULL,
    phdp DOUBLE DEFAULT NULL,
    status_kawin INT DEFAULT NULL,
    jml_tanggungan INT DEFAULT NULL,
    jml_jiwa INT DEFAULT NULL,
    penghasilan_kotor DOUBLE DEFAULT NULL,
    total_potongan DOUBLE DEFAULT NULL,
    total_add_tambahan DOUBLE DEFAULT NULL,
    total_add_potongan DOUBLE DEFAULT NULL,
    penghasilan_bersih DOUBLE DEFAULT NULL,
    penghasilan_bersih2 DOUBLE DEFAULT NULL,
    pembulatan DOUBLE DEFAULT NULL,
    pembulatan2 DOUBLE DEFAULT NULL,
    penghasilan_bersih_final DOUBLE DEFAULT NULL,
    penghasilan_bersih_final2 DOUBLE DEFAULT NULL,
    pajak DOUBLE DEFAULT NULL,
    is_different BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_master_root (batch_root_id),
    KEY idx_gaji_batch_master_periode (periode),
    KEY idx_gaji_batch_master_pegawai (pegawai_id),
    KEY idx_gaji_batch_master_nipam (nipam),
    KEY idx_gaji_batch_master_nama (nama),
    CONSTRAINT fk_gaji_batch_master_root FOREIGN KEY (batch_root_id) REFERENCES gaji_batch_root(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_batch_master_proses
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_master_proses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_master_id BIGINT DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    urut INT DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_gaji VARCHAR(255) DEFAULT NULL,
    nilai DOUBLE DEFAULT NULL,
    formula TEXT DEFAULT NULL,
    nilai_formula TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_master_proses_master (batch_master_id),
    CONSTRAINT fk_gaji_batch_master_proses_master FOREIGN KEY (batch_master_id) REFERENCES gaji_batch_master(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_batch_potongan_tkk
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_potongan_tkk (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_id VARCHAR(64) DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    potongan INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_potongan_tkk_batch (batch_id),
    KEY idx_gaji_batch_potongan_tkk_nipam (nipam)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_batch_root_error_logs
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_root_error_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    root_batch_id VARCHAR(64) NOT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_root_error_logs_root (root_batch_id),
    KEY idx_gaji_batch_root_error_logs_nipam (nipam),
    KEY idx_gaji_batch_root_error_logs_nama (nama),
    CONSTRAINT fk_gaji_batch_root_error_logs_root FOREIGN KEY (root_batch_id) REFERENCES gaji_batch_root(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_batch_root_lampiran
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_root_lampiran (
    id BIGINT NOT NULL AUTO_INCREMENT,
    root_batch_id VARCHAR(64) NOT NULL,
    jenis_lampiran_gaji INT DEFAULT NULL,
    mime_type VARCHAR(255) DEFAULT NULL,
    file_name VARCHAR(255) DEFAULT NULL,
    hashed_file_name VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_gaji_batch_root_lampiran_root (root_batch_id),
    CONSTRAINT fk_gaji_batch_root_lampiran_root FOREIGN KEY (root_batch_id) REFERENCES gaji_batch_root(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_phdp
-- ----------------------------------------------------------------------
CREATE TABLE gaji_phdp (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    urut INT DEFAULT NULL,
    kondisi VARCHAR(255) DEFAULT NULL,
    formula TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_gaji_phdp_urut (urut),
    KEY idx_gaji_phdp_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- Envers _AUD siblings
-- ======================================================================

-- ----------------------------------------------------------------------
-- gaji_batch_root_AUD (partial: only @Audited on updatedBy, isDeleted)
-- ----------------------------------------------------------------------
CREATE TABLE gaji_batch_root_AUD (
    id VARCHAR(64) NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BOOLEAN DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_batch_root_aud_rev (REV),
    CONSTRAINT fk_gaji_batch_root_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- gaji_phdp_AUD
-- ----------------------------------------------------------------------
CREATE TABLE gaji_phdp_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    urut INT DEFAULT NULL,
    kondisi VARCHAR(255) DEFAULT NULL,
    formula TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_phdp_aud_rev (REV),
    CONSTRAINT fk_gaji_phdp_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- V2-reference @Audited entities — _AUD backfill for tables that exist
-- in V2 but did not originally have _AUD siblings. Envers requires
-- these to record audit history.
-- ======================================================================

-- dasar_gaji_AUD
CREATE TABLE dasar_gaji_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    deskripsi VARCHAR(255) DEFAULT NULL,
    tanggal_awal DATE DEFAULT NULL,
    tanggal_akhir DATE DEFAULT NULL,
    aktif BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_dasar_gaji_aud_rev (REV),
    CONSTRAINT fk_dasar_gaji_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- detail_dasar_gaji_AUD
CREATE TABLE detail_dasar_gaji_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    dasar_gaji_id BIGINT DEFAULT NULL,
    mkg INT DEFAULT NULL,
    golongan_kode INT DEFAULT NULL,
    nominal DOUBLE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_detail_dasar_gaji_aud_rev (REV),
    CONSTRAINT fk_detail_dasar_gaji_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_profil_AUD
CREATE TABLE gaji_profil_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_profil_aud_rev (REV),
    CONSTRAINT fk_gaji_profil_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_komponen_AUD
CREATE TABLE gaji_komponen_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    urut INT DEFAULT NULL,
    profil_gaji_id BIGINT DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_gaji VARCHAR(31) DEFAULT NULL,
    nilai DOUBLE DEFAULT NULL,
    is_reference BIT(1) DEFAULT NULL,
    formula TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_komponen_aud_rev (REV),
    CONSTRAINT fk_gaji_komponen_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_tunjangan_AUD
CREATE TABLE gaji_tunjangan_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    jenis_tunjangan TINYINT DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nominal DOUBLE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_tunjangan_aud_rev (REV),
    CONSTRAINT fk_gaji_tunjangan_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_potongan_tkk_AUD
CREATE TABLE gaji_potongan_tkk_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    status_pegawai TINYINT DEFAULT NULL,
    level_id BIGINT DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nominal DOUBLE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_potongan_tkk_aud_rev (REV),
    CONSTRAINT fk_gaji_potongan_tkk_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_parameter_setting_AUD
CREATE TABLE gaji_parameter_setting_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    nominal DOUBLE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_parameter_setting_aud_rev (REV),
    CONSTRAINT fk_gaji_parameter_setting_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- gaji_pendapatan_non_pajak_AUD
CREATE TABLE gaji_pendapatan_non_pajak_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    kode VARCHAR(255) DEFAULT NULL,
    nominal DOUBLE DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_gaji_pendapatan_non_pajak_aud_rev (REV),
    CONSTRAINT fk_gaji_pendapatan_non_pajak_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

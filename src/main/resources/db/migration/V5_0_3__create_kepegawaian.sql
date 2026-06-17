-- V5_0_3__create_kepegawaian.sql
-- Kepegawaian domain: 9 entity tables (riwayat_cuti, riwayat_keluar,
-- riwayat_kontrak, riwayat_mutasi, riwayat_sk, riwayat_sp, riwayat_terminasi,
-- statistik_pegawai, lampiran_sk) + 8 _AUD tables (statistik_pegawai
-- is NOT @Audited → 8 _AUD).
-- LampiranSk + LampiranSp are @MappedSuperclass (no own table — fields
-- are inherited by their child entities; columns are added to those
-- child tables below).

-- ----------------------------------------------------------------------
-- riwayat_sk (extends LampiranSk which has @MappedSuperclass fields)
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_sk (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- LampiranSk inherited fields
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    -- RiwayatSk own fields
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    ref_sk_id BIGINT DEFAULT NULL,
    jenis_kontrak INT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    is_latest BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_riwayat_sk_pegawai (pegawai_id),
    KEY idx_riwayat_sk_nomor (nomor_sk),
    KEY idx_riwayat_sk_tanggal_sk (tanggal_sk),
    CONSTRAINT fk_riwayat_sk_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_riwayat_sk_ref_sk FOREIGN KEY (ref_sk_id) REFERENCES riwayat_sk(id),
    CONSTRAINT fk_riwayat_sk_organisasi FOREIGN KEY (organisasi_id) REFERENCES organisasi(id),
    CONSTRAINT fk_riwayat_sk_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_mutasi (extends LampiranSk, references RiwayatSk)
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_mutasi (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- LampiranSk inherited
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    -- RiwayatMutasi own fields
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    jenis_mutasi INT DEFAULT NULL,
    riwayat_sk_id BIGINT DEFAULT NULL,
    tmt_berlaku DATE DEFAULT NULL,
    tanggal_berakhir DATE DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    profesi_id BIGINT DEFAULT NULL,
    nama_profesi VARCHAR(255) DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nama_golongan VARCHAR(255) DEFAULT NULL,
    organisasi_lama_id BIGINT DEFAULT NULL,
    nama_organisasi_lama VARCHAR(255) DEFAULT NULL,
    jabatan_lama_id BIGINT DEFAULT NULL,
    nama_jabatan_lama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_riwayat_mutasi_pegawai (pegawai_id),
    KEY idx_riwayat_mutasi_sk (riwayat_sk_id),
    CONSTRAINT fk_riwayat_mutasi_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_riwayat_mutasi_sk FOREIGN KEY (riwayat_sk_id) REFERENCES riwayat_sk(id),
    CONSTRAINT fk_riwayat_mutasi_organisasi FOREIGN KEY (organisasi_id) REFERENCES organisasi(id),
    CONSTRAINT fk_riwayat_mutasi_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id),
    CONSTRAINT fk_riwayat_mutasi_profesi FOREIGN KEY (profesi_id) REFERENCES profesi(id),
    CONSTRAINT fk_riwayat_mutasi_golongan FOREIGN KEY (golongan_id) REFERENCES golongan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_kontrak (extends LampiranSk)
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_kontrak (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- LampiranSk inherited
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    -- RiwayatKontrak own fields
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nomor_kontrak VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_riwayat_kontrak_pegawai_nomor (pegawai_id, nomor_kontrak),
    KEY idx_riwayat_kontrak_nomor (nomor_kontrak),
    KEY idx_riwayat_kontrak_tanggal_mulai (tanggal_mulai),
    CONSTRAINT fk_riwayat_kontrak_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_keluar (extends LampiranSk, NOT @Audited → no _AUD)
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_keluar (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- LampiranSk inherited
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    -- RiwayatKeluar own fields
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    tanggal_keluar DATE DEFAULT NULL,
    alasan_berhenti_id BIGINT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_riwayat_keluar_pegawai (pegawai_id),
    CONSTRAINT fk_riwayat_keluar_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_riwayat_keluar_alasan FOREIGN KEY (alasan_berhenti_id) REFERENCES alasan_berhenti(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_terminasi (extends IdsAbstract directly, not LampiranSk — but
-- the entity uses some LampiranSk-like fields). Check entity: extends
-- IdsAbstract. Fields below mirror RiwayatTerminasi.java exactly.
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_terminasi (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- RiwayatTerminasi own fields
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    alasan_terminasi_id BIGINT DEFAULT NULL,
    nomor_sk VARCHAR(255) DEFAULT NULL,
    sk_terminasi_id BIGINT DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nama_golongan VARCHAR(255) DEFAULT NULL,
    tanggal_terminasi DATE DEFAULT NULL,
    tahun_terminasi INT DEFAULT NULL,
    masa_kerja INT DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_riwayat_terminasi_pegawai (pegawai_id),
    KEY idx_riwayat_terminasi_nomor (nomor_sk),
    KEY idx_riwayat_terminasi_tanggal (tanggal_terminasi),
    CONSTRAINT fk_riwayat_terminasi_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_riwayat_terminasi_alasan FOREIGN KEY (alasan_terminasi_id) REFERENCES alasan_berhenti(id),
    CONSTRAINT fk_riwayat_terminasi_sk FOREIGN KEY (sk_terminasi_id) REFERENCES riwayat_sk(id),
    CONSTRAINT fk_riwayat_terminasi_organisasi FOREIGN KEY (organisasi_id) REFERENCES organisasi(id),
    CONSTRAINT fk_riwayat_terminasi_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id),
    CONSTRAINT fk_riwayat_terminasi_golongan FOREIGN KEY (golongan_id) REFERENCES golongan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_sp (extends LampiranSp which is @MappedSuperclass)
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_sp (
    id BIGINT NOT NULL AUTO_INCREMENT,
    -- LampiranSp inherited
    nomor_sp VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    -- RiwayatSp own fields
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    tanggal_sp DATE DEFAULT NULL,
    jenis_sp_id BIGINT DEFAULT NULL,
    sanksi_id BIGINT DEFAULT NULL,
    sanksi_notes TEXT DEFAULT NULL,
    tanggal_eksekusi_sanksi DATE DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    penanda_tangan VARCHAR(255) DEFAULT NULL,
    jabatan_penanda_tangan VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_riwayat_sp_pegawai (pegawai_id),
    KEY idx_riwayat_sp_tanggal_sp (tanggal_sp),
    CONSTRAINT fk_riwayat_sp_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_riwayat_sp_organisasi FOREIGN KEY (organisasi_id) REFERENCES organisasi(id),
    CONSTRAINT fk_riwayat_sp_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id),
    CONSTRAINT fk_riwayat_sp_jenis FOREIGN KEY (jenis_sp_id) REFERENCES jenis_sp(id),
    CONSTRAINT fk_riwayat_sp_sanksi FOREIGN KEY (sanksi_id) REFERENCES sanksi_sp(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- riwayat_cuti
-- ----------------------------------------------------------------------
CREATE TABLE riwayat_cuti (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    cuti_pegawai_id BIGINT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    jumlah_hari INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_riwayat_cuti_pegawai (pegawai_id),
    KEY idx_riwayat_cuti_cuti_pegawai (cuti_pegawai_id),
    CONSTRAINT fk_riwayat_cuti_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id)
    -- FK fk_riwayat_cuti_cuti_pegawai is added in V5_0_4 (forward ref to cuti_pegawai).
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- statistik_pegawai (NOT @Audited → no _AUD)
-- ----------------------------------------------------------------------
CREATE TABLE statistik_pegawai (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    organisasi_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    total_pegawai INT DEFAULT NULL,
    gol_a INT DEFAULT NULL,
    gol_b INT DEFAULT NULL,
    gol_c INT DEFAULT NULL,
    gol_d INT DEFAULT NULL,
    golongan_a INT DEFAULT NULL,
    golongan_b INT DEFAULT NULL,
    golongan_c INT DEFAULT NULL,
    golongan_d INT DEFAULT NULL,
    kontrak INT DEFAULT NULL,
    capeg INT DEFAULT NULL,
    honorer INT DEFAULT NULL,
    tetap INT DEFAULT NULL,
    adm INT DEFAULT NULL,
    pelayanan INT DEFAULT NULL,
    teknik INT DEFAULT NULL,
    pria INT DEFAULT NULL,
    wanita INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_statistik_pegawai_organisasi (organisasi_id),
    KEY idx_statistik_pegawai_jabatan (jabatan_id),
    KEY idx_statistik_pegawai_golongan (golongan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- Envers _AUD siblings (8 — statistik_pegawai is NOT @Audited, riwayat_keluar
-- is NOT @Audited, lampiran_sk is @MappedSuperclass with no own table)
-- ======================================================================

CREATE TABLE riwayat_sk_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    ref_sk_id BIGINT DEFAULT NULL,
    jenis_kontrak INT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    is_latest BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_sk_aud_rev (REV),
    CONSTRAINT fk_riwayat_sk_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE riwayat_mutasi_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    jenis_mutasi INT DEFAULT NULL,
    riwayat_sk_id BIGINT DEFAULT NULL,
    tmt_berlaku DATE DEFAULT NULL,
    tanggal_berakhir DATE DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    profesi_id BIGINT DEFAULT NULL,
    nama_profesi VARCHAR(255) DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nama_golongan VARCHAR(255) DEFAULT NULL,
    organisasi_lama_id BIGINT DEFAULT NULL,
    nama_organisasi_lama VARCHAR(255) DEFAULT NULL,
    jabatan_lama_id BIGINT DEFAULT NULL,
    nama_jabatan_lama VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_mutasi_aud_rev (REV),
    CONSTRAINT fk_riwayat_mutasi_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE riwayat_kontrak_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    nomor_sk VARCHAR(255) DEFAULT NULL,
    tanggal_sk DATE DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nomor_kontrak VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_kontrak_aud_rev (REV),
    CONSTRAINT fk_riwayat_kontrak_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE riwayat_terminasi_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    alasan_terminasi_id BIGINT DEFAULT NULL,
    nomor_sk VARCHAR(255) DEFAULT NULL,
    sk_terminasi_id BIGINT DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    golongan_id BIGINT DEFAULT NULL,
    nama_golongan VARCHAR(255) DEFAULT NULL,
    tanggal_terminasi DATE DEFAULT NULL,
    tahun_terminasi INT DEFAULT NULL,
    masa_kerja INT DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_terminasi_aud_rev (REV),
    CONSTRAINT fk_riwayat_terminasi_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE riwayat_sp_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    nomor_sp VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    nama_organisasi VARCHAR(255) DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    nama_jabatan VARCHAR(255) DEFAULT NULL,
    tanggal_sp DATE DEFAULT NULL,
    jenis_sp_id BIGINT DEFAULT NULL,
    sanksi_id BIGINT DEFAULT NULL,
    sanksi_notes TEXT DEFAULT NULL,
    tanggal_eksekusi_sanksi DATE DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    penanda_tangan VARCHAR(255) DEFAULT NULL,
    jabatan_penanda_tangan VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_sp_aud_rev (REV),
    CONSTRAINT fk_riwayat_sp_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE riwayat_cuti_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    cuti_pegawai_id BIGINT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    jumlah_hari INT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_riwayat_cuti_aud_rev (REV),
    CONSTRAINT fk_riwayat_cuti_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- LampiranSk + LampiranSp _AUD are NOT created — they are
-- @MappedSuperclass with no own table.

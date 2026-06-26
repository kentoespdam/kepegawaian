-- V5_0_1__create_profil.sql
-- Profil domain: 9 entity tables (biodata, kartu_identitas, keahlian,
-- lampiran_profil, pelatihan, pendidikan, pengalaman_kerja,
-- profil_keluarga, profil_update) + 8 _AUD tables (profil_update is a
-- record DTO, not @Entity — no _AUD). Biodata uses String PK (nik).

-- ----------------------------------------------------------------------
-- biodata (PK = String nik, versioned)
-- ----------------------------------------------------------------------
CREATE TABLE biodata (
    nik VARCHAR(255) NOT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_kelamin INT DEFAULT NULL,
    tempat_lahir VARCHAR(255) DEFAULT NULL,
    tanggal_lahir DATE DEFAULT NULL,
    alamat VARCHAR(255) DEFAULT NULL,
    telp VARCHAR(255) DEFAULT NULL,
    agama INT DEFAULT NULL,
    ibu_kandung VARCHAR(255) DEFAULT NULL,
    pendidikan_id BIGINT DEFAULT NULL,
    golongan_darah VARCHAR(31) DEFAULT NULL,
    status_kawin INT DEFAULT NULL,
    foto_profil VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    is_pegawai BIT(1) NOT NULL DEFAULT 0,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    PRIMARY KEY (nik),
    KEY idx_biodata_pendidikan (pendidikan_id),
    CONSTRAINT fk_biodata_pendidikan FOREIGN KEY (pendidikan_id) REFERENCES jenjang_pendidikan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- kartu_identitas
-- ----------------------------------------------------------------------
CREATE TABLE kartu_identitas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) NOT NULL,
    jenis_kitas_id BIGINT NOT NULL,
    nomor VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    tanggal_berlaku DATE DEFAULT NULL,
    tanggal_berakhir DATE DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_kartu_identitas_biodata (biodata_id),
    KEY idx_kartu_identitas_jenis (jenis_kitas_id),
    CONSTRAINT fk_kartu_identitas_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik),
    CONSTRAINT fk_kartu_identitas_jenis_kitas FOREIGN KEY (jenis_kitas_id) REFERENCES jenis_kitas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- keahlian
-- ----------------------------------------------------------------------
CREATE TABLE keahlian (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) NOT NULL,
    jenis_keahlian_id BIGINT NOT NULL,
    kualifikasi INT DEFAULT NULL,
    sertifikasi BIT(1) DEFAULT NULL,
    institusi VARCHAR(255) DEFAULT NULL,
    tahun INT DEFAULT NULL,
    masa_berlaku VARCHAR(255) DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    tanggal_pengajuan DATETIME DEFAULT NULL,
    tanggal_disetujui DATETIME DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_keahlian_biodata (biodata_id),
    KEY idx_keahlian_jenis (jenis_keahlian_id),
    CONSTRAINT fk_keahlian_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik),
    CONSTRAINT fk_keahlian_jenis_keahlian FOREIGN KEY (jenis_keahlian_id) REFERENCES jenis_keahlian(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- lampiran_profil
-- ----------------------------------------------------------------------
CREATE TABLE `lampiran_profil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `ref` tinyint(4) NOT NULL,
  `ref_id` bigint(20) NOT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXnqxyy5rw082ve9ndt7bsti7ww` (`is_deleted`) USING BTREE,
  KEY `IDXcbxxx72ltdffrpex2eoky44lw` (`ref`) USING BTREE,
  KEY `IDXsr43t3b8l3f27u4q7ciqsmkke` (`ref_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- pelatihan
-- ----------------------------------------------------------------------
CREATE TABLE pelatihan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) NOT NULL,
    jenis_pelatihan_id BIGINT NOT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    lembaga VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    lulus BIT(1) DEFAULT NULL,
    nilai VARCHAR(255) DEFAULT NULL,
    ikatan_dinas BIT(1) NOT NULL DEFAULT 0,
    tanggal_akhir_ikatan DATE DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    tanggal_pengajuan DATETIME DEFAULT NULL,
    tanggal_disetujui DATETIME DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pelatihan_biodata (biodata_id),
    KEY idx_pelatihan_jenis (jenis_pelatihan_id),
    CONSTRAINT fk_pelatihan_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik),
    CONSTRAINT fk_pelatihan_jenis FOREIGN KEY (jenis_pelatihan_id) REFERENCES jenis_pelatihan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- pendidikan
-- ----------------------------------------------------------------------
CREATE TABLE pendidikan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) NOT NULL,
    jenjang_pendidikan_id BIGINT NOT NULL,
    institusi VARCHAR(255) DEFAULT NULL,
    jurusan VARCHAR(255) DEFAULT NULL,
    tahun_masuk INT DEFAULT NULL,
    tahun_lulus INT DEFAULT NULL,
    no_ijazah VARCHAR(255) DEFAULT NULL,
    tanggal_ijazah DATE DEFAULT NULL,
    file_ijazah VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pendidikan_biodata (biodata_id),
    KEY idx_pendidikan_jenjang (jenjang_pendidikan_id),
    CONSTRAINT fk_pendidikan_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik),
    CONSTRAINT fk_pendidikan_jenjang FOREIGN KEY (jenjang_pendidikan_id) REFERENCES jenjang_pendidikan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- pengalaman_kerja
-- ----------------------------------------------------------------------
CREATE TABLE pengalaman_kerja (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) NOT NULL,
    perusahaan VARCHAR(255) DEFAULT NULL,
    jabatan VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    alasan_berhenti TEXT DEFAULT NULL,
    gaji_terakhir DOUBLE DEFAULT NULL,
    referensi VARCHAR(255) DEFAULT NULL,
    kontak_referensi VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_pengalaman_kerja_biodata (biodata_id),
    CONSTRAINT fk_pengalaman_kerja_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- profil_keluarga
-- ----------------------------------------------------------------------
CREATE TABLE profil_keluarga (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nik VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_kelamin INT DEFAULT NULL,
    agama INT DEFAULT NULL,
    hubungan_keluarga INT DEFAULT NULL,
    tempat_lahir VARCHAR(255) DEFAULT NULL,
    tanggal_lahir DATE DEFAULT NULL,
    tanggungan BIT(1) NOT NULL DEFAULT 0,
    pendidikan_id BIGINT DEFAULT NULL,
    status_pendidikan INT DEFAULT NULL,
    status_kawin BIT(1) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    biodata_id VARCHAR(255) NOT NULL,
    changed_status BIT(1) NOT NULL DEFAULT 0,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    KEY idx_profil_keluarga_biodata (biodata_id),
    KEY idx_profil_keluarga_pendidikan (pendidikan_id),
    CONSTRAINT fk_profil_keluarga_biodata FOREIGN KEY (biodata_id) REFERENCES biodata(nik),
    CONSTRAINT fk_profil_keluarga_pendidikan FOREIGN KEY (pendidikan_id) REFERENCES jenjang_pendidikan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- profil_update (no _AUD — record DTO, not @Entity)
-- Table is created to satisfy the entity (PegawaiProfilUpdate references it
-- for some queries). Kept minimal to mirror @Column shapes.
-- ----------------------------------------------------------------------
CREATE TABLE profil_update (
    id BIGINT NOT NULL AUTO_INCREMENT,
    biodata_id VARCHAR(255) DEFAULT NULL,
    field_name VARCHAR(255) DEFAULT NULL,
    old_value TEXT DEFAULT NULL,
    new_value TEXT DEFAULT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- Envers _AUD siblings
-- ======================================================================

CREATE TABLE biodata_AUD (
    nik VARCHAR(255) NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_kelamin INT DEFAULT NULL,
    tempat_lahir VARCHAR(255) DEFAULT NULL,
    tanggal_lahir DATE DEFAULT NULL,
    alamat VARCHAR(255) DEFAULT NULL,
    telp VARCHAR(255) DEFAULT NULL,
    agama INT DEFAULT NULL,
    ibu_kandung VARCHAR(255) DEFAULT NULL,
    pendidikan_id BIGINT DEFAULT NULL,
    golongan_darah VARCHAR(31) DEFAULT NULL,
    status_kawin INT DEFAULT NULL,
    foto_profil VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    is_pegawai BIT(1) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (nik, REV),
    KEY idx_biodata_aud_rev (REV),
    CONSTRAINT fk_biodata_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE kartu_identitas_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    jenis_kitas_id BIGINT DEFAULT NULL,
    nomor VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    tanggal_berlaku DATE DEFAULT NULL,
    tanggal_berakhir DATE DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_kartu_identitas_aud_rev (REV),
    CONSTRAINT fk_kartu_identitas_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE keahlian_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    jenis_keahlian_id BIGINT DEFAULT NULL,
    kualifikasi INT DEFAULT NULL,
    sertifikasi BIT(1) DEFAULT NULL,
    institusi VARCHAR(255) DEFAULT NULL,
    tahun INT DEFAULT NULL,
    masa_berlaku VARCHAR(255) DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    tanggal_pengajuan DATETIME DEFAULT NULL,
    tanggal_disetujui DATETIME DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_keahlian_aud_rev (REV),
    CONSTRAINT fk_keahlian_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lampiran_profil_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_lampiran_profil_aud_rev (REV),
    CONSTRAINT fk_lampiran_profil_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pelatihan_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    jenis_pelatihan_id BIGINT DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    lembaga VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    lulus BIT(1) DEFAULT NULL,
    nilai VARCHAR(255) DEFAULT NULL,
    ikatan_dinas BIT(1) DEFAULT NULL,
    tanggal_akhir_ikatan DATE DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    disetujui BIT(1) DEFAULT NULL,
    tanggal_pengajuan DATETIME DEFAULT NULL,
    tanggal_disetujui DATETIME DEFAULT NULL,
    disetujui_oleh VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_pelatihan_aud_rev (REV),
    CONSTRAINT fk_pelatihan_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pendidikan_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    jenjang_pendidikan_id BIGINT DEFAULT NULL,
    institusi VARCHAR(255) DEFAULT NULL,
    jurusan VARCHAR(255) DEFAULT NULL,
    tahun_masuk INT DEFAULT NULL,
    tahun_lulus INT DEFAULT NULL,
    no_ijazah VARCHAR(255) DEFAULT NULL,
    tanggal_ijazah DATE DEFAULT NULL,
    file_ijazah VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_pendidikan_aud_rev (REV),
    CONSTRAINT fk_pendidikan_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pengalaman_kerja_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    perusahaan VARCHAR(255) DEFAULT NULL,
    jabatan VARCHAR(255) DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    alasan_berhenti TEXT DEFAULT NULL,
    gaji_terakhir DOUBLE DEFAULT NULL,
    referensi VARCHAR(255) DEFAULT NULL,
    kontak_referensi VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_pengalaman_kerja_aud_rev (REV),
    CONSTRAINT fk_pengalaman_kerja_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE profil_keluarga_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    nik VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    jenis_kelamin INT DEFAULT NULL,
    agama INT DEFAULT NULL,
    hubungan_keluarga INT DEFAULT NULL,
    tempat_lahir VARCHAR(255) DEFAULT NULL,
    tanggal_lahir DATE DEFAULT NULL,
    tanggungan BIT(1) DEFAULT NULL,
    pendidikan_id BIGINT DEFAULT NULL,
    status_pendidikan INT DEFAULT NULL,
    status_kawin BIT(1) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    biodata_id VARCHAR(255) DEFAULT NULL,
    changed_status BIT(1) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_profil_keluarga_aud_rev (REV),
    CONSTRAINT fk_profil_keluarga_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

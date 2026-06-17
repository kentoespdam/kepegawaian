-- V5_0_4__create_cuti.sql
-- Cuti domain (5 entity tables) — cuti_jenis + cuti_kuota are partially in
-- V2 but new fields needed: 4 _AUD siblings (cuti_pegawai, cuti_approval,
-- cuti_approval_chain, cuti_kuota). CutiJenis already has _AUD in V2; not
-- touched here. CutiKlaimDetail is plain @Entity, no @Audited.
-- - CutiPegawai extends IdsAbstract (Long PK, with audit columns)
-- - CutiApproval extends IdsAbstract
-- - CutiApprovalChain is standalone (Long PK, only its own fields)
-- - CutiKlaimDetail is standalone (no @Audited, no soft delete)
-- - CutiKuota extends IdsAbstract (Long PK, with audit columns)

-- ----------------------------------------------------------------------
-- cuti_pegawai
-- ----------------------------------------------------------------------
CREATE TABLE cuti_pegawai (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    pegawai_id BIGINT NOT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pangkat_golongan VARCHAR(255) DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    jenis_pengajuan_cuti INT DEFAULT NULL,
    cuti_jenis_id BIGINT DEFAULT NULL,
    cuti_penambah INT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    jumlah_hari INT DEFAULT NULL,
    alamat_cuti VARCHAR(255) DEFAULT NULL,
    telp_cuti VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_cuti_pegawai_pegawai (pegawai_id),
    KEY idx_cuti_pegawai_cuti_jenis (cuti_jenis_id),
    KEY idx_cuti_pegawai_organisasi (organisasi_id),
    KEY idx_cuti_pegawai_jabatan (jabatan_id),
    KEY is_deleted_idx (is_deleted),
    CONSTRAINT fk_cuti_pegawai_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id),
    CONSTRAINT fk_cuti_pegawai_cuti_jenis FOREIGN KEY (cuti_jenis_id) REFERENCES cuti_jenis(id),
    CONSTRAINT fk_cuti_pegawai_organisasi FOREIGN KEY (organisasi_id) REFERENCES organisasi(id),
    CONSTRAINT fk_cuti_pegawai_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- cuti_approval
-- ----------------------------------------------------------------------
CREATE TABLE cuti_approval (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    cuti_pegawai_id BIGINT NOT NULL,
    approver_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    approval_level INT DEFAULT NULL,
    approval_status INT DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_cuti_approval_cuti_pegawai (cuti_pegawai_id),
    KEY idx_cuti_approval_approver (approver_id),
    KEY idx_cuti_approval_jabatan (jabatan_id),
    CONSTRAINT fk_cuti_approval_cuti_pegawai FOREIGN KEY (cuti_pegawai_id) REFERENCES cuti_pegawai(id),
    CONSTRAINT fk_cuti_approval_approver FOREIGN KEY (approver_id) REFERENCES pegawai(id),
    CONSTRAINT fk_cuti_approval_jabatan FOREIGN KEY (jabatan_id) REFERENCES jabatan(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- cuti_approval_chain (standalone, no @MappedSuperclass audit columns)
-- ----------------------------------------------------------------------
CREATE TABLE cuti_approval_chain (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ref_cuti_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    jabatan_nama VARCHAR(255) DEFAULT NULL,
    approval_level INT DEFAULT NULL,
    approval_status INT DEFAULT NULL,
    read_write_status INT DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_cuti_approval_chain_read_write (read_write_status),
    KEY idx_cuti_approval_chain_status (approval_status),
    CONSTRAINT fk_cuti_approval_chain_ref FOREIGN KEY (ref_cuti_id) REFERENCES cuti_pegawai(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- cuti_kuota
-- ----------------------------------------------------------------------
CREATE TABLE cuti_kuota (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    pegawai_id BIGINT NOT NULL,
    tahun INT DEFAULT NULL,
    kuota INT DEFAULT 0,
    kuota_terpakai INT DEFAULT 0,
    kuota_tambahan INT DEFAULT 0,
    sisa_kuota INT DEFAULT 0,
    expired DATE DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_cuti_kuota_pegawai (pegawai_id),
    KEY is_deleted_idx (is_deleted),
    CONSTRAINT fk_cuti_kuota_pegawai FOREIGN KEY (pegawai_id) REFERENCES pegawai(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------
-- cuti_klaim_detail (no @Audited → no _AUD; plain @Entity)
-- ----------------------------------------------------------------------
CREATE TABLE cuti_klaim_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ref_cuti_id BIGINT DEFAULT NULL,
    tanggal DATE DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_cuti_klaim_detail_ref_cuti (ref_cuti_id),
    CONSTRAINT fk_cuti_klaim_detail_ref_cuti FOREIGN KEY (ref_cuti_id) REFERENCES cuti_pegawai(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================================
-- Envers _AUD siblings
-- ======================================================================

CREATE TABLE cuti_pegawai_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    nipam VARCHAR(255) DEFAULT NULL,
    nama VARCHAR(255) DEFAULT NULL,
    pangkat_golongan VARCHAR(255) DEFAULT NULL,
    organisasi_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    jenis_pengajuan_cuti INT DEFAULT NULL,
    cuti_jenis_id BIGINT DEFAULT NULL,
    cuti_penambah INT DEFAULT NULL,
    tanggal_mulai DATE DEFAULT NULL,
    tanggal_selesai DATE DEFAULT NULL,
    jumlah_hari INT DEFAULT NULL,
    alamat_cuti VARCHAR(255) DEFAULT NULL,
    telp_cuti VARCHAR(255) DEFAULT NULL,
    file_url VARCHAR(255) DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_cuti_pegawai_aud_rev (REV),
    CONSTRAINT fk_cuti_pegawai_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cuti_approval_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    cuti_pegawai_id BIGINT DEFAULT NULL,
    approver_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    approval_level INT DEFAULT NULL,
    approval_status INT DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_cuti_approval_aud_rev (REV),
    CONSTRAINT fk_cuti_approval_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cuti_approval_chain_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    ref_cuti_id BIGINT DEFAULT NULL,
    jabatan_id BIGINT DEFAULT NULL,
    jabatan_nama VARCHAR(255) DEFAULT NULL,
    approval_level INT DEFAULT NULL,
    approval_status INT DEFAULT NULL,
    read_write_status INT DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_cuti_approval_chain_aud_rev (REV),
    CONSTRAINT fk_cuti_approval_chain_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cuti_kuota_AUD (
    id BIGINT NOT NULL,
    REV BIGINT NOT NULL,
    REVTYPE TINYINT DEFAULT NULL,
    created_by VARCHAR(255) DEFAULT NULL,
    updated_by VARCHAR(255) DEFAULT NULL,
    is_deleted BIT(1) DEFAULT NULL,
    pegawai_id BIGINT DEFAULT NULL,
    tahun INT DEFAULT NULL,
    kuota INT DEFAULT NULL,
    kuota_terpakai INT DEFAULT NULL,
    kuota_tambahan INT DEFAULT NULL,
    sisa_kuota INT DEFAULT NULL,
    expired DATE DEFAULT NULL,
    PRIMARY KEY (id, REV),
    KEY idx_cuti_kuota_aud_rev (REV),
    CONSTRAINT fk_cuti_kuota_aud_rev FOREIGN KEY (REV) REFERENCES revinfo(REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- V5_0_6__create_master_hari_libur.sql
-- HariLibur is the one master entity that wasn't migrated to
-- MasterBaseEntity in the F5/E0 work — it still extends MasterBaseEntity
-- but is NOT @Audited, so it gets no _AUD sibling. The entity specifies
-- @SQLDelete to set is_deleted=true and a unique constraint on tanggal.

CREATE TABLE hari_libur (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BIT(1) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 1,
    tanggal DATE DEFAULT NULL,
    jenis_libur INT DEFAULT NULL,
    notes TEXT DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_tanggal_idx (tanggal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

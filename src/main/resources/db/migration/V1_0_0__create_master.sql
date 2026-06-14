-- Master module: referensi/data tables for kepegawaian system
-- ============================================================

-- 1. LEVEL (tingkatan/eselon, melekat pada Jabatan)
CREATE TABLE IF NOT EXISTS `level` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `nama`       VARCHAR(255) NOT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_level_nama` (`nama`),
  INDEX `idx_level_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. GOLONGAN (kepangkatan)
CREATE TABLE IF NOT EXISTS `golongan` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `golongan`   VARCHAR(255) NOT NULL,
  `pangkat`    VARCHAR(255) DEFAULT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_golongan_golongan` (`golongan`),
  INDEX `idx_golongan_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. GRADE (tingkat gaji, anak Level)
CREATE TABLE IF NOT EXISTS `grade` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `level_id`   BIGINT       DEFAULT NULL,
  `grade`      INT(11)      NOT NULL,
  `tukin`      DOUBLE       DEFAULT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_grade_grade` (`grade`),
  INDEX `idx_grade_is_deleted` (`is_deleted`),
  INDEX `idx_grade_level` (`level_id`),
  CONSTRAINT `fk_grade_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. ORGANISASI (unit kerja, tree self-ref)
CREATE TABLE IF NOT EXISTS `organisasi` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `kode`       VARCHAR(50)  NOT NULL,
  `parent_id`  BIGINT       DEFAULT NULL,
  `level_org`  INT(11)      DEFAULT NULL,
  `nama`       VARCHAR(255) NOT NULL,
  `short_name` VARCHAR(100) DEFAULT NULL,
  `category`   VARCHAR(100) DEFAULT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_organisasi_kode` (`kode`),
  INDEX `idx_organisasi_nama` (`nama`),
  INDEX `idx_organisasi_level_org` (`level_org`),
  INDEX `idx_organisasi_parent` (`parent_id`),
  INDEX `idx_organisasi_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_organisasi_parent` FOREIGN KEY (`parent_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. JABATAN (kedudukan struktural, tree self-ref + FK Organisasi + FK Level)
CREATE TABLE IF NOT EXISTS `jabatan` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `kode`          VARCHAR(50)  DEFAULT NULL,
  `parent_id`     BIGINT       DEFAULT NULL,
  `organisasi_id` BIGINT       DEFAULT NULL,
  `level_id`      BIGINT       DEFAULT NULL,
  `nama`          VARCHAR(255) NOT NULL,
  `is_deleted`    BIT(1)       NOT NULL DEFAULT b'0',
  `created_at`    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by`    VARCHAR(255) DEFAULT NULL,
  `updated_at`    DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_jabatan_kode` (`kode`),
  INDEX `idx_jabatan_nama` (`nama`),
  INDEX `idx_jabatan_parent` (`parent_id`),
  INDEX `idx_jabatan_organisasi` (`organisasi_id`),
  INDEX `idx_jabatan_level` (`level_id`),
  INDEX `idx_jabatan_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_jabatan_parent` FOREIGN KEY (`parent_id`) REFERENCES `jabatan` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_jabatan_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_jabatan_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. PROFESI (peran/pekerjaan pegawai, aggregate root)
CREATE TABLE IF NOT EXISTS `profesi` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `nama`          VARCHAR(255) NOT NULL,
  `detail`        TEXT         DEFAULT NULL,
  `resiko`        VARCHAR(255) DEFAULT NULL,
  `organisasi_id` BIGINT       DEFAULT NULL,
  `jabatan_id`    BIGINT       DEFAULT NULL,
  `level_id`      BIGINT       DEFAULT NULL,
  `grade_id`      BIGINT       DEFAULT NULL,
  `is_deleted`    BIT(1)       NOT NULL DEFAULT b'0',
  `created_at`    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by`    VARCHAR(255) DEFAULT NULL,
  `updated_at`    DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by`    VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_profesi_nama` (`nama`),
  INDEX `idx_profesi_organisasi` (`organisasi_id`),
  INDEX `idx_profesi_jabatan` (`jabatan_id`),
  INDEX `idx_profesi_level` (`level_id`),
  INDEX `idx_profesi_grade` (`grade_id`),
  INDEX `idx_profesi_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_profesi_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. ALAT KERJA (perlengkapan kerja per profesi)
CREATE TABLE IF NOT EXISTS `alat_kerja` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `profesi_id` BIGINT       NOT NULL,
  `nama`       VARCHAR(255) NOT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_alat_kerja_nama` (`nama`),
  INDEX `idx_alat_kerja_profesi` (`profesi_id`),
  INDEX `idx_alat_kerja_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_alat_kerja_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. APD (alat pelindung diri per profesi)
CREATE TABLE IF NOT EXISTS `apd` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `profesi_id` BIGINT       NOT NULL,
  `nama`       VARCHAR(255) NOT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_apd_nama` (`nama`),
  INDEX `idx_apd_profesi` (`profesi_id`),
  INDEX `idx_apd_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_apd_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. JENIS SP (jenis surat peringatan)
CREATE TABLE IF NOT EXISTS `jenis_sp` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `kode`       VARCHAR(10)  NOT NULL,
  `nama`       VARCHAR(255) NOT NULL,
  `is_deleted` BIT(1)       NOT NULL DEFAULT b'0',
  `created_at` DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` VARCHAR(255) DEFAULT NULL,
  `updated_at` DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_jenis_sp_kode` (`kode`),
  INDEX `idx_jenis_sp_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. SANKSI_SP (sanksi, anak Jenis SP)
CREATE TABLE IF NOT EXISTS `sanksi_sp` (
  `id`                BIGINT       NOT NULL AUTO_INCREMENT,
  `kode`              VARCHAR(10)  NOT NULL,
  `keterangan`        TEXT,
  `pot_tkk`           BIT(1)       DEFAULT b'0',
  `jml_pot_tkk`       INT          DEFAULT 0,
  `is_pending_pangkat` BIT(1)      DEFAULT b'0',
  `is_pending_gaji`   BIT(1)       DEFAULT b'0',
  `is_turun_pangkat`  BIT(1)       DEFAULT b'0',
  `is_turun_jabatan`  BIT(1)       DEFAULT b'0',
  `is_suspension`     BIT(1)       DEFAULT b'0',
  `is_terminate_dh`   BIT(1)       DEFAULT b'0',
  `is_terminate_th`   BIT(1)       DEFAULT b'0',
  `jenis_sp_id`       BIGINT       DEFAULT NULL,
  `is_deleted`        BIT(1)       NOT NULL DEFAULT b'0',
  `created_at`        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_by`        VARCHAR(255) DEFAULT NULL,
  `updated_at`        DATETIME(6)  DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by`        VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_sanksi_sp_kode` (`kode`),
  INDEX `idx_sanksi_sp_jenis_sp` (`jenis_sp_id`),
  INDEX `idx_sanksi_sp_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_sanksi_sp_jenis_sp` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

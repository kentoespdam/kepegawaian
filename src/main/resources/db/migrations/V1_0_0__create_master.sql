CREATE TABLE IF NOT EXISTS `status_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nama_idx` (`nama`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nama_idx` (`nama`)
) ENGINE=InnoDB;

CREATE TABLE `golongan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `golongan` varchar(255) DEFAULT NULL,
   `pangkat` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `golongan_idx` (`golongan`),
  KEY `pangkat_idx` (`pangkat`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level_id` bigint(20) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `tukin` double DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `grade_idx` (`grade`),
  KEY `level_idx` (`level_id`),
  CONSTRAINT `fk_level_grade` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `organisasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `level_org` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nama_idx` (`nama`),
  KEY `level_idx` (`level_org`),
  KEY `parent_idx` (`parent_id`),
  CONSTRAINT `fk_parent_organisasi` FOREIGN KEY (`parent_id`) REFERENCES `organisasi` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `profesi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nama_idx` (`nama`),
  KEY `level_idx` (`level_id`),
  CONSTRAINT `fk_level_profesi` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `jabatan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `nama_idx` (`nama`),
  KEY `golongan_idx` (`golongan_id`),
  KEY `parent_idx` (`parent_id`),
  KEY `level_idx` (`level_id`),
  KEY `organisasi_idx` (`organisasi_id`),
  CONSTRAINT `fk_parent_jabatan` FOREIGN KEY (`parent_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_level_jabatan` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `fk_organisasi_jabatan` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_golongan_jabatan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`)
) ENGINE=InnoDB;

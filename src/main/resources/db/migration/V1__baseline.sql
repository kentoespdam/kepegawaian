-- V1_0_0__baseline.sql
-- Squashed baseline — V1__baseline.sql (sebelumnya V1_0_0..V5_1_0) (ADR-0032, epic kepegawaian-odb)
-- 89 tables (12 orphan master _AUD removed per ADR-0003)
-- Seed data: V3_0_0..V3_0_22 | View: V5_0_0__create_view_v_pegawai.sql

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `alasan_berhenti`;
CREATE TABLE `alasan_berhenti` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_als_brh_nama` (`nama`) USING BTREE,
  KEY `idx_als_brh_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `alat_kerja`;
CREATE TABLE `alat_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_alt_krj_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_alt_krj_nama` (`nama`) USING BTREE,
  KEY `idx_alt_krj_pfs_id` (`profesi_id`) USING BTREE,
  CONSTRAINT `fk_alt_krj_pfs_pfs_id` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `apd`;
CREATE TABLE `apd` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_apd_nama` (`nama`) USING BTREE,
  KEY `idx_apd_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_apd_pfs_id` (`profesi_id`) USING BTREE,
  CONSTRAINT `fk_apd_pfs_pfs_id` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `biodata`;
CREATE TABLE `biodata` (
  `nik` varchar(255) NOT NULL,
  `agama` tinyint(4) DEFAULT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `foto_profil` varchar(255) DEFAULT NULL,
  `golongan_darah` enum('A','B','AB','O') DEFAULT NULL,
  `ibu_kandung` varchar(255) DEFAULT NULL,
  `is_pegawai` bit(1) DEFAULT NULL,
  `jenis_kelamin` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status_kawin` tinyint(4) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `telp` varchar(255) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`nik`) USING BTREE,
  KEY `idx_bio_nama` (`nama`) USING BTREE,
  KEY `idx_bio_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_bio_jns_kelamin` (`jenis_kelamin`) USING BTREE,
  KEY `idx_bio_alamat` (`alamat`) USING BTREE,
  KEY `idx_bio_is_pgw` (`is_pegawai`) USING BTREE,
  KEY `idx_bio_ddk_id` (`pendidikan_id`) USING BTREE,
  CONSTRAINT `fk_bio_jjg_ddk_ddk_id` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `biodata_aud`;
CREATE TABLE `biodata_aud` (
  `nik` varchar(255) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `agama` tinyint(4) DEFAULT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `foto_profil` varchar(255) DEFAULT NULL,
  `golongan_darah` enum('A','B','AB','O') DEFAULT NULL,
  `ibu_kandung` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `is_pegawai` bit(1) DEFAULT NULL,
  `jenis_kelamin` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status_kawin` tinyint(4) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `telp` varchar(255) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`nik`) USING BTREE,
  CONSTRAINT `fk_bio_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_approval`;
CREATE TABLE `cuti_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `approver_id` bigint(20) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_apv_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_ct_apv_approver_id` (`approver_id`) USING BTREE,
  KEY `idx_ct_apv_ct_pgw_id` (`cuti_pegawai_id`) USING BTREE,
  KEY `idx_ct_apv_jbt_id` (`jabatan_id`) USING BTREE,
  CONSTRAINT `fk_ct_apv_ct_pgw_ct_pgw_id` FOREIGN KEY (`cuti_pegawai_id`) REFERENCES `cuti_pegawai` (`id`),
  CONSTRAINT `fk_ct_apv_pgw_approver_id` FOREIGN KEY (`approver_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_ct_apv_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_approval_aud`;
CREATE TABLE `cuti_approval_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `approver_id` bigint(20) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ct_apv_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_approval_chain`;
CREATE TABLE `cuti_approval_chain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_nama` varchar(255) DEFAULT NULL,
  `read_write_status` tinyint(4) DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_apv_chn_ref_ct_id` (`ref_cuti_id`) USING BTREE,
  KEY `idx_ct_apv_chn_read_write_status` (`read_write_status`) USING BTREE,
  KEY `idx_ct_apv_chn_apv_status` (`approval_status`) USING BTREE,
  CONSTRAINT `fk_ct_apv_chn_ct_pgw_ref_ct_id` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_jenis`;
CREATE TABLE `cuti_jenis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `max_hari` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `potong_kuota_tahunan` bit(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_jns_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_jenis_aud`;
CREATE TABLE `cuti_jenis_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `max_hari` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `potong_kuota_tahunan` bit(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ct_jns_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_klaim_detail`;
CREATE TABLE `cuti_klaim_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tanggal` date DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_klm_dtl_ref_ct_id` (`ref_cuti_id`) USING BTREE,
  CONSTRAINT `fk_ct_klm_dtl_ct_pgw_ref_ct_id` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_kuota`;
CREATE TABLE `cuti_kuota` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expired` date DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `kuota_tambahan` int(11) DEFAULT NULL,
  `kuota_terpakai` int(11) DEFAULT NULL,
  `sisa_kuota` int(11) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_kta_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_ct_kta_pgw_id` (`pegawai_id`) USING BTREE,
  CONSTRAINT `fk_ct_kta_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_kuota_aud`;
CREATE TABLE `cuti_kuota_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `expired` date DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `kuota_tambahan` int(11) DEFAULT NULL,
  `kuota_terpakai` int(11) DEFAULT NULL,
  `sisa_kuota` int(11) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ct_kta_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_pegawai`;
CREATE TABLE `cuti_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alasan` varchar(255) DEFAULT NULL,
  `approval_cuti_status` tinyint(4) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `is_claimed` tinyint(1) DEFAULT 0,
  `jenis_pengajuan_cuti` tinyint(4) DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  `jumlah_hari_kerja` int(11) DEFAULT NULL,
  `kuota_akhir` int(11) DEFAULT NULL,
  `kuota_awal` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pangkat_golongan` varchar(255) DEFAULT NULL,
  `riwayat_kuota0` int(11) DEFAULT NULL,
  `riwayat_kuota1` int(11) DEFAULT NULL,
  `riwayat_pakai0` int(11) DEFAULT NULL,
  `riwayat_pakai1` int(11) DEFAULT NULL,
  `riwayat_sisa0` int(11) DEFAULT NULL,
  `riwayat_sisa1` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_cuti_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `pic_saat_ini_id` bigint(20) DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  `sub_jenis_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ct_pgw_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_ct_pgw_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_ct_pgw_jns_ct_id` (`jenis_cuti_id`) USING BTREE,
  KEY `idx_ct_pgw_org_id` (`organisasi_id`) USING BTREE,
  KEY `idx_ct_pgw_pgw_id` (`pegawai_id`) USING BTREE,
  KEY `idx_ct_pgw_pic_saat_ini_id` (`pic_saat_ini_id`) USING BTREE,
  KEY `idx_ct_pgw_sub_jns_ct_id` (`sub_jenis_cuti_id`) USING BTREE,
  CONSTRAINT `fk_ct_pgw_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_ct_pgw_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_ct_pgw_jbt_pic_saat_ini_id` FOREIGN KEY (`pic_saat_ini_id`) REFERENCES `jabatan` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_ct_pgw_ct_jns_jns_ct_id` FOREIGN KEY (`jenis_cuti_id`) REFERENCES `cuti_jenis` (`id`),
  CONSTRAINT `fk_ct_pgw_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_ct_pgw_ct_jns_sub_jns_ct_id` FOREIGN KEY (`sub_jenis_cuti_id`) REFERENCES `cuti_jenis` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `cuti_pegawai_aud`;
CREATE TABLE `cuti_pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `alasan` varchar(255) DEFAULT NULL,
  `approval_cuti_status` tinyint(4) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `is_claimed` tinyint(1) DEFAULT 0,
  `jenis_pengajuan_cuti` tinyint(4) DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  `jumlah_hari_kerja` int(11) DEFAULT NULL,
  `kuota_akhir` int(11) DEFAULT NULL,
  `kuota_awal` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pangkat_golongan` varchar(255) DEFAULT NULL,
  `riwayat_kuota0` int(11) DEFAULT NULL,
  `riwayat_kuota1` int(11) DEFAULT NULL,
  `riwayat_pakai0` int(11) DEFAULT NULL,
  `riwayat_pakai1` int(11) DEFAULT NULL,
  `riwayat_sisa0` int(11) DEFAULT NULL,
  `riwayat_sisa1` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_cuti_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `pic_saat_ini_id` bigint(20) DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  `sub_jenis_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ct_pgw_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `dasar_gaji`;
CREATE TABLE `dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `aktif` bit(1) NOT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `tanggal_akhir` date DEFAULT NULL,
  `tanggal_awal` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_dsr_gj_deskripsi` (`deskripsi`) USING BTREE,
  KEY `idx_dsr_gj_aktif` (`aktif`) USING BTREE,
  KEY `idx_dsr_gj_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `dasar_gaji_aud`;
CREATE TABLE `dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `aktif` bit(1) DEFAULT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `tanggal_akhir` date DEFAULT NULL,
  `tanggal_awal` date DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_dsr_gj_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `detail_dasar_gaji`;
CREATE TABLE `detail_dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `golongan_kode` int(11) DEFAULT NULL,
  `mkg` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `dasar_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_dtl_dsr_gj_mkg` (`mkg`) USING BTREE,
  KEY `idx_dtl_dsr_gj_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_dtl_dsr_gj_dsr_gj_id` (`dasar_gaji_id`) USING BTREE,
  CONSTRAINT `fk_dtl_dsr_gj_dsr_gj_dsr_gj_id` FOREIGN KEY (`dasar_gaji_id`) REFERENCES `dasar_gaji` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `detail_dasar_gaji_aud`;
CREATE TABLE `detail_dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `golongan_kode` int(11) DEFAULT NULL,
  `mkg` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `dasar_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_dtl_dsr_gj_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_master`;
CREATE TABLE `gaji_batch_master` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gaji_pokok` double DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `golongan` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `is_different` bit(1) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jml_jiwa` int(11) DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `kode_pajak` varchar(255) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pajak` double DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `pembulatan` double DEFAULT NULL,
  `pembulatan2` double DEFAULT NULL,
  `penghasilan_bersih` double DEFAULT NULL,
  `penghasilan_bersih2` double DEFAULT NULL,
  `penghasilan_bersih_final` double DEFAULT NULL,
  `penghasilan_bersih_final2` double DEFAULT NULL,
  `penghasilan_kotor` double DEFAULT NULL,
  `periode` varchar(255) DEFAULT NULL,
  `phdp` double DEFAULT NULL,
  `status_kawin` tinyint(4) DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `total_add_potongan` double DEFAULT NULL,
  `total_add_tambahan` double DEFAULT NULL,
  `total_potongan` double DEFAULT NULL,
  `batch_root_id` varchar(255) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_mst_periode` (`periode`) USING BTREE,
  KEY `idx_gj_bt_mst_nipam` (`nipam`) USING BTREE,
  KEY `idx_gj_bt_mst_nama` (`nama`) USING BTREE,
  KEY `idx_gj_bt_mst_bt_root_id` (`batch_root_id`) USING BTREE,
  KEY `idx_gj_bt_mst_gj_pp_nn_pjk_id` (`gaji_pendapatan_non_pajak_id`) USING BTREE,
  KEY `idx_gj_bt_mst_org_id` (`organisasi_id`) USING BTREE,
  KEY `idx_gj_bt_mst_pgw_id` (`pegawai_id`) USING BTREE,
  CONSTRAINT `fk_gj_bt_mst_gj_bt_root_bt_root_id` FOREIGN KEY (`batch_root_id`) REFERENCES `gaji_batch_root` (`id`),
  CONSTRAINT `fk_gj_bt_mst_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_gj_bt_mst_gj_pp_nn_pjk_gj_pp_nn_pjk_id` FOREIGN KEY (`gaji_pendapatan_non_pajak_id`) REFERENCES `gaji_pendapatan_non_pajak` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_master_proses`;
CREATE TABLE `gaji_batch_master_proses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_master_id` bigint(20) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `nilai_formula` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_mst_prs_urut` (`urut`) USING BTREE,
  KEY `idx_gj_bt_mst_prs_kode` (`kode`) USING BTREE,
  KEY `idx_gj_bt_mst_prs_nama` (`nama`) USING BTREE,
  KEY `idx_gj_bt_mst_prs_bt_mst_id` (`batch_master_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_potongan_tkk`;
CREATE TABLE `gaji_batch_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `potongan` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_ptg_tkk_bt_id` (`batch_id`) USING BTREE,
  KEY `idx_gj_bt_ptg_tkk_nipam` (`nipam`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_root`;
CREATE TABLE `gaji_batch_root` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `di_proses_oleh` varchar(255) DEFAULT NULL,
  `di_setujui_oleh` varchar(255) DEFAULT NULL,
  `di_verifikasi_oleh_tahap1` varchar(255) DEFAULT NULL,
  `di_verifikasi_oleh_tahap2` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `jabatan_pemroses` varchar(255) DEFAULT NULL,
  `jabatan_penyetuju` varchar(255) DEFAULT NULL,
  `jabatan_verifikasi_tahap1` varchar(255) DEFAULT NULL,
  `jabatan_verifikasi_tahap2` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `periode` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `tanggal_persetujuan` datetime(6) DEFAULT NULL,
  `tanggal_proses` datetime(6) DEFAULT NULL,
  `tanggal_verifikasi_tahap1` datetime(6) DEFAULT NULL,
  `tanggal_verifikasi_tahap2` datetime(6) DEFAULT NULL,
  `total_pegawai` int(11) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_root_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_gj_bt_root_tanggal_prs` (`tanggal_proses`) USING BTREE,
  KEY `idx_gj_bt_root_tanggal_verifikasi_tahap1` (`tanggal_verifikasi_tahap1`) USING BTREE,
  KEY `idx_gj_bt_root_tanggal_verifikasi_tahap2` (`tanggal_verifikasi_tahap2`) USING BTREE,
  KEY `idx_gj_bt_root_tanggal_persetujuan` (`tanggal_persetujuan`) USING BTREE,
  KEY `idx_gj_bt_root_status` (`status`) USING BTREE,
  KEY `idx_gj_bt_root_periode` (`periode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_root_aud`;
CREATE TABLE `gaji_batch_root_aud` (
  `id` varchar(255) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_bt_root_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_root_error_logs`;
CREATE TABLE `gaji_batch_root_error_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `root_batch_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_root_err_log_nipam` (`nipam`) USING BTREE,
  KEY `idx_gj_bt_root_err_log_nama` (`nama`) USING BTREE,
  KEY `idx_gj_bt_root_err_log_root_bt_id` (`root_batch_id`) USING BTREE,
  CONSTRAINT `fk_gj_bt_root_err_log_gj_bt_root_root_bt_id` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_batch_root_lampiran`;
CREATE TABLE `gaji_batch_root_lampiran` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `jenis_lampiran_gaji` tinyint(4) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `root_batch_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_bt_root_lmp_root_bt_id` (`root_batch_id`) USING BTREE,
  CONSTRAINT `fk_gj_bt_root_lmp_gj_bt_root_root_bt_id` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_komponen`;
CREATE TABLE `gaji_komponen` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `formula` varchar(255) DEFAULT NULL,
  `is_reference` bit(1) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `profil_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_kpn_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_gj_kpn_prf_gj_id` (`profil_gaji_id`) USING BTREE,
  CONSTRAINT `fk_gj_kpn_gj_prf_prf_gj_id` FOREIGN KEY (`profil_gaji_id`) REFERENCES `gaji_profil` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_komponen_aud`;
CREATE TABLE `gaji_komponen_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `is_reference` bit(1) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `profil_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_kpn_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_parameter_setting`;
CREATE TABLE `gaji_parameter_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_prm_stg_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_parameter_setting_aud`;
CREATE TABLE `gaji_parameter_setting_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_prm_stg_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak`;
CREATE TABLE `gaji_pendapatan_non_pajak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_pp_nn_pjk_kode` (`kode`) USING BTREE,
  KEY `idx_gj_pp_nn_pjk_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak_aud`;
CREATE TABLE `gaji_pendapatan_non_pajak_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_pp_nn_pjk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_phdp`;
CREATE TABLE `gaji_phdp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `formula` varchar(255) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_gj_phdp_urut` (`urut`) USING BTREE,
  KEY `idx_gj_phdp_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_phdp_aud`;
CREATE TABLE `gaji_phdp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_phdp_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_potongan_tkk`;
CREATE TABLE `gaji_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nominal` double DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_ptg_tkk_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_gj_ptg_tkk_glg_id` (`golongan_id`) USING BTREE,
  KEY `idx_gj_ptg_tkk_lvl_id` (`level_id`) USING BTREE,
  CONSTRAINT `fk_gj_ptg_tkk_glg_glg_id` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_gj_ptg_tkk_lvl_lvl_id` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_potongan_tkk_aud`;
CREATE TABLE `gaji_potongan_tkk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_ptg_tkk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_profil`;
CREATE TABLE `gaji_profil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_prf_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_profil_aud`;
CREATE TABLE `gaji_profil_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_prf_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_tunjangan`;
CREATE TABLE `gaji_tunjangan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jenis_tunjangan` tinyint(4) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gj_tjg_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_gj_tjg_glg_id` (`golongan_id`) USING BTREE,
  KEY `idx_gj_tjg_lvl_id` (`level_id`) USING BTREE,
  CONSTRAINT `fk_gj_tjg_lvl_lvl_id` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `fk_gj_tjg_glg_glg_id` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `gaji_tunjangan_aud`;
CREATE TABLE `gaji_tunjangan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `jenis_tunjangan` tinyint(4) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_tjg_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `golongan`;
CREATE TABLE `golongan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `golongan` varchar(255) DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_glg_glg` (`golongan`) USING BTREE,
  KEY `idx_glg_pangkat` (`pangkat`) USING BTREE,
  KEY `idx_glg_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `grade`;
CREATE TABLE `grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grade` int(11) DEFAULT NULL,
  `tukin` double DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_grd_grd` (`grade`) USING BTREE,
  KEY `idx_grd_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_grd_lvl_id` (`level_id`) USING BTREE,
  CONSTRAINT `fk_grd_lvl_lvl_id` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `hari_libur`;
CREATE TABLE `hari_libur` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jenis_libur` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_hr_lbr_tanggal` (`tanggal`) USING BTREE,
  KEY `idx_hr_lbr_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jabatan`;
CREATE TABLE `jabatan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_jbt_kode` (`kode`) USING BTREE,
  KEY `idx_jbt_nama` (`nama`) USING BTREE,
  KEY `idx_jbt_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_jbt_lvl_id` (`level_id`) USING BTREE,
  KEY `idx_jbt_org_id` (`organisasi_id`) USING BTREE,
  CONSTRAINT `fk_jbt_lvl_lvl_id` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `fk_jbt_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jenis_keahlian`;
CREATE TABLE `jenis_keahlian` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_jns_ahl_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jenis_kitas`;
CREATE TABLE `jenis_kitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_jns_kitas_nama` (`nama`) USING BTREE,
  KEY `idx_jns_kitas_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jenis_pelatihan`;
CREATE TABLE `jenis_pelatihan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_jns_lth_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jenis_sp`;
CREATE TABLE `jenis_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(10) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_jns_sp_kode` (`kode`) USING BTREE,
  KEY `idx_jns_sp_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `jenjang_pendidikan`;
CREATE TABLE `jenjang_pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_statistik` bit(1) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `seq` int(11) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_jjg_ddk_nama` (`nama`) USING BTREE,
  KEY `idx_jjg_ddk_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `kartu_identitas`;
CREATE TABLE `kartu_identitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `nomor_kartu` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_expired` date DEFAULT NULL,
  `tanggal_terima` date DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `jenis_kitas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_kartu_idn_nik_jns_kitas_id` (`nik`,`jenis_kitas_id`) USING BTREE,
  KEY `idx_kartu_idn_nomor_kartu` (`nomor_kartu`) USING BTREE,
  KEY `idx_kartu_idn_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_kartu_idn_jns_kitas_id` (`jenis_kitas_id`) USING BTREE,
  CONSTRAINT `fk_kartu_idn_bio_nik` FOREIGN KEY (`nik`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_kartu_idn_jns_kitas_jns_kitas_id` FOREIGN KEY (`jenis_kitas_id`) REFERENCES `jenis_kitas` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `kartu_identitas_aud`;
CREATE TABLE `kartu_identitas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nomor_kartu` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_expired` date DEFAULT NULL,
  `tanggal_terima` date DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `jenis_kitas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_kartu_idn_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `keahlian`;
CREATE TABLE `keahlian` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `kualifikasi` tinyint(4) DEFAULT NULL,
  `masa_berlaku` varchar(255) DEFAULT NULL,
  `sertifikasi` bit(1) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_keahlian_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ahl_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_ahl_disetujui` (`disetujui`) USING BTREE,
  KEY `idx_ahl_bio_id` (`biodata_id`) USING BTREE,
  KEY `idx_ahl_jns_ahl_id` (`jenis_keahlian_id`) USING BTREE,
  CONSTRAINT `fk_ahl_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_ahl_jns_ahl_jns_ahl_id` FOREIGN KEY (`jenis_keahlian_id`) REFERENCES `jenis_keahlian` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `keahlian_aud`;
CREATE TABLE `keahlian_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `kualifikasi` tinyint(4) DEFAULT NULL,
  `masa_berlaku` varchar(255) DEFAULT NULL,
  `sertifikasi` bit(1) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_keahlian_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ahl_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `lampiran_profil`;
CREATE TABLE `lampiran_profil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `ref` tinyint(4) NOT NULL,
  `ref_id` bigint(20) NOT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lmp_prf_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_lmp_prf_ref` (`ref`) USING BTREE,
  KEY `idx_lmp_prf_ref_id` (`ref_id`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `lampiran_profil_aud`;
CREATE TABLE `lampiran_profil_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `ref` tinyint(4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_lmp_prf_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `lampiran_sk`;
CREATE TABLE `lampiran_sk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `ref` tinyint(4) NOT NULL,
  `ref_id` bigint(20) NOT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lmp_sk_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_lmp_sk_ref` (`ref`) USING BTREE,
  KEY `idx_lmp_sk_ref_id` (`ref_id`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `lampiran_sk_aud`;
CREATE TABLE `lampiran_sk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `ref` tinyint(4) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_lmp_sk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `level`;
CREATE TABLE `level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lvl_nama` (`nama`) USING BTREE,
  KEY `idx_lvl_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `organisasi`;
CREATE TABLE `organisasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `level_org` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_org_kode` (`kode`) USING BTREE,
  KEY `idx_org_nama` (`nama`) USING BTREE,
  KEY `idx_org_lvl_org` (`level_org`) USING BTREE,
  KEY `idx_org_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pegawai`;
CREATE TABLE `pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `absensi_id` bigint(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `is_askes` bit(1) DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `nipam` varchar(32) NOT NULL,
  `notes` text DEFAULT NULL,
  `phdp` double DEFAULT NULL,
  `ref_sk_capeg_id` bigint(20) DEFAULT NULL,
  `ref_sk_gaji_berkala_id` bigint(20) DEFAULT NULL,
  `ref_sk_gol_id` bigint(20) DEFAULT NULL,
  `ref_sk_jabatan_id` bigint(20) DEFAULT NULL,
  `ref_sk_mutasi_id` bigint(20) DEFAULT NULL,
  `ref_sk_pegawai_id` bigint(20) DEFAULT NULL,
  `status_kerja` tinyint(4) NOT NULL,
  `status_pegawai` tinyint(4) NOT NULL,
  `tanggal_pengangkatan` date DEFAULT NULL,
  `tmt_gaji_berkala` date DEFAULT NULL,
  `tmt_golongan` date DEFAULT NULL,
  `tmt_jabatan` date DEFAULT NULL,
  `tmt_kerja` date DEFAULT NULL,
  `tmt_mutasi` date DEFAULT NULL,
  `tmt_pegawai` date DEFAULT NULL,
  `tmt_pensiun` date DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `rumah_dinas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_pgw_nipam` (`nipam`) USING BTREE,
  KEY `idx_pgw_tmt_pensiun` (`tmt_pensiun`) USING BTREE,
  KEY `idx_pgw_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_pgw_gj_prf_id` (`gaji_profil_id`) USING BTREE,
  KEY `idx_pgw_glg_id` (`golongan_id`) USING BTREE,
  KEY `idx_pgw_grd_id` (`grade_id`) USING BTREE,
  KEY `idx_pgw_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_pgw_gj_pp_nn_pjk_id` (`gaji_pendapatan_non_pajak_id`) USING BTREE,
  KEY `idx_pgw_org_id` (`organisasi_id`) USING BTREE,
  KEY `idx_pgw_pfs_id` (`profesi_id`) USING BTREE,
  KEY `idx_pgw_rmh_dns_id` (`rumah_dinas_id`) USING BTREE,
  KEY `idx_pgw_bio_id` (`biodata_id`) USING BTREE,
  CONSTRAINT `fk_pgw_pfs_pfs_id` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  CONSTRAINT `fk_pgw_glg_glg_id` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_pgw_gj_pp_nn_pjk_gj_pp_nn_pjk_id` FOREIGN KEY (`gaji_pendapatan_non_pajak_id`) REFERENCES `gaji_pendapatan_non_pajak` (`id`),
  CONSTRAINT `fk_pgw_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_pgw_gj_prf_gj_prf_id` FOREIGN KEY (`gaji_profil_id`) REFERENCES `gaji_profil` (`id`),
  CONSTRAINT `fk_pgw_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_pgw_rmh_dns_rmh_dns_id` FOREIGN KEY (`rumah_dinas_id`) REFERENCES `rumah_dinas` (`id`),
  CONSTRAINT `fk_pgw_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_pgw_grd_grd_id` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pegawai_aud`;
CREATE TABLE `pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `absensi_id` bigint(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `is_askes` bit(1) DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `nipam` varchar(32) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `phdp` double DEFAULT NULL,
  `ref_sk_capeg_id` bigint(20) DEFAULT NULL,
  `ref_sk_gaji_berkala_id` bigint(20) DEFAULT NULL,
  `ref_sk_gol_id` bigint(20) DEFAULT NULL,
  `ref_sk_jabatan_id` bigint(20) DEFAULT NULL,
  `ref_sk_mutasi_id` bigint(20) DEFAULT NULL,
  `ref_sk_pegawai_id` bigint(20) DEFAULT NULL,
  `status_kerja` tinyint(4) DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `tanggal_pengangkatan` date DEFAULT NULL,
  `tmt_gaji_berkala` date DEFAULT NULL,
  `tmt_golongan` date DEFAULT NULL,
  `tmt_jabatan` date DEFAULT NULL,
  `tmt_kerja` date DEFAULT NULL,
  `tmt_mutasi` date DEFAULT NULL,
  `tmt_pegawai` date DEFAULT NULL,
  `tmt_pensiun` date DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `rumah_dinas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_pgw_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pelatihan`;
CREATE TABLE `pelatihan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `ikatan_dinas` bit(1) DEFAULT NULL,
  `lembaga` varchar(255) DEFAULT NULL,
  `lulus` bit(1) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_akhir_ikatan` date DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_pelatihan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lth_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_lth_bio_id` (`biodata_id`) USING BTREE,
  KEY `idx_lth_jns_lth_id` (`jenis_pelatihan_id`) USING BTREE,
  CONSTRAINT `fk_lth_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_lth_jns_lth_jns_lth_id` FOREIGN KEY (`jenis_pelatihan_id`) REFERENCES `jenis_pelatihan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pelatihan_aud`;
CREATE TABLE `pelatihan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `ikatan_dinas` bit(1) DEFAULT NULL,
  `lembaga` varchar(255) DEFAULT NULL,
  `lulus` bit(1) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_akhir_ikatan` date DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_pelatihan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_lth_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pendidikan`;
CREATE TABLE `pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `disetujui` tinyint(1) DEFAULT 0,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `gelar_belakang` varchar(255) DEFAULT NULL,
  `gelar_depan` varchar(255) DEFAULT NULL,
  `gpa` double DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `is_latest` tinyint(1) DEFAULT 0,
  `is_lulus` bit(1) DEFAULT NULL,
  `jurusan` varchar(255) DEFAULT NULL,
  `kota` varchar(255) DEFAULT NULL,
  `tahun_lulus` int(11) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenjang_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_ddk_bio_id_jjg_id_tahun_masuk` (`biodata_id`,`jenjang_id`,`tahun_masuk`) USING BTREE,
  KEY `idx_ddk_jjg_id` (`jenjang_id`) USING BTREE,
  KEY `idx_ddk_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_ddk_is_latest` (`is_latest`) USING BTREE,
  KEY `idx_ddk_disetujui_oleh` (`disetujui_oleh`) USING BTREE,
  CONSTRAINT `fk_ddk_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_ddk_jjg_ddk_jjg_id` FOREIGN KEY (`jenjang_id`) REFERENCES `jenjang_pendidikan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pendidikan_aud`;
CREATE TABLE `pendidikan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` tinyint(1) DEFAULT 0,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `gelar_belakang` varchar(255) DEFAULT NULL,
  `gelar_depan` varchar(255) DEFAULT NULL,
  `gpa` double DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `is_latest` tinyint(1) DEFAULT 0,
  `is_lulus` bit(1) DEFAULT NULL,
  `jurusan` varchar(255) DEFAULT NULL,
  `kota` varchar(255) DEFAULT NULL,
  `tahun_lulus` int(11) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenjang_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_ddk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pengalaman_kerja`;
CREATE TABLE `pengalaman_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `disetujui` tinyint(1) DEFAULT 0,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `lokasi` varchar(255) DEFAULT NULL,
  `nama_perusahaan` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tahun_keluar` int(11) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `type_perusahaan` varchar(255) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_plm_krj_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_plm_krj_bio_id` (`biodata_id`) USING BTREE,
  CONSTRAINT `fk_plm_krj_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pengalaman_kerja_aud`;
CREATE TABLE `pengalaman_kerja_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `disetujui` tinyint(1) DEFAULT 0,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `lokasi` varchar(255) DEFAULT NULL,
  `nama_perusahaan` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tahun_keluar` int(11) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tanggal_disetujui` datetime(6) DEFAULT NULL,
  `tanggal_pengajuan` datetime(6) DEFAULT NULL,
  `type_perusahaan` varchar(255) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_plm_krj_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `pref_role`;
CREATE TABLE `pref_role` (
  `id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `profesi`;
CREATE TABLE `profesi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `detail` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `resiko` varchar(255) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pfs_nama` (`nama`) USING BTREE,
  KEY `idx_pfs_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_pfs_grd_id` (`grade_id`) USING BTREE,
  KEY `idx_pfs_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_pfs_lvl_id` (`level_id`) USING BTREE,
  KEY `idx_pfs_org_id` (`organisasi_id`) USING BTREE,
  CONSTRAINT `fk_pfs_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_pfs_grd_grd_id` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`),
  CONSTRAINT `fk_pfs_lvl_lvl_id` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `fk_pfs_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `profil_keluarga`;
CREATE TABLE `profil_keluarga` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `agama` tinyint(4) DEFAULT NULL,
  `hubungan_keluarga` tinyint(4) DEFAULT NULL,
  `jenis_kelamin` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status_kawin` bit(1) DEFAULT NULL,
  `status_pendidikan` tinyint(4) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tanggungan` tinyint(1) DEFAULT 0,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_prf_klg_bio_id_version_nama_tanggal_lahir_is_deleted` (`biodata_id`,`version`,`nama`,`tanggal_lahir`,`is_deleted`) USING BTREE,
  KEY `idx_prf_klg_ddk_id` (`pendidikan_id`) USING BTREE,
  KEY `idx_prf_klg_nik` (`nik`) USING BTREE,
  KEY `idx_prf_klg_nama` (`nama`) USING BTREE,
  KEY `idx_prf_klg_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_prf_klg_tanggungan` (`tanggungan`) USING BTREE,
  CONSTRAINT `fk_prf_klg_jjg_ddk_ddk_id` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`),
  CONSTRAINT `fk_prf_klg_bio_bio_id` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `profil_keluarga_aud`;
CREATE TABLE `profil_keluarga_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `agama` tinyint(4) DEFAULT NULL,
  `hubungan_keluarga` tinyint(4) DEFAULT NULL,
  `jenis_kelamin` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `status_kawin` bit(1) DEFAULT NULL,
  `status_pendidikan` tinyint(4) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tanggungan` tinyint(1) DEFAULT 0,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_prf_klg_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `profil_update`;
CREATE TABLE `profil_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `req_date` timestamp NULL DEFAULT current_timestamp(),
  `table_name` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_prf_upd_nipam` (`nipam`) USING BTREE,
  KEY `idx_prf_upd_nama` (`nama`) USING BTREE,
  KEY `idx_prf_upd_apv_status` (`approval_status`) USING BTREE,
  KEY `idx_prf_upd_req_date` (`req_date`) USING BTREE,
  `action_type` tinyint(4) DEFAULT NULL,
  `data_description` varchar(255) DEFAULT NULL,
  `rev_id` bigint(20) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `approval_date` datetime(6) DEFAULT NULL,
  `approval_pic` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `revinfo`;
CREATE TABLE `revinfo` (
  `rev` int(11) NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_cuti`;
CREATE TABLE `riwayat_cuti` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_rwt_ct_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_ct_pgw_id` (`pegawai_id`) USING BTREE,
  CONSTRAINT `fk_rwt_ct_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_cuti_aud`;
CREATE TABLE `riwayat_cuti_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_ct_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_keluar`;
CREATE TABLE `riwayat_keluar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `disetujui_oleh_nama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_jabatan_penyetuju` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nama_organisasi_penyetuju` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_permohonan` date DEFAULT NULL,
  `tanggal_persetujuan` date DEFAULT NULL,
  `disetujui_oleh` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `penyetuju_jabatan_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `penyetuju_organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_rwt_klr_tanggal_permohonan` (`tanggal_permohonan`) USING BTREE,
  KEY `idx_rwt_klr_tanggal_persetujuan` (`tanggal_persetujuan`) USING BTREE,
  KEY `idx_rwt_klr_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_klr_disetujui_oleh` (`disetujui_oleh`) USING BTREE,
  KEY `idx_rwt_klr_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_rwt_klr_penyetuju_jbt_id` (`penyetuju_jabatan_id`) USING BTREE,
  KEY `idx_rwt_klr_org_id` (`organisasi_id`) USING BTREE,
  KEY `idx_rwt_klr_penyetuju_org_id` (`penyetuju_organisasi_id`) USING BTREE,
  KEY `idx_rwt_klr_pgw_id` (`pegawai_id`) USING BTREE,
  CONSTRAINT `fk_rwt_klr_jbt_penyetuju_jbt_id` FOREIGN KEY (`penyetuju_jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_rwt_klr_pgw_disetujui_oleh` FOREIGN KEY (`disetujui_oleh`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_rwt_klr_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_rwt_klr_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_rwt_klr_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_rwt_klr_org_penyetuju_org_id` FOREIGN KEY (`penyetuju_organisasi_id`) REFERENCES `organisasi` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_kontrak`;
CREATE TABLE `riwayat_kontrak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_latest` bit(1) DEFAULT NULL,
  `jenis_kontrak` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_kontrak` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_rwt_ktrk_pgw_id_nomor_ktrk` (`pegawai_id`,`nomor_kontrak`) USING BTREE,
  KEY `idx_rwt_ktrk_nomor_ktrk` (`nomor_kontrak`) USING BTREE,
  KEY `idx_rwt_ktrk_tanggal_mulai` (`tanggal_mulai`) USING BTREE,
  KEY `idx_rwt_ktrk_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_ktrk_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_rwt_ktrk_org_id` (`organisasi_id`) USING BTREE,
  CONSTRAINT `fk_rwt_ktrk_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_rwt_ktrk_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_rwt_ktrk_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_kontrak_aud`;
CREATE TABLE `riwayat_kontrak_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `is_latest` bit(1) DEFAULT NULL,
  `jenis_kontrak` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_kontrak` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_ktrk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_mutasi`;
CREATE TABLE `riwayat_mutasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jenis_mutasi` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `nama_golongan_lama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_jabatan_lama` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nama_organisasi_lama` varchar(255) DEFAULT NULL,
  `nama_profesi` varchar(255) DEFAULT NULL,
  `nama_profesi_lama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `golongan_lama_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_lama_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `organisasi_lama_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `profesi_lama_id` bigint(20) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_rwt_mts_pgw_id_rwt_sk_id` (`pegawai_id`,`riwayat_sk_id`) USING BTREE,
  KEY `idx_rwt_mts_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_mts_rwt_sk_id` (`riwayat_sk_id`) USING BTREE,
  CONSTRAINT `fk_rwt_mts_rwt_sk_rwt_sk_id` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`),
  CONSTRAINT `fk_rwt_mts_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_mutasi_aud`;
CREATE TABLE `riwayat_mutasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `jenis_mutasi` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `nama_golongan_lama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_jabatan_lama` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nama_organisasi_lama` varchar(255) DEFAULT NULL,
  `nama_profesi` varchar(255) DEFAULT NULL,
  `nama_profesi_lama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `golongan_lama_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_lama_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `organisasi_lama_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `profesi_lama_id` bigint(20) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_mts_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_sk`;
CREATE TABLE `riwayat_sk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gaji_pokok` double DEFAULT NULL,
  `jenis_sk` tinyint(4) DEFAULT NULL,
  `kenaikan_berikutnya` date DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `mkgb_bulan` int(11) DEFAULT NULL,
  `mkgb_tahun` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `update_master` bit(1) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_rwt_sk_nomor_sk` (`nomor_sk`) USING BTREE,
  KEY `idx_rwt_sk_nipam` (`nipam`) USING BTREE,
  KEY `idx_rwt_sk_nama` (`nama`) USING BTREE,
  KEY `idx_rwt_sk_tanggal_sk` (`tanggal_sk`) USING BTREE,
  KEY `idx_rwt_sk_mkg_tahun` (`mkg_tahun`) USING BTREE,
  KEY `idx_rwt_sk_mkgb_tahun` (`mkgb_tahun`) USING BTREE,
  KEY `idx_rwt_sk_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_sk_glg_id` (`golongan_id`) USING BTREE,
  KEY `idx_rwt_sk_pgw_id` (`pegawai_id`) USING BTREE,
  CONSTRAINT `fk_rwt_sk_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_rwt_sk_glg_glg_id` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_sk_aud`;
CREATE TABLE `riwayat_sk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `jenis_sk` tinyint(4) DEFAULT NULL,
  `kenaikan_berikutnya` date DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `mkgb_bulan` int(11) DEFAULT NULL,
  `mkgb_tahun` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `update_master` bit(1) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_sk_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_sp`;
CREATE TABLE `riwayat_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `jabatan_penanda_tangan` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sp` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `penanda_tangan` varchar(255) DEFAULT NULL,
  `sanksi_notes` text DEFAULT NULL,
  `tanggal_eksekusi_sanksi` date DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `tanggal_sp` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `sanksi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_rwt_sp_nomor_sp` (`nomor_sp`) USING BTREE,
  KEY `idx_rwt_sp_tanggal_sp` (`tanggal_sp`) USING BTREE,
  KEY `idx_rwt_sp_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_rwt_sp_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_rwt_sp_jns_sp_id` (`jenis_sp_id`) USING BTREE,
  KEY `idx_rwt_sp_org_id` (`organisasi_id`) USING BTREE,
  KEY `idx_rwt_sp_pgw_id` (`pegawai_id`) USING BTREE,
  KEY `idx_rwt_sp_snk_id` (`sanksi_id`) USING BTREE,
  CONSTRAINT `fk_rwt_sp_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_rwt_sp_jns_sp_jns_sp_id` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`),
  CONSTRAINT `fk_rwt_sp_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_rwt_sp_snk_sp_snk_id` FOREIGN KEY (`sanksi_id`) REFERENCES `sanksi_sp` (`id`),
  CONSTRAINT `fk_rwt_sp_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_sp_aud`;
CREATE TABLE `riwayat_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `jabatan_penanda_tangan` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sp` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `penanda_tangan` varchar(255) DEFAULT NULL,
  `sanksi_notes` text DEFAULT NULL,
  `tanggal_eksekusi_sanksi` date DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `tanggal_sp` date DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `sanksi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_sp_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_terminasi`;
CREATE TABLE `riwayat_terminasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `masa_kerja` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tahun_terminasi` int(11) DEFAULT NULL,
  `tanggal_terminasi` date DEFAULT NULL,
  `alasan_terminasi_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_rwt_trm_pgw_id` (`pegawai_id`) USING BTREE,
  KEY `idx_rwt_trm_nipam` (`nipam`) USING BTREE,
  KEY `idx_rwt_trm_nama` (`nama`) USING BTREE,
  KEY `idx_rwt_trm_nomor_sk` (`nomor_sk`) USING BTREE,
  KEY `idx_rwt_trm_tanggal_trm` (`tanggal_terminasi`) USING BTREE,
  KEY `idx_rwt_trm_als_trm_id` (`alasan_terminasi_id`) USING BTREE,
  KEY `idx_rwt_trm_rwt_sk_id` (`riwayat_sk_id`) USING BTREE,
  KEY `idx_rwt_trm_glg_id` (`golongan_id`) USING BTREE,
  KEY `idx_rwt_trm_jbt_id` (`jabatan_id`) USING BTREE,
  KEY `idx_rwt_trm_org_id` (`organisasi_id`) USING BTREE,
  CONSTRAINT `fk_rwt_trm_rwt_sk_rwt_sk_id` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`),
  CONSTRAINT `fk_rwt_trm_org_org_id` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_rwt_trm_pgw_pgw_id` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_rwt_trm_als_brh_als_trm_id` FOREIGN KEY (`alasan_terminasi_id`) REFERENCES `alasan_berhenti` (`id`),
  CONSTRAINT `fk_rwt_trm_jbt_jbt_id` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_rwt_trm_glg_glg_id` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `riwayat_terminasi_aud`;
CREATE TABLE `riwayat_terminasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `masa_kerja` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tahun_terminasi` int(11) DEFAULT NULL,
  `tanggal_terminasi` date DEFAULT NULL,
  `alasan_terminasi_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_rwt_trm_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `rumah_dinas`;
CREATE TABLE `rumah_dinas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_rmh_dns_nama` (`nama`) USING BTREE,
  KEY `idx_rmh_dns_is_deleted` (`is_deleted`) USING BTREE,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `sanksi_sp`;
CREATE TABLE `sanksi_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_pending_gaji` bit(1) DEFAULT NULL,
  `is_pending_pangkat` bit(1) DEFAULT NULL,
  `is_suspension` bit(1) DEFAULT NULL,
  `is_terminate_dh` bit(1) DEFAULT NULL,
  `is_terminate_th` bit(1) DEFAULT NULL,
  `is_turun_jabatan` bit(1) DEFAULT NULL,
  `is_turun_pangkat` bit(1) DEFAULT NULL,
  `jml_pot_tkk` int(11) DEFAULT NULL,
  `keterangan` text DEFAULT NULL,
  `kode` varchar(10) NOT NULL,
  `pot_tkk` bit(1) DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_snk_sp_kode` (`kode`) USING BTREE,
  KEY `idx_snk_sp_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_snk_sp_jns_sp_id` (`jenis_sp_id`) USING BTREE,
  CONSTRAINT `fk_snk_sp_jns_sp_jns_sp_id` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `statistik_pegawai`;
CREATE TABLE `statistik_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `adm` int(11) DEFAULT NULL,
  `bulan` int(11) DEFAULT NULL,
  `capeg` int(11) DEFAULT NULL,
  `golongan_a` int(11) DEFAULT NULL,
  `golongan_b` int(11) DEFAULT NULL,
  `golongan_c` int(11) DEFAULT NULL,
  `golongan_d` int(11) DEFAULT NULL,
  `honorer` int(11) DEFAULT NULL,
  `kontrak` int(11) DEFAULT NULL,
  `non_golongan` int(11) DEFAULT NULL,
  `pelayanan` int(11) DEFAULT NULL,
  `pendidikan` varchar(255) DEFAULT NULL,
  `pria` int(11) DEFAULT NULL,
  `seq` int(11) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `teknik` int(11) DEFAULT NULL,
  `tetap` int(11) DEFAULT NULL,
  `wanita` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_stt_pgw_bulan_tahun_ddk` (`bulan`,`tahun`,`pendidikan`) USING BTREE,
  KEY `idx_stt_pgw_bulan` (`bulan`) USING BTREE,
  KEY `idx_stt_pgw_tahun` (`tahun`) USING BTREE,
  KEY `idx_stt_pgw_ddk` (`pendidikan`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;

-- 89 tables created (12 orphan _AUD skipped)

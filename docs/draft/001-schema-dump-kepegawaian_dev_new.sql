-- NOTE: This is a GENERATED file from mysqldump --no-data.
-- It is a WORKING DRAFT for the baseline-rebuild epic (kepegawaian-odb).
-- DO NOT commit this to db/migration/.
-- Generated: 2026-07-23
-- Source: kepegawaian_dev_new@192.168.230.84:3307
-- See: docs/CLAIM-ORDER-baseline-rebuild.md

/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.14-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.230.84    Database: kepegawaian_dev_new
-- ------------------------------------------------------
-- Server version	11.1.5-MariaDB-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alasan_berhenti`
--

DROP TABLE IF EXISTS `alasan_berhenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `alasan_berhenti` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `notes` text DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alasan_berhenti_aud`
--

DROP TABLE IF EXISTS `alasan_berhenti_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `alasan_berhenti_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_alasan_berhenti_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alat_kerja`
--

DROP TABLE IF EXISTS `alat_kerja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `alat_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profesi_id` bigint(20) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_alat_kerja_nama` (`nama`),
  KEY `idx_alat_kerja_profesi` (`profesi_id`),
  KEY `idx_alat_kerja_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_alat_kerja_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apd`
--

DROP TABLE IF EXISTS `apd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `apd` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `profesi_id` bigint(20) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_apd_nama` (`nama`),
  KEY `idx_apd_profesi` (`profesi_id`),
  KEY `idx_apd_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_apd_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `biodata`
--

DROP TABLE IF EXISTS `biodata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `biodata` (
  `nik` varchar(255) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_kelamin` int(11) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `telp` varchar(255) DEFAULT NULL,
  `agama` int(11) DEFAULT NULL,
  `ibu_kandung` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  `golongan_darah` enum('A','AB','B','O') DEFAULT NULL,
  `status_kawin` int(11) DEFAULT NULL,
  `foto_profil` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_pegawai` bit(1) NOT NULL DEFAULT b'0',
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  PRIMARY KEY (`nik`),
  KEY `idx_biodata_pendidikan` (`pendidikan_id`),
  CONSTRAINT `fk_biodata_pendidikan` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `biodata_aud`
--

DROP TABLE IF EXISTS `biodata_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `biodata_aud` (
  `nik` varchar(255) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_kelamin` int(11) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  `telp` varchar(255) DEFAULT NULL,
  `agama` int(11) DEFAULT NULL,
  `ibu_kandung` varchar(255) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  `golongan_darah` enum('A','AB','B','O') DEFAULT NULL,
  `status_kawin` int(11) DEFAULT NULL,
  `foto_profil` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_pegawai` bit(1) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`nik`,`REV`),
  KEY `idx_biodata_aud_rev` (`REV`),
  CONSTRAINT `fk_biodata_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval`
--

DROP TABLE IF EXISTS `cuti_approval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  `cuti_pegawai_id` bigint(20) NOT NULL,
  `approver_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cuti_approval_cuti_pegawai` (`cuti_pegawai_id`),
  KEY `idx_cuti_approval_approver` (`approver_id`),
  KEY `idx_cuti_approval_jabatan` (`jabatan_id`),
  CONSTRAINT `fk_cuti_approval_approver` FOREIGN KEY (`approver_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_cuti_approval_cuti_pegawai` FOREIGN KEY (`cuti_pegawai_id`) REFERENCES `cuti_pegawai` (`id`),
  CONSTRAINT `fk_cuti_approval_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval_aud`
--

DROP TABLE IF EXISTS `cuti_approval_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `approver_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_cuti_approval_aud_rev` (`REV`),
  CONSTRAINT `fk_cuti_approval_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval_chain`
--

DROP TABLE IF EXISTS `cuti_approval_chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval_chain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_nama` varchar(255) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` int(11) DEFAULT NULL,
  `read_write_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cuti_approval_chain_read_write` (`read_write_status`),
  KEY `idx_cuti_approval_chain_status` (`approval_status`),
  KEY `fk_cuti_approval_chain_ref` (`ref_cuti_id`),
  CONSTRAINT `fk_cuti_approval_chain_ref` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4262 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval_chain_aud`
--

DROP TABLE IF EXISTS `cuti_approval_chain_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval_chain_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_nama` varchar(255) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` int(11) DEFAULT NULL,
  `read_write_status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_cuti_approval_chain_aud_rev` (`REV`),
  CONSTRAINT `fk_cuti_approval_chain_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_jenis`
--

DROP TABLE IF EXISTS `cuti_jenis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_jenis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) NOT NULL,
  `max_hari` int(11) DEFAULT NULL,
  `potong_kuota_tahunan` bit(1) NOT NULL DEFAULT b'0',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cuti_jenis_parent` (`parent_id`),
  CONSTRAINT `fk_cuti_jenis_parent` FOREIGN KEY (`parent_id`) REFERENCES `cuti_jenis` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_jenis_aud`
--

DROP TABLE IF EXISTS `cuti_jenis_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_jenis_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `max_hari` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `potong_kuota_tahunan` bit(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_cuti_jenis_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_klaim_detail`
--

DROP TABLE IF EXISTS `cuti_klaim_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_klaim_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cuti_klaim_detail_ref_cuti` (`ref_cuti_id`),
  CONSTRAINT `fk_cuti_klaim_detail_ref_cuti` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=435 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_kuota`
--

DROP TABLE IF EXISTS `cuti_kuota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_kuota` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  `pegawai_id` bigint(20) NOT NULL,
  `tahun` int(11) DEFAULT NULL,
  `kuota` int(11) DEFAULT 0,
  `kuota_terpakai` int(11) DEFAULT 0,
  `kuota_tambahan` int(11) DEFAULT 0,
  `sisa_kuota` int(11) DEFAULT 0,
  `expired` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cuti_kuota_pegawai` (`pegawai_id`),
  KEY `is_deleted_idx` (`is_deleted`),
  CONSTRAINT `fk_cuti_kuota_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_kuota_aud`
--

DROP TABLE IF EXISTS `cuti_kuota_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_kuota_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `kuota_terpakai` int(11) DEFAULT NULL,
  `kuota_tambahan` int(11) DEFAULT NULL,
  `sisa_kuota` int(11) DEFAULT NULL,
  `expired` date DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_cuti_kuota_aud_rev` (`REV`),
  CONSTRAINT `fk_cuti_kuota_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_pegawai`
--

DROP TABLE IF EXISTS `cuti_pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  `pegawai_id` bigint(20) NOT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pangkat_golongan` varchar(255) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_pengajuan_cuti` int(11) DEFAULT NULL,
  `cuti_jenis_id` bigint(20) DEFAULT NULL,
  `cuti_penambah` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  `alamat_cuti` varchar(255) DEFAULT NULL,
  `telp_cuti` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cuti_pegawai_pegawai` (`pegawai_id`),
  KEY `idx_cuti_pegawai_cuti_jenis` (`cuti_jenis_id`),
  KEY `idx_cuti_pegawai_organisasi` (`organisasi_id`),
  KEY `idx_cuti_pegawai_jabatan` (`jabatan_id`),
  KEY `is_deleted_idx` (`is_deleted`),
  CONSTRAINT `fk_cuti_pegawai_cuti_jenis` FOREIGN KEY (`cuti_jenis_id`) REFERENCES `cuti_jenis` (`id`),
  CONSTRAINT `fk_cuti_pegawai_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_cuti_pegawai_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_cuti_pegawai_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_pegawai_aud`
--

DROP TABLE IF EXISTS `cuti_pegawai_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pangkat_golongan` varchar(255) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jenis_pengajuan_cuti` int(11) DEFAULT NULL,
  `cuti_jenis_id` bigint(20) DEFAULT NULL,
  `cuti_penambah` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  `alamat_cuti` varchar(255) DEFAULT NULL,
  `telp_cuti` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_cuti_pegawai_aud_rev` (`REV`),
  CONSTRAINT `fk_cuti_pegawai_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dasar_gaji`
--

DROP TABLE IF EXISTS `dasar_gaji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deskripsi` varchar(255) DEFAULT NULL,
  `tanggal_awal` date DEFAULT NULL,
  `tanggal_akhir` date DEFAULT NULL,
  `aktif` bit(1) NOT NULL DEFAULT b'0',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dasar_gaji_aud`
--

DROP TABLE IF EXISTS `dasar_gaji_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `tanggal_awal` date DEFAULT NULL,
  `tanggal_akhir` date DEFAULT NULL,
  `aktif` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_dasar_gaji_aud_rev` (`REV`),
  CONSTRAINT `fk_dasar_gaji_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detail_dasar_gaji`
--

DROP TABLE IF EXISTS `detail_dasar_gaji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail_dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dasar_gaji_id` bigint(20) NOT NULL,
  `mkg` int(11) DEFAULT NULL,
  `golongan_kode` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_detail_dasar_gaji_dasar_gaji` (`dasar_gaji_id`),
  CONSTRAINT `fk_detail_dasar_gaji_dasar_gaji` FOREIGN KEY (`dasar_gaji_id`) REFERENCES `dasar_gaji` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=546 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detail_dasar_gaji_aud`
--

DROP TABLE IF EXISTS `detail_dasar_gaji_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail_dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `dasar_gaji_id` bigint(20) DEFAULT NULL,
  `mkg` int(11) DEFAULT NULL,
  `golongan_kode` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_detail_dasar_gaji_aud_rev` (`REV`),
  CONSTRAINT `fk_detail_dasar_gaji_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_master`
--

DROP TABLE IF EXISTS `gaji_batch_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_master` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_root_id` varchar(64) NOT NULL,
  `periode` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `golongan` varchar(255) DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  `status_pegawai` int(11) DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `kode_pajak` varchar(255) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `phdp` double DEFAULT NULL,
  `status_kawin` int(11) DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `jml_jiwa` int(11) DEFAULT NULL,
  `penghasilan_kotor` double DEFAULT NULL,
  `total_potongan` double DEFAULT NULL,
  `total_add_tambahan` double DEFAULT NULL,
  `total_add_potongan` double DEFAULT NULL,
  `penghasilan_bersih` double DEFAULT NULL,
  `penghasilan_bersih2` double DEFAULT NULL,
  `pembulatan` double DEFAULT NULL,
  `pembulatan2` double DEFAULT NULL,
  `penghasilan_bersih_final` double DEFAULT NULL,
  `penghasilan_bersih_final2` double DEFAULT NULL,
  `pajak` double DEFAULT NULL,
  `is_different` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_master_root` (`batch_root_id`),
  KEY `idx_gaji_batch_master_periode` (`periode`),
  KEY `idx_gaji_batch_master_pegawai` (`pegawai_id`),
  KEY `idx_gaji_batch_master_nipam` (`nipam`),
  KEY `idx_gaji_batch_master_nama` (`nama`),
  CONSTRAINT `fk_gaji_batch_master_root` FOREIGN KEY (`batch_root_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_master_proses`
--

DROP TABLE IF EXISTS `gaji_batch_master_proses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_master_proses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_master_id` bigint(20) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `formula` text DEFAULT NULL,
  `nilai_formula` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_master_proses_master` (`batch_master_id`),
  CONSTRAINT `fk_gaji_batch_master_proses_master` FOREIGN KEY (`batch_master_id`) REFERENCES `gaji_batch_master` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_potongan_tkk`
--

DROP TABLE IF EXISTS `gaji_batch_potongan_tkk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(64) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `potongan` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_potongan_tkk_batch` (`batch_id`),
  KEY `idx_gaji_batch_potongan_tkk_nipam` (`nipam`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root`
--

DROP TABLE IF EXISTS `gaji_batch_root`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root` (
  `id` varchar(64) NOT NULL,
  `periode` varchar(255) DEFAULT NULL,
  `status` varchar(32) NOT NULL,
  `total_pegawai` int(11) DEFAULT NULL,
  `tanggal_proses` datetime DEFAULT NULL,
  `di_proses_oleh` varchar(255) DEFAULT NULL,
  `jabatan_pemroses` varchar(255) DEFAULT NULL,
  `tanggal_verifikasi_tahap1` datetime DEFAULT NULL,
  `di_verifikasi_oleh_tahap1` varchar(255) DEFAULT NULL,
  `jabatan_verifikasi_tahap1` varchar(255) DEFAULT NULL,
  `tanggal_verifikasi_tahap2` datetime DEFAULT NULL,
  `di_verifikasi_oleh_tahap2` varchar(255) DEFAULT NULL,
  `jabatan_verifikasi_tahap2` varchar(255) DEFAULT NULL,
  `tanggal_persetujuan` datetime DEFAULT NULL,
  `di_setujui_oleh` varchar(255) DEFAULT NULL,
  `jabatan_penyetuju` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_root_is_deleted` (`is_deleted`),
  KEY `idx_gaji_batch_root_status` (`status`),
  KEY `idx_gaji_batch_root_periode` (`periode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_aud`
--

DROP TABLE IF EXISTS `gaji_batch_root_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_aud` (
  `id` varchar(64) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_batch_root_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_batch_root_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_error_logs`
--

DROP TABLE IF EXISTS `gaji_batch_root_error_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_error_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `root_batch_id` varchar(64) NOT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_root_error_logs_root` (`root_batch_id`),
  KEY `idx_gaji_batch_root_error_logs_nipam` (`nipam`),
  KEY `idx_gaji_batch_root_error_logs_nama` (`nama`),
  CONSTRAINT `fk_gaji_batch_root_error_logs_root` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_lampiran`
--

DROP TABLE IF EXISTS `gaji_batch_root_lampiran`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_lampiran` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `root_batch_id` varchar(64) NOT NULL,
  `jenis_lampiran_gaji` int(11) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_gaji_batch_root_lampiran_root` (`root_batch_id`),
  CONSTRAINT `fk_gaji_batch_root_lampiran_root` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_komponen`
--

DROP TABLE IF EXISTS `gaji_komponen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_komponen` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `urut` int(11) DEFAULT NULL,
  `profil_gaji_id` bigint(20) NOT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `is_reference` bit(1) NOT NULL DEFAULT b'0',
  `formula` text DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_gaji_komponen_profil` (`profil_gaji_id`),
  CONSTRAINT `fk_gaji_komponen_profil` FOREIGN KEY (`profil_gaji_id`) REFERENCES `gaji_profil` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_komponen_aud`
--

DROP TABLE IF EXISTS `gaji_komponen_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_komponen_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `profil_gaji_id` bigint(20) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `is_reference` bit(1) DEFAULT NULL,
  `formula` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_komponen_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_komponen_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_parameter_setting`
--

DROP TABLE IF EXISTS `gaji_parameter_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_parameter_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_parameter_setting_aud`
--

DROP TABLE IF EXISTS `gaji_parameter_setting_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_parameter_setting_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_parameter_setting_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_parameter_setting_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_pendapatan_non_pajak`
--

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_pendapatan_non_pajak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_pendapatan_non_pajak_aud`
--

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_pendapatan_non_pajak_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_pendapatan_non_pajak_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_pendapatan_non_pajak_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_phdp`
--

DROP TABLE IF EXISTS `gaji_phdp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_phdp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  `urut` int(11) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `formula` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gaji_phdp_urut` (`urut`),
  KEY `idx_gaji_phdp_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_phdp_aud`
--

DROP TABLE IF EXISTS `gaji_phdp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_phdp_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `formula` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_phdp_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_phdp_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_potongan_tkk`
--

DROP TABLE IF EXISTS `gaji_potongan_tkk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_gaji_potongan_tkk_level` (`level_id`),
  KEY `fk_gaji_potongan_tkk_golongan` (`golongan_id`),
  CONSTRAINT `fk_gaji_potongan_tkk_golongan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_gaji_potongan_tkk_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_potongan_tkk_aud`
--

DROP TABLE IF EXISTS `gaji_potongan_tkk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_potongan_tkk_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_potongan_tkk_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_potongan_tkk_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_profil`
--

DROP TABLE IF EXISTS `gaji_profil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_profil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_profil_aud`
--

DROP TABLE IF EXISTS `gaji_profil_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_profil_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_profil_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_profil_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_tunjangan`
--

DROP TABLE IF EXISTS `gaji_tunjangan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_tunjangan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jenis_tunjangan` tinyint(4) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_gaji_tunjangan_level` (`level_id`),
  KEY `fk_gaji_tunjangan_golongan` (`golongan_id`),
  CONSTRAINT `fk_gaji_tunjangan_golongan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_gaji_tunjangan_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_tunjangan_aud`
--

DROP TABLE IF EXISTS `gaji_tunjangan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_tunjangan_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `jenis_tunjangan` tinyint(4) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_gaji_tunjangan_aud_rev` (`REV`),
  CONSTRAINT `fk_gaji_tunjangan_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `golongan`
--

DROP TABLE IF EXISTS `golongan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `golongan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `golongan` varchar(255) NOT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_golongan_golongan` (`golongan`),
  KEY `idx_golongan_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `golongan_aud`
--

DROP TABLE IF EXISTS `golongan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `golongan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `golongan` varchar(255) DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_golongan_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grade`
--

DROP TABLE IF EXISTS `grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level_id` bigint(20) DEFAULT NULL,
  `grade` int(11) NOT NULL,
  `tukin` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_grade_grade` (`grade`),
  KEY `idx_grade_is_deleted` (`is_deleted`),
  KEY `idx_grade_level` (`level_id`),
  CONSTRAINT `fk_grade_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grade_aud`
--

DROP TABLE IF EXISTS `grade_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `tukin` double DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_grade_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hari_libur`
--

DROP TABLE IF EXISTS `hari_libur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `hari_libur` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `version` bigint(20) DEFAULT 1,
  `tanggal` date DEFAULT NULL,
  `jenis_libur` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_tanggal_idx` (`tanggal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hari_libur_aud`
--

DROP TABLE IF EXISTS `hari_libur_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `hari_libur_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `jenis_libur` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_hari_libur_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jabatan`
--

DROP TABLE IF EXISTS `jabatan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jabatan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(50) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_jabatan_kode` (`kode`),
  KEY `idx_jabatan_nama` (`nama`),
  KEY `idx_jabatan_parent` (`parent_id`),
  KEY `idx_jabatan_organisasi` (`organisasi_id`),
  KEY `idx_jabatan_level` (`level_id`),
  KEY `idx_jabatan_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_jabatan_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_jabatan_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_jabatan_parent` FOREIGN KEY (`parent_id`) REFERENCES `jabatan` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jabatan_aud`
--

DROP TABLE IF EXISTS `jabatan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jabatan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_jabatan_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_keahlian`
--

DROP TABLE IF EXISTS `jenis_keahlian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_keahlian` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_kitas`
--

DROP TABLE IF EXISTS `jenis_kitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_kitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_kitas_aud`
--

DROP TABLE IF EXISTS `jenis_kitas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_kitas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_jenis_kitas_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_pelatihan`
--

DROP TABLE IF EXISTS `jenis_pelatihan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_pelatihan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_sp`
--

DROP TABLE IF EXISTS `jenis_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(10) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_jenis_sp_kode` (`kode`),
  KEY `idx_jenis_sp_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_sp_aud`
--

DROP TABLE IF EXISTS `jenis_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(10) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_jenis_sp_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenjang_pendidikan`
--

DROP TABLE IF EXISTS `jenjang_pendidikan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenjang_pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `seq` int(11) DEFAULT NULL,
  `is_statistik` bit(1) NOT NULL DEFAULT b'0',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kartu_identitas`
--

DROP TABLE IF EXISTS `kartu_identitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `kartu_identitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `jenis_kitas_id` bigint(20) NOT NULL,
  `nomor_kartu` varchar(50) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `tanggal_berlaku` date DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_kartu_identitas_biodata` (`biodata_id`),
  KEY `idx_kartu_identitas_jenis` (`jenis_kitas_id`),
  CONSTRAINT `fk_kartu_identitas_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_kartu_identitas_jenis_kitas` FOREIGN KEY (`jenis_kitas_id`) REFERENCES `jenis_kitas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1878 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kartu_identitas_aud`
--

DROP TABLE IF EXISTS `kartu_identitas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `kartu_identitas_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_kitas_id` bigint(20) DEFAULT NULL,
  `nomor_kartu` varchar(50) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `tanggal_berlaku` date DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_kartu_identitas_aud_rev` (`REV`),
  CONSTRAINT `fk_kartu_identitas_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keahlian`
--

DROP TABLE IF EXISTS `keahlian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `keahlian` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `jenis_keahlian_id` bigint(20) NOT NULL,
  `kualifikasi` int(11) DEFAULT NULL,
  `sertifikasi` bit(1) DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `masa_berlaku` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `tanggal_disetujui` datetime DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_keahlian_biodata` (`biodata_id`),
  KEY `idx_keahlian_jenis` (`jenis_keahlian_id`),
  CONSTRAINT `fk_keahlian_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_keahlian_jenis_keahlian` FOREIGN KEY (`jenis_keahlian_id`) REFERENCES `jenis_keahlian` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keahlian_aud`
--

DROP TABLE IF EXISTS `keahlian_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `keahlian_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_keahlian_id` bigint(20) DEFAULT NULL,
  `kualifikasi` int(11) DEFAULT NULL,
  `sertifikasi` bit(1) DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `masa_berlaku` varchar(255) DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `tanggal_disetujui` datetime DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_keahlian_aud_rev` (`REV`),
  CONSTRAINT `fk_keahlian_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_profil`
--

DROP TABLE IF EXISTS `lampiran_profil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_profil_aud`
--

DROP TABLE IF EXISTS `lampiran_profil_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_profil_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_lampiran_profil_aud_rev` (`REV`),
  CONSTRAINT `fk_lampiran_profil_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_sk`
--

DROP TABLE IF EXISTS `lampiran_sk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_sk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  PRIMARY KEY (`id`),
  KEY `idx_lampiran_sk_is_deleted` (`is_deleted`),
  KEY `idx_lampiran_sk_ref` (`ref`),
  KEY `idx_lampiran_sk_ref_id` (`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_sk_aud`
--

DROP TABLE IF EXISTS `lampiran_sk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_sk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_lampiran_sk_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `level`
--

DROP TABLE IF EXISTS `level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_level_nama` (`nama`),
  KEY `idx_level_is_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `level_aud`
--

DROP TABLE IF EXISTS `level_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `level_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `idx_level_aud_rev` (`rev`),
  CONSTRAINT `fk_level_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisasi`
--

DROP TABLE IF EXISTS `organisasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organisasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(50) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `level_org` int(11) DEFAULT NULL,
  `nama` varchar(255) NOT NULL,
  `short_name` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_organisasi_kode` (`kode`),
  KEY `idx_organisasi_nama` (`nama`),
  KEY `idx_organisasi_level_org` (`level_org`),
  KEY `idx_organisasi_parent` (`parent_id`),
  KEY `idx_organisasi_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_organisasi_parent` FOREIGN KEY (`parent_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisasi_aud`
--

DROP TABLE IF EXISTS `organisasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organisasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `level_org` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_organisasi_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pegawai`
--

DROP TABLE IF EXISTS `pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `no_ktp` varchar(255) DEFAULT NULL,
  `npwp` varchar(255) DEFAULT NULL,
  `no_bpjs_ketenagakerjaan` varchar(255) DEFAULT NULL,
  `no_bpjs_kesehatan` varchar(255) DEFAULT NULL,
  `tanggal_masuk` date DEFAULT NULL,
  `tanggal_berakhir_kontrak` date DEFAULT NULL,
  `status_pegawai` int(11) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `status_kerja` int(11) NOT NULL,
  `tmt_kerja` date DEFAULT NULL,
  `ref_sk_capeg_id` bigint(20) DEFAULT NULL,
  `tanggal_pengangkatan` date DEFAULT NULL,
  `tmt_pensiun` date DEFAULT NULL,
  `ref_sk_pegawai_id` bigint(20) DEFAULT NULL,
  `tmt_pegawai` date DEFAULT NULL,
  `ref_sk_gol_id` bigint(20) DEFAULT NULL,
  `tmt_golongan` date DEFAULT NULL,
  `ref_sk_jabatan_id` bigint(20) DEFAULT NULL,
  `tmt_jabatan` date DEFAULT NULL,
  `ref_sk_mutasi_id` bigint(20) DEFAULT NULL,
  `tmt_mutasi` date DEFAULT NULL,
  `ref_sk_gaji_berkala_id` bigint(20) DEFAULT NULL,
  `tmt_gaji_berkala` date DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `rumah_dinas_id` bigint(20) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `is_askes` bit(1) NOT NULL DEFAULT b'0',
  `phdp` double DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `absensi_id` bigint(20) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_pegawai_biodata` (`biodata_id`),
  KEY `idx_pegawai_organisasi` (`organisasi_id`),
  KEY `idx_pegawai_jabatan` (`jabatan_id`),
  KEY `idx_pegawai_profesi` (`profesi_id`),
  KEY `idx_pegawai_golongan` (`golongan_id`),
  KEY `idx_pegawai_grade` (`grade_id`),
  KEY `idx_pegawai_gaji_profil` (`gaji_profil_id`),
  KEY `idx_pegawai_kode_pajak` (`gaji_pendapatan_non_pajak_id`),
  KEY `idx_pegawai_rumah_dinas` (`rumah_dinas_id`),
  CONSTRAINT `fk_pegawai_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_pegawai_gaji_profil` FOREIGN KEY (`gaji_profil_id`) REFERENCES `gaji_profil` (`id`),
  CONSTRAINT `fk_pegawai_golongan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_pegawai_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`),
  CONSTRAINT `fk_pegawai_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_pegawai_kode_pajak` FOREIGN KEY (`gaji_pendapatan_non_pajak_id`) REFERENCES `gaji_pendapatan_non_pajak` (`id`),
  CONSTRAINT `fk_pegawai_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_pegawai_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  CONSTRAINT `fk_pegawai_rumah_dinas` FOREIGN KEY (`rumah_dinas_id`) REFERENCES `rumah_dinas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=574 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pegawai_aud`
--

DROP TABLE IF EXISTS `pegawai_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `no_ktp` varchar(255) DEFAULT NULL,
  `npwp` varchar(255) DEFAULT NULL,
  `no_bpjs_ketenagakerjaan` varchar(255) DEFAULT NULL,
  `no_bpjs_kesehatan` varchar(255) DEFAULT NULL,
  `tanggal_masuk` date DEFAULT NULL,
  `tanggal_berakhir_kontrak` date DEFAULT NULL,
  `status_pegawai` int(11) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `status_kerja` int(11) DEFAULT NULL,
  `tmt_kerja` date DEFAULT NULL,
  `ref_sk_capeg_id` bigint(20) DEFAULT NULL,
  `tanggal_pengangkatan` date DEFAULT NULL,
  `tmt_pensiun` date DEFAULT NULL,
  `ref_sk_pegawai_id` bigint(20) DEFAULT NULL,
  `tmt_pegawai` date DEFAULT NULL,
  `ref_sk_gol_id` bigint(20) DEFAULT NULL,
  `tmt_golongan` date DEFAULT NULL,
  `ref_sk_jabatan_id` bigint(20) DEFAULT NULL,
  `tmt_jabatan` date DEFAULT NULL,
  `ref_sk_mutasi_id` bigint(20) DEFAULT NULL,
  `tmt_mutasi` date DEFAULT NULL,
  `ref_sk_gaji_berkala_id` bigint(20) DEFAULT NULL,
  `tmt_gaji_berkala` date DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `rumah_dinas_id` bigint(20) DEFAULT NULL,
  `gaji_pokok` double DEFAULT NULL,
  `is_askes` bit(1) DEFAULT NULL,
  `phdp` double DEFAULT NULL,
  `jml_tanggungan` int(11) DEFAULT NULL,
  `mkg_tahun` int(11) DEFAULT NULL,
  `mkg_bulan` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `absensi_id` bigint(20) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_pegawai_aud_rev` (`REV`),
  CONSTRAINT `fk_pegawai_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pelatihan`
--

DROP TABLE IF EXISTS `pelatihan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pelatihan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `jenis_pelatihan_id` bigint(20) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `lembaga` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `lulus` bit(1) DEFAULT NULL,
  `nilai` varchar(255) DEFAULT NULL,
  `ikatan_dinas` bit(1) NOT NULL DEFAULT b'0',
  `tanggal_akhir_ikatan` date DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `tanggal_disetujui` datetime DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_pelatihan_biodata` (`biodata_id`),
  KEY `idx_pelatihan_jenis` (`jenis_pelatihan_id`),
  CONSTRAINT `fk_pelatihan_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_pelatihan_jenis` FOREIGN KEY (`jenis_pelatihan_id`) REFERENCES `jenis_pelatihan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3341 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pelatihan_aud`
--

DROP TABLE IF EXISTS `pelatihan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pelatihan_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenis_pelatihan_id` bigint(20) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `lembaga` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `lulus` bit(1) DEFAULT NULL,
  `nilai` varchar(255) DEFAULT NULL,
  `ikatan_dinas` bit(1) DEFAULT NULL,
  `tanggal_akhir_ikatan` date DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `disetujui` bit(1) DEFAULT NULL,
  `tanggal_pengajuan` datetime DEFAULT NULL,
  `tanggal_disetujui` datetime DEFAULT NULL,
  `disetujui_oleh` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_pelatihan_aud_rev` (`REV`),
  CONSTRAINT `fk_pelatihan_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pendidikan`
--

DROP TABLE IF EXISTS `pendidikan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `jenjang_pendidikan_id` bigint(20) NOT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `jurusan` varchar(255) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tahun_lulus` int(11) DEFAULT NULL,
  `no_ijazah` varchar(255) DEFAULT NULL,
  `tanggal_ijazah` date DEFAULT NULL,
  `file_ijazah` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_pendidikan_biodata` (`biodata_id`),
  KEY `idx_pendidikan_jenjang` (`jenjang_pendidikan_id`),
  CONSTRAINT `fk_pendidikan_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_pendidikan_jenjang` FOREIGN KEY (`jenjang_pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1268 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pendidikan_aud`
--

DROP TABLE IF EXISTS `pendidikan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pendidikan_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `jenjang_pendidikan_id` bigint(20) DEFAULT NULL,
  `institusi` varchar(255) DEFAULT NULL,
  `jurusan` varchar(255) DEFAULT NULL,
  `tahun_masuk` int(11) DEFAULT NULL,
  `tahun_lulus` int(11) DEFAULT NULL,
  `no_ijazah` varchar(255) DEFAULT NULL,
  `tanggal_ijazah` date DEFAULT NULL,
  `file_ijazah` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_pendidikan_aud_rev` (`REV`),
  CONSTRAINT `fk_pendidikan_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pengalaman_kerja`
--

DROP TABLE IF EXISTS `pengalaman_kerja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pengalaman_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) NOT NULL,
  `perusahaan` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `alasan_berhenti` text DEFAULT NULL,
  `gaji_terakhir` double DEFAULT NULL,
  `referensi` varchar(255) DEFAULT NULL,
  `kontak_referensi` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_pengalaman_kerja_biodata` (`biodata_id`),
  CONSTRAINT `fk_pengalaman_kerja_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pengalaman_kerja_aud`
--

DROP TABLE IF EXISTS `pengalaman_kerja_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pengalaman_kerja_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `perusahaan` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `alasan_berhenti` text DEFAULT NULL,
  `gaji_terakhir` double DEFAULT NULL,
  `referensi` varchar(255) DEFAULT NULL,
  `kontak_referensi` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_pengalaman_kerja_aud_rev` (`REV`),
  CONSTRAINT `fk_pengalaman_kerja_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pref_role`
--

DROP TABLE IF EXISTS `pref_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pref_role` (
  `id` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profesi`
--

DROP TABLE IF EXISTS `profesi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `detail` text DEFAULT NULL,
  `resiko` varchar(255) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_profesi_nama` (`nama`),
  KEY `idx_profesi_organisasi` (`organisasi_id`),
  KEY `idx_profesi_jabatan` (`jabatan_id`),
  KEY `idx_profesi_level` (`level_id`),
  KEY `idx_profesi_grade` (`grade_id`),
  KEY `idx_profesi_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_profesi_grade` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_level` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_profesi_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profesi_aud`
--

DROP TABLE IF EXISTS `profesi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `resiko` varchar(255) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_profesi_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profil_keluarga`
--

DROP TABLE IF EXISTS `profil_keluarga`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profil_keluarga` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nik` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_kelamin` int(11) DEFAULT NULL,
  `agama` int(11) DEFAULT NULL,
  `hubungan_keluarga` int(11) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tanggungan` bit(1) NOT NULL DEFAULT b'0',
  `pendidikan_id` bigint(20) DEFAULT NULL,
  `status_pendidikan` int(11) DEFAULT NULL,
  `status_kawin` bit(1) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `biodata_id` varchar(255) NOT NULL,
  `changed_status` bit(1) NOT NULL DEFAULT b'0',
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_profil_keluarga_biodata` (`biodata_id`),
  KEY `idx_profil_keluarga_pendidikan` (`pendidikan_id`),
  CONSTRAINT `fk_profil_keluarga_biodata` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `fk_profil_keluarga_pendidikan` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3248 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profil_keluarga_aud`
--

DROP TABLE IF EXISTS `profil_keluarga_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profil_keluarga_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `jenis_kelamin` int(11) DEFAULT NULL,
  `agama` int(11) DEFAULT NULL,
  `hubungan_keluarga` int(11) DEFAULT NULL,
  `tempat_lahir` varchar(255) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tanggungan` bit(1) DEFAULT NULL,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  `status_pendidikan` int(11) DEFAULT NULL,
  `status_kawin` bit(1) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `changed_status` bit(1) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_profil_keluarga_aud_rev` (`REV`),
  CONSTRAINT `fk_profil_keluarga_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profil_update`
--

DROP TABLE IF EXISTS `profil_update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profil_update` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_type` tinyint(4) DEFAULT NULL,
  `approval_date` datetime(6) DEFAULT NULL,
  `approval_pic` varchar(255) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `data_description` varchar(255) DEFAULT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `req_date` timestamp NULL DEFAULT current_timestamp(),
  `rev_id` bigint(20) DEFAULT NULL,
  `table_name` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_profile_update_nipam` (`nipam`) USING BTREE,
  KEY `idx_profile_update_nama` (`nama`) USING BTREE,
  KEY `idx_profile_update` (`approval_status`) USING BTREE,
  KEY `idx_profile_update_req_date` (`req_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `revinfo` (
  `REV` bigint(20) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_cuti`
--

DROP TABLE IF EXISTS `riwayat_cuti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_cuti` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_cuti_pegawai` (`pegawai_id`),
  KEY `idx_riwayat_cuti_cuti_pegawai` (`cuti_pegawai_id`),
  CONSTRAINT `fk_riwayat_cuti_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_cuti_aud`
--

DROP TABLE IF EXISTS `riwayat_cuti_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_cuti_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `jumlah_hari` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_cuti_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_cuti_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_keluar`
--

DROP TABLE IF EXISTS `riwayat_keluar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_keluar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `tanggal_keluar` date DEFAULT NULL,
  `alasan_berhenti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_keluar_pegawai` (`pegawai_id`),
  KEY `fk_riwayat_keluar_alasan` (`alasan_berhenti_id`),
  CONSTRAINT `fk_riwayat_keluar_alasan` FOREIGN KEY (`alasan_berhenti_id`) REFERENCES `alasan_berhenti` (`id`),
  CONSTRAINT `fk_riwayat_keluar_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_kontrak`
--

DROP TABLE IF EXISTS `riwayat_kontrak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_kontrak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nomor_kontrak` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_riwayat_kontrak_pegawai_nomor` (`pegawai_id`,`nomor_kontrak`),
  KEY `idx_riwayat_kontrak_nomor` (`nomor_kontrak`),
  KEY `idx_riwayat_kontrak_tanggal_mulai` (`tanggal_mulai`),
  CONSTRAINT `fk_riwayat_kontrak_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=296 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_kontrak_aud`
--

DROP TABLE IF EXISTS `riwayat_kontrak_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_kontrak_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nomor_kontrak` varchar(255) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_kontrak_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_kontrak_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_mutasi`
--

DROP TABLE IF EXISTS `riwayat_mutasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_mutasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `jenis_mutasi` int(11) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `nama_profesi` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `organisasi_lama_id` bigint(20) DEFAULT NULL,
  `nama_organisasi_lama` varchar(255) DEFAULT NULL,
  `jabatan_lama_id` bigint(20) DEFAULT NULL,
  `nama_jabatan_lama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_mutasi_pegawai` (`pegawai_id`),
  KEY `idx_riwayat_mutasi_sk` (`riwayat_sk_id`),
  KEY `fk_riwayat_mutasi_organisasi` (`organisasi_id`),
  KEY `fk_riwayat_mutasi_jabatan` (`jabatan_id`),
  KEY `fk_riwayat_mutasi_profesi` (`profesi_id`),
  KEY `fk_riwayat_mutasi_golongan` (`golongan_id`),
  CONSTRAINT `fk_riwayat_mutasi_golongan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_riwayat_mutasi_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_riwayat_mutasi_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_riwayat_mutasi_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_riwayat_mutasi_profesi` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  CONSTRAINT `fk_riwayat_mutasi_sk` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2944 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_mutasi_aud`
--

DROP TABLE IF EXISTS `riwayat_mutasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_mutasi_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `jenis_mutasi` int(11) DEFAULT NULL,
  `riwayat_sk_id` bigint(20) DEFAULT NULL,
  `tmt_berlaku` date DEFAULT NULL,
  `tanggal_berakhir` date DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `nama_profesi` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `organisasi_lama_id` bigint(20) DEFAULT NULL,
  `nama_organisasi_lama` varchar(255) DEFAULT NULL,
  `jabatan_lama_id` bigint(20) DEFAULT NULL,
  `nama_jabatan_lama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_mutasi_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_mutasi_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sk`
--

DROP TABLE IF EXISTS `riwayat_sk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `ref_sk_id` bigint(20) DEFAULT NULL,
  `jenis_kontrak` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `is_latest` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_sk_pegawai` (`pegawai_id`),
  KEY `idx_riwayat_sk_nomor` (`nomor_sk`),
  KEY `idx_riwayat_sk_tanggal_sk` (`tanggal_sk`),
  KEY `fk_riwayat_sk_ref_sk` (`ref_sk_id`),
  KEY `fk_riwayat_sk_organisasi` (`organisasi_id`),
  KEY `fk_riwayat_sk_jabatan` (`jabatan_id`),
  CONSTRAINT `fk_riwayat_sk_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_riwayat_sk_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_riwayat_sk_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_riwayat_sk_ref_sk` FOREIGN KEY (`ref_sk_id`) REFERENCES `riwayat_sk` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7796 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sk_aud`
--

DROP TABLE IF EXISTS `riwayat_sk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sk_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `biodata_id` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `tanggal_sk` date DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `ref_sk_id` bigint(20) DEFAULT NULL,
  `jenis_kontrak` int(11) DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `is_latest` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_sk_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_sk_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sp`
--

DROP TABLE IF EXISTS `riwayat_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nomor_sp` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `tanggal_sp` date DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  `sanksi_id` bigint(20) DEFAULT NULL,
  `sanksi_notes` text DEFAULT NULL,
  `tanggal_eksekusi_sanksi` date DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `penanda_tangan` varchar(255) DEFAULT NULL,
  `jabatan_penanda_tangan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_sp_pegawai` (`pegawai_id`),
  KEY `idx_riwayat_sp_tanggal_sp` (`tanggal_sp`),
  KEY `fk_riwayat_sp_organisasi` (`organisasi_id`),
  KEY `fk_riwayat_sp_jabatan` (`jabatan_id`),
  KEY `fk_riwayat_sp_jenis` (`jenis_sp_id`),
  KEY `fk_riwayat_sp_sanksi` (`sanksi_id`),
  CONSTRAINT `fk_riwayat_sp_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_riwayat_sp_jenis` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`),
  CONSTRAINT `fk_riwayat_sp_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_riwayat_sp_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_riwayat_sp_sanksi` FOREIGN KEY (`sanksi_id`) REFERENCES `sanksi_sp` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sp_aud`
--

DROP TABLE IF EXISTS `riwayat_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sp_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `nomor_sp` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `tanggal_sp` date DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  `sanksi_id` bigint(20) DEFAULT NULL,
  `sanksi_notes` text DEFAULT NULL,
  `tanggal_eksekusi_sanksi` date DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `tanggal_selesai` date DEFAULT NULL,
  `penanda_tangan` varchar(255) DEFAULT NULL,
  `jabatan_penanda_tangan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_sp_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_sp_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_terminasi`
--

DROP TABLE IF EXISTS `riwayat_terminasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_terminasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `alasan_terminasi_id` bigint(20) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `sk_terminasi_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `tanggal_terminasi` date DEFAULT NULL,
  `tahun_terminasi` int(11) DEFAULT NULL,
  `masa_kerja` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_riwayat_terminasi_pegawai` (`pegawai_id`),
  KEY `idx_riwayat_terminasi_nomor` (`nomor_sk`),
  KEY `idx_riwayat_terminasi_tanggal` (`tanggal_terminasi`),
  KEY `fk_riwayat_terminasi_alasan` (`alasan_terminasi_id`),
  KEY `fk_riwayat_terminasi_sk` (`sk_terminasi_id`),
  KEY `fk_riwayat_terminasi_organisasi` (`organisasi_id`),
  KEY `fk_riwayat_terminasi_jabatan` (`jabatan_id`),
  KEY `fk_riwayat_terminasi_golongan` (`golongan_id`),
  CONSTRAINT `fk_riwayat_terminasi_alasan` FOREIGN KEY (`alasan_terminasi_id`) REFERENCES `alasan_berhenti` (`id`),
  CONSTRAINT `fk_riwayat_terminasi_golongan` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `fk_riwayat_terminasi_jabatan` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `fk_riwayat_terminasi_organisasi` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `fk_riwayat_terminasi_pegawai` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `fk_riwayat_terminasi_sk` FOREIGN KEY (`sk_terminasi_id`) REFERENCES `riwayat_sk` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_terminasi_aud`
--

DROP TABLE IF EXISTS `riwayat_terminasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_terminasi_aud` (
  `id` bigint(20) NOT NULL,
  `REV` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_deleted` bit(1) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `alasan_terminasi_id` bigint(20) DEFAULT NULL,
  `nomor_sk` varchar(255) DEFAULT NULL,
  `sk_terminasi_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `nama_organisasi` varchar(255) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `nama_jabatan` varchar(255) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `nama_golongan` varchar(255) DEFAULT NULL,
  `tanggal_terminasi` date DEFAULT NULL,
  `tahun_terminasi` int(11) DEFAULT NULL,
  `masa_kerja` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `idx_riwayat_terminasi_aud_rev` (`REV`),
  CONSTRAINT `fk_riwayat_terminasi_aud_rev` FOREIGN KEY (`REV`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rumah_dinas`
--

DROP TABLE IF EXISTS `rumah_dinas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rumah_dinas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) NOT NULL,
  `nilai` double DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rumah_dinas_aud`
--

DROP TABLE IF EXISTS `rumah_dinas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rumah_dinas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_rumah_dinas_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanksi_sp`
--

DROP TABLE IF EXISTS `sanksi_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanksi_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `kode` varchar(10) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `pot_tkk` bit(1) DEFAULT b'0',
  `jml_pot_tkk` int(11) DEFAULT 0,
  `is_pending_pangkat` bit(1) DEFAULT b'0',
  `is_pending_gaji` bit(1) DEFAULT b'0',
  `is_turun_pangkat` bit(1) DEFAULT b'0',
  `is_turun_jabatan` bit(1) DEFAULT b'0',
  `is_suspension` bit(1) DEFAULT b'0',
  `is_terminate_dh` bit(1) DEFAULT b'0',
  `is_terminate_th` bit(1) DEFAULT b'0',
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `created_at` datetime(6) NOT NULL DEFAULT current_timestamp(6),
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL ON UPDATE current_timestamp(6),
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_sanksi_sp_kode` (`kode`),
  KEY `idx_sanksi_sp_jenis_sp` (`jenis_sp_id`),
  KEY `idx_sanksi_sp_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_sanksi_sp_jenis_sp` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanksi_sp_aud`
--

DROP TABLE IF EXISTS `sanksi_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanksi_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` bigint(20) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `is_pending_gaji` bit(1) DEFAULT NULL,
  `is_pending_pangkat` bit(1) DEFAULT NULL,
  `is_suspension` bit(1) DEFAULT NULL,
  `is_terminate_dh` bit(1) DEFAULT NULL,
  `is_terminate_th` bit(1) DEFAULT NULL,
  `is_turun_jabatan` bit(1) DEFAULT NULL,
  `is_turun_pangkat` bit(1) DEFAULT NULL,
  `jml_pot_tkk` int(11) DEFAULT NULL,
  `keterangan` text DEFAULT NULL,
  `kode` varchar(10) DEFAULT NULL,
  `pot_tkk` bit(1) DEFAULT NULL,
  `jenis_sp_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`),
  CONSTRAINT `fk_sanksi_sp_aud_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statistik_pegawai`
--

DROP TABLE IF EXISTS `statistik_pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistik_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_deleted` bit(1) NOT NULL DEFAULT b'0',
  `organisasi_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `total_pegawai` int(11) DEFAULT NULL,
  `gol_a` int(11) DEFAULT NULL,
  `gol_b` int(11) DEFAULT NULL,
  `gol_c` int(11) DEFAULT NULL,
  `gol_d` int(11) DEFAULT NULL,
  `golongan_a` int(11) DEFAULT NULL,
  `golongan_b` int(11) DEFAULT NULL,
  `golongan_c` int(11) DEFAULT NULL,
  `golongan_d` int(11) DEFAULT NULL,
  `kontrak` int(11) DEFAULT NULL,
  `capeg` int(11) DEFAULT NULL,
  `honorer` int(11) DEFAULT NULL,
  `tetap` int(11) DEFAULT NULL,
  `adm` int(11) DEFAULT NULL,
  `pelayanan` int(11) DEFAULT NULL,
  `teknik` int(11) DEFAULT NULL,
  `pria` int(11) DEFAULT NULL,
  `wanita` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_statistik_pegawai_organisasi` (`organisasi_id`),
  KEY `idx_statistik_pegawai_jabatan` (`jabatan_id`),
  KEY `idx_statistik_pegawai_golongan` (`golongan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'kepegawaian_dev_new'
--

--
-- Dumping routines for database 'kepegawaian_dev_new'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-23 11:51:02

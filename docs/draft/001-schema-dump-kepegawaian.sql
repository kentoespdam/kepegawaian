-- NOTE: This is a GENERATED file from mysqldump --no-data.
-- It is a WORKING DRAFT for the baseline-rebuild epic (kepegawaian-odb).
-- DO NOT commit this to db/migration/.
-- Generated: 2026-07-23
-- Source: kepegawaian (legacy) @ 192.168.230.84:3307
-- See: docs/CLAIM-ORDER-baseline-rebuild.md

/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.14-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.230.84    Database: kepegawaian
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
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT 0,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXd3w33nn12u6cxhm7hsxwmqt8c` (`nama`) USING BTREE,
  KEY `IDX5hkvinq0k05krw4tgaftm95qa` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alasan_berhenti_aud`
--

DROP TABLE IF EXISTS `alasan_berhenti_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `alasan_berhenti_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK1868iy9oslsjrvvinwy7faspg` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alat_kerja`
--

DROP TABLE IF EXISTS `alat_kerja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `alat_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXtqiyul14xblvlouo46lpy5u8p` (`is_deleted`) USING BTREE,
  KEY `IDXh0qaso5xurqnxopqttk55w37q` (`nama`) USING BTREE,
  KEY `FKlr2nyht8vni7oah23u4tmsemu` (`profesi_id`) USING BTREE,
  CONSTRAINT `FKlr2nyht8vni7oah23u4tmsemu` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `apd`
--

DROP TABLE IF EXISTS `apd`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `apd` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXgb6muk74wm5xpcs2pbdu80cj2` (`nama`) USING BTREE,
  KEY `IDX88pqwc7hvwf6kimwtomqg640` (`is_deleted`) USING BTREE,
  KEY `FKr19ssvlfxu0op3e7ic5y3hm7r` (`profesi_id`) USING BTREE,
  CONSTRAINT `FKr19ssvlfxu0op3e7ic5y3hm7r` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `biodata`
--

DROP TABLE IF EXISTS `biodata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `biodata` (
  `nik` varchar(255) NOT NULL,
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
  `version` bigint(20) DEFAULT 0,
  `pendidikan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`nik`) USING BTREE,
  KEY `IDXnqr3ym1wkj2nocdkdqoeiq9yp` (`nama`) USING BTREE,
  KEY `IDXnhubjktwf27bi0gagw7f837ye` (`is_deleted`) USING BTREE,
  KEY `IDXna7vw7hureuqxs36ly93ej6rv` (`jenis_kelamin`) USING BTREE,
  KEY `IDXdfmikwohxek88eo8f16pcbw6p` (`alamat`) USING BTREE,
  KEY `IDXroplfpwcfu9fgltilani0ws1m` (`is_pegawai`) USING BTREE,
  KEY `FKnemsw9hc2kgf5gtc9hpwcvl1a` (`pendidikan_id`) USING BTREE,
  CONSTRAINT `FKnemsw9hc2kgf5gtc9hpwcvl1a` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `biodata_aud`
--

DROP TABLE IF EXISTS `biodata_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
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
  CONSTRAINT `FKogvc1alscr1if1ivcjj4v8p79` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval`
--

DROP TABLE IF EXISTS `cuti_approval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `approver_id` bigint(20) DEFAULT NULL,
  `cuti_pegawai_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `is_deleted_idx` (`is_deleted`) USING BTREE,
  KEY `FK95xgbdtgd8947kv02xhofvkj3` (`approver_id`) USING BTREE,
  KEY `FK4do9tshk9xwo87722dtd94tdj` (`cuti_pegawai_id`) USING BTREE,
  KEY `FKs0c49chlm4r8cy8w1xaysnkos` (`jabatan_id`) USING BTREE,
  CONSTRAINT `FK4do9tshk9xwo87722dtd94tdj` FOREIGN KEY (`cuti_pegawai_id`) REFERENCES `cuti_pegawai` (`id`),
  CONSTRAINT `FK95xgbdtgd8947kv02xhofvkj3` FOREIGN KEY (`approver_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKs0c49chlm4r8cy8w1xaysnkos` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4421 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval_aud`
--

DROP TABLE IF EXISTS `cuti_approval_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FK20coyhpwfqdufhcgcimb525t7` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_approval_chain`
--

DROP TABLE IF EXISTS `cuti_approval_chain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_approval_chain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `approval_level` int(11) DEFAULT NULL,
  `approval_status` tinyint(4) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `jabatan_nama` varchar(255) DEFAULT NULL,
  `read_write_status` tinyint(4) DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FKn3ao94l5vyrgih77rkxoeb0sf` (`ref_cuti_id`) USING BTREE,
  KEY `IDX64dcslnh77bjfcecb6qhkqxew` (`read_write_status`) USING BTREE,
  KEY `IDX7v2omea1f7ld52xi2gfdovc54` (`approval_status`) USING BTREE,
  CONSTRAINT `FKn3ao94l5vyrgih77rkxoeb0sf` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4546 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_jenis`
--

DROP TABLE IF EXISTS `cuti_jenis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_jenis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `max_hari` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `potong_kuota_tahunan` bit(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `is_deleted_idx` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_jenis_aud`
--

DROP TABLE IF EXISTS `cuti_jenis_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_jenis_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK9sur2315833kbiwl4sua5oki2` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_klaim_detail`
--

DROP TABLE IF EXISTS `cuti_klaim_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_klaim_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tanggal` date DEFAULT NULL,
  `ref_cuti_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK793v6rc4hkwwx6pxtro6hlm22` (`ref_cuti_id`) USING BTREE,
  CONSTRAINT `FK793v6rc4hkwwx6pxtro6hlm22` FOREIGN KEY (`ref_cuti_id`) REFERENCES `cuti_pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=472 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_kuota`
--

DROP TABLE IF EXISTS `cuti_kuota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_kuota` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `expired` date DEFAULT NULL,
  `kuota` int(11) DEFAULT NULL,
  `kuota_tambahan` int(11) DEFAULT NULL,
  `kuota_terpakai` int(11) DEFAULT NULL,
  `sisa_kuota` int(11) DEFAULT NULL,
  `tahun` int(11) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `is_deleted_idx` (`is_deleted`) USING BTREE,
  KEY `FK8e4brq58446u1ism0wqsp1os2` (`pegawai_id`) USING BTREE,
  CONSTRAINT `FK8e4brq58446u1ism0wqsp1os2` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2420 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_kuota_aud`
--

DROP TABLE IF EXISTS `cuti_kuota_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_kuota_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FK6cky1q18ueydc77mk56stca8s` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_pegawai`
--

DROP TABLE IF EXISTS `cuti_pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  KEY `is_deleted_idx` (`is_deleted`) USING BTREE,
  KEY `FKdvyno26i1j90vhd43yy1jaxh5` (`jabatan_id`) USING BTREE,
  KEY `FKhooagj7gj49e1cfqy2j60924m` (`jenis_cuti_id`) USING BTREE,
  KEY `FK94qggce9t69gtatc46xn729e1` (`organisasi_id`) USING BTREE,
  KEY `FKrnp3oip017rvu8f3vclhe4mxn` (`pegawai_id`) USING BTREE,
  KEY `FKedhj9emrnv8msw77768d02vbg` (`pic_saat_ini_id`) USING BTREE,
  KEY `FKskgmhy1rclhvusx4x7ow3tk66` (`sub_jenis_cuti_id`) USING BTREE,
  CONSTRAINT `FK94qggce9t69gtatc46xn729e1` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKdvyno26i1j90vhd43yy1jaxh5` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FKedhj9emrnv8msw77768d02vbg` FOREIGN KEY (`pic_saat_ini_id`) REFERENCES `jabatan` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FKhooagj7gj49e1cfqy2j60924m` FOREIGN KEY (`jenis_cuti_id`) REFERENCES `cuti_jenis` (`id`),
  CONSTRAINT `FKrnp3oip017rvu8f3vclhe4mxn` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKskgmhy1rclhvusx4x7ow3tk66` FOREIGN KEY (`sub_jenis_cuti_id`) REFERENCES `cuti_jenis` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1246 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cuti_pegawai_aud`
--

DROP TABLE IF EXISTS `cuti_pegawai_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuti_pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKt7ru9sqxlicxj7eouotpxodnr` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dasar_gaji`
--

DROP TABLE IF EXISTS `dasar_gaji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `aktif` bit(1) NOT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `tanggal_akhir` date DEFAULT NULL,
  `tanggal_awal` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXkt4m34x9x881klhuwjf2wotr5` (`deskripsi`) USING BTREE,
  KEY `IDXq19ovuyirxvu21w04gg9p51ec` (`aktif`) USING BTREE,
  KEY `IDX5waqqukreq6vlfjc6gunojv0l` (`is_deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dasar_gaji_aud`
--

DROP TABLE IF EXISTS `dasar_gaji_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKawtsykklm2brer5a81qu6jtox` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detail_dasar_gaji`
--

DROP TABLE IF EXISTS `detail_dasar_gaji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail_dasar_gaji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `golongan_kode` int(11) DEFAULT NULL,
  `mkg` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `dasar_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX8b01oba43819lt5sejby8frds` (`mkg`) USING BTREE,
  KEY `IDXcug080nfl52x7b0fhokpmgnp9` (`is_deleted`) USING BTREE,
  KEY `FKbx0uyjs3uy0elioah79da7yxd` (`dasar_gaji_id`) USING BTREE,
  CONSTRAINT `FKbx0uyjs3uy0elioah79da7yxd` FOREIGN KEY (`dasar_gaji_id`) REFERENCES `dasar_gaji` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detail_dasar_gaji_aud`
--

DROP TABLE IF EXISTS `detail_dasar_gaji_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail_dasar_gaji_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKnrpc9ldtujcfgt86m216d4rne` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_master`
--

DROP TABLE IF EXISTS `gaji_batch_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
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
  KEY `IDX2881agtf8kcmvm9pmdoxdy277` (`periode`) USING BTREE,
  KEY `IDXhexggly068gpjbi9ry7scexkb` (`nipam`) USING BTREE,
  KEY `IDXtg6hvsniqi1xhc2lr2msw4srg` (`nama`) USING BTREE,
  KEY `FKd1adksql6drof7vnp6pocwh09` (`batch_root_id`) USING BTREE,
  KEY `FKp517hr1rwc0i22dofirsek8kn` (`gaji_pendapatan_non_pajak_id`) USING BTREE,
  KEY `FKgr17qmjxk5s1cnst11en4h9wo` (`organisasi_id`) USING BTREE,
  KEY `IDXm86js7c12wwdls5v0nw12whfw` (`pegawai_id`) USING BTREE,
  CONSTRAINT `FKd1adksql6drof7vnp6pocwh09` FOREIGN KEY (`batch_root_id`) REFERENCES `gaji_batch_root` (`id`),
  CONSTRAINT `FKgr17qmjxk5s1cnst11en4h9wo` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKp517hr1rwc0i22dofirsek8kn` FOREIGN KEY (`gaji_pendapatan_non_pajak_id`) REFERENCES `gaji_pendapatan_non_pajak` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
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
  `formula` varchar(255) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `nilai_formula` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXoj7601u6fwtlv4o9wq30pklkq` (`urut`) USING BTREE,
  KEY `IDXe5g9xpiwfvugcgqh7g3a5tgu0` (`kode`) USING BTREE,
  KEY `IDX8c7f8xmw7ahw2vpfoccuj5soa` (`nama`) USING BTREE,
  KEY `IDX4m7uf76b7pcxhtbd1d5bbe3j` (`batch_master_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40095 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_potongan_tkk`
--

DROP TABLE IF EXISTS `gaji_batch_potongan_tkk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `potongan` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX1xiph00fi6yrqq809m5lejbjo` (`batch_id`) USING BTREE,
  KEY `IDXrgupsyajkfuib28n59hf45oin` (`nipam`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root`
--

DROP TABLE IF EXISTS `gaji_batch_root`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
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
  KEY `IDXfguxv4xtbt9kf2ofk6knscauj` (`is_deleted`) USING BTREE,
  KEY `IDX40h27le3wp0dnypdeff1k5gw7` (`tanggal_proses`) USING BTREE,
  KEY `IDXqmwyo91kusws227mhxrg9fhdn` (`tanggal_verifikasi_tahap1`) USING BTREE,
  KEY `IDXrrsw1egu8poj1717cxp3jqife` (`tanggal_verifikasi_tahap2`) USING BTREE,
  KEY `IDXegxnakc1evf17gnre350cdm88` (`tanggal_persetujuan`) USING BTREE,
  KEY `IDXbamfb5ywst7lpo1nqpihgqcqr` (`status`) USING BTREE,
  KEY `IDXaeka97uyjf2wqohofvp76jmo1` (`periode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_aud`
--

DROP TABLE IF EXISTS `gaji_batch_root_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_aud` (
  `id` varchar(255) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKbvm82xag911hmiibbq8xqwgi6` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_error_logs`
--

DROP TABLE IF EXISTS `gaji_batch_root_error_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_error_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `root_batch_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXnplpll71oaxf0nc4lluvgbio0` (`nipam`) USING BTREE,
  KEY `IDXf379px1h80fr1yut9xirhwhsa` (`nama`) USING BTREE,
  KEY `FKcjp75555i9tgo8brfdexsli47` (`root_batch_id`) USING BTREE,
  CONSTRAINT `FKcjp75555i9tgo8brfdexsli47` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_batch_root_lampiran`
--

DROP TABLE IF EXISTS `gaji_batch_root_lampiran`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_batch_root_lampiran` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `hashed_file_name` varchar(255) DEFAULT NULL,
  `jenis_lampiran_gaji` tinyint(4) DEFAULT NULL,
  `mime_type` varchar(255) DEFAULT NULL,
  `root_batch_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FKfui0dihb70q2uetalgbmo7q9j` (`root_batch_id`) USING BTREE,
  CONSTRAINT `FKfui0dihb70q2uetalgbmo7q9j` FOREIGN KEY (`root_batch_id`) REFERENCES `gaji_batch_root` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_komponen`
--

DROP TABLE IF EXISTS `gaji_komponen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_komponen` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `is_reference` bit(1) DEFAULT NULL,
  `jenis_gaji` enum('NONE','PEMASUKAN','POTONGAN') DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  `profil_gaji_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXl2pic9x86xb26opxh6gcgfk3` (`is_deleted`) USING BTREE,
  KEY `FK5phamfq1atjudiiyqy0x2il98` (`profil_gaji_id`) USING BTREE,
  CONSTRAINT `FK5phamfq1atjudiiyqy0x2il98` FOREIGN KEY (`profil_gaji_id`) REFERENCES `gaji_profil` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_komponen_aud`
--

DROP TABLE IF EXISTS `gaji_komponen_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_komponen_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FK8s9wai8150msbqi0mntm1o06y` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_parameter_setting`
--

DROP TABLE IF EXISTS `gaji_parameter_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_parameter_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX234u6esru90nl6l4sxkqydrly` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_parameter_setting_aud`
--

DROP TABLE IF EXISTS `gaji_parameter_setting_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_parameter_setting_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK9ih1if6l76ttd8vpciy2rfvaj` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_pendapatan_non_pajak`
--

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_pendapatan_non_pajak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX4j0ykodguj6ee6swubto7uxun` (`kode`) USING BTREE,
  KEY `IDX4uo1pgbaf1njs66c9pqnmp27e` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_pendapatan_non_pajak_aud`
--

DROP TABLE IF EXISTS `gaji_pendapatan_non_pajak_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_pendapatan_non_pajak_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKnqhkw4ck5w0kbpl2kyjtl5ndk` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_phdp`
--

DROP TABLE IF EXISTS `gaji_phdp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_phdp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK10j12e3aaflv25q4dthlok5ub` (`urut`) USING BTREE,
  KEY `IDXkkpgdi07uko10cven4e2kjuc1` (`is_deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_phdp_aud`
--

DROP TABLE IF EXISTS `gaji_phdp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_phdp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `formula` varchar(255) DEFAULT NULL,
  `kondisi` varchar(255) DEFAULT NULL,
  `urut` int(11) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKglji8prkrk7ssdlr0t5wh6r3q` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_potongan_tkk`
--

DROP TABLE IF EXISTS `gaji_potongan_tkk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_potongan_tkk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `status_pegawai` tinyint(4) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX1j99jgo8p61mgxlpjk0a4ja3j` (`is_deleted`) USING BTREE,
  KEY `FKboe0vgmhai1if02scv0uqi3hv` (`golongan_id`) USING BTREE,
  KEY `FKjmrp5ricyxa9b82kcpj51m7nc` (`level_id`) USING BTREE,
  CONSTRAINT `FKboe0vgmhai1if02scv0uqi3hv` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `FKjmrp5ricyxa9b82kcpj51m7nc` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_potongan_tkk_aud`
--

DROP TABLE IF EXISTS `gaji_potongan_tkk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_potongan_tkk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKare7661xylm2o4a9cmc38xave` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_profil`
--

DROP TABLE IF EXISTS `gaji_profil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_profil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXnem9iajkkjcvu3yf3bab077ic` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_profil_aud`
--

DROP TABLE IF EXISTS `gaji_profil_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_profil_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKt37lx96jelfe4hwe1drqo36mc` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_tunjangan`
--

DROP TABLE IF EXISTS `gaji_tunjangan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_tunjangan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `jenis_tunjangan` tinyint(4) DEFAULT NULL,
  `nominal` double DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXm2y8dmyyrqe14tueu4ulq7ank` (`is_deleted`) USING BTREE,
  KEY `FKcyv33jef4ikcaiuopdr20x8yo` (`golongan_id`) USING BTREE,
  KEY `FK3swgjihdva7shai6by6mq8980` (`level_id`) USING BTREE,
  CONSTRAINT `FK3swgjihdva7shai6by6mq8980` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `FKcyv33jef4ikcaiuopdr20x8yo` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gaji_tunjangan_aud`
--

DROP TABLE IF EXISTS `gaji_tunjangan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `gaji_tunjangan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKgkr9qrjgnqrw5wggvrpuojf4e` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `golongan`
--

DROP TABLE IF EXISTS `golongan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `golongan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `golongan` varchar(255) DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXtb6may6a8k5ha9q3cwn45cdj3` (`golongan`) USING BTREE,
  KEY `IDXc0xqgf5wko9hu0x8p4tw7gfjn` (`pangkat`) USING BTREE,
  KEY `IDX70633pfabjfcfl9q5ry2wkx91` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `golongan_aud`
--

DROP TABLE IF EXISTS `golongan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `golongan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `golongan` varchar(255) DEFAULT NULL,
  `pangkat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKi1fuplb0ab25cbwt9m96dea44` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grade`
--

DROP TABLE IF EXISTS `grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `tukin` double DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXlpbhlvkq9i4uur83af9cm9w3` (`grade`) USING BTREE,
  KEY `IDXfx6ddqosvqgoawlhl4beaikkd` (`is_deleted`) USING BTREE,
  KEY `FK1hburlj6slfkhor144ur1vl7c` (`level_id`) USING BTREE,
  CONSTRAINT `FK1hburlj6slfkhor144ur1vl7c` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `grade_aud`
--

DROP TABLE IF EXISTS `grade_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK6lrx2mk3o3ylt0lioi1817mx0` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hari_libur`
--

DROP TABLE IF EXISTS `hari_libur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `hari_libur` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `jenis_libur` tinyint(4) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_tanggal_idx` (`tanggal`) USING BTREE,
  KEY `is_deleted_idx` (`is_deleted`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hari_libur_aud`
--

DROP TABLE IF EXISTS `hari_libur_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `hari_libur_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKs2b5n9r03gnrvsx98naibjlu9` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jabatan`
--

DROP TABLE IF EXISTS `jabatan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jabatan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX659bcb76wp8722jx2sda5y39g` (`kode`) USING BTREE,
  KEY `IDXb1tvuvx2aggmo4ssk4r80bsdh` (`nama`) USING BTREE,
  KEY `IDX7nam7cg9wbt5x5svvxhg6lvlu` (`is_deleted`) USING BTREE,
  KEY `FK7uhntawsl2946ok908j4in6y1` (`level_id`) USING BTREE,
  KEY `FKqnsm2lqkf182h1mi515255uee` (`organisasi_id`) USING BTREE,
  CONSTRAINT `FK7uhntawsl2946ok908j4in6y1` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `FKqnsm2lqkf182h1mi515255uee` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jabatan_aud`
--

DROP TABLE IF EXISTS `jabatan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jabatan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK3fh3m0ka6uukh0evn4ko7idio` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_keahlian`
--

DROP TABLE IF EXISTS `jenis_keahlian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_keahlian` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX7xfxs7vo50dkdymmpdujui29w` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_kitas`
--

DROP TABLE IF EXISTS `jenis_kitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_kitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXqy4m36ovhlqbdikwqpnideip4` (`nama`) USING BTREE,
  KEY `IDXl28c2iod6p6sug9o5b62022tm` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_kitas_aud`
--

DROP TABLE IF EXISTS `jenis_kitas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_kitas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK3q5tfxobmpfwa7u538csw362r` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_pelatihan`
--

DROP TABLE IF EXISTS `jenis_pelatihan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_pelatihan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXfhvh2rl2t8wo8k8ihl7rem9ih` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_sp`
--

DROP TABLE IF EXISTS `jenis_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `kode` varchar(10) NOT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK3rhfemkl2dgf47ij6hxm7h3uy` (`kode`) USING BTREE,
  KEY `IDX4ymiwei39jol3ppambi7ncbhf` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenis_sp_aud`
--

DROP TABLE IF EXISTS `jenis_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenis_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `kode` varchar(10) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKsbnsy5iwnk2sci8elgjkjcvc8` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `jenjang_pendidikan`
--

DROP TABLE IF EXISTS `jenjang_pendidikan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `jenjang_pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `is_statistik` bit(1) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `seq` int(11) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX46y37o1995bn49ftpsmcbprr6` (`nama`) USING BTREE,
  KEY `IDX79vc6dm5xutwmg0kad7aegjwy` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kartu_identitas`
--

DROP TABLE IF EXISTS `kartu_identitas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `kartu_identitas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nomor_kartu` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `tanggal_expired` date DEFAULT NULL,
  `tanggal_terima` date DEFAULT NULL,
  `nik` varchar(255) DEFAULT NULL,
  `jenis_kitas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK2nf4gs8xkwln5so1b54pqkak7` (`nik`,`jenis_kitas_id`) USING BTREE,
  KEY `IDX52l5aa49ludqbumw4fq0vo2nx` (`nomor_kartu`) USING BTREE,
  KEY `IDXo5uj40j1dwnnkrph726i5io3s` (`is_deleted`) USING BTREE,
  KEY `FKogvq6ij2nu8jw7cs5jehj4nlx` (`jenis_kitas_id`) USING BTREE,
  CONSTRAINT `FKlup69oa33gbn5wim4answm4kd` FOREIGN KEY (`nik`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `FKogvq6ij2nu8jw7cs5jehj4nlx` FOREIGN KEY (`jenis_kitas_id`) REFERENCES `jenis_kitas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1896 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `kartu_identitas_aud`
--

DROP TABLE IF EXISTS `kartu_identitas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `kartu_identitas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKhu5nu2yulmtqd0g0gypiyegcu` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keahlian`
--

DROP TABLE IF EXISTS `keahlian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `keahlian` (
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
  KEY `IDXbbw133asj6rk5i58oqshbf1r` (`is_deleted`) USING BTREE,
  KEY `IDX975e93t2vjbtkigoaxs0vc0sj` (`disetujui`) USING BTREE,
  KEY `FK12slgn5r1h6nduddqywk9xows` (`biodata_id`) USING BTREE,
  KEY `FK5j2f24cm94bmd91xbclncvjtb` (`jenis_keahlian_id`) USING BTREE,
  CONSTRAINT `FK12slgn5r1h6nduddqywk9xows` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `FK5j2f24cm94bmd91xbclncvjtb` FOREIGN KEY (`jenis_keahlian_id`) REFERENCES `jenis_keahlian` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=763 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keahlian_aud`
--

DROP TABLE IF EXISTS `keahlian_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `keahlian_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKabcgsri5hyemcp0cbe2ypp9fo` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_profil`
--

DROP TABLE IF EXISTS `lampiran_profil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_profil` (
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
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXnqxyy5rw082ve9ndt7bsti7ww` (`is_deleted`) USING BTREE,
  KEY `IDXcbxxx72ltdffrpex2eoky44lw` (`ref`) USING BTREE,
  KEY `IDXsr43t3b8l3f27u4q7ciqsmkke` (`ref_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_profil_aud`
--

DROP TABLE IF EXISTS `lampiran_profil_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_profil_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKqnky7ane6dowl0be7lteu8c0d` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
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
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXo5k97tgdhj3hk06vmadvny9gl` (`is_deleted`) USING BTREE,
  KEY `IDX67ffvwqihr6jhi8jli03qk7s6` (`ref`) USING BTREE,
  KEY `IDX7vu612o8y79jghb2o4vjno6nn` (`ref_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lampiran_sk_aud`
--

DROP TABLE IF EXISTS `lampiran_sk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `lampiran_sk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK1e6lxyshgk533yrwukjn6hqfs` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `level`
--

DROP TABLE IF EXISTS `level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK6y80v1qh60vnboj20ctxo4466` (`nama`) USING BTREE,
  KEY `IDX9wgoui1tq9bbge31209xlr839` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `level_aud`
--

DROP TABLE IF EXISTS `level_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `level_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`) USING BTREE,
  KEY `FKir9bvbcwdl8pw9ir9ka1u0ro8` (`rev`) USING BTREE,
  CONSTRAINT `FKir9bvbcwdl8pw9ir9ka1u0ro8` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisasi`
--

DROP TABLE IF EXISTS `organisasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organisasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `kode` varchar(255) DEFAULT NULL,
  `level_org` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `short_name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXsa1fucn04x52oql8wcsvk0m9f` (`kode`) USING BTREE,
  KEY `IDXaxnxmx8kybp4hxvyk54lcy3ta` (`nama`) USING BTREE,
  KEY `IDXoxcjalaow2dwxd19iopxyb50p` (`level_org`) USING BTREE,
  KEY `IDXfhdsiemvdo8sn9j70wx80ugx` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organisasi_aud`
--

DROP TABLE IF EXISTS `organisasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `organisasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKt53j5d0pmkrcy2njvrioh08kk` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pegawai`
--

DROP TABLE IF EXISTS `pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pegawai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  `nik` varchar(255) DEFAULT NULL,
  `gaji_profil_id` bigint(20) DEFAULT NULL,
  `golongan_id` bigint(20) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `gaji_pendapatan_non_pajak_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  `profesi_id` bigint(20) DEFAULT NULL,
  `rumah_dinas_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UKisnbt4m95wx5rulu4w0gjt6fd` (`nipam`) USING BTREE,
  KEY `IDXao1o051nqtmvcxy3ymoj7f3aa` (`tmt_pensiun`) USING BTREE,
  KEY `IDXjcy2jtw75l4ba211amu5cqv8g` (`is_deleted`) USING BTREE,
  KEY `FKdywgpysiy8mwgqnn25e9jtvky` (`gaji_profil_id`) USING BTREE,
  KEY `FK8cklqjprprg22x5hfvcqm5xeh` (`golongan_id`) USING BTREE,
  KEY `FKob6boseleyil4f8d9ogce89g1` (`grade_id`) USING BTREE,
  KEY `FKfqhtko3uv12dsru8c7aopln1v` (`jabatan_id`) USING BTREE,
  KEY `FKd4c58p3ujbnv0r1yqwr4i9jxg` (`gaji_pendapatan_non_pajak_id`) USING BTREE,
  KEY `FKdl9imf4ajcji1360yfodcgdnm` (`organisasi_id`) USING BTREE,
  KEY `FK6mfhioga8lvaftubpp0jggenv` (`profesi_id`) USING BTREE,
  KEY `FKlvrhwemgvwtqh84wb0eilkwbf` (`rumah_dinas_id`) USING BTREE,
  KEY `FKmpae090hs1mdswdqlqn1o6nid` (`nik`) USING BTREE,
  CONSTRAINT `FK6mfhioga8lvaftubpp0jggenv` FOREIGN KEY (`profesi_id`) REFERENCES `profesi` (`id`),
  CONSTRAINT `FK8cklqjprprg22x5hfvcqm5xeh` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`),
  CONSTRAINT `FKd4c58p3ujbnv0r1yqwr4i9jxg` FOREIGN KEY (`gaji_pendapatan_non_pajak_id`) REFERENCES `gaji_pendapatan_non_pajak` (`id`),
  CONSTRAINT `FKdl9imf4ajcji1360yfodcgdnm` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKdywgpysiy8mwgqnn25e9jtvky` FOREIGN KEY (`gaji_profil_id`) REFERENCES `gaji_profil` (`id`),
  CONSTRAINT `FKfqhtko3uv12dsru8c7aopln1v` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FKlvrhwemgvwtqh84wb0eilkwbf` FOREIGN KEY (`rumah_dinas_id`) REFERENCES `rumah_dinas` (`id`),
  CONSTRAINT `FKmpae090hs1mdswdqlqn1o6nid` FOREIGN KEY (`nik`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `FKob6boseleyil4f8d9ogce89g1` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=575 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pegawai_aud`
--

DROP TABLE IF EXISTS `pegawai_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pegawai_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKlao5sw0ysp1sdily7ao4lffv` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pelatihan`
--

DROP TABLE IF EXISTS `pelatihan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pelatihan` (
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
  KEY `IDX12tj1xxayshc5jj7ffsva80wl` (`is_deleted`) USING BTREE,
  KEY `FK3glvwcgryiubq55umiijpiwua` (`biodata_id`) USING BTREE,
  KEY `FKosgpc63qwymbvkmks2tcdj52o` (`jenis_pelatihan_id`) USING BTREE,
  CONSTRAINT `FK3glvwcgryiubq55umiijpiwua` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `FKosgpc63qwymbvkmks2tcdj52o` FOREIGN KEY (`jenis_pelatihan_id`) REFERENCES `jenis_pelatihan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10449 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pelatihan_aud`
--

DROP TABLE IF EXISTS `pelatihan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pelatihan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKd9do03o1gvl1bhl607noje3k1` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pendidikan`
--

DROP TABLE IF EXISTS `pendidikan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pendidikan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `UKaj1q73dlgqmio4s6785xvtvm5` (`biodata_id`,`jenjang_id`,`tahun_masuk`) USING BTREE,
  KEY `IDXk3t3mmu4quvwl5olwilohl4il` (`jenjang_id`) USING BTREE,
  KEY `IDXl6ko8xm11ghslb1hdf3wc7ucj` (`is_deleted`) USING BTREE,
  KEY `IDXmp0nrpamaxrf6tli4t36v4qa9` (`is_latest`) USING BTREE,
  KEY `IDXls2xihe492jtwnr3yh60f8xe2` (`disetujui_oleh`) USING BTREE,
  CONSTRAINT `FK64bc36wviclglokxfdn10efut` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`),
  CONSTRAINT `FKtq9hxlq4cghvmc2ffn2hgnjoy` FOREIGN KEY (`jenjang_id`) REFERENCES `jenjang_pendidikan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2136 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pendidikan_aud`
--

DROP TABLE IF EXISTS `pendidikan_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pendidikan_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKb2mjb0g4i7t2f7u0r33uikm28` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pengalaman_kerja`
--

DROP TABLE IF EXISTS `pengalaman_kerja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pengalaman_kerja` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  KEY `IDXo52n89yl6ivygurlkbbiuteum` (`is_deleted`) USING BTREE,
  KEY `FKtfwlou3lu3982trudgjvsq8uh` (`biodata_id`) USING BTREE,
  CONSTRAINT `FKtfwlou3lu3982trudgjvsq8uh` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pengalaman_kerja_aud`
--

DROP TABLE IF EXISTS `pengalaman_kerja_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pengalaman_kerja_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKok3dy65wlduwdo4kbdn18ob7w` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pref_role`
--

DROP TABLE IF EXISTS `pref_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pref_role` (
  `id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profesi`
--

DROP TABLE IF EXISTS `profesi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `resiko` varchar(255) DEFAULT NULL,
  `grade_id` bigint(20) DEFAULT NULL,
  `jabatan_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `organisasi_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXdf2v2d8m8kj2fno6jia21aqwu` (`nama`) USING BTREE,
  KEY `IDXa6ttub6neno90pt9pdompahy9` (`is_deleted`) USING BTREE,
  KEY `FK9mtqtfvreok33qvc1h05d09vp` (`grade_id`) USING BTREE,
  KEY `FK7y0j8pf2rxywrwhqybj06va69` (`jabatan_id`) USING BTREE,
  KEY `FKgrqrca3adjk3bj0u9rgqwplfy` (`level_id`) USING BTREE,
  KEY `FKpdo18v9pyfninmfhqd1rjop6o` (`organisasi_id`) USING BTREE,
  CONSTRAINT `FK7y0j8pf2rxywrwhqybj06va69` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FK9mtqtfvreok33qvc1h05d09vp` FOREIGN KEY (`grade_id`) REFERENCES `grade` (`id`),
  CONSTRAINT `FKgrqrca3adjk3bj0u9rgqwplfy` FOREIGN KEY (`level_id`) REFERENCES `level` (`id`),
  CONSTRAINT `FKpdo18v9pyfninmfhqd1rjop6o` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profesi_aud`
--

DROP TABLE IF EXISTS `profesi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKskug4my2ghtumiy06fof1gs65` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profil_keluarga`
--

DROP TABLE IF EXISTS `profil_keluarga`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profil_keluarga` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `uc_profilkeluarga_nik` (`biodata_id`,`version`,`nama`,`tanggal_lahir`,`is_deleted`) USING BTREE,
  KEY `FK1q84v1jnhu5rjwt62mecuq5p8` (`pendidikan_id`) USING BTREE,
  KEY `idx_profilkeluarga_nik` (`nik`) USING BTREE,
  KEY `idx_profilkeluarga_nama` (`nama`) USING BTREE,
  KEY `idx_profilkeluarga_is_deleted` (`is_deleted`) USING BTREE,
  KEY `idx_profilkeluarga_tanggungan` (`tanggungan`) USING BTREE,
  CONSTRAINT `FK1q84v1jnhu5rjwt62mecuq5p8` FOREIGN KEY (`pendidikan_id`) REFERENCES `jenjang_pendidikan` (`id`),
  CONSTRAINT `FKp5imt94n88qu16sbjgi4x4hhn` FOREIGN KEY (`biodata_id`) REFERENCES `biodata` (`nik`)
) ENGINE=InnoDB AUTO_INCREMENT=1094 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profil_keluarga_aud`
--

DROP TABLE IF EXISTS `profil_keluarga_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `profil_keluarga_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKb319jiq2h9bhxoos7xvahbji` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revinfo`
--

DROP TABLE IF EXISTS `revinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `revinfo` (
  `rev` int(11) NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_cuti`
--

DROP TABLE IF EXISTS `riwayat_cuti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_cuti` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXrkdufm3p19r35f2uu1ayy78gd` (`is_deleted`) USING BTREE,
  KEY `FKq8hbg0vdwlghbjoxapqpqkf8f` (`pegawai_id`) USING BTREE,
  CONSTRAINT `FKq8hbg0vdwlghbjoxapqpqkf8f` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_cuti_aud`
--

DROP TABLE IF EXISTS `riwayat_cuti_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_cuti_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `pegawai_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKlviq0lt0woh8x1xocvw4ut8jf` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_keluar`
--

DROP TABLE IF EXISTS `riwayat_keluar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_keluar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  KEY `IDX4303d82n5vyrvm1kxp1qv63fw` (`tanggal_permohonan`) USING BTREE,
  KEY `IDXs3gb8ts46k0eheqbi3res4yya` (`tanggal_persetujuan`) USING BTREE,
  KEY `IDXnuaktfstuwpo9ax32vcwiklwy` (`is_deleted`) USING BTREE,
  KEY `FK57u6wudd5sq037fe4l894iqf1` (`disetujui_oleh`) USING BTREE,
  KEY `FKiei6jfqnroni5v6ry1ys8hxph` (`jabatan_id`) USING BTREE,
  KEY `FK3lvct7ovnocjdjblqtsdvagcn` (`penyetuju_jabatan_id`) USING BTREE,
  KEY `FKd5s3rs6pk2mk58wgmuux14yjx` (`organisasi_id`) USING BTREE,
  KEY `FKr987kq4dboeaf3fycg5d8yc2b` (`penyetuju_organisasi_id`) USING BTREE,
  KEY `FKl69jx9od4ll637wkc99n2tm6m` (`pegawai_id`) USING BTREE,
  CONSTRAINT `FK3lvct7ovnocjdjblqtsdvagcn` FOREIGN KEY (`penyetuju_jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FK57u6wudd5sq037fe4l894iqf1` FOREIGN KEY (`disetujui_oleh`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKd5s3rs6pk2mk58wgmuux14yjx` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKiei6jfqnroni5v6ry1ys8hxph` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FKl69jx9od4ll637wkc99n2tm6m` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKr987kq4dboeaf3fycg5d8yc2b` FOREIGN KEY (`penyetuju_organisasi_id`) REFERENCES `organisasi` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_kontrak`
--

DROP TABLE IF EXISTS `riwayat_kontrak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_kontrak` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `UK29scgdjh0pqy24y60lq1mtecq` (`pegawai_id`,`nomor_kontrak`) USING BTREE,
  KEY `IDXl5f85xvlsg8xhi9hg4ewp3lt0` (`nomor_kontrak`) USING BTREE,
  KEY `IDXfly936vec9jq13y7849f885qg` (`tanggal_mulai`) USING BTREE,
  KEY `IDX4572jmrmniqxid5x5clnco7ta` (`is_deleted`) USING BTREE,
  KEY `FKnj30whk7609auo3sxh5d2ekjo` (`jabatan_id`) USING BTREE,
  KEY `FKh265iad1i84d7jxixkqed7h6t` (`organisasi_id`) USING BTREE,
  CONSTRAINT `FKdub36mt3wn43m6yrmankm2vow` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKh265iad1i84d7jxixkqed7h6t` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKnj30whk7609auo3sxh5d2ekjo` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=298 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_kontrak_aud`
--

DROP TABLE IF EXISTS `riwayat_kontrak_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_kontrak_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FK4are3ylgfj39xa7mp1u7ovcn4` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_mutasi`
--

DROP TABLE IF EXISTS `riwayat_mutasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_mutasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `UK3kcomkcxj12ct7qpphyrfgf7a` (`pegawai_id`,`riwayat_sk_id`) USING BTREE,
  KEY `IDXk6u8m763ghr8tg2q59ckb7wx7` (`is_deleted`) USING BTREE,
  KEY `FK4aphjcvfb1eqnwb9ppx0f3h8w` (`riwayat_sk_id`) USING BTREE,
  CONSTRAINT `FK4aphjcvfb1eqnwb9ppx0f3h8w` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`),
  CONSTRAINT `FKgkfsjfw66awvp7ve72jwuusaq` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=971 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_mutasi_aud`
--

DROP TABLE IF EXISTS `riwayat_mutasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_mutasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKb3ggxr4norschwqcyfr987axy` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sk`
--

DROP TABLE IF EXISTS `riwayat_sk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  KEY `IDX882gv5nau8lwry4hlml5hmb7m` (`nomor_sk`) USING BTREE,
  KEY `IDXouq7s77krd1g3g8h6jpep0vne` (`nipam`) USING BTREE,
  KEY `IDXmarsv7hd37q67rs3tmvk8ln1c` (`nama`) USING BTREE,
  KEY `IDXn0xlylmugo3l3hfrlo0g7lwlw` (`tanggal_sk`) USING BTREE,
  KEY `IDXblwdymmys5g1oru6kgqt3xs96` (`mkg_tahun`) USING BTREE,
  KEY `IDXaj7k5li46qabctnfnvf0bonx6` (`mkgb_tahun`) USING BTREE,
  KEY `IDX1nvfjfgruxhbp8hsinrj9xwoj` (`is_deleted`) USING BTREE,
  KEY `FKssdkk6in2b2rp1fmts4n4mu7r` (`golongan_id`) USING BTREE,
  KEY `FKs8em44aff7jvh06i31wfpjnjg` (`pegawai_id`) USING BTREE,
  CONSTRAINT `FKs8em44aff7jvh06i31wfpjnjg` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKssdkk6in2b2rp1fmts4n4mu7r` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7975 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sk_aud`
--

DROP TABLE IF EXISTS `riwayat_sk_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sk_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKp3m078bv90rt7px9nmagvslr5` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sp`
--

DROP TABLE IF EXISTS `riwayat_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
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
  `notes` varchar(255) DEFAULT NULL,
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
  KEY `IDX9byhsag4c837gm2cyn3oa8rks` (`nomor_sp`) USING BTREE,
  KEY `IDX482og76fse137vvuhewppw4hs` (`tanggal_sp`) USING BTREE,
  KEY `IDXfrm7005gbxjrd5n5evvpfs341` (`is_deleted`) USING BTREE,
  KEY `FK2q8e6hbp5n4dbgbtp1yvuvg9e` (`jabatan_id`) USING BTREE,
  KEY `FKbig7gak8csif6hu1096nxtu04` (`jenis_sp_id`) USING BTREE,
  KEY `FKch0qhfc0ih9i6qlag1eea1c7f` (`organisasi_id`) USING BTREE,
  KEY `FKme2kgemscnc9eb7u7fs1lf8n` (`pegawai_id`) USING BTREE,
  KEY `FKi7myy0nwfd6i2tqe2k2fo0pwb` (`sanksi_id`) USING BTREE,
  CONSTRAINT `FK2q8e6hbp5n4dbgbtp1yvuvg9e` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FKbig7gak8csif6hu1096nxtu04` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`),
  CONSTRAINT `FKch0qhfc0ih9i6qlag1eea1c7f` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKi7myy0nwfd6i2tqe2k2fo0pwb` FOREIGN KEY (`sanksi_id`) REFERENCES `sanksi_sp` (`id`),
  CONSTRAINT `FKme2kgemscnc9eb7u7fs1lf8n` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_sp_aud`
--

DROP TABLE IF EXISTS `riwayat_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  `notes` varchar(255) DEFAULT NULL,
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
  CONSTRAINT `FK7iah35ihqr5736omeremethn4` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_terminasi`
--

DROP TABLE IF EXISTS `riwayat_terminasi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_terminasi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `UKrov05ve9uioon25c0bmljmv0` (`pegawai_id`) USING BTREE,
  KEY `IDXexk6t51xuifxp2ocw9rqydfv7` (`nipam`) USING BTREE,
  KEY `IDXggf8ocaxnq7bxrj4dtg5w6vlq` (`nama`) USING BTREE,
  KEY `IDXrv3h5a4wjysno8uclwrtfnrkp` (`nomor_sk`) USING BTREE,
  KEY `IDXme7p1lddrqlv6kjs5yggvbe8s` (`tanggal_terminasi`) USING BTREE,
  KEY `FKfbdmeg2vyr1rhdq7u2ruvtcc4` (`alasan_terminasi_id`) USING BTREE,
  KEY `FK9n6xxa3onbwarlkx3gi1r04ri` (`riwayat_sk_id`) USING BTREE,
  KEY `FKmq5496lfdg1pm8rf5h9dlop4l` (`golongan_id`) USING BTREE,
  KEY `FKgvqu47wbafpfnd25wpdiuko1e` (`jabatan_id`) USING BTREE,
  KEY `FKbljn2u6fw0pmea0tkhh8en0fk` (`organisasi_id`) USING BTREE,
  CONSTRAINT `FK9n6xxa3onbwarlkx3gi1r04ri` FOREIGN KEY (`riwayat_sk_id`) REFERENCES `riwayat_sk` (`id`),
  CONSTRAINT `FKbljn2u6fw0pmea0tkhh8en0fk` FOREIGN KEY (`organisasi_id`) REFERENCES `organisasi` (`id`),
  CONSTRAINT `FKcw1918nhrcakvd4hw1b9pkh78` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`id`),
  CONSTRAINT `FKfbdmeg2vyr1rhdq7u2ruvtcc4` FOREIGN KEY (`alasan_terminasi_id`) REFERENCES `alasan_berhenti` (`id`),
  CONSTRAINT `FKgvqu47wbafpfnd25wpdiuko1e` FOREIGN KEY (`jabatan_id`) REFERENCES `jabatan` (`id`),
  CONSTRAINT `FKmq5496lfdg1pm8rf5h9dlop4l` FOREIGN KEY (`golongan_id`) REFERENCES `golongan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `riwayat_terminasi_aud`
--

DROP TABLE IF EXISTS `riwayat_terminasi_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `riwayat_terminasi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
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
  CONSTRAINT `FKq57nupqntctuoshi7sm1o68qw` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rumah_dinas`
--

DROP TABLE IF EXISTS `rumah_dinas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rumah_dinas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDXd0vnqkhyiixsqx0ahw8y7c5pr` (`nama`) USING BTREE,
  KEY `IDXfomdcvgxl78tjlpji2drlqs5u` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rumah_dinas_aud`
--

DROP TABLE IF EXISTS `rumah_dinas_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `rumah_dinas_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nama` varchar(255) DEFAULT NULL,
  `nilai` double DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FKmc1n1yq0m2gal0skke1plnw0o` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanksi_sp`
--

DROP TABLE IF EXISTS `sanksi_sp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanksi_sp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `changed_status` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
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
  UNIQUE KEY `UKp71coj7jr8ch1tl5cvuhlpoga` (`kode`) USING BTREE,
  KEY `IDXtn6xhiriw0i0hfntp2afhilv2` (`is_deleted`) USING BTREE,
  KEY `FKg0oh1pkayjn4gjcfe0hr9celt` (`jenis_sp_id`) USING BTREE,
  CONSTRAINT `FKg0oh1pkayjn4gjcfe0hr9celt` FOREIGN KEY (`jenis_sp_id`) REFERENCES `jenis_sp` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sanksi_sp_aud`
--

DROP TABLE IF EXISTS `sanksi_sp_aud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `sanksi_sp_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
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
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `FK9npxhi3fb4jv5n71hrq40yrie` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `statistik_pegawai`
--

DROP TABLE IF EXISTS `statistik_pegawai`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
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
  UNIQUE KEY `UKavurxo7a986mtugglrfro8mb8` (`bulan`,`tahun`,`pendidikan`) USING BTREE,
  KEY `bulan_idx` (`bulan`) USING BTREE,
  KEY `tahun_idx` (`tahun`) USING BTREE,
  KEY `pendidikan_idx` (`pendidikan`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=423 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary table structure for view `v_pegawai`
--

DROP TABLE IF EXISTS `v_pegawai`;
/*!50001 DROP VIEW IF EXISTS `v_pegawai`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8mb4;
/*!50001 CREATE VIEW `v_pegawai` AS SELECT
 1 AS `id`,
  1 AS `nipam`,
  1 AS `nik`,
  1 AS `nama`,
  1 AS `jenis_kelamin`,
  1 AS `status_kawin`,
  1 AS `tempat_lahir`,
  1 AS `tanggal_lahir`,
  1 AS `organisasi_id`,
  1 AS `nama_organisasi`,
  1 AS `jabatan_id`,
  1 AS `nama_jabatan`,
  1 AS `golongan_id`,
  1 AS `golongan`,
  1 AS `pangkat`,
  1 AS `status_kerja`,
  1 AS `status_pegawai` */;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'kepegawaian'
--

--
-- Dumping routines for database 'kepegawaian'
--

--
-- Final view structure for view `v_pegawai`
--

/*!50001 DROP VIEW IF EXISTS `v_pegawai`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `v_pegawai` AS select `peg`.`id` AS `id`,`peg`.`nipam` AS `nipam`,`bio`.`nik` AS `nik`,`bio`.`nama` AS `nama`,`bio`.`jenis_kelamin` AS `jenis_kelamin`,`bio`.`status_kawin` AS `status_kawin`,`bio`.`tempat_lahir` AS `tempat_lahir`,`bio`.`tanggal_lahir` AS `tanggal_lahir`,`org`.`id` AS `organisasi_id`,`org`.`nama` AS `nama_organisasi`,`jab`.`id` AS `jabatan_id`,`jab`.`nama` AS `nama_jabatan`,`gol`.`id` AS `golongan_id`,`gol`.`golongan` AS `golongan`,`gol`.`pangkat` AS `pangkat`,`peg`.`status_kerja` AS `status_kerja`,`peg`.`status_pegawai` AS `status_pegawai` from ((((`pegawai` `peg` join `biodata` `bio` on(`peg`.`nik` = `bio`.`nik`)) join `organisasi` `org` on(`peg`.`organisasi_id` = `org`.`id`)) join `jabatan` `jab` on(`peg`.`jabatan_id` = `jab`.`id`)) join `golongan` `gol` on(`peg`.`golongan_id` = `gol`.`id`)) where `peg`.`status_kerja` in (1,2) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-23 11:53:42

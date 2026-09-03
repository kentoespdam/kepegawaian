/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.183_3306
 Source Server Type    : MySQL
 Source Server Version : 50614
 Source Host           : 192.168.1.183:3306
 Source Schema         : smartoffice

 Target Server Type    : MySQL
 Target Server Version : 50614
 File Encoding         : 65001

 Date: 03/09/2026 10:29:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization` (
                                `org_id` int(11) NOT NULL AUTO_INCREMENT,
                                `org_code` varchar(16) DEFAULT NULL,
                                `org_name` varchar(64) DEFAULT NULL,
                                `org_level` tinyint(1) DEFAULT NULL,
                                `org_parent` int(11) DEFAULT NULL,
                                `org_level_1` int(11) DEFAULT NULL,
                                `org_level_2` int(11) DEFAULT NULL,
                                `org_level_3` int(11) DEFAULT NULL,
                                `org_level_4` int(11) DEFAULT NULL,
                                `org_level_5` int(11) DEFAULT NULL,
                                `org_status` enum('Enabled','Disabled','Deleted') DEFAULT 'Enabled',
                                `mail_code` varchar(8) DEFAULT NULL,
                                `office_code` varchar(8) DEFAULT '',
                                `group` varchar(64) DEFAULT '',
                                `category` varchar(45) DEFAULT NULL,
                                PRIMARY KEY (`org_id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of organization
-- ----------------------------
BEGIN;
INSERT INTO `organization` VALUES (1, 'DPW', 'DEWAN PENGAWAS', 1, 0, 1, NULL, NULL, NULL, NULL, 'Enabled', '', 'PUSAT', '', NULL);
INSERT INTO `organization` VALUES (2, 'D1', 'DIREKTORAT UTAMA', 2, 1, 1, 2, NULL, NULL, NULL, 'Enabled', 'DIR', 'PUSAT', '01.DIREKSI', 'ADM');
INSERT INTO `organization` VALUES (3, 'D2', 'DIREKTORAT TEKNIK', 3, 2, 1, 2, 3, NULL, NULL, 'Enabled', 'DIR', 'PUSAT', '01.DIREKSI', 'TEKNIK');
INSERT INTO `organization` VALUES (4, 'D3', 'DIREKTORAT ADMIN & KEUANGAN', 3, 2, 1, 2, 4, NULL, NULL, 'Enabled', 'DIR', 'PUSAT', '01.DIREKSI', 'ADM');
INSERT INTO `organization` VALUES (5, 'B1', 'SATUAN PENGAWAS INTERN', 3, 2, 1, 2, 5, NULL, NULL, 'Enabled', 'BPI', 'PUSAT', '02.SATUAN PENGAWAS INTERN', 'ADM');
INSERT INTO `organization` VALUES (6, 'B2', 'BID. PENELITIAN & PENGEMBANGAN', 3, 2, 1, 2, 6, NULL, NULL, 'Deleted', 'LITBANG', 'PUSAT', '03.BIDANG PENELITIAN & PENGEMBANGAN', 'ADM');
INSERT INTO `organization` VALUES (7, 'B3', 'BID. TEKNOLOGI INFORMASI', 3, 2, 1, 2, 7, NULL, NULL, 'Deleted', 'IT', 'ALL', '04.BIDANG TEKNOLOGI INFORMASI', 'ADM');
INSERT INTO `organization` VALUES (8, 'C1', 'CABANG PURWOKERTO 1', 3, 2, 1, 2, 8, NULL, NULL, 'Enabled', 'PWK-1', 'PWK-1', '11.CABANG PURWOKERTO 1', 'ADM');
INSERT INTO `organization` VALUES (9, 'C2', 'CABANG PURWOKERTO 2', 3, 2, 1, 2, 9, NULL, NULL, 'Enabled', 'PWK-2', 'PWK-2', '12.CABANG PURWOKERTO 2', 'ADM');
INSERT INTO `organization` VALUES (10, 'C3', 'CABANG AJIBARANG', 3, 2, 1, 2, 10, NULL, NULL, 'Enabled', 'AJIB', 'AJB', '13.CABANG AJIBARANG', 'ADM');
INSERT INTO `organization` VALUES (11, 'C4', 'CABANG WANGON', 3, 2, 1, 2, 11, NULL, NULL, 'Enabled', 'WGN', 'WGN', '14.CABANG WANGON', 'ADM');
INSERT INTO `organization` VALUES (12, 'C5', 'CABANG BANYUMAS', 3, 2, 1, 2, 12, NULL, NULL, 'Enabled', 'BMS', 'BMS', '15.CABANG BANYUMAS', 'ADM');
INSERT INTO `organization` VALUES (13, 'BA1', 'BAG. KEUANGAN', 4, 4, 1, 2, 4, 13, NULL, 'Enabled', 'KUG', 'PUSAT', '08.BAGIAN KEUANGAN', 'ADM');
INSERT INTO `organization` VALUES (14, 'BA2', 'BAG. HUMAS & HUKUM', 4, 4, 1, 2, 4, 14, NULL, 'Deleted', 'HUMAS', 'PUSAT', '06.BAGIAN HUMAS & HUKUM', 'ADM');
INSERT INTO `organization` VALUES (15, 'BA3', 'BAG. KESEKRETARIATAN', 4, 4, 1, 2, 4, 15, NULL, 'Enabled', 'SEKRE', 'PUSAT', '07.BAGIAN KESEKRETARIATAN', 'ADM');
INSERT INTO `organization` VALUES (16, 'BA4', 'BAG. SUMBER DAYA MANUSIA & TI', 4, 4, 1, 2, 4, 16, NULL, 'Enabled', 'SDM', 'PUSAT', '09.BAGIAN SUMBER DAYA MANUSIA & TI', 'ADM');
INSERT INTO `organization` VALUES (17, 'BA5', 'BAG. PERENCANAAN & PENGEMBANGAN', 4, 3, 1, 2, 3, 17, NULL, 'Enabled', 'RENBANG', 'PUSAT', '03.BAGIAN PERENCANAAN & PENGEMBANGAN', 'TEKNIK');
INSERT INTO `organization` VALUES (18, 'BA6', 'BAG. PRODUKSI & DISTRIBUSI 1', 4, 3, 1, 2, 3, 18, NULL, 'Enabled', 'PRODIS1', 'PUSAT', '04.BAGIAN PRODUKSI & DISTRIBUSI 1', 'TEKNIK');
INSERT INTO `organization` VALUES (19, 'BA7', 'BAG. PRODUKSI & DISTRIBUSI 2', 4, 3, 1, 2, 3, 19, NULL, 'Enabled', 'PRODIS2', 'PUSAT', '05.BAGIAN PRODUKSI & DISTRIBUSI 2', 'TEKNIK');
INSERT INTO `organization` VALUES (20, 'BA8', 'BAG. PENGENDALIAN TEKNIK', 4, 3, 1, 2, 3, 20, NULL, 'Enabled', 'DALTEK', 'PUSAT', '06.BAGIAN PENGENDALIAN TEKNIK', 'TEKNIK');
INSERT INTO `organization` VALUES (21, 'C1.1', 'SUB BAG ADM & KEU CAB. PWKT 1', 4, 8, 1, 2, 8, 21, NULL, 'Enabled', 'PWK-1', 'PWK-1', '11.CABANG PURWOKERTO 1', 'ADM');
INSERT INTO `organization` VALUES (22, 'C1.2', 'SUB BAG UMUM CAB. PWKT 1', 4, 8, 1, 2, 8, 22, NULL, 'Deleted', 'PWK-1', 'PWK-1', '13.CABANG PURWOKERTO 1', 'ADM');
INSERT INTO `organization` VALUES (23, 'C1.3', 'SUB BAG PELAYANAN CAB. PWKT 1', 4, 8, 1, 2, 8, 23, NULL, 'Enabled', 'PWK-1', 'PWK-1', '11.CABANG PURWOKERTO 1', 'PELAYANAN');
INSERT INTO `organization` VALUES (24, 'C1.4', 'SUB BAG TEKNIK CAB. PWKT 1', 4, 8, 1, 2, 8, 24, NULL, 'Enabled', 'PWK-1', 'PWK-1', '11.CABANG PURWOKERTO 1', 'TEKNIK');
INSERT INTO `organization` VALUES (25, 'C2.1', 'SUB BAG ADM & KEU CAB. PWKT 2', 4, 9, 1, 2, 9, 25, NULL, 'Enabled', 'PWK-2', 'PWK-2', '12.CABANG PURWOKERTO 2', 'ADM');
INSERT INTO `organization` VALUES (26, 'C2.2', 'SUB BAG UMUM CAB. PWKT 2', 4, 9, 1, 2, 9, 26, NULL, 'Deleted', 'PWK-2', 'PWK-2', '14.CABANG PURWOKERTO 2', 'ADM');
INSERT INTO `organization` VALUES (27, 'C2.3', 'SUB BAG PELAYANAN CAB. PWKT 2', 4, 9, 1, 2, 9, 27, NULL, 'Enabled', 'PWK-2', 'PWK-2', '12.CABANG PURWOKERTO 2', 'PELAYANAN');
INSERT INTO `organization` VALUES (28, 'C2.4', 'SUB BAG TEKNIK CAB. PWKT 2', 4, 9, 1, 2, 9, 28, NULL, 'Enabled', 'PWK-2', 'PWK-2', '12.CABANG PURWOKERTO 2', 'TEKNIK');
INSERT INTO `organization` VALUES (29, 'C3.1', 'SUB BAG ADM & KEU CAB. AJIBARANG', 4, 10, 1, 2, 10, 29, NULL, 'Enabled', 'AJIB', 'AJB', '13.CABANG AJIBARANG', 'ADM');
INSERT INTO `organization` VALUES (30, 'C3.2', 'SUB BAG UMUM CAB. AJIBARANG', 4, 10, 1, 2, 10, 30, NULL, 'Deleted', 'AJIB', 'AJB', '15.CABANG AJIBARANG', 'ADM');
INSERT INTO `organization` VALUES (31, 'C3.3', 'SUB BAG PELAYANAN CAB. AJIBARANG', 4, 10, 1, 2, 10, 31, NULL, 'Enabled', 'AJIB', 'AJB', '13.CABANG AJIBARANG', 'PELAYANAN');
INSERT INTO `organization` VALUES (32, 'C3.4', 'SUB BAG TEKNIK CAB. AJIBARANG', 4, 10, 1, 2, 10, 32, NULL, 'Enabled', 'AJIB', 'AJB', '13.CABANG AJIBARANG', 'TEKNIK');
INSERT INTO `organization` VALUES (33, 'C4.1', 'SUB BAG ADM & KEU CAB. WANGON', 4, 11, 1, 2, 11, 33, NULL, 'Enabled', 'WGN', 'WGN', '14.CABANG WANGON', 'ADM');
INSERT INTO `organization` VALUES (34, 'C4.2', 'SUB BAG UMUM CAB. WANGON', 4, 11, 1, 2, 11, 34, NULL, 'Deleted', 'WGN', 'WGN', '16.CABANG WANGON', 'ADM');
INSERT INTO `organization` VALUES (35, 'C4.3', 'SUB BAG PELAYANAN CAB. WANGON', 4, 11, 1, 2, 11, 35, NULL, 'Enabled', 'WGN', 'WGN', '14.CABANG WANGON', 'PELAYANAN');
INSERT INTO `organization` VALUES (36, 'C4.4', 'SUB BAG TEKNIK CAB. WANGON', 4, 11, 1, 2, 11, 36, NULL, 'Enabled', 'WGN', 'WGN', '14.CABANG WANGON', 'TEKNIK');
INSERT INTO `organization` VALUES (37, 'C5.1', 'SUB BAG ADM & KEU CAB. BANYUMAS', 4, 12, 1, 2, 12, 37, NULL, 'Enabled', 'BMS', 'BMS', '15.CABANG BANYUMAS', 'ADM');
INSERT INTO `organization` VALUES (38, 'C5.2', 'SUB BAG UMUM CAB. BANYUMAS', 4, 12, 1, 2, 12, 38, NULL, 'Deleted', 'BMS', 'BMS', '17.CABANG BANYUMAS', 'ADM');
INSERT INTO `organization` VALUES (39, 'C5.3', 'SUB BAG PELAYANAN CAB. BANYUMAS', 4, 12, 1, 2, 12, 39, NULL, 'Enabled', 'BMS', 'BMS', '15.CABANG BANYUMAS', 'PELAYANAN');
INSERT INTO `organization` VALUES (40, 'C5.4', 'SUB BAG TEKNIK CAB. BANYUMAS', 4, 12, 1, 2, 12, 40, NULL, 'Enabled', 'BMS', 'BMS', '15.CABANG BANYUMAS', 'TEKNIK');
INSERT INTO `organization` VALUES (41, 'BA1.3', 'SUB BAG ANGGARAN & PELAPORAN', 5, 13, 1, 2, 4, 13, 41, 'Enabled', 'KUG', 'PUSAT', '08.BAGIAN KEUANGAN', 'ADM');
INSERT INTO `organization` VALUES (42, 'BA1.1', 'SUB BAG AKUNTANSI', 5, 13, 1, 2, 4, 13, 42, 'Enabled', 'KUG', 'PUSAT', '08.BAGIAN KEUANGAN', 'ADM');
INSERT INTO `organization` VALUES (43, 'BA1.2', 'SUB BAG PERBENDAHARAAN', 5, 13, 1, 2, 4, 13, 43, 'Enabled', 'KUG', 'PUSAT', '08.BAGIAN KEUANGAN', 'ADM');
INSERT INTO `organization` VALUES (44, 'BA3.3', 'SUB BAG HUMAS', 5, 15, 1, 2, 4, 15, 44, 'Enabled', 'SEKRE', 'PUSAT', '07.BAGIAN KESEKRETARIATAN', 'ADM');
INSERT INTO `organization` VALUES (45, 'B4.1', 'SUB BAG PEMASARAN', 4, 68, 1, 2, 68, 45, NULL, 'Enabled', 'AMDK', 'PUSAT', '16.UNIT BISNIS AMDK', 'ADM');
INSERT INTO `organization` VALUES (46, 'BA3.2', 'SUB BAG HUKUM & PERIZINAN', 5, 15, 1, 2, 4, 15, 46, 'Enabled', 'SEKRE', 'PUSAT', '07.BAGIAN KESEKRETARIATAN', 'ADM');
INSERT INTO `organization` VALUES (47, 'BA3.1', 'SUB BAG TATA USAHA & RUMAH TANGGA', 5, 15, 1, 2, 4, 15, 47, 'Enabled', 'SEKRE', 'PUSAT', '07.BAGIAN KESEKRETARIATAN', 'ADM');
INSERT INTO `organization` VALUES (48, 'BA9.3', 'SUB BAG ASET', 5, 69, 1, 2, 4, 69, 48, 'Enabled', 'PRLKP', 'PUSAT', '10.BAGIAN PERLENGKAPAN', 'ADM');
INSERT INTO `organization` VALUES (49, 'BA9.1', 'SUB BAG PENGADAAN', 5, 69, 1, 2, 4, 69, 49, 'Enabled', 'PRLKP', 'PUSAT', '10.BAGIAN PERLENGKAPAN', 'ADM');
INSERT INTO `organization` VALUES (50, 'BA4.1', 'SUB BAG ADM & PENGEMBANGAN SDM', 5, 16, 1, 2, 4, 16, 50, 'Enabled', 'SDM', 'PUSAT', '09.BAGIAN SUMBER DAYA MANUSIA & TI', 'ADM');
INSERT INTO `organization` VALUES (51, 'BA4.2', 'SUB BAG REMUNERASI & K3', 5, 16, 1, 2, 4, 16, 51, 'Enabled', 'SDM', 'PUSAT', '09.BAGIAN SUMBER DAYA MANUSIA & TI', 'ADM');
INSERT INTO `organization` VALUES (52, 'BA5.1', 'SUB BAG PERENCANAAN', 5, 17, 1, 2, 3, 17, 52, 'Enabled', 'RENBANG', 'PUSAT', '03.BAGIAN PERENCANAAN & PENGEMBANGAN', 'TEKNIK');
INSERT INTO `organization` VALUES (53, 'B1.3', 'SUB BAG PENGAWASAN PEKERJAAN', 4, 5, 1, 2, 5, 53, NULL, 'Enabled', 'RENBANG', 'PUSAT', '02.SATUAN PENGAWAS INTERN', 'TEKNIK');
INSERT INTO `organization` VALUES (54, 'BA6.1', 'SUB BAG PENGENDALIAN PRODUKSI 1', 5, 18, 1, 2, 3, 18, 54, 'Enabled', 'PRODIS1', 'PUSAT', '04.BAGIAN PRODUKSI & DISTRIBUSI 1', 'TEKNIK');
INSERT INTO `organization` VALUES (55, 'BA8.1', 'SUB BAG PENJAMINAN MUTU AIR', 5, 20, 1, 2, 3, 20, 55, 'Enabled', 'DALTEK', 'PUSAT', '06.BAGIAN PENGENDALIAN TEKNIK', 'TEKNIK');
INSERT INTO `organization` VALUES (56, 'BA6.2', 'SUB BAG PENGENDALIAN DISTRIBUSI 1', 5, 18, 1, 2, 3, 18, 56, 'Enabled', 'PRODIS1', 'PUSAT', '04.BAGIAN PRODUKSI & DISTRIBUSI 1', 'TEKNIK');
INSERT INTO `organization` VALUES (57, 'BA7.2', 'SUB BAG PENGENDALIAN DISTRIBUSI 2', 5, 19, 1, 2, 3, 19, 57, 'Enabled', 'PRODIS2', 'PUSAT', '05.BAGIAN PRODUKSI & DISTRIBUSI 2', 'TEKNIK');
INSERT INTO `organization` VALUES (58, 'BA8.3', 'SUB BAG MEKANIKAL & ELEKTRIKAL', 5, 20, 1, 2, 3, 20, 58, 'Enabled', 'DALTEK', 'PUSAT', '06.BAGIAN PENGENDALIAN TEKNIK', 'TEKNIK');
INSERT INTO `organization` VALUES (59, 'BA8.2', 'SUB BAG REHAB JARINGAN PIPA', 5, 20, 1, 2, 3, 20, 59, 'Deleted', 'REHAB', 'PUSAT', '12.BAGIAN REHABILITASI & PENGENDALIAN PEMELIHARAAN', 'TEKNIK');
INSERT INTO `organization` VALUES (60, 'B1.1', 'SUB BAG STANDARDISASI', 4, 5, 1, 2, 5, 60, NULL, 'Enabled', 'BPI', 'PUSAT', '02.SATUAN PENGAWAS INTERN', 'ADM');
INSERT INTO `organization` VALUES (61, 'B1.2', 'SUB BAG AUDIT INTERN', 4, 5, 1, 2, 5, 61, NULL, 'Enabled', 'BPI', 'PUSAT', '02.SATUAN PENGAWAS INTERN', 'ADM');
INSERT INTO `organization` VALUES (62, 'B1.3', 'SUB BID PENGENDALIAN DOKUMEN', 4, 5, 1, 2, 5, 62, NULL, 'Deleted', 'BPI', 'PUSAT', '02.BIDANG PENGAWASAN INTERNAL', 'ADM');
INSERT INTO `organization` VALUES (63, 'BA5.2', 'SUB BAG PENGEMBANGAN 2', 5, 17, 1, 2, 3, 17, NULL, 'Enabled', 'RENBANG', 'PUSAT', '03.BAGIAN PERENCANAAN & PENGEMBANGAN', 'ADM');
INSERT INTO `organization` VALUES (64, 'BA8.2', 'SUB BAG PENGENDALIAN KEHILANGAN AIR', 5, 20, 1, 2, 3, 20, NULL, 'Enabled', 'DALTEK', 'PUSAT', '06.BAGIAN PENGENDALIAN TEKNIK', 'TEKNIK');
INSERT INTO `organization` VALUES (65, 'BA4.3', 'SUB BAG TEKNOLOGI INFORMASI', 5, 16, 1, 2, 4, 16, NULL, 'Enabled', 'SDM', 'ALL', '09.BAGIAN SUMBER DAYA MANUSIA & TI', 'ADM');
INSERT INTO `organization` VALUES (66, 'B3.2', 'SUB BID MANAJEMEN INFORMASI', 4, 7, 1, 2, 7, 66, NULL, 'Deleted', 'IT', 'ALL', '04.BIDANG TEKNOLOGI INFORMASI', 'ADM');
INSERT INTO `organization` VALUES (67, 'B3.3', 'SUB BID.KEAMANAN SYSTEM', 4, 7, 1, 2, 7, 67, NULL, 'Deleted', NULL, 'ALL', '', 'NULL');
INSERT INTO `organization` VALUES (68, 'B4', 'UNIT BISNIS AMDK', 3, 2, 1, 2, 68, NULL, NULL, 'Deleted', 'AMDK', 'PUSAT', '16.UNIT BISNIS AMDK', 'TEKNIK');
INSERT INTO `organization` VALUES (69, 'BA9', 'BAG. PERLENGKAPAN', 4, 4, 1, 2, 4, 69, NULL, 'Enabled', 'PRLKP', 'PUSAT', '10.BAGIAN PERLENGKAPAN', 'ADM');
INSERT INTO `organization` VALUES (70, 'BA5.3', 'SUB BAG PENGEMBANGAN', 5, 17, 1, 2, 3, 17, 70, 'Deleted', 'PRTEK   ', 'PUSAT', '09.BAGIAN PERENCANAAN & PENGEMBANGAN', 'TEKNIK');
INSERT INTO `organization` VALUES (71, 'BA6.3', 'SUB BAG PENGENDALIAN DISTRIBUSI 1 baru', 5, 18, 1, 2, 3, 18, 71, 'Deleted', NULL, 'PUSAT', '10.BAGIAN PRODUKSI & DISTRIBUSI 1', NULL);
INSERT INTO `organization` VALUES (72, 'BA7.1', 'SUB BAG PENGENDALIAN PRODUKSI 2', 5, 19, 1, 2, 3, 19, 72, 'Enabled', 'PRODIS2', 'PUSAT', '05.BAGIAN PRODUKSI & DISTRIBUSI 2', 'TEKNIK');
INSERT INTO `organization` VALUES (73, 'BA7.4', 'SUB BAG PENGENDALIAN DISTRIBUSI 2 - baru', 5, 19, 1, 2, 3, 19, 73, 'Deleted', NULL, 'PUSAT', '11.BAGIAN PRODUKSI & DISTRIBUSI 2', NULL);
INSERT INTO `organization` VALUES (74, 'BA9.2', 'SUB BAG PERSEDIAAN', 5, 69, 1, 2, 4, 69, 74, 'Enabled', 'PRLKP', 'PUSAT', '10.BAGIAN PERLENGKAPAN', 'ADM');
INSERT INTO `organization` VALUES (75, 'B4.2', 'SUB BAG PRODUKSI', 4, 68, 1, 2, 68, 75, NULL, 'Enabled', 'AMDK', 'PUSAT', '16.UNIT BISNIS AMDK', 'TEKNIK');
INSERT INTO `organization` VALUES (76, 'BA3.4', 'SUB BAG SEKRETARIAT DIREKSI & PROTOKOL', 5, 15, 1, 2, 4, 15, 76, 'Enabled', NULL, 'PUSAT', '07.BAGIAN KESEKRETARIATAN', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
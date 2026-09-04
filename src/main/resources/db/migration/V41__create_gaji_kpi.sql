-- kepegawaian-8seb: tabel Gaji KPI — tunjangan kinerja (tunkin) + PPh21 terutang
-- per pegawai (nipam) per periode (YYYY-MM). Sumber REF_TUNJ_KINERJA di engine
-- proses gaji (Wave 4/6); lookup by nipam + periode.
-- Soft-delete + audit Envers, pola sama dengan master penggajian lain.

CREATE TABLE `gaji_kpi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `periode` varchar(7) DEFAULT NULL,
  `tunkin` double DEFAULT NULL,
  `pph21_ter` double DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_gj_kpi_nipam_periode` (`nipam`,`periode`) USING BTREE,
  KEY `idx_gj_kpi_nipam` (`nipam`) USING BTREE,
  KEY `idx_gj_kpi_periode` (`periode`) USING BTREE,
  KEY `idx_gj_kpi_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

CREATE TABLE `gaji_kpi_aud` (
  `id` bigint(20) NOT NULL,
  `rev` int(11) NOT NULL,
  `revtype` tinyint(4) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `created_by` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT 0,
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `updated_by` varchar(255) DEFAULT NULL,
  `nipam` varchar(255) DEFAULT NULL,
  `periode` varchar(7) DEFAULT NULL,
  `tunkin` double DEFAULT NULL,
  `pph21_ter` double DEFAULT NULL,
  PRIMARY KEY (`rev`,`id`) USING BTREE,
  CONSTRAINT `fk_gj_kpi_aud_rev_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

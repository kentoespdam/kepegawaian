-- kepegawaian-m04g follow-up: index untuk filter `?group=` (equal) di /master/organisasi.
-- Menyusul pola idx_org_* (kode/nama/lvl_org/is_deleted) di V1.
ALTER TABLE `organisasi` ADD INDEX `idx_org_org_group` (`org_group`) USING BTREE;
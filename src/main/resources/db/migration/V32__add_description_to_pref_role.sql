-- ADR-0039: Role mendapat atribut description (label untuk UI manajemen role).
-- Nullable — role custom boleh tanpa deskripsi.

ALTER TABLE `pref_role` ADD COLUMN `description` varchar(255) NULL AFTER `id`;

-- Backfill deskripsi seed role (V21) supaya UI manajemen role langsung bermakna.
UPDATE `pref_role` SET `description` = 'Bootstrap system — tidak bisa dihapus; pemegang guard endpoint /system/**' WHERE `id` = 'SYSTEM';
UPDATE `pref_role` SET `description` = 'Administrator penuh — semua 20 permission (V31); tidak bisa dihapus (fallback dual-mode hasRole)' WHERE `id` = 'ADMIN';
UPDATE `pref_role` SET `description` = 'Pegawai biasa — role default user baru (V21/ADR-0037)' WHERE `id` = 'USER';
UPDATE `pref_role` SET `description` = 'Petugas kepegawaian — operasional minus SYSTEM:*, CUTI:CREATE, PENGGAJIAN:WRITE/PROCESS (V31)' WHERE `id` = 'HRD';
UPDATE `pref_role` SET `description` = 'Petugas penggajian' WHERE `id` = 'PENGGAJIAN';

-- V5_0_0__create_view_v_pegawai.sql
-- View v_pegawai — extracted from draft schema dump (ADR-0032, epic kepegawaian-odb)
--
-- FK column: pegawai.biodata_id → biodata.nik (entity @JoinColumn(name = "biodata_id"))
-- Ditempatkan di V5 (setelah V3 seed) karena tergantung tabel: pegawai, biodata,
-- organisasi, jabatan, golongan.

CREATE OR REPLACE VIEW `v_pegawai` AS
SELECT
    `peg`.`id` AS `id`,
    `peg`.`nipam` AS `nipam`,
    `bio`.`nik` AS `nik`,
    `bio`.`nama` AS `nama`,
    `bio`.`jenis_kelamin` AS `jenis_kelamin`,
    `bio`.`status_kawin` AS `status_kawin`,
    `bio`.`tempat_lahir` AS `tempat_lahir`,
    `bio`.`tanggal_lahir` AS `tanggal_lahir`,
    `org`.`id` AS `organisasi_id`,
    `org`.`nama` AS `nama_organisasi`,
    `jab`.`id` AS `jabatan_id`,
    `jab`.`nama` AS `nama_jabatan`,
    `gol`.`id` AS `golongan_id`,
    `gol`.`golongan` AS `golongan`,
    `gol`.`pangkat` AS `pangkat`,
    `peg`.`status_kerja` AS `status_kerja`,
    `peg`.`status_pegawai` AS `status_pegawai`
FROM `pegawai` `peg`
JOIN `biodata` `bio` ON(`peg`.`biodata_id` = `bio`.`nik`)
JOIN `organisasi` `org` ON(`peg`.`organisasi_id` = `org`.`id`)
JOIN `jabatan` `jab` ON(`peg`.`jabatan_id` = `jab`.`id`)
JOIN `golongan` `gol` ON(`peg`.`golongan_id` = `gol`.`id`)
WHERE `peg`.`status_kerja` IN (1, 2);

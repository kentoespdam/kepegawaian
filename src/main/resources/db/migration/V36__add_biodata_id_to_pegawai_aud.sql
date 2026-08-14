-- V36__add_biodata_id_to_pegawai_aud.sql
-- Fix: "Unknown column 'biodata_id' in 'field list'" when Envers writes pegawai_aud
-- (every Pegawai save/update, e.g. PATCH /pegawai/{id}/gaji -> 500).
-- The Pegawai entity maps the FK as "biodata_id" (via @JoinColumn(name = "biodata_id",
-- referencedColumnName = "nik")), but the audit table (pegawai_aud) was created with the
-- old column name "nik" in the V1 baseline. Keeping both columns for backward
-- compatibility with existing audit data.

ALTER TABLE `pegawai_aud`
    ADD COLUMN `biodata_id` varchar(255) DEFAULT NULL;

-- Backfill existing audit rows that have the old column populated
UPDATE `pegawai_aud`
SET `biodata_id` = `nik`
WHERE `biodata_id` IS NULL AND `nik` IS NOT NULL;

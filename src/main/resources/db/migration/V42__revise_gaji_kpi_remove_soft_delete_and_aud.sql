-- 1. Bersihkan bangkai / soft-deleted records
DELETE FROM gaji_kpi WHERE is_deleted = 1;

-- 2. Drop audit table Envers
DROP TABLE IF EXISTS gaji_kpi_aud;

-- 3. Hapus index dan kolom is_deleted
ALTER TABLE gaji_kpi DROP INDEX idx_gj_kpi_is_deleted;
ALTER TABLE gaji_kpi DROP COLUMN is_deleted;

SET FOREIGN_KEY_CHECKS = 0;
-- Seed: dasar_gaji (1)

INSERT INTO dasar_gaji (id, deskripsi, tanggal_awal, tanggal_akhir, aktif, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'Skala Gaji - PP No.30 Thn 2015', '2015-01-01', '2025-12-31', 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');
SET FOREIGN_KEY_CHECKS = 1;

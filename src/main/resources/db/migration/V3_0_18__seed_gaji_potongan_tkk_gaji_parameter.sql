-- Seed: gaji_potongan_tkk (22) and gaji_parameter_setting (2)

INSERT INTO gaji_potongan_tkk (status_pegawai, level_id, golongan_id, nominal, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(2, 2, NULL, 227500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 3, NULL, 204750, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 4, NULL, 204750, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 5, NULL, 113500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 6, NULL, 100000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 1, 75000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 2, 75000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 3, 75000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 4, 75000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 5, 80500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 6, 80500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 7, 80500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 8, 80500, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 9, 86000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 10, 86000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 11, 86000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 7, 12, 86000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(0, NULL, NULL, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(1, NULL, NULL, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, NULL, NULL, 75000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, NULL, NULL, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO gaji_parameter_setting (kode, nominal, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
('maksimal_potongan_jpn', 100423, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('maksimal_potongan_askes', 120000, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

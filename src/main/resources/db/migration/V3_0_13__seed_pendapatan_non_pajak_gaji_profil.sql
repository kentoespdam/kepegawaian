-- Seed: gaji_pendapatan_non_pajak (8) and gaji_profil (9)

INSERT INTO gaji_pendapatan_non_pajak (kode, nominal, notes, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
('TK', 4500000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('K/0', 4875000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('K/1', 5250000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('K/2', 5625000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('K/3', 6000000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('TK/3', 5625000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('TK/1', 4875000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('TK/2', 5250000, '', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO gaji_profil (nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
('Profil Komponen dan Formula Gaji Direktur', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Pegawai Tetap', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Calon Pegawai Tetap', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Calon Pegawai Honorer Tetap', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Pegawai Honorer Tetap', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Pegawai Kontrak', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Suami Istri se Kantor', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen & Formula Gaji Capeg < UMK', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
('Profil Komponen dan Formula Gaji Direktur Utama', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');


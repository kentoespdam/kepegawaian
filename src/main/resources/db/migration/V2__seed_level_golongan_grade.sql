SET FOREIGN_KEY_CHECKS = 0;
-- Level reference data
INSERT INTO level (id, nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'DEWAS', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'DIRUT', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'DIRTEK', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'DIRUM', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'MANAJER', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'SUPERVISOR', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'STAF', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

-- Golongan reference data
INSERT INTO golongan (id, golongan, pangkat, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'A.1', 'Pegawai Dasar Muda', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'A.2', 'Pegawai Dasar Muda Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'A.3', 'Pegawai Dasar', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'A.4', 'Pegawai Dasar Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'B.1', 'Pelaksana Muda', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'B.2', 'Pelaksana Muda Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'B.3', 'Pelaksana', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'B.4', 'Pelaksana Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 'C.1', 'Staf Muda', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(10, 'C.2', 'Staf Muda Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(11, 'C.3', 'Staf', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(12, 'C.4', 'Staf Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(13, 'D.1', 'Manajer Muda', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(14, 'D.2', 'Manajer Muda Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(15, 'D.3', 'Manajer', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(16, 'D.4', 'Manajer Tk.I', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(17, 'D.5', 'Manajer Utama', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(18, 'A.3 (C)', 'Pegawai Dasar (Capeg)', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

-- Grade reference data
INSERT INTO grade (id, level_id, grade, tukin, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 5, 1, 3000000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 5, 2, 3150000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 5, 3, 3300000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 6, 1, 1930500.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 6, 2, 2216500.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 6, 3, 2250500.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 6, 4, 3500000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 7, 1, 715000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 7, 2, 1072500.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(10, 7, 3, 1430000.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(11, 7, 4, 1787500.00, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');
SET FOREIGN_KEY_CHECKS = 1;

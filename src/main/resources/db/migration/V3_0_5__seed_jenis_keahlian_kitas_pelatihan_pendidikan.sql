-- Seed: jenis_keahlian (16), jenis_kitas (12), jenis_pelatihan (8), jenjang_pendidikan (9)

INSERT INTO jenis_keahlian (id, nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'Pemrograman', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'Desain Grafis', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'Bhs. Inggris', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'Teknisi Komputer', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'Ahli MAM Muda', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'Ahli MAM Madya', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'Ahli MAM Utama', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'Ahli Akuntansi', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 'Ahli Pengadaan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(10, 'Assessor', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(11, 'Water Sampling', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(12, 'Manajemen Risiko', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(13, 'Operator PLTD', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(14, 'Perpipaan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(15, 'SPAM', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(16, 'ASET', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO jenis_kitas (id, nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'KTP', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'NPWP', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'Jamsostek', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'ASKES', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'KTP', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'SIM', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'Dapenma', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'JPn', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 'Yakan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(10, 'Inkop Pamsi', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(11, 'Korpri', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(12, 'ID Card', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO jenis_pelatihan (id, nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'Administrasi', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'Keuangan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'Pelayanan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'IT', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'Perpipaan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'Listrik & Perpompaan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'Pengolahan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'SPAM', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO jenjang_pendidikan (id, nama, short_name, seq, is_statistik, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'SD - Sederajat', 'SD', 1, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'SMP - Sederajat', 'SMP', 2, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'SMA - Sederajat', 'SMA', 3, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'Diploma 1 ', 'D1', 4, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'Diploma 2 ', 'D2', 5, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'Diploma 3 ', 'D3', 6, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'S1', 'S1', 7, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'S2', 'S2', 8, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 'S3', 'S3', 9, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

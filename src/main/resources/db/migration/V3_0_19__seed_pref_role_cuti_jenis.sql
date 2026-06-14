-- Seed: pref_role (5) and cuti_jenis (18)

INSERT INTO pref_role (id) VALUES
('SYSTEM'),
('ADMIN'),
('USER'),
('HRD'),
('PENGGAJIAN');

SET FOREIGN_KEY_CHECKS=0;

INSERT INTO cuti_jenis (id, parent_id, nama, max_hari, potong_kuota_tahunan, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, NULL, 'Cuti tahunan', 12, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, NULL, 'Cuti besar', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, NULL, 'Cuti sakit', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, NULL, 'Cuti melaksanakan ibadah', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, NULL, 'Cuti karena alasan penting', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, NULL, 'Cuti bersalin', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, NULL, 'Cuti di luar tanggungan perusahan', 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 4, 'Menunaikan ibadah haji', 45, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(9, 4, 'Menunaikan ibadah umroh', 15, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(10, 4, 'Menunaikan ibadah lainnya', 45, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(11, 5, 'Menikah', 3, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(12, 5, 'Menikahkan anak', 2, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(13, 5, 'Mengkhitankan anak', 2, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(14, 5, 'Membaptiskan anak', 2, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(15, 5, 'Istri melahirkan atau keguguran kandungan', 2, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(16, 5, 'Suami/istri, orang tua/mertua, anak/menantu meninggal dunia', 2, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(17, 5, 'Anggota keluarga dalam satu rumah meninggal dunia', 1, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(18, 5, 'Saudara kandung/ipar/tiri/angkat meninggal dunia', 1, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

SET FOREIGN_KEY_CHECKS=1;

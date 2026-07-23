SET FOREIGN_KEY_CHECKS = 0;
-- Seed: jenis_sp (4) and sanksi_sp (8)

INSERT INTO jenis_sp (id, kode, nama, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'TG-LISAN', 'Teguran Lisan', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'SP-1', 'Surat Peringatan Kesatu', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'SP-2', 'Surat Peringatan Kedua', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'SP-3', 'Surat Peringatan Ketiga', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');

INSERT INTO sanksi_sp (id, kode, keterangan, jenis_sp_id, pot_tkk, jml_pot_tkk, is_pending_pangkat, is_pending_gaji, is_turun_pangkat, is_turun_jabatan, is_suspension, is_terminate_dh, is_terminate_th, is_deleted, created_at, created_by, updated_at, updated_by) VALUES
(1, 'S1', 'Potongan TKK 1 Hari', 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(2, 'S2', 'Potongan TKK 2 Hari & Penundaan Gaji Berkala selama 1 Tahun', 2, 1, 2, 0, 1, 0, 0, 0, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(3, 'S3', 'Potongan TKK 3 Hari & Penundaan Kenaikan Pangkat selama 1 Tahun', 3, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(4, 'S4', 'Tidak menerima TKK satu bulan dan Penurunan pangkat satu tingkat', 4, 1, 22, 0, 0, 1, 0, 0, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(5, 'S5', 'Tidak menerima TKK satu bulan dan Penurunan jabatan satu tingkat', 4, 1, 22, 0, 0, 0, 1, 0, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(6, 'S6', 'Pemberhentian sementara sebagai pegawai', 4, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(7, 'S7', 'Pemberhentian dengan hormat tidak atas permintaan sendiri sebagai pegawai', 4, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'),
(8, 'S8', 'Pemberhentian dengan tidak hormat sebagai pegawai', 4, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM');
SET FOREIGN_KEY_CHECKS = 1;

-- kepegawaian-8seb: tambah kolom `jenis_error` pada log error batch gaji.
-- Membedakan error DATA (per-pegawai, lanjut proses) vs SYSTEM (fatal, batch gagal).

ALTER TABLE `gaji_batch_root_error_logs` ADD COLUMN `jenis_error` varchar(10) NOT NULL DEFAULT 'SYSTEM';

# Staged Batch ETL Runner untuk Migrasi Data SmartOffice ke Kepegawaian Baru

Migrasi data dari database monolitik legacy `smartoffice` ke `kepegawaian_dev_new` dilakukan menggunakan **Staged Batch ETL Runner** independen, bukan sinkronisasi real-time (CDC/dual-write). Pendekatan ini dipilih karena fokus sistem adalah cutover migrasi data eksisting agar aplikasi baru dapat segera beroperasi penuh dengan data riil secara aman, deterministik, dan idempotent.

## Konteks

Database legacy `smartoffice` menggabungkan modul persuratan berukuran jutaan baris dengan data kepegawaian. Terdapat diskrepansi dan data yang belum termigrasi lengkap di database baru (misalnya selisih data pada `riwayat_sp`, `riwayat_kontrak`, `cuti_pegawai`, dan `pelatihan`). Sistem membutuhkan mekanisme migrasi yang andal untuk melengkapi dan menyelaraskan data ini tanpa membebani runtime aplikasi utama.

## Considered Options

- **Continuous CDC / Binlog Sync Daemon** (ditolak): Menambah kompleksitas infrastruktur (Kafka/Debezium/polling daemon) dan memicu risiko inkonsistensi state mesin selama dual-run, padahal target bisnis adalah cutover ke sistem baru.
- **REST-triggered Online Migration Endpoint** (ditolak): Berisiko mengalami HTTP timeout pada tabel bervolume besar (seperti riwayat SK dan payroll) dan mencampuradukkan beban ETL batch ke dalam application server web.
- **Staged Batch ETL Runner Idempotent** (dipilih): Eksekusi bertahap per domain (`master` → `profil` → `pegawai` → `kepegawaian` → `cuti` → `penggajian`), dapat di-rerun berkali-kali tanpa duplikasi data, mendukung mode `--dry-run`, dan menyediakan log validasi anomali.

## Consequences

- Dibutuhkan tabel/mekanisme penampung state mapping (`migration_id_map`) untuk mencatat relasi legacy ID $\to$ new ID serta hash integritas record.
- Runner dirancang modular dan mandiri (standalone CLI/runner), sehingga tidak mengotori codebase utama sistem `kepegawaian` dan dapat dipensiunkan atau diarsipkan setelah cutover tuntas.
- Penanganan data audit trail (Hibernate Envers `*_aud` & `revinfo`) harus ditangani secara eksplisit oleh runner saat memasukkan data historis.

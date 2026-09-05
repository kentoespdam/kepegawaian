# Two-Phase Migration untuk Berkas Fisik dan Metadata Lampiran Kepegawaian

Untuk memisahkan siklus hidup migrasi skema database dari beban transfer I/O berkas fisik berukuran besar (~10,98 GB), runner migrasi mengadopsi pola **Two-Phase Migration**: Fase 1 memproses metadata dan mencatat pemetaan ke **File Sync Manifest**, disusul Fase 2 berupa worker mandiri yang menyalin dan merestrukturisasi berkas fisik secara terpisah, *multithreaded*, dan *resumable*.

## Konteks

Pada sistem legacy `smartoffice`, berkas lampiran disimpan secara polimorfik di tabel tunggal `attachments` (mencakup 14.737 berkas kepegawaian dengan total ukuran ~10,98 GB yang didominasi 97,4% format PDF) menggunakan path fisik `attachments/<YYYYMM>/<file_name>` serta 202 foto profil di `attachments/employee/`.

Di sistem baru `kepegawaian_dev_new`, metadata lampiran dinormalisasi dan dipecah per domain entitas (`lampiran_sk`, `lampiran_profil`, `riwayat_sp`, serta `biodata.foto_profil`), sementara berkas fisik disimpan pada filesystem lokal atau Docker Volume (`attachments/`) mengikuti konvensi folder dan penamaan `<JENIS_ENUM>/<refId>/<UUID_hex_32>`. Menyalin berkas fisik sebesar ~11 GB secara langsung bersamaan dengan eksekusi migrasi database akan membebani I/O, memperlambat proses migrasi database secara drastis, dan rentan gagal di lingkungan development yang belum memiliki dump berkas fisik lengkap.

## Considered Options

- **Migrasi Monolitik (Salin Berkas Bersamaan dengan Insert Database)** (ditolak): Worker migrasi langsung menyalin dan me-rename file fisik pada disk saat mengeksekusi *insert* baris ke database. Pilihan ini menyebabkan eksekusi migrasi database terblokir oleh I/O disk (membutuhkan waktu berjam-jam), transaksi rentan gagal atau inkonsisten jika ada file yang hilang/korup di disk, serta menghambat pengujian di lingkungan development yang hanya membutuhkan data tabular.
- **Background Async Copy di Aplikasi Utama (Spring Boot)** (ditolak): Menyerahkan sinkronisasi berkas fisik ke background task atau queue di aplikasi utama Java. Pilihan ini membebani runtime produksi baru, memerlukan implementasi layer kompatibilitas path legacy di dalam core application, dan melanggar prinsip *standalone decoupled migration*.
- **Two-Phase Migration dengan File Sync Manifest** (dipilih):
  1. **Fase 1 (Metadata ETL)**: Runner membaca data `attachments` legacy, men-generate `hashed_file_name` (UUID hex 32-karakter), menyisipkan metadata ke tabel baru (`lampiran_sk`, `lampiran_profil`, dll.), dan mencatat manifes pemetaan path berkas ke SQLite lokal (`file_sync_manifest.sqlite` atau CSV).
  2. **Fase 2 (File Copy Worker)**: Sub-command mandiri (`python run.py sync-files --source /path/to/legacy/attachments`) bertugas membaca manifes, memverifikasi ketersediaan file fisik lama, lalu menyalin dan me-rename file ke struktur target baru secara *multithreaded* dan *resumable*.

## Consequences

- Migrasi database inti berjalan sangat cepat (hitungan detik hingga menit) tanpa terblokir oleh operasi I/O disk 11 GB.
- Lingkungan development dapat langsung menguji fungsionalitas data aplikasi tanpa diwajibkan mengunduh seluruh 11 GB berkas fisik.
- File fisik dapat disinkronkan kapan saja secara aman tanpa risiko korupsi database jika ada berkas legacy yang hilang di disk.
- Pemetaan path berkas terdokumentasi rapi di dalam manifes lokal sehingga proses sinkronisasi fisik bersifat idempoten dan dapat dipantau progresnya secara terukur.

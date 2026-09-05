# Injeksi Baseline Revision Hibernate Envers untuk Integritas Audit Awal

Untuk memastikan kelengkapan rekam jejak audit sejak hari pertama operasional, runner migrasi menerapkan strategi **Injeksi Baseline Revision Global** dengan mencatat entri revisi di tabel `revinfo` dan menyisipkan snapshot data awal ke 31 tabel `*_aud` (`revtype = 0` / `ADD`) secara langsung via SQL di luar JPA container.

## Konteks

Pada database baru `kepegawaian_dev_new`, terdapat 31 tabel `*_aud` dan tabel `revinfo` yang dikelola oleh Hibernate Envers untuk memantau siklus hidup data beranotasi `@Audited` (seperti entitas `pegawai`, `biodata`, `riwayat_sk`, `riwayat_mutasi`, `profil_keluarga`, dll.). Fitur aplikasi baru seperti `RevInfoService` dan `ProfileUpdateStrategy` (workflow pengajuan & persetujuan pembaruan data profil pegawai) sangat bergantung pada riwayat revisi Envers (`AuditReaderFactory`) untuk membandingkan perubahan data sebelum/sesudah pengajuan serta melakukan *revert* ke revisi sebelumnya jika pengajuan ditolak.

Karena runner migrasi berbasis Python CLI menulis langsung via SQL (di luar JPA container) demi performa dan isolasi aplikasi, operasi migrasi tidak otomatis memicu event listener Hibernate Envers (`AuditRevisionListener`). Jika tabel-tabel audit dibiarkan kosong pasca migrasi, entitas hasil migrasi tidak memiliki riwayat revisi awal, sehingga eksekusi perbandingan atau rollback pada workflow profil pegawai akan memicu kegagalan runtime seperti `RevisionDoesNotExistException`.

## Considered Options

- **Mengabaikan Tabel Audit Saat Migrasi** (ditolak): Hanya mengisi tabel operasional utama dan membiarkan tabel `*_aud` kosong. Pilihan ini menyebabkan data hasil migrasi tidak memiliki baseline audit, memicu `RevisionDoesNotExistException` saat `AuditReaderFactory` mencoba membaca revisi awal atau melakukan revert pada pengajuan profil pegawai.
- **Eksekusi Migrasi Melalui JPA / Spring Boot Service** (ditolak): Menjalankan proses migrasi melalui layer JPA agar event listener Envers terpicu otomatis. Pilihan ini ditolak karena performa batch lambat, menimbulkan overhead siklus hidup entity yang tidak perlu, dan melanggar arsitektur decoupled runner migrasi mandiri.
- **Injeksi Baseline Revision Global via Runner Migrasi** (dipilih):
  1. Membuat satu record revisi global resmi di tabel `revinfo` pada setiap batch/tahapan migrasi (menyimpan nomor revisi dan timestamp eksekusi).
  2. Untuk setiap entitas yang masuk ke tabel master beranotasi `@Audited` (`pegawai`, `biodata`, `riwayat_sk`, `riwayat_mutasi`, `profil_keluarga`, dll.), runner sekaligus meng-insert baris snapshot baseline ke tabel `*_aud` terkait dengan nomor revisi (`rev`) tersebut dan `revtype = 0` (`ADD`).

## Consequences

- 100% konsisten dengan ekosistem Hibernate Envers: seluruh data migrasi memiliki rekam jejak baseline awal resmi sejak hari pertama sistem aktif.
- Workflow perbandingan revisi dan revert pada pengajuan profil pegawai (`RevInfoService`, `ProfileUpdateStrategy`, `AuditReaderFactory`) berjalan mulus tanpa risiko error `RevisionDoesNotExistException` atau riwayat audit kosong.
- Skrip runner migrasi bertanggung jawab penuh menulis pasangan tabel operasional dan tabel audit snapshot-nya secara atomik dan terkoordinasi.

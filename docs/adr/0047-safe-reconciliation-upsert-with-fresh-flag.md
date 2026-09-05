# Safe Reconciliation & Upsert sebagai Strategi Default Penanganan Data Target

Proses migrasi data dari `smartoffice` ke `kepegawaian_dev_new` menerapkan strategi **Safe Reconciliation & Upsert** secara default, dengan opsi flag `--fresh` khusus untuk reset bersih di lingkungan pengembangan.

## Konteks

Database target `kepegawaian_dev_new` tidak dalam keadaan kosong, melainkan telah berisi data dasar (seperti 570 master pegawai dan 497 biodata), namun memiliki gap data yang belum termigrasi lengkap (seperti 228 data SP yang belum ada dan 247 data cuti yang tertinggal). Melakukan truncate sepihak akan merusak relasi dan menghapus data uji coba baru, sedangkan mode append-only akan membiarkan data eksisting yang masih memiliki field kosong/inkonsisten tetap cacat.

## Considered Options

- **Strict Wipe / Selalu TRUNCATE** (ditolak): Merusak data baru yang sudah dibuat di development/staging dan berisiko melanggar integritas foreign key Envers.
- **Append-Only** (ditolak): Tidak dapat memperbaiki field data lama yang tidak lengkap atau memiliki nilai NULL akibat migrasi terdahulu yang parsial.
- **Safe Reconciliation & Upsert + Flag `--fresh`** (dipilih): Mencocokkan record dengan natural key (`NIPAM`, `NIK`, atau `migration_id_map`). Melakukan insert untuk data baru yang tertinggal, update untuk melengkapi field yang kosong/berubah, dan menyediakan flag `--fresh` eksplisit jika developer ingin melakukan truncate terisolasi per domain di development.

## Consequences

- Setiap domain migrasi memiliki logika komparasi record untuk menentukan apakah record harus di-insert, di-update, atau di-skip.
- Runner aman dijalankan berulang kali (idempotent) tanpa menduplikasi data.
- Developer memiliki kontrol penuh: eksekusi default berjalan aman tanpa data loss, sementara flag `--fresh` tersedia saat dibutuhkan kanvas kosong.

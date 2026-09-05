# Rekonsiliasi Snapshot Kuota Cuti dan Ingesti Delta Transaksi Cuti

Untuk menjamin akurasi 100% saldo sisa cuti pegawai pada sistem baru tanpa risiko deviasi akibat siklus tahunan cuti yang unik, runner migrasi mengadopsi strategi **Snapshot Reconciliation & Delta Ingestion**: menyinkronkan saldo `cuti_kuota` sebagai snapshot 1:1 dari database legacy `smartoffice`, memigrasikan delta transaksi aktif terbaru `cuti_pegawai` (beserta tabel approval dan detail), serta mengabaikan tabel-tabel usang tahun 2016 dan artefak dead schema.

## Konteks

Pada database legacy `smartoffice`, data cuti operasional aktif berada pada famili tabel `cuti_*` (`cuti_pegawai` sebanyak 1.384 baris untuk periode 2019–2026 dan `cuti_kuota` sebanyak 2.411 baris untuk periode 2018–2026), sedangkan tabel `emp_leave` dan `emp_leave_history` merupakan data usang tahun 2016. Di database baru `kepegawaian_dev_new`, tabel `riwayat_cuti` merupakan artefak skema mati (*dead schema artifact*) yang memiliki 0 baris dan tidak digunakan sama sekali oleh modul aplikasi `cuti/`.

Berdasarkan audit delta data, terdapat gap 247 baris transaksi `cuti_pegawai` (periode 2025–2026) dan perubahan saldo sisa kuota tahun 2026 untuk 40 pegawai serta 31 pegawai baru di `cuti_kuota`. Karena siklus kuota cuti tahunan instansi berjalan unik (1 Juli s/d 30 Juni tahun berikutnya) dengan carry-over saldo dan penyesuaian khusus oleh HRD, menghitung ulang kuota dari nol murni dari tanggal transaksi pengajuan akan menghasilkan deviasi dan diskrepansi signifikan terhadap saldo riil sisa cuti.

## Considered Options

- **Kalkulasi Ulang Kuota dari Nol Berdasarkan Transaksi Riwayat** (ditolak): Menghitung ulang saldo kuota pegawai dengan mengakumulasi hari cuti dari transaksi tanggal mulai dan selesai. Pendekatan ini berisiko tinggi menghasilkan diskrepansi saldo sisa cuti karena formula cuti memiliki siklus unik (1 Juli s/d 30 Juni), hak cuti bersama, carry-over saldo antar-periode, dan penyesuaian khusus langsung oleh HRD yang tidak tercatat murni sebagai transaksi reguler.
- **Migrasi Menyeluruh Termasuk Skema Legacy 2016 dan Dead Schema** (ditolak): Memaksakan ekstraksi data usang `emp_leave` / `emp_leave_history` atau mengisi tabel `riwayat_cuti`. Pilihan ini mengotori database baru dengan data kadaluwarsa 2016 dan membuang waktu memetakan tabel `riwayat_cuti` yang tidak dikonsumsi oleh entitas JPA maupun service modul cuti sistem baru.
- **Snapshot Reconciliation & Delta Ingestion** (dipilih):
  1. Mengabaikan tabel legacy usang tahun 2016 (`emp_leave`, `emp_leave_history`) dan mengabaikan dead schema `riwayat_cuti`.
  2. Rekonsiliasi `cuti_kuota` sebagai snapshot 1:1 dari `smartoffice.cuti_kuota` (meng-upsert sisa kuota dan kuota terpakai tahun 2026, serta menyisipkan baris kuota 2026 untuk pegawai aktif baru).
  3. Memigrasikan delta 247 baris transaksi `cuti_pegawai` (beserta tabel `cuti_pegawai_approval`, `cuti_pegawai_approval_chain`, dan `cuti_pegawai_detail`) dengan pemetaan foreign key yang valid ke `pegawai_id` dan `cuti_jenis_id`.

## Consequences

- Saldo sisa kuota cuti pegawai tahun berjalan 100% presisi dan sinkron dengan catatan operasional HRD tanpa risiko deviasi formula carry-over 1 Juli – 30 Juni.
- Database baru bersih dari polusi skema usang 2016 (`emp_leave`, `emp_leave_history`) dan tidak ada redundansi pada dead schema `riwayat_cuti`.
- Proses migrasi cuti bersifat idempoten dan aman dieksekusi berkali-kali tanpa risiko duplikasi transaksi maupun korupsi saldo kuota.
- Integritas referensial data transaksi cuti baru tetap terjaga utuh melalui resolusi foreign key ke master `pegawai` dan `cuti_jenis`.

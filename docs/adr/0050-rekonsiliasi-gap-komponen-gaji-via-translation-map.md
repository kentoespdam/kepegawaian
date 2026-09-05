# Rekonsiliasi Gap Pengkodean Komponen Gaji via Translation Map dan Passthrough Historis

Dalam migrasi data penggajian historis dari tabel legacy `smartoffice.salary_process_detail` ke `kepegawaian_dev_new.gaji_batch_master_proses`, runner migrasi menerapkan **pemetaan simbol `ctype` ke enum `jenis_gaji`**, standarisasi kode komponen melalui kamus terpusat (**`COMPONENT_CODE_MAP`**), serta mekanisme **passthrough aman** untuk komponen ad-hoc/insidental tanpa keterikatan Foreign Key ke master baru demi menjaga keseimbangan nominal 100% dengan arsip fisik lama.

## Konteks

Pada database legacy `smartoffice.salary_process_detail`, tipe komponen gaji direpresentasikan menggunakan simbol `'+'` (penambah/pendapatan) dan `'-'` (pengurang/potongan), serta terdapat variasi pengkodean yang tinggi akibat riwayat perubahan formula, singkatan lama, dan komponen tunjangan/potongan insidental (ad-hoc) yang dibuat sewaktu-waktu.

Di database baru `kepegawaian_dev_new.gaji_batch_master_proses`, tabel ini bertindak murni sebagai snapshot rincian proses hitung gaji historis (tanpa Foreign Key ke tabel master `gaji_komponen`). Struktur kolom baru menggunakan enum `jenis_gaji` (`NONE`, `PEMASUKAN`, `POTONGAN`) dan kolom `kode` berbasis `VARCHAR`. Jika migrasi memaksakan validasi referensial kaku hanya terhadap komponen yang terdaftar di master komponen baru, sejumlah besar baris komponen legacy berisiko tertolak atau hilang, yang akan merusak keseimbangan total penerimaan kotor maupun potongan bersih pada slip gaji historis pegawai.

## Considered Options

- **Validasi Kaku ke Master Komponen Baru** (ditolak): Menolak atau mengabaikan (drop) baris komponen legacy yang kodenya tidak terdaftar pada tabel `gaji_komponen` baru. Pilihan ini menyebabkan kehilangan data (loss of historical data) dan merusak kalkulasi total take-home pay sehingga slip gaji historis tidak lagi klop dengan arsip fisik.
- **Auto-Generate Komponen Dummy ke Master Baru** (ditolak): Membuat entri baru secara otomatis di tabel master `gaji_komponen` untuk setiap kode ad-hoc atau singkatan lama yang ditemukan. Pilihan ini mengotori master komponen aktif sistem baru dengan puluhan kode usang yang sudah tidak relevan lagi untuk penggajian berjalan.
- **Translation Map & Passthrough Snapshot Aman** (dipilih):
  1. Konversi `ctype`: Mengonversi karakter `'+'` $\to$ `PEMASUKAN`, `'-'` $\to$ `POTONGAN`, dan selain itu $\to$ `NONE`.
  2. Kamus terpusat (`COMPONENT_CODE_MAP`): Memetakan kode-kode singkatan/variasi lama yang umum ke kode kanonikal standar baru.
  3. Passthrough aman: Komponen insidental/ad-hoc yang tidak ada di master baru tetap dipertahankan dengan kode (`kode`) dan deskripsi (`nama`) aslinya, memanfaatkan tabel `gaji_batch_master_proses` yang berkarakteristik snapshot tanpa batasan Foreign Key ke `gaji_komponen`.

## Consequences

- Integritas nominal perhitungan slip gaji historis (khususnya 12 bulan terakhir) terjamin presisi dan akurat tanpa kehilangan data (zero data loss).
- Total penerimaan kotor (bruto), akumulasi potongan, dan penerimaan bersih (netto) pada slip historis tetap seimbang 100% dan cocok dengan arsip fisik lama.
- Tidak terjadi pelanggaran integritas referensial database karena skema tabel `gaji_batch_master_proses` memang terisolasi sebagai snapshot tanpa Foreign Key ke master komponen.
- Tabel master `gaji_komponen` di database baru tetap bersih dari polusi kode-kode komponen legacy atau ad-hoc yang sudah kadaluwarsa.

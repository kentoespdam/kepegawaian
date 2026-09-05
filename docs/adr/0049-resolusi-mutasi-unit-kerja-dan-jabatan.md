# Resolusi Pemetaan Mutasi Unit Kerja dan Jabatan dari Legacy ke Riwayat Mutasi

Dalam migrasi data dari tabel legacy `emp_work_history` ke `riwayat_mutasi`, runner migrasi mengklasifikasikan `EJenisMutasi` secara cerdas berdasarkan perubahan unit kerja (`org_id`) dan jabatan (`pos_id`), dengan prioritas `MUTASI_LOKER` bila keduanya berubah bersamaan sesuai preferensi domain HR, serta menghubungkan riwayat mutasi ke `riwayat_sk` via nomor SK (`ewh_sk_no`).

## Konteks

Pada aplikasi legacy SmartOffice, form mutasi `MutationWindow.js` mencatat mutasi pegawai ke dalam tabel `emp_work_history` dengan memperbarui unit kerja (`org_id`) dan/atau jabatan (`pos_id`) secara bersamaan dalam satu form. Tim HR memandang istilah mutasi identik dengan perpindahan unit kerja/lokasi kerja. Sementara itu, pada aplikasi baru, entitas `riwayat_mutasi` mendukung penelusuran organisasi dan jabatan sekaligus (beserta nilai lama dan barunya), namun mewajibkan klasifikasi `jenisMutasi` berbasis enum `EJenisMutasi` (seperti `MUTASI_LOKER` dan `MUTASI_JABATAN`).

## Considered Options

- **Pemisahan Menjadi Dua Record Terpisah** (ditolak): Memecah satu kejadian mutasi legacy menjadi dua baris `riwayat_mutasi` (satu `MUTASI_LOKER` dan satu `MUTASI_JABATAN`) menyebabkan duplikasi riwayat SK yang sama dan tidak mencerminkan fakta bahwa kedua perubahan terjadi dalam satu SK penugasan.
- **Pukul Rata Menjadi Satu Jenis Tunggal** (ditolak): Menetapkan seluruh baris legacy sebagai `MUTASI_LOKER` atau `MUTASI_JABATAN` secara seragam akan merusak akurasi laporan mutasi untuk kasus yang murni hanya rotasi jabatan atau murni pindah unit kerja.
- **Klasifikasi Cerdas Berbasis Delta Organisasi & Jabatan + Prioritas `MUTASI_LOKER`** (dipilih): Runner membandingkan state lama vs baru:
  1. Jika hanya unit kerja (`org_id`) yang berubah $\to$ `EJenisMutasi.MUTASI_LOKER` (Pindah Lokasi Kerja/Unit Kerja).
  2. Jika hanya jabatan (`pos_id`) yang berubah $\to$ `EJenisMutasi.MUTASI_JABATAN` (Perubahan Jabatan).
  3. Jika keduanya berubah $\to$ `EJenisMutasi.MUTASI_LOKER` (karena preferensi HR: mutasi = pindah unit kerja) dengan snapshot jabatan baru tetap tersimpan pada record yang sama.
  4. Relasi foreign key ke `riwayat_sk` dipetakan via nomor SK (`ewh_sk_no`).

## Consequences

- Sebanyak 1.248 baris data historis `emp_work_history` dapat termigrasi dengan presisi ke `riwayat_mutasi` tanpa kehilangan jejak unit kerja lama/baru maupun jabatan lama/baru.
- Laporan mutasi kepegawaian menghasilkan data analitik yang akurat dan selaras dengan persepsi tim HR.
- Konsistensi relasi antara `riwayat_mutasi` dan `riwayat_sk` terjaga rapi melalui pencocokan nomor SK, memudahkan audit jejak SK penugasan pegawai.

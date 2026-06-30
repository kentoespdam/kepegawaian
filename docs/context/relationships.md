# Context — Relasi Antar Domain

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat memahami ketergantungan antar modul, validasi cross-domain, atau arah dependency lintas-modul.

## Relasi

- Sebuah **Profesi** menunjuk tepat satu **Organisasi**, satu **Jabatan**, satu **Grade**.
- **Lingkungan** menentukan rantai keamanan: **production** memvalidasi **Appwrite JWT**; **development** memakai **Dev User** tanpa validasi.
- Sebuah **Appwrite User** / **Dev User** membawa satu atau lebih **Role**; **Role** menentukan akses endpoint.
- **Level** sebuah **Profesi** **diturunkan dari Jabatan-nya** (`profesi.level = jabatan.level`) — tidak diinput terpisah.
- Keunikan **Profesi** ditentukan oleh kombinasi `nama` + **Jabatan** + **Grade**.
- Membuat **Profesi** dengan kombinasi yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu.
- **Mengubah** sebuah **Profesi** agar kombinasinya sama dengan Profesi lain — baik yang aktif maupun yang sudah diarsip — **ditolak**.
- Keunikan **Organisasi** ditentukan oleh kombinasi `nama` + **parent**. Kode dan level TIDAK masuk kunci.
- Membuat **Organisasi** dengan kombinasi nama+parent yang dulu pernah dihapus akan **menghidupkan kembali** record lama itu.
- **Mengubah** sebuah **Organisasi** agar nama+parent-nya sama dengan Organisasi lain — baik aktif maupun diarsip — **ditolak**.
- Setiap tulis ke data **Profil** menghasilkan satu **Riwayat Perubahan** (revisi Envers) — selalu, apa pun role. Hanya tulis dengan `changedStatus=true` yang **juga** memunculkan **Pengajuan Perubahan**.
- Menulis sebuah **Pendidikan** dengan `isLatest=true` menyinkronkan **Pendidikan Terakhir** di **Biodata** (bulk update `@Modifying @Query`, bukan `save()` — sengaja, supaya tidak memunculkan Riwayat Perubahan Envers palsu pada Biodata).
- **Role** penulis menentukan `changedStatus`: **SDM** → `false` (langsung stabil), **pegawai** → `true` (menunggu). Keputusan ini diambil **server** dari principal, bukan dari body request.
- Membuat sebuah **Biodata** **otomatis menyemai (seed) dua data anak awal**: satu **Kartu Identitas** kosong dan satu baris **Pendidikan** `isLatest=true`. Keseluruhan dalam **satu transaksi** (gagal salah satu → batal semua). Aktor seed adalah **sistem**, jadi `changedStatus=false` ditetapkan **eksplisit**, bukan lewat penentuan berbasis role.
- Pembacaan data **Profil** selalu **terikat pada satu pegawai** (`biodataId` wajib `@NotBlank`). **Pengecualian: Biodata** — direktori global yang sah bagi SDM (filter opsional).
- Perilaku **membuat ulang data Profil yang pernah dihapus** berbeda per jenis data:
  - **Pendidikan** (kunci: pegawai + jenjang + tahun masuk) dan **Kartu Identitas** (kunci: NIK + jenis kartu) — menghidupkan kembali baris lama. Penambahan-ulang tetap berstatus **tambah** (`INSERT`): bila pegawai yang melakukannya dan SDM menolak → baris dihapus permanen.
  - **Keluarga** — baris aktif dan arsip boleh berdampingan; menambah ulang = baris baru. Hanya duplikat **aktif** persis yang ditolak.
  - **Keahlian**, **Pelatihan**, **Pengalaman Kerja**, **Lampiran Profil** — tak punya kunci alami; tiap penambahan selalu baris baru.
- Sisi tulis Profil tetap memicu pembuatan **Pengajuan Perubahan**, tetapi seluruh logika **Disetujui/Ditolak** dan revert milik **modul updateProfile** — ketergantungan satu arah: profil → updateProfile.
- Membuat sebuah **Pegawai** adalah **saga lintas-modul satu transaksi** (atomik): seed **Biodata** bila NIK belum ada → simpan Pegawai → buat **Riwayat SK** (atau Kontrak) sesuai **Status Pegawai** → set balik `refSk*Id` + reset mkg ke 0/0 → buat user Appwrite (`authService.createUser`). Gagal di langkah mana pun → **batal semua**.
- **Status Pegawai NON_PEGAWAI** memutus saga lebih awal: cukup seed/temukan Biodata lalu selesai SUKSES — tak ada record Pegawai inti, SK, kontrak, maupun user yang dibuat.
- **PUT `/pegawai/{id}` (`update`) sengaja tidak menyentuh Riwayat SK**: ia koreksi snapshot administratif (perbaikan data), bukan jalur naik pangkat/mutasi.
- **Pejabat non-pegawai-biasa** = **Dewan Pengawas** (jabatan id `1`) + **Direksi** (jabatan id `2`, `3`, `25`). Dikecualikan dari validasi ketat PegawaiTetap dan tidak diberi Golongan. Id ini **harus dipindah ke konfigurasi/env**, tidak di-hardcode.

## Arah Dependency Lintas-Modul

| Arah | Keterangan |
|------|-----------|
| `kepegawaian → pegawai` | Writeback: setiap event SK memutakhirkan field denormalisasi di Pegawai |
| `pegawai → kepegawaian` (via port) | Bootstrap: `SkBootstrapPort` & `KontrakBootstrapPort` di `services/pegawai/port/` agar arah compile tetap searah |
| `profil → updateProfile` | Profil memicu Pengajuan Perubahan; logika approve/reject milik updateProfile |
| `pegawai ← riwayat_sk` (baca langsung tabel) | Detail/Ringkasan Pegawai membaca `riwayat_sk` langsung (tanpa melewati modul kepegawaian) |

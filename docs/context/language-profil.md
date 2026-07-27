# Context — Modul Profil (Self-Service Data Pegawai)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `profil/` (biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, lampiran profil, updateProfile).

## Glossary

**Profil**:
Data pribadi yang dimiliki & dikelola pegawai sendiri — biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, beserta lampiran. Berbeda dari Master (data referensi milik admin): Profil dimiliki pegawai dan perubahannya melewati persetujuan.

**Pengajuan Perubahan** (ProfileUpdate):
Catatan bahwa pegawai mengubah salah satu data Profil-nya dan menunggu keputusan admin. Setiap pengajuan menunjuk satu baris data (`revId`), satu **Jenis Aksi** (tambah/ubah/hapus), dan satu tabel Profil yang terkena.
_Avoid_: "audit", "log" — ini bukan jejak pasif, melainkan antrian persetujuan yang menggerakkan revert.

**Jenis Aksi** (actionType): tambah (INSERT), ubah (UPDATE), atau hapus (DELETE) — menentukan perilaku saat **ditolak**.

**Dashboard Pegawai** (`GET /profil/biodata/{nik}/dashboard`):
Endpoint ringan khusus untuk tampilan dashboard pegawai di FE. Mengembalikan 13 field biodata + pegawai + `detailPendidikanTerakhir` — cukup untuk header profil dan ringkasan esensial, tanpa 40+ field seperti `BiodataDetail`.
_Path_: `{nik}` = NIK biodata. _Akses_: semua user terautentikasi (tanpa ownership check). _404 guard_: NIK tanpa baris di `pegawai` → NotFoundException (INNER JOIN PEGAWAI).
_Sumber field_: `noTelp` = `biodata.telp`, `email` = `pegawai.email` (join via `biodata.nik = pegawai.biodata_id`), `kodePajak` = `gaji_pendapatan_non_pajak.kode` (String flat, bisa null jika LEFT JOIN-nya tak punya record).
_Pendidikan_: hanya satu baris dengan `is_latest=true AND changed_status=false`; di-render sebagai `PendidikanDashboard` (tingkat/jurusan/institusi/tahunLulus). Null jika tidak ada pendidikan yang cocok.
_Enum labels_: `jenisKelamin` dikonversi dari ordinal Byte ke label "Laki-Laki"/"Perempuan"; `agama` & `statusKawin` dari enum Byte ke `.toString()`. Semua null-safe.
_Query layer_: `BiodataDashboardQuery` di repositori JOOQ — query terpisah dari `BiodataDetailQuery` (yang sudah 98 baris).

**Pendidikan Terlatest** (`isLatest`) & **Pendidikan Terakhir** (`pendidikanTerakhir`):
Seorang pegawai punya banyak baris **Pendidikan**; tepat satu ditandai sebagai yang terkini (`isLatest=true`). **Pendidikan Terakhir** adalah field turunan (denormalisasi) di **Biodata** — jenjang dari Pendidikan yang `isLatest`-nya `true`. Disimpan di Biodata sebagai jalan pintas baca, bukan sumber kebenaran tersendiri.
_Catatan domain_: nilai ini **diturunkan**, bukan diinput bebas. Menandai satu Pendidikan `isLatest=true` otomatis menyingkirkan tanda itu dari baris lain milik pegawai yang sama, lalu menyalin jenjangnya ke `Biodata.pendidikanTerakhir`.

**Status Berubah** (changedStatus):
Penanda pada baris data Profil bahwa ada perubahan yang **belum disetujui**. `true` = menunggu keputusan; `false` = stabil/disetujui. Hanya perubahan dengan `changedStatus=true` yang memunculkan Pengajuan Perubahan.
Nilainya **ditentukan server berdasarkan role**, bukan dikirim client: edit oleh **SDM** (petugas kepegawaian) langsung stabil (`changedStatus=false`, tanpa Pengajuan Perubahan); edit oleh **pegawai** menjadi menunggu (`changedStatus=true`, memunculkan Pengajuan Perubahan).
_Avoid_: menyamakan `changedStatus` dengan riwayat. `changedStatus` hanya menggerbang **Antrian Persetujuan**, bukan **Riwayat Perubahan** — riwayat selalu dicatat di setiap tulis, apa pun nilai `changedStatus`.

**Riwayat Perubahan** (Envers) vs **Antrian Persetujuan** (ProfileUpdate) — dua lapis terpisah:
- **Riwayat Perubahan**: entity Profil ber-`@Audited`, jadi **setiap** tulis (`save`) menghasilkan satu revisi Envers — tanpa peduli role maupun `changedStatus`. Riwayat tidak pernah hilang.
- **Antrian Persetujuan**: baris **Pengajuan Perubahan** hanya dibuat saat `changedStatus=true`. Ini daftar tunggu keputusan SDM, bukan riwayat.
- Konsekuensi: edit oleh **SDM** (`changedStatus=false`) **tetap** punya riwayat Envers penuh; yang dilewati hanya antrian persetujuan.
- Itu sebabnya `changed_status` dari body client adalah **bug keamanan**: membiarkan pegawai menyelundupkan perubahan langsung ke status stabil tanpa di-acc SDM (bypass persetujuan).

**Disetujui / Ditolak**:
Keputusan admin atas Pengajuan Perubahan.
- **Disetujui**: baris ditandai stabil (`changedStatus=false`); nilai yang diajukan dipertahankan.
- **Ditolak**: dikembalikan menurut Jenis Aksi —
  - tambah ditolak → baris dihapus (tak pernah sah ada);
  - ubah ditolak → **dikembalikan ke revisi sebelumnya** (dua revisi Envers terakhir dibaca, nilai revisi lama ditulis ulang);
  - hapus ditolak → batal hapus (baris diaktifkan kembali).

## Aturan Bisnis Penting

- Sisi tulis Profil tetap memicu pembuatan **Pengajuan Perubahan** (memanggil `profileUpdateService.create(...)`), tetapi seluruh logika **Disetujui/Ditolak** dan revert milik **modul updateProfile** — ketergantungan satu arah: profil → updateProfile.
- Pembacaan data **Profil** selalu **terikat pada satu pegawai** (`nik`, dibawa field `biodataId` pada query): `biodataId` adalah field **wajib** (`@NotBlank`) pada IndexQuery — bukan filter opsional. **Pengecualian: Biodata** — ia adalah direktori global yang sah bagi SDM (filter opsional).
- Membuat sebuah **Biodata** **otomatis menyemai (seed) dua data anak awal**: satu **Kartu Identitas** kosong dan satu baris **Pendidikan** `isLatest=true`. Kedua seed lahir oleh sistem (`changedStatus=false`, tanpa Pengajuan Perubahan). Keseluruhan pembuatan Biodata + dua seed berada dalam **satu transaksi**.
- **Perilaku membuat ulang** data Profil yang pernah dihapus berbeda per jenis:
  - **Pendidikan** (kunci: pegawai + jenjang + tahun masuk) dan **Kartu Identitas** (kunci: NIK + jenis kartu) — menghidupkan kembali baris lama, bukan membuat baru.
  - **Keluarga** — baris aktif dan arsip boleh berdampingan; menambah ulang = baris baru.
  - **Keahlian, Pelatihan, Pengalaman Kerja, Lampiran** — tak punya kunci alami; tiap penambahan selalu baris baru.

# Context — Modul Profil (Self-Service Data Pegawai)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `profil/` (biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, lampiran profil, updateProfile).

## Glossary

**Profil**:
Data pribadi yang dimiliki & dikelola pegawai sendiri — biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, beserta lampiran. Berbeda dari Master (data referensi milik admin): Profil dimiliki pegawai dan perubahannya melewati persetujuan.

**Pengajuan Perubahan** (ProfileUpdate):
Catatan bahwa pegawai mengubah salah satu data Profil-nya dan menunggu keputusan petugas kepegawaian. Setiap pengajuan menunjuk satu baris data (`revId`), satu **Jenis Aksi** (tambah/ubah/hapus), dan satu tabel Profil yang terkena.
_Cakupan (keputusan grill 2026-08-12)_: **8 entity** — Biodata, Keluarga (ProfilKeluarga), Pendidikan, Keahlian, Pelatihan, PengalamanKerja, KartuIdentitas, LampiranProfil.
_Avoid_: "audit", "log" — ini bukan jejak pasif, melainkan antrian persetujuan yang menggerakkan revert.

**Jenis Aksi** (actionType): tambah (INSERT), ubah (UPDATE), atau hapus (DELETE) — menentukan perilaku saat **ditolak**.

**Dashboard Pegawai** (`GET /profil/biodata/{nik}/dashboard`):
Endpoint ringan khusus untuk tampilan dashboard pegawai di FE. Mengembalikan 14 field biodata + pegawai + `detailPendidikanTerakhir` — cukup untuk header profil dan ringkasan esensial, tanpa 40+ field seperti `BiodataDetail`.
_Path_: `{nik}` = NIK biodata. _Akses_: semua user terautentikasi — ownership check via `ownershipGuard.assertSelfRead(nik)` (HRD/ADMIN bebas via `PROFIL:READ`; pegawai biasa hanya bisa baca data sendiri → 404 jika bukan miliknya). _404 guard_: NIK tidak ditemukan di `biodata` → `NotFoundException("Biodata not found or NIK bukan pegawai")`.
_Sumber field_: `noTelp` = `biodata.telp`, `email` = `pegawai.email` (multiset subquery correlated on `biodata.nik = pegawai.biodata_id`), `kodePajak` = `gaji_pendapatan_non_pajak.kode` (multiset, bisa null jika pegawai tidak punya record). `changedStatus` = `biodata.changed_status` (Boolean — `true` berarti biodata menunggu approval, diambil langsung dari kolom tabel tanpa filter).
_Pendidikan_: multiset subquery — hanya satu baris dengan `is_latest=true AND changed_status=false`; di-render sebagai `PendidikanDashboard` (tingkat/jurusan/institusi/tahunLulus). Null jika tidak ada pendidikan yang cocok.
_Enum labels_: `jenisKelamin` dikonversi dari ordinal Byte ke label "Laki-Laki"/"Perempuan"; `agama` & `statusKawin` dari enum Byte ke `.toString()`. Semua null-safe.
_Query layer_: `BiodataDashboardQuery` di repositori JOOQ — **multiset subqueries** isolasi PEGAWAI dan PENDIDIKAN dari main query BIODATA (mencegah JOIN fan-out; sebelumnya flat JOINs menyebabkan `Cursor returned more than one result`).

**Pendidikan Terlatest** (`isLatest`) & **Pendidikan Terakhir** (`pendidikanTerakhir`):
Seorang pegawai punya banyak baris **Pendidikan**; tepat satu ditandai sebagai yang terkini (`isLatest=true`). **Pendidikan Terakhir** adalah field turunan (denormalisasi) di **Biodata** — jenjang dari Pendidikan yang `isLatest`-nya `true`. Disimpan di Biodata sebagai jalan pintas baca, bukan sumber kebenaran tersendiri.
_Catatan domain_: nilai ini **diturunkan**, bukan diinput bebas. Menandai satu Pendidikan `isLatest=true` otomatis menyingkirkan tanda itu dari baris lain milik pegawai yang sama, lalu menyalin jenjangnya ke `Biodata.pendidikanTerakhir`.
_Catatan guard (ADR-0035)_: invarian "tepat satu `true` per pegawai" dijamin **dua lapis** — normalisasi aplikasi (`handleUpdateIsLatest`, transaksional) **dan** generated column `is_latest_biodata` + `UNIQUE` di level DB. Saat baris `true` dihapus/di-set `false`, pointer `pendidikanTerakhir` **dibiarkan** (tidak di-clear); sinkron hanya terjadi saat `isLatest=true` di-set.

**Status Disetujui** (`disetujui` di baris Profil):
Boolean di baris data (mis. `pendidikan.disetujui`) yang menandai data **sudah diverifikasi/disetujui petugas kepegawaian** — disertai `tanggalDisetujui` dan `disetujuiOleh` (stamp sekali, tidak berubah-ubah). _Jangan disamakan dengan_: (a) **Status Berubah** (`changedStatus`) — menggerbang antrian persetujuan dan bisa berubah-ubah per perubahan; (b) keputusan **"Disetujui / Ditolak"** atas **Pengajuan Perubahan** — itu keputusan petugas kepegawaian di antrian, ini status permanen baris.
_Catatan domain (Pendidikan, ADR-0035)_: nilainya **ditentukan server berdasarkan role**, bukan dari request — penulis **HRD/ADMIN** → `true` + stamp; penulis **pegawai/self-service** → `false` sampai HRD menyetujui di antrian (approve → `true` + stamp oleh approver; reject → tetap `false`).

**Status Berubah** (changedStatus):
Penanda pada baris data Profil bahwa ada perubahan yang **belum disetujui**. `true` = menunggu keputusan; `false` = stabil/disetujui. Hanya perubahan dengan `changedStatus=true` yang memunculkan Pengajuan Perubahan.
Nilainya **ditentukan server berdasarkan role**, bukan dikirim client: edit oleh **HRD/ADMIN** (petugas kepegawaian) langsung stabil (`changedStatus=false`, tanpa Pengajuan Perubahan); edit oleh **pegawai** menjadi menunggu (`changedStatus=true`, memunculkan Pengajuan Perubahan). Guard pemanggilan `create()` di CommandService **seragam**: hanya dipanggil saat `changedStatus=true`.
_Pengecualian LampiranProfil (keputusan grill 2026-08-12)_: lampiran hanya punya operasi **Insert/Delete** dan `LampiranProfil` **tidak memiliki kolom `changedStatus`**. Ia tetap masuk antrian, tetapi guard enqueue memakai `resolver.requiresApproval()` langsung, dan status menunggu dibaca dari `disetujui=false` + adanya entri Pengajuan Perubahan ber-status PENDING. (7 entity lain tetap memakai kolom `changedStatus`.)
_Avoid_: menyamakan `changedStatus` dengan riwayat. `changedStatus` hanya menggerbang **Antrian Persetujuan**, bukan **Riwayat Perubahan** — riwayat selalu dicatat di setiap tulis, apa pun nilai `changedStatus`.

**Riwayat Perubahan** (Envers) vs **Antrian Persetujuan** (ProfileUpdate) — dua lapis terpisah:
- **Riwayat Perubahan**: entity Profil ber-`@Audited`, jadi **setiap** tulis (`save`) menghasilkan satu revisi Envers — tanpa peduli role maupun `changedStatus`. Riwayat tidak pernah hilang.
- **Antrian Persetujuan**: baris **Pengajuan Perubahan** hanya dibuat saat `changedStatus=true`. Ini daftar tunggu keputusan SDM, bukan riwayat.
- Konsekuensi: edit oleh **SDM** (`changedStatus=false`) **tetap** punya riwayat Envers penuh; yang dilewati hanya antrian persetujuan.
- Itu sebabnya `changed_status` dari body client adalah **bug keamanan**: membiarkan pegawai menyelundupkan perubahan langsung ke status stabil tanpa di-acc SDM (bypass persetujuan).

**Disetujui / Ditolak**:
Keputusan petugas kepegawaian atas Pengajuan Perubahan.
- **Disetujui**: baris ditandai stabil (`changedStatus=false`); nilai yang diajukan dipertahankan. Untuk entity ber-`disetujui` (Pendidikan, Keahlian, Pelatihan, PengalamanKerja, LampiranProfil) → `disetujui=true` + stamp oleh approver (pola ADR-0035); KartuIdentitas cukup `changedStatus=false`.
- **Ditolak**: dikembalikan menurut Jenis Aksi —
  - tambah ditolak → baris dihapus (tak pernah sah ada); lampiran: file fisik ikut dihapus;
  - ubah ditolak → **dikembalikan ke revisi sebelumnya** (revisi Envers sebelumnya dibaca, nilai lama ditulis ulang via setter eksplisit — pola load-and-set-and-save, bukan refleksi blind maupun bulk JPQL, lihat ADR-0036);
  - hapus ditolak → batal hapus (baris diaktifkan kembali).

## Aturan Bisnis Penting

- Sisi tulis Profil tetap memicu pembuatan **Pengajuan Perubahan** (memanggil `profileUpdateService.create(...)`), tetapi seluruh logika **Disetujui/Ditolak** dan revert milik **modul updateProfile** — ketergantungan satu arah: profil → updateProfile.
- Pembacaan data **Profil** selalu **terikat pada satu pegawai** (`nik`, dibawa field `biodataId` pada query): `biodataId` adalah field **wajib** (`@NotBlank`) pada IndexQuery — bukan filter opsional. **Pengecualian: Biodata** — ia adalah direktori global yang sah bagi SDM (filter opsional).
- Membuat sebuah **Biodata** **otomatis menyemai (seed) dua data anak awal**: satu **Kartu Identitas** kosong dan satu baris **Pendidikan** `isLatest=true`. Kedua seed lahir oleh sistem (`changedStatus=false`, tanpa Pengajuan Perubahan). Keseluruhan pembuatan Biodata + dua seed berada dalam **satu transaksi**.
- **Perilaku membuat ulang** data Profil yang pernah dihapus berbeda per jenis:
  - **Pendidikan** (kunci: pegawai + jenjang + tahun masuk) dan **Kartu Identitas** (kunci: NIK + jenis kartu) — menghidupkan kembali baris lama, bukan membuat baru.
  - **Keluarga** — baris aktif dan arsip boleh berdampingan; menambah ulang = baris baru.
  - **Keahlian, Pelatihan, Pengalaman Kerja, Lampiran** — tak punya kunci alami; tiap penambahan selalu baris baru.

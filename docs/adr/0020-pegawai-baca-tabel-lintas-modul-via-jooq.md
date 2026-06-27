# Baca read-model Pegawai membaca tabel lintas modul langsung lewat JOOQ

> **Status:** accepted — memperluas ADR-0017 (JOOQ di repository) untuk kasus baca lintas batas modul.

Read-model Pegawai (list/page, `/{nipam}/nipam`, dan Ringkasan) dirakit oleh satu `PegawaiQueryRepository` (`repositories/pegawai/jooq/`) yang menjangkau **tabel milik modul lain secara langsung** — bukan lewat service/query modul tersebut. Konkretnya, satu query JOOQ Pegawai membaca:

- `riwayat_sk` (milik modul **kepegawaian**) lewat `multiset`/sub-select untuk 7 slot SK terbaru-per-jenis (skCapeg, skPegawai, skGolongan, skJabatan, skMutasi, skKontrak, skGajiBerkala).
- `pendidikan` (milik modul **profil**) difilter `is_latest = true` untuk `lembagaPendidikan`/`tahunLulus`.
- `kartu_identitas` (milik modul **profil**) difilter per **nama jenis kartu** (NPWP/JPn/BPJS/ID Card) untuk nomor kartu pada Ringkasan.

Semua perakitan/format string (mis. `pangkat-golongan`, `"N Tahun M Bulan"`) tetap **di Java** pada mapper, bukan di SQL.

## Considered Options

- **Panggil service modul lain** (mis. `riwayatSkQueryService`, `pendidikanQueryService`): hormati enkapsulasi modul, tapi memaksa N+1 atau round-trip antarservice, mengubah baca tunggal jadi banyak query, dan menyeret read-model Pegawai ke bentuk DTO modul lain yang lebih berat dari yang dibutuhkan.
- **Baca tabel langsung via JOOQ** (dipilih): satu query rakitan, proyeksi `row(...)` ramping persis sesuai kebutuhan FE. JOOQ membaca **tabel**, bukan kode modul — kontrak baca Pegawai tidak ikut berubah saat *kode* modul kepegawaian/profil ditulis ulang, selama skema tabelnya stabil.

## Consequences

- **Kopling ke skema tabel, bukan ke API modul.** Yang mengikat sekarang adalah nama tabel/kolom (`riwayat_sk`, `pendidikan.is_latest`, `kartu_identitas`), bukan signature service. Ini justru lebih tahan terhadap rewrite modul lain (sumber JOOQ digenerate dari skema, ketahuan saat compile bila kolom hilang) — sejalan dengan alasan memilih `multiset` di tingkat tabel.
- **Risiko tercatat: filter kartu identitas by-string-nama rapuh.** `kartu_identitas` difilter dengan mencocokkan **nama** jenis kartu ("NPWP"/"JPn"/"BPJS"/"ID Card"). Bila master Jenis Kartu di-rename, kolom Ringkasan diam-diam jadi kosong tanpa error. Di luar scope rewrite Pegawai (perlu master Jenis Kartu yang stabil/by-id), tapi **dicatat** sebagai utang.
- **Batas modul jadi konvensi, bukan paksaan kompilasi.** Tidak ada lagi dinding service yang mencegah Pegawai membaca tabel modul lain. Disiplin dijaga lewat aturan arah: hanya **baca**, hanya read-model, dan hanya tabel (bukan menulis silang modul). Menulis tetap lewat modul pemilik (lihat [ADR-0021](0021-pegawai-saga-atomik-dengan-sistem-eksternal.md)).
- **Relasi:** memperluas ADR-0017 (akses JOOQ di repository) ke kasus lintas modul; konsisten dengan keputusan read-model di CONTEXT.md (bagian "Keputusan baca Ringkasan").

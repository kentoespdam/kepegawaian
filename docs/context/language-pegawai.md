# Context — Modul Pegawai (Catatan Kepegawaian Inti)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan modul `pegawai/` atau modul `kepegawaian/` (SK, Mutasi, Kontrak, Terminasi, SP).

## Glossary

**Pegawai**:
Catatan kepegawaian inti seseorang — menunjuk satu **Biodata** (lewat NIK), satu Organisasi, Jabatan, Profesi, Golongan, Grade, plus status & data gaji. Kunci tampil **NIPAM**.
_Avoid_: "karyawan", "user" (user = principal autentikasi, beda konsep).

**NIPAM**:
Nomor induk pegawai PERUMDAMTS — identitas pegawai yang bermakna bagi manusia (dipakai di endpoint `/{nipam}/nipam`). Berbeda dari `id` (kunci teknis Long).

**Status Pegawai** (EStatusPegawai):
Kelas kepegawaian: CALON (capeg), PEGAWAI (tetap), KONTRAK, HONORER, CALON_HONORER, NON_PEGAWAI, dst. Menentukan jalur saga saat pembuatan (capeg→SK Capeg, tetap/honorer→SK Pegawai, kontrak→kontrak, non-pegawai→berhenti tanpa SK) dan apakah golongan diisi.

**Status Kerja** (EStatusKerja):
Status aktif/berhenti pegawai (mis. masih bekerja, pensiun, berhenti) — terpisah dari Status Pegawai yang menyatakan kelas kepegawaian.

**Jenis SK** (EJenisSk):
Kategori Surat Keputusan, disimpan sebagai **enum ordinal** (bukan tabel referensi): SK_CAPEG, SK_PEGAWAI_TETAP, SK_KENAIKAN_PANGKAT_GOLONGAN, SK_JABATAN, SK_MUTASI, SK_LAINNYA, SK_KENAIKAN_GAJI_BERKALA, dst. Karena ordinal, nama jenis diselesaikan di Java (`EJenisSk.values()[ordinal]`), tanpa JOIN tabel.

**Slot SK Terkini** (7 slot bernama):
Detail pegawai mengekspos tepat 7 slot SK, tiap slot = baris **Riwayat SK** terbaru (`tmt_berlaku` desc) untuk satu Jenis SK: `skCapeg`=SK_CAPEG, `skPegawai`=SK_PEGAWAI_TETAP, `skGolongan`=SK_KENAIKAN_PANGKAT_GOLONGAN, `skJabatan`=SK_JABATAN, `skMutasi`=SK_MUTASI, `skKontrak`=SK_LAINNYA, `skGajiBerkala`=SK_KENAIKAN_GAJI_BERKALA. Tiap slot membawa data Golongan ringkas (id, golongan, pangkat). `skCapeg` jika ada meng-override `tanggalSk` detail = `skCapeg.tmtBerlaku`.

**Riwayat SK**:
Histori Surat Keputusan milik pegawai (entity `RiwayatSk`, domain `kepegawaian` — **modul terpisah** dari Pegawai). Tiap baris: nomor SK, tanggal, tmt berlaku, Jenis SK, golongan, gaji pokok, masa kerja golongan. Detail pegawai membacanya langsung dari **tabel** `riwayat_sk` (bukan via modul kepegawaian) supaya rewrite modul itu kelak tak membatalkan baca pegawai.
**Nomor SK = referensi dokumen, BUKAN identitas baris.** Identitas baris adalah `id`. Nomor SK boleh terpakai ulang antar baris (mis. peristiwa "kembali ke jabatan semula" memakai ulang nomor SK pengangkatan asli) — tidak ada unique constraint di DB maupun aplikasi. Yang dijaga hanyalah anti-duplikat eksak: baris dengan `pegawai + nomorSk + jenisSk + tanggalSk` sama persis ditolak (ADR-0034).

**PLT (Pelaksana Tugas)**:
Penugasan sementara pegawai pada suatu jabatan. Saat masa PLT berakhir, pegawai kembali ke jabatan definitifnya — dicatat sebagai **baris Riwayat SK / Mutasi baru** yang **memakai ulang nomor SK pengangkatan asli** (tanggal & TMT baru). PLT **tidak dimodelkan** sebagai entitas/field/flag tersendiri (keputusan sesi 2026-08-12); sistem hanya melihatnya sebagai riwayat jabatan biasa. `notes` pada baris SK dapat dipakai untuk menandai "kembali ke jabatan semula".

**Session** vs **Tabel** vs **Ringkasan** vs **Detail**:
Empat tingkat baca pegawai, dari paling ringan ke paling berat. **Session** (`/{id}/session`) = payload **paling minim** untuk di-cache FE **sesaat setelah login** + jadi kunci shortcut-fetch ke page (dashboard, data-pegawai, terminasi): `id (Long)`, `nipam`, `nik`, `nama`, `jabatan{id,nama}`, `organisasi{id,nama}`. **Tabel** (`GET /pegawai` — root) = DTO **PegawaiTableResponse** khusus render tabel FE: 14 kolom flat + nested organisasi/jabatan/profesi `{id,nama}` via `RefMiniResponse`. **Ringkasan** (`/{id}/ringkasan`) = bentuk pipih siap-tampil untuk halaman profil, banyak field string hasil format Java (mis. `pangkatGolongan` = golongan+" - "+pangkat, `mkg` = "X Tahun Y Bulan") plus nomor kartu identitas (NPWP/JPn/BPJS/ID Card) yang difilter dari Kartu Identitas pegawai. **Detail** (`/{id}`) = agregat penuh + 7 slot SK terkini.
_Keputusan baca Session_: query JOOQ paling ramping — hanya JOIN `biodata` (nama+nik), `organisasi`, `jabatan`. **Tanpa** multiset, **tanpa** JOIN LEVEL, tanpa field gaji/SK. Objek bersarang pakai record generik `RefMiniResponse(id, nama)`. Prinsip tetap: pangkas ke `id`+label; tambah field hanya kalau FE nyata butuh (YAGNI).
_Keputusan baca Tabel (PegawaiTableResponse)_: query JOOQ ramping — JOIN `biodata`, `organisasi`, `jabatan`, `profesi`, `golongan`, `gaji_pendapatan_non_pajak`. **DROP** `pendidikan` & `grade` (tidak dipakai render). Kolom: `id, nipam, nama, jenisKelamin, tanggalLahir, tmtPensiun, statusKawin, kodePajak, isBpjs=PEGAWAI.IS_ASKES, pangkatGolongan=golongan+" - "+pangkat, statusPegawai, organisasi{id,nama}, jabatan{id,nama}, profesi{id,nama}`.
  - `isBpjs` = mapping dari `PEGAWAI.IS_ASKES` (Askes=nama lama BPJS Kesehatan).
  - `jenisKelamin` label: `EJenisKelamin.values()[b]==LAKI_LAKI?"Laki-Laki":"Perempuan"`.
  - `statusKawin` label: `EStatusKawin.values()[b].toString()` (enum name).
  - `statusPegawai` label: `EStatusPegawai.values()[b].value`.
  - Nested mini pakai `RefMiniResponse(id, nama)`.
  - Mapper: `PegawaiTableRecordMapper.mapTableResponse()`.
_Keputusan baca Ringkasan_: satu query JOOQ proyeksi field mentah, dengan **baca tabel lintas modul langsung** — `pendidikan` (baris `is_latest=true`) dan `kartu_identitas` (difilter per **nama jenis kartu**). Semua perakitan string tetap **di Java** pada layer mapper, bukan di SQL.

**Masa Kerja Golongan** (mkg: mkgTahun + mkgBulan):
Lama pegawai berada pada golongan saat ini, dalam tahun + bulan. Di Ringkasan diformat jadi string "X Tahun Y Bulan".

**Bentuk JSON baca (nested mini)**:
`PegawaiResponse` (`/{nipam}/nipam`) **tetap bersarang** karena FE sudah membaca `.organisasi.nama` dsb. — TIDAK dipipihkan. Tapi tiap objek bersarang adalah **mini response** (proyeksi JOOQ `row(...)` ramping), bukan DTO penuh: biodata=`nik,nama,gelarDepan,gelarBelakang`; organisasi=`id,nama`; jabatan=`id,nama`; profesi=`id,nama`; golongan=`id,golongan,pangkat`; grade=`id,grade`; kodePajak=`id,nama`. Plus field skalar denormalisasi pegawai. Prinsip: pangkas ke `id`+label; tambah lagi kalau FE benar-benar butuh.
`PegawaiTableResponse` (`GET /pegawai` root) memakai `RefMiniResponse(id,nama)` untuk nested organisasi/jabatan/profesi — sengaja lebih ramping dari `PegawaiResponse` karena tabel FE cuma butuh id+nama.

**Pejabat non-pegawai-biasa**:
**Dewan Pengawas** (jabatan id `1`) + **Direksi** (jabatan id `2`, `3`, `25`). Dikecualikan dari validasi ketat PegawaiTetap saat dibuat sebagai status PEGAWAI, dan tidak diberi **Golongan** (golongan dipaksa null). Id ini **harus dipindah ke konfigurasi/env**, tidak di-hardcode.

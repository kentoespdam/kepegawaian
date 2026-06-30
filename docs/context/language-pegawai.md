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

**Ringkasan** vs **Detail**:
Dua bentuk baca pegawai. **Detail** (`/{id}`) = agregat penuh + 7 slot SK terkini. **Ringkasan** (`/{id}/ringkasan`) = bentuk pipih siap-tampil, banyak field string hasil format Java (mis. `pangkatGolongan` = pangkat+"-"+golongan, `mkg` = "X Tahun Y Bulan") plus nomor kartu identitas (NPWP/JPn/BPJS/ID Card) yang difilter dari Kartu Identitas pegawai.
_Keputusan baca Ringkasan_: satu query JOOQ proyeksi field mentah, dengan **baca tabel lintas modul langsung** — `pendidikan` (baris `is_latest=true`) dan `kartu_identitas` (difilter per **nama jenis kartu**). Semua perakitan string tetap **di Java** pada layer mapper, bukan di SQL.

**Masa Kerja Golongan** (mkg: mkgTahun + mkgBulan):
Lama pegawai berada pada golongan saat ini, dalam tahun + bulan. Di Ringkasan diformat jadi string "X Tahun Y Bulan".

**Bentuk JSON baca (nested mini)**:
`PegawaiResponse` (list/page & `/{nipam}/nipam`) **tetap bersarang** karena FE sudah membaca `.organisasi.nama` dsb. — TIDAK dipipihkan. Tapi tiap objek bersarang adalah **mini response** (proyeksi JOOQ `row(...)` ramping), bukan DTO penuh: biodata=`nik,nama,gelarDepan,gelarBelakang`; organisasi=`id,nama`; jabatan=`id,nama`; profesi=`id,nama`; golongan=`id,golongan,pangkat`; grade=`id,grade`; kodePajak=`id,nama`. Plus field skalar denormalisasi pegawai. Prinsip: pangkas ke `id`+label; tambah lagi kalau FE benar-benar butuh.

**Pejabat non-pegawai-biasa**:
**Dewan Pengawas** (jabatan id `1`) + **Direksi** (jabatan id `2`, `3`, `25`). Dikecualikan dari validasi ketat PegawaiTetap saat dibuat sebagai status PEGAWAI, dan tidak diberi **Golongan** (golongan dipaksa null). Id ini **harus dipindah ke konfigurasi/env**, tidak di-hardcode.

# 0056 — Potongan Tambahan: parsing fesod lokal menggantikan forward ke service penggajian legacy

Status: Accepted
Date: 2026-09-07

## Context

Endpoint `PATCH /penggajian/batch/master/upload/{rootBatchId}`
(`GajiBatchMasterCommandService.uploadPotonganTambahan`) hari ini tidak pernah
mengurai file: ia menyimpan berkas (`FileUploadUtil`), mencatat
`GajiBatchRootLampiran` ber-`POTONGAN_TAMBAHAN`, lalu **meneruskan file mentah
ke service penggajian legacy** (`PATCH {penggajian.endpoint}/upload/{id}/additional_gaji`).
Artinya validasi, pembuatan baris `ADD_` di `gaji_batch_master_proses`, dan
recalculate semuanya terjadi di luar sistem ini — mesin recalculate/rollback
`ADD_%` lokal (`GajiBatchMasterProsesCommandService`) sudah ada sejak rewrite
CQRS (lihat `docs/penggajian-cqrs-claim-order.md`) tapi tidak pernah dipakai
oleh alur upload ini.

Sementara itu, dua upload lain sudah memakai **Apache Fesod** (incubating,
`fesod-sheet`) lokal dengan pola yang sama: **Gaji KPI** (`GajiKpiBatchService`)
dan **Potongan TKK** (`GajiBatchPotonganTkkBatchService`) — read → normalize
NIPAM → validasi master → batch insert via JOOQ.

File input Potongan Tambahan bukan template sederhana. File nyata
("DAFTAR POTONGAN GAJI PEGAWAI", mis. `data_potongan_gaji_202608.xlsx`) adalah
**workbook multi-sheet** — satu sheet per organisasi (DIREKSI, BAGIAN
KEUANGAN, CABANG PURWOKERTO 1, … 16 sheet) — dengan header tabel di **baris 9**
(`NO. | NAMA | NIPAM | GAJI | POTONGAN(E10..X10) | JUMLAH POTONGAN | GAJI
BERSIH`), 20 kategori potongan di kolom E..X, dan baris total di ekor sheet.
Ancestor legacy-nya adalah `PayrollVerification::UploadAdhocComponent`
(smartoffice `payrollverification.php`), bukan `CreateBatch` di
`payrollprocess.php` (yang itu ancestor Potongan TKK).

## Decision

**Parsing dipindah ke lokal dengan Fesod; forward ke service legacy dihapus
dari alur Potongan Tambahan.** Rincian keputusan (hasil grilling
2026-09-07):

1. **File = Form Potongan Gaji resmi.** Parser membaca SEMUA sheet.
   Header sheet terdeteksi dinamis: baris dengan kolom B=`NAMA` **dan**
   C=`NIPAM`; label kategori diambil dari baris berikutnya, **mulai kolom E
   sampai kolom berlabel terakhir** (mengikuti pola legacy `UploadAdhocComponent`
   — kolom Y/Z tidak punya label baris-10 karena merge, jadi otomatis
   ter-exclude). Baris data = kolom A, B, C semua terisi. Baris total tidak
   lolos deteksi (kolom B/C kosong).
2. **Kode dinamis dari header + prefix `ADD_`**, bukan satu kode tetap.
   Slug: uppercase, karakter non-alphanumeric → `_`, run `_` dipadatkan —
   `PINJ.KOPR.` → `ADD_PINJ_KOPR`, `SIMPANAN KOPERASI` → `ADD_SIMPANAN_KOPERASI`,
   `Gn. SIMPING` / `Gn SIMPING` → `ADD_GN_SIMPING` (konvergen), `LAIN-LAIN` →
   `ADD_LAIN_LAIN`. `nama` = label asli sheet; `jenisGaji = POTONGAN`;
   `urut = 99`. Divergensi disengaja dari legacy (yang memakai satu kode
   `adhoc` + description) demi keter-query-an per kategori.
3. **Multi-rows per NIPAM adalah normal** — satu baris per kategori terisi.
   Validasi duplikat NIPAM hanya antar-sheet (NIPAM sama di dua sheet = error,
   legacy menoleransi ini = potongan ganda).
4. **Nilai:** kosong / non-numerik / 0 → tidak menghasilkan baris (fidelity
   legacy `is_numeric && > 0`); negatif → error penolakan file.
5. **Unknown NIPAM menolak seluruh file** (all-or-nothing, digest 20 error ala
   TKK/KPI). Strictness di dua level: NIPAM harus terdaftar **dan** harus jadi
   `GajiBatchMaster` milik batch ini (bukan sekadar pegawai terdaftar — baris
   `ADD_` butuh `batchMasterId`). Legacy diam-diam me-skip NIPAM tak dikenal =
   potongan hilang tanpa jejak.
6. **Re-upload = replace.** Hapus semua baris `ADD_%` milik batch (per
   `batchMasterId` anak-anaknya) → insert ulang → recalculate. Mengganti
   perilaku legacy **accumulate** (dobel upload = dobel potongan) yang terbukti
   jadi sumber kesalahan; selaras pola delete+insert Potongan TKK.
7. **Window upload = hanya `WAIT_VERIFICATION_PHASE_2`** (padanan status
   legacy `3`). Di luar window → tolak dengan pesan yang menyebut status
   kini. Menutup lubang keamanan endpoint lama yang tidak punya guard.
8. **Recalculate** memakai `recalculateAdditional` yang sudah ada per master
   terdampak — tanpa fork logika matematika legacy (`rounding_2 = 0` di legacy
   adalah perilaku rewrite saat ini).
9. **Lampiran tetap dicatat** (`GajiBatchRootLampiran` `POTONGAN_TAMBAHAN`)
   berdampingan dengan baris `ADD_`; file tetap tersimpan lokal.
10. **Template per-batch digenerate**, bukan statik: satu sheet per
    `namaOrganisasi` dari `GajiBatchMaster` milik batch, baris pre-fill
    NO | NAMA | NIPAM | GAJI (`gaji_pokok` snapshot), sel potongan kosong.
    `mail_code` legacy tidak ada di DB baru → pengganti sementara
    **`org_group`**. Endpoint `GET .../template/download/{rootBatchId}`.
11. **Respons** `SavedResult<String>` `"{n} success"` sesuai ADR-0031
    (n = jumlah baris `ADD_` ditulis). Insert massal via JOOQ batch.
12. **Out of scope, follow-up:** `downloadPotonganGaji` masih forward ke legacy
    (`/export/potongan/`) — issue terpisah.

## Considered Options

- **Parsing lokal + hapus forward** (dipilih): satu sumber kebenaran; mesin
  recalculate lokal terpakai; validasi strict all-or-nothing; window status
  ditegakkan. Biaya: parser harus tahan banting terhadap variasi form
  (typo header antar-sheet → diatasi slug; posisi kolom tetap diasumsikan
  mengikuti form resmi).
- **Parse lokal + forward tetap jalan** (ditolak): baris `ADD_` dibuat dua
  kali di dua sistem → dobel potongan atau drift total; forward butuh
  service legacy hidup selamanya.
- **Parse lokal untuk validasi saja, legacy tetap penulis** (ditolak):
  validasi dan penulisan berpisah = race antara keputusan dan efek; rollback
  lokal tidak pernah tahu data legacy.
- **Kode tetap satu `ADD_POTONGAN`** (ditolak): kehilangan keter-query-an per
  kategori; totals per kategori tak bisa dihitung tanpa LIKE atas `nama`.
- **Slug strip semua non-alphanumeric** (ditolak): `ADD_PINJKOPR` lebih sulit
  dibaca; underscore dipilih demi keterbacaan, typo tanda baca tetap konvergen.
- **Label header dibaca penuh-dinamis tanpa jangkar** (ditolak): tanpa jangkar
  B=`NAMA`/C=`NIPAM`, satu perubahan layout merusak deteksi tanpa jejak.

## Consequences

- Service penggajian legacy tidak lagi menjadi dependensi alur Potongan
  Tambahan (satu langkah menuju retirement; endpoint `/upload/{id}/additional_gaji`
  legacy tinggal mati tanpa penelepon dari sini).
- Parser sensitif terhadap perubahan layout **form resmi** (bukan template
  internal): penambahan kategori kolom otomatis terangkat jadi kode baru
  ber-`ADD_` — inilah tujuan desain dinamis; pergeseran posisi kolom A–D
  harus mengikuti deteksi header (B/C anchor), bukan indeks mati.
- Baris `ADD_<KATEGORI>` baru muncul di laporan tanpa perlu perubahan kode —
  konsekuensi yang diinginkan, tapi laporan yang hard-code daftar kategori
  perlu update.
- Re-upload aman; tidak ada lagi mekanisme "rollback dulu baru upload".
  `rollback(rootBatchId)` tetap ada untuk pembatalan penuh via workflow.
- Upload di luar `WAIT_VERIFICATION_PHASE_2` sekarang 4xx — client lama yang
  mengandalkan window longgar akan kena; komunikasikan ke FE.

## References

- Legacy ancestor: `smartoffice/server/application/direct/payrollverification.php`
  → `UploadAdhocComponent` (bukan `payrollprocess.php` — itu jalur Potongan TKK).
- Mesin recalculate/rollback: `GajiBatchMasterProsesCommandService`
  (`recalculateAdditional`, `rollback`).
- Referensi file nyata: `template/data_potongan_gaji_202608.xlsx` (16 sheet).
- Glossary: `docs/context/language-penggajian.md` — **Potongan Tambahan**,
  **Form Potongan Gaji**.

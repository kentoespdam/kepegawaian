# Context — Keputusan Rewrite: Modul Pegawai & Kepegawaian

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan rewrite modul `pegawai/` atau `kepegawaian/` (SK, Mutasi, Kontrak, Terminasi, SP, Lampiran SK).

---

## Keputusan Rewrite Sisi-Tulis Pegawai

- **Struktur Command**: satu `PegawaiCommandService` `@Transactional` dengan helper privat per cabang `Status Pegawai` (capeg / tetap-honorer / kontrak / non-pegawai). BUKAN dipecah ke kelas Command/Step terpisah — mengikuti gaya CommandService tunggal modul master & profil; saga ini jarang berubah, lapisan abstraksi tambahan tak sepadan. `authService.createUser` + pembuatan SK dipanggil berurutan dalam transaksi yang sama.

- **SortParam**: pakai implementasi nyata `final class SortParam.resolve(sortBy, sortDir, Map<String,Field<?>> allowedSorts, Field<?> defaultColumn)` (di `dto/commons`), BUKAN bentuk `record`+`Map<String,String>` yang masih tertulis di guide (guide basi, perlu dikoreksi terpisah). `sortBy` tak dikenal/blank → `defaultColumn` (default kolom ID), tanpa error; hanya `"asc"` eksplisit (case-insensitive) yang ascending, selain itu descending.

- **Endpoint PATCH dipertahankan apa adanya**: `patchGaji` (kodePajak, gajiProfil, rumahDinas) dan `patchProfil` (golongan, organisasi, jabatan, profesi) tetap endpoint PATCH parsial terpisah dari PUT `update`. Walau field `patchProfil ⊂ update`, FE punya **menu "update profil" tersendiri** → kontrak tak boleh diubah. Semua mengembalikan `{status,id}` tanpa re-read.

---

## Keputusan Rewrite Modul Kepegawaian

- **Modul Kepegawaian** mengelola enam aggregate **Riwayat**: **Riwayat SK**, **Riwayat Mutasi**, **Riwayat Kontrak**, **Riwayat Terminasi**, **Riwayat SP**, dan **Lampiran SK**. Semua bersifat *append* histori administratif; tak ada hard-delete (soft-delete `is_deleted`).

- **Riwayat SK adalah aggregate akar** modul ini: Mutasi, Kontrak, dan Terminasi semuanya **menghasilkan satu baris Riwayat SK** sebagai efek samping (mutasi → SK jabatan/golongan, kontrak → SK kontrak, terminasi → SK pensiun/berhenti).

- **Penulisan-balik ke Pegawai (Pegawai Writeback)**: setiap event SK yang sah memutakhirkan field denormalisasi di Pegawai (`nipam`, `gajiPokok`, `statusPegawai`, `mkgTahun/Bulan`, dan pasangan `refSk*Id`+`tmt*` per jenis SK). Gabungkan kedua jalur writeback legacy ke **satu kelas pemilik** `PegawaiWriteback` di `services/pegawai/pegawai/`. Tiap operasi tetap jadi method tersendiri (`updateGolongan`, `updateJabatan`, `updateKontrak`, `applyFromRequest`).

- **Arah dependency lintas-modul = satu arah `kepegawaian → pegawai` (DIP port)**: modul pegawai mendefinisikan **DUA port** karena method bootstrap jatuh ke dua aggregate berbeda — `SkBootstrapPort` (`createSkCapeg`, `createSkPegawaiTetap`) dan `KontrakBootstrapPort` (`createKontrakFromPegawai`), diletakkan di `services/pegawai/port/`. `RiwayatSkCommandService` meng-`implements` `SkBootstrapPort`; `RiwayatKontrakCommandService` meng-`implements` `KontrakBootstrapPort`. Detail: [ADR-0023](../adr/0023-cross-module-dip-port-sk-bootstrap.md).

- **Split seragam 6 Command + 6 Query** (satu pasang per aggregate: SK, Mutasi, Kontrak, Terminasi, SP, Lampiran), masing-masing di `services/kepegawaian/<aggregate>/`. Tiap aggregate punya controller sendiri (6 controller) yang inject KEDUA `queryService` + `commandService` — pemisahan Command/Query hanya di **layer service**, TIDAK ada `*CommandController`/`*QueryController` terpisah.

- **Tidak ada lagi kelas `Generic*Service`** di hasil rewrite. Orkestrasi tulis (validasi → buat SK → writeback Pegawai → lampiran) menjadi tanggung jawab `*CommandService` masing-masing aggregate. Kebutuhan "buat SK dari aggregate lain" diekspos sebagai **method publik `RiwayatSkCommandService`** yang dipanggil sibling CommandService.

- **Orkestrasi writeback — method-per-operasi, SK CommandService pegang writeback, exception tak ditelan**: bekas 10 method `GenericSkService` dipertahankan **1:1 sebagai method publik `RiwayatSkCommandService`** (nama dirapikan). **Exception tak ditelan**: legacy `try/catch → SavedStatus.FAILED` (SK tersimpan walau writeback gagal) DIHAPUS; method internal **melempar** exception, hanya method `save()` publik entry-point yang `@Transactional` + membungkus hasil jadi `SavedStatus` (ADR-0021).

- **Struktur package**: `dto/<modul>/<aggregate>/`, `services/<modul>/<aggregate>/`, `mapper/<modul>/<aggregate>/`; `repositories/<modul>/{jpa,jooq}/` (split teknologi, BUKAN per-aggregate); `controllers/<modul>/` (flat).

- **Jalur tulis tetap JPA, jalur baca pakai JOOQ**: method tulis (findById/exists/findAll(Specification)) tinggal di repo `jpa/`. Jalur baca: Specification jalur Query digantikan `where`-JOOQ + `SortParam.resolve`.

- **Magic number `{1L,2L,3L,25L}` → `PegawaiProperties`**: bean `PegawaiProperties` (`app.pegawai`) sudah ada dengan dua set — `excludedJabatanIds` & `excludedGolonganStatuses`. `RiwayatSkCommandService` baru inject `PegawaiProperties` dan ikut pola persis. Buang magic array + dependency `org.apache.commons.lang3.ArrayUtils`.

- **Validasi jalur tulis SK + perbaikan bug legacy**: (1) **bug TMT banding-diri-sendiri** — legacy `tmtBerlaku.isBefore(tmtBerlaku)` (selalu `false`) **diperbaiki** jadi `tmtBerlaku.isBefore(tanggalSk)`. (2) **cek duplikat** dipertahankan saat create, tapi jalur update pakai spec yang **mengecualikan `id` sendiri**.

- **Lookup master jalur tulis** ([ADR-0022](../adr/0022-label-snapshot-riwayat-findbyid.md)): buang `DetailFromList.findAll`-lalu-cari. Dua jalur: **FK murni → `getReferenceById`** (nol SELECT, patuh ADR-0008); **Snapshot label → `findById`** (entitas wajib ter-hidrasi, berlaku untuk Mutasi & Terminasi yang menyalin label master ke kolom denormalisasi).

- **Penempatan mapper baca — dua pola**: **Pola A (flat/sederhana)**: `private toQuery(Record)` di dalam QueryRepository JOOQ. **Pola B (nested/berat)**: kelas statik `final` terpisah `*Mapper.map(Record, Result...)` di `mapper/<modul>/<aggregate>/`. Semua mapper = **kelas statik `final` + private ctor, BUKAN `@Component`**. Pembagian: `LampiranSk`/`RiwayatKontrak`/`RiwayatSp`/`RiwayatSk` → Pola A; `RiwayatMutasi`/`RiwayatTerminasi` → Pola B.

- **Laporan kepegawaian DI LUAR scope rewrite**: `controllers/laporan/kepegawaian/` (8 controller — proxy `RestClient` ke service laporan eksternal via `LaporanKepegawaianService`) dibiarkan apa adanya.

- **Pemecahan Lampiran SK — ikut template `LampiranProfil` persis**: `LampiranSkQueryService` + `LampiranSkCommandService`. Baca pakai **Pola A** (`toQuery` private di `LampiranSkQueryRepository`). `deleteByRefId(Long refId)` dipertahankan TANPA tambah `EJenisSk`.

- **Saga Terminasi — orkestrator tunggal, fan-out 4 tulis**: `RiwayatTerminasiCommandService.save` jadi orkestrator `@Transactional` tunggal; tiap tulis lintas-aggregate dipanggil lewat **method publik CommandService pemiliknya**: `RiwayatSkCommandService.createForTerminasi`, `RiwayatMutasiCommandService.createFromTerminasi`, `RiwayatKontrakCommandService.createForTerminasi` (cabang KONTRAK). Exception tak ditelan (ADR-0021).

- **Pembersihan DTO mati (master/profil/pegawai)**: hapus **hanya** bila blast-radius kosong (verifikasi via `gitnexus_impact(direction:"upstream")` + grep). Jalur tulis dipertahankan. Eksekusi di **wave paling akhir** setelah Command/Query kepegawaian beres.

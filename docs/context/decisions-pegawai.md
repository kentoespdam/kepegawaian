# Context — Keputusan Rewrite: Modul Pegawai & Kepegawaian

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan rewrite modul `pegawai/` atau `kepegawaian/` (SK, Mutasi, Kontrak, Terminasi, SP, Lampiran SK).

---

## Keputusan Rewrite Sisi-Tulis Pegawai

- **Struktur Command**: satu `PegawaiCommandService` `@Transactional` dengan helper privat per cabang `Status Pegawai` (capeg / tetap-honorer / kontrak / non-pegawai). BUKAN dipecah ke kelas Command/Step terpisah — mengikuti gaya CommandService tunggal modul master & profil; saga ini jarang berubah, lapisan abstraksi tambahan tak sepadan. `authService.createUser` + pembuatan SK dipanggil berurutan dalam transaksi yang sama.

- **SortParam**: pakai implementasi nyata `final class SortParam.resolve(sortBy, sortDir, Map<String,Field<?>> allowedSorts, Field<?> defaultColumn)` (di `dto/commons`), BUKAN bentuk `record`+`Map<String,String>` yang masih tertulis di guide (guide basi, perlu dikoreksi terpisah). `sortBy` tak dikenal/blank → `defaultColumn` (default kolom ID), tanpa error; hanya `"asc"` eksplisit (case-insensitive) yang ascending, selain itu descending.

- **Request baca `/list` TIDAK extends `PagedRequest`** (refactor 2026-07-31): endpoint `GET /pegawai/list` memakai `PegawaiListRequest` = `{search, statusKerja}` saja — tanpa `page`/`size`/`sortBy`/`sortDirection`. Sort di-hardcode di `PegawaiQueryRepository.findAll(PegawaiListRequest)` → `.orderBy(BIODATA.NAMA.asc())`. Endpoint index (paged) tetap `PegawaiRequest extends PagedRequest`. Pola sama diterapkan ke `/list` modul lain (GajiProfil, GajiParameterSetting, GajiPendapatanNonPajak, CutiJenis, RiwayatSk) — masing-masing pakai `<Agg>ListRequest` filter-only; pengecualian **Biodata**: `listQuery` tidak memakai filter apa pun → `list()` jadi **no-arg** tanpa DTO. Lihat `docs/profil-cqrs-implementation-patterns.md` §1b.

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

- **Nomor SK bukan identitas unik Riwayat SK** (grilling 2026-08-12, [ADR-0034](../adr/0034-nomor-sk-bukan-identitas-unik-riwayat-sk.md)): nomor SK boleh terpakai ulang antar baris (kasus PLT berakhir → kembali ke jabatan semula). Tiga cek duplikat (`RiwayatSk`, `RiwayatMutasi`, `RiwayatTerminasi`) diseragamkan jadi **guard anti-duplikat eksak** `(pegawai, nomorSk, [jenisSk,] tanggalSk)`; `RiwayatKontrak` mendapat FK `riwayat_sk_id` (pola `RiwayatMutasi`) dan `delete()` kontrak memakai FK, bukan cocokkan nomor SK. PLT tidak dimodelkan sebagai entitas.

- **Lookup master jalur tulis** ([ADR-0022](../adr/0022-label-snapshot-riwayat-findbyid.md)): buang `DetailFromList.findAll`-lalu-cari. Dua jalur: **FK murni → `getReferenceById`** (nol SELECT, patuh ADR-0008); **Snapshot label → `findById`** (entitas wajib ter-hidrasi, berlaku untuk Mutasi & Terminasi yang menyalin label master ke kolom denormalisasi).

- **Penempatan mapper baca — dua pola**: **Pola A (flat/sederhana)**: `private toQuery(Record)` di dalam QueryRepository JOOQ. **Pola B (nested/berat)**: kelas statik `final` terpisah `*Mapper.map(Record, Result...)` di `mapper/<modul>/<aggregate>/`. Semua mapper = **kelas statik `final` + private ctor, BUKAN `@Component`**. Pembagian: `LampiranSk`/`RiwayatKontrak`/`RiwayatSp`/`RiwayatSk` → Pola A; `RiwayatMutasi`/`RiwayatTerminasi` → Pola B.

- **Laporan kepegawaian DI LUAR scope rewrite**: `controllers/laporan/kepegawaian/` (8 controller — proxy `RestClient` ke service laporan eksternal via `LaporanKepegawaianService`) dibiarkan apa adanya.

- **Pemecahan Lampiran SK — ikut template `LampiranProfil` persis**: `LampiranSkQueryService` + `LampiranSkCommandService`. Baca pakai **Pola A** (`toQuery` private di `LampiranSkQueryRepository`). `deleteByRefId(Long refId)` dipertahankan TANPA tambah `EJenisSk`.

- **Saga Terminasi — orkestrator tunggal, fan-out 4 tulis**: `RiwayatTerminasiCommandService.save` jadi orkestrator `@Transactional` tunggal; tiap tulis lintas-aggregate dipanggil lewat **method publik CommandService pemiliknya**: `RiwayatSkCommandService.createForTerminasi`, `RiwayatMutasiCommandService.createFromTerminasi`, `RiwayatKontrakCommandService.createForTerminasi` (cabang KONTRAK). Exception tak ditelan (ADR-0021).

- **Read mapper profil salah paket — relokasi ke `mapper/`** (grilling 2026-07-01, epic `kepegawaian-3kj`): 10 `*RowMapper`/`*MultisetMapper` profil terlanjur di `repositories/profil/jooq/` (kesalahan agent sebelumnya). Dipindah ke `mapper/profil/<aggregate>/*JooqMapper.java` sebagai **Pola B** (kelas `final`, private ctor, `implements RecordMapper`, BUKAN `@Component`) — konsisten dengan modul `cuti`. Multiset mapper (`Pendidikan`/`KartuIdentitas`) adalah nested child pada **detail view** `BiodataDetailQuery`, jadi **tidak** melanggar ADR-0001 mini-projection (mini-projection hanya mengikat paged-list root). Wave 1: `kepegawaian-iki/g5n/fvi/5ny/rpq/ure/4lt/lep`.

- **File-download keluar dari Command layer → `*QueryService`** (grilling 2026-07-01): `getFileLampiranById`/`findFotoProfil` yang cuma delegasi `lampiranProfilQueryService` dipindah dari `*CommandService` ke `*QueryService` aggregat, controller diarahkan ke QueryService — read tidak lagi di write-layer. Sekaligus split file `>120` baris (Pendidikan 164, Keluarga 151, Biodata 148, KartuIdentitas 126) → CRUD vs Lampiran command service. **Entity data-holder dikecualikan** dari batas 120 baris. Wave 2: `kepegawaian-z6c/yp6/0bv/44t/xfh`.

- **`ProfileUpdateService` interface single-impl + read masih JPA Specification**: interface (20 baris, 1 impl) dibuang per decisions-cuti §11 — `ProfileUpdateServiceImpl` di-rename jadi `ProfileUpdateService`, 6 injector tak berubah teksnya (`kepegawaian-mfq`). Read-side `ProfileUpdate` masih pakai `repository.findAll(spec, pageable)` — **belum** migrasi JOOQ; dijadwalkan terpisah (`kepegawaian-996`, blocked oleh `mfq`).

- **Pembersihan DTO mati (master/profil/pegawai)**: hapus **hanya** bila blast-radius kosong (verifikasi via `gitnexus_impact(direction:"upstream")` + grep). Jalur tulis dipertahankan. Eksekusi di **wave paling akhir** setelah Command/Query kepegawaian beres.

---

## Interface Cleanup Lintas-Modul

- **AuthService, RevInfoService, UserService — interface single-impl dibuang** (grilling 2026-07-08, sesi improve-codebase-architecture): ketiga service ini masih punya interface + Impl (violasi ADR-0007). 
  - `AuthService` + `AuthServiceImpl` → collapse jadi `AuthService` (konkret `@Service`), 3 consumer tak berubah
  - `RevInfoService` + `RevInfoServiceImpl` → collapse jadi `RevInfoService` (konkret `@Service`), 3 consumer tak berubah
  - `UserService` + `UserServiceImpl` → collapse jadi `UserService` (konkret `@Service`), 1 consumer tak berubah
  - `ProfileUpdateApprovalService` **dipertahankan** sebagai interface — punya 2 implementasi legitimate (`Pendidikan`, `Keluarga`).

- **Appwrite REST client extraction — `AppwriteClient` typed adapter** (grilling 2026-07-08): AuthService dan JwtTokenService sebelumnya memanggil REST API Appwrite langsung via RestClient dengan duplikasi header dan URL concatenation di 6 titik berbeda. Diekstrak ke `AppwriteClient` (`config/appwrite/AppwriteClient.java`) + `AppwriteProperties` (`@ConfigurationProperties`). Detail: [ADR-0029](../adr/0029-appwriteclient-typed-adapter.md).

- **CustomResult.optional(Optional<T>) — type-safe handler untuk Optional return** (grilling 2026-07-08): `CustomResult.any(T data)` sebelumnya punya `instanceof Optional` hack yang tidak type-safe. Ditambahkan method `optional(Optional<T>)` yang clean tanpa `instanceof`, langsung `orElse(null)` → SingleResult otomatis return 404 untuk empty. `any()` diperbaiki pakai Java 21 pattern matching. Issue follow-up: [kepegawaian-vf5](../beads/issues/kepegawaian-vf5) untuk migrasi caller controller dari `.any()` ke `.optional()`.

---

## Entity Mapping Convention: @Column(name) Revisi

**Keputusan (2026-07-23, issue `kepegawaian-kb7`)**: Field entity yang nama Java-nya sudah mengikuti camelCase → snake_case via Spring Boot default `CamelCaseToUnderscoresNamingStrategy` **tidak perlu** `@Column(name = "...")` eksplisit. Hapus `name` dari `@Column` kalau satu-satunya atribut adalah `name`.

**Tetap pakai `@Column`** kalau ada metadata lain:
- `nullable = false`
- `unique = true`
- `columnDefinition = "..."`

**Entity yang sudah dibersihkan**: 18 entity files (~30 `@Column` DDL-only dihapus).
- **Pegawai**: `Pegawai.java` (26 total — 22 name-only + 4 metadata-only)
- **Penggajian**: `GajiBatchRoot.java`, `GajiBatchRootErrorLogs.java`, `GajiPhdp.java`
- **Master**: `Sanksi.java`, `HariLibur.java`
- **Kepegawaian**: `LampiranSk.java`, `RiwayatSk.java`, `RiwayatSp.java`
- **Profil**: `KartuIdentitas.java`, `Keahlian.java`, `ProfilKeluarga.java`, `LampiranProfil.java`, `Pelatihan.java`, `PengalamanKerja.java`, `Pendidikan.java`
- **Cuti**: `CutiApproval.java`, `CutiPegawai.java`

**Pengecualian**: `@Column` dengan `updatable=false` (audit fields) tetap dipertahankan — ini runtime behavior JPA, bukan DDL.

Alasan: boilerplate `@Column(name)` risk inconsistency (seperti `jmlTanggungan` vs `jml_tanggungan` sebelumnya) dan zero value added karena naming strategy sudah handle mapping otomatis.

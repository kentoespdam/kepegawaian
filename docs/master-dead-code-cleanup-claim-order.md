# Dead Code & Unused Import Cleanup — Master Module

> **Tujuan:** Identifikasi dan hapus dead code (DTO, import, file) di modul master yang sudah tidak dipakai pasca-refactoring ke Java record + JOOQ query pattern.
>
> **Prioritas:** P2 (membersihkan technical debt sebelum refactor lanjutan)
>
> **Dibuat:** 2026-07-08
> **Oleh:** Codebuff (Buffy) — Analisis otomatis berbasis grep + code search

---

## Ringkasan Temuan

### 🟢 Dead DTO (Confirmed — Tidak Dipakai Sama Sekali)

| DTO | File | Alasan |
|-----|------|--------|
| `OrganisasiResponse` | `dto/master/organisasi/OrganisasiResponse.java` | Hanya self-reference. Semua read path pakai `OrganisasiQuery` (record). |
| `SanksiResponse` | `dto/master/sanksi/SanksiResponse.java` | Hanya self-reference. Semua read path pakai `SanksiQuery` (record). |
| `JenisKontrakResponse` | `dto/master/jenisKontrak/JenisKontrakResponse.java` | Tidak dipakai. Controller `JenisKontrakController` return `EnumOption`. |
| `JenisMutasiResponse` | `dto/master/jenisMutasi/JenisMutasiResponse.java` | Tidak dipakai. Controller `JenisMutasiController` return `EnumOption`. |
| `JenisSkResponse` | `dto/master/jenisSk/JenisSkResponse.java` | Tidak dipakai. Controller `JenisSkController` return `EnumOption`. |
| `StatusKerjaResponse` | `dto/master/statusKerja/StatusKerjaResponse.java` | Tidak dipakai. Controller `StatusKerjaController` return `EnumOption`. |

### 🟢 Dead Selects File

| File | Alasan |
|------|--------|
| `JenisSpSelects.java` | Zero external reference — tidak ada `JenisSpJooqMapper`, `JenisSpQueryRepository` pakai inline field langsung |

### 🟢 Dead Field/Array di dalam Selects

| Field | File | Alasan |
|-------|------|--------|
| `GRADE_QUERY_COLUMNS` | `GradeSelects.java:17` | Didefinisikan tapi **tidak pernah dipakai** — `GradeQueryRepository` memakai field individu (`GradeSelects.ID`, dll) secara inline |
| `PROFESI_COLUMNS` | `ProfesiSelects.java:14` | Didefinisikan tapi **tidak pernah dipakai** — hanya `PROFESI_QUERY_COLUMNS` dan `PROFESI_DETAIL_COLUMNS` yang digunakan |

### ✅ Alive Selects (lengkap)

| File | Field/Array | Status | Dipakai Oleh |
|------|-------------|--------|-------------|
| `GradeSelects.java` | `ID`, `GRADE_`, `TUKIN`, `LEVEL_ID`, `LEVEL_NAMA` | ✅ | `GradeQueryRepository`, `GradeJooqMapper` |
| `JabatanSelects.java` | `ID`, `KODE`, `NAMA`, `PARENT_ID`, `PARENT_KODE`, `PARENT_NAMA`, `JABATAN_COLUMNS`, `parentColumns()` | ✅ | `JabatanQueryRepository`, `JabatanJooqMapper` |
| `OrganisasiSelects.java` | `ID`, `KODE`, `LEVEL_ORG`, `NAMA`, `SHORT_NAME`, `CATEGORY`, `PARENT_ID`.., `ORGANISASI_COLUMNS`, `parentColumns()` | ✅ | `OrganisasiQueryRepository`, `OrganisasiJooqMapper` |
| `SanksiSelects.java` | Semua field + `SANKSI_QUERY_COLUMNS` | ✅ | `SanksiQueryRepository` (4x), `SanksiJooqMapper` |
| `ProfesiSelects.java` | `PROFESI_QUERY_COLUMNS`, `PROFESI_DETAIL_COLUMNS` | ✅ | `ProfesiQueryRepository`, `ProfesiDetailQuery` |
| `SharedSelects.java` | Semua field (`ORG_ID`, `JABATAN_ID`, `LEVEL_ID`, `GRADE_ID` dll) | ✅ | `JabatanJooqMapper`, `SharedMappers`, `ProfesiJooqMapper`, `JabatanQueryRepository`, `ProfesiSelects` |

### 🟡 Unused Imports

| File | Unused Import | Detail |
|------|--------------|--------|
| `JenisKeahlianController.java` | `ErrorResult` (line 17) | Sisa refactoring controller — `Errors errors` parameter sudah dihapus |
| `JenisKeahlianController.java` | `Errors` (line 24) | Sisa refactoring — import `org.springframework.validation.Errors` tidak lagi dipakai |

### 🔵 Cross-Module DTO (Masih Dipakai — JANGAN Dihapus)

DTO ini masih dipakai oleh modul lain (pegawai, profil, kepegawaian). Hanya bisa dihapus setelah modul pemakai juga migrasi ke Query record:

| DTO | Dipakai Oleh |
|-----|-------------|
| `AlasanBerhentiResponse` | `RiwayatTerminasiResponse`, `RiwayatTerminasiQuery` |
| `GolonganResponse` | `PegawaiRecordMapper`, `GajiPotonganTkkJooqMapper`, `GajiTunjanganJooqMapper`, `PegawaiQueryRepository`, dll |
| `GradeResponse` | `PegawaiQueryRepository`, `PegawaiResponseDetail` |
| `RumahDinasResponse` | `PegawaiQueryRepository`, `PegawaiResponseDetail` |
| `JenisKeahlianResponse` | `KeahlianJooqMapper`, `KeahlianQuery`, `KeahlianResponse` |
| `JenisKitasResponse` | `PegawaiQueryRepository`, `KartuIdentitasMiniResponse` |
| `StatusPegawaiResponse` | `StatusPegawaiQueryService`, `StatusPegawaiController` |
| `JenjangPendidikanResponse` | 20+ references across profil, pegawai modules |
| `AlasanBerhentiResponse` | `RiwayatTerminasiResponse`, `RiwayatTerminasiQuery` |

### 🔵 Old *Mapper.java vs *JooqMapper.java (BUKAN Dead Code)

Ditemukan 6 direktori mapper dengan dual mapper (old `*Mapper.java` dan new `*JooqMapper.java`). **Ini BUKAN dead code** — old mapper masih dipakai oleh `CommandService` untuk operasi write (create/update entity), sementara `*JooqMapper` dipakai untuk read (query) path.

| Direktori | Old Mapper | New JooqMapper |
|-----------|-----------|----------------|
| `grade/` | `GradeMapper.java` | `GradeJooqMapper.java` |
| `hariLibur/` | `HariLiburMapper.java` | `HariLiburJooqMapper.java` |
| `jabatan/` | `JabatanMapper.java` | `JabatanJooqMapper.java` |
| `organisasi/` | `OrganisasiMapper.java` | `OrganisasiJooqMapper.java` |
| `profesi/` | `ProfesiMapper.java` | `ProfesiJooqMapper.java` |
| `sanksi/` | `SanksiMapper.java` | `SanksiJooqMapper.java` |

### 🔵 Old Response DTO (Masih Dipakai Sebagai Nested Object)

DTO di bawah masih dipakai untuk nested object/mapping — BUKAN dead code:

- `OrganisasiMiniResponse`, `JabatanMiniResponse`, `GradeMiniResponse`, `JenisSpMiniResponse`, `SanksiMiniResponse` — semua masih dipakai oleh cross-module mapper & repository.

---

## Claim Order & Checklist

### [D3] Verifikasi Cross-Module — kepegawaian-5o6
**Dependency:** `kepegawaian-6bu.1` (foundation)
**Blokir:** D1

Sebelum hapus DTO, verifikasi dampak cross-module dengan bantuan gitnexus_impact:

- [x] `gitnexus_impact({target:"OrganisasiResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"SanksiResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"JenisKontrakResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"JenisMutasiResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"JenisSkResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"StatusKerjaResponse"})` — pastikan zero reference ✅
- [x] `gitnexus_impact({target:"JenisKeahlianController"})` — pastikan unused import aman dihapus ✅
- [x] Buat daftar final DTO yang bisa dihapus vs yang harus ditunda ✅
- [x] `./gradlew compileJava` — pastikan tidak ada reference broken ✅

---

### [D1] Hapus Dead DTO — kepegawaian-0ox
**Dependency:** D3 (kepegawaian-5o6)

Hapus 6 DTO yang sudah diverifikasi dead:

- [x] Hapus `dto/master/organisasi/OrganisasiResponse.java` ✅
- [x] Hapus `dto/master/sanksi/SanksiResponse.java` ✅
- [x] Hapus `dto/master/jenisKontrak/JenisKontrakResponse.java` ✅
- [x] Hapus `dto/master/jenisMutasi/JenisMutasiResponse.java` ✅
- [x] Hapus `dto/master/jenisSk/JenisSkResponse.java` ✅
- [x] Hapus `dto/master/statusKerja/StatusKerjaResponse.java` ✅
- [x] `./gradlew compileJava` — BUILD SUCCESSFUL ✅
- [x] `gitnexus_detect_changes()` — hanya 7 file dihapus ✅

---

### [D2] Hapus Unused Import — kepegawaian-k29
**Dependency:** `kepegawaian-6bu.1` (foundation — guard ArchUnit sudah jalan)

Hapus 2 unused import di `JenisKeahlianController.java`:

- [x] Hapus `import id.perumdamts.kepegawaian.dto.commons.ErrorResult;` ✅
- [x] Hapus `import org.springframework.validation.Errors;` ✅
- [x] `./gradlew compileJava` — BUILD SUCCESSFUL ✅
- [x] `gitnexus_detect_changes()` — hanya 2 baris dihapus ✅
- [x] Format ulang import sesuai konvensi (import grouping) ✅

---

### [D4] Final Cleanup & Build — kepegawaian-aak
**Dependency:** D1 + D2

- [x] `./gradlew clean compileJava` — zero errors, zero warnings ✅
- [x] `./gradlew test --tests "id.perumdamts.kepegawaian.ArchUnitTest"` — PASS ✅
- [x] `./gradlew compileTestJava` — BUILD SUCCESSFUL ✅
- [ ] `gitnexus_detect_changes()` — verify hanya file target yang berubah
- [ ] Review `TestController.java` — apakah masih dipakai? (dev-only endpoint)
- [ ] Review `SetupMasterController.java` — apakah masih relevan?

---

### [D5] Cleanup Selects — Hapus Dead Field/Array + File — kepegawaian-aak
**Dependency:** D4

Hapus field/array mati di Selects — perhatikan akses package-private (satu paket dengan QueryRepository):

#### Hapus `JenisSpSelects.java` (seluruh file)
- [x] `gitnexus_impact({target:"JenisSpSelects"})` — pastikan zero reference ✅
- [x] Hapus file `repositories/master/jooq/JenisSpSelects.java` ✅
- [x] `./gradlew compileJava` — BUILD SUCCESSFUL ✅

#### Hapus `GRADE_QUERY_COLUMNS` dari `GradeSelects.java`
- [x] Hapus deklarasi array + isinya dari `GradeSelects.java` ✅
- [x] Verifikasi `GradeQueryRepository` tetap pakai field individu ✅
- [x] `./gradlew compileJava` — BUILD SUCCESSFUL ✅

#### Hapus `PROFESI_COLUMNS` dari `ProfesiSelects.java`
- [x] Hapus deklarasi array + isinya dari `ProfesiSelects.java` ✅
- [x] Hapus juga orphaned `SELF_JABATAN_ID`, `SELF_LEVEL_ID`, `SELF_GRADE_ID` ✅
- [x] Verifikasi `ProfesiQueryRepository` pakai `PROFESI_QUERY_COLUMNS` ✅
- [x] Verifikasi `ProfesiDetailQuery` pakai `PROFESI_DETAIL_COLUMNS` ✅
- [x] `./gradlew compileJava` — BUILD SUCCESSFUL ✅

#### Final verification
- [x] `./gradlew clean compileJava` — BUILD SUCCESSFUL ✅
- [x] `./gradlew compileTestJava` — BUILD SUCCESSFUL ✅
- [ ] `gitnexus_detect_changes()` — verify hanya target yang berubah

---

## Metodologi Analisis

### Tools yang Dipakai
1. **ripgrep (`grep -rn`)** — untuk mencari reference antar file
2. **`gitnexus_impact`** — untuk blast radius analysis sebelum hapus symbol
3. **`gitnexus_detect_changes`** — untuk verifikasi cakupan perubahan

### Kriteria "Dead Code"
- **DTO**: File yang hanya dirujuk oleh dirinya sendiri (zero external reference)
- **Import**: Import yang tidak muncul di body class mana pun
- **Method**: Method yang tidak dipanggil oleh kode produksi manapun

### Peringatan
- JANGAN hapus `*Mapper.java` lama — masih dipakai CommandService untuk write path (CQRS: write via JPA entity mapper, read via JOOQ mapper)
- JANGAN hapus `XxxMiniResponse` atau `XxxListResponse` — masih dipakai sebagai nested/shared DTO lintas modul
- WAJIB `gitnexus_impact` SEBELUM hapus symbol apa pun
- WAJIB `./gradlew compileJava` SETELAH setiap batch penghapusan
- `GRADE_QUERY_COLUMNS` dan `PROFESI_COLUMNS` bersifat **package-private** — hapus aman selama tidak ada referensi dari kelas lain di paket yang sama (sudah diverifikasi: tidak ada)

---

## Reference

| Dokumen | Isi |
|---------|-----|
| `docs/master-query-optimization-pattern.md` | Pattern JOOQ query optimization |
| `docs/master-record-refactor-claim-order.md` | Refactoring claim order untuk Java record |
| `CODING_RULES.md` | Aturan main project |
| `.beads/issues.jsonl` | Issue tracker (beads) |

## Issue Tracker

| ID | Title | Status | Priority |
|----|-------|--------|----------|
| `kepegawaian-5o6` | D3: Verifikasi cross-module — stale Response DTO | `closed` | P2 |
| `kepegawaian-0ox` | D1: Hapus dead DTO (Organisasi, Sanksi, JenisKontrak, JenisMutasi, JenisSk, StatusKerja) | `closed` | P2 |
| `kepegawaian-k29` | D2: Hapus unused import — ErrorResult + Errors di JenisKeahlianController | `closed` | P2 |
| `kepegawaian-aak` | D4 + D5: Final cleanup — Selects field death + verifikasi build | `closed` | P2 |

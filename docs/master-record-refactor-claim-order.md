# Master Record Refactor — Claim Order & Checklist

> Implementasi Java record + typed RecordMapper + lean column select untuk semua modul master.
>
> Pattern rujukan: `docs/master-query-optimization-pattern.md`
> Exemplar: Modul **Profesi** (commit `b732295`)
> CODING_RULES: `CODING_RULES.md`
> AGENTS: `AGENTS.md`

---

## Status Real (per 2026-07-08)

Banyak modul sudah diimplementasi sebagian/seluruhnya. Dokumen ini mencerminkan **status real** kode di working tree.

| Modul | DTO Record | Selects | Mapper | Repository | Controller Typed | Hapus Errors |
|-------|:----------:|:-------:|:------:|:----------:|:----------------:|:------------:|
| **E0 Foundation** | — | ✅ | ✅ | — | — | — |
| **E1 Flat Batch 1** | ✅ | — | — | ✅ | ✅ | ✅ |
| **E2 Flat Batch 2** | ✅ | — | — | ✅ | ✅ | ✅ |
| **E3 JenjangPendidikan** | ✅ | — | — | ✅ | ✅ | ✅ |
| **E4 JenisSp** | ✅ | ✅ | ✅* | ✅ | ✅ | ✅ |
| **E4 Sanksi** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **E5 Grade** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **E6 Organisasi** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **E7 Jabatan** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **E8 Enum Read-Only** | ✅ | — | — | — | ✅ | ✅ |
| **Level** | ✅ | — | — | ✅ | ✅ | ✅ |

\* JenisSpQueryRepository pakai `fetchInto`, bukan method reference — fine untuk flat entity.

---

## Dependency Graph

```
E0 Foundation ───────────── kepegawaian-hkq [SUDAH]
├── blocks E1 Flat batch 1  kepegawaian-5k9 [CONTROLLER SUDAH, DTO/REPO PERLU DICEK]
├── blocks E2 Flat batch 2  kepegawaian-1xy [CONTROLLER SUDAH, DTO/REPO PERLU DICEK]
├── blocks E4 JenisSp+Sanksi kepegawaian-sr1 [BACKEND SUDAH, CONTROLLER BELUM]
├── blocks E5 Grade          kepegawaian-oqe   [BACKEND SUDAH, CONTROLLER BELUM]
├── blocks E6 Organisasi     kepegawaian-rfc   [BACKEND SUDAH, CONTROLLER BELUM]
├── blocks E7 Jabatan        kepegawaian-bjk   [SUDAH SELESAI]
│   └── blocks from E6

E3 JenjangPendidikan ────── kepegawaian-1ws [DTO SUDAH, CONTROLLER BELUM]
E8 Enum read-only ───────── kepegawaian-78r  [SUDAH SELESAI]

⚠️ GAP: LevelController (tidak ada issue, tidak disebut)
```

**Claim order:** Kerjakan yang belum selesai. Prioritas:
1. Controller typed + hapus Errors → Grade, Organisasi, JenisSp, Sanksi, JenjangPendidikan, **Level**
2. Verifikasi E1/E2 DTO/Repository — apakah sudah record dan lean query?
3. Column set arrays di Selects yang belum punya
4. Unit test untuk RecordMapper baru
5. Konversi mini response DTO ke record (opsional)

---

## E0: Foundation (kepegawaian-hkq) — ✅ SELESAI

**Tujuan:** Extract shared infrastructure agar setiap modul tidak perlu bikin ulang.

### Status

| Item | Status | File |
|------|--------|------|
| `SharedSelects.java` | ✅ **SUDAH** | `repositories/master/jooq/SharedSelects.java` |
| `SharedMappers.java` | ✅ **SUDAH** | `mapper/master/SharedMappers.java` |
| ProfesiSelects update | ✅ **SUDAH** | Pakai SharedSelects |

**Catatan:** SharedMappers masih menggunakan setter (`o.setId()`) untuk `OrganisasiMiniResponse`, `JabatanMiniResponse`, `GradeMiniResponse` — karena DTO tersebut masih `@Data` class (bukan record). Jika ingin fully immutable, perlu dikonversi ke record nanti.

### Checklist verifikasi

- [x] `SharedSelects.java` — public final class, private constructor
- [x] Field untuk ORGANISASI, JABATAN, LEVEL, GRADE
- [x] `SharedMappers.java` — builder: buildOrganisasi, buildJabatan, buildGrade, buildLevel
- [x] ProfesiJooqMapper import dari shared
- [ ] ~~`./gradlew compileJava` sukses~~ ✅ sudah
- [ ] ~~`./gradlew test` sukses~~ ✅ sudah

---

## E1: Flat Batch 1 (kepegawaian-5k9) — ⚠️ PERLU VERIFIKASI

**Modul:** Golongan, JenisKeahlian, JenisPelatihan, JenisKitas

**Karakteristik:** Flat entity, tanpa JOIN, tanpa nested object.

### Status saat ini

Controller untuk keempat modul SUDAH typed (dari diff):
```java
// Contoh JenisPelatihanController
public ResponseEntity<PageResult<Page<JenisPelatihanQuery>>> index(...)
public ResponseEntity<ListResult<JenisPelatihanListResponse>> list()
public ResponseEntity<SingleResult<JenisPelatihanQuery>> findById(...)
```

**PERLU DIVERIFIKASI:** Apakah DTO (`XxxQuery`, `XxxListResponse`) dan repository (`XxxQueryRepository`) sudah sesuai pattern? Cek:
- `XxxQuery` → record (bukan @Data)?
- `XxxListResponse` → record?
- Repository → `fetchInto(XxxQuery.class)` untuk page?
- Repository → kolom select minimal (tidak over-fetch)?
- Controller → hapus `Errors errors`?

### Checklist (per modul)

- [ ] **DTO:** `@Data` → `public record XxxQuery(Long id, String nama, ...)` — VERIFIKASI
- [ ] **List DTO:** `XxxListResponse(Long id, String nama)` record — VERIFIKASI
- [ ] **Query:** Hapus `record.intoMap()` — ganti `fetchInto(XxxQuery.class)` — VERIFIKASI
- [ ] **Service:** Update return type — VERIFIKASI
- [ ] **Controller:** Hapus parameter `Errors errors` — VERIFIKASI
- [ ] `./gradlew compileJava` sukses
- [ ] `./gradlew test --tests '*xxx*'` sukses

### Detail per modul

| Modul | Query Fields | Nested? | List endpoint? | Status |
|-------|-------------|---------|----------------|--------|
| Golongan | id, golongan, pangkat | ❌ | ya | Controller typed ✅ |
| JenisKeahlian | id, nama | ❌ | ya | Controller typed ✅ |
| JenisPelatihan | id, nama | ❌ | ya | Controller typed ✅ |
| JenisKitas | id, nama | ❌ | ya | Controller typed ✅ |

---

## E2: Flat Batch 2 (kepegawaian-1xy) — ⚠️ PERLU VERIFIKASI

**Modul:** AlasanBerhenti, RumahDinas, HariLibur

**Karakteristik:** Flat entity, tanpa JOIN.

### Status saat ini

Controller sudah typed (dari diff). Sama seperti E1 — perlu verifikasi DTO dan repository.

### Detail per modul

| Modul | Query Fields | List endpoint? |
|-------|-------------|----------------|
| AlasanBerhenti | id, nama, notes | ya |
| RumahDinas | id, nama, nilai | ya |
| HariLibur | id, tanggal, jenisLibur, notes | ya |

### Checklist (sama seperti E1)

- [ ] **DTO:** record — VERIFIKASI
- [ ] **List DTO:** record — VERIFIKASI
- [ ] **Query:** `fetchInto` — VERIFIKASI
- [ ] **Controller:** typed + hapus Errors — VERIFIKASI
- [ ] `./gradlew compileJava` sukses

---

## E3: JenjangPendidikan (kepegawaian-1ws) — DTO ✅, CONTROLLER ❌

**Modul:** JenjangPendidikan

**Catatan:** `JenjangPendidikanResponse` dipakai sebagai **nested object** di banyak modul (Biodata, ProfilKeluarga, dll).

### Status

| Item | Status |
|------|--------|
| `JenjangPendidikanResponse` → record | ✅ **SUDAH** |
| `from()` static methods | ✅ **SUDAH** |
| Controller typed | ❌ **BELUM** — masih `ResponseEntity<?>` |
| Hapus `Errors errors` | ❌ **BELUM** |

### Checklist

- [x] `JenjangPendidikanResponse` → `public record` (5 fields: id, nama, shortName, seq, isStatistik)
- [x] `from(JenjangPendidikan)` — static method di record
- [x] `from(List<JenjangPendidikan>)` — static method di record
- [ ] **Controller:** `ResponseEntity<PageResult<Page<JenjangPendidikanResponse>>>` typed
- [ ] **Controller:** Hapus `Errors errors` dari POST/PUT
- [ ] **WAJIB: gitnexus_impact + gitnexus_query** sebelum edit — cari seluruh pengguna `JenjangPendidikanResponse` di modul profil
- [ ] Update seluruh referensi `.setXxx()` → akses langsung `.xxx()`
- [ ] `./gradlew compileJava` sukses
- [ ] `./gradlew test` sukses

---

## E4: JenisSp + Sanksi (kepegawaian-sr1) — BACKEND ✅, CONTROLLER ❌

**Modul:** JenisSp, Sanksi

**Karakteristik:** Dua modul terpisah tapi relasi FK (Sanksi → JenisSp).

### Status

| Item | JenisSp | Sanksi |
|------|:-------:|:------:|
| Query → record | ✅ | ✅ |
| Selects | ✅ `JenisSpSelects` | ✅ `SanksiSelects` |
| Mapper | — (fetchInto) | ✅ `SanksiJooqMapper` |
| Repository | ✅ `JenisSpQueryRepository` | ✅ `SanksiQueryRepository` |
| Controller typed | ❌ | ❌ |
| Hapus Errors | ✅ | ✅ |

### Issues

1. **`SanksiResponse` masih `@Data`** — perlu dikonversi ke record (atau dihapus jika hanya dipakai dari JPA)
2. **`JenisSpMiniResponse` masih `@Data`** — perlu dikonversi ke record agar konsisten
3. **`mapToQuery`** di SanksiJooqMapper namanya tidak konsisten — pattern pakai `toQuery`

### Checklist

- [x] `JenisSpQuery` → record (flat ✅)
- [x] `JenisSpSelects.java` — public class ✅
- [x] `JenisSpListResponse` — record ✅
- [x] `SanksiQuery` → record, tanpa FK duplikat ✅
- [x] `SanksiSelects.java` — public class ✅
- [x] `SanksiJooqMapper` — typed `toQuery(Record)` ✅
- [ ] **Controller:** typed ResponseEntity
- [ ] **`SanksiResponse`** → record atau hapus (masih `@Data`)
- [ ] **`JenisSpMiniResponse`** → record (masih `@Data`)
- [ ] **Rename** `SanksiJooqMapper.mapToQuery` → `toQuery` (konsisten pattern)
- [ ] **Tambahkan column set arrays** di SanksiSelects (SANKSI_QUERY_COLUMNS dll) — lihat gap pattern
- [ ] `./gradlew compileJava` sukses

---

## E5: Grade (kepegawaian-oqe) — BACKEND ✅, CONTROLLER ❌

**Modul:** Grade

**Karakteristik:** Mempunyai relasi ke Level melalui `levelId`.

### Status

| Item | Status |
|------|--------|
| `GradeQuery` → record | ✅ `(Long id, Integer grade, Double tukin, LevelResponse level)` |
| Hapus `levelId` duplikat | ✅ |
| `GradeSelects.java` | ✅ |
| `GradeJooqMapper` | ✅ |
| `GradeQueryRepository` | ✅ method reference |
| `GradeListResponse` | ✅ `(Long id, Integer grade)` |
| Controller typed | ❌ **BELUM** |

### Issues

1. **GradeSelects tidak punya column set array** — melanggar pattern (harus ada `GRADE_QUERY_COLUMNS`)
2. **GradeController** masih `ResponseEntity<?>` — tinggal update return type

### Checklist

- [x] `GradeQuery` → record tanpa `levelId`
- [x] `GradeSelects.java` — public class
- [x] `GradeJooqMapper` — typed `toQuery(Record)`, pakai `SharedMappers.buildLevel()`
- [x] `GradeQueryRepository` — method reference
- [x] `GradeListResponse` — record
- [ ] **Controller:** typed ResponseEntity
- [ ] **Tambahkan column set array** `GRADE_QUERY_COLUMNS` di GradeSelects
- [ ] `./gradlew compileJava` sukses

---

## E6: Organisasi (kepegawaian-rfc) — BACKEND ✅, CONTROLLER ❌

**Modul:** Organisasi

**Karakteristik:** Tree entity (self-ref `parent_id`).

### Status

| Item | Status |
|------|--------|
| `OrganisasiQuery` → record | ✅ tanpa `parentId` |
| `OrganisasiSelects.java` | ✅ |
| `OrganisasiJooqMapper` | ✅ |
| `OrganisasiQueryRepository` | ✅ method reference |
| `OrganisasiListResponse` | ✅ `(Long id, String nama)` |
| Controller typed | ❌ **BELUM** |

### Issues

1. **OrganisasiSelects tidak punya column set array** — melanggar pattern
2. **Controller** masih `ResponseEntity<?>`

### Checklist

- [x] `OrganisasiQuery` → record tanpa `parentId`
- [x] `OrganisasiSelects.java` — public class
- [x] `OrganisasiJooqMapper` — typed `toQuery(Record)`
- [x] `OrganisasiQueryRepository` — method reference
- [x] `OrganisasiListResponse` — record
- [ ] **Controller:** typed ResponseEntity
- [ ] **Tambahkan column set array** `ORGANISASI_QUERY_COLUMNS` di OrganisasiSelects
- [ ] `./gradlew compileJava` sukses

---

## E7: Jabatan (kepegawaian-bjk) — ✅ SELESAI

**Modul:** Jabatan

**Karakteristik:** Tree entity + relasi ke Organisasi + Level. Modul paling kompleks (3 join).

### Status

| Item | Status |
|------|--------|
| `JabatanQuery` → record | ✅ tanpa `parentId`, `organisasiId`, `levelId` |
| `JabatanSelects.java` | ✅ |
| `JabatanJooqMapper` | ✅ pakai SharedSelects + SharedMappers |
| `JabatanQueryRepository` | ✅ |
| `JabatanListResponse` | ✅ |
| Controller typed | ✅ |

### Issues

1. **JabatanSelects tidak punya column set array** — melanggar pattern

### Checklist

- [x] `JabatanQuery` → record, hapus FK duplikat
- [x] `JabatanListResponse` — record
- [x] `JabatanSelects.java` — public class
- [x] `JabatanJooqMapper` — typed `toQuery(Record)`, build helpers
- [x] `JabatanQueryRepository` — method reference
- [x] Controller typed ResponseEntity
- [ ] **Tambahkan column set array** `JABATAN_QUERY_COLUMNS` di JabatanSelects
- [ ] `./gradlew compileJava` sukses

---

## E8: Enum Read-Only (kepegawaian-78r) — ✅ SELESAI

**Modul:** StatusKerja, StatusPegawai, JenisKontrak, JenisMutasi, JenisSk

**Karakteristik:** Tidak punya entity/tabel — hanya mapping `enum.values()` ke response.

### Status

| Item | Status |
|------|--------|
| `EnumOption` record | ✅ |
| `StatusPegawaiResponse` record | ✅ (dengan `urut`) |
| StatusKerjaController | ✅ typed |
| StatusPegawaiController | ✅ typed |
| JenisKontrakController | ✅ typed |
| JenisMutasiController | ✅ typed |
| JenisSkController | ✅ typed |

**Catatan:** File `StatusPegawaiOption.java` (sesuai nama di desain) tidak perlu dibuat — `StatusPegawaiResponse` sudah fulfil kebutuhan.

### Checklist

- [x] `EnumOption` record di `dto/commons/`
- [x] `StatusPegawaiResponse` record di `dto/master/statusPegawai/`
- [x] StatusKerjaController — `ListResult<EnumOption>`
- [x] StatusPegawaiController — `ListResult<StatusPegawaiResponse>`
- [x] JenisKontrakController — `ListResult<EnumOption>`
- [x] JenisMutasiController — `ListResult<EnumOption>`
- [x] JenisSkController — `ListResult<EnumOption>`
- [x] Controller typed ResponseEntity
- [x] `./gradlew compileJava` sukses

---

## ⚠️ GAP: LevelController (TIDAK ADA DI SCOPE)

**Modul:** Level

**Alasan perlu masuk scope:** Level adalah entity inti yang dijadikan nested object di Grade (E5) dan Jabatan (E7). Controller-nya masih:

| Item | Status |
|------|--------|
| `ResponseEntity<?>` | ❌ **masih** |
| `Errors errors` | ❌ **masih** (POST, POST/batch, PUT) |
| DTO `LevelResponse` | ✅ **sudah record** |

### Checklist

- [ ] Tambahkan import typed result
- [ ] `ResponseEntity<PageResult<Page<LevelResponse>>>` untuk index
- [ ] `ResponseEntity<ListResult<LevelResponse>>` untuk list
- [ ] `ResponseEntity<SingleResult<LevelResponse>>` untuk findById
- [ ] Hapus `Errors errors` dari POST/PUT/POST/batch
- [ ] `./gradlew compileJava` sukses

---

## 📋 GAP Pattern — Column Set Arrays

Pattern (`master-query-optimization-pattern.md`) mewajibkan **column set per endpoint** (`XXX_QUERY_COLUMNS`), tapi hanya `ProfesiSelects` yang memilikinya.

| Selects | Column Array | Severitas |
|---------|:-----------:|:---------:|
| ProfesiSelects | ✅ `PROFESI_COLUMNS, _QUERY_, _DETAIL_` | — |
| GradeSelects | ❌ | Medium |
| OrganisasiSelects | ❌ | Medium |
| JabatanSelects | ❌ | Medium |
| JenisSpSelects | ❌ | Low (flat) |
| SanksiSelects | ❌ | Medium |

**Tindakan:** Tambahkan column set arrays ke setiap Selects kelas setelah controller typing selesai.

---

## 📋 GAP Pattern — Mini Response DTO ke Record

`SharedMappers.buildOrganisasi()` dan kawan-kawan masih menggunakan setter karena `OrganisasiMiniResponse`, `JabatanMiniResponse`, `GradeMiniResponse` masih `@Data` class. Pattern menghendaki immutable record.

| Mini Response | Status |
|:-------------|:------:|
| `OrganisasiMiniResponse` | ❌ masih `@Data` |
| `JabatanMiniResponse` | ❌ masih `@Data` |
| `GradeMiniResponse` | ❌ masih `@Data` |
| `JenisSpMiniResponse` | ❌ masih `@Data` |

**Tindakan:** Konversi ke record, update SharedMappers pakai constructor `new OrganisasiMiniResponse(id, kode, nama, shortName)`.

---

## 📋 GAP CODING_RULES

### 1. gitnexus_impact WAJIB sebelum edit

AGENTS.md dan CODING_RULES mewajibkan `gitnexus_impact` sebelum mengubah simbol apa pun.

| Modul | Wajib impact analysis? |
|-------|:---------------------:|
| E3 JenjangPendidikan | ✅ **YA** — banyak digunakan cross-modul |
| E4 Sanksi (FK ke JenisSp) | ✅ **YA** — relasi |
| E5 Grade (FK ke Level) | ✅ **YA** — relasi |
| E6 Organisasi (self-ref parent) | ✅ **YA** — tree |
| E7 Jabatan (FK ke Org+Level) | ✅ **YA** — multi join |
| LevelController | ✅ **YA** — dipakai Grade & Jabatan |

### 2. Unit test untuk logic baru

CODING_RULES: "Unit tests required for new logic."

- RecordMapper baru (GradeJooqMapper, SanksiJooqMapper) — perlu unit test
- Konversi DTO (record equality, null handling) — perlu unit test
- Minimal: test `toQuery()` untuk valid mapping

### 3. Max 120 lines per file

CODING_RULES: "Max 120 lines per file. Split if exceeded."
- Existing `SanksiQueryRepository.java` — ~120 lines (perlu dicek)
- `OrganisasiQueryRepository.java` — >120 lines (perlu dicek)
- Pastikan file baru tidak melebihi batas.

---

## Quick Reference — Pola per Tipe Modul

### Flat (tanpa JOIN)

```
XxxQuery          → record(id, field1, field2)
XxxListResponse   → record(id, nama)              // untuk dropdown
Repository        → fetchInto(XxxQuery.class)     // page
                  → fetchInto(XxxListResponse.class) // list
Mapper            → tidak perlu (fetchInto langsung)
Controller        → ResponseEntity<PageResult<Page<XxxQuery>>>
```

### Moderate (1 JOIN)

```
XxxQuery          → record(id, field, ..., NestedObject nested)  // tanpa FK duplikat
XxxSelects        → public class, public static Field + column set array
XxxJooqMapper     → typed toQuery(Record), builder helpers
Repository        → method reference ::toQuery
Controller        → typed ResponseEntity
```

### Tree (self-ref JOIN)

```
XxxQuery          → record(id, field, ..., ParentMiniResponse parent)
XxxSelects        → parent aliased fields (PARENT.ID.as("parent_id"), dll)
XxxJooqMapper     → buildParent(Record) helper
Repository        → leftJoin parent table
```

### Enum Read-Only

```
EnumOption        → record(id, nama)
Controller        → Arrays.stream(enum.values()).map(e → new EnumOption(...)).toList()
```

---

## Common Mistakes

| Jangan | Lakukan |
|--------|---------|
| `record.intoMap()` + `(Long) map.get("id")` | `record.get(PROFESI.ID)` |
| `fetch(record → mapper.method(record.intoMap()))` | `fetch(mapper::method)` |
| `.fetchInto(XxxQuery.class)` untuk query dengan JOIN | `.fetch(XxxJooqMapper::toQuery)` dengan Selects |
| Menyimpan FK ID di record (`organisasiId`, `jabatanId`) | Hapus — client pakai `organisasi.id()` |
| `ResponseEntity<?>` | `ResponseEntity<SingleResult<XxxDetail>>` |
| `Errors errors` parameter di controller | Hapus — validasi via `@Valid` |
| Lupa `gitnexus_impact` sebelum edit | Jalankan dulu — cek blast radius |
| Lupa column set array di Selects | Tambahkan `XXX_QUERY_COLUMNS` per endpoint |
| Lewati unit test untuk RecordMapper | Tulis test `toQuery()` valid mapping |

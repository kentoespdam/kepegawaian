# Master Record Refactor — Claim Order & Checklist

> Implementasi Java record + typed RecordMapper + lean column select untuk semua modul master.
>
> Pattern rujukan: `docs/master-query-optimization-pattern.md`
> Exemplar: Modul **Profesi** (commit `b732295`)
> CODING_RULES: `CODING_RULES.md`
> AGENTS: `AGENTS.md`

---

## Status Real (per 2026-07-08)

✅ **SEMUA MODUL SUDAH FULLY IMPLEMENTED.**

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
| **Level (GAP)** | ✅ | — | — | ✅ | ✅ | ✅ |

\* JenisSpQueryRepository pakai `fetchInto`, bukan method reference — fine untuk flat entity.
\* SanksiResponse sudah dihapus (dead code cleanup). SanksiJooqMapper sudah pakai `toQuery`.
\* MiniResponse DTO (Organisasi, Jabatan, Grade, JenisSp) sudah semuanya `record`.

---

## Dependency Graph

```
E0 Foundation ───────────── kepegawaian-hkq [SUDAH ✅]
├── blocks E1 Flat batch 1  kepegawaian-5k9 [SUDAH ✅]
├── blocks E2 Flat batch 2  kepegawaian-1xy [SUDAH ✅]
├── blocks E4 JenisSp+Sanksi kepegawaian-sr1 [SUDAH ✅]
├── blocks E5 Grade          kepegawaian-oqe   [SUDAH ✅]
├── blocks E6 Organisasi     kepegawaian-rfc   [SUDAH ✅]
├── blocks E7 Jabatan        kepegawaian-bjk   [SUDAH ✅]

E3 JenjangPendidikan ────── kepegawaian-1ws [SUDAH ✅]
E8 Enum read-only ───────── kepegawaian-78r  [SUDAH ✅]
Level (GAP) ─────────────── [SUDAH ✅]
```

**Claim order:** ✅ Semua selesai. Tidak ada pekerjaan tersisa untuk record refactor modul master.

---

## E0: Foundation (kepegawaian-hkq) — ✅ SELESAI

**Tujuan:** Extract shared infrastructure agar setiap modul tidak perlu bikin ulang.

### Status

| Item | Status | File |
|------|--------|------|
| `SharedSelects.java` | ✅ **SUDAH** | `repositories/master/jooq/SharedSelects.java` |
| `SharedMappers.java` | ✅ **SUDAH** | `mapper/master/SharedMappers.java` |
| ProfesiSelects update | ✅ **SUDAH** | Pakai SharedSelects |
| MiniResponse → record | ✅ **SUDAH** | Semua sudah `record` |
| SharedMappers → constructor | ✅ **SUDAH** | Pakai `new OrganisasiMiniResponse(id, ...)` |

### Checklist

- [x] `SharedSelects.java` — public final class, private constructor
- [x] Field untuk ORGANISASI, JABATAN, LEVEL, GRADE
- [x] `SharedMappers.java` — builder: buildOrganisasi, buildJabatan, buildGrade, buildLevel
- [x] ProfesiJooqMapper import dari shared
- [x] `./gradlew compileJava` sukses
- [x] `./gradlew test` sukses

---

## E1: Flat Batch 1 (kepegawaian-5k9) — ✅ SELESAI

**Modul:** Golongan, JenisKeahlian, JenisPelatihan, JenisKitas

**Verifikasi 2026-07-08:** Semua sudah sesuai pattern.

| Modul | Query (`record`) | ListResponse (`record`) | Repository (`fetchInto`) | Controller (typed) |
|-------|:----------------:|:-----------------------:|:-----------------------:|:------------------:|
| Golongan | ✅ `(Long id, String golongan, String pangkat)` | ✅ | ✅ `GolonganQueryRepository` | ✅ |
| JenisKeahlian | ✅ `(Long id, String nama)` | ✅ | ✅ | ✅ |
| JenisPelatihan | ✅ `(Long id, String nama)` | ✅ | ✅ | ✅ |
| JenisKitas | ✅ `(Long id, String nama)` | ✅ | ✅ | ✅ |

---

## E2: Flat Batch 2 (kepegawaian-1xy) — ✅ SELESAI

**Modul:** AlasanBerhenti, RumahDinas, HariLibur

**Verifikasi 2026-07-08:** Semua sudah sesuai pattern.

| Modul | Query (`record`) | ListResponse (`record`) | Repository (`fetchInto`) | Controller (typed) |
|-------|:----------------:|:-----------------------:|:-----------------------:|:------------------:|
| AlasanBerhenti | ✅ `(Long id, String nama, String notes)` | ✅ | ✅ | ✅ |
| RumahDinas | ✅ `(Long id, String nama, Double nilai)` | ✅ | ✅ | ✅ |
| HariLibur | ✅ `(Long id, LocalDate tanggal, ...)` | ✅ | ✅ | ✅ |

---

## E3: JenjangPendidikan (kepegawaian-1ws) — ✅ SELESAI

### Status

| Item | Status |
|------|--------|
| `JenjangPendidikanResponse` → record | ✅ **SUDAH** |
| `from()` static methods | ✅ **SUDAH** |
| Controller typed | ✅ **SUDAH** |
| Hapus `Errors errors` | ✅ **SUDAH** |

---

## E4: JenisSp + Sanksi (kepegawaian-sr1) — ✅ SELESAI

**Verifikasi 2026-07-08:** Semua sudah sesuai pattern.

| Item | JenisSp | Sanksi |
|------|:-------:|:------:|
| Query → record | ✅ | ✅ |
| Selects | ✅ | ✅ `SANKSI_QUERY_COLUMNS` |
| Mapper | — (fetchInto) | ✅ `toQuery(Record)` |
| Repository | ✅ | ✅ |
| Controller typed | ✅ | ✅ |
| `SanksiResponse` | — | ✅ **sudah dihapus** (dead code) |
| `JenisSpMiniResponse` → record | ✅ **sudah record** | — |
| `SanksiJooqMapper.mapToQuery` | — | ✅ **sudah `toQuery`** |

---

## E5: Grade (kepegawaian-oqe) — ✅ SELESAI

### Status

| Item | Status |
|------|--------|
| `GradeQuery` → record | ✅ |
| `GradeSelects.java` | ✅ (individual fields, column array tidak perlu — pakai inline) |
| `GradeJooqMapper` | ✅ |
| `GradeQueryRepository` | ✅ method reference |
| `GradeListResponse` | ✅ record |
| Controller typed | ✅ |

---

## E6: Organisasi (kepegawaian-rfc) — ✅ SELESAI

### Status

| Item | Status |
|------|--------|
| `OrganisasiQuery` → record | ✅ |
| `OrganisasiSelects.java` | ✅ (`ORGANISASI_COLUMNS` + `parentColumns()`) |
| `OrganisasiJooqMapper` | ✅ |
| `OrganisasiQueryRepository` | ✅ method reference |
| `OrganisasiListResponse` | ✅ record |
| Controller typed | ✅ |

---

## E7: Jabatan (kepegawaian-bjk) — ✅ SELESAI

### Status

| Item | Status |
|------|--------|
| `JabatanQuery` → record | ✅ |
| `JabatanSelects.java` | ✅ (`JABATAN_COLUMNS` + `parentColumns()`) |
| `JabatanJooqMapper` | ✅ |
| `JabatanQueryRepository` | ✅ method reference |
| `JabatanListResponse` | ✅ record |
| Controller typed | ✅ |

---

## E8: Enum Read-Only (kepegawaian-78r) — ✅ SELESAI

### Status

| Item | Status |
|------|--------|
| `EnumOption` record | ✅ |
| `StatusPegawaiResponse` record | ✅ |
| Semua controller typed | ✅ |

---

## LevelController (GAP) — ✅ SELESAI

| Item | Status |
|------|--------|
| `LevelResponse` → record | ✅ |
| Controller typed | ✅ |
| Hapus `Errors errors` | ✅ |

---

## Column Set Arrays — ✅ SELESAI

| Selects | Column Array | Status |
|---------|:-----------:|:------:|
| ProfesiSelects | `PROFESI_QUERY_COLUMNS`, `PROFESI_DETAIL_COLUMNS` | ✅ |
| OrganisasiSelects | `ORGANISASI_COLUMNS` | ✅ |
| JabatanSelects | `JABATAN_COLUMNS` | ✅ |
| SanksiSelects | `SANKSI_QUERY_COLUMNS` | ✅ |
| GradeSelects | — (tidak perlu, pakai inline individual fields) | ✅ |

---

## Mini Response DTO → Record — ✅ SELESAI

| Mini Response | Status |
|:-------------|:------:|
| `OrganisasiMiniResponse` | ✅ `record(Long id, String kode, String nama, String shortName)` |
| `JabatanMiniResponse` | ✅ `record(Long id, String kode, LevelResponse level, String nama)` |
| `GradeMiniResponse` | ✅ `record(Long id, Integer grade, Double tukin)` |
| `JenisSpMiniResponse` | ✅ `record(Long id, String kode, String nama, List<SanksiMiniResponse> sanksiSp)` |

---

## Kesimpulan

✅ **Semua modul master sudah sesuai pattern Java record + typed RecordMapper + lean column select.**
Tidak ada pekerjaan tersisa dari `docs/master-record-refactor-claim-order.md`.

---

## Quick Reference — Pola per Tipe Modul

### Flat (tanpa JOIN)

```
XxxQuery          → record(id, field1, field2)
XxxListResponse   → record(id, nama)              // untuk dropdown (default)
Repository        → fetchInto(XxxQuery.class)     // page
                  → fetchInto(XxxListResponse.class) // list
Mapper            → tidak perlu (fetchInto langsung)
Controller        → ResponseEntity<PageResult<Page<XxxQuery>>>
```

> **Pengecualian:** Bila FE butuh field tambahan untuk cascading filter (mis. `levelId` pada Grade/Jabatan untuk filter Grade by Level di form Profesi), field tsb ditambahkan ke `XxxListResponse`. Jangan tambah sembarangan — hanya dengan evidence FE benar-benar pakai. Contoh: `GradeListResponse(Long id, Integer grade, Long levelId)`, `JabatanListResponse(Long id, String nama, Long levelId)`. Lihat docs/master-query-optimization-pattern.md §3a untuk detail.

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

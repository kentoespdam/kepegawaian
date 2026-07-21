# JOOQ mapping master: `fetchInto` flat, `*JooqMapper` join-nested & multiset

> **Status:** accepted — mengikat semua aggregate modul `master/` (17 DB-backed + 5 enum read-only).

Dalam modul `master/`, tiga strategi JOOQ mapping dipakai secara hybrid berdasarkan kompleksitas projeksi:

1. **`fetchInto(XxxQuery.class)`** — untuk aggregate **flat** (tanpa join ke nested object). JOOQ `DefaultRecordMapper` memetakan kolom snake_case ke field camelCase secara otomatis. Tidak butuh kelas mapper terpisah.
2. **`fetch(XxxJooqMapper::mapToQuery)`** — untuk aggregate yang join-nya menghasilkan **nested object** dalam response DTO (mis. `GradeQuery.level: LevelResponse`, `SanksiQuery.jenisSp: JenisSpMiniResponse`). `fetchInto` tidak bisa populate nested object; mapping manual diperlukan.
3. **`select(multiset(...).convertFrom(r -> r.map(mapping(RowRecord::new))))`** — untuk aggregate yang memiliki **nested list** one-to-many (mis. `ProfesiDetail.apdList: List<ApdRow>`, `JenisSpQuery.sanksiList: List<SanksiRow>`). JOOQ `multiset` menghasilkan subquery korelated sebagai kolom JSON, lalu dikonversi ke `List<RowRecord>` via `Records.mapping`.

`*JooqMapper` mengikuti Pola A dari `profil-cqrs-implementation-patterns.md §2b`:
- `final class`, private ctor, **BUKAN `@Component`**
- static `mapToQuery(Record record)` atau `mapToResponse(Record record)`
- Ditempatkan di `mapper/master/<agg>/`, bukan di dalam `@Repository`

## Latar belakang

Sebelum ADR ini, `GradeQueryRepository` dan `SanksiQueryRepository` punya `private toQuery(Map<String, Object>)` yang embed mapping logic langsung di dalam `@Repository`. Ini melanggar pemisahan concern: query-building dan result-mapping bercampur dalam satu kelas. Dengan ekstraksi ke `*JooqMapper`, repo kembali murni sebagai query-builder.

## Considered Options

- **`fetchInto` seragam untuk semua** (ditolak): tidak bisa handle nested object (`LevelResponse`, `JenisSpMiniResponse`). Juga tidak bisa handle `multiset` (hasilnya jadi string JSON, bukan `List`).
- **`*JooqMapper` seragam untuk semua** (ditolak): verbose dan berlebihan untuk aggregate flat yang `fetchInto` sudah cukup. Overkill, melanggar KISS.
- **Hybrid: `fetchInto` flat / `*JooqMapper` join-nested & multiset** (dipilih): setiap aggregate pakai strategi yang paling sederhana yang cukup. Aturan keputusan jelas: flat → `fetchInto`; ada nested object → `*JooqMapper`; ada nested list one-to-many → `multiset` + `*JooqMapper`.

## Aturan keputusan

| Kondisi aggregate | Strategi mapping | Butuh `*JooqMapper`? |
|-------------------|-----------------|----------------------|
| Flat (satu tabel, tanpa join ke nested DTO) | `fetchInto(XxxQuery.class)` | Tidak |
| Join → nested object dalam DTO | `fetch(XxxJooqMapper::mapToQuery)` | Ya, di `mapper/master/<agg>/` |
| `multiset` → nested list one-to-many | `fetch(XxxJooqMapper::mapToQuery)` | Ya, di `mapper/master/<agg>/` |

Aggregate yang saat ini butuh `*JooqMapper`:
- **Grade** (`GradeQuery.level: LevelResponse`) → join → `GradeJooqMapper`
- **Sanksi** (`SanksiQuery.jenisSp: JenisSpMiniResponse`) → join → `SanksiJooqMapper`
- **Profesi** (`ProfesiDetail.apdList: List<ApdRow>`, `alatKerjaList: List<AlatKerjaRow>`) → multiset → `ProfesiJooqMapper`
- **JenisSp** (`JenisSpQuery.sanksiList: List<SanksiRow>`) → multiset → `JenisSpJooqMapper`

### Pola multiset

Untuk nested list one-to-many, proyeksi child adalah Java record sederhana (seperti `ApdRow`, `SanksiRow`) dengan field yang sesuai kolom yang diselect. Di query repo:

```java
multiset(dsl.select(CHILD.ID, CHILD.NAMA)
        .from(CHILD)
        .where(CHILD.PARENT_ID.eq(PARENT.ID))
        .and(CHILD.IS_DELETED.eq(false))
        .orderBy(CHILD.NAMA.asc()))
        .as("child_list")
        .convertFrom(r -> r.map(mapping(ChildRow::new)))
```

Di mapper, cast `record.get("child_list", List.class)` ke `List<ChildRow>`.

## Consequences

- `GradeQueryRepository` dan `SanksiQueryRepository` kehilangan `private toQuery` — repo jadi murni query-building.
- Aggregate baru yang join-nested atau one-to-many wajib buat `*JooqMapper`; aggregate flat cukup `fetchInto`.
- Pemilihan strategi jadi explicit decision per aggregate, bukan implisit dari kode.
- Record row untuk multiset harus Java 16+ `record` dengan constructor yang sesuai urutan kolom di `dsl.select(...)` — JOOQ `Records.mapping` bergantung pada positional constructor matching.
- Boolean kolom MariaDB via JOOQ generated: `record.get(col, Boolean.class)` langsung (bukan `Byte`).

# JOOQ mapping master: `fetchInto` untuk flat, `*JooqMapper` untuk join-nested

> **Status:** accepted — mengikat semua aggregate modul `master/` (17 DB-backed + 5 enum read-only).

Dalam modul `master/`, dua strategi JOOQ mapping dipakai secara hybrid berdasarkan kompleksitas projeksi:

1. **`fetchInto(XxxQuery.class)`** — untuk aggregate **flat** (tanpa join ke nested object). JOOQ `DefaultRecordMapper` memetakan kolom snake_case ke field camelCase secara otomatis. Tidak butuh kelas mapper terpisah.
2. **`fetch(XxxJooqMapper::mapToQuery)`** — untuk aggregate yang join-nya menghasilkan **nested object** dalam response DTO (mis. `GradeQuery.level: LevelResponse`, `SanksiQuery.jenisSp: JenisSpMiniResponse`). `fetchInto` tidak bisa populate nested object; mapping manual diperlukan.

`*JooqMapper` mengikuti Pola A dari `profil-cqrs-implementation-patterns.md §2b`:
- `final class`, private ctor, **BUKAN `@Component`**
- static `mapToQuery(Record record)` atau `mapToResponse(Record record)`
- Ditempatkan di `mapper/master/<agg>/`, bukan di dalam `@Repository`

## Latar belakang

Sebelum ADR ini, `GradeQueryRepository` dan `SanksiQueryRepository` punya `private toQuery(Map<String, Object>)` yang embed mapping logic langsung di dalam `@Repository`. Ini melanggar pemisahan concern: query-building dan result-mapping bercampur dalam satu kelas. Dengan ekstraksi ke `*JooqMapper`, repo kembali murni sebagai query-builder.

## Considered Options

- **`fetchInto` seragam untuk semua** (ditolak): tidak bisa handle nested object (`LevelResponse`, `JenisSpMiniResponse`). Memaksa flatten DTO → breaking change API untuk consumer yang sudah expect objek nested.
- **`*JooqMapper` seragam untuk semua** (ditolak): verbose dan berlebihan untuk aggregate flat yang `fetchInto` sudah cukup. Overkill, melanggar KISS.
- **Hybrid: `fetchInto` flat / `*JooqMapper` join-nested** (dipilih): setiap aggregate pakai strategi yang paling sederhana yang cukup. Aturan keputusan jelas: ada nested object → `*JooqMapper`; flat → `fetchInto`.

## Aturan keputusan

| Kondisi aggregate | Strategi mapping | Butuh `*JooqMapper`? |
|-------------------|-----------------|----------------------|
| Flat (satu tabel, tanpa join ke nested DTO) | `fetchInto(XxxQuery.class)` | Tidak |
| Join → nested object dalam DTO | `fetch(XxxJooqMapper::mapToQuery)` | Ya, di `mapper/master/<agg>/` |

Aggregate yang saat ini butuh `*JooqMapper`:
- **Grade** (`GradeQuery.level: LevelResponse`) → `GradeJooqMapper`
- **Sanksi** (`SanksiQuery.jenisSp: JenisSpMiniResponse`) → `SanksiJooqMapper`

## Consequences

- `GradeQueryRepository` dan `SanksiQueryRepository` kehilangan `private toQuery` — repo jadi murni query-building.
- Aggregate baru yang join-nested wajib buat `*JooqMapper`; aggregate flat cukup `fetchInto`.
- Pemilihan strategi jadi explicit decision per aggregate, bukan implisit dari kode.
- Boolean kolom MariaDB via JOOQ generated: `record.get(col, Boolean.class)` langsung (bukan `Byte`).

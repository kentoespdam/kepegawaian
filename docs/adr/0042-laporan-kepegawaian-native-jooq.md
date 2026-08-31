# Laporan Kepegawaian: native JOOQ menggantikan proxy Python

> **Status:** accepted

Service pelaporan (`/laporan/kepegawaian/*`) saat ini berjalan sebagai FastAPI Python terpisah (`LAPORAN_KEPEGAWAIAN_URL`). Spring Boot hanya bertindak sebagai **proxy** — `LaporanKepegawaianService` meneruskan setiap request via `RestClient` ke Python service. Migrasi ini menghilangkan dependensi tersebut dengan menulis ulang query SQL + business logic langsung di Spring Boot menggunakan JOOQ DSL, dengan Excel generation via Apache POI.

## Considered Options

### 1. JOOQ DSL (dipilih)

- **Pro:** Type-safe, tabel sudah ter-generate (90+ tables di `org.jooq.kepegawaian.Tables`), kompatibel dengan existing `DSLContext` bean, compilable query.
- **Con:** MySQL-specific functions (`TIMESTAMPDIFF`, `CONCAT_WS`) perlu `DSL.field()` workaround — tidak 100% type-safe.

### 2. JdbcTemplate + raw SQL

- **Pro:** Porting paling mudah (SQL Python → Java), mudah diverifikasi.
- **Con:** SQL strings tidak compile-checked, scattered across service layer, tidak memanfaatkan JOOQ codegen yang sudah ada.

### 3. `@Query(nativeQuery=true)` pada JPA repository

- **Pro:** Sudah dipakai di profil repos (Biodata, Pendidikan, ProfilKeluarga).
- **Con:** Tidak fleksibel untuk dynamic WHERE (Kontrak punya 6 filter variant, Kenaikan Berkala punya 4). Annotation-based SQL sulit di-compose.

### 4. Biarkan proxy Python (status quo, ditolak)

- **Pro:** Zero effort.
- **Con:** Extra deployment unit, network latency, dependensi eksternal untuk fitur internal.

## Key Design Decisions

### Layer structure (mengikuti ADR-0006, ADR-0017)

```
repositories/laporan/kepegawaian/   ← JOOQ DSLContext queries
services/laporan/kepegawaian/       ← @Service, @Transactional(readOnly=true)
dto/laporan/kepegawaian/            ← Typed response DTOs
mapper/laporan/kepegawaian/         ← Static record→DTO mappers
```

Satu repository + satu service per modul laporan (8 modules). Tidak ada interface/impl — mengikuti ADR-0007 (concrete services).

### Cleanup/transformation: hybrid SQL + Java

- **SQL handles:** `TIMESTAMPDIFF`, `CONCAT_WS`, `DATE_FORMAT`, `IFNULL`, `IF(cond, a, b)` — semua bisa ditulis sebagai `DSL.field("...", type, args)`.
- **Java handles:** Enum decode (status_pegawai → label), percentage calculation, date formatting untuk Excel output, boolean cleanup dari MySQL `b'\x01'`.

### Excel: Apache POI + classpath templates

Template `.xlsx` dari Python project dicopy ke `src/main/resources/templates/laporan/`. Apache POI memuat template, mengisi body data via `Row`/`Cell` API, lalu stream sebagai `ByteArrayResource`. Ini mengikuti pola Python (template + body fill), bukan generating Excel mentah dari nol.

### API paths: tidak berubah

`/laporan/kepegawaian/*` dipertahankan. Frontend tidak perlu ubah apa pun. Controller yang ada hanya di-update injection-nya dari `LaporanKepegawaianService` (proxy) ke service spesifik.

## MySQL-Specific → JOOQ Translation

| MySQL | JOOQ |
|-------|------|
| `TIMESTAMPDIFF(YEAR, a, b)` | `DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", Integer.class, a, b)` |
| `TIMESTAMPDIFF(MONTH, a, b)` | `DSL.field("TIMESTAMPDIFF(MONTH, {0}, {1})", Integer.class, a, b)` |
| `CONCAT_WS(' ', a, b, c)` | `DSL.field("CONCAT_WS(' ', {0}, {1}, {2})", String.class, a, b, c)` |
| `DATE_FORMAT(d, '%d.%m.%Y')` | `DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, d)` |
| `IF(cond, a, b)` | `DSL.field("IF({0}, {1}, {2})", type, cond, a, b)` |
| `IFNULL(a, b)` | `DSL.coalesce(a, b)` |
| `CURDATE()` | `DSL.currentDate()` |
| `NOW()` | `DSL.currentLocalDateTime()` |

## Consequences

- **Hapus `LaporanKepegawaianService`** (proxy) dan env var `LAPORAN_KEPEGAWAIAN_URL` setelah migrasi selesai.
- **Hapus RestClient import** dari semua laporan controller (hanya inject service spesifik).
- **8 repository + 8 service + ~15 DTO + 8 mapper** file baru.
- **Template Excel** (.xlsx) perlu di-copy dari Python `template/` ke classpath Spring Boot.
- **Tidak ada perubahan database schema** — query yang sama, hanya beda eksekusi.
- **Testing:** Bandingkan JSON output dari Spring Boot vs Python untuk setiap endpoint, menggunakan data produksi yang sama.

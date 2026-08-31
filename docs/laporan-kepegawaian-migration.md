# Laporan Kepegawaian — Migration Plan

> Migrasi service Python `laporan-kepegawaian` (FastAPI) ke dalam Spring Boot
> agar tidak perlu fetching ke service lain.

## Status

| Item | Value |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Python service | `/home/dev/python/laporan-kepegawaian` (FastAPI + pandas + openpyxl) |
| Target | Native Spring Boot (JOOQ + Apache POI) |
| API paths | **Dipertahankan** `/laporan/kepegawaian/*` |

## Architecture Decisions (Summary)

| Decision | Choice | ADR |
|----------|--------|-----|
| Query layer | JOOQ DSL (generated tables) | ADR-0039 |
| Excel generation | Apache POI + classpath templates | ADR-0039 |
| Response format | Typed DTOs per module | — |
| Service structure | One service per report module | — |
| API paths | Keep `/laporan/kepegawaian/*` | — |
| Cleanup/transformation | Hybrid (SQL for DB-specific, Java for business logic) | — |

## Module Scope

| # | Module | Python file | Endpoints | Complexity |
|---|--------|------------|-----------|------------|
| 1 | **DUK** (Daftar Urut Kepangkatan) | `models/duk.py`, `services/duk.py` | `GET /duk/`, `GET /duk/excel` | Low |
| 2 | **DNP** (Daftar Nominatif Pegawai) | `models/dnp.py`, `services/dnp.py` | `GET /dnp/`, `GET /dnp/excel` | Medium |
| 3 | **SO** (Struktur Organisasi) | `models/so.py`, `services/so.py` | `GET /so/` | Low |
| 4 | **Statistik** (8 sub-endpoints) | `models/statistik.py`, `services/statistik.py` | `GET /statistik/{type}` | Medium |
| 5 | **Mutasi** | `models/mutasi.py`, `services/mutasi.py` | `GET /mutasi/{from}/{to}`, `GET /mutasi/excel/{from}/{to}` | Low |
| 6 | **Kontrak** | `models/kontrak.py`, `services/kontrak.py` | `GET /kontrak/`, `GET /kontrak/excel` | Medium |
| 7 | **Kenaikan Berkala** | `models/kenaikan_berkala.py`, `services/kenaikan_berkala.py` | `GET /kenaikan_berkala/`, `GET /count`, `GET /excel` | High |
| 8 | **LTA** (Lepas Tanggungan Anak) | `models/lepas_tanggungan_anak.py`, `services/lepas_tanggungan_anak.py` | `GET /lepas_tanggungan_anak/`, `GET /count`, `GET /excel` | Medium |

## Claim Order (Urutan Pengerjaan)

### Phase 1: Foundation

- [x] **1.1** Copy template `.xlsx` files dari Python `template/` ke `src/main/resources/templates/laporan/`
- [x] **1.2** Create package structure:
  ```
  repositories/laporan/kepegawaian/    ← JOOQ query repositories
  services/laporan/kepegawaian/        ← One service per module (replace proxy)
  dto/laporan/kepegawaian/             ← Typed response DTOs (extend existing enums)
  mapper/laporan/kepegawaian/          ← Record → DTO mappers
  ```
- [x] **1.3** Create response DTOs untuk semua 8 modules (lihat DTO section di bawah)

### Phase 2: Simple Modules (DUK, SO, Mutasi)

- [x] **2.1** **DUK** — `DukRepository` + `DukService` + `DukResponse`
  - Query: JOIN pegawai + biodata + golongan + jabatan + pendidikan + jenjang_pendidikan
  - Cleanup: format tanggal (dd.MM.yyyy), hitung sisa bulan, decode status_pegawai
- [x] **2.2** **SO** — `SoRepository` + `SoService` + `SoResponse`
  - Query: jabatan LEFT JOIN pegawai (active) + biodata, level_id ≤ 6
  - Cleanup: build hierarchy tree (recursive parent→children)
- [x] **2.3** **Mutasi** — `MutasiRepository` + `MutasiService` + `MutasiResponse`
  - Query: SELECT from `riwayat_mutasi` (VIEW) with date range filter
  - Cleanup: decode jenis_mutasi, format tanggal

### Phase 3: Medium Modules (DNP, Kontrak, LTA, Statistik)

- [x] **3.1** **DNP** — `DnpRepository` + `DnpService` + `DnpResponse`
  - Complex query: 7-table JOIN (pegawai + biodata + jabatan + golongan + organisasi + pendidikan + jenjang_pendidikan)
  - Cleanup: mask kode_organisasi for direksi, compute mkg_bulan, format tanggal
  - Excel: grouped by organisasi, nested rows
- [x] **3.2** **Kontrak** — `KontrakRepository` + `KontrakService` + `KontrakResponse`
  - Dynamic WHERE based on filter enum (6 variants)
  - Query: pegawai + biodata + riwayat_kontrak (is_latest) + organisasi + jabatan
  - Cleanup: format tanggal Indonesia
- [x] **3.3** **LTA** — `LtaRepository` + `LtaService` + `LtaResponse`
  - Complex: month/year rollover calculation, TIMESTAMPDIFF with STR_TO_DATE
  - Query: profil_keluarga + biodata + pegawai + jabatan
  - Cleanup: decode jenis_kelamin, format tanggal, boolean cleanup
- [x] **3.4** **Statistik** — `StatistikRepository` + `StatistikService` + 8 response types
  - 8 sub-endpoints: golongan, pendidikan1, pendidikan2, umur, jenis_kelamin, gelar_akademik, agama, status_pegawai
  - Most are simple GROUP BY aggregations
  - Pendidikan2 reads from snapshot table `statistik_pegawai`

### Phase 4: Complex Module (Kenaikan Berkala)

- [x] **4.1** **Kenaikan Berkala** — `KenaikanBerkalaRepository` + `KenaikanBerkalaService` + `KenaikanBerkalaResponse`
  - Most complex: 8-table JOIN (riwayat_sk + pegawai + biodata + golongan + jabatan + pendidikan + jenjang_pendidikan + riwayat_sp + sanksi_sp)
  - Dynamic conditions: filter changes WHERE clause (BULAN_INI, GTE_1, GTE_2, TAHUN_INI)
  - Cleanup: decode is_pending_gaji/pangkat from byte, conditional nullification
  - Count endpoint with same filter logic

### Phase 5: Cleanup

- [x] **5.1** Remove `LaporanKepegawaianService` (the proxy)
- [x] **5.2** Remove `LAPORAN_KEPEGAWAIAN_URL` from `application.yml`
- [x] **5.3** Update controllers to inject specific services instead of proxy
- [x] **5.4** Verify all endpoints match Python behavior (JSON format, status codes)
- [x] **5.5** Build & test: `./gradlew build`

## Target File Structure

```
src/main/java/id/perumdamts/kepegawaian/
├── controllers/laporan/kepegawaian/      ← EXISTING (update injections)
│   ├── LaporanDnpController.java         ← update: inject DnpService
│   ├── LaporanDukController.java         ← update: inject DukService
│   ├── LaporanSoController.java          ← update: inject SoService
│   ├── LaporanMutasiController.java      ← update: inject MutasiService
│   ├── LaporanKontrakController.java     ← update: inject KontrakService
│   ├── LaporanLtaController.java         ← update: inject LtaService
│   ├── LaporanStatistikController.java   ← update: inject StatistikService
│   └── LaporanKenaikanBerkalaController.java ← update: inject KenaikanBerkalaService
├── repositories/laporan/kepegawaian/     ← NEW
│   ├── DukRepository.java
│   ├── DnpRepository.java
│   ├── SoRepository.java
│   ├── MutasiRepository.java
│   ├── KontrakRepository.java
│   ├── LtaRepository.java
│   ├── StatistikRepository.java
│   └── KenaikanBerkalaRepository.java
├── services/laporan/kepegawaian/         ← REPLACE proxy
│   ├── DukService.java
│   ├── DnpService.java
│   ├── SoService.java
│   ├── MutasiService.java
│   ├── KontrakService.java
│   ├── LtaService.java
│   ├── StatistikService.java
│   └── KenaikanBerkalaService.java
├── dto/laporan/kepegawaian/              ← NEW (extend existing enums)
│   ├── DukResponse.java
│   ├── DnpResponse.java
│   ├── DnpOrganisasiResponse.java
│   ├── SoResponse.java (hierarchy node)
│   ├── MutasiResponse.java
│   ├── KontrakResponse.java
│   ├── LtaResponse.java
│   ├── StatistikGolonganResponse.java
│   ├── StatistikPendidikan1Response.java
│   ├── StatistikPendidikan2Response.java
│   ├── StatistikUmurResponse.java
│   ├── StatistikUmurRangeResponse.java
│   ├── StatistikJenisKelaminResponse.java
│   ├── StatistikGelarResponse.java
│   ├── StatistikAgamaResponse.java
│   ├── StatistikStatusPegawaiResponse.java
│   ├── KenaikanBerkalaResponse.java
│   └── (existing enums: EFilterKontrak, EFilterLta, etc.)
└── mapper/laporan/kepegawaian/           ← NEW
    ├── DukRecordMapper.java
    ├── DnpRecordMapper.java
    ├── SoRecordMapper.java
    ├── MutasiRecordMapper.java
    ├── KontrakRecordMapper.java
    ├── LtaRecordMapper.java
    ├── StatistikRecordMapper.java
    └── KenaikanBerkalaRecordMapper.java
```

## Key JOOQ Table References

Semua tabel sudah ter-generate di `org.jooq.kepegawaian.Tables`:

| Python table | JOOQ constant | Used by |
|-------------|---------------|---------|
| `pegawai` | `PEGAWAI` | All modules |
| `biodata` | `BIODATA` | All modules |
| `golongan` | `GOLONGAN` | DUK, DNP, KB |
| `jabatan` | `JABATAN` | All modules |
| `organisasi` | `ORGANISASI` | DNP, DUK, SO, Kontrak |
| `pendidikan` | `PENDIDIKAN` | DUK, DNP, KB, Statistik |
| `jenjang_pendidikan` | `JENJANG_PENDIDIKAN` | DUK, DNP, KB, Statistik |
| `riwayat_sk` | `RIWAYAT_SK` | KB |
| `riwayat_sp` | `RIWAYAT_SP` | KB |
| `sanksi_sp` | `SANKSI_SP` | KB |
| `riwayat_kontrak` | `RIWAYAT_KONTRAK` | Kontrak |
| `riwayat_mutasi` | `RIWAYAT_MUTASI` | Mutasi |
| `profil_keluarga` | `PROFIL_KELUARGA` | LTA |
| `statistik_pegawai` | `STATISTIK_PEGAWAI` | Statistik (pendidikan2) |
| `level` | `LEVEL` | SO |

## MySQL-Specific SQL Functions → JOOQ

| MySQL | JOOQ equivalent |
|-------|-----------------|
| `TIMESTAMPDIFF(YEAR, a, b)` | `DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", Integer.class, a, b)` |
| `TIMESTAMPDIFF(MONTH, a, b)` | `DSL.field("TIMESTAMPDIFF(MONTH, {0}, {1})", Integer.class, a, b)` |
| `CONCAT_WS(' ', a, b)` | `DSL.field("CONCAT_WS(' ', {0}, {1})", String.class, a, b)` |
| `DATE_FORMAT(d, '%d.%m.%Y')` | `DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, d)` |
| `IF(cond, a, b)` | `DSL.field("IF({0}, {1}, {2})", type, cond, a, b)` |
| `IFNULL(a, b)` | `DSL.coalesce(a, b)` |
| `CURDATE()` | `DSL.currentDate()` |
| `NOW()` | `DSL.currentLocalDateTime()` |

## DTO Response Pattern

Mengikuti existing project pattern — `CustomResult.any(data)`:

```java
// Controller
@GetMapping()
public ResponseEntity<SingleResult<List<DukResponse>>> lapDuk() {
    return CustomResult.any(service.fetch());
}

// Service
@Service @Transactional(readOnly = true)
@RequiredArgsConstructor
public class DukService {
    private final DukRepository repository;
    public List<DukResponse> fetch() {
        return repository.fetch();
    }
}

// Repository
@Repository @RequiredArgsConstructor
public class DukRepository {
    private final DSLContext dsl;
    public List<DukResponse> fetch() {
        return dsl.select(...).from(...).where(...).fetch(DukRecordMapper::map);
    }
}
```

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| JOOQ TIMESTAMPDIFF syntax | Query won't compile | Use `DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", ...)` |
| `is_latest` is TINYINT(1) not boolean | Wrong filter | Cast: `PENDIDIKAN.IS_LATEST.eq((byte) 1)` |
| Excel template format mismatch | Output differs from Python | Test with same data, compare visually |
| Dynamic WHERE in Kontrak/KB | Hard to read JOOQ | Use `Condition` builder pattern (already done in `PegawaiQueryRepository`) |
| `profil_keluarga.tanggungan` is byte not boolean | Wrong display | Map in record mapper |

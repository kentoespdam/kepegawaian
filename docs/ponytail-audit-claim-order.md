# Ponytail Audit — Claim Order

> Generated: 2026-08-20
> Scope: Whole-repo over-engineering audit vs CODING_RULES.md
> Status: **ALL COMPLETE** (verified 2026-08-24, build green)

## Summary

| Category | Items | Lines Saved | Files |
|----------|-------|-------------|-------|
| Dead code | 5 classes (4 + AuditRevisionEntity) | -~100 lines | -5 files |
| Redundant patterns | 1 annotation, 1 interface, 1 Serializable | -~100 lines | -2 files |
| Duplicate DTOs | 4 Query/ListResponse pairs | -~20 lines | -4 files |
| Mutable configs | 3 ConfigurationProperties | -~30 lines | -3 files |
| YAML cleanup | 15 dead keys | -~30 lines | -1 file |
| Config compliance | open-in-view, @EnableWebMvc | -~5 lines | -1 file |
| Documented (later) | Redis config, UrlBuilder | -~50 lines | -2 files |
| OpenAPI docs | @Tag + @Operation (0/76, 0/407) | +docs | 0 files |
| **Total** | **20 items** | **-~335 lines** | **-18 files** |

---

## Issue 1: `kepegawaian-80p1` — Dead Code & Redundant Patterns ✅

### Claim Order

- [x] **Step 1:** Hapus 5 dead classes
  - `utils/DetailFromList.java` — zero callers
  - `config/DefConfig.java` — zero imports
  - `config/audit/AuditRevisionListener.java` — empty class, commented body
  - `entities/commons/AuditRevisionEntity.java` — fully commented-out, zero references
  - `dto/commons/ErrorCode.java` — zero callers
  - → `./gradlew clean compileJava`

- [x] **Step 2:** Hapus `@EnableWebMvc` dari `OpenApiConfig`
  - `config/OpenApiConfig.java` — remove annotation + import
  - → `./gradlew clean compileJava`

- [x] **Step 3:** Collapse `MimeTypesUtils` interface + impl
  - Delete `utils/MimeTypesUtils.java` (interface)
  - Rename `utils/MimeTypesUtilsImpl.java` → `utils/MimeTypesUtils.java`
  - Add `final` to class, keep `@Component`
  - Update `BiodataController` import if needed
  - → `./gradlew clean compileJava`

- [x] **Step 4:** Hapus `Serializable` dari `UploadResultUtil`
  - Remove `implements Serializable` + import
  - → `./gradlew clean compileJava`

- [x] **Step 5:** Merge 4 identical Query/ListResponse pairs
  - Delete `GolonganListResponse.java` — use `GolonganQuery` in `GolonganQueryService.listQuery()`
  - Delete `JenisKeahlianListResponse.java` — use `JenisKeahlianQuery`
  - Delete `JenisKitasListResponse.java` — use `JenisKitasQuery`
  - Delete `JenisPelatihanListResponse.java` — use `JenisPelatihanQuery`
  - Update query service return types + controller response types
  - → `./gradlew clean compileJava`

- [x] **Step 6:** `./gradlew test` — all green

- [x] **Step 7:** Update graph + commit

---

## Issue 2: `kepegawaian-0ejq` — ConfigurationProperties → Immutable Record ✅

### Claim Order

- [x] **Step 1:** Konvert `AppwriteProperties` → record
  - `config/AppwriteProperties.java` → `public record AppwriteProperties(String endpoint, String projectId, String apiKey) {}`
  - Add `@EnableConfigurationProperties(AppwriteProperties.class)` on main app class or config
  - → `./gradlew clean compileJava`

- [x] **Step 2:** Update `AppwriteClientTest`
  - `new AppwriteProperties()` + setters → `new AppwriteProperties(ENDPOINT, PROJECT_ID, API_KEY)`
  - → `./gradlew clean compileJava`

- [x] **Step 3:** Konvert `CutiProperties` → record
  - 9 fields → record components
  - → `./gradlew clean compileJava`

- [x] **Step 4:** Konvert `PegawaiProperties` → record
  - 2 fields → record components
  - → `./gradlew clean compileJava`

- [x] **Step 5:** `./gradlew test` — all green

- [x] **Step 6:** Update graph + commit

---

## Issue 3: `kepegawaian-ofd5` — Redis Cache Config + UrlBuilder Refactor ✅

### Claim Order

#### Part A: Redis Cache Config
- [x] **Step A1:** Hapus `RedisConfig.java`
  - → `./gradlew clean compileJava`

- [x] **Step A2:** Tambah `spring.cache` di `application.yml`
  ```yaml
  spring:
    cache:
      type: redis
      redis:
        time-to-live: ${REDIS_CACHE_TTL:600000}
        cache-null-values: false
        key-prefix: "kepegawaian:"
        use-key-prefix: true
  ```

- [x] **Step A3:** Tambah `CacheConfig` bean
  - `@Configuration @EnableCaching`
  - `RedisCacheManagerBuilderCustomizer` — per-cache-name TTL
  - `CacheErrorHandler` — Redis down → degrade to DB

- [x] **Step A4:** Tambah `REDIS_CACHE_TTL` ke `env.example`

#### Part B: UrlBuilder Refactor
- [x] **Step B1:** Refactor `LaporanStatistikController` (9 call sites)
  - `UrlBuilder.build(BASE_PATH, "/endpoint")` → `UriComponentsBuilder.fromPath(BASE_PATH).path("/endpoint").toUriString()`
  - `UrlBuilder.build(BASE_PATH, "/endpoint?tahun=X&bulan=Y")` → `.queryParam("tahun", tahun).queryParam("bulan", bulan)`
  - → `./gradlew clean compileJava`

- [x] **Step B2:** Refactor `LaporanKontrakController` (2 call sites)
  - `UrlBuilder.buildFilter(BASE_PATH, "/endpoint", filter)` → `.queryParam("filter", filter)`
  - → `./gradlew clean compileJava`

- [x] **Step B3:** Refactor `LaporanKenaikanBerkalaController` (3 call sites)
  - `UrlBuilder.build(BASE_PATH, "/endpoint", request)` → explicit `queryParam()` calls
  - → `./gradlew clean compileJava`

- [x] **Step B4:** Refactor `LaporanDukController` (2 call sites)
  - → `./gradlew clean compileJava`

- [x] **Step B5:** Refactor `LaporanDnpController` (2 call sites)
  - → `./gradlew clean compileJava`

- [x] **Step B6:** Refactor `LaporanLtaController` (3 call sites)
  - → `./gradlew clean compileJava`

- [x] **Step B7:** Hapus `helpers/UrlBuilder.java`
  - → `./gradlew clean compileJava`

- [x] **Step B8:** `./gradlew test` — all green

- [x] **Step 9:** Update graph + commit

---

## Issue 4: `kepegawaian-k8cg` — SpringDoc OpenAPI @Tag + @Operation ✅

### Claim Order

#### Domain: master (22 controllers)
- [x] **Step 1:** Tambah `@Tag` ke semua master controllers
- [x] **Step 2:** Tambah `@Operation(summary = "...")` ke semua master methods (~80)
- [x] **Step 3:** `./gradlew clean compileJava`

#### Domain: profil (17 controllers)
- [x] **Step 4:** Tambah `@Tag` ke semua profil controllers
- [x] **Step 5:** Tambah `@Operation` ke semua profil methods (~70)
- [x] **Step 6:** `./gradlew clean compileJava`

#### Domain: penggajian (12 controllers)
- [x] **Step 7:** Tambah `@Tag` ke semua penggajian controllers
- [x] **Step 8:** Tambah `@Operation` ke semua penggajian methods (~60)
- [x] **Step 9:** `./gradlew clean compileJava`

#### Domain: kepegawaian (6 controllers)
- [x] **Step 10:** Tambah `@Tag` ke semua kepegawaian controllers
- [x] **Step 11:** Tambah `@Operation` ke semua kepegawaian methods (~40)
- [x] **Step 12:** `./gradlew clean compileJava`

#### Domain: cuti (4 controllers)
- [x] **Step 13:** Tambah `@Tag` ke semua cuti controllers
- [x] **Step 14:** Tambah `@Operation` ke semua cuti methods (~30)
- [x] **Step 15:** `./gradlew clean compileJava`

#### Domain: laporan (8 controllers)
- [x] **Step 16:** Tambah `@Tag` ke semua laporan controllers
- [x] **Step 17:** Tambah `@Operation` ke semua laporan methods (~40)
- [x] **Step 18:** `./gradlew clean compileJava`

#### Domain: auth + system + pegawai (6 controllers)
- [x] **Step 19:** Tambah `@Tag` ke semua auth/system/pegawai controllers
- [x] **Step 20:** Tambah `@Operation` ke semua methods (~30)
- [x] **Step 21:** `./gradlew clean compileJava`

- [x] **Step 22:** `./gradlew test` — all green
- [x] **Step 23:** Update graph + commit

---

## Issue 5: `kepegawaian-iuro` — Spring Config Cleanup ✅

### Claim Order

- [x] **Step 1:** Tambah `spring.jpa.open-in-view: false` ke `application.yml`
  - → `./gradlew clean compileJava`

- [x] **Step 2:** Bersihkan `custom.*` YAML — hapus 15 dead keys
  - **Keep:** `custom.cors.allowed-origins`, `custom.jabatan.supervisorSdm`
  - **Hapus:** `custom.protected.*`, `custom.jenisCuti.*`, `custom.jabatan.direkturUtama/direkturTeknik/direkturUmum/managerSdm`, `custom.levelJabatan.*` (6 keys), `custom.security.dev.*`
  - → `./gradlew clean compileJava`

- [x] **Step 3:** Hapus `entities/commons/AuditRevisionEntity.java`
  - Fully commented-out, zero references (bersama AuditRevisionListener di Issue 1)
  - → `./gradlew clean compileJava`

- [x] **Step 4:** `./gradlew test` — all green
- [x] **Step 5:** Update graph + commit

---

## Noted (Not Over-Engineering)

| Item | Verdict | Reason |
|------|---------|--------|
| 31 empty PutRequest classes | **KEEP** | Intentional separation for future divergence |
| `RandomStringHelper` | **KEEP** | Used by `FileUploadUtilImpl` (4 callers) |
| `SpecificationBuilder` (232 lines) | **KEEP** | Well-used (89 callers) |
| `CutiKuotaDeductionAllocator` | **KEEP** | Domain concept, self-documenting |
| `DateHelper.generateDate()` | **KEEP** | Used by cuti handlers, low value to inline |
| `SavedStatus @Enumerated` | **COSMETIC** | Harmless on POJO, not worth a separate issue |
| `PageResult` extend ResultAbstract | **SKIP** | ~20 lines duplikasi, risk JSON contract change (tambah `errors`/`message` fields) |

## Dependencies Between Issues

```
Issue 1 (dead code) ──→ Issue 5 (AuditRevisionEntity + YAML cleanup)
Issue 2 (ConfigProperties) ──→ Issue 5 (custom.* YAML cleanup, shared keys)
Issue 3 (Redis + UrlBuilder) — independent
Issue 4 (OpenAPI docs) — independent
```

Recommended execution order: Issue 1 → Issue 2 → Issue 5 → Issue 3 → Issue 4

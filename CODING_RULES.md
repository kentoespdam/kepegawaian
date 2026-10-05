# CODING_RULES

## Workflow

1. **Understand** — Read issue. Clarify ambiguities.
2. **Plan** — Identify files, approach, edge cases. `EnterPlanMode` for nontrivial tasks.
3. **Explore** — `gitnexus_impact` / `gitnexus_context` / `gitnexus_query` before editing. Context7 for library docs.
4. **Write** — Target 150–250 LOC per file. Hard ceiling 300 LOC. Split if exceeded. Follow conventions.
5. **Test** — Unit tests required for new logic.
6. **Scope** — Out-of-scope errors → file new issue. Do not resolve ad-hoc.
7. **Finish** — Zero errors. Run quality gates (tests, linters, builds).
8. **Ship** — Commit: `<type>: <description>`.  
   `git pull --rebase` → `bd dolt push` → `git push` → verify "up to date with origin".

Sequence: Understand → Plan → Explore → Write → Test → Close issue → Ship

## Git mv + Edit Workflow (HARD INVARIANT)

When moving files with `git mv` and then editing their headers, **the Edit tool writes to the working tree, NOT the index**. If you `git add` before the Edit lands (e.g. in a parallel block), the commit captures the OLD header. This pitfall hit 3x in ADR-0017 wave-1 (tn1, 0fs, yz9) — each time requiring a `fix()` follow-up commit.

**Recipe (mandatory):**

1. `git mv old new` — rename-preserving move
2. `mkdir -p` destination first (git mv does NOT auto-create folders)
3. Read new path (Edit tool refuses on post-mv path until read)
4. Edit header on new path
5. Edit ALL importers (no parallel Edit+Add blocks)
6. **Single `git add` batch at the end** — every modified file in one command
7. Verify with `git diff --cached` — moved files MUST show content lines, not 0
8. **Post-commit sanity:** `./gradlew clean compileJava` — if that fails, the commit captured stale content; make a `fix()` commit (never amend)

**Anti-patterns (forbidden):**
- Parallel `git add` + Edit blocks (race condition: Edit may land after Add snapshot)
- `git add` per-file between Edits (defeats the "single batch" guarantee)
- Amending the broken commit instead of a new `fix()` commit (project policy: never amend)
- Trusting `compileJava UP-TO-DATE` (Gradle content-hash cache can mask missing content)

---

## Architecture: CQRS-lite

```
Controller (Query/Command)
    ├── *QueryService (@Transactional(readOnly = true))
    │       └── *QueryRepository (jOOQ DSLContext → Java record DTOs)
    └── *CommandService (@Transactional)
            └── *Repository (Spring Data JPA → Entity lifecycle + Envers audit)
```

**Use JPA when:** CRUD by ID, domain entities with invariants, `@Version` optimistic lock, Envers audit, cascade saves.  
**Use jOOQ when:** complex joins/subqueries, `multiset()` nested DTOs, dynamic search, bulk ops, window functions/CTEs, large exports.

---

## Spring Boot 4 / Java 25

| Rule | Description |
|------|-------------|
| **Jakarta EE 11** | All imports `jakarta.*`. Zero `javax.*` allowed. |
| **Jackson 2 (DTO & Entity)** | Pakai `com.fasterxml.jackson.*` untuk semua DTO, Entity, dan anotasi (`@JsonProperty`, `@JsonIgnore`, dll). |
| **Jackson 3 (`tools.jackson`)** | Hanya untuk handler infrastruktur (e.g., `JwtAuthEntryPoint`). JANGAN gunakan di DTO/Entity layer. |
| **@MockitoBean** | `@MockBean`/`@SpyBean` removed. Use `org.springframework.test.context.bean.override.mockito.MockitoBean`. |
| **@ServiceConnection** | Prefer over `@DynamicPropertySource` for Testcontainers. |
| **Lambda DSL** | All `HttpSecurity` config MUST use lambda DSL. `.and()` chaining removed. |
| **Virtual Threads** | Enabled via `spring.threads.virtual.enabled=true` for I/O-heavy workloads (Java 21+). |
| **@ConfigurationProperties** | Use immutable Java `record` with `@Validated`. No `@ConstructorBinding` on single-constructor records. |
| **Property keys** | Always kebab-case in `application.yml` (e.g., `app.security.token-expiration-seconds`). |
| **Actuator** | NEVER `exposure.include=*`. Enumerate endpoints explicitly. Isolate on management port. |

---

## Entity Design

### Lombok on Entities (MANDATORY)

```java
@Entity
@Table(name = "ref_profesi")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA
@AllArgsConstructor
public class Profesi extends MasterBaseEntity { ... }
```

| ✅ Use | ❌ FORBIDDEN on `@Entity` |
|--------|--------------------------|
| `@Getter`, `@Setter` | `@Data` (breaks entity identity) |
| `@NoArgsConstructor(access = PROTECTED)` | `@EqualsAndHashCode` (default — includes all fields) |
| `@AllArgsConstructor` | `@ToString` (unscoped — triggers lazy loads) |
| `@Builder` / `@SuperBuilder` | `@Value` (makes entity `final`) |
| `@Builder.Default` for field defaults | |

### equals/hashCode

Use proxy-safe pattern from `IdsAbstract` — compare by ID only, handle `HibernateProxy`. NEVER include collections or generated IDs in `hashCode()`.

### Associations

| Rule | Detail |
|------|--------|
| **Default LAZY** | ALL associations `FetchType.LAZY`. Override `@ManyToOne`/`@OneToOne` explicitly (JPA defaults to EAGER). |
| **@ToString.Exclude** | MUST exclude all `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne(LAZY)` from toString. |
| **@JsonBackReference** | On `@ManyToOne` to prevent infinite recursion. |
| **Collections** | `List<T>` for `@OneToMany` + `@ManyToOne` back-ref. `Set<T>` for `@ManyToMany`. |
| **No multi-bag fetch** | Never `JOIN FETCH` multiple `List` collections in one JPQL query. |
| **Sync helpers** | Bidirectional relations MUST have `addChild()`/`removeChild()` on parent. |

### Enums

Persist with `@Enumerated(EnumType.ORDINAL)` (project convention).

### Soft Delete

```java
@SQLDelete(sql = "UPDATE <table> SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
```

- Index `(is_deleted, <query_column>)` on frequently queried tables.
- On create, check for soft-deleted duplicate → revive instead of duplicate error.
- To query soft-deleted records (admin restore), use native SQL.

### Envers Audit

- `@Audited` on operational entities (`Pegawai`, `RiwayatSk`, `CutiPegawai`).
- `@Audited(targetAuditMode = NOT_AUDITED)` for relations to unaudited entities.
- Master entities: Envers excluded (ADR-0010/0003).
- Bulk `@Modifying` updates bypass Envers — use entity lifecycle when audit trail required.
- Configure `ValidityAuditStrategy` (stores `REVEND` — avoids expensive sub-selects).

---

## Repository Conventions

### Spring Data JPA Repository

```java
public interface PegawaiRepository extends
        JpaRepository<Pegawai, Long>,
        JpaSpecificationExecutor<Pegawai>,
        RevisionRepository<Pegawai, Long, Integer> { }
```

| Rule | Detail |
|------|--------|
| **Derived methods** | Max 3 parameters. Beyond that → `@Query` or `Specification`. |
| **@Query** | Prefer JPQL. Use named params (`@Param`). Use `JOIN FETCH` to eliminate N+1. |
| **@Modifying** | Always `@Modifying(clearAutomatically = true, flushAutomatically = true)`. |
| **Dynamic filters** | Use `JpaSpecificationExecutor<T>` + `SpecificationBuilder`. No string concat. |

### jOOQ Query Repository

| Rule | Detail |
|------|--------|
| **Naming** | `*QueryRepository` (e.g., `PegawaiQueryRepository`). |
| **Dynamic WHERE** | Start with `DSL.noCondition()`, chain `.and()`/`.or()`. |
| **Nested DTOs** | Use `DSL.multiset()` + `Records.mapping(Dto::new)`. |
| **No `select(*)`** | Explicitly list columns matching DTO constructor. Compile-time safety. |
| **Type-safe mapping** | Use `Records.mapping(Dto::new)`. NEVER `record.into(Class)` reflection mapping. |
| **Pagination** | Whitelist sort fields. Return `PageImpl<>(data, pageable, total)`. |
| **JPA→jOOQ flush** | If JPA writes then jOOQ reads in same `@Transactional`, call `entityManager.flush()` first. |
| **No jOOQ transactions** | Use Spring `@Transactional`, NOT `dsl.transaction(tx -> ...)`. |
| **Large exports** | Use `fetchStream()`/`fetchLazy()` with `fetchSize(1000)` + try-with-resources. Never `fetch()` on unbounded result sets. |
| **Batch ops** | Use `dsl.batchInsert()` / `dsl.batch()`. Chunk 500–2000 items. |

---

## Transaction Management

| Rule | Detail |
|------|--------|
| **Service-only** | `@Transactional` on Service layer ONLY. Never on controllers or repositories. |
| **Class-level readOnly** | `@Transactional(readOnly = true)` on QueryService class. Override per-method for writes. |
| **rollbackFor** | `@Transactional(rollbackFor = Exception.class)` on mutating methods with checked exceptions. |
| **No self-invocation** | Calling `@Transactional` method within same class bypasses AOP proxy. Extract to separate bean. |
| **Keep short** | NEVER do HTTP calls, file I/O, email dispatch inside `@Transactional` boundary. |
| **OSIV disabled** | `spring.jpa.open-in-view: false`. Surface lazy-loading bugs early. |

---

## DTO & Mapper Conventions

### DTO Naming

| Suffix | Purpose | Example |
|--------|---------|---------|
| `*IndexQuery` / `*Request` | Paginated search (extends `PagedRequest`) | `PegawaiIndexQuery` |
| `*PostRequest` | Create payload | `GolonganPostRequest` |
| `*PutRequest` | Update payload | `GolonganPutRequest` |
| `*Patch*` | Partial update | `PegawaiPatchGaji` |
| `*Batch*Request` | Batch operation | `PegawaiBatchCreateRequest` |
| `*MiniResponse` | Minimal record (id + display) | `OrganisasiMiniResponse` |
| `*TableResponse` / `*ListResponse` | Table/list view record | `PegawaiTableResponse` |
| `*Response` / `*ResponseDetail` | Full response record | `PegawaiResponseDetail` |

### Rules

- **Java records** for all read projections. Immutable.
- **Never return `@Entity` from controllers.** Map to DTO in service layer.
- Date fields: `@JsonSerialize(using = LocalDateSerializer.class)` + `@JsonFormat(pattern = "yyyy-MM-dd")`.
- `@Schema` annotations on DTOs only, NEVER on `@Entity` classes.
- Validation groups for conditional validation (e.g., `groups = PegawaiTetap.class`).

### Mappers

- **Command mappers** (`*Mapper`): `final class` + private constructor + static methods `toEntity()` / `updateEntity()`.
- **jOOQ mappers** (`*JooqMapper`): Convert `org.jooq.Record` → DTO records. Extract aliased fields, map ordinals to enums.
- **Shared mappers** (`SharedMappers`): Reusable static functions for cross-domain mini-responses.

---

## Controller Conventions

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/<domain>/<feature>")
@Tag(name = "Feature Name", description = "...")
public class FeatureController {
    private final FeatureQueryService query;
    private final FeatureCommandService command;
}
```

### Response Envelopes

| Method | Return |
|--------|--------|
| `CustomResult.any(data)` | Single entity |
| `CustomResult.optional(optional)` | Optional entity |
| `CustomResult.list(list)` | List |
| `CustomResult.page(page)` | Paginated |
| `CustomResult.save(SavedStatus.build(...))` | Create/Update |
| `CustomResult.delete(boolean)` | Delete |

### Security

- `@PreAuthorize("hasRole('ADMIN') or hasAuthority('DOMAIN:ACTION')")` on mutating endpoints.
- Ownership verification → **service layer** via `OwnershipGuard` (`@Component`), bukan SpEL inline. Lempar `NotFoundException` (404, bukan 403) untuk mencegah information leakage. Blokir akun DEV dari self-service endpoints.
- NEVER `allowedOrigins("*")` with `allowCredentials(true)`.

### Validation

- `@Valid` on `@RequestBody` and `@ParameterObject` parameters.
- Paged queries: `@ParameterObject @Valid <Entity>IndexQuery request`.

### SpringDoc OpenAPI

- `@Tag` on controller class. `@Operation(summary = "...")` on methods.
- `@Schema` on DTO fields only.
- Group APIs via `GroupedOpenApi` beans in `OpenApiConfig`.
- Disable Swagger UI in production (`springdoc.swagger-ui.enabled=false`).

---

## Flyway Migrations

| Rule | Detail |
|------|--------|
| **Naming** | `V<YYYYMMDDHHmmss>__<snake_case_description>.sql` (timestamp prevents branch collisions). |
| **Separator** | Exactly two underscores (`__`) between version and description. |
| **Repeatable** | `R__<description>.sql` for views, stored procedures, static lookup data only. |
| **Atomic** | ONE logical change per migration file. ONE DDL statement for critical operations (MariaDB has no transactional DDL). |
| **Separate DDL/DML** | Never mix structural changes and data manipulation in same file. |
| **Additive only** | Use expand/contract pattern. Never direct `RENAME COLUMN` (breaks running N-1 pods). |
| **NOT NULL** | Add column as `NULL` → backfill → add `NOT NULL` in subsequent migration. |
| **Immutable** | Once merged/applied, NEVER modify or delete a migration file. Fix-forward with new `V*` file. |
| **MariaDB DDL** | Specify `ALGORITHM=INSTANT` or `ALGORITHM=INPLACE, LOCK=NONE` for large tables. |
| **Encoding** | `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci` on new tables. |
| **Out-of-order** | Enabled in dev/staging (`spring.flyway.out-of-order: true`). |
| **Production** | `spring.jpa.hibernate.ddl-auto: validate` (or `none`). Never `update`/`create`. |

---

## Redis

| Rule | Detail |
|------|--------|
| **No JDK serialization** | NEVER use `JdkSerializationRedisSerializer`. Use `StringRedisSerializer` (keys) + `GenericJackson2JsonRedisSerializer` (values). |
| **Key format** | `kepegawaian:<domain>:<identifier>` (colon-separated hierarchy). |
| **Mandatory TTL** | Every key MUST have a TTL. Configure per-cache TTL in `RedisCacheManagerBuilderCustomizer`. |
| **Cache proxy** | Never call `@Cacheable` from within the same class (bypasses AOP proxy). |
| **Explicit keys** | Always specify `key` SpEL in `@Cacheable`/`@CacheEvict`. Never rely on default key generation. |
| **Error handler** | Configure `CacheErrorHandler` — Redis down degrades to DB, not 500 error. |
| **Connection pool** | Add `commons-pool2`. Configure Lettuce pool (`max-active`, `max-idle`, `min-idle`) and timeouts. |

---

## Kafka _(Planned — Not Yet Active)_

> [!NOTE] Kafka belum diimplementasikan di codebase saat ini. Aturan ini berlaku saat integrasi aktif.

| Rule | Detail |
|------|--------|
| **Topic naming** | `<env>.<domain>.<entity>.<event-type>.<version>` (e.g., `prod.kepegawaian.pegawai.created.v1`). Lowercase, dot-separated. |
| **Producer** | `acks: all`, `enable.idempotence: true`. Compress with `snappy`/`zstd`. |
| **After-commit publish** | Use `TransactionSynchronizationManager.registerSynchronization(afterCommit(...))`. Never publish inside uncommitted transaction. |
| **Consumer group** | Format: `<service>.<domain>-consumer-group` (e.g., `kepegawaian.pegawai-sync-group`). |
| **No auto-commit** | `enable-auto-commit: false`. Use `AckMode.MANUAL_IMMEDIATE` or `RECORD`. |
| **Error handling** | Wrap deserializers with `ErrorHandlingDeserializer`. Route poison pills to DLT. |
| **Trusted packages** | Restrict `spring.json.trusted.packages` to app domain packages. Never `*`. |
| **Idempotency** | Deduplicate via Redis `SET key NX EX <ttl>` or DB unique constraint on `eventId`. |

---

## Apache POI (Excel)

| Rule | Detail |
|------|--------|
| **Streaming** | NEVER `XSSFWorkbook` for >1000 rows. Use `SXSSFWorkbook(100)`. |
| **Dispose** | MUST call `workbook.dispose()` in `finally` block (deletes temp files). |
| **Try-with-resources** | All `Workbook`, `InputStream`, `OutputStream`. |
| **Style limit** | Max 64k cell styles. Pre-create all styles once, reuse. NEVER create styles in loops. |
| **Web export** | Stream directly to `HttpServletResponse.getOutputStream()` or `StreamingResponseBody`. No intermediate `byte[]`. |

---

## Lombok (Non-Entity)

| Rule | Detail |
|------|--------|
| **@Slf4j** | Always. Never manual `LoggerFactory.getLogger(...)`. |
| **Parameterized logging** | Use `{}` placeholders. NEVER string concat (`+`). |
| **Exception logging** | Throwable as final arg without placeholder: `log.error("Failed: {}", id, ex)`. |
| **@Builder.Default** | MUST annotate fields with default values. |
| **@SuperBuilder** | On both parent and child when inheritance involved. |
| **@RequiredArgsConstructor** | On `@Service`, `@RestController`, `@Component` with `private final` deps. |

---

## Spring Security

| Rule | Detail |
|------|--------|
| **Filter chain** | Lambda DSL only. `JwtAuthFilter extends OncePerRequestFilter` before `UsernamePasswordAuthenticationFilter`. |
| **CSRF** | Disabled for stateless JWT REST API: `csrf(AbstractHttpConfigurer::disable)`. |
| **Session** | `SessionCreationPolicy.STATELESS`. |
| **Permission inflation** | ADR-0037: JWT roles → query `PrefRoleRepository` → inflate `SimpleGrantedAuthority("ENTITY:ACTION")`. |
| **Multi-profile** | `@Profile("!development")`: `JwtAuthFilter`. `@Profile("development")`: `DevAuthFilter` (hardcoded admin). |
| **Test auth** | `@WithMockUser(roles = {"ADMIN"})` for role tests. Custom `@WithSecurityContext` for JWT principal. Test 200, 401, 403 for every protected endpoint. |

---

## Testing

### Test Types

| Type | Suffix | Context | Speed |
|------|--------|---------|-------|
| Unit | `*Test` | No Spring context. Mockito only. | <50ms/test |
| Slice | `*ControllerTest` / `*RepositoryTest` | `@WebMvcTest` / `@DataJpaTest` | Fast |
| Integration | `*IT` / `*IntegrationTest` | `@SpringBootTest` + Testcontainers | Slow |

### Testcontainers

- Singleton pattern: shared container in abstract base class.
- `@ServiceConnection` on `@Container` beans (no `@DynamicPropertySource` boilerplate).
- `.withReuse(true)` for local dev speed.
- Test against real MariaDB, not H2.

### ArchUnit

- Enforce layered architecture (Controllers → Services → Repositories).
- Controllers must not return `@Entity` directly.
- `@Valid` required on all mutation `@RequestBody` params.
- Naming: `*Controller`, `*Service`/`*ServiceImpl`, `*Repository`.

### Mockito

- `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks`.
- `InOrder` verification for security-sensitive flows (ownership check before domain logic).

---

## Naming Conventions Summary

| Artifact | Convention | Example |
|----------|-----------|---------|
| Entity class | PascalCase, singular | `Pegawai`, `CutiPengajuan` |
| DB table | snake_case | `pegawai`, `cuti_pengajuan` |
| FK column | `<target>_id` | `jabatan_id`, `biodata_id` |
| Boolean column | `is_`/`has_` prefix | `is_deleted`, `is_active` |
| JPA Repository | `<Entity>Repository` | `PegawaiRepository` |
| jOOQ Repository | `<Entity>QueryRepository` | `PegawaiQueryRepository` |
| Audit table | `<table>_aud` | `pegawai_aud` |
| Command Service | `<Entity>CommandService` | `PegawaiCommandService` |
| Query Service | `<Entity>QueryService` | `PegawaiQueryService` |
| Controller | `<Feature>Controller` | `PegawaiController` |
| Command Mapper | `<Entity>Mapper` | `PegawaiMapper` |
| jOOQ Mapper | `<Entity>JooqMapper` | `PegawaiJooqMapper` |
| Redis key | `kepegawaian:<domain>:<id>` | `kepegawaian:pegawai:10042` |
| Kafka topic | `<env>.<domain>.<entity>.<event>.<ver>` | `prod.kepegawaian.pegawai.created.v1` |
| Kafka consumer group | `<svc>.<domain>-consumer-group` | `kepegawaian.pegawai-sync-group` |
| Migration file | `V<timestamp>__<desc>.sql` | `V20260820104500__create_employee_table.sql` |

---

## Base Entity Hierarchy

Dua base class tersedia di `entities.commons`:

| Base Class | Digunakan untuk | Fitur |
|-----------|-----------------|-------|
| `IdsAbstract` | Entitas transaksional/operasional (`Pegawai`, `CutiPengajuan`, dll) | `@Audited`, `@Version` (optimistic lock, init=1), `createdBy`/`createdAt`/`updatedBy`/`updatedAt`, soft-delete, proxy-safe `equals`/`hashCode` |
| `MasterBaseEntity` | Entitas referensi/master (`Golongan`, `Jabatan`, dll) | Tanpa `@Audited`, tanpa `@Version`, `@SQLRestriction("is_deleted = FALSE")`, minimal audit fields |

- Entitas dengan PK non-Long (e.g., `Biodata` ber-PK String `nik`) implementasikan audit & soft-delete **secara manual** tanpa inheritance.
- JANGAN mencampur base class: operasional → `IdsAbstract`, master → `MasterBaseEntity`.

---

## Exception Handling & Error Response

### Hierarki Exception

```
ApiException (abstract, memuat HttpStatus)
├── NotFoundException       → 404
├── BadRequestException     → 400
├── ConflictException       → 409
├── ForbiddenException      → 403
└── GajiFormulaException    → 400
```

- Lempar subclass spesifik. DILARANG membuat `RuntimeException` baru tanpa extend `ApiException`.
- `GlobalExceptionHandler` (`@RestControllerAdvice extends ResponseEntityExceptionHandler`) menangkap semua exception.

### Format Error Envelope

```json
{
  "status": 404,
  "statusText": "Not Found",
  "errors": ["field [nip] : already exists"],
  "message": "Data tidak ditemukan",
  "data": null,
  "timestamp": "2026-10-05T07:00:00Z"
}
```

- Security errors (`JwtAuthEntryPoint`, `DeniedHandler`) mengembalikan format `{ status, error, message, timestamp, path }` — ini pengecualian yang disengaja.
- DILARANG membocorkan stack trace di response body production.
- Aktifkan RFC 9457: `spring.mvc.problemdetails.enabled=true` (menghasilkan `Content-Type: application/problem+json`).

---

## Package Structure

```
src/main/java/id/co/<pkg>/
├── controllers/
│   └── <domain>/           # flat per-domain (e.g., master/GolonganController.java)
├── services/
│   └── <domain>/
│       └── <feature>/      # per-fitur (e.g., master/golongan/GolonganCommandService.java)
├── repositories/
│   └── <domain>/
│       ├── jpa/            # Spring Data JPA interfaces
│       └── jooq/           # jOOQ DSL query repos
├── dto/
│   └── <domain>/
│       └── <feature>/
├── mapper/
│   └── <domain>/
├── entities/
│   ├── commons/            # IdsAbstract, MasterBaseEntity
│   └── <domain>/
└── config/
```

- Repository wajib dipisah: `.jpa` untuk Spring Data JPA, `.jooq` untuk jOOQ DSLContext.
- Controllers bersifat flat per-domain (tidak ada sub-package feature di controller).


## PagedRequest Contract

```java
public abstract class PagedRequest {
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    // direction regex: (?i)asc|desc
    
    public Pageable getPageable() { ... } // returns Pageable dengan sort & limit safe
}
```

- Semua `*IndexQuery` WAJIB extend `PagedRequest`.
- `MAX_SIZE = 100` adalah batas keras — tidak boleh dioverride tanpa alasan eksplisit.

---

## File Upload & Multipart

| Rule | Detail |
|------|--------|
| **Base dir** | `System.getProperty("user.dir") + "/attachments/"` via `FileUploadUtil`. |
| **MIME validation** | Validasi tipe file via `MimeTypesUtils`. DILARANG mengandalkan extension saja. |
| **Response** | Bungkus hasil upload dalam `UploadResultUtil` DTO. |
| **Controller annotation** | `@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)` + `@ModelAttribute @Valid <Dto>`. |
| **Tomcat config** | `server.tomcat.max-part-count: 1000` (default 10 di SB4 terlalu kecil). `max-http-form-post-size: 100MB`. |
| **Try-with-resources** | Semua `InputStream`/`OutputStream` dari multipart wajib di-close. |

---

## Apache Fesod (Excel Read/Import)

Untuk **parsing/import** Excel batch, gunakan `org.apache.fesod:fesod-sheet` (bukan POI manual):

```java
@ExcelProperty(index = 0)
private String nip;

// Listener pattern:
FesodSheet.read(inputStream, MyRow.class, row -> {
    // process each row
});
```

| Rule | Detail |
|------|--------|
| **Import** | Gunakan `fesod-sheet` dengan anotasi `@ExcelProperty` + listener streaming. |
| **Export** | Tetap gunakan `SXSSFWorkbook(100)` (Apache POI streaming) untuk output Excel. |
| **DILARANG** | `XSSFWorkbook` untuk >1000 rows di import maupun export. |

---

## Spring Events & Async (Virtual Threads)

Pola async pasca-commit menggunakan Spring Events (bukan Kafka):

```java
// Publisher (di CommandService, setelah operasi transaksional)
private final ApplicationEventPublisher eventPublisher;

@Transactional
public void prosesGaji(GajiCommand cmd) {
    // ... logic ...
    eventPublisher.publishEvent(new GajiBatchEvent(this, cmd.getId()));
}

// Listener (thread terpisah, pasca-commit)
@Component
@RequiredArgsConstructor
public class GajiBatchRootEventListener {

    @Async("gajiProsesExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onGajiBatch(GajiBatchEvent event) {
        // dijalankan di Virtual Thread setelah commit
    }
}
```

```java
// ThreadPoolConfig
@Bean("gajiProsesExecutor")
public Executor gajiProsesExecutor() {
    return Executors.newVirtualThreadPerTaskExecutor();
}
```

| Rule | Detail |
|------|--------|
| **AFTER_COMMIT only** | Gunakan `TransactionPhase.AFTER_COMMIT`. JANGAN jalankan side-effect dalam transaksi aktif. |
| **Virtual Thread executor** | Gunakan `Executors.newVirtualThreadPerTaskExecutor()`. JANGAN buat thread pool konvensional untuk task I/O async. |
| **DILARANG** | Menyuntikkan `ApplicationEventPublisher` di controller. Publish event hanya dari service layer. |

---

## HTTP Client (RestClient)

Semua komunikasi HTTP keluar menggunakan `RestClient` (bukan `RestTemplate` — deprecated di Spring 7):

```java
@Bean
public RestClient appwriteRestClient(RestClient.Builder builder) {
    return builder
        .requestFactory(new JdkClientHttpRequestFactory())
        .baseUrl(appwriteConfig.getEndpoint())
        .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "identity")
        .build();
}
```

| Rule | Detail |
|------|--------|
| **`RestClient`** | WAJIB untuk semua HTTP call sinkronus baru. |
| **`@HttpExchange`** | Gunakan declarative interface via `HttpServiceProxyFactory` untuk client dengan banyak endpoint. |
| **DILARANG** | `RestTemplate` (deprecated Spring Framework 7 / Boot 4). |
| **HTTP/1.1 pin** | Explicit pin ke HTTP/1.1 dengan `JdkClientHttpRequestFactory` jika server target tidak support HTTP/2. |

---

## Java 25 Concurrency

### ScopedValue (menggantikan ThreadLocal)

```java
public static final ScopedValue<AppwriteUser> CURRENT_USER = ScopedValue.newInstance();

// Binding di filter/interceptor:
ScopedValue.runWhere(CURRENT_USER, user, () -> service.execute());

// Akses:
var user = CURRENT_USER.get();
```

| Rule | Detail |
|------|--------|
| **`ScopedValue`** | Gunakan untuk propagating context (User ID, Auth, Tenant ID) di Virtual Threads (JEP 506, Java 25 finalized). |
| **DILARANG `ThreadLocal`** | Di Virtual Threads, `ThreadLocal` menyebabkan memory footprint besar (jutaan carrier thread copies). |

### Structured Concurrency

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var fA = scope.fork(() -> serviceA.getData());
    var fB = scope.fork(() -> serviceB.getData());
    scope.join().throwIfFailed();
    return new Result(fA.get(), fB.get());
}
```

| Rule | Detail |
|------|--------|
| **`StructuredTaskScope`** | Gunakan untuk subtask paralel yang harus selesai bersama (JEP 505). Child tasks dibatalkan otomatis jika salah satu gagal. |
| **DILARANG** | `CompletableFuture` tak-terstruktur untuk task paralel yang saling bergantung. |

---

## Observability (Structured Logging & Tracing)

### Structured Logging

```yaml
# application.yml
logging:
  structured:
    format:
      console: logstash   # atau 'ecs'
```

- JANGAN pakai `logstash-logback-encoder` dependency terpisah — Spring Boot 3.4+ menyediakan native.
- `micrometer-tracing-bridge-otel` otomatis menginjeksi `traceId` dan `spanId` ke JSON log output.

### Trace Propagation ke Async

```java
@Bean
public TaskDecorator mdcTaskDecorator() {
    return runnable -> {
        var context = MDC.getCopyOfContextMap();
        return () -> {
            if (context != null) MDC.setContextMap(context);
            try { runnable.run(); } finally { MDC.clear(); }
        };
    };
}
```

| Rule | Detail |
|------|--------|
| **Trace propagation** | Async executor WAJIB dikonfigurasi dengan `TaskDecorator` untuk membawa MDC context (traceId, spanId) ke worker thread. |
| **Correlation ID** | Inject `X-Correlation-ID` dari request header ke MDC di `OncePerRequestFilter`. |
| **DILARANG** | `System.out.println` atau `e.printStackTrace()`. Selalu `log.error("msg", ex)` via `@Slf4j`. |

---

## GraalVM Native Image Hints

```java
@RegisterReflectionForBinding({PegawaiResponse.class, PegawaiPostRequest.class})
@RestController
public class PegawaiController { ... }
```

| Rule | Detail |
|------|--------|
| **`@RegisterReflectionForBinding`** | Prioritaskan untuk Jackson 3 DTO binding di native image. Tempelkan di controller atau service yang menggunakan DTO tersebut. |
| **`RuntimeHintsRegistrar`** | Gunakan HANYA untuk dynamic proxy atau resource pattern eksternal (`hints.resources().registerPattern(...)`). Daftarkan via `@ImportRuntimeHints`. |
| **Pre-AOT test** | Uji kompatibilitas refleksi dengan flag `-Dspring.aot.enabled=true` sebelum build native. |
| **DILARANG** | Menggunakan `reflect-config.json` manual jika anotasi Spring bisa menanganinya. |

---

## jOOQ 3.20+ Additions

| Rule | Detail |
|------|--------|
| **Redacted columns** | Tandai kolom PII/sensitif (gaji, NIK) dengan konfigurasi redacted di jOOQ generator agar tersensor di SQL debug logs. |
| **`leftAntiJoin()`** | Gantikan pola `LEFT JOIN ... WHERE right.id IS NULL` dengan `.leftAntiJoin()` untuk keterbacaan dan optimasi query plan. |
| **Multiset JSON Array** | Pada `DSL.multiset()` one-to-many, prioritaskan mapping berbasis JSON Array (bukan JSON Object) untuk mengurangi alokasi memori. |

---

## Flyway 11+ Additions

| Rule | Detail |
|------|--------|
| **MariaDB module** | Tambahkan `org.flywaydb:flyway-database-mariadb` di Gradle bersama `flyway-core` (Flyway 11 memecah database drivers). |
| **DILARANG `clean-on-validation-error`** | Fitur deprecated/dihapus di Flyway 11. Jangan ada di konfigurasi mana pun. |

---

## Jakarta Validation 3.1 Additions

| Rule | Detail |
|------|--------|
| **Record component validation** | Letakkan anotasi constraint langsung di komponen record: `public record Req(@NotBlank String nip, @Positive BigDecimal gaji)`. Divalidasi otomatis saat `@Valid` binding. |
| **`@UUID` constraint** | Gunakan `@UUID` bawaan (Hibernate Validator 9) untuk validasi format UUID. DILARANG regex manual `@Pattern(regexp = "UUID_REGEX")`. |
| **Validation groups** | Gunakan `groups = {...}` untuk conditional validation (e.g., berbeda antara create dan update). |

---

## RedisHelper (One-Time Token)

```java
// Generate one-time download token (5 menit TTL)
String token = redisHelper.generateToken("download:laporan", userId);

// Verify & consume (one-time)
boolean valid = redisHelper.verifyAndConsumeToken(token);
```

| Rule | Detail |
|------|--------|
| **TTL 5 menit** | Default TTL untuk one-time token operasi idempoten/unduhan. |
| **Key pattern** | `kepegawaian:token:<purpose>:<userId>` |
| **One-time** | Token dihapus setelah satu verifikasi berhasil (idempotent protection). |

# Mail Service — Code Patterns (Verified Analysis)

> Scope: `src/main/java/id/perumdamts/mail/{controller,dto,entity,repository,service}`
> Method: every pattern below was re-read against source before being written here.
> Where a claim maps to a specific file, the path is cited. Divergences and
> confirmed bugs are called out explicitly rather than smoothed over — this is a
> map of what the code *actually does today* (HEAD `0396138`), not an idealized model.

---

## 0. How to read this document

The codebase has **two generations of code** living side by side:

- **Master modules** (`master/…`, e.g. DocumentType, Publication) — the older,
  most consistent layer. Treat this as the **canonical blueprint**.
- **Core modules** (`core/…`, e.g. Mail) — newer, written after the team's
  conventions had evolved. It deliberately diverges in a few places.

When the two disagree, both styles are documented so you can match whichever
module you are editing. Do **not** assume master style applies to core Mail.

---

## 1. CQRS-lite: Command / Query split

Every domain module is split into two services with distinct persistence
technologies and transaction semantics.

| Side    | Tech | Annotation | Role |
|---------|------|-----------|------|
| Command | JPA  | `@Service @Transactional @RequiredArgsConstructor` | writes (create/update/delete/toggle) |
| Query   | JOOQ | `@Service @Transactional(readOnly = true) @RequiredArgsConstructor` | reads (list/lookup/find) |

**Verified in:**
- `service/master/documentType/DocumentTypeCommandService.java` — `@Service @Transactional`, methods `create/update/delete/toggleStatus`, each write re-reads via `queryRepository.findById(...)` to return the JOOQ-projected Response DTO (not the JPA entity).
- `service/master/documentType/DocumentTypeQueryService.java` — `@Service @Transactional(readOnly = true)`, delegates list to the JOOQ repo; note `lookup()` is the one place a Query service uses the **JPA** repo (`findAllByStatusOrderByIdAsc`) instead of JOOQ.

**Key convention:** the Command service does not build its own response. After a
write it calls the **Query repository** (JOOQ) so the caller always gets the same
projected shape as a read. See `DocumentTypeCommandService.create():27` —
`return queryRepository.findById(saved.getId()).orElseThrow();`.

**Repository layout mirrors the split:**
- `repository/*/jpa/` — Spring Data JPA repos (write side)
- `repository/*/jooq/` — hand-written JOOQ repos (read side)

---

## 2. JOOQ read pattern — single-query pagination via window function

Read repositories avoid the classic "count query + data query" round-trip by
selecting `count().over()` as an extra column on every row.

**Verified in:**
- `repository/master/jooq/DocumentTypeQueryRepository.java` — selects `count().over().as("total_count")`, orders by `params.toSortField()`, `.limit(params.getSize()).offset(params.offset())`, then wraps into a Spring `PageImpl<>`.
- `repository/core/jooq/MailQueryRepository.java` — same window-function idea but names the column `totalCount`; total is pulled from the first row and fed into a custom `PagedResponse<>`.

**Canonical shape (master):**
```java
Condition cond = field("jd.status").ne(inline("DELETED"));
// ... build conditions ...
var rows = dsl.select(/* projected cols */, count().over().as("total_count"))
    .from("jenis_dokumen jd")
    // correlated subquery for publication_count
    .where(cond)
    .orderBy(params.toSortField())
    .limit(params.getSize())
    .offset(params.offset())
    .fetch();
long total = rows.isEmpty() ? 0 : rows.get(0).get("total_count", Long.class);
return new PageImpl<>(content, PageRequest.of(params.getPage(), params.getSize()), total);
```

**Safety conventions inside JOOQ repos:**
- Soft-delete filter uses `inline("DELETED")` (a bound literal), not string concat.
- Fulltext search uses a **parameterized** `condition(...)`, never interpolation:
  `condition("MATCH(m.m_subject, m.m_content) AGAINST (? IN BOOLEAN MODE)", keyword)`
  (`MailQueryRepository.findMailsInFolder`). This directly satisfies the B1 SQL-injection guideline.
- Sqid inputs are decoded to raw longs **before** entering a condition (see §3).

---

## 3. Sqid opaque external IDs

The system never exposes raw DB `Long` ids over HTTP. Every id is encoded to a
per-model, prefixed, shuffled Sqid string.

**The stack:**

| Piece | File | Role |
|-------|------|------|
| `SqidEntity` | `entity/SqidEntity.java` | entity contract: `Long getId()` |
| `HasSqid` | `dto/common/HasSqid.java` | DTO contract: `String getId()` |
| `SqidsEncoder` | `util/SqidsEncoder.java` | encode/decode, `@Component` |
| `SqidMapper<E>` | `dto/common/SqidMapper.java` | MapStruct base, `@Autowired SqidsEncoder`, `protected String sqid(E)` |

**Encoding rule** (`SqidsEncoder.encode`, line 18):
`consonantPrefix(modelClass) + separator + encodedId`.
- `consonantPrefix` = first ≤3 consonants of the class simple-name, lowercased (line 54).
- Alphabet is **shuffled per model** using seed `modelName + shuffleKey` (`alphabetForModel`, line 74) — so the same numeric id encodes differently across entities.

**Decoding is strict** (`SqidsEncoder.decode`, line 24) — throws `IllegalArgumentException` on:
1. wrong prefix (line 27),
2. too short (line 34),
3. undecodable (line 39),
4. **non-canonical** re-encode mismatch (line 47) — decode→re-encode must be byte-identical.

**Edge-decode convention:** controllers decode at the boundary and pass raw
`long` inward; services keep `long` signatures.
```java
long rawId = encoder.decode(DocumentType.class, id);   // controller edge
```
`IllegalArgumentException` from a bad Sqid is mapped centrally to HTTP 400 (§9),
so a malformed id becomes a clean 400, not a 500.

**Mapper convention** (`dto/master/documentType/DocumentTypeMapper.java`):
abstract class `extends SqidMapper<DocumentType>`, `@Mapper(componentModel = SPRING)`,
with `@Mapping(target = "id", expression = "java(sqid(entity))")`.

> **Future work:** ADR-010 (`docs/adr/010-opaque-external-id-typed-wrappers.md`)
> proposes replacing raw `String`/`long` at DTO boundaries with a typed
> `SqidId` sealed wrapper. Not yet implemented. Memory key: `sqid-seam-adr10`.

---

## 4. Pagination base classes

Custom (non-Spring) base types in `dto/common/` drive request binding and sorting.

- **`PageRequest`** (`dto/common/PageRequest.java`) — abstract. `DEFAULT_SIZE = 20`,
  `MAX_SIZE = 100`; clamps `page ≥ 0` and `size` into `(0, 100]`; `offset() = page * size`.
  Has setters so Jackson / `@ModelAttribute` can bind query params.
- **`PagedRequest extends PageRequest`** (`dto/common/PagedRequest.java`) — adds
  `sortBy` / `sortDir`; declares abstract `allowedSorts()` + `defaultSortColumn()`;
  `toSortField()` delegates to `SortParam.resolve(...)`.
- **`SortParam`** (`dto/common/SortParam.java`) — record. `resolve(sortBy, sortDir, allowedSorts, defaultColumn)`
  does a **whitelist** lookup (unknown `sortBy` → falls back to default column),
  returns a JOOQ `SortField` (default direction **desc**). This is the sort-injection guard.

**Per-module params** subclass `PagedRequest` and supply the whitelist:
`dto/master/documentType/DocumentTypeParams` defines an ALLOWED-sorts map and
`defaultSortColumn = "jd.jenis_dokumen"`.

### 4a. DIVERGENCE — two pagination response shapes

| Module | Returns | Built with |
|--------|---------|-----------|
| **master** (DocumentType, …) | Spring `Page<T>` → wrapped as `PagedModel<>` in controller | `new PageImpl<>(...)` |
| **core** (Mail) | custom `PagedResponse<T>` record | `PagedResponse.of(content, page, size, total)` |

- `dto/common/PagedResponse.java` — `record PagedResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last)` with `of(...)` / `empty(...)` factories.
- Master controller: `return new PagedModel<>(queryService.findAll(params));` (`controller/master/DocumentTypeController.java`).
- Core controller: search/report return `PagedResponse<>` directly (`controller/core/MailController.java`).

> Match the shape of the module you are in. Do not convert one to the other
> ad hoc. (Related: memory `cache-redis-serialization` — do **not** cache
> `Page<T>`; wrap in a plain record before caching.)

---

## 5. Soft delete

### 5a. Master convention (canonical)
- Entities carry a `RecordStatus` enum field and `@SQLRestriction("status <> 'DELETED'")`,
  so JPA reads transparently exclude deleted rows.
- Entities expose `markDeleted()`, `toggleStatus()`, `isActive()`.
  Command services call `entity.markDeleted()` then `save()` — no physical delete
  (`DocumentTypeCommandService.delete():53`).
- JOOQ reads replicate the filter manually: `field("...status").ne(inline("DELETED"))`.

### 5b. DIVERGENCE — core `Mail`
The core `Mail` entity does **not** use `RecordStatus`/`@SQLRestriction`. It uses
an `Integer status` mapped through the `MailStatus` enum (`DRAFT = 0`, `SENT = 1`),
and JOOQ reads filter explicitly, e.g. `field("m.m_status").eq(1)` for sent mail
(`MailQueryRepository.searchMails`). There is no automatic `@SQLRestriction`
exclusion on `Mail` — every query must state its status filter.

---

## 6. Numbering — Strategy pattern

Per-tenant mail-number formats are handled by a strategy family selected at runtime.

- Abstract base: `service/core/mail/numbering/AbstractMailNumberGenerator`.
- Concrete strategies: Default / BMS / SMD / BPN, each a `@Component extends AbstractMailNumberGenerator`.
- Selection: `MailNumberGeneratorDelegator` (`@Primary`, `@Order(1)`) picks the
  strategy whose `supports(clientCode)` matches the tenant.
- Race safety: sequence read uses `SELECT ... FOR UPDATE` inside the write transaction.

**Verified concrete example** (`service/core/mail/numbering/BmsMailNumberGenerator.java`):
```java
@Component
public class BmsMailNumberGenerator extends AbstractMailNumberGenerator {
    public boolean supports(String clientCode) { return "BMS".equalsIgnoreCase(clientCode); }
    protected String getOfficeCode()    { return "BMS"; }
    protected String getFormatRefCode() { return "BMS_MAIL_NUMBER_FORMAT"; }
}
```

Sequence scope is **per `(YEAR(m_created_date), m_category)`**, not global per-year
(verified against legacy data — memory `mail-seq-scope-verified`). See §10 for a
confirmed bug in how the sequence is computed.

---

## 7. Event-driven side effects

Heavy/async work is fanned out via `ApplicationEventPublisher` + async listeners,
keeping the core write transaction lean.

**Publisher** — the write service holds `ApplicationEventPublisher` and publishes
after the DB work but still inside `@Transactional`:
`service/core/mail/MailSendService.java:112` publishes `MailSentEvent` at the end
of `send()` (which performs 10 documented side-effects — validate recipients,
generate number, DRAFT→SENT via `mail.send(number)`, batch inbox creation via
`UserTask.inbox(...)` + `saveAll`, folder moves via `userTaskRepository.updateFolder`,
mark-parent-read on reply, then publish).

**Event** — an immutable record:
`event/MailSentEvent.java` = `record MailSentEvent(Long mailId, Long senderId, String senderName, List<Long> recipientUserIds, Instant sentAt)` with a convenience constructor defaulting `sentAt = Instant.now()`.

**Listeners** — `@Service` with `@TransactionalEventListener @Async` handlers, so
they run **after commit**, off the request thread:
`event/MailStatisticListener.java`:
```java
@TransactionalEventListener
@Async
public void onMailSent(MailSentEvent event) { ... }
```
Fan-out for `MailSentEvent`: statistics / notification / response-time listeners.
Other domain events follow the same shape: `ArchivePublishedEvent`,
`PublicationPublishedEvent`.

> `@TransactionalEventListener` (not plain `@EventListener`) matters: side effects
> only fire if the mail-send transaction actually commits.

---

## 8. Security & authorization

- **Auth chain:** AppWrite JWT → `AppWriteAuthFilter` → `MailPrincipal`
  (a record implementing `UserDetails`). Controllers inject the caller via
  `@AuthenticationPrincipal MailPrincipal principal` — verified across every write
  endpoint in `controller/core/MailController.java`. `MailSendService.send` reads
  `principal.userId()` / `principal.name()`.

### 8a. DIVERGENCE — `@PreAuthorize` is sparse (contradicts CLAUDE.md)
CLAUDE.md states `@PreAuthorize` is used "on controllers" broadly. **In the actual
code it is present on only ~3 places:**
- `AllowedFileTypeController` — method-level `hasRole('ADMIN')`
- `MailArchiveController` — method-level `hasRole('ADMIN')`
- `MailFolderController` — class-level `isAuthenticated()`

`MailController` itself has **no** `@PreAuthorize` (it relies on the authenticated
principal + filter chain). Treat authorization as **not uniformly enforced at the
annotation layer** — if you add a sensitive endpoint, add the guard explicitly;
don't assume it's inherited.

### 8b. Constructor-injection divergence
- Master controllers/services: Lombok `@RequiredArgsConstructor`.
- Core `MailController`: **explicit** hand-written constructor (no Lombok).
Both are acceptable in their respective modules; match the file you're editing.

---

## 9. Centralized exception handling

A single `@RestControllerAdvice` maps exceptions to a uniform `ApiError` body.

**Verified in** `controller/GlobalExceptionHandler.java`:

| Exception | HTTP status |
|-----------|-------------|
| `MethodArgumentNotValidException` | 400 (with per-field errors) |
| `IllegalArgumentException` | 400 |
| `EntityNotFoundException` | 404 |
| `IllegalStateException` | 409 CONFLICT |
| `UnauthorizedException` | 401 |
| `AccessDeniedException` | 403 |
| `Exception` (fallback) | 500 |

Response body is the `ApiError` DTO (with a nested `FieldError` for validation).
This is why services can throw plain `IllegalArgumentException` (bad input, e.g.
a malformed Sqid or duplicate name) or `IllegalStateException` (business conflict,
e.g. "mail already sent", "N publications still use this type") and get the right
status for free. Examples:
`DocumentTypeCommandService` throws `IllegalArgumentException` on duplicate name
and `IllegalStateException` when a type is still referenced;
`MailSendService.send` throws `IllegalStateException("Mail already sent")` and
`IllegalArgumentException("Cannot send mail without recipients")`.

---

## 10. Confirmed pre-existing bugs (do NOT fix without a beads issue)

Both were verified against source and cross-checked against existing memories.

1. **`MailMapper.toAuditDto` encodes with the wrong model class.**
   The audit DTO's `createdBy` field is encoded via `encoder.encode(Mail.class, entity.getCreatedBy())`
   — it should use the employee/user model class, not `Mail.class`. The Sqid
   prefix/alphabet are per-model, so this produces a value that decodes under the
   wrong type. Structural fix is folded into ADR-010 (memory `sqid-seam-adr10`).

2. **`AbstractMailNumberGenerator.getNextSequence` computes the sequence wrong.**
   It uses `COUNT(*)` instead of `MAX(parsed_seq)` (breaks if any row in the scope
   was deleted or numbering has gaps), and filters with a non-sargable
   `YEAR(m_created_date) = ?` instead of a `BETWEEN` date range (defeats the index).
   Correct approach: `MAX(parsed_seq)` over the `(year, category)` scope with a
   sargable `m_created_date BETWEEN ? AND ?`. Memory `mail-seq-scope-verified`.

---

## Appendix — Quick "which style do I use?" cheat sheet

| Concern | Master modules | Core Mail module |
|---------|---------------|------------------|
| Constructor | `@RequiredArgsConstructor` | explicit constructor |
| Pagination response | `Page` → `PagedModel<>` | `PagedResponse<>` record |
| Soft delete | `RecordStatus` + `@SQLRestriction` | `Integer status` + `MailStatus`, explicit filter |
| Read tech | JOOQ, `count().over().as("total_count")` | JOOQ, `count().over().as("totalCount")` |
| Write returns | JOOQ Response via `queryRepository.findById` | entity / mapped DTO |
| Authz annotation | mostly absent; a few `hasRole('ADMIN')` | none on `MailController` |

**Invariants that hold everywhere:**
- CQRS-lite: JPA writes, JOOQ reads.
- Sqid opaque ids; decode at controller edge; services take `long`.
- Parameterized JOOQ conditions only (no string interpolation of user input).
- Whitelisted sort columns via `SortParam`.
- Uniform errors via `GlobalExceptionHandler` → `ApiError`.
- Async side effects via `@TransactionalEventListener @Async`.

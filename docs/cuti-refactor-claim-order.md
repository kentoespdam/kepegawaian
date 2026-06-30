# Cuti Refactor — Claim Order & Monitoring

Epic **kepegawaian-y7u** — Enforce CODING_RULES §4 (max 120 lines) untuk modul cuti service/command classes. Basis: grilling decisions Q11-Q17 di `docs/context/decisions-cuti.md`.

Urutan klaim di bawah **mengikuti dependency**, bukan nomor issue. Kerjakan per **PHASE**: semua issue dalam satu phase boleh diklaim paralel (tidak saling blok); phase berikutnya baru terbuka setelah phase sebelumnya selesai. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca deskripsi epic di `bd show kepegawaian-y7u` dan WORKING AGREEMENT: gitnexus-first (`gitnexus_impact` sebelum edit symbol, `gitnexus_detect_changes` sebelum commit), **strict scope — JANGAN AI SLOP**, alur `claim → code → test → detect_changes → close → ship` (CODING_RULES.md).

---

## Dependency Graph

```
kepegawaian-y7u (Epic: 120-line enforcement)
├─ kepegawaian-sqf [PHASE 1] JOOQ mapper extraction (Q14-Q16)
├─ kepegawaian-scn [PHASE 1] SaveCutiService split (Q11) [FOUNDATIONAL]
│  └─ kepegawaian-hit [PHASE 2] PengajuanCutiCommand classifier (Q13)
├─ kepegawaian-39o [PHASE 1] Validator split (Q12)
├─ kepegawaian-rq2 [PHASE 2] KlaimCutiCommand settlement (Q12) [DEPENDS: 39o + scn]
└─ kepegawaian-llq [PHASE 3] ApprovalCutiCommand lifecycle (Q13)
```

---

## Claim Order (by Phase)

| Order | Issue ID | Title | State when you start | Claim cmd |
|-------|----------|-------|----------------------|-----------|
| **PHASE 1 — Foundational (parallel OK)** |||||
| 1a | kepegawaian-scn | SaveCutiService split (Q11) | READY | `bd update kepegawaian-scn --claim` |
| 1b | kepegawaian-sqf | JOOQ mapper extraction (Q14-Q16) | READY | `bd update kepegawaian-sqf --claim` |
| 1c | kepegawaian-39o | Validator split (Q12) | READY | `bd update kepegawaian-39o --claim` |
| **PHASE 2 — Dependent (after Phase 1)** |||||
| 2a | kepegawaian-hit | PengajuanCutiCommand classifier (Q13) | BLOCKED → READY after scn closes | `bd update kepegawaian-hit --claim` |
| 2b | kepegawaian-rq2 | KlaimCutiCommand settlement (Q12) | BLOCKED → READY after scn + 39o close | `bd update kepegawaian-rq2 --claim` |
| **PHASE 3 — Cleanup (anytime after Phase 1)** |||||
| 3 | kepegawaian-llq | ApprovalCutiCommand lifecycle (Q13) | READY | `bd update kepegawaian-llq --claim` |
| — | kepegawaian-y7u | Epic (umbrella, do not claim directly) | OPEN, auto-closes | — |

---

## STEP 0 — Before any code (every claim)

- [ ] `bd prime` (recover beads workflow context)
- [ ] `git status` clean; on branch `rewrite/master-cqrs`
- [ ] Re-read decisions-cuti.md Q11-Q17 for context
- [ ] `bd update <id> --claim` the issue you're starting

---

## ISSUE 1a — `kepegawaian-scn` (Phase 1: SaveCutiService split)

**Goal:** Split SaveCutiService (262 lines) into 5 period-specific handlers + 1 classifier. Foundational for hit and rq2.
**Status:** ○ OPEN

### Pre-edit
- [ ] `gitnexus_impact({target: "SaveCutiService", direction: "upstream"})` — report blast radius
- [ ] Warn user if returns HIGH or CRITICAL

### Extract Components
- [ ] NEW `helpers/cuti/CutiPeriodClassifier.java` — `final` class, private ctor, static `classify(LocalDate start, LocalDate end, int nowYear) → ECutiPeriod`
- [ ] NEW `entities/commons/ECutiPeriod.java` enum { NEXT_YEAR, OVERLAPPING, JAN_JUN, JUL_DES, JUN_JUL }
- [ ] NEW 5 handler classes in `services/cuti/handlers/`:
  - [ ] `ForNextYearHandler.java`
  - [ ] `OverlappingYearHandler.java` 
  - [ ] `Between1JanAnd30JunHandler.java`
  - [ ] `Between1JulAnd31DecHandler.java`
  - [ ] `Between30JunAnd1JulHandler.java`
- [ ] NEW `services/cuti/handlers/CutiPeriodHandlerFactory.java` — dispatch via `switch(period)`
- [ ] REFACTOR `SaveCutiService` — keep shared logic (validation, kuota ops), delegate period-specific to factory

### Verify
- [ ] `wc -l SaveCutiService.java` — confirm <120 lines
- [ ] Each handler <120 lines
- [ ] `./gradlew clean build` — BUILD SUCCESSFUL
- [ ] `./gradlew test` — all tests pass

### Ship
- [ ] `gitnexus_detect_changes()` — verify affected scope
- [ ] `git add` + commit: `refactor(cuti): split SaveCutiService period handlers (Q11)`
- [ ] `bd close kepegawaian-scn`
- [ ] `bd dolt push` → `git pull --rebase` → `git push`

---

## ISSUE 1b — `kepegawaian-sqf` (Phase 1: JOOQ mapper extraction)

**Goal:** Extract mapper logic from 3 JOOQ query repositories to Pola B mapper classes.
**Status:** ○ OPEN

### Pre-edit
- [ ] `gitnexus_impact({target: "CutiPengajuanQueryRepository", direction: "upstream"})`
- [ ] `gitnexus_impact({target: "CutiKuotaQueryRepository", direction: "upstream"})`
- [ ] `gitnexus_impact({target: "CutiJenisQueryRepository", direction: "upstream"})`

### Extract Mappers
- [ ] NEW `mapper/cuti/CutiPengajuanJooqMapper.java` — extract lines 256-346 from CutiPengajuanQueryRepository
  - [ ] Pola B: `final` class, private ctor, NOT `@Component`
  - [ ] Static methods: `mapToResponse`, `mapToMiniResponse`, `mapCommonFields`, enum converters
- [ ] NEW `mapper/cuti/CutiKuotaJooqMapper.java` — extract lines 182-207 from CutiKuotaQueryRepository
  - [ ] Static method: `mapToResponse(Record)`
- [ ] NEW `mapper/cuti/CutiJenisJooqMapper.java` — extract inline mapper triplikasi
  - [ ] Static method: `mapToResponse(Record)`
  - [ ] Replace 3 inline lambdas with `.fetch(CutiJenisJooqMapper::mapToResponse)`

### Verify
- [ ] `wc -l` all 3 query repositories — confirm reduced line counts
- [ ] CutiPengajuanQueryRepository: ~257 lines (pure SQL acceptable)
- [ ] CutiKuotaQueryRepository: ~182 lines (pure SQL acceptable)
- [ ] CutiJenisQueryRepository: ~107 lines (under 120)
- [ ] `./gradlew clean build` — BUILD SUCCESSFUL

### Ship
- [ ] `gitnexus_detect_changes()`
- [ ] `git add` + commit: `refactor(cuti): extract JOOQ mappers to Pola B (Q14-Q16)`
- [ ] `bd close kepegawaian-sqf`
- [ ] Ship protocol

---

## ISSUE 1c — `kepegawaian-39o` (Phase 1: Validator split)

**Goal:** Split CutiPengajuanValidator (123 lines) into two validator classes by operation seam.
**Status:** ○ OPEN

### Pre-edit
- [ ] `gitnexus_impact({target: "CutiPengajuanValidator", direction: "upstream"})`

### Split Validators
- [ ] KEEP `CutiPengajuanValidator.java` — retain `validate(CutiPengajuanPostRequest)` method (59 lines)
- [ ] NEW `services/cuti/klaim/CutiKlaimValidator.java` — extract `validateKlaim(CutiPengajuanKlaimPostRequest)` method (36 lines)
  - [ ] Inject shared dependencies: CutiPegawaiRepository, CutiJenisRepository, CutiKuotaQueryRepository, CutiProperties
- [ ] UPDATE `KlaimCutiCommand` — inject new `CutiKlaimValidator` instead of `CutiPengajuanValidator`

### Verify
- [ ] `wc -l` both validators — confirm <70 lines each
- [ ] `./gradlew clean build`

### Ship
- [ ] `gitnexus_detect_changes()`
- [ ] `git add` + commit: `refactor(cuti): split validators by operation seam (Q12)`
- [ ] `bd close kepegawaian-39o`
- [ ] Ship protocol

---

## ISSUE 2a — `kepegawaian-hit` (Phase 2: PengajuanCutiCommand classifier)

**Goal:** Simplify PengajuanCutiCommand (161 lines) using CutiPeriodClassifier from scn.
**Status:** ○ BLOCKED → READY after kepegawaian-scn closes

### Pre-edit
- [ ] Confirm `kepegawaian-scn` is CLOSED (`bd show kepegawaian-scn`)
- [ ] `bd update kepegawaian-hit --claim`
- [ ] `gitnexus_impact({target: "PengajuanCutiCommand", direction: "upstream"})`

### Refactor
- [ ] INJECT `CutiPeriodClassifier` (from scn)
- [ ] REPLACE 5-way if-else blocks (lines 73-85 save, 133-143 update) with `switch(classifier.classify())`
- [ ] REMOVE double-subtract `setJumlahHari/HariKerja` (4 lines per §47)
- [ ] DELEGATE approval chain pointer init (lines 87-96) to `CutiApprovalChainGenerator` (if exists, else extract)

### Verify
- [ ] `wc -l PengajuanCutiCommand.java` — confirm <120 lines
- [ ] `./gradlew clean build`

### Ship
- [ ] `gitnexus_detect_changes()`
- [ ] `git add` + commit: `refactor(cuti): simplify PengajuanCutiCommand with classifier (Q13)`
- [ ] `bd close kepegawaian-hit`
- [ ] Ship protocol

---

## ISSUE 2b — `kepegawaian-rq2` (Phase 2: KlaimCutiCommand settlement)

**Goal:** Extract settlement service from KlaimCutiCommand (193 lines).
**Status:** ○ BLOCKED → READY after kepegawaian-scn + kepegawaian-39o close

### Pre-edit
- [ ] Confirm `kepegawaian-scn` and `kepegawaian-39o` are CLOSED
- [ ] `bd update kepegawaian-rq2 --claim`
- [ ] `gitnexus_impact({target: "KlaimCutiCommand", direction: "upstream"})`

### Extract Settlement
- [ ] NEW `services/cuti/klaim/CutiKlaimSettlementService.java` — extract 5 period-specific settlement methods:
  - [ ] `forNextYear`, `overlappingYear`, `between1JanAnd30Jun`, `between1JulAnd31Dec`, `between30JunAnd1Jul`
  - [ ] Mirror handler pattern from scn
- [ ] REFACTOR `KlaimCutiCommand` — inject `CutiKlaimSettlementService` + `CutiKlaimValidator` (from 39o)
  - [ ] Keep single `@Transactional` entry point
  - [ ] Delegate settlement to service

### Verify
- [ ] `wc -l KlaimCutiCommand.java` — confirm <120 lines
- [ ] `./gradlew clean build`

### Ship
- [ ] `gitnexus_detect_changes()`
- [ ] `git add` + commit: `refactor(cuti): extract KlaimCutiCommand settlement service (Q12)`
- [ ] `bd close kepegawaian-rq2`
- [ ] Ship protocol

---

## ISSUE 3 — `kepegawaian-llq` (Phase 3: ApprovalCutiCommand lifecycle)

**Goal:** ADR-0021 lifecycle cleanup for ApprovalCutiCommand (121 lines).
**Status:** ○ OPEN (independent, can start anytime after Phase 1)

### Pre-edit
- [ ] `gitnexus_impact({target: "ApprovalCutiCommand", direction: "upstream"})`

### Refactor
- [ ] REMOVE 5 redundant `save()` calls for MANAGED entities (lines 92-94, 101-102, 113, 118)
- [ ] KEEP 3 `save(cutiApproval)` calls for NEW entity (91, 100, 112)
- [ ] EXTRACT helper methods:
  - [ ] NEW `advanceChainPointer(currentChain, nextChain, cutiPegawai, status)` — state mutation without persistence
  - [ ] NEW `terminateChain(currentChain, cutiPegawai, status)` — terminal state logic
- [ ] RENAME `validateToken()` → `isTokenAlreadyUsed()` per §33

### Verify
- [ ] `wc -l ApprovalCutiCommand.java` — confirm ~105 lines
- [ ] `./gradlew clean build`

### Ship
- [ ] `gitnexus_detect_changes()`
- [ ] `git add` + commit: `refactor(cuti): ApprovalCutiCommand lifecycle cleanup (Q13 ADR-0021)`
- [ ] `bd close kepegawaian-llq`
- [ ] Ship protocol

---

## Keputusan Desain yang Dikunci (rujukan saat coding)

Hasil sesi grilling Q11-Q17 — detail di **docs/context/decisions-cuti.md**:

| # | Keputusan | Rujukan |
|---|-----------|---------||
| Q11 | SaveCutiService (262→<120): ekstrak 5 period handlers + CutiPeriodClassifier + factory; shared logic tetap di koordinator | decisions-cuti.md Q11 |
| Q12 | KlaimCutiCommand (193→<120): ekstrak CutiKlaimSettlementService (5 methods) + CutiKlaimValidator; koordinator tetap single @Transactional | decisions-cuti.md Q12 |
| Q13 | PengajuanCutiCommand (161→<120): gunakan CutiPeriodClassifier (Q11), delegate chain-init, hapus double-subtract | decisions-cuti.md Q13 |
| Q13 | ApprovalCutiCommand (121→~105): buang 5 redundant save() MANAGED entities, keep 3 save() NEW entity, ekstrak 2 helper transisi | decisions-cuti.md Q13 |
| Q14 | CutiPengajuanQueryRepository (347): ekstrak mapper ~91 baris → CutiPengajuanJooqMapper; remaining ~257 pure SQL acceptable | decisions-cuti.md Q14 |
| Q15 | CutiKuotaQueryRepository (208): ekstrak mapper ~26 baris; remaining ~182 pure SQL acceptable | decisions-cuti.md Q15 |
| Q16 | CutiJenisQueryRepository (146→107): ekstrak mapper triplikasi inline ~42 baris; naturally under 120 after extraction | decisions-cuti.md Q16 |
| Q17 | CutiPegawai entity (126): pertahankan as-is, lenient untuk data-holders | decisions-cuti.md Q17 |

**Exception Rules:**
- **JOOQ `*QueryRepository`**: Pure SQL construction >120 acceptable **only if mapper extracted** (Pola B: `final` class, private ctor, NOT `@Component`)
- **Entity data-holders**: Lenient (field count reasonable + utility methods)
- **Strict enforcement**: Service/Command/Logic classes must be <120 lines

**Pola failure (semua command):** FK/entity hilang → `.orElseThrow(RuntimeException)` atau custom exception; duplikat → `throw RuntimeException` atau `ConflictException`; unexpected → 500 via `GlobalExceptionHandler`.

---

## Guardrails (apply on ALL issues)

- NEVER edit a symbol without `gitnexus_impact` first
- NEVER rename/move with find-and-replace — use `gitnexus_rename` or `git mv`
- NEVER commit without `gitnexus_detect_changes()`
- beads is the ONLY tracker — no TodoWrite / markdown TODOs
- Stop and ask if any impact analysis returns HIGH/CRITICAL
- Git mv invariant: Use `git mv` for file renames (CODING_RULES §17)
- Single transaction entry: Keep ADR-0021 pattern (§32)
- No premature abstraction: Extract only per decisions, don't over-engineer
- Test preservation: All existing tests must pass

---

## Issue Terkait (bugs preserved for post-refactor fix)

- **kepegawaian-ciw** (P2) — `forNextYear` getYear()-1 asimetri. Parent: kepegawaian-is7.12. Marked "preserve, do not fix inline".
- **kepegawaian-ebt** (P2) — `CutiKuotaUpdateByCutiService` LocalDate.now() cross-year deduction bug. Marked "preserve".
- **kepegawaian-s5n** (P2) — `saveKlaim` entity.equals() vs getId() inconsistency (Hibernate proxy-rapuh). Marked "preserve".
- **kepegawaian-sfq** (P2) — `between1JanAnd30Jun` LocalDate.now() wall-clock bug (approval outcome depends on click time). Marked "preserve".

> Bugs akan diselesaikan **setelah** refactoring selesai (refactor for structural clarity first, then fix bugs in clean code).

---

## Cara Update Checklist Ini

Tandai `[x]` saat subtask selesai. Sumber kebenaran status tetap **beads** (`bd show kepegawaian-y7u`, `bd ready`); file ini ringkasan manusiawi untuk monitoring phase.

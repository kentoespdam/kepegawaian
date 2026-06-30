# Cuti Refactor — Claim Order & Monitoring

> **Epic:** `kepegawaian-y7u` — Enforce CODING_RULES §4 (max 120 lines) untuk modul cuti service/command classes.  
> **Basis:** grilling decisions Q11-Q17 di `docs/context/decisions-cuti.md`

**Urutan klaim mengikuti dependency**, bukan nomor issue. Kerjakan per **PHASE**: semua issue dalam satu phase boleh diklaim paralel (tidak saling blok); phase berikutnya baru terbuka setelah phase sebelumnya selesai. `bd ready` selalu jadi sumber kebenaran.

> **WORKING AGREEMENT:** gitnexus-first (`gitnexus_impact` sebelum edit symbol, `gitnexus_detect_changes` sebelum commit), **strict scope — JANGAN AI SLOP**, alur `claim → code → test → detect_changes → close → ship` (CODING_RULES.md).

---

## 📋 Quick Reference

| Phase | Issue | Title | Status | Cmd |
|-------|-------|-------|--------|-----|
| **1** | `kepegawaian-scn` | SaveCutiService split | ✓ CLOSED | — |
| **1** | `kepegawaian-sqf` | JOOQ mapper extraction | ✓ CLOSED | — |
| **1** | `kepegawaian-39o` | Validator split | ✓ CLOSED | — |
| **2** | `kepegawaian-hit` | PengajuanCutiCommand classifier | ✓ CLOSED | — |
| **2** | `kepegawaian-rq2` | KlaimCutiCommand settlement | ✓ CLOSED | — |
| **3** | `kepegawaian-llq` | ApprovalCutiCommand lifecycle | ✓ CLOSED | — |
| **4** | `kepegawaian-y7u.1` | Facade trio cleanup | ○ OPEN | `bd update kepegawaian-y7u.1 --claim` |
| **4** | `kepegawaian-y7u.2` | CutiApprovalQueryService + JOOQ | ○ OPEN | `bd update kepegawaian-y7u.2 --claim` |

---

## 🔗 Dependency Graph

```
kepegawaian-y7u (Epic: 120-line enforcement)
│
├─ kepegawaian-sqf [PHASE 1] JOOQ mapper extraction (Q14-Q16)
│
├─ kepegawaian-scn [PHASE 1] SaveCutiService split (Q11) [FOUNDATIONAL]
│   └─ kepegawaian-hit [PHASE 2] PengajuanCutiCommand classifier (Q13)
│
├─ kepegawaian-39o [PHASE 1] Validator split (Q12)
│   └─ kepegawaian-rq2 [PHASE 2] KlaimCutiCommand settlement (Q12)
│
├─ kepegawaian-llq [PHASE 3] ApprovalCutiCommand lifecycle (Q13)
│
├─ kepegawaian-y7u.1 [PHASE 4] Facade trio cleanup (Q2)
│  └─ Depends: hit, llq, rq2 all closed
│
└─ kepegawaian-y7u.2 [PHASE 4] CutiApprovalQueryService + JOOQ (Q5)
   └─ Depends: y7u.1 closed (facade gone)
```

---

## 🚀 Before Any Claim (Every Time)

- [ ] `bd prime` — recover beads workflow context
- [ ] `git status` clean; on branch `rewrite/master-cqrs`
- [ ] Re-read `decisions-cuti.md` Q11-Q17 for context
- [ ] `bd update <id> --claim` the issue you're starting

---

## 📌 Issue Details

### 1a — `kepegawaian-scn` · Phase 1

| | |
|---|---|
| **Goal** | Split `SaveCutiService` (262→<120 lines) → 5 period handlers + classifier |
| **Status** | ✓ CLOSED |
| **Depends** | — |

**Pre:**
- [x] `gitnexus_impact({target: "SaveCutiService", direction: "upstream"})`

**Extract:**
- [x] `helpers/cuti/CutiPeriodClassifier.java` — `final` class, static `classify()`
- [x] `entities/commons/ECutiPeriod.java` — enum
- [x] 5 handlers in `services/cuti/handlers/` | Factory via `switch(period)`
- [x] REFACTOR `SaveCutiService` — shared logic stays, period logic delegated

**Verify & Ship:**
- [x] `wc -l SaveCutiService.java` <120 | `./gradlew clean build && test`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-scn`

---

### 1b — `kepegawaian-sqf` · Phase 1

| | |
|---|---|
| **Goal** | Extract JOOQ mappers from 3 query repos → Pola B classes |
| **Status** | ✓ CLOSED |
| **Depends** | — |

**Pre:**
- [x] `gitnexus_impact` on all 3 query repos

**Extract:**
- [x] `mapper/cuti/CutiPengajuanJooqMapper.java` — `final`, private ctor, static methods
- [x] `mapper/cuti/CutiKuotaJooqMapper.java` — `mapToResponse(Record)`
- [x] `mapper/cuti/CutiJenisJooqMapper.java` — replace 3 inline lambdas

**Verify & Ship:**
- [x] Query repos ≤257 / ≤182 / ≤107 lines | `./gradlew clean build`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-sqf`

---

### 1c — `kepegawaian-39o` · Phase 1

| | |
|---|---|
| **Goal** | Split `CutiPengajuanValidator` (123→<70×2 lines) |
| **Status** | ✓ CLOSED |
| **Depends** | — |

**Pre:**
- [x] `gitnexus_impact({target: "CutiPengajuanValidator", direction: "upstream"})`

**Split:**
- [x] KEEP `CutiPengajuanValidator` — `validate(Pengajuan)` (~59 lines)
- [x] NEW `services/cuti/klaim/CutiKlaimValidator.java` — `validateKlaim()` (~36 lines)
- [x] UPDATE `KlaimCutiCommand` — inject new validator

**Verify & Ship:**
- [x] Both <70 lines | `./gradlew clean build`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-39o`

---

### 2a — `kepegawaian-hit` · Phase 2

| | |
|---|---|
| **Goal** | Simplify `PengajuanCutiCommand` (161→<120 lines) |
| **Status** | ✓ CLOSED |
| **Depends** | `kepegawaian-scn` |

**Pre (after scn closes):**
- [x] Confirm scn CLOSED → `bd update kepegawaian-hit --claim`
- [x] `gitnexus_impact({target: "PengajuanCutiCommand", direction: "upstream"})`

**Refactor:**
- [x] INJECT `CutiPeriodClassifier` | REPLACE 5-way if-else → `switch()`
- [x] REMOVE double-subtract | DELEGATE chain pointer init

**Verify & Ship:**
- [x] `<120 lines` | `./gradlew clean build`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-hit`

---

### 2b — `kepegawaian-rq2` · Phase 2

| | |
|---|---|
| **Goal** | Extract settlement service from `KlaimCutiCommand` (193→<120 lines) |
| **Status** | ✓ CLOSED |
| **Depends** | `kepegawaian-scn` + `kepegawaian-39o` |

**Pre (both closed):**
- [x] `bd update kepegawaian-rq2 --claim`
- [x] `gitnexus_impact({target: "KlaimCutiCommand", direction: "upstream"})`

**Extract:**
- [x] NEW `CutiKlaimSettlementService` — 5 period methods (mirror scn pattern)
- [x] REFACTOR `KlaimCutiCommand` — single `@Transactional` entry, delegate settlement

**Verify & Ship:**
- [x] `<120 lines` | `./gradlew clean build`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-rq2`

---

### 3 — `kepegawaian-llq` · Phase 3

| | |
|---|---|
| **Goal** | ADR-0021 lifecycle cleanup `ApprovalCutiCommand` (121→~105 lines) |
| **Status** | ✓ CLOSED |
| **Depends** | Phase 1 complete |

**Pre:**
- [x] `gitnexus_impact({target: "ApprovalCutiCommand", direction: "upstream"})`

**Refactor:**
- [x] REMOVE 5 redundant `save()` MANAGED | KEEP 3 `save()` NEW
- [x] EXTRACT `advanceChainPointer()` + `terminateChain()` helpers
- [x] RENAME `validateToken()` → `isTokenAlreadyUsed()`

**Verify & Ship:**
- [x] `~105 lines` | `./gradlew clean build`
- [x] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-llq`

---

### 4 — `kepegawaian-y7u.1` · Phase 4

| | |
|---|---|
| **Goal** | Delete facade trio: 3 interface + 3 impl (Q2) |
| **Status** | ○ OPEN |
| **Depends** | kepegawaian-hit, kepegawaian-llq, kepegawaian-rq2 |

**Pre:**
- [ ] Confirm hit, llq, rq2 CLOSED → `bd update kepegawaian-y7u.1 --claim`
- [ ] `gitnexus_impact` on 6 facade files (3 interface + 3 impl)

**Delete:**
- [ ] `CutiPengajuanService` + `CutiPengajuanServiceImpl`
- [ ] `CutiApprovalService` + `CutiApprovalServiceImpl`
- [ ] `CutiApprovalChainService` + `CutiApprovalChainServiceImpl`
- [ ] Update controller imports (already rewired to Command in hit/llq/rq2)

**Verify & Ship:**
- [ ] `./gradlew clean build` (no compile errors)
- [ ] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-y7u.1`

---

### 4b — `kepegawaian-y7u.2` · Phase 4

| | |
|---|---|
| **Goal** | Create CutiApprovalQueryService + JOOQ for approval history (Q5) |
| **Status** | ○ OPEN |
| **Depends** | kepegawaian-y7u.1 |

**Pre:**
- [ ] Confirm y7u.1 CLOSED → `bd update kepegawaian-y7u.2 --claim`
- [ ] Read `CutiApprovalIndexQuery.getSpecification()` to understand filter fields

**Create:**
- [ ] NEW `services/cuti/approval/CutiApprovalQueryService` — `findByCutiId(Long)`
- [ ] NEW `repositories/cuti/jooq/CutiApprovalQueryRepository` — SELECT + baseWhere
- [ ] NEW `mapper/cuti/CutiApprovalJooqMapper` (Pola B) — mapToResponse
- [ ] Port 4 filter fields (id/cutiId/approverId/jabatanId) from spec to baseWhere
- [ ] UPDATE `CutiApprovalController` — inject QueryService for read

**Verify & Ship:**
- [ ] `./gradlew clean build`
- [ ] Test `GET /cuti/approval/{cutiId}` returns approval history
- [ ] `gitnexus_detect_changes()` → commit → `bd close kepegawaian-y7u.2`

---

## 📖 Design Decisions (Q11-Q17)

| # | Decision | Target | Ref |
|---|----------|--------|-----|
| Q11 | 5 handlers + classifier + factory | `SaveCutiService` 262→<120 | `decisions-cuti.md` |
| Q12 | Settlement service + klaim validator | `KlaimCutiCommand` 193→<120 | `decisions-cuti.md` |
| Q13 | Use classifier, delegate chain-init | `PengajuanCutiCommand` 161→<120 | `decisions-cuti.md` |
| Q13 | Remove redundant saves, extract helpers | `ApprovalCutiCommand` 121→~105 | `decisions-cuti.md` |
| Q14 | Extract mapper ~91 lines | `CutiPengajuanQueryRepository` 347→257 | `decisions-cuti.md` |
| Q15 | Extract mapper ~26 lines | `CutiKuotaQueryRepository` 208→182 | `decisions-cuti.md` |
| Q16 | Extract mapper triplikasi ~42 lines | `CutiJenisQueryRepository` 146→107 | `decisions-cuti.md` |
| Q17 | Keep as-is (data-holder lenient) | `CutiPegawai` entity 126 | `decisions-cuti.md` |
| Q2 | Delete facade trio (interface + impl) | 6 files → 0 | `decisions-cuti.md` |
| Q5 | CutiApprovalQueryService + JOOQ repo | GET /cuti/approval/{cutiId} | `decisions-cuti.md` |

### Exception Rules
- **JOOQ `*QueryRepository`**: >120 lines OK **if mapper extracted** (Pola B: `final`, private ctor, no `@Component`)
- **Entity data-holders**: Lenient
- **Service/Command/Logic**: Strict <120 lines

### Failure Pattern (all commands)
FK missing → `.orElseThrow()` · Duplicate → `throw` · Unexpected → 500 via `GlobalExceptionHandler`

---

## ⚠️ Guardrails

- **NEVER** edit symbol without `gitnexus_impact` first
- **NEVER** rename/move with find-and-replace — use `gitnexus_rename` or `git mv`
- **NEVER** commit without `gitnexus_detect_changes()`
- beads is the **ONLY** tracker — no TodoWrite / markdown TODOs
- Stop & ask if impact returns HIGH/CRITICAL
- `git mv` for file renames (CODING_RULES §17)
- Single `@Transactional` entry (ADR-0021 §32)
- No premature abstraction
- All existing tests must pass

---

## 🐛 Preserved Bugs (post-refactor fixes)

| Issue | Bug | Priority |
|-------|-----|----------|
| `kepegawaian-ciw` | `forNextYear` getYear()-1 asymmetry | P2 |
| `kepegawaian-ebt` | `CutiKuotaUpdateByCutiService` LocalDate.now() cross-year deduction | P2 |
| `kepegawaian-s5n` | `saveKlaim` entity.equals() vs getId() (Hibernate proxy) | P2 |
| `kepegawaian-sfq` | `between1JanAnd30Jun` wall-clock bug (approval outcome depends on click time) | P2 |

> Fix bugs **after** refactoring — structural clarity first, then clean code fixes.

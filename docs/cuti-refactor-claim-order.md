# Cuti Refactor Claim Order

> **Epic**: kepegawaian-y7u  
> **Date**: 2026-06-30  
> **Basis**: Grilling decisions Q11-Q17 di `docs/context/decisions-cuti.md`

## Dependency Graph

```
kepegawaian-y7u (Epic: 120-line enforcement)
├─ kepegawaian-sqf [INDEPENDENT] JOOQ mapper extraction (Q14-Q16)
├─ kepegawaian-scn [FOUNDATIONAL] SaveCutiService split (Q11)
│  └─ kepegawaian-hit [DEPENDS: scn] PengajuanCutiCommand classifier (Q13)
├─ kepegawaian-39o [INDEPENDENT] Validator split (Q12)
├─ kepegawaian-rq2 [DEPENDS: 39o, scn] KlaimCutiCommand settlement (Q12)
└─ kepegawaian-llq [INDEPENDENT] ApprovalCutiCommand lifecycle (Q13)
```

## Recommended Claim Order

### Phase 1: Foundational (Independent, High Value)
**Claim in parallel or any order:**

1. **kepegawaian-scn** - SaveCutiService split (Q11)  
   _Why first_: Creates `CutiPeriodClassifier` needed by hit, enables period handler isolation  
   _Effort_: High (262→<120, extract 5 handlers + classifier + factory)  
   _Risk_: Medium (core allocation logic)

2. **kepegawaian-sqf** - JOOQ mapper extraction (Q14-Q16)  
   _Why first_: Independent, reduces duplication, enforces Q14 exception rule  
   _Effort_: Medium (3 mappers, Pola B pattern)  
   _Risk_: Low (pure data mapping)

3. **kepegawaian-39o** - Validator split (Q12)  
   _Why first_: Independent, enables rq2, simple semantic split  
   _Effort_: Low (123→2 files <70 each)  
   _Risk_: Low (validation logic)

### Phase 2: Dependent (After Phase 1 Complete)
**Sequential dependencies:**

4. **kepegawaian-hit** - PengajuanCutiCommand classifier (Q13)  
   _Depends on_: kepegawaian-scn (needs `CutiPeriodClassifier`)  
   _Effort_: Low (replace if-else with switch, remove double-subtract)  
   _Risk_: Low (simplification)

5. **kepegawaian-rq2** - KlaimCutiCommand settlement (Q12)  
   _Depends on_: kepegawaian-39o (CutiKlaimValidator) + kepegawaian-scn (period handlers pattern)  
   _Effort_: High (193→<120, extract 5 settlement methods)  
   _Risk_: Medium (settlement logic)

### Phase 3: Independent Cleanup (Anytime After Phase 1)
**Can be done in parallel with Phase 2:**

6. **kepegawaian-llq** - ApprovalCutiCommand lifecycle (Q13)  
   _Why last_: Independent, ADR-0021 enforcement, low coupling  
   _Effort_: Low (remove redundant saves, extract 2 helpers)  
   _Risk_: Low (lifecycle cleanup)

## Critical Path

```
scn (foundational) → hit (uses classifier)
39o (validator) ────→ rq2 (uses validator)
scn (pattern) ──────→ rq2 (mirrors handlers)
```

**Minimum for parallel work**: Start scn + sqf + 39o together, then hit + rq2 after scn/39o done, llq anytime.

## Exception Rules (from Q14)

- **JOOQ `*QueryRepository`**: Pure SQL construction >120 acceptable **only if mapper extracted**
- **Entity data-holders**: Lenient (e.g., `CutiPegawai` 126 lines retained per Q17)
- **Strict enforcement**: Service/Command/Logic classes must be <120 lines

## Verification Checklist (Per Task)

- [ ] Run `gitnexus_impact` before editing any symbol
- [ ] Verify affected scope matches expectation
- [ ] Run build after changes (`./gradlew build`)
- [ ] Run tests (`./gradlew test`)
- [ ] Run `gitnexus_detect_changes` before commit
- [ ] Verify file line counts: `wc -l <files>`
- [ ] Update `decisions-cuti.md` if implementation deviates
- [ ] Close beads issue with implementation notes

## Notes

- **Git mv invariant**: Use `git mv` for file renames (CODING_RULES §17)
- **Single transaction entry**: Keep ADR-0021 pattern (§32)
- **No premature abstraction**: Extract only per decisions, don't over-engineer
- **Test preservation**: All existing tests must pass, add tests for extracted components

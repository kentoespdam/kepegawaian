# Claim Order — ValidatorFactory Fix

**Issue:** kepegawaian-637t
**Priority:** P3 (MEDIUM)
**Severity:** MEDIUM — manual ValidatorFactory instantiation is wasteful and non-idiomatic

---

## Context

Code review finding: `RiwayatSkController` and `RiwayatMutasiController` manually create `ValidatorFactory` instances:

```java
private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
private final Validator validator = factory.getValidator();
```

**Problems:**
1. Each controller instance creates its own factory (expensive)
2. `Validation.buildDefaultValidatorFactory()` is expensive to call
3. Spring already provides a `Validator` bean — should inject it
4. The `ValidatorFactory` is never closed (minor resource leak)

**Note:** The conditional validation logic (group-based validation) is legitimate controller behavior — this fix is ONLY about how the Validator is obtained.

---

## Claim Order (Step-by-Step)

### Step 1: Fix RiwayatSkController
- [x] Remove `private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();`
- [x] Remove `private final Validator validator = factory.getValidator();`
- [x] Add `private final Validator validator;` (will be injected by `@RequiredArgsConstructor`)
- [x] Verify: `./gradlew clean compileJava`

### Step 2: Fix RiwayatMutasiController
- [x] Same changes as Step 1
- [x] Verify: `./gradlew clean compileJava`

### Step 3: Verify
- [ ] `./gradlew test` — all green
+ [x] Conditional validation works (group-based validation)

---

## Files

| Action | File |
|--------|------|
| Edit | `controllers/kepegawaian/RiwayatSkController.java` |
| Edit | `controllers/kepegawaian/RiwayatMutasiController.java` |

---

## Dependencies

None — can be done parallel with other issues.

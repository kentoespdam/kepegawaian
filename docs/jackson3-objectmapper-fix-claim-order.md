# Claim Order — Replace Mutable ObjectMapper with Jackson 3 JsonMapper

**Issue:** kepegawaian-79n0
**Priority:** P2 (HIGH)
**Severity:** HIGH — mutable ObjectMapper violates CODING_RULES "Jackson 3 — immutable JsonMapper.builder()"

---

## Context

CODING_RULES: "Jackson 3 | Package `tools.jackson`; immutable `JsonMapper.builder()`. Do NOT use mutable `ObjectMapper`."

2 security handlers create `new ObjectMapper()` from `com.fasterxml.jackson.databind` (Jackson 2):
- `DeniedHandler.java`
- `JwtAuthEntryPoint.java`

**Approach:** Replace with `tools.jackson.databind.json.JsonMapper` (Jackson 3). Use singleton pattern via `@RequiredArgsConstructor` injection.

---

## Claim Order

### Step 1: Fix DeniedHandler
- [ ] Replace `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.json.JsonMapper`
- [ ] Inject via constructor (already has `@RequiredArgsConstructor`)
- [ ] Replace `new ObjectMapper()` with injected `JsonMapper`
- [ ] Verify: `./gradlew clean compileJava`

### Step 2: Fix JwtAuthEntryPoint
- [ ] Same changes as Step 1
- [ ] Verify: `./gradlew clean compileJava`

### Step 3: Verify
- [ ] `./gradlew test` — all green

---

## Files

| Action | File |
|--------|------|
| Edit | `config/security/DeniedHandler.java` |
| Edit | `config/security/JwtAuthEntryPoint.java` |

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
- [x] Replace `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.json.JsonMapper`
- [x] Inject via constructor (already has `@RequiredArgsConstructor`)
- [x] Replace `new ObjectMapper()` with injected `JsonMapper`
- [x] Verify: `./gradlew clean compileJava`

### Step 2: Fix JwtAuthEntryPoint
- [x] Same changes as Step 1
- [x] Verify: `./gradlew clean compileJava`

### Step 3: Verify
- [x] `./gradlew test` — all green

---

## Files

| Action | File |
|--------|------|
| Edit | `config/security/DeniedHandler.java` |
| Edit | `config/security/JwtAuthEntryPoint.java` |

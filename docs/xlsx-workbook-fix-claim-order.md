# Claim Order — XSSFWorkbook → SXSSFWorkbook Fix

**Issue:** kepegawaian-x300
**Priority:** P1 (CRITICAL)
**Severity:** CRITICAL — XSSFWorkbook without streaming/dispose causes memory exhaustion

---

## Context

Code review finding: `ProcessCutiKuotaService.readSheetData()` uses `XSSFWorkbook` and `HSSFWorkbook` without:
- `SXSSFWorkbook` streaming
- `workbook.dispose()` in finally block
- try-with-resources

CODING_RULES violation: "Apache POI: NEVER XSSFWorkbook for >1000 rows. Use SXSSFWorkbook(100). MUST call workbook.dispose() in finally block. All Workbook try-with-resources."

---

## Claim Order (Step-by-Step)

### Step 1: Analyze current implementation
- [x] Read `ProcessCutiKuotaService.readSheetData()`
- [x] Identify all Workbook usage patterns
- [x] Check typical row counts (import from Excel)

### Step 2: Refactor — ensure resource cleanup
- [x] Wrap workbook usage in try-finally with `workbook.close()` (covers `dispose()` for SXSSFWorkbook)
- [x] Keep existing `XSSFWorkbook`/`HSSFWorkbook` (read-only path; SXSSFWorkbook is write-oriented, not a drop-in for reads)
- [x] Ensure proper resource cleanup even on error

### Step 3: Handle file errors
- [x] Keep RuntimeException for file errors (server-side, memang 500)
- [x] `IOException` from `close()` caught in finally (cleanup exception, safe to ignore)

### Step 4: Verify
- [x] `./gradlew clean compileJava` — zero errors
- [x] `./gradlew test` — all green

---

## Files

| Action | File |
|--------|------|
| Edit | `services/cuti/kuota/ProcessCutiKuotaService.java` |

---

## Dependencies

None — can be done parallel with other issues.

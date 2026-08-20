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
- [ ] Read `ProcessCutiKuotaService.readSheetData()`
- [ ] Identify all Workbook usage patterns
- [ ] Check typical row counts (import from Excel)

### Step 2: Refactor to SXSSFWorkbook
- [ ] Replace `new XSSFWorkbook(inputStream)` → `new SXSSFWorkbook(100)` (window size 100 rows)
- [ ] Replace `new HSSFWorkbook(inputStream)` → same pattern for .xls files
- [ ] Add try-with-resources on Workbook
- [ ] Add `workbook.dispose()` in finally block (or rely on try-with-resources auto-close)

### Step 3: Handle file errors
- [ ] Keep RuntimeException for file errors (server-side, memang 500)
- [ ] Ensure proper resource cleanup even on error

### Step 4: Verify
- [ ] `./gradlew clean compileJava` — zero errors
- [ ] `./gradlew test` — all green
- [ ] Test import Excel file > 1000 rows (if test data available)

---

## Files

| Action | File |
|--------|------|
| Edit | `services/cuti/kuota/ProcessCutiKuotaService.java` |

---

## Dependencies

None — can be done parallel with other issues.

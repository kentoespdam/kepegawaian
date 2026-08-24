# Claim Order — Cuti Services: RuntimeException → Typed Exceptions

**Issue:** kepegawaian-mjwa
**Priority:** P2 (HIGH)
**Severity:** HIGH — RuntimeException maps to HTTP 500, leaking stack traces to clients

---

## Context

Code review finding: 16 `RuntimeException` throws across cuti services, all business validation errors that should return proper HTTP status codes (400/404/409).

**Approach:** Split per subdomain. Each subdomain gets its own sub-issue for granularity.

---

## Sub-Issues

### Sub-Issue A: Cuti Pengajuan Validator (4 exceptions)
**File:** `services/cuti/pengajuan/CutiPengajuanValidator.java`

| Line | Current | Fix |
|------|---------|-----|
| 44 | `RuntimeException("Masih ada pengajuan cuti yang belum diapprove")` | `BadRequestException` |
| 57 | `RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti besar")` | `BadRequestException` |
| 64 | `RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti melaksanakan ibadah")` | `BadRequestException` |
| 76 | `RuntimeException("Kuota cuti tidak mencukupi")` | `BadRequestException` |

### Sub-Issue B: Cuti Klaim (5 exceptions)
**Files:** `KlaimCutiCommand.java`, `CutiKlaimValidator.java`, `CutiApproveKlaimCutiService.java`, `CutiKlaimCrossYearSettlement.java`

| File | Line | Current | Fix |
|------|------|---------|-----|
| KlaimCutiCommand | 100 | `RuntimeException("Approver Pegawai not found")` | `NotFoundException` |
| CutiKlaimValidator | 26 | `RuntimeException("Cuti ini tidak perlu di klaim")` | `BadRequestException` |
| CutiKlaimValidator | 40 | `RuntimeException("Pengajuan Klaim Cuti ini sudah ada")` | `BadRequestException` |
| CutiKlaimValidator | 50 | `RuntimeException("Klaim cuti tidak dapat diproses...")` | `BadRequestException` |
| CutiApproveKlaimCutiService | 37 | `RuntimeException("Cuti claim rejected...")` | `BadRequestException` |
| CutiApproveKlaimCutiService | 94 | `RuntimeException("Cuti claim rejected...")` | `BadRequestException` |
| CutiKlaimCrossYearSettlement | 61 | `RuntimeException("Kuota Cuti Tahun depan tidak tersedia...")` | `BadRequestException` |

### Sub-Issue C: Cuti Approval (2 exceptions)
**File:** `services/cuti/approval/ApprovalCutiCommand.java`

| Line | Current | Fix |
|------|---------|-----|
| 54 | `RuntimeException("You are not allowed to approve...")` | `ForbiddenException` |
| 62 | `RuntimeException("Unknown Approval Status")` | `BadRequestException` |

### Sub-Issue D: Cuti Kuota/Process (3 exceptions)
**File:** `services/cuti/kuota/ProcessCutiKuotaService.java`

| Line | Current | Fix |
|------|---------|-----|
| 42 | `RuntimeException("Gagal membaca file")` | **Keep RuntimeException** (server-side) |
| 45 | `RuntimeException("Tidak ada data")` | `BadRequestException` |
| 59 | `RuntimeException("Failed to process spreadsheet")` | **Keep RuntimeException** (server-side) |

---

## Claim Order (Step-by-Step)

### Step 1: Sub-Issue A — Pengajuan Validator
- [x] Replace 4 RuntimeException → BadRequestException (+ 1 NotFoundException for Unknown Jenis Cuti)
- [x] Add import for BadRequestException + NotFoundException
- [x] Verify: `./gradlew clean compileJava`

### Step 2: Sub-Issue B — Cuti Klaim
- [x] Replace 7 RuntimeException → typed exceptions (NotFoundException + BadRequestException)
- [x] Verify: `./gradlew clean compileJava`

### Step 3: Sub-Issue C — Cuti Approval
- [x] Replace 2 RuntimeException → ForbiddenException + BadRequestException (+ 3 NotFoundException for entity lookups)
- [x] Verify: `./gradlew clean compileJava`

### Step 4: Sub-Issue D — Cuti Kuota
- [x] Replace 1 RuntimeException → BadRequestException (keep 2 file errors as RuntimeException)
- [x] Verify: `./gradlew clean compileJava`

### Step 5: Full verification
- [x] `./gradlew test` — all green
- [x] No stack traces leaked in error responses

---

## Dependencies

None — can be done parallel with other issues.

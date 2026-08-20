# Claim Order — System Controllers @Transactional Fix

**Issue:** kepegawaian-e98l
**Priority:** P1 (CRITICAL)
**Severity:** CRITICAL — @Transactional on controller methods violates architecture rules

---

## Context

Code review finding: `PrefPermissionController` and `PrefRoleController` have `@Transactional` directly on controller methods.

CODING_RULES violation: "Transaction Management: @Transactional on Service layer ONLY. Never on controllers or repositories."

**Approach:** Minimal — buat service baru, pindahkan hanya methods yang butuh `@Transactional`. `store()` dan `update()` tetap di controller (single-entity operations, Spring auto-commits).

---

## Claim Order (Step-by-Step)

### Step 1: Buat PrefPermissionService
- [ ] Buat `services/system/PrefPermissionService.java`
- [ ] Pindahkan logic `assign()` dari controller ke service method
- [ ] Pindahkan logic `revoke()` dari controller ke service method
- [ ] Tambahkan `@Service` + `@Transactional` pada service class
- [ ] Inject `PrefRoleRepository` + `PrefPermissionRepository`

### Step 2: Buat PrefRoleService
- [ ] Buat `services/system/PrefRoleService.java`
- [ ] Pindahkan logic `destroy()` dari controller ke service method
- [ ] Tambahkan `@Service` + `@Transactional` pada service method
- [ ] Inject `PrefRoleRepository`
- [ ] Pindahkan `PROTECTED_ROLES` constant ke service

### Step 3: Refactor controllers
- [ ] `PrefPermissionController`: inject `PrefPermissionService`, panggil service methods
- [ ] `PrefRoleController`: inject `PrefRoleService`, panggil service method
- [ ] Hapus `@Transactional` dari controller methods
- [ ] Hapus direct repository injection dari controllers (hanya butuh service)

### Step 4: Verify
- [ ] `./gradlew clean compileJava` — zero errors
- [ ] `./gradlew test` — all green
- [ ] Test assign/revoke/destroy endpoints仍然 work correctly

---

## Files

| Action | File |
|--------|------|
| Create | `services/system/PrefPermissionService.java` |
| Create | `services/system/PrefRoleService.java` |
| Edit | `controllers/system/PrefPermissionController.java` |
| Edit | `controllers/system/PrefRoleController.java` |

---

## Dependencies

None — can be done parallel with other issues.

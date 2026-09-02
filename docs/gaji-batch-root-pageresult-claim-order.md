# GajiBatchRoot — ListResult → PageResult Claim Order

> Manager-authored work order. Claim issue `kepegawaian-59mu`.
> Grilling session completed 2026-09-02. All decisions documented below.

## Context

`GET /penggajian/batch` (index) and `GET /penggajian/batch/{periode}/periode/{status}/status` (byPeriode) return `ListResult` (flat list) via `CustomResult.list(...)`. Per ADR-0040/0041, all index endpoints must return `PageResult<Page<...>>` via `CustomResult.page(...)`.

**Infrastructure already exists**: `pageQuery()` in repository, `findPage()` in service, `GajiBatchRootIndexQuery extends PagedRequest`. Only the controller wiring is wrong.

**Breaking change**: Response envelope changes from `ListResult` to `PageResult`. FE must update.

## Grilling Decisions

| Decision | Answer |
|----------|--------|
| Both endpoints? | Yes — index + byPeriode |
| Delete dead code? | Yes — `findAll()` and `listQuery()` have zero callers after fix |
| byPeriode pagination defaults | page=0, size=20 (from PagedRequest) |
| FE-CONTRACT update? | Yes — document breaking change |

## Claim Order

| Order | Step | File | Description | Status |
|-------|------|------|-------------|--------|
| 1 | DTO | `GajiBatchRootIndexQuery.java` | No change — already extends PagedRequest | [x] |
| 2 | Controller | `GajiBatchRootController.java` | Change 2 lines: `CustomResult.list(findAll)` → `CustomResult.page(findPage)` | [x] |
| 3 | Service | `GajiBatchRootQueryService.java` | Delete `findAll()` method + unused `List` import | [x] |
| 4 | Repository | `GajiBatchRootQueryRepository.java` | Delete `listQuery()` method (List import kept — still used in getById) | [x] |
| 5 | Docs | `docs/frontend/FE-CONTRACT-file-endpoints.md` | Document breaking change for `/penggajian/batch` | [x] |
| 6 | Test | `GajiBatchRootControllerTest.java` | Update mocks: findAll → findPage, assertions for PageResult | [x] |
| 7 | Build | `./gradlew compileJava` | Verify zero errors | [x] |

---

## STEP 0 — Before any code

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] Baca `docs/context/language-penggajian.md` — domain terminology
- [x] `bd show kepegawaian-59mu` — baca checklist di issue
- [x] `bd update kepegawaian-59mu --claim`

---

## STEP 1 — Controller

**File**: `src/main/java/id/perumdamts/kepegawaian/controllers/penggajian/GajiBatchRootController.java`

- [x] Line 33: `return CustomResult.list(queryService.findAll(request));` → `return CustomResult.page(queryService.findPage(request));`
- [x] Line 51: `return CustomResult.list(queryService.findAll(request));` → `return CustomResult.page(queryService.findPage(request));`
- [x] Update return type from `ResponseEntity<ListResult<GajiBatchRootResponse>>` to `ResponseEntity<PageResult<Page<GajiBatchRootResponse>>>`
- [x] Add import for `PageResult` if not present

**Impact check**:
- [ ] `gitnexus_impact({target: "GajiBatchRootController", direction: "upstream"})` — laporkan blast radius
- [ ] WARN bila HIGH/CRITICAL

---

## STEP 2 — Service (dead code removal)

**File**: `src/main/java/id/perumdamts/kepegawaian/services/penggajian/gajiBatchRoot/GajiBatchRootQueryService.java`

- [x] Delete `findAll(GajiBatchRootIndexQuery query)` method
- [x] Remove unused `java.util.List` import (if no other method uses it)
- [x] Verify: `findPage()` and `findById()` remain unchanged

**Impact check**:
- [ ] `gitnexus_impact({target: "GajiBatchRootQueryService.findAll", direction: "upstream"})` — verify zero callers
- [ ] Confirm: only callers were the two controller endpoints being fixed

---

## STEP 3 — Repository (dead code removal)

**File**: `src/main/java/id/perumdamts/kepegawaian/repositories/penggajian/jooq/GajiBatchRootQueryRepository.java`

- [x] Delete `listQuery(GajiBatchRootIndexQuery query)` method
- [x] `java.util.List` import kept — still used in `getById()`
- [x] Verify: `pageQuery()` and `getById()` remain unchanged

**Impact check**:
- [ ] `gitnexus_impact({target: "GajiBatchRootQueryRepository.listQuery", direction: "upstream"})` — verify zero callers
- [ ] Confirm: only caller was `GajiBatchRootQueryService.findAll()` (also being deleted)

---

## STEP 4 — Docs

**File**: `docs/frontend/FE-CONTRACT-file-endpoints.md`

- [x] Add section for `/penggajian/batch` breaking change
- [x] Document: `ListResult` → `PageResult` for both index endpoints
- [x] Document: empty list now returns 200 + empty page (previously 404)
- [x] Document: `page` and `size` query params now available

---

## STEP 5 — Test

**File**: `src/test/java/id/perumdamts/kepegawaian/controllers/penggajian/GajiBatchRootControllerTest.java`

- [x] Change mock: `when(queryService.findAll(...))` → `when(queryService.findPage(...))`
- [x] Update assertions: expect `PageResult` instead of `ListResult`

---

## STEP 6 — Build & Verify

- [x] `./gradlew compileJava` — zero error
- [x] `gitnexus_detect_changes()` — scope sesuai
- [x] `git diff` — verifikasi hanya file terkait yang berubah

---

## Ship

- [ ] `git add` batch tunggal di akhir
- [ ] `git diff --cached` menampilkan konten
- [ ] Commit: `fix(penggajian): GajiBatchRoot index endpoints ListResult → PageResult (ADR-0040)`
- [ ] `bd close kepegawaian-59mu`
- [ ] `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin"
- [ ] Post-commit sanity `./gradlew clean compileJava`; bila gagal → `fix()` commit (JANGAN amend)

---

## Guardrails

- NEVER edit simbol tanpa `gitnexus_impact` dulu; NEVER abaikan HIGH/CRITICAL
- NEVER rename/move dengan find-and-replace — pakai `gitnexus_rename` / `git mv`
- NEVER commit tanpa `gitnexus_detect_changes()`; NEVER amend (buat `fix()` commit)
- beads = SATU-SATUNYA tracker — tanpa TodoWrite / markdown TODO

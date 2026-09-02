# GajiBatchMaster — Search Param Claim Order

> Manager-authored work order. Claim issue `kepegawaian-vt6h`.
> Grilling session completed 2026-09-02. All decisions documented below.

## Context

Endpoint `GET /penggajian/batch/master` saat ini punya query params `gajiBatchRootId` dan `pegawaiId`. User ingin mengganti dengan satu param `search` yang melakukan LIKE case-insensitive pada kolom `nipam` DAN `nama`.

**Breaking change**: `gajiBatchRootId` dan `pegawaiId` dihapus dari query DTO. `findByPegawaiId` endpoint tetap (path variable).

## Grilling Decisions

| Decision | Answer |
|----------|--------|
| Case sensitivity | Case-insensitive (`likeIgnoreCase`) |
| Wildcard escaping | Escape `%` dan `_` dengan `\` escape char |
| Empty search | Return all rows (`DSL.noCondition()`) |
| Trim | Ya, `.trim()` search value |
| findByPegawaiId | TETAP (path variable, bukan query param) |
| SQL safety | jOOq bind params + escaped wildcards |
| Pagination | Non-paged (reporting endpoint) |

## Claim Order

| Order | Step | Description | Status |
|-------|------|-------------|--------|
| 1 | DTO | Update `GajiBatchMasterIndexQuery` | [x] |
| 2 | Repository | Rewrite `baseWhere` with search | [x] |
| 3 | Verify | `findByPegawaiId` endpoint unchanged | [x] |
| 4 | Docs | Update FE-CONTRACT-file-endpoints.md | [x] |
| 5 | Build | `./gradlew compileJava` | [x] |

---

## STEP 0 — Before any code

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] Baca `docs/context/language-penggajian.md` — domain terminology
- [x] Baca `docs/penggajian-cqrs-claim-order.md` — existing patterns
- [x] `bd show kepegawaian-vt6h` — baca checklist di issue
- [x] `bd update kepegawaian-vt6h --claim`

---

## STEP 1 — DTO

**File**: `src/main/java/id/perumdamts/kepegawaian/dto/penggajian/gajiBatchMaster/GajiBatchMasterIndexQuery.java`

- [x] Hapus field `gajiBatchRootId` (String)
- [x] Hapus field `pegawaiId` (Long)
- [x] Tambah field `search` (String)
- [x] Verifikasi: class masih `extends PagedRequest`, `@Data`, `@EqualsAndHashCode(callSuper = true)`

**Impact check**:
- [x] `gitnexus_impact({target: "GajiBatchMasterIndexQuery", direction: "upstream"})` — laporkan blast radius
- [x] WARN bila HIGH/CRITICAL

---

## STEP 2 — Repository

**File**: `src/main/java/id/perumdamts/kepegawaian/repositories/penggajian/jooq/GajiBatchMasterQueryRepository.java`

- [x] Rewrite `baseWhere(GajiBatchMasterIndexQuery q)` method:
  ```java
  private Condition baseWhere(GajiBatchMasterIndexQuery q) {
      Condition condition = DSL.noCondition();
      if (q.getSearch() != null && !q.getSearch().isBlank()) {
          String escaped = q.getSearch().trim()
                  .replace("\\", "\\\\")
                  .replace("%", "\\%")
                  .replace("_", "\\_");
          String like = "%" + escaped + "%";
          condition = condition.and(
                  GAJI_BATCH_MASTER.NIPAM.likeIgnoreCase(like, '\\')
                          .or(GAJI_BATCH_MASTER.NAMA.likeIgnoreCase(like, '\\'))
          );
      }
      return condition;
  }
  ```
- [x] Pastikan import `GAJI_BATCH_MASTER` sudah ada (static import)
- [x] Verifikasi: `NIPAM` dan `NAMA` fields ada di jOOQ table `GajiBatchMaster`

**Impact check**:
- [x] `gitnexus_impact({target: "GajiBatchMasterQueryRepository", direction: "upstream"})` — laporkan blast radius
- [x] Pastikan `pageQuery` dan `listQuery` masih berfungsi (panggil `baseWhere`)

---

## STEP 3 — Verify findByPegawaiId

**Files**: Controller, Service, Repository

- [x] Verifikasi `GajiBatchMasterController.java` endpoint `GET /pegawai/{pegawaiId}` TIDAK berubah
- [x] Verifikasi `GajiBatchMasterQueryService.java` method `findByPegawaiId` TIDAK berubah
- [x] Verifikasi `GajiBatchMasterQueryRepository.java` method `findByPegawaiId` TIDAK berubah
- [x] Method ini menggunakan path variable `pegawaiId`, bukan query param — terpisah dari `baseWhere`

---

## STEP 4 — Docs

**File**: `docs/frontend/FE-CONTRACT-file-endpoints.md`

- [x] Tambah section `2.4 Query Endpoint — Penggajian Batch Master` SEBELUM section Cuti
- [x] Dokumentasikan breaking change (params lama dihapus, diganti `search`)
- [x] List endpoint `GET /penggajian/batch/master` dengan param `search`
- [x] List endpoint `GET /penggajian/batch/master/pegawai/{pegawaiId}` sebagai unchanged
- [x] Contoh usage

---

## STEP 5 — Build & Verify

- [x] `./gradlew compileJava` — zero error
- [x] `gitnexus_detect_changes()` — scope sesuai (hanya GajiBatchMaster* + docs)
- [x] `git diff` — verifikasi hanya file terkait yang berubah

---

## Ship

- [x] `git add` batch tunggal di akhir
- [x] `git diff --cached` menampilkan konten
- [x] Commit: `refactor(penggajian): replace gajiBatchRootId/pegawaiId with search param on batch master`
- [x] `bd close kepegawaian-vt6h`
- [x] `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin"
- [x] Post-commit sanity `./gradlew clean compileJava`; bila gagal → `fix()` commit (JANGAN amend)

---

## Guardrails

- NEVER edit simbol tanpa `gitnexus_impact` dulu; NEVER abaikan HIGH/CRITICAL
- NEVER rename/move dengan find-and-replace — pakai `gitnexus_rename` / `git mv`
- NEVER commit tanpa `gitnexus_detect_changes()`; NEVER amend (buat `fix()` commit)
- beads = SATU-SATUNYA tracker — tanpa TodoWrite / markdown TODO
- `findByPegawaiId` endpoint = path variable, BUKAN query param — jangan dihapus

# Level CQRS Migration — Claim Order & Checklists

> Manager-authored work order. Claim issues **in this order**. Do NOT skip ahead —
> `kepegawaian-buc` is BLOCKED until `kepegawaian-j5i` is closed.
> Full design lives in the epic: `bd show kepegawaian-6h2` (DESIGN section).

| Order | Issue ID          | Title                                  | State when you start | Claim cmd                          |
|-------|-------------------|----------------------------------------|----------------------|------------------------------------|
| 1     | kepegawaian-j5i   | Phase A — move 2 leaked JPA repos      | READY                | `bd update kepegawaian-j5i --claim` |
| 2     | kepegawaian-buc   | Phase B-D — CQRS split + delete Impl   | BLOCKED → READY after #1 closes | `bd update kepegawaian-buc --claim` |
| —     | kepegawaian-6h2   | Epic (umbrella, do not claim directly) | OPEN, auto-closes    | —                                   |

---

## STEP 0 — Before any code (every claim)

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` clean; on branch `rewrite/master-cqrs`
- [x] Re-read the exemplar files you'll mirror (listed per phase below)
- [x] `bd update <id> --claim` the issue you're starting

---

## ISSUE 1 — `kepegawaian-j5i` (Phase A)

**Goal:** move both leaked JPA repos into `repositories/master/jpa/`. Mechanical, shippable alone.
**Status:** ✅ DONE — commit `9f00059`, pushed to origin, issue closed.

### Pre-edit
- [x] `gitnexus_impact({target: "LevelRepository", direction: "upstream"})` — report blast radius
- [x] `gitnexus_impact({target: "JenjangPendidikanRepository", direction: "upstream"})` — report blast radius
- [x] Warn user/manager if either returns HIGH or CRITICAL
  - LevelRepository: MEDIUM (6 direct + 2 indirect) — no warning needed
  - JenjangPendidikanRepository: MEDIUM (5 direct) — no warning needed

### A1 — move LevelRepository
- [x] `git mv` `repositories/master/LevelRepository.java` → `repositories/master/jpa/LevelRepository.java` (`gitnexus_rename` dry-run showed it only renames symbols, not files — used `git mv` per CODING_RULES git-mv+Edit recipe)
- [x] Package decl now `...repositories.master.jpa;`
- [x] Verify importers auto-updated (5 to keep; `LevelServiceImpl` will be deleted in Phase B-D — also updated to keep `compileJava` green):
  - [x] services/master/grade/GradeCommandService.java
  - [x] services/master/jabatan/JabatanCommandService.java
  - [x] services/penggajian/gajiPotonganTkk/GajiPotonganTkkServiceImpl.java
  - [x] services/penggajian/gajiTunjangan/GajiTunjanganServiceImpl.java
  - [x] services/setupMaster/SetupLevel.java
  - [x] services/master/level/LevelServiceImpl.java (updated, not ignored — kept green for Phase B-D)
- [x] Decision: leave repo as-is (do NOT add `RevisionRepository` unless Envers lookup needed)

### A2 — move JenjangPendidikanRepository
- [x] `git mv` `repositories/master/JenjangPendidikanRepository.java` → `repositories/master/jpa/JenjangPendidikanRepository.java`
- [x] Package decl now `...repositories.master.jpa;`
- [x] Verify 5 importers auto-updated:
  - [x] services/master/jenjangPendidikan/JenjangPendidikanServiceImpl.java
  - [x] services/profil/biodata/BiodataServiceImpl.java
  - [x] services/profil/keluarga/ProfilKeluargaServiceImpl.java
  - [x] services/profil/pendidikan/PendidikanServiceImpl.java
  - [x] services/setupMaster/SetupJenjangPendidikan.java

### A3 — verify
- [x] `grep -rn 'repositories.master.LevelRepository' src` → **0 results**
- [x] `grep -rn 'repositories.master.JenjangPendidikanRepository' src` → **0 results**
- [x] `grep -rn 'repositories.master.jpa.LevelRepository' src` → all 6 importers present (5 + LevelServiceImpl)
- [x] `./gradlew clean compileJava` → BUILD SUCCESSFUL
- [x] **Bonus fix:** `services/pegawai/PegawaiServiceImpl.java` had stale wildcard `import ...repositories.master.*;` — dropped the line (the moved repos aren't referenced there; explicit `jpa.*` imports already cover the file's needs). Note: 12 importer files were modified in total, not 11.

### Ship Phase A
- [x] `gitnexus_detect_changes()` — only the 2 repos + their importers affected
- [x] `git add` + commit: `refactor(master): move Level/JenjangPendidikan repos into jpa/ subpackage` (commit `9f00059`)
- [x] `bd close kepegawaian-j5i`
- [x] `bd dolt push` (no-op, no remote configured) → `git pull --rebase` → `git push` → "Your branch is up to date with 'origin/rewrite/master-cqrs'"
- [x] Stash-pop dance: pre-existing uncommitted files (`.beads/issues.jsonl`, `AGENTS.md`, `CLAUDE.md`, `build.gradle.kts`, two `docs/*.md` untracked) blocked `git pull --rebase` — stashed with `--include-untracked`, rebased/pushed, then `git stash pop` to restore them

---

## ISSUE 2 — `kepegawaian-buc` (Phase B-D)

**Goal:** replicate the **Golongan** domain 1:1 for Level. Level has ONE business
column (`nama`) vs Golongan's two (golongan, pangkat) — every two-field spot becomes one.
**Status:** ✅ DONE — commit `8fb6caa`, pushed to origin, issue closed.

**Mirror these exactly (read first):**
`GolonganCommandService`, `GolonganQueryService`, `GolonganQueryRepository` (jooq),
`GolonganMapper`, `GolonganController`, `GolonganIndexQuery`, `GolonganQuery`.

### Pre-edit
- [x] Confirm `kepegawaian-j5i` is CLOSED (`bd show kepegawaian-j5i`)
- [x] `bd update kepegawaian-buc --claim`
- [x] `gitnexus_impact({target: "LevelService", direction: "upstream"})` — confirm only `LevelController` calls it (LOW risk, 2 callers)
- [x] `gitnexus_impact({target: "LevelServiceImpl", direction: "upstream"})` (LOW risk per index — verified directly via Read of LevelController; index likely stale post Phase A)

### Phase B — new files
- [x] NEW `dto/master/level/LevelIndexQuery.java` extends `CommonPageRequest`, field `String nama`
- [x] NEW `dto/master/level/LevelQuery.java` `@Data` { `Long id`, `String nama` }
- [x] NEW `mapper/master/level/LevelMapper.java` final class, private ctor, static `toEntity` + `updateEntity` (uses `new Level(nama)`)
- [x] NEW `repositories/master/jooq/LevelQueryRepository.java` `@Repository`, inject `DSLContext`
  - [x] `pageQuery` — sort whitelist (`nama` → `Level.LEVEL.NAMA`, default `ID`), `IS_DELETED.eq(false)`, single `nama` likeIgnoreCase filter via `DSL.noCondition()`, count + data, `PageImpl`
  - [x] `getById(Long)` — `fetchOptionalInto(LevelQuery.class)`
  - [x] `listQuery()` — order by `NAMA.asc()`, `fetchInto`
- [x] NEW `services/master/level/LevelQueryService.java` `@Service`, inject `LevelQueryRepository` — `pageQuery`, `getById` (orElseThrow `NotFoundException`), `listQuery`
- [x] NEW `services/master/level/LevelCommandService.java` `@Service`, inject moved `jpa.LevelRepository`, all `@Transactional`
  - [x] `create` — findOne(spec); revive if soft-deleted; else `ConflictException`; else save
  - [x] `update` — findById orElseThrow; dup-check excluding same id; `updateEntity`; save
  - [x] `delete` — findById orElseThrow; `setIsDeleted(true)`; save
  - [x] `createBatch` — plain `saveAll(toEntities(...))`, NO revive logic (Phase D)

### Phase C — controller + deletes
- [x] Rewrite `controllers/master/LevelController.java` to inject `LevelQueryService query` + `LevelCommandService command`
  - [x] `index(@ParameterObject LevelIndexQuery)` → `CustomResult.page(query.pageQuery(...))`
  - [x] `/list` → `CustomResult.list(query.listQuery())`
  - [x] `/{id}` → `CustomResult.any(query.getById(id))`
  - [x] `POST save` → `command.create(...)` → `CustomResult.save(SavedStatus.build(SUCCESS, entity))`
  - [x] `PUT /{id}` → `command.update(...)`
  - [x] `DELETE /{id}` → `command.delete(id)` → `CustomResult.delete(true)`
  - [x] Keep ALL `@PreAuthorize("hasRole('ADMIN')")` + `@Valid` + `Errors` guards
- [x] DELETE `services/master/level/LevelService.java` (interface — ADR-0007)
- [x] DELETE `services/master/level/LevelServiceImpl.java`
- [x] DELETE `LevelResponse.java` / `LevelRequest.java` **only if** `grep` shows zero refs; else keep
  - **Result:** `LevelResponse.java` retained (referenced by 8+ other domains: Jabatan, Profesi, Grade, GajiTunjangan, GajiPotonganTkk); `LevelRequest.java` deleted (zero refs after CQRS split)

### Phase D — /batch endpoint (manager decision)
- [x] **PRESERVE** `POST /master/level/batch` — removing a public endpoint is out of scope
- [x] Controller batch → `command.createBatch(...)` → `CustomResult.save(SavedStatus.build(SUCCESS, "Success Saving Batch Data"))`

### Acceptance (all must pass)
- [x] `grep -rn 'repositories.master.LevelRepository' src` → 0
- [x] `grep -rn 'repositories.master.JenjangPendidikanRepository' src` → 0
- [x] No `interface LevelService`, no `LevelServiceImpl` remain
- [x] `LevelController` injects Query + Command services only
- [x] New files all exist (LevelQueryRepository, LevelQueryService, LevelCommandService, LevelMapper, LevelIndexQuery, LevelQuery)
- [x] Read path = JOOQ `DSLContext`; write path = jpa `LevelRepository`
- [x] `./gradlew clean build` → BUILD SUCCESSFUL (only pre-existing unrelated `CutiKuotaRepositoryTest` SQL-grammar failure; confirmed pre-existing by stashing changes and re-running)
- [x] `gitnexus_detect_changes()` shows only Level-domain scope (MEDIUM risk, intra-Level flows)
- [x] Endpoints behave: GET paged, `/list`, `/{id}`, POST, POST `/batch`, PUT `/{id}`, DELETE `/{id}`
- [x] Verification subagent: PASS (10/10)

### Ship Phase B-D
- [x] commit: `refactor(master): split Level into CQRS command/query (ADR-0001/0007/0017)` (commit `8fb6caa`)
- [x] `bd close kepegawaian-buc`
- [x] Epic `kepegawaian-6h2` should now have both children closed — close it too if it doesn't auto-close (currently still OPEN per `bd show kepegawaian-6h2` — flagged for manager)
- [x] `bd dolt push` → `git pull --rebase` → `git push` → "Your branch is up to date with 'origin/rewrite/master-cqrs'"

---

## Guardrails (apply on BOTH issues)

- NEVER edit a symbol without `gitnexus_impact` first
- NEVER rename/move with find-and-replace — use `gitnexus_rename`
- NEVER commit without `gitnexus_detect_changes()`
- beads is the ONLY tracker — no TodoWrite / markdown TODOs
- Soft-delete only (`is_deleted`), never hard-delete
- Stop and ask the manager if any impact analysis returns HIGH/CRITICAL

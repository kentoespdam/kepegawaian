# CLAUDE.md

Canonical guidance. **AGENTS.md defers here.**

## Worktree

- **Ini** (`rewrite/master-cqrs`) — kode baru.
- **`../kepegawaian-legacy`** (tag `legacy-snapshot`, read-only) — kode lama.

Dipisah agar tidak compile bareng. Detail: [WORKTREE.md](WORKTREE.md).

## Build & Run

Java 21, Spring Boot 3.5.6, Gradle.

```bash
./gradlew build       # Build
./gradlew bootRun     # Dev profile
./gradlew test        # All tests
./gradlew clean build # Clean build
```

## Architecture

Employee management (kepegawaian) for PERUMDAMTS — Spring Boot REST API.

**Stack:** MariaDB (JPA/Hibernate + Envers), Redis, Kafka, Appwrite (JWT).
**Source:** `src/main/java/id/perumdamts/kepegawaian/`

| Domain         | Purpose                                                          |
|----------------|------------------------------------------------------------------|
| `profil/`      | biodata, pendidikan, keahlian, keluarga, pelatihan               |
| `pegawai/`     | core employee records (NIPAM key)                                |
| `master/`      | referensi (organisasi, jabatan, golongan, grade, level, profesi) |
| `cuti/`        | leave, multi-level approval                                      |
| `kepegawaian/` | SK, SP, mutasi, kontrak, terminasi                               |
| `penggajian/`  | payroll, batch processing                                        |

**Patterns:**
- Controllers: `CustomResult.any/list/save/delete()` → `{status, statusText, data, timestamp}`.
- CRUD + `@Valid` + `Errors`; mutating `@PreAuthorize("hasRole('ADMIN')")`; paginated `@ParameterObject`.
- Soft delete: `is_deleted` flag; never hard-delete.
- Audit: `created_at/by`, `updated_at/by` via JPA `AuditAware`; Envers revision history.
- Approval workflows: cuti & profil — PENDING/APPROVED/REJECTED chain.
- IDs: mostly `Long` auto; `Biodata` keyed by `NIK` (String).

**Auth:** Appwrite JWT via `JwtAuthFilter`. Dev profile, no token → hardcoded admin.

**Config:** `application.yml` from env vars. Docker configs in `docker/`.

## Agent Skills

- **Issue tracker:** beads (`bd`, primary) + GitHub Issues (`kentoespdam/kepegawaian`). See `docs/agents/issue-tracker.md`.
- **Triage labels:** needs-triage, needs-info, ready-for-agent, ready-for-human, wont-fix. See `docs/agents/triage-labels.md`.
- **Domain docs:** `CONTEXT.md` + `docs/adr/`. See `docs/agents/domain.md`.

## Issue Tracking (beads)

Run `bd prime` for detailed workflow.

```bash
bd ready              # Available work
bd show <id>          # Issue detail
bd update <id> --claim # Claim work
bd close <id>         # Complete work
```

**Rules:** use `bd` for ALL tracking — no TodoWrite, TaskCreate, markdown TODOs.  
**Session close:** quality gates → `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin".  
Full protocol: [CODING_RULES.md](CODING_RULES.md) "Ship" step.

## GitNexus (Code Intelligence)

Indexed as **kepegawaian**. See `.claude/skills/gitnexus/` for skill files.  
**Stale index:** `npx gitnexus analyze`.

**Pre-edit:** `gitnexus_impact({target, direction: "upstream"})` — report blast radius.  
**Pre-commit:** `gitnexus_detect_changes()` — verify affected scope.  
**Never:** edit without impact, ignore HIGH/CRITICAL risk, rename with find/replace.

# CLAUDE.md

Canonical guidance for agents in this repo. **AGENTS.md and other agent configs defer here.**

## Rewrite — Worktree Setup

Proyek sedang **rewrite** (Spring Boot 4 + CQRS) memakai git worktree:
- **Folder utama** (`rewrite/master-cqrs`) — kode baru, tempat kerja.
- **`../kepegawaian-legacy`** (tag `legacy-snapshot`, read-only) — kode lama utuh, referensi spec saat grilling tiap modul.

Kode lama **sengaja dipisah** agar tidak ikut compile (beda versi Spring Boot). Jangan commit di worktree legacy. Detail lengkap: **[WORKTREE.md](WORKTREE.md)**.

## Build & Run

Java 21, Spring Boot 3.5.6, Gradle.

```bash
./gradlew build                                              # Build
./gradlew bootRun                                            # Run (development profile)
./gradlew test                                               # All tests
./gradlew test --tests "id.perumdamts.kepegawaian.SomeTest"  # Single test class
./gradlew clean build                                        # Clean build
```

## Architecture

Employee management (kepegawaian) system for PERUMDAMTS (water utility), a Spring Boot REST API.

**Stack:** MariaDB (JPA/Hibernate + Spring Data Envers audit), Redis (cache), Kafka (payroll events), Appwrite (JWT auth).

**Source root:** `src/main/java/id/perumdamts/kepegawaian/`

**Layers:** Controllers → Services → Repositories → Entities, organized by domain:
- `profil/` — employee personal data (biodata, education, skills, family, training)
- `pegawai/` — core employee records (keyed by NIPAM)
- `master/` — reference data (organisasi, jabatan, golongan, grade, level, profesi)
- `cuti/` — leave management, multi-level approval workflow
- `kepegawaian/` — employee affairs (SK, SP, mutasi, kontrak, terminasi)
- `penggajian/` — payroll with batch processing

### Key Patterns

- **Response wrappers:** controllers return `CustomResult.any/list/save/delete()` → `{status, statusText, data, timestamp}`. DTOs: `PageResult`, `ListResult`, `SingleResult`, `SavedResult`, `ErrorResult`.
- **Controllers:** standard CRUD with `@Valid` + `Errors` validation; mutating endpoints use `@PreAuthorize("hasRole('ADMIN')")`; paginated lists via `@ParameterObject` request DTOs.
- **Soft delete:** `is_deleted` flag — never hard-delete records.
- **Audit:** `created_at/by`, `updated_at/by` via JPA `AuditAware`; Envers provides full revision history.
- **Approval workflows:** cuti (leave) and profil updates — multi-level PENDING/APPROVED/REJECTED chain.
- **Entity IDs:** mostly `Long` auto-generated; exception: `Biodata` keyed by `NIK` (String).

### Auth

Appwrite JWT validated by `JwtAuthFilter`. In `development` profile with no token, a hardcoded admin user is injected — no Appwrite needed locally.

### Configuration

`application.yml` reads all settings from env vars (DB_HOST, APPWRITE_*, REDIS_*, KAFKA_*, etc.). Docker configs in `docker/` with `development`/`production` profiles.

## Agent Skills

- **Issue tracker:** beads (`bd`, primary) + GitHub Issues on `kentoespdam/kepegawaian`. See `docs/agents/issue-tracker.md`.
- **Triage labels:** needs-triage, needs-info, ready-for-agent, ready-for-human, wontfix. See `docs/agents/triage-labels.md`.
- **Domain docs:** `CONTEXT.md` + `docs/adr/` at repo root. See `docs/agents/domain.md`.

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **kepegawaian** (7657 symbols, 20996 relationships, 300 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/kepegawaian/context` | Codebase overview, check index freshness |
| `gitnexus://repo/kepegawaian/clusters` | All functional areas |
| `gitnexus://repo/kepegawaian/processes` | All execution flows |
| `gitnexus://repo/kepegawaian/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->


<!-- BEGIN BEADS INTEGRATION v:1 profile:minimal hash:ca08a54f -->
## Beads Issue Tracker

This project uses **bd (beads)** for issue tracking. Run `bd prime` to see full workflow context and commands.

### Quick Reference

```bash
bd ready              # Find available work
bd show <id>          # View issue details
bd update <id> --claim  # Claim work
bd close <id>         # Complete work
```

### Rules

- Use `bd` for ALL task tracking — do NOT use TodoWrite, TaskCreate, or markdown TODO lists
- Run `bd prime` for detailed command reference and session close protocol
- Use `bd remember` for persistent knowledge — do NOT use MEMORY.md files

## Session Completion

**When ending a work session**, you MUST complete ALL steps below. Work is NOT complete until `git push` succeeds.

**MANDATORY WORKFLOW:**

1. **File issues for remaining work** - Create issues for anything that needs follow-up
2. **Run quality gates** (if code changed) - Tests, linters, builds
3. **Update issue status** - Close finished work, update in-progress items
4. **PUSH TO REMOTE** - This is MANDATORY:
   ```bash
   git pull --rebase
   bd dolt push
   git push
   git status  # MUST show "up to date with origin"
   ```
5. **Clean up** - Clear stashes, prune remote branches
6. **Verify** - All changes committed AND pushed
7. **Hand off** - Provide context for next session

**CRITICAL RULES:**
- Work is NOT complete until `git push` succeeds
- NEVER stop before pushing - that leaves work stranded locally
- NEVER say "ready to push when you are" - YOU must push
- If push fails, resolve and retry until it succeeds
<!-- END BEADS INTEGRATION -->

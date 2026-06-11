# CLAUDE.md

Canonical guidance for agents in this repo. **AGENTS.md and other agent configs defer here.**

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

Indexed as **kepegawaian** (7573 symbols, 20911 relationships, 300 flows). Use GitNexus MCP tools to understand code, assess impact, navigate. If a tool warns the index is stale, run `npx gitnexus analyze` first.

**Always:**
- Run `gitnexus_impact({target, direction:"upstream"})` BEFORE editing any symbol; report blast radius (callers, affected processes, risk) and WARN on HIGH/CRITICAL before proceeding.
- Run `gitnexus_detect_changes()` BEFORE committing to confirm only expected symbols/flows changed.
- Explore via `gitnexus_query({query})` (process-grouped flows) instead of grep; use `gitnexus_context({name})` for a symbol's callers/callees/flows.

**Never:** edit a symbol without `gitnexus_impact`; ignore HIGH/CRITICAL risk; rename via find-and-replace (use `gitnexus_rename`); commit without `gitnexus_detect_changes()`.

**Resources:** `gitnexus://repo/kepegawaian/{context|clusters|processes|process/{name}}` — overview/freshness, functional areas, all flows, single-flow trace.

**CLI skills** (`.claude/skills/gitnexus/`): exploring (architecture), impact-analysis (blast radius), debugging (trace bugs), refactoring (rename/extract/split), guide (tools/schema), cli (index/status/clean/wiki).

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

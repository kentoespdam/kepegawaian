# CLAUDE.md

Canonical guidance. **AGENTS.md defers here.**

## Worktree

- **Ini** (`rewrite/master-cqrs`) — kode baru.
- **`../kepegawaian-legacy`** (tag `legacy-snapshot`, read-only) — kode lama.

Dipisah agar tidak compile bareng. Detail: [WORKTREE.md](WORKTREE.md).

## Build & Run

Java 25, Spring Boot 4.0.3, Gradle.

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

## Domain Context (Lazy Read)

Context dipecah per modul. **Baca `CONTEXT-MAP.md` dulu**, lalu pilih sub-context yang relevan — jangan baca semua sekaligus.

| Jika tugas menyentuh... | Baca |
|-------------------------|------|
| `master/` (Profesi, Jabatan, Organisasi) | `docs/context/language-master.md` |
| `pegawai/` — terminologi | `docs/context/language-pegawai.md` |
| `pegawai/` atau `kepegawaian/` — keputusan rewrite | `docs/context/decisions-pegawai.md` |
| `profil/` (biodata, pendidikan, updateProfile) | `docs/context/language-profil.md` |
| `cuti/` — terminologi | `docs/context/language-cuti.md` |
| `cuti/` — keputusan rewrite | `docs/context/decisions-cuti.md` |
| Auth, JWT, Spring profiles | `docs/context/language-security.md` |
| Relasi / dependency lintas-modul | `docs/context/relationships.md` |
| ADRs | `docs/adr/` |

## Agent Skills

- **Issue tracker:** beads (`bd`, primary) + GitHub Issues (`kentoespdam/kepegawaian`). See `docs/agents/issue-tracker.md`.
- **Triage labels:** needs-triage, needs-info, ready-for-agent, ready-for-human, wont-fix. See `docs/agents/triage-labels.md`.
- **Domain docs:** multi-context (`CONTEXT-MAP.md` → `docs/context/`). See `docs/agents/domain.md`.

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

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **kepegawaian** (17911 symbols, 39560 relationships, 300 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> Index stale? Run `node .gitnexus/run.cjs analyze` from the project root — it auto-selects an available runner. No `.gitnexus/run.cjs` yet? `npx gitnexus analyze` (npm 11 crash → `npm i -g gitnexus`; #1939).

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows. For regression review, compare against the default branch: `detect_changes({scope: "compare", base_ref: "master"})`.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `query({search_query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `context({name: "symbolName"})`.
- For security review, `explain({target: "fileOrSymbol"})` lists taint findings (source→sink flows; needs `analyze --pdg`).

## Never Do

- NEVER edit a function, class, or method without first running `impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `rename` which understands the call graph.
- NEVER commit changes without running `detect_changes()` to check affected scope.

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

# CODING_RULES

## Workflow

1. **Understand** — Read issue. Clarify ambiguities.
2. **Plan** — Identify files, approach, edge cases. `EnterPlanMode` for nontrivial tasks.
3. **Explore** — `gitnexus_impact` / `gitnexus_context` / `gitnexus_query` before editing. Context7 for library docs.
4. **Write** — Max 120 lines per file. Split if exceeded. Follow conventions.
5. **Test** — Unit tests required for new logic.
6. **Scope** — Out-of-scope errors → file new issue. Do not resolve ad-hoc.
7. **Finish** — Zero errors. Run quality gates (tests, linters, builds).
8. **Ship** — Commit: `<type>: <description>`.  
   `git pull --rebase` → `bd dolt push` → `git push` → verify "up to date with origin".

Sequence: Understand → Plan → Explore → Write → Test → Close issue → Ship

## Git mv + Edit Workflow (HARD INVARIANT)

When moving files with `git mv` and then editing their headers, **the Edit tool writes to the working tree, NOT the index**. If you `git add` before the Edit lands (e.g. in a parallel block), the commit captures the OLD header. This pitfall hit 3x in ADR-0017 wave-1 (tn1, 0fs, yz9) — each time requiring a `fix()` follow-up commit.

**Recipe (mandatory):**

1. `git mv old new` — rename-preserving move
2. `mkdir -p` destination first (git mv does NOT auto-create folders)
3. Read new path (Edit tool refuses on post-mv path until read)
4. Edit header on new path
5. Edit ALL importers (no parallel Edit+Add blocks)
6. **Single `git add` batch at the end** — every modified file in one command
7. Verify with `git diff --cached` — moved files MUST show content lines, not 0
8. **Post-commit sanity:** `./gradlew clean compileJava` — if that fails, the commit captured stale content; make a `fix()` commit (never amend)

**Anti-patterns (forbidden):**
- Parallel `git add` + Edit blocks (race condition: Edit may land after Add snapshot)
- `git add` per-file between Edits (defeats the "single batch" guarantee)
- Amending the broken commit instead of a new `fix()` commit (project policy: never amend)
- Trusting `compileJava UP-TO-DATE` (Gradle content-hash cache can mask missing content)


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

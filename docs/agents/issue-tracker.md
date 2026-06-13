# Issue tracker: beads + GitHub

This repo tracks work in two complementary systems:

- **beads (`bd`)** — the primary tracker for day-to-day engineering work, tasks, and persistent knowledge. Run `bd prime` for the full workflow and command reference.
- **GitHub Issues** — for published issues and PRDs on `kentoespdam/kepegawaian`, via the `gh` CLI. Use for work that needs external visibility or reporter collaboration.

## beads conventions (default for task tracking)

- **Find work**: `bd ready`
- **View**: `bd show <id>`
- **Create**: `bd create --title "..." --body "..."` (run `bd prime` for the full flag reference)
- **Claim**: `bd update <id> --claim`
- **Close**: `bd close <id>`
- **Persistent knowledge**: `bd remember` (do NOT use MEMORY.md files)

Per project rules: use `bd` for ALL task tracking — not TaskCreate or markdown TODO lists.

## GitHub conventions (published issues / PRDs)

- **Create an issue**: `gh issue create --title "..." --body "..."`. Use a heredoc for multi-line bodies.
- **Read an issue**: `gh issue view <number> --comments`, filtering comments by `jq` and also fetching labels.
- **List issues**: `gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'` with appropriate `--label` and `--state` filters.
- **Comment on an issue**: `gh issue comment <number> --body "..."`
- **Apply / remove labels**: `gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **Close**: `gh issue close <number> --comment "..."`

`gh` infers the repo from `git remote -v` automatically when run inside a clone.

## When a skill says "publish to the issue tracker"

Default to beads (`bd create`). If the work needs external visibility, reporter collaboration, or is a PRD meant for GitHub, create a GitHub issue instead.

## When a skill says "fetch the relevant ticket"

Run `bd show <id>` for a beads issue, or `gh issue view <number> --comments` for a GitHub issue.

---
name: beads
description: Use when managing, creating, updating, or closing issue tracking tasks using the Beads (bd) CLI or beads-mcp tool. Activates when tracking work progress, checking task dependencies, or updating issue statuses.
---

# Beads (bd) Issue Tracking Skill

Use the beads skill to manage the project's task lifecycle. Beads uses Dolt (a git-like database) underneath to version-control task dependencies and history.

## Core Workflow

1. **Find Work**: Run `bd ready` to list open issues that are not blocked by any dependencies.
2. **Claim Work**: Run `bd update <id> --claim` to mark the issue as in-progress.
3. **Review Details**: Run `bd show <id>` to read comments, context, and requirements.
4. **Complete Work**: Run `bd close <id>` when finished.
5. **Sync**: Run `bd dolt push` to push the task updates to the remote repository.

## Commands

- `bd prime`: Load AI-optimized workflow context. Run this at the start of a session.
- `bd ready`: Show open tasks with no active blockers.
- `bd list --status=open`: Show all open issues.
- `bd create "Title" -t task -p 2`: Create a new issue (priority 2 is medium/default).
- `bd dep add <issue-id> <depends-on-id>`: Add a dependency.
- `bd dolt push`: Sync beads changes with remote database.

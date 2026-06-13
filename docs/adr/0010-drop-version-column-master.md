# 0010 — Drop the `@Version` / `version` column from rewritten master entities

Status: Accepted
Date: 2026-06-12

## Context

Every old entity inherits `@Version private Integer version` from
`entities/commons/IdsAbstract.java` (line 53). It was assumed to be part of
the audit machinery. It is not.

- `@Version` is Hibernate **optimistic locking** only: it adds `WHERE version = ?`
  to UPDATE statements and throws `ObjectOptimisticLockingFailureException` on a
  stale write. Verified via Hibernate ORM docs (context7): an `@Audited` entity
  with no `@Version` audits correctly — auditing rides entirely on the separate
  `*_AUD` table (`REV`/`REVTYPE`), never on the `version` column.
- The old API never exposes `version`: no master request DTO carries it, no
  response DTO returns it. Concurrent edits are already silently last-write-wins.

## Decision

The rewrite's master base class does **not** declare `@Version`, and the master
Flyway migration does **not** create a `version` column.

This follows deductively from three already-settled decisions:

1. Envers does not need `@Version` (proven above).
2. ADR-0003 scopes Envers to penggajian + kepegawaian — **master is not audited**.
3. Q36 = A — the master write contract does **not** expose optimistic concurrency
   (parity with old last-write-wins behaviour).

With all three true, a `version` column on a master entity would be written by
Hibernate but never read, audited, or checked. KISS → remove it.

## Scope guard

Applies to **rewritten master entities only**. The old `IdsAbstract` is untouched.
When penggajian/kepegawaian are rewritten later, `@Version` is a fresh decision in
those modules — they *are* audited and may have real write contention, so this ADR
must not be read as a blanket removal of optimistic locking project-wide.

## Consequences

- Master tables have no `version` column; migration author must omit it.
- If true lost-update protection is ever needed for a high-contention master table,
  re-introducing `@Version` + the column is a forward migration (backfill `1`) and a
  reversal of Q36 = A — not blocked, just out of current scope.

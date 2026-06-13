# 0014 — GET `/master/x/{id}` on a missing/soft-deleted row returns 404, not 200-null

Status: Accepted
Date: 2026-06-12

## Context

The old master services degrade softly when a primary row is absent — they never
signal "not found" as a status:

- `findById(id)` missing → returns **`null`**, wrapped as HTTP **200** with
  `data: null` (`ProfesiServiceImpl.java:44-45`).
- `update(id, …)` missing → `SavedStatus.build(FAILED, "Unknown Profesi")` — a soft
  failure, not 404 (`ProfesiServiceImpl.java:74-76`).
- `deleteById(id)` missing → returns **`false`** (`ProfesiServiceImpl.java:96-98`).

ADR-0013 already routes the **mutating** paths (update/delete on a missing row)
through a thrown typed not-found → **404**, because those go through the advice. But
GET `/{id}` on a missing row is a **normal return**, not an exception, so ADR-0013
does not settle it. Soft-delete (`@SQLRestriction("is_deleted = FALSE")`) makes a
tombstoned row indistinguishable from a never-existing id at the read layer — both
are simply absent.

The fork: does GET `/{id}` on an absent (missing or soft-deleted) row stay **200
with `data: null`** (old parity) or become **404**?

## Decision

GET `/master/x/{id}` on an absent row (never existed, or soft-deleted and thus
hidden by `@SQLRestriction`) **throws a typed not-found**, which the ADR-0013 advice
renders as `ApiResponse` with status **404**, `data: null`.

GET, update, and delete now agree: "no such row" is always **404**, never a 200 body
the client must null-check, and never a soft `FAILED`.

## Considered Options

- **Uniform 404** (chosen): GET joins update/delete in treating a missing id as 404
  (ADR-0013 already chose 404 for the mutating paths). One consistent meaning of
  "missing" across the whole resource. REST-correct: the client distinguishes
  "found, empty payload" from "does not exist" by status, not by inspecting the
  body. Cost: a read-side contract change from old clients that relied on 200-null.
- **200 with `data: null`** (old parity, rejected): byte-for-byte compatible with
  old clients on the read side, but read and write then disagree on what "missing"
  means, and a 200 hides absence — every client must null-check a success body to
  detect a missing row.

## Consequences

- The query side gains a not-found throw on `/{id}`; it is the same typed
  exception ADR-0013 maps to 404, so no new advice branch is needed.
- A soft-deleted row reads as 404 (it is hidden by `@SQLRestriction`), identical to
  a never-existing id — intentional: the read API does not expose tombstones.
- Applies to single-resource GET `/{id}` across master. Collection/index endpoints
  are unaffected (an empty page is still 200 with an empty list).
- This is a deliberate break from old 200-null read parity; clients that detected
  absence via a null body must switch to status-code handling.

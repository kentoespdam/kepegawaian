# 0031 — Batch/workflow endpoints return `SavedResult<String>` with literal `"success"`

Status: Accepted
Date: 2026-07-09

## Context

ADR-0013 locks one success envelope, `ApiResponse<T>`, and ADR-0014 makes a
missing row a thrown 404. Both settle the **single-entity** create/update path:
the `data` slot carries the new `Long` id (`SavedStatus.build(SUCCESS, entity.getId())`).

But a second family of mutating endpoints does **not** produce one id:

- **Batch saves** — `saveBatch(List<...>)` in `PegawaiCommandService`,
  `DasarGajiCommandService`, `DetailDasarGajiCommandService`,
  `GajiBatchRootCommandService`: persist N rows in one transaction.
- **Workflow transitions** — `GajiBatchRootWorkflowCommandService` reprocess /
  verify1 / verify2 / accept, and `GajiBatchMasterCommandService.uploadPotonganTambahan`:
  mutate state without returning a fresh identity.

The old code stuffed these into the `data` slot inconsistently: `SavedStatus.build(SUCCESS, null)`
(`PegawaiCommandService.saveBatch:112`), or a free-text string like
`"Save Batch Dasar Gaji Success"`. The generic `SavedStatus<?>` let each service
invent its own payload, and controllers double-cast
`(ResponseEntity<SavedResult<Object>>)(ResponseEntity<?>) …` to erase the wildcard.

The fork, once `SavedStatus<?>` is banned in favour of a concrete type: what does
the `data` slot carry when there is **no single id** to return?

## Decision

Batch and workflow endpoints return **`SavedStatus<String>`** whose value on
success is the literal **`"success"`**. The controller signature is
`ResponseEntity<SavedResult<String>>` — no wildcard, no cast.

- `status` / `statusText` (201 / "Created") already carry the outcome.
- Failure never travels in `data`: a missing referenced row throws
  `NotFoundException` → 404, a duplicate throws `ConflictException` → 409, and any
  other exception surfaces via the ADR-0013 advice. The old
  `try { … } catch (Exception e) { return SavedStatus.build(FAILED, e.getMessage()); }`
  wrapper is deleted (it duplicated the advice).

The `data` slot for these endpoints is therefore a **constant** — it exists only
to keep the envelope shape uniform, not to carry information.

## Considered Options

- **Literal `"success"` string** (chosen): trivially uniform, and honest about the
  fact that a batch/workflow op has no single identity to report. The envelope's
  `status`/`statusText` already tell the client the outcome; `data` is a
  placeholder. Zero coupling to the number or identity of affected rows, so the FE
  contract never shifts when batch internals change. Cost: `data` carries no useful
  payload — a client that wants "how many rows" or "which ids" must ask elsewhere.
- **Count of affected rows (`SavedResult<Integer>`)** (rejected): more informative,
  but invents a contract the FE never asked for, and is ambiguous for workflow
  transitions (verify/accept touch no countable row set). Would force each service
  to thread a count it does not naturally have.
- **List of created ids (`SavedResult<List<Long>>`)** (rejected): meaningful only
  for `saveBatch`, undefined for workflow transitions — so the two families would
  diverge again, re-introducing the per-service payload drift this ADR removes.
- **HTTP 204 No Content** (rejected): semantically clean for "done, nothing to
  return", but breaks the ADR-0013 invariant that **every** response — success or
  error — is a parseable `ApiResponse<T>` body. The FE would need a second code
  path for empty responses. KISS loses.

## Consequences

- One concrete return type across both mutating families: single-entity →
  `SavedResult<Long>` (id), batch/workflow → `SavedResult<String>` (`"success"`).
  `SavedStatus<?>` is eliminated from the command layer.
- Controllers drop the `(ResponseEntity<SavedResult<Object>>)` double-cast; the
  typed signature flows straight from the service.
- `data: "success"` is a sentinel, not data — documented here so a future reader
  does not mistake it for a payload that should carry row counts or ids.
- Enforced per-file via the checklist in
  [`docs/refactor/typed-controller-result.md`](../refactor/typed-controller-result.md)
  (epic `kepegawaian-51j`).

# 0031 — Batch/workflow endpoints return `SavedResult<String>` (`"{n} success"` / `"success"`)

Status: Accepted
Date: 2026-07-09

## Context

ADR-0013 locks one success envelope, `ApiResponse<T>`, and ADR-0014 makes a
missing row a thrown 404. Both settle the **single-entity** create/update path:
the `data` slot carries the new `Long` id (`SavedStatus.build(SUCCESS, entity.getId())`).

But a second family of mutating endpoints does **not** produce one id. It splits
into two sub-families:

- **Batch saves** — `saveBatch(List<...>)` in `PegawaiCommandService`,
  `DasarGajiCommandService`, `DetailDasarGajiCommandService`,
  `GajiBatchRootCommandService`, and `GajiBatchMasterCommandService.uploadPotonganTambahan`:
  persist **N rows** in one transaction. N is known — it is `requests.size()`.
- **Workflow transitions** — `GajiBatchRootWorkflowCommandService` reprocess /
  verify1 / verify2 / accept: mutate the **state of a single batch** without a
  natural, countable row set to report.

The old code stuffed these into the `data` slot inconsistently: `SavedStatus.build(SUCCESS, null)`
(`PegawaiCommandService.saveBatch:112`), or a free-text string like
`"Save Batch Dasar Gaji Success"`. The generic `SavedStatus<?>` let each service
invent its own payload, and controllers double-cast
`(ResponseEntity<SavedResult<Object>>)(ResponseEntity<?>) …` to erase the wildcard.

The fork, once `SavedStatus<?>` is banned in favour of a concrete type: what does
the `data` slot carry when there is **no single id** to return?

## Decision

Batch and workflow endpoints return **`SavedResult<String>`** — no wildcard, no
cast. The controller signature is `ResponseEntity<SavedResult<String>>`. The
string value depends on the sub-family:

- **Batch save** (has a countable input) → **`"{n} success"`** where `n` is the
  number of rows processed, e.g. `"5 success"`. The count comes for free from
  `requests.size()` (or the number of rows written) — the service already holds
  it. This is more informative than a bare token: the client sees how much the
  batch actually touched.
- **Workflow transition** (no countable row set) → the literal **`"success"`**.
  Reprocess / verify1 / verify2 / accept move the status of *one* batch; there is
  no honest "row count" to report, so no number is invented.

Common to both:

- `status` / `statusText` (201 / "Created") already carry the outcome.
- Failure never travels in `data`: a missing referenced row throws
  `NotFoundException` → 404, a duplicate throws `ConflictException` → 409, and any
  other exception surfaces via the ADR-0013 advice. The old
  `try { … } catch (Exception e) { return SavedStatus.build(FAILED, e.getMessage()); }`
  wrapper is deleted (it duplicated the advice).

The distinction is deliberate: the number appears **only when it is real**. A batch
knows how many rows it wrote; a workflow transition does not, so it does not
pretend to.

## Considered Options

- **`"{n} success"` for batch, `"success"` for workflow** (chosen): the count is
  emitted exactly where it is meaningful (the caller passed N rows and learns N
  were saved) and omitted where it would be fabricated (a status transition has no
  countable set). Stays a single concrete type, `SavedResult<String>`, so the
  envelope and the controller signature never diverge between the two families.
  Cost: the string is now semi-structured — a client that machine-parses the count
  must split on the space. Accepted: the count is a human-readable convenience, not
  a typed contract; machine clients should rely on `status`/`statusText`.
- **Literal `"success"` for both** (rejected — was the original 0031 decision):
  maximally uniform, but discards information the batch path already has in hand.
  The user asked for the row count precisely because a bare `"success"` was
  uninformative for batch saves.
- **Typed count for both (`SavedResult<Integer>`)** (rejected): forces workflow
  transitions to invent a count they do not have (a fake `1`, or a misleading `0`),
  re-introducing the per-service payload drift this ADR removes. Embedding the
  count in the String keeps *one* return type while letting the workflow path stay
  honest with a plain `"success"`.
- **List of created ids (`SavedResult<List<Long>>`)** (rejected): meaningful only
  for `saveBatch`, undefined for workflow transitions, and heavier than the FE
  asked for.
- **HTTP 204 No Content** (rejected): breaks the ADR-0013 invariant that **every**
  response is a parseable `ApiResponse<T>` body; the FE would need a second code
  path for empty responses.

## Consequences

- One concrete return type across both mutating families: single-entity →
  `SavedResult<Long>` (id), batch/workflow → `SavedResult<String>`
  (`"{n} success"` or `"success"`). `SavedStatus<?>` is eliminated from the
  command layer.
- Controllers drop the `(ResponseEntity<SavedResult<Object>>)` double-cast; the
  typed signature flows straight from the service.
- `data` on the batch path carries a human-readable count string, not a typed
  number — documented here so a future reader treats `status`/`statusText` (not the
  string) as the machine-readable outcome.
- Enforced per-file via the checklist in
  [`docs/refactor/typed-controller-result.md`](../refactor/typed-controller-result.md)
  (epic `kepegawaian-51j`).

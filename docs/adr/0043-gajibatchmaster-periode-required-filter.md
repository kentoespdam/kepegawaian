# ADR-0043: GajiBatchMaster — `periode` required exact-match filter

## Status

Accepted — 2026-09-02

## Context

`GET /penggajian/batch/master` endpoint (method `getGajiBatchMasterByPeriode`) has no `periode` query parameter despite the method name implying it. The `GAJI_BATCH_MASTER.PERIODE` column exists and is indexed. The sister endpoint `GET /penggajian/batch/root` already supports `periode` filtering. The `search` refactor (ADR scope) removed `gajiBatchRootId` and `pegawaiId` but forgot to add `periode`.

## Decision

1. **Add `String periode`** to `GajiBatchMasterIndexQuery` with `@NotBlank` — it is **required**.
2. **Exact match** (`PERIODE.eq(value)`) — not LIKE. The indexed column makes this efficient; callers always know the exact periode value (e.g., `"2026-09"`).
3. **Apply to both query endpoints**: `GET /batch/master` and `GET /batch/master/pegawai/{pegawaiId}` (they share the DTO).
4. When `periode` is missing → **HTTP 400** (Bean Validation via `@NotBlank`).

## Consequences

- **FE must always send `?periode=...`** on both batch-master query endpoints.
- No ambiguity: the query always narrows to a single periode, matching the business model (one batch run = one periode).
- `baseWhere` in `GajiBatchMasterQueryRepository` becomes simpler (no null-check needed — validated upstream).

## Alternatives considered

| Alternative | Why rejected |
|-------------|-------------|
| LIKE substring match | Overkill — caller always knows exact periode. Less efficient. |
| Optional (return all if omitted) | Too broad — a careless call returns entire table. The endpoint name is "By Periode" for a reason. |

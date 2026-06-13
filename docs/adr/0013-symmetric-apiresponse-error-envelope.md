# 0013 — Error path reuses the `ApiResponse<T>` envelope, not `ProblemDetail`

Status: Accepted
Date: 2026-06-12

## Context

The rewrite locks one success envelope: `ApiResponse<T>(int status, String
statusText, T data, Instant timestamp)`. The error path was still open.

Three facts force the shape of that path:

- The old code has **no** `@ControllerAdvice` anywhere. Every controller threads
  an `Errors` param and returns `ErrorResult.build(errors)` inline. That path can
  only ever produce HTTP **400** — it has no throw, no advice, no non-400 branch.
- The rewrite has **three** distinct error sources: `@Valid` body validation →
  400; bad FK from `getReferenceById` → **409** (ADR-0008); not-found on
  update/delete → **404**. The inline 400-only pattern cannot express 409/404, so
  *some* centralization via `@RestControllerAdvice` is forced regardless.
- Spring Boot 4 / Spring Framework 7 ship **`ProblemDetail` (RFC 9457)** as the
  framework-default error shape: auto-registered Jackson mixin, emitted by the
  built-in `ResponseEntityExceptionHandler`, media type
  `application/problem+json`. Verified via context7. So "do nothing custom" now
  yields a *different* body shape for errors than for success.

The genuine fork: when the advice translates an exception, does the body stay the
**same envelope as success** (`ApiResponse`) or switch to the framework-default
**`ProblemDetail`**?

## Decision

A single `@RestControllerAdvice` translates typed exceptions into the **same**
`ApiResponse<Object>` envelope used for success:

- `status` / `statusText` carry the HTTP code and message.
- `data` carries the field-error list for 400 validation, and `null` for 409/404.
- `timestamp` is the same `Instant` ISO-8601 as success.

The advice handles, at minimum:

- bean-validation failure (`MethodArgumentNotValidException`) → 400, `data` = list
  of `field : message` strings (parity with old `ErrorResult.build(Errors)`),
- bad-FK `EntityNotFoundException` raised by `getReferenceById` → **409**
  (ADR-0008),
- not-found on update/delete → **404**.

`ProblemDetail` is **not** used. `ApiResponse` is the only shape the client parses,
success or failure.

## Considered Options

- **Symmetric `ApiResponse<T>` error envelope** (chosen): the whole motivating goal
  of the rewrite is a predictable, consistent client contract. Success is already
  `ApiResponse<T>`; reusing it for errors means the FE parses **one** shape for the
  entire API. The FE never consumes RFC 9457 `type`/`instance` URIs. KISS. Cost:
  we deliberately opt out of the framework default and own a small advice +
  exception-to-envelope mapping.
- **Framework-default `ProblemDetail` (RFC 9457) for errors** (rejected): keep
  `ApiResponse<T>` for success but let errors flow through Spring 7's first-class
  `ProblemDetail`. Standards-compliant and less custom code, but the client now
  parses **two** different shapes depending on outcome — asymmetric envelopes for
  no consumer that asked for RFC 9457.
- **Old per-controller inline `ErrorResult.build()`** (not viable): cannot reach
  409/404 (400-only, no throw, no advice) and contradicts the single locked
  envelope. Not a live option.

## Consequences

- One `@RestControllerAdvice` becomes a foundation-slice artifact, built alongside
  `ApiResponse` / `PageResult` before any master endpoint.
- The rewrite introduces typed exceptions for the non-validation paths (e.g. a
  not-found exception for 404; bad-FK surfaces as `EntityNotFoundException` per
  ADR-0008 → mapped to 409). Controllers stop returning `ErrorResult` inline; they
  throw, and the advice shapes the body.
- We forgo RFC 9457 interoperability. If a future external/3rd-party consumer ever
  requires `application/problem+json`, re-introducing `ProblemDetail` for a subset
  of endpoints is a forward change — not blocked, just out of current scope.
- Consistent with the project's KISS value and the locked success envelope: one
  contract shape end-to-end.

# Profile-conditional authentication

The security module is wired through two profile-selected `SecurityFilterChain` beans rather than one filter that branches on the active profile at runtime. In all profiles **except** `development`, the chain validates an Appwrite-issued JWT (`JwtAuthFilter`) and requires authentication on every request. Under the `development` profile, a dev-only filter injects a static principal (`DEV`, roles `ADMIN`+`SYSTEM`) and **no authentication is performed at all**, so the API can be exercised without Appwrite or a token.

**Why:** the dev bypass must be impossible to ship to production by accident. With `@Profile`, the bypass bean is never instantiated outside development; the previous inline `profile.equals("development")` check left the bypass code always on the classpath, one misread `PROFILE` env var away from disabling auth in prod. It also keeps `JwtAuthFilter` ignorant of environments — it only validates tokens.

**Consequence:** development has *no* authorization enforcement. `@PreAuthorize` checks still evaluate, but always against the static ADMIN/SYSTEM principal, so role-restriction bugs cannot surface in dev — they must be tested under a real profile.

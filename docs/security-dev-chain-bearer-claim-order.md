# Claim Order — Security: Dev Chain Validasi Bearer Token + Fallback DevAuth (ADR-0033)

Urutan klaim eksekusi epic beads `kepegawaian-95h` (child: `.jxk`, `.0fn`, `.gbt`) agar alur kerja SecurityFilter dev/prod berubah dari "dev = tanpa autentikasi" menjadi "dev = validasi Bearer bila ada, fallback DEV bila tanpa Bearer". Referensi kebenaran desain: [ADR 0033](adr/0033-dev-chain-bearer-fallback-devauth.md) (baru) + [ADR 0016](adr/0016-profile-conditional-auth.md) (superseded sebagian).

**Prinsip klaim**: 1 issue = 1 PR. Kerjakan berurutan sesuai kolom "Klaim" — child 1 dulu (logika filter), lalu child 2 (konfigurasi chain), lalu child 3 (log noise). Verifikasi tiap child dengan `./gradlew compileJava` + test.

> **Cara pakai**: Buka file ini, klaim sesuai urutan di kolom "Klaim". Tandai checklist `[x]` setelah setiap child selesai di-PR.

---

## A. Klaim berurutan (master list)

| # | Epic | Child | Judul | Tipe | Prioritas | Catatan |
|---|------|-------|-------|------|-----------|---------|
| 1 | `kepegawaian-95h` | `.jxk` | DevAuthFilter trigger tanpa-Bearer + clearContext hanya saat DEV di-inject | **PILOT** | P2 | Logika inti fallback DEV |
| 2 | `kepegawaian-95h` | `.0fn` | dev chain `authenticated()` + `exceptionHandling` di WebSecurity | wave 1 | P2 | Mengubah devFilterChain |
| 3 | `kepegawaian-95h` | `.gbt` | AppwriteClient 401 tanpa `log.error` + JwtAuthFilter short-circuit token blank | wave 1 | P2 | Menghilangkan noise log |

**Urutan tidak boleh diputar**: child `.jxk` (logika filter) mendahului `.0fn` (konfigurasi chain) karena `.0fn` memasang `DevAuthFilter` + `JwtAuthFilter` di chain yang sama — trigger tanpa-Bearer harus sudah benar dulu supaya auth Appwrite tidak terhapus `clearContext()`.

---

## B. Semantik target (acceptance semua child)

| Header `Authorization` di dev | Hasil |
|---|---|
| missing / blank / `Bearer ` kosong / `Basic xxx` | `DevAuthFilter` injek **DEV user** (fallback) |
| `Bearer <token>` valid | `JwtAuthFilter` validasi ke Appwrite → **user asli** |
| `Bearer <token>` invalid/expired | Tidak ada auth → **401 strict** |

Di **prod** (`!development`): chain tidak berubah sama sekali — `JwtAuthFilter` saja, `authenticated()`, tanpa `DevAuthFilter`.

---

## C. Pre-flight checklist (sekali sebelum mulai)

- [x] `bd prime` jalan tanpa error
- [x] Branch `rewrite/master-cqrs` bersih dari noise (`git status`)
- [x] `docs/adr/0033-dev-chain-bearer-fallback-devauth.md` sudah dibaca & dipahami
- [x] `docs/context/language-security.md` sudah dibaca (glossary Lingkungan/Dev User sudah update)
- [x] File terkait dibaca:
  - `src/main/java/id/perumdamts/kepegawaian/config/WebSecurity.java`
  - `src/main/java/id/perumdamts/kepegawaian/config/security/JwtAuthFilter.java`
  - `src/main/java/id/perumdamts/kepegawaian/config/security/DevAuthFilter.java`
  - `src/main/java/id/perumdamts/kepegawaian/config/appwrite/AppwriteClient.java`
  - `src/test/java/id/perumdamts/kepegawaian/config/appwrite/AppwriteClientTest.java`

---

## D. Per-child checklist

### D.1 Child `.jxk` — DevAuthFilter: trigger tanpa-Bearer + clearContext terkondisi

- [x] Issue di-claim via `bd update kepegawaian-jxk --claim`
- [x] Baca `DevAuthFilter.java` — pahami struktur `try/finally` saat ini
- [x] Tambah helper `hasBearerToken(request)`: header != null && startsWith("Bearer ") && substring setelah "Bearer " tidak blank
- [x] Bila `hasBearerToken` = true → `filterChain.doFilter(request, response); return;` (skip total: TIDAK inject, TIDAK clear)
- [x] Bila `hasBearerToken` = false → inject DEV user (logika eksisting) di dalam `try`, `clearContext()` di `finally` **tetap jalan** (karena ini satu-satunya jalur yang inject)
- [x] Konfirmasi: `SecurityContextHolder.getContext().setAuthentication(...)` hanya dipanggil di jalur tanpa-Bearer
- [x] `./gradlew compileJava` hijau
- [x] `gitnexus_detect_changes` bersih: hanya menyentuh `DevAuthFilter.java`

### D.2 Child `.0fn` — WebSecurity: dev chain `authenticated()` + `exceptionHandling`

- [x] Issue di-claim via `bd update kepegawaian-0fn --claim`
- [x] Baca `WebSecurity.java` — bandingkan `jwtFilterChain` vs `devFilterChain`
- [x] `devFilterChain`: tambah `.exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint).accessDeniedHandler(deniedHandler))`
- [x] `devFilterChain`: ganti `.anyRequest().permitAll()` dengan daftar permitAll SAMA dengan prod chain (`/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`, `/auth/**`) + `.anyRequest().authenticated()`
- [x] `devFilterChain`: tambah `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)` **sebelum** `.addFilterBefore(devAuthFilter, ...)` — JwtAuthFilter menangani Bearer, DevAuthFilter fallback
- [x] Konfirmasi filter order: `jwtAuthFilter` di-register sebelum `devAuthFilter` (dalam satu chain dev)
- [x] Pastikan `jwtFilterChain` (prod) TIDAK tersentuh
- [x] `./gradlew compileJava` hijau
- [x] `gitnexus_detect_changes` bersih: hanya menyentuh `WebSecurity.java`

### D.3 Child `.gbt` — AppwriteClient 401 tanpa log.error + JwtAuthFilter short-circuit

- [x] Issue di-claim via `bd update kepegawaian-gbt --claim`
- [x] Baca `AppwriteClient.validateToken()` — saat ini `catch (Exception e) { log.error("JWT Auth Error", e); return null; }`
- [x] Ganti menjadi: `catch (HttpClientErrorException.Unauthorized e) { log.debug(...); return null; }` + `catch (Exception e) { log.error("JWT Auth Error", e); return null; }`
- [x] Cek import `org.springframework.web.client.HttpClientErrorException` ditambahkan
- [x] Baca `JwtAuthFilter.getAuthentication()` — setelah `String token = tokenString.substring(BEARER.length());` tambah: `if (token.isBlank()) return null;` (jangan panggil Appwrite dengan token kosong)
- [x] Cek test `AppwriteClientTest.java` — pastikan test `validateToken_shouldReturnNullOnBadRequest` (atau 401 case) masih hijau; update test bila meng-assert `log.error` (tidak seharusnya)
- [x] `./gradlew compileJava` + `./gradlew test --tests "*AppwriteClientTest*"` hijau
- [x] `gitnexus_detect_changes` bersih: hanya menyentuh `AppwriteClient.java`, `JwtAuthFilter.java`, dan file test terkait

---

## E. Verifikasi per epic

- [x] `./gradlew clean compileJava` hijau (gate tanpa DB)
- [x] `./gradlew test` hijau
- [x] Boot dev (`PROFILE=development`, diverifikasi 2026-08-10 via `--spring.profiles.active=development`):
  - [x] curl tanpa header → 200 principal DEV, `JWT Auth Error` di log = 0
  - [x] curl `Authorization: Bearer <valid>` → **terbukti end-to-end setelah fix gzip (lihat H baris 3)**: `GET /master/level/list` → 200 + data, `userFromToken` = user asli (sebelumnya ter-blokir: session creation 409 di admin API + gzip rusak)
  - [x] curl `Authorization: Bearer <invalid>` → **401 dalam 0.07s** (bukan fallback DEV)
  - [x] curl `Authorization: Basic xxx` → 200 DEV (bukan 401)
  - [x] curl `Authorization: Bearer ` (kosong) → 200 DEV, tidak ada panggilan Appwrite
- [x] Boot prod (`--spring.profiles.active=production`, diverifikasi 2026-08-10): curl tanpa header → **401** (bukan DEV); `/auth/csrf-token` (permitAll) → 200; 0 sebutan `devAuthFilter` di log
- [x] Tidak ada dependency Gradle baru
- [x] 1 commit per child (gaya repo, 3 child + MD + close-state beads)
- [x] `bd close <child-id>` setelah selesai
- [x] Setelah semua child closed → `bd close kepegawaian-95h`

---

## F. Pitfalls

- [ ] **Jangan lupa filter order**: `jwtAuthFilter` harus di-register sebelum `devAuthFilter` di chain dev. Kalau `devAuthFilter` duluan, `clearContext()`-nya bisa menghapus auth hasil JwtAuthFilter (jika trigger tanpa-Bearer salah)
- [ ] **`finally { clearContext() }`** di DevAuthFilter hanya boleh mengeksekusi saat DEV di-inject — jangan clear auth asli Appwrite
- [ ] **Prod chain jangan tersentuh** — scope epic ini hanya dev chain + AppwriteClient + JwtAuthFilter short-circuit
- [ ] **Post-mv re-Read**: kalau ada `git mv`, baca ulang path baru sebelum Edit
- [ ] **Clean compileJava**: tutup epic dengan `./gradlew clean compileJava`

---

## H. Temuan verifikasi runtime (di luar claim order — fix terpisah, 2026-08-10)

Verifikasi section E + debugging lanjutan jalur Bearer valid mengungkap 3 temuan pre-existing yang menghambat verifikasi itu sendiri dan berdampak produksi. Semuanya di-fix dengan commit terpisah + didokumentasikan di sini (bukan bagian epic):

| Temuan | Bukti | Fix |
|--------|-------|-----|
| **DevAuthFilter bocor ke prod** — `@Component` tanpa `@Profile` → Spring Boot auto-register sebagai servlet filter di SEMUA profile; prod `/test` tanpa header → 200 DEV (melanggar safety property ADR-0016/0033 "dev bypass tidak pernah di-wire di prod") | Verifikasi prod awal: HTTP 200 body `AppwriteUser{$id='DEV'}`; setelah fix: 401 | `@Profile("development")` di `DevAuthFilter` + `WebSecurity` pindah ke method-parameter injection `devFilterChain(HttpSecurity, DevAuthFilter)` (constructor injection lama membuat prod gagal boot: *required a bean of type DevAuthFilter*) |
| **JDK HttpClient h2c deadlock** — `RestClient.create()` memakai `HttpClient.newHttpClient()` (default HTTP/2) yang mengirim preface h2c pada URL plain-http; proxy nginx Appwrite `192.168.230.254:82` HTTP/1.1-only tidak pernah menjawab → SEMUA call Appwrite dari app menggantung selamanya (tanpa timeout). Reproduksi deterministik: probe P1/P3 (default HTTP/2) hang 12s+, P2/P4 (`version(HTTP_1_1)`) → 401 dalam 15ms | `GET /account` via curl = 401 dalam 7ms; via JDK HttpClient default = timeout; `git bisect` probe 4 varian | `WebClientConfig`: pin `HttpClient.Version.HTTP_1_1` + `connectTimeout(5s)` + `JdkClientHttpRequestFactory.setReadTimeout(10s)` — juga melindungi layanan lain (`penggajian`, `laporan` plain-http) |
| **Proxy Appwrite `:82` kirim `Content-Encoding: gzip` dengan body bukan gzip** — JDK HttpClient stack (Java 25) mengirim `Accept-Encoding: gzip` otomatis + auto-decompress; proxy debug-fallback (`X-Debug-Fallback: true`, `Server: swoole-http-server`/`Appwrite`) membalas header gzip dengan body yang TIDAK valid → `ZipException: incorrect header check` → `validateToken()` null → anonymous → 401 **"Full authentication is required"** padahal token VALID (curl 200). Gejala menyesatkan: terlihat seperti masalah Authorization header | Token asli + JDK/Spring path → `Error while extracting response` dgn `Caused by: ZipException: incorrect header check`; echo server lokal → JDK 25 kirim `Accept-Encoding: gzip` otomatis; curl tanpa header itu → 200 JSON polos; test `AppwriteUser` strict Jackson 3 PASS (DTO bukan masalah) | `WebClientConfig`: `.defaultHeader(HttpHeaders.ACCEPT_ENCODING, "identity")` — proxy tak pernah compress → JSON polos. Terbukti end-to-end: Bearer valid → `GET /master/level/list` → 200 + data, log `userFromToken: AppwriteUser{...roles=[SYSTEM, ADMIN, USER]}`. Juga melindungi `penggajian`/`laporan`. Regression test: `AppwriteClientTest.productionRestClientConfig_shouldSendAcceptEncodingIdentity` |

**Catatan CHECK 2 (Bearer valid)**: awalnya ter-blokir oleh dua quirk infra di proxy `:82` — (a) session creation tidak bisa di-mint dari admin API (409), (b) gzip rusak (baris tabel ke-3). Setelah fix gzip (2026-08-10), jalur Bearer valid **terbukti end-to-end**: `GET /master/level/list` dengan token asli → HTTP 200 + data, `userFromToken` berisi user asli. Token JWT di-mint lewat jalur lain (bukan admin API) dan tetap divalidasi dengan benar oleh `JwtAuthFilter` → Appwrite `/account`.

---

## G. Out-of-scope (JANGAN dikerjakan di claim-order ini)

| Item | Alasan |
|------|--------|
| `JwtAuthEntryPoint` response format → `CustomResult` envelope | Di luar scope ADR-0033; dicatat di Consequences ADR sebagai catatan |
| Merge `JwtTokenService` ke `JwtAuthFilter` | Sudah ditunda sejak ADR-0029 (Opsi B) |
| Ubah mekanisme pemilihan environment (mis. 1 chain + cek runtime) | Ditolak di ADR-0033 Considered Options |
| Token invalid di dev → fallback DEV | Ditolak (strict: 401) |

---

## REF

- `docs/adr/0033-dev-chain-bearer-fallback-devauth.md` — sumber kebenaran desain (baru)
- `docs/adr/0016-profile-conditional-auth.md` — ADR lama, superseded sebagian
- `docs/context/language-security.md` — glossary Lingkungan/Dev User (sudah update)
- Epic: `kepegawaian-95h`; children: `kepegawaian-jxk`, `kepegawaian-0fn`, `kepegawaian-gbt`

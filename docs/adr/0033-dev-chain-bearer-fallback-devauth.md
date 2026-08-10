# Dev chain memvalidasi Bearer token, fallback Dev User hanya saat tanpa Bearer

> **Status:** accepted — keputusan sesi grilling 2026-08-10. **Menggantikan sebagian [ADR 0016](0016-profile-conditional-auth.md)** (bagian perilaku dev chain); struktur 2 chain `@Profile` dipertahankan.

## Konteks

[ADR 0016](0016-profile-conditional-auth.md) memisahkan keamanan jadi 2 `SecurityFilterChain` yang dipilih `@Profile`:

- `jwtFilterChain` (`!development`) — `JwtAuthFilter` memvalidasi Appwrite JWT, `anyRequest().authenticated()`.
- `devFilterChain` (`development`) — `permitAll()` + `DevAuthFilter` yang selalu menginjeksi principal statis `DEV` (role `ADMIN`+`SYSTEM`), **tanpa validasi token sama sekali**.

Problem dengan desain lama:

1. **Tidak bisa menguji jalur auth asli di dev.** Bearer token yang dikirim diabaikan total; semua request jadi `DEV`. Bug role-restriction (`@PreAuthorize`) tidak pernah muncul di dev, harus menunggu environment real.
2. **Noise log.** `AppwriteClient.validateToken()` menangkap semua `Exception` dan menulis `log.error("JWT Auth Error", e)` dengan stack trace penuh — padahal `401 Unauthorized` adalah respons *diharapkan* saat token invalid/expired. Setiap request dengan token basi membanjiri log.

## Keputusan

Pertahankan **2 chain `@Profile`** (safety property ADR-0016: kode bypass dev tidak pernah di-wire di prod), tetapi ubah komposisi **dev chain**:

```
devFilterChain (development):
  permitAll("/auth/**", "/api-docs/**", "/swagger-ui*/**", "/v3/api-docs/**")
  + authenticated() untuk sisanya
  + JwtAuthFilter      (memvalidasi Bearer token → auth Appwrite asli, atau null)
  + DevAuthFilter      (fallback: injek DEV HANYA jika tidak ada Bearer token)
```

Semantik per request di dev:

| Kondisi header `Authorization` | Hasil |
|--------------------------------|-------|
| Tidak ada / blank / `Bearer ` kosong / skema non-Bearer (mis. `Basic xxx`) | `DevAuthFilter` menginjeksi `DEV` (fallback) |
| `Bearer <token>` valid | `JwtAuthFilter` memvalidasi ke Appwrite → principal user asli |
| `Bearer <token>` invalid/expired | Tidak ada auth → **401 strict** (fallback DEV **tidak** berlaku) |

Aturan perilaku:

- **`JwtAuthFilter` tidak berubah** — tetap buta environment; hanya memvalidasi token bila header diawali `Bearer `. Bila token substring-nya blank, langsung return null (short-circuit) tanpa memanggil Appwrite.
- **`DevAuthFilter` mendapat kondisi trigger**: injek `DEV` hanya jika request **tidak memiliki Bearer token** (header missing/blank/`Bearer ` kosong/`Basic xxx`). Bila ada Bearer token → skip total (tidak inject, tidak clear context) — biarkan `JwtAuthFilter` yang memutuskan.
- **`finally { clearContext() }` hanya dieksekusi saat `DEV` benar-benar di-inject** — jangan menghapus auth asli hasil validasi Appwrite yang di-set `JwtAuthFilter`.
- **`AppwriteClient.validateToken()`**: `401 Unauthorized` → return `null` **tanpa** `log.error` (expected outcome; boleh `log.debug`). Exception lain (timeout, 5xx, network) tetap `log.error` — itu error infrastruktur asli.

## Considered Options

- **1 chain + cek env runtime di `JwtAuthFilter`** (ditolak): `JwtAuthFilter` cek `Environment.acceptsProfiles("development")` → header kosong + dev → bangun DEV. Lebih literal dengan permintaan awal, tetapi membalik safety property ADR-0016 — kode bypass dev selalu ada di filter yang berjalan di prod, dijaga hanya oleh pengecekan profile runtime. Risiko "satu misread env var dari auth mati di prod" kembali muncul.
- **Dev + token invalid → fallback DEV** (ditolak): token expired diam-diam jadi `ADMIN` di dev → "test JWT asli" jadi meaningless, tidak bisa diandalkan untuk tes auth.
- **Dev chain tetap `permitAll()`** (ditolak): dengan `permitAll`, request tanpa auth lolos begitu saja; "invalid → 401 strict" tidak mungkin dicapai. Harus naik ke `authenticated()` agar entry point 401 aktif.
- **2 chain + `DevAuthFilter` trigger header kosong** (dipilih): ADR-0016 dipertahankan, bypass dev mustahil nyasar ke prod, `JwtAuthFilter` tetap ignorant terhadap environment.

## Consequences

- **Dev sekarang menguji auth asli**: Bearer valid → principal Appwrite asli, `@PreAuthorize` dievaluasi terhadap role asli → bug role-restriction bisa muncul di dev.
- **Bypass dev tetap aman**: `DevAuthFilter` hanya di-wire di dev chain; prod chain identik dengan sebelumnya.
- **Log noise 401 hilang**: token invalid tidak lagi mencetak stack trace; error infrastruktur (timeout/5xx) tetap terlihat.
- **Perubahan pada `WebSecurity`**: dev chain membutuhkan `exceptionHandling` (entry point + denied handler) dan daftar `permitAll` yang sama dengan prod chain.
- **`JwtAuthEntryPoint`** menghasilkan respons JSON non-`CustomResult` — di luar scope ADR ini; perhatikan bila ingin diseragamkan dengan envelope `CustomResult`.
- ADR-0016 tetap berlaku untuk struktur 2 chain; bagian "development tanpa autentikasi" tidak berlaku lagi.

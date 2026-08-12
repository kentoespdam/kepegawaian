# Context — Security (Autentikasi & Lingkungan)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan auth, filter JWT, `@PreAuthorize`, Spring profiles, atau Dev User.

## Glossary

**Lingkungan** (environment):
Mode jalan aplikasi yang menentukan rantai keamanan mana yang aktif. Dua nilai: **production** (wajib validasi Appwrite JWT) dan **development** (validasi Bearer token bila ada, fallback ke Dev User bila tidak ada). Dipilih lewat Spring profile (`@Profile` pada `SecurityFilterChain`), bukan flag runtime. Detail: [ADR 0033](../adr/0033-dev-chain-bearer-fallback-devauth.md).

**Dev User** (principal statis):
Identitas tetap yang disuntikkan otomatis di **development** (`DEV`, role `ADMIN`+`SYSTEM`) **hanya saat request tidak membawa Bearer token** — supaya API bisa diuji tanpa token. Bila request membawa Bearer token, yang berlaku adalah validasi JWT normal (valid → user Appwrite asli, invalid → 401; fallback DEV **tidak** berlaku). Role-nya bisa ditimpa lewat `DEV_ROLES` untuk menguji jalur penolakan (403).
_Avoid_: "user palsu", "mock user" (ini principal nyata, hanya tidak diautentikasi)

**AppwriteClient**:
Typed adapter `@Component` yang meng-enkapsulasi semua komunikasi REST ke Appwrite. Satu tempat untuk header construction, URL building, dan error handling. Dipakai oleh `AuthService` (CRUD user) dan `JwtTokenService` (validasi token). Lokasi: `config/appwrite/AppwriteClient.java`. Detail: [ADR-0029](../adr/0029-appwriteclient-typed-adapter.md).

**AppwriteProperties**:
`@ConfigurationProperties(prefix="appwrite")` bean untuk konfigurasi endpoint, projectId, dan apiKey — menggantikan 3 `@Value` yang tersebar di AuthService dan JwtTokenService.

**Role**:
Label hak akses yang di-assign ke Appwrite User via `prefs.roles` (mis. `ADMIN`, `HRD`, `SYSTEM`). Di Spring di-prefix `ROLE_`. Satu user bisa punya lebih dari satu Role; permission-nya adalah union dari semua role yang dimiliki. Role itu sendiri tidak memiliki makna akses — akses ditentukan oleh Permission yang terikat ke Role di database.
_Avoid_: "privilege", "level akses"

**Permission**:
Atomic unit akses dengan format `{ENTITY}:{ACTION}` (mis. `CUTI:APPROVE`, `PEGAWAI:WRITE`, `MASTER:DELETE`). Disimpan di tabel `pref_permission` MariaDB dan terikat ke Role via `pref_role_permission`. Di-inject ke `GrantedAuthority` Spring sebagai string literal (tanpa prefix). Di-enforce via `@PreAuthorize("hasAuthority('CUTI:APPROVE')")`. Tidak bisa di-assign langsung ke user — hanya bisa via Role.
_Avoid_: "izin", "hak", "access right"

**Permission Matrix**:
Mapping deklaratif Role → Set\<Permission\> yang menjadi data seed. Didefinisikan secara terpisah dari infrastruktur RBAC; bisa diubah via API tanpa deploy ulang.
_Avoid_: "ACL", "access table"

**Permission Inflation**:
Proses di `JwtAuthFilter` saat JWT masuk: baca roles dari Appwrite prefs → load permission set dari MariaDB → inject keduanya (`ROLE_xxx` + `ENTITY:ACTION`) ke `GrantedAuthority` Spring untuk satu request. Dilakukan setiap request; tidak di-cache.
_Avoid_: "permission loading", "role expansion"

## Aturan Bisnis Penting

- **Lingkungan** menentukan rantai keamanan: **production** memvalidasi **Appwrite JWT**; **development** memvalidasi Bearer token bila ada, dan memakai **Dev User** hanya bila tidak ada Bearer token.
- Di **development**, Bearer token **invalid/expired → 401** (strict); fallback Dev User tidak berlaku untuk token yang ada tapi gagal validasi.
- Sebuah **Appwrite User** / **Dev User** membawa satu atau lebih **Role**; **Role** menentukan akses endpoint.
- **Role** penulis menentukan `changedStatus` pada data Profil: **SDM** → `false` (langsung stabil), **pegawai** → `true` (menunggu). Keputusan ini diambil **server** dari principal, bukan dari body request.

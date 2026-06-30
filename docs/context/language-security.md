# Context — Security (Autentikasi & Lingkungan)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan auth, filter JWT, `@PreAuthorize`, Spring profiles, atau Dev User.

## Glossary

**Lingkungan** (environment):
Mode jalan aplikasi yang menentukan apakah autentikasi diberlakukan. Dua nilai: **production** (perlu autentikasi) dan **development** (tanpa autentikasi). Dipilih lewat Spring profile, bukan flag runtime.

**Dev User** (principal statis):
Identitas tetap yang disuntikkan otomatis di **development** (`DEV`, role `ADMIN`+`SYSTEM`) supaya API bisa diuji tanpa token. Role-nya bisa ditimpa lewat `DEV_ROLES` untuk menguji jalur penolakan (403).
_Avoid_: "user palsu", "mock user" (ini principal nyata, hanya tidak diautentikasi)

**Appwrite JWT**:
Token yang diterbitkan Appwrite, divalidasi di **production** dengan memanggil Appwrite (`/account/jwt`) untuk mendapatkan **Appwrite User** beserta role-nya.

**Role**:
Hak akses pada principal (mis. `ADMIN`, `SYSTEM`). Menentukan endpoint mana yang boleh diakses (`@PreAuthorize`). Di Spring di-prefix `ROLE_`.

## Aturan Bisnis Penting

- **Lingkungan** menentukan rantai keamanan: **production** memvalidasi **Appwrite JWT**; **development** memakai **Dev User** tanpa validasi.
- Sebuah **Appwrite User** / **Dev User** membawa satu atau lebih **Role**; **Role** menentukan akses endpoint.
- **Role** penulis menentukan `changedStatus` pada data Profil: **SDM** → `false` (langsung stabil), **pegawai** → `true` (menunggu). Keputusan ini diambil **server** dari principal, bukan dari body request.

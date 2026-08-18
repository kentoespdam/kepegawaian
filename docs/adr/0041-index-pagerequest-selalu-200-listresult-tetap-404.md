# 0041 — Envelope index: `PageResult` selalu 200 + empty page; `ListResult` tetap 404 saat list kosong (diputuskan, bukan kelalaian)

Grill session (2026-08-18) mengaudit **seluruh endpoint ber-envelope `PageResult`** untuk memverifikasi kepatuhan pada konvensi "data tidak ditemukan → 200 + empty page ber-metadata" (ditetapkan di ADR-0040 untuk `GET /cuti/kuota`). Hasil: **semua endpoint index sudah patuh** — tidak ada lagi 404-on-empty di jalur page. Audit menemukan dua anomali terkait yang butuh keputusan eksplisit.

## Hasil Audit — `PageResult` 100% patuh

- **Lapisan envelope**: seluruh 40+ controller index memakai `CustomResult.page(...)` yang hard-wired `ResponseEntity.ok(...)` (`CustomResult.java:27`) — mustahil menghasilkan 404.
- **Lapisan query**: tidak ada `return null` di jalur page. Repository JOOQ selalu membangun `PageImpl<>(data, pageable, count)` (count 0 → `totalElements: 0`, `totalPages: 0`); jalur JPA (`findAll(spec, pageable)`) tidak pernah null. Semua `return null` / `orElseThrow(NotFoundException)` yang tersisa berada di jalur **detail** (`getById`/`findById`) — konvensi ADR-0014 (get-by-id missing → 404), bukan index.
- **Pelanggar historis**: `GET /cuti/kuota` (SingleResult + `return null` saat kosong → 404) sudah diperbaiki di ADR-0040.

## Keputusan

1. **`GET /system/users` (index) dinormalisasi dari `SingleResult` → `PageResult`.** Sebelumnya `ResponseEntity<SingleResult<Page<UserResponse>>>` via `CustomResult.any(...)` — satu-satunya endpoint index yang menyimpang dari konvensi ADR-0040 ("semua controller index CQRS/JOOQ mengembalikan `PageResult<Page<...>>` via `CustomResult.page(...)`"). Perilaku HTTP sebenarnya tidak berubah (Page tidak pernah null → SingleResult tetap 200 saat kosong), tapi envelope berubah: field `message` hilang, konsisten dengan 40+ endpoint page lain. Perubahan satu baris + return type; blast radius rendah karena `/system/**` berguard `hasRole('SYSTEM')` / `SYSTEM:MANAGE_USER` (tooling internal/admin).

2. **`ListResult` TETAP 404 saat list kosong — keputusan sadar, bukan kelalaian.** 52 call site (`CustomResult.list(...)`: `GET /{modul}/{entity}/list`, `GET /profil/{entity}/lampiran`, `GET /system/roles/list`, dst.) memetakan list kosong → `HttpStatus.NOT_FOUND` + `message: "Data not found!"` (`ListResult.java:15-20`). Anti-pattern yang sama sudah dihapus dari `PageResult` (ADR-0040), tapi untuk `ListResult` **sengaja dipertahankan**: FE saat ini (termasuk `kepegawaian-fe`) sudah bergantung pada 404 sebagai sinyal "tidak ada data" untuk beberapa list (terdokumentasi di `docs/frontend/FE-CONTRACT-profil-update-approval-rbac.md:103` — "perilaku bawaan ListResult"); list non-paged tidak membawa metadata halaman, jadi 404-vs-200 tidak menghilangkan informasi. Catatan: ini deviasi yang **disengaja** — jangan "perbaiki" ke 200 tanpa koordinasi FE dan revisi ADR ini.

## Considered Options

- **Normalisasi semua `ListResult` ke 200 + `data: []`** — ditolak (keputusan grill 2026-08-18): breaking bagi FE yang mengecek 404 sebagai "list kosong"; tanpa metadata halaman, 200 kosong tidak menambah informasi; biaya koordinasi FE untuk 52 endpoint > manfaat. Berbeda dengan `PageResult` di ADR-0040, di mana 404-on-empty **memang bug** (envelope menyimpang + membuang metadata halaman).
- **Biarkan `GET /system/users` tetap `SingleResult`** — ditolak: envelope beda dari semua endpoint index lain; ADR-0040 menetapkan gold standard; `message: "Data Found"` di envelope index tidak punya makna operasional dan memaksa FE branch khusus.
- **Perluas konvensi 404 ke `PageResult` (meniadakan ADR-0040)** — ditolak: filter tanpa hasil bukan error klien; 4xx menyiratkan kesalahan; FE kehilangan pagination metadata.

## Consequences

- `GET /system/users` → **envelope `PageResult`**: `{status, statusText, data: Page, timestamp}`, tanpa `message`; kosong → 200 + empty page ber-metadata. FE yang membaca `message` dari endpoint ini harus berhenti.
- Aturan kontrak yang kini eksplisit dan teruji:
  - **Index/page** (`PageResult` via `CustomResult.page`) → selalu **200**, kosong = empty page.
  - **List non-paged** (`ListResult` via `CustomResult.list`) → **404** saat list kosong (keputusan dipertahankan).
  - **Detail** (`SingleResult` via `CustomResult.any`/`optional`) → **404** saat data null (ADR-0014).
- Catatan FE-CONTRACT ditambahkan ke `docs/frontend/FE-CONTRACT-profil-update-approval-rbac.md` (section 3 — user provisioning).

# Index kuota cuti — PageResult dengan data `{page, kuotaTahunSebelumnya}` (deviasi dari pola Page mentah)

Grill session (2026-08-18) menemukan `GET /cuti/kuota` (index) menyimpang dari konvensi seluruh codebase: semua controller index CQRS/JOOQ mengembalikan `PageResult<Page<...>>` via `CustomResult.page(...)` (gold standard `ProfesiController`), sedangkan kuota membungkus payload page dalam **`SingleResult`** via `CustomResult.any(...)` — membawa `message: "Data Found"/"Data not found!"` dan **404** saat halaman kosong (`CutiKuotaQueryRepository.pageQuery` me-`return null` saat `page.isEmpty()`).

**Keputusan**: `GET /cuti/kuota` mengembalikan **`PageResult<CutiKuotaPegawaiResponse>`** via `CustomResult.page(...)`, dengan `data` tetap bersarang `{page, kuotaTahunSebelumnya}`:

- **Envelope seragam**: `PageResult` (`status`/`statusText`/`data`/`timestamp`, tanpa `message`) — sama seperti semua endpoint page lain.
- **Shape `data` dipertahankan** — deviasi **sengaja**: `kuotaTahunSebelumnya` (dulu `additional`) membundel query tahun−1 (baris kuota siklus sebelumnya untuk pegawai di halaman tsb) dalam satu response, biar FE tidak perlu 2× panggil. Ini bisnis logika yang sudah terdokumentasi di `decisions-cuti.md`.
- **Page kosong → 200 + empty page ber-metadata**, bukan 404/204: `pageQueryWithPreviousYear` tidak pernah `return null`; kosong → `new CutiKuotaPegawaiResponse(page, List.of())` dengan `page` tetap `PageImpl` (`totalElements: 0`, `totalPages: 0`).
- **Rename wire**: `data.additional` → **`data.kuotaTahunSebelumnya`** (breaking, koordinasi FE — lihat `docs/frontend/FE-CONTRACT-cuti-kuota-index.md`).
- **Detail tidak berubah**: `GET /cuti/kuota/{id}` dan `GET /cuti/kuota/{pegawaiId}/{tahun}/sisa` tetap `SingleResult` → 404 saat tidak ada (ADR-0014: get-by-id missing row → 404).

## Considered Options

- **Normalisasi penuh ke `PageResult<Page<CutiKuotaResponse>>` mentah** — ditolak: FE kehilangan data tahun−1 (harus 2× panggil) atau query bundel dibuang; `kuotaTahunSebelumnya` adalah fitur grid carry-over yang disengaja.
- **204 No Content untuk page kosong** — ditolak: RFC mengizinkan, tapi tidak lazim untuk GET collection; body kosong → FE kehilangan pagination metadata (`totalElements: 0` dst.) dan harus branch khusus sebelum `res.json()` (fetch melempar `SyntaxError` pada 204, whatwg/fetch #113). Konsisten dengan seluruh endpoint page lain yang 200 + empty page. Sumber: [API Handyman — Empty list 200 vs 204 vs 404](https://apihandyman.io/empty-lists-http-status-code-200-vs-204-vs-404/).
- **404 untuk page kosong** — ditolak: 4xx menyiratkan kesalahan klien; filter tanpa hasil bukan error; melanggar common practice (404 = path/ID tidak ada) dan membuat FE sulit membedakan halaman kosong vs endpoint salah.
- **Tetap `SingleResult` (status quo)** — ditolak: envelope berbeda dari semua controller lain + 404-on-empty yang tidak konsisten.

## Consequences

- **FE (Next.js) breaking**: `data.additional` → `data.kuotaTahunSebelumnya`; `message` hilang dari envelope index; page kosong kini 200 + `{page: {content: [], ...}, kuotaTahunSebelumnya: []}` (sebelumnya 404). Aksi terperinci di `docs/frontend/FE-CONTRACT-cuti-kuota-index.md`.
- `CutiKuotaQueryRepository.pageQuery` di-rename → `pageQueryWithPreviousYear`; `CutiKuotaQueryService.findPage` → `findIndex` (nama jujur: bukan mengembalikan `Page`). `CutiPengajuanValidator` tidak terpengaruh (hanya memakai `findByPegawai`).
- Detail endpoint (`/{id}`, `/{pegawaiId}/{tahun}/sisa`) tidak berubah — 404 tetap benar per ADR-0014.
- Glossary `language-cuti.md` mendapat istilah **Kuota Tahun Sebelumnya** (`kuotaTahunSebelumnya`).

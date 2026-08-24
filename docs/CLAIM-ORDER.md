# Claim Order — Temuan Grilling Arsitektur (2026-07-09)

Urutan claim `bd` untuk temuan `/improve-codebase-architecture` (CODING_RULES.md + master-query-optimization-pattern.md).
Hormati dependency. Claim satu-per-satu: `bd update <id> --claim` → kerjakan → `bd close <id>`.

**Aturan tetap:** impact analysis (gitnexus) sebelum edit simbol · `detect_changes` sebelum commit · soft-delete only · max 120 baris/file · CQRS: JPA write / JOOQ read.

---

## Fase 1 — Cuti: shared selects & inbox N+1 (urut, ada dependency)

- [x] **kepegawaian-2nd** (P2) — Ekstrak `CutiPegawaiSelects` (shared `Field<?>[]`)
  - Prasyarat semua fix cuti. WAJIB pertahankan kontrak string-alias (`org_*`, `jab_*`, `jc_*`, `sjc_*`, `pic_*`).
- [x] **kepegawaian-d23** (P2) — Fix inbox N+1 via `QUALIFY ROW_NUMBER()` *(blocked-by 2nd)*
  - `PARTITION BY REF_CUTI_ID ORDER BY APPROVAL_LEVEL DESC, ID DESC = 1`; JOIN full cols; refCuti→`mapToMiniResponse`; `countDistinct` dipertahankan.
- [x] **kepegawaian-4aw** (P2) — `CutiApprovalChainResponse.refCuti` → `CutiPengajuanMiniResponse`
  - Buang nesting berlebih; factory `from()` panggil `CutiPengajuanMiniResponse.from`.
- [x] **kepegawaian-9j0** (P3) — Hapus secondary N+1 di `CutiPengajuanQueryRepository.pageQuery`
  - Buang blok `getMiniById` per-row; list `refCuti=null` (operasional, bukan display). `getById` tetap isi refCuti.

## Fase 2 — Master: hapus jalur seeding redundan

- [x] **kepegawaian-rvw** (P2, child of be8) — Hapus `services/setupMaster/` + `SetupMasterController`
  - Disupersede Flyway `V3_0_*`. Blast-radius nol (25 kelas → 1 controller). Lihat [ADR 0030](adr/0030-hapus-seeding-imperatif-setupmaster.md). `SetupDetailDasarGaji` dihapus, **bukan** di-refactor ≤120.

## Fase 3 — Sweep global (tidak memblokir cuti)

- [x] **kepegawaian-jyh** (P3) — Audit FK-duplikat semua read-DTO (§4c)
  - Cuti sudah bersih. `pegawaiId` cuti BUKAN duplikat (tak ada nested pegawai).
- [x] **kepegawaian-a66** (P3) — Migrasi DTO/read model sisi Query CQRS ke Java record
  - ✅ Anak **2fl**, **9iy**, **a66.1** sudah selesai (commit `e73e759`). Induk `a66` masih open — perlu close bila semua anak sudah done.

---

## Fase 4 — Pegawai read-side: dekomposisi findById

- [x] **kepegawaian-5ca** (P2) — Dekomposisi `PegawaiQueryRepository.findById` (625 baris → §4)
  - Akar = **3 tanggung jawab belum terekstrak** (bukan triplication, bukan N+1). 3 file baru: `PegawaiDetailSelects` + `PegawaiDetailRecordMapper` + `PegawaiDetailRefMapper`.
  - `detailFields()` **standalone** (subset kolom beda dari `pegawaiResponseFields`). `skList` di-pass sbg argumen; `findRiwayatSkList` tetap di repo. Subquery korelasi `grade_level_nama` **dipertahankan**. Kontrak string-alias WAJIB dijaga. 1 pemanggil (`PegawaiQueryService`), pure decomposition.

## Sudah digrill — DTO→record (child of a66)

- [x] **kepegawaian-2fl** — `AlatKerjaQuery` (`@Data`→record). Record-safe: hanya `fetchOptionalInto`, tak ada mutasi setter eksternal.
- [x] **kepegawaian-9iy** — `SanksiMiniResponse` (`@AllArgsConstructor @Data`→record). Record-safe; `from(Sanksi)` dipertahankan.
- [x] **kepegawaian-a66.1** — `ProfilUpdateDetail<T>`→record + buang kebocoran entity JPA (field→`ProfileUpdateQuery`, `build()` dijaga, tambah `ProfileUpdateQuery.from(entity)`; jalur baca tetap JPA/Envers).

## Catatan bukan-prioritas

- `GajiBatchRootQueryRepository.getById` inline `.fetch()` utk errorLogs/lampirans — **bukan** N+1 (bounded children, single detail row). Jangan diutak-atik.

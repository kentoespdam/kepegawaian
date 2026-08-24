# Claim Order — Adopsi Pattern Publication ke Modul Master

Urutan klaim eksekusi issue beads `pattern-adoption` agar pattern paging/sort + write-flow `{status,id}` ter-rollout konsisten ke semua modul master. Dipasangkan dengan `docs/master-response-pattern-guide.md` (sumber kebenaran pattern) dan Organization kepegawaian-dcz (referensi shipped).

**Prinsip klaim**: 1 issue = 1 PR. Pilot dulu, baru wave. Paging/sort SELALU sebelum write-flow (urutan dalam child issue tidak diputar). Apd/AlatKerja khusus: write-flow saja, paging tidak applicable.

> **Status akhir**: Semua wave SELESAI — kode terverifikasi (2026-08-24). Build green. Semua modul master menggunakan `PagedRequest` + `SortParam` (dto/commons) + `{status,id}` response. Issues bd sudah closed.

---

## A. Klaim berurutan (master list)

| # | Epic | Child paging/sort | Child write-flow | Tipe | Prioritas | Catatan |
|---|------|-------------------|------------------|------|-----------|---------|
| 1 | `kepegawaian-92j` | `.1` ✓ | `.2` ✓ | **PILOT** | P2 | Jabatan — SHIPPED 2026-06-22 (commits 3d6f6da + 9cea3fb) |
| 2 | `kepegawaian-jgt` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Grade — SHIPPED 2026-06-22 (commits 6ca8622 + d6fbaae) |
| 3 | `kepegawaian-5oz` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Profesi — SHIPPED 2026-06-22 (commits f05fae8 + 9bb91bc) |
| 4 | `kepegawaian-e21` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Level — SHIPPED 2026-06-22 (commits 8d81665 + 9f60698) |
| 5 | `kepegawaian-1lw` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Golongan — SHIPPED 2026-06-22 (commits 9e7d4da + ed95060) |
| 6 | `kepegawaian-92q` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | AlasanBerhenti — Response keep |
| 7 | `kepegawaian-oit` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | JenisKeahlian — Response keep |
| 8 | `kepegawaian-04q` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | JenisKitas — Response keep |
| 9 | `kepegawaian-c0d` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | JenisPelatihan — Response keep |
| 10 | `kepegawaian-4fj` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | JenisSp — Response 0 consumer |
| 11 | `kepegawaian-kh2` | `.1` ✓ | `.2` ✓ | wave 2 | P2 | Sanksi — Response 0 consumer |
| 12 | `kepegawaian-ehy` | `.1` ✓ | `.2` ✓ | wave 3 | P2 | HariLibur — SHIPPED (d691944) |
| 13 | `kepegawaian-tc1` | `.1` ✓ | `.2` ✓ | wave 3 | P2 | RumahDinas — SHIPPED (ae860fa) |
| 14 | `kepegawaian-avt` | — | `.1` ✓ | wave 4 | P2 | Apd — write-flow ONLY (sub-resource) |
| 15 | `kepegawaian-wbo` | — | `.1` ✓ | wave 4 | P2 | AlatKerja — write-flow ONLY (sub-resource) |

**Sub-resource (Apd, AlatKerja)**: hanya 1 child (write-flow), bukan 2. Paging/sort tidak applicable karena pakai JPA `Pageable`, bukan JOOQ switch inline.

---

## B. Wave structure (urutan eksekusi + verifikasi)

| Wave | Modul | Status |
|------|-------|--------|
| **Pilot** | Jabatan (1 epic, 2 children) | ✅ SHIPPED |
| **Wave 1** | Grade, Profesi, Level, Golongan (4 epic paralel) | ✅ SHIPPED |
| **Wave 2** | AlasanBerhenti, JenisKeahlian, JenisKitas, JenisPelatihan, JenisSp, Sanksi (6 epic paralel) | ✅ SHIPPED |
| **Wave 3** | HariLibur, RumahDinas (2 epic, butuh klarifikasi A/B) | ✅ SHIPPED |
| **Wave 4** | Apd, AlatKerja (2 epic sub-resource, write-flow only) | ✅ SHIPPED |

---

## C. Pre-flight checklist

- [x] `bd prime` jalan tanpa error
- [x] Branch `rewrite/master-cqrs` digunakan
- [x] `docs/master-response-pattern-guide.md` sudah dibaca
- [x] `docs/organisasi-publication-pattern-claim-order.md` sudah dibaca
- [x] Pattern referensi Organisasi diverifikasi

---

## D. Per-module checklist (template — sudah dijalankan untuk semua modul)

### D.1 Pre-flight per modul

- [x] Semua IndexQuery extends `PagedRequest` (bukan `CommonPageRequest`)
- [x] Semua QueryRepository pakai `SortParam.resolve()` (bukan switch inline)

### D.2 Child paging/sort checklist

- [x] PagedRequest & SortParam di `dto/commons` (consolidated dari per-module)
- [x] Semua IndexQuery extends PagedRequest
- [x] Semua QueryRepository pakai `allowedSorts()` + `SortParam.resolve()`
- [x] Default sortBy null/blank → ID, tidak error
- [x] `./gradlew compileJava` hijau

### D.3 Child write-flow checklist

- [x] Semua Controller POST/PUT: `entity.getId()` (bukan entity)
- [x] Semua Controller DELETE: `CustomResult.delete(command.delete(id))`
- [x] CommandService tidak diubah
- [x] Tidak ada kebocoran relasi/lazy entity
- [x] Soft-delete tetap utuh
- [x] `./gradlew compileJava` + `./gradlew test` hijau

### D.4 Sub-resource khusus (Apd/AlatKerja)

- [x] Skip child paging/sort
- [x] Write-flow: `command.create/update` return Long langsung

### D.5 Verifikasi per epic

- [x] `./gradlew clean compileJava` hijau (verified 2026-08-24)
- [x] Tidak ada dependency Gradle baru
- [x] Semua modul: POST/PUT → `{status, id}`, GET sort → tidak error, fallback ke default

---

## E. Per-wave checklist

### E.1 Pilot wave (Jabatan) ✅

- [x] Issue `kepegawaian-92j.1` selesai
- [x] Issue `kepegawaian-92j.2` selesai
- [x] Epic `kepegawaian-92j` di-close

### E.2 Wave 1 (Grade, Profesi, Level, Golongan) ✅

- [x] 4 × (D.1 → D.2 → D.3 → D.5) selesai
- [x] Consolidation refactor: all per-module PagedRequest/SortParam → dto/commons/

### E.3 Wave 2 (AlasanBerhenti, JenisKeahlian, JenisKitas, JenisPelatihan, JenisSp, Sanksi) ✅

- [x] 6 × (D.1 → D.2 → D.3 → D.5) selesai

### E.4 Wave 3 (HariLibur, RumahDinas) ✅

- [x] Klarifikasi A/B selesai
- [x] 2 × (D.1 → D.2 → D.3 → D.5) selesai

### E.5 Wave 4 (Apd, AlatKerja) ✅

- [x] 2 × (D.1 → D.4 → D.3 → D.5) selesai

---

## F. Pitfalls

Dokumentasi untuk reference — semua sudah dijalankan.

---

## G. Out-of-scope reminders

Modul-modul ini **TIDAK** dibuat epic `pattern-adoption`:

| Modul | Alasan |
|-------|--------|
| `Organisasi` | Sudah SHIPPED di kepegawaian-dcz |
| `StatusKerja` | Enum-style |
| `StatusPegawai` | Enum-style |
| `JenisKontrak` | Enum-style |
| `JenisMutasi` | Enum-style |
| `JenisSk` | Enum-style |
| `JenjangPendidikan` | Non-CQRS — perlu CQRS split penuh |

---

## REF

- `docs/master-response-pattern-guide.md` — sumber kebenaran pattern
- `docs/organisasi-publication-pattern-claim-order.md` — template Organisasi
- Build verified: `./gradlew clean compileJava` = BUILD SUCCESSFUL (2026-08-24)

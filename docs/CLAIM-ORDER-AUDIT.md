# Claim Order Audit — 2026-08-20

> Audit seluruh file `CLAIM-ORDER` di folder `docs/`.
> Status: **36 file** diperiksa, **28 belum selesai** (ada item `[ ]`), **8 sudah 100%**.

---

## 🔴 Prioritas Tinggi (security / data-integrity / blocking)

| # | File                                                  | Unchecked | Status | Catatan |
|---|-------------------------------------------------------|-----------|--------|---------|
| 1 | ~~`CLAIM-ORDER-baseline-column-order.md`~~            | 8/8 | **belum mulai** | Seluruhnya belum dikerjakan — baseline column order belum direformat |
| 2 | ~~`security-dev-chain-bearer-claim-order.md`~~        | 5 | **belum mulai** | Security filter chain — dev auth + filter order |
| 3 | ~~`master-delete-guard-claim-order.md`~~              | 27 | **belum mulai** | Refuse delete saat child aktif — data integrity guard |
| 4 | ~~`system-controllers-transactional-claim-order.md`~~ | 17 | **belum mulai** | @Transactional di controller (violation layering) |

## 🟡 Prioritas Menengah (bug fix / refactor core)

| #  | File                                           | Unchecked | Status | Catatan |
|----|------------------------------------------------|-----------|--------|---------|
| 5  | ~~`jackson3-objectmapper-fix-claim-order.md`~~ | 7 | **belum mulai** | Jackson 2→3 migration (DeniedHandler + JwtAuthEntryPoint) |
| 6  | ~~`cuti-exception-typing-claim-order.md`~~     | 11 | **belum mulai** | RuntimeException → typed exceptions |
| 7  | ~~`xlsx-workbook-fix-claim-order.md`~~         | 12 | **belum mulai** | XSSFWorkbook → SXSSFWorkbook (memory fix) |
| 8  | `claim-order-2026-06-17-analisis-bug.md`       | 46 | **belum mulai** | GajiBatchRoot — exception handling, file upload, kafka |
| 9  | ~~`penggajian-cqrs-claim-order.md`~~           | 7 | **progres** | Penggajian CQRS split — cleanup & commit tersisa |
| 10 | ~~`lampiranProfil-cqrs-claim-order.md`~~       | 7 | **progres** | LampiranProfil CQRS — impact, detect, test tersisa |
| 11 | ~~`entity-layer-fixes-claim-order.md`~~        | 2 | **hampir selesai** | Tinggal verify test (ready untuk close) |
| 12 | ~~`remove-entity-tostring-claim-order.md`~~    | 2 | **hampir selesai** | Tinggal verify test (ready untuk close) |
| 13 | ~~`validator-factory-fix-claim-order.md`~~     | 2 | **hampir selesai** | Tinggal verify test (ready untuk close) |

## 🟢 Prioritas Rendah (polish / documentation / quick wins)

| #  | File                                                | Unchecked | Status             | Catatan                                              |
|----|-----------------------------------------------------|-----------|--------------------|------------------------------------------------------|
| 14 | `CLAIM-ORDER.md`                                    | 1         | **hampir selesai** | a66 — migrasi DTO ke record, tinggal close           |
| 15 | ~~`level-cqrs-claim-order.md`~~                     | 1         | **hampir selesai** | Epic kepegawaian-6h2 tinggal close                   |
| 16 | ~~`profesi-apd-alatkerja-diagnose-claim-order.md`~~ | 1         | **hampir selesai** | Tinggal `bd dolt push`                               |
| 17 | ~~`pegawai-record-refactor-claim-order.md`~~        | 1         | **ditunda**        | Hapus parameter Errors — ditunda ke batch berikutnya |
| 18 | ~~`pegawai-table-response-claim-order.md`~~         | 1         | **ditunda**        | Tunggu koordinasi FE                                 |
| 19 | ~~`form-mutasi-claim-order.md`~~                    | 4         | **belum mulai**    | Jawaban desain + kabari FE                           |
| 20 | ~~`master-dead-code-cleanup-claim-order.md`~~       | 4         | **belum mulai**    | Detect changes + review dead code                    |
| 21 | ~~`cuti-cqrs-rewrite-claim-order.md`~~              | 4         | **final steps**    | detect, commit, close, push                          |
| 22 | ~~`cuti-refactor-claim-order.md`~~                  | 4         | **belum mulai**    | Init steps: prime, claim                             |
| 23 | ~~`profil-cqrs-cleanup-claim-order.md`~~            | 4         | **final steps**    | detect, commit, close, push                          |
| 24 | `CLAIM-ORDER-drop-commonpagerequest.md`             | 8         | **template only**  | Checklist per-slice — epik sudah selesai             |
| 25 | `organisasi-publication-pattern-claim-order.md`     | 13        | **belum mulai**    | Publication pattern — checklist belum di-check       |
| 26 | `master-pattern-claim-order.md`                     | 59        | **belum mulai**    | Massive — wave execution checklist                   |
| 27 | `ponytail-audit-claim-order.md`                     | 54        | **belum mulai**    | Ponytail audit sweep — 5 sub-epics                   |

---

## ✅ Sudah 100% Selesai

| # | File | Catatan |
|---|------|---------|
| 1 | `CLAIM-ORDER-baseline-rebuild.md` | Baseline rebuild — semua 8 gate selesai |
| 2 | `CLAIM-ORDER-biodata-dashboard.md` | Dashboard biodata — semua selesai |
| 3 | `CLAIM-ORDER-session-statusPegawai.md` | StatusPegawai di session — semua selesai |
| 4 | `adr-0017-claim-order.md` | ADR-0017 — semua selesai |
| 5 | `claim-order-biodata-patch-changedstatus.md` | Biodata changedStatus patch — semua selesai |
| 6 | `claim-order-gajibatchroot-kafka.md` | GajiBatchRoot kafka — semua selesai |
| 7 | `claim-order-pendidikan-disetujui-islatest.md` | Pendidikan isLatest — semua selesai |
| 8 | `claim-order-profileupdate-8-entity.md` | ProfileUpdate 8 entity — semua selesai |

---

## Quick Wins (1-2 langkah untuk close)

1. **`entity-layer-fixes-claim-order.md`** — `./gradlew test` → all green → close
2. **`remove-entity-tostring-claim-order.md`** — `./gradlew test` → all green → close
3. **`validator-factory-fix-claim-order.md`** — `./gradlew test` → all green → close
4. **`CLAIM-ORDER.md`** — close `kepegawaian-a66` → all children done
5. **`level-cqrs-claim-order.md`** — close epic `kepegawaian-6h2`
6. **`profesi-apd-alatkerja-diagnose-claim-order.md`** — `bd dolt push` → done

---

## Rekomendasi Eksekusi

### Batch 1 — Quick Wins (langsung close, ~30 menit)
- `entity-layer-fixes`, `remove-entity-tostring`, `validator-factory-fix`
- `CLAIM-ORDER.md` (a66), `level-cqrs` (6h2), `profesi-apd-alatkerja-diagnose`

### Batch 2 — Security & Data Integrity (P0-P1)
- `security-dev-chain-bearer` — dev auth filter
- `jackson3-objectmapper-fix` — Jackson 2→3
- `master-delete-guard` — delete guard

### Batch 3 — Core Refactor (P1-P2)
- `system-controllers-transactional` — @Transactional layering
- `cuti-exception-typing` — typed exceptions
- `xlsx-workbook-fix` — memory optimization
- `penggajian-cqrs`, `lampiranProfil-cqrs` — CQRS split

### Batch 4 — Low Priority / Documentation
- `form-mutasi`, `master-dead-code-cleanup`, `cuti-cqrs-rewrite`, `profil-cqrs-cleanup`
- `master-pattern`, `ponytail-audit`, `organisasi-publication-pattern`

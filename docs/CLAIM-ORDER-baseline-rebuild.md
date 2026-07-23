# Claim Order — Rebuild Baseline `kepegawaian_dev_new`

> **Epic:** `kepegawaian-odb` · **ADR:** [0032](adr/0032-squash-migration-baseline-bersih-dari-dump-db-existing.md)
> Monitoring papan kerja. Kerjakan **berurutan** — tiap issue memblokir berikutnya. Klaim: `bd update <id> --claim` · Selesai: `bd close <id>`.

## TL;DR keputusan (terkunci lewat grilling)

| # | Keputusan |
|---|-----------|
| 1 | **Draft** = dump schema DB `kepegawaian` existing · **Oracle** = entity JPA |
| 2 | Pertahankan ADR-0003: Envers hanya `penggajian` + `kepegawaian` |
| 3 | Squash `V1..V5_1_0` → baseline bersih tunggal |
| 4 | Verifikasi via Hibernate (bukan generate from-scratch) |
| 5 | **GATE ganda**: `ddl-auto=validate` HIJAU **DAN** `jooqCodegen` HIJAU |

## Diagnosis akar masalah (bukti)

- **camelCase hanya 2 kolom** — `jmlTanggungan` (`@Column` eksplisit `legacy/Pegawai.java:149`), `nomorKartu` (naming strategy lama kosong → `PhysicalNamingStrategyStandardImpl`). Kode baru = default snake_case.
- **13 tabel `_AUD` master yatim** di `V5_1_0` — entity master **nol** `@Audited`; klaim "dibuat Envers runtime" keliru. Langgar ADR-0003.
- **3 kolom ENUM native** — `golongan_darah`, 2× `jenis_gaji`; ketiganya `@Enumerated(EnumType.STRING)`, tak muncul otomatis dari dump/export.

## Urutan kerja

| Urut | Issue | Judul | Blocked by | Gate keluar |
|:---:|---|---|:---:|---|
| 1 | `kepegawaian-odb.1` | Dump schema-only DB existing → draft | — | dump tersimpan, tabel ter-inventaris |
| 2 | `kepegawaian-odb.2` | Drop 13 `_AUD` master (ADR-0003) | .1 | `_AUD` penggajian+kepegawaian & REVINFO tetap |
| 3 | ~~`kepegawaian-odb.3`~~ | ~~Fix 2 kolom camelCase → snake_case~~ | .2 | ✅ **SELESAI** — V5_0_8 fix `jmlTanggungan`, V5_0_9 fix `nomor→nomor_kartu` |
| 4 | `kepegawaian-odb.4` | Reconcile 3 ENUM native + urutan enum | .3 | ENUM native benar & urut konsisten |
| 5 | `kepegawaian-odb.5` | Preserve 23 seed `V3_*` + view `v_pegawai` | .4 | seed & view jadi migration terpisah, urut FK aman |
| 6 | `kepegawaian-odb.6` | Squash → baseline bersih (V1 + seed + view) | .5 | flyway clean+migrate DB kosong SUKSES |
| 7 | `kepegawaian-odb.7` | **GATE-1** `ddl-auto=validate` boot HIJAU | .6 | boot tanpa `SchemaManagementException` |
| 8 | `kepegawaian-odb.8` | **GATE-2** `jooqCodegen` HIJAU tanpa drift | .7 | sources tergenerate, tipe/nama benar, di-commit |

## Checklist monitoring

- [x] **odb.1** — Dump schema-only (`--no-data`), inventaris tabel & daftar `_AUD` ter-dump
- [x] **odb.2** — 12 orphan master `_AUD` di-drop · `_AUD` penggajian+kepegawaian tetap · cuti_jenis_aud tetap (entity @Audited) · REVINFO akan ditambahkan di odb.6
- [x] **odb.3** ✅ — `jml_tanggungan` (V5_0_8) & `nomor_kartu` (V5_0_9) snake_case · grep `[a-z][A-Z]` bersih · komentar V5_0_9 diperbaiki (copy-paste)
- [ ] **odb.4** — 3 ENUM native ada · urutan konsisten `EGolonganDarah`/`EJenisGaji` · cek 29 field ORDINAL tak terlewat
- [ ] **odb.5** — 23 seed `V3_*` teridentifikasi · `v_pegawai` ter-ekstrak · urut FK aman
- [ ] **odb.6** — baseline tunggal gantikan `V1..V5_1_0` · `V5_0_8`/`V5_0_9` dihapus · clean+migrate sukses
- [ ] **odb.7** — GATE-1: boot `validate` HIJAU, semua entity cocok schema
- [ ] **odb.8** — GATE-2: `jooqCodegen` sukses · ENUM→enum JOOQ · snake_case di generated · diff di-commit (ADR-0015) · status drift Testcontainers dicatat (ADR-0004)

## Catatan risiko

- **Destruktif:** squash membuang riwayat migration. Aman selama `kepegawaian_dev_new` masih dev — **konfirmasi tak ada env lain yang sudah apply migration lama** sebelum odb.6.
- **Drift jooqCodegen:** `../build.gradle.kts` mengarahkan `jdbcUrl` ke live DB, bukan Testcontainers (ADR-0004/0012). Dicatat di odb.8; bila perlu diselaraskan → issue terpisah.
- **Urutan ENUM `golongan_darah`:** enum Java `A,B,AB,O` vs `V5_1_0` `ENUM('A','AB','B','O')`. Karena STRING (by-name) tak fatal utk JPA, tapi samakan utk konsistensi JOOQ.

## Data transaksional (DEFERRED — di luar scope baseline)

Issue `kepegawaian-2ze` (status **deferred**, blocked oleh `odb.8`). Prinsip terkunci:

- **Schema = Flyway** (source of truth); **data = script cutover sekali-jalan**, bukan migration.
- ALTER-in-place dump lama **ditolak** → bikin Flyway fiksi, drift balik.
- Data transaksional = **PII → tidak masuk git**.
- Saat dibuka: `INSERT...SELECT` old→new (EXCLUDE master, sudah di-seed `V3_*`), mapping eksplisit **hanya** 2 kolom rename + cast ENUM, reconciliation row-count.

## Session close (wajib)

Quality gates → `bd dolt push` → `git pull --rebase` → `git push` → verifikasi "up to date with origin".

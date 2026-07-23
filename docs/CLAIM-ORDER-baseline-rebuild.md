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
| 4 | ~~`kepegawaian-odb.4`~~ | ~~Reconcile 3 ENUM native + urutan enum~~ | .3 | ✅ **SELESAI** — `golongan_darah` order fix di draft, `jenis_gaji` sudah cocok |
| 5 | `kepegawaian-odb.5` | Preserve 23 seed `V3_*` + view `v_pegawai` | .4 | seed & view jadi migration terpisah, urut FK aman |
| 6 | ~~`kepegawaian-odb.6`~~ | ~~Squash → baseline bersih (V1 + seed + view)~~ | .5 | ✅ **SELESAI** — 25 migrations, flyway clean+migrate SUKSES (23s) |
| 7 | `kepegawaian-odb.7` | **GATE-1** `ddl-auto=validate` boot HIJAU | .6 | boot tanpa `SchemaManagementException` |
| 8 | `kepegawaian-odb.8` | **GATE-2** `jooqCodegen` HIJAU tanpa drift | .7 | sources tergenerate, tipe/nama benar, di-commit |

## Checklist monitoring

- [x] **odb.1** — Dump schema-only (`--no-data`), inventaris tabel & daftar `_AUD` ter-dump
- [x] **odb.2** — 12 orphan master `_AUD` di-drop · `_AUD` penggajian+kepegawaian tetap · cuti_jenis_aud tetap (entity @Audited) · REVINFO akan ditambahkan di odb.6
- [x] **odb.3** ✅ — `jml_tanggungan` (V5_0_8) & `nomor_kartu` (V5_0_9) snake_case · grep `[a-z][A-Z]` bersih · komentar V5_0_9 diperbaiki (copy-paste)
- [x] **odb.4** ✅ — `golongan_darah` `('A','B','AB','O')` (draft fix) · `jenis_gaji` ×2 `('NONE','PEMASUKAN','POTONGAN')` ✅ · 29+ field ORDINAL bukan ENUM native — aman
- [x] **odb.5** ✅ — 23 seed teridentifikasi & diurut per FK (lihat lampiran) · `v_pegawai` view diekstrak dari draft dump (disesuaikan `biodata_id`)
- [x] **odb.6** ✅ — V1_0_0__baseline.sql (89 tabel) gantikan `V1..V5_1_0` · V5_0_8/V5_0_9 dihapus · seed wrapped FK_CHECKS=0 · flyway clean+migrate SUKSES (25 migrasi, 23s)
- [ ] **odb.7** — GATE-1: boot `validate` HIJAU, semua entity cocok schema
- [ ] **odb.8** — GATE-2: `jooqCodegen` sukses · ENUM→enum JOOQ · snake_case di generated · diff di-commit (ADR-0015) · status drift Testcontainers dicatat (ADR-0004)

## Lampiran: Urutan Seed pasca-squash (23 V3_*)

Seed tetap jadi migration terpisah pasca-baseline, urut berdasarkan FK:

**Batch A — Master reference (tanpa FK ke seed lain)**

| Urut | File | Isi |
|:---:|---|---|
| 1 | `V3_0_0` | `level`, `golongan`, `grade` |
| 2 | `V3_0_1` | `organisasi` |
| 3 | `V3_0_5` | `jenis_keahlian`, `jenis_kitas`, `jenis_pelatihan`, `jenjang_pendidikan` |
| 4 | `V3_0_6` | `jenis_sp`, `sanksi_sp` |
| 5 | `V3_0_19` | `pref_role`, `cuti_jenis` |

**Batch B — Master dengan FK**

| Urut | File | Isi |
|:---:|---|---|
| 6 | `V3_0_2` | `jabatan` part1 (FK: organisasi, level, parent) |
| 7 | `V3_0_4` | `jabatan` part2 |
| 8 | `V3_0_22` | `profesi` part1 (FK: grade, jabatan, level, organisasi) |
| 9 | `V3_0_20` | `profesi` part2 |

**Batch C — Penggajian master**

| Urut | File | Isi |
|:---:|---|---|
| 10 | `V3_0_13` | `gaji_pendapatan_non_pajak`, `gaji_profil` |
| 11 | `V3_0_17` | `rumah_dinas`, `gaji_tunjangan` |
| 12 | `V3_0_18` | `gaji_potongan_tkk`, `gaji_parameter_setting` |
| 13 | `V3_0_21` | `alasan_berhenti` |
| 14 | `V3_0_6_1` | `dasar_gaji` |

**Batch D — Penggajian detail (FK: dasar_gaji)**

| Urut | File | Isi |
|:---:|---|---|
| 15 | `V3_0_7` — `V3_0_12` | `detail_dasar_gaji` (6 part, 545 records) |
| 21 | `V3_0_14` — `V3_0_16` | `gaji_komponen` (3 part, FK: gaji_profil) |

**View v_pegawai** (ekstrak dari draft dump line 3230)
- Bergabung setelah baseline DDL + seed (tergantung tabel: pegawai, biodata, organisasi, jabatan, golongan)
- Perlu adaptasi: view saat ini pakai `peg.nik` → sesuaikan ke `biodata_id`
- Ditempatkan sebagai migration terakhir setelah semua seed

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

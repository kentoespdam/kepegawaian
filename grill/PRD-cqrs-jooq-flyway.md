# PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian

**Status:** `needs-triage`
**Tanggal:** 2026-05-05
**Sumber:** [Grilling Session](./2026-05-05_cqrs-jooq-flyway-migration.md)

---

## Problem Statement

Aplikasi kepegawaian PERUMDAMTS saat ini menggunakan arsitektur monolitik di mana setiap service mencampur operasi baca dan tulis dalam satu class. Semua operasi baca melewati JPA/Hibernate yang menyebabkan:

1. **N+1 query problem** — `FetchType.EAGER` pada relasi utama (`Pegawai` → `Biodata`, `Organisasi`, `Jabatan`) memicu query tambahan setiap kali entity di-load
2. **Over-fetching** — Setiap query baca me-load seluruh entity graph meskipun hanya butuh beberapa kolom
3. **Audit overhead berlebihan** — `@Audited` (Envers) di base class `IdsAbstract` berarti 60+ entity memiliki tabel audit, padahal hanya ~8 entity yang secara bisnis memerlukan full revision history
4. **Schema management manual** — Tidak ada migration tool; DDL dikelola Hibernate `create` di dev dan manual di production
5. **Kolom tidak terpakai** — `changedStatus` ada di setiap entity via `IdsAbstract`, padahal hanya relevan untuk approval workflow di profil

## Solution

Menerapkan **CQRS** (Command Query Responsibility Segregation) secara bertahap di seluruh domain kepegawaian, didukung oleh:

- **JOOQ** sebagai query engine untuk seluruh operasi baca — type-safe SQL dengan direct DTO projection, menghilangkan N+1 dan over-fetching
- **JPA/Hibernate** tetap untuk operasi tulis — mempertahankan AuditAware, Envers (selektif), soft-delete, dan optimistic locking
- **Flyway** untuk schema versioning — semua perubahan schema terdokumentasi dan reproducible
- **Audit three-tier** — Envers hanya untuk entity kritis, simple audit untuk sisanya

## User Stories

1. Sebagai developer, saya ingin schema database dikelola Flyway, sehingga perubahan schema terdokumentasi, reproducible, dan bisa di-rollback
2. Sebagai developer, saya ingin Flyway baseline dari schema production yang ada, sehingga environment baru bisa di-bootstrap dari nol
3. Sebagai developer, saya ingin JOOQ code-gen dari database live, sehingga saya mendapat class Java type-safe yang merepresentasikan tabel dan kolom
4. Sebagai developer, saya ingin JOOQ generated code di-commit ke Git, sehingga CI/CD tidak perlu koneksi database saat build
5. Sebagai developer, saya ingin setiap domain memiliki `CommandService` dan `QueryService` terpisah, sehingga operasi baca dan tulis bisa dioptimasi secara independen
6. Sebagai developer, saya ingin JOOQ query langsung memproyeksikan ke DTO, sehingga tidak perlu mapping entity-ke-DTO yang mahal
7. Sebagai developer, saya ingin repository diorganisasi per domain dengan subdirectory `jpa/` dan `jooq/`, sehingga mudah menemukan file berdasarkan domain
8. Sebagai developer, saya ingin semua relasi JPA menggunakan `FetchType.LAZY`, sehingga command-side tidak over-load data saat write
9. Sebagai developer, saya ingin Envers `@Audited` hanya pada entity kritis (Pegawai, Riwayat SK/SP/Mutasi/Kontrak/Terminasi, CutiPegawai, GajiProfil), sehingga overhead write berkurang drastis
10. Sebagai developer, saya ingin `changedStatus` dihapus dari `IdsAbstract` dan approval ditangani entity terpisah, sehingga base class lebih bersih
11. Sebagai pengguna API, saya ingin endpoint baca lebih cepat, sehingga daftar pegawai dan pencarian tidak lambat
12. Sebagai pengguna API, saya ingin response hanya berisi data yang diperlukan (tanpa over-fetching), sehingga payload lebih kecil dan response lebih cepat
13. Sebagai DBA, saya ingin jumlah tabel audit berkurang dari 60+ menjadi ~8-10, sehingga storage dan backup lebih efisien
14. Sebagai DevOps, saya ingin `DDL_AUTO=none` di semua environment, sehingga Hibernate tidak pernah mengubah schema tanpa melalui Flyway
15. Sebagai developer, saya ingin migrasi dilakukan bertahap per domain (master → profil → pegawai → kepegawaian → cuti → penggajian → laporan), sehingga risiko minimal dan setiap iterasi bisa di-deploy
16. Sebagai developer, saya ingin service layer tanpa interface (concrete class langsung), konsisten dengan mail-service, sehingga mengurangi boilerplate
17. Sebagai developer, saya ingin controller inject `CommandService` dan `QueryService` secara terpisah, sehingga dependency jelas dan testable
18. Sebagai developer masa depan, saya ingin bisa menambahkan SQIDS dan MapStruct di atas JOOQ query layer, sehingga ID encoding bisa diterapkan tanpa refactor besar

## Implementation Decisions

### Modul yang Dibangun/Dimodifikasi

#### M1: Flyway Infrastructure
- Enable Flyway dependency di `build.gradle` (uncomment `flyway-core` dan `flyway-mysql`)
- Generate `V1__baseline.sql` dari schema database production yang sudah ada
- Rename folder `db/migrations/` → `db/migration/`
- Hapus `V1_0_0__create_master.sql` (tidak representatif — hanya 7 dari 60+ tabel)
- Set `FLYWAY_ENABLED=true`, `DDL_AUTO=none` di semua environment
- Konfigurasi Flyway di `application.yml`

#### M2: JOOQ Code Generation
- Tambah Gradle task `jooqCodegen` yang konek ke MariaDB live dan generate class Java
- Output ke `src/main/java/id/perumdamts/kepegawaian/jooq/`
- Generated code di-commit ke Git
- Package structure: `jooq.tables`, `jooq.tables.records`, `jooq.keys`
- Konfigurasi `jooq` section di `build.gradle` dengan database credentials dari environment variable

#### M3: IdsAbstract Refactoring
- Hapus `@Audited` dari `IdsAbstract` — pindah ke entity-level (Tier 1 only)
- Hapus field `changedStatus` dari `IdsAbstract`
- Pertahankan `@EntityListeners(AuditingEntityListener.class)` untuk simple audit
- Pertahankan `@Version` untuk optimistic locking

#### M4: Entity Performance Hardening
- Ubah semua `FetchType.EAGER` ke `FetchType.LAZY` di seluruh entity
- Tambah `@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)` ke entity Tier 1
- Flyway migration untuk drop kolom `changed_status` dari semua tabel
- Flyway migration untuk drop tabel `_aud` yang tidak diperlukan (Tier 2 dan 3 entities)

#### M5: Master Domain CQRS (Pilot)
- Split 15+ master services menjadi `XxxCommandService` + `XxxQueryService`
- Entity: Golongan, Jabatan, Organisasi, Level, Grade, Profesi, JenjangPendidikan, JenisKeahlian, JenisPelatihan, JenisSp, Sanksi, AlatKerja, Apd, AlasanBerhenti, RumahDinas, HariLibur
- Buat `repositories/master/jpa/` (pindahkan existing JPA repos)
- Buat `repositories/master/jooq/` (baru — JOOQ query repos)
- Update controller untuk inject CommandService + QueryService
- Hapus interface lama (misal `GolonganService` interface)

#### M6: Profil Domain CQRS
- Split ~8 profil services: Biodata, Pendidikan, Pelatihan, Keahlian, ProfilKeluarga, PengalamanKerja, LampiranProfil, KartuIdentitas
- Perhatian khusus: Biodata menggunakan NIK (String) sebagai PK, bukan Long

#### M7: Pegawai Domain CQRS
- Split PegawaiService + GenericPegawaiService
- Entity terbesar dengan banyak relasi — JOOQ query repository paling kompleks
- Perlu multiple DTO projection (list ringkas, detail lengkap, dll)

#### M8: Kepegawaian Domain CQRS
- Split services: RiwayatSk, RiwayatSp, RiwayatMutasi, RiwayatKontrak, RiwayatTerminasi, LampiranSk
- Semua entity ini Tier 1 (full Envers)

#### M9: Cuti Domain CQRS
- Split services: CutiPengajuan, CutiApproval, CutiApprovalChain, CutiJenis, CutiKuota
- Approval workflow tetap di command-side

#### M10: Penggajian Domain CQRS
- Split ~15 payroll services
- Batch processing dan Kafka integration tetap di command-side
- Query-side untuk reporting dan lookup

### Architectural Decisions

Seluruh keputusan arsitektur didokumentasikan di:
- `CONTEXT.md` — Domain glossary
- `docs/adr/0001-cqrs-jooq-query-jpa-command.md` — CQRS split
- `docs/adr/0002-envers-selective-three-tier-audit.md` — Envers three-tier

### Schema Changes (via Flyway)

- `V1__baseline.sql` — Full schema snapshot dari production
- `V2__drop_changed_status.sql` — Hapus kolom `changed_status` dari semua tabel
- `V3__drop_unnecessary_audit_tables.sql` — Drop tabel `_aud` untuk entity Tier 2 dan 3
- Migration berikutnya: sesuai kebutuhan per domain saat CQRS diterapkan

## Testing Decisions

### Prinsip Testing
- Test external behavior, bukan implementation detail
- Command service: unit test dengan mocked JPA repository
- Query service: integration test terhadap database (JOOQ queries perlu database nyata untuk validasi SQL)
- Controller: `@WebMvcTest` untuk validasi endpoint contract

### Modul yang Di-test

| Modul | Jenis Test | Prioritas |
|-------|-----------|-----------|
| M5 Master CommandService | Unit test (mock JPA repo) | Tinggi — pilot pattern |
| M5 Master QueryRepository | Integration test (Testcontainers/in-memory DB) | Tinggi — validasi JOOQ SQL |
| M3 IdsAbstract | Unit test (entity lifecycle) | Sedang |
| M7 Pegawai QueryService | Integration test | Tinggi — query paling kompleks |

### Prior Art
Dari mail-service, pola testing yang sudah terbukti:
- `QuickMessageCommandServiceTest` — unit test command service master data dengan `setUp()`, test `toggleStatus` (active/inactive/deleted/notFound)
- `PublicationQueryRepositoryTest` — integration test JOOQ query repository dengan filter dan pagination

## Out of Scope

- **SQIDS encoding** — Akan diimplementasi di fase berikutnya setelah CQRS stabil
- **MapStruct** — Ditambahkan bersamaan dengan SQIDS
- **Data migration dari SmartOffice** — Sudah selesai, tidak termasuk dalam scope ini
- **Perubahan endpoint API contract** — Fokus pada internal refactor, response shape tetap sama agar frontend tidak breaking
- **Multi-tenancy** — Tidak dibahas dalam grilling session
- **Microservice decomposition** — Tetap monolith, hanya internal restructuring

## Further Notes

- Flyway hanya mengelola DDL (schema), bukan DML (data) — data dari SmartOffice sudah dimigrasikan sepenuhnya
- JOOQ code-gen akan dijalankan manual via Gradle task saat schema berubah — bukan otomatis saat setiap build
- Envers `@Audited` yang dihapus dari `IdsAbstract` memerlukan Flyway migration untuk drop tabel `_aud` yang tidak dibutuhkan — tabel ini bisa cukup besar jika ada riwayat perubahan yang panjang
- Migration domain dilakukan secara atomic per domain — satu domain selesai (service + repo + controller + test) sebelum pindah ke domain berikutnya
- `changedStatus` akan dihapus di Flyway migration terpisah setelah memastikan tidak ada logic yang bergantung padanya

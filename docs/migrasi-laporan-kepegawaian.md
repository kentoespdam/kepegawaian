# Migrasi Laporan Kepegawaian — Plan & Checklist

Dokumen rencana dan checklist migrasi service pelaporan kepegawaian (`/laporan/kepegawaian/*`) dari FastAPI Python (`LAPORAN_KEPEGAWAIAN_URL`) menjadi native Spring Boot berbasis JOOQ DSL, Apache POI, Claim Order Pattern (Asynchronous Background Worker), dan scheduled housekeeping per ADR-0042.

Terdapat 5 beads issue yang terbagi dalam 5 klaster tahapan:

- **Klaster A — Fondasi Aset & Storage** (`kepegawaian-jbzj`)
- **Klaster B — Kontrak & Domain DTO** (`kepegawaian-2udc`)
- **Klaster C — Query Engine jOOQ** (`kepegawaian-yvjg`)
- **Klaster D — Asynchronous Processing & Claim Order Worker** (`kepegawaian-x6tq`)
- **Klaster E — Scheduled Cron Cleanup & Quality Gates** (`kepegawaian-lcti`)

---

## Status Realisasi

| Urut | ID | Prio | Status | Deskripsi Singkat |
|------|----|------|--------|-------------------|
| 1 | `kepegawaian-jbzj` | P1 | **OPEN** | Pindahkan template xlsx & Setup Storage Laporan Sementara |
| 2 | `kepegawaian-2udc` | P1 | **OPEN** | Buat Sub-Package Laporan Terpusat & Java Record DTO |
| 3 | `kepegawaian-yvjg` | P1 | **OPEN** | Translasi Raw SQL Python ke jOOQ DSL |
| 4 | `kepegawaian-x6tq` | P1 | **OPEN** | Implementasi Claim Order Pattern (Background Worker) |
| 5 | `kepegawaian-lcti` | P2 | **OPEN** | Implementasi Scheduled Cron Cleanup & JSON Parity Testing |

---

## Urutan Claim

| Urut | ID | Prio | Klaster | Judul singkat | Alasan urutan |
|------|----|------|---------|---------------|---------------|
| 1 | `kepegawaian-jbzj` | P1 | A | Pindahkan template xlsx & Setup Storage Laporan Sementara | **Fondasi Fisik.** Aset workbook Excel (`.xlsx`) dan lokasi direktori temporary storage harus tersedia sebelum worker rendering dapat memproses file. Menyiapkan classpath template dan infrastruktur storage `FileUploadUtilImpl`. |
| 2 | `kepegawaian-2udc` | P1 | B | Buat Sub-Package Laporan Terpusat & Java Record DTO | **Kontrak Data.** Semua query repository jOOQ membutuhkan target proyeksi tipe data yang strictly-typed. Membuat immutable Java record DTO dan struktur sub-package terpusat untuk 8 modul laporan. |
| 3 | `kepegawaian-yvjg` | P1 | C | Translasi Raw SQL Python ke jOOQ DSL | **Logika Ekstraksi Data.** Menggantikan query SQL mentah Python dengan jOOQ DSL type-safe query repositories (`Tables.*`) dan record mapper statis untuk 8 modul laporan. |
| 4 | `kepegawaian-x6tq` | P1 | P1 | Implementasi Claim Order Pattern (Background Worker) | **Orkestrasi Asinkron & Rendering.** Menghubungkan query jOOQ dengan rendering Apache POI via asynchronous worker, claim ticket generation, dan endpoint polling/download untuk menghindari HTTP timeout. |
| 5 | `kepegawaian-lcti` | P2 | E | Implementasi Scheduled Cron Cleanup & JSON Parity Testing | **Housekeeping & Verifikasi.** Pembersihan berkas kedaluwarsa secara berkala (`@Scheduled`), validasi paritas JSON response (Spring Boot vs Python), serta dekomisi proxy lama. |

---

## Catatan Ketergantungan

```text
[Klaster A: kepegawaian-jbzj] (Templates & Temp Storage)
             │
             ▼
[Klaster B: kepegawaian-2udc] (Record DTOs & Sub-packages)
             │
             ▼
[Klaster C: kepegawaian-yvjg] (jOOQ Repositories & Mappers)
             │
             ▼
[Klaster D: kepegawaian-x6tq] (Claim Order Worker & Endpoints)
             │
             ▼
[Klaster E: kepegawaian-lcti] (Scheduled Cron Cleanup & Parity Tests)
```

---

## Perintah Claim

```bash
# Eksekusi serial berurutan
bd update kepegawaian-jbzj --claim   # Issue 1: Template xlsx & Storage Sementara
bd update kepegawaian-2udc --claim   # Issue 2: Record DTOs & Package Structure
bd update kepegawaian-yvjg --claim   # Issue 3: Translasi jOOQ Query
bd update kepegawaian-x6tq --claim   # Issue 4: Claim Order Background Worker
bd update kepegawaian-lcti --claim   # Issue 5: Scheduled Cleanup & Parity Testing
```

---

## Checklist Detail per Issue

> Pola checklist: **Read** (verifikasi) → **Plan** (catat pendekatan) → **Edit** (ubah kode) →
> **compile** (wajib `./gradlew clean compileJava` per memory `clean-compile-required`) →
> **test** (unit/integration/smoke test) →
> **commit** (1 commit kohesif per issue, per memory `commit-granularity`) →
> **close** (`bd close <id>` + `git restore --staged .beads/issues.jsonl`).

### 1. `kepegawaian-jbzj` — Migrasi Laporan 1: Pindahkan template xlsx & Setup Storage Laporan Sementara

- [x] **Read** direktori template Python `/home/dev/python/laporan-kepegawaian/template/` dan pastikan file template ada:
  - `template_duk.xlsx`
  - `template_dnp.xlsx`
  - `template_mutasi.xlsx`
  - `template_kontrak.xlsx`
  - `template_kenaikan_berkala.xlsx`
  - `template_lta.xlsx`
  - `template_pendidikan_2.xlsx`
- [x] **Read** `src/main/java/id/perumdamts/kepegawaian/utils/FileUploadUtil.java` dan `FileUploadUtilImpl.java` untuk konvensi path penyimpanan file lokal.
- [x] **Plan** siapkan folder classpath target `src/main/resources/templates/laporan/` dan definisikan sub-direktori storage sementara (misal `attachments/laporan/temp/` atau via konfigurasi storage terpusat).
- [x] **Edit** salin semua berkas template `.xlsx` dari repo Python ke `src/main/resources/templates/laporan/`.
- [x] **Edit** tambahkan interface dan implementasi method di `FileUploadUtil`/`FileUploadUtilImpl` (atau helper dedicated `ReportStorageHelper`) untuk:
  - Membuat direktori temporary report jika belum ada.
  - Menyimpan byte stream / workbook hasil render ke direktori sementara dengan UUID/ticket hash.
  - Mengambil `Path` file laporan berdasarkan ticket id.
  - Menghapus berkas laporan usang / dibatalkan.
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** unit test / smoke test memverifikasi `ClassPathResource("templates/laporan/template_duk.xlsx").exists()` bernilai `true` dan verifikasi penulisan file temporary storage.
- [x] **commit** `feat(kepegawaian-jbzj): copy excel report templates and setup temporary file storage`.
- [x] **close** `bd close kepegawaian-jbzj` + `git restore --staged .beads/issues.jsonl`.

---

### 2. `kepegawaian-2udc` — Migrasi Laporan 2: Buat Sub-Package Laporan Terpusat & Java Record DTO

- [x] **Read** schema model Python di `/home/dev/python/laporan-kepegawaian/app/models/` (`duk.py`, `dnp.py`, `so.py`, `mutasi.py`, `kontrak.py`, `kenaikan_berkala.py`, `lepas_tanggungan_anak.py`, `statistik.py`).
- [x] **Read** ADR-0042 section "Layer structure" dan pastikan penamaan sub-package konsisten dengan arsitektur CQRS:
  - `repositories/laporan/kepegawaian/`
  - `services/laporan/kepegawaian/`
  - `dto/laporan/kepegawaian/`
  - `mapper/laporan/kepegawaian/`
- [x] **Plan** definisikan Java record DTOs immutable untuk 8 modul laporan dan response claim order:
  - `DukResponse`
  - `DnpResponse`, `DnpOrganisasiResponse`
  - `SoResponse` (hierarchical node record)
  - `MutasiResponse`
  - `KontrakResponse`
  - `KenaikanBerkalaResponse`, `KenaikanBerkalaCountResponse`
  - `LtaResponse`, `LtaCountResponse`
  - Statistik responses: `StatistikGolonganResponse`, `StatistikPendidikan1Response`, `StatistikPendidikan2Response`, `StatistikUmurResponse`, `StatistikUmurRangeResponse`, `StatistikJenisKelaminResponse`, `StatistikGelarResponse`, `StatistikAgamaResponse`, `StatistikStatusPegawaiResponse`
  - Ticket DTO: `ReportJobResponse` (`jobId`, `status`, `downloadUrl`, `createdAt`, `completedAt`, `errorMessage`)
- [x] **Edit** buat Java record DTOs di package `id.perumdamts.kepegawaian.dto.laporan.kepegawaian`.
- [x] **Edit** buat filter DTO/Enums pendukung jika belum ada (`EFilterKontrak`, `EFilterLta`, `EFilterKenaikanBerkala`).
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** verifikasi Jackson serialisasi record DTO via test class.
- [x] **commit** `feat(kepegawaian-2udc): scaffold report sub-packages and immutable java record dtos`.
- [x] **close** `bd close kepegawaian-2udc` + `git restore --staged .beads/issues.jsonl`.

---

### 3. `kepegawaian-yvjg` — Migrasi Laporan 3: Translasi Raw SQL Python ke jOOQ DSL

- [x] **Read** implementasi query SQL di `/home/dev/python/laporan-kepegawaian/app/services/` untuk setiap modul laporan.
- [x] **Read** tabel-tabel jOOQ yang ter-generate di `org.jooq.kepegawaian.Tables` (`PEGAWAI`, `BIODATA`, `GOLONGAN`, `JABATAN`, `ORGANISASI`, `PENDIDIKAN`, `JENJANG_PENDIDIKAN`, `RIWAYAT_SK`, `RIWAYAT_SP`, `SANKSI_SP`, `RIWAYAT_KONTRAK`, `RIWAYAT_MUTASI`, `PROFIL_KELUARGA`, `STATISTIK_PEGAWAI`, `LEVEL`).
- [x] **Plan** petakan MySQL functions ke jOOQ DSL:
  - `TIMESTAMPDIFF(YEAR, a, b)` → `DSL.field("TIMESTAMPDIFF(YEAR, {0}, {1})", Integer.class, a, b)`
  - `TIMESTAMPDIFF(MONTH, a, b)` → `DSL.field("TIMESTAMPDIFF(MONTH, {0}, {1})", Integer.class, a, b)`
  - `CONCAT_WS(' ', a, b)` → `DSL.field("CONCAT_WS(' ', {0}, {1})", String.class, a, b)`
  - `DATE_FORMAT(d, '%d.%m.%Y')` → `DSL.field("DATE_FORMAT({0}, '%d.%m.%Y')", String.class, d)`
  - `IF(cond, a, b)` → `DSL.field("IF({0}, {1}, {2})", type, cond, a, b)`
  - `IFNULL(a, b)` → `DSL.coalesce(a, b)`
- [x] **Edit** buat query repositories di `id.perumdamts.kepegawaian.repositories.laporan.kepegawaian`:
  - `DukRepository`
  - `DnpRepository`
  - `SoRepository`
  - `MutasiRepository`
  - `KontrakRepository` (dynamic WHERE condition builder)
  - `KenaikanBerkalaRepository` (dynamic condition builder: BULAN_INI, GTE_1, GTE_2, TAHUN_INI)
  - `LtaRepository`
  - `StatistikRepository` (8 query aggregasi)
- [x] **Edit** buat mapper statis di `id.perumdamts.kepegawaian.mapper.laporan.kepegawaian` untuk konversi `Record` jOOQ ke record DTO.
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** jalankan slice test / integration test query jOOQ dengan test database untuk memvalidasi syntax dan mapping.
- [x] **commit** `feat(kepegawaian-yvjg): translate python sql queries into native jooq repositories and mappers`.
- [x] **close** `bd close kepegawaian-yvjg` + `git restore --staged .beads/issues.jsonl`.

---

### 4. `kepegawaian-x6tq` — Migrasi Laporan 4: Implementasi Claim Order Pattern (Background Worker)

- [x] **Read** ADR-0042 spesifikasi Asynchronous Claim Order Pattern dan Apache POI workbook streaming.
- [x] **Read** permission mapping: re-use existing authority (`PEGAWAI:READ`, dll) pada controller.
- [x] **Plan** arsitektur Claim Order Pattern:
  1. *Ticket State Manager*: kelola lifecycle job (SUBMITTED / PROCESSING / COMPLETED / FAILED) menggunakan in-memory state / Redis dengan TTL tiket.
  2. *Worker Service*: `@Async("reportTaskExecutor")` worker yang mengambil data dari jOOQ repository, mengisi template POI (`.xlsx`), menulis ke temporary storage, dan mengupdate state tiket.
  3. *Endpoints*:
     - `POST /laporan/kepegawaian/{modul}/export` (atau trigger async pada endpoint excel) mengembalikan `jobId` & `claimUrl`.
     - `GET /laporan/kepegawaian/jobs/{jobId}` mengembalikan status progress / detail job.
     - `GET /laporan/kepegawaian/jobs/{jobId}/download` streaming binary `.xlsx` jika status sudah COMPLETED.
- [x] **Edit** implementasikan core asynchronous job manager (`ReportJobManager` / `ReportJobService`).
- [x] **Edit** implementasikan concrete service untuk 8 modul laporan di `id.perumdamts.kepegawaian.services.laporan.kepegawaian` (`DukService`, `DnpService`, `SoService`, `MutasiService`, `KontrakService`, `KenaikanBerkalaService`, `LtaService`, `StatistikService`) yang mengintegrasikan jOOQ repo + POI generation via worker.
- [x] **Edit** buat / update controller laporan di `id.perumdamts.kepegawaian.controllers.laporan.kepegawaian` untuk mendukung claim order trigger dan buat `ReportJobController` untuk polling & download.
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** integration test async export: trigger request → poll status → download completed file.
- [x] **commit** `feat(kepegawaian-x6tq): implement asynchronous claim order worker and excel download endpoints`.
- [x] **close** `bd close kepegawaian-x6tq` + `git restore --staged .beads/issues.jsonl`.

---

### 5. `kepegawaian-lcti` — Migrasi Laporan 5: Implementasi Scheduled Cron Cleanup & JSON Parity Testing

- [x] **Read** ADR-0042 section "Housekeeping penyimpanan lokal" dan retensi berkas sementara (TTL 2-24 jam).
- [x] **Plan** rancang:
  1. `@Scheduled` cron task untuk membersihkan berkas `.xlsx` di folder temporary report yang berusia lebih dari threshold (misal > 2 jam).
  2. JSON Parity Test suite yang membandingkan response dari Spring Boot terhadap snapshot response FastAPI Python.
  3. Penghapusan proxy `LaporanKepegawaianService` dan env var `LAPORAN_KEPEGAWAIAN_URL`.
- [x] **Edit** buat scheduled job `ReportCleanupScheduler` dengan cron schedule (misal `@Scheduled(cron = "${app.report.cleanup.cron:0 0 * * * *}")`) untuk purge file expired.
- [x] **Edit** buat JSON parity integration test (`LaporanParityIntegrationTest`) memvalidasi struktur JSON field-by-field untuk DUK, DNP, SO, Mutasi, Kontrak, Kenaikan Berkala, LTA, dan Statistik.
- [x] **Edit** hapus `LaporanKepegawaianService` (proxy lama) dan bersihkan referensi `LAPORAN_KEPEGAWAIAN_URL` dari `application.yml` serta controller.
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** jalankan `./gradlew test` memastikan seluruh test parity dan regression lulus.
- [x] **commit** `feat(kepegawaian-lcti): add scheduled file cleanup, json parity test suite, and decommission python proxy`.
- [x] **close** `bd close kepegawaian-lcti` + `git restore --staged .beads/issues.jsonl`.

---

## Pola Commit yang Direkomendasikan

Mengikuti konvensi commit kohesif per beads issue:

- `feat(kepegawaian-jbzj): copy excel report templates and setup temporary file storage`
- `feat(kepegawaian-2udc): scaffold report sub-packages and immutable java record dtos`
- `feat(kepegawaian-yvjg): translate python sql queries into native jooq repositories and mappers`
- `feat(kepegawaian-x6tq): implement asynchronous claim order worker and excel download endpoints`
- `feat(kepegawaian-lcti): add scheduled file cleanup, json parity test suite, and decommission python proxy`

---

## Cross-Reference Memory & ADR

- [ADR-0042: Laporan Kepegawaian Native JOOQ](file:///home/dev/idea/kepegawaian/docs/adr/0042-laporan-kepegawaian-native-jooq.md) — Arsitektur native jOOQ, POI, Claim Order, dan Scheduled Cleanup.
- [ADR-0017: CQRS Layer Split Architecture](file:///home/dev/idea/kepegawaian/docs/adr/0017-cqrs-layer-split-architecture.md) — Pemisahan layer query (jOOQ DSL) dan command (JPA).
- `clean-compile-required-after-refactor-commits.md` — Wajib `./gradlew clean compileJava` sebelum verifikasi commit.
- `commit-granularity-cross-cutting-rewrite.md` — 1 commit kohesif per beads issue.
- `beads-issues-jsonl-auto-stage-pattern.md` — `bd close` auto-stage, periksa `.beads/issues.jsonl`.

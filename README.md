# Kepegawaian — PERUMDAMTS Employee Management

REST API untuk manajemen kepegawaian (pegawai, profil, master data, cuti,
penggajian) — **PERUMDAMTS**.

Build dari **worktree `rewrite/master-cqrs`** (Spring Boot 4 + CQRS); kode lama
tersedia dalam bentuk read-only sebagai referensi spec.

> **Canonical ops guide:** [CLAUDE.md](./CLAUDE.md) — build/run, arsitektur, issue
> tracking (beads), dan GitNexus code intelligence.
> **Rewrite/layout:** [WORKTREE.md](./WORKTREE.md).
> **Domain language (lazy read):** [CONTEXT.md](./CONTEXT.md) → pilih sub-context yang
> relevan.

---

## Makefile (Default Workflow)

Sebagian besar alur dev/ops dibungkus di `Makefile`. Untuk melihat daftar target
yang tersedia:

```bash
make migrate-help   # menampilkan target migrasi yang terdefinisi di Makefile
```

> **Catatan:** `make help` tidak terdefinisi di `Makefile` ini — gunakan
> `make migrate-help` untuk menu migrasi (baca juga bagian _Data Migration_ di
> bawah).

### Setup Lingkungan

```bash
# Salin template env
cp .env.example .env        # lalu edit .env sesuai env Anda (baca bagian Env vars di bawah)

# Migration tooling (opsional — butuh Python untuk tools/migration)
make migrate-venv           # buat .venv + pasang tools/migration/requirements.txt
```

> **Catatan:** project ini juga menyediakan `.env.dummy` sebagai referensi nilai
> default; `.env` adalah file konfigurasi yang dipakai saat eksekusi (lihat
> [application.yml](./src/main/resources/application.yml) dan pola baca env di
> `Makefile` / `bootRun` task).

> **Dev vs production image:** Dockerfiles di `docker/development/` dan
> `docker/production/` keduanya memakai GraalVM JDK 25 Community dan memuat JAR
> dari `build/libs/`. Production compose lewatkan `--env-file ./.env` saat
> `make start-prod` agar env prod dibaca dari file.

### Build & Jalankan Dev (Make)

```bash
# Build jar
make bootJar                # ./gradlew bootJar

# Jalankan stack dev via Docker Compose
make start-dev             # docker compose -f ./docker/development/docker-compose.yml up -d

# Berhenti
make stop-dev              # docker compose ... down

# Build ulang + restart (jar fresh + image + compose)
make rebuild-dev           # stop-dev && build-dev && start-dev
```

> **Peringatan tentang kaptur layer Docker:** `build-dev` mem-build ulang jar
> (`bootJar`) sebelum Docker build, dan `rebuild-dev` mengandung `stop-dev` di
> dalamnya. Jangan skip langkah build jika JAR sudah berubah — `docker buildx bake`
> meng-COPY `build/libs/*.jar`, dan cache layer bisa menyimpan JAR lama.

> **Dev tanpa Docker (gradle langsung):** jika tidak ingin memakai Docker compose,
> jalankan `./gradlew bootRun` (baca env dari `.env`/`run.sh`). `run.sh` juga
> tersedia untuk export env dari `.env` lalu menjalankan `gradlew bootRun`.
> Stack Docker development memetakan port `8080:8080` (lihat
> `docker/development/docker-compose.yml`).

### Dev Tanpa Docker (gradle langsung)

Jika hanya ingin menjalankan via Gradle (bukan Docker image):

```bash
./gradlew bootRun        # profile bawaan development; baca env dari .env
```

Skrip `run.sh` juga tersedia untuk poll env dari `.env` lalu menjalankan
`gradlew bootRun`.

### Build Production

```bash
make build-prod          # docker compose -f ./docker/production/docker-compose.yml build
make start-prod          # docker compose --env-file ./.env -f ./docker/production/docker-compose.yml up -d
make stop-prod           # docker compose ... down
```

> **Development vs Production image:** Dockerfiles development dan production
> keduanya memakai GraalVM JDK 25 Community + jar; perbedaan utama ada di
> konfigurasi compose dan env file yang disuntikkan.

### Data Migration (tools/migration)

Alat migrasi data ada di `tools/migration/`. Jalankan lewat make target yang
bersesuaian:

```bash
# Menu bantuan migrasi
make migrate-help

# Preflight (Stage 0: cek konektivitas DB & Appwrite)
make migrate-preflight

# Jalankan seluruh pipeline migrasi
make migrate-run-all

# Jalankan stage tertentu
make migrate-stage STAGE=stage2

# Audit
make migrate-audit
make migrate-audit-integrity
make migrate-reconcile-payroll

# Sinkronisasi file (Phase 2 Worker)
make migrate-sync-files-dry    # dry-run simule
make migrate-sync-files        # jalankan (WORKERS bawaan 4)

# Provisioning auth ke Appwrite
make migrate-sync-auth-dry
make migrate-sync-auth

# Unit test modul migrasi
make migrate-test
```

---

## Quickstart

**Prerequisites:** Java 25, MariaDB, Redis, Appwrite (opsional untuk dev auth).

```bash
./gradlew build         # build + test
./gradlew bootRun       # dev profile (baca config dari .env atau env var)
./gradlew test          # jalankan semua test
./gradlew clean build   # clean build
```

**Env vars wajib (lihat [.env.example](./.env.example) dan [.env.dummy](./.env.dummy)):**

| Variabel | Keterangan | Default di .env.dummy |
|----------|------------|------------------------|
| `DB_HOST` / `DB_PORT` / `DB_SCHEMA` | MariaDB | `192.168.230.84` / `3307` / `kepegawaian_dev_new` |
| `DB_USER` / `DB_PASSWORD` | kredensial DB | `dev` / `password` |
| `REDIS_HOST` / `REDIS_PORT` | Redis | `192.168.1.214` / `6379` |
| `APPWRITE_ENDPOINT` / `APPWRITE_PROJECT_ID` / `APPWRITE_API_KEY` | Appwrite (auth) | disediakan di dummy |
| `PENGGAJIAN_URL` | endpoint penggajian eksternal | `http://192.168.1.214:81` |

> **Dev profile (bawaan):** jika tidak ada Bearer token, request otomatis jadi
> principal statis `DEV` (role `ADMIN`+`SYSTEM`). Token yang ada tetap divalidasi
> secara ketat → invalid/expired → `401`. Lihat [CLAUDE.md](./CLAUDE.md) dan
> [docs/context/language-security.md](./docs/context/language-security.md).
> Beberapa env var/konfigurasi hardcode (mis. ID jabatan tertentu untuk supervisor
> SDM) masih dalam proses migrasi ke config/env — cek [CONTEXT.md](./CONTEXT.md).

---

## Arsitektur & Domain

**Stack:** Spring Boot 4.0.3, Java 25, Gradle, JPA/Hibernate + Envers, JOOQ
(read-side CQRS), Flyway migration, MariaDB, Redis (cache + pub/sub), Appwrite
(JWT).

Pola: `CustomResult.any/list/save/delete()` → `{status, statusText, data, timestamp}`;
CRUD + `@Valid` + `Errors`; tulis/hapus dilindungi `@PreAuthorize("hasRole('ADMIN')")`
atau permission spesifik; halaman pakai `@ParameterObject`; soft delete pakai
flag `is_deleted` (tidak ada hard-delete di domain ini); audit `created_at/by`,
`updated_at/by` lewat JPA `AuditAware` + Envers revision history.

**Folder sumber utama:** `src/main/java/id/perumdamts/kepegawaian/`.

| Domain (subpaket) | Isi singkat |
|---------------------|-------------|
| `profil/` | biodata, pendidikan, keahlian, keluarga, pelatihan, pengalaman kerja, kartu identitas, lampiran, **Pengajuan Perubahan** (antrian approval) |
| `pegawai/` | core employee records — kunci tampil `NIPAM`, NIK asal Biodata, status pegawai/kerja |
| `master/` | referensi: Organisasi (pohon), Jabatan (pohon + level), Profesi (nama+jabatan+grade), Grade, Level, APD, Alat Kerja, SP/Sanksi, Jenis Cuti, Kuota Cuti, dll |
| `cuti/` | pengajuan & klaim cuti, multi-level approval chain, kuota per-tahun, PIC berjalan |
| `kepegawaian/` | SK (7 jenis + riwayat), SP, mutasi, kontrak, terminasi |
| `penggajian/` | payroll: Dasar Gaji, Profil Gaji, Komponen Gaji, batch processing (`GajiBatchRoot` → `GajiBatchMaster` → proses per-komponen), potongan TKK/tambahan, rekonsiliasi |
| `config/`, `controllers/`, `dto/`, `entities/`, `exceptions/`, `helpers/`, `jooq/`, `mapper/`, `repositories/`, `services/`, `utils/` | kode infrastruktur & layer aplikasi |

Baca konteks per-modul di `docs/context/` sesuai kebutuhan — jangan baca semua
bersamaan; lihat peta di [CONTEXT.md](./CONTEXT.md).

---

## API & Dokumentasi

- Base: konfigurasi port di `SERVER_PORT` (default `8080`).
- Struktur response seragam (`CustomResult`). Lihat pola di kode controller.

**OpenAPI (optional, untuk eksplorasi):** jika project memakai SpringDoc / Swagger UI
dan profile yang bersangkutan mengaktifkannya, dokumentasi OpenAPI tersedia via
endpoint yang sesuai. (Holy kode saat ini fokus pada warisan/refactoring; cek
build.gradle.kts untuk dependency `springdoc-openapi` dan konfigurasi profile.)

> **Catatan:** tidak semua modul sudah tuntas di worktree ini — ini adalah kode
> rewrite yang masih berkembang. Fitur yang sudah ada dan yang belum di-rewrite
> dibedakan lewat konteks/tiket beads.

---

## Database & Migrasi

- **Sumber kebenaran schema:** Flyway migration di `src/main/resources/db/migration`.
- Jalur `ddl-auto` dikontrol via `DDL_AUTO` (bawaan `none` — jangan 그리고
  `update/create` di prod).
- Audit/revisi riwayat: kombinasi JPA `AuditAware` + Envers (terarah ke modul
  penggajian/kepegawaian sesuai ADR yang berlaku).

Untuk urgensi migration khusus (baseline dari dump DB eksisting, injeksi revision
Envers, rekonsiliasi, dll.) lihat ADR di `docs/adr/`.

---

## Test

```bash
./gradlew test
```

Test pakai JUnit 5 / Spring Boot Test + Security Test + Redis Testcontainers +
ArchUnit (guard struktur). Beberapa test context bisa butuh DB/Redis container —
cek konfigurasi test (testcontainers / H2 fallback) di file test masing-masing.

---

## Gabung & Kerja

- **Issue tracking:** beads (`bd`) — lihat [CLAUDE.md](./CLAUDE.md) dan
  [docs/agents/issue-tracker.md](./docs/agents/issue-tracker.md).
- **Code intelligence:** GitNexus (repo `kepegawaian`) — lihat
  [CLAUDE.md](./CLAUDE.md). Sebelum edit, jalankan impact analysis; sebelum commit,
  jalankan detect_changes.
- **Lingkungan kerja:** folder ini = `rewrite/master-cqrs`; kode lama ada di
  `../kepegawaian-legacy` (tag `legacy-snapshot`, read-only) sebagai referensi
  spec mientras grilling/rewriting modul. Lihat [WORKTREE.md](./WORKTREE.md).

---

## Catatan Tambahan

- Worktree ini sedang dalam proses **rewrite**; tidak semua endpoint/modul sudah
  migrated. Rujuk bead/tiket terkait untuk status modul.
- Demo/dummy credentials di `.env.dummy` hanya untuk pengembangan lokal.
- Konfigurasi org spesifik (mis. ID supervisor/manager SDM, ID direksi, kuota cuti
  default, dll.) sebagian besar masih di config/env; komitmen ada untuk memindahkan
  hardcode sesuai roadmap (lihat konten yang relevan di `docs/context/` dan ADR).

---

## Referensi

- [CLAUDE.md](./CLAUDE.md) — panduan operasi, arsitektur, issue tracking, skills.
- [WORKTREE.md](./WORKTREE.md) — setup worktree rewrite vs legacy.
- [CONTEXT.md](./CONTEXT.md) — entry point konteks domain (lazy read).
- `docs/context/` — bahasa & keputusan per-modul.
- `docs/adr/` — catatan keputusan arsitektur.
- `.env.example` / `.env.dummy` — referensi env vars.

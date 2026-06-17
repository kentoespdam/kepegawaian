# Claim Order — Analisis Bug GajiBatchRoot + Config + CQRS (2026-06-17)

Sebelas beads issue hasil analisis komprehensif (file `dummy_prompt.md` scope). Terbagi
dalam 3 klaster:

- **Klaster A — GajiBatchRoot transactional integrity** (7 issue, sentuh file yang sama → serial)
- **Klaster B — Spring Boot configuration** (1 issue, independen)
- **Klaster C — Master CQRS layer-split ADR-0017** (2 issue, bisa paralel via worktree)

## Urutan Claim

| Urut | ID | Prio | Klaster | Judul singkat | Alasan urutan |
|------|----|------|---------|---------------|---------------|
| 1 | `kepegawaian-9v9` | P1 | A | Status pakai `enum.ordinal()` — rapuh terhadap reorder enum | **Fondasi.** Semua transisi status lain (kdo, save, verify) bergantung pada type sistem enum ini benar. Fix ini dulu → call-site `ordinal()`/`value()` jadi konsisten. Lihat catatan: `EProsesGaji.value()` saat ini return `ordinal()` (int) — nama method misleading. |
| 2 | `kepegawaian-g2j` | P1 | A | `logAndBuildFailure` bocorkan `e.getMessage()` mentah ke client | Setelah enum benar, baru fix response shape. Pakai enum `ErrorCode` yang sama (DRY dengan #1). Kalau enum masih broken, error code di response jadi salah map. |
| 3 | `kepegawaian-0fe` | P1 | A | File upload di dalam tx DB → orphan file saat rollback | Topik tx boundary. Butuh fondasi #1 (status field type aman) untuk pola outbox: row minimal disimpan dulu (status=DRAFT) → upload afterCommit → update ke COMPLETED. |
| 4 | `kepegawaian-7rk` | P1 | A | `processPotonganTkk` di dalam tx save() — tx terlalu panjang | Masih tx boundary, mirip #3. Setelah #3 fix, baru audit `processPotonganTkk` apakah write-heavy. Bisa digabung dengan #3 dalam satu commit. |
| 5 | `kepegawaian-u68` | P2 | A | `delete()` tanpa `@Transactional` | Konsistensi anotasi tx dengan `save()` (sudah diurus 5ea5abb sebelumnya). Polish. |
| 6 | `kepegawaian-qgp` | P2 | A | `registerSynchronization` duplikasi di save() & reprocessHandler() | Extract helper `publishAfterCommit(...)`. Aman dilakukan setelah #1, #2, #3, #4 selesai — saat itu body save() & reprocessHandler() sudah final shape. |
| 7 | `kepegawaian-x8o` | P3 | A | `reprocess()` punya parameter `String id` tak terpakai (dead arg) | Cleanup terakhir, 1 line. |
| 8 | `kepegawaian-pvr` | P2 | B | Spring Data Redis: 54 warning boot noise | Independen dari Klaster A. Bisa **paralel** dengan #1–#7 (file berbeda: `application.yml` + `KepegawaianApplication.java`). |
| 9 | `kepegawaian-uf8` | P2 | B | Default kredensial DB & show-sql, no profile separation | Setelah #8 (konfigurasi Redis sudah fix), baru pisah profile dev/prod. `application.yml` jadi base yang strict. |
| 10 | `kepegawaian-6h2` | P2 | C | `LevelServiceImpl` tidak CQRS, 2 repo bocor dari `jpa/` subpackage | Independen dari A & B. Bisa paralel via worktree. Tapi karena ini refactor Layer-2 ADR-0017 (bukan sekadar bug fix), kerjakan **paling akhir** agar fondasi CQRS 4 domain lain (golongan/grade/jabatan/organisasi) sudah stabil sebagai referensi. |
| 11 | `kepegawaian-ytz` | P3 | C | `PegawaiServiceImpl` wildcard+eksplisit = grep blindness | Setelah #10 (semua `master.jpa.*` repo sudah pindah dengan benar), baru perbaiki import di `PegawaiServiceImpl`. |

## Catatan Ketergantungan

```
Klaster A (serial, satu file: GajiBatchRootServiceImpl.java + entity)
   kepegawaian-9v9  (enum type sistem: ordinal vs name vs converter)
        │
        ▼
   kepegawaian-g2j  (response shape, ErrorCode enum DRY dengan #1)
        │
        ▼
   kepegawaian-0fe  (tx boundary: file upload afterCommit + outbox ringan)
        │
        ▼
   kepegawaian-7rk  (tx boundary: processPotonganTkk REQUIRES_NEW)
        │               (bisa satu commit dengan 0fe)
        ▼
   kepegawaian-u68  (@Transactional di delete())
        │
        ▼
   kepegawaian-qgp  (extract publishAfterCommit helper)
        │
        ▼
   kepegawaian-x8o  (dead arg cleanup)

Klaster B (paralel dgn A; config file, bukan service code)
   kepegawaian-pvr  (Redis auto-detect noise)
        │
        ▼
   kepegawaian-uf8  (profile dev/prod pisah)

Klaster C (paralel via worktree; refactor layer)
   ... tunggu A & B selesai ...
   kepegawaian-6h2  (LevelServiceImpl CQRS split + 2 repo pindahkan ke jpa/)
        │
        ▼
   kepegawaian-ytz  (PegawaiServiceImpl: ganti wildcard dgn eksplisit)
```

- `9v9` → fondasi. Tanpa fix ini, pola ordinal/name akan bercampur setelah #3–#6 mengubah flow status.
- `g2j` → butuh `9v9` agar `ErrorCode` mapping akurat (jangan duplikat definisi enum).
- `0fe` → butuh `9v9` agar status `DRAFT`/`COMPLETED` punya enum value valid.
- `7rk` → bisa paralel dengan `0fe` (touchpoint beda), tapi keduanya sama-sama tx refactor — kerjakan bareng agar tidak bolak-balik file.
- `u68` → butuh `0fe`/`7rk` agar pola `@Transactional` di file sudah stabil, baru konsistensi.
- `qgp` → butuh `u68` agar `delete()` final (jika `delete()` juga kirim Kafka).
- `x8o` → terakhir, dead-arg cleanup, signature mungkin berubah saat refactor.
- `pvr` (B) → independen, tidak butuh apa-apa. Bisa kerjakan duluan sambil Klaster A serial.
- `uf8` (B) → butuh `pvr` (Redis config fix dulu, baru pisah profile).
- `6h2` (C) → butuh A & B selesai. Refactor Layer-2 ADR-0017 = wave baru. Jangan ganggu tiket di tengah eksekusi A.
- `ytz` (C) → butuh `6h2` (semua `master.jpa.*` sudah final lokasi, baru fix import di `PegawaiServiceImpl`).

## Perintah Claim

```bash
# Klaster A — serial, satu PR besar (atau dipecah per sub-batch)
bd update kepegawaian-9v9 --claim
bd update kepegawaian-g2j --claim
bd update kepegawaian-0fe --claim
bd update kepegawaian-7rk --claim
bd update kepegawaian-u68 --claim
bd update kepegawaian-qgp --claim
bd update kepegawaian-x8o --claim

# Klaster B — bisa paralel dengan A (file berbeda)
bd update kepegawaian-pvr --claim
bd update kepegawaian-uf8 --claim

# Klaster C — jalankan setelah A & B stabil, ideal via worktree
bd update kepegawaian-6h2 --claim
bd update kepegawaian-ytz --claim
```

---

## Checklist Detail per Issue

> Pola checklist: **Read** (verifikasi) → **plan** (catat pendekatan) → **Edit** (ubah kode) →
> **compile** (wajib `clean compileJava` per memory `clean-compile-required`) →
> **commit** (1 commit kohesif per issue, per memory `commit-granularity`) →
> **close** (`bd close` + `git restore --staged .beads/issues.jsonl`).

### 1. `kepegawaian-9v9` — Status enum pakai `ordinal()`

- [ ] **Read** `entities/commons/EProsesGaji.java` — konfirmasi `value()` saat ini return `int ordinal()` (misleading). Cek `entities/penggajian/GajiBatchRoot.java:47` (`private Integer status = 0;`).
- [ ] **Read** semua call-site yang bandingkan `entity.getStatus()` dengan `EProsesGaji.X.value()` / `.ordinal()`.
- [ ] **Plan** pilih strategi: (A) `AttributeConverter<EProsesGaji, String>` + ubah kolom `status` ke `VARCHAR(32)`, atau (B) rename `value()` → `ordinalValue()`, dokumentasikan kontrak.
- [ ] **Edit** buat `EProsesGajiConverter implements AttributeConverter<EProsesGaji, String>`. Tambah `@Convert(converter = ...)` di field `status` entity (atau cara setara).
- [ ] **Edit** ubah tipe field `status` dari `Integer` jadi `EProsesGaji` (atau `String` + converter).
- [ ] **Edit** migrasi Flyway script: `ALTER TABLE gaji_batch_root MODIFY status VARCHAR(32) NOT NULL;` — simpan sebagai `db/migration/Vxx__gaji_status_to_varchar.sql`.
- [ ] **Edit** update semua call-site: ganti `entity.getStatus().equals(EProsesGaji.PROSES.value())` jadi `entity.getStatus() == EProsesGaji.PROSES`.
- [ ] **compile** `./gradlew clean compileJava` (wajib, per memory `clean-compile-required`).
- [ ] **test** boot + smoke test: create batch → status transisi harus benar, reload dapat enum value benar.
- [ ] **commit** 1 commit kohesif dengan pesan `fix(kepegawaian-9v9): migrate GajiBatchRoot.status to enum-via-converter`.
- [ ] **close** `bd close kepegawaian-9v9` + `git restore --staged .beads/issues.jsonl`.

### 2. `kepegawaian-g2j` — `logAndBuildFailure` bocor `e.getMessage()`

- [ ] **Read** `GajiBatchRootServiceImpl.java:204-207` (helper `logAndBuildFailure`) dan semua catch block (line 100-102, 114-116, dll).
- [ ] **Read** cek apakah sudah ada `@RestControllerAdvice` global — kalau belum, akan dibuat.
- [ ] **Plan** definisikan `enum ErrorCode { DB_ERROR, KAFKA_PUBLISH_FAILED, FILE_UPLOAD_FAILED, VALIDATION_FAILED, UNKNOWN_BATCH_PROCESS, INTERNAL }` dengan message default.
- [ ] **Edit** buat `exceptions/GlobalExceptionHandler.java` (`@RestControllerAdvice`) — map `DataAccessException` → DB_ERROR, `KafkaException` → KAFKA_PUBLISH_FAILED, `RuntimeException("Unknown Batch Process")` → UNKNOWN_BATCH_PROCESS, sisanya → INTERNAL. Response shape: `{ code, message, traceId }`.
- [ ] **Edit** refactor `logAndBuildFailure` jadi: log.error(..., e) → return `SavedStatus.failedWith(errorCode, userMessage)` (bukan raw `e.getMessage()`).
- [ ] **Edit** `Unknown Batch Process` di `reprocess()` (line 110) ubah jadi `SavedStatus.FAILED` — konsisten dengan verify1/2/accept (lihat commit 5ea5abb).
- [ ] **compile** `./gradlew clean compileJava`.
- [ ] **test** trigger `NullPointerException` + constraint violation → response tidak bocor detail.
- [ ] **commit** `fix(kepegawaian-g2j): centralize exception handling with @RestControllerAdvice + ErrorCode`.
- [ ] **close** `bd close kepegawaian-g2j` + restore issues.jsonl.

### 3. `kepegawaian-0fe` — File upload di dalam tx → orphan

- [ ] **Read** `GajiBatchRootServiceImpl.java:55-92` (save flow) + `utils/FileUploadUtil.java`/`FileUploadUtilImpl.java` (signature `uploadPenggajian`).
- [ ] **Read** entity `GajiBatchRootLampiran` — apakah ada field `upload_status`?
- [ ] **Plan** pilih Pola 1 (outbox ringan): row minimal disimpan (status=DRAFT, file_path=null) → `afterCommit` panggil upload → update row dengan `file_path` + status=COMPLETED. Scheduler retry untuk yang gagal.
- [ ] **Edit** tambah `upload_status` di `GajiBatchRootLampiran` entity + Flyway migration.
- [ ] **Edit** refactor `save()`: pisah jadi `saveMetadata()` (tx) + `publishAfterCommit()` (upload + update status). Panggil `processPotonganTkk` di afterCommit juga (DRY untuk #4).
- [ ] **Edit** tambah scheduled job `@Scheduled(fixedDelay = 60_000)` yang scan `upload_status=FAILED` dan retry upload.
- [ ] **compile** + test rollback path: throw RuntimeException setelah upload di test mode → filesystem bersih.
- [ ] **commit** `fix(kepegawaian-0fe): move file upload out of @Transactional via afterCommit + outbox retry`.
- [ ] **close** `bd close kepegawaian-0fe` + restore issues.jsonl.

### 4. `kepegawaian-7rk` — `processPotonganTkk` di dalam tx

- [ ] **Read** class `processPotonganTkk` — apakah read-only, write, atau mix? Berapa row yang di-query?
- [ ] **Plan** berdasarkan audit: kalau write-heavy → `@Transactional(propagation = REQUIRES_NEW)` di `process()`. Kalau read-only → panggil di afterCommit (gabung dengan pola #3).
- [ ] **Edit** sesuaikan anotasi tx atau pindahkan call-site ke afterCommit hook.
- [ ] **Edit** tambah `log.info("processPotonganTkk took {}ms", elapsed)` di awal/akhir.
- [ ] **compile** + test concurrent: 2 batch insert bersamaan → tidak saling tunggu lock.
- [ ] **commit** `fix(kepegawaian-7rk): isolate processPotonganTkk from save() transaction`.
- [ ] **close** `bd close kepegawaian-7rk` + restore issues.jsonl.

### 5. `kepegawaian-u68` — `delete()` tanpa `@Transactional`

- [ ] **Read** `GajiBatchRootServiceImpl.java:165-174` (delete method) + interface `GajiBatchRootService.java`.
- [ ] **Plan** tambah `@Transactional(propagation = REQUIRED)` (sama dengan `save()`). Cek apakah `delete()` juga publish Kafka — kalau ya, bungkus dengan `afterCommit` (DRY dengan #6).
- [ ] **Edit** tambah anotasi + import statement (match existing file: `org.springframework.transaction.annotation.Transactional`).
- [ ] **compile** + test delete dengan exception di tengah → row masih ada (rollback bekerja).
- [ ] **commit** `fix(kepegawaian-u68): add @Transactional to GajiBatchRootServiceImpl.delete()`.
- [ ] **close** `bd close kepegawaian-u68` + restore issues.jsonl.

### 6. `kepegawaian-qgp` — `registerSynchronization` duplikasi

- [ ] **Read** `GajiBatchRootServiceImpl.java:86-98` (di save) dan 188-200 (di reprocessHandler). Identifikasi bagian yang persis sama.
- [ ] **Plan** extract private method:
  ```text
  private void publishAfterCommit(String topic, String key, String payload) { ... }
  ```
- [ ] **Edit** buat helper. Tambah null-check untuk `kafkaTemplate` + `getRecordMetadata()`.
- [ ] **Edit** tambah `log.info("kafka publish topic={} key={} partition={} offset={}", ...)`.
- [ ] **Edit** ganti 2 call-site pakai helper.
- [ ] **compile** + test boot + publish event smoke.
- [ ] **commit** `refactor(kepegawaian-qgp): extract publishAfterCommit helper to dedupe registerSynchronization`.
- [ ] **close** `bd close kepegawaian-qgp` + restore issues.jsonl.

### 7. `kepegawaian-x8o` — Dead arg di `reprocess()`

- [ ] **Read** `GajiBatchRootServiceImpl.java:109-110` + interface `GajiBatchRootService.java` (signature `reprocess`).
- [ ] **Plan** pilih Opsi A (hapus param `String id`, gunakan `request.getId()`) atau Opsi B (gunakan param `id` di body). Rekomendasi: Opsi A — minimal change.
- [ ] **Edit** update interface + impl. Cari & update semua caller.
- [ ] **compile** + pastikan tidak ada warning unused parameter.
- [ ] **commit** `chore(kepegawaian-x8o): remove dead String id arg from reprocess()`.
- [ ] **close** `bd close kepegawaian-x8o` + restore issues.jsonl.

### 8. `kepegawaian-pvr` — Redis warning noise

- [ ] **Read** `application.yml` area `spring.data.redis.*` + `KepegawaianApplication.java:8-9`.
- [ ] **Plan** Opsi A: tambah `spring.data.redis.repositories.enabled: false` di `application.yml`. 1 line, no Java change.
- [ ] **Edit** tambah 1 baris di `application.yml`.
- [ ] **compile** + boot ulang, verifikasi log tidak ada 54 warning.
- [ ] **commit** `chore(kepegawaian-pvr): disable Spring Data Redis repository auto-config to silence 54 boot warnings`.
- [ ] **close** `bd close kepegawaian-pvr` + restore issues.jsonl.

### 9. `kepegawaian-uf8` — Default credentials & show-sql

- [ ] **Read** `application.yml` semua setting JPA, logging, datasource.
- [ ] **Plan** pecah jadi `application.yml` (base strict: show-sql=false, INFO, no creds) + `application-dev.yml` (longgar: show-sql=true, DEBUG, dev creds) + `application-prod.yml` (strict, env-driven secrets).
- [ ] **Edit** buat `application-dev.yml` + `application-prod.yml`. Pindahkan setting longgar & credentials ke dev; setting strict ke base.
- [ ] **Edit** hapus default DB credentials dari `application.yml` (paksa env var di prod).
- [ ] **Edit** set `SPRING_PROFILES_ACTIVE=dev` default di launcher (atau hapus default = paksa set).
- [ ] **compile** + test boot dengan profile `dev` (longgar) dan `prod` (strict, no creds → harus pakai env).
- [ ] **commit** `chore(kepegawaian-uf8): split application-{dev,prod}.yml; remove default DB credentials`.
- [ ] **close** `bd close kepegawaian-uf8` + restore issues.jsonl.

### 10. `kepegawaian-6h2` — `LevelServiceImpl` tidak CQRS, 2 repo bocor

- [ ] **Read** `services/master/level/LevelServiceImpl.java` + `repositories/master/LevelRepository.java` + `JenjangPendidikanRepository.java`.
- [ ] **Read** bandingkan dengan `golongan/`/`grade/`/`jabatan/`/`organisasi/` yang sudah CQRS — pahami polanya.
- [ ] **Plan** dua sub-bagian:
  - 10a: refactor CQRS — pecah `LevelServiceImpl` jadi `LevelQueryService` (JOOQ) + `LevelCommandService` (JPA), sesuai pola 4 domain lain.
  - 10b: `git mv` `LevelRepository.java` → `repositories/master/jpa/LevelRepository.java` (update package). Sama untuk `JenjangPendidikanRepository.java`.
- [ ] **Edit 10a** generate `LevelQueryRepository`, `LevelMapper`, `LevelQueries` (atau setara). Ikuti pola `GolonganQueryRepository` per memory `wave-3-golongan-completion.md`.
- [ ] **Edit 10b** `git mv` + update `package` declaration. Update import di semua call-site (cek: `PegawaiServiceImpl` line 16-20).
- [ ] **compile** `./gradlew clean compileJava` (wajib, per memory `clean-compile-required`).
- [ ] **test** smoke test endpoint yang query `Level`/`JenjangPendidikan`.
- [ ] **commit** `refactor(kepegawaian-6h2): CQRS-split LevelServiceImpl + relocate 2 repos to jpa/ subpackage`.
- [ ] **close** `bd close kepegawaian-6h2` + restore issues.jsonl.

### 11. `kepegawaian-ytz` — `PegawaiServiceImpl` wildcard+eksplisit

- [ ] **Read** `services/pegawai/PegawaiServiceImpl.java:15-20`. List semua `*Repository` yang dipakai.
- [ ] **Plan** hapus wildcard `import id.perumdamts.kepegawaian.repositories.master.*;`. Pertahankan 5 eksplisit, tambah sisanya.
- [ ] **Edit** refactor import section.
- [ ] **compile** `./gradlew clean compileJava` (per memory `clean-compile-required`).
- [ ] **test** smoke test semua endpoint Pegawai.
- [ ] **commit** `chore(kepegawaian-ytz): replace master.* wildcard with explicit jpa.* imports in PegawaiServiceImpl`.
- [ ] **close** `bd close kepegawaian-ytz` + restore issues.jsonl.

## Pola Commit yang Direkomendasikan

Per memory `commit-granularity-cross-cutting-rewrite`: untuk 1 epic/cluster yang saling
bergantungan, 1 commit kohesif lebih murah daripada split per-wave. Rekomendasi:

- **Klaster A** (1 epic besar, 7 issue) → pecah jadi **2 commit**:
  - Commit A1: `9v9` + `g2j` (fondasi: enum + response shape) — keduanya sentuh file berbeda
    dan keduanya P1.
  - Commit A2: `0fe` + `7rk` + `u68` (tx refactor) — bertumpu pada fondasi A1.
  - Commit A3: `qgp` + `x8o` (dedup + cleanup).
- **Klaster B** (2 issue config) → 1 commit kohesif atau 2 commit kecil.
- **Klaster C** (2 issue refactor) → 2 commit terpisah, ideal di branch berbeda.

## Cross-Reference Memory

- `commit-granularity-cross-cutting-rewrite.md` — granularitas commit untuk multi-issue.
- `verify-before-claiming-done.md` — wajib verify (compile + test) sebelum close.
- `clean-compile-required-after-refactor-commits.md` — `clean compileJava`, bukan cuma `compileJava`.
- `beads-issues-jsonl-auto-stage-pattern.md` — `bd close` auto-stage, restore manual.
- `bd-claim-is-not-reversible.md` — `bd update --claim` tidak bisa di-release.
- `pegawai-service-impl-wildcard-hub.md` — `PegawaiServiceImpl` lintas-domain, perhatikan import.
- `read-jpa-style-methods-on-renamed-jooq-repos.md` — saat `git mv` repo, method body tidak berubah.
- `wildcard-import-grep-blindness.md` — cegah `import x.*;` agar FQN-grep tidak buta.
- `transactional-import-matches-subpackage.md` — `@Transactional` jakarta vs spring: match existing file.
- `aftercommit-and-whencomplete-for-fire-and-forget-kafka.md` — pola `afterCommit` + `whenComplete`.
- `simplify` skill — jalankan di akhir Klaster A untuk review dedup + reuse.

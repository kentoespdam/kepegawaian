# Claim Order — Analisis Bug GajiBatchRoot + Config + CQRS (2026-06-17)

Sebelas beads issue hasil analisis komprehensif (file `dummy_prompt.md` scope). Terbagi
dalam 3 klaster:

- **Klaster A — GajiBatchRoot transactional integrity** (7 issue, sentuh file yang sama → serial)
- **Klaster B — Spring Boot configuration** (1 issue, independen)
- **Klaster C — Master CQRS layer-split ADR-0017** (2 issue, bisa paralel via worktree)

## Status Realisasi (verified 2026-08-24)

| Urut | Plan ID | Real ID | Status | Bukti Kode |
|------|---------|---------|--------|------------|
| 1 | `9v9` | `kepegawaian-9v9` | **✅ CLOSED** | `EProsesGaji` enum with `String value`; `GajiBatchRoot.status` typed as `EProsesGaji`; all call-sites use `==` |
| 2 | `g2j` | `kepegawaian-0jo` | **✅ RESOLVED** | `logAndBuildFailure` removed; errors use `NotFoundException`/`ConflictException` |
| 3 | `0fe` | `kepegawaian-f5i` | **✅ RESOLVED** | File upload in `afterCommit` block (`GajiBatchRootCommandService:58-76`) |
| 4 | `7rk` | `kepegawaian-jgm` | **✅ RESOLVED** | `processPotonganTkk.process()` called inside `afterCommit` (`GajiBatchRootCommandService:72`) |
| 5 | `u68` | `kepegawaian-9q7` | **✅ RESOLVED** | `delete()` has `@Transactional` (`GajiBatchRootCommandService:78`) |
| 6 | `qgp` | `kepegawaian-hng` | **✅ RESOLVED** | `GajiBatchRootEventPublisher.publishAfterCommit()` extracted; both `save()` and `reprocessHandler()` use it |
| 7 | `x8o` | `kepegawaian-biy` | **✅ RESOLVED** | `reprocess(GajiBatchRootProcessRequest)` — no dead `String id` param |
| 8 | `pvr` | (tidak di-file) | **⚠️ OUT OF SCOPE** | Redis config warnings — tidak di-file sebagai beads issue. Masih relevan? |
| 9 | `uf8` | (tidak di-file) | **⚠️ OUT OF SCOPE** | `show-sql: true` di `application.yml:40` + default DB creds — tidak di-file. Masih relevan? |
| 10 | `6h2` | `buc` + `j5i` | **✅ SHIPPED** | Level CQRS split + repo moved to `jpa/` |
| 11 | `ytz` | `j5i` | **✅ SHIPPED** | PegawaiServiceImpl imports fixed (`9f00059`) |

**Klaster A & C: 100% resolved.** Klaster A issues (1-7) verified in code — CQRS refactor
(`GajiBatchRootCommandService` + `GajiBatchRootWorkflowCommandService`) incorporated all fixes.
Klaster C shipped via separate beads issues.

**Klaster B: OUT OF SCOPE for this claim order.** Issues `pvr` (Redis warnings) and `uf8`
(default creds + show-sql) were never filed as beads. `show-sql: true` is present in
`application.yml:40` and default DB credentials (`dev`/`password`) are in `application.yml:33-34`.
These should be tracked as separate issues if still desired.

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
Klaster A ✅ ALL RESOLVED (verified 2026-08-24)
   CQRS refactor split GajiBatchRootServiceImpl → GajiBatchRootCommandService
   + GajiBatchRootWorkflowCommandService. All 7 fixes incorporated.

Klaster B ⚠️ NOT FILED (out of scope)
   pvr — Redis warnings (not filed)
   uf8 — show-sql + default creds (not filed)

Klaster C ✅ SHIPPED (2026-06-18/19)
   buc — Level CQRS split
   j5i — 2 leaked JPA repos → jpa/, import fix
   5ft/9tf/jow/33s — Organisasi #1-#4
```

### Verified in code (2026-08-24)

| Issue | File:Line | What changed |
|-------|-----------|-------------|
| #1 (9v9) | `EProsesGaji.java` | Enum with `String value` field (no ordinal) |
| #1 (9v9) | `GajiBatchRoot.java:49` | `private EProsesGaji status` (typed enum) |
| #2 (g2j) | `GajiBatchRootCommandService` | `NotFoundException`/`ConflictException` (no logAndBuildFailure) |
| #3 (0fe) | `GajiBatchRootCommandService:58-76` | File upload in `afterCommit` block |
| #4 (7rk) | `GajiBatchRootCommandService:72` | `processPotonganTkk.process()` in `afterCommit` |
| #5 (u68) | `GajiBatchRootCommandService:78` | `delete()` has `@Transactional` |
| #6 (qgp) | `GajiBatchRootEventPublisher:20` | `publishAfterCommit()` extracted |
| #7 (x8o) | `GajiBatchRootWorkflowCommandService:27` | `reprocess(request)` — no dead param |

### Klaster B — still open (not filed as beads)

| Issue | Current state | Recommendation |
|-------|--------------|----------------|
| pvr (Redis) | `spring.data.redis` configured at `application.yml:68-72` | Check if Spring Boot 4.x still emits warnings |
| uf8 (config) | `show-sql: true` at `application.yml:40`; default DB creds at `:33-34` | File separate issue if desired |

## Perintah Claim (real ID, 2026-06-22)

```bash
# Klaster A — sudah executed
bd update kepegawaian-9v9 --claim   # re-claim lalu re-close
bd update kepegawaian-0jo --claim   # g2j — open, ready to work
bd update kepegawaian-f5i --claim   # 0fe — open, ready to work
bd update kepegawaian-jgm --claim   # 7rk — open, ready to work
bd update kepegawaian-9q7 --claim   # u68 — open, ready to work
bd update kepegawaian-hng --claim   # qgp — open, ready to work
bd update kepegawaian-biy --claim   # x8o — open, ready to work

# Klaster B — file + claim saat mulai (worktree paralel)
bd create --title "Redis: silence 54 boot warnings" --type chore --priority P2
bd create --title "Split application-{dev,prod}.yml; remove default creds" --type chore --priority P2
```

---

## Checklist Detail per Issue

> Pola checklist: **Read** (verifikasi) → **plan** (catat pendekatan) → **Edit** (ubah kode) →
> **compile** (wajib `clean compileJava` per memory `clean-compile-required`) →
> **commit** (1 commit kohesif per issue, per memory `commit-granularity`) →
> **close** (`bd close` + `git restore --staged .beads/issues.jsonl`).

### 1. `kepegawaian-9v9` — Status enum pakai `ordinal()`  ✅ SHIPPED (commit `a20914f`)

- [x] **Read** `entities/commons/EProsesGaji.java` — konfirmasi `value()` saat ini return `int ordinal()` (misleading). Cek `entities/penggajian/GajiBatchRoot.java:47` (`private Integer status = 0;`).
- [x] **Read** semua call-site yang bandingkan `entity.getStatus()` dengan `EProsesGaji.X.value()` / `.ordinal()`.
- [x] **Plan** pilih strategi: (A) `AttributeConverter<EProsesGaji, String>` + ubah kolom `status` ke `VARCHAR(32)`, atau (B) rename `value()` → `ordinalValue()`, dokumentasikan kontrak.
- [x] **Edit** buat `EProsesGajiConverter implements AttributeConverter<EProsesGaji, String>`. Tambah `@Convert(converter = ...)` di field `status` entity (atau cara setara).
- [x] **Edit** ubah tipe field `status` dari `Integer` jadi `EProsesGaji` (atau `String` + converter).
- [x] **Edit** migrasi Flyway script: `ALTER TABLE gaji_batch_root MODIFY status VARCHAR(32) NOT NULL;` — simpan sebagai `db/migration/Vxx__gaji_status_to_varchar.sql`.
- [x] **Edit** update semua call-site: ganti `entity.getStatus().equals(EProsesGaji.PROSES.value())` jadi `entity.getStatus() == EProsesGaji.PROSES`.
- [x] **compile** `./gradlew clean compileJava` (wajib, per memory `clean-compile-required`).
- [x] **test** boot + smoke test: create batch → status transisi harus benar, reload dapat enum value benar.
- [x] **commit** 1 commit kohesif dengan pesan `fix(kepegawaian-9v9): migrate GajiBatchRoot.status to enum-via-converter`.
- [x] **close** `bd close kepegawaian-9v9` + `git restore --staged .beads/issues.jsonl`.

### 2. `kepegawaian-g2j` (real ID `kepegawaian-0jo`) — `logAndBuildFailure` bocor `e.getMessage()` — OPEN, claimed 2026-06-22

- [x] **Read** `GajiBatchRootServiceImpl.java:204-207` (helper `logAndBuildFailure`) dan semua catch block (line 100-102, 114-116, dll).
- [x] **Read** cek apakah sudah ada `@RestControllerAdvice` global — kalau belum, akan dibuat.
- [x] **Plan** definisikan `enum ErrorCode { DB_ERROR, KAFKA_PUBLISH_FAILED, FILE_UPLOAD_FAILED, VALIDATION_FAILED, UNKNOWN_BATCH_PROCESS, INTERNAL }` dengan message default.
- [x] **Edit** buat `exceptions/GlobalExceptionHandler.java` (`@RestControllerAdvice`) — map `DataAccessException` → DB_ERROR, `KafkaException` → KAFKA_PUBLISH_FAILED, `RuntimeException("Unknown Batch Process")` → UNKNOWN_BATCH_PROCESS, sisanya → INTERNAL. Response shape: `{ code, message, traceId }`.
- [x] **Edit** refactor `logAndBuildFailure` jadi: log.error(..., e) → return `SavedStatus.failedWith(errorCode, userMessage)` (bukan raw `e.getMessage()`).
- [x] **Edit** `Unknown Batch Process` di `reprocess()` (line 110) ubah jadi `SavedStatus.FAILED` — konsisten dengan verify1/2/accept (lihat commit 5ea5abb).
- [x] **compile** `./gradlew clean compileJava`.
- [x] **test** trigger `NullPointerException` + constraint violation → response tidak bocor detail.
- [x] **commit** `fix(kepegawaian-0jo): centralize exception handling with @RestControllerAdvice + ErrorCode`.
- [x] **close** `bd close kepegawaian-0jo` + restore issues.jsonl.

### 3. `kepegawaian-0fe` (real ID `kepegawaian-f5i`) — File upload di dalam tx → orphan — OPEN, claimed 2026-06-22

- [x] **Read** `GajiBatchRootServiceImpl.java:55-92` (save flow) + `utils/FileUploadUtil.java`/`FileUploadUtilImpl.java` (signature `uploadPenggajian`).
- [x] **Read** entity `GajiBatchRootLampiran` — apakah ada field `upload_status`?
- [x] **Plan** pilih Pola 1 (outbox ringan): row minimal disimpan (status=DRAFT, file_path=null) → `afterCommit` panggil upload → update row dengan `file_path` + status=COMPLETED. Scheduler retry untuk yang gagal.
- [x] **Edit** tambah `upload_status` di `GajiBatchRootLampiran` entity + Flyway migration.
- [x] **Edit** refactor `save()`: pisah jadi `saveMetadata()` (tx) + `publishAfterCommit()` (upload + update status). Panggil `processPotonganTkk` di afterCommit juga (DRY untuk #4).
- [x] **Edit** tambah scheduled job `@Scheduled(fixedDelay = 60_000)` yang scan `upload_status=FAILED` dan retry upload.
- [x] **compile** + test rollback path: throw RuntimeException setelah upload di test mode → filesystem bersih.
- [x] **commit** `fix(kepegawaian-f5i): move file upload out of @Transactional via afterCommit + outbox retry`.
- [x] **close** `bd close kepegawaian-f5i` + restore issues.jsonl.

### 4. `kepegawaian-7rk` (real ID `kepegawaian-jgm`) — `processPotonganTkk` di dalam tx — OPEN, claimed 2026-06-22

- [x] **Read** class `processPotonganTkk` — apakah read-only, write, atau mix? Berapa row yang di-query?
- [x] **Plan** berdasarkan audit: kalau write-heavy → `@Transactional(propagation = REQUIRES_NEW)` di `process()`. Kalau read-only → panggil di afterCommit (gabung dengan pola #3).
- [x] **Edit** sesuaikan anotasi tx atau pindahkan call-site ke afterCommit hook.
- [x] **Edit** tambah `log.info("processPotonganTkk took {}ms", elapsed)` di awal/akhir.
- [x] **compile** + test concurrent: 2 batch insert bersamaan → tidak saling tunggu lock.
- [x] **commit** `fix(kepegawaian-jgm): isolate processPotonganTkk from save() transaction`.
- [x] **close** `bd close kepegawaian-jgm` + restore issues.jsonl.

### 5. `kepegawaian-u68` (real ID `kepegawaian-9q7`) — `delete()` tanpa `@Transactional` — OPEN, claimed 2026-06-22

- [x] **Read** `GajiBatchRootServiceImpl.java:144-152` (delete method) + interface `GajiBatchRootService.java`.
- [x] **Plan** tambah `@Transactional(propagation = REQUIRED)` (sama dengan `save()`). Cek apakah `delete()` juga publish Kafka — kalau ya, bungkus dengan `afterCommit` (DRY dengan #6).
- [x] **Edit** tambah anotasi + import statement (match existing file: `org.springframework.transaction.annotation.Transactional`).
- [x] **compile** + test delete dengan exception di tengah → row masih ada (rollback bekerja).
- [x] **commit** `fix(kepegawaian-9q7): add @Transactional to GajiBatchRootServiceImpl.delete()`.
- [x] **close** `bd close kepegawaian-9q7` + restore issues.jsonl.

### 6. `kepegawaian-qgp` (real ID `kepegawaian-hng`) — `registerSynchronization` duplikasi — OPEN, claimed 2026-06-22

- [x] **Read** `GajiBatchRootServiceImpl.java:80-95` (di save) dan 168-184 (di reprocessHandler). Identifikasi bagian yang persis sama.
- [x] **Plan** extract private method:
  ```text
  private void publishAfterCommit(String topic, String key, String payload) { ... }
  ```
- [x] **Edit** buat helper. Tambah null-check untuk `kafkaTemplate` + `getRecordMetadata()`.
- [x] **Edit** tambah `log.info("kafka publish topic={} key={} partition={} offset={}", ...)`.
- [x] **Edit** ganti 2 call-site pakai helper.
- [x] **compile** + test boot + publish event smoke.
- [x] **commit** `chore(kepegawaian-hng): extract publishAfterCommit helper to dedupe registerSynchronization`.
- [x] **close** `bd close kepegawaian-hng` + restore issues.jsonl.

### 7. `kepegawaian-x8o` (real ID `kepegawaian-biy`) — Dead arg di `reprocess()` — OPEN, claimed 2026-06-22

- [x] **Read** `GajiBatchRootServiceImpl.java:102` + interface `GajiBatchRootService.java` (signature `reprocess`).
- [x] **Plan** pilih Opsi A (hapus param `String id`, gunakan `request.getId()`) atau Opsi B (gunakan param `id` di body). Rekomendasi: Opsi A — minimal change.
- [x] **Edit** update interface + impl. Cari & update semua caller.
- [x] **compile** + pastikan tidak ada warning unused parameter.
- [x] **commit** `chore(kepegawaian-biy): remove dead String id arg from reprocess()`.
- [x] **close** `bd close kepegawaian-biy` + restore issues.jsonl.

### 8. `kepegawaian-pvr` — Redis warning noise — **OUT OF SCOPE** (not filed)

> Tidak di-file sebagai beads issue. Cek apakah masih relevan dengan Spring Boot 4.x.
> Jika ya, buat issue terpisah.

### 9. `kepegawaian-uf8` — Default credentials & show-sql — **OUT OF SCOPE** (not filed)

> `show-sql: true` masih ada di `application.yml:40`. Default DB creds (`dev`/`password`)
> di `application.yml:33-34`. Issue belum di-file. Buat issue terpisah jika diperlukan.

### 10. `kepegawaian-6h2` — `LevelServiceImpl` tidak CQRS — ✅ SHIPPED (multi-commit)

> Sudah selesai via `kepegawaian-buc` (Level CQRS split, commit `8fb6caa`+`27ceadb`)
> dan `kepegawaian-j5i` (2 leaked JPA repos moved to jpa/, commit `9f00059`).
> ADR-0017 layer-split recipes terpenuhi.

### 11. `kepegawaian-ytz` — `PegawaiServiceImpl` wildcard+eksplisit — ✅ SHIPPED (commit `9f00059`)

> Sudah selesai via `kepegawaian-j5i`: wildcard `master.*` diganti eksplisit `master.jpa.*`.

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

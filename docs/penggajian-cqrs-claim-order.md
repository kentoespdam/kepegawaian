# Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists

> Manager-authored work order. Claim issues **in this order**. Do NOT skip ahead —
> Wave 2 batch is BLOCKED until `kepegawaian-awf.1` (DasarGaji) proves the master
> pattern; Wave 3 `awf.12` is BLOCKED until both Wave 2 batch issues close.
> Full plan lives in the epic: `bd show kepegawaian-awf` (DESCRIPTION section).
> Pola konkret per layer: [`profil-cqrs-implementation-patterns.md`](profil-cqrs-implementation-patterns.md).
> Aturan alur kerja + Git-mv invariant: [`../CODING_RULES.md`](../CODING_RULES.md).

## Prinsip modul (baca sekali di awal)

- **read = JOOQ** (QueryService → QueryRepository), **write = JPA** (CommandService + write mapper). Dua service diinject ke **SATU** controller — tanpa `*CommandController`/`*QueryController`.
- Mapper = `final` + private ctor, **BUKAN `@Component`**. Read mapper Pola A (`mapToResponse` static) atau Pola B (`implements RecordMapper` + `INSTANCE`).
- File ≤ 120 baris (kecuali entity data-holder & pure-query repo). Rename `*ServiceImpl` → `*CommandService` via **`gitnexus_rename`** — JANGAN find/replace.
- **`repositories/penggajian/` sekarang FLAT** → tiap issue memindah JPA repo ke `jpa/` dan menambah QueryRepository di `jooq/` (mirror `repositories/cuti/`).
- **Soft-delete `is_deleted`** — lihat aturan filter per-grup di bawah. Never hard-delete.

### Aturan `IS_DELETED` per aggregate (WAJIB benar)

| Punya kolom `is_deleted` → baca `WAJIB IS_DELETED.eq(false)`                                                                                                    | TANPA kolom (hard-delete → baca tanpa filter)                                                               |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| DasarGaji, DetailDasarGaji, GajiKomponen, GajiTunjangan, GajiPhdp, GajiProfil, GajiPotonganTkk, GajiParameterSetting, GajiPendapatanNonPajak, **GajiBatchRoot** | GajiBatchMaster, GajiBatchMasterProses, GajiBatchPotonganTkk, GajiBatchRootLampiran, GajiBatchRootErrorLogs |

> Grup A/B dibedakan lewat basis SCHEMA (kehadiran tabel Envers `_Aud`), bukan tebakan. Grup B pada dasarnya **tetap CRUD** — pengecualian nyata hanya Kafka (GajiBatchRoot), RestClient PATCH eksternal (GajiBatchMaster), download/upload xlsx, math `recalculateAdditional`, dan verb state-machine — semua sebagai side-effect di dalam method tulis, dipertahankan verbatim.

---

## Claim order

| Order | Issue ID           | Aggregate                              | Wave | State when you start                  | Claim cmd                              |
|-------|--------------------|----------------------------------------|------|---------------------------------------|----------------------------------------|
| 1     | kepegawaian-awf.1  | DasarGaji (pilot pattern)              | W1   | CLOSED                                | `bd update kepegawaian-awf.1 --claim`  |
| 2     | kepegawaian-awf.2  | DetailDasarGaji                        | W1   | CLOSED                                | `bd update kepegawaian-awf.2 --claim`  |
| 3     | kepegawaian-awf.3  | GajiKomponen                           | W1   | CLOSED                                | `bd update kepegawaian-awf.3 --claim`  |
| 4     | kepegawaian-awf.4  | GajiTunjangan                          | W1   | CLOSED                                | `bd update kepegawaian-awf.4 --claim`  |
| 5     | kepegawaian-awf.5  | GajiPhdp                               | W1   | CLOSED                                | `bd update kepegawaian-awf.5 --claim`  |
| 6     | kepegawaian-awf.6  | GajiProfil                             | W1   | CLOSED                                | `bd update kepegawaian-awf.6 --claim`  |
| 7     | kepegawaian-awf.7  | GajiPotonganTkk                        | W1   | CLOSED                                | `bd update kepegawaian-awf.7 --claim`  |
| 8     | kepegawaian-awf.8  | GajiParameterSetting                   | W1   | CLOSED                                | `bd update kepegawaian-awf.8 --claim`  |
| 9     | kepegawaian-awf.9  | GajiPendapatanNonPajak                 | W1   | CLOSED                                | `bd update kepegawaian-awf.9 --claim`  |
| 10    | kepegawaian-awf.10 | GajiBatchMaster                        | W2   | CLOSED                                | `bd update kepegawaian-awf.10 --claim` |
| 11    | kepegawaian-awf.11 | GajiBatchMasterProses                  | W2   | CLOSED                                | `bd update kepegawaian-awf.11 --claim` |
| 12    | kepegawaian-awf.12 | GajiBatchRoot                          | W3   | READY                                 | `bd update kepegawaian-awf.12 --claim` |
| —     | kepegawaian-awf    | Epic (umbrella, do not claim directly) | —    | OPEN, auto-closes                     | —                                      |

> Wave 1 issues #2–#9 tak diblokir secara teknis (paralel-able), tapi **selesaikan #1 dulu** sebagai pilot: ia menetapkan bentuk file yang di-mirror 8 issue lainnya. Setelah #1 hijau, #2–#9 boleh dikerjakan berurutan/paralel.

---

## STEP 0 — Before any code (setiap claim)

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] Baca ulang exemplar `cuti/` (CutiJenis*) + `mapper/profil/` yang di-mirror
- [x] Baca `profil-cqrs-implementation-patterns.md` §1–§5
- [x] `bd show <id>` — baca checklist §5 di issue
- [x] `bd prime` (recover beads workflow context)
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] Baca ulang exemplar `cuti/` (CutiJenis*) + `mapper/profil/` yang di-mirror
- [x] Baca `profil-cqrs-implementation-patterns.md` §1–§5
- [x] `bd show <id>` — baca checklist §5 di issue
- [x] `bd update <id> --claim` issue yang mulai dikerjakan

---

## WAVE 1 — 9 master aggregate (grup A, CRUD murni)

**Pola identik untuk .1–.9.** Tiap issue = satu aggregate. Mirror `CutiJenis*` exemplar; ganti nama aggregate + tabel JOOQ + kolom filter. Checklist per-issue (dari §5 p**Pre-edit**
- [x] `gitnexus_impact({target: "<Agg>ServiceImpl", direction: "upstream"})` — laporkan blast radius
- [x] `gitnexus_impact({target: "<Agg>Repository", direction: "upstream"})` — laporkan blast radius
- [x] WARN manager bila HIGH/CRITICAL sebelum lanjut

**DTO**
- [x] `<Agg>PostRequest` / `<Agg>PutRequest` — `@Data`, validasi, `@JsonIgnore` di `getSpecification()`
- [x] `<Agg>IndexQuery extends PagedRequest` — request baca; **WAJIB `extends PagedRequest`** (base baru: `@Max(100)` clamp, `getPageNumber()`/`getSizeOrDefault()`, sort-whitelist type-safe), `@EqualsAndHashCode(callSuper = true) @Data`; tambah field filter domain saja (tanpa Specification). **JANGAN pakai `CommonPageRequest`** (base lama, tanpa clamp/whitelist). Exemplar: `GradeIndexQuery`.
- [x] `<Agg>Response`/`<Agg>Query` — POJO datar (nested → `*MiniResponse`)

**Mapper** (di `mapper/penggajian/<agg>/`, BUKAN di `repositories/`)
- [x] Write `<Agg>Mapper` — `final`, private ctor, static `toEntity`/`updateEntity`
- [x] Read `<Agg>JooqMapper` — Pola A (`mapToResponse` static) ATAU Pola B (`RecordMapper` + `INSTANCE`); boolean MariaDB = `Byte` → `!= null && == 1`

**Repository** (split teknologi)
- [x] `git mv` JPA repo → `repositories/penggajian/jpa/<Agg>Repository` (+ package decl); mirror Git-mv invariant CODING_RULES §17
- [x] NEW `repositories/penggajian/jooq/<Agg>QueryRepository` — `@Repository @RequiredArgsConstructor`, inject `DSLContext`; `pageQuery(<Agg>IndexQuery)`/`listQuery`/`getById`; `SortParam.resolve(query.getSortBy(), query.getSortDirection(), allowedSorts(), <TBL>.ID)`; paging via `query.getSizeOrDefault()` + `query.getPageNumber()`; `baseWhere` shared; **WAJIB `IS_DELETED.eq(false)`**; kondisi opsional → `DSL.noCondition()`. Exemplar: `GradeQueryRepository`.

**Service**
- [x] `<Agg>QueryService` tipis — delegasi `findPage`/`findList`/`findById` (+ file-download bila ada, milik QueryService)
- [x] `<Agg>CommandService` `@Transactional` — `exists(spec)`→DUPLICATE, `getReferenceById` FK murni, write mapper; **rename `<Agg>ServiceImpl` → `<Agg>CommandService` via `gitnexus_rename`**

**Controller & gates**
- [x] Controller inject KEDUA service; pertahankan `@PreAuthorize`/`@Valid`/`Errors`; tanpa `*CommandController`
- [x] Semua file ≤ 120 baris (DetailDasarGaji 131 & GajiKomponen 126 otomatis mengecil setelah baca pindah to QueryService)
- [x] **Cleanup — dead code:** hapus field/method/DTO lama yang tak lagi ter-referensi setelah split (mis. `getSpecification()` di request baca, mapper manual yang tergantikan JOOQ). Verifikasi zero-ref via `gitnexus_impact({direction: "upstream"})` SEBELUM hapus.
- [x] **Cleanup — unused import:** buang import yang menggantung setelah pindah/hapus kode (`gitnexus_rename` & split kerap menyisakan import mati). Pastikan `./gradlew clean compileJava` bersih tanpa warning import.
- [x] `gitnexus_detect_changes()` scope sesuai; `./gradlew clean compileJava` SUCCESS
- [x] `bd close <id>` → ship (lihat "Ship tiap issue")e sesuai; `./gradlew clean compileJava` SUCCESS
- [ ] `bd close <id>` → ship (lihat "Ship tiap issue")

**Tabel JOOQ per aggregate:** `.1` DASAR_GAJI · `.2` DETAIL_DASAR_GAJI · `.3` GAJI_KOMPONEN · `.4` GAJI_TUNJANGAN · `.5` GAJI_PHDP · `.6` GAJI_PROFIL · `.7` GAJI_POTONGAN_TKK · `.8` GAJI_PARAMETER_SETTING · `.9` GAJI_PENDAPATAN_NON_PAJAK.

---

## WAVE 2 — batch (grup B) · BLOCKED sampai `awf.1` close

### ISSUE 10 — `kepegawaian-awf.10` GajiBatchMaster

**Goal:** CQRS split; read=JOOQ; pertahankan RestClient PATCH upload eksternal + download/upload xlsx. **TANPA filter `IS_DELETED`** (tabel hard-delete).

- [ ] `gitnexus_impact` `GajiBatchMasterServiceImpl` (upstream) + WARN bila HIGH/CRITICAL
- [ ] DTO tulis (`*PostRequest`/`*PutRequest`) + **`GajiBatchMasterIndexQuery extends PagedRequest`** (baca) + write mapper + `GajiBatchMasterJooqMapper` (read)
- [ ] JPA repo → `jpa/`; NEW `jooq/GajiBatchMasterQueryRepository` (tabel GAJI_BATCH_MASTER; **tanpa** `IS_DELETED`; `findByPegawaiId` filter `EProsesGaji.FINISHED`)
- [ ] `GajiBatchMasterQueryService` — `findAll`/`findById`/`findByPegawaiId` (paged) + **download** `downloadTableGaji`/`downloadPotonganGaji` (xlsx `ByteArrayResource`, milik QueryService)
- [ ] `GajiBatchMasterCommandService` `@Transactional` — `uploadPotonganTambahan` (FileUploadUtil + save `GajiBatchRootLampiran` + RestClient PATCH ke `${penggajian.endpoint}/upload/{id}/additional_gaji`); **konversi `@Autowired` field → constructor injection** (`@RequiredArgsConstructor`)
- [ ] Controller inject KEDUA service; endpoint download panggil QueryService
- [ ] ≤120 baris/file; `detect_changes`; `clean compileJava` SUCCESS

### ISSUE 11 — `kepegawaian-awf.11` GajiBatchMasterProses

**Goal:** CQRS split; read=JOOQ; pertahankan `recalculateAdditional` (math payroll) + `rollback`. **TANPA filter `IS_DELETED`**. **Cleanup dead field `ENDPOINT`** (tak dipakai RestClient apa pun).

- [ ] `gitnexus_impact` `GajiBatchMasterProsesServiceImpl` (upstream) + WARN bila HIGH/CRITICAL
- [ ] DTO tulis + **`GajiBatchMasterProsesIndexQuery extends PagedRequest`** (baca) + write mapper + `GajiBatchMasterProsesJooqMapper` (read)
- [ ] JPA repo → `jpa/`; NEW `jooq/GajiBatchMasterProsesQueryRepository` (tabel GAJI_BATCH_MASTER_PROSES; **tanpa** `IS_DELETED`); baca `findPage`/`findById`/`findByMasterId` + `getSumByJenisGaji`/`getSumAdditionalByJenisGaji` (kode `startsWith "ADD_"`)/`filterGajiBatchMasterProses`
- [ ] `GajiBatchMasterProsesQueryService` — delegasi sum/filter queries
- [ ] `GajiBatchMasterProsesCommandService` `@Transactional` — `save` (+`recalculateAdditional`: `penghasilanBersih2`/`pembulatan2`/`penghasilanBersihFinal2` via `Math.round`/`Math.ceil`), `rollback(rootBatchId)` (hapus proses `ADD_%` + nolkan total), `delete` (+recalculate)
- [ ] **HAPUS `@Value("${penggajian.endpoint}") private String ENDPOINT;`** (dead field) — verifikasi zero-ref dulu
- [ ] Controller inject KEDUA service; ≤120 baris/file; `detect_changes`; `clean compileJava` SUCCESS

---

## WAVE 3 — GajiBatchRoot (grup B, paling kompleks) · BLOCKED sampai #10 & #11 close

### ISSUE 12 — `kepegawaian-awf.12` GajiBatchRoot (4-file split)

**Goal:** state machine + Kafka + upload. **PUNYA `is_deleted` → baca WAJIB `IS_DELETED.eq(false)`.** Membangun di atas 4 issue Kafka yang SUDAH close. Split menjadi 4 file agar tiap ≤120 baris:

- [ ] `gitnexus_impact` `GajiBatchRootServiceImpl` (upstream) + WARN bila HIGH/CRITICAL (kemungkinan tinggi — state-machine hub)
- [ ] DTO tulis + **`GajiBatchRootIndexQuery extends PagedRequest`** (baca) + write mapper + `GajiBatchRootJooqMapper` (read)
- [ ] JPA repo → `jpa/`; NEW `jooq/GajiBatchRootQueryRepository` (tabel GAJI_BATCH_ROOT; **WAJIB `IS_DELETED.eq(false)`**)
- [ ] **`GajiBatchRootQueryService`** — `findAll`/`findById` (delegasi JOOQ)
- [ ] **`GajiBatchRootCommandService`** `@Transactional` — `save` (upload PotonganTKK + `ProcessPotonganTkk` + compensating action), `delete` (soft-delete)
- [ ] **`GajiBatchRootWorkflowCommandService`** `@Transactional` — verb state-machine `reprocess`/`verify1`/`verify2`/`accept` (EProsesGaji: PENDING→PROSES→WAIT_VERIFICATION_PHASE_1→WAIT_VERIFICATION_PHASE_2→WAIT_APPROVAL→FINISHED) + `reprocessHandler`/`logAndBuildFailure`
- [ ] **`GajiBatchRootEventPublisher`** — Kafka `publishAfterCommit` via `TransactionSynchronizationManager.registerSynchronization(...)` (fire-and-forget after DB commit); `@Value PENGGAJIAN_TOPIC` + `KafkaTemplate` diisolasi ke sini
- [ ] Controller inject Query + Command + Workflow service; publisher diinject ke Command/Workflow (bukan controller)
- [ ] ≤120 baris/file; `detect_changes`; `clean compileJava` SUCCESS
- [ ] **ADR** — isolasi Kafka ke `GajiBatchRootEventPublisher` sudah didokumentasikan di [ADR-0024](adr/0024-gajibatchroot-kafka-diisolasi-ke-eventpublisher.md) (publish after-commit, fire-and-forget). Implementasi WAJIB sesuai keputusan itu; bila menyimpang → update ADR sebelum ship.

---

## Ship tiap issue (CODING_RULES §Ship)

- [ ] **Cleanup dead code + unused import** — verifikasi zero-ref (`gitnexus_impact` upstream) sebelum hapus; `clean compileJava` bersih tanpa import menggantung
- [ ] `gitnexus_detect_changes()` — scope hanya aggregate terkait
- [ ] `git add` batch tunggal di akhir; `git diff --cached` menampilkan konten (moved files bukan 0 baris)
- [ ] commit `refactor(penggajian): split <Agg> into CQRS command/query (read=JOOQ)`
- [ ] `bd close <id>`
- [ ] `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin"
- [ ] Post-commit sanity `./gradlew clean compileJava`; bila gagal → `fix()` commit (JANGAN amend)

---

## Guardrails (semua issue)

- NEVER edit simbol tanpa `gitnexus_impact` dulu; NEVER abaikan HIGH/CRITICAL
- NEVER rename/move dengan find-and-replace — pakai `gitnexus_rename` / `git mv`
- NEVER commit tanpa `gitnexus_detect_changes()`; NEVER amend (buat `fix()` commit)
- beads = SATU-SATUNYA tracker — tanpa TodoWrite / markdown TODO
- Soft-delete `is_deleted` saja untuk grup A + GajiBatchRoot; grup B lain hard-delete (baca tanpa filter)
- `../kepegawaian-legacy` read-only = referensi spec; kode baru di `rewrite/master-cqrs`

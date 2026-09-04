# Claim Order: Engine Proses Gaji Background

> **Issue**: `kepegawaian-8seb` — penggajian: implementasi engine proses gaji background
> **Dibuat**: 2026-09-04
> **Referensi legacy**: `docs/php/payroll_bms.php` + `docs/php/payrollmodel.php`
> **Context domain**: `docs/context/language-penggajian.md`

---

## Keputusan Desain (Locked)

| # | Keputusan | Detail |
|---|-----------|--------|
| 1 | Background | `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` virtual threads — **ganti Kafka** |
| 2 | On failure | Status → `FAILED` otomatis |
| 3 | Error log | `GajiBatchRootErrorLogs` + kolom `jenisError` enum `DATA`/`SYSTEM` |
| 4 | Output | `GajiBatchMasterProses` untuk SEMUA komponen (standar = kode biasa, ad-hoc = prefix `ADD_`) |
| 5 | Formula eval | **exp4j** — formula dinamis dari DB, kode komponen sebagai variabel |
| 6 | Scope | Dua fase: **Snapshot** (populate `GajiBatchMaster`) + **Kalkulasi** (populate `GajiBatchMasterProses`) |
| 7 | GP source | `Pegawai.gajiPokok` snapshot langsung |
| 8 | Filter pegawai | `statusKerja = KARYAWAN_AKTIF` + `statusPegawai != NON_PEGAWAI` |
| 9 | POT_TKK | `SUM(GajiBatchPotonganTkk.potongan)` by batchId + nipam |
| 10 | TUNJ_KK guard | Cek `RiwayatSp` otomatis sebagai second guard (zero jika SP3 aktif di periode) |
| 11 | PHDP | `Pegawai.phdp` langsung — logika historis deprecated |
| 12 | GajiKpi | Buat migration + entity + full CRUD |
| 13 | Concurrency | Parallel per pegawai — `StructuredTaskScope` (virtual threads) |
| 14 | Reprocess | Engine selalu reset (idempoten): hapus `GajiBatchMaster` + `GajiBatchMasterProses`, hitung ulang |

---

## #SYSTEM Resolver Map

| Kode Komponen | Sumber Data | Catatan |
|---------------|-------------|---------|
| `GP` | `GajiBatchMaster.gajiPokok` | Snapshot dari `Pegawai.gajiPokok` |
| `JML_ANAK` | `GajiBatchMaster.jmlTanggungan` | Snapshot dari `Pegawai.jmlTanggungan` |
| `JML_JIWA` | `1 + JML_ANAK + (KAWIN/MENIKAH_SEKANTOR ? 1 : 0)` | Computed dari `GajiBatchMaster.statusKawin` |
| `REF_PTKP` | `GajiPendapatanNonPajak.nominal` | Via `Pegawai.kodePajak` → snapshot `GajiBatchMaster.gajiPendapatanNonPajakId` |
| `REF_ASKES` | `Pegawai.isAskes ? 1.0 : 0.0` | Boolean → Double |
| `REF_SEWA_RUMDIN` | `Pegawai.rumahDinas.nilai` | 0.0 jika `rumahDinas` null |
| `REF_POT_TKK` | `GajiPotonganTkk` lookup | By `statusPegawai` + `levelId` atau `golonganId` |
| `REF_JML_POT_KK` | `SUM(GajiBatchPotonganTkk.potongan)` | Filter by `batchId` + `nipam`; default 0 |
| `REF_TUNJ_JABATAN` | `GajiTunjangan` (JABATAN) | By `levelId` atau `golonganId` |
| `REF_TUNJ_BERAS` | `GajiTunjangan` (BERAS) | By `golonganId` |
| `REF_TUNJ_KK` | `GajiTunjangan` (KINERJA) | By `levelId`/`golonganId`; **0 jika SP3 aktif** via `RiwayatSp` |
| `REF_TUNJ_AIR` | `GajiTunjangan` (AIR) | By `levelId` atau `golonganId` |
| `REF_PHDP` | `GajiBatchMaster.phdp` | Snapshot dari `Pegawai.phdp` |
| `TUNJ_KINERJA` | `GajiKpi.tunkin` | By `nipam` + `periode`; **default 0** jika tidak ada data |

---

## Arsitektur Engine

```
GajiBatchRootProcessEvent (ApplicationEvent)
    ↓  @TransactionalEventListener(AFTER_COMMIT) + @Async("gajiProsesExecutor")
GajiBatchRootEventListener
    ↓  calls
GajiBatchProsesCommandService.prosesGaji(rootBatchId)
    ├── 1. update status → PROSES
    ├── 2. reset(rootBatchId)
    │       → hapus GajiBatchMasterProses (cascade via batchMasterId IN ...)
    │       → hapus GajiBatchMaster (by batch_root_id)
    ├── 3. GajiBatchProsesSnapshotService.snapshot(batchRoot)
    │       → query Pegawai eligible → create GajiBatchMaster[]
    ├── 4. StructuredTaskScope.ShutdownOnFailure — parallel per pegawai:
    │       GajiBatchProsesKalkulasiService.hitung(batchMaster)
    │           → resolve #SYSTEM via GajiBatchProsesReferenceResolver
    │           → evaluate formula via GajiFormulaEvaluator (exp4j)
    │           → save GajiBatchMasterProses per komponen
    │           → update GajiBatchMaster totals
    ├── 5. update GajiBatchRoot: totalPegawai, status → WAIT_VERIFICATION_PHASE_1
    └── 6. on error → GajiBatchRootErrorLogs(jenisError=DATA/SYSTEM) + status → FAILED
```

---

## Claim Order Checklist

### Wave 0 — Fondasi

- [x] **W0-1** Tambah dep `exp4j` ke `build.gradle.kts`: `implementation("net.objecthunter:exp4j:0.4.8")`
- [x] **W0-2** Migration: `V40__add_jenis_error_to_gaji_batch_root_error_logs.sql`
  - `ALTER TABLE gaji_batch_root_error_logs ADD COLUMN jenis_error VARCHAR(10) NOT NULL DEFAULT 'SYSTEM';`
- [x] **W0-3** Enum `EJenisErrorGaji { DATA, SYSTEM }` di `entities/commons/`
- [x] **W0-4** Update entity `GajiBatchRootErrorLogs` + field `jenisError: EJenisErrorGaji`

---

### Wave 1 — GajiKpi CRUD

- [ ] **W1-1** Migration: `Vy__create_gaji_kpi.sql` — tabel `gaji_kpi` + `gaji_kpi_aud`
  - Kolom: `id, nipam, periode (VARCHAR 7, format YYYY-MM), tunkin, pph21_ter, is_deleted, audit`
  - Unique key: `(nipam, periode)`
- [ ] **W1-2** Entity: `GajiKpi` (extends `IdsAbstract`, `@Audited`, soft-delete)
- [ ] **W1-3** DTO: `GajiKpiPostRequest`, `GajiKpiPutRequest`, `GajiKpiResponse`, `GajiKpiIndexQuery`
- [ ] **W1-4** Mapper: `GajiKpiMapper` (write), `GajiKpiJooqMapper` (read)
- [ ] **W1-5** Repository JPA: `GajiKpiRepository` + `findByNipamAndPeriode(nipam, periode)`
- [ ] **W1-6** Repository JOOQ: `GajiKpiQueryRepository`
- [ ] **W1-7** Service: `GajiKpiCommandService` (save, update, delete), `GajiKpiQueryService` (page, list, findById)
- [ ] **W1-8** Controller: `GajiKpiController` → `@RequestMapping("/api/penggajian/kpi")`

---

### Wave 2 — Event Infrastructure (ganti Kafka)

- [ ] **W2-1** `GajiBatchRootProcessEvent extends ApplicationEvent` — field: `String rootBatchId`
- [ ] **W2-2** `@Bean("gajiProsesExecutor")` `Executor` menggunakan `Executors.newVirtualThreadPerTaskExecutor()`
- [ ] **W2-3** Modifikasi `GajiBatchRootEventPublisher`:
  - Ganti `KafkaTemplate` → inject `ApplicationEventPublisher`
  - `publishAfterCommit(batchId)` → `publisher.publishEvent(new GajiBatchRootProcessEvent(this, batchId))`
- [ ] **W2-4** Buat `GajiBatchRootEventListener`:
  - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`
  - `@Async("gajiProsesExecutor")`
  - Panggil `prosesCommandService.prosesGaji(event.getRootBatchId())`
- [ ] **W2-5** Modifikasi `GajiBatchRootWorkflowCommandService.reprocess()`:
  - Setelah state kembali ke `PENDING`, publish `GajiBatchRootProcessEvent`

---

### Wave 3 — Engine Utilities

- [ ] **W3-1** `GajiFormulaEvaluator` di `utils/`:
  - Wrapper exp4j dengan custom function `ceil(x)` → `Math.ceil(x)`
  - `double evaluate(String formula, Map<String, Double> vars) throws GajiFormulaException`
  - Empty formula → return 0.0
  - Parse error → throw `GajiFormulaException` (caught sebagai DATA error)

---

### Wave 4 — Reference Resolver

- [ ] **W4-1** `GajiBatchProsesReferenceResolver` di `services/penggajian/gajiBatchMasterProses/`:
  - Inject: `GajiTunjanganRepository`, `GajiPotonganTkkRepository`, `GajiBatchPotonganTkkRepository`, `GajiKpiRepository`, `RiwayatSpRepository`
  - `double resolve(String kode, GajiBatchMaster master, Map<String, Double> ctx, String batchId)`
  - SP3 check untuk `REF_TUNJ_KK`: cek `RiwayatSp` where `pegawai.id = master.pegawaiId` AND `jenisSp` = SP3 AND periode overlap (21 prev month — 20 current month)

---

### Wave 5 — Snapshot Service

- [ ] **W5-1** `GajiBatchProsesSnapshotService`:
  - Query `Pegawai` where `statusKerja = KARYAWAN_AKTIF` AND `statusPegawai != NON_PEGAWAI`
  - Eager load: `biodata` (untuk statusKawin), `jabatan`, `golongan`, `organisasi`, `gajiProfil`, `kodePajak`, `rumahDinas`
  - Buat `GajiBatchMaster` per pegawai dengan snapshot lengkap
  - `GajiBatchMasterRepository.saveAll(masters)`

---

### Wave 6 — Kalkulasi Service

- [ ] **W6-1** `GajiBatchProsesKalkulasiService`:
  - Load `GajiKomponen` by `profilGajiId` ordered by `urut`
  - Maintain `Map<String, Double> ctx` (accumulator semua nilai komponen)
  - Per komponen:
    - `isReference = true` atau `formula = "#SYSTEM"` → `referenceResolver.resolve(kode, ...)`
    - `formula` kosong → 0.0
    - Else → substitusi kode komponen dalam formula + `formulaEvaluator.evaluate(...)`
  - Hasil dibulatkan: `Math.round(nilai)`
  - Simpan `GajiBatchMasterProses`: `kode, urut, nama, jenisGaji, nilai, formula (asli), nilaiFormula (formula + nilai tersubstitusi)`
  - Update `GajiBatchMaster` totals dari ctx: `penghasilanKotor, totalPotongan, penghasilanBersih, pembulatan, penghasilanBersihFinal, pajak`

---

### Wave 7 — Orchestrator

- [ ] **W7-1** `GajiBatchProsesCommandService`:
  - `reset(rootBatchId)`: hapus `GajiBatchMasterProses` cascade → hapus `GajiBatchMaster`
  - `prosesGaji(rootBatchId)`:
    1. Update `GajiBatchRoot.status = PROSES`
    2. `reset(rootBatchId)`
    3. `snapshotService.snapshot(batchRoot)` → `List<GajiBatchMaster>`
    4. Parallel via `StructuredTaskScope.ShutdownOnFailure`:
       - Per pegawai: `kalkulasiService.hitung(master)` dalam try-catch
       - Exception per pegawai → catat `GajiBatchRootErrorLogs(jenisError=DATA/SYSTEM)`, lanjut
    5. Update `GajiBatchRoot`: `totalPegawai = masters.size()`, `status = WAIT_VERIFICATION_PHASE_1`
    6. Jika exception fatal → `status = FAILED`, log `jenisError=SYSTEM`

---

### Wave 8 — Startup Recovery

- [ ] **W8-1** `GajiBatchProsesStartupService` (`@Component`, `@ApplicationListener<ApplicationReadyEvent>`):
  - Cari `GajiBatchRoot` dengan `status = PROSES` → set `FAILED` + log "Server restart detected" (SYSTEM)
  - Cari `GajiBatchRoot` dengan `status = PENDING` → publish `GajiBatchRootProcessEvent` (re-queue)

---

## Dependencies antar Wave

```
W0 ──┬──→ W1 (GajiKpi CRUD)
     ├──→ W2 (Event Infrastructure)
     └──→ W3 (Formula Evaluator)
              ↓
W1 + W3 ───→ W4 (Reference Resolver)
              ↓
W4 ────────→ W5 (Snapshot Service)
              ↓
W3+W4+W5 ──→ W6 (Kalkulasi Service)
              ↓
W5+W6 ─────→ W7 (Orchestrator)
              ↓
W7 ────────→ W8 (Startup Recovery)
```

## File yang Perlu Dimodifikasi

| File | Perubahan |
|------|-----------|
| `build.gradle.kts` | Tambah dep `exp4j` |
| `GajiBatchRootErrorLogs.java` | Tambah field `jenisError: EJenisErrorGaji` |
| `GajiBatchRootEventPublisher.java` | Ganti KafkaTemplate → ApplicationEventPublisher |
| `GajiBatchRootWorkflowCommandService.java` | `reprocess()` publish event setelah state reset ke PENDING |

---

## Verifikasi

- [ ] `./gradlew clean compileJava` — zero error
- [ ] Unit test `GajiFormulaEvaluator` — semua formula seed dievaluasi benar (termasuk CEIL)
- [ ] Unit test `GajiBatchProsesReferenceResolver` — mock tiap resolver
- [ ] Integration test: batch root created → PENDING → engine runs → WAIT_VERIFICATION_PHASE_1
- [ ] Test reprocess: FAILED → reprocess → engine reset + recalculate → WAIT_VERIFICATION_PHASE_1
- [ ] Test concurrency: 50+ pegawai, tidak ada race condition
- [ ] Test startup recovery: simulate PROSES state on boot → auto FAILED

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
| 15 | Clamp POT | Engine-level clamp pasca-eval (pola legacy): `POT_JP` → `maksimal_potongan_jpn`, `POT_ASKES` → `maksimal_potongan_askes` dari `gaji_parameter_setting` (di-lock 2026-09-04, hasil audit legacy) |
| 16 | Periode format | Batch root & `GajiBatchMaster.periode` = **`YYYYMM`** (6 digit, `GajiBatchRootPostRequest.tahun+bulan`); `GajiKpi.periode` = **`YYYY-MM`** (VARCHAR 7, validasi `\d{4}-\d{2}`). Engine memakai format `YYYY-MM` sbg kanonik internal → resolver normalisasi `"202509"` → `"2025-09"` utk lookup KPI & window SP-3 (di-lock 2026-09-04, hasil implementasi W4-1) |

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
| `TUNJ_KINERJA` | `GajiKpi.tunkin` | By `nipam` + `periode` (**`YYYY-MM`**, keputusan #16); **default 0** jika tidak ada data |

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
    │       GajiBatchProsesKalkulasiService.hitung(batchMaster, batchId)
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

- [x] **W1-1** Migration: `V41__create_gaji_kpi.sql` — tabel `gaji_kpi` + `gaji_kpi_aud`
  - Kolom: `id, nipam, periode (VARCHAR 7, format YYYY-MM), tunkin, pph21_ter, is_deleted, audit`
  - Unique key: `(nipam, periode)`
- [x] **W1-2** Entity: `GajiKpi` (extends `IdsAbstract`, `@Audited`, soft-delete)
- [x] **W1-3** DTO: `GajiKpiPostRequest`, `GajiKpiPutRequest`, `GajiKpiResponse`, `GajiKpiIndexQuery` (+`GajiKpiListRequest` utk endpoint `/list`)
- [x] **W1-4** Mapper: `GajiKpiMapper` (write), `GajiKpiJooqMapper` (read)
- [x] **W1-5** Repository JPA: `GajiKpiRepository` + `findByNipamAndPeriode(nipam, periode)`
- [x] **W1-6** Repository JOOQ: `GajiKpiQueryRepository`
- [x] **W1-7** Service: `GajiKpiCommandService` (save, update, delete), `GajiKpiQueryService` (page, list, findById)
- [x] **W1-8** Controller: `GajiKpiController` → `@RequestMapping("/penggajian/kpi")` (prefix `/api` tidak dipakai modul lain; path mengikuti konvensi controller existing)

---

### Wave 2 — Event Infrastructure (ganti Kafka)

- [x] **W2-1** `GajiBatchRootProcessEvent extends ApplicationEvent` — field: `String rootBatchId`
- [x] **W2-2** `@Bean("gajiProsesExecutor")` `Executor` menggunakan `Executors.newVirtualThreadPerTaskExecutor()` (+`@EnableAsync` di `ThreadPoolConfig`)
- [x] **W2-3** Modifikasi `GajiBatchRootEventPublisher`:
  - Ganti `KafkaTemplate` → inject `ApplicationEventPublisher`
  - `publishAfterCommit(batchId)` → `publisher.publishEvent(new GajiBatchRootProcessEvent(this, batchId))` (AFTER_COMMIT dijamin listener)
- [x] **W2-4** Buat `GajiBatchRootEventListener`:
  - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`
  - `@Async("gajiProsesExecutor")`
  - Panggil `prosesCommandService.prosesGaji(event.getRootBatchId())`
- [x] **W2-5** Modifikasi `GajiBatchRootWorkflowCommandService.reprocess()`:
  - Setelah state kembali ke `PENDING`, publish `GajiBatchRootProcessEvent` — **sudah ada** (`reprocessHandler` → `publishAfterCommit` saat PENDING), API publisher tak berubah

> **Catatan W2 → W7:** `GajiBatchProsesCommandService.prosesGaji()` dibuat sebagai **stub** di Wave 2 (keputusan user) — set status `PROSES` + `tanggalProses` saja; snapshot (W5) + kalkulasi (W6) + reset idempoten diisi saat W7-1.

---

### Wave 3 — Engine Utilities

- [x] **W3-1** `GajiFormulaEvaluator` di `utils/`:
  - Wrapper exp4j dengan custom function `ceil(x)` → `Math.ceil(x)`
  - `double evaluate(String formula, Map<String, Double> vars) throws GajiFormulaException`
  - Empty formula → return 0.0
  - Parse error → throw `GajiFormulaException` (caught sebagai DATA error)
  - **Temuan:** exp4j 0.4.8 tidak punya fungsi bawaan + lookup nama fungsi case-sensitive → normalisasi `(?i)\bceil\b` → `ceil` (seed DB pakai `CEIL(` huruf besar, 9x di V16–V18)

---

### Wave 4 — Reference Resolver

- [x] **W4-1** `GajiBatchProsesReferenceResolver` di `services/penggajian/gajiBatchMasterProses/`:
  - Inject: `GajiTunjanganRepository`, `GajiPotonganTkkRepository`, `GajiBatchPotonganTkkRepository`, `GajiKpiRepository`, `RiwayatSpRepository` (+`PegawaiRepository`)
  - `double resolve(String kode, GajiBatchMaster master, Map<String, Double> ctx, String batchId)`
  - SP3 check untuk `REF_TUNJ_KK`: cek `RiwayatSp` where `pegawai.id = master.pegawaiId` AND `jenisSp` = SP3 AND periode overlap (21 prev month — 20 current month)
  - **Keputusan (2026-09-04):** `REF_ASKES` & `REF_SEWA_RUMDIN` di-resolve **live dari `Pegawai`** (`PegawaiRepository.findIsAskesById` / `findRumahDinasNilaiById` — scalar query, hindari lazy-load di luar tx) — snapshot `gaji_batch_master` tidak punya kolom is_askes/rumdins dan W4-1 tidak inject `PegawaiRepository`
  - Periode batch root `"YYYYMM"` dinormalisasi → `"YYYY-MM"` utk lookup `GajiKpi` (format KPI) & parsing window SP-3 → **keputusan #16 (locked)**

---

### Wave 5 — Snapshot Service

- [x] **W5-1** `GajiBatchProsesSnapshotService`:
  - Query `Pegawai` where `statusKerja = KARYAWAN_AKTIF` AND `statusPegawai != NON_PEGAWAI` — `PegawaiRepository.findEligibleForGaji` (JPQL fetch join: biodata, jabatan+level, golongan, gajiProfil, kodePajak)
  - Eager load: `biodata` (untuk statusKawin), `jabatan`+`level`, `golongan`, `organisasi` (EAGER entity), `gajiProfil`, `kodePajak` — `rumahDinas` TIDAK di-eager-load: REF_SEWA_RUMDIN di-resolve live (keputusan W4-1), kolomnya tidak ada di snapshot
  - Buat `GajiBatchMaster` per pegawai dengan snapshot lengkap (termasuk `jmlJiwa` = 1 + jmlTanggungan + kawin, sama dgn resolver)
  - `GajiBatchMasterRepository.saveAll(masters)`
  - Catatan: no-arg ctor `GajiBatchMaster` dilebarkan PROTECTED → public (konvensi repo, mirror `GajiBatchMasterProses`)

---

### Audit legacy — resolve komponen is_reference / formula kosong (2026-09-04)

> Sumber: `docs/php/payroll_bms.php` `hitung_gaji()` — dump dari DB dev (`gaji_komponen`, 9 profil).

Legacy **tidak punya** konsep `is_reference`/`#SYSTEM` — SEMUA nilai referensi diisi lewat `switch ($key)` per kode di PHP:

| Pola komponen | Cara legacy | Catatan utk engine baru |
|---------------|-------------|--------------------------|
| `gp`, `phdp` | `$row['emp_gp']` / `$row['emp_phdp']` langsung (**formula & nilai komponen diabaikan**) | Konfirmasi keputusan #11: PHDP = snapshot `Pegawai.phdp`. Nilai statis `PHDP` di seed (21362814 / 23736460) **mati** — jangan dipakai engine |
| `jml_anak` / `jml_jiwa` | Bukan komponen: `query_tanggungan_anak` + `1 + jml_anak + (kawin ? 1 : 0)` | Cocok dgn resolver map — tapi token ini TIDAK ada sbg baris komponen; ctx W6 harus di-seed dari `GajiBatchMaster` (jmlTanggungan/statusKawin) |
| `t_kpi` / `t_ter` | `query_kpi`/`query_ter` per pegawai per tahun | → `GajiKpi.tunkin` / `GajiKpi.pph21Ter` (W1) |
| `t_jabatan`, `t_kk`, `t_air`, `p_rudin`, `t_beras`, `p_askes`, `p_pph21`, `p_jp`, `p_tkk` | Query lookup langsung (level/golongan, rumdin, flag askes, PTKP, pot TKK) — **jalan walau formula komponen kosong** | **KEPUTUSAN (2026-09-04): implicit resolve per kode** — komponen formula kosong dgn lookup di-resolve via resolver: `TUNJ_JABATAN→REF_TUNJ_JABATAN`, `TUNJ_BERAS→REF_TUNJ_BERAS`, `TUNJ_KK→REF_TUNJ_KK`, `TUNJ_AIR→REF_TUNJ_AIR`, `PHDP→REF_PHDP` (keputusan #11). Kode tanpa lookup (`TUNJ_SI/ANAK/KESEHATAN/PPH21`, `ASTEK`, `PKP`, `POT_PENSIUN`) → 0.0, sama dgn guard legacy `if ($formula != '')` |
| `p_jp`/`p_askes` | Engine-level clamp `result > maksimal_potongan_* → maksimal_potongan_*` dari `sys_reference(code=payroll)` | **Nilai SUDAH di-seed** di `gaji_parameter_setting` rewrite — dikerjakan di W6-2 (keputusan #15, locked) |
| `p_pph21`/`t_pph21` | Clamp `result < 0 → 0` | Formula potongan pajak |
| Lainnya (formula aritmetika) | `replace_formula` (token komponen → nilai) lalu `eval` | Setara W6 substitusi + evaluator; hasil per-komponen di-`round(x,0)` |

**Temuan token:** hanya variabel non-komponen dalam formula seed = `JML_ANAK`, `JML_JIWA` (ctx) + fungsi `CEIL` — semua sudah dicover.



---

### Wave 6 — Kalkulasi Service

- [x] **W6-1** `GajiBatchProsesKalkulasiService`:
  - Load `GajiKomponen` by `profilGajiId` ordered by `urut`
  - Maintain `Map<String, Double> ctx` (accumulator semua nilai komponen), seed token non-komponen `JML_ANAK`/`JML_JIWA` dari resolver
  - Per komponen:
    - `isReference = true` atau `formula = "#SYSTEM"` → `referenceResolver.resolve(kode, ...)`
    - `formula` kosong → **implicit resolve per kode** (keputusan 2026-09-04: `TUNJ_JABATAN/BERAS/KK/AIR` → `REF_TUNJ_*`, `PHDP` → `REF_PHDP`; kode tanpa lookup → 0.0)
    - Else → substitusi kode komponen dalam formula + `formulaEvaluator.evaluate(...)`
  - Hasil dibulatkan: `Math.round(nilai)` (nilai round masuk ctx, dipakai formula berikutnya)
  - Simpan `GajiBatchMasterProses`: `kode, urut, nama, jenisGaji, nilai, formula (asli), nilaiFormula (formula + nilai tersubstitusi, format `BigDecimal.toPlainString` — tanpa notasi ilmiah)`
  - Update `GajiBatchMaster` totals dari ctx: `penghasilanKotor` (PENGHASILAN_KOTOR), `totalPotongan` (POTONGAN), `penghasilanBersih` (PENGHASILAN_BERSIH), `pembulatan` (PEMBULATAN), `penghasilanBersihFinal` (PENGHASILAN_BERSIH_FINAL), `pajak` (POT_PPH21 — legacy `tax = p_pph21`)
  - Signature `hitung(GajiBatchMaster master, String batchId)` — batchId eksplisit utk resolver (`REF_JML_POT_KK` per batch)
- [x] **W6-2** Clamp engine-level pasca-eval (keputusan #15): `POT_JP` → `min(nilai, maksimal_potongan_jpn)`, `POT_ASKES` → `min(nilai, maksimal_potongan_askes)`
  - `GajiParameterSettingRepository` + tambah finder `findByKode(kode)`; mapping kode komponen → kode parameter hardcode (2 entry)
  - Parameter tak ditemukan → log warn, tanpa cap (jangan cap ke 0 seperti default legacy)

---

### Wave 7 — Orchestrator

- [x] **W7-1** `GajiBatchProsesCommandService`:
  - `reset(rootBatchId)`: hapus `GajiBatchMasterProses` (`deleteByBatchMasterIdIn`) → hapus `GajiBatchMaster` (`findByGajiBatchRoot_Id` + `deleteAll`)
  - `prosesGaji(rootBatchId)`:
    1. Update `GajiBatchRoot.status = PROSES` + `tanggalProses`
    2. `reset(rootBatchId)`
    3. `snapshotService.snapshot(batchRoot)` → `List<GajiBatchMaster>`
    4. Parallel per pegawai: `kalkulasiService.hitung(master, batchId)` dalam try-catch — **deviasi**: spec minta `StructuredTaskScope.ShutdownOnFailure` tapi API itu masih **preview di JDK 25** (butuh `--enable-preview` di compile+test+run) → dipakai padanan setara `Executors.newVirtualThreadPerTaskExecutor()` + submit/get (`hitungSatu` men-catch SEMUA exception → tak ada subtask gagal, wait-all identik). Ganti bila project aktifkan preview
    5. Exception per pegawai → `GajiBatchRootErrorLogs`: `GajiFormulaException` → **DATA**, lainnya → **SYSTEM**; lanjut (error log di-*collect* dari fork, dicatat di thread utama setelah join — session Hibernate tidak thread-safe), cascade via `root.errorLogs` (cascade ALL)
    6. `totalPegawai = masters.size()`, `status = WAIT_VERIFICATION_PHASE_1`
    7. Exception fatal (di luar per-pegawai) → `status = FAILED` + error `SYSTEM` — **tidak rethrow** supaya FAILED + error log ter-persist (commit normal, bukan rollback)
  - `GajiBatchRootErrorLogsRepository` JPA tidak dibuat — error log dicatat via relasi cascade `root.errorLogs`

---

### Wave 8 — Startup Recovery

- [x] **W8-1** `GajiBatchProsesStartupService` (`@Component`, `@ApplicationListener<ApplicationReadyEvent>`):
  - Cari `GajiBatchRoot` dengan `status = PROSES` → set `FAILED` + error log SYSTEM "Server restart detected — proses terputus" (via cascade `root.errorLogs`)
  - Cari `GajiBatchRoot` dengan `status = PENDING` → publish `GajiBatchRootProcessEvent` (re-queue)
  - **Penting:** `onApplicationEvent` di-`@Transactional` — listener proses gaji adalah `@TransactionalEventListener(AFTER_COMMIT)`, publish di luar transaksi akan **dibuang** (fallbackExecution default false). Re-queue hanya jalan karena publish terjadi dalam transaksi
  - `findByStatus(EProsesGaji)` baru di `GajiBatchRootRepository`; soft-delete tetap ter-filter otomatis (@SQLRestriction)

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

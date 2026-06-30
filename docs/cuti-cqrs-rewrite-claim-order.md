# Cuti CQRS Rewrite — Claim Order & Checklists

> Manager-authored work order untuk rewrite modul **Cuti** (CQRS, JPA-write / JOOQ-read).
> Klaim fase **berurutan** — tiap fase punya dependensi ke fase sebelumnya (lihat kolom *State when you start*).
> Desain lengkap: **`CONTEXT.md` → "Keputusan rewrite modul Cuti" (Keputusan #1–#16)**. Tiap fase di bawah menautkan nomor keputusannya.
> Exemplar pola: **modul `kepegawaian/` (sudah selesai)** + **modul `master/`** (CRUD ref) — baca dulu sebelum menulis kode.

| Order | Fase                                  | Keputusan | State when you start                | Catatan                                  |
|-------|---------------------------------------|-----------|-------------------------------------|------------------------------------------|
| 0     | Pra-implementasi (setup)              | —         | kepegawaian-is7                     | Bikin epic + child issues beads dulu     |
| 1     | `CutiProperties` (config)             | #2        | kepegawaian-is7.1                   | Fondasi — dipakai semua fase             |
| 2     | Fungsi murni (Workday + MinimalCuti)  | #14, #12  | kepegawaian-is7.2                   | Tanpa Spring/DB, unit-test dulu          |
| 3     | DTO Jenis konsolidasi + mini-proj     | #15, #3   | kepegawaian-is7.3                   | Buang `JenisCuti{Mini,}Response` duplikat |
| 4     | CutiJenis CRUD (non-akar)             | #11, #15  | kepegawaian-is7.4                   | Mirror master, flat CRUD                 |
| 5     | CutiKuota CRUD + Excel POI            | #11       | kepegawaian-is7.5                   | Import/template tetap di Command         |
| 6     | Read-side JOOQ (Pengajuan/Inbox)      | #3, #15   | kepegawaian-is7.6                   | mini-projection, `refCuti` self-join     |
| 7     | Validator pengajuan + klaim           | #12       | kepegawaian-is7.7                   | `existsBy` eksplisit, `CutiProperties`   |
| 8     | Chain generator (approval chain)      | #4        | kepegawaian-is7.8                   | data-driven, `existsByJabatanId` konsisten |
| 9     | Quota allocator (reservasi)           | #5        | kepegawaian-is7.9                   | allocator murni, feed `jumlahHariKerja`  |
| 10    | Pengajuan Command (save/update/batal) | #1,#6,#8,#9 | kepegawaian-is7.10                | entry `@Transactional` tunggal           |
| 11    | Approval Command (state-machine)      | #6        | kepegawaian-is7.11                  | tak telan exception (ADR-0021)           |
| 12    | Klaim Command + allocator klaim 1:1   | #16, #10  | kepegawaian-is7.12                  | port 1:1, bug dilacak (lihat di bawah)   |
| 13    | Controllers (4, URL persis)           | #13       | kepegawaian-is7.13                  | buang `CutiController` kosong            |

**Bug issue terkait (JANGAN diperbaiki inline — preserve & track):**
`kepegawaian-ebt` (#10 now() cross-year), `kepegawaian-ciw` (#16 forNextYear −1),
`kepegawaian-sfq` (#16 now() reject-gate), `kepegawaian-s5n` (#16 entity .equals).

---

## STEP 0 — Sebelum kode apa pun (setiap klaim)

- [ ] `bd prime` (recover beads workflow context)
- [ ] `git status` clean; di branch `rewrite/master-cqrs`
- [ ] `npx gitnexus analyze` bila index stale (cek warning hook)
- [ ] Baca exemplar files yang akan ditiru (tercantum per fase)
- [ ] `bd update <id> --claim` issue yang dimulai

---

## FASE 0 — Pra-implementasi (setup beads)

**Goal:** bikin struktur tracking sebelum kode. Claim-order ini work-order; beads tetap satu-satunya tracker.

- [ ] `bd create` **epic** "Rewrite modul Cuti ke CQRS (Keputusan #1–#16)" — type=epic, simpan ringkasan 16 keputusan di field `--design`
- [ ] `bd create` child issue per fase (1–13), set `--design` rujuk nomor Keputusan di `CONTEXT.md`
- [ ] Tautkan dependency antar-fase via `bd dep add <blocked> <blocker>` sesuai kolom *State when you start*
- [ ] Tautkan 4 bug issue (ebt/ciw/sfq/s5n) sebagai child fase 12 (klaim) & fase 9/kuota — flag "preserve, do not fix inline"
- [ ] Update tabel di atas dengan ID issue nyata setelah dibuat

---

## FASE 1 — `CutiProperties` (Keputusan #2)

**Goal:** ganti `@Value`/`DefConfig` tersebar dengan satu bean `@ConfigurationProperties(prefix="app.cuti")`. Mirror `PegawaiProperties`.
**Exemplar (baca dulu):** `config/PegawaiProperties.java`.

### Pre-edit
- [ ] `gitnexus_impact({target: "DefConfig", direction: "upstream"})` — petakan semua pembaca `getJenisCuti{Tahunan,Besar,Ibadah}`, level jabatan, dll
- [ ] Warn manager bila HIGH/CRITICAL

### Implementasi
- [ ] NEW `config/CutiProperties.java` — `@ConfigurationProperties(prefix="app.cuti")`, field typed: `jenisCutiTahunan/Besar/Ibadah` (id), level/jabatan SDM & direksi (lihat #4)
- [ ] `application.yml` — blok `app.cuti.*` dari env var (pola proyek)
- [ ] Daftarkan via `@EnableConfigurationProperties` / `@ConfigurationPropertiesScan`
- [ ] Catat: belum hapus `DefConfig` (masih dipakai modul lain) — hanya konsumen cuti yang pindah

### Acceptance
- [ ] Semua konsumen cuti inject `CutiProperties`, bukan `DefConfig`/`@Value`
- [ ] `./gradlew compileJava` → SUCCESSFUL
- [ ] `gitnexus_detect_changes()` scope sesuai

---

## FASE 2 — Fungsi murni: WorkdayCalculator + MinimalCutiRule (Keputusan #14, #12)

**Goal:** ekstrak dua aturan jadi fungsi statik murni (tanpa Spring/DB), **perbaiki bug double-subtract** hari kerja.

### WorkdayCalculator (#14)
- [ ] NEW `helpers/cuti/WorkdayCalculator.java` — `static int count(LocalDate tglMulai, LocalDate tglSelesai, Set<LocalDate> libur)`
- [ ] Logika **satu lintasan**: hitung tanggal yang `!weekend && !libur` (pinjam logika benar `DateHelper.getWorkingDays`)
- [ ] **FIX double-subtract**: jangan `countWeekday − countLibur` (libur di Sabtu/Minggu ter-kurang ganda)
- [ ] Sumber libur via `HariLiburRepository.findByTanggalBetween(...)` → `Set<LocalDate>` (bukan `countByTanggalBetween`)
- [ ] Unit test: kasus libur di weekday, libur di weekend (harus TIDAK mengurangi lagi), rentang lintas bulan

### MinimalCutiRule (#12)
- [ ] NEW `helpers/cuti/MinimalCutiRule.java` — `static void check(int totalHariKerja, int totalSisaKuota)`
- [ ] Port aritmatika: `<3` hari → bila `sisaKuota >= 3` throw "minimal 3 hari"; else bila `hariKerja < sisaKuota` throw "Sisa Kuota … harus diambil semua"
- [ ] Unit test tiga cabang

### Acceptance
- [ ] Nol dependensi Spring di kedua kelas
- [ ] Tiga titik lama (create/update/`findTotalHariKerja`) nanti panggil `WorkdayCalculator.count` yang sama (diverifikasi di fase 6 & 10)

---

## FASE 3 — Konsolidasi DTO Jenis + mini-projection (Keputusan #15, #3)

**Goal:** satu hierarki mini `{id,nama}` untuk semua nested jenis; baca dirakit di mapper JOOQ (Pola B), bukan `from(entity)` lazy.
**Exemplar:** `mapper/master/jabatan/JabatanMapper.java` (Pola B), mapper JOOQ master mana pun.

### Pre-edit
- [ ] `gitnexus_impact` pada `JenisCutiResponse`, `JenisCutiMiniResponse`, `CutiJenisResponse`, `CutiJenisMiniResponse` — konfirmasi consumer (sudah diverifikasi: keempatnya dipakai, dua mini identik)

### Implementasi
- [ ] Pertahankan `CutiJenisMiniResponse {id,nama}` sebagai SATU mini kanonik
- [ ] Pertahankan `CutiJenisResponse {id,parent,nama,maxHari,potongKuotaTahunan}` (CRUD Jenis)
- [ ] DELETE `JenisCutiMiniResponse` (duplikat literal `{id,nama}`)
- [ ] DELETE `JenisCutiResponse` (pengajuan rujuk `CutiJenisMiniResponse` untuk nested `jenisCuti`/`subJenisCuti` — cukup `{id,nama}`)
- [ ] Update `CutiPengajuanResponse`/`CutiPengajuanMiniResponse` agar nested pakai `CutiJenisMiniResponse`

### Acceptance
- [ ] `grep -rn 'JenisCutiResponse\|JenisCutiMiniResponse' src` → 0 (kecuali `CutiJenisMiniResponse`)
- [ ] `./gradlew compileJava` → SUCCESSFUL

---

## FASE 4 — CutiJenis CRUD non-akar (Keputusan #11, #15)

**Goal:** CRUD standar pola master, **flat** (response hanya bawa `parent` mini, bukan nested children).
**Exemplar:** modul master CRUD (Command+Query+JOOQ+Mapper+Controller).

- [ ] NEW `mapper/cuti/jenis/CutiJenisMapper.java` — final, private ctor, `toEntity`/`updateEntity`
- [ ] NEW `repositories/cuti/jooq/CutiJenisQueryRepository.java` — `pageQuery`/`getById`/`listQuery`, `IS_DELETED.eq(false)`, parent via self-join mini
- [ ] NEW `services/cuti/jenis/CutiJenisQueryService.java`
- [ ] NEW `services/cuti/jenis/CutiJenisCommandService.java` — create (revive bila soft-deleted), update, delete (soft)
- [ ] DELETE interface `CutiJenisService` + `CutiJenisServiceImpl` (ADR-0007)
- [ ] `delete()` stub lama (bila ada) dibuang — andalkan soft-delete `is_deleted`

### Acceptance
- [ ] Read = JOOQ, write = JPA `getReferenceById` untuk parent
- [ ] `./gradlew build` → SUCCESSFUL

---

## FASE 5 — CutiKuota CRUD + Excel POI (Keputusan #11)

**Goal:** CRUD + `importData` (parse Excel) + `exportTemplate` (build Excel) + `findByPegawai(pegawaiId, tahun)`. **Import & template-build tetap di sisi Command** (operasi tulis / I/O byte-stream, bukan QueryService).

- [ ] NEW `mapper/cuti/kuota/CutiKuotaMapper.java`
- [ ] NEW `repositories/cuti/jooq/CutiKuotaQueryRepository.java` — `pageQuery`/`getById`, `findByPegawai(pegawaiId, tahun → CutiKuotaSisa)`
- [ ] NEW `services/cuti/kuota/CutiKuotaQueryService.java` — hanya baca (page/getById/sisa)
- [ ] NEW `services/cuti/kuota/CutiKuotaCommandService.java`:
  - [ ] `create`/`update`/`delete` (soft)
  - [ ] `importData` — parse POI (HSSF/XSSF), dedup `existsByTahun`, expired default `LocalDate.of(tahun+1,6,30)`, filter pegawai aktif
  - [ ] `exportTemplate` — build POI SXSSF, kembalikan byte-stream
- [ ] Pertahankan logika POI apa adanya
- [ ] DELETE interface + Impl lama (ADR-0007)

### Acceptance
- [ ] `/master/cuti/kuota` (atau path lama) tetap melayani `/template`, `/import`, `/{pegawaiId}/{tahun}/sisa`
- [ ] `./gradlew build` → SUCCESSFUL (catat status `CutiKuotaRepositoryTest` bila masih ada)

---

## FASE 6 — Read-side JOOQ Pengajuan + Inbox (Keputusan #3, #15)

**Goal:** `CutiPegawaiQuery` JOOQ — index pengajuan, by-pegawai, by-id, **inbox approval** (`/cuti/pengajuan/approval`). mini-projection, nested = `row(id+label)`, rakit string di Java mapper. `refCuti` via **self-join** ke `cuti_pegawai`.

- [ ] NEW `repositories/cuti/jooq/CutiPegawaiQueryRepository.java`
  - [ ] index pengajuan (filter status/pegawai/tahun — **FIX bug filter tahun** MONTH→YEAR bila terbawa)
  - [ ] inbox: pengajuan dengan `picSaatIni` = jabatan approver, status PENDING/RETURNED
  - [ ] `getById` rakit nested: pegawai/biodata, jenisCuti/subJenisCuti (mini `{id,nama}`), `refCuti` self-join
- [ ] NEW `services/cuti/pengajuan/CutiPegawaiQueryService.java`
- [ ] Mapper JOOQ rakit `CutiPengajuanResponse` — **bukan** `from(entity)` lazy (Pola B)

### Acceptance
- [ ] Tidak ada `CutiPengajuanResponse.from(CutiPegawai)` lazy tersisa di read-path
- [ ] Inbox diarahkan ke Query JOOQ, bukan `CutiApprovalChainService`
- [ ] `./gradlew build` → SUCCESSFUL

---

## FASE 7 — Validator pengajuan & klaim (Keputusan #12)

**Goal:** validator kolaborator khusus dipanggil Command sebelum mutasi. Ganti Specification-on-DTO → JPA derived `existsBy…`. Inject `CutiProperties`.

- [ ] NEW `services/cuti/pengajuan/CutiPengajuanValidator.java` (atau pertahankan nama lama)
  - [ ] `validate(...)` — existsBy PENDING; existsBy cuti besar; existsBy cuti ibadah → throw masing-masing
  - [ ] panggil `MinimalCutiRule.check(...)` (fase 2)
  - [ ] `validateKlaim(...)` → kembalikan `CutiPegawai` tervalidasi (refCuti APPROVED, jenis ∈ {tahunan,ibadah}, klaim belum ada, tak ada ibadah berlangsung)
- [ ] Tambah derived query di repo tulis: `existsByPegawaiIdAnd…StatusIn`, `existsByPegawaiIdAndJenisCutiId…`
- [ ] Hapus Specification-builder di DTO request (`getPendingStatusSpecification`, `getSpecificationByJenisCuti`)
- [ ] Inject `CutiProperties` ganti `DefConfig`

### Acceptance
- [ ] Nol `repository.exists(Specification)` di path cuti
- [ ] Unit-test minimal rule lewat (via fase 2)

---

## FASE 8 — Chain generator approval (Keputusan #4)

**Goal:** `generateApprovalChain` 3 metode near-dup → satu data-driven. Konsisten `existsByJabatanId` (bukan campur `findById`). Buang hardcode string jabatan.

### Pre-edit
- [ ] `gitnexus_impact({target: "CutiApprovalChainService", direction: "upstream"})`

### Implementasi
- [ ] Rakit chain dari daftar jabatan (data-driven by `approvalLevel` & jabatan hierarki), bukan 3 cabang `if jabatanLevelId`
- [ ] Gunakan `existsByJabatanId` seragam (FIX asimetri findById vs existsBy)
- [ ] Hapus hardcode "Supervisor Adm. & Pengembangan SDM" → ambil dari `CutiProperties`
- [ ] Inject `CutiProperties` ganti `@Value` (supervisorSdm/managerSdm/direkturUtama/direkturUmum/levelSupervisor/levelManager)

### Acceptance
- [ ] Satu metode generator, tabel/loop bukan copy-paste
- [ ] `./gradlew build` → SUCCESSFUL

---

## FASE 9 — Quota allocator reservasi (Keputusan #5)

**Goal:** 6 metode periode → satu allocator murni untuk **reservasi** N hari ke bucket riwayat0/riwayat1. Diberi makan `jumlahHariKerja` bersih dari `WorkdayCalculator` (fase 2).

- [ ] NEW allocator reservasi (murni / minim-DB) — alokasi lintas batas tahun (hanya 2 bucket)
- [ ] FIX `saveCutiNonTahunan` double-set bila terbawa
- [ ] Hindari `LocalDate.now()` di dalam allocator — terima `nowYear`/tahun dari pemanggil
- [ ] Pertahankan path `CutiKuotaUpdateByCutiService` **terpisah** (deduksi/finalisasi) — **bug now() cross-year DIPERTAHANKAN** → issue `kepegawaian-ebt`

### Acceptance
- [ ] Reservasi memakai `jumlahHariKerja` hasil `WorkdayCalculator` (bukan rumus buggy)
- [ ] Unit test alokasi: dalam-tahun, menyebrang-tahun, tahun-depan

---

## FASE 10 — Pengajuan Command (Keputusan #1, #6, #8, #9)

**Goal:** `PengajuanCutiCommand` — save/update/pembatalan dalam satu entry `@Transactional`, tak menelan exception (ADR-0021).
**Exemplar:** Command service modul `kepegawaian/` yang sudah selesai.

- [ ] NEW `services/cuti/pengajuan/PengajuanCutiCommand.java`
  - [ ] `save` — validate (fase 7) → `WorkdayCalculator.count` → `toEntity` (Mapper) → allocator reservasi (fase 9) → generate chain (fase 8) → set WRITE pointer
  - [ ] `update` — sama, atas entity existing
  - [ ] `pembatalan(id)` — set status CANCELED (bukan hard-delete); buang `delete()` stub `return false`
  - [ ] FK via `getReferenceById` (ADR-0008)
- [ ] CSRF single-use token Redis dipertahankan (Keputusan #7) — UUID, 5-min TTL
- [ ] FIX `@SQLDelete` target tabel salah pada child entity (Keputusan #8)
- [ ] **TIDAK** ada `try{...}catch(Exception){FAILED}` membungkus seluruh body — exception naik, hanya entry yang membungkus ke `SavedStatus`

### Acceptance
- [ ] Satu `@Transactional` public per operasi
- [ ] `gitnexus_detect_changes()` scope sesuai

---

## FASE 11 — Approval Command state-machine (Keputusan #6)

**Goal:** `ApprovalCutiCommand.savePengajuan` — advance/retreat chain pointer (APPROVED→level+1, REJECTED→level−1), finalisasi panggil deduksi kuota. Tak menelan exception.

- [ ] NEW `services/cuti/approval/ApprovalCutiCommand.java`
  - [ ] validasi approver = `picSaatIni` **via `.getId()`** (konsisten)
  - [ ] `doSaveAcceptReject` — temukan WRITE chain, advance/retreat, set pointer, finalisasi → `cutiKuotaUpdateByCutiService.updateKuota`
  - [ ] `rejectCutiPegawai` — set REJECTED, mundurkan chain
  - [ ] CSRF token check (Keputusan #7)
- [ ] **TIDAK** ada exception-swallowing (ADR-0021) — perbaikan ini in-scope (beda dari math klaim #16)

### Acceptance
- [ ] Pointer chain benar saat APPROVED/REJECTED/RETURNED
- [ ] `./gradlew build` → SUCCESSFUL

---

## FASE 12 — Klaim Command + allocator klaim 1:1 (Keputusan #16, #10)

**Goal:** `KlaimCutiCommand` + port `CutiApproveKlaimCutiService` **1:1**. Klaim = settlement/refund (beda dari reservasi #5). Dispatch periode 5-cara dibiarkan duplikat. **Semua bug dipertahankan, dilacak terpisah.**

- [ ] NEW `services/cuti/klaim/KlaimCutiCommand.java` (save/update klaim) — validate via `validateKlaim` (fase 7)
- [ ] Port `CutiApproveKlaimCutiService` 5 metode periode **apa adanya** (forNextYear/overlappingYear/between1JanAnd30Jun/between1JulAnd31Dec/between30JunAnd1Jul)
- [ ] Settlement math: `sisa = jumlahHariPengajuan − jumlahHariKlaim`, refund (`kuotaTerpakai −= sisa; sisaKuota += sisa`), set `refCuti.isClaimed = true`
- [ ] **JANGAN** ekstrak classifier bersama — dispatch inline (keputusan eksplisit #16)
- [ ] **Bug DIPERTAHANKAN (track, do NOT fix inline):**
  - [ ] `forNextYear` `getYear() − 1` → `kepegawaian-ciw`
  - [ ] `between1JanAnd30Jun` `LocalDate.now()` reject-gate → `kepegawaian-sfq`
  - [ ] `saveKlaim` `picSaatIni.equals(jabatan)` entity-ref → `kepegawaian-s5n`
- [ ] **Pengecualian:** exception-swallowing `saveKlaim` BUKAN dipertahankan — entry `@Transactional` tunggal tak menelan exception (selaras fase 11)

### Acceptance
- [ ] Tiap bug yang dipertahankan punya komentar `// PRESERVED: see kepegawaian-xxx` di kode + issue masih open
- [ ] `./gradlew build` → SUCCESSFUL

---

## FASE 13 — Controllers (Keputusan #13)

**Goal:** pertahankan kontrak URL **persis** (kompat FE), 4 controller, buang `CutiController` kosong.

- [ ] DELETE `controllers/cuti/CutiController.java` (kosong/dead)
- [ ] `CutiPengajuanController` (`/cuti/pengajuan`) ← `PengajuanCutiCommand` + `KlaimCutiCommand` + `CutiPegawaiQuery`
  - [ ] Routes lama persis: index, `/approval` (inbox→Query), `/{pegawaiId}/pegawai`, `/{id}`, `/{tglMulai}/{tglSelesai}/total-hari-kerja`, POST create, PUT `/{id}`, POST `/klaim`, PUT `/klaim/{id}`, DELETE `/{id}` (pembatalan)
  - [ ] Guard tanggal di controller: `tanggalMulai.isAfter(tanggalSelesai)`, `tanggalMulai.isBefore(LocalDate.now())`
- [ ] `CutiApprovalController` (`/cuti/approval`) ← `ApprovalCutiCommand`
- [ ] `CutiJenisController` (`/cuti/jenis`) ← Query+Command (fase 4)
- [ ] `CutiKuotaController` (`/cuti/kuota`) ← Query+Command + `/template`, `/import`, `/{pegawaiId}/{tahun}/sisa` (fase 5)
- [ ] Semua mutating endpoint `@PreAuthorize("hasRole('ADMIN')")` + `@Valid` + `Errors`
- [ ] Envelope `CustomResult.any/list/page/save/delete`

### Acceptance (final modul)
- [ ] Semua path & verb lama identik (kompat FE) — diff kontrak = 0
- [ ] `grep -rn 'CutiController' src` → 0
- [ ] `./gradlew clean build` → BUILD SUCCESSFUL
- [ ] `gitnexus_detect_changes()` scope = modul cuti
- [ ] Verifikasi subagent: read=JOOQ, write=JPA, tak ada interface+Impl sisa, tak ada exception-swallowing (kecuali bug klaim yang ter-track)

---

## Ship (tiap fase)

- [ ] `gitnexus_detect_changes()` — scope hanya simbol yang diharapkan
- [ ] `git add` + commit (pesan rujuk Keputusan # & ADR)
- [ ] `bd close <id>`
- [ ] `bd dolt push` (no-op bila no remote) → `git pull --rebase` → `git push` → verify "up to date with origin"

---

## Guardrails (semua fase)

- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER rename/move dengan find-and-replace — pakai `gitnexus_rename`
- NEVER commit tanpa `gitnexus_detect_changes()`
- beads satu-satunya tracker — tanpa TodoWrite / markdown TODO
- Soft-delete only (`is_deleted`), never hard-delete
- 4 bug klaim/kuota **dipertahankan** (parity) — JANGAN fix inline, biarkan issue open
- Stop & tanya manager bila impact analysis HIGH/CRITICAL

# Profil CQRS Cleanup — Claim Order & Checklists

> Manager-authored work order untuk **pembersihan konsistensi CQRS** modul **Profil** (bukan rewrite penuh — modul sudah berjalan, ini merapikan pelanggaran pola).
> Hasil grilling `/grill-with-docs` (senior dev vs `CODING_RULES.md` §4 + `docs/context/decisions-pegawai.md` §51/§53/§55 + `docs/context/decisions-cuti.md` §11), 2026-07-01.
> Epic payung: **`kepegawaian-3kj`**. Semua fase di bawah = child issue nyata (bukan placeholder).
> Klaim per **wave**: edit dalam satu wave **saling lepas** (file disjoint) → boleh paralel; antar-wave file bisa beririsan → kerjakan berurutan agar tak baca file ulang (hemat token).
> Exemplar pola: **modul `cuti/` (sudah selesai)** — mapper Pola B di `mapper/cuti/<aggregate>/`, read=JOOQ / write=JPA. Baca dulu sebelum menulis kode.

| Order | Fase (issue)                                            | Sumber keputusan            | State when you start    | Catatan                                          |
|-------|---------------------------------------------------------|-----------------------------|-------------------------|--------------------------------------------------|
| 0     | Pra-implementasi (setup epik + verifikasi)              | —                           | `kepegawaian-3kj`       | Epik + 15 child sudah dibuat; verifikasi tree    |
| W1a   | Relokasi `BiodataRowMapper`                             | decisions-pegawai §51, §43  | `kepegawaian-iki`       | 1 importer: `BiodataQueryRepository`             |
| W1b   | Relokasi `BiodataDetailRowMapper` + 2 Multiset          | decisions-pegawai §51       | `kepegawaian-g5n`       | 3 mapper, 1 importer `BiodataDetailQuery`        |
| W1c   | Relokasi `PendidikanRowMapper`                          | decisions-pegawai §51       | `kepegawaian-fvi`       | 2 importer (Query + DetailQuery)                 |
| W1d   | Relokasi `ProfilKeluargaRowMapper`                      | decisions-pegawai §51       | `kepegawaian-5ny`       | 2 importer (Query + DetailQuery)                 |
| W1e   | Relokasi `KeahlianRowMapper`                            | decisions-pegawai §51       | `kepegawaian-rpq`       | 2 importer (Query + DetailQuery)                 |
| W1f   | Relokasi `KartuIdentitasRowMapper`                      | decisions-pegawai §51       | `kepegawaian-ure`       | inline `new KartuIdentitasRowMapper()`           |
| W1g   | Relokasi `PelatihanRowMapper`                           | decisions-pegawai §51       | `kepegawaian-4lt`       | 1 importer                                       |
| W1h   | Relokasi `PengalamanKerjaRowMapper`                     | decisions-pegawai §51       | `kepegawaian-lep`       | 1 importer                                       |
| W2a   | `PendidikanCommandService` (164) — download→Query + split | decisions-pegawai §53, CR§4 | `kepegawaian-z6c`       | split CRUD vs Lampiran                            |
| W2b   | `ProfilKeluargaCommandService` (151) — download→Query + split | decisions-pegawai §53, CR§4 | `kepegawaian-yp6`       | split bila masih >120                            |
| W2c   | `BiodataCommandService` (148) — `findFotoProfil`→Query + split | decisions-pegawai §53, CR§4 | `kepegawaian-0bv`       | entity `Biodata` dikecualikan max-line           |
| W2d   | `KartuIdentitasCommandService` (126) — download→Query + split | decisions-pegawai §53, CR§4 | `kepegawaian-44t`       | mungkin ≤120 tanpa split lanjutan                |
| W2e   | Keahlian/Pelatihan/PengalamanKerja — download→Query     | decisions-pegawai §53       | `kepegawaian-xfh`       | sudah ≤120 (113/118/101), cukup relokasi         |
| PU-1  | Buang interface `ProfileUpdateService` (single-impl)    | decisions-cuti §11          | `kepegawaian-mfq`       | rename Impl→interface name; injector tak berubah |
| PU-2  | Migrasi read `ProfileUpdate` ke JOOQ + split            | decisions-pegawai §55, §35  | `kepegawaian-996`       | **blocked oleh PU-1**; scope terbesar            |

---

## STEP 0 — Sebelum kode apa pun (setiap klaim)

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` clean; di branch `rewrite/master-cqrs`
- [x] `npx gitnexus analyze` bila index stale (cek warning hook — index terakhir 48725e6)
- [x] Baca exemplar `cuti`: satu `mapper/cuti/<aggregate>/*Mapper.java` (Pola B) + `CutiPengajuanQueryRepository`
- [x] `bd update kepegawaian-<id> --claim` issue yang dimulai

---

## FASE 0 — Pra-implementasi (setup beads)

**Goal:** struktur tracking sudah jadi. Claim-order ini work-order; beads tetap satu-satunya tracker.

- [x] `bd create` **epic** `kepegawaian-3kj` — ringkasan 4 temuan grilling di `--description`
- [x] `bd create` 15 child (W1a–h, W2a–e, PU-1, PU-2), tautkan parent-child ke epik
- [x] `bd dep add kepegawaian-996 kepegawaian-mfq` (PU-2 blocked by PU-1)
- [x] `decisions-pegawai.md` ditambah 4 bullet keputusan (§51, §53, §55) — commit `fc6d071`
- [x] Push epik + issue ke Dolt/remote saat session-close

---

## WAVE 1 — Relokasi read mapper ke `mapper/profil/` (Pola B)

**Sumber:** decisions-pegawai §51. **Semua W1a–W1h file-disjoint → boleh paralel.**
**Goal:** 10 `*RowMapper`/`*MultisetMapper` yang salah taruh di `repositories/profil/jooq/` (kesalahan agent lama) dipindah ke `mapper/profil/<aggregate>/*JooqMapper.java`. Package destinasi **sudah ada** (8 aggregate: biodata, kartuIdentitas, keahlian, keluarga, lampiranProfil, pelatihan, pengalamanKerja, pendidikan).

**Pola B (wajib):** `public final class`, private ctor, `implements RecordMapper<Record, DTO>`, **BUKAN `@Component`**. Multiset mapper (`Pendidikan`/`KartuIdentitas`) **LEGIT** — nested child pada detail view `BiodataDetailQuery`, tidak melanggar ADR-0001 mini-projection.

**Workflow HARD INVARIANT (CODING_RULES) tiap mapper:**
1. `gitnexus_impact({target, direction:"upstream"})` — konfirmasi importer
2. `git mv <old> <new>` (JANGAN find/replace)
3. Read path baru
4. Edit `package` line + `import` di setiap importer
5. **single `git add` batch di akhir** (NEVER paralel `git add` + Edit; NEVER amend)
6. `git diff --cached` → `./gradlew compileJava`

- [x] **W1a `iki`** `BiodataRowMapper` → `mapper/profil/biodata/BiodataJooqMapper.java`. Importer: `BiodataQueryRepository.java`
- [x] **W1b `g5n`** 3 mapper, importer TUNGGAL `BiodataDetailQuery.java` (edit sekali):
  - `BiodataDetailRowMapper` → `mapper/profil/biodata/`
  - `PendidikanMultisetMapper` → `mapper/profil/pendidikan/`
  - `KartuIdentitasMultisetMapper` → `mapper/profil/kartuIdentitas/`
- [x] **W1c `fvi`** `PendidikanRowMapper` → `mapper/profil/pendidikan/`. Importer: `PendidikanQueryRepository` + `PendidikanDetailQuery`
- [x] **W1d `5ny`** `ProfilKeluargaRowMapper` → `mapper/profil/keluarga/`. Importer: `ProfilKeluargaQueryRepository` + `ProfilKeluargaDetailQuery`
- [x] **W1e `rpq`** `KeahlianRowMapper` → `mapper/profil/keahlian/`. Importer: `KeahlianQueryRepository` + `KeahlianDetailQuery`
- [x] **W1f `ure`** `KartuIdentitasRowMapper` → `mapper/profil/kartuIdentitas/`. Importer: `KartuIdentitasQueryRepository` (inline `new KartuIdentitasRowMapper()`)
- [x] **W1g `4lt`** `PelatihanRowMapper` → `mapper/profil/pelatihan/`. Importer: `PelatihanQueryRepository`
- [x] **W1h `lep`** `PengalamanKerjaRowMapper` → `mapper/profil/pengalamanKerja/`. Importer: `PengalamanKerjaQueryRepository`

### Acceptance (Wave 1)
- [x] `grep -rl "repositories.profil.jooq" src --include='*.java'` → 0 referensi ke mapper yang dipindah
- [x] Tak ada `*RowMapper`/`*MultisetMapper` tersisa di `repositories/profil/jooq/`
- [x] Tiap mapper = `final class` + private ctor + `implements RecordMapper`, tanpa `@Component`
- [x] `./gradlew compileJava` → SUCCESSFUL
- [x] `gitnexus_detect_changes()` scope = mapper + importer terkait saja

---

## WAVE 2 — File-download keluar dari Command → `*QueryService` + max-line split

**Sumber:** decisions-pegawai §53 + CODING_RULES §4. **W2a–W2e file-disjoint per aggregate → boleh paralel; TAPI kerjakan setelah Wave 1** (Wave 1 menyentuh QueryRepository, Wave 2 menyentuh QueryService/CommandService — hindari overlap ragu).
**Goal:** method file-download (`getFileLampiranById`/`findFotoProfil`) yang cuma delegasi ke `lampiranProfilQueryService` **dipindah** dari `*CommandService` ke `*QueryService` aggregat (keputusan user — read tak boleh di write-layer). Controller diarahkan ke QueryService; pass-through di CommandService dihapus. Lalu split file yang masih >120.

**Pola per issue:** (1) tambah method baca di `*QueryService`; (2) arahkan controller ke QueryService; (3) hapus pass-through dari `*CommandService`; (4) bila sisa CommandService masih >120 → pisah CRUD (create/update/delete) vs Lampiran (addLampiran/deleteLampiran) ke `*LampiranCommandService`. **Entity data-holder DIKECUALIKAN** dari batas 120.

**Workflow:** `gitnexus_impact` dulu → edit → `./gradlew compileJava` → verifikasi `wc -l ≤ 120` tiap file hasil.

- [x] **W2a `z6c`** `PendidikanCommandService` (164) → download ke Query, split CRUD vs `PendidikanLampiranCommandService`
- [x] **W2b `yp6`** `ProfilKeluargaCommandService` (151) → download ke Query, split bila masih >120
- [x] **W2c `0bv`** `BiodataCommandService` (148) → `findFotoProfil` ke Query, split bila >120. Entity `Biodata.java` dikecualikan
- [x] **W2d `44t`** `KartuIdentitasCommandService` (126) → download ke Query (kemungkinan ≤120 tanpa split lanjutan)
- [x] **W2e `xfh`** `Keahlian`(113)/`Pelatihan`(118)/`PengalamanKerja`(101)CommandService → **hanya relokasi download** (sudah ≤120, tak perlu split)

### Acceptance (Wave 2)
- [x] Nol method file-download tersisa di `*CommandService` profil
- [x] Controller download endpoint memanggil `*QueryService`, bukan `*CommandService`
- [x] `wc -l` semua `*CommandService`/`*LampiranCommandService` profil ≤ 120 (entity dikecualikan)
- [x] `./gradlew build` → SUCCESSFUL
- [x] `gitnexus_detect_changes()` scope sesuai

---

## FASE PU-1 — Buang interface `ProfileUpdateService` (decisions-cuti §11)

**State when you start:** `kepegawaian-mfq`. **Goal:** interface single-impl (20 baris, 1 impl) dibuang.

- [x] `gitnexus_impact({target:"ProfileUpdateService", direction:"upstream"})` — konfirmasi 6 injector
- [x] `gitnexus_rename` `ProfileUpdateServiceImpl` → `ProfileUpdateService` (JANGAN find/replace)
- [x] Hapus file interface lama
- [x] Karena field injection bertipe interface (`private final ProfileUpdateService ...`), teks 6 injector (5 CommandService + `ProfilUpdateController`) **tidak berubah** — tak perlu disentuh
- [x] `./gradlew compileJava` → SUCCESSFUL

### Acceptance
- [x] `grep -rn "ProfileUpdateServiceImpl" src` → 0
- [x] Tak ada interface single-impl `ProfileUpdateService` tersisa

---

## FASE PU-2 — Migrasi read `ProfileUpdate` ke JOOQ + split (BLOCKED oleh PU-1)

**State when you start:** `kepegawaian-996` (depends on `mfq`). **Goal:** read-side `ProfileUpdate` masih JPA Specification (`repository.findAll(spec, pageable)` di baris ~40) — migrasi ke JOOQ selaras arah modul. **Scope terbesar** — bila membengkak, boleh dipromosikan jadi epik terpisah.

- [ ] Tunggu PU-1 selesai (nama kelas final `ProfileUpdateService`)
- [ ] `gitnexus_impact` pada method read `findPage`/`findById` ProfileUpdate
- [ ] NEW `repositories/profil/jooq/ProfileUpdateQueryRepository.java` (JOOQ) + mapper Pola B di `mapper/profil/<aggregate>/`
- [ ] Ganti `findPage`/`findById` dari Specification → JOOQ `where` + `SortParam.resolve`
- [ ] Hapus Specification-builder pada DTO request bila blast-radius kosong
- [ ] Split command vs query bila file hasil >120

### Acceptance
- [ ] Nol `repository.findAll(Specification, ...)` di path ProfileUpdate
- [ ] Read = JOOQ, write = JPA
- [ ] `wc -l` file hasil ≤ 120
- [ ] `./gradlew build` → SUCCESSFUL

---

## Ship (tiap issue)

- [ ] `gitnexus_detect_changes()` — scope hanya simbol yang diharapkan
- [ ] `git add` (single batch) + commit (pesan rujuk decisions-pegawai § & epic `kepegawaian-3kj`)
- [ ] `bd close kepegawaian-<id>`
- [ ] `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin"

---

## Guardrails (semua fase)

- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER rename/move dengan find-and-replace — pakai `gitnexus_rename` / `git mv`
- NEVER commit tanpa `gitnexus_detect_changes()`
- Git mv + Edit HARD INVARIANT: single `git add` batch di akhir; NEVER paralel `git add` + Edit; NEVER amend
- beads satu-satunya tracker — tanpa TodoWrite / markdown TODO
- Soft-delete only (`is_deleted`), never hard-delete
- Entity data-holder & repository pure-query **dikecualikan** dari batas 120 baris (CODING_RULES §4, sesuai keputusan user)
- Multiset mapper detail-view LEGIT — bukan pelanggaran ADR-0001
- Stop & tanya manager bila impact analysis HIGH/CRITICAL

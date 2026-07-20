# Profesi Page — apdList & alatKerjaList — Claim Order & Checklist

> Manager-authored work order. Satu issue, satu owner. Kerjakan langkah **berurutan** —
> tiap langkah menyiapkan langkah berikutnya. Detail lengkap: `bd show kepegawaian-li1`.

| Order | Issue ID        | Title                                                        | State  | Claim cmd                           |
|-------|-----------------|--------------------------------------------------------------|--------|-------------------------------------|
| 1     | kepegawaian-li1 | GET /master/profesi page tidak mengembalikan apdList & alatKerjaList | READY | `bd update kepegawaian-li1 --claim` |

---

## Konteks (baca dulu)

- **Root cause:** regresi rewrite CQRS master (`6a8a08b`) + konversi record (`b732295`).
  Saat `ProfesiQuery` dibangun ulang dari nol, dua list `apdList`/`alatKerjaList` tidak ikut
  dibawa. Bukan terhapus dari DB — hanya tidak diproyeksikan di query page.
- **`GET /{id}` sudah benar:** `ProfesiDetailQuery.getById()` sudah proyeksikan kedua list via
  JOOQ `multiset`. Itu **pola yang dijiplak** untuk page.

### Keputusan manager (hasil grilling)
1. **Satu DTO.** Hapus `ProfesiQuery`; page & detail sama-sama pakai `ProfesiDetail`
   (field sudah identik + dua list). FE tidak akan punya page/CRUD terpisah untuk APD & Alat
   Kerja — keduanya dikelola menyatu dengan Profesi.
2. **Pakai `multiset` apa adanya** (jiplak `ProfesiDetailQuery`), BUKAN batch fetch.
   Glossary master: APD & Alat Kerja per profesi selalu sedikit.
3. **Scope HANYA page.** `GET /{id}` dan `/list` tidak disentuh.

---

## STEP 0 — Sebelum sentuh kode

- [x] `bd prime` (recover beads workflow context)
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] `bd update kepegawaian-li1 --claim`
- [x] Baca file yang dijiplak: `repositories/master/jooq/ProfesiDetailQuery.java`
      (pola dua `multiset` + `convertFrom(mapping(...))`)

### Pre-edit (impact)
- [x] `gitnexus_impact({target: "pageQuery", direction: "upstream"})` — lapor blast radius
- [x] `gitnexus_impact({target: "ProfesiQuery", direction: "upstream"})` — konfirmasi pemakai
      hanya di dalam paket `master/profesi` (mapper, DTO, controller, repo, selects) → aman dihapus
- [x] STOP & lapor manager jika ada hasil HIGH/CRITICAL

---

## LANGKAH KERJA

### 1 — Tambah dua multiset ke `pageQuery`
File: `repositories/master/jooq/ProfesiQueryRepository.java`
- [x] Ganti return type `Page<ProfesiQuery>` → `Page<ProfesiDetail>`
- [x] Di `dsl.select(...)`, tambahkan dua `multiset` subquery — jiplak persis dari
      `ProfesiDetailQuery`, tapi ganti `.where(APD.PROFESI_ID.eq(id))` menjadi join ke
      `PROFESI.ID` (per-baris page, bukan single id):
      `.where(APD.PROFESI_ID.eq(PROFESI.ID))` — sama untuk `ALAT_KERJA`
- [x] Alias tetap `"apd_list"` & `"alat_kerja_list"` (dibaca `toDetail`)
- [x] `.fetch(ProfesiJooqMapper::toQuery)` → `.fetch(ProfesiJooqMapper::toDetail)`
- [x] Ganti kolom select `ProfesiSelects.PROFESI_QUERY_COLUMNS` → `ProfesiSelects.PROFESI_COLUMNS`
- [x] Buang import `ProfesiQuery` yang jadi tak terpakai

### 2 — Collapse DTO & kolom (hapus `ProfesiQuery`)
- [x] `ProfesiSelects.java`: hapus `PROFESI_QUERY_COLUMNS` (identik dgn detail); sisakan satu
      array — rename `PROFESI_DETAIL_COLUMNS` → `PROFESI_COLUMNS` dan pakai di kedua query
      (page + detail). Update pemakai di `ProfesiDetailQuery` juga.
- [x] `ProfesiJooqMapper.java`: hapus method `toQuery(Record)` + import `ProfesiQuery`
- [x] DELETE `dto/master/profesi/ProfesiQuery.java`
- [x] `grep -rn 'ProfesiQuery' src` → **0 results** (kecuali `ProfesiIndexQuery`, beda kelas)

### 3 — Rapikan signature hulu
- [x] `services/master/profesi/ProfesiQueryService.java`: `pageQuery` return
      `Page<ProfesiDetail>`; buang import `ProfesiQuery` yang tak terpakai
- [x] `controllers/master/ProfesiController.java`: `index()` return
      `ResponseEntity<PageResult<Page<ProfesiDetail>>>`
- [x] `/list` (`ProfesiListResponse`) & `/{id}` (`ProfesiDetail`) TIDAK disentuh

### 4 — Verifikasi
- [x] `grep -rn 'ProfesiQuery\b' src` → 0 (only `ProfesiIndexQuery` boleh muncul)
- [x] `grep -rn 'toQuery' src/main/.../profesi` → 0
- [x] `grep -rn 'PROFESI_QUERY_COLUMNS' src` → 0
- [x] `./gradlew compileJava` → BUILD SUCCESSFUL
- [x] `GET /master/profesi` → tiap item punya `apdList` & `alatKerjaList` terisi (via multiset)
- [x] `GET /master/profesi/{id}` masih sama seperti sebelumnya (regression check)
- [x] `GET /master/profesi/list` masih hanya `{id, nama}`
- [x] File ≤ 120 baris (CODING_RULES §4) — cek yang disentuh

### Ship
- [x] Code review clean — no issues found
- [x] commit: `fix(master): restore apdList & alatKerjaList on profesi page (kepegawaian-li1)`
- [x] `bd close kepegawaian-li1`
- [x] `bd dolt push` → `git pull --rebase` → `git push` → "up to date with origin"

---

## Guardrails
- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER rename/hapus dengan find-and-replace — pakai `gitnexus_rename`
- NEVER commit tanpa `gitnexus_detect_changes()`
- beads SATU-SATUNYA tracker — no TodoWrite / markdown TODO
- Soft-delete only (`is_deleted`), `multiset` sudah filter `IS_DELETED.eq(false)`
- Stop & tanya manager jika impact HIGH/CRITICAL

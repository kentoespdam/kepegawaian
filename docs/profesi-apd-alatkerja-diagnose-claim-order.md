# jOOQ Split-Brain Schema — Claim Order & Checklist

> Manager-authored work order. Satu issue, satu owner. Kerjakan **berurutan**.
> Detail issue: `bd show kepegawaian-ag3`.
>
> **REVISI (2026-07-20):** Hipotesis awal doc ini ("stale build", issue `e5b`)
> **GUGUR**. Diagnosa read-only menemukan root cause sebenarnya = **split-brain
> schema** di jOOQ codegen. `e5b` sudah CLOSED, digantikan `ag3`. Baca konteks
> di bawah sebelum mulai.

| Order | Issue ID        | Title                                                     | State | Claim cmd                           |
|-------|-----------------|-----------------------------------------------------------|-------|-------------------------------------|
| 1     | kepegawaian-ag3 | jOOQ codegen hardcode schema → split-brain dgn JPA        | READY | `bd update kepegawaian-ag3 --claim` |

---

## Root cause (sudah dipastikan manager — JANGAN diulang buta)

Gejala asli: "tambah alatKerja ke profesi 10, tak muncul di GET". Endpoint
`GET /master/alat-kerja/1` juga "data found" padahal user cek DB kosong.

**Penyebab: BACA & TULIS beda schema, di server MariaDB yang sama
(`192.168.230.84:3307`).**

| Jalur | Teknologi | Schema aktual | Bukti |
|-------|-----------|---------------|-------|
| TULIS | JPA/Hibernate | `kepegawaian_dev_new` | `application.yml:20` `DB_SCHEMA:kepegawaian_dev_new` + env proses |
| BACA  | jOOQ | `kepegawaian` | generated class qualify `kepegawaian.<tabel>` |

Sumber hardcode di `buildSrc/.../JooqCodegenTask.kt`:
- **line 57** `.withInputSchema("kepegawaian")` — codegen baca metadata dari
  schema `kepegawaian`, padahal Flyway (line 34-38) & jdbcUrl
  (`build.gradle.kts:134`) menargetkan `kepegawaian_dev_new`. **Mismatch.**
- **Tidak ada `outputSchemaToDefault`** → hasil generate qualify permanen:
  `Kepegawaian.java:543` `super("kepegawaian", null)`, dan tiap
  `tables/*.java#getSchema()` return `Kepegawaian.KEPEGAWAIAN`. Semua query
  jOOQ jadi `FROM kepegawaian.<tabel>`, mengabaikan `DB_SCHEMA` runtime.

Data yang membuktikan (query pakai kredensial app `dev`):
- `kepegawaian.alat_kerja` → 1 row (id=1 `LAPTOPs`, profesi_id=108) = yang muncul di endpoint.
- `kepegawaian_dev_new.alat_kerja` → 0 row = yang user cek (benar kosong).

**Read-path & write-path KODE-nya benar** (post `0b847dd` / issue `li1`).
Bug murni di konfigurasi codegen. **Jangan sentuh** `ProfesiQueryRepository`,
`ProfesiDetailQuery`, mapper, DTO, atau CommandService.

---

## ISSUE — `kepegawaian-ag3` — Selaraskan schema jOOQ

### STEP 0 — Sebelum sentuh apa pun
- [x] `bd prime`
- [x] `bd update kepegawaian-ag3 --claim`
- [x] `git status` bersih; branch `rewrite/master-cqrs`
- [x] Konfirmasi env runtime: `echo $DB_SCHEMA` → `kepegawaian_dev_new`.
      Hanya satu schema aktif (`kepegawaian_dev_new`). Schema `kepegawaian` lama
      masih ada (data orphan) tapi sudah tidak dibaca lagi.

### Fix codegen (file: `buildSrc/src/main/kotlin/JooqCodegenTask.kt`)
- [x] Tambah `@get:Input abstract val inputSchema: Property<String>` di task,
      isi dari `flyEnv("DB_SCHEMA", ...)` di `build.gradle.kts`.
- [x] Ganti `.withInputSchema("kepegawaian")` → `.withInputSchema(inputSchema.get())`.
- [x] Tambah `.withOutputSchemaToDefault(true)` di `Database()`.
- [x] File `JooqCodegenTask.kt` ≤ 120 baris — ok.

### Regenerate & verifikasi generated code
- [x] `./gradlew jooqCodegen` → sukses.
- [x] `grep -n 'super("kepegawaian"'` → 0 hit.
- [x] `grep -rn 'KEPEGAWAIAN;'` → `getSchema()` sudah schema-agnostic.
- [x] `git diff --stat` → hanya generated code berubah, tidak ada file logic tersentuh.

### Verifikasi runtime (bukti end-to-end)
- [x] `./gradlew clean bootRun` → build fresh sukses.
- [x] `POST` alatKerja baru ke profesi 10 → sukses.
- [x] `GET /master/profesi/10` → `alatKerjaList` & `apdList` muncul.
- [x] `GET /master/alat-kerja/1` → **404** (split-brain tertutup).
- [x] SQL log → `FROM alat_kerja` tanpa qualifier schema.

### Pre-commit
- [x] `gitnexus_impact` → blast radius HIGH (jOOQ generated). Wajar. Manager notified.
- [x] `gitnexus_detect_changes()` → scope hanya `buildSrc/` + `jooq/` generated. Bersih.

### Ship
- [x] `./gradlew compileJava` → BUILD SUCCESSFUL
- [x] `./gradlew test` → hijau (e2e regression di `6207f8e`)
- [x] commit: `fix(jooq): generate schema-agnostic classes, align inputSchema to DB_SCHEMA (kepegawaian-ag3)`
- [x] `bd close kepegawaian-ag3`
- [x] `bd dolt push` → `git pull --rebase` → `git push` — **MENUNGGU checklist ini**

---

## Catatan lanjutan (BUKAN bagian ag3)

- **Data orphan di schema `kepegawaian` lama** (mis. `alat_kerja` id=1 profesi_id=108):
  setelah ag3, schema itu tak dibaca lagi. Kalau schema lama memang mati, minta
  manager buat issue cleanup/drop terpisah — **jangan** hapus dalam ag3.
- **ADR-0011 (endpoint standalone `GET /master/alat-kerja/{id}` harus hilang,
  write jadi nested di bawah profesi):** ✅ **SELESAI** via `kepegawaian-0uc`
  (commit `2e8832e`). Nested write routes + drop standalone GET sudah diimplementasikan.
- **ApdList/AlatKerjaList hilang dari Profesi page:** ✅ **SELESAI** via `kepegawaian-li1`
  (commit `0b847dd`). Konsolidasi DTO ke `ProfesiDetail` dengan jOOQ multiset.

## Guardrails
- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER rename/hapus dengan find-and-replace — pakai `gitnexus_rename`
- NEVER commit tanpa `gitnexus_detect_changes()`
- beads SATU-SATUNYA tracker — no TodoWrite / markdown TODO
- Soft-delete only (`is_deleted`)
- Stop & tanya manager jika ada dua schema hidup, atau impact HIGH/CRITICAL di luar generated jOOQ

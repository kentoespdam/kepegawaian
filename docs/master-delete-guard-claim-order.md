# Master Delete-Guard — Claim Order & Checklist

> Manager-authored work order. Satu issue: `kepegawaian-15u`.
> Spec lengkap: `bd show kepegawaian-15u` (field design) + `docs/context/decisions-master.md` §8.
> Kerjakan **berurutan** — tiap parent independen, tapi lakukan Profesi lebih dulu (paling berisiko).

| Order | Issue ID        | Title                                                       | State  | Claim cmd                           |
|-------|-----------------|-------------------------------------------------------------|--------|-------------------------------------|
| 1     | kepegawaian-15u | Delete master: guard owned-child (jangan cascade)           | CLOSED | `bd update kepegawaian-15u --claim` |

---

## STEP 0 — Sebelum sentuh kode

- [x] `bd prime`
- [x] `git status` bersih; di branch `rewrite/master-cqrs`
- [x] `bd update kepegawaian-15u --claim`
- [x] Baca `docs/context/decisions-master.md` §8

### Pre-edit (impact) — WAJIB per file

- [x] `gitnexus_impact({target: "delete", direction: "upstream", ...})` per command service yang disentuh
- [x] STOP & lapor manager jika HIGH/CRITICAL

---

## LANGKAH KERJA

### 1 — Profesi (paling berisiko; kena ceiling 120 baris)

- [x] **LAPOR manager** soal ceiling 121 baris sebelum edit → keputusan: extract `ProfesiDeleteGuardHelper`
- [x] `ApdRepository`: tambah `boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId)`
- [x] `AlatKerjaRepository`: tambah `boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId)`
- [x] `ProfesiCommandService.delete`: sebelum `setIsDeleted(true)`, cek APD lalu AlatKerja
      (short-circuit); jika ada → `throw new ConflictException("Profesi masih memiliki APD/Alat Kerja")`
- [x] Inject `ApdRepository` + `AlatKerjaRepository` → diekstrak ke `ProfesiDeleteGuardHelper`

### 2 — Organisasi (self-ref)

- [x] `OrganisasiRepository`: tambah `boolean existsByParentIdAndIsDeletedFalse(Long parentId)`
- [x] `OrganisasiCommandService.delete`: guard sebelum `setIsDeleted(true)` →
      `throw new ConflictException("Organisasi masih memiliki sub-organisasi")`

### 3 — Jabatan (self-ref)

- [x] `JabatanRepository`: tambah `boolean existsByParentIdAndIsDeletedFalse(Long parentId)`
- [x] `JabatanCommandService.delete`: guard → `"Jabatan masih memiliki sub-jabatan"`

### 4 — JenisSp

- [x] `SanksiRepository`: tambah `boolean existsByJenisSpIdAndIsDeletedFalse(Long jenisSpId)`
- [x] `JenisSpCommandService.delete`: inject `SanksiRepository`; guard →
      `"JenisSp masih memiliki sanksi"`

### 5 — Verifikasi

- [x] `./gradlew compileJava` → BUILD SUCCESSFUL
- [x] Manual/uji: DELETE parent yg punya child aktif → **409** dgn pesan child-spesifik
- [x] DELETE parent tanpa child aktif → sukses (soft-delete seperti biasa)
- [x] 13 master lain: delete tetap jalan tanpa guard (tak tersentuh)
- [x] File yang disentuh ≤ 120 baris (Profesi: diekstrak ke `ProfesiDeleteGuardHelper`)

### Ship

- [x] Code review clean
- [x] `gitnexus_detect_changes()` — verifikasi scope hanya 4 command service + 5 repo
- [x] commit: `feat(master): refuse delete when owned-child active (kepegawaian-15u)`
- [x] `bd close kepegawaian-15u`
- [x] `bd dolt push` → `git pull --rebase` → `git push` → "up to date with origin"

---

## Guardrails

- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER cascade / `orphanRemoval` / loop hapus child otomatis
- NEVER guard karena lookup-referrer — hanya owned-child
- Pakai `existsBy` (bukan `countBy`, bukan JOOQ di command path)
- Soft-delete only (`is_deleted`)
- STOP & lapor manager jika impact HIGH/CRITICAL atau ceiling 120 baris kelewat

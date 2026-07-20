# Master Delete-Guard — Claim Order & Checklist

> Manager-authored work order. Satu issue: `kepegawaian-15u`.
> Spec lengkap: `bd show kepegawaian-15u` (field design) + `docs/context/decisions-master.md` §8.
> Kerjakan **berurutan** — tiap parent independen, tapi lakukan Profesi lebih dulu (paling berisiko).

| Order | Issue ID        | Title                                                       | State | Claim cmd                           |
|-------|-----------------|-------------------------------------------------------------|-------|-------------------------------------|
| 1     | kepegawaian-15u | Delete master: guard owned-child (jangan cascade)           | OPEN  | `bd update kepegawaian-15u --claim` |

---

## Konteks (baca dulu)

- **Root cause:** semua `*CommandService.delete` master hanya `findById → setIsDeleted(true) → save`.
  `@OneToMany` di parent **tanpa** `cascade`/`orphanRemoval`, dan path pakai `save` (bukan
  `repository.delete`, jadi `@SQLDelete` child tak terpicu). Owned-child menggantung ke parent
  non-aktif = **orphan logis**.
- **Kebijakan (BUKAN cascade):** child TIDAK ikut terhapus. Delete parent **DITOLAK** (`ConflictException`
  → 409) selama masih ada owned-child aktif (`is_deleted=false`).
- **Infra sudah lengkap:** `ConflictException(409)` + `GlobalExceptionHandler` + semua child repo
  sudah ada. Yang baru **hanya** method `existsBy...AndIsDeletedFalse` per repo + blok guard.

### Owned-child vs lookup-referrer (definisi memblokir)
- **Memblokir** = hierarki self-ref (`parent`) atau child yang eksistensinya bergantung pada parent.
- **TIDAK memblokir** = lookup-referrer (FK masuk sekadar referensi: `Profesi.organisasi`,
  `Pegawai.organisasi`, data transaksional/riwayat, `Grade.profesiList`, `Jabatan.profesiList`).
  Kalau lookup ikut memblokir, Organisasi praktis tak pernah bisa dihapus.

### Parent yang kena guard (4 dari 17 master entity)

| Parent | Owned-child pemblokir | Repo (sudah ada) | Method baru (Spring Data derived) | Pesan 409 |
|--------|----------------------|------------------|-----------------------------------|-----------|
| Organisasi | sub-Organisasi (self-ref `parent`) | OrganisasiRepository | `existsByParentIdAndIsDeletedFalse` | "Organisasi masih memiliki sub-organisasi" |
| Jabatan | sub-Jabatan (self-ref `parent`) | JabatanRepository | `existsByParentIdAndIsDeletedFalse` | "Jabatan masih memiliki sub-jabatan" |
| Profesi | `apd`, `alatKerja` | ApdRepository, AlatKerjaRepository | `existsByProfesiIdAndIsDeletedFalse` | "Profesi masih memiliki APD/Alat Kerja" |
| JenisSp | `sanksi` | SanksiRepository | `existsByJenisSpIdAndIsDeletedFalse` | "JenisSp masih memiliki sanksi" |

**13 master lain TANPA guard** (soft-delete langsung, jangan disentuh): AlasanBerhenti, Golongan,
Grade, HariLibur, JenisKeahlian, JenisKitas, JenisPelatihan, JenjangPendidikan, Level, RumahDinas,
Sanksi, Apd, AlatKerja.

### ⚠️ Ceiling — CODING_RULES §4 (max 120 baris)
`ProfesiCommandService.java` **sudah 121 baris** sebelum guard. Menambah 2 inject repo + blok guard
akan memperparah. **STOP & lapor manager** sebelum edit Profesi — putuskan dulu: extract helper,
pisah guard ke kelas terpisah, atau refactor lain. Tiga file lain aman (77/86/55 baris).

---

## STEP 0 — Sebelum sentuh kode

- [ ] `bd prime`
- [ ] `git status` bersih; di branch `rewrite/master-cqrs`
- [ ] `bd update kepegawaian-15u --claim`
- [ ] Baca `docs/context/decisions-master.md` §8

### Pre-edit (impact) — WAJIB per file
- [ ] `gitnexus_impact({target: "delete", direction: "upstream", ...})` per command service yang disentuh
- [ ] STOP & lapor manager jika HIGH/CRITICAL

---

## LANGKAH KERJA

### 1 — Profesi (paling berisiko; kena ceiling 120 baris)
- [ ] **LAPOR manager** soal ceiling 121 baris sebelum edit (lihat ⚠️ di atas)
- [ ] `ApdRepository`: tambah `boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId)`
- [ ] `AlatKerjaRepository`: tambah `boolean existsByProfesiIdAndIsDeletedFalse(Long profesiId)`
- [ ] `ProfesiCommandService.delete`: sebelum `setIsDeleted(true)`, cek APD lalu AlatKerja
      (short-circuit); jika ada → `throw new ConflictException("Profesi masih memiliki APD/Alat Kerja")`
- [ ] Inject `ApdRepository` + `AlatKerjaRepository`

### 2 — Organisasi (self-ref)
- [ ] `OrganisasiRepository`: tambah `boolean existsByParentIdAndIsDeletedFalse(Long parentId)`
- [ ] `OrganisasiCommandService.delete`: guard sebelum `setIsDeleted(true)` →
      `throw new ConflictException("Organisasi masih memiliki sub-organisasi")`

### 3 — Jabatan (self-ref)
- [ ] `JabatanRepository`: tambah `boolean existsByParentIdAndIsDeletedFalse(Long parentId)`
- [ ] `JabatanCommandService.delete`: guard → `"Jabatan masih memiliki sub-jabatan"`

### 4 — JenisSp
- [ ] `SanksiRepository`: tambah `boolean existsByJenisSpIdAndIsDeletedFalse(Long jenisSpId)`
- [ ] `JenisSpCommandService.delete`: inject `SanksiRepository`; guard →
      `"JenisSp masih memiliki sanksi"`

### 5 — Verifikasi
- [ ] `./gradlew compileJava` → BUILD SUCCESSFUL
- [ ] Manual/uji: DELETE parent yg punya child aktif → **409** dgn pesan child-spesifik
- [ ] DELETE parent tanpa child aktif → sukses (soft-delete seperti biasa)
- [ ] 13 master lain: delete tetap jalan tanpa guard (tak tersentuh)
- [ ] File yang disentuh ≤ 120 baris (khusus Profesi: konfirmasi hasil keputusan ceiling)

### Ship
- [ ] Code review clean
- [ ] `gitnexus_detect_changes()` — verifikasi scope hanya 4 command service + 5 repo
- [ ] commit: `feat(master): refuse delete when owned-child active (kepegawaian-15u)`
      — **sekalian** dengan commit doc §8 + issue (manager: digabung saat implementasi)
- [ ] `bd close kepegawaian-15u`
- [ ] `bd dolt push` → `git pull --rebase` → `git push` → "up to date with origin"

---

## Guardrails
- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER cascade / `orphanRemoval` / loop hapus child otomatis
- NEVER guard karena lookup-referrer — hanya owned-child
- Pakai `existsBy` (bukan `countBy`, bukan JOOQ di command path)
- Soft-delete only (`is_deleted`)
- STOP & lapor manager jika impact HIGH/CRITICAL atau ceiling 120 baris kelewat

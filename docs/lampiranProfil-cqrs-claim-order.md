# lampiranProfil CQRS — Claim Order & Checklist

Epic: **kepegawaian-94u**. Kerjakan **berurutan** `#2 → #3 → #4` (sudah dikunci via blocking deps; hanya issue READY yang muncul di `bd ready`).

Tiap issue: `bd update <id> --claim` saat mulai, `bd close <id>` saat selesai (test hijau).

---

## Aturan wajib tiap langkah (CODING_RULES)
- [ ] `gitnexus_impact({target, direction:"upstream"})` sebelum edit symbol → laporkan blast radius
- [ ] `gitnexus_detect_changes()` sebelum commit
- [ ] `./gradlew test` hijau
- [ ] max 120 baris/file
- [ ] JANGAN hard-delete (selalu `setIsDeleted(true)`)
- [ ] JANGAN rename via find/replace (pakai `gitnexus_rename`)

---

## [x] #2 — READ side · `kepegawaian-94u.1` (READY)
> Additive. Belum hapus apa pun. `LampiranRow` JANGAN disentuh.

- [x] `bd update kepegawaian-94u.1 --claim`
- [x] gitnexus_impact: `LampiranProfilServiceImpl.getLampiran` & `getLampiranById` (upstream)
- [x] Buat `dto/profil/lampiranProfil/LampiranProfilQuery.java` — POJO `@Data`, 9 field (id, ref, refId, fileName, mimeType, notes, disetujui, disetujuiOleh, tanggalDisetujui). Pertahankan `@JsonFormat`/`@JsonSerialize` pada `tanggalDisetujui`. TANPA `.from(entity)`.
- [x] Buat `repositories/profil/jooq/LampiranProfilQueryRepository.java` (JOOQ, DSLContext):
  - [x] `List<LampiranProfilQuery> findByRefAndRefId(ref, refId)` — where REF=`(byte) ref.ordinal()` AND REF_ID AND IS_DELETED=false
  - [x] `Optional<LampiranProfilQuery> getById(id)` — where ID AND IS_DELETED=false
  - [x] map byte ordinal → enum via `EJenisLampiranProfil.values()[b]`
- [x] Buat `services/profil/lampiranProfil/LampiranProfilQueryService.java`:
  - [x] `getLampiran(jenis,id)` → queryRepo.findByRefAndRefId
  - [x] `getLampiranById(id)` → queryRepo.getById(...).orElse(null)
  - [x] `getFileLampiranById(jenis,id)` → PINDAH logika streaming byte dari Impl (baris 47–65) APA ADANYA
- [x] detect_changes → `./gradlew test` hijau (harus tetap kompilasi tanpa ubah consumer)
- [x] `bd close kepegawaian-94u.1`

## [x] #3 — WRITE side · `kepegawaian-94u.2` (blocked by #2)
> Interface lama MASIH ada (6 consumer pakai). Jangan dihapus di sini.

- [x] `bd update kepegawaian-94u.2 --claim`
- [x] gitnexus_impact: `addLampiran`, `deleteById`, `acceptLampiran`, `deleteByRefId` (upstream)
- [x] Buat `services/profil/lampiranProfil/LampiranProfilCommandService.java` (semua `@Transactional`):
  - [x] `addLampiran(...)` — pertahankan cek duplicate + upload; `SavedStatus.build(status, entity.getId())` (id, bukan string)
  - [x] `deleteById(id)` — soft-delete, return boolean
  - [x] `acceptLampiran(req, oleh)` — `SavedStatus` isi data = id lampiran
  - [x] `deleteByRefId(jenis,id)` — soft-delete batch, void
- [x] Update `controllers/profil/LampiranProfilController.java`: inject `command` + `query`
  - [x] GET `/file/{jenis}/{id}` → `query.getFileLampiranById`
  - [x] POST `/accept` → `command.acceptLampiran`
  - [x] DELETE `/delete/{id}` → `command.deleteById`
- [x] detect_changes → `./gradlew test` hijau
- [x] `bd close kepegawaian-94u.2`

## [ ] #4 — CUTOVER + hapus shim lama · `kepegawaian-94u.3` (blocked by #3)
> 6 consumer: keahlian, pendidikan, pelatihan, pengalamanKerja, kartuIdentitas, keluarga (ProfilKeluarga).

- [x] `bd update kepegawaian-94u.3 --claim`
- [x] gitnexus_impact: `LampiranProfilService` (upstream) — konfirmasi 8 importer
- [x] Per consumer `*CommandService.java`: ganti field `LampiranProfilService` → `LampiranProfilQueryService` + `LampiranProfilCommandService`
  - [x] keahlian
  - [x] pendidikan
  - [x] pelatihan
  - [x] pengalamanKerja
  - [x] kartuIdentitas
  - [x] keluarga (ProfilKeluarga)
  - [x] read delegate → QueryService, ubah return `List<LampiranProfilQuery>`/`LampiranProfilQuery`, hapus import `LampiranProfilResponse`
  - [x] write delegate → CommandService
- [x] Verifikasi tiap controller profil tetap kompilasi (CustomResult generic)
- [x] grep `LampiranProfilService` & `LampiranProfilResponse` = 0 pemakai produktif
- [x] HAPUS: `LampiranProfilService.java`, `LampiranProfilServiceImpl.java`, `LampiranProfilResponse.java` (hapus bersih, bukan shim)
- [x] detect_changes → `./gradlew test` hijau (satu batch `git add` di akhir)
- [x] `bd close kepegawaian-94u.3`

---

## Session close (setelah semua hijau)
- [x] quality gates hijau
- [x] `bd dolt push`
- [x] `git pull --rebase`
- [x] `git push` → verifikasi "up to date with origin"

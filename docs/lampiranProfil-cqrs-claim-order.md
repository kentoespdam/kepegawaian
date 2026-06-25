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

## [ ] #2 — READ side · `kepegawaian-94u.1` (READY)
> Additive. Belum hapus apa pun. `LampiranRow` JANGAN disentuh.

- [ ] `bd update kepegawaian-94u.1 --claim`
- [ ] gitnexus_impact: `LampiranProfilServiceImpl.getLampiran` & `getLampiranById` (upstream)
- [ ] Buat `dto/profil/lampiranProfil/LampiranProfilQuery.java` — POJO `@Data`, 9 field (id, ref, refId, fileName, mimeType, notes, disetujui, disetujuiOleh, tanggalDisetujui). Pertahankan `@JsonFormat`/`@JsonSerialize` pada `tanggalDisetujui`. TANPA `.from(entity)`.
- [ ] Buat `repositories/profil/jooq/LampiranProfilQueryRepository.java` (JOOQ, DSLContext):
  - [ ] `List<LampiranProfilQuery> findByRefAndRefId(ref, refId)` — where REF=`(byte) ref.ordinal()` AND REF_ID AND IS_DELETED=false
  - [ ] `Optional<LampiranProfilQuery> getById(id)` — where ID AND IS_DELETED=false
  - [ ] map byte ordinal → enum via `EJenisLampiranProfil.values()[b]`
- [ ] Buat `services/profil/lampiranProfil/LampiranProfilQueryService.java`:
  - [ ] `getLampiran(jenis,id)` → queryRepo.findByRefAndRefId
  - [ ] `getLampiranById(id)` → queryRepo.getById(...).orElse(null)
  - [ ] `getFileLampiranById(jenis,id)` → PINDAH logika streaming byte dari Impl (baris 47–65) APA ADANYA
- [ ] detect_changes → `./gradlew test` hijau (harus tetap kompilasi tanpa ubah consumer)
- [ ] `bd close kepegawaian-94u.1`

## [ ] #3 — WRITE side · `kepegawaian-94u.2` (blocked by #2)
> Interface lama MASIH ada (6 consumer pakai). Jangan dihapus di sini.

- [ ] `bd update kepegawaian-94u.2 --claim`
- [ ] gitnexus_impact: `addLampiran`, `deleteById`, `acceptLampiran`, `deleteByRefId` (upstream)
- [ ] Buat `services/profil/lampiranProfil/LampiranProfilCommandService.java` (semua `@Transactional`):
  - [ ] `addLampiran(...)` — pertahankan cek duplicate + upload; `SavedStatus.build(status, entity.getId())` (id, bukan string)
  - [ ] `deleteById(id)` — soft-delete, return boolean
  - [ ] `acceptLampiran(req, oleh)` — `SavedStatus` isi data = id lampiran
  - [ ] `deleteByRefId(jenis,id)` — soft-delete batch, void
- [ ] Update `controllers/profil/LampiranProfilController.java`: inject `command` + `query`
  - [ ] GET `/file/{jenis}/{id}` → `query.getFileLampiranById`
  - [ ] POST `/accept` → `command.acceptLampiran`
  - [ ] DELETE `/delete/{id}` → `command.deleteById`
- [ ] detect_changes → `./gradlew test` hijau
- [ ] `bd close kepegawaian-94u.2`

## [ ] #4 — CUTOVER + hapus shim lama · `kepegawaian-94u.3` (blocked by #3)
> 6 consumer: keahlian, pendidikan, pelatihan, pengalamanKerja, kartuIdentitas, keluarga (ProfilKeluarga).

- [ ] `bd update kepegawaian-94u.3 --claim`
- [ ] gitnexus_impact: `LampiranProfilService` (upstream) — konfirmasi 8 importer
- [ ] Per consumer `*CommandService.java`: ganti field `LampiranProfilService` → `LampiranProfilQueryService` + `LampiranProfilCommandService`
  - [ ] keahlian
  - [ ] pendidikan
  - [ ] pelatihan
  - [ ] pengalamanKerja
  - [ ] kartuIdentitas
  - [ ] keluarga (ProfilKeluarga)
  - [ ] read delegate → QueryService, ubah return `List<LampiranProfilQuery>`/`LampiranProfilQuery`, hapus import `LampiranProfilResponse`
  - [ ] write delegate → CommandService
- [ ] Verifikasi tiap controller profil tetap kompilasi (CustomResult generic)
- [ ] grep `LampiranProfilService` & `LampiranProfilResponse` = 0 pemakai produktif
- [ ] HAPUS: `LampiranProfilService.java`, `LampiranProfilServiceImpl.java`, `LampiranProfilResponse.java` (hapus bersih, bukan shim)
- [ ] detect_changes → `./gradlew test` hijau (satu batch `git add` di akhir)
- [ ] `bd close kepegawaian-94u.3`

---

## Session close (setelah semua hijau)
- [ ] quality gates hijau
- [ ] `bd dolt push`
- [ ] `git pull --rebase`
- [ ] `git push` → verifikasi "up to date with origin"

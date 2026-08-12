# Claim Order — ProfileUpdateService: cakupan 8 entity + mesin generik approval

> Work-order hasil grilling `/grill-with-docs` (grilling + domain-modeling), 2026-08-12.
> Konteks: pemeriksaan `ProfileUpdateService` pada modul profil → **sudah diimplementasikan** (PU-1 ✅ interface dibuang, PU-2 ✅ read JOOQ), tapi ditemukan 5 gap vs domain + mismatch role.
> Keputusan final: **ADR-0036** (`docs/adr/0036-profileupdate-mesin-generik-8-entity.md`). Glossary diperbarui di `docs/context/language-profil.md`.
> Epic payung beads: `kepegawaian-xxx` (dibuat di fase 0).

---

## FASE 0 — Pra-implementasi (setup beads + verifikasi)

**Goal:** struktur tracking sudah jadi; verifikasi fakta sebelum edit.

- [ ] `bd prime` (recover workflow context)
- [ ] `git status` clean; di branch `rewrite/master-cqrs`
- [ ] `npx gitnexus analyze` bila index stale
- [ ] `bd create` **epic** (ringkasan 5 gap + keputusan ADR-0036 di `--description`)
- [ ] `bd create` child issues (W1–W6 di bawah), tautkan parent-child ke epic
- [ ] `bd dep add` sesuai blokir antar fase

---

## W1 — Role & guard seragam

**Sumber:** ADR-0036 §2–3.

| # | Task | File | Keterangan |
|---|------|------|-----------|
| W1a | Fix `ChangedStatusResolver`: `ROLE_SDM` → `ROLE_ADMIN` ATAU `ROLE_HRD` | `services/profil/ChangedStatusResolver.java` | Update Javadoc; `requiresApproval()` = true bila tidak punya ADMIN/HRD |
| W1b | Fix `PengalamanKerjaCommandService`: tambah guard `if (Boolean.FALSE.equals(changedStatus)) return;` sebelum `create()` (3 titik: create/update/delete) | `services/profil/pengalamanKerja/PengalamanKerjaCommandService.java` | Sejajarkan pola `handleRevisionUpdate` milik Keahlian/Pelatihan/Pendidikan |
| W1c | Fix `BiodataCommandService.patchBiodata`: `changedStatus = resolver.requiresApproval()` + guard `create()` | `services/profil/biodata/BiodataCommandService.java` | Inject `ChangedStatusResolver`; hardcode `true` dihapus |
| W1d | Wire `ProfilKeluargaCommandService`: `changedStatus=resolver.requiresApproval()` (sudah) + `create()` dengan guard | `services/profil/keluarga/ProfilKeluargaCommandService.java` | Tambah `ProfileUpdateService` + `EProfileUpdateTable.KELUARGA`; perbaiki "handler KELUARGA dead code" |
| W1e | Wire `KartuIdentitasCommandService`: `create()` dengan guard (create/update/delete) | `services/profil/kartuIdentitas/KartuIdentitasCommandService.java` | `EProfileUpdateTable.KARTU_IDENTITAS` baru |
| W1f | Tambah `KARTU_IDENTITAS`, `LAMPIRAN` di enum | `entities/commons/EProfileUpdateTable.java` | +2 nilai |

**Acceptance W1:** `grep -rn "ROLE_SDM" src` → 0 (kecuali test lama yang di-update); semua 7 CommandService profil memanggil `create()` dengan guard `changedStatus`; `./gradlew compileJava` SUCCESS.

---

## W2 — Mesin generik approval (refactor total handler)

**Sumber:** ADR-0036 §5, §7. **Blokir: W1 selesai** (nama enum + resolver final).

**Prinsip:** satu class generik mengelola flow (changeHandler/markAsStable/resetEntityState/handleRejected); **revert via setter eksplisit per entity** (pola `ProfileUpdateBiodataApprovalService`, load-and-set-and-save) — BUKAN refleksi blind, BUKAN bulk JPQL baru (riset 2026-08-12).

| # | Task | File |
|---|------|------|
| W2a | Desain mesin generik (mis. `ProfileUpdateApprovalHandler`): strategi per entity — stamp vs non-stamp, setter revert, soft-delete handling | `services/profil/profilUpdate/` (baru) |
| W2b | Refactor `ProfileUpdateBiodataApprovalService` → mesin (non-stamp, setter revert) | `services/profil/profilUpdate/` |
| W2c | Refactor `ProfileUpdateKeluargaApprovalService` → mesin (non-stamp, pertahankan `rollbackPrevVersion` existing) | `services/profil/profilUpdate/` |
| W2d | Refactor `ProfileUpdatePendidikanApprovalService` → mesin (stamp + ADR-0035 guard `disetujui`) | `services/profil/profilUpdate/` |
| W2e | Tambah strategi: Keahlian, Pelatihan, PengalamanKerja (stamp), KartuIdentitas (non-stamp) | `services/profil/profilUpdate/` (baru) |
| W2f | Tambah strategi: LampiranProfil (stamp; INSERT-reject → hapus baris + file via `FileUploadUtil`) | `services/profil/profilUpdate/` (baru) |
| W2g | `ProfileUpdateService.approval()` — switch lengkap 8 tipe via mesin | `services/profil/profilUpdate/ProfileUpdateService.java` |

**Acceptance W2:** `approval()` punya case untuk 8 tipe; approve/reject benar-benar mengubah entity (tidak hanya stamp antrian); `wc -l` tiap file hasil ≤ 120 (CODING_RULES §4); `./gradlew build` SUCCESS; test existing (`ProfileUpdatePendidikanApprovalServiceTest`) tetap hijau.

---

## W3 — Detail antrian per-tipe (RevInfoService + routing)

**Sumber:** ADR-0036 §8–9.

| # | Task | File |
|---|------|------|
| W3a | Tambah 6 method `RevInfoService`: `findBiodataRevision`, `findKeahlianRevision`, `findPelatihanRevision`, `findPengalamanKerjaRevision`, `findKartuIdentitasRevision`, `findLampiranRevision` (pola `findKeluargaRevision`/`findPendidikan`) | `services/revInfo/RevInfoService.java` |
| W3b | Fix `ProfileUpdateQueryService.findById`: switch `tableName` → delegasi method yang benar (HAPUS fallback `else → findPendidikan`) | `services/profil/profilUpdate/ProfileUpdateQueryService.java` |
| W3c | DTO detail per tipe (mis. `ProfilUpdateDetail<T>` + response) bila belum ada | `dto/profil/profileUpdate/` |

**Acceptance W3:** `GET /profil/profil-update/{id}` untuk BIODATA (revId=NIK) tidak lagi `NumberFormatException`; semua 8 tipe menampilkan snapshot benar.

---

## W4 — LampiranProfil ke antrian + arahkan acceptLampiran

**Sumber:** ADR-0036 §6. **Blokir: W2/W3 selesai.**

| # | Task | File |
|---|------|------|
| W4a | `LampiranProfilCommandService.addLampiran`/`deleteById`: guard `resolver.requiresApproval()` → `profileUpdateService.create(id, INSERT/DELETE, LAMPIRAN)` | `services/profil/lampiranProfil/LampiranProfilCommandService.java` |
| W4b | Arahkan `acceptLampiran()` (service + `POST /profil/lampiran/accept`) → antrian: hapus jalur langsung, ganti approval via `PUT /profil/profil-update/{id}` | `services/.../LampiranProfilCommandService.java`, `controllers/profil/LampiranProfilController.java`, `dto/.../LampiranProfilAcceptRequest.java` |
| W4c | **Koordinasi FE**: cek pemakaian `POST /profil/lampiran/accept` di `kepegawaian-fe` sebelum hapus; siapkan nota breaking change | (koordinasi) |

**Acceptance W4:** lampiran baru = `disetujui` default tetap `true` untuk jalur HRD/ADMIN; self-service → PENDING di antrian; tidak ada jalur approval ganda.

---

## W5 — Cleanup & konsistensi

- [ ] `grep -rn "SDM" src/main --include='*.java'` → hanya istilah domain wajar (mis. komentar sudah di-update ke HRD/ADMIN)
- [ ] Update test `PendidikanCommandServiceTest` (komentar SDM → HRD) bila masih menyebut SDM
- [ ] Tambah unit test mesin generik (approve stamp / reject revert / soft-delete) — pola `ProfileUpdatePendidikanApprovalServiceTest`
- [ ] `./gradlew test` hijau

---

## Ship (tiap fase)

- [ ] `gitnexus_impact` sebelum edit tiap simbol (WAJIB)
- [ ] `gitnexus_detect_changes()` — scope hanya simbol yang diharapkan
- [ ] `git add` (single batch) + commit (pesan rujuk ADR-0036 + epic beads)
- [ ] `bd close kepegawaian-<id>`
- [ ] `bd dolt push` → `git pull --rebase` → `git push` → verify "up to date with origin"

---

## Guardrails

- NEVER edit simbol tanpa `gitnexus_impact` dulu
- NEVER rename/move dengan find-and-replace
- NEVER commit tanpa `gitnexus_detect_changes()`
- beads satu-satunya tracker
- Soft-delete only (`is_deleted`), never hard-delete — kecuali reject-INSERT pada entity yang memang di-hard-delete (pola `repository.deleteById` existing)
- Entity data-holder & repository pure-query dikecualikan dari batas 120 baris
- Stop & tanya manager bila impact analysis HIGH/CRITICAL

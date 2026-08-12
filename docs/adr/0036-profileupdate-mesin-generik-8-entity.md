# ProfileUpdate: mesin generik approval + cakupan 8 entity profil

> **Status:** accepted — keputusan sesi grilling 2026-08-12 (grill-with-docs + domain-modeling), dari pemeriksaan implementasi `ProfileUpdateService` pada modul profil.

## Konteks

Pemeriksaan (2026-08-12) menemukan `ProfileUpdateService` sudah diimplementasikan sebagai *concrete class* (PU-1 ✅: interface single-impl dibuang), read-side sudah JOOQ (PU-2 ✅), dan 3 handler approval (`ProfileUpdateBiodataApprovalService`, `ProfileUpdateKeluargaApprovalService`, `ProfileUpdatePendidikanApprovalService`) + 5 caller `create()`. Namun ditemukan **5 gap** terhadap domain yang didokumentasikan di `docs/context/language-profil.md`:

1. **`KELUARGA` — handler ada tapi tidak pernah dipanggil.** `ProfilKeluargaCommandService.create/update` tidak memanggil `profileUpdateService.create(...)` — tidak ada satu pun entri `EProfileUpdateTable.KELUARGA` yang dibuat.
2. **`KARTU_IDENTITAS` — punya `changedStatus` tapi tidak masuk antrian.** `KartuIdentitasCommandService` set `changedStatus=resolver.requiresApproval()` tapi tidak enqueue → perubahan pegawai selamanya `changedStatus=true` tanpa bisa di-approve. Tidak ada `EProfileUpdateTable.KARTU_IDENTITAS`.
3. **`KEAHLIAN/PELATIHAN/PENGALAMAN_KERJA` — enqueue tapi `approval()` tidak punya case.** Switch di `ProfileUpdateService.approval()` hanya menangani BIODATA/KELUARGA/PENDIDIKAN. Entri 3 tabel ini tetap di-stamp APPROVED/REJECTED oleh `handleApproval()` (status antrian berubah) tapi **entity-nya tidak tersentuh** — `changedStatus` baris data tetap `true` selamanya.
4. **Bug routing detail: `ProfileUpdateQueryService.findById()` merouting semua non-KELUARGA ke `findPendidikan`.** Entri BIODATA (revId = NIK, String) akan masuk `findPendidikan` → `Long.valueOf(NIK)` → `NumberFormatException` / routing salah entity.
5. **`LampiranProfil` — tidak pernah masuk antrian** dan punya jalur approval sendiri (`POST /profil/lampiran/accept` + `acceptLampiran()`), duplikatif dengan antrian ProfileUpdate.

Selain itu, role yang dibebaskan dari approval tidak konsisten dengan seed:

- `ChangedStatusResolver.requiresApproval()` memeriksa **`ROLE_SDM`**, padahal seed `pref_role` (V21) berisi **SYSTEM, ADMIN, USER, HRD, PENGGAJIAN** — tidak ada `SDM`. Tidak ada controller yang memakai `HRD`. Role sebenarnya petugas kepegawaian = **HRD**.

Riset (2026-08-12, researcher-web + researcher-docs) tentang mekanisme revert:

- **Refleksi blind (`BeanUtils.copyProperties`) tidak aman** untuk revert Envers: berisiko menimpa `id`, `@Version` (pemicu `OptimisticLockException`), kolom audit (`createdBy/At`, `updatedBy/At`), dan korup lazy-association/proxy.
- **JPQL bulk update (`rollbackPrevVersion`) mem-bypass** Hibernate lifecycle, `@EntityListeners`, dan **Envers tidak merekam revisi baru** untuk aksi rollback.
- **Pendekatan yang dianjurkan: load-and-set-fields-and-save** — baca revision sebelumnya, set field mutable secara eksplisit, biarkan JPA menangani `@Version` + audit + Envers baru. Ini persis pola `ProfileUpdateBiodataApprovalService` yang sudah dipakai.
- **Soft-delete (`@SQLDelete`/`is_deleted`) butuh perhatian**: `findById` tidak menemukan baris ter-soft-delete (karena `@SQLRestriction`), jadi revert DELETE harus flip `isDeleted=false`.

## Keputusan

1. **Cakupan antrian = 8 entity**: Biodata, ProfilKeluarga, Pendidikan, Keahlian, Pelatihan, PengalamanKerja, KartuIdentitas, LampiranProfil. Tambah `KARTU_IDENTITAS` dan `LAMPIRAN` di `EProfileUpdateTable`.
2. **Role bebas approval = `ADMIN` + `HRD`** (bukan `SDM`). `ChangedStatusResolver` diperbaiki: `ROLE_ADMIN` ATAU `ROLE_HRD` → `requiresApproval()=false`; selain itu `true`. Komentar/test yang menyebut "SDM" diselaraskan ke HRD/ADMIN.
3. **Guard `create()` seragam di semua CommandService**: `changedStatus = resolver.requiresApproval()` dan `profileUpdateService.create(...)` **hanya dipanggil jika `true`** (perbaiki `PengalamanKerjaCommandService` yang tanpa guard dan `BiodataCommandService.patchBiodata` yang hardcode `changedStatus=true`). Pengecualian LampiranProfil (lihat #6) — guard enqueue langsung `resolver.requiresApproval()`.
4. **Semantik approve per-tipe**:
   - **Stamp entity** (punya field `disetujui`+stamp: Pendidikan, Keahlian, Pelatihan, PengalamanKerja, LampiranProfil): approve → `changedStatus=false` + `disetujui=true` + stamp `tanggalDisetujui`/`disetujuiOleh` (pola ADR-0035).
   - **Non-stamp** (Biodata, ProfilKeluarga, KartuIdentitas): approve → `changedStatus=false` saja.
   - **Reject** mengikuti Jenis Aksi: INSERT → hapus baris (lampiran: + hapus file fisik); UPDATE → revert ke revisi Envers sebelumnya; DELETE → aktifkan kembali (`isDeleted=false`, `changedStatus=false`).
5. **Revert = pola load-and-set-fields-and-save** (pola `ProfileUpdateBiodataApprovalService`), bukan refleksi blind dan bukan bulk JPQL baru. `rollbackPrevVersion` existing di `PendidikanRepository`/`ProfilKeluargaRepository` dipertahankan (sudah jalan, dipakai) — entity baru TIDAK menambah bulk JPQL.
6. **LampiranProfil masuk antrian TANPA migrasi `changed_status`**: hanya INSERT/DELETE, tidak ada UPDATE → kolom `changedStatus` tidak punya fungsi (tidak ada "nilai berubah tapi belum disetujui"). Guard enqueue langsung `resolver.requiresApproval()`; status menunggu dibaca dari `disetujui=false` + entri PENDING. Jalur approval lama `POST /profil/lampiran/accept` + `acceptLampiran()` **diarahkan ke antrian ProfileUpdate** (lihat claim-order untuk koordinasi FE).
7. **Struktur handler = satu mesin generik + setter eksplisit per entity**: satu class generik mengelola flow (changeHandler/markAsStable/resetEntityState/handleRejected), revert via setter eksplisit per entity (bukan refleksi). Handler per-entity existing (3 buah) **di-refactor ke mesin ini** — keputusan user: "refactor total ke generik", disesuaikan dengan hasil riset (bukan refleksi blind).
8. **Routing detail `ProfileUpdateQueryService.findById` diperbaiki**: switch `tableName` → delegasi ke method `RevInfoService` yang sesuai. Tambah 6 method baru di `RevInfoService` (biodata, keahlian, pelatihan, pengalamanKerja, kartuIdentitas, lampiran) — pola sama `findKeluargaRevision`/`findPendidikan`.
9. **Detail per-tipe** (GET `/profil/profil-update/{id}`): FE melihat snapshot Envers untuk SEMUA tipe, bukan hanya KELUARGA/PENDIDIKAN.

## Considered Options

- **Cakupan 5 entity (status quo)** (ditolak): 3 tabel kecil di-enqueue tanpa handler → approve/reject palsu; `changedStatus` selamanya `true`. Melanggar glossary.
- **Lampiran tetap pakai jalur `accept` sendiri** (ditolak): dua mekanisme approval untuk objek yang sama; tidak konsisten dengan glossary "semua perubahan self-service melewati antrian".
- **Tambah kolom `changed_status` di LampiranProfil** (ditolak): lampiran hanya INSERT/DELETE — kolom tidak punya fungsi; menyeret migrasi tabel + `*_aud` tanpa nilai tambah.
- **Revert refleksi blind (BeanUtils)** (ditolak setelah riset): menimpa `@Version`/`id`/kolom audit, `OptimisticLockException`, korup relasi.
- **Revert bulk JPQL untuk entity baru** (ditolak setelah riset): bypass Envers + lifecycle → rollback tidak terekam sebagai revisi baru.
- **Handler per-entity (5 class baru)** (ditolak): banyak boilerplate setter identik; user memilih "refactor total ke generik".
- **`ChangedStatusResolver` tetap cek `ROLE_SDM`** (ditolak): role tidak ada di seed; SEMUA user (termasuk ADMIN/HRD) akan masuk antrian → kontradiksi dengan jalur admin FE.

## Consequences

- **BE — implementasi butuh**: `EProfileUpdateTable` +2 nilai; `ChangedStatusResolver` role fix; guard seragam di 5 CommandService (2 diperbaiki, 3 diverifikasi); mesin generik approval + refactor 3 handler; 6 method `RevInfoService`; routing `ProfileUpdateQueryService.findById`; arahkan `acceptLampiran` ke antrian (hapus/redirect endpoint `POST /profil/lampiran/accept`); perbaiki `PengalamanKerjaCommandService` guard.
- **FE (`kepegawaian-fe`)**: `POST /profil/lampiran/accept` diarahkan/hapus → perlu koordinasi (checklist di claim-order). Detail antrian kini menampilkan snapshot semua tipe. Regenerate tipe (`node docs/api/extract-types.js`) bila DTO berubah.
- **Keamanan**: `changedStatus` tidak lagi bisa "nyangkut" di `true` tanpa keputusan; jalur admin/HRD tetap bebas approval (kontrak FE tidak berubah).
- **Docs**: glossary `language-profil.md` diperbarui (role HRD/ADMIN, cakupan 8 entity, pengecualian LampiranProfil).

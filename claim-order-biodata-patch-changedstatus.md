# Claim Order — Biodata PATCH: changedStatus + ProfileUpdateService

**Issue:** `kepegawaian-7xt`
**Branch:** `rewrite/master-cqrs`

## Ringkasan

PATCH `/profil/biodata/{id}` diubah jadi **self-service endpoint**: setiap pegawai (termasuk ADMIN/SDM) yang edit biodata sendiri → `changedStatus=true` → masuk approval queue (`ProfileUpdateService.create`). PUT tetap ADMIN-only untuk koreksi data orang lain.

## Checklist

| # | Task | File | Status |
|---|------|------|--------|
| 1 | Entity: tambah field `changedStatus` di `Biodata.java` | ✅ |
| 2 | Migration: V26 ALTER TABLE biodata + biodata_aud | ✅ |
| 3 | CommandService: set changedStatus=true di patchBiodata() | ✅ |
| 4 | Controller: buka auth PATCH (tanpa ROLE_ADMIN) + ownership validation | ✅ |
| 5 | Test: update `ChangedStatusPlacementTest` include Biodata | ✅ |
| 6 | Compile Java — verify | ✅ |
| 7 | Test: ChangedStatusPlacementTest | ✅ |
| 8 | Code review | ✅ |
| 9 | Regen jOOQ (skip — not needed for existing queries) | ⬜ |
| 10 | Commit + push | ⬜ |
| 11 | **kepegawaian-dq9**: integrasi ProfileUpdateService.create() di patchBiodata() + revId fix + BIODATA approval handler | ⬜ |

## Dependencies

- **kepegawaian-7xt** (this) — blocks → **kepegawaian-dq9** (Biodata: integrasi ProfileUpdateService)

## Referensi

- [ADR-0018](docs/adr/0018-changedstatus-server-resolved-by-role.md) — changedStatus server-resolved
- [Context Profil](docs/context/language-profil.md) — domain glossary, Status Berubah, Pengajuan Perubahan
- [Relationships](docs/context/relationships.md) — arah dependency profil → updateProfile

## Notes

- **ProfileUpdateService.create() deferred** → **kepegawaian-dq9**: `ProfileUpdate.revId` is `Long` but `Biodata.pk` is `String` (NIK). Integration requires:
  1. Fix revId type or add overloaded create(String, ...)
  2. Build BIODATA approval handler (similar to PENDIDIKAN)
  3. Registration injection BiodataCommandService + panggil profileUpdateService.create() di patchBiodata()

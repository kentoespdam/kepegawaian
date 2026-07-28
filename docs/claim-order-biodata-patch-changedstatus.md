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
| 11 | **kepegawaian-dq9**: integrasi ProfileUpdateService.create() di patchBiodata() + revId fix + BIODATA approval handler | ✅ |

## Dependencies

- **kepegawaian-7xt** (this) — blocks → **kepegawaian-dq9** (Biodata: integrasi ProfileUpdateService)

## Referensi

- [ADR-0018](docs/adr/0018-changedstatus-server-resolved-by-role.md) — changedStatus server-resolved
- [Context Profil](docs/context/language-profil.md) — domain glossary, Status Berubah, Pengajuan Perubahan
- [Relationships](docs/context/relationships.md) — arah dependency profil → updateProfile

## Kepegawaian-dq9 — Final Design

**Keputusan (grill 2026-07-28):** revId diubah dari `Long` ke `String` di entity `ProfileUpdate`.

### Checklist

| # | Task | File | Status |
|---|------|------|--------|
| 1 | Migration: ALTER TABLE ProfilUpdate MODIFY rev_id VARCHAR(255) | `V27__alter_profil_update_rev_id_to_string.sql` | ✅ (user) |
| 2 | Entity: ProfileUpdate.revId Long → String | `entities/profil/ProfileUpdate.java` | ✅ |
| 3 | Interface: ProfileUpdateApprovalService — semua method Long → String | `profilUpdate/ProfileUpdateApprovalService.java` | ✅ |
| 4 | Service: ProfileUpdateService.create() — Long → String | `profilUpdate/ProfileUpdateService.java` | ✅ |
| 5 | Service: ProfileUpdateService.approval() — + case BIODATA | `profilUpdate/ProfileUpdateService.java` | ✅ |
| 6 | Handler: PendidikanApprovalService — konversi Long.valueOf(revId) | `profilUpdate/ProfileUpdatePendidikanApprovalService.java` | ✅ |
| 7 | Handler: KeluargaApprovalService — konversi Long.valueOf(revId) | `profilUpdate/ProfileUpdateKeluargaApprovalService.java` | ✅ |
| 8 | Handler: BIODATA — ProfileUpdateBiodataApprovalService (class baru) | `profilUpdate/ProfileUpdateBiodataApprovalService.java` | ✅ |
| 9 | RevInfoService: overload findLatestRevision(entityClass, String entityId) | `revInfo/RevInfoService.java` | ✅ |
| 10 | RevInfoService: findKeluargaRevision()+findPendidikan() → Long.valueOf(pu.getRevId()) | `revInfo/RevInfoService.java` | ✅ |
| 11 | Callers: PendidikanCommandService dll → String.valueOf(save.getId()) | 5 files | ✅ |
| 12 | BiodataCommandService: + profileUpdateService.create(nik, UPDATE, BIODATA) | `biodata/BiodataCommandService.java` | ✅ |
| 13 | Compile Java | `./gradlew compileJava` | ✅ |
| 14 | Commit + push | | ⬜ |

### Migration

```sql
ALTER TABLE ProfilUpdate MODIFY COLUMN rev_id VARCHAR(255);
```

### Approval Flow (tidak bermasalah)

| Langkah | revId (String) | Dipakai sebagai |
|---------|----------------|----------------|
| `profileUpdate.getRevId()` → Pendidikan | `"42"` | `Long.valueOf("42")` → `findById(42L)` ✅ |
| `profileUpdate.getRevId()` → Biodata | NIK | Langsung `findById(NIK)` ✅ |
| `RevInfoService.findLatestRevision()` | Object | `AuditEntity.id().eq(object)` — Envers support semua tipe PK ✅ |

### Referensi

- ProfileUpdatePendidikanApprovalService.java — pattern handler
- ADR-0018 — changedStatus server-resolved

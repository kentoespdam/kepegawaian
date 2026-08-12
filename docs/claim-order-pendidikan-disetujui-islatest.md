# Claim Order — Pendidikan: `disetujui` role-conditional + guard `isLatest`

**Issues:**
- `kepegawaian-yu3` (P1) — expose `disetujui` di query + auto-set role-conditional + stamp
- `kepegawaian-xx6` (P1) — migration V29 (backfill + guard generated column)
- `kepegawaian-c74` (P2) — tests (blocked oleh yu3 & xx6)

**Branch:** `rewrite/master-cqrs`
**Keputusan:** [ADR-0035](docs/adr/0035-pendidikan-disetujui-role-conditional-guard-islatest.md) · [BE-REQUIREMENT](docs/BE-REQUIREMENT-pendukung-pendidikan.md)

## Ringkasan

FE butuh field `disetujui` di response `PendidikanQuery` untuk badge. Kolom DB **sudah ada sejak V1**
(`disetujui`, `disetujui_oleh`, `tanggal_disetujui`, `tanggal_pengajuan`) tapi entity JPA tidak memetakannya.
Keputusan grill: `disetujui` **role-conditional** (SDM → true + stamp; non-SDM → false sampai approve di
antrian), `tanggalPengajuan` create+update, backfill baris stabil, dan guard DB `isLatest`
(generated column `is_latest_biodata` + UNIQUE). Normalisasi aplikasi + sinkron
`biodata.pendidikanTerakhirId` **sudah ada** di `handleUpdateIsLatest()`.

## Checklist — kepegawaian-yu3 (expose + auto-set + stamp)

| # | Task | File | Status |
|---|------|------|--------|
| 1 | Entity: tambah `disetujui` (Boolean), `tanggalPengajuan`/`tanggalDisetujui` (LocalDateTime + `@JsonFormat` pola Keahlian), `disetujuiOleh` (String) | `entities/profil/Pendidikan.java` | ✅ |
| 2 | DTO: tambah 4 field di `PendidikanQuery` (posisi persis `KeahlianQuery`) | `dto/profil/pendidikan/PendidikanQuery.java` | ✅ |
| 3 | Selects: tambah `DISETUJUI`, `TANGGAL_PENGAJUAN`, `TANGGAL_DISETUJUI`, `DISETUJUI_OLEH` | `repositories/profil/jooq/PendidikanSelects.java` | ✅ |
| 4 | Mapper: map 4 field (Byte→Boolean null-safe untuk `disetujui`) | `PendidikanJooqMapper.java`, `PendidikanMultisetJooqMapper.java` | ✅ |
| 5 | CommandService create/update: auto-set role-conditional + stamp `tanggalDisetujui`/`disetujuiOleh=$id`; `tanggalPengajuan=now()` create+update; `requiresApproval` dihitung sekali | `services/profil/pendidikan/PendidikanCommandService.java` | ✅ |
| 6 | Seed: `seedFromBiodata` → `disetujui=true` | `PendidikanCommandService.java` | ✅ |
| 7 | Approval handler: `markAsStable` → `disetujui=true` + stamp approver; reject (UPDATE) restore kolom approval dari revisi sebelumnya | `profilUpdate/ProfileUpdatePendidikanApprovalService.java` + `PendidikanRepository.rollbackPrevVersion` (+4 param) | ✅ |
| 8 | Compile Java + test suite penuh | `./gradlew clean compileJava` + `./gradlew test` | ✅ |
| 9 | OpenAPI: field baru otomatis via springdoc | — | ✅ (otomatis) |

## Checklist — kepegawaian-xx6 (migration V29 + guard)

| # | Task | File | Status |
|---|------|------|--------|
| 1 | V29: backfill `disetujui=1`, `tanggal_disetujui=COALESCE(created_at, updated_at)`, `disetujui_oleh=created_by` WHERE `is_deleted=0 AND changed_status=0`; baris pending tetap `0` | `db/migration/V29__pendidikan_disetujui_backfill_guard_islatest.sql` | ✅ |
| 2 | V29: dedup `is_latest` (sisakan id terbesar per biodata) sebelum guard | `db/migration/V29__*.sql` | ✅ |
| 3 | V29: generated column `is_latest_biodata` = `IF(is_latest=1 AND is_deleted=0, biodata_id, NULL)` + `UNIQUE KEY uk_ddk_islatest_biodata` | `db/migration/V29__*.sql` | ✅ |
| 4 | Clear `is_latest` saat delete / cakup baris deleted di `updateIsLatest` | — | ✅ tidak perlu: guard memuat `AND is_deleted=0` (mayat record → NULL, tak memblokir) |
| 5 | Regenerasi jOOQ | — | ⬜ skip: tidak ada kode yang mereferensikan `is_latest_biodata` (queries enumerate kolom) |
| 6 | Compile Java + test suite penuh | `./gradlew clean compileJava` + `./gradlew test` | ✅ |

## Checklist — kepegawaian-c74 (tests)

| # | Task | File | Status |
|---|------|------|--------|
| 1 | Unit: mapper jOOQ memetakan `disetujui` (Byte→Boolean null-safe, timestamp, oleh) | `test/.../PendidikanJooqMapperTest.java` | ✅ (2 test baru) |
| 2 | Unit (Mockito): create oleh SDM → `disetujui=true` + stamp; non-SDM → `false` + queue | `test/.../PendidikanCommandServiceTest.java` | ✅ (2 test) |
| 3 | Unit (Mockito): approve → `disetujui=true` + stamp approver; reject (UPDATE) → restore kolom approval | `test/.../ProfileUpdatePendidikanApprovalServiceTest.java` | ✅ (2 test) |
| 4 | IT guard level-DB (dua `true` ditolak DB) | — | ⬜ skip: guard deklaratif (generated column + UNIQUE); verifikasi saat migration diterapkan di lingkungan |
| 5 | IT delete record terakhir | — | ⬜ skip: guard memuat `is_deleted=0`, mayat tak memblokir (dijamin desain, lihat item xx6-4) |

## Dependencies

- `kepegawaian-c74` (tests) **blocks ←** `kepegawaian-yu3` + `kepegawaian-xx6`
- `kepegawaian-1dp` (P2, tech debt — Keahlian/PengalamanKerja/Pelatihan/LampiranSk pola approval setengah mati): independen, boleh dikerjakan kapan saja

## Referensi

- [ADR-0035](docs/adr/0035-pendidikan-disetujui-role-conditional-guard-islatest.md) — keputusan lengkap + considered options
- [BE-REQUIREMENT pendukung-pendidikan](docs/BE-REQUIREMENT-pendukung-pendidikan.md) — permintaan FE + Jawaban BE
- [Context Profil](docs/context/language-profil.md) — glossary: Status Disetujui (`disetujui`) vs Status Berubah (`changedStatus`)
- [Relationships](docs/context/relationships.md) — sinkron `pendidikanTerakhir` via bulk update (tanpa revisi Envers palsu)
- Pola entity/DTO: `Keahlian.java` / `KeahlianQuery.java` (shape); perilaku **tidak** ditiru (PUT=false + kolom mati — tech debt `kepegawaian-1dp`)

# Organisasi Code Migration Checklist (ADR 0060)

Epic Issue: `kepegawaian-ey6o`

## Workflow Protocol per Task
Untuk setiap task dalam migrasi ini, wajib mengikuti protokol berikut:
1. **Klaim Task**: `bd update <ISSUE_ID> --claim`
2. **Impact Analysis**: Jalankan `gitnexus impact` pada simbol/service yang diubah.
3. **Implementasi & Test**: Modifikasi kode/seed dan jalankan `./gradlew test`.
4. **Tutup Task**: `bd close <ISSUE_ID>`

---

## Claim Order & Target Files Inventory

### 1. Task 1: Validasi kode organisasi pada DTO Post/Put Request [x]
* **Issue ID**: `kepegawaian-9g7z`
* **Target Files**:
  - `src/main/java/id/perumdamts/kepegawaian/dto/master/organisasi/OrganisasiPostRequest.java`
  - `src/main/java/id/perumdamts/kepegawaian/dto/master/organisasi/OrganisasiPutRequest.java`
* **Acceptance Criteria**:
  - Tambahkan `@NotBlank(message = "Kode tidak boleh kosong")` dan `@Size(max = 64, message = "Kode maksimal 64 karakter")` pada field `kode`.

### 2. Task 2: Penyelarasan nilai kode di V3__seed_organisasi.sql dengan org_code legacy [x]
* **Issue ID**: `kepegawaian-qu6h`
* **Target Files**:
  - `src/main/resources/db/migration/V3__seed_organisasi.sql`
* **Acceptance Criteria**:
  - Perbarui nilai kolom `kode` pada setiap baris INSERT agar sesuai dengan `org_code` dari legacy `docs/organization.sql`.

### 3. Task 3: Verifikasi pengujian Organisasi (Unit & Integration Tests) [x]
* **Issue ID**: `kepegawaian-act4`
* **Target Files**:
  - `src/test/java/id/perumdamts/kepegawaian/services/master/organisasi/OrganisasiCommandServiceTest.java`
* **Acceptance Criteria**:
  - Jalankan test suite modul organisasi dan pastikan seluruh skenario validasi, create, update, dan revive lulus tanpa error.

### 4. Task 4: Quality gates, GitNexus detect_changes, dan finalisasi checklist [x]
* **Issue ID**: `kepegawaian-33rn`
* **Target Actions**:
  - Jalankan `gitnexus detect_changes()` untuk memverifikasi scope perubahan.
  - Jalankan `./gradlew build` dan pastikan hijau (zero error).
  - Update status checklist dan `bd dolt push` && `git push`.

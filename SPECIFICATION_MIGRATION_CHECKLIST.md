# SpecificationBuilder Migration Checklist (CQRS-lite jOOQ & JPA Derived Query)

Epic Issue: `kepegawaian-jsld`

## Workflow Protocol per Task
Untuk setiap task dalam fase migrasi, wajib mengikuti protokol berikut:
1. **Klaim Task**: `bd update <ISSUE_ID> --claim`
2. **Impact Analysis**: Jalankan `gitnexus impact` pada simbol/service yang diubah.
3. **Implementasi & Test**: Modifikasi kode (CQRS-lite jOOQ / JPA Derived Query) dan jalankan `./gradlew test`.
4. **Tutup Task**: `bd close <ISSUE_ID>`

---

## Claim Order & Target Files Inventory

### 1. Fase 1: Migrasi Sisa Query Side (PrefRole & UserService) ke jOOQ [x]
* **Issue ID**: `kepegawaian-jsld.1`
* **Target Files**:
  - `src/main/java/com/pdam/kepegawaian/service/PrefRoleQueryService.java`
  - `src/main/java/com/pdam/kepegawaian/service/UserServiceQueryService.java`
  - Terkait jOOQ repository / query repository pendukung.

### 2. Fase 2.1: Refactor Validasi Command Side - Modul Master Data (14 Services) [x]
* **Issue ID**: `kepegawaian-jsld.2`
* **Target Files** (14 Services Command):
  - `AgamaCommandService.java`
  - `BankCommandService.java`
  - `BagianCommandService.java`
  - `BidangCommandService.java`
  - `CabangCommandService.java`
  - `GolonganCommandService.java`
  - `JabatanCommandService.java`
  - `JenisPegawaiCommandService.java`
  - `KantorCommandService.java`
  - `PangkatCommandService.java`
  - `PendidikanCommandService.java`
  - `StatusPegawaiCommandService.java`
  - `UnitCommandService.java`
  - `JamKerjaCommandService.java` (atau sejenisnya di master data)

### 3. Fase 2.2: Refactor Validasi Command Side - Modul Profil & Biodata (8 Services) [x]
* **Issue ID**: `kepegawaian-jsld.3`
* **Target Files** (8 Services Command):
  - `PegawaiCommandService.java`
  - `KeluargaCommandService.java`
  - `PendidikanPegawaiCommandService.java`
  - `PelatihanCommandService.java`
  - `SertifikasiCommandService.java`
  - `PenghargaanCommandService.java`
  - `HukumanCommandService.java`
  - `OrganisasiCommandService.java`
  - `LampiranProfilCommandService.java`

### 4. Fase 2.3: Refactor Validasi Command Side - Modul Riwayat Kepegawaian (6 Services) [x]
* **Issue ID**: `kepegawaian-jsld.4`
* **Target Files** (6 Services Command):
  - `LampiranSkCommandService.java`
  - `RiwayatMutasiCommandService.java`
  - `RiwayatKontrakCommandService.java`
  - `RiwayatSkCommandService.java`
  - `RiwayatSpCommandService.java`
  - `RiwayatTerminasiCommandService.java`

### 5. Fase 2.4: Refactor Validasi Command Side - Modul Cuti (2 Services) [x]
* **Issue ID**: `kepegawaian-jsld.5`
* **Target Files** (2 Services Command):
  - `CutiCommandService.java`
  - `JenisCutiCommandService.java`

### 6. Fase 2.5: Refactor Validasi Command Side - Modul Penggajian (9 Services) [x]
* **Issue ID**: `kepegawaian-jsld.6`
* **Target Files** (9 Services Command):
  - `GajiPokokCommandService.java`
  - `TunjanganCommandService.java`
  - `PotonganCommandService.java`
  - `KomponenGajiCommandService.java`
  - `SlipGajiCommandService.java`
  - `GajiBerkalaCommandService.java`
  - `TunjanganPegawaiCommandService.java`
  - `PotonganPegawaiCommandService.java`
  - `PayrollCommandService.java`

### 7. Fase 3: Lepas JpaSpecificationExecutor dari 52 Repository & Hapus SpecificationBuilder [x]
* **Issue ID**: `kepegawaian-jsld.7`
* **Target Files**:
  - 52 Spring Data JPA Repository interfaces (menghapus extends `JpaSpecificationExecutor<T>`).
  - `src/main/java/com/pdam/kepegawaian/repository/SpecificationBuilder.java` (Hapus total).

### 8. Fase 4: Quality Gate, Test Suite Verification & Update Graphify
* **Issue ID**: `kepegawaian-jsld.8`
* **Target Actions**:
  - Jalankan `./gradlew clean test`
  - Verifikasi semua unit dan integration tests pass.
  - Jalankan `graphify . --update` untuk memperbarui knowledge graph.

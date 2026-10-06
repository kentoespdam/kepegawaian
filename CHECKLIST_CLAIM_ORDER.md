# Checklist — Endpoint GET /penggajian/batch/master/self (kepegawaian-d8zo)

- [x] **1. DTO & Query Request**
  - Buat atau sesuaikan parameter request (`GajiBatchMasterSelfQuery` tanpa validasi `@NotBlank` wajib pada `periode`).
- [x] **2. Repository / Query Repository**
  - Tambahkan method `findHistoryByPegawaiId` untuk mengambil rekapitulasi gaji per pegawai berdasarkan ID dengan filter status `FINISHED`.
- [x] **3. QueryService**
  - Implementasikan method `findHistoryByPegawaiId` di `GajiBatchMasterQueryService`.
- [x] **4. Controller Endpoint**
  - Tambahkan endpoint `GET /penggajian/batch/master/self` di `GajiBatchMasterController` dengan anotasi `@PreAuthorize("isAuthenticated()")` dan resolusi principal `AppwriteUser`.
- [x] **5. Testing & Verification**
  - Jalankan `./gradlew test` dan verifikasi perubahan berhasil.

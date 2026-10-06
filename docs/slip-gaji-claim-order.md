# Claim Order — Fitur Download Slip Gaji PDF Pegawai

Urutan claim `bd` untuk implementasi endpoint download slip gaji berformat PDF (`GET /penggajian/batch/master/{id}/slip-gaji`) berbasis OpenPDF.
Referensi: [ADR-0058](adr/0058-openpdf-generator-slip-gaji.md) dan [Domain Glossary Penggajian](context/language-penggajian.md).
Hormati dependency. Claim satu-per-satu: `bd update <id> --claim` → kerjakan → `bd close <id>`.

**Aturan tetap:**
- Run impact analysis sebelum modifikasi kode.
- Target ukuran file modular: 150–250 LOC (Hard ceiling max 300 LOC).
- CQRS-lite: JPA write / jOOQ DSLContext read (Java record DTO).
- Run `detect_changes` dan `./gradlew test` sebelum commit.

---

## Epic
- **kepegawaian-f2s5** (P2) — Fitur Download Slip Gaji PDF (Perorangan) Modul Penggajian

---

## Fase 1 — Dependensi & Konfigurasi (Foundation)
- [x] **kepegawaian-wpdt** (P2) — Dependensi OpenPDF, aset logo Tirta Satria, dan konfigurasi kop surat
  - Tambah `com.github.librepdf:openpdf:2.0.3` ke `build.gradle.kts`.
  - Ekstrak/simpan logo Tirta Satria ke `src/main/resources/images/logo-tirta-satria.png`.
  - Konfigurasi `app.kepegawaian.slip-gaji.kop` di `application.yaml` dan buat `SlipGajiProperties`.

## Fase 2 — CQRS Query DTO & Data Retrieval
- [x] **kepegawaian-blfc** (P2) — DTO Record dan Query Service data Slip Gaji (jOOQ) *(blocked-by kepegawaian-wpdt)*
  - Buat record DTO `SlipGajiDto` & `SlipGajiKomponenItemDto`.
  - Implementasi query data di `GajiBatchMasterQueryService` / `GajiBatchMasterQueryRepository` via jOOQ.
  - Ambil metadata pegawai, validasi status batch `FINISHED`, dan grouping komponen 4 kuadran (Penerimaan, Potongan, Penerimaan Tambahan, Potongan Tambahan `ADD_...`).

## Fase 3 — PDF Generator Engine
- [x] **kepegawaian-e2cz** (P2) — Implementasi SlipGajiPdfGenerator berbasis OpenPDF *(blocked-by kepegawaian-wpdt, kepegawaian-blfc)*
  - Buat component `SlipGajiPdfGenerator` (target ≤250 LOC).
  - Susun kop surat + logo, judul slip [Bulan YYYY], info pegawai (Nama, NIPAM, Jabatan, Golongan/Pangkat, Bank `- - -`).
  - Render Kotak 1: Penerimaan (+), Potongan (-), Penerimaan - Potongan, Pembulatan, Sub Total.
  - Render Kotak 2: Penerimaan Tambahan, Potongan Tambahan (`ADD_...`).
  - Render Total Dibayarkan dengan pemformatan rupiah `Rp. X,XXX,XXX`.

## Fase 4 — Controller Endpoint & Security Guard
- [x] **kepegawaian-crz6** (P2) — Endpoint Controller `GET /penggajian/batch/master/{id}/slip-gaji` & Security Guard *(blocked-by kepegawaian-blfc, kepegawaian-e2cz)*
  - Tambah endpoint di `GajiBatchMasterController`.
  - Terapkan guard kepemilikan sesi (pegawai hanya bisa unduh slip miliknya via ID/NIPAM sesi) atau role Admin/HRD / permission `PENGGAJIAN_SLIP_READ`.
  - Terapkan guard status batch: tolak jika status belum `FINISHED`.
  - Return `ResponseEntity<Resource>` dengan `Content-Type: application/pdf` dan `Content-Disposition: inline; filename="slip-gaji-{nipam}-{periode}.pdf"`.

## Fase 5 — Verification & Tests
- [x] **kepegawaian-sngh** (P3) — Unit & Integration Tests untuk SlipGajiPdfGenerator dan Endpoint Controller *(blocked-by kepegawaian-crz6)*
  - Unit test `SlipGajiPdfGeneratorTest` (validasi non-null byte stream PDF).
  - Controller test `GajiBatchMasterControllerSlipGajiTest` (guard otorisasi, status FINISHED, content-disposition).
  - Verifikasi `./gradlew test` pass tanpa error.

# Form Mutasi — Claim Order & Checklist

**Docs referensi:** `docs/BE-REQUIREMENT-form-mutasi.md`
**Issues terkait:** `kepegawaian-nil`, `kepegawaian-qly`

---

## P1 — Blocking FE (wajib dikerjakan)

### 1. `form-mutasi: endpoint GET /pegawai/{id}/mutasi-context` (`kepegawaian-nil`)

- [x] 1.1 Buat DTO `PegawaiResponseMutasiContext` di `dto/pegawai/pegawai/`
- [x] 1.2 Buat `PegawaiMutasiContextQueryRepository` di `repositories/pegawai/jooq/`
- [x] 1.3 Tambah method `findMutasiContext()` di `PegawaiQueryService`
- [x] 1.4 Tambah endpoint di `PegawaiController`
- [x] 1.5 Golongan.nama format `"{golongan} - {pangkat}"`
- [x] 1.6 Null-safe untuk field nullable
- [x] 1.7 Build zero error

### 2. `form-mutasi: endpoint GET /master/profesi/jabatan/{id}` (`kepegawaian-qly`)

- [x] 2.1 Tambah method `findByJabatanId()` di `ProfesiQueryRepository`
- [x] 2.2 Tambah method `findByJabatanId()` di `ProfesiQueryService`
- [x] 2.3 Tambah endpoint di `ProfesiController`
- [x] 2.4 `ListResult<ProfesiListResponse>` — tidak paged
- [x] 2.5 Urut ASC berdasarkan `nama`
- [x] 2.6 Jabatan tanpa profesi → `data: []`
- [x] 2.7 Build zero error

---

## P2 — Konfirmasi

### 3. Konfirmasi snapshot `*LamaId` & `masaKerja`

- [ ] 3.1 Jawab #3: BE snapshot SELALU dari master pegawai saat simpan RiwayatMutasi
- [ ] 3.2 Jawab #4: Ya, `masaKerja` == `mkgTahun`
- [ ] 3.3 Update docs/TODO.md dengan jawaban
- [ ] 3.4 Kabari FE

---

## Finalisasi

- [x] `./gradlew build` — zero error
- [x] Close issue `kepegawaian-nil`
- [x] Close issue `kepegawaian-qly`
- [x] Commit & push

---

## Catatan

- **PegawaiResponseMutasiContext** memakai `RefMiniResponse` yang sudah ada — tidak perlu tipe baru untuk referensi.
- **ProfesiListResponse** juga sudah ada — reuse.
- Pola endpoint mengikuti `JabatanController.findByOrganisasiId()`.

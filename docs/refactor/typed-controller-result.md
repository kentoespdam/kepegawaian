# Typed Controller/Result Consistency — Claim Order & Checklist

> Epic: **kepegawaian-51j** · Pattern: [`docs/master-query-optimization-pattern.md`](../master-query-optimization-pattern.md)
> Gold standard: `ProfesiController` (controller) + `PegawaiCommandService` (service)
> ADR terkait: [0013](../adr/0013-symmetric-apiresponse-error-envelope.md) (envelope + buang `Errors`),
> [0014](../adr/0014-get-by-id-missing-row-returns-404.md) (missing row → 404, bukan `FAILED`),
> [0031](../adr/0031-batch-endpoint-returns-success-string.md) (batch → `"{n} success"`, workflow → `"success"`).
> **Kerja ini menegakkan ADR-ADR tsb, bukan keputusan baru.**

Menegakkan pola typed result di **seluruh** controller. Empat kriteria digabung
per-domain supaya **1 file = 1 owner** — tak ada dua issue menyentuh file yang sama.

## 4 Kriteria

| # | Masalah | Aturan target |
|---|---------|---------------|
| **(a)** | `ResponseEntity<?>` (wildcard, 45 controller) | Typed penuh: `ResponseEntity<PageResult<...>>`, `ResponseEntity<SavedResult<Long>>`, dst. |
| **(b)** | `SavedResult<Object>` double-cast (6 controller) + `SavedStatus<?>` (50 service) | **DEEP CLEAN** — lihat §Deep Clean di bawah |
| **(c)** | Param `Errors` (11 controller) | Buang. Validasi via `@Valid` + `GlobalExceptionHandler` saja. |
| **(d)** | `@Valid` hilang (~6 endpoint) | Tambah `@Valid` di body/query param |

## Deep Clean — aturan service layer (kriteria b)

`SavedStatus<?>` saat ini dipakai sebagai *Result-monad murahan* — status error
diselipkan ke slot `data`. Ini menduplikasi `GlobalExceptionHandler` (persis
anti-pattern `Errors` di kriteria c). Aturan:

1. **`SUCCESS` + `entity.getId()`** → return `SavedStatus<Long>`.
2. **`SUCCESS` + string pesan** ("PhDP Saved", "Data Saved!") pada create/update
   yang punya entity → ubah jadi `SavedStatus<Long>` bawa `getId()`.
   (Pesan sukses sudah di-set `SavedResult`; string di `data` redundan.)
3. **Batch / workflow** → return `SavedStatus<String>` (ADR-0031):
   - **Batch save** (saveBatch, uploadPotonganTambahan) — punya jumlah row →
     value `"{n} success"`, mis. `"5 success"`. `n` = `requests.size()` / jumlah row ditulis.
   - **Workflow transition** (reprocess, verify1/2, accept) — tak ada row count natural →
     value literal `"success"` (jangan mengarang angka).
4. **`FAILED, "…not found"` / "Unknown …"** → hapus, ganti `throw new NotFoundException(...)`.
5. **`DUPLICATE, "… sudah ada"`** → hapus, ganti `throw new ConflictException(...)`.
6. **`try { } catch (Exception e) { return FAILED, e.getMessage() }`** (12 file) →
   **buang total**. Biarkan exception naik ke `GlobalExceptionHandler`.
7. Controller berhenti `(ResponseEntity<SavedResult<Object>>)(ResponseEntity<?>) …`
   cast — signature langsung `ResponseEntity<SavedResult<Long>>` atau `<String>`.

> ⚠️ **Breaking-change kecil (master domain):** `ApdController` & `AlatKerjaController`
> saat ini `create/update` pakai `CustomResult.any(...)` → balas **200 SingleResult**.
> Create harus **201 SavedResult**. Perbaiki service → `SavedStatus<Long>`, controller
> → `CustomResult.save()`. Catat 200→201 di deskripsi PR.

## Claim Order

Urut berdasar kepadatan & risiko. Ambil dari atas.

| # | Issue | Domain | Beban | Kenapa urutan ini |
|---|-------|--------|-------|-------------------|
| 1 | `kepegawaian-51j.1` | penggajian | 11 ctrl + 13 svc | Paling banjir + satu-satunya pemegang param `Errors` massal (10). Selesaikan dulu utk kunci pola deep-clean. |
| 2 | `kepegawaian-51j.2` | master | 2 ctrl + 2 svc | Kecil tapi ada bug kontrak 200→201; jadikan contoh bersih lebih awal. |
| 3 | `kepegawaian-51j.6` | pegawai/auth/users/system | 5 ctrl + 3 svc | `PegawaiController.saveBatch` = contoh kanonik `SavedResult<Object>`. |
| 4 | `kepegawaian-51j.3` | profil | 9 ctrl + 3 svc | Deep clean lampiran/keluarga/update. |
| 5 | `kepegawaian-51j.4` | cuti | 4 ctrl + 6 svc | Deep clean + @Valid CutiKuota. |
| 6 | `kepegawaian-51j.5` | kepegawaian+laporan | 14 ctrl | Mayoritas read-only (query), risiko rendah — terakhir. |

Domain independen → **boleh paralel** (tak ada file bentrok). Urutan di atas hanya
prioritas bila dikerjakan serial oleh satu orang.

## Checklist per Domain

### 1 · penggajian (`kepegawaian-51j.1`)

**Controllers** — buang `ResponseEntity<?>` (a) + param `Errors` (c):
- [ ] `GajiParameterSettingController`
- [ ] `GajiPendapatanNonPajakController`
- [ ] `GajiPhdpController`
- [ ] `GajiPotonganTkkController`
- [ ] `DetailDasarGajiController`
- [ ] `GajiBatchMasterProsesController`
- [ ] `DasarGajiController`
- [ ] `GajiBatchMasterController` — batch → `SavedResult<String>` `"{n} success"`
- [ ] `GajiProfilController`
- [ ] `GajiTunjanganController`
- [ ] `GajiKomponenController`

**Services** — deep clean `SavedStatus<?>`:
- [ ] `GajiBatchMasterCommandService` (`uploadPotonganTambahan` → `<String>` `"{n} success"`)
- [ ] `GajiBatchMasterProsesCommandService`
- [ ] `DetailDasarGajiCommandService` (`saveBatch` → `"{n} success"`; buang try/catch)
- [ ] `GajiBatchRootCommandService` (`save` batch → `"{n} success"`)
- [ ] `GajiBatchRootWorkflowCommandService` (reprocess/verify1/verify2/accept → workflow, tetap `"success"`)
- [ ] `GajiPotonganTkkCommandService`
- [ ] `GajiParameterSettingCommandService`
- [ ] `GajiPendapatanNonPajakCommandService`
- [ ] `GajiPhdpCommandService`
- [ ] `GajiTunjanganCommandService`
- [ ] `GajiKomponenCommandService`
- [ ] `GajiProfilCommandService`
- [ ] `DasarGajiCommandService` (`saveBatch` → "success"; buang try/catch)

### 2 · master (`kepegawaian-51j.2`) — + bug 200→201
- [ ] `ApdController` — typed (a) + `@Valid` (d) + `.save()` bukan `.any()`
- [ ] `AlatKerjaController` — typed (a) + `@Valid` (d) + `.save()` bukan `.any()`
- [ ] `ApdCommandService` — `create/update` → `SavedStatus<Long>`
- [ ] `AlatKerjaCommandService` — `create/update` → `SavedStatus<Long>`

### 3 · profil (`kepegawaian-51j.3`)
Controllers (a): `Pelatihan`, `PengalamanKerja`, `Pendidikan`, `ProfilKeluarga`,
`Biodata`, `KartuIdentitas`, `LampiranProfil`, `ProfilUpdate`, `Keahlian`.
- [ ] Semua 9 controller: buang `ResponseEntity<?>`
- [ ] `LampiranProfilController`, `ProfilKeluargaController`, `ProfilUpdateController`: buang `SavedResult<Object>` cast
- [ ] `ProfileUpdateService`, `ProfilKeluargaCommandService`, `LampiranProfilCommandService`: deep clean → `SavedStatus<Long/String>`

### 4 · cuti (`kepegawaian-51j.4`)
- [ ] Controllers (a): `CutiJenis`, `CutiApproval`, `CutiPengajuan`, `CutiKuota`
- [ ] `CutiKuotaController`: `@Valid` (d)
- [ ] Services deep clean: `KlaimCutiCommand`, `ApprovalCutiCommand`, `CutiKuotaCommandService`, `ProcessCutiKuotaService`, `PengajuanCutiCommand`, `CutiJenisCommandService`

### 5 · kepegawaian + laporan (`kepegawaian-51j.5`)
- [ ] kepegawaian (a): `RiwayatSp`, `RiwayatKontrak`, `RiwayatMutasi`, `RiwayatSk`, `LampiranSk`, `RiwayatTerminasi`
- [ ] `LampiranSkController`: buang param `Errors` (c)
- [ ] laporan (a, read-only): `LaporanDnp`, `LaporanDuk`, `LaporanMutasi`, `LaporanKontrak`, `LaporanStatistik`, `LaporanLta`, `LaporanSo`, `LaporanKenaikanBerkala`
- [ ] `LaporanKenaikanBerkalaController`: `@Valid` di 3 endpoint `@ParameterObject` (d)

### 6 · pegawai + auth + users + system (`kepegawaian-51j.6`)
- [ ] `PegawaiController.saveBatch`: buang `SavedResult<Object>` cast → `SavedResult<String>`
- [ ] `PegawaiCommandService.saveBatch`: `SavedStatus<?>` (null) → `SavedStatus<String>` `"{n} success"` (`n` = `requests.size()`)
- [ ] `AuthController` (a) + `AuthService` (2 method deep clean)
- [ ] `UsersController`, `PrefRoleController` (a) + `UserService` (deep clean)

## Definition of Done (tiap issue)

- [ ] Tidak ada `ResponseEntity<?>` tersisa di file domain
- [ ] Tidak ada `SavedResult<Object>` / `@SuppressWarnings("unchecked")` cast
- [ ] Tidak ada param `Errors` di controller
- [ ] Tidak ada `try/catch` yang mengubah exception jadi `FAILED` di service
- [ ] Endpoint create → 201, mutating → `@PreAuthorize("hasRole('ADMIN')")`, body/query → `@Valid`
- [ ] `./gradlew build` hijau
- [ ] File ≤ 120 baris (CODING_RULES §4)
- [ ] `bd close <id>`

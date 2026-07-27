# Claim Order — `GET /profil/biodata/{id}/dashboard`

Issue tracking: **kepegawaian-ws8**

> Endpoint dashboard pegawai di `BiodataController`. Dibuat berdasarkan sesi grilling 2026-07-27.

---

## Konteks & Keputusan Desain

| Aspek | Keputusan |
|---|---|
| Path | `GET /profil/biodata/{id}/dashboard` (`{id}` = NIK) |
| Auth | Semua user terautentikasi (tidak ada ownership check) |
| 404 guard | NIK tidak punya baris di `pegawai` → `NotFoundException` |
| `noTelp` | Alias dari `biodata.telp` |
| `email` | `pegawai.email` (join `biodata.nik = pegawai.biodata_id`) |
| `kodePajak` | `gaji_pendapatan_non_pajak.kode` via `pegawai.gaji_pendapatan_non_pajak_id` (String flat) |
| `tingkat` | `jenjang_pendidikan.nama` |
| `institusi` | `pendidikan.institusi` (FE yang menyesuaikan label) |
| Filter pendidikan | `is_latest = true AND changed_status = false` |
| Query layer | `BiodataDashboardQuery` **BARU** (bukan di `BiodataDetailQuery` yang sudah 98 baris) |

---

## Response Shape

```json
{
  "nik": "...",
  "nama": "...",
  "jenisKelamin": "LAKI_LAKI",
  "tempatLahir": "...",
  "tanggalLahir": "yyyy-MM-dd",
  "agama": "...",
  "statusKawin": "...",
  "alamat": "...",
  "noTelp": "...",
  "email": "...",
  "kodePajak": "...",
  "ibuKandung": "...",
  "detailPendidikanTerakhir": {
    "tingkat": "Sarjana",
    "jurusan": "...",
    "institusi": "...",
    "tahunLulus": 2020
  }
}
```

---

## Checklist Implementasi

### 1. DTO
- [x] Buat `BiodataDashboardResponse` record di `dto/profil/biodata/`
  - Field: `nik`, `nama`, `jenisKelamin`, `tempatLahir`, `tanggalLahir`, `agama`, `statusKawin`, `alamat`, `noTelp`, `email`, `kodePajak`, `ibuKandung`, `detailPendidikanTerakhir`
- [x] Buat nested record `PendidikanDashboard` (inner record)
  - Field: `tingkat`, `jurusan`, `institusi`, `tahunLulus`

### 2. Repository — JOOQ Query
- [x] Buat `BiodataDashboardQuery` di `repositories/profil/jooq/`
- [x] Join: `BIODATA` → `PEGAWAI` (INNER JOIN, on `PEGAWAI.BIODATA_ID = BIODATA.NIK`)
- [x] Join: `PEGAWAI` → `GAJI_PENDAPATAN_NON_PAJAK` (LEFT JOIN, on `PEGAWAI.GAJI_PENDAPATAN_NON_PAJAK_ID = GAJI_PENDAPATAN_NON_PAJAK.ID`)
- [x] Join: `PENDIDIKAN` (LEFT JOIN, on `PENDIDIKAN.BIODATA_ID = BIODATA.NIK` + `IS_LATEST = true` + `CHANGED_STATUS = false`)
- [x] Join: `JENJANG_PENDIDIKAN` (LEFT JOIN, on `PENDIDIKAN.JENJANG_ID = JENJANG_PENDIDIKAN.ID`)
- [x] Return `Optional<BiodataDashboardResponse>`
- [x] Referensi pola: `PegawaiRingkasanQueryRepository`, `BiodataDetailQuery`

### 3. Service
- [x] Tambah `getDashboard(String nik)` di `BiodataQueryService`
- [x] Throw `NotFoundException` jika `Optional` kosong

### 4. Controller
- [x] Tambah endpoint di `BiodataController`:
  ```java
  @GetMapping("/{id}/dashboard")
  public ResponseEntity<SingleResult<BiodataDashboardResponse>> getDashboard(@PathVariable String id) {
      return CustomResult.any(queryService.getDashboard(id));
  }
  ```

### 5. Verifikasi
- [x] `gitnexus_impact` sebelum menyentuh `BiodataController`, `BiodataQueryService`
- [x] `./gradlew compileJava` SUCCESS
- [x] Semua file baru ≤ 120 baris (CODING_RULES)

---

## Referensi File

| File | Peran |
|---|---|
| `controllers/profil/BiodataController.java` | Tambah endpoint baru di sini |
| `services/profil/biodata/BiodataQueryService.java` | Tambah `getDashboard()` |
| `repositories/profil/jooq/BiodataDetailQuery.java` | Pola query JOOQ yang ditiru |
| `repositories/profil/jooq/BiodataDashboardQuery.java` | **File baru** |
| `dto/profil/biodata/BiodataDashboardResponse.java` | **File baru** |
| `repositories/pegawai/jooq/PegawaiRingkasanQueryRepository.java` | Contoh join `kodePajak` |

---

> **WAJIB**: Jalankan `gitnexus_impact` pada `BiodataController` dan `BiodataQueryService`
> **sebelum** menyentuh file tersebut. Laporkan blast radius ke user jika HIGH/CRITICAL.

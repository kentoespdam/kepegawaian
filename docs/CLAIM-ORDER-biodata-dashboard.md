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
| `changedStatus` | `biodata.changed_status` (Boolean — status approval biodata) |
| `tingkat` | `jenjang_pendidikan.nama` |
| `institusi` | `pendidikan.institusi` (FE yang menyesuaikan label) |
| Filter pendidikan | `is_latest = true AND changed_status = false` |
| Query layer | `BiodataDashboardQuery` — **multiset subqueries** isolasi PEGAWAI & PENDIDIKAN dari main query BIODATA (refactor 2026-08-19, sebelumnya flat JOINs menyebabkan fan-out) |

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
  "changedStatus": false,
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
  - Field: `nik`, `nama`, `jenisKelamin`, `tempatLahir`, `tanggalLahir`, `agama`, `statusKawin`, `alamat`, `noTelp`, `email`, `kodePajak`, `ibuKandung`, `detailPendidikanTerakhir`, `changedStatus`
- [x] Buat nested record `PendidikanDashboard` (inner record)
  - Field: `tingkat`, `jurusan`, `institusi`, `tahunLulus`

### 2. Repository — JOOQ Query
- [x] Buat `BiodataDashboardQuery` di `repositories/profil/jooq/`
- [x] **Multiset subqueries** (refactor 2026-08-19): PEGAWAI & PENDIDIKAN diisolasi dari main query BIODATA — mencegah JOIN fan-out (`Cursor returned more than one result`)
- [x] PEGAWAI multiset: correlated on `BIODATA.NIK`, select `EMAIL` + `GAJI_PENDAPATAN_NON_PAJAK.KODE` (LEFT JOIN)
- [x] PENDIDIKAN multiset: correlated on `BIODATA.NIK`, filter `IS_LATEST = 1` + `CHANGED_STATUS = 0` + `IS_DELETED = false`, LEFT JOIN `JENJANG_PENDIDIKAN`
- [x] Main query: `BIODATA` only (selalu 0 atau 1 baris, `fetchOptional()` aman)
- [x] Return `Optional<BiodataDashboardResponse>`
- [x] Referensi pola: `BiodataDetailQuery` (multiset subqueries)

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
| `services/profil/biodata/BiodataQueryService.java` | Tambah `getDashboard()` + ownership check |
| `repositories/profil/jooq/BiodataDetailQuery.java` | Pola multiset subqueries yang ditiru |
| `repositories/profil/jooq/BiodataDashboardQuery.java` | Multiset subqueries — PEGAWAI + PENDIDIKAN |
| `dto/profil/biodata/BiodataDashboardResponse.java` | Response record + `PendidikanDashboard` nested |

---

> **WAJIB**: Jalankan `gitnexus_impact` pada `BiodataController` dan `BiodataQueryService`
> **sebelum** menyentuh file tersebut. Laporkan blast radius ke user jika HIGH/CRITICAL.

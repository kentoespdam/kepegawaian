# FE Contract — Endpoint Write Riwayat Kepegawaian (SK, Mutasi, Terminasi)

Dokumen kontrak untuk **endpoint tulis** modul kepegawaian: `Riwayat SK`, `Riwayat Mutasi`, `Riwayat Terminasi`.
Menyusul refactor DTO dedicated (`RiwayatTerminasiPostRequest`, `RiwayatMutasiPostRequest` tidak lagi mewarisi `RiwayatSkPostRequest`) —
kontrak field di bawah adalah **sumber kebenaran** (Swagger render dari DTO yang sama).

Referensi terkait: [FE-CONTRACT-file-endpoints.md](./FE-CONTRACT-file-endpoints.md) (aturan upload multipart umum),
[BE-REQUIREMENT-form-mutasi.md](../BE-REQUIREMENT-form-mutasi.md) (visibilitas field form mutasi).

---

## 1. Ringkasan Endpoint

Guard: `KEPEGAWAIAN:READ` (GET), `KEPEGAWAIAN:WRITE` (POST/PUT), `KEPEGAWAIAN:DELETE` (DELETE), atau role `ADMIN`.

| Method & Path | Binding | Guard | DTO | Duplikat → 409 (message) |
|---------------|---------|-------|-----|--------------------------|
| `POST /kepegawaian/riwayat/sk` | `@RequestBody` **JSON** | WRITE | `RiwayatSkPostRequest` | `Riwayat SK is Exists` |
| `PUT /kepegawaian/riwayat/sk/{id}` | `@RequestBody` **JSON** | WRITE | `RiwayatSkPutRequest` | `Riwayat SK is Exists` |
| `DELETE /kepegawaian/riwayat/sk/{id}` | — | DELETE | — | — |
| `POST /kepegawaian/riwayat/mutasi` | `@RequestBody` **JSON** | WRITE | `RiwayatMutasiPostRequest` | `Riwayat Mutasi is already Exists` |
| `PUT /kepegawaian/riwayat/mutasi/{id}` | `@RequestBody` **JSON** | WRITE | `RiwayatMutasiPutRequest` | `Riwayat Mutasi is already Exists` |
| `DELETE /kepegawaian/riwayat/mutasi/{id}` | — | DELETE | — | — |
| `POST /kepegawaian/riwayat/terminasi` | `@ModelAttribute` **multipart/form-data** | WRITE | `RiwayatTerminasiPostRequest` | `Terminasi is already exist` |
| `PUT /kepegawaian/riwayat/terminasi/{id}` | `@ModelAttribute` **multipart/form-data** | WRITE | `RiwayatTerminasiPutRequest` | `Terminasi is already exist` |

> ⚠️ **Binding berbeda!** SK & Mutasi = JSON body. Terminasi = `multipart/form-data` (kirim JSON → **415**).

### 1.1 Endpoint baca (ringkas — detail field di Swagger)

| Method & Path | Guard | Response |
|---------------|-------|----------|
| `GET /kepegawaian/riwayat/sk` / `/sk/pegawai/{id}` / `/sk/list` / `/sk/{id}` | READ | `PageResult<Page<RiwayatSkQuery>>` / `ListResult` / `SingleResult` |
| `GET /kepegawaian/riwayat/mutasi/pegawai/{id}` / `/mutasi/{id}` | READ | `PageResult<Page<RiwayatMutasiQuery>>` / `SingleResult` |
| `GET /kepegawaian/riwayat/terminasi` / `/terminasi/calon-pensiun` / `/terminasi/{id}` | READ | `PageResult<Page<RiwayatTerminasiQuery>>` / `PageResult<Page<PegawaiResponse>>` / `SingleResult` |

---

## 2. Envelope Respons

Semua respons mewarisi `ResultAbstract`:

```json
{
  "status": 201,
  "statusText": "CREATED",
  "errors": [],
  "message": "Data saved successfully",
  "data": 12345,
  "timestamp": "2026-08-14 14:30:00"
}
```

| Kode | Kasus | Bentuk |
|------|-------|--------|
| **201** | Simpan sukses | `SavedResult` — `data: {id}` (Long), `message: "Data saved successfully"` |
| **400** | Validasi gagal / `FAILED` | `ErrorResult` — `errors[]` berisi `field [x] : <pesan>` |
| **404** | Master/entitas tidak ditemukan | `NotFoundException` (`Unknown Pegawai`, `Unknown Organisasi`, `Unknown Jabatan`, `Unknown Golongan`, `Unknown Profesi`, `Unknown Alasan Terminasi`, `Unknown Riwayat ...`) |
| **409** | Duplikat guard `(pegawai, nomorSk, [jenisSk,] tanggalSk)` | message sesuai tabel §1 |
| **401 / 403** | Tanpa login / tanpa permission | lihat FE-CONTRACT-profil-update-approval-rbac |
| **415** | Terminasi dikirim sebagai JSON | — |

> Enum (`jenisSk`, `jenisMutasi`) dikirim sebagai **nama** (`"SK_PENSIUN"`), bukan ordinal.
> Tanggal dikirim sebagai string `"yyyy-MM-dd"`.

---

## 3. Riwayat SK — `POST /kepegawaian/riwayat/sk` & `PUT /kepegawaian/riwayat/sk/{id}`

Body JSON (`RiwayatSkPostRequest` / `RiwayatSkPutRequest` — field sama). PUT = penggantian penuh (semua field wajib ikut dikirim).

| Field | Tipe | Wajib | Ketentuan |
|-------|------|-------|-----------|
| `pegawaiId` | integer | ✅ | `>= 1` |
| `nomorSk` | string | ✅ | non-empty |
| `jenisSk` | enum | ✅ | nama: `SK_KENAIKAN_PANGKAT_GOLONGAN`, `SK_CAPEG`, `SK_PEGAWAI_TETAP`, `SK_JABATAN`, `SK_MUTASI`, `SK_PENSIUN`, `SK_LAINNYA`, `SK_PENYESUAIAN_GAJI`, `SK_KENAIKAN_GAJI_BERKALA` |
| `tanggalSk` | date | ✅ | `yyyy-MM-dd` |
| `tmtBerlaku` | date | ✅ | `yyyy-MM-dd` |
| `updateMaster` | boolean | ❌ | default `false`. **Jika `true`** → validasi grup `GajiSk` ikut aktif: `golonganId`, `gajiPokok`, `kenaikanBerikutnya` wajib, `mkgTahun`/`mkgBulan` `>= 0` |
| `golonganId` | integer | ⚠️ | wajib **hanya jika** `updateMaster=true` |
| `gajiPokok` | number | ⚠️ | wajib **hanya jika** `updateMaster=true` |
| `mkgTahun` / `mkgBulan` | integer | ⚠️ | `>= 0`; divalidasi hanya jika `updateMaster=true` |
| `kenaikanBerikutnya` | date | ⚠️ | wajib **hanya jika** `updateMaster=true` |
| `mkgbTahun` / `mkgbBulan` | integer | ❌ | opsional, tidak divalidasi |
| `notes` | string | ❌ | opsional |

**Efek `updateMaster=true`:** selain menyimpan baris SK, BE memperbarui master pegawai (gajiPokok, golongan, mkg, `refSk*Id` sesuai `jenisSk`).

---

## 4. Riwayat Mutasi — `POST /kepegawaian/riwayat/mutasi` & `PUT /kepegawaian/riwayat/mutasi/{id}`

Body JSON (`RiwayatMutasiPostRequest` / `RiwayatMutasiPutRequest` — field sama). DTO dedicated: **tidak** ada `updateMaster`, `nipam`, `nama` — jangan dikirim.

**Field wajib selalu:** `pegawaiId`, `jenisMutasi`, `nomorSk`, `jenisSk`, `tanggalSk`, `tmtBerlaku`. Opsional selalu: `notes`.

> **Catatan:** `jenisSk` yang dikirim FE **di-overwrite BE** dari `jenisMutasi` (mis. `MUTASI_LOKER` → `SK_MUTASI`, `MUTASI_GOLONGAN` → `SK_KENAIKAN_PANGKAT_GOLONGAN`). FE tetap wajib mengirim `jenisSk` (non-null).

| Field | Tipe | Wajib | Ketentuan |
|-------|------|-------|-----------|
| `pegawaiId` | integer | ✅ | `>= 1` |
| `jenisMutasi` | enum | ✅ | nama: `PENGANGKATAN_PERTAMA`, `MUTASI_LOKER`, `MUTASI_JABATAN`, `MUTASI_GOLONGAN`, `MUTASI_GAJI`, `MUTASI_GAJI_BERKALA`, `TERMINASI` |
| `nomorSk` | string | ✅ | non-empty |
| `jenisSk` | enum | ✅ | lihat catatan di atas |
| `tanggalSk` / `tmtBerlaku` | date | ✅ | `yyyy-MM-dd` |
| `tanggalBerakhir` | date | ❌ | opsional |
| `notes` | string | ❌ | opsional |

### 4.1 Field kondisional per `jenisMutasi` (POST)

Validasi grup diaktifkan **manual di controller POST** (PUT tidak):

| `jenisMutasi` | Field yang wajib dikirim | Group validasi |
|---------------|--------------------------|----------------|
| `MUTASI_LOKER`, `MUTASI_JABATAN` | `organisasiId`, `jabatanId`, `profesiId` (+ `organisasiLamaId`, `jabatanLamaId`, `profesiLamaId` — nilai lama, dikirim FE) | `MutasiJabatan` |
| `MUTASI_GOLONGAN` | `golonganId` (+ `golonganLamaId`), `mkgTahun`, `mkgBulan`, `kenaikanBerikutnya`, `mkgbTahun`, `mkgbBulan` — **tanpa** `gajiPokok` | `MutasiGolongan` |
| `MUTASI_GAJI`, `MUTASI_GAJI_BERKALA` | idem `MUTASI_GOLONGAN`, **plus `gajiPokok`** | `MutasiGolongan` |
| `PENGANGKATAN_PERTAMA`, `TERMINASI` | — (tidak lewat DTO ini di POST mutasi) | — |

> ⚠️ **PUT tidak menjalankan validasi grup** (hanya POST). Field kondisional yang tidak terkirim di PUT akan menghasilkan `Unknown ...` 404 saat resolver master, bukan 400.

---

## 5. Riwayat Terminasi — `POST /kepegawaian/riwayat/terminasi` & `PUT /kepegawaian/riwayat/terminasi/{id}`

**`multipart/form-data`** (`@ModelAttribute`). DTO dedicated: **tidak** ada field SK-gaji (`gajiPokok`, `mkg*`, `kenaikanBerikutnya`, `updateMaster`) — jangan dikirim.

| Field | Tipe | Wajib | Ketentuan |
|-------|------|-------|-----------|
| `pegawaiId` | form field | ✅ | `>= 1` |
| `nomorSk` | form field | ✅ | non-empty |
| `jenisSk` | form field | ✅ | **`SK_PENSIUN`** (SK pensiun/berhenti) |
| `tanggalSk` | form field | ✅ | `yyyy-MM-dd` |
| `tmtBerlaku` | form field | ✅ | `yyyy-MM-dd` — menjadi `tanggalTerminasi` + dasar `masaKerja` |
| `golonganId` | form field | ❌ | opsional (`>= 1` jika ada) — snapshot label golongan |
| `notes` | form field | ❌ | opsional |
| `alasanTerminasiId` | form field | ✅ | `>= 1` (dari master `alasan_berhenti`) |
| `nipam` | form field | ✅ | non-empty |
| `nama` | form field | ✅ | non-empty |
| `organisasiId` | form field | ✅ | `>= 1` |
| `jabatanId` | form field | ✅ | `>= 1` |
| `fileName` | **file part** | ❌ | opsional — jika dikirim, lampiran SK pensiun ikut dibuat |

**Efek sisi (saga):** selain baris terminasi + SK pensiun, BE menulis: status pegawai → `BERHENTI_OR_KELUAR`, user Appwrite di-disable (best-effort, ADR-0039), baris `RiwayatMutasi` jenis `TERMINASI`, dan — khusus pegawai berstatus `KONTRAK` — baris `RiwayatKontrak` jenis `TERMINASI` (isLatest diperbarui).

Contoh multipart:

```http
POST /api/kepegawaian/riwayat/terminasi
Content-Type: multipart/form-data
Authorization: Bearer <jwt>

--boundary
Content-Disposition: form-data; name="pegawaiId"
123
--boundary
Content-Disposition: form-data; name="nomorSk"
SK/2026/PSN/001
--boundary
Content-Disposition: form-data; name="jenisSk"
SK_PENSIUN
--boundary
Content-Disposition: form-data; name="tanggalSk"
2026-08-14
--boundary
Content-Disposition: form-data; name="tmtBerlaku"
2026-08-14
--boundary
Content-Disposition: form-data; name="alasanTerminasiId"
1
--boundary
Content-Disposition: form-data; name="nipam"
199001012010011001
--boundary
Content-Disposition: form-data; name="nama"
Budi Santoso
--boundary
Content-Disposition: form-data; name="organisasiId"
5
--boundary
Content-Disposition: form-data; name="jabatanId"
12
--boundary
Content-Disposition: form-data; name="fileName"; filename="sk-pensiun.pdf"
Content-Type: application/pdf
<binary>
--boundary--
```

---

## 6. Matriks Field Noise yang Sengaja Tidak Ada

| DTO | Tidak ada (jangan dikirim) | Alasan |
|-----|---------------------------|--------|
| `RiwayatTerminasiPostRequest` / `...PutRequest` | `gajiPokok`, `mkgTahun`, `mkgBulan`, `kenaikanBerikutnya`, `mkgbTahun`, `mkgbBulan`, `updateMaster` | SK pensiun tidak membawa data gaji; terminasi tidak pernah update master gaji |
| `RiwayatMutasiPostRequest` / `...PutRequest` | `updateMaster` | writeback pegawai eksplisit via `PegawaiWriteback` (ADR-0023); tidak dikirim FE |
| `RiwayatMutasiPostRequest` / `...PutRequest` | `nipam`, `nama` | snapshot diambil BE dari `RiwayatSk`/`Pegawai` — field request tidak dibaca |

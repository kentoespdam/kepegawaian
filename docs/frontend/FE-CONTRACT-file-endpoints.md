# FE Contract — Semua Endpoint File (Upload & Download)

> Dokumen kontrak untuk tim **Frontend**: aturan baku untuk semua endpoint yang menerima/mengirim **file** — cara mengirim (multipart), nama field file, guard, dan perilaku error.

| Item | Nilai |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Tanggal | 2026-08-14 |
| ADR terkait | [ADR-0036](../adr/0036-profileupdate-mesin-generik-8-entity.md) (approval lampiran via antrian), [ADR-0038](../adr/0038-profil-endpoint-split-admin-vs-selfservice.md), [ADR-0039](../adr/0039-rbac-user-lifecycle-no-hard-delete.md), decisions-pegawai §53 (download file = QueryService) |
| Ruang lingkup | **Upload** (multipart) + **download** file di seluruh modul |

---

## Status Perubahan

| Perubahan | Status | Dampak FE |
|-----------|--------|-----------|
| Semua endpoint upload kini `consumes = multipart/form-data` **eksplisit** (konsisten, tidak lagi string literal / tanpa deklarasi) | ✅ **LIVE** | Request **JSON** ke endpoint file → **HTTP 415** Unsupported Media Type (sebelumnya 400 membingungkan / silent fail) |
| `PUT /profil/biodata/{id}/foto-profil` kini `consumes = multipart/form-data` | ✅ **LIVE** | Wajib kirim `multipart/form-data`; JSON → **415** |

> ⚠️ **Poin kunci**: semua endpoint upload **WAJIB** `Content-Type: multipart/form-data`. File dikirim sebagai **form part** (bukan base64 di JSON). Mengirim JSON → `415` sekarang (jelas), bukan error binding yang membingungkan.

---

## 1. Aturan Umum Upload File

1. **Content-Type**: `multipart/form-data` — jangan pernah `application/json` untuk endpoint file.
2. **Nama field file** mengikuti tabel di section 2 (`fileName`, `file`, atau `fotoProfil`). Nama part TIDAK boleh salah — salah nama = file `null`.
3. **Binding**: semua endpoint upload memakai `@ModelAttribute` (field form biasa dibind otomatis) — kecuali foto-profil yang memakai `@RequestParam("fotoProfil")` (satu part file).
4. **Opsional vs wajib**:
   - Field file **wajib** (`@NotNull`) → dikirim tanpa file = **400** dengan detail di `errors[]`: `LampiranSkPostRequest`, `<Entity>LampiranProfilPostRequest`, `CutiKuotaImportRequest.file`.
   - Field file **opsional** → tanpa file tetap valid (file tidak dibuat): `RiwayatSpPostRequest.fileName`, `RiwayatTerminasiPostRequest.fileName`.
5. **Error**: validasi gagal → HTTP 400, detail di `errors[]`. JSON/multipart salah → **415**. Tanpa login / tanpa permission → **401/403** (format body 403 berbeda, lihat FE-CONTRACT-profil-update-approval-rbac section 6).
6. **Download**: endpoint download mengembalikan **file biner** (bukan envelope JSON) — stream via `ResponseEntity<Resource>`.

---

## 2. Endpoint Upload (multipart/form-data)

### 2.1 Modul Kepegawaian — guard `KEPEGAWAIAN:WRITE`

| Method & Path | Field file | Wajib | DTO |
|---------------|-----------|-------|-----|
| `POST /kepegawaian/riwayat/sp` | `fileName` | opsional | `RiwayatSpPostRequest` |
| `PUT /kepegawaian/riwayat/sp/{id}` | `fileName` | opsional | `RiwayatSpPutRequest` |
| `POST /kepegawaian/riwayat/terminasi` | `fileName` | opsional | `RiwayatTerminasiPostRequest` |
| `PUT /kepegawaian/riwayat/terminasi/{id}` | `fileName` | opsional | `RiwayatTerminasiPutRequest` |
| `POST /kepegawaian/lampiran-sk` | `fileName` | ✅ **wajib** | `LampiranSkPostRequest` (`ref` = `EJenisSk`, `refId`) |

Contoh (SP / Terminasi — field form lain ikut dikirim seperti biasa):

```http
POST /api/kepegawaian/riwayat/sp
Content-Type: multipart/form-data
Authorization: Bearer <jwt>

--boundary
Content-Disposition: form-data; name="pegawaiId"
123
--boundary
Content-Disposition: form-data; name="nomorSp"
SP/2026/001
--boundary
Content-Disposition: form-data; name="fileName"; filename="sp-001.pdf"
Content-Type: application/pdf
<binary>
--boundary--
```

Respons sukses: `SavedResult` (`status: 201`, `data: {id}`). Duplikat → 409 (`message: "Riwayat SP is Exists"` / `"Terminasi is already exist"`).

> ⚠️ **Terminasi**: field lengkap ada di [FE-CONTRACT-kepegawaian-riwayat-write.md](./FE-CONTRACT-kepegawaian-riwayat-write.md) §5. Inti: `jenisSk = SK_PENSIUN` (wajib); field SK-gaji (`gajiPokok`, `mkg*`, `kenaikanBerikutnya`, `updateMaster`) **tidak ada** di DTO — jangan dikirim. Tanpa file → lampiran SK pensiun tidak dibuat (opsional).

### 2.2 Modul Profil

| Method & Path | Field file | Wajib | Guard |
|---------------|-----------|-------|-------|
| `POST /profil/{entity}/lampiran` | `fileName` | ✅ wajib | `PROFIL:UPDATE` (self → masuk antrian approval) |
| `POST /admin/profil/{entity}/lampiran` | `fileName` | ✅ wajib | `PROFIL:APPROVE` (admin → langsung stabil) |
| `PUT /profil/biodata/{id}/foto-profil` | `fotoProfil` (`@RequestParam`) | ✅ wajib | `PROFIL:APPROVE` |

- `{entity}` = `pendidikan` | `keluarga` | `keahlian` | `pelatihan` | `kartu-identitas` | `pengalaman-kerja`.
- DTO lampiran: `<Entity>LampiranPostRequest` → field `ref` (`EJenisLampiranProfil`), `refId`, `fileName`, `notes`.
- **foto-profil**: WAJIB file **gambar** — server cek ekstensi MIME (`mimeTypesUtils.isImage`); bukan gambar → **500/400** (`"File must be an image"`). Part name harus persis `fotoProfil`.

```http
PUT /api/profil/biodata/NIK123/foto-profil
Content-Type: multipart/form-data
Authorization: Bearer <jwt>

--boundary
Content-Disposition: form-data; name="fotoProfil"; filename="foto.jpg"
Content-Type: image/jpeg
<binary>
--boundary--
```

> ⚠️ **Routing self vs admin** (ADR-0038): self (`/profil/...`) selalu masuk antrian approval; admin (`/admin/profil/...`) langsung stabil. Pemilihan endpoint = konteks halaman, sama seperti endpoint CRUD profil (lihat FE-CONTRACT-profil-update-approval-rbac section 5.1).

### 2.3 Modul Penggajian

| Method & Path | Field file | Wajib | Guard |
|---------------|-----------|-------|-------|
| `POST /penggajian/batch/root` | `fileName` | opsional (validasi service) | `PENGGAJIAN:PROCESS` |
| `PATCH /penggajian/batch/master/upload/{rootBatchId}` | `file` | sesuai DTO | `PENGGAJIAN:WRITE` |

- `POST /penggajian/batch/root` — DTO `GajiBatchRootPostRequest` (`tahun`, `bulan`, `diProsesOleh`, `jabatanPemroses`, `fileName`).
- `PATCH .../upload/{rootBatchId}` — DTO hanya berisi `file` (upload potongan gaji tambahan).

### 2.4 Query Endpoint - Penggajian Batch Master

> **Breaking Change** (2026-09-02): query params `nipam`, `nama`, `gajiBatchRootId`, `pegawaiId` pada `GET /penggajian/batch/master` **dihapus**. Diganti satu param `search` yang melakukan LIKE case-insensitive pada kolom `nipam` DAN `nama`.

| Method & Path | Param | Tipe | Keterangan |
|---------------|-------|------|------------|
| `GET /penggajian/batch/master` | `search` | `string` (opsional) | Filter by nipam/nama (`LIKE %search%`, case-insensitive) |
| `GET /penggajian/batch/master/pegawai/{pegawaiId}` | `pegawaiId` | `long` (path) | Tidak berubah - filter by pegawai + status FINISHED |

Contoh:
```
GET /api/penggajian/batch/master?search=budi
GET /api/penggajian/batch/master              <- semua data (tanpa filter)
GET /api/penggajian/batch/master/pegawai/123  <- tidak berubah
```

### 2.5 Modul Cuti

| Method & Path | Field file | Wajib | Guard |
|---------------|-----------|-------|-------|
| `POST /cuti/kuota/import` | `file` | wajib (`@NotNull` + `tahun` `@Min(2000)`) | `CUTI:WRITE` |

- DTO `CutiKuotaImportRequest`: `tahun` (form field integer), `file` (Excel template).

---

## 3. Endpoint Download (biner, bukan JSON)

| Method & Path | Keterangan |
|---------------|------------|
| `GET /kepegawaian/riwayat/sp/{id}/file` | File SP |
| `GET /kepegawaian/lampiran-sk/file/{jenis}/{id}` | Lampiran SK (`jenis` = `EJenisSk`) |
| `GET /profil/{entity}/lampiran/{id}/file` | Lampiran profil per entity |
| `GET /profil/lampiran/file/{jenis}/{id}` | Lampiran profil via jenis (`EJenisLampiranProfil`) |
| `GET /profil/biodata/{id}/foto-profil` | Foto profil |
| `GET /penggajian/batch/master/download/table-gaji/{rootBatchId}` | Excel tabel gaji |
| `GET /penggajian/batch/master/download/potongan-gaji/{rootBatchId}` | Excel potongan gaji |
| `GET /cuti/kuota/template` | Template Excel kuota cuti |

---

## 4. Verifikasi Foto-Profil dari Sisi FE

> ⚠️ Kode frontend **tidak ada di repo backend ini** — kontrak di bawah adalah kebenaran dari sisi server. Silakan verifikasi implementasi FE di repo FE:

1. Buka halaman edit foto profil → buka **Network tab** (F12) → cek request `PUT /profil/biodata/{id}/foto-profil`:
   - `Content-Type` harus **`multipart/form-data`** (bukan `application/json`).
   - Nama part harus **`fotoProfil`** (cek di payload request).
2. Jika FE mengirim JSON / nama part lain / base64:
   - Sekarang → **415** (content-type) atau file `null` → error validasi/`"File must be an image"`.
3. Implementasi FE yang benar (contoh fetch):

```js
const fd = new FormData();
fd.append('fotoProfil', fileInput.files[0]);
await fetch(`/api/profil/biodata/${nik}/foto-profil`, {
  method: 'PUT',
  headers: { Authorization: `Bearer ${token}` }, // jangan set Content-Type manual!
  body: fd,
});
```

> Browser otomatis men-set `Content-Type: multipart/form-data; boundary=...` saat body `FormData` — **jangan** set `Content-Type` manual, boundary akan hilang dan server 400/415.

---

## 5. Checklist Aksi Tim FE

- [ ] Semua upload file pakai `FormData` + `multipart/form-data` (jangan JSON, jangan base64).
- [ ] Nama part file sesuai tabel: `fileName` | `file` | `fotoProfil`.
- [ ] Jangan set `Content-Type` manual saat pakai `FormData` (biarkan browser).
- [ ] Tangani **415** (kirim JSON ke endpoint file) sebagai error kontrak — perbaiki sisi FE, bukan server.
- [ ] Field file wajib (`@NotNull`) → tampilkan validasi FE sebelum upload (jangan harap 400).
- [ ] Foto profil: validasi ekstensi gambar di FE juga (server cek MIME → bukan gambar = error).
- [ ] Endpoint self vs admin profil (`/profil/{entity}/lampiran` vs `/admin/profil/{entity}/lampiran`) dipilih per konteks halaman (approval vs langsung stabil).

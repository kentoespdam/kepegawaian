# FE Contract — Profil Update Approval & RBAC Permission per Role

> Dokumen kontrak untuk tim **Frontend**: perubahan Backend yang memengaruhi cara FE memanggil API, membaca respons, dan me-render UI.

| Item | Nilai |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Commit | `221f497e` (HEAD — self/admin 6 modul profil) — semua seri RBAC/profil ter-push |
| Tanggal | 2026-08-12 |
| ADR | [ADR-0037](../adr/0037-rbac-permission-per-role-didb-mariadb.md), [ADR-0038](../adr/0038-profil-endpoint-split-admin-vs-selfservice.md) |
| Issue | `kepegawaian-9b6l` (RBAC — ✅ closed) · `kepegawaian-qp0m` (default roles — ✅ closed) · `kepegawaian-huis` (split profil — ✅ closed) · `kepegawaian-y5vt` (seed V31 — ✅ closed) · `kepegawaian-nilt` (`GET /account/me` — ✅ closed) · `kepegawaian-3blf` (ownership check self — ✅ closed) · `kepegawaian-jiv4` (ownership read self — ✅ closed) · `kepegawaian-t3s3` (antrian approval — ⏳ OPEN) |

---

## Status Perubahan

| Perubahan | Status | Dampak FE |
|-----------|--------|-----------|
| RBAC infrastructure: permission granular per role | ✅ **LIVE** | Role management API baru + field `permissions` di respons role |
| User provisioning: role default `[ADMIN, USER]` → `[USER]` | ✅ **LIVE** | User baru **tidak lagi** dapat role `ADMIN` |
| Dev User: dapat semua permission | ✅ **LIVE** | Hanya untuk environment dev, tidak relevan di prod |
| Enforcement permission di controller (`hasAuthority`) | 🕐 **SEBAGIAN** (dual-mode) | Modul **master** + **pegawai** + **admin-profil** sudah dual-mode; modul lain masih `hasRole('ADMIN')`. Dual-mode = izin lama tetap jalan → **tidak ada endpoint yang menyempit** |
| Endpoint split profil — biodata + 6 modul lain | ✅ **LIVE** (kepegawaian-huis) | **BREAKING**: `PATCH /profil/biodata/{id}` lama DIHAPUS — FE wajib routing per halaman (section 5) |
| Seed permission matrix (V31) | ✅ **LIVE** (kepegawaian-y5vt) | `ADMIN`=20, `HRD`=15 — HRD kini bisa write/delete master + `PATCH /admin/profil/{id}` (section 2.4) |
| `GET /account/me` | ✅ **LIVE** (kepegawaian-nilt) | Endpoint baru: roles + permissions user login untuk UI berbasis permission (section 1) |

> ⚠️ **Poin kunci**: migrasi ke `hasAuthority` dilakukan bertahap per modul dengan pola **dual-mode** (`hasRole('ADMIN') or hasAuthority('...')`) — izin lama tidak pernah dicabut. Modul **master** (write/delete), **pegawai** (write/delete) dan **admin-profil** sudah dual-mode; modul lain menyusul. Untuk endpoint existing FE tidak perlu mengubah guard UI — satu-satunya **breaking change** adalah pemisahan endpoint self vs admin profil (section 5).

---

## 1. Konsep: Role vs Permission

- **Role** (Appwrite `prefs.roles`): label seperti `ADMIN`, `HRD`, `USER`, `SYSTEM`, `PENGGAJIAN`.
- **Permission**: unit akses granular format `{ENTITY}:{ACTION}` (mis. `CUTI:APPROVE`, `MASTER:DELETE`), disimpan di MariaDB, terikat ke Role.
- Satu user dengan banyak role mendapat **union** semua permission dari semua role-nya.
- Di sisi server, authority yang di-inject ke setiap request: `ROLE_<nama role>` **dan** string permission `ENTITY:ACTION` (tanpa prefix).

### Katalog Permission (20)

| Permission | Arti |
|------------|------|
| `MASTER:READ` / `MASTER:WRITE` / `MASTER:DELETE` | Master data (jabatan, organisasi, golongan, dst.) |
| `PEGAWAI:READ` / `PEGAWAI:WRITE` / `PEGAWAI:DELETE` | Data pegawai |
| `KEPEGAWAIAN:READ` / `KEPEGAWAIAN:WRITE` / `KEPEGAWAIAN:DELETE` | SK, mutasi, kontrak, SP |
| `PROFIL:READ` / `PROFIL:UPDATE` / `PROFIL:APPROVE` | Profil: baca / update sendiri / approve antrian |
| `CUTI:READ` / `CUTI:CREATE` / `CUTI:APPROVE` | Cuti |
| `PENGGAJIAN:READ` / `PENGGAJIAN:WRITE` / `PENGGAJIAN:PROCESS` | Penggajian |
| `SYSTEM:MANAGE_USER` / `SYSTEM:MANAGE_ROLE` | Manajemen user & role |

> **Sudah tersedia — `GET /account/me`** (envelope `SingleResult`):
> ```json
> {
>   "status": 200, "statusText": "OK", "errors": [], "message": "Data Found",
>   "data": {
>     "id": "123",
>     "name": "Budi Santoso",
>     "roles": ["ADMIN", "HRD"],
>     "permissions": ["MASTER:DELETE", "PEGAWAI:READ", "PROFIL:APPROVE"]
>   },
>   "timestamp": "2026-08-12 14:30:00"
> }
> ```
> - `roles` dan `permissions` sudah ter-sort. `permissions` = union dari semua role user (hasil inflation per request, sesuai matrix DB saat itu).
> - Endpoint ini butuh login (Bearer token / DevUser di dev). Pakai untuk show/hide menu berbasis permission.

---

## 2. RBAC — Endpoint Baru (✅ LIVE)

Semua endpoint di bawah diproteksi `@PreAuthorize("hasRole('SYSTEM')")` — **hanya role `SYSTEM`** yang bisa akses (403 untuk lainnya).

### 2.1 `GET /system/permissions` — list semua permission

```http
GET /api/system/permissions
Authorization: Bearer <jwt>
```

Respons (envelope `ListResult`):
```json
{
  "status": 200,
  "statusText": "OK",
  "errors": [],
  "message": "Data found!",
  "data": [
    { "name": "MASTER:READ" },
    { "name": "MASTER:WRITE" }
  ],
  "timestamp": "2026-08-12 14:30:00"
}
```

> ⚠️ List kosong → HTTP **404** dengan `message: "Data not found!"` (perilaku bawaan `ListResult`).

### 2.2 `POST /system/roles/{roleId}/permissions/{permName}` — assign permission ke role

```http
POST /api/system/roles/HRD/permissions/PROFIL:APPROVE
```

| Kode | Kasus |
|------|-------|
| 201 | Berhasil — `SavedResult` (`message: "Data saved successfully"`) |
| 404 | Role atau permission tidak ditemukan |
| 409 | Permission sudah ter-assign ke role tersebut |
| 403 | Bukan role `SYSTEM` |

### 2.3 `DELETE /system/roles/{roleId}/permissions/{permName}` — revoke permission dari role

```http
DELETE /api/system/roles/HRD/permissions/PROFIL:APPROVE
```

| Kode | Kasus |
|------|-------|
| 200 | Berhasil — `DeletedResult` (`message: "Data berhasil dihapus"`) |
| 404 | Role/permission tidak ada, atau permission tidak ter-assign ke role tsb |
| 403 | Bukan role `SYSTEM` |

### 2.4 Perubahan respons `GET /system/roles` — field `permissions` baru

Endpoint role yang **sudah ada** (`GET /system/roles`, `GET /system/roles/list`, `GET /system/roles/{id}`, `POST /system/roles`) kini mengembalikan **field tambahan `permissions`** per role (dari relasi DB):

```json
// Sebelum
{ "id": "HRD" }

// Sesudah
{
  "id": "HRD",
  "permissions": [
    { "name": "PROFIL:APPROVE" },
    { "name": "CUTI:APPROVE" }
  ]
}
```

- Field `permissions` bisa kosong (`[]`) jika role belum punya permission.
- Ini perubahan **additive** — FE lama tetap jalan, tapi halaman manajemen role sebaiknya menampilkan list permission ini.

> **Seed matrix (V31, sudah live):** role `ADMIN` ter-seed **20 permission** (semua), role `HRD` ter-seed **15** (operasional minus `SYSTEM:*`, `CUTI:CREATE`, `PENGGAJIAN:WRITE/PROCESS`). Implikasi:
> - HRD kini bisa akses **write/delete master** (dual-mode `MASTER:WRITE`/`MASTER:DELETE` di controller master), **write/delete pegawai** (dual-mode `PEGAWAI:WRITE`/`PEGAWAI:DELETE`) dan **`PATCH /admin/profil/{id}`** (punya `PROFIL:APPROVE`).
> - `CUTI:CREATE` tetap milik pegawai (`USER`) — HRD hanya approve.
> - Matrix bisa diubah runtime via API assign/revoke (section 2.2–2.3).

---

## 3. User Provisioning — Role Default Berubah (✅ LIVE)

**Perubahan**: user baru yang dibuat via `POST /api/system/users` (createUser) **hanya mendapat role `USER`**, tidak lagi `[ADMIN, USER]`.

**Implikasi FE:**
- Flow "register user baru" tidak berubah bentuknya, tapi user baru = akses pegawai biasa (`USER`).
- Role `ADMIN` (dan role lain) harus di-assign **eksplisit** oleh admin `SYSTEM` via endpoint yang sudah ada:
  `PATCH /api/system/users/pref/{userId}` dengan body `{ "roles": ["ADMIN"] }` (cek kontrak existing endpoint ini).
- Halaman manajemen user: pastikan ada UI untuk assign role per user (bukan hanya "user baru otomatis admin").

### Dev User (hanya profile `development`)

Saat request **tanpa** Bearer token di environment dev: principal `DEV` mendapat role dari config `security.dev.roles` (default `ADMIN,SYSTEM`) **+ SEMUA 20 permission** (hardcoded, tanpa query DB). Saat request **dengan** Bearer token: validasi JWT normal berlaku (401 jika token invalid — fallback DEV tidak berlaku).

---

## 4. Profil Update Approval — Kontrak Saat Ini (tidak berubah)

Mekanisme approval profil **tidak berubah** oleh pekerjaan RBAC. Kontrak berikut untuk referensi tim FE (terutama terkait permission `PROFIL:APPROVE` di masa depan):

### 4.1 Endpoint

| Method & Path | Fungsi | Guard saat ini |
|---------------|--------|----------------|
| `GET /profil/profil-update` | List antrian perubahan profil (paging) | Login saja |
| `GET /profil/profil-update/{id}` | Detail: data sebelum & sesudah revisi | Login saja |
| `PUT /profil/profil-update/{id}` | Approve / reject antrian | Login saja |

> ⚠️ **Catatan penting**: saat ini endpoint di atas **belum** punya `@PreAuthorize` — hanya butuh login. Rencana (migrasi permission): `PUT .../{id}` akan digate **`PROFIL:APPROVE`**. Saat itu tiba, FE harus menyembunyikan tombol approve bagi user tanpa permission tsb. **Belum aktif sekarang.**

### 4.2 Query params `GET /profil/profil-update`

| Param | Tipe | Default | Keterangan |
|-------|------|---------|------------|
| `nipam` | string | — | Filter NIPAM |
| `nama` | string | — | Filter nama |
| `tanggalPengajuan` | `yyyy-MM-dd` | — | Filter tanggal pengajuan |
| `approvalStatus` | enum | `PENDING` | `PENDING` / `APPROVED` / `REJECT` |
| `page`, `size`, `sort` | — | standar | Paging Spring (`sort=nama,asc`) |

### 4.3 Body `PUT /profil/profil-update/{id}`

```json
{
  "approval": "APPROVED",
  "pegawaiId": 123
}
```

- `approval` (required): **`"APPROVED"`** atau **`"REJECT"`** — perhatikan ejaan `REJECT` (bukan `REJECTED`).
- `pegawaiId` (required, min 1): id pegawai PIC/approver yang melakukan tindakan.
- Validasi gagal → HTTP 400 dengan detail error di field `errors[]`.
- Approve hanya bisa untuk antrian berstatus `PENDING`; id lain → 404.

### 4.4 Respons list (envelope `PageResult` + Spring Page)

```json
{
  "status": 200,
  "statusText": "OK",
  "data": {
    "content": [
      {
        "id": 12,
        "nipam": "199501012024011001",
        "nama": "Budi Santoso",
        "jabatan": "Staff SDM",
        "reqDate": "2026-08-12T10:00:00",
        "tableName": "PENDIDIKAN",
        "actionType": "UPDATE",
        "dataDescription": "Perubahan data pendidikan",
        "revId": "7",
        "approvalStatus": "PENDING",
        "approvalDate": null,
        "approvalPic": null
      }
    ],
    "pageable": { "...": "..." },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": { "sorted": false, "unsorted": true, "empty": true },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  },
  "timestamp": "2026-08-12 14:30:00"
}
```

Nilai enum yang mungkin:
- `tableName`: `BIODATA` | `KELUARGA` | `PENDIDIKAN` | `PENGALAMAN_KERJA` | `PELATIHAN` | `KEAHLIAN` | `KARTU_IDENTITAS` | `LAMPIRAN`
- `actionType` (RevisionType): `INSERT` | `UPDATE` | `DELETE`
- `approvalStatus`: `PENDING` | `APPROVED` | `REJECT`

### 4.5 Respons detail `GET /profil/profil-update/{id}` (envelope `SingleResult`)

```json
{
  "status": 200,
  "statusText": "OK",
  "errors": [],
  "message": "Data Found",
  "data": {
    "profileUpdate": { "...": "sama seperti item list di atas" },
    "latestRevision": { "...": "data entitas SETELAH perubahan (bentuk entitas, mis. record Pendidikan)" },
    "previousRevision": { "...": "data entitas SEBELUM perubahan" }
  },
  "timestamp": "2026-08-12 14:30:00"
}
```

- `latestRevision` / `previousRevision` bertipe **bentuk entitas domain** (field-nya mengikuti entity, mis. `Pendidikan`: `id`, `biodataId`, `jenjangId`, `institusi`, `jurusan`, `gelarDepan`, `gelarBelakang`, `tahunMasuk`, `tahunLulus`, `isLulus`, `gpa`, `kota`, `isLatest`, `changedStatus`, dll).
- FE sebaiknya render diff dari kedua field ini (tampilkan nilai lama vs baru per field).

### 4.6 Mekanisme `changedStatus` (konteks)

> ⚠️ **Diperbarui (ADR-0038)**: sejak split self/admin, keputusan `changedStatus` **tidak lagi berbasis role principal**, melainkan **berbasis konteks endpoint**:

- Update lewat endpoint **self** (`/profil/...`) → `changedStatus=true` → masuk antrian approval (`PENDING`), **untuk semua user termasuk ADMIN/HRD**.
- Update lewat endpoint **admin** (`/admin/profil/...`) → `changedStatus=false` → langsung stabil, tidak masuk antrian.
- Keputusan diambil **server** berdasarkan endpoint yang dipanggil (bukan dari body request) — FE tidak mengirim/mengatur field status, cukup pilih endpoint yang benar per konteks halaman (tabel routing di section 5 & 5.1).

---

## 5. LIVE — Endpoint Split Profil (ADR-0038): Self vs Admin

> ⚠️ **BREAKING CHANGE**: `PATCH /profil/biodata/{id}` (routing changedStatus berbasis role) **telah DIHAPUS**. FE wajib migrasi ke 2 endpoint di bawah ini (routing per halaman).

| Endpoint | Perilaku | Diproteksi |
|----------|----------|------------|
| `PATCH /admin/profil/{id}` | Edit profil siapa pun oleh HRD/ADMIN — **tidak pernah** trigger approval queue (langsung stable, `changedStatus=false`) | `hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')` |
| `PATCH /profil` | Edit profil **diri sendiri** oleh pegawai — **selalu** masuk approval queue (`changedStatus=true`); NIK diambil dari token, **tidak ada** id di path/body | Login (self) |

**Body kedua endpoint** (sama dengan yang lama) — `BiodataPatchRequest`:
```json
{
  "nama": "Budi Santoso",
  "alamat": "Jl. Merdeka No. 1",
  "jenisKelamin": "LAKI_LAKI",
  "statusKawin": "KAWIN",
  "agama": "ISLAM",
  "tempatLahir": "Surabaya",
  "tanggalLahir": "1995-01-01",
  "ibuKandung": "Siti",
  "telp": "081234567890"
}
```
Semua field opsional (PATCH parsial); yang tidak dikirim tidak berubah. Nilai enum dikirim sebagai **string nama** (Jackson by name): `jenisKelamin`: `LAKI_LAKI`/`PEREMPUAN`; `statusKawin`: `BELUM_KAWIN`/`KAWIN`/`JANDA_DUDA`/`MENIKAH_SEKANTOR`/`TIDAK_TAHU`; `agama`: `TIDAK_TAHU`/`ISLAM`/`KRISTEN`/`KATOLIK`/`HINDU`/`BUDHA`/`KONGHUCHU`/`ALIRAN_KEPERCAYAAN`/`LAINNYA`.

**Aturan main FE:**
1. Halaman admin/HRD → panggil `PATCH /admin/profil/{id}` (`id` = NIK target).
2. Halaman self-service pegawai → panggil `PATCH /profil` (tanpa id — NIK dari token).
3. Jangan pakai mekanisme `X-Acting-As` header / flag `asAdmin` di body — sengaja **tidak** didukung (bisa di-bypass).
4. Pegawai biasa yang memanggil `/admin/profil/{id}` → **403** (tidak punya `PROFIL:APPROVE`).
5. `ADMIN` dan `HRD` (sejak seed V31, HRD punya `PROFIL:APPROVE`) bisa akses `/admin/profil/{id}`.
6. Principal `DEV` (dev tanpa token) tidak bisa pakai `PATCH /profil` (tidak punya akun riil) — gunakan `/admin/profil/{id}` atau Bearer token asli untuk menguji alur approval.

### 5.1 Modul profil lainnya (pendidikan, keluarga, keahlian, pelatihan, kartu-identitas, pengalaman-kerja, lampiran)

> ⚠️ **Behavior change**: sejak split ini, endpoint self (`/profil/{entity}/...`) **selalu** memasukkan perubahan ke approval queue (`changedStatus=true`) — **termasuk untuk user ADMIN/HRD**. Admin yang mengedit data pegawai harus lewat endpoint admin di bawah, bukan endpoint self.

| Konteks | Endpoint (pola per entity) | Perilaku |
|---------|---------------------------|----------|
| **Self** | `POST /profil/{entity}` · `PUT /profil/{entity}/{id}` · `DELETE /profil/{entity}/{id}` (+ `/lampiran` add/delete) — endpoint existing | **Selalu** `changedStatus=true` → approval queue. Request tetap membawa `biodataId`/`nik` di body. |
| **Admin** | `POST /admin/profil/{entity}` · `PUT /admin/profil/{entity}/{id}` · `DELETE /admin/profil/{entity}/{id}` (+ `/lampiran` add/delete) — **baru** | **Selalu** `changedStatus=false` (langsung stable). Guard: `hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')` → 403 tanpa itu. |

Path entity: `pendidikan`, `keluarga`, `keahlian`, `pelatihan`, `kartu-identitas`, `pengalaman-kerja`. Request body sama persis dengan endpoint self.

> ✅ **Ownership check (kepegawaian-3blf, sudah LIVE)**: endpoint self kini memverifikasi bahwa `biodataId`/`nik` di body **milik principal** — user login **tidak bisa** lagi create/update/delete data profil milik orang lain lewat jalur self (termasuk lampiran: ownership di-resolve dari `ref`+`refId`). Bila target bukan miliknya → **404** (sengaja sama seperti data tidak ada, hindari info leak). Endpoint admin (`/admin/profil/...`) tidak dibatasi. Perilaku normal self-service tidak berubah.
>
> ✅ **Ownership read (kepegawaian-jiv4, sudah LIVE)**: jalur **read** self kini dibatasi — user tanpa `PROFIL:READ`/`ROLE_ADMIN` hanya bisa baca data profil **miliknya sendiri**:
> - `GET /profil/{entity}?biodataId=...` — biodataId yang dikirim **di-force ke NIK sendiri** (bukan 404), data orang lain tidak pernah muncul.
> - `GET /profil/{entity}/{id}` · `GET /profil/{entity}/lampiran/...` · `GET /profil/lampiran/file/...` · biodata detail/dashboard/foto — target bukan milik sendiri → **404**.
> - **HRD/ADMIN bebas baca semua** (dual-mode `PROFIL:READ`, sudah ter-seed V31). FE tidak perlu pindah endpoint — pembeda konteks otomatis di server.
>
> ⚠️ **Batas yang diketahui (`kepegawaian-t3s3`, OPEN)**: `GET /profil/profil-update` (antrian approval) masih terbuka untuk semua user login — akan di-split approver (`PROFIL:APPROVE`) vs self (antrian sendiri).

---

## 6. Envelope Respons Standar (referensi)

Semua endpoint memakai envelope berikut (kecuali error handler khusus):

**`ListResult` / `SingleResult` / `SavedResult` / `DeletedResult`**
```json
{
  "status": 200,
  "statusText": "OK",
  "errors": [],
  "message": "Data found!",
  "data": {},
  "timestamp": "2026-08-12 14:30:00"
}
```

**HTTP status per tipe:**

| Envelope | Sukses | Kosong/Gagal |
|----------|--------|--------------|
| `ListResult` | 200 `"Data found!"` | 404 `"Data not found!"` (list kosong) |
| `SingleResult` | 200 `"Data Found"` | 404 `"Data not found!"` (data null) |
| `SavedResult` | 201 `"Data saved successfully"` | 400 (failed) / 409 (duplicate), detail di `errors[]` |
| `DeletedResult` | 200 `"Data berhasil dihapus"` | 400 `"Data gagal dihapus"` |
| `PageResult` | 200 — `data` = objek Spring Page | — |

**Error 403 (Forbidden)** — body dari `DeniedHandler`, format berbeda dari envelope biasa:
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "timestamp": 1786519500000,
  "path": "/api/system/permissions"
}
```

---

## 7. Checklist Aksi Tim FE

- [ ] Halaman **manajemen role**: render list permission per role (field `permissions` di `GET /system/roles/list`), plus UI assign/revoke via endpoint baru (section 2.2–2.3) — khusus role `SYSTEM`.
- [ ] Halaman **manajemen user**: user baru hanya role `USER`; pastikan ada UI assign role eksplisit via `PATCH /system/users/pref/{userId}` (section 3).
- [ ] Halaman **approval profil**: siapkan logic sembunyikan tombol approve saat guard `PROFIL:APPROVE` aktif di `PUT /profil/profil-update/{id}` (section 4.1 note — belum aktif).
- [x] **Routing split profil** (`/admin/profil/{id}` vs `/profil`) — **sudah LIVE**; `PATCH /profil/biodata/{id}` lama sudah dihapus (section 5).
- [x] **Routing admin vs self untuk 6 modul profil lain** — admin pindah ke `/admin/profil/{entity}/...`, self tetap di `/profil/{entity}/...` (selalu approval) (section 5.1).
- [x] **UI berbasis permission**: pakai `GET /account/me` (roles + permissions user login) — sudah live, lihat catatan di section 1.
- [x] **Seed matrix (V31)**: `ADMIN`=20 / `HRD`=15 sudah live — HRD punya write/delete master + pegawai + admin-profil (section 2.4).
- [x] **Ownership self-endpoint**: endpoint self verifikasi kepemilikan `biodataId`/`nik` — **sudah LIVE** (kepegawaian-3blf); target bukan milik principal → 404 (section 5.1).
- [x] **Ownership read self**: jalur read profil dibatasi ke data sendiri (force NIK di list, 404 di detail/file) — **sudah LIVE** (kepegawaian-jiv4); HRD/ADMIN bebas via `PROFIL:READ` (section 5.1).
- [x] **Terdaftar di OpenAPI**: `GET /account/me` masuk group swagger `auth` (`/v3/api-docs/auth` — paths `/auth/**`, `/account/**`); semua `/admin/profil/**` masuk group `profil` (`/v3/api-docs/profil` — paths `/profil/**`, `/admin/profil/**`) — FE bisa regenerate tipe dari kedua group tersebut.

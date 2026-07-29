# BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`)

**Untuk:** Tim Backend (Spring Boot)
**Dari:** Tim Frontend (`kepegawaian-fe`)
**Tanggal:** 2026-07-29
**Konteks FE:** `src/app/(app)/kepegawaian/data/[pegawaiId]/riwayat/mutasi/mutasi-form-sheet.tsx`
**Status:** FE **blocked** pada item #1 dan #2 di bawah.

---

## Ringkasan

Form Mutasi Pegawai sedang diubah agar field-nya tampil **kondisional** mengikuti
`jenisMutasi`, mengikuti tampilan aplikasi legacy. Perubahan ini membutuhkan **2 endpoint
baru** dari BE. Tidak ada perubahan pada kontrak `RiwayatMutasiPostRequest` /
`RiwayatMutasiPutRequest` yang sudah ada.

| # | Kebutuhan | Prioritas | Blocking FE? |
|---|-----------|-----------|--------------|
| 1 | `GET /pegawai/{id}/mutasi-context` — konteks pegawai untuk form mutasi | P1 | ✅ Ya |
| 2 | `GET /master/profesi/jabatan/{id}` — list profesi per jabatan (cascade) | P1 | ✅ Ya |
| 3 | Konfirmasi perilaku snapshot nilai lama (`*LamaId`) | P2 | ⚠️ Perlu jawaban |
| 4 | Konfirmasi + rapikan `GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` | P2 | ⚠️ Perlu jawaban |

---

## 1. `GET /pegawai/{id}/mutasi-context`

### Kenapa perlu

Form mutasi menampilkan blok **"Data Pegawai"** (read-only) berisi **6 field**: NIPAM,
Nama, Golongan, Unit Kerja, Jabatan, Profesi. Blok ini juga berfungsi sebagai **nilai
"Lama"** yang ikut dikirim saat submit — jadi FE butuh **`id` sekaligus `nama`** untuk
tiap referensi (tampilkan `nama`, kirim `id`).

Endpoint yang ada sekarang tidak ada yang cocok:

| Endpoint | Masalah |
|----------|---------|
| `GET /pegawai/{id}/session` → `PegawaiResponseSession` | Hanya `{id, nipam, nik, nama, jabatan, organisasi}`. **Tidak ada `golongan`, tidak ada `profesi`.** |
| `GET /pegawai/{id}/ringkasan` → `PegawaiResponseRingkasan` | Punya keenam field tapi sebagai **string datar** (`pangkatGolongan`, `unitKerja`, `jabatan`, `profesi`) — **tidak ada `id`**, jadi tidak bisa dipakai untuk kirim `*LamaId`. Juga 35 field untuk pakai 6. |
| `GET /pegawai/{id}` → `PegawaiResponseDetail` | Punya semua data + id, tapi payload sangat besar (biodata, gaji, 6 objek SK, rumah dinas, dll) — pemborosan untuk 6 field. |

### Kontrak yang diminta

```
GET /pegawai/{id}/mutasi-context
```

**Path parameter**

| Nama | Tipe | Wajib | Keterangan |
|------|------|-------|------------|
| `id` | `integer($int64)` | ✅ | `pegawaiId` |

**Response `200` — `SingleResultPegawaiResponseMutasiContext`**

Bungkus dengan envelope standar yang sudah dipakai (`status`, `statusText`, `message`, `data`).

```jsonc
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": {
    "id": 1234,
    "nipam": "890300426",
    "nama": "ABDUL AZIZ MIFTAHUDDIN, S.Kom.",
    "golongan":   { "id": 14, "nama": "B.4 - Pelaksana Tk.I" },
    "organisasi": { "id":  7, "nama": "SUB BAG TEKNOLOGI INFORMASI" },
    "jabatan":    { "id": 22, "nama": "Supervisor Teknologi Informasi" },
    "profesi":    { "id": 31, "nama": "Supervisor Teknologi Informasi" }
  }
}
```

**Schema `PegawaiResponseMutasiContext`**

| Field | Tipe | Nullable | Keterangan |
|-------|------|----------|------------|
| `id` | `integer($int64)` | — | `pegawaiId` |
| `nipam` | `string` | — | |
| `nama` | `string` | — | |
| `golongan` | `RefMiniResponse` | ✅ | `{id, nama}` |
| `organisasi` | `RefMiniResponse` | ✅ | `{id, nama}` — label UI = "Unit Kerja" |
| `jabatan` | `RefMiniResponse` | ✅ | `{id, nama}` |
| `profesi` | `RefMiniResponse` | ✅ | `{id, nama}` |

`RefMiniResponse` = `{ id: int64, nama: string }` — **schema yang sudah ada**, dipakai
`PegawaiResponseSession`. Tidak perlu tipe baru.

### Catatan penting

- **Label `golongan.nama`** harus sudah ter-format siap tampil: `"B.4 - Pelaksana Tk.I"`
  (pola `{golongan} - {pangkat}`, sama seperti yang FE rakit sendiri untuk combobox golongan).
  Kalau BE mengirim `"B.4"` saja, FE tidak bisa menampilkan pangkat karena tidak punya
  data pangkat di response ini.
- **Field nullable** — pegawai baru bisa saja belum punya golongan/profesi. FE render `—`.
- **Response `404`** kalau `pegawaiId` tidak ditemukan.
- Tolong daftarkan di OpenAPI (`/v3/api-docs`) agar FE bisa regenerate tipe via
  `docs/api/extract-types.js`. FE **tidak menulis tipe manual**.

---

## 2. `GET /master/profesi/jabatan/{id}`

### Kenapa perlu

Form mutasi punya rantai **Unit Kerja → Jabatan → Profesi**. Rantai pertama sudah
didukung endpoint yang ada:

```
GET /master/jabatan/organisasi/{id}   →  ListResultJabatanQuery   ✅ sudah ada
```

Rantai kedua (Profesi per Jabatan) **belum ada endpoint-nya**:

| Endpoint yang ada | Masalah |
|-------------------|---------|
| `GET /master/profesi/list` → `ListResultProfesiListResponse` | **Tidak menerima parameter apa pun.** Selalu mengembalikan seluruh profesi. |
| `GET /master/profesi?jabatanId=N` → `PageResultPageProfesiDetail` | Bisa memfilter, tapi **paged** (`size` maks 100 → risiko terpotong diam-diam) dan payload `ProfesiDetail` jauh lebih berat daripada `{id, nama}` yang dibutuhkan combobox. |

Yang diminta adalah endpoint **simetris** dengan `/master/jabatan/organisasi/{id}` yang
sudah ada, supaya FE cukup memakai helper `api.listBy()` yang sudah dipakai untuk cascade
jabatan — nol pola baru.

### Kontrak yang diminta

```
GET /master/profesi/jabatan/{id}
```

**Path parameter**

| Nama | Tipe | Wajib | Keterangan |
|------|------|-------|------------|
| `id` | `integer($int64)` | ✅ | `jabatanId` |

**Response `200` — `ListResultProfesiListResponse`** (**schema yang sudah ada**, tidak perlu tipe baru)

```jsonc
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": [
    { "id": 31, "nama": "Supervisor Teknologi Informasi" },
    { "id": 32, "nama": "Staf Teknologi Informasi" }
  ]
}
```

**Perilaku**

- Tidak paged — kembalikan seluruh profesi milik `jabatanId` tersebut.
- Jabatan tanpa profesi → `data: []` (bukan `404`).
- Urutkan berdasarkan `nama` ASC.
- Filter = `Profesi.jabatanId == {id}` (relasi `jabatanId` sudah ada di entity Profesi —
  lihat `ProfesiPostRequest.jabatanId` yang `required`).

### Precedent yang diikuti

```
GET /master/jabatan/organisasi/{id}   operationId: findByOrganisasiId
GET /master/profesi/jabatan/{id}      operationId: findByJabatanId    ← diminta
```

---

## 3. Konfirmasi — snapshot nilai "Lama" (`*LamaId`)

### Situasi

`RiwayatMutasiPostRequest` punya 4 field optional untuk nilai lama:

```
golonganLamaId?, organisasiLamaId?, jabatanLamaId?, profesiLamaId?
```

Di UI baru, nilai-nilai ini ditampilkan **read-only** (diambil dari endpoint #1) dan
**ikut dikirim** oleh FE saat submit.

### Yang perlu dikonfirmasi

**Apakah BE juga melakukan snapshot sendiri dari master pegawai saat menyimpan?**

| Jika | Konsekuensi |
|------|-------------|
| BE **tidak** snapshot — murni pakai yang dikirim FE | Perilaku sekarang aman. FE tetap kirim. |
| BE **selalu** snapshot, mengabaikan kiriman FE | FE boleh berhenti mengirim `*LamaId` (payload lebih ringkas). Tolong kabari. |
| BE snapshot **hanya jika** FE tidak mengirim | Perilaku sekarang aman, tapi tolong dikonfirmasi supaya tidak ada ambiguitas. |

Jawaban apa pun tidak memblokir FE — FE default mengirim. Ini hanya untuk menghindari
inkonsistensi data historis.

---

## 4. `GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` — sudah ada, 2 hal perlu dikonfirmasi

### Cara FE memakainya

Pada `jenisMutasi = MUTASI_GAJI` / `MUTASI_GAJI_BERKALA`, field **Gaji Pokok** punya
tombol **search** di sampingnya. Tombol itu memanggil endpoint yang **sudah ada**:

```
GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}
    operationId: findByGolonganIdAndMasaKerja
    → SingleResultDetailDasarGaji
```

FE mengambil `data.nominal` lalu mengisi field Gaji Pokok. Nilai tetap bisa
di-override manual oleh user. **Endpoint ini tidak memblokir FE.**

### 4a. Konfirmasi arti `masaKerja`

Path param `masaKerja` bertipe `integer($int32)` — **tunggal**. Di form, masa kerja
golongan dibagi dua: **MKG Tahun** dan **MKG Bulan**.

> **Pertanyaan:** apakah `masaKerja` == `mkgTahun` (tahun saja, bulan diabaikan)?
> FE saat ini mengasumsikan **ya**. Kalau salah, tolong kabari format yang benar.

### 4b. Response membocorkan entity JPA mentah

`SingleResultDetailDasarGaji` mengembalikan entity `DetailDasarGaji` apa adanya:

```
id, createdBy, createdAt, updatedBy, updatedAt, isDeleted, version,
dasarGaji: DasarGaji (objek nested penuh), mkg, golonganKode, nominal
```

FE hanya butuh `nominal`. Kolom audit (`createdBy`, `updatedBy`, `createdAt`,
`updatedAt`, `isDeleted`, `version`) dan objek `DasarGaji` nested tidak dipakai —
sekaligus membocorkan metadata internal ke klien.

Sudah ada schema yang lebih ramping di kodebase: **`DetailDasarGajiResponse`**.
Saran: pakai itu untuk endpoint ini. Prioritas rendah — FE bisa jalan tanpa ini.

---

## 5. Konteks — matriks visibilitas field (FYI, tidak butuh perubahan BE)

Supaya BE paham field mana yang dikirim kapan. Semua field di bawah **sudah ada** di
`RiwayatMutasiPostRequest`; yang berubah hanya *kapan* FE mengirimnya.

| `jenisMutasi` | Field yang dikirim (di luar field wajib) |
|---------------|-------------------------------------------|
| `PENGANGKATAN_PERTAMA` | — |
| `TERMINASI` | — |
| `MUTASI_LOKER` | `organisasiId`, `jabatanId`, `profesiId` (+ `*LamaId` terkait) |
| `MUTASI_JABATAN` | `organisasiId`, `jabatanId`, `profesiId` (+ `*LamaId` terkait) |
| `MUTASI_GOLONGAN` | `golonganId`, `mkgTahun`, `mkgBulan`, `kenaikanBerikutnya`, `mkgbTahun`, `mkgbBulan` (+ `golonganLamaId`) — **tanpa `gajiPokok`** |
| `MUTASI_GAJI` | idem `MUTASI_GOLONGAN`, **plus `gajiPokok`** |
| `MUTASI_GAJI_BERKALA` | idem `MUTASI_GOLONGAN`, **plus `gajiPokok`** |

> **Kenapa `MUTASI_GOLONGAN` tidak mengirim `gajiPokok`:** alur bisnisnya, perubahan
> golongan memicu **proses penyesuaian gaji tersendiri** di belakang layar — bukan
> diinput manual di form mutasi. Kalau BE mengharapkan `gajiPokok` juga terkirim untuk
> `MUTASI_GOLONGAN`, tolong kabari.

Field wajib yang **selalu** dikirim: `pegawaiId`, `jenisMutasi`, `nomorSk`, `jenisSk`,
`tanggalSk`, `tmtBerlaku`. Optional yang selalu tersedia: `notes`.

> Validasi kombinasi field ↔ `jenisMutasi` **diserahkan sepenuhnya ke BE**
> (keputusan FE, terkunci). FE tidak memvalidasi konsistensi ini.

---

## Definition of Done (BE)

- [ ] `GET /pegawai/{id}/mutasi-context` live + terdaftar di OpenAPI
- [ ] `GET /master/profesi/jabatan/{id}` live + terdaftar di OpenAPI
- [ ] `golongan.nama` sudah ter-format `"{golongan} - {pangkat}"`
- [ ] Jawaban atas pertanyaan #3 (snapshot `*LamaId`)
- [ ] Jawaban atas pertanyaan #4a (`masaKerja` == `mkgTahun`?)
- [ ] (Opsional) `/penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` pakai `DetailDasarGajiResponse`, bukan entity mentah
- [ ] Kabari FE agar bisa `node docs/api/extract-types.js` untuk regenerate tipe

## Kontak / referensi FE

| Hal | Lokasi |
|-----|--------|
| Form yang diubah | `src/app/(app)/kepegawaian/data/[pegawaiId]/riwayat/mutasi/mutasi-form-sheet.tsx` |
| Tipe kontrak mutasi | `src/types/kepegawaian/riwayat.ts` |
| Precedent cascade | `src/app/(app)/kepegawaian/data/tambah/tambah-form.tsx` |
| Keputusan desain 1–12 | `docs/CLAIM-ORDER-riwayat-pegawai.md` |
| Konteks domain | `docs/context/kepegawaian-riwayat.md` |

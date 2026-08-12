# BE Requirement — Data Pendukung · Pendidikan: tambah `disetujui` di response + normalisasi `isLatest`

**Untuk:** Tim Backend (Spring Boot)
**Dari:** Tim Frontend (`kepegawaian-fe`)
**Tanggal:** 2026-08-12
**Status:** FE **partially blocked** — tabel/form/lampiran bisa dibangun sekarang; **badge "Disetujui"**
menunggu perubahan BE #1. Normalisasi `isLatest` (perubahan #2) menunggu konfirmasi + implementasi BE.

> **Keputusan grill 2026-08-12 sudah ada** (lihat bagian "Jawaban BE & Keputusan") — FE bisa
> membangun badge & normalisasi sesuai keputusan. ADR: [0035](../adr/0035-pendidikan-disetujui-role-conditional-guard-islatest.md).

---

## Ringkasan

FE sedang membangun konsol **Data Pendukung** (`/kepegawaian/data/{pegawaiId}/pendukung/*`) — mirror
konsol Riwayat (ADR-0013/0014), sumber data resource `/profil/*`. Kategori pertama yang dibangun:
**Data Pendidikan** (`/profil/pendidikan`), CRUD penuh + kartu Lampiran, konsol admin/HR.

Hasil grill user (2026-08-12):

1. **Kolom status di tabel**: HR ingin melihat **badge "Disetujui"** dan **badge "Terakhir"** per
   baris pendidikan.
2. **`disetujui` = auto-`true` saat admin menulis** — konsol ini hanya admin/HR; tidak ada alur
   approval (keputusan user: *"karena ini menu admin, tidak perlu ada approval / disetujui = True"*).
3. **`isLatest` = satu-true per pegawai** — flag "pendidikan terakhir" harus dijamin hanya satu
   record `true` per `biodataId`.

| # | Kebutuhan | Prioritas | Blocking? |
|---|-----------|-----------|-----------|
| 1 | Tambah field `disetujui` (+ `tanggalDisetujui`, `disetujuiOleh`) di response `PendidikanQuery`; auto-set `true` saat POST/PUT | **P1** | ✅ Ya (badge) |
| 2 | Normalisasi `isLatest` (satu-`true` per `biodataId`, transaksional) + klarifikasi sinkron `biodata.pendidikanTerakhirId` | **P1** | ⚠️ Sebagian (badge "Terakhir" bisa dari data existing; normalisasi mencegah data rusak saat HR menulis) |

---

## Jawaban BE & Keputusan grill (2026-08-12)

Hasil sesi grill (BE + domain-modeling). Detail: [ADR-0035](../adr/0035-pendidikan-disetujui-role-conditional-guard-islatest.md).

### Jawaban untuk "mohon jawaban BE" — sinkron `biodata.pendidikanTerakhirId`

**Sudah ada, disinkronkan.** `PendidikanCommandService.handleUpdateIsLatest()`: saat `isLatest=true`
di-set (POST/PUT/seed), BE men-set `isLatest=false` ke semua baris lain milik biodata sama
(`repository.updateIsLatest`) **dan** menyalin `jenjangId` record itu ke `Biodata.pendidikanTerakhir`.
Catatan penting: sinkron ini via **bulk update** (`@Modifying @Query`), sengaja **bukan** `save()`,
supaya tidak memunculkan revisi Envers palsu pada Biodata (sudah terdokumentasi di
`docs/context/relationships.md`).

### Keputusan (7 butir)

| # | Keputusan | Kesimpulan |
|---|-----------|------------|
| 1 | Semantik `disetujui` | **Kondisional per-role** (server-side, bukan dari request): SDM menulis → `true` + stamp; non-SDM menulis → `false` + masuk antrian approval, di-`true`-kan saat approve. Badge selalu jujur. |
| 2 | Stamping | `tanggalDisetujui=now()`, `disetujuiOleh=$id` user Appwrite (identifier stabil & unik). |
| 3 | `tanggalPengajuan` | Diisi `now()` saat **create dan update** (pola Keahlian); data lama tetap null. |
| 4 | Backfill data lama | Migration V29: baris `is_deleted=0 AND changed_status=0` → `disetujui=1`, `tanggal_disetujui=created_at`, `disetujui_oleh=created_by`. Baris pending tetap `0`. |
| 5 | Guard `isLatest` | Generated column `is_latest_biodata` + `UNIQUE` (MySQL banyak-NULL): invarian satu-`true` dijamin DB saat race; baris soft-deleted ikut di-clear. |
| 6 | Pointer basi | `biodata.pendidikanTerakhirId` **dibiarkan** saat PUT `true→false` atau delete record terakhir — sinkron hanya saat `isLatest=true`; FE/HR yang menentukan pengganti. |
| 7 | Scope | **Pendidikan saja**; inkonsistensi Keahlian/PengalamanKerja/Pelatihan/LampiranSk → tech debt issue terpisah. |

### Perubahan terhadap isi dokumen ini

- "Auto-set `true` saat create/update" (butir 1) **dipersempit menjadi: auto-set `true` hanya saat penulis SDM**.
  Alasan: endpoint POST/PUT `/profil/pendidikan` sama untuk admin & self-service (dibedakan by role
  via `ChangedStatusResolver`); auto-true penuh membuat record pending tampil "Disetujui". FE sendiri
  menulis *"data di luar konsol admin boleh `disetujui=false`"* — keputusan ini memuaskan keduanya.
- Kolom DB `disetujui` dkk **sudah ada sejak V1 baseline** — tidak ada migration untuk menambah kolom;
  pekerjaan BE = expose di entity/DTO/mapper + auto-set + backfill + guard.

---

## 1. Response `PendidikanQuery` — tambah field `disetujui`

### Situasi saat ini

`GET /profil/pendidikan?biodataId=...` (list & detail) mengembalikan `PendidikanQuery`:

```jsonc
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": {
    "content": [{
      "id": 501,
      "biodataId": "3273012345678901",
      "biodataNik": "3273012345678901",
      "biodataNama": "ABDUL AZIZ MIFTAHUDDIN, S.Kom.",
      "jenjangId": 4,
      "jenjangPendidikan": { "id": 4, "nama": "S1", "shortName": "S1", "seq": 4, "isStatistik": true },
      "gelarDepan": "Dr.",
      "gelarBelakang": "S.T., M.T.",
      "jurusan": "Teknik Informatika",
      "institusi": "Universitas Gadjah Mada",
      "kota": "Yogyakarta",
      "tahunMasuk": 2010,
      "isLulus": true,
      "tahunLulus": 2015,
      "gpa": 3.72,
      "isLatest": true,
      "changedStatus": "0"
    }]
  }
}
```

**Tidak ada field `disetujui`** — padahal `KeahlianQuery` (entity saudara di modul yang sama,
`/profil/keahlian`) sudah punya `disetujui`, `tanggalPengajuan`, `tanggalDisetujui`, `disetujuiOleh`.
FE tidak bisa menampilkan badge status tanpa field ini.

### Alternatif yang dipertimbangkan (ditolak)

| Alternatif | Alasan ditolak |
|------------|----------------|
| FE menebak status (selalu tampil "Disetujui") | Berbohong ke HR bila ada data lama/import yang `disetujui=false` |
| Fetch `/profil/keahlian`-style endpoint lain | Tidak ada — data pendidikan hanya di `/profil/pendidikan` |
| Tambah field `disetujui` ke request POST/PUT | Tidak diminta — konsol admin auto-approve; request tidak boleh membawa status (FE tidak mengatur approval) |

### Perubahan yang diminta

**Tambah field berikut ke `PendidikanQuery` (response list & detail) — persis pola `KeahlianQuery`:**

| Field | Tipe | Nullable | Keterangan |
|-------|------|----------|------------|
| `disetujui` | `boolean` | ✅ | Status persetujuan record. **Auto-set `true`** saat create/update via POST/PUT dari sisi admin |
| `tanggalPengajuan` | `string` (date-time) | ✅ | Diisi BE saat record dibuat (untuk data lama boleh null) |
| `tanggalDisetujui` | `string` (date-time) | ✅ | Diisi BE saat `disetujui` menjadi `true` |
| `disetujuiOleh` | `string` | ✅ | Identifier user yang menyetujui (dari session/`pegawaiId`) |

**Aturan auto-approve (kunci):**

- `POST /profil/pendidikan` dan `PUT /profil/pendidikan/{id}` → BE **menetapkan `disetujui = true`**
  sendiri. Request body **tidak** memuat field status ini; FE tidak boleh mengirimnya.
- Data yang dibuat/import di luar konsol admin (mis. self-service pegawai) boleh `disetujui=false`
  — FE hanya membaca nilai apa adanya untuk badge.

### Dampak

**FE:**
- Regenerate tipe via `node docs/api/extract-types.js` → `disetujui` dkk. otomatis masuk ke
  `PendidikanQuery` (di `src/types/_shared.ts`)
- Tabel render badge "Disetujui" / "Belum" dari field ini; form **tidak** menyentuhnya
- Kode lain yang membaca `PendidikanQuery` tidak rusak (penambahan field = additive)

**BE:**
- Tambah 4 field di DTO `PendidikanQuery` + mapper
- Auto-set `disetujui=true` di service layer pada create/update (tanpa menunggu field dari request)
- Terdaftar di OpenAPI (`/v3/api-docs`)

---

## 2. Normalisasi `isLatest` (satu-`true` per `biodataId`)

### Situasi saat ini

`PendidikanQuery.isLatest: boolean` sudah ada. **Perilaku BE saat dua record `isLatest=true`**
untuk `biodataId` yang sama **tidak terdokumentasi** — tidak ada jaminan konsistensi.

### Perubahan yang diminta

1. **Normalisasi transaksional:** saat sebuah record di-set `isLatest=true` (via `POST` atau `PUT`),
   BE dalam transaksi yang sama men-set `isLatest=false` untuk **semua record lain** dengan
   `biodataId` yang sama. Hasil akhir selalu ≤ 1 record `true` per `biodataId`.
2. **Kasus delete:** saat record `isLatest=true` dihapus, tidak ada record pengganti otomatis
   (FE/HR yang menentukan berikutnya) — cukup pastikan tidak ada data yang tersisa `true` selain itu.
3. **Klarifikasi sinkron `biodata.pendidikanTerakhirId` (mohon jawaban BE):** `Biodata` punya
   `pendidikanTerakhirId: number` yang menunjuk **jenjang** (`JenjangPendidikanResponse`), bukan
   record pendidikan. Apakah saat `isLatest=true` di-set, BE juga memperbarui
   `biodata.pendidikanTerakhirId` ke `jenjangId` record itu? **Rekomendasi FE: ya, sinkronkan** —
   "pendidikan terakhir" di biodata dipakai laporan/statistik (`isStatistik`). Kalau tidak,
   dua sumber kebenaran bisa berbeda. Keputusan final di tangan BE — FE menyesuaikan diri.

### Alternatif yang dipertimbangkan (ditolak)

| Alternatif | Alasan ditolak |
|------------|----------------|
| FE mengelola manual (HR unset yang lama sendiri) | Rawan dua record `true`; konsol CRUD jadi tidak aman. Keputusan user: **BE yang menormalisasi** |
| Validasi di FE saja | Tidak melindungi tulis dari client lain / API langsung |

---

## Definition of Done (BE)

- [x] Jawaban tertulis soal sinkron `biodata.pendidikanTerakhirId` — **sudah ada**: `handleUpdateIsLatest()`
      menyinkronkan via bulk update (bukan `save()`, menghindari revisi Envers palsu). Lihat bagian Jawaban BE.
- [ ] `PendidikanQuery` response punya `disetujui`, `tanggalPengajuan`, `tanggalDisetujui`, `disetujuiOleh`
      (entity + DTO + `PendidikanSelects` + kedua mapper jOOQ)
- [ ] Auto-set `disetujui=true` **kondisional per-role** pada create/update SDM + stamp; approve di antrian
      approval juga stamp (request tidak memuat field status)
- [ ] Migration V29: backfill `disetujui=1` baris stabil + generated column `is_latest_biodata` guard unique
- [ ] Normalisasi `isLatest` (sudah ada di aplikasi) + pengaman level-DB (guard) + clear `is_latest` saat delete
- [ ] Terdaftar di OpenAPI (`/v3/api-docs`)
- [ ] FE regenerate tipe: `node docs/api/extract-types.js` sukses
- [ ] `bun run build` di FE — zero error

> Checklist implementasi BE (berikut file & status): `docs/claim-order-pendidikan-disetujui-islatest.md`

## Kontak / referensi FE

| Hal | Lokasi |
|-----|--------|
| Delta kategori (keputusan desain) | `docs/context/kepegawaian-pendukung-pendidikan.md` |
| Shared infra konsol | `docs/context/kepegawaian-pendukung.md` (P1–P8) |
| ADR konsol | `docs/adr/0014-data-pendukung-konsol-profil.md` |
| Tipe pendidikan saat ini | `src/types/_shared.ts` → `PendidikanQuery`; `src/types/profil/pendidikan.ts` |
| Pola field approval yang sudah ada | `src/types/_shared.ts` → `KeahlianQuery` (`disetujui`, `tanggalPengajuan`, `tanggalDisetujui`, `disetujuiOleh`) |
| BE requirement precedent | `docs/BE-REQUIREMENT-riwayat-kontrak-status-pegawai.md` |

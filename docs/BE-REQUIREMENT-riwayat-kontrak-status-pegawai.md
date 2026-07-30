# BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session

**Untuk:** Tim Backend (Spring Boot)
**Dari:** Tim Frontend (`kepegawaian-fe`)
**Tanggal:** 2026-07-30
**Status:** FE **blocked** — butuh perubahan BE

---

## Ringkasan

FE sedang membangun halaman **Riwayat Kontrak Kerja** (`/kepegawaian/data/{pegawaiId}/riwayat/kontrak`).
Page ini punya aturan bisnis: **hanya pegawai dengan `statusPegawai = KONTRAK` yang bisa menambah/mengedit
data kontrak**. Pegawai dengan status lain hanya bisa melihat history (read-only).

Untuk menerapkan aturan ini, FE butuh `statusPegawai` dari endpoint **session** yang sudah dipakai di layout.

| # | Kebutuhan | Prioritas | Blocking? |
|---|-----------|-----------|-----------|
| 1 | Tambah field `statusPegawai` di response `GET /pegawai/{id}/session` | **P1** | ✅ Ya |

---

## 1. `GET /pegawai/{id}/session` — tambah field `statusPegawai`

### Situasi saat ini

Endpoint `GET /pegawai/{id}/session` sudah ada dan dipakai di layout riwayat
(`layout.tsx`) untuk menampilkan header `"[NIPAM] (Nama)"`.

**Response skema saat ini — `PegawaiResponseSession`:**

```jsonc
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": {
    "id": 1234,
    "nipam": "890300426",
    "nik": "3273012345678901",
    "nama": "ABDUL AZIZ MIFTAHUDDIN, S.Kom.",
    "jabatan":  { "id": 22, "nama": "Supervisor Teknologi Informasi" },
    "organisasi": { "id": 7, "nama": "SUB BAG TEKNOLOGI INFORMASI" }
  }
}
```

**Tidak ada `statusPegawai`** — FE tidak punya cara untuk mengetahui apakah pegawai
berstatus KONTRAK atau bukan dari endpoint ini.

### Alternatif yang dipertimbangkan (ditolak)

| Alternatif | Alasan ditolak |
|------------|----------------|
| Fetch `GET /pegawai/{id}` (full detail) hanya untuk dapat `statusPegawai` | +1 request penuh, payload besar (biodata, 6 objek SK, gaji, dll) hanya untuk 1 field |
| Fetch `GET /pegawai/{id}/ringkasan` | Juga +1 request ekstra, endpoint ini sebenarnya untuk panel ringkasan — bukan untuk gate logic |
| Parse dari data lain (jabatan/organisasi) | Tidak mungkin — `statusPegawai` adalah field independen |

Solusi paling bersih dan minimal: **tambah 1 field ke endpoint session yang sudah ada**.

### Perubahan yang diminta

**Tambah field `statusPegawai: string` ke `PegawaiResponseSession`.**

```jsonc
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": {
    "id": 1234,
    "nipam": "890300426",
    "nik": "3273012345678901",
    "nama": "ABDUL AZIZ MIFTAHUDDIN, S.Kom.",
    "statusPegawai": "KONTRAK",               // ← BARU
    "jabatan":  { "id": 22, "nama": "Supervisor Teknologi Informasi" },
    "organisasi": { "id": 7, "nama": "SUB BAG TEKNOLOGI INFORMASI" }
  }
}
```

**Spesifikasi:**

| Field | Tipe | Nullable | Enum | Keterangan |
|-------|------|----------|------|------------|
| `statusPegawai` | `string` | ✅ (bisa null) | `KONTRAK`, `CAPEG`, `PEGAWAI`, `CALON_HONORER`, `HONORER`, `NON_PEGAWAI` | Status kepegawaian sesuai entity `Pegawai.statusPegawai` |

- Nilai enum harus **persis sama** dengan yang sudah digunakan di endpoint `GET /pegawai` dan `POST /pegawai` — tidak boleh ada nilai baru.
- `nullable` karena skenario non-pegawai (`PegawaiResponseSession` mungkin tidak punya relasi ke data kepegawaian).

### Dampak

**FE:**
- Regenerate tipe via `node docs/api/extract-types.js` → field `statusPegawai` otomatis masuk ke `PegawaiResponseSession`
- Layout (`layout.tsx`) yang sudah memakai session endpoint **tidak perlu diubah** — field baru tidak merusak kode yang ada
- Halaman kontrak membaca `statusPegawai` dari response session dan toggle visibilitas tombol aksi

**BE:**
- Tambah field `statusPegawai` di DTO `PegawaiResponseSession`
- Pastikan mapper mengisi field ini dari `Pegawai.statusPegawai`
- Response 404 tetap 404 — tidak ada perubahan flow error

---

## 2. Konteks — bagaimana FE memakai field ini (FYI, tidak butuh perubahan BE)

Supaya BE paham konteksnya:

```
layout.tsx (sessionQuery → PegawaiResponseSession)
  └─ header: "[NIPAM] (Nama)"  
  └─ kontrak/page.tsx
       ├─ baca sessionQuery.data.statusPegawai
       ├─ if statusPegawai === "KONTRAK":
       │    └─ render tombol "+ Tambah Kontrak" + ikon Edit/Hapus di baris
       └─ else:
            └─ sembunyikan tombol "+" dan ikon aksi — tabel read-only
```

---

## Definition of Done (BE)

- [ ] Field `statusPegawai` ditambahkan ke `PegawaiResponseSession` DTO
- [ ] Response `GET /pegawai/{id}/session` menyertakan `statusPegawai`
- [ ] Terdaftar di OpenAPI (`/v3/api-docs`)
- [ ] FE regenerate tipe: `node docs/api/extract-types.js` sukses
- [ ] `bun run build` di FE — zero error

## Kontak / referensi FE

| Hal | Lokasi |
|-----|--------|
| Layout yang pakai session | `src/app/(app)/kepegawaian/data/[pegawaiId]/riwayat/layout.tsx` |
| Tipe session saat ini | `src/types/pegawai/pegawai.ts` → `PegawaiResponseSession` |
| Tipe shared | `src/types/_shared.ts` → `StatusKepegawaian` |
| Enum labels FE | `src/lib/enum-labels.ts` → `labelStatus()` |
| BE requirement precedent | `docs/BE-REQUIREMENT-form-mutasi.md` |


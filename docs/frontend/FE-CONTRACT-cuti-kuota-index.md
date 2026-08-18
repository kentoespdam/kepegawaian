# FE Contract — `GET /cuti/kuota` (Index Kuota Cuti)

> Dokumen kontrak untuk tim **Frontend (Next.js)**: perubahan kontrak response `GET /cuti/kuota` — envelope, shape data, dan perilaku halaman kosong. **Aksi FE wajib** sebelum/bersamaan dengan rilis backend ini.

| Item | Nilai |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Tanggal | 2026-08-18 |
| ADR terkait | [ADR-0040](../adr/0040-cuti-kuota-index-pagerequest-dengan-kuota-tahun-sebelumnya.md) (deviasi index kuota), [ADR-0014](../adr/0014-get-by-id-missing-row-returns-404.md) (detail → 404) |
| Ruang lingkup | Hanya `GET /cuti/kuota` (index). Detail `/{id}` & sisa `/{pegawaiId}/{tahun}/sisa` **tidak berubah** |

---

## Status Perubahan

| Perubahan | Status | Dampak FE |
|-----------|--------|-----------|
| Envelope index: `SingleResult` → **`PageResult`** | 🔧 **BERUBAH (breaking)** | Field `message` hilang; tambahan `timestamp` sudah ada di keduanya |
| Halaman kosong: **404 "Data not found!"** → **200 + empty page** | 🔧 **BERUBAH (breaking)** | Kode FE yang menganggap 404 = "tidak ada data" harus diganti: sekarang selalu 200, cek `data.page.content.length === 0` |
| Rename wire: `data.additional` → **`data.kuotaTahunSebelumnya`** | 🔧 **BERUBAH (breaking)** | Akses `data.additional` → `data.kuotaTahunSebelumnya` |
| `GET /cuti/kuota/{id}` & `/{pegawaiId}/{tahun}/sisa` | ✅ **TIDAK berubah** | Tetap `SingleResult`; 404 saat data tidak ada |

---

## 1. Envelope Baru

**Sebelumnya** (`SingleResult`):

```json
{
  "status": 200,
  "statusText": "OK",
  "errors": [],
  "message": "Data Found",
  "data": { "...": "..." },
  "timestamp": "2026-08-18 10:00:00"
}
```

**Sekarang** (`PageResult` — sama seperti semua endpoint list/page lain, mis. `/cuti/jenis`, `/cuti/pengajuan`):

```json
{
  "status": 200,
  "statusText": "OK",
  "data": { "...": "..." },
  "timestamp": "2026-08-18 10:00:00"
}
```

Perbedaan: **`message` dan `errors` tidak ada** di envelope index. Jangan bergantung pada `message` untuk status sukses — cukup cek `status === 200`.

## 2. Shape `data`

```jsonc
{
  "data": {
    "page": {
      "content": [ /* CutiKuotaResponse tahun berjalan (sesuai filter `tahun`, default tahun sekarang) */ ],
      "totalElements": 123,
      "totalPages": 7,
      "number": 0,
      "size": 20,
      "numberOfElements": 20,
      "first": true,
      "last": false
      // ... properti Page standard Spring
    },
    "kuotaTahunSebelumnya": [ /* rename dari `additional`: baris kuota tahun−1 utk pegawai di halaman ini */ ]
  }
}
```

- **`page`** — konten & metadata pagination (kontrak Page standard, tidak berubah).
- **`kuotaTahunSebelumnya`** (dulu `additional`) — daftar `CutiKuotaResponse` kuota **tahun sebelumnya** (tahun − 1) untuk pegawai yang sama dengan isi `page`. Bisa kosong. Dipakai untuk konteks carry-over di grid (sisa tahun lalu), **bukan** pengganti `sisaKuota` per baris.

## 3. Halaman Kosong — JANGAN cek 404

**Sebelumnya**: tidak ada data → **HTTP 404** `{ message: "Data not found!" }`.
**Sekarang**: tidak ada data → **HTTP 200** dengan page kosong:

```json
{
  "status": 200,
  "data": {
    "page": { "content": [], "totalElements": 0, "totalPages": 0, "number": 0, "size": 20, "first": true, "last": true },
    "kuotaTahunSebelumnya": []
  }
}
```

Perilaku FE yang benar: **selalu parse body** setelah `response.ok`, lalu cek `data.page.content.length`. Jangan treat 404 sebagai "data kosong".

## 4. Contoh Kode FE (Next.js)

```ts
const res = await fetch(`/api/cuti/kuota?tahun=${tahun}`, {
  headers: { Authorization: `Bearer ${token}` },
});
if (!res.ok) throw new Error(`HTTP ${res.status}`);

const body = await res.json(); // selalu aman: tidak ada lagi 204/404 untuk index
const { page, kuotaTahunSebelumnya } = body.data;
const rows = page.content; // [] saat kosong — bukan error
```

## 5. Checklist Aksi Tim FE

- [ ] Ganti akses `data.additional` → `data.kuotaTahunSebelumnya` (rename di typed interface / model).
- [ ] Hapus ketergantungan pada `message` ("Data Found"/"Data not found!") di halaman kuota — cek `status` saja.
- [ ] Ubah penanganan "tidak ada data": jangan lagi mengharap 404 — cek `data.page.content.length === 0` pada 200.
- [ ] Pastikan tipe envelope `PageResult` (tanpa `errors`/`message`) tidak dipakai kode bersama yang membutuhkan field tsb.
- [ ] Endpoint detail (`/{id}`, `/{pegawaiId}/{tahun}/sisa`) tetap `SingleResult` + 404 — jangan diubah.

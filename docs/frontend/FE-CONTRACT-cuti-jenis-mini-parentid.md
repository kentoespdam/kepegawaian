# FE Contract — `CutiJenisMiniResponse` kini membawa `parentId`

> Dokumen kontrak untuk tim **Frontend (Next.js)**: shape nested **mini jenis cuti** berubah dari `{id, nama}` menjadi `{id, nama, parentId}`. Perubahan **aditif (non-breaking)** — tidak ada field yang hilang/berubah nama.

| Item | Nilai |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Tanggal | 2026-08-18 |
| ADR terkait | Tidak ada (perubahan aditif, tanpa keputusan arsitektur) |
| Ruang lingkup | Semua endpoint yang memuat `CutiJenisMiniResponse` sebagai objek nested |

---

## 1. Shape Baru

**Sebelumnya:**

```jsonc
{
  "id": 2,
  "nama": "Cuti Sakit"
}
```

**Sekarang:**

```jsonc
{
  "id": 2,
  "nama": "Cuti Sakit",
  "parentId": 1   // Long | null
}
```

- **`parentId`** — id dari **parent jenis cuti** (pohon `CutiJenis` ber-induk). `null` berarti jenis cuti ini **root** (tidak berinduk) atau parent-nya tidak tersedia di query.
- Tidak ada field lain yang berubah.

## 2. Di Mana Mini Ini Muncul (Nested)

| Endpoint | Field nested | Isi `parentId` |
|----------|--------------|----------------|
| `GET /cuti/jenis` (index) | `parent` | `null` (parent dari parent tidak di-select query) |
| `GET /cuti/jenis/{id}` (detail) | `parent` | `null` (sama) |
| `GET /cuti/pengajuan` (index) | `jenisCuti` | `null` (jenis root) |
| `GET /cuti/pengajuan` (index) | `subJenisCuti` | **id `jenisCuti`** — parent-nya memang jenis cuti tsb. |
| `GET /cuti/pengajuan/{pegawaiId}/pegawai` | `jenisCuti` / `subJenisCuti` | sama seperti index |
| `GET /cuti/pengajuan/{id}` (detail) | `jenisCuti` / `subJenisCuti` (termasuk di `refCuti` nested) | sama seperti index |

## 3. Contoh JSON

`GET /cuti/pengajuan` — satu baris (potongan):

```jsonc
{
  "jenisCuti":   { "id": 1, "nama": "Cuti Tahunan", "parentId": null },
  "subJenisCuti": { "id": 2, "nama": "Cuti Sakit",   "parentId": 1 }
}
```

Relasi yang bisa dipakai FE: **`subJenisCuti.parentId === jenisCuti.id`** untuk baris yang sama.

`GET /cuti/jenis/list` — kini mengembalikan **`CutiJenisMiniResponse` langsung** (bukan `CutiJenisResponse` + `parent` nested), satu baris (potongan):

```jsonc
{
  "id": 2,
  "nama": "Cuti Sakit",
  "parentId": 1   // null untuk jenis root
}
```

> `parentId` di `/cuti/jenis/list` di-select langsung dari kolom `parent_id` — **bernilai riil** (null hanya jika jenis tsb. root). Berbeda dengan mini nested di `/cuti/jenis/*` (index/detail) dan list pengajuan, yang parent-nya diwakili mini `{id,nama}` tanpa `parentId` (tidak di-select query → `null`). Jangan pakai `parentId` untuk membedakan "root vs data rusak" pada mini nested — pakai `parent == null` (di `/cuti/jenis/*`) atau `subJenisCuti == null` (di pengajuan) untuk itu.

## 4. Contoh Kode FE (Next.js)

```ts
// typed interface — tambah satu field
interface CutiJenisMini {
  id: number;
  nama: string;
  parentId: number | null;
}

// grup sub-jenis di bawah parent-nya (data pengajuan)
const byParent = new Map<number, CutiJenisMini[]>();
for (const row of rows) {
  if (!row.subJenisCuti) continue;
  const key = row.subJenisCuti.parentId ?? 0;
  byParent.set(key, [...(byParent.get(key) ?? []), row.subJenisCuti]);
}
```

## 5. Checklist Aksi Tim FE

- [ ] Tambah `parentId: number | null` ke interface/model `CutiJenisMini` (dipakai di halaman jenis & pengajuan).
- [ ] Gunakan `parentId` untuk mengelompokkan sub-jenis ke parent-nya di dropdown/pohon jenis cuti (opsional — kontrak baru).
- [ ] Jangan jadikan `parentId === null` sebagai penanda "rusak" — null valid untuk root & mini yang parent-nya tidak ikut di-select query.
- [ ] Tidak ada perubahan lain: field `id`, `nama`, dan struktur envelope (`PageResult`/`SingleResult`) tetap.

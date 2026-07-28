# Optimasi `GET /pegawai` — DTO Tabel Ramping — Claim Order & Checklist

> Ganti response `GET /pegawai` (root) dari `PegawaiResponse` (~40 field, 8 join, over-fetch)
> ke DTO baru **`PegawaiTableResponse`** — hanya 12 kolom yang benar-benar dirender FE tabel.
> Epic beads: **`kepegawaian-chp`** · Pattern rujukan: `docs/master-query-optimization-pattern.md`
> CODING_RULES: `CODING_RULES.md` (max 120 baris/file)

---

## Konteks

FE tabel pegawai hanya butuh kolom berikut untuk render:

`id, nipam, nama, jenisKelamin, pangkat/golongan, organisasi.nama, jabatan.nama,
profesi.nama, tanggalLahir, tmtPensiun, statusKawin, kodePajak, isBpjs, statusPegawai`

Endpoint sekarang mengembalikan `PegawaiResponse` (nested, berat) yang **over-fetch ~40 field**
sekaligus **kekurangan** `jenisKelamin`, `tanggalLahir`, `statusKawin`, `isBpjs`. Tugas ini menutup
kedua gap itu dengan DTO khusus tabel.

---

## Keputusan Grilling

| Aspek | Keputusan |
|-------|-----------|
| **DTO** | Buat **baru** `PegawaiTableResponse`. **JANGAN sentuh** `PegawaiResponse` (dipakai bersama `/{nipam}/nipam`). |
| **Bentuk** | Hibrida — enum & pangkat/golongan sebagai **string label**; `organisasi`/`jabatan`/`profesi` sebagai `{id, nama}`. |
| **`isBpjs`** | = `PEGAWAI.IS_ASKES` (boolean). **Askes = nama lama BPJS Kesehatan.** Tidak ada kolom `IS_BPJS`; tidak pakai multiset `KARTU_IDENTITAS`. |
| **`jenisKelamin`** | `BIODATA.JENIS_KELAMIN` → label `"Laki-Laki"`/`"Perempuan"`. |
| **`tanggalLahir`** | `BIODATA.TANGGAL_LAHIR` (`LocalDate`). |
| **`statusKawin`** | `BIODATA.STATUS_KAWIN` → `EStatusKawin.toString()`. |
| **`kodePajak`** | `GAJI_PENDAPATAN_NON_PAJAK.KODE` (String). |
| **`pangkatGolongan`** | gabungan `golongan + ' - ' + pangkat`. |
| **Filter & sort** | **Reuse `PegawaiRequest` apa adanya** — `buildConditions()` & `ALLOWED_SORTS` yang sudah ada. Tak tambah param. |
| **Path** | **Ganti di root** `GET /pegawai` → `Page<PegawaiTableResponse>`. Breaking change **terkoordinasi dgn FE**. |
| **`findPage()` lama** | **Hapus** (service + repo). Sisakan `pegawaiResponseFields()` — masih dipakai `findByNipam`. |

---

## Bentuk DTO Target

```java
public record PegawaiTableResponse(
    Long id,
    String nipam,
    String nama,
    String jenisKelamin,     // "Laki-Laki" / "Perempuan"
    LocalDate tanggalLahir,
    LocalDate tmtPensiun,
    String statusKawin,      // label EStatusKawin
    String kodePajak,        // "K/1"
    Boolean isBpjs,          // = IS_ASKES
    String pangkatGolongan,  // "III/c - Penata"
    String statusPegawai,    // label EStatusPegawai
    Organisasi organisasi,   // {id, nama}
    Jabatan jabatan,         // {id, nama}
    Profesi profesi          // {id, nama}
) {}
```

Pola label reusable dari `PegawaiRingkasanMapper`:
- `jenisKelamin`: `EJenisKelamin.values()[b] == LAKI_LAKI ? "Laki-Laki" : "Perempuan"`
- `statusKawin`: `EStatusKawin.values()[b].toString()`
- `statusPegawai`: `EStatusPegawai.values()[b].value`
- `pangkatGolongan`: `golongan + " - " + pangkat`

---

## Claim Order (kerjakan berurutan — tiap task blok task berikutnya)

| # | Beads ID | Task | Blocked by | Status |
|---|----------|------|-----------|--------|
| T1 | `kepegawaian-chp.1` | DTO `PegawaiTableResponse` (record) + inner mini records | — | ☑ done |
| T2 | `kepegawaian-chp.2` | Proyeksi `pegawaiTableFields()` di `PegawaiQueryRepository` | T1 | ☑ done |
| T3 | `kepegawaian-chp.3` | Mapper `PegawaiTableRecordMapper.mapTableResponse()` | T2 | ☑ done |
| T4 | `kepegawaian-chp.4` | `findTablePage()` repo+service, swap `index()` ke DTO baru | T3 | ☑ done |
| T5 | `kepegawaian-chp.5` | Hapus `findPage()` lama; verifikasi `pegawaiResponseFields` tetap dipakai `findByNipam` | T4 | ☑ done |
| T6 | `kepegawaian-chp.6` | Update `docs/context/language-pegawai.md` + build & test | T5 | ☑ done |

Isi checkbox → `☑` dan status → `in_progress`/`done` sejalan dgn `bd update`.

---

## Yang TIDAK Boleh Disentuh

- `PegawaiResponse` + `PegawaiRecordMapper.mapResponse()` — masih dipakai `/{nipam}/nipam`.
- `pegawaiResponseFields()` — masih dipakai `findByNipam`. **Hanya `findPage()` yang dihapus.**
- `PegawaiListResponse` (`/list`), `PegawaiResponseRingkasan` (`/{id}/ringkasan`), detail (`/{id}`), session.
- Write-side: `PegawaiRequest` boleh dibaca/reuse tapi **tak diubah**; jangan sentuh Post/Put/Patch requests.

---

## Definition of Done

- [x] `GET /pegawai` mengembalikan `Page<PegawaiTableResponse>` dgn tepat 12 kolom render.
- [x] Tidak ada join/kolom yang tak dipakai DTO tabel (PENDIDIKAN & GRADE di-drop).
- [x] `findPage()` lama terhapus; `findByNipam` & `PegawaiResponse` aman.
- [x] Filter & sort lama tetap jalan (reuse `PegawaiRequest`).
- [x] `./gradlew build` & `./gradlew test` hijau.
- [x] `docs/context/language-pegawai.md` terupdate (level read tabel + `isBpjs=IS_ASKES`).
- [ ] Breaking change dikonfirmasi terkoordinasi dgn FE. _(tunggu FE)_
- [x] Tiap file ≤ 120 baris.

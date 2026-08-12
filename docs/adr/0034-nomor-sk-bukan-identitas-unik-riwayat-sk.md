# Nomor SK bukan identitas unik Riwayat SK — boleh terpakai ulang

> **Status:** accepted — keputusan sesi grilling 2026-08-12 (grill-with-docs + domain-modeling).

## Konteks

`riwayat_sk.nomor_sk` di level DB hanya index biasa (`idx_rwt_sk_nomor_sk`), **tanpa unique constraint** — keunikan dijaga semata di lapisan aplikasi lewat cek `exists()` sebelum create, di **tiga** tempat:

| Tempat | Cek hari ini |
|--------|--------------|
| `RiwayatSkCommandService.save()` (`RiwayatSkPostRequest.getSpecification()`) | `pegawai + nomorSk + jenisSk + golonganId` |
| `RiwayatMutasiCommandService.save()` (`getSpecificationMutasi()`) | `pegawai + riwayatSk.nomorSk` |
| `RiwayatTerminasiCommandService.save()` (`getTerminasiSpecification()`) | `pegawai + skTerminasi.nomorSk` |

**Kebutuhan bisnis:** saat jabatan **PLT (Pelaksana Tugas)** berakhir, pegawai kembali ke jabatan definitifnya, dan peristiwa itu dicatat sebagai **baris SK/mutasi baru yang memakai ulang nomor SK pengangkatan asli** (dokumen "menyambung" SK awal). Ketiga cek di atas memblokir kebutuhan ini: pegawai sama, nomor SK sama, jenis SK (`SK_JABATAN`) sama, golongan sama — hanya tanggal SK yang berbeda.

**Efek samping yang ikut ditemukan:** `RiwayatKontrakCommandService.delete()` me-soft-delete **semua** `riwayat_sk` yang `nomorSk`-nya cocok dengan nomor kontrak (per pegawai). Dengan nomor SK yang boleh dobel, delete satu kontrak bisa ikut menghapus SK lain yang kebetulan bernomor sama.

## Keputusan

1. **`nomorSk` adalah referensi dokumen, bukan identitas baris Riwayat SK** — identitas baris tetap `id`. Nomor SK boleh terpakai ulang antar baris selama data barisnya berbeda.
2. Ketiga cek duplikat diganti jadi **guard anti-duplikat eksak**:
   - `RiwayatSk`: `(pegawai, nomorSk, jenisSk, tanggalSk)` — `golonganId` diganti `tanggalSk`.
   - `RiwayatMutasi`: `(pegawai, nomorSk, tanggalSk)` — tambah `riwayatSk.tanggalSk`.
   - `RiwayatTerminasi`: `(pegawai, nomorSk, tanggalSk)` — tambah `skTerminasi.tanggalSk`.
   - Baris identik persis tetap ditolak `ConflictException`; nomor SK terpakai ulang dengan tanggal SK berbeda **diterima**.
3. **Jalur `update()` ikut guard yang sama** (spec yang mengecualikan `id` sendiri) — hari ini `update()` ketiganya tanpa cek duplikat.
4. **`RiwayatKontrak` ditambah FK `riwayat_sk_id`** (pola persis `RiwayatMutasi`: `@UniqueConstraint(pegawai_id, riwayat_sk_id)`); `delete()` kontrak soft-delete baris SK **lewat FK ini**, bukan cocokkan `nomorSk`.
5. **PLT tidak dimodelkan** — tidak ada entitas/field/flag baru; cukup istilah domain di glossary.

## Considered Options

- **Hapus semua cek duplikat total** (ditolak): paling sederhana, tapi double-submit baris identik (salah klik / input ulang) tidak terdeteksi sama sekali.
- **Guard anti-duplikat eksak** (dipilih): satu aturan konsisten di semua entry point; kasus PLT lolos, baris identik persis tetap tertangkap.
- **Kontrak: perketat filter delete jadi `(pegawai, nomorSk, tanggalSk)` tanpa FK** (ditolak): cukup untuk sekarang, tapi menyisakan asumsi "nomor SK bisa jadi kunci" di satu tempat dan rapuh terhadap perubahan aturan berikutnya. FK (dipilih) konsisten dengan pola `RiwayatMutasi` yang sudah ada dan menghapus tebakan berbasis nomor selamanya.

## Consequences

- **Kasus "kembali ke jabatan semula" (PLT berakhir) bisa dicatat** di ketiga jalur input (SK langsung, mutasi, terminasi) dengan nomor SK terpakai ulang.
- **Implementasi butuh:** ubah 3 spec di DTO, tambah guard di jalur `update()` SK (dan selaraskan mutasi/terminasi), tambah field `riwayatSk` di entity `RiwayatKontrak` + mapper + backfill migrasi (cocokkan `pegawai + nomorSk` — data lama unik sehingga aman) + ALTER TABLE, regenerasi JOOQ (ADR-0004/0012), ubah `RiwayatKontrakCommandService.delete()`.
- **Tidak ada perubahan skema `riwayat_sk`** — index `idx_rwt_sk_nomor_sk` tetap, tidak perlu unique constraint. Data yang sudah terlanjur terlanjur unik tidak perlu dimigrasi.
- **`riwayat_kontrak` tetap unik per `(pegawai, nomor_kontrak)`** — di luar scope; kontrak mempertahankan keunikannya sendiri.
- **"Slot SK Terkini" & writeback tidak berubah** — slot tetap memilih baris terbaru per Jenis SK (`tmt_berlaku` desc); baris SK "kembali" menjadi SK jabatan terkini otomatis.
- Backfill FK kontrak: baris `riwayat_kontrak` lama dipetakan ke `riwayat_sk` via `(pegawai_id, nomor_kontrak = nomor_sk)` — aman karena sebelum keputusan ini nomor SK memang unik per pegawai.

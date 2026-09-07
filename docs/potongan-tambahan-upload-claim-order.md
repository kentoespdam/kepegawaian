# Claim Order — Upload Potongan Tambahan (Fesod Lokal)

Epic: **kepegawaian-7hjt** · ADR: **0056** (`docs/adr/0056-potongan-tambahan-fesod-lokal-menggantikan-forward-legacy.md`) · Hasil grilling 2026-09-07.

Satu issue, satu owner. Kerjakan langkah **berurutan**. Referensi perilaku: legacy `smartoffice/server/application/direct/payrollverification.php` → `UploadAdhocComponent`.

## Ringkasan Keputusan (jangan ditebak ulang)

| Aspek | Keputusan |
|---|---|
| Forward legacy (`PATCH /upload/{id}/additional_gaji`) | **Dihapus** — parsing lokal, tulis `ADD_` + recalculate di sini |
| File input | **Form Potongan Gaji** multi-sheet (1 sheet/organisasi), header baris 9, label kategori baris 10 mulai kolom E |
| Deteksi header | Dinamis: baris dengan B=`NAMA` && C=`NIPAM`; label = baris berikutnya, kolom E..label terakhir |
| Kode | `ADD_<SLUG(label)>` — uppercase, non-alnum→`_`, collapse run; `nama`=label asli; `jenisGaji=POTONGAN`; `urut=99` |
| Nilai | kosong/0 → skip; negatif → **error** (file ditolak) |
| NIPAM | normalize ala TKK; duplikat antar-sheet → error; **wajib** ada di `GajiBatchMaster` batch ini → else error |
| Error handling | all-or-nothing, digest 20 error ala TKK/KPI |
| Re-upload | **replace**: hapus `ADD_%` milik batch → insert ulang |
| Window status | hanya `WAIT_VERIFICATION_PHASE_2` |
| Recalculate | pakai `recalculateAdditional` eksisting — **JANGAN fork** |
| Lampiran | tetap disimpan + `GajiBatchRootLampiran` `POTONGAN_TAMBAHAN` |
| Response | `SavedResult<String>` `"{n} success"` (ADR-0031) |
| Template | per-batch digenerate; sheet per `namaOrganisasi` (pengganti `mail_code`); pre-fill NO/NAMA/NIPAM/GAJI |

## Claim Order

- [ ] **T1 · Parsing core** — `GajiBatchPotonganTambahanBatchService`:
  - `@ExcelProperty(index=…)` model kolom A..X (no, nama, nipam, gaji, Double potongan) — pola `GajiBatchPotonganTkkExcelRow`.
  - Baca SEMUA sheet (loop index; pola legacy `getSheetCount`). Deteksi header B/C; label dari baris berikutnya; baris data A,B,C non-blank; baris total otomatis lolos (B/C kosong).
  - Slug kode + prefix `ADD_`; kumpulkan `ValidatedRow(rowNum, sheetName, nipam, kode, nama, nilai)`.
- [ ] **T2 · Validasi & guard** — status guard `WAIT_VERIFICATION_PHASE_2`; duplikat antar-sheet; NIPAM→master batch (chunk 1000); negatif; digest 20. Satu error saja → `BadRequestException`, no write.
- [ ] **T3 · Persist** — simpan file (`FileUploadUtil.uploadPenggajian`) + lampiran; delete `ADD_%` per batch (JOOQ repo baru, pola `GajiBatchPotonganTkkBatchRepository`); batch insert; recalculate per master terdampak (expose `recalculateAdditional` — cek `gitnexus_impact` dulu); return `{n} success`.
- [ ] **T4 · Controller** — `PATCH /upload/{rootBatchId}` signature tetap; tambah `GET /template/download/{rootBatchId}` (stub → T5). `PreAuthorize` mengikuti pola existing (WRITE utk upload, READ utk template).
- [ ] **T5 · Template generator** — fesod write, sheet per `namaOrganisasi`, pre-fill dari `GajiBatchMaster` (NO/NAMA/NIPAM/GAJI=gaji_pokok), header baris 9–10 identik form (label kategori standar form), A6 judul + A7 `BULAN {periode}`. Filename `potongan_tambahan_{rootBatchId}.xlsx`.
- [ ] **T6 · Test** — unit parser (multi-sheet, typo header, baris total, 0/negatif, duplikat antar-sheet, unknown NIPAM), slug, guard status, replace semantics, recalculate terpanggil; controller test `"{n} success"`.
- [ ] **T7 · Ship** — `./gradlew clean compileJava` + `./gradlew test` zero error → `detect_changes` → close issue → commit `feat: potongan tambahan upload via fesod lokal` → push.

## Dependency

T1 → T2 → T3 → T4 → T5 → T6 → T7. T5 butuh T3 (butuh data master + pola repo). T6 boleh mulai paralel dengan T5 selama T1–T3 hijau.

## Follow-up (di luar issue ini)

- `downloadPotonganGaji` masih forward ke legacy `/export/potongan/` — buat issue terpisah saat ini selesai.
- DB baru belum punya `mail_code` — kalau nanti tersedia, ganti pengganti `org_group` di template generator (T5).

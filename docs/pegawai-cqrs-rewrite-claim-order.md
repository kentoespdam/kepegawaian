# Pegawai — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring

Epic **kepegawaian-khj** — Rewrite modul **Pegawai** ke pola CQRS yang sama dengan master & profil: **tulis JPA, baca JOOQ**. Memecah `PegawaiServiceImpl` (244 baris, 14 collaborator) jadi `PegawaiCommandService` (tulis, saga `@Transactional`) + `PegawaiQueryService` (baca, JOOQ).

Urutan klaim mengikuti **dependency**, bukan nomor issue. Issue dalam wave yang sama **menyentuh file berbeda dan tidak saling blok** → boleh diklaim paralel. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca keputusan manajer yang dikunci di `bd show kepegawaian-khj` (field `design`). Keputusan tidak boleh diubah tanpa eskalasi:

1. **Baca = proyeksi JOOQ.** `PegawaiResponse` (list/page & `/{nipam}/nipam`) **tetap nested** demi kompat FE, tapi tiap objek nested = **mini-projection** `row(id+label)` (biodata: `nik,nama,gelarDepan,gelarBelakang`; organisasi/jabatan/profesi `id,nama`; golongan `id,golongan,pangkat`; grade `id,grade`; kodePajak `id,nama`). **Tidak** ada DTO berat baru / MapStruct / sqids.
2. **Mutasi kembalikan `{status,id}`** via `SavedStatus.build(ESaveStatus, entity.getId())` — **tanpa** re-read.
3. **`PageRequest` + `SortParam` ditulis di dalam modul pegawai** (commons dihapus di rewrite berikutnya). `SortParam` = `final class`, `resolve(sortBy,sortDir,Map<String,Field<?>> allowedSorts,Field<?> defaultColumn)`; `sortBy` tak dikenal/blank → `defaultColumn` (ID), **tanpa error**; hanya `"asc"` eksplisit = ascending.
4. **Soft-delete dipertahankan;** DELETE tak pernah hard-delete.
5. **Ringkasan baca tabel lintas modul langsung** (ADR-0020): `riwayat_sk` multiset 7 slot SK terbaru-per-jenis, `pendidikan` `is_latest=true`, `kartu_identitas` per nama jenis (NPWP/JPn/BPJS/ID Card). Perakitan string **tetap di Java** pada mapper.
6. **Saga tulis** (ADR-0021): `save()` = satu `@Transactional`, helper privat per cabang status, `authService.createUser` (Appwrite) **dipanggil paling akhir**. `save()` versi Command **harus melempar** exception (jangan ditelan) agar `saveBatch` `@Transactional` bisa rollback.
7. **`EXCLUDED_JABATAN_IDS` yang benar = `{1,2,3,25}`** (controller sudah benar; `{1,2,3,4}` di service lama = **BUG**). Externalisasi ke `application.yml` via `@ConfigurationProperties`.
8. **Endpoint PATCH** (`patchGaji`, `patchProfil`) **dipertahankan apa adanya** — FE punya menu update-profil tersendiri.

**Per-issue wajib:** `gitnexus_impact(direction:"upstream")` sebelum edit symbol · `detect_changes` sebelum commit · warn HIGH/CRITICAL · soft-delete dipertahankan · **jangan** rename via find/replace (pakai `gitnexus_rename`) · `./gradlew test` hijau.

---

## WAVE 0 — Epic (gerbang, tidak dikerjakan langsung)

- [x] **khj** (EPIC): rewrite CQRS modul Pegawai · `P1` · deps: —

> Kontainer keputusan + grounding. Tidak ada koding di epic. Tutup setelah semua child selesai.

## WAVE 1 — Fondasi (2 issue paralel, file berbeda, tidak saling blok)

- [x] **khj.1**: read foundation — `PageRequest` + `SortParam` in-module + bentuk DTO mini-projection (target shape utk khj.3 & khj.4) · `P1` · deps: khj
- [x] **khj.2**: write foundation — `@ConfigurationProperties` externalisasi excluded jabatan `{1,2,3,25}` + golongan-statuses + `application.yml` · `P1` · deps: khj

> khj.1 menyentuh `dto/pegawai` + paging/sort baru. khj.2 menyentuh `config/` + `application.yml`. Tak ada file bertabrakan → aman paralel.

## WAVE 2 — Builder (3 issue paralel, file berbeda, tidak saling blok)

- [ ] **khj.3**: `PegawaiQueryRepository` (JOOQ) — list/page + findByNipam + findById + findByIds, nested mini-projection · `P2` · deps: khj.1
- [ ] **khj.4**: Ringkasan read (JOOQ lintas modul) — `riwayat_sk` multiset + `pendidikan.is_latest` + `kartu_identitas`; mapper + query baru · `P2` · deps: khj.1
- [ ] **khj.5**: `PegawaiCommandService` — saga `@Transactional` (save/saveBatch/update/patchGaji/patchProfil/delete), createUser terakhir, exception tak ditelan · `P2` · deps: khj.2

> khj.3 & khj.4 = file baru di `repositories/pegawai/jooq` (+ `mapper/pegawai` utk khj.4). khj.5 = file baru di `services/pegawai`. Tiga-tiganya file baru terpisah → aman paralel. khj.3/khj.4 butuh khj.1; khj.5 butuh khj.2.

## WAVE 3 — Konvergensi (1 issue, titik tabrakan tunggal = controller)

- [ ] **khj.6**: wiring + retire `ServiceImpl` — `PegawaiQueryService` orkestrasi + rewire `PegawaiController` ke Command/Query, hapus `PegawaiServiceImpl`/`PegawaiService` lama · `P2` · deps: khj.3, khj.4, khj.5

> Hanya issue ini menyentuh `PegawaiController`. Dijalankan terakhir setelah ketiga builder hijau agar tak ada caller tergantung ke ServiceImpl lama saat dihapus.

---

## Dependency map (ringkas)

```
khj(EPIC) ─┬─ khj.1 (read foundation) ─┬─ khj.3 (QueryRepository) ─┐
           │                           └─ khj.4 (Ringkasan)       ─┤
           └─ khj.2 (write foundation) ─── khj.5 (CommandService) ─┴─ khj.6 (wiring + retire)
```

## Acceptance ringkas per issue

**khj.1 (read foundation)**
- [x] `PageRequest` & `SortParam` ada di dalam modul pegawai (bukan `dto/commons` global)
- [x] Shape mini-projection terdokumentasi & dipakai khj.3/khj.4 (id+label per relasi)
- [x] `PegawaiResponse` tetap nested; nol DTO berat baru / MapStruct / sqids
- [x] Nol dependency build baru; `gitnexus_impact` dilaporkan; `detect_changes` bersih

**khj.2 (write foundation)**
- [x] Properties bean terbaca dari `application.yml`; default jabatan `{1,2,3,25}` & golongan-statuses `{KONTRAK,CALON_HONORER,HONORER}`
- [x] Tidak ada magic-id hardcoded tersisa di jalur tulis baru
- [x] Pendekatan table-flag tetap backlog `kepegawaian-1dx` (P4) — tidak dikerjakan di sini
- [x] `detect_changes` hanya `config/` + `application.yml`

**khj.3 (QueryRepository)**
- [ ] list/page, findByNipam, findById, findByIds dilayani JOOQ; perilaku sort/paging identik lama
- [ ] `sortBy` tak dikenal → ID, tanpa error; soft-delete terfilter
- [ ] `detect_changes` hanya file jooq read baru

**khj.4 (Ringkasan lintas modul)**
- [ ] Field `PegawaiResponseRingkasan` identik lama; 7 slot SK benar; override `skCapeg` (tanggalSk = skCapeg.tmtBerlaku) jalan
- [ ] `pendidikan.is_latest=true`; kartu difilter per nama jenis; perakitan string di Java
- [ ] Filter kartu by-nama dicatat sbg utang (ADR-0020); `detect_changes` hanya file ringkasan baru

**khj.5 (CommandService saga)**
- [ ] Semua endpoint tulis return `{status,id}` tanpa re-read
- [ ] `createUser` (Appwrite) dipanggil paling akhir; `saveBatch` rollback saat 1 gagal (exception **tak** ditelan)
- [ ] `update()` tidak menyentuh `RiwayatSk`; soft-delete pada `deleteById`
- [ ] `gitnexus_impact` upstream `PegawaiServiceImpl` dilaporkan sebelum edit

**khj.6 (wiring + retire)**
- [ ] Controller pakai `PegawaiQueryService` (baca) + `PegawaiCommandService` (tulis); semua endpoint & `@PreAuthorize` ADMIN dipertahankan
- [ ] Validasi `PegawaiTetap` utk `PEGAWAI` pakai `ignoreJabatan {1,2,3,25}` dari properties khj.2
- [ ] `PegawaiServiceImpl`/`PegawaiService` lama dihapus tanpa caller tersisa (`gitnexus_impact` bersih)
- [ ] `./gradlew test` hijau; `detect_changes` = controller + service wiring saja

## Cara update checklist

- Klaim: `bd update kepegawaian-<id> --claim`
- Selesai: `bd close kepegawaian-<id>` → centang `[x]` di sini
- Cek yang siap dikerjakan: `bd ready`
- Status keseluruhan: `bd list --status=open | grep khj`
- Pohon dependency: `bd dep tree kepegawaian-khj`

## REF

- Pattern sumber: `docs/master-response-pattern-guide.md` (5 keputusan terkunci response/DTO)
- ADR-0020 (baca tabel lintas modul via JOOQ) · ADR-0021 (saga atomik dgn Appwrite)
- ADR-0017 (JOOQ di repository) · ADR-0014 (GET id 404) · ADR-0001 (CQRS JPA-write/JOOQ-read)
- Glossary keputusan baca/tulis Pegawai: `CONTEXT.md` (bagian "Keputusan baca Ringkasan" & "Keputusan rewrite sisi-tulis Pegawai")
- Backlog terkait: `kepegawaian-1dx` (P4, table-flag pejabat tanpa-golongan)

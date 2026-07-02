# Claim Order — Drop `CommonPageRequest` → Rewrite CQRS/JOOQ 5 Modul Terakhir

> **Epik:** `kepegawaian-6bu` · **Total:** 20 isu (1 epik induk + 4 epik domain + `#foundation` + 13 slice + `#final`)
> **Sumber keputusan:** [ADR-0026](adr/0026-cleanup-commonpagerequest-memicu-rewrite-cqrs.md) (rewrite penuh, bukan swap-superclass), [ADR-0027](adr/0027-default-page-size-20-cap-100.md) (default 20/cap 100), [ADR-0028](adr/0028-archunit-guard-valid-pada-pagedrequest.md) (guard `@Valid`).

Dokumen ini menetapkan **urutan claim** (`bd update <id> --claim`) supaya eksekusi tidak menabrak dependensi dan tidak ada yang terlewat seperti regresi `@Valid` sebelumnya. Urutan dijaga oleh DAG beads: **13 slice `blocked-by` `#foundation`**, **`#final` `blocked-by` semua 13 slice**. `bd ready` hanya akan menampilkan isu yang boleh dikerjakan.

---

## Checklist Pengerjaan (Master)

Cermin status beads dalam urutan claim. Centang saat isu terkait `closed`. Sumber kebenaran tetap `bd ready` / `bd show <id>` — checklist ini untuk pandangan sekilas.

- [ ] **1. `kepegawaian-6bu.1`** — `#foundation`: fix `PagedRequest` + guard ArchUnit + fix `@Valid` `BiodataController.index` *(WAJIB pertama)*
- [ ] **2. `kepegawaian-6bu.2.1`** — cuti: CQRS `CutiJenis`
- [ ] **3. `kepegawaian-6bu.2.2`** — cuti: CQRS `CutiKuota`
- [ ] **4. `kepegawaian-6bu.2.3`** — cuti: CQRS `CutiApprovalChain`
- [ ] **5. `kepegawaian-6bu.2.4`** — cuti: CQRS `CutiApproval`
- [ ] **6. `kepegawaian-6bu.2.5`** — cuti: CQRS `CutiPengajuan`
- [ ] **7. `kepegawaian-6bu.3.1`** — kepegawaian: CQRS `RiwayatSk`
- [ ] **8. `kepegawaian-6bu.3.2`** — kepegawaian: CQRS `RiwayatSp`
- [ ] **9. `kepegawaian-6bu.3.3`** — kepegawaian: CQRS `RiwayatMutasi`
- [ ] **10. `kepegawaian-6bu.3.4`** — kepegawaian: CQRS `RiwayatKontrak`
- [ ] **11. `kepegawaian-6bu.3.5`** — kepegawaian: CQRS `RiwayatTerminasi`
- [ ] **12. `kepegawaian-6bu.4.1`** — profil: CQRS `Pendidikan`
- [ ] **13. `kepegawaian-6bu.5.1`** — system: CQRS `PrefRole`
- [ ] **14. `kepegawaian-6bu.5.2`** — users: CQRS `User`
- [ ] **15. `kepegawaian-6bu.6`** — `#final`: hapus `CommonPageRequest` + verifikasi 0 referensi *(WAJIB terakhir)*

> **Progress:** 0/15 selesai · Epik `kepegawaian-6bu` masih `open`.

---

## Aturan Wajib (jangan dilanggar)

1. **`#foundation` (`kepegawaian-6bu.1`) DULU, sendirian.** Semua slice diblokir olehnya — `bd ready` tak akan memunculkan slice sebelum foundation `closed`.
2. **`#final` (`kepegawaian-6bu.6`) TERAKHIR.** Penghapusan `CommonPageRequest` baru boleh setelah 13 slice bersih (0 referensi).
3. **Per slice, sebelum edit:** `gitnexus_impact({target, direction:"upstream"})` → laporkan blast radius. **Jangan** rename via find/replace — pakai `gitnexus_rename`.
4. **Per slice, sebelum commit:** `gitnexus_detect_changes()` → verifikasi scope hanya menyentuh simbol yang diharapkan.
5. **DoD tiap slice** (dari `#foundation`): controller `index(@Valid @ParameterObject FooIndexQuery)` **tanpa** `Errors`; guard ArchUnit hijau; `≤120` baris/file; clean build.

---

## Tahap 0 — Foundation (WAJIB PERTAMA)

| Urutan | ID | Judul | Isi inti |
|--------|-----|-------|----------|
| 1 | `kepegawaian-6bu.1` | `#foundation`: fix PagedRequest + guard ArchUnit + DoD | Perbaiki `PagedRequest` (default 20, cap 100, `@Pattern` sortDir, `getPageable()` kosong → `Sort.UNSORTED`, bersihkan javadoc basi); tambah `testImplementation archunit-junit5` + rule guard `@Valid`; perbaiki regresi `BiodataController.index` (tambah `@Valid`). |

> Build **akan merah** begitu guard ditambahkan (menangkap `BiodataController.index` yang tanpa `@Valid`) — memperbaikinya adalah bagian dari `#foundation`. Foundation `closed` = build hijau.

---

## Tahap 1 — 13 Slice (setelah foundation `closed`)

Semua slice `blocked-by kepegawaian-6bu.1`. Antar-slice **independen** → boleh paralel/pilih bebas. Urutan di bawah = rekomendasi (per domain, dari paling sederhana). Exemplar template: `GajiBatchMasterQueryRepository`.

### Domain `cuti` — epik `kepegawaian-6bu.2`

| Urutan | Slice ID | DTO lama (`extends CommonPageRequest`) |
|--------|----------|----------------------------------------|
| 2 | `kepegawaian-6bu.2.1` | `dto/cuti/jenis/CutiJenisRequest` |
| 3 | `kepegawaian-6bu.2.2` | `dto/cuti/kuota/CutiKuotaRequest` |
| 4 | `kepegawaian-6bu.2.3` | `dto/cuti/approvalChain/CutiApprovalChainRequest` |
| 5 | `kepegawaian-6bu.2.4` | `dto/cuti/approval/CutiApprovalRequest` |
| 6 | `kepegawaian-6bu.2.5` | `dto/cuti/pengajuan/CutiPengajuanRequest` |

### Domain `kepegawaian` — epik `kepegawaian-6bu.3`

| Urutan | Slice ID | DTO lama |
|--------|----------|----------|
| 7 | `kepegawaian-6bu.3.1` | `dto/kepegawaian/riwayatSk/RiwayatSkRequest` |
| 8 | `kepegawaian-6bu.3.2` | `dto/kepegawaian/riwayatSp/RiwayatSpRequest` |
| 9 | `kepegawaian-6bu.3.3` | `dto/kepegawaian/mutasi/RiwayatMutasiRequest` |
| 10 | `kepegawaian-6bu.3.4` | `dto/kepegawaian/riwayatKontrak/RiwayatKontrakRequest` |
| 11 | `kepegawaian-6bu.3.5` | `dto/kepegawaian/terminasi/RiwayatTerminasiRequest` |

### Domain `profil` — epik `kepegawaian-6bu.4`

| Urutan | Slice ID | DTO lama |
|--------|----------|----------|
| 12 | `kepegawaian-6bu.4.1` | `dto/profil/pendidikan/PendidikanRequest` |

### Domain `system` + `users` — epik `kepegawaian-6bu.5`

| Urutan | Slice ID | DTO lama |
|--------|----------|----------|
| 13 | `kepegawaian-6bu.5.1` | `dto/system/roles/PrefRoleRequest` |
| 14 | `kepegawaian-6bu.5.2` | `dto/users/UserRequest` |

---

## Tahap 2 — Final (WAJIB TERAKHIR)

| Urutan | ID | Judul | Gate |
|--------|-----|-------|------|
| 15 | `kepegawaian-6bu.6` | `#final`: hapus `CommonPageRequest` + verifikasi 0 referensi | `blocked-by` semua 13 slice. Hapus class, `grep -r "CommonPageRequest" src/main/java` → **0 hasil**, clean build hijau. |

---

## Per-Slice Checklist (template CQRS)

Tiap slice mengubah satu DTO `extends CommonPageRequest` → slice CQRS/JOOQ penuh:

- [ ] `*IndexQuery extends PagedRequest` (ganti `*Request` lama untuk read) + `*PostRequest`/`*PutRequest` + `*Response`.
- [ ] `repositories/<modul>/jooq/*QueryRepository` — `SortParam.resolve(getSortBy, getSortDirection, allowedSorts(), <defaultCol>)`, `.limit(getSizeOrDefault()).offset(getPageNumber()*getSizeOrDefault())`, `baseWhere()` dengan `DSL.noCondition()`, kembalikan `PageImpl<>`.
- [ ] `repositories/<modul>/jpa/*Repository` — write JPA.
- [ ] `*JooqMapper` — `mapToResponse`.
- [ ] `*QueryService` (read) + `*CommandService @Transactional` (write); buang `*ServiceImpl` (single-impl interface) via `gitnexus_rename`.
- [ ] Controller inject dua service; `index(@Valid @ParameterObject *IndexQuery)` **tanpa** `Errors`; save/update buang `Errors` boilerplate (ditangani `GlobalExceptionHandler`, ADR-0013).
- [ ] Guard ArchUnit hijau; semua file `≤120` baris; clean build.
- [ ] `gitnexus_detect_changes()` verifikasi scope; `bd close <slice-id>`.

---

## Ringkasan Alur

```
6bu.1 (#foundation)  ──►  6bu.2.1 … 6bu.5.2 (13 slice, paralel)  ──►  6bu.6 (#final, hapus class)
   ▲ WAJIB pertama              ▲ blocked-by foundation                 ▲ blocked-by 13 slice
```

Cek kesiapan kapan saja: `bd ready`. Lihat graf: `bd graph --compact kepegawaian-6bu`.

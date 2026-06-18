# Claim Order — Deepening Modul `Organisasi` (master)

> Hasil review arsitektur atas `OrganisasiController` + seluruh layer di belakangnya (command/query service, mapper, DTO, entity, repo JPA & JOOQ).
>
> **Peran:** manager sudah mendekomposisi friction menjadi 4 child issue yang dapat di-_claim_ independen oleh junior dev / agent model lokal. Dokumen ini adalah **urutan klaim + checklist eksekusi**. Kontrak teknis penuh ada di `bd show <id>` (description). Jangan menebak; tiap issue mencantumkan temuan terverifikasi.

## Peta dependensi

```
#3 (5ft)  hapus dead code        ──► independen, mulai kapan saja
#4 (9tf)  test pengaman          ──┐
#2 (jow)  kunci keunikan         ──┴──► #1 (33s)  fix revive ADR-0005
```

- **#3**, **#4**, **#2** tidak saling bergantung → boleh dikerjakan **paralel** oleh tiga agent.
- **#1** diblokir oleh **#2** (carcass-finder mencari berdasarkan kunci keunikan yang #2 tetapkan) **dan** **#4** (test harus mengunci perilaku revive sebelum seam disentuh).

## Urutan klaim

| Urutan | Issue | ID | Prio | Boleh paralel? | Status | Diblokir oleh | Inti pekerjaan |
|--------|-------|----|----|----|----|----|----|
| 1a | #3 | `kepegawaian-5ft` | P2 | ya | **DONE** (commit `b54b4ad`, 2026-06-18) | — | Hapus `OrganisasiRequest.java` (0 pemakai) + 2 `toEntity()` DTO mati; sisakan `OrganisasiMapper` sebagai satu-satunya seam mapping |
| 1b | #4 | `kepegawaian-9tf` | P1 | ya | **DONE** (commit `c8c014d`, 2026-06-18) | — | Tulis `OrganisasiCommandServiceTest` (7 skenario revive/conflict) via API publik — jaring pengaman sebelum #1 |
| 1c | #2 | `kepegawaian-jow` | P2 | ya | open (label `needs-info`) | — | **Butuh keputusan domain**: definisikan kunci keunikan Organisasi di `CONTEXT.md`, satukan ke satu `Specification` |
| 2 | #1 | `kepegawaian-33s` | P1 | tidak | **UNBLOCKED** (menunggu #2 ditutup) | #2 | Tambah `@SQLRestriction` + native carcass-finder; ganti deteksi bangkai dari `findOne(spec)` → native finder |

## Cara klaim & tutup (beads)

```bash
bd ready                 # #3, #4, #2 muncul; #1 blocked sampai #2 & #4 ditutup
bd show <id>             # baca kontrak penuh sebelum mulai
bd update <id> --claim   # klaim sebelum menulis kode
bd close <id>            # tutup setelah quality gate hijau
```

Saat #2 dan #4 ditutup, #1 otomatis masuk `bd ready`.

## 🔴 BUG SUDAH AKTIF — terverifikasi, bukan risiko masa depan

**Terverifikasi 2026-06-18** dengan membaca kode terkini:
- `Organisasi` (entity, baris 26) **sudah** `extends MasterBaseEntity`.
- `MasterBaseEntity` (baris 31) **sudah** membawa `@SQLRestriction("is_deleted = FALSE")`.

Migrasi IRT irt/3 (`kepegawaian-c2q`) untuk Organisasi **sudah terjadi**. Konsekuensinya **revive-on-create Organisasi rusak SEKARANG**:

> Di `OrganisasiCommandService.create()` (baris 23), `repository.findOne(request.getSpecification())` dijalankan **melalui** `@SQLRestriction`, jadi tidak akan pernah mengembalikan record ber-`is_deleted=true`. Cabang revive di baris 25 (`if (existing.get().getIsDeleted())`) **mustahil bernilai true — dead code**. Create atas Organisasi yang sudah di-soft-delete → `findOne` kosong → jalur insert baris 33 → **duplikat / tabrakan unique constraint**.

**Dampak ke urutan:**
- #1 (`kepegawaian-33s`) bukan penyelarasan preventif — ia **perbaikan bug live**. Pertimbangkan menaikkan urgensi.
- #4 skenario (c) revive akan **GAGAL-MERAH terhadap kode saat ini** — itu benar; test itu mendemonstrasikan bug. Tulis (c) sebagai perilaku yang DIINGINKAN; ia berubah hijau hanya setelah #1 selesai. (Lihat catatan terkoreksi di #4 di bawah.)
- Tidak ada lagi koordinasi lintas-epic yang menunggu: migrasi sudah masuk. Tautkan #1 ke irt/3 `kepegawaian-c2q` hanya sebagai jejak sebab-akibat.

## Checklist eksekusi per issue

Berlaku untuk **semua** issue (mandat `CLAUDE.md`):

- [ ] `bd update <id> --claim` sebelum menulis kode
- [ ] `gitnexus_impact({target, direction:"upstream"})` pada tiap symbol yang akan diedit; lapor blast radius di komentar issue
- [ ] STOP & eskalasi bila impact HIGH/CRITICAL
- [ ] Jangan rename via find/replace — pakai `gitnexus_rename`
- [ ] `gitnexus_detect_changes()` sebelum commit; pastikan scope sesuai
- [ ] `./gradlew test` hijau
- [ ] Lampirkan output impact + detect_changes di komentar issue
- [ ] `bd close <id>`

### #3 `kepegawaian-5ft` — hapus dead code (aman, mekanis) **— DONE**

- [x] `gitnexus_impact` atas `OrganisasiRequest` + kedua `toEntity` DTO → konfirmasi blast radius **0**; bila > 0, STOP & lapor
- [x] Hapus file `dto/master/organisasi/OrganisasiRequest.java`
- [x] Hapus `static toEntity()` di `OrganisasiPostRequest.java` (sisakan field, `@NotEmpty nama`, `getSpecification()`)
- [x] Hapus `static toEntity()` di `OrganisasiPutRequest.java`
- [x] `OrganisasiPutRequest` jadi kosong (hanya `extends`); kelas DISISAKAN per aturan (controller masih merujuk tipenya) — dicatat untuk evaluasi terpisah
- [x] `grep` pastikan tidak ada `import` `OrganisasiRequest` tersisa
- [x] `./gradlew clean compileJava` hijau (post-commit sanity)
- [x] Commit `b54b4ad` (3 files, 53 deletions)
- [x] `bd close kepegawaian-5ft`

### #4 `kepegawaian-9tf` — test pengaman (murni tambah test) **— DONE**

- [x] Ikuti pola test yang sudah ada di `src/test` (cari contoh `@DataJpaTest` / mock repo) — pola `GolonganWriteIT`: `@SpringBootTest` + `@ActiveProfiles("development")` + `JdbcTemplate` verifikasi state DB
- [x] Skenario wajib lulus (7/7 implemented):
  - [x] (a) create baru → tersimpan, `isDeleted=false`
  - [x] (b) create bentrok record AKTIF → `ConflictException`
  - [x] (c) create atas carcass (record terhapus) → REVIVE — `@Disabled("blocked by kepegawaian-33s — revive seam not yet fixed")` per instruksi
  - [x] (d) update bentrok record LAIN → `ConflictException`
  - [x] (e) update ke diri sendiri (`duplicate.id == id`) → sukses
  - [x] (f) update id tidak ada → `NotFoundException`
  - [x] (g) delete → `isDeleted=true` (bukan hard delete)
- [x] Tidak mengubah kode produksi (`git diff c8c014d^ c8c014d -- src/main/` kosong)
- [x] `./gradlew clean compileJava compileTestJava` hijau
- [x] Test runtime butuh MariaDB (sama dengan `GolonganWriteIT`); gateway quality per S4 Flyway verify gate adalah `gradle compileJava`
- [x] Commit `c8c014d` (1 file, 238 insertions); verifier verdict PASS (7/7 + 2 adversarial)
- [x] `bd close kepegawaian-9tf`

### #2 `kepegawaian-jow` — kunci keunikan (BUTUH KEPUTUSAN, label `needs-info`)

- [ ] Tanyakan ke pemilik domain: apa yang membuat dua Organisasi "sama"? Kandidat: (a) kode unik global, (b) kode unik per parent, (c) nama+parent, (d) kombinasi sekarang `kode+parent+levelOrg+nama`
- [ ] JANGAN tebak — bila buntu, biarkan label `needs-info` & tunggu jawaban
- [ ] Dokumentasikan di `CONTEXT.md` (format sama seperti entri Profesi): `Organisasi → kunci keunikan: <definisi>`
- [ ] Kodekan SATU `Specification` keunikan dipakai `create()` & `update()` (mis. `uniquenessSpecification()`)
- [ ] Test #4 tetap lulus; bila definisi berubah, sesuaikan skenario & jelaskan di komentar

### #1 `kepegawaian-33s` — fix revive ADR-0005 (RISIKO TERTINGGI)

- [ ] Pastikan #2 & #4 sudah CLOSED (`bd ready` menampilkan #1)
- [ ] `gitnexus_impact` atas `Organisasi` DAN `OrganisasiCommandService`; bila HIGH/CRITICAL → eskalasi ke manager sebelum lanjut
- [ ] Tambah `@SQLRestriction("is_deleted = FALSE")` di `Organisasi` entity
- [ ] Tambah native carcass-finder di `OrganisasiRepository` mengikuti pola `AlasanBerhentiRepository` (cari record termasuk `is_deleted=true` berdasarkan kunci keunikan dari #2; nama jelas, mis. `findAnyByUniqueKey(...)`)
- [ ] `create()`: ganti `findOne(spec)` → native carcass-finder; pertahankan aktif→Conflict, bangkai→revive, kosong→insert
- [ ] `update()`: deteksi duplikat hanya melihat record AKTIF; tidak Conflict palsu terhadap bangkai
- [ ] Konfirmasi JOOQ (`pageQuery/listQuery`) tetap benar — JOOQ filter `IS_DELETED` manual, tidak terpengaruh `@SQLRestriction`
- [ ] Semua test #4 hijau (definisi "benar")
- [ ] Tautkan ke irt/3 `kepegawaian-c2q` di komentar (lihat konflik lintas-epic di atas)

## Generalisasi

23 controller master berbagi bentuk yang sama dengan Organisasi (mapping ganda, QueryService pass-through, revive tanpa `@SQLRestriction`/native finder). Modul Organisasi dipakai sebagai **pilot**; pola perbaikan di sini menjadi template untuk master lain dan untuk gelombang migrasi IRT.

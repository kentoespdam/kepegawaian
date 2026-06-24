# Profil Rewrite — Claim Order & Monitoring

Epic **kepegawaian-mee** — Rewrite modul PROFIL ke CQRS (JPA write / JOOQ read), meniru pola modul MASTER (Profesi sebagai template kanonik).

Urutan klaim di bawah **mengikuti dependency**, bukan nomor issue. Kerjakan per **WAVE**: semua issue dalam satu wave boleh diklaim paralel (tidak saling blok); wave berikutnya baru terbuka setelah wave sebelumnya selesai. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca deskripsi epic di `bd show kepegawaian-mee` dan WORKING AGREEMENT: gitnexus-first (`gitnexus_impact` sebelum edit symbol, `gitnexus_detect_changes` sebelum commit), context7-first untuk docs library, DRY + KISS, **strict scope — JANGAN AI SLOP**, alur `claim → code → test → detect_changes → close → ship` (CODING_RULES.md).

**8 entitas profil:** biodata (akar agregat, kunci NIK), pendidikan, kartuIdentitas, keahlian, pelatihan, pengalamanKerja, keluarga (ProfilKeluarga), lampiranProfil. 7 entitas non-biodata adalah anak dari satu Biodata.

---

## WAVE 0 — Fondasi (1 issue, blokir semua write-side)

- [x] **mee.1** · F1: `ChangedStatusResolver` (`@Component`, baca `ROLE_SDM` dari principal) + buang `changed_status` dari semua request DTO profil · `P1` · deps: —

> Tidak ada write-side slice yang boleh mulai sebelum ini merge. **Catatan kritis:** `ROLE_SDM` sebagai Spring authority **belum ada** (saat ini 'SDM' hanya jabatan di rantai cuti). Verifikasi mekanisme authority dulu; bila butuh keputusan produk, **file issue baru — jangan tebak**.

## WAVE 1 — Slice Referensi Pendidikan (2 issue, GERBANG)

- [x] **mee.2** · F2: Pendidikan **READ** (JOOQ) — slice referensi baca · `P1` · deps: mee.1
- [x] **mee.3** · F3: Pendidikan **WRITE** (JPA Command) — slice referensi tulis · `P1` · deps: mee.1

> **Gerbang replikasi.** 6 anak berikutnya meniru bentuk F2 (read) + F3 (write) persis. Review ekstra ketat — pola yang salah di sini menyebar ke banyak issue. Idealnya F2 selesai/stabil sebelum F3, tapi keduanya hanya butuh F1.

## WAVE 2 — Replikasi 5 anak (5 issue paralel, semua butuh F2+F3)

- [x] **mee.4** · F4: KartuIdentitas READ+WRITE — **revive-on-create** (kunci NIK+jenis kartu), varian Pendidikan · `P2` · deps: mee.2, mee.3
- [x] **mee.5** · F5: Keahlian READ+WRITE — **append-only** (tanpa kunci alami, tanpa revive) · `P2` · deps: mee.2, mee.3
- [x] **mee.6** · F6: Pelatihan READ+WRITE — append-only · `P2` · deps: mee.2, mee.3
- [x] **mee.7** · F7: PengalamanKerja READ+WRITE — append-only · `P2` · deps: mee.2, mee.3
- [x] **mee.8** · F8: ProfilKeluarga READ+WRITE — **arsip & aktif berdampingan** (re-add = baris baru, tolak hanya duplikat aktif persis) · `P2` · deps: mee.2, mee.3

## WAVE 3 — Akar Agregat (1 issue)

- [x] **mee.9** · F9: Biodata READ+WRITE — akar agregat + **cascade seed (Q19)**; read global ala master (pencarian direktori) · `P2` · deps: mee.3, **mee.4**

> Butuh F3 **dan** F4 karena `create` Biodata men-seed 1 Pendidikan (`PendidikanCommandService.seedFromBiodata`) + 1 KartuIdentitas (`KartuIdentitasCommandService.seedFromBiodata`) dalam satu `@Transactional`. Seed = aktor sistem → `changedStatus=false` eksplisit, **tak** mengantri ProfileUpdate, tapi tetap memunculkan revisi Envers.

## WAVE 4 — Integrasi & Sapu-bersih (1 issue, penutup)

- [x] **mee.10** · F10: Pensiunkan legacy `XxxServiceImpl` (cek pemakai via gitnexus_impact), amandemen ADR-0003 (profil masuk roster Envers), verifikasi silang anti-pattern, `./gradlew clean build` · `P2` · deps: mee.2–mee.9

> Hanya boleh mulai setelah 8 entitas selesai. Perubahan ADR-0003 = dokumen (tak butuh beads/gitnexus gate untuk `.md`).

---

## Dependency map (ringkas)

```
F1(1) ──┬── F2(2) ──┬───────────────────────────────────┐
        │           ├── F4(4) ──┐                        │
        │           ├── F5(5)   │                        │
        └── F3(3) ──┼── F6(6)   │                        ├──► F10(10)
                    ├── F7(7)   │                        │
                    ├── F8(8)   │                        │
                    └───────────┴── F9(9) ───────────────┘
                       (F9 butuh F3 + F4)
```

---

## Keputusan desain yang dikunci (rujukan saat coding)

Hasil sesi grilling — detail di **CONTEXT.md** (baris 98–106) & ADR terkait:

| # | Keputusan | Rujukan |
|---|-----------|---------|
| Q14 | Deteksi revive/collision tetap di sisi tulis (JPA) | ADR-0019 |
| Q15 | Command profil: paritas penuh Master — `throw` exception domain, **tanpa** try/catch swallow, kembalikan entity; controller bungkus `CustomResult.save()` | ADR-0013 |
| Q16 | Dua-bean per anak (`XxxQueryRepository` + `XxxDetailQuery`); **DROP `listQuery()`** untuk 7 anak; `biodataId` (`@NotBlank`) wajib di IndexQuery | ADR-0017, CONTEXT.md:101 |
| Q17 | Lampiran di-embed ke `XxxDetail` via JOOQ **multiset**; `record LampiranRow` bersama; predikat `REF = EJenisLampiranProfil.X.ordinal()`; isi file = endpoint streaming terpisah | ADR-0001/0017; bug kepegawaian-2bz |
| Q18 | `SortParam.resolve(allowedSorts(), <default>)` whitelist; **Pendidikan default `IS_LATEST DESC, TAHUN_MASUK DESC`**, 6 lain default `ID` | ADR-0017 |
| Q19 | Biodata `create` men-seed initial KartuIdentitas + isLatest Pendidikan; seed sistem → `changedStatus=false` eksplisit, no queue, Envers ya, satu transaksi | CONTEXT.md (seksi seed) |

**Pola failure (semua command profil):** FK/entity hilang → `.orElseThrow(NotFoundException)` (404); duplikat aktif → `throw ConflictException` (409); exception tak terduga → 500 asli via `GlobalExceptionHandler` (**bukan** HTTP-200 `SavedStatus.FAILED` tersamar — anti-pattern legacy, akar bug kepegawaian-1sf).

**changedStatus (ADR-0018):** ditentukan server dari role (`ROLE_SDM` → `false` langsung stabil; selain itu → `true` menunggu approval). Apa pun rolenya Envers tetap mencatat revisi. Hanya `changedStatus=true` yang memanggil `profileUpdateService.create(...)` (dependensi satu arah profil → updateProfile).

**Revive-on-create per jenis data (CONTEXT.md:102–105):**
- **Pendidikan, KartuIdentitas** (punya kunci alami) → re-add kombinasi terhapus **menghidupkan** baris lama; reject pegawai → hard-delete by id (satu-satunya pengecualian no-hard-delete).
- **ProfilKeluarga** → arsip & aktif berdampingan; re-add = baris baru; tolak hanya duplikat aktif persis.
- **Keahlian, Pelatihan, PengalamanKerja, LampiranProfil** → tanpa kunci alami; tiap add = baris baru.

---

## Referensi template (BACA DULU sebelum coding)

| Berkas | Untuk |
|--------|-------|
| `repositories/master/jooq/ProfesiQueryRepository.java` | template `pageQuery` + `allowedSorts()` + `baseWhere` |
| `repositories/master/jooq/ProfesiDetailQuery.java` | template `getById` + **multiset** |
| `repositories/master/jooq/ProfesiSelects.java`, `ProfesiRowMapper.java` | proyeksi kolom + row mapper |
| `services/master/profesi/ProfesiQueryService.java` | inject 2 bean; `getById` → orElseThrow |
| `services/master/profesi/ProfesiCommandService.java` | pola failure command (orElseThrow/throw, return entity) |
| `dto/master/profesi/{ProfesiIndexQuery,ProfesiQuery,ProfesiDetail,ApdRow}.java` | bentuk DTO + record row |
| `exceptions/GlobalExceptionHandler.java` | bukti exception tak terduga → 500, bukan FAILED |

## Issue terkait (bug)

- **kepegawaian-1sf** (P2) — re-add soft-deleted Pendidikan/KartuIdentitas gagal `ConstraintViolationException` mentah. **Diperbaiki struktural oleh F3** (revive-on-create). Ditautkan: F3 blocks 1sf.
- **kepegawaian-2bz** (P3) — `LampiranProfil.ref` `@Enumerated(ORDINAL)` rapuh. Sisi-baca aman karena predikat pakai `.ordinal()` (tracks enum). Migrasi `STRING` + backfill ditunda (sentuh data persisted).

---

## Cara update checklist ini

Tandai `[x]` saat issue di-`bd close`. Sumber kebenaran status tetap **beads** (`bd show kepegawaian-mee`, `bd ready`); file ini ringkasan manusiawi untuk monitoring wave.

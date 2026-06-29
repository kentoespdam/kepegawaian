# Kepegawaian — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring

Epic **kepegawaian-2u7** — Rewrite modul **Kepegawaian** (6 aggregate: SK, Mutasi, Kontrak, Terminasi, SP, Lampiran) ke pola CQRS yang sama dengan master/profil/pegawai: **tulis JPA, baca JOOQ**. Memecah 6 `*ServiceImpl` + `Generic*Service` legacy jadi `<Aggregate>CommandService` (tulis, saga `@Transactional`) + `<Aggregate>QueryService` (baca, JOOQ), menegakkan struktur paket **h02**, dan membalik arah dependency lintas-modul `pegawai ↔ kepegawaian` lewat dua **DIP port** (ADR-0023). Diakhiri pembersihan DTO mati di master/profil/pegawai.

Urutan klaim mengikuti **dependency**, bukan nomor issue. Issue dalam wave yang sama **menyentuh file berbeda dan tidak saling blok** → boleh diklaim paralel. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca keputusan manajer yang dikunci di `bd show kepegawaian-2u7` (field `design`). Keputusan tidak boleh diubah tanpa eskalasi:

1. **Struktur paket h02 (CLOSED).** `dto|services|mapper /kepegawaian/<aggregate>/` (nested per-aggregate); `repositories/kepegawaian/{jpa,jooq}/` (tech-split — pindahkan `RiwayatSp` dari `repositories/kepegawaian/riwayatSp/`); `controllers/kepegawaian/` (flat). **6 controller**, satu per aggregate — tiap satu kelas inject **KEDUA** query+command service (pola `PegawaiController`); pemisahan Command/Query **hanya di layer service**, TIDAK ada `*CommandController/*QueryController`.
2. **Service konkret, tanpa interface+`Impl`** (ADR-0007). Retire semua `*ServiceImpl` + `Generic*Service`. Satu-satunya interface yang dibenarkan = dua DIP port (butir 6).
3. **Dua pola baca.** **Pola A** = `private toQuery(Record)` di dalam JOOQ QueryRepository → `*Query` DTO flat (SK, Lampiran, SP). **Pola B** = `static final *Mapper.map(Record, …)` di `mapper/kepegawaian/<aggregate>/` → `*Response` nested (Mutasi, Terminasi yang snapshot label). Semua mapper `static final` + ctor privat, **bukan** `@Component`. Response tetap nested utk kompat FE; objek nested collapse ke `row(id+label)`; perakitan string di Java; tanpa MapStruct/sqids.
4. **FK attach (ADR-0008/0022).** FK murni → `getReferenceById` (mis. `RiwayatSk` hanya `setGolongan`). **Pengecualian ADR-0022:** aggregate yang **men-snapshot label** (`RiwayatMutasi`/`RiwayatTerminasi`) WAJIB `findById` (butuh entity terhidrasi). Buang full-table-scan `DetailFromList.findExist*` (`findAll`) di Terminasi.
5. **Saga atomik (ADR-0021).** Tiap tulis = satu `@Transactional`; exception **tidak ditelan** (jangan `try/catch → SavedStatus.FAILED` seperti `RiwayatTerminasiServiceImpl` lama); sistem eksternal (Appwrite) dipanggil terakhir. Mutasi kembalikan `{status,id}` via `SavedStatus.build(ESaveStatus, id)` tanpa re-read.
6. **DIP port lintas-modul (ADR-0023).** Modul **pegawai** mendefinisikan **dua port** di `services/pegawai/port/`: `SkBootstrapPort`(`createSkCapeg`,`createSkPegawaiTetap`) & `KontrakBootstrapPort`(`createKontrakFromPegawai`) — keduanya **return entity** (`RiwayatSk`/`RiwayatKontrak`) karena saga butuh `sk.getId()` utk `refSk*Id`. `RiwayatSkCommandService` implements `SkBootstrapPort`; `RiwayatKontrakCommandService` implements `KontrakBootstrapPort`. `PegawaiCommandService` inject **kedua interface lokal** (bukan kelas konkret kepegawaian). **Reviewer jangan sederhanakan balik** ke inject konkret → memulihkan siklus impor. Pemanggilan port di dalam `@Transactional` pemanggil (`REQUIRED`); `refSk*Id` tetap diset di `PegawaiCommandService`.
7. **Kepemilikan tulis lintas-aggregate.** Terminasi (orkestrator fan-out) memanggil **CommandService pemilik** tiap aggregate, BUKAN inject repository mentah (CONTEXT baris 170). `GenericPegawaiService` legacy → `PegawaiWriteback` (method-per-operasi), dipakai jalur SK-driven.
8. **Soft-delete dipertahankan;** DELETE tak pernah hard-delete.

**Per-issue wajib:** `gitnexus_impact(direction:"upstream")` sebelum edit symbol · `detect_changes` sebelum commit · warn HIGH/CRITICAL · pindah-paket repo & rename `Generic*` **pakai `gitnexus_rename`** (jangan find/replace) · `./gradlew test` hijau.

---

## WAVE 0 — Epic (gerbang, tidak dikerjakan langsung)

- [ ] **2u7** (EPIC): rewrite CQRS modul Kepegawaian · `P1` · deps: —

> Kontainer keputusan + grounding. Tidak ada koding di epic. Tutup setelah semua child selesai.

## WAVE 1 — Fondasi (3 issue paralel, file berbeda, tidak saling blok)

- [ ] **2u7.1**: pindah 6 repo JPA → `repositories/kepegawaian/jpa/` + scaffold `jooq/` & `mapper/<aggregate>/` (`gitnexus_rename`) · `P1` · deps: 2u7
- [ ] **2u7.2**: definisikan `SkBootstrapPort` + `KontrakBootstrapPort` di `services/pegawai/port/` (belum di-wire) · `P1` · deps: 2u7
- [ ] **2u7.3**: `GenericPegawaiService` → `PegawaiWriteback` (`gitnexus_rename`, method-per-operasi) · `P1` · deps: 2u7

> 2u7.1 menyentuh `repositories/kepegawaian`. 2u7.2 menyentuh paket baru `services/pegawai/port`. 2u7.3 menyentuh writeback + caller-nya. Tak ada file bertabrakan → aman paralel.

## WAVE 2 — Leaf CRUD (2 issue paralel) + akar SK Query

- [ ] **2u7.4**: Lampiran Command+Query (template LampiranProfil, Pola A) · `P2` · deps: 2u7.1
- [ ] **2u7.5**: SP Command+Query (CRUD murni + berkas, retire `RiwayatSpServiceImpl`) · `P2` · deps: 2u7.1
- [ ] **2u7.7**: SK Query (JOOQ Pola A, mini-projection golongan) · `P2` · deps: 2u7.1

> Tiga file baru terpisah (Lampiran/SP/SK-read). 2u7.4 harus siap sebelum SK Command (SK.delete → `lampiran.deleteByRefId`).

## WAVE 3 — Akar SK Command (titik konvergensi fondasi)

- [ ] **2u7.6**: SK Command — implements `SkBootstrapPort`; pegang `PegawaiWriteback`; `createSk*`; guard validasi · `P2` · deps: 2u7.2, 2u7.3, 2u7.4

> Butuh port (2u7.2), writeback (2u7.3), dan Lampiran (2u7.4, utk cascade delete). Aggregate akar → blok seluruh WAVE 4.

## WAVE 4 — SK-dependent (Mutasi & Kontrak paralel, lalu Terminasi)

- [ ] **2u7.8**: Mutasi Command+Query (Pola B, snapshot `findById` ADR-0022) · `P2` · deps: 2u7.6
- [ ] **2u7.9**: Kontrak Command (implements `KontrakBootstrapPort`)+Query (retire `GenericKontrakService`) · `P2` · deps: 2u7.6
- [ ] **2u7.10**: Terminasi Command (orkestrator fan-out 4 tulis)+Query (Pola B) · `P2` · deps: 2u7.6, 2u7.8, 2u7.9

> 2u7.8 & 2u7.9 paralel (file aggregate berbeda). 2u7.10 = orkestrator → butuh ketiganya (SK+Mutasi+Kontrak) lewat CommandService pemilik.

## WAVE 5 — Konvergensi (rewire + retire)

- [ ] **2u7.11**: rewire `PegawaiCommandService` → dua DIP port; hapus inject konkret `Generic*`/`RiwayatSkService` · `P2` · deps: 2u7.6, 2u7.9
- [ ] **2u7.12**: wiring 6 controller kepegawaian → Command/Query; retire semua `*ServiceImpl` · `P2` · deps: 2u7.4–2u7.10

> 2u7.11 menyelesaikan inversi dependency (butuh kedua implementor port). 2u7.12 = titik tabrakan controller, dijalankan setelah semua builder hijau.

## WAVE 6 — Cleanup DTO mati (per modul, paralel, wave paling akhir)

- [ ] **2u7.13**: hapus DTO mati modul **master** (verifikasi blast-radius per-DTO) · `P3` · deps: 2u7.11, 2u7.12
- [ ] **2u7.14**: hapus DTO mati modul **profil** (verifikasi blast-radius per-DTO) · `P3` · deps: 2u7.11, 2u7.12
- [ ] **2u7.15**: hapus DTO mati modul **pegawai** (verifikasi blast-radius per-DTO) · `P3` · deps: 2u7.11, 2u7.12

> Dipecah per modul agar bisa diklaim & di-review terpisah. Tiap DTO: `gitnexus_impact` upstream dulu — hapus hanya jika nol caller hidup. Jangan hapus borongan.

---

## Dependency map (ringkas)

```
2u7(EPIC) ─┬─ 2u7.1 (repo->jpa/jooq) ─┬─ 2u7.4 (Lampiran) ─┐
           │                          ├─ 2u7.5 (SP) ───────┼──────────────┐
           │                          └─ 2u7.7 (SK Query) ─┼──────────────┤
           ├─ 2u7.2 (DIP port) ───────┐                    │              │
           └─ 2u7.3 (PegawaiWriteback)┴─ 2u7.6 (SK Command)┤              │
                                            │              ├─ 2u7.8 (Mutasi) ─┐
                                            │              └─ 2u7.9 (Kontrak)─┼─ 2u7.10 (Terminasi)
                                            │                                 │
   2u7.6 ─┬─ 2u7.11 (rewire Pegawai->port) ─┐                                 │
   2u7.9 ─┘                                 ├─ 2u7.13/14/15 (cleanup DTO)     │
   2u7.4..2u7.10 ── 2u7.12 (controller wiring + retire Impl) ─────────────────┘
```

## Acceptance ringkas per issue

**2u7.1 (repo → jpa/jooq)**
- [ ] 6 repo JPA di `repositories/kepegawaian/jpa/`; subpaket `riwayatSp/` dihapus
- [ ] Pindah via `gitnexus_rename`; paket `jooq/` + `mapper/<aggregate>/` ter-scaffold
- [ ] `gitnexus_impact` dilaporkan; `detect_changes` hanya perpindahan paket; `./gradlew build` hijau

**2u7.2 (DIP port)**
- [ ] `SkBootstrapPort` & `KontrakBootstrapPort` di `services/pegawai/port/`; return `RiwayatSk`/`RiwayatKontrak`
- [ ] Tanpa `@Service`/implementor/inject baru; javadoc rujuk ADR-0023; build hijau

**2u7.3 (PegawaiWriteback)**
- [ ] Tidak ada `Generic*Service` writeback tersisa; semua caller terupdate via `gitnexus_rename`
- [ ] `detect_changes` hanya rename writeback + caller; build hijau

**2u7.4 (Lampiran)**
- [ ] Command+Query terpisah; baca Pola A; soft-delete; `deleteByRefId` tersedia utk SK
- [ ] `detect_changes` hanya file Lampiran; build+test hijau

**2u7.5 (SP)**
- [ ] CRUD+berkas identik lama; baca Pola A; soft-delete; `RiwayatSpServiceImpl` di-retire tanpa caller tersisa
- [ ] `detect_changes` hanya file SP; build+test hijau

**2u7.6 (SK Command)**
- [ ] implements `SkBootstrapPort`; `createSk*` return entity dgn `getId()`; `getReferenceById` utk golongan
- [ ] delete cascade lampiran via 2u7.4; `PegawaiWriteback` dipakai; exception tak ditelan
- [ ] `gitnexus_impact` upstream `RiwayatSkServiceImpl` dilaporkan; build+test hijau

**2u7.7 (SK Query)**
- [ ] Baca JOOQ Pola A; mini-projection golongan `row(id,golongan,pangkat)`; sort/paging identik lama
- [ ] Soft-delete terfilter; `detect_changes` hanya file SK read baru; build hijau

**2u7.8 (Mutasi)**
- [ ] `findById` utk field yang di-snapshot (ADR-0022); Command panggil SK CommandService, bukan repo mentah
- [ ] Baca Pola B mapper `static final`; `{status,id}` tanpa re-read; build+test hijau

**2u7.9 (Kontrak)**
- [ ] implements `KontrakBootstrapPort` return entity; cabang PERPANJANGAN/PENGANGKATAN jelas; `setIsLatest` dipertahankan
- [ ] `GenericKontrakService` di-retire; Command panggil SK via 2u7.6; build+test hijau

**2u7.10 (Terminasi)**
- [ ] Fan-out lewat CommandService pemilik (SK/Mutasi/Kontrak), bukan repo mentah; single `@Transactional` exception tak ditelan
- [ ] Tidak ada `findAll` full-scan tersisa; snapshot via `findById`; build+test hijau

**2u7.11 (rewire Pegawai → port)**
- [ ] `PegawaiCommandService` inject hanya dua interface port (bukan kelas konkret kepegawaian); siklus impor hilang
- [ ] `refSk*Id` tetap diset di `PegawaiCommandService`; `gitnexus_impact` dilaporkan; build+test hijau

**2u7.12 (controller wiring + retire Impl)**
- [ ] 6 controller per-aggregate; semua endpoint + `@PreAuthorize` ADMIN dipertahankan; tanpa `*CommandController/*QueryController`
- [ ] Semua `*ServiceImpl` kepegawaian dihapus tanpa caller tersisa (`gitnexus_impact` bersih); `./gradlew test` hijau

**2u7.13 / 2u7.14 / 2u7.15 (cleanup DTO master/profil/pegawai)**
- [ ] Tiap DTO dihapus punya bukti nol caller (`gitnexus_impact`); tidak ada hapus borongan
- [ ] build+test hijau; `detect_changes` hanya `dto/<modul>` masing-masing

## Cara update checklist

- Klaim: `bd update kepegawaian-<id> --claim`
- Selesai: `bd close kepegawaian-<id>` → centang `[x]` di sini
- Cek yang siap dikerjakan: `bd ready`
- Status keseluruhan: `bd list --status=open | grep 2u7`
- Pohon dependency: `bd dep tree kepegawaian-2u7`

## REF

- Pattern sumber: `docs/pegawai-cqrs-rewrite-claim-order.md` (template wave) · `docs/master-response-pattern-guide.md` (keputusan response/DTO)
- ADR-0001 (CQRS JPA-write/JOOQ-read) · 0006 (layer-first) · 0007 (concrete service) · 0008 (getReferenceById)
- ADR-0017 (JOOQ di repository) · 0020 (baca tabel lintas modul) · 0021 (saga atomik) · 0022 (findById utk snapshot label) · **0023 (DIP port lintas-modul)**
- Struktur paket dikunci: `kepegawaian-h02` (CLOSED) · Glossary keputusan: `CONTEXT.md`
- Backlog terkait: `kepegawaian-9kn` (P3 dead code) · `kepegawaian-1dx` (P4 table-flag)

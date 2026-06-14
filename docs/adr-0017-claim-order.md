# ADR-0017 — Claim Order & Monitoring

Epic **kepegawaian-hgg** — Pindahkan akses data JOOQ master ke lapisan repository (`repositories/master/jpa/` + `jooq/`) + mapper statik ke `mapper/master/<domain>/`. Sesuai [ADR-0017](adr/0017-jooq-read-into-repository-layer.md).

Urutan klaim **mengikuti dependency**, bukan nomor issue. Kerjakan per **WAVE**: semua issue dalam satu wave boleh diklaim paralel (tidak saling blok); wave berikutnya baru terbuka setelah wave sebelumnya selesai. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun:** patuhi WORKING AGREEMENT — gitnexus-first (`gitnexus_impact` upstream sebelum edit, WARN bila HIGH/CRITICAL), strict scope (HANYA file di issue; di luar scope → `bd create --deps discovered-from:<id>`), DRY+KISS, concrete `@Service`/`@Repository` (tanpa interface+Impl, ADR-0007), soft-delete only, alur `claim → git mv + Edit → gitnexus_detect_changes → ./gradlew test → close`. `git mv` TIDAK auto-buat folder tujuan — `mkdir -p` dulu.

**Scope:** domain `master` saja, branch `rewrite/master-cqrs`. **JenjangPendidikan + Level TETAP datar** di `repositories/master/` (gaya lama `ServiceImpl`, belum CQRS — bukan bagian epic ini).

---

## WAVE 0 — Exemplar (1 issue, GERBANG)

- [ ] **hgg / s55** · golongan (EXEMPLAR): `GolonganQueries` → `repositories/master/jooq/GolonganQueryRepository.java` (`@Service`→`@Repository`); `GolonganRepository` → `repositories/master/jpa/`; `GolonganMapper` → `mapper/master/golongan/`; `QueryService` tetap di `services/` (orkestrasi tipis, inject `GolonganQueryRepository`, lempar `NotFoundException`) · `P1` · deps: — · importers: 9

> **Gerbang replikasi.** Semua domain WAVE 1 meniru pola ini. Review ekstra ketat — pola yang salah di sini menyebar ke 13 issue berikutnya. Satu-satunya issue yang muncul di `bd ready` sekarang.

## WAVE 1 — Replikasi (13 issue paralel, semua butuh s55)

Pola identik exemplar: `*Queries`→`jooq/*QueryRepository` (`@Repository`), `*Repository`→`jpa/`, `*Mapper`→`mapper/master/<domain>/`, `QueryService` tetap orkestrasi tipis.

- [ ] **uon** · alasanBerhenti · `P2` · deps: s55 · importers: 3
- [ ] **mh9** · grade · `P2` · deps: s55 · importers: 3
- [ ] **tn1** · hariLibur · `P2` · deps: s55 · importers: 5
- [ ] **0fs** · jabatan · `P2` · deps: s55 · importers: 8
- [ ] **s0q** · jenisKeahlian · `P2` · deps: s55 · importers: 3
- [ ] **thh** · jenisKitas · `P2` · deps: s55 · importers: 3
- [ ] **08u** · jenisPelatihan · `P2` · deps: s55 · importers: 3
- [ ] **550** · jenisSp · `P2` · deps: s55 · importers: 4
- [ ] **9ma** · organisasi · `P2` · deps: s55 · importers: 7
- [ ] **409** · rumahDinas · `P2` · deps: s55 · importers: 3
- [ ] **c0v** · sanksi · `P2` · deps: s55 · importers: 2

### Issue khusus (pola beda — baca design issue penuh)

- [ ] **yz9** · **profesi** (aggregate JOOQ) · `P2` · deps: s55 · importers: 5
  - `ProfesiQueries` → `ProfesiQueryRepository`, PLUS `ProfesiDetailQuery` (`@Service` kedua), `ProfesiRowMapper`, `ProfesiSelects` SEMUA → `repositories/master/jooq/` (jaga satu paket agar helper package-private tetap akses).
  - `ProfesiRepository` → `jpa/`. Hanya `ProfesiMapper` (JPA) → `mapper/master/profesi/`.
- [ ] **dqn** · **apd + alatKerja** (JPA-only, tanpa JOOQ) · `P2` · deps: s55 · importers: 1 + 1
  - Hanya `*Repository` → `jpa/` dan `*Mapper` → `mapper/`. Tidak ada kelas Queries (read-side terlipat ke agregat Profesi, ADR-0011).

---

## Dependency map (ringkas)

```
s55 (golongan EXEMPLAR) ──┬── uon (alasanBerhenti)
                          ├── mh9 (grade)
                          ├── tn1 (hariLibur)
                          ├── 0fs (jabatan)
                          ├── s0q (jenisKeahlian)
                          ├── thh (jenisKitas)
                          ├── 08u (jenisPelatihan)
                          ├── 550 (jenisSp)
                          ├── 9ma (organisasi)
                          ├── 409 (rumahDinas)
                          ├── c0v (sanksi)
                          ├── yz9 (profesi*)
                          └── dqn (apd+alatKerja*)

semua 14 leaf ──► hgg (epic, in_progress) — tutup terakhir
```

## Cara update checklist

- Klaim: `bd update kepegawaian-<id> --claim`
- Selesai: `bd close kepegawaian-<id>` → centang `[x]` di sini
- Cek yang siap dikerjakan kapan saja: `bd ready`
- Status keseluruhan: `bd dep tree kepegawaian-hgg`
- Epic baru boleh ditutup setelah ke-14 leaf selesai (semuanya memblokir `hgg`).

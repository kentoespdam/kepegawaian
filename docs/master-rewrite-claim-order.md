# Master Rewrite — Claim Order & Monitoring

Epic **kepegawaian-5x7** — Rewrite modul MASTER ke Spring Boot 4 + CQRS (JPA write / JOOQ read).

Urutan klaim di bawah **mengikuti dependency**, bukan nomor issue. Kerjakan per **WAVE**: semua issue dalam satu wave boleh diklaim paralel (tidak saling blok); wave berikutnya baru terbuka setelah wave sebelumnya selesai. `bd ready` selalu jadi sumber kebenaran issue yang sudah unblocked.

**Sebelum klaim apa pun**, baca WORKING AGREEMENT di `bd show kepegawaian-5x7` (gitnexus-first, context7-first, DRY+KISS, strict scope, claim→code→detect_changes→test→close).

---

## WAVE 0 — Akar (1 issue, blokir semua)

- [ ] **5x7.1** · F1: `build.gradle.kts` — Gradle Kotlin DSL, Spring Boot 4.0.3 + JOOQ (+springdoc 3.0.x, drop webflux) · `P1` · deps: —

> Tidak ada pekerjaan lain yang boleh mulai sebelum ini merge.

## WAVE 1 — Foundation paralel (5 issue, semua butuh F1)

- [ ] **5x7.2** · F2: Flyway V1 master migration + seed (schema source of truth) · `P1` · deps: 5x7.1
- [ ] **5x7.4** · F4: Foundation slice — ApiResponse, PageResult, RestControllerAdvice, typed exceptions · `P1` · deps: 5x7.1
- [ ] **5x7.5** · F5: Base class master (`@MappedSuperclass`) + JPA auditing · `P1` · deps: 5x7.1
- [ ] **5x7.14** · F6.S1: Migrasi OpenApiConfig ke springdoc-openapi 3.x (Boot 4) · `P1` · deps: 5x7.1
- [ ] **5x7.15** · F6.S2: Migrasi klien Appwrite WebClient → RestClient (drop webflux) · `P1` · deps: 5x7.1

## WAVE 2 — Lanjutan foundation (3 issue)

- [ ] **5x7.3** · F3: Task Gradle codegen JOOQ via GenerationTool (manual, sumber di-commit) · `P1` · deps: 5x7.1, **5x7.2**
- [ ] **5x7.16** · F6.S3: Split SecurityFilterChain jadi 2 `@Profile` (prod JWT / dev bypass) + CORS · `P1` · deps: **5x7.15**
- [ ] **5x7.13** · E7: 5 master enum-backed READ-ONLY (StatusKerja/StatusPegawai/JenisKontrak/JenisMutasi/JenisSk) · `P2` · deps: **5x7.4** _(hanya butuh F4 — boleh jalan lebih awal, tidak menunggu exemplar)_

## WAVE 3 — Exemplar (1 issue, GERBANG)

- [ ] **5x7.6** · E0 (EXEMPLAR): Golongan — pola kanonik CQRS satu entity master flat · `P1` · deps: 5x7.14, 5x7.16, 5x7.2, 5x7.3, 5x7.4, 5x7.5

> **Gerbang replikasi.** Semua E1–E6 meniru pola ini. Review ekstra ketat — pola yang salah di sini menyebar ke 6 issue berikutnya.

## WAVE 4 — Replikasi pola flat/tree (5 issue paralel, semua butuh E0)

- [ ] **5x7.7** · E1: 8 entity master FLAT — ikuti pola exemplar Golongan · `P2` · deps: 5x7.6
- [ ] **5x7.8** · E2: Organisasi — entity tree self-ref + parent-embed mini record · `P2` · deps: 5x7.6
- [ ] **5x7.9** · E3: Jabatan — tree self-ref + FK Level/Golongan + feeder per-organisasi · `P2` · deps: 5x7.6
- [ ] **5x7.10** · E4: Grade — tree self-ref + FK Level · `P2` · deps: 5x7.6
- [ ] **5x7.12** · E6: JenisSp + Sanksi — dua entity standalone, Sanksi FK JenisSp · `P2` · deps: 5x7.6

## WAVE 5 — Aggregate (1 issue)

- [ ] **5x7.11** · E5: Profesi (AGGREGATE) — APD & AlatKerja dilipat, MULTISET, level denorm · `P2` · deps: 5x7.6, **5x7.9** _(butuh Jabatan untuk derivasi level)_

---

## Dependency map (ringkas)

```
F1(1) ──┬── F2(2) ──┬── F3(3) ─────────────┐
        ├── F4(4) ──┼───────────────┐      │
        │           └── E7(13)      │      │
        ├── F5(5) ──────────────────┤      │
        ├── S1(14) ─────────────────┤      │
        └── S2(15) ── S3(16) ───────┴──► E0(6) ──┬── E1(7)
                                                  ├── E2(8)
                                                  ├── E3(9) ──► E5(11)
                                                  ├── E4(10)
                                                  └── E6(12)
```

## Cara update checklist

- Klaim: `bd update kepegawaian-5x7.<n> --claim`
- Selesai: `bd close kepegawaian-5x7.<n>` → centang `[x]` di sini
- Cek yang siap dikerjakan kapan saja: `bd ready`
- Status keseluruhan: `bd list --status=open | grep 5x7`

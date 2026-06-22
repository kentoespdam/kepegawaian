# Claim Order — Adopsi Pattern Publication ke Modul Master

Urutan klaim eksekusi issue beads `pattern-adoption` agar pattern paging/sort + write-flow `{status,id}` ter-rollout konsisten ke semua modul master. Dipasangkan dengan `docs/master-response-pattern-guide.md` (sumber kebenaran pattern) dan Organization kepegawaian-dcz (referensi shipped).

**Prinsip klaim**: 1 issue = 1 PR. Pilot dulu, baru wave. Paging/sort SELALU sebelum write-flow (urutan dalam child issue tidak diputar). Apd/AlatKerja khusus: write-flow saja, paging tidak applicable.

> **Cara pakai**: Buka file ini, klaim sesuai urutan di kolom "Klaim". Tandai checklist `[x]` setelah setiap child selesai di-PR. Sebelum pindah ke issue berikutnya di urutan, kerjakan dulu checklist per-module.

---

## A. Klaim berurutan (master list)

| # | Epic | Child paging/sort | Child write-flow | Tipe | Prioritas | Catatan |
|---|------|-------------------|------------------|------|-----------|---------|
| 1 | `kepegawaian-92j` | `.1` ✓ | `.2` ✓ | **PILOT** | P2 | Jabatan — SHIPPED 2026-06-22 (commits 3d6f6da + 9cea3fb); drift: spec said `Map<String,String>` but shipped Organisasi uses `Map<String,Field<?>>`; verifier pending before wave 1 |
| 2 | `kepegawaian-jgt` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Grade — SHIPPED 2026-06-22 (commits 6ca8622 + d6fbaae) |
| 3 | `kepegawaian-5oz` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Profesi — SHIPPED 2026-06-22 (commits f05fae8 + 9bb91bc) |
| 4 | `kepegawaian-e21` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Level — SHIPPED 2026-06-22 (commits 8d81665 + 9f60698) |
| 5 | `kepegawaian-1lw` | `.1` ✓ | `.2` ✓ | wave 1 | P2 | Golongan — SHIPPED 2026-06-22 (commits 9e7d4da + ed95060); Response NOT deleted (8 external consumers — epic claim was stale) |
| 6 | `kepegawaian-92q` | `.1` | `.2` | wave 2 | P2 | AlasanBerhenti — Response keep |
| 7 | `kepegawaian-oit` | `.1` | `.2` | wave 2 | P2 | JenisKeahlian — Response keep |
| 8 | `kepegawaian-04q` | `.1` | `.2` | wave 2 | P2 | JenisKitas — Response keep |
| 9 | `kepegawaian-c0d` | `.1` | `.2` | wave 2 | P2 | JenisPelatihan — Response keep |
| 10 | `kepegawaian-4fj` | `.1` | `.2` (+ opsional hapus Response) | wave 2 | P2 | JenisSp — Response 0 consumer |
| 11 | `kepegawaian-kh2` | `.1` | `.2` (+ opsional hapus Response) | wave 2 | P2 | Sanksi — Response 0 consumer |
| 12 | `kepegawaian-ehy` | `.1` (klarifikasi A/B dulu) | `.2` (+ opsional hapus Response) | wave 3 | P2 | HariLibur — paging-issue butuh klarifikasi |
| 13 | `kepegawaian-tc1` | `.1` (klarifikasi A/B dulu) | `.2` | wave 3 | P2 | RumahDinas — paging-issue butuh klarifikasi |
| 14 | `kepegawaian-avt` | — | `.1` | wave 4 | P2 | Apd — write-flow ONLY (sub-resource) |
| 15 | `kepegawaian-wbo` | — | `.1` | wave 4 | P2 | AlatKerja — write-flow ONLY (sub-resource) |

**Sub-resource (Apd, AlatKerja)**: hanya 1 child (write-flow), bukan 2. Paging/sort tidak applicable karena pakai JPA `Pageable`, bukan JOOQ switch inline.

---

## B. Wave structure (urutan eksekusi + verifikasi)

| Wave | Modul | Setelah wave ini |
|------|-------|------------------|
| **Pilot** | Jabatan (1 epic, 2 children) | verifier PASS 1 modul → minta user ACC wave 1 |
| **Wave 1** | Grade, Profesi, Level, Golongan (4 epic paralel) | verifier PASS 4 modul → minta user ACC wave 2 |
| **Wave 2** | AlasanBerhenti, JenisKeahlian, JenisKitas, JenisPelatihan, JenisSp, Sanksi (6 epic paralel) | verifier PASS 6 modul → minta user ACC wave 3 |
| **Wave 3** | HariLibur, RumahDinas (2 epic, butuh klarifikasi A/B) | verifier PASS → minta user ACC wave 4 |
| **Wave 4** | Apd, AlatKerja (2 epic sub-resource, write-flow only) | verifier PASS → epic-closing PR |

**Setiap wave ditutup** dengan: `bd close <epic>` + `gitnexus_detect_changes(scope=all)` bersih + push PR + `bd ready` di wave berikut.

**Cadence 1-issue-then-tanya** (lihat memory `wave-replication-cadence`): Setelah pilot Jabatan SHIP, JANGAN langsung lanjut wave 1 — tanya user dulu via AskUserQuestion.

---

## C. Pre-flight checklist (jalankan sekali sebelum mulai)

- [ ] `bd prime` jalan tanpa error (issue tracker terkoneksi)
- [ ] `gitnexus list_repos` melihat repo `kepegawaian` terindeks (lihat `AGENTS.md`)
- [ ] Branch `rewrite/master-cqrs` ada dan bersih dari noise (`git status` clean atau hanya file terkait)
- [ ] `docs/master-response-pattern-guide.md` sudah dibaca dan dipahami
- [ ] `docs/organisasi-publication-pattern-claim-order.md` sudah dibaca (template eksekusi Organisasi sebelumnya, referensi keputusan serupa)
- [ ] `Organization` epic kepegawaian-dcz status SHIPPED — dijadikan acuan template PagedRequest/SortParam
- [ ] `gitnexus_rename` atau `git mv` **TIDAK** dipakai untuk rename simbol (lihat memory `gitnexus-rename-vs-mv`)
- [ ] Lokasi pattern referensi:
  - `Organisasi PagedRequest`: `src/main/java/id/perumdamts/kepegawaian/dto/master/organisasi/commons/PagedRequest.java`
  - `Organisasi SortParam`: `src/main/java/id/perumdamts/kepegawaian/dto/master/organisasi/commons/SortParam.java`
  - `OrganisasiIndexQuery`: `src/main/java/id/perumdamts/kepegawaian/dto/master/organisasi/OrganisasiIndexQuery.java`
  - `OrganisasiController`: `src/main/java/id/perumdamts/kepegawaian/controllers/master/OrganisasiController.java`
  - `OrganisasiQueryRepository`: `src/main/java/id/perumdamts/kepegawaian/repositories/master/jooq/OrganisasiQueryRepository.java`

---

## D. Per-module checklist (salin untuk setiap modul)

### D.1 Pre-flight per modul

- [ ] Issue di-claim via `bd update --claim <issue-id>` (lihat memory `beads-workflow-patterns`)
- [ ] `gitnexus_impact({target:"<X>IndexQuery", direction:"upstream", repo:"kepegawaian"})` dijalankan & blast radius dicatat di komentar issue
- [ ] `gitnexus_impact({target:"<X>QueryRepository", direction:"upstream", repo:"kepegawaian"})` dijalankan & blast radius dicatat
- [ ] `gitnexus_impact({target:"<X>Controller", direction:"upstream", repo:"kepegawaian"})` dijalankan & blast radius dicatat
- [ ] Jika salah satu impact = HIGH/CRITICAL → STOP, eskalasi ke user (lihat memory `manager-locked-decisions-beads`)
- [ ] `<X>IndexQuery extends PagedRequest` (bukan `CommonPageRequest`) — konfirmasi dengan Read
- [ ] `<X>QueryRepository` punya `switch (query.getSortBy())` atau pakai Spring `PageRequest.of(...)` — konfirmasi
- [ ] `<X>Response` external consumer count dihitung: `grep -rln "<X>Response" src/main/java/ | grep -v "dto/master/<x>" | wc -l`

### D.2 Child paging/sort checklist

- [ ] File `dto/master/<x>/commons/PagedRequest.java` dibuat — copy dari Organisasi, page/size clamp + offset()
- [ ] File `dto/master/<x>/commons/SortParam.java` dibuat — record + `resolve(sortBy, sortDir, allowedSorts, defaultColumn)`
- [ ] `<X>IndexQuery`: `extends CommonPageRequest` → `extends PagedRequest` (+ `@EqualsAndHashCode(callSuper=true)` jika perlu)
- [ ] `<X>QueryRepository`: switch inline diganti `private static final Map<String,String> ALLOWED = Map.of(...)` + `SortParam.resolve(..., "<DEFAULT_COL>")`
- [ ] Ternary `"asc".equalsIgnoreCase(query.getSortDirection()) ? asc : desc` dihapus (pindah ke `SortParam.resolve`)
- [ ] Default sortBy null/blank → ID DESC, **tidak error**
- [ ] Jika modul pakai Spring `PageRequest.of(...)` (HariLibur/RumahDinas): SortParam tetap ditulis untuk konsistensi, tapi whitelist bisa kosong / repo tetap pakai Spring
- [ ] `./gradlew compileJava` hijau
- [ ] `gitnexus_detect_changes(scope=unstaged)` bersih: hanya sentuh `dto/master/<x>/` + `repositories/master/jooq/<X>QueryRepository.java`

### D.3 Child write-flow checklist

- [ ] `<X>Controller` POST: `SavedStatus.build(ESaveStatus.SUCCESS, entity)` → `SavedStatus.build(ESaveStatus.SUCCESS, entity.getId())`
- [ ] `<X>Controller` PUT: sama seperti POST
- [ ] `<X>Controller` DELETE: lihat OrganisasiController untuk konvensi (`CustomResult.delete(...)` atau `entity.getId()` jika return entity)
- [ ] `<X>CommandService` method signature **TIDAK** diubah (transaksi/audit internal)
- [ ] Tidak ada re-read dari DB setelah save
- [ ] Tidak ada kebocoran relasi/lazy entity di response JSON
- [ ] Soft-delete tetap utuh (cek log: `Hibernate update set is_deleted=true`, BUKAN delete SQL)
- [ ] Jika `<X>Response` punya 0 external consumer (per pre-flight): hapus file + hapus semua reference (services/repository imports). Jika ada external consumer: JANGAN hapus, catat di issue notes untuk fase rewrite berikut
- [ ] `./gradlew compileJava` + `./gradlew test` hijau
- [ ] `gitnexus_detect_changes(scope=unstaged)` bersih: hanya sentuh `<X>Controller` (+ opsional `<X>Response.java`)

### D.4 Sub-resource khusus (Apd/AlatKerja)

- [ ] Skip child paging/sort (tidak applicable — JPA Pageable)
- [ ] Hanya jalankan child write-flow (D.3) dengan path controller: `/master/profesi/{profesiId}/apd` atau `/master/profesi/{profesiId}/alat-kerja`
- [ ] Read endpoint TIDAK diubah (di luar scope epic ini)

### D.5 Verifikasi per epic

- [ ] `gitnexus_detect_changes(scope=staged)` hijau — hanya scope sesuai pre-flight
- [ ] `./gradlew compileJava` + `./gradlew compileTestJava` (gate tanpa DB, lihat memory `it-test-compile-gate-no-db`)
- [ ] `./gradlew bootRun` jalan + curl test POST/PUT/DELETE → response body hanya `{status, id}`
- [ ] GET `/{x}/page?sortBy=kode&sortDir=asc` → tidak error, hasil ter-sort sesuai kolom
- [ ] GET `/{x}/page?sortBy=invalid_column` → tidak error, fallback ke default ID
- [ ] Tidak ada dependency Gradle baru di `build.gradle.kts`
- [ ] 1 PR per child issue (1 epic = 2 PR), commit message gaya repo (lihat `git log --oneline -20`)
- [ ] `bd close <issue-id>` setelah PR merged
- [ ] Setelah kedua child closed → `bd close <epic-id>`

---

## E. Per-wave checklist

### E.1 Pilot wave (Jabatan)

- [x] Issue `kepegawaian-92j.1` selesai sesuai checklist D.2, PR merged
- [x] Issue `kepegawaian-92j.2` selesai sesuai checklist D.3, PR merged
- [x] Epic `kepegawaian-92j` di-close
- [x] Verifier PASS (lihat memory `verification-subagent-gate`)
- [ ] **STOP & Tanya user** via AskUserQuestion: "Pilot Jabatan SHIPPED, lanjut wave 1 (Grade/Profesi/Level/Golongan)?"

### E.2 Wave 1 (Grade, Profesi, Level, Golongan)

- [x] 4 × (D.1 → D.2 → D.3 → D.5) selesai
- [x] Verifier PASS untuk SEMUA 4 epic
- [ ] **STOP & Tanya user** via AskUserQuestion: "Wave 1 SHIPPED, lanjut wave 2?"

### E.3 Wave 2 (AlasanBerhenti, JenisKeahlian, JenisKitas, JenisPelatihan, JenisSp, Sanksi)

- [ ] 6 × (D.1 → D.2 → D.3 → D.5) selesai
- [ ] Verifier PASS untuk SEMUA 6 epic
- [ ] **STOP & Tanya user** via AskUserQuestion: "Wave 2 SHIPPED, lanjut wave 3?"

### E.4 Wave 3 (HariLibur, RumahDinas) — KLARIFIKASI A/B

- [ ] **Sebelum mulai**: Read `<X>QueryRepository` dan `<X>IndexQuery` untuk konfirmasi sort switch vs Spring `PageRequest.of`
- [ ] Update issue notes dengan hasil klarifikasi (kasus A = switch inline; kasus B = Spring PageRequest.of)
- [ ] 2 × (D.1 → D.2 → D.3 → D.5) selesai sesuai klarifikasi
- [ ] Verifier PASS untuk 2 epic
- [ ] **STOP & Tanya user** via AskUserQuestion: "Wave 3 SHIPPED, lanjut wave 4 (sub-resource)?"

### E.5 Wave 4 (Apd, AlatKerja)

- [ ] 2 × (D.1 → D.4 → D.3 → D.5) selesai (D.4 = sub-resource skip paging)
- [ ] Verifier PASS untuk 2 epic
- [ ] **Tutup dengan AskUserQuestion**: "Semua wave pattern-adoption SHIPPED. Tutup semua epic pattern-adoption atau ada modul tambahan?"

---

## F. Pitfalls (lihat memory cluster `git-edit-build-pitfalls`)

- [ ] **Post-mv re-Read**: Setelah `git mv`, WAJIB `Read` lagi sebelum `Edit` (stale path invalid)
- [ ] **Stage-then-Edit**: Setelah `git add`, WAJIB `git status` dulu, jangan `Edit` paralel (cache invalidation)
- [ ] **Parallel-block silent no-op**: Cek return value `bd create` jangan diabaikan (silent ID bisa konflik)
- [ ] **Clean compileJava**: Setiap wave tutup dengan full clean build (`./gradlew clean compileJava`)
- [ ] **Scope commit saat noise**: Jika working tree ada file pre-existing noise dari sesi lain, pakai `git commit -- <paths>` (lihat memory `scoped-commit-pre-existing-noise`)
- [ ] **Verifier interrupted**: Jika verifier return truncated (output_tokens 0-9), cek transcript + run gate sendiri (lihat memory `verifier-interrupted-partial-evidence`)

---

## G. Out-of-scope reminders (JANGAN kerjakan di claim-order ini)

Modul-modul ini **TIDAK** dibuat epic `pattern-adoption` karena di luar cakupan pattern-guide:

| Modul | Alasan out-of-scope |
|-------|---------------------|
| `Organisasi` | Sudah SHIPPED di kepegawaian-dcz (referensi) |
| `StatusKerja` | Enum-style (no entity, no Command service) |
| `StatusPegawai` | Enum-style |
| `JenisKontrak` | Enum-style (no soft-delete) |
| `JenisMutasi` | Enum-style |
| `JenisSk` | Enum-style |
| `JenjangPendidikan` | Non-CQRS (JPA-based, satu Service) — perlu CQRS split penuh, bukan adopsi pattern |

Jika user nanti meminta modul-modul ini, **buat epic terpisah** dengan judul berbeda (mis. `JenjangPendidikan: CQRS split`) — JANGAN gabungkan dengan claim-order pattern-adoption.

---

## REF

- `docs/master-response-pattern-guide.md` — sumber kebenaran pattern
- `docs/organisasi-publication-pattern-claim-order.md` — template claim-order Organisasi (referensi keputusan serupa)
- `kepegawaian-dcz` — epic Organisasi SHIPPED
- Memory: `claim-order-manifest`, `wave-replication-cadence`, `pilot-wave-overrides-monolithic`, `manager-locked-decisions-beads`, `verification-subagent-gate`, `beads-workflow-patterns`

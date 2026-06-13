# Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build

ADR-0012 mengunci *mekanisme* codegen (`GenerationTool` di satu task imperatif, rantai Testcontainers→Flyway→generate). ADR ini menutup fork **kebijakan output**-nya: apakah sumber ter-generate dibuat ulang tiap build atau di-commit, dan apakah codegen disambungkan ke `compileJava`.

Gesekannya nyata dan terdokumentasi: PRD/grill lama (`grill/PRD-cqrs-jooq-flyway.md` US#4, `grill/2026-05-05_cqrs-jooq-flyway-migration.md:22`) menginginkan sumber **di-commit** ke `src/main/java/.../jooq/` dan regen manual supaya "CI/CD tidak perlu koneksi database saat build". Sementara konsekuensi awal ADR-0012 menyebut `compileJava` **bergantung** pada task codegen — yang berarti tiap build menyalakan kontainer. Keduanya tidak bisa benar bersamaan.

## Keputusan

Sumber JOOQ ter-generate **di-commit ke git** di `src/main/java/id/perumdamts/kepegawaian/jooq/`. Task codegen ADR-0012 dijalankan **manual** (mis. `./gradlew jooqCodegen`) **hanya saat skema berubah**, dan **tidak** disambungkan ke `compileJava`.

Konsekuensi langsung: build biasa dan CI di GitHub Actions **tidak pernah** menyalakan Testcontainers / Docker. Hanya Flyway migration + sumber JOOQ yang sudah ter-commit yang dipakai saat kompilasi.

## Considered Options

- **Generate-on-build, gitignored, `compileJava` depends on codegen** (ditolak): tiap build regen dari skema live, jadi sumber JOOQ mustahil drift dari migrasi Flyway — selalu lockstep. Harga: CI butuh Docker/Testcontainers dan tiap clean build bayar ongkos start-kontainer + migrate. **Pemblokir keras:** target CI adalah GitHub Actions free tier yang tidak bisa menjalankan kontainer untuk codegen ini. Opsi ini menggugurkan kebutuhan CI bebas-DB yang dipatok PRD lama.
- **Commit + regen manual** (dipilih): sumber di-commit, codegen task manual, `compileJava` tidak depend. CI hermetik, cepat, nol Docker — cocok dengan free tier. Harga: sumber ter-generate bisa diam-diam drift dari migrasi bila seseorang lupa regen setelah mengubah migration; dimitigasi dengan disiplin "ubah migration → jalankan `jooqCodegen` → commit sumbernya bareng migration di PR yang sama".

## Consequences

- `compileJava` **tidak** bergantung pada task codegen (mengoreksi konsekuensi awal ADR-0012; ADR-0012 sudah di-amend agar konsisten).
- CI GitHub Actions free tier aman: tanpa kontainer saat build. Verifikasi codegen adalah tanggung jawab developer di mesin lokal sebelum push.
- Direktori `src/main/java/id/perumdamts/kepegawaian/jooq/` adalah artefak ter-generate yang ikut ditelusuri git; perubahannya muncul di diff PR sehingga drift skema-vs-JOOQ kasat mata saat review.
- Disiplin proses jadi kontrak: setiap PR yang mengubah migration Flyway WAJIB menyertakan hasil regen JOOQ di commit yang sama.
- Konsisten dengan PRD/grill lama (`grill/PRD-cqrs-jooq-flyway.md` US#4) dan nilai KISS proyek.

## Status

Accepted — 2026-06-13

# Flyway sebagai sumber kebenaran schema

Dalam rewrite, schema database dimiliki oleh migration Flyway (`db/migrations`), bukan oleh `ddl-auto` Hibernate. Hibernate dikonfigurasi `validate` saja; production tidak pernah `create`/`update`.

Alasan: jalur baca memakai JOOQ, yang men-*generate* kode dari schema saat build. JOOQ butuh schema deterministik dan sudah jadi sebelum kompilasi — `ddl-auto: create` (dipakai project lama) tidak layak produksi dan membuat urutan build rapuh (JOOQ bergantung pada DB hidup hasil Hibernate). Dengan Flyway, migration → DB → JOOQ codegen menjadi rantai yang deterministik.

## Considered Options

- **Hibernate `ddl-auto` sebagai sumber kebenaran** (ditolak): entity JPA jadi otoritas schema, tapi `create` tak layak produksi dan codegen JOOQ bergantung DB hasil bootstrap Hibernate.
- **Flyway sebagai sumber kebenaran** (dipilih): migration deterministik, Hibernate `validate`, JOOQ codegen jalan terhadap DB hasil migrasi.

## Consequences

- Migration ditulis & dipelihara manual (boleh dibantu generate dari field entity, tapi wajib review) — tidak lagi gratis dari Hibernate. Kolom `version` (optimistic lock) dan tabel Envers (untuk modul yang pakai) harus eksplisit di migration.
- File `V1_0_0__create_master.sql` yang ada mengandung bug: index `golongan_idx (golongan_id)` di tabel `jabatan` menunjuk kolom yang tak dideklarasikan, dan belum ada kolom `version`. Harus dibereskan sebelum migration diaktifkan.
- Hibernate `ddl-auto` di-set `validate` agar drift entity-vs-schema ketahuan saat startup.

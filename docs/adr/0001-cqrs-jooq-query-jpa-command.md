# CQRS: JOOQ untuk Query, JPA untuk Command

Menerapkan pola CQRS di mana JOOQ menangani seluruh operasi baca (query, pagination, proyeksi DTO) dan JPA tetap menangani operasi tulis (create, update, soft-delete).

Alternatif yang dipertimbangkan: JOOQ menggantikan JPA sepenuhnya. Ditolak karena akan kehilangan Spring Data Envers (audit trail), `@EntityListeners` (AuditAware untuk `created_by`/`updated_by`), `@SQLDelete` (soft-delete otomatis), dan `@Version` (optimistic locking) — semua sudah digunakan secara konsisten di 60+ entity melalui `IdsAbstract`.

Konsekuensi: setiap domain yang dimigrasikan akan memiliki dua service (`XxxCommandService` untuk JPA, `XxxQueryService` untuk JOOQ) dan dua repository layer. JOOQ code-gen harus dijalankan terhadap schema yang dikelola Flyway.

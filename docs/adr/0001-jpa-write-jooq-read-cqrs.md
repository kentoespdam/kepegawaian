# Pemisahan jalur Command (JPA) dan Query (JOOQ)

Dalam rewrite ke Spring Boot 4 (Java, build `build.gradle.kts`), modul master menerapkan CQRS ringan: jalur tulis (Command) memakai JPA/Hibernate, jalur baca (Query) memakai JOOQ. Satu database MariaDB, dua teknologi akses.

Alasan: jalur tulis butuh soft-delete (`@SQLDelete`), auto-audit kolom (`@CreatedBy`/`@CreatedDate`), dan optimistic locking (`@Version`) — semuanya nyaris gratis dari Hibernate. JOOQ unggul di jalur baca yang kompleks (join Profesi→Organisasi→Jabatan→Level→Grade tanpa N+1, proyeksi langsung ke DTO).

Envers (full revision history) **tidak** dipakai di master — lihat ADR-0003. Untuk master, justifikasi JPA di sisi tulis bertumpu pada soft-delete + optimistic lock + kolom audit, bukan Envers.

## Considered Options

- **JOOQ untuk write dan read** (ditolak): satu teknologi, tapi memaksa reimplementasi Envers, soft-delete, dan optimistic locking secara manual — justru menambah kompleksitas, melanggar KISS.
- **Event-sourced / read-write DB terpisah** (ditolak): overkill untuk master data referensi.

## Consequences

- Dua teknologi persistence hidup berdampingan dalam satu aplikasi — beberapa orang menganggap ini sendiri melanggar KISS. Trade-off diterima demi audit gratis di sisi tulis.
- Struktur folder memisahkan kode JPA dan JOOQ. Service dipisah per file (mis. `ProfesiCommandService` dan `ProfesiQueryService`) tanpa command/query bus atau mediator.

# JOOQ codegen membaca schema dari Testcontainers MariaDB

Saat build, JOOQ code generation membaca schema dari kontainer **MariaDB ephemeral (Testcontainers)** yang lebih dulu dimigrasi oleh Flyway, bukan dari DB hidup atau parser DDL. Rantainya: Gradle nyalakan kontainer → Flyway migrate → JOOQ codegen baca schema → kontainer dimatikan.

Alasan: ini menutup rantai deterministik yang jadi alasan memilih Flyway (lihat ADR-0002). Migrasi yang sama yang jalan di produksi juga yang jalan saat codegen, terhadap engine MariaDB asli — jadi tipe dan perilaku kolom 100% akurat, dan hasil codegen tak bergantung keadaan DB seseorang.

## Considered Options

- **Live dev DB** (ditolak): codegen menunjuk DB MariaDB yang sudah jalan. Sederhana tapi rapuh — hasil bergantung keadaan DB itu; kalau migrasi belum jalan, kode ter-generate salah. Kerapuhan yang sama yang membuat kita menolak `ddl-auto`.
- **JOOQ `DDLDatabase`** (ditolak): parse file `.sql` migrasi tanpa DB. Tanpa Docker, tercepat, tapi parser DDL JOOQ tak sepenuhnya paham dialek MariaDB (`ON UPDATE CURRENT_TIMESTAMP`, `columnDefinition` khusus) — bisa gagal/parsial.
- **Testcontainers MariaDB** (dipilih): DB asli, deterministik, akurat. Harga: Docker wajib ada di mesin build/CI.

## Consequences

- Docker menjadi prasyarat build (bukan cuma test). CI harus menyediakan daemon Docker. Proyek sudah memakai Docker (`docker/`), jadi biayanya kecil.
- Build sedikit lebih lambat karena spin-up kontainer; bisa diringankan dengan reuse kontainer saat dev lokal.
- Flyway dipakai di dua tempat: produksi dan langkah codegen. Migrasi harus selalu hijau di MariaDB — bug seperti `V1_0_0__create_master.sql` (kolom `golongan_id` tak dideklarasi) akan menggagalkan build, bukan diam-diam lolos. Ini fitur, bukan bug.

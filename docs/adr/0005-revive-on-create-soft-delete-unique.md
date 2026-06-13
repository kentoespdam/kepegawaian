# Revive-on-create untuk menegakkan UNIQUE di bawah soft-delete

Modul master memakai soft-delete (`is_deleted`), tapi keunikan domain (mis. Profesi = `nama` + `jabatan_id` + `grade_id`) ingin ditegakkan di level DB lewat constraint `UNIQUE` biasa. Keduanya bertabrakan: row "bangkai" (soft-deleted) tetap menempati slot unik, sehingga membuat ulang nilai yang sama akan ditolak DB.

Keputusan: **jalur create yang tuple-nya bentrok dengan bangkai akan menghidupkan kembali (revive) bangkai itu** (`is_deleted = false` + timpa field dari request), bukan INSERT baru. Dengan begitu tak pernah ada dua row dengan tuple identik, dan `UNIQUE(nama, jabatan_id, grade_id)` polos cukup — tanpa kolom generated, tanpa partial index.

Untuk menemukan bangkai, command service memakai **native query** di repository (mis. `findAnyByNamaAndJabatanIdAndGradeId`, `nativeQuery = true`). `@SQLRestriction("is_deleted = FALSE")` di entity menyaring bangkai dari **semua** query Hibernate biasa, jadi lookup normal buta terhadapnya; SQL native tidak tunduk pada `@SQLRestriction` sehingga bisa melihat bangkai.

## Considered Options

### Penegakan keunikan di bawah soft-delete

- **Kolom generated sebagai diskriminator** (ditolak): kolom ter-generate yang menjadikan tuple unik hanya untuk row aktif. Ditolak karena merepotkan saat backup & restore manual lewat MariaDB ketika terjadi error — kolom generated menambah beban operasional pemulihan.
- **Partial / filtered unique index** (tidak tersedia): MariaDB tidak mendukung `UNIQUE ... WHERE is_deleted = false` seperti Postgres.
- **Revive-on-create + UNIQUE polos** (dipilih): tak ada dua tuple identik karena bangkai dihidupkan kembali alih-alih digandakan. Constraint sederhana, backup/restore tetap lugas. Harga: jalur create menanggung logika lookup + revive.

### Cara menemukan bangkai (yang disembunyikan `@SQLRestriction`)

- **Native query** (dipilih): satu method sempit "cari termasuk terhapus". Tidak mengusik `@SQLRestriction` yang melindungi semua jalur tulis lain; tidak melanggar pemisahan JPA/JOOQ (ADR-0001).
- **Ganti `@SQLRestriction` → `@Filter`** (ditolak): bisa di-disable per-session, tapi butuh diaktifkan eksplisit tiap session — boilerplate global untuk satu kasus, padahal baca sudah pindah ke JOOQ.
- **Query bangkai lewat JOOQ** (ditolak): JOOQ memintas Hibernate sehingga melihat bangkai, tapi mencampur JOOQ ke command service dan menodai batas sisi-tulis-murni-JPA dari ADR-0001.

## Consequences

- Endpoint create master bersifat idempoten terhadap bangkai: membuat ulang nilai yang pernah dihapus = menghidupkan record lama (id-nya sama seperti sebelum dihapus), bukan record baru. Konsumen yang mengandalkan id baru harus paham ini.
- Setiap entity master yang punya `UNIQUE` butuh satu native lookup "termasuk terhapus" di repository-nya. Pola berulang, dijaga seragam.
- Audit: revive adalah `update` (kolom `updated_by/at` ter-set), bukan `create` baru. Jejak "pernah dihapus lalu dihidupkan" terlihat dari perubahan `is_deleted` + timestamp, cukup untuk master (Envers tidak dipakai di master — ADR-0003).
- Endpoint restore-by-id terpisah (mis. dari layar arsip) bersifat opsional/pelengkap; perilaku inti yang menjaga UNIQUE adalah revive-on-create.
- Migration `V1_0_0__create_master.sql` harus menambahkan kolom `organisasi_id`/`jabatan_id`/`grade_id` (saat ini tak dideklarasikan), `version`, lalu `UNIQUE(nama, jabatan_id, grade_id)` — sebelum constraint bisa aktif.

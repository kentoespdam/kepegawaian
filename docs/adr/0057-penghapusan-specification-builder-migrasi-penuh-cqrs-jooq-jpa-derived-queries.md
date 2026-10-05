# 0057 — Penghapusan SpecificationBuilder dan migrasi penuh CQRS jOOQ serta JPA derived queries

Status: Accepted
Date: 2026-10-05

## Context

Dalam evolusi arsitektur CQRS-lite kepegawaian (Spring Boot 4 / Java 25), sebagian besar modul telah memisahkan jalur tulis (JPA/Command) dan jalur baca (jOOQ/Query). Namun, sisa penggunaan `JpaSpecificationExecutor` dan helper `SpecificationBuilder` di beberapa repositori (termasuk PrefRole dan UserService) serta validasi command-side masih menyisakan ketergantungan pada JPA Criteria API. Ketergantungan ini menciptakan duplikasi pola antara dynamic Criteria/Specification dan jOOQ `where`-clause, serta menghambat keseragaman arsitektur.

## Decision

1. **Migrasi penuh jalur baca ke jOOQ**: Menyelesaikan migrasi sisa query jOOQ untuk `PrefRole` dan `UserService`.
2. **Refactor validasi Command-side**: Mengubah seluruh validasi unik/eksistensi pada sisi command (Master, Profil, Riwayat, Cuti, Penggajian) dari `Specification` ke JPA derived queries (mis. `existsBy...`).
3. **Pencabutan `JpaSpecificationExecutor` & `SpecificationBuilder`**: Menghapus `JpaSpecificationExecutor` dari 52 repository serta menghapus total file `SpecificationBuilder.java` beserta helper terkait di seluruh modul.

## Consequences

- **Arsitektur Konsisten**: Jalur baca sepenuhnya menggunakan jOOQ (`baseWhere` + `SortParam`), sementara jalur tulis menggunakan JPA murni dengan derived queries eksplisit (tanpa Specification API).
- **Reduksi Kompleksitas**: Penghapusan `SpecificationBuilder` dan `JpaSpecificationExecutor` menghilangkan boilerplate Criteria API dan menyederhanakan pemeliharaan repositori.

# Akses data JOOQ pindah ke lapisan repository

> **Status:** accepted — mengubah sebagian ADR-0006 (lihat "Hubungan dengan ADR-0006").

Semua logika akses data baca (JOOQ `DSLContext`) yang tadinya hidup di `@Service` (`services/master/<domain>/<Domain>Queries.java`) dipindah ke lapisan repository sebagai kelas `@Repository`: `repositories/master/jooq/<Domain>QueryRepository.java`. Lapisan `repositories/master/` dipecah jadi sub-folder `jpa/` dan `jooq/` per domain. `QueryService` tetap di `services/` sebagai orkestrasi tipis yang menyuntik `<Domain>QueryRepository` dan melempar `NotFoundException`. Mapper statik (`<Domain>Mapper`) dipindah keluar dari folder service ke paket baru `mapper/master/<domain>/`.

Alasan: di proyek ini "semua masuk di service" — akses data JPA *dan* JOOQ bercampur dengan orkestrasi dalam satu lapisan, sehingga batas persistence kabur. Memindahkan JOOQ ke `repositories/` menyatukan **semua** akses data (tulis JPA + baca JOOQ) dalam satu lapisan, sejajar dengan pola repo referensi `mail-migration` (`repository/core/jpa/` vs `repository/core/jooq/`). Service kembali murni sebagai orkestrasi.

## Considered Options

- **Biarkan `*Queries` di `services/`** (status quo ADR-0006, ditolak): akses data baca tetap tercampur di lapisan service; diff mental dari kode lama lebih kecil, tapi lapisan repository tidak pernah jadi satu-satunya tempat persistence.
- **Pindah + rename + `@Repository`** (dipilih): `*Queries` → `*QueryRepository` di `repositories/master/jooq/`. Persistence terkumpul di satu lapisan; service murni orkestrasi.

## Hubungan dengan ADR-0006

ADR-0006 (layer-first, bukan vertical-slice) **tetap berlaku** — pohon top-level `controllers/ services/ repositories/ dto/` dipertahankan; rewrite tidak beralih ke feature-first. Yang berubah dari 0006:

- Contoh 0006 menaruh `*Queries.java` di `services/master/<domain>/` — sekarang pindah ke `repositories/master/jooq/`.
- `repositories/master/` yang tadinya datar kini punya sub-folder `jpa/` + `jooq/`.
- ADR ini menambah lapisan baru `mapper/` yang tidak disebut 0006 (menyimpang dari repo referensi yang tak punya paket mapper — keputusan sadar agar mapper tidak menumpang di folder service).

## Consequences

- **Cakupan bertahap, bukan sekaligus.** Berlawanan dengan kalimat 0006 "berlaku untuk semua modul rewrite": restrukturisasi ini diterapkan **hanya pada domain `master` dulu** (sesuai branch `rewrite/master-cqrs`). Domain lain (cuti, kepegawaian, penggajian, profil) menyusul saat giliran CQRS-nya.
- **Kriteria pindah = punya `CommandService`.** 15 repo master pindah ke `repositories/master/jpa/`: 13 domain CQRS dengan pasangan JOOQ, plus `apd` dan `alatKerja` yang hanya punya sisi tulis (read-side-nya terlipat ke agregat Profesi per ADR-0011). Dua repo gaya lama `ServiceImpl` tanpa JOOQ — **JenjangPendidikan** dan **Level** — tetap longgar di `repositories/master/` sampai di-CQRS-kan.
- **Profesi ikut pindah penuh.** Profesi punya pasangan JOOQ, jadi repo-nya pindah ke `jpa/` dan kelas bacanya ke `jooq/`. Selain `ProfesiQueries` → `ProfesiQueryRepository`, file JOOQ Profesi lain (`ProfesiDetailQuery` kelas `@Service` kedua, plus helper package-private `ProfesiRowMapper` dan `ProfesiSelects`) ikut ke `repositories/master/jooq/` agar helper tetap satu paket. Hanya `ProfesiMapper` (mapper JPA) yang ke `mapper/`.
- File importer repo JPA yang pindah perlu pembaruan import; gunakan `gitnexus_rename`, bukan find/replace.
- Selama transisi, sebagian domain master memakai struktur baru (jpa/jooq + mapper) sementara domain lain belum — campuran sementara yang diterima.

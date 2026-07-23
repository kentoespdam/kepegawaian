# Squash migration jadi baseline bersih, di-derive dari dump DB `kepegawaian` existing

Rantai migration `V1_0_0..V5_1_0` yang tambal-sulam di-*squash* jadi **satu baseline bersih**. Baseline tidak ditulis ulang from-scratch, melainkan **di-derive dari dump schema DB `kepegawaian` existing** (sebagai *draft*), lalu diedit agar selaras dengan **entity JPA** (sebagai *oracle* kebenaran). Migration patch `V5_0_8`, `V5_0_9`, dan bagian _AUD-master di `V5_1_0` dibuang karena sudah benar sejak baseline.

Ini konsekuensi turunan dari [ADR 0002](0002-flyway-schema-source-of-truth.md) (Flyway = schema source of truth) dan menegakkan [ADR 0003](0003-envers-scope-penggajian-kepegawaian.md) (Envers hanya `penggajian`+`kepegawaian`).

## Konteks

Base migration lama di-*dump* dari schema aplikasi LAMA yang di-generate Hibernate tanpa naming strategy snake_case. Akibatnya lahir kolom camelCase (`jmlTanggungan` dari `@Column` eksplisit; `nomorKartu` dari default `PhysicalNamingStrategyStandardImpl` karena config `naming:` lama kosong), lalu ditambal migration rename `V5_0_8`/`V5_0_9`. `V5_1_0` menambah 13 tabel `_AUD` master secara manual — padahal entity master **nol** `@Audited`, sehingga tabel itu yatim dan melanggar ADR-0003. Hasilnya: drift terhadap entity dan output `jooqCodegen` yang salah.

## Considered Options

- **Generate schema from-scratch via Hibernate `create`** (ditolak): memaksa re-inject 23 seed migration `V3_*`, view `v_pegawai`, dan 3 kolom ENUM native yang semuanya sudah ada di DB existing — kerja lebih banyak untuk hasil setara. Wabah camelCase ternyata hanya 2 kolom, tidak sistemik.
- **Pertahankan rantai tambal-sulam, tambah migration patch baru** (ditolak): drift menumpuk, riwayat menyesatkan, `jooqCodegen` tetap rawan salah tipe/nama.
- **Squash dari dump DB existing, entity sebagai oracle** (dipilih): draft 90% benar (seed+view+ENUM sudah ada), tinggal 3 koreksi terarah (drop _AUD master, snake_case 2 kolom, tegakkan ENUM native), diverifikasi dual-gate.

## Consequences

- **Destruktif — riwayat migration lama dibuang.** Aman karena `kepegawaian_dev_new` masih dev (belum prod); wajib konfirmasi tidak ada environment lain yang sudah apply migration lama sebelum eksekusi.
- **Gate ganda wajib HIJAU** sebelum baseline dianggap selesai: (1) Hibernate `ddl-auto=validate` boot sukses (write-side, memburu drift kolom/tabel otomatis) DAN (2) `jooqCodegen` sukses tanpa drift (read-side, tipe & nama kolom benar). Lihat [ADR 0004](0004-jooq-codegen-testcontainers.md).
- **Struktur baseline target:** V1 DDL bersih (schema-only, snake_case, ENUM native, tanpa _AUD master) → seed `V3_*` → view `v_pegawai`. Seed & view tetap migration terpisah, tidak dilebur ke DDL dump.
- **3 kolom ENUM native** (`biodata.golongan_darah`, `gaji_batch_master_proses.jenis_gaji`, `gaji_komponen.jenis_gaji`) tak muncul otomatis dari dump/export karena field-nya `@Enumerated(EnumType.STRING)` — harus ditegakkan manual, dengan urutan nilai konsisten enum Java (`EGolonganDarah`, `EJenisGaji`).
- Eksekusi dilacak di bd epic `kepegawaian-odb` (child `kepegawaian-odb.1..8`), urutan di [CLAIM-ORDER-baseline-rebuild.md](../CLAIM-ORDER-baseline-rebuild.md).

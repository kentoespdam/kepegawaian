# Guard ArchUnit: `@Valid` wajib pada parameter controller ber-tipe turunan `PagedRequest`

> **Status:** accepted — test guard di `#foundation`, menegakkan pola `index(@Valid @ParameterObject FooIndexQuery)`.

Pada rewrite sebelumnya, validasi paginasi (`@Min`/`@Max`/`@Pattern` di DTO) **diam-diam mati** karena `@Valid` terlewat di parameter `@ParameterObject` controller (mis. `BiodataController.index` — anotasi Bean Validation ada di DTO tapi tak pernah dieksekusi). Regresi ini lolos tanpa gejala: kompilasi sukses, endpoint jalan, hanya validasi yang absen. Diputuskan menambahkan test **ArchUnit** (`archunit-junit5`, test-scope) yang gagal-build bila ada parameter controller ber-tipe turunan `PagedRequest` tanpa `@Valid`.

## Considered Options

- **Disiplin manual + code review** (ditolak): persis mekanisme yang sudah gagal sekali — tak ada sinyal saat terlewat.
- **Reflection test polos tanpa dependency baru** (ditolak): scan classpath + refleksi parameter manual (~50-80 baris plumbing), menulis ulang yang sudah dilakukan ArchUnit dengan bug-surface lebih besar.
- **ArchUnit** (dipilih): aturan ini *adalah* aturan arsitektur ("controller X wajib anotasi Y") — use-case inti ArchUnit; deklaratif ~15 baris, dependency test-scope (tak masuk artefak produksi), dan murah diperluas untuk aturan CQRS lain nanti.

## Consequences

- **Dependency test baru:** `testImplementation "com.tngtech.archunit:archunit-junit5"` di `build.gradle`. Test-scope only; `legacy-snapshot` tak terpengaruh.
- **Guard menangkap regresi eksisting**, termasuk `BiodataController.index` yang saat ini tanpa `@Valid` — build akan merah sampai diperbaiki, memaksa penuntasan bug lama sebagai bagian `#foundation`.
- **Pola kanonik ditetapkan:** `index(@Valid @ParameterObject FooIndexQuery query)` **tanpa** parameter `Errors`. `Errors` + `if (errors.hasErrors())` redundan karena `GlobalExceptionHandler` sudah meng-override `handleMethodArgumentNotValid` + menangani `ConstraintViolationException` secara global (lihat [ADR-0013](0013-symmetric-apiresponse-error-envelope.md)).
- **Definition-of-Done** tiap slice memuat butir "controller `index` ber-`@Valid` & guard ArchUnit hijau".
- **Relasi:** menutup celah regresi dari rewrite [ADR-0026](0026-cleanup-commonpagerequest-memicu-rewrite-cqrs.md) dan menjadi prasyarat eksekusi cap size di [ADR-0027](0027-default-page-size-20-cap-100.md).

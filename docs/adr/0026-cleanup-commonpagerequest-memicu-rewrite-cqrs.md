# Penghapusan `CommonPageRequest` memicu rewrite CQRS/JOOQ 4 modul, bukan swap-superclass

> **Status:** accepted — mengikat epik `rewrite-cqrs-drop-commonpagerequest` dan 13 slice-nya.

`CommonPageRequest` (base-class paginasi lama, default `size=10`, `getPageable()` untuk jalur JPA/Specification) akan dihapus dan digantikan `PagedRequest` (base-class rewrite, dipakai JOOQ query repository). Tersisa 13 DTO `extends CommonPageRequest` di 5 modul yang **belum** di-rewrite CQRS: cuti, kepegawaian, profil (pendidikan), system (roles), users. Diputuskan: modul-modul itu **di-rewrite penuh ke CQRS/JOOQ** (seperti master & penggajian), bukan sekadar menukar superclass `CommonPageRequest` → `PagedRequest` sambil mempertahankan jalur JPA/Specification.

## Considered Options

- **Swap-superclass saja** (ditolak): ubah `extends CommonPageRequest` → `extends PagedRequest`, sesuaikan `getSpecification()`/`getPageable()`, selesai. Murah & cepat, tapi meninggalkan 5 modul di pola lama (JPA read via Specification) sementara master & penggajian sudah CQRS/JOOQ — kodebase jadi dua-pola permanen. `PagedRequest` juga dirancang untuk kontrak JOOQ (`getSizeOrDefault()`/`offset()`/`getPageNumber()`), bukan `getPageable()`; mempertahankan `getPageable()` berarti menyeret bagasi lama ke base-class baru.
- **Rewrite penuh CQRS/JOOQ** (dipilih): jadikan penghapusan base-class sebagai pemicu untuk menuntaskan rewrite yang terlewat. Read pindah ke `*QueryRepository` JOOQ, write tetap JPA di `*CommandService`, satu pola di seluruh kodebase.

## Consequences

- **Scope membengkak dari 1 PR mekanis menjadi 13 slice** (dto `*IndexQuery` + repo JOOQ/JPA + mapper + service split + controller per DTO). Dikelola sebagai epik dengan isu per-slice + `#foundation` + `#final`; lihat MD claim-order.
- **`getPageable()` ikut dihapus** dari kontrak paginasi — tidak ada lagi jalur JPA-Pageable untuk list berpaginasi; semua lewat `SortParam.resolve(...)` + `offset()` JOOQ.
- **Relasi:** menuntaskan arah [ADR-0001](0001-jpa-write-jooq-read-cqrs.md) (JPA-write/JOOQ-read) dan [ADR-0017](0017-jooq-read-into-repository-layer.md) (read JOOQ di repository layer) ke 5 modul terakhir. Konsekuensi default-size dari `PagedRequest` dicatat di [ADR-0027](0027-default-page-size-20-cap-100.md); guard `@Valid` yang mencegah regresi validasi di [ADR-0028](0028-archunit-guard-valid-pada-pagedrequest.md).

# Default page size 10 → 20, cap keras 100 (breaking change kontrak API)

> **Status:** accepted — properti `PagedRequest`, berlaku untuk semua endpoint berpaginasi setelah rewrite.

`PagedRequest` menetapkan `size` default **20** (`DEFAULT_SIZE`) dengan batas atas **100** (`MAX_SIZE`, ditegakkan `@Min(1) @Max(100)`). Ini menggantikan default **10** milik `CommonPageRequest` yang tak bercap. Perubahan default adalah **breaking change** pada kontrak API: klien yang mengandalkan 10 baris/halaman tanpa mengirim `size` kini menerima 20.

## Considered Options

- **Pertahankan default 10** (ditolak): kompatibel, tapi mempermanenkan dua default berbeda antar-modul (master/penggajian sudah 20) dan menahan `PagedRequest` di angka yang tak pernah jadi keputusan sadar — hanya warisan.
- **Default 20, cap 100** (dipilih): satu default konsisten di seluruh kodebase; cap keras 100 mencegah klien meminta halaman raksasa (`size=100000`) yang membebani DB — perlindungan yang `CommonPageRequest` tak punya.

## Consequences

- **Breaking change disadari & didokumentasikan**, bukan senyap. Konsumen API yang mengandalkan default 10 harus mengirim `size=10` eksplisit. Wajib diumumkan di changelog rilis.
- **`size > 100` kini ditolak `400`** (via `@Max(100)` + `handleMethodArgumentNotValid`), bukan diam-diam diterima. Ini sendiri perubahan perilaku bagi klien yang sebelumnya menarik halaman besar.
- **Relasi:** perubahan ini menjadi mungkin karena rewrite [ADR-0026](0026-cleanup-commonpagerequest-memicu-rewrite-cqrs.md); penegakan `@Max` bergantung pada guard `@Valid` di [ADR-0028](0028-archunit-guard-valid-pada-pagedrequest.md) — tanpa `@Valid`, cap 100 tak pernah dieksekusi.

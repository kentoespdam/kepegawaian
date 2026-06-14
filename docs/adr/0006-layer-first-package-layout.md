# Tata letak paket: layer-first (bukan vertical slice)

> **Status:** accepted — pilihan inti (layer-first) tetap berlaku, tetapi sebagian spesifik (lokasi `*Queries`, folder repo datar, "semua modul sekaligus", tanpa paket mapper) diubah oleh **ADR-0017**.

Rewrite mempertahankan struktur paket **layer-first** dari proyek lama: pohon top-level `controllers/`, `services/`, `repositories/`, `dto/`, masing-masing punya subpohon per-domain (`master/...`, `profesi/`, dst). Tujuan rewrite menyebut "vertical slice", tetapi keputusan ini sengaja **menolak** feature-first demi keberlanjutan dengan kode lama.

Contoh untuk Profesi:

```
controllers/master/ProfesiController.java
services/master/profesi/ProfesiCommandService.java   (JPA write)
services/master/profesi/ProfesiQueryService.java     (JOOQ read)
services/master/profesi/ProfesiQueries.java          (DSL JOOQ: page query, sort whitelist, proyeksi MULTISET)
repositories/master/ProfesiRepository.java
dto/master/profesi/{ProfesiRequest, ProfesiOption, ProfesiListItem, ProfesiDetail, RefItem}
```

Pemisahan Command/Query tetap per-file (sesuai ADR-0001) — yang berubah hanya **tempat** file, bukan pemisahan teknologinya. JPA dan JOOQ tetap di file terpisah, hanya saja tersebar di pohon layer-first, bukan terkumpul dalam satu folder fitur.

## Considered Options

- **Feature-first / vertical slice** (ditolak): satu paket per aggregate (`master/profesi/` berisi controller + command + query + dto + entity). Membuat batas CQRS terlihat berdampingan dan cocok dengan tujuan rewrite, tetapi merupakan penyimpangan besar dari pohon lama — siapa pun yang memindahkan kode lama harus memetakan ulang bentuk paket.
- **Layer-first** (dipilih): pertahankan pohon lama. Familiar bagi yang mengenal kode lama, diff mental lebih kecil, porting file lebih lurus.

## Consequences

- Pasangan Command/Query satu aggregate tersebar di empat pohon top-level (`controllers/`, `services/`, `repositories/`, `dto/`) — struktur CQRS tidak langsung terlihat dari satu folder. Trade-off diterima demi kontinuitas dengan kode lama.
- Konvensi ini berlaku untuk **semua** modul rewrite, bukan hanya master. Sekali dipilih, seluruh project mengikuti.
- Komponen bersama (`ApiResponse`, `PageResult`, `Conditions.active`, `@RestControllerAdvice`, exception typed) tetap di paket commons/shared, bukan di paket fitur.

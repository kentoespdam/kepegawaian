# Decisions — Modul Master (CQRS Cleanup)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan issue cleanup CQRS modul `master/` (epic `kepegawaian-be8`).

---

## §1 — Scope rewrite modul master

**Keputusan**: 17 aggregate DB-backed memakai **JPA write + JOOQ read** (ADR-0001). 5 aggregate enum-backed (JenisKontrak, JenisMutasi, JenisSk, StatusKerja, StatusPegawai) **dikecualikan** dari CQRS — mereka membaca dari Java enum, bukan tabel DB.

---

## §2 — Enum-backed aggregate: no interface, rename ke *QueryService

**Keputusan**: `JenisKontrakService`/`JenisKontrakServiceImpl` dan 4 aggregate serupa → hapus interface, rename Impl ke `JenisKontrakQueryService`. Ikuti ADR-0007 (no interface single-impl). Controller inject kelas konkret langsung.

_Avoid_: memigrasikan enum-backed ke tabel DB hanya demi "konsistensi CQRS" — tidak ada justifikasi bisnis.

---

## §3 — Write mapper: `final` class terpisah, bukan method di DTO

**Keputusan**: Semua `static toEntity`/`updateEntity` milik DTO request **dipindah** ke `mapper/master/<agg>/<Agg>Mapper.java` (final class, private ctor). DTO request tidak boleh mengandung mapping logic. CommandService menerima dependency yang sudah di-resolve (`getReferenceById`), lalu lempar ke mapper.

_Avoid_: `@Component` mapper, mapper yang inject repository, toEntity di dalam DTO.

**Dead code yang harus dibersihkan**: Banyak DTO di modul master masih punya `static toEntity` walau CommandService sudah pakai Mapper — ini dead code, harus dihapus (issue `kepegawaian-drm`).

---

## §4 — JOOQ mapping: `fetchInto` flat, `*JooqMapper` join-nested

**Keputusan** (ADR-0025): Hybrid berdasarkan kompleksitas projeksi:
- **Flat** (tanpa join ke nested object) → `fetchInto(XxxQuery.class)`. Zero boilerplate.
- **Join → nested object** dalam DTO → `fetch(XxxJooqMapper::mapToQuery)`. Mapper di `mapper/master/<agg>/`.

Aggregate yang saat ini punya nested object: **Grade** (`level: LevelResponse`), **Sanksi** (`jenisSp: JenisSpMiniResponse`).

_Avoid_: embed `private toQuery(Map<>)` di dalam `@Repository` — pisahkan ke `*JooqMapper`.

---

## §5 — CommandService return: entity JPA (bukan SavedStatus)

**Keputusan**: `CommandService` di modul master return **entity JPA** (`Level`, `Grade`, dst). Controller yang construct `SavedStatus.build(ESaveStatus.SUCCESS, entity.getId())`. Ini berbeda dari pola `profil/cuti` yang return `SavedStatus<?>` dari service.

Alasan: pattern master lebih bersih untuk chaining (service lain bisa consume entity); dan exception propagasi ke `@RestControllerAdvice` tidak tertelan oleh try-catch di service (konflik ADR-0013 bila mengikuti pola profil/cuti).

_Avoid_: try-catch generic di CommandService yang menelan exception — biarkan propagasi ke ControllerAdvice.

---

## §6 — FK attach: `getReferenceById` di CommandService, bukan di mapper

**Keputusan** (ADR-0008): FK relasi (mis. `Grade→Level`) di-resolve via `levelRepository.getReferenceById(id)` di CommandService, lalu hasilnya diteruskan ke mapper sebagai parameter. Mapper adalah pure static utility tanpa akses ke repository.

_Avoid_: `new Level(id)` (detached entity), `findById` untuk FK yang hanya perlu set nilai (round-trip SELECT sia-sia).

---

## §7 — Revive-on-create: lewat Specification (tanpa @SQLRestriction di entity master)

**Keputusan**: Entity master flat (Level, Golongan, Grade, dll) **tidak** punya `@SQLRestriction` — hanya punya `@SQLDelete`. Akibatnya, `repository.findOne(request.getSpecification())` mengembalikan row deleted maupun non-deleted. CommandService cek `getIsDeleted()` untuk memilih antara revive (deleted) atau throw conflict (non-deleted).

JOOQ QueryRepository selalu filter `IS_DELETED.eq(false)` secara eksplisit di `baseWhere` atau `where` clause.

Aggregate dengan logika revive kompleks (Profesi: cek kombinasi nama+jabatan+grade) memakai native query terpisah — lihat ADR-0005.

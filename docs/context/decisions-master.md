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

## §4 — JOOQ mapping: `fetchInto` flat, `*JooqMapper` join-nested, `multiset` one-to-many

**Keputusan** (ADR-0025): Hybrid berdasarkan kompleksitas projeksi:
- **Flat** (tanpa join ke nested object) → `fetchInto(XxxQuery.class)`. Zero boilerplate.
- **Join → nested object** dalam DTO → `fetch(XxxJooqMapper::mapToQuery)`. Mapper di `mapper/master/<agg>/`.
- **`multiset` → nested list** (one-to-many, mis. Profesi→APD/AlatKerja, JenisSp→Sanksi) → `select(multiset(...).convertFrom(...))` + `*JooqMapper`. Gunakan `Records.mapping(RowRecord::new)` sebagai converter.

Aggregate yang saat ini punya nested object / nested list:
- **Grade** (`level: LevelResponse`) → join → `GradeJooqMapper`
- **Sanksi** (`jenisSp: JenisSpMiniResponse`) → join → `SanksiJooqMapper`
- **Profesi** (`apdList: List<ApdRow>`, `alatKerjaList: List<AlatKerjaRow>`) → multiset → `ProfesiJooqMapper`
- **JenisSp** (`sanksiList: List<SanksiRow>`) → multiset → `JenisSpJooqMapper`

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

---

## §8 — Delete parent: guard owned-child, JANGAN cascade (issue `kepegawaian-15u`)

**Keputusan**: Child **tidak** ikut terhapus saat parent dihapus. Sebaliknya, delete parent **ditolak** (`ConflictException` → HTTP 409) selama masih ada **owned-child** aktif (`is_deleted=false`).

**Definisi "owned-child" (yang memblokir)**: hanya relasi yang parent-nya benar-benar *memiliki* child — hierarki self-ref (`parent`) atau child yang eksistensinya bergantung pada parent. **BUKAN** lookup-referrer, yaitu entity lain yang sekadar punya FK ke parent sebagai referensi (mis. `Profesi.organisasi`, `Pegawai.organisasi`, data transaksional/riwayat, `Grade.profesiList`, `Jabatan.profesiList`). Lookup-referrer TIDAK memblokir delete — kalau ikut memblokir, master yang banyak dipakai (Organisasi) praktis tak akan pernah bisa dihapus.

**Parent yang kena guard** (hasil audit seluruh 17 master entity):

| Parent | Owned-child pemblokir | Repo cek | Method (Spring Data derived) |
|--------|----------------------|----------|------------------------------|
| Organisasi | sub-Organisasi (self-ref `parent`) | OrganisasiRepository | `existsByParentIdAndIsDeletedFalse` |
| Jabatan | sub-Jabatan (self-ref `parent`) | JabatanRepository | `existsByParentIdAndIsDeletedFalse` |
| Profesi | `apd`, `alatKerja` | ApdRepository, AlatKerjaRepository | `existsByProfesiIdAndIsDeletedFalse` |
| JenisSp | `sanksi` (Sanksi ada berdasarkan JenisSp) | SanksiRepository | `existsByJenisSpIdAndIsDeletedFalse` |

**13 master lainnya tanpa guard** (delete = soft-delete langsung, seperti sekarang): AlasanBerhenti, Golongan, Grade, HariLibur, JenisKeahlian, JenisKitas, JenisPelatihan, JenjangPendidikan, Level, RumahDinas, Sanksi, Apd, AlatKerja. Alasan: tak punya owned-child; referrer ke mereka (mis. Profesi→Grade/Jabatan, Sanksi→JenisSp sebagai kategori terbalik) adalah lookup, bukan kepemilikan.

**Kondisi saat ini (temuan)**: semua `*CommandService.delete` master hanya `findById → setIsDeleted(true) → save`. `@OneToMany` di parent **tanpa** `cascade`/`orphanRemoval`, dan path delete pakai `save` (bukan `repository.delete`, jadi `@SQLDelete` child tak terpicu). Akibatnya owned-child tetap aktif menggantung ke parent non-aktif = **orphan logis**.

**Spec guard (mekanis)**: sebelum `setIsDeleted(true)`, cek `existsBy...AndIsDeletedFalse` per owned-child secara berurutan (short-circuit); jika `true` → `throw new ConflictException("<Parent> masih memiliki <Child>")`. Pakai `existsBy` (SELECT 1/LIMIT 1), **bukan** `countBy` (tak perlu angka), **bukan** JOOQ (jangan tarik DSLContext ke command path). Untuk Profesi, cek `apd` lalu `alatKerja`.

_Avoid_: `cascade = CascadeType.ALL` / `orphanRemoval = true` pada relasi master; loop menghapus child otomatis; memblokir delete karena lookup-referrer.

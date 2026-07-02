# Profil CQRS — Pola Implementasi per Layer

> Referensi pola konkret untuk mengeksekusi claim-order [`profil-cqrs-cleanup-claim-order.md`](profil-cqrs-cleanup-claim-order.md).
> Semua cuplikan = **kode nyata** dari modul `cuti/` (exemplar selesai) & `mapper/profil/` (Wave 1 sudah dieksekusi). Bukan pseudocode — salin bentuknya, ganti nama aggregate.
> Keputusan desain: [`docs/context/decisions-pegawai.md`](context/decisions-pegawai.md) §35/§43/§51/§53/§55, [`decisions-cuti.md`](context/decisions-cuti.md) §11.
> Aturan lintas semua layer: **read = JOOQ, write = JPA** · mapper = `final` + private ctor, **BUKAN `@Component`** · file ≤ 120 baris (entity data-holder & pure-query repo dikecualikan) · soft-delete `is_deleted`.

---

## Peta layer (satu aggregate)

```
dto/profil/<agg>/         <Agg>PostRequest, <Agg>PutRequest      → tulis (JPA)
                          <Agg>Request                           → filter/paging baca
                          <Agg>Query / <Agg>Response             → hasil baca (JOOQ)
mapper/profil/<agg>/      <Agg>Mapper (write: toEntity/updateEntity)
                          <Agg>JooqMapper (read: Record → Query/Response)
repositories/profil/jpa/  <Agg>Repository  (JpaRepository + Specification + Revision)
repositories/profil/jooq/ <Agg>QueryRepository (DSLContext, where + SortParam)
services/profil/<agg>/    <Agg>CommandService (@Transactional, tulis)
                          <Agg>QueryService   (delegasi ke QueryRepository)
controllers/profil/       <Agg>Controller (inject KEDUA service; TANPA *CommandController)
```

---

## 1. DTO

### 1a. Request tulis — `<Agg>PostRequest` / `<Agg>PutRequest`

`@Data` Lombok + validasi Bean Validation. **Boleh** memuat `getSpecification()` (JPA, dipakai command untuk cek duplikat) dan factory `toEntity` ringkas — TAPI bila mapping non-trivial, pindahkan ke `mapper/`. Contoh nyata `CutiJenisPostRequest`:

```java
@Data
public class CutiJenisPostRequest {
    private Long parentId;
    @NotNull(message = "Nama is required")
    @NotEmpty(message = "Nama is required")
    private String nama;
    private Integer maxHari = 0;
    private Boolean potongKuotaTahunan = false;

    @JsonIgnore
    public Specification<CutiJenis> getSpecification() {
        return (root, query, cb) ->
                cb.and(cb.equal(cb.lower(root.get("nama")), nama.toLowerCase()));
    }
}
```

> `@JsonIgnore` wajib pada `getSpecification()` agar tak bocor ke response JSON. `getSpecification()` HANYA untuk jalur tulis (cek duplikat di CommandService). Jalur baca TIDAK pakai Specification — pakai JOOQ `where`.

### 1b. Request baca — `<Agg>Request`

Field paging/sort/filter: `page`, `size`, `sortBy`, `sortDirection`, plus filter domain (`nama`, `parentId`). Dikonsumsi `QueryRepository`. Tidak ada Specification di sini.

> **Base class — modul baru WAJIB `<Agg>IndexQuery extends PagedRequest`** (base baru: `@Max(100)` clamp, `getPageNumber()`/`getSizeOrDefault()`, sort-whitelist type-safe; `@EqualsAndHashCode(callSuper = true) @Data`). Exemplar `GradeIndexQuery`/`GradeQueryRepository`. Cuplikan `CutiJenisRequest`/`CutiJenisQueryRepository` di §3b memakai `CommonPageRequest` (base lama, tanpa clamp/whitelist) — akurat secara historis untuk `cuti/`, TAPI **jangan disalin untuk aggregate baru** (mis. seluruh `penggajian/`). Pakai `getSizeOrDefault()`/`getPageNumber()`, bukan pola `getSize() != null ? … : 10`.

### 1c. Response baca — `<Agg>Response` / `<Agg>Query`

`@Data` POJO datar. Nested pakai `*MiniResponse`. Contoh `CutiJenisResponse` (factory `from(entity)` boleh ada untuk jalur non-JOOQ, tapi jalur JOOQ diisi via JooqMapper):

```java
@Data
public class CutiJenisResponse {
    private Long id;
    private CutiJenisMiniResponse parent;
    private String nama;
    private Integer maxHari;
    private Boolean potongKuotaTahunan;
}
```

---

## 2. Mapper — `final`, private ctor, BUKAN `@Component`

Dua tanggung jawab terpisah dalam dua kelas: **write mapper** (`<Agg>Mapper`) dan **read mapper** (`<Agg>JooqMapper`).

### 2a. Write mapper — `<Agg>Mapper` (dipakai CommandService)

Static `toEntity` (create) + `updateEntity` (mutasi in-place entity terkelola). Contoh `CutiJenisMapper`:

```java
public final class CutiJenisMapper {
    private CutiJenisMapper() {}

    public static CutiJenis toEntity(CutiJenisPostRequest request, CutiJenis parent) {
        CutiJenis entity = new CutiJenis();
        entity.setParent(parent);
        entity.setNama(request.getNama());
        entity.setMaxHari(request.getMaxHari());
        entity.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
        return entity;
    }

    public static void updateEntity(CutiJenis entity, CutiJenisPutRequest request, CutiJenis parent) {
        entity.setParent(parent);
        entity.setNama(request.getNama());
        entity.setMaxHari(request.getMaxHari());
        entity.setPotongKuotaTahunan(request.getPotongKuotaTahunan());
    }
}
```

> `updateEntity` **void** (mutasi entity terkelola) atau kembalikan `entity` — keduanya dipakai di codebase; pilih void bila entity sudah managed dari `findById`.

### 2b. Read mapper Pola A (flat) — static `mapToResponse(Record)`

Untuk projeksi datar. Referensi kolom via JOOQ generated (`CUTI_JENIS.NAMA`) bila tabel utama; alias join via string key. Contoh `CutiJenisJooqMapper`:

```java
public final class CutiJenisJooqMapper {
    private CutiJenisJooqMapper() {}

    public static CutiJenisResponse mapToResponse(Record record) {
        if (record == null) return null;
        CutiJenisResponse response = new CutiJenisResponse();
        response.setId(record.get(CUTI_JENIS.ID));
        response.setNama(record.get(CUTI_JENIS.NAMA));
        response.setMaxHari(record.get(CUTI_JENIS.MAX_HARI));
        response.setPotongKuotaTahunan(record.get(CUTI_JENIS.POTONG_KUOTA_TAHUNAN));
        if (record.get("parent_id") != null) {
            CutiJenisMiniResponse mini = new CutiJenisMiniResponse();
            mini.setId((Long) record.get("parent_id"));
            mini.setNama((String) record.get("parent_nama"));
            response.setParent(mini);
        }
        return response;
    }
}
```

Dipakai di repo sebagai method-ref: `.fetch(CutiJenisJooqMapper::mapToResponse)`.

### 2c. Read mapper Pola B (`implements RecordMapper`) — dipakai profil

Untuk projeksi berat / dipakai berulang / multiset detail. Singleton `INSTANCE`, `implements RecordMapper<Record, T>`, akses kolom via string + tipe eksplisit. Contoh nyata `PendidikanJooqMapper` (profil, sudah relokasi Wave 1):

```java
public final class PendidikanJooqMapper implements RecordMapper<Record, PendidikanQuery> {
    public static final PendidikanJooqMapper INSTANCE = new PendidikanJooqMapper();

    private PendidikanJooqMapper() {}

    @Override
    public PendidikanQuery map(Record record) {
        PendidikanQuery q = new PendidikanQuery();
        q.setId(record.get("id", Long.class));
        q.setBiodataNik(record.get("biodata_nik", String.class));
        // nested master object hanya bila FK resolve
        Long jenjangId = record.get("jenjang_id", Long.class);
        if (jenjangId != null) {
            JenjangPendidikanResponse jp = new JenjangPendidikanResponse();
            jp.setId(jenjangId);
            jp.setNama(record.get("jenjang_nama", String.class));
            q.setJenjangPendidikan(jp);
        }
        Byte isLatest = record.get("is_latest", Byte.class);
        q.setIsLatest(isLatest != null && isLatest == 1);
        return q;
    }
}
```

**Multiset mapper** (child bersarang pada detail view, mis. `KartuIdentitasMultisetJooqMapper`) = varian sama, dipanggil dari `BiodataDetailQuery`. **LEGIT** — bukan pelanggaran ADR-0001 (mini-projection hanya mengikat paged-list root, bukan detail view). Lihat decisions-pegawai §51.

> Konvensi tipe MariaDB: boolean tersimpan `Byte` → baca `record.get(col, Byte.class)` lalu `!= null && == 1`. Tanggal → `LocalDate.class`.

---

## 3. Repository — split teknologi

### 3a. JPA (tulis) — `repositories/profil/jpa/<Agg>Repository`

Interface `JpaRepository` + `JpaSpecificationExecutor` + `RevisionRepository` (Envers). Native query hanya untuk kasus khusus (mis. cari record ter-soft-delete untuk revive). Contoh `CutiJenisRepository`:

```java
public interface CutiJenisRepository extends JpaRepository<CutiJenis, Long>,
        JpaSpecificationExecutor<CutiJenis>,
        RevisionRepository<CutiJenis, Long, Integer> {

    @Query(value = "SELECT * FROM cuti_jenis WHERE LOWER(nama) = LOWER(:nama) AND is_deleted = true LIMIT 1",
            nativeQuery = true)
    Optional<CutiJenis> findDeletedByName(@Param("nama") String nama);
}
```

> Repository interface **dikecualikan** dari batas 120 baris (pure declaration).

### 3b. JOOQ (baca) — `repositories/profil/jooq/<Agg>QueryRepository`

`@Repository @RequiredArgsConstructor`, inject `DSLContext`. Tiga method baku: `pageQuery` (paged+count), `listQuery`, `getById`. `where` di-share via `private baseWhere(...)`, sort via `SortParam.resolve(sortBy, sortDir, allowedSorts(), defaultColumn)`. **WAJIB filter `IS_DELETED.eq(false)`.** Contoh `CutiJenisQueryRepository`:

```java
@Repository
@RequiredArgsConstructor
public class CutiJenisQueryRepository {
    private final DSLContext dsl;

    public Page<CutiJenisResponse> pageQuery(CutiJenisRequest query) {
        var parent = CUTI_JENIS.as("parent");
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), CUTI_JENIS.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount().from(CUTI_JENIS).where(where).fetchOne(0, Long.class);
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        var data = dsl.select(CUTI_JENIS.ID, CUTI_JENIS.NAMA, /* ... */
                        parent.ID.as("parent_id"), parent.NAMA.as("parent_nama"))
                .from(CUTI_JENIS)
                .leftJoin(parent).on(CUTI_JENIS.PARENT_ID.eq(parent.ID))
                .where(where).orderBy(sortOrder)
                .limit(size).offset(page * size)
                .fetch(CutiJenisJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(page, size), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of("nama", CUTI_JENIS.NAMA, "maxHari", CUTI_JENIS.MAX_HARI);
    }

    private Condition baseWhere(CutiJenisRequest q) {
        return CUTI_JENIS.IS_DELETED.eq(false)
                .and(q.getParentId() != null ? CUTI_JENIS.PARENT_ID.eq(q.getParentId()) : DSL.noCondition())
                .and(q.getNama() != null ? CUTI_JENIS.NAMA.likeIgnoreCase("%" + q.getNama() + "%") : DSL.noCondition());
    }
}
```

> Kondisi opsional → `DSL.noCondition()` (bukan `null`). Filter kolom besar → pisahkan `SELECT` list ke `<Agg>Selects` bila repo mendekati 120 baris (pola profil: `BiodataSelects`, `PendidikanSelects`). Query repo **dikecualikan** batas 120 bila murni deklarasi query.

---

## 4. Service — pisah Command vs Query

### 4a. `<Agg>QueryService` — tipis, delegasi ke JOOQ repo

```java
@Service
@RequiredArgsConstructor
public class CutiJenisQueryService {
    private final CutiJenisQueryRepository queryRepository;

    public Page<CutiJenisResponse> findPage(CutiJenisRequest request) { return queryRepository.pageQuery(request); }
    public List<CutiJenisResponse> findList(CutiJenisRequest request) { return queryRepository.listQuery(request); }
    public CutiJenisResponse findById(Long id) { return queryRepository.getById(id); }
}
```

> **File-download milik QueryService**, bukan CommandService (decisions-pegawai §53). Method `getFileLampiranById`/`findFotoProfil` yang mendelegasi `lampiranProfilQueryService` ditaruh di sini. Controller download endpoint memanggil QueryService.

### 4b. `<Agg>CommandService` — `@Transactional`, tulis via JPA + write mapper

Pola `save`/`update`/`delete`. `getReferenceById` untuk FK murni (nol SELECT), `findById` bila entity perlu ter-hidrasi. Cek duplikat via `repository.exists(spec)`. Soft-delete lewat `repository.delete(...)` (di-intercept jadi `is_deleted=true`). Contoh `CutiJenisCommandService`:

```java
@Service
@RequiredArgsConstructor
public class CutiJenisCommandService {
    private final CutiJenisRepository repository;

    @Transactional
    public SavedStatus<?> save(CutiJenisPostRequest request) {
        try {
            if (repository.exists(request.getSpecification()))
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Cuti Jenis sudah ada");
            CutiJenis parent = request.getParentId() != null
                    ? repository.getReferenceById(request.getParentId()) : null;
            CutiJenis entity = CutiJenisMapper.toEntity(request, parent);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Saved!");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public SavedStatus<?> update(Long id, CutiJenisPutRequest request) {
        Optional<CutiJenis> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Data Not Found!");
        CutiJenis parent = request.getParentId() != null
                ? repository.getReferenceById(request.getParentId()) : null;
        CutiJenisMapper.updateEntity(byId.get(), request, parent);
        repository.save(byId.get());
        return SavedStatus.build(ESaveStatus.SUCCESS, "Data Updated!");
    }
}
```

> **Bila CommandService > 120 baris** (Pendidikan 164, Keluarga 151, Biodata 148): pisahkan cabang lampiran ke `<Agg>LampiranCommandService` (add/deleteLampiran), sisakan CRUD di `<Agg>CommandService`. Lihat claim-order Wave 2.
> **Interface single-impl dibuang** (decisions-cuti §11): `ProfileUpdateServiceImpl` → rename jadi `ProfileUpdateService` via `gitnexus_rename`, tanpa interface. Injektor tak berubah karena field bertipe nama yang sama.

---

## 5. Checklist penerapan (tiap aggregate)

- [ ] DTO: PostRequest/PutRequest (`@Data`, validasi, `@JsonIgnore` di `getSpecification`); request baca **`<Agg>IndexQuery extends PagedRequest`** untuk aggregate baru (BUKAN `CommonPageRequest`; exemplar `GradeIndexQuery`); Response/Query datar
- [ ] Write mapper `<Agg>Mapper` — `final`, private ctor, static `toEntity`/`updateEntity`
- [ ] Read mapper `<Agg>JooqMapper` — Pola A (`mapToResponse` static) ATAU Pola B (`implements RecordMapper` + `INSTANCE`); di `mapper/profil/<agg>/`, BUKAN di `repositories/`
- [ ] JPA repo di `jpa/` (Specification + Revision); JOOQ repo di `jooq/` (`baseWhere` + `SortParam` + `IS_DELETED=false`)
- [ ] QueryService tipis (delegasi + file-download); CommandService `@Transactional` (exists→duplicate, getReferenceById FK, write mapper)
- [ ] Controller inject KEDUA service; tanpa `*CommandController`/`*QueryController` terpisah
- [ ] Semua file ≤ 120 baris kecuali entity data-holder & pure-query repo
- [ ] **Cleanup dead code:** hapus field/method/DTO tak ter-referensi pasca-split (mis. `getSpecification()` di request baca, mapper manual tergantikan JOOQ); verifikasi zero-ref via `gitnexus_impact({direction: "upstream"})` SEBELUM hapus
- [ ] **Cleanup unused import:** buang import menggantung pasca pindah/hapus/rename; `./gradlew clean compileJava` bersih tanpa warning import
- [ ] `./gradlew compileJava` SUCCESSFUL; `gitnexus_detect_changes()` scope sesuai

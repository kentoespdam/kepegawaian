# Master Query Optimization Pattern

> Pedoman implementasi CQRS read side — Java record + typed JOOQ RecordMapper + lean column select.
>
> Berdasarkan refactor pada modul **Master Profesi** (commit `b732295`).

---

## Daftar Isi

1. [Prinsip](#1-prinsip)
2. [Lapisan Arsitektur](#2-lapisan-arsitektur)
3. [Pola per Endpoint](#3-pola-per-endpoint)
   - [3a. List / Dropdown (`GET /list`)](#3a-list--dropdown-get-list)
   - [3b. Index / Page (`GET /`)](#3b-index--page-get-)
   - [3c. Detail (`GET /{id}`)](#3c-detail-get-id)
4. [Aturan Penting](#4-aturan-penting)
   - [4a. Jangan gunakan `record.intoMap()`](#4a-jangan-gunakan-recordintomap)
   - [4a. Kolom yang tidak dipakai DTO jangan di-*select*](#4b-kolom-yang-tidak-dipakai-dto-jangan-di-select)
   - [4c. FK ID tidak perlu jika nested object sudah punya `id`](#4c-fk-id-tidak-perlu-jika-nested-object-sudah-punya-id)
   - [4d. Method reference, bukan lambda](#4d-method-reference-bukan-lambda)
5. [File-file yang Terlibat](#5-file-file-yang-terlibat)
   - [Template DTO Record](#template-dto-record)
   - [Template Selects](#template-selects)
   - [Template Mapper](#template-mapper)
   - [Template Repository](#template-repository)
   - [Template Service](#template-service)
   - [Template Controller](#template-controller)
6. [Cek List Implementasi](#6-cek-list-implementasi)

---

## 1. Prinsip

1. **DTO Read adalah Java `record`** — immutable, tanpa Lombok `@Data`, tanpa setter.
2. **Query hanya select kolom yang dipakai DTO** — tidak over-fetch. Setiap endpoint punya column set sendiri.
3. **Mapper pakai `record.get(Field<T>)`** — type-safe, tanpa `record.intoMap()` + raw cast.
4. **Method reference `Mapper::method`** — bukan lambda `record -> mapper.method(record.intoMap())`.
5. **Nested object helper di-share** — `buildOrganisasi()`, `buildJabatan()`, dll dipakai oleh semua mapper method.
6. **Nullable dari LEFT JOIN** — selalu guard dengan `record.get(Field) != null` sebelum build nested object.

---

## 2. Lapisan Arsitektur

```
Controller                              → DTO response (record)
  ↓
Service                                 → delegasi ke repository
  ↓
Repository (JOOQ)                       → SELECT + JOIN, mapping via method reference
  ↓
XxxSelects (column definitions)         → Field<?>[] arrays per endpoint
  ↓
XxxJooqMapper (typed RecordMapper)      → record.get(Field), builder helpers
```

**Alur data:**

```
HTTP Request → Controller → Service → Repository
                                           ↓
                                      JOOQ SELECT
                                           ↓
                                      XxxJooqMapper::method
                                           ↓
                                      Java record DTO
                                           ↓
                              JSON response ke client
```

---

## 3. Pola per Endpoint

### 3a. List / Dropdown (`GET /list`)

**Tujuan:** Dropdown filter untuk FE — cukup `id` + `nama`, tanpa JOIN.

**DTO:**
```java
public record XxxListResponse(
        Long id,
        String nama
) {}
```

**Repository:**
```java
public List<XxxListResponse> listQuery() {
    return dsl.select(TABLE.ID, TABLE.NAMA)
            .from(TABLE)
            .where(TABLE.IS_DELETED.eq(false))
            .orderBy(TABLE.NAMA.asc())
            .fetchInto(XxxListResponse.class);
}
```

> Gunakan `fetchInto(RecordClass.class)` — JOOQ DefaultRecordMapper cocokkan nama kolom ke parameter constructor record.

### 3b. Index / Page (`GET /`)

**Tujuan:** Tabel dengan filter + sorting + pagination, menampilkan nested object (organisasi, jabatan, dll).

**DTO:**
```java
public record XxxQuery(
        Long id,
        String nama,
        // scalar fields first
        ...,
        // nested objects after — tanpa FK ID duplikat
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        ...
) {}
```

**Selects:**
```java
public final class XxxSelects {
    // public static final Field<...> untuk akses dari mapper lintas package
    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");
    // ... semua aliased field yang dipakai DTO

    static final Field<?>[] XXX_QUERY_COLUMNS = new Field[] {
            TABLE.ID,
            TABLE.NAMA,
            // ... hanya kolom yang dipakai XxxQuery, tanpa FK duplikat
            ORG_ID,
            ORG_NAMA,
            ...
    };
}
```

**Mapper:**
```java
public static XxxQuery toQuery(Record record) {
    return new XxxQuery(
            record.get(TABLE.ID),
            record.get(TABLE.NAMA),
            ...,
            record.get(XxxSelects.ORG_ID) != null ? buildOrganisasi(record) : null,
            record.get(XxxSelects.JABATAN_ID) != null ? buildJabatan(record) : null,
            ...
    );
}
```

**Repository:**
```java
public Page<XxxQuery> pageQuery(XxxIndexQuery query) {
    // ... count query
    var data = dsl.select(XxxSelects.XXX_QUERY_COLUMNS)
            .from(TABLE)
            .leftJoin(ORGANISASI).on(...)
            .leftJoin(JABATAN).on(...)
            .where(where)
            .orderBy(sortOrder)
            .limit(...).offset(...)
            .fetch(XxxJooqMapper::toQuery);   // method reference!
    return new PageImpl<>(...);
}
```

### 3c. Detail (`GET /{id}`)

**Tujuan:** Detail satu baris termasuk relasi child (multiset).

**DTO:**
```java
public record XxxDetail(
        Long id,
        String nama,
        ...,
        OrganisasiMiniResponse organisasi,
        ...,
        List<AnakRow> anakList,         // multiset
        List<LainRow> lainList
) {}
```

**Repository:**
```java
public Optional<XxxDetail> getById(Long id) {
    return dsl.select(XxxSelects.XXX_DETAIL_COLUMNS)
            .select(
                    multiset(dsl.select(ANAK.ID, ANAK.NAMA)
                            .from(ANAK)
                            .where(ANAK.REF_ID.eq(id))
                            .and(ANAK.IS_DELETED.eq(false)))
                            .as("anak_list")
                            .convertFrom(r -> r.map(mapping(AnakRow::new)))
            )
            .from(TABLE)
            .leftJoin(...)
            .where(TABLE.ID.eq(id))
            .and(TABLE.IS_DELETED.eq(false))
            .fetchOptional(XxxJooqMapper::toDetail);
}
```

---

## 4. Aturan Penting

### 4a. Jangan gunakan `record.intoMap()`

❌ **Salah:**
```java
.fetchOptional(record -> mapper.toDetail(record.intoMap()));
// Map<String, Object> → raw casts → runtime error
```

✅ **Benar:**
```java
.fetchOptional(XxxJooqMapper::toDetail);
// Record → record.get(Field<T>) → compile-time type safety
```

### 4b. Kolom yang tidak dipakai DTO jangan di-*select*

Setiap endpoint punya column set sendiri:

| Column Set | Dipakai Oleh | Kolom |
|---|---|---|
| `PROFESI_COLUMNS` | (legacy, semua kolom) | 20 |
| `PROFESI_QUERY_COLUMNS` | `toQuery()` → `ProfesiQuery` | 16 |
| `PROFESI_DETAIL_COLUMNS` | `toDetail()` → `ProfesiDetail` | 16 |

Kolom yang dihapus: `organisasi_id`, `self_jabatan_id`, `self_level_id`, `self_grade_id` — karena:
- `organisasi_id`: sudah ada `ORG_ID` dari join
- `self_*_id`: sudah ada `jabatan.id()`, `level.id()`, `grade.id()` dari nested object

### 4c. FK ID tidak perlu jika nested object sudah punya `id`

❌ **DTO dengan FK duplikat:**
```java
public record XxxQuery(
    Long id,
    Long organisasiId,      // ❌ duplikat — sudah ada di organisasi.id()
    OrganisasiMiniResponse organisasi,
    ...
) {}
```

✅ **DTO tanpa FK duplikat:**
```java
public record XxxQuery(
    Long id,
    String nama,
    OrganisasiMiniResponse organisasi,   // ✅ client pakai organisasi.id()
    ...
) {}
```

### 4d. Method reference, bukan lambda

❌ **Salah:**
```java
.fetch(record -> XxxJooqMapper.toQuery(record.intoMap()))
```

✅ **Benar:**
```java
.fetch(XxxJooqMapper::toQuery)
```

---

## 5. File-file yang Terlibat

### Template DTO Record

```java
// dto/.../XxxQuery.java
public record XxxQuery(
        Long id,
        String nama,
        // ... field tanpa FK duplikat
        OrganisasiMiniResponse organisasi,
        JabatanMiniResponse jabatan,
        // ...
) {}
```

### Template Selects

```java
// repositories/.../XxxSelects.java
public final class XxxSelects {
    private XxxSelects() {}

    // Public — untuk akses dari mapper lintas package
    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");
    // ...

    // Per column set per endpoint
    static final Field<?>[] XXX_QUERY_COLUMNS = new Field[] {
            TABLE.ID, TABLE.NAMA, ...,
            ORG_ID, ORG_NAMA, ...
    };

    static final Field<?>[] XXX_DETAIL_COLUMNS = new Field[] {
            TABLE.ID, TABLE.NAMA, ...,
            ORG_ID, ORG_NAMA, ...
    };
}
```

### Template Mapper

```java
// mapper/.../XxxJooqMapper.java
public final class XxxJooqMapper {
    private XxxJooqMapper() {}

    public static XxxQuery toQuery(Record record) {
        return new XxxQuery(
                record.get(TABLE.ID),
                record.get(TABLE.NAMA),
                ...,
                record.get(XxxSelects.ORG_ID) != null ? buildOrganisasi(record) : null,
                record.get(XxxSelects.JABATAN_ID) != null ? buildJabatan(record) : null,
                ...
        );
    }

    @SuppressWarnings("unchecked")  // hanya untuk multiset
    public static XxxDetail toDetail(Record record) {
        return new XxxDetail(
                record.get(TABLE.ID),
                record.get(TABLE.NAMA),
                ...,
                record.get(XxxSelects.ORG_ID) != null ? buildOrganisasi(record) : null,
                ...,
                (List<AnakRow>) record.get("anak_list")   // unchecked cast
        );
    }

    // Builder helpers — di-share antara toQuery dan toDetail
    private static OrganisasiMiniResponse buildOrganisasi(Record record) {
        var o = new OrganisasiMiniResponse();
        o.setId(record.get(XxxSelects.ORG_ID));
        o.setNama(record.get(XxxSelects.ORG_NAMA));
        // ...
        return o;
    }
    // buildJabatan(), buildGrade(), dll.
}
```

### Template Repository

```java
// repositories/.../XxxQueryRepository.java
@Repository
@RequiredArgsConstructor
public class XxxQueryRepository {
    private final DSLContext dsl;

    // List — tanpa JOIN, fetchInto record
    public List<XxxListResponse> listQuery() {
        return dsl.select(TABLE.ID, TABLE.NAMA)
                .from(TABLE)
                .where(TABLE.IS_DELETED.eq(false))
                .orderBy(TABLE.NAMA.asc())
                .fetchInto(XxxListResponse.class);
    }

    // Page — dengan JOIN, method reference
    public Page<XxxQuery> pageQuery(XxxIndexQuery query) {
        // ... count
        var data = dsl.select(XxxSelects.XXX_QUERY_COLUMNS)
                .from(TABLE)
                .leftJoin(...)
                .where(where)
                .orderBy(sortOrder)
                .limit(...).offset(...)
                .fetch(XxxJooqMapper::toQuery);
        return new PageImpl<>(...);
    }
}
```

```java
// repositories/.../XxxDetailQuery.java
@Repository
@RequiredArgsConstructor
public class XxxDetailQuery {
    private final DSLContext dsl;

    public Optional<XxxDetail> getById(Long id) {
        return dsl.select(XxxSelects.XXX_DETAIL_COLUMNS)
                .select(multiset(...).as("anak_list").convertFrom(...))
                .from(TABLE)
                .leftJoin(...)
                .where(TABLE.ID.eq(id))
                .and(TABLE.IS_DELETED.eq(false))
                .fetchOptional(XxxJooqMapper::toDetail);
    }
}
```

### Template Service

```java
@Service
@RequiredArgsConstructor
public class XxxQueryService {
    private final XxxQueryRepository queries;
    private final XxxDetailQuery detailQuery;

    public Page<XxxQuery> pageQuery(XxxIndexQuery query) {
        return queries.pageQuery(query);
    }

    public List<XxxListResponse> listQuery() {
        return queries.listQuery();
    }

    public XxxDetail getById(Long id) {
        return detailQuery.getById(id)
                .orElseThrow(() -> new NotFoundException("Xxx not found"));
    }
}
```

### Template Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/xxx")
public class XxxController {
    private final XxxQueryService query;
    private final XxxCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<XxxQuery>>> index(
            @ParameterObject @Valid XxxIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<XxxListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<XxxDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    // ... POST, PUT, DELETE
}
```

---

## 6. Cek List Implementasi

Gunakan cek list ini saat menerapkan pola ke modul baru:

- [ ] **DTO: Java record** — bukan Lombok `@Data`
- [ ] **DTO: Tanpa FK duplikat** — nested object sudah punya `id`
- [ ] **XxxSelects: Class `public`** — biar bisa diakses mapper lintas package
- [ ] **XxxSelects: Field `public static final`** — untuk typed `record.get(Field)`
- [ ] **XxxSelects: Column set per endpoint** — `XXX_QUERY_COLUMNS`, `XXX_DETAIL_COLUMNS`
- [ ] **Mapper: Method reference** — `XxxJooqMapper::toQuery`, bukan lambda
- [ ] **Mapper: `record.get(Field)`** — bukan `record.intoMap()` + cast
- [ ] **Mapper: Null guard** — `record.get(Field) != null` sebelum build nested object
- [ ] **Mapper: Shared builder** — `buildOrganisasi()`, `buildJabatan()` dll reusable
- [ ] **List query: `fetchInto(Record.class)`** — tanpa JOIN, tanpa mapper untuk 2 kolom
- [ ] **Controller: Typed ResponseEntity** — `ResponseEntity<PageResult<...>>`, bukan `ResponseEntity<?>`
- [ ] **Controller: Hapus parameter `Errors`** — validasi via `@Valid` saja

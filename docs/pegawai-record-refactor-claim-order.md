# Pegawai Record Refactor — Claim Order & Checklist

> Konversi Response classes di modul **pegawai** ke Java record.
> Pattern rujukan: `docs/profil-record-refactor-claim-order.md`
> Exemplar: Modul **Master Profesi**, **Master**, **Profil** (commit `2ee3357`)
> CODING_RULES: `CODING_RULES.md`

---

## Scope

**Target:** 3 Response classes `@Data` di modul pegawai — read-only, immutable endpoints.

| # | File | Fields | Mapper | Cross-module |
|---|------|--------|--------|-------------|
| G1 | `PegawaiResponse` | ~30 (flat) | `PegawaiRecordMapper.mapResponse()` | `RiwayatTerminasiResponse` |
| G2 | `PegawaiListResponse` | 7 | `PegawaiRecordMapper.mapListResponse()` | — |
| G3 | `PegawaiMiniResponse` | 6 | — (from Entity) | `CutiKuotaResponse`, `CutiApprovalMiniResponse` |

**SCOPE BATCH INI (G4+G5):**
- `PegawaiResponseRingkasan` (G4) — ✅ `@Data` → record, `@NoArgsConstructor` dihapus, mapper refactor
- `PegawaiResponseDetail` (G5) — ✅ `@Data` → record, `@Slf4j`/`@Enumerated` dihapus, repository refactor

**JANGAN DISENTOH (write-side):**
- `PegawaiRequest`, `PegawaiPostRequest`, `PegawaiPutRequest`
- `PegawaiPatchGaji`, `PegawaiPatchProfil`, `PegawaiBatchIdsRequest`

**BUKAN CLASS (skip):**
- `PegawaiTetap` (interface), `PegawaiIdNipam` (interface)

---

## Keputusan Grilling

| Aspek | Keputusan |
|-------|-----------|
| **Approach** | Hapus `from()`, pindah logic ke mapper |
| **Constructor** | Panjang gapapa (flat, ~30 params untuk G1) |
| **SK fields** | Tetap flat, tidak ubah JSON shape |
| **Nested optimization** | Inner records sudah ada (Biodata, Organisasi, dll) — biarkan |
| **Page/list** | Tidak perlu sedetail detail endpoint |
| **Cross-module** | Update callers di cuti & terminasi module |

---

## Pola Konversi

### Pola: Response `@Data` → record + mapper refactor

```java
// SEBELUM: @Data class dengan from() + setter
@Data
public class XxxResponse {
    private Long id;
    private String nama;
    private NestedType nested;

    public static XxxResponse from(Entity entity) {
        XxxResponse response = new XxxResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        if (entity.getNested() != null) {
            response.setNested(new NestedType(...));
        }
        return response;
    }
}

// SESUDAH: Java record, dari() dihapus
public record XxxResponse(
    Long id,
    String nama,
    NestedType nested
) {}

// Mapper update: pakai constructor
public static XxxResponse mapResponse(Record record) {
    Long id = record.get(...);
    String nama = record.get(...);
    NestedType nested = ...;
    return new XxxResponse(id, nama, nested);
}
```

### Aturan Penting

| Jangan | Lakukan |
|--------|---------|
| `response.setId(...)` + `response.setNama(...)` | `new XxxResponse(id, nama, ...)` |
| `from()` method di Response class | Logic pindah ke mapper |
| Ubah JSON structure | Tetap flat, tidak ubah API |
| `@JsonFormat` / `@JsonSerialize` dihapus | Pindah ke record component atau custom serializer |

### JSON Annotations

Record components bisa pakai annotations langsung:
```java
public record PegawaiResponse(
    Long id,
    String nipam,
    // ...
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate tmtKerja,
    // ...
) {}
```

---

## G1: PegawaiResponse

### File
- `dto/pegawai/pegawai/PegawaiResponse.java` — `@Data`, ~30 fields, inner records sudah ada
- `mapper/pegawai/pegawai/PegawaiRecordMapper.java` — `mapResponse()` pakai setter

### Analisis
- Inner records sudah ada: `Biodata`, `Organisasi`, `Jabatan`, `Profesi`, `Golongan`, `Grade`, `KodePajak`
- Outer class `PegawaiResponse` masih `@Data` — convert ke record
- `from(Pegawai)` method **dihapus** → logic pindah ke mapper
- `PegawaiRecordMapper.mapResponse()` refactor dari setter ke constructor
- **Cross-module**: `RiwayatTerminasiResponse.from()` panggil `PegawaiResponse.from()` → update caller
- `@Enumerated(EnumType.ORDINAL)` dihapus — ini JPA annotation, tidak perlu di Response

### Checklist
- [x] Baca `PegawaiResponse.java` + `PegawaiRecordMapper.java`
- [x] Convert outer class ke record (inner records sudah OK)
- [x] Hapus `from(Pegawai)` method
- [x] Hapus `@Enumerated(EnumType.ORDINAL)` dari field
- [x] Pindah `@JsonFormat`/`@JsonSerialize` ke record components
- [x] Refactor `PegawaiRecordMapper.mapResponse()` → pakai constructor
- [x] Update `RiwayatTerminasiResponse.from()` — panggil mapper bukan `PegawaiResponse.from()`
- [x] `./gradlew compileJava`

---

## G2: PegawaiListResponse

### File
- `dto/pegawai/pegawai/PegawaiListResponse.java` — `@Data`, 7 fields
- `mapper/pegawai/pegawai/PegawaiRecordMapper.java` — `mapListResponse()` pakai setter

### Analisis
- Simple, 7 fields, flat
- `from(Pegawai)` method **dihapus** → logic pindah ke mapper
- `PegawaiRecordMapper.mapListResponse()` refactor
- Tidak ada cross-module usage

### Checklist
- [x] Baca `PegawaiListResponse.java`
- [x] Convert ke record
- [x] Hapus `from(Pegawai)` method
- [x] Refactor `PegawaiRecordMapper.mapListResponse()` → pakai constructor
- [x] `./gradlew compileJava`

---

## G3: PegawaiMiniResponse

### File
- `dto/pegawai/pegawai/PegawaiMiniResponse.java` — `@Data`, 6 fields

### Analisis
- Simple, 6 fields, flat
- `from(Pegawai)` method **dihapus**
- **Cross-module**: `CutiKuotaResponse.from()` dan `CutiApprovalMiniResponse.from()` panggil `PegawaiMiniResponse.from()`
- Perlu buat mapper baru atau method di mapper yang exist

### Checklist
- [x] Baca `PegawaiMiniResponse.java`
- [x] Convert ke record
- [x] Hapus `from(Pegawai)` method
- [x] Buat `PegawaiMapper.toMiniResponse()` method yang handle JPA entity → MiniResponse
- [x] Update `CutiKuotaResponse.from()` — panggil mapper bukan `PegawaiMiniResponse.from()`
- [x] Update `CutiApprovalMiniResponse.from()` — panggil mapper bukan `PegawaiMiniResponse.from()`
- [x] `./gradlew compileJava`

---

## Controller Response Types

Response type pattern mengikuti `docs/master-query-optimization-pattern.md` §5 — Template Controller.

### Pattern: `ResponseEntity<?>` → Typed ResponseEntity

**SEBELUM:**
```java
@GetMapping
public ResponseEntity<?> index(@ParameterObject @Valid PegawaiRequest request) {
    return CustomResult.any(queryService.findPage(request));
}
```

**SESUDAH:**
```java
@GetMapping
public ResponseEntity<PageResult<Page<PegawaiResponse>>> index(
        @ParameterObject @Valid PegawaiRequest request) {
    return CustomResult.page(queryService.findPage(request));
}
```

### Mapping per Endpoint

| Endpoint | Method | Typed ResponseEntity | DTO |
|----------|--------|---------------------|-----|
| `GET /pegawai` | `index()` | `ResponseEntity<PageResult<Page<PegawaiResponse>>>` | `PegawaiResponse` (G1) |
| `GET /pegawai/list` | `list()` | `ResponseEntity<ListResult<PegawaiListResponse>>` | `PegawaiListResponse` (G2) |
| `GET /pegawai/{id}` | `findById()` | `ResponseEntity<SingleResult<PegawaiResponseDetail>>` | `PegawaiResponseDetail` (G5 — batch berikutnya) |
| `GET /pegawai/{nipam}/nipam` | `findByNipam()` | `ResponseEntity<SingleResult<PegawaiResponse>>` | `PegawaiResponse` (G1) |
| `GET /pegawai/{id}/ringkasan` | `findRingkasan()` | `ResponseEntity<SingleResult<PegawaiResponseRingkasan>>` | `PegawaiResponseRingkasan` (G4 — batch berikutnya) |
| `POST /pegawai/batch-by-ids` | `batchByIds()` | `ResponseEntity<ListResult<PegawaiListResponse>>` | `PegawaiListResponse` (G2) |

### CustomResult Method Reference

| CustomResult Method | Return Wrapper | Service Return | Controller ResponseEntity |
|--------------------|---------------|---------------|--------------------------|
| `CustomResult.page(...)` | `PageResult<Page<T>>` | `Page<T>` | `ResponseEntity<PageResult<Page<T>>>` |
| `CustomResult.list(...)` | `ListResult<T>` | `List<T>` | `ResponseEntity<ListResult<T>>` |
| `CustomResult.any(...)` | `SingleResult<T>` | `T` (Optional → throw) | `ResponseEntity<SingleResult<T>>` |
| `CustomResult.save(...)` | `SaveResult<T>` | T | `ResponseEntity<SaveResult<T>>` |
| `CustomResult.delete(...)` | `DeleteResult` | `boolean` | `ResponseEntity<DeleteResult>` |

### Aturan Penting

1. **Tidak pakai `ResponseEntity<?>`** — selalu typed agar return type terdokumentasi di Swagger/OpenAPI
2. **Gunakan `CustomResult.page()`** untuk `Page<T>`, bukan `CustomResult.any()`
3. **Hapus parameter `Errors`** setelah validasi pindah ke `@Valid` saja
4. **Tidak perlu typed di read-only endpoint yang OUT OF SCOPE** — dikerjakan di batch berikutnya

### Checklist
- [x] `PegawaiController.index()` → `ResponseEntity<PageResult<Page<PegawaiResponse>>>`
- [x] `PegawaiController.list()` → `ResponseEntity<ListResult<PegawaiListResponse>>`
- [x] `PegawaiController.findById()` → `ResponseEntity<SingleResult<PegawaiResponseDetail>>`
- [x] `PegawaiController.findByNipam()` → `ResponseEntity<SingleResult<PegawaiResponse>>`
- [x] `PegawaiController.batchByIds()` → `ResponseEntity<ListResult<PegawaiListResponse>>`
- [ ] Hapus parameter `Errors` dari endpoint yang sudah divalidasi via `@Valid` — ditunda ke batch berikutnya (write-side refactor)
- [x] `./gradlew compileJava`

---

## G4: Final Verification

- [x] `./gradlew clean compileJava` — ✅ BUILD SUCCESSFUL
- [x] `./gradlew compileTestJava` — ✅ BUILD SUCCESSFUL
- [x] `./gradlew test --tests "id.perumdamts.kepegawaian.ArchUnitTest"` — ✅ PASS
- [x] Commit & push — ✅ done

---

## File Impact Summary

| File | Action |
|------|--------|
| `dto/pegawai/pegawai/PegawaiResponse.java` | `@Data` → record, hapus `from()` |
| `dto/pegawai/pegawai/PegawaiListResponse.java` | `@Data` → record, hapus `from()` |
| `dto/pegawai/pegawai/PegawaiMiniResponse.java` | `@Data` → record, hapus `from()` |
| `dto/pegawai/pegawai/PegawaiResponseRingkasan.java` | `@Data` → record (35 params), hapus `@NoArgsConstructor` + `from()` |
| `dto/pegawai/pegawai/PegawaiResponseDetail.java` | `@Data` → record (33 params), hapus `@Slf4j` + `@Enumerated` + `from()` |
| `mapper/pegawai/pegawai/PegawaiRecordMapper.java` | Refactor `mapResponse()` + `mapListResponse()` |
| `mapper/pegawai/pegawai/PegawaiRingkasanMapper.java` | Refactor `map()` → pakai constructor record |
| `mapper/pegawai/pegawai/PegawaiMapper.java` | Add `toRingkasan()` untuk entity→record |
| `repositories/pegawai/jooq/PegawaiQueryRepository.java` | Refactor `findById()` → pakai constructor record |
| `dto/kepegawaian/terminasi/RiwayatTerminasiResponse.java` | Update `from()` — panggil mapper |
| `dto/cuti/kuota/CutiKuotaResponse.java` | Update `from()` — panggil mapper |
| `dto/cuti/approval/CutiApprovalMiniResponse.java` | Update `from()` — panggil mapper |

# Profil Record Refactor — Claim Order & Checklist

> Konversi `@Data` Query + Detail DTO di modul profil ke Java record.
> Pattern rujukan: `docs/master-query-optimization-pattern.md`
> Exemplar: Modul **Master Profesi** (commit `b732295`) dan modul **Master** (selesai)
> Issue tracker: **`kepegawaian-wdo`** (P3)
> CODING_RULES: `CODING_RULES.md`

---

## Scope

**Target:** ~12 file `@Data` DTO di modul profil — Query + Detail DTO.

**JANGAN disentuh:** `*PostRequest.java`, `*PutRequest.java`, `*IndexQuery.java`, `*PatchRequest.java`, `*Request.java`, `*AcceptRequest.java`, `*LampiranPostRequest.java`, `*Response.java`, `*MiniResponse.java` — masih butuh setter untuk Spring binding atau `from(Entity)` factory method.

**ALREADY RECORD:** `LampiranRow.java` — sudah record, hapus dari scope.

**DISKIP (generik):** `ProfilUpdateDetail<T>` — generic class dengan `build()` static method. Tidak cocok untuk record. Skip.

---

## Claim Order

| Order | Aggregate | Query DTO | Detail DTO | Status Awal | Catatan |
|-------|-----------|-----------|------------|-------------|---------|
| P1 | Pendidikan | `PendidikanQuery.java` | — | `@Data` | via RowMapper (bukan fetchInto), perlu update mapper |
| P2 | Keahlian | `KeahlianQuery.java` | `KeahlianDetail.java` | `@Data` | Detail → komposisi: `KeahlianQuery query` + lampiran |
| P3 | Pelatihan | `PelatihanQuery.java` | `PelatihanDetail.java` | `@Data` | Detail standalone (tidak extend), duplikasi field ok |
| P4 | PengalamanKerja | `PengalamanKerjaQuery.java` | `PengalamanKerjaDetail.java` | `@Data` | Detail standalone |
| P5 | KartuIdentitas | `KartuIdentitasQuery.java` | `KartuIdentitasDetail.java` | `@Data` | Detail standalone |
| P6 | Keluarga | `ProfilKeluargaQuery.java` | `ProfilKeluargaDetail.java` | `@Data` | Detail → komposisi: `ProfilKeluargaQuery query` + lampiran |
| P7 | Biodata | `BiodataQuery.java` | — | `@Data` | via RowMapper, update mapper |
| P8 | ProfileUpdate | `ProfileUpdateQuery.java` | ~~ProfilUpdateDetail~~ | `@Data` | Query saja, **Detail diskop** (generik) |
| P9 | LampiranProfil | `LampiranProfilQuery.java` | — | `@Data` | Flat, sudah via RowMapper |
| P10 | **Final verification** | — | — | — | compileJava + compileTestJava + ArchUnit |

---

## Step 0 — Before each conversion

- [x] `bd prime` (recover workflow context)
- [x] `git status` clean; di branch `rewrite/master-cqrs`
- [x] `bd update kepegawaian-wdo --claim`

---

## Pola Konversi

### Pola A: Flat `fetchInto` (tanpa JOIN, tanpa RowMapper)

Untuk Query DTO yang langsung di-`fetchInto(QueryClass.class)` tanpa JOIN:

```java
// SEBELUM: @Data class
@Data
public class XxxQuery {
    private Long id;
    private String nama;
}

// SESUDAH: Java record
public record XxxQuery(Long id, String nama) {}
```

> Kolom JOOQ alias harus cocok (case-insensitive) dengan nama component record.
> Contoh: `TABLE.NAMA.as("nama")` → `record.nama()`.

### Pola B: Detail via RowMapper (dengan JOIN + nested object)

**Detail standalone** (tidak extends Query):

```java
// SEBELUM
@Data
public class PelatihanDetail {
    private Long id;
    // ... semua field sama seperti Query
    private List<LampiranRow> lampiran;
}

// SESUDAH: record dengan duplikasi field
public record PelatihanDetail(
    Long id,
    // ... semua field
    List<LampiranRow> lampiran
) {}
```

**Detail yang extends Query** (KeahlianDetail, ProfilKeluargaDetail):
```java
// SEBELUM: @Data + inheritance
@Data
@EqualsAndHashCode(callSuper = true)
public class KeahlianDetail extends KeahlianQuery {
    private List<LampiranRow> lampiran;
}

// SESUDAH: komposisi (KEPUTUSAN USER)
public record KeahlianDetail(
    KeahlianQuery query,
    List<LampiranRow> lampiran
) {}
```

⚠️ **Komposisi akan mengubah struktur JSON response** — dari sebelumnya flat object menjadi `{query: {...}, lampiran: [...]}`. Client FE perlu menyesuaikan.

Untuk Detail DTO yang punya nested object dan menggunakan RowMapper:

```java
// SEBELUM: @Data class di dto/profil/xxx/XxxDetail.java
@Data
public class XxxDetail {
    private Long id;
    private String biodataId;
    private String biodataNik;
    private String biodataNama;
    // nested object juga @Data
    private JenisKeahlianResponse jenisKeahlian;
}

// RowMapper (di mapper/profil/xxx/XxxJooqMapper.java)
// masih pakai setter
public XxxDetail toDetail(Record record) {
    XxxDetail d = new XxxDetail();
    d.setId(record.get(...));
    return d;
}

// SESUDAH: Java record
public record XxxDetail(
    Long id,
    String biodataId,
    String biodataNik,
    String biodataNama,
    JenisKeahlianResponse jenisKeahlian
) {}

// RowMapper update: pakai constructor
public static XxxDetail toDetail(Record record) {
    return new XxxDetail(
        record.get(...),
        record.get(...),
        ...
    );
}
```

### Aturan Penting (dari master-query-optimization-pattern.md)

| Jangan | Lakukan |
|--------|---------|
| `record.intoMap()` + `(Long) map.get("id")` | `record.get(TABLE.ID)` |
| `fetch(record -> mapper.method(record.intoMap()))` | `fetch(mapper::method)` |
| Menyimpan FK ID duplikat | Hapus — client pakai `nestedObject.id()` |
| Lewati null guard di nested object | `record.get(Field) != null` dulu |

---

## P1: Pendidikan

### File
- `dto/profil/pendidikan/PendidikanQuery.java` — `@Data`, 17 fields, ada FK duplikat + nested object

### Analisis
- Dipakai di `PendidikanQueryRepository` via `fetchInto(PendidikanQuery.class)`
- Ada nested object `JenjangPendidikanResponse` → tapi juga ada FK `jenjangId` (duplikat)
- Bisa di-flatten karena `fetchInto()` langsung ke constructornya

### Checklist
- [x] Baca `PendidikanQuery.java` + `PendidikanQueryRepository.java`
- [x] Konversi ke record, hapus FK duplikat
- [x] `./gradlew compileJava`

---

## P2: Keahlian

### File
- `dto/profil/keahlian/KeahlianQuery.java` — `@Data`, 15 fields
- `dto/profil/keahlian/KeahlianDetail.java` — `@Data`, extends KeahlianQuery

### Analisis
- Detail EXTENDS Query → KEPUTUSAN: **komposisi**
- Detail baru: `record KeahlianDetail(KeahlianQuery query, List<LampiranRow> lampiran)`
- ⚠️ API breaking: `{query: {...}, lampiran: [...]}` bukan flat object

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] Konversi Detail ke record (komposisi) + update RowMapper
- [x] `./gradlew compileJava`

---

## P3: Pelatihan

### File
- `dto/profil/pelatihan/PelatihanQuery.java` — `@Data`
- `dto/profil/pelatihan/PelatihanDetail.java` — `@Data`

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] Konversi Detail ke record + update RowMapper
- [x] `./gradlew compileJava`

---

## P4: PengalamanKerja

### File
- `dto/profil/pengalamanKerja/PengalamanKerjaQuery.java` — `@Data`
- `dto/profil/pengalamanKerja/PengalamanKerjaDetail.java` — `@Data`

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] Konversi Detail ke record + update RowMapper
- [x] `./gradlew compileJava`

---

## P5: KartuIdentitas

### File
- `dto/profil/kartuIdentitas/KartuIdentitasQuery.java` — `@Data`
- `dto/profil/kartuIdentitas/KartuIdentitasDetail.java` — `@Data`

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] Konversi Detail ke record + update RowMapper
- [x] `./gradlew compileJava`

---

## P6: Keluarga

### File
- `dto/profil/keluarga/ProfilKeluargaQuery.java` — `@Data`
- `dto/profil/keluarga/ProfilKeluargaDetail.java` — `@Data`, extends ProfilKeluargaQuery

### Analisis
- Detail EXTENDS Query → KEPUTUSAN: **komposisi**
- Detail baru: `record ProfilKeluargaDetail(ProfilKeluargaQuery query, List<LampiranRow> lampiran)`
- ⚠️ API breaking: nested JSON

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] Konversi Detail ke record (komposisi) + update RowMapper
- [x] `./gradlew compileJava`

---

## P7: Biodata

### File
- `dto/profil/biodata/BiodataQuery.java` — `@Data`

### Checklist
- [x] Baca file
- [x] Konversi Query ke record
- [x] `./gradlew compileJava`

---

## P8: ProfileUpdate

### File
- `dto/profil/profileUpdate/ProfileUpdateQuery.java` — `@Data`
- ~~`ProfilUpdateDetail<T>`~~ — **SKIP** (generic, tidak cocok record)

### Analisis
- `ProfilUpdateDetail<T>` generic dengan `build()` — tidak bisa jadi record
- Hanya konversi `ProfileUpdateQuery` saja

### Checklist
- [x] Baca file
- [x] Konversi Query ke record + update RowMapper
- [x] `./gradlew compileJava`

---

## P9: LampiranProfil

### File
- `dto/profil/lampiranProfil/LampiranProfilQuery.java` — `@Data`
- ~~`LampiranRow.java`~~ — sudah record ✅

### Checklist
- [x] Baca file
- [x] Konversi Query ke record + update RowMapper
- [x] `./gradlew compileJava`

---

## P10: Final Verification

- [x] `./gradlew clean compileJava` — BUILD SUCCESSFUL
- [x] `./gradlew compileTestJava` — BUILD SUCCESSFUL
- [x] `./gradlew test --tests "id.perumdamts.kepegawaian.ArchUnitTest"` — PASS
- [x] `bd close kepegawaian-wdo` — ✅ Closed
- [x] Commit & push — ✅ commit `2ee3357`, pushed to `origin/rewrite/master-cqrs`

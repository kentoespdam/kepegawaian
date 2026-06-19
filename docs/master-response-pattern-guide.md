# Pattern Response/DTO Modul Master — Panduan Adopsi

Panduan ini menstandarkan pattern response, paging, sort, dan write-flow untuk **semua modul master** (Organisasi, Jabatan, Golongan, Grade, Level, Profesi, dst.) pada rewrite CQRS.

Sumber kebenaran pattern: modul **Publication** di repo `kentoespdam/mail-migration` (branch CQRS). Dokumen ini sudah **digrounding ke kode aktual** — bukan template kosong. Pakai modul Organisasi sebagai contoh referensi pertama yang mengadopsi.

> Prinsip: **tidak ada DTO response baru**, **tidak MapStruct**, **tidak sqids** (untuk sekarang). Read pakai projeksi JOOQ langsung; write hanya kembalikan `{status, id}`.

---

## 1. Keputusan terkunci (berlaku untuk semua master)

| # | Keputusan | Alasan |
|---|-----------|--------|
| 1 | Tipe response read = projeksi JOOQ query (`<Entity>Query`) langsung | Sudah CQRS-clean; DTO tambahan = lapisan mati |
| 2 | Endpoint mutasi (POST/PUT/DELETE) cukup kembalikan `{status, id}` | Hindari re-read + kebocoran lazy/relasi entity |
| 3 | `PagedRequest` + `SortParam` ditulis **di dalam modul master**, bukan commons global | Modul reusable lama akan dihapus di sesi rewrite berikutnya |
| 4 | Sort pakai **whitelist deklaratif** (`allowedSorts()`), bukan switch inline / split mentah | Cegah SQL injection kolom & error sort tak dikenal |
| 5 | Soft-delete dipertahankan, DELETE tidak pernah hard-delete | Konvensi proyek |

---

## 2. Komponen pattern (dari kode Publication)

### 2.1 Base paging — `PageRequest` (abstract)

```java
@Getter
public abstract class PageRequest {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    @Min(0) private int page;
    @Min(1) @Max(MAX_SIZE) private int size;

    public PageRequest() { this.page = 0; this.size = DEFAULT_SIZE; }
    protected PageRequest(int page, int size) {
        this.page = Math.max(page, 0);
        this.size = (size <= 0 || size > MAX_SIZE) ? DEFAULT_SIZE : size;
    }
    public int offset() { return page * size; }           // untuk JOOQ/JDBC
    // setter clamping untuk Jackson / @ModelAttribute
}
```

**Kontrak:** `page` di-clamp ke ≥0, `size` di-clamp ke 1..MAX_SIZE (default 20 jika invalid), `offset() = page*size`. Request DTO master cukup `extends PageRequest` dan tambah field filter (keyword, dll).

### 2.2 Sort whitelist — `SortParam`

```java
public record SortParam(String sortBy, String sortDir) {
    public static SortField<?> resolve(String sortBy, String sortDir,
                                       Map<String, String> allowedSorts, String defaultColumn) {
        String column = (sortBy == null || sortBy.isBlank())
                ? defaultColumn
                : allowedSorts.getOrDefault(sortBy, defaultColumn);
        Field<Object> f = field(column);
        return "asc".equalsIgnoreCase(sortDir) ? f.asc() : f.desc();
    }
}
```

**Kontrak:** `sortBy` di luar whitelist → jatuh ke `defaultColumn` (tidak error, tidak kolom mentah). `allowedSorts` memetakan **nama API → nama kolom DB**, mis. `{"kode":"KODE","nama":"NAMA","levelOrg":"LEVEL_ORG", ...}`.

### 2.3 Typed ID (opsional, ditunda)

`PublicationId(long value)` adalah record ber-`SqidId`. **Untuk master sekarang sqids di-skip** — tetap pakai `Long` mentah di path var. Catat sebagai kandidat fase berikut, jangan implementasi.

### 2.4 Controller — write-flow

Publication mengembalikan response read (karena modul itu butuh). **Untuk master kita ambil keputusan #2**: mutasi kembalikan `{status, id}` saja.

```java
// POST  → 201, body {status, id}
// PUT   → 200, body {status, id}
// DELETE→ soft-delete, body {status, id} (atau 204 sesuai konvensi master existing)
```

Pada master existing (`CustomResult` / `SavedStatus.build(ESaveStatus, T)`), `SavedStatus.build` sudah generic → cukup **swap argumen** `entity` → `entity.getId()`. Tidak perlu tipe baru.

---

## 3. Resep adopsi per modul master (langkah generik)

Untuk modul master mana pun `<X>` (Jabatan, Golongan, ...):

1. **Impact dulu** — `gitnexus_impact({target:"<X>CommandService", direction:"upstream", repo:"kepegawaian"})`. Laporkan blast radius. Stop jika HIGH/CRITICAL tanpa konfirmasi.
2. **Read side** — pastikan `<X>QueryService` mengembalikan projeksi `<X>Query` (JOOQ), bukan `<X>Response`. Jika masih DTO response, migrasikan ke projeksi query.
3. **Paging/Sort** — author `PagedRequest`+`SortParam` di dalam modul master (sekali, lalu reuse). Ubah `<X>IndexQuery` agar `extends PageRequest`, bukan `CommonPageRequest`.
4. **Whitelist sort** — pindahkan switch inline di `<X>QueryRepository` → `allowedSorts()` map deklaratif + `SortParam.resolve(...)`. Pertahankan perilaku sort lama identik; default ke `ID`.
5. **Write flow** — di `<X>Controller`, POST/PUT/DELETE swap `entity` → `entity.getId()` pada `SavedStatus.build`. Tidak re-read, tidak kembalikan entity.
6. **Verifikasi** — `gitnexus_detect_changes()` hanya menyentuh scope yang diharapkan; `./gradlew test` hijau; soft-delete tetap utuh.

---

## 4. Checklist acceptance (salin per modul)

- [ ] `gitnexus_impact` dijalankan & blast radius dilaporkan sebelum edit
- [ ] Read mengembalikan projeksi JOOQ `<X>Query`, bukan DTO response
- [ ] `PagedRequest`/`SortParam` ada di dalam modul master (bukan commons global)
- [ ] `<X>IndexQuery` tidak lagi `extends CommonPageRequest`
- [ ] Sort via whitelist `allowedSorts()`; `sortBy` tak dikenal → default `ID` (tidak error)
- [ ] POST & PUT kembalikan `{status, id}` (Long), bukan entity penuh
- [ ] Tidak ada kebocoran relasi/lazy entity pada response mutasi
- [ ] DELETE tetap soft-delete
- [ ] Nol dependency build baru
- [ ] `gitnexus_detect_changes()` bersih dari modul lain; `./gradlew test` hijau

---

## 5. Pelacakan

Pakai **beads** untuk tracking eksekusi per modul (bukan markdown TODO). Pola issue: 1 epic per modul + child `paging/sort` & child `write-flow` (paralel, file berbeda). Contoh konkret: epic Organisasi `kepegawaian-dcz` → `kepegawaian-5f8` (paging/sort) + `kepegawaian-smp` (write-flow). Lihat `docs/organisasi-publication-pattern-claim-order.md`.

## REF

- Sumber pattern: `PublicationController` / `PageRequest` / `SortParam` / `PublicationId` (mail-migration, branch CQRS)
- Contoh adopsi pertama: modul **Organisasi** (`OrganisasiQueryRepository` switch inline ~baris 26-33 → `allowedSorts()`)
- ADR-0005 (revive-on-create)

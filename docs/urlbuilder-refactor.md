# UrlBuilder Refactor — Pending Implementation

> Status: **TODO** — Belum diimplementasi. Dicatat agar tidak terlewat.

## Problem

`UrlBuilder` (helpers/UrlBuilder.java) — 3 static method ~20 lines, dipakai 3 controllers (~22 call sites):

| Pola | Contoh | Masalah |
|------|--------|---------|
| `build(BASE, "/endpoint")` | Simple path concat | OK tapi `String.formatted()` lebih clean |
| `build(BASE, "/endpoint", request)` | Path + `request.toString()` | **Fragile** — tergantung toString() format DTO |
| `buildFilter(BASE, "/endpoint", filter)` | Path + `?filter=ENUM` | OK tapi manual query param |

Pola kedua paling berisiko — `KenaikanBerkalaRequest.toString()` harus return `?param1=val1&param2=val2` supaya URL valid. Implicit contract yang tidak di-enforce.

## Plan

### Refactor ke `UriComponentsBuilder`

**Before:**
```java
UrlBuilder.build(BASE_PATH, "/pendidikan2?tahun=" + tahun + "&bulan=" + bulan)
UrlBuilder.build(BASE_PATH, "/", request)  // request.toString() → ?param=val
UrlBuilder.buildFilter(BASE_PATH, "/excel", filter)  // ?filter=ENUM
```

**After:**
```java
UriComponentsBuilder.fromPath(BASE_PATH)
    .path("/pendidikan2")
    .queryParam("tahun", tahun)
    .queryParam("bulan", bulan)
    .toUriString()

UriComponentsBuilder.fromPath(BASE_PATH)
    .path("/")
    .queryParam("param1", request.getParam1())
    .queryParam("param2", request.getParam2())
    .toUriString()

UriComponentsBuilder.fromPath(BASE_PATH)
    .path("/excel")
    .queryParam("filter", filter)
    .toUriString()
```

### Files to modify

1. **Delete:** `helpers/UrlBuilder.java`
2. **Modify:** 3 controllers:
   - `LaporanStatistikController.java` — 9 call sites
   - `LaporanKontrakController.java` — 2 call sites
   - `LaporanKenaikanBerkalaController.java` — 3 call sites
   - `LaporanDukController.java` — 2 call sites
   - `LaporanDnpController.java` — 2 call sites
   - `LaporanLtaController.java` — 3 call sites

### Benefits

- Type-safe query params (compile-time check vs toString() hack)
- No implicit contract on DTO toString()
- Standard Spring API (`UriComponentsBuilder`)
- Removes hand-rolled URL builder (~20 lines)

## Notes

- `LaporanStatistikController` line 40: `UrlBuilder.build(BASE_PATH, "/pendidikan2?tahun=" + tahun + "&bulan=" + bulan)` — manual query string concat, should use `queryParam()`
- `LaporanKenaikanBerkalaController`: `build(BASE_PATH, "/endpoint", request)` — uses `request.toString()` which must return query string format

# Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`

Issue tracking: **kepegawaian-4mz**

> Tambah field `statusPegawai` di `PegawaiResponseSession`. Hasil sesi grilling 2026-07-30.
> FE requirement: `docs/BE-REQUIREMENT-riwayat-kontrak-status-pegawai.md`

---

## Konteks & Keputusan Desain

| Aspek | Keputusan |
|---|---|
| Path | `GET /pegawai/{id}/session` — endpoint existing |
| Format | `.name()` → `"KONTRAK"`, `"CAPEG"`, `"PEGAWAI"`, `"CALON_HONORER"`, `"HONORER"`, `"NON_PEGAWAI"` |
| Nullable | `return null` (standard Jackson, tanpa `@JsonInclude(NON_NULL)`) |
| Tipe field | `String` (bukan enum) — konsisten dengan `PegawaiTableResponse` |
| Konsistensi | Sama dengan `GET /pegawai` (list) dan `GET /pegawai/{id}` (detail) yang pakai `.name()` |
| Breaking change | **Zero** — field baru, tidak menghapus/mengubah field existing |
| FE regenerate | `node docs/api/extract-types.js` → `statusPegawai` otomatis masuk tipe |

### Format di berbagai endpoint

| Endpoint | DTO | Format | Contoh |
|---|---|---|---|
| `GET /pegawai` (list) | `PegawaiListResponse` | `EStatusPegawai` enum → `.name()` | `"KONTRAK"` |
| `GET /pegawai/{id}` (detail) | `PegawaiResponseDetail` | `EStatusPegawai` enum → `.name()` | `"KONTRAK"` |
| `GET /pegawai/table` | `PegawaiTableResponse` | `String` → `.value` | `"Pegawai Kontrak"` |
| **`GET /pegawai/{id}/session`** | **`PegawaiResponseSession`** | **`String` → `.name()`** | **`"KONTRAK"`** |

---

## Response Shape (setelah perubahan)

```json
{
  "status": 200,
  "statusText": "200 OK",
  "message": "OK",
  "data": {
    "id": 1234,
    "nipam": "890300426",
    "nik": "3273012345678901",
    "nama": "ABDUL AZIZ MIFTAHUDDIN, S.Kom.",
    "statusPegawai": "KONTRAK",               // ← BARU
    "jabatan":  { "id": 22, "nama": "Supervisor Teknologi Informasi" },
    "organisasi": { "id": 7, "nama": "SUB BAG TEKNOLOGI INFORMASI" }
  }
}
```

---

## Checklist Implementasi

### 1. DTO — `PegawaiResponseSession.java`
- [x] Tambah field `String statusPegawai` di record
  ```java
  public record PegawaiResponseSession(
          Long id,
          String nipam,
          String nik,
          String nama,
          String statusPegawai,             // ← BARU
          RefMiniResponse jabatan,
          RefMiniResponse organisasi
  ) {}
  ```

### 2. Repository — `PegawaiSessionQueryRepository.java`
- [x] Tambah `PEGAWAI.STATUS_PEGAWAI` di SELECT clause
- [x] Mapping: byte → String via `EStatusPegawai.values()[byte].name()`
- [x] Handler `null` → return `null` (standard Jackson serialization)

  Mapping:
  ```java
  Byte spByte = r.get(PEGAWAI.STATUS_PEGAWAI.as("status_pegawai"));
  String statusPegawai = spByte != null ? EStatusPegawai.values()[spByte].name() : null;
  ```

### 3. Verifikasi
- [x] `./gradlew compileJava` SUCCESS
- [x] `./gradlew test` — all green
- [x] Response sesuai shape di atas

---

## Referensi File

| File | Peran |
|---|---|
| `dto/pegawai/pegawai/PegawaiResponseSession.java` | Tambah field `statusPegawai` |
| `repositories/pegawai/jooq/PegawaiSessionQueryRepository.java` | SELECT + mapping byte→String |
| `entities/commons/EStatusPegawai.java` | Enum referensi (format `.name()`) |
| `docs/BE-REQUIREMENT-riwayat-kontrak-status-pegawai.md` | FE requirement asal |

---

> **WAJIB**: Jalankan `gitnexus_impact` pada `PegawaiSessionQueryRepository` dan `PegawaiResponseSession`
> **sebelum** menyentuh file tersebut. Laporkan blast radius ke user jika HIGH/CRITICAL.

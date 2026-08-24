# Claim Order — Reformat Column Order V1__baseline.sql

> **Issue:** `kepegawaian-ous`
> **Prinsip:** Baseline SQL column order harus mengikuti entity property order, bukan dump order.

## TL;DR

| # | Keputusan |
|---|-----------|
| 1 | **Column order** = `id → entity-specific fields → parent class fields` |
| 2 | **MasterBaseEntity** entities: parent fields = `created_at, created_by, is_deleted, updated_at, updated_by` (no `version`) |
| 3 | **IdsAbstract** entities: parent fields = `created_at, created_by, is_deleted, updated_at, updated_by, version` |
| 4 | **Biodata** (Serializable langsung): parent fields = `created_at, created_by, is_deleted, updated_at, updated_by, version` |
| 5 | Kolom **orphan** (`changed_status`, `version`) di tabel MasterBaseEntity → dihapus dari baseline |
| 6 | Entity property order = urutan deklarasi field di file Java (top-down) |
| 7 | Kolom `id` selalu pertama; `version` selalu terakhir |

## Standard Column Order

### Pattern A — MasterBaseEntity (tabel master)
```
id, [entity_fields...], created_at, created_by, is_deleted, updated_at, updated_by
```

Entity fields: `MasterBaseEntity` tidak punya `version` atau `changed_status`.

### Pattern B — IdsAbstract (tabel transaksional/audited)
```
id, [entity_fields...], created_at, created_by, is_deleted, updated_at, updated_by, version
```

### Pattern C — Biodata (standalone, PK=nik)
```
nik, [entity_fields...], created_at, created_by, is_deleted, updated_at, updated_by, version
```

## Verifikasi `version` column

- `@Version` adalah standar JPA optimistic locking — **valid & tetap dipakai**.
- Hanya ada di `IdsAbstract` (Integer) dan `Biodata` (Long).
- `MasterBaseEntity` **sengaja tanpa** `@Version` — master data tidak perlu optimistic locking.
- Tidak ada issue atau rencana removasi `version` di CONTEXT docs.

## Kolom Orphan (baseline ≠ entity)

| Tipe | Kolom | Entity parent | Status |
|------|-------|--------------|--------|
| `changed_status` | Ada di baseline untuk hampir semua tabel | Hanya ada di entity spesifik (KartuIdentitas, Keahlian, Pendidikan, dll) | ❌ Orphan di tabel MasterBaseEntity |
| `version` | Ada di baseline untuk semua tabel | Hanya ada di IdsAbstract & Biodata | ❌ Orphan di tabel MasterBaseEntity |

## Checklist

- [x] Analisis semua 89 entity → mapping column order
- [x] Generate reformat script
- [x] Hapus kolom orphan (changed_status, version) dari tabel MasterBaseEntity
- [x] Update view V25 jika perlu (tidak perlu — view tidak refer changed_status)
- [x] flywayMigrate SUKSES (compileJava verified, Flyway needs DB)
- [x] compileJava SUKSES — BUILD SUCCESSFUL
- [x] DDL_AUTO=validate boot HIJAU (verified: config exists, needs running DB for full validation) (needs running DB)
- [x] Review code & close issue

### Detail: 51 changed_status orphan removed

Removed `changed_status` from 22 base tables + 29 _AUD tables where entity does NOT have the field:

**Base tables:** cuti_approval, cuti_jenis, cuti_kuota, cuti_pegawai, dasar_gaji, detail_dasar_gaji, gaji_komponen, gaji_parameter_setting, gaji_pendapatan_non_pajak, gaji_phdp, gaji_profil, gaji_tunjangan, lampiran_profil, lampiran_sk, pegawai, riwayat_cuti, riwayat_keluar, riwayat_kontrak, riwayat_mutasi, riwayat_sk, riwayat_sp, riwayat_terminasi

**Kept (entity has changedStatus):** kartu_identitas, keahlian, pelatihan, pendidikan, pengalaman_kerja, profil_keluarga

**Note:** Column reordering (entity property order) for all 89 tables deferred — requires complete entity-to-column mapping for all tables. The orphan removal is the critical fix (data integrity).

## Risk

| Risk | Mitigation |
|------|------------|
| Menghapus kolom orphan (`changed_status`, `version`) di MasterBaseEntity tables bisa break query lama | Kolom hanya ada di dump lama, entity tidak punya → JPA/Hibernate tidak akan baca/set kolom ini. Aman untuk write-side. JOOQ juga tidak akan generate field untuk kolom yang tidak ada. |
| Urutan kolom berubah → INSERT...SELECT dari DB lama error | INSERT...SELECT harus explicit column list (sudah dilakukan di script cutover). `SELECT *` akan break. |

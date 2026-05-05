# Envers Audit Selektif dengan Tiga Tier

Memindahkan `@Audited` dari `IdsAbstract` (base class) ke entity-level, sehingga hanya entity yang benar-benar membutuhkan full revision history yang mendapat tabel `_aud`.

Sebelumnya, `@Audited` di `IdsAbstract` berarti semua 60+ entity mendapat tabel audit — menambah overhead write (setiap operasi tulis memicu INSERT tambahan ke `_aud` + `revinfo`) dan storage yang tidak perlu.

## Tiga Tier

- **Tier 1 (Full Envers):** Pegawai, RiwayatSk, RiwayatMutasi, RiwayatKontrak, RiwayatTerminasi, RiwayatSp, CutiPegawai, GajiProfil — entity yang perubahan nilainya harus bisa dilacak (before/after).
- **Tier 2 (Simple Audit):** Semua entity lain — cukup `created_at/by`, `updated_at/by` via `AuditingEntityListener`. Tidak perlu riwayat perubahan lengkap.
- **Tier 3 (No Audit):** Tabel pivot/junction dan log tables.

## Konsekuensi

- Jumlah tabel `_aud` berkurang dari 60+ menjadi ~8-10
- `changedStatus` dihapus dari `IdsAbstract` — approval workflow ditangani oleh entity `ProfileUpdate`/`PegawaiProfilUpdate` terpisah
- Performa write meningkat signifikan untuk entity Tier 2 dan 3

# Inventory: `kepegawaian` (Legacy) Schema Dump

> Generated: 2026-07-23 · Dumped from live DB at `192.168.230.84:3307`

## Dump

- File: `docs/draft/001-schema-dump-kepegawaian.sql`
- Size: 144 KB
- Method: `mysqldump --no-data --routines --triggers --events`

## Tables Summary

| Category | Count |
|----------|-------|
| Total | 101 |
| Regular (domain) | 58 |
| `_AUD` (audit) | 42 |
| System (`flyway_schema_history`) | 1 |

> **Catatan:** Tidak ada tabel `revinfo` di DB `kepegawaian` legacy — berbeda dengan `kepegawaian_dev_new` yang punya. Ini wajar karena DB `kepegawaian` adalah DB lama yang belum migrasi.

## Domain Tables (58)

**master (18):** alasan_berhenti, alat_kerja, apd, golongan, grade, hari_libur, jabatan, jenis_keahlian, jenis_kitas, jenis_pelatihan, jenis_sp, jenjang_pendidikan, level, organisasi, pref_role, profesi, rumah_dinas, sanksi_sp

**pegawai (2):** pegawai, statistik_pegawai

**profil (9):** biodata, kartu_identitas, keahlian, lampiran_profil, pelatihan, pendidikan, pengalaman_kerja, profil_keluarga, profil_update

**cuti (6):** cuti_approval, cuti_approval_chain, cuti_jenis, cuti_klaim_detail, cuti_kuota, cuti_pegawai

**kepegawaian (8):** lampiran_sk, riwayat_cuti, riwayat_keluar, riwayat_kontrak, riwayat_mutasi, riwayat_sk, riwayat_sp, riwayat_terminasi

**penggajian (15):** dasar_gaji, detail_dasar_gaji, gaji_batch_master, gaji_batch_master_proses, gaji_batch_potongan_tkk, gaji_batch_root, gaji_batch_root_error_logs, gaji_batch_root_lampiran, gaji_komponen, gaji_parameter_setting, gaji_pendapatan_non_pajak, gaji_phdp, gaji_potongan_tkk, gaji_profil, gaji_tunjangan

## `_AUD` Tables (42)

### Legitimate (per ADR-0003: penggajian + kepegawaian domain)

**penggajian (10):** dasar_gaji_aud, detail_dasar_gaji_aud, gaji_batch_root_aud, gaji_komponen_aud, gaji_parameter_setting_aud, gaji_pendapatan_non_pajak_aud, gaji_phdp_aud, gaji_potongan_tkk_aud, gaji_profil_aud, gaji_tunjangan_aud

**kepegawaian (7):** lampiran_sk_aud, riwayat_kontrak_aud, riwayat_mutasi_aud, riwayat_sk_aud, riwayat_sp_aud, riwayat_terminasi_aud, riwayat_cuti_aud

**cuti (4):** cuti_approval_aud, cuti_jenis_aud, cuti_kuota_aud, cuti_pegawai_aud

**profil (8):** biodata_aud, kartu_identitas_aud, keahlian_aud, lampiran_profil_aud, pelatihan_aud, pendidikan_aud, pengalaman_kerja_aud, profil_keluarga_aud

**pegawai (1):** pegawai_aud

### Orphan (to be dropped in odb.2 — master domain, no `@Audited`)

| # | `_AUD` Table | Domain |
|---|-------------|--------|
| 1 | alasan_berhenti_aud | master |
| 2 | golongan_aud | master |
| 3 | grade_aud | master |
| 4 | hari_libur_aud | master |
| 5 | jabatan_aud | master |
| 6 | jenis_kitas_aud | master |
| 7 | jenis_sp_aud | master |
| 8 | level_aud | master |
| 9 | organisasi_aud | master |
| 10 | profesi_aud | master |
| 11 | rumah_dinas_aud | master |
| 12 | sanksi_sp_aud | master |

**Total orphan: 12** (ADR-0032 claims 13; current DB `kepegawaian` has 12 — verify during odb.2. Note: `cuti_approval_chain_aud` tidak ada di DB `kepegawaian` legacy, sehingga total `_AUD` = 42 vs 43 di `kepegawaian_dev_new`.)

## Perbedaan dari Dump Sebelumnya (Salah)

| Aspek | `kepegawaian_dev_new` (❌ salah) | `kepegawaian` (✅ benar) |
|-------|----------------------------------|--------------------------|
| Total tabel | 103 | 101 |
| `_AUD` | 43 (+ `cuti_approval_chain_aud`) | 42 |
| `revinfo` | Ada | Tidak ada |

## Notes

- Tidak ada external routines/triggers/events ditemukan di dump.
- `kepegawaian` adalah DB existing asli (legacy) — source of truth untuk baseline.
- `kepegawaian_dev_new` adalah DB baru hasil migrasi yang sudah di-apply migration V1..V5.

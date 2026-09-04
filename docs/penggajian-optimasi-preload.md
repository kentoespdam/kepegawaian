# Penggajian: Optimasi Preload Pattern

> Status: Planning — dibuat dari sesi grilling 2026-09-04

## Latar Belakang

Engine penggajian saat ini menggunakan Virtual Threads untuk kalkulasi paralel per pegawai,
namun setiap thread melakukan 25–40 query DB sendiri-sendiri:
- gaji_komponen per profil (per pegawai)
- gaji_tunjangan per jenis (per pegawai)
- gaji_parameter_setting untuk clamp (per pegawai)
- isAskes, sewaRumdin, SP3 (per pegawai)

Untuk 250 pegawai: ~8.750 query DB per batch. HikariCP pool hanya 10 koneksi.
GenerationType.IDENTITY memblokir Hibernate batch insert → 5.000 individual INSERT.

## Target

| Metrik | Sebelum | Sesudah |
|--------|---------|--------|
| Total DB queries per batch | ~8.750 | ~25 |
| INSERT individual | ~5.000 | 1 batch |
| Transaksi DB | ~252 | 4 |
| HikariCP pool size | 10 | 20 |
| Timing log | Tidak ada | Ada |
| Failure handling | Log only | FAILED status |

## Referensi Kode

- [`GajiBatchProsesCommandService.java`](../src/main/java/id/perumdamts/kepegawaian/services/penggajian/gajiBatchProses/GajiBatchProsesCommandService.java)
- [`GajiBatchProsesKalkulasiService.java`](../src/main/java/id/perumdamts/kepegawaian/services/penggajian/gajiBatchMasterProses/GajiBatchProsesKalkulasiService.java)
- [`GajiBatchProsesReferenceResolver.java`](../src/main/java/id/perumdamts/kepegawaian/services/penggajian/gajiBatchMasterProses/GajiBatchProsesReferenceResolver.java)
- [`GajiBatchMasterProses.java`](../src/main/java/id/perumdamts/kepegawaian/entities/penggajian/GajiBatchMasterProses.java)
- [`application.yml`](../src/main/resources/application.yml)
- `docs/adr/0024-gajibatchroot-kafka-diisolasi-ke-eventpublisher.md`
- `docs/penggajian-proses-gaji-claim-order.md`

## Claim Order

### Issue 1 — Infrastructure [#kepegawaian-grfn] (REVISED — No Schema Change)

> ✅ TIDAK ada Flyway migration, TIDAK ada ALTER TABLE

- [x] Tambah `@EnableCaching` di `KepegawaianApplication.java` atau `CacheConfig.java`
- [x] Update `application.yml`:
  - [x] Tambah `&rewriteBatchedStatements=true` ke JDBC URL
  - [x] Tambah `hibernate.jdbc.batch_size: 100`
  - [x] Tambah `hibernate.order_updates: true`
  - [x] Update `hikari.maximum-pool-size: ${DB_POOL_SIZE:20}`
- [x] `./gradlew clean compileJava` — zero error
- [x] `bd close kepegawaian-grfn`

### Issue 2 — Preload Core + Redis Cache [#kepegawaian-ne43]

Depends on: kepegawaian-grfn

- [x] Buat package `preload/` di `services/penggajian/gajiBatchMasterProses/`
- [x] Buat `GajiPreloadContext.java` — 13 fields + inner keys + helper resolvers
- [x] Buat `HitungPegawaiResult.java` — record (master, prosesList, error)
- [x] Buat `GajiPreloadService.java`:
  - [x] @Cacheable methods untuk Kategori A (komponen, tunjangan, parameter, ptkp, potongan-tkk)
  - [x] Live fetch methods untuk Kategori B (askes, rumdin, sumPotTkk, kpi, sp3)
  - [x] Entry point preload(batchId, periode, masters)
- [x] Tambah `@CacheEvict` di 5 service write:
  - [x] GajiKomponenCommandService
  - [x] GajiTunjanganCommandService
  - [x] GajiParameterSettingCommandService
  - [x] GajiPendapatanNonPajakCommandService
  - [x] GajiPotonganTkkCommandService
- [x] Tambah `findAllPegawaiIdsWithActiveSp3In` di RiwayatSpRepository
- [x] `./gradlew clean compileJava` — zero error
- [x] `bd close kepegawaian-ne43`

### Issue 3 — Engine Refactor + JdbcTemplate Batch [#kepegawaian-b01i]

Depends on: kepegawaian-ne43

- [x] Buat `GajiBatchMasterProsesJdbcRepository.java` (JdbcTemplate batchInsert)
- [x] Refactor `GajiBatchProsesKalkulasiService.java`:
  - [x] Hapus @Transactional + repo injections
  - [x] Return HitungPegawaiResult
  - [x] Semua lookup via ctx.resolveXxx()
- [x] Hapus `GajiBatchProsesReferenceResolver.java`
- [x] Refactor `GajiBatchProsesCommandService.java`:
  - [x] StopWatch timing
  - [x] Preload step (preloadService.preload(...))
  - [x] Collect HitungPegawaiResult dari futures
  - [x] JdbcTemplate.batchInsert(proses) + saveAll(masters)
  - [x] Status: WAIT_VERIFICATION_PHASE_1 atau FAILED
  - [x] setNotes() summary JSON + log timing
- [x] `./gradlew clean compileJava` — zero error
- [x] `./gradlew test` — all green
- [x] `bd close kepegawaian-b01i`

## Urutan Deploy ke Production

1. [ ] **Tidak perlu backup khusus** — TIDAK ada schema change
2. [ ] Deploy JAR baru
3. [ ] Jalankan 1 batch test di staging — cek log `[GAJI] Batch xxx selesai:`
4. [ ] Cek `GajiBatchRoot.notes` — ada summary JSON
5. [ ] Cek status batch jika ada error pegawai — harus FAILED
6. [ ] Update tarif tunjangan via API, jalankan batch lagi — nilai terbaru (cache evicted)

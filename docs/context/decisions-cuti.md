# Context — Keputusan Rewrite: Modul Cuti

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat mengerjakan rewrite modul `cuti/`. Untuk glossary istilah, baca juga [`language-cuti.md`](./language-cuti.md).

---

## Keputusan Rewrite Modul Cuti

- **Tiga aggregate, multi-command per akar**: modul cuti dipecah jadi **tiga aggregate** — **Jenis Cuti**, **Kuota Cuti**, dan **Cuti Pegawai** (akar). `CutiApproval`, `CutiApprovalChain`, dan `CutiKlaimDetail` **bukan aggregate terpisah** melainkan **child entity** di bawah Cuti Pegawai. **Sisi tulis Cuti Pegawai dipecah per-operasi**: `PengajuanCutiCommand` (alur Pengajuan), `KlaimCutiCommand` (alur Klaim), `ApprovalCutiCommand` (proses approve/reject/return), dan generator rantai approval sebagai helper internal.

- **Sisi baca → JOOQ penuh + mini-projection** (ikut ADR-0001): `CutiPegawaiJooqRepository` (index pengajuan + inbox approval), `CutiKuotaJooqRepository`, `CutiJenisJooqRepository`. Tiap `*Response` **tetap nested** demi kompat FE, tapi tiap objek nested = **mini-projection** `row(id+label)`. **Inbox approval**: `CutiApprovalChainCustomRepositoryImpl` (CriteriaBuilder) **dibuang**, diganti query JOOQ `GROUP BY ref_cuti_id` + `max(read_write_status)`. **Bug diperbaiki saat port**: filter `tahun` di `CutiPengajuanRequest` salah pakai `MONTH(tanggal_mulai)`, dikoreksi jadi `YEAR(tanggal_mulai)`.

- **ID konfigurasi cuti → `CutiProperties` typed bean** (`@ConfigurationProperties(prefix="app.cuti")`): legacy menyebar ID hardcoded lewat `@Value`/`DefConfig` di 4+ kelas. Satu bean `CutiProperties` dengan grup `jenisCuti` (tahunan/besar/ibadah), `jabatan` (supervisorSdm/managerSdm/direkturUtama/direkturUmum), `level` (staff/supervisor/manager/dst.). **Field jenis-cuti dipindah keluar dari `DefConfig`** — `DefConfig` menyisakan `PROTECTED_KARTU_IDENTITAS_ID` saja.

- **Generator rantai approval → unifikasi data-driven + satu pointer-init** (helper internal di bawah `PengajuanCutiCommand`). Tiga metode hampir duplikat legacy (`levelStafList`/`levelSupervisorList`/`levelManagerList`) diringkas jadi **satu rutin `initPointer(slots)`** dengan daftar slot sebagai data. **Bug latent diperbaiki**: slot statis (SDM/direksi) kini dicek `existsByJabatanId` (sama seperti slot dinamis) — jabatan SDM kosong tak lagi memblokir rantai. Rantai **klaim** tetap terpisah (hanya `supervisorSdm`, langsung `WRITE`+`PENDING`).

- **Alokasi kuota & date-bucketing → satu allocator murni → `KuotaAllocation` record immutable**. Enam metode entry legacy diringkas jadi **fungsi alokasi murni** `(pegawai, tglMulai, tglSelesai, jenis, totalHariKerja) → KuotaAllocation` (record: kuotaAwal/akhir + 6 field riwayat); sumber kuota di-inject via port (unit-test tanpa DB). **Bug diperbaiki saat port** (`saveCutiNonTahunan`): `setKuotaAwal(...)` dipanggil dobel & cabang `else` tak pernah set `kuotaAkhir`.

- **State-machine approval → restrukturisasi per ADR-0021 + helper transisi murni**. Legacy `CutiApprovalServiceImpl.savePengajuan`/`saveKlaim` membungkus seluruh body dalam `try/catch → SavedStatus.FAILED` (anti-pola: exception tertangkap di dalam → **tx tak rollback**). Keputusan: metode transisi internal **melempar bebas** (tak swallow); hanya **satu entry publik `@Transactional`** di `ApprovalCutiCommand` yang membungkus hasil ke `SavedStatus`. Ekstrak advance/retreat pointer jadi **helper transisi murni**. **Buang `save()` redundan** (4× manual di `doSaveAcceptReject`) — andalkan dirty-checking dalam tx. CSRF idempotency tetap, tapi jadi **guard di entry** + rename `validateToken`→`isTokenAlreadyUsed`.

- **Idempotency token (CSRF single-use Redis) → guard seragam di semua entry + `DUPLICATE` konsisten**. Guard diaktifkan di **semua** command tulis cuti (termasuk `klaim()` create yang dikomentari di legacy). Kembalikan `ESaveStatus.DUPLICATE` konsisten (bukan throw). Rename helper jadi `isTokenAlreadyUsed`/`consumeToken`. Pertahankan TTL 5 menit & mekanisme Redis; token tetap di **request body**.

- **Perbaiki target `@SQLDelete` `CutiApproval` ke `cuti_approval`** dan **pertahankan `@Audited`+soft-delete** — `CutiApproval` adalah jejak audit keputusan yang paling butuh histori. `CutiApprovalChain` & `CutiKlaimDetail` tetap child murni.

- **Pembatalan vs `delete()` → buang stub, hanya `cancel()` (`→ CANCELED`)**. Legacy `delete(id)` adalah **stub `return false`** (dead code). Dihapus; satu-satunya jalur "hapus" adalah `pembatalan`, di-rename `cancel()`.

- **Deduksi kuota saat approval (`CutiKuotaUpdateByCutiService`) → port 1:1, bug `now()` SENGAJA dipertahankan**. Klasifikasi periode berbasis `LocalDate.now()` di sisi deduksi: bug lintas-tahun dipertahankan (parity), dilacak sebagai **beads issue terpisah**. ⚠️ Buat issue `bd` untuk bug `LocalDate.now()` deduksi kuota lintas-tahun sebelum/selagi porting service ini.

- **Aggregate Jenis & Kuota → Command+Query standar, Excel di sisi Command**. **CutiJenis**: flat CRUD (tree entity tapi service flat — response cuma bawa `parent` mini). **CutiKuota**: CRUD + `importData` (POI HSSF/XSSF) + `exportTemplate` (POI SXSSF) + `findByPegawai`. Import & template-build tetap di sisi Command. **Konsolidasi DTO Jenis**: **satu `CutiJenisMiniResponse {id,nama}`** dipakai di mana pun; **buang `JenisCutiMiniResponse` & `JenisCutiResponse`**.

- **Layanan validasi pengajuan → validator kolaborator khusus, `existsBy` eksplisit, aturan minimal jadi fungsi murni**. Tiga perbaikan saat port: (1) **buang Specification-on-DTO** → query derived eksplisit; (2) **`validateMinimalCuti` jadi fungsi murni statik** (`MinimalCutiRule.check(totalHariKerja, totalSisaKuota)`); (3) **inject `CutiProperties`** ganti `DefConfig`.

- **Layout controller — pertahankan kontrak URL persis, 4 controller, buang `CutiController` kosong**. Tiap controller inject `*Query` + `*Command` yang relevan. `CutiPengajuanController` boleh inject ≥2 command (FE URL-prefix melayani >1 operasi aggregate). **Inbox tetap di `/cuti/pengajuan/approval`** tapi diarahkan ke `CutiPegawaiQuery`. **Guard tanggal boundary tetap di controller** (`@Valid`+`Errors`+`ErrorResult.build`).

- **Kalkulasi hari kerja → satu fungsi murni, perbaiki bug double-subtract**. Legacy `DateHelper.countWeekdaysBetween` − `hariLiburRepository.countBy...` **terduplikasi di 3 titik** + bug: hari libur jatuh Sabtu/Minggu tetap dikurangi → undercount. Keputusan: **ekstrak satu fungsi murni** `WorkdayCalculator.count(tglMulai, tglSelesai, Set<LocalDate> libur)` dengan logika `getWorkingDays` yang benar (bukan-weekend DAN bukan-libur), perbaiki bug double-subtract, hapus duplikasi.

- **Strategi mapper → Pola B (kelas `*Mapper`) konsisten rewrite**. Legacy cuti pakai Pola A (`Response.from(entity)` yang lazy-load). Rewrite memakai Pola B: `mapper/cuti/<aggregate>/CutiJenisMapper` (toEntity/updateEntity), response baca dirakit `CutiPegawaiJooqMapper`/`CutiKuotaJooqMapper`/`CutiJenisJooqMapper`. **`refCuti`** dirakit via **JOOQ self-join** ke `cuti_pegawai` (bukan `entity.getRefCuti()` lazy-load).

- **Alokasi kuota sisi klaim (`CutiApproveKlaimCutiService`) → port 1:1**. Klaim **bukan** pemakaian ulang allocator reservasi — semantik berbeda (settle vs reservasi). Dispatch periode **5-cara** kini triplikat, dibiarkan per stance parity. **Tiga bug klaim dipertahankan, masing-masing dilacak beads issue terpisah**:
  1. `forNextYear:22` pakai `getTanggalMulai().getYear() − 1` (asimetri vs `overlappingYear:29`).
  2. `between1JanAnd30Jun:46` pakai `LocalDate.now()` — outcome approval tergantung kapan tombol diklik.
  3. `saveKlaim:222` bandingkan `picSaatIni.equals(approver.getJabatan())` pada referensi entity (rapuh di bawah proxy Hibernate); `savePengajuan` bandingkan via `.getId()`.
  Catatan: exception-swallowing `saveKlaim` (`try{…}catch(Exception){FAILED}`) **bukan** termasuk yang dipertahankan — sudah masuk lingkup rework approval-Command (entry `@Transactional` tunggal).

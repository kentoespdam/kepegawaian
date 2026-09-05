# Context — Modul Migrasi Data (Legacy SmartOffice ke Kepegawaian Baru)

Bagian dari [CONTEXT-MAP.md](../../CONTEXT-MAP.md). Baca file ini saat merancang atau mengeksekusi migrasi data dari database legacy `smartoffice` ke `kepegawaian_dev_new`.

## Glossary

**Migration Batch Runner**:
Aplikasi/runner modular mandiri bertipe Command Line Interface (CLI) yang mengeksekusi ekstraksi, transformasi, validasi integritas, dan pemuatan data per domain secara bertahap.
_Avoid_: Sync daemon, CDC listener, real-time replicator.

**Idempotent Migration**:
Sifat proses migrasi yang menjamin pemanggilan berulang kali dengan input yang sama akan menghasilkan state akhir database yang konsisten tanpa menduplikasi data (melalui pengecekan hash atau mekanisme upsert/skip).
_Avoid_: Direct dump & reload, destructive overwrite.

**Migration ID Map (`migration_id_map`)**:
Tabel penampung state relasi antara ID entitas di database legacy (`smartoffice`) dengan ID yang dihasilkan di database baru (`kepegawaian_dev_new`), dilengkapi record hash dan status migrasi untuk resolusi foreign key.
_Avoid_: In-memory dictionary, hardcoded ID translation.

**Staged Execution (Migrasi Bertahap)**:
Urutan eksekusi migrasi yang mengikuti rantai dependensi referensial antar domain: `master` → `profil` → `pegawai` → `kepegawaian/SK` → `cuti` → `penggajian`.

**Standalone Microapp**:
Aplikasi migrasi yang terpisah sepenuhnya (decoupled) dari aplikasi utama `kepegawaian`, memiliki lifecycle dan konfigurasi mandiri sehingga tidak membebani binary maupun runtime production.
_Avoid_: Embedded migration profile, in-app admin controller.

**Single-Use Cutover**:
Karakteristik eksekusi di lingkungan staging/production yang hanya dijalankan satu kali saat transisi sistem, namun didesain aman dijalankan berulang kali (re-entrant) di lingkungan development.

**Migration CLI (`tools/migration/`)**:
Workspace modular berbasis Python 3 mandiri di direktori `tools/migration/` yang memuat logika ekstraksi data legacy, sanitasi/transformasi, pelacakan mapping ID, dan pemuatan data terverifikasi ke database baru.
_Avoid_: In-tree Java batch job, ad-hoc one-liner script.

**Synthetic NIK Fallback**:
Mekanisme otomatis pengisian kunci primer `biodata.nik` menggunakan NIPAM pegawai (`employee.emp_code`) ketika NIK KTP pada data profil legacy kosong atau tidak valid, memastikan kelengkapan relasi seluruh data personal turunan tanpa data hilang.
_Avoid_: Random dummy NIK, record dropping.

**Mutasi Unit Kerja vs Jabatan**:
Aturan resolusi klasifikasi `EJenisMutasi` pada migrasi `emp_work_history`: jika hanya unit kerja berubah menjadi `MUTASI_LOKER`, jika hanya jabatan berubah menjadi `MUTASI_JABATAN`, dan jika keduanya berubah diprioritaskan sebagai `MUTASI_LOKER` (sesuai preferensi domain HR bahwa mutasi identik dengan pindah unit kerja) dengan tetap merekam snapshot organisasi dan jabatan baru dalam satu record yang terhubung ke `riwayat_sk` via nomor SK.
_Avoid_: Split mutation records, hardcoded generic mutation type.

**Component Translation Map & Passthrough**:
Strategi rekonsiliasi komponen payroll legacy (`salary_process_detail`) ke snapshot `gaji_batch_master_proses`: mengonversi simbol `ctype` (`+`/`-`) ke enum `jenis_gaji` (`PEMASUKAN`/`POTONGAN`), menstandarkan kode umum via kamus `COMPONENT_CODE_MAP`, dan meloloskan (passthrough) komponen ad-hoc/insidental dengan kode serta deskripsi aslinya tanpa Foreign Key ke master baru demi menjaga keseimbangan nominal bruto/netto 100% dengan arsip fisik lama.
_Avoid_: Strict foreign key validation, auto-generating obsolete master components, dropping ad-hoc deductions.

**Envers Baseline Revision Injection**:
Strategi pembuatan rekam jejak audit awal resmi pada proses migrasi data langsung via SQL di luar JPA container: runner migrasi mencatat satu record revisi global resmi di tabel `revinfo` pada setiap batch/tahapan migrasi dan menyisipkan snapshot data awal ke seluruh tabel `*_aud` terkait dengan `revtype = 0` (`ADD`), sehingga fitur perbandingan revisi dan revert (`RevInfoService`, `ProfileUpdateStrategy`, `AuditReaderFactory`) berjalan mulus tanpa risiko `RevisionDoesNotExistException` atau riwayat audit kosong.
_Avoid_: Empty audit history, skipping audit tables during batch migration, JPA container overhead for migration.

**Two-Phase File Migration**:
Strategi pemisahan migrasi data lampiran berkas fisik berukuran besar (~11 GB) menjadi dua tahap independen: Fase 1 mengeksekusi ETL metadata ke tabel baru (`lampiran_sk`, `lampiran_profil`, dll.) dan mencatat pemetaan path ke manifes lokal; Fase 2 menjalankan worker penyalinan fisik (`sync-files`) secara terpisah, *multithreaded*, dan *resumable*, sehingga migrasi database inti tidak terhambat I/O disk.
_Avoid_: In-band synchronous file copy, monolithic migration, blocking DB transaction for disk I/O.

**File Sync Manifest**:
Database lokal ringan (SQLite `file_sync_manifest.sqlite` atau CSV) yang dihasilkan pada Fase 1 migrasi untuk mencatat pemetaan path file legacy (`attachments/<YYYYMM>/<file_name>`), path target baru (`<JENIS>/<refId>/<UUID_hex_32>`), checksum/ukuran berkas, serta status verifikasi fisik untuk dikonsumsi oleh worker penyalinan file pada Fase 2.
_Avoid_: In-memory queue, ad-hoc file scanning, writing temporary sync state directly to target operational database.

**Appwrite Auth Provisioning**:
Proses pembuatan akun pengguna terautomasi di Appwrite Server (`/v1/users`) pasca-migrasi database bagi pegawai aktif (`status_kerja = 1` atau `2`) menggunakan `userId` setara `pegawai.id`, email dinas (`<nipam>@perumdamts.com`), default password, dan `prefs.roles = ["USER"]` tanpa menimpa password akun yang telah ada (idempoten).
_Avoid_: Manual admin console account creation, self-registration, credential overwriting on existing accounts.

**Lifecycle Account Deactivation**:
Mekanisme penguncian atau penonaktifan akun (`status: false` / blocked) di Appwrite Auth untuk pegawai yang berstatus pensiun atau berhenti (`status_kerja = 3`) sesuai prinsip ADR-0039, memastikan eks-pegawai tidak dapat lagi login ke sistem tanpa menghapus jejak audit identitas.
_Avoid_: Hard delete Appwrite user, leaving retired employee accounts active.

**Leave Quota Snapshot Reconciliation**:
Strategi rekonsiliasi kuota cuti tahunan dengan memperlakukan tabel `smartoffice.cuti_kuota` sebagai snapshot kebenaran mutlak (single source of truth) 1:1 ke tabel `kepegawaian_dev_new.cuti_kuota` (meng-upsert sisa kuota dan kuota terpakai tahun berjalan 2026 serta menyisipkan entri kuota pegawai baru) guna menghindari diskrepansi saldo sisa cuti akibat siklus unik cuti 1 Juli – 30 Juni, carry-over saldo, dan penyesuaian khusus oleh HRD.
_Avoid_: Recalculating leave balance from raw transaction dates, overwriting HR adjustments with generic formulas, full wipe and reload.

**Leave Transaction Delta Ingestion**:
Mekanisme ekstraksi dan pemuatan selektif delta transaksi cuti operasional terbaru (gap transaksi 2025–2026) dari famili tabel `smartoffice.cuti_*` (`cuti_pegawai`, `cuti_pegawai_approval`, `cuti_pegawai_approval_chain`, dan `cuti_pegawai_detail`) dengan pemetaan referensial foreign key yang valid ke `pegawai_id` dan `cuti_jenis_id`, sekaligus mengabaikan skema usang 2016 (`emp_leave`, `emp_leave_history`) dan dead schema `riwayat_cuti`.
_Avoid_: Migrating obsolete 2016 leave tables, populating dead schema `riwayat_cuti`, blind insert without foreign key resolution.



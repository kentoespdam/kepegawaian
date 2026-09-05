# Microapp Migrasi Data Kepegawaian

Microapp mandiri berbasis Python 3 untuk orkestrasi, ekstraksi, transformasi, pemuatan data (*ETL*), sinkronisasi berkas fisik lampiran, provisioning autentikasi, serta audit kualitas data dari basis data legacy **Smartoffice** (`smartoffice`) ke sistem baru **Kepegawaian** (`kepegawaian_dev_new`).

---

## 1. Arsitektur & Landasan Desain (ADR)

Microapp ini dirancang dan diimplementasikan secara ketat mematuhi seluruh Architecture Decision Record (ADR):

1. **[ADR-0049: Delta Matching Riwayat Mutasi](../../docs/adr/0049-delta-matching-riwayat-mutasi-lintas-posisi.md)**
   Rekonsiliasi riwayat jabatan/organisasi dari log flat `emp_work_history` menjadi event mutasi berstatus dengan deteksi delta perubahan unit kerja (`MUTASI_LOKER`) vs jabatan (`MUTASI_JABATAN`).
2. **[ADR-0050: Rekonsiliasi Gap Pengkodean Komponen Gaji](../../docs/adr/0050-rekonsiliasi-gap-komponen-gaji-via-translation-map.md)**
   Penyelarasan kode komponen payroll historis via `COMPONENT_CODE_MAP` dan normalisasi `ctype` (`+`/`-` $\to$ `PEMASUKAN`/`POTONGAN`) dengan mekanisme passthrough aman berpresisi toleransi Rp 0,- (*Zero Deviation*).
3. **[ADR-0051: Injeksi Baseline Revision Hibernate Envers](../../docs/adr/0051-injeksi-baseline-revision-hibernate-envers.md)**
   Pencatatan global revision ke tabel `revinfo` dan penulisan baris audit awal (`revtype = 0` / `REVTYPE_ADD`) ke tabel `*_aud` untuk seluruh entitas master dan transaksional.
4. **[ADR-0052: Two-Phase File Attachment Migration](../../docs/adr/0052-two-phase-file-attachment-migration.md)**
   Migrasi berkas dalam dua fase:
   - **Fase 1 (Stage 6)**: Perekaman metadata ke tabel `lampiran_sk`, `lampiran_profil`, dan antrean SQLite `file_sync_manifest.sqlite`.
   - **Fase 2 (Worker)**: Penyalinan berkas fisik multi-threaded (`ThreadPoolExecutor`) dengan hashing UUIDv4 32-hex, verifikasi checksum SHA-256, dan pelacakan status (`SYNCED`, `FAILED`, `SKIPPED`).
5. **[ADR-0053: Appwrite Auth Provisioning](../../docs/adr/0053-appwrite-auth-provisioning-kepegawaian.md)**
   Sinkronisasi akun pengguna ke Appwrite Auth via REST API: pembuatan akun aktif (`status_kerja IN (1, 2)`) dengan password default dan pemblokiran akun purna tugas (`status_kerja = 3`).
6. **[ADR-0054: Rekonsiliasi Snapshot Kuota Cuti & Delta Transaksi](../../docs/adr/0054-rekonsiliasi-snapshot-kuota-cuti-dan-delta-transaksi.md)**
   Rekonsiliasi snapshot 1:1 kuota cuti tahun berjalan 2026, baseline kuota untuk 31 pegawai baru, serta ingesti delta transaksi `cuti_pegawai` gap tahun 2025–2026.

---

## 2. Struktur Direktori Microapp

```text
tools/migration/
├── __init__.py
├── config.py                 # Manajemen konfigurasi environment (.env)
├── requirements.txt          # Dependensi Python
├── run.py                    # CLI Entrypoint utama microapp
├── README.md                 # Dokumentasi panduan operasional
├── core/
│   ├── __init__.py
│   ├── db.py                 # Koneksi DB pymysql, pool, & batch helpers
│   ├── envers.py             # Injeksi baseline revision Hibernate Envers
│   ├── manifest.py           # SQLite ManifestManager untuk sinkronisasi berkas
│   └── state.py              # Pelacakan pemetaan ID (migration_id_map)
├── stages/
│   ├── __init__.py           # Ekspor seluruh fungsi run_stage0 s/d run_stage7
│   ├── common.py             # Kontainer StageResult & kelas data
│   ├── stage0_preflight.py   # Verifikasi koneksi DB & API Appwrite
│   ├── stage1_master.py      # Organisasi, Jabatan, Golongan, CutiJenis
│   ├── stage2_pegawai.py     # Pegawai, Biodata, & Profil Anak
│   ├── stage3_kepegawaian.py # Riwayat SK, Mutasi, SP, Kontrak
│   ├── stage4_cuti.py        # Kuota Cuti 2026 & Delta Transaksi 2025-2026
│   ├── stage5_penggajian.py  # Penggajian Historis (Root, Master, Proses)
│   ├── stage6_lampiran.py    # Metadata Lampiran & Antrean Manifest SQLite
│   └── stage7_auth.py        # Appwrite Auth User Provisioning
├── workers/
│   ├── __init__.py
│   └── file_sync_worker.py   # Worker fisik penyalinan berkas lampiran (Fase 2)
├── audit/
│   ├── __init__.py
│   ├── verify_integrity.py   # Audit zero-orphan, integritas FK, & Envers
│   └── reconcile_payroll.py  # Rekonsiliasi nominal gaji legacy vs target
└── tests/
    ├── test_stages.py        # Unit test logika stage migrasi
    └── test_workers_and_audit.py # Unit test worker berkas & modul audit
```

---

## 3. Prasyarat & Instalasi

Pastikan Python versi 3.10 atau lebih baru telah terpasang pada lingkungan eksekusi:

```bash
python3 --version
```

### 3.1 Instalasi Dependensi

Disarankan menggunakan virtual environment:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r tools/migration/requirements.txt
```

Isi pustaka dependensi (`tools/migration/requirements.txt`):
- `pymysql`: Driver MariaDB/MySQL berkinerja tinggi.
- `cryptography`: Enkripsi otentikasi koneksi basis data.
- `requests`: Klien HTTP REST API Appwrite.
- `rich`: Rendering antarmuka terminal, tabel, dan banner.
- `tqdm`: Indikator kemajuan proses (*progress bar*).

---

## 4. Konfigurasi Environment (`.env`)

Microapp secara otomatis memuat variabel lingkungan dari file `.env` di root proyek atau direktori `tools/migration/.env`.

Contoh konfigurasi `.env`:

```ini
# Database Legacy (Smartoffice)
LEGACY_DB_HOST=192.168.230.84
LEGACY_DB_PORT=3307
LEGACY_DB_USER=dev
LEGACY_DB_PASSWORD=password
LEGACY_DB_SCHEMA=smartoffice

# Database Target (Kepegawaian Baru)
TARGET_DB_HOST=192.168.230.84
TARGET_DB_PORT=3307
TARGET_DB_USER=dev
TARGET_DB_PASSWORD=password
TARGET_DB_SCHEMA=kepegawaian_dev_new

# Appwrite Server
APPWRITE_ENDPOINT=http://192.168.230.254:82/v1
APPWRITE_PROJECT_ID=65cd62cc3385d8434a53
APPWRITE_API_KEY=061b4abb7743ecc570cc693483b36bc0f50616b2631c5f7cec3825e15cd196d7...

# Berkas Lampiran & Manifest
LEGACY_ATTACHMENTS_PATH=/home/dev/php/smartoffice/server/attachments
TARGET_ATTACHMENTS_PATH=./attachments
MANIFEST_DB_PATH=file_sync_manifest.sqlite
```

---

## 5. Panduan Penggunaan CLI (`run.py`)

Microapp menyediakan antarmuka baris perintah (*CLI*) terpadu melalui `tools/migration/run.py`.

### 5.1 Menampilkan Bantuan

```bash
python3 tools/migration/run.py --help
```

### 5.2 Menjalankan Seluruh Pipeline (`run-all`)

Menjalankan Stage 0 hingga Stage 7 secara berurutan:

```bash
# Eksekusi standar (payroll 12 bulan terakhir)
python3 tools/migration/run.py run-all

# Eksekusi simulasi tanpa mutasi basis data (dry-run)
python3 tools/migration/run.py run-all --dry-run

# Eksekusi penuh dengan seluruh tahun arsip penggajian
python3 tools/migration/run.py run-all --payroll-all

# Eksekusi bersih dari awal (membersihkan migration_id_map & manifest)
python3 tools/migration/run.py run-all --fresh

# Eksekusi mengabaikan kegagalan preflight Stage 0
python3 tools/migration/run.py run-all --force
```

### 5.3 Menjalankan Stage Spesifik (`stage`)

Menjalankan satu stage migrasi secara terisolasi:

```bash
# Stage 0: Preflight check koneksi dan perizinan
python3 tools/migration/run.py stage --name stage0

# Stage 1: Sinkronisasi master referensi (organisasi, jabatan, golongan, cuti_jenis)
python3 tools/migration/run.py stage --name stage1

# Stage 2: Migrasi data biodata pegawai dan profil keluarga/pendidikan/pelatihan
python3 tools/migration/run.py stage --name stage2

# Stage 3: Migrasi riwayat SK, mutasi (delta matching), SP, dan kontrak
python3 tools/migration/run.py stage --name stage3

# Stage 4: Rekonsiliasi kuota cuti 2026 dan delta transaksi cuti
python3 tools/migration/run.py stage --name stage4_cuti

# Stage 5: Migrasi riwayat penggajian (batch root, master, dan komponen proses)
python3 tools/migration/run.py stage --name stage5_penggajian --payroll-all

# Stage 6: Migrasi metadata lampiran berkas dan inisialisasi SQLite manifest
python3 tools/migration/run.py stage --name stage6_lampiran

# Stage 7: Provisioning akun pengguna ke Appwrite Auth
python3 tools/migration/run.py stage --name stage7_auth
```

### 5.4 Sinkronisasi Fisik Berkas Lampiran (`sync-files`)

Menjalankan worker multi-threaded Fase 2 untuk menyalin file lampiran dari path legacy ke target:

```bash
# Menjalankan worker dengan 4 thread paralel
python3 tools/migration/run.py sync-files

# Menjalankan worker dengan 8 thread dan sumber kustom
python3 tools/migration/run.py sync-files --source /path/to/legacy/attachments --workers 8

# Mencoba ulang berkas yang sebelumnya berstatus FAILED
python3 tools/migration/run.py sync-files --retry-failed

# Simulasi tanpa menyalin berkas fisik
python3 tools/migration/run.py sync-files --dry-run

# Melewatkan verifikasi checksum SHA-256 demi kecepatan maksimal
python3 tools/migration/run.py sync-files --no-checksum
```

### 5.5 Provisioning Autentikasi Mandiri (`sync-auth`)

```bash
# Menjalankan provisioning akun ke Appwrite
python3 tools/migration/run.py sync-auth

# Simulasi akun tanpa memanggil API mutasi Appwrite
python3 tools/migration/run.py sync-auth --dry-run
```

### 5.6 Audit Kualitas Data & Rekonsiliasi (`audit`)

Menjalankan verifikasi integritas referensial dan rekonsiliasi nominal penggajian:

```bash
# Menjalankan seluruh rangkaian audit
python3 tools/migration/run.py audit

# Menjalankan audit dengan toleransi deviasi ketat (gagal jika ada orphan)
python3 tools/migration/run.py audit --strict

# Menjalankan hanya audit rekonsiliasi matematika payroll
python3 tools/migration/run.py audit --payroll-only

# Menjalankan hanya audit zero-orphan dan kepatuhan Envers
python3 tools/migration/run.py audit --integrity-only

# Menyimpan hasil audit ke lokasi laporan kustom
python3 tools/migration/run.py audit \
    --export-integrity laporan_integritas.json \
    --export-payroll laporan_gaji.json
```

---

## 6. Berkas Artefak Output

| Nama Berkas | Format | Deskripsi |
|---|---|---|
| `file_sync_manifest.sqlite` | SQLite3 Database | Basis data antrean dan status sinkronisasi fisik berkas lampiran (Fase 1 & Fase 2). |
| `audit_integrity_report.json` | JSON | Laporan terstruktur hasil pemeriksaan Zero Orphan, kepatuhan Hibernate Envers, dan ringkasan kuantitas baris tabel. |
| `reconcile_payroll_report.json` | JSON | Laporan komparasi nominal pendapatan kotor, potongan, dan take-home pay per periode/batch penggajian. |
| `audit_auth_sync.csv` | CSV | Catatan audit sinkronisasi akun Appwrite: NIPAM, email, status kerja, aksi (`CREATE`/`BLOCKED`/`SKIPPED`), dan respon API. |

---

## 7. Menjalankan Pengujian Mandiri (*Unit Tests*)

Microapp dilengkapi dengan cakupan pengujian unit mandiri tanpa membutuhkan dependensi aktif ke database fisik:

```bash
python3 -m unittest discover -s tools/migration/tests -v
```

# Analisis Project Kepegawaian

## Overview

Sistem manajemen kepegawaian (HRM) untuk PERUMDAMTS berbasis Spring Boot 3.5.6 (Java 21, Gradle). Aplikasi REST API yang mengelola data pegawai, profil, cuti, penggajian, dan master data organisasi.

## Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| Framework | Spring Boot 3.5.6 |
| Bahasa | Java 21 |
| Build Tool | Gradle |
| Database | MariaDB |
| ORM | JPA/Hibernate 6.6.29 + Spring Data Envers |
| Autentikasi | JWT via Appwrite |
| Cache | Redis |
| Message Queue | Kafka (topic: penggajian) |
| API Docs | Springdoc OpenAPI 3 |
| Excel | Apache POI |
| HTTP Client | Spring WebFlux (WebClient) |
| Code Gen | Lombok |

## Struktur Direktori

```
src/main/java/id/perumdamts/kepegawaian/
├── config/
│   ├── security/          # JWT filter, Appwrite integration, WebSecurity
│   ├── audit/             # JPA auditing (created_by, updated_by, timestamps)
│   ├── RedisConfig.java
│   ├── KafkaConfig.java
│   ├── WebClientConfig.java
│   ├── OpenApiConfig.java
│   └── ThreadPoolConfig.java
├── controllers/           # 68+ REST controllers
│   ├── auth/
│   ├── profil/
│   ├── pegawai/
│   ├── kepegawaian/
│   ├── cuti/
│   ├── penggajian/
│   ├── laporan/
│   ├── master/
│   └── system/
├── services/              # Business logic
├── repositories/          # Spring Data JPA repositories
├── entities/              # 86+ JPA entities
│   ├── commons/           # Enum & abstract classes
│   ├── profil/
│   ├── pegawai/
│   ├── cuti/
│   ├── kepegawaian/
│   ├── master/
│   └── penggajian/
├── dto/                   # Request/Response DTOs
│   └── commons/           # PageResult, ListResult, SavedResult, SingleResult, ErrorResult
├── helpers/               # DateHelper, RedisHelper, ExcelHelper
└── utils/
```

## Domain & Entity

### 1. Pegawai (Data Utama Pegawai)

Entity utama `Pegawai` dengan identifier unik **NIPAM**.

**Status Pegawai (`EStatusPegawai`):**
| Status | Keterangan |
|--------|-----------|
| KONTRAK | Pegawai kontrak |
| CAPEG | Calon pegawai |
| PEGAWAI | Pegawai tetap |
| HONORER | Pegawai honorer |
| NON_PEGAWAI | Non pegawai |

**Status Kerja (`EStatusKerja`):** Aktif / Tidak Aktif

### 2. Profil (Data Pribadi)

| Entity | Keterangan |
|--------|-----------|
| Biodata | Data pribadi (PK: NIK). Nama, gender, tanggal lahir, alamat, agama, golongan darah, status pernikahan |
| KartuIdentitas | Kartu identitas (KTP, Paspor, dll) |
| Pendidikan | Riwayat pendidikan |
| Pelatihan | Riwayat pelatihan |
| Keahlian | Keahlian/kompetensi |
| ProfilKeluarga | Data anggota keluarga |
| PengalamanKerja | Riwayat pengalaman kerja |
| LampiranProfil | Dokumen lampiran profil |

### 3. Master Data (Referensi)

| Entity | Keterangan |
|--------|-----------|
| Organisasi | Struktur organisasi (hierarki parent-child) |
| Jabatan | Jabatan/posisi (hierarki parent-child) |
| Golongan | Golongan/klasifikasi gaji |
| Grade | Grade detail dengan tukin/tunjangan |
| Level | Level jabatan: Direktur, Manager, Supervisor, Staff |
| Profesi | Profesi/spesialisasi |
| JenjangPendidikan | Jenjang pendidikan |
| JenisKeahlian | Jenis keahlian |
| JenisPelatihan | Jenis pelatihan |
| JenisSp | Jenis surat peringatan |
| Sanksi | Sanksi disipliner |
| AlatKerja | Alat kerja |
| Apd | Alat pelindung diri |
| AlasanBerhenti | Alasan berhenti/terminasi |
| RumahDinas | Rumah dinas |
| HariLibur | Hari libur |

### 4. Cuti (Manajemen Cuti)

| Entity | Keterangan |
|--------|-----------|
| CutiPegawai | Pengajuan cuti |
| CutiJenis | Jenis cuti (Tahunan, Besar, Ibadah) |
| CutiApprovalChain | Rantai approval multi-level |
| CutiApproval | Record approval per level |
| CutiKuota | Kuota cuti per pegawai |

**Alur Approval:**
```
Pengajuan → PENDING → [Approval Level 1] → [Approval Level 2] → ... → APPROVED/REJECTED
```

Status: `PENDING` → `APPROVED` / `REJECTED`

### 5. Kepegawaian (Administrasi Pegawai)

| Entity | Keterangan |
|--------|-----------|
| RiwayatSk | Riwayat Surat Keputusan |
| RiwayatSp | Riwayat Surat Peringatan |
| RiwayatMutasi | Riwayat mutasi/transfer |
| RiwayatKontrak | Riwayat kontrak kerja |
| RiwayatTerminasi | Riwayat terminasi/berhenti |
| LampiranSk | Lampiran dokumen SK |

### 6. Penggajian (Payroll)

15 entity terkait penggajian:

| Entity | Keterangan |
|--------|-----------|
| GajiProfil | Profil gaji pegawai |
| DasarGaji | Gaji dasar |
| DetailDasarGaji | Detail breakdown gaji |
| GajiKomponen | Komponen gaji |
| GajiTunjangan | Tunjangan |
| GajiPendapatanNonPajak | Pendapatan non-pajak |
| GajiPotonganTkk | Potongan TKK |
| GajiPhdp | Perhitungan PHDP (pajak) |
| GajiParameterSetting | Parameter konfigurasi gaji |
| GajiBatchRoot | Master batch penggajian |
| GajiBatchMaster | Detail batch |
| GajiBatchMasterProses | Status proses batch |

Penggajian menggunakan batch processing bulanan dan terintegrasi dengan external service via Kafka.

## API Design

### Endpoint Groups

| Prefix | Keterangan |
|--------|-----------|
| `/auth/**` | Autentikasi |
| `/profil/**` | Manajemen profil pegawai |
| `/pegawai/**` | Data pegawai |
| `/master/**` | Master data |
| `/kepegawaian/**` | Administrasi kepegawaian |
| `/penggajian/**` | Penggajian |
| `/cuti/**` | Manajemen cuti |
| `/laporan/kepegawaian/**` | Laporan |
| `/system/**` | Konfigurasi sistem |

### Pola Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/resource-path")
public class ResourceController {
    private final ResourceService service;

    @GetMapping                                          // Paginated list
    public ResponseEntity<?> index(@ParameterObject ResourceRequest request);

    @GetMapping("/list")                                 // Full list
    public ResponseEntity<?> list();

    @GetMapping("/{id}")                                 // Get by ID
    public ResponseEntity<?> findById(@PathVariable Long id);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping                                         // Create
    public ResponseEntity<?> save(@Valid @RequestBody ResourcePostRequest request, Errors errors);

    @PutMapping("/{id}")                                 // Update
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ResourcePutRequest request, Errors errors);

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")                              // Delete (soft)
    public ResponseEntity<?> deleteById(@PathVariable Long id);
}
```

### Format Response

```json
{
  "status": 200,
  "statusText": "OK",
  "data": { ... },
  "timestamp": "2026-03-20 12:30:45"
}
```

Response wrapper classes:
- `PageResult<T>` — response paginasi
- `ListResult<T>` — response list
- `SingleResult<T>` — response single item
- `SavedResult<T>` — response save/update
- `ErrorResult` — response error dengan detail validasi

## Autentikasi & Keamanan

### Alur JWT + Appwrite

```
Client → Bearer Token → JwtAuthFilter → JwtTokenService → Appwrite API (/account/jwt)
       → AppwriteUser (roles) → Spring Security Authentication
```

**Konfigurasi Security:**
- CORS: Disabled
- CSRF: Disabled
- Session: Stateless
- Form Login & HTTP Basic: Disabled

**Public Endpoints:** `/api-docs/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/auth/**`

**Development Mode:** Profile `development` tanpa token → inject hardcoded user dengan role ADMIN dan SYSTEM.

**Role-Based Access:** `@PreAuthorize("hasRole('ADMIN')")` pada endpoint mutasi data. Roles disimpan di Appwrite user prefs, di-prefix `ROLE_` untuk Spring Security.

## Pola & Konvensi

### Soft Delete
Semua entity menggunakan flag `is_deleted`. Data tidak pernah dihapus secara fisik dari database.

### Audit Trail
- Field otomatis: `created_at`, `created_by`, `updated_at`, `updated_by` via JPA `AuditAware`
- Spring Data Envers untuk full revision history pada entity yang di-audit

### Approval Workflow
Digunakan pada modul **cuti** dan **profil update** (`PegawaiProfilUpdate`):
- Multi-level approval chain
- Setiap level memiliki permission read/write
- Status tracking per level: PENDING → APPROVED/REJECTED

### Entity ID
- Mayoritas entity: `Long` auto-generated
- Pengecualian: `Biodata` menggunakan `NIK` (String) sebagai primary key

## Konfigurasi

### Environment Variables

```bash
# Server
SERVER_PORT=8080
PROFILE=development|production

# Database
DB_HOST, DB_PORT, DB_SCHEMA, DB_USER, DB_PASSWORD, DB_POOL_SIZE
DDL_AUTO=create|none

# Appwrite
APPWRITE_ENDPOINT, APPWRITE_PROJECT_ID, APPWRITE_API_KEY

# Kafka & Redis
KAFKA_BOOTSTRAP_SERVERS
REDIS_HOST, REDIS_PORT, REDIS_PASSWORD, REDIS_DATABASE

# External Services
PENGGAJIAN_URL, LAPORAN_KEPEGAWAIAN_URL

# Custom Properties
JENIS_CUTI_TAHUNAN=1, JENIS_CUTI_BESAR=2, JENIS_CUTI_IBADAH=4
JABATAN_DIREKTUR_UTAMA, JABATAN_MANAGER_SDM, ...
LEVEL_DIREKTUR_UTAMA, LEVEL_MANAGER, LEVEL_STAFF, ...
```

### Profiles
- **development** — DDL auto `create`, relaxed security, hardcoded admin user
- **production** — DDL auto `none`, full Appwrite auth, external service URLs

### Docker
File konfigurasi tersedia di `docker/` directory untuk kedua profile.

## Integrasi External

| Service | Protokol | Fungsi |
|---------|----------|--------|
| Appwrite | REST (WebClient) | Autentikasi JWT & user management |
| Kafka | Message Queue | Event publishing penggajian |
| Redis | Cache | Token caching & utility |
| Penggajian Service | REST (WebClient) | Kalkulasi gaji external |
| Laporan Service | REST (WebClient) | Reporting external |

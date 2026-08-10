# Graph Report - kepegawaian  (2026-08-10)

## Corpus Check
- 1231 files · ~373,342 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 14895 nodes · 43029 edges · 440 communities (389 shown, 51 thin omitted)
- Extraction: 81% EXTRACTED · 19% INFERRED · 0% AMBIGUOUS · INFERRED: 8158 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1900f510`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Core Entities & Pagination
- Many-to-Many & Base Entities
- List & Java Collections
- DTO Patterns & Builders
- Validation & Error Handling
- Relation Mappings & DSL
- Pegawai Join Queries
- Adapter & Config Mappers
- Penggajian Payroll Entities
- Kepegawaian SK & SP
- Enums & Constants
- Master References
- Profil Biodata & Pendidikan
- Cuti Leave Module
- Domain Context Docs
- Claim Order & ADRs
- PagedRequest
- PengalamanKerjaAudRecord
- PegawaiAudRecord
- .toQuery
- GajiBatchMasterRecord
- CutiPegawaiRecord
- RecordMapper
- KartuIdentitasAudRecord
- Cuti CQRS Rewrite — Claim Order & Checklists
- DasarGaji
- PendidikanAudRecord
- RiwayatSkRecord
- Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring
- PelatihanRecord
- BiodataRecord
- SanksiSpRecord
- RiwayatTerminasiRecord
- RevinfoPath
- KeahlianAudRecord
- LampiranSkAudRecord
- StatistikPegawaiRecord
- LampiranProfilRecord
- PegawaiPath
- VPegawaiRecord
- CutiKuotaAudRecord
- DasarGajiAudRecord
- KartuIdentitasRecord
- JabatanMiniResponse
- GajiPotonganTkkAudRecord
- CutiJenisAudRecord
- DetailDasarGajiAudRecord
- .getId
- DasarGajiRecord
- GajiPendapatanNonPajakAudRecord
- RiwayatCutiAudRecord
- CutiPegawai
- Organisasi
- RiwayatCutiRecord
- GajiParameterSettingAudRecord
- Jabatan
- GajiPendapatanNonPajakRecord
- Ringkasan Temuan
- Analisis Project Kepegawaian
- Biodata
- Tables
- RiwayatTerminasi
- GajiParameterSettingRecord
- GajiPhdpRecord
- GajiProfilAudRecord
- FlywaySchemaHistoryCopy1Record
- Master Record Refactor — Claim Order & Checklist
- RiwayatSp
- Grade
- GajiProfilRecord
- RiwayatKontrakRecord
- RiwayatSk
- Golongan
- Profil Record Refactor — Claim Order & Checklist
- SanksiSp
- GajiBatchMaster
- ProfilKeluarga
- KartuIdentitas
- RiwayatMutasi
- AlasanBerhentiRecord
- RumahDinasRecord
- JenisSpRecord
- ApdRecord
- GajiTunjangan
- Keahlian
- tables/Pelatihan.java
- GajiPotonganTkk
- JenisSp
- AlatKerjaRecord
- Pegawai Record Refactor — Claim Order & Checklist
- Claim Order — Adopsi Pattern Publication ke Modul Master
- GajiBatchRoot
- tables/GajiBatchRootLampiran.java
- tables/GajiBatchRootErrorLogs.java
- tables/PengalamanKerja.java
- Specification
- tables/CutiApprovalChain.java
- tables/GajiProfil.java
- tables/CutiKuota.java
- tables/CutiKlaimDetail.java
- tables/DetailDasarGaji.java
- Page
- JenisKitasRecord
- Level
- JenisPelatihanRecord
- JenisKeahlianRecord
- Decisions Cuti
- NotFoundException
- tables/JenisKeahlian.java
- tables/RumahDinas.java
- Profil CQRS — Pola Implementasi per Layer
- CutiApprovalAudRecord
- GajiPhdpAudRecord
- RiwayatMutasiAud.java
- JenjangPendidikanResponse
- CutiPegawaiAud.java
- RiwayatSpAud.java
- PelatihanAudRecord
- Profesi
- GajiKomponenAud.java
- RiwayatKontrakAudRecord
- Graph Report
- LampiranSkRecord
- UpdatableRecordImpl
- Organisasi
- KeahlianAud.java
- LampiranProfilAudRecord
- CutiApprovalRecord
- EJenisLampiranProfil
- Domain Docs
- ISSUE 2 — kepegawaian-buc (Phase B-D)
- Checklist Detail per Issue
- LANGKAH KERJA
- RiwayatMutasiAudRecord
- GajiBatchRootErrorLogsRecord
- Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists
- Organisasi Claim Order
- JwtAuthFilter
- Profil CQRS Cleanup — Claim Order & Checklists
- Kepegawaian — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring
- LANGKAH KERJA
- Claim Order — Drop CommonPageRequest → Rewrite CQRS/JOOQ 5 Modul Terakhir
- Checklist per Domain
- .getBiodata
- FileUploadUtilImpl
- ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ
- Claim Order — Reformat Column Order V1baseline.sql
- Profil Rewrite — Claim Order & Monitoring
- Master Rewrite — Claim Order & Monitoring
- ApiException
- PegawaiController
- Worktree
- Pegawai — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring
- Decisions — Modul Master (CQRS Cleanup)
- Checklist Implementasi
- OrganisasiQueryRepository.java
- GajiBatchMasterProsesRecord
- BiodataAudGolonganDarah
- BiodataGolonganDarah.java
- Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist
- Glossary
- 0008 Fk Via Getreference On Write
- PrefRole
- GajiKomponenAudJenisGaji
- PendidikanQueryService.java
- LampiranProfilController.java
- ADR-0017 — Claim Order & Monitoring
- Issue tracker: beads + GitHub
- JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset
- GajiBatchRoot
- Level
- DefaultSchema
- ProfesiController
- Coding Rules
- CONTEXT-MAP — Kepegawaian
- Claim Order — GajiBatchRootServiceImpl (Kafka)
- Context — Relasi Antar Domain
- 0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success")
- 0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail
- 0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null
- RiwayatSpAudRecord
- DetailDasarGajiCommandService.java
- DasarGajiQueryRepository.java
- GajiProfilResponse
- GajiPhdpResponse
- SavedStatus
- JenisKitasQueryRepository.java
- GolonganQueryRepository.java
- JenisSpQueryRepository.java
- RiwayatSpRecord
- PrefRole
- Context — Contoh Dialog & Ambiguitas Terflag
- Context — Modul Master (Data Referensi)
- APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone
- Flyway sebagai sumber kebenaran schema
- KepegawaianApplication
- RiwayatMutasiRecord
- KartuIdentitasSelects
- KenaikanBerkalaRequest
- DeletedResult
- .toEntity
- GajiBatchRootCommandService.java
- ProfilUpdateRecord
- KepegawaianApplicationTests.java
- Kepegawaian — Master Context
- Context — Modul Cuti (Pengajuan & Approval Cuti)
- Profil Rewrite Claim Order
- PatchSanksiJenisSpRequest
- HariLiburQueryRepository.java
- PegawaiTetap.java
- MutasiJabatan.java
- MutasiGolongan.java
- PerpanjanganKontrak.java
- KontrakToCapeg.java
- GajiSk.java
- AppwriteUser
- CutiJenisQueryRepository.java
- GajiBatchMasterProsesResponse
- CutiKuotaDeductionResult
- EApprovalCutiStatus
- ListResult
- RiwayatTerminasiAudRecord
- JenjangPendidikan
- GajiPendapatanNonPajakResponse
- .delete
- ErrorCode
- ProfileUpdate
- AuthServiceImplTest.java
- AppwriteClient
- RiwayatKeluarRecord
- AuditRevisionListener.java
- StatistikPegawai
- ProcessPotonganTkkImpl.java
- RiwayatKontrakController.java
- PageResult
- GajiKomponen
- GajiBatchRootLampiran
- .toString
- ConflictException
- ProfilKeluargaAudRecord
- HariLibur
- EStatusCuti
- EJenisTunjangan
- List
- EJenisSk
- JenisKontrakController.java
- RiwayatSkQuery
- CutiPegawaiAudRecord
- RedisTestApplication.java
- Todo
- Triage Labels
- GajiBatchPotonganTkkRecord
- PengalamanKerjaRecord
- JabatanQueryRepository.java
- BiodataPath
- KeahlianRecord
- GajiBatchMasterResponse
- GajiProfil
- JenisSp
- Pendidikan
- WebSecurity.java
- PelatihanQueryService.java
- SanksiQueryRepository.java
- .between1JanAnd30Jun
- GajiKomponenRecord
- Keys
- tables/GajiPendapatanNonPajak.java
- tables/GajiKomponen.java
- ProfilUpdateController.java
- LampiranRow
- JenjangPendidikanRecord
- GitNexus — Code Intelligence
- Keahlian
- Knowledge — kepegawaian (PERUMDAMTS)
- JenisKeahlianQueryRepository.java
- RumahDinasQueryRepository.java
- TableImpl
- tables/JenisPelatihan.java
- PegawaiQueryService
- JenisPelatihanQueryRepository.java
- CutiJenis
- GradeRecord
- BiodataAud.java
- DasarGajiAud.java
- PegawaiAud.java
- ProfilKeluargaAud.java
- PengalamanKerjaCommandService.java
- AlasanBerhentiQueryRepository.java
- RiwayatSkAud.java
- DetailDasarGajiRecord
- GradeQuery
- FileUploadUtil
- tables/HariLibur.java
- BiodataAudRecord
- BiodataQueryService.java
- CutiKuota
- GajiKomponenAudRecord
- DetailDasarGajiQueryRepository.java
- GajiTunjanganRecord
- GajiParameterSettingCommandService.java
- LampiranProfilCommandService
- KeahlianQueryService.java
- IdsAbstract
- CutiJenisRecord
- Dummy Prompt
- CutiJenis
- GajiPotonganTkkRecord
- HariLiburRecord
- SpecificationBuilder
- GajiPhdpCommandService.java
- PengalamanKerjaQueryService.java
- GajiBatchRootController.java
- ProfilKeluargaJooqMapperTest
- DasarGajiController.java
- BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`)
- Master Query Optimization Pattern
- JenjangPendidikanController.java
- AppwriteClientTest
- RiwayatKontrakQueryRepository.java
- PendidikanAud.java
- ADR-0003
- IdsAbstract
- BiodataDashboardQueryTest
- RiwayatTerminasiAud.java
- CutiKuotaTemplateBuilder.java
- Claim Order — Security: Dev Chain Validasi Bearer Token + Fallback DevAuth (ADR-0033)
- GlobalExceptionHandler.java
- CutiApprovalChainRecord
- GolonganRecord
- 📌 Issue Details
- Mail Service — Code Patterns (Verified Analysis)
- LampiranProfil
- OpenApiConfig
- KartuIdentitasQueryService.java
- Penggajian Cqrs Claim Order
- JenisKitasPostRequest
- JenisSpCommandServiceTest
- .build
- MimeTypesUtilsImpl
- StatusPegawaiController.java
- CutiApprovalChain
- ProfileUpdateService
- GajiParameterSetting
- .Jabatan
- GajiPendapatanNonPajak
- PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian
- LampiranProfilQueryService
- MasterBaseEntity
- Serializable
- RumahDinas
- MasterBaseEntity
- CutiKlaimDetail
- DateHelper
- .delete_withChildSubJabatan_throwsConflict
- Claim Order 2026 06 17 Analisis Bug
- AlasanBerhenti
- JenisKitas
- Pelatihan
- DownloadPenggajian
- 0012 Jooq Codegen Via Generationtool Not Plugin
- BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session
- Modul yang Dibangun/Dimodifikasi
- Prefs
- .save
- JenisKeahlianPostRequest
- JenisKeahlian
- .KeahlianAud
- .RiwayatTerminasiAud
- .handle
- Configuration
- RedisHelperTest
- Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`
- .createStyle
- Keputusan yang Disepakati
- AuthController.java
- PelatihanController
- RiwayatKeluar
- Apd
- JenisPelatihan
- .PendidikanAud
- GolonganWriteIT.java
- AuditConfig.java
- 0010 — Drop the @Version / version column from rewritten master entities
- Inventory: kepegawaian (Legacy) Schema Dump
- Form Mutasi — Claim Order & Checklist
- CQRS Migration Roadmap
- TestController.java
- RedisConfig.java
- Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration
- Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build
- context7
- GajiBatchRootPostRequest
- BiodataDetailJooqMapperTest
- ArchUnitTest.java
- AuditAwareImpl
- 0005 Revive On Create Soft Delete Unique
- Graph Report - .  (2026-05-05)
- KafkaConfig.java
- GajiKomponenJenisGaji.java
- CutiPegawaiSelects
- .hasDeclaredChangedStatus
- Test
- 0018 Changedstatus Server Resolved By Role
- JooqConfig.java
- KafkaTemplate
- SELECT
- .restClient
- GajiBatchRootEventPublisher
- Context — Keputusan Rewrite: Modul Pegawai & Kepegawaian
- context7
- Perubahan Code yang Harus Dilakukan
- gradlew
- JabatanPutRequest
- OrganisasiPutRequest
- ProfesiPutRequest
- EReferensiPegawai
- GajiBatchMasterRecord.java
- GajiBatchRootRecord.java
- KeahlianAudRecord.java
- PegawaiAudRecord.java
- PendidikanAudRecord.java
- RiwayatMutasiAudRecord.java
- RiwayatSpAudRecord.java
- ProfesiSelects.java
- .key
- .key
- .key
- CutiAllocationHelper.java
- build-dev.sh
- copy.sh
- run.sh

## God Nodes (most connected - your core abstractions)
1. `DefaultSchema` - 189 edges
2. `LocalDate` - 163 edges
3. `Communities` - 152 edges
4. `from()` - 148 edges
5. `Page` - 140 edges
6. `from()` - 139 edges
7. `SavedResult` - 134 edges
8. `Graph Report` - 123 edges
9. `PagedRequest` - 108 edges
10. `Pegawai` - 106 edges

## Surprising Connections (you probably didn't know these)
- `Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration` --cites--> `ADR-0001`  [EXTRACTED]
  grill/2026-05-05_cqrs-jooq-flyway-migration.md → docs/profil-rewrite-claim-order.md
- `AuthService` --references--> `Decisions Pegawai`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/decisions-pegawai.md
- `AuthService` --references--> `Language Security`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/language-security.md
- `RiwayatSp` --references--> `Typed Controller Result`  [EXTRACTED]
  GRAPH_REPORT.md → docs/refactor/typed-controller-result.md
- `CutiKlaimDetail` --references--> `Decisions Cuti`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/decisions-cuti.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Biodata changedStatus Flow** — biodata_dashboard_response_changedstatus, changed_status_server_resolved, profile_update_service [INFERRED 0.85]

## Communities (440 total, 51 thin omitted)

### Community 0 - "Core Entities & Pagination"
Cohesion: 0.03
Nodes (170): ADR-0007, ADR-0008, ADR-0022, ADR-0023, AlatKerja, Apd, ApplicationEvent, AuthServiceImpl (+162 more)

### Community 1 - "Many-to-Many & Base Entities"
Cohesion: 0.04
Nodes (47): from(), from(), RiwayatSkPutRequest, from(), from(), from(), from(), PegawaiPostRequest (+39 more)

### Community 2 - "List & Java Collections"
Cohesion: 0.05
Nodes (33): PatchMapping, PostMapping, DeleteMapping, PostMapping, PostMapping, JabatanPostRequest, from(), getName() (+25 more)

### Community 3 - "DTO Patterns & Builders"
Cohesion: 0.09
Nodes (24): KenaikanBerkalaRequest, SingleResult, GetMapping, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+16 more)

### Community 4 - "Validation & Error Handling"
Cohesion: 0.05
Nodes (43): SelectField, SuppressWarnings, from(), from(), build(), Field, Page, Page (+35 more)

### Community 5 - "Relation Mappings & DSL"
Cohesion: 0.03
Nodes (45): PostMapping, ResponseEntity, PostMapping, PutMapping, from(), CutiApprovalPostRequest, from(), CutiPengajuanKlaimPostRequest (+37 more)

### Community 6 - "Pegawai Join Queries"
Cohesion: 0.07
Nodes (26): GolonganPostRequest, Data, JsonIgnore, Specification, from(), from(), from(), Golongan (+18 more)

### Community 7 - "Adapter & Config Mappers"
Cohesion: 0.07
Nodes (42): LaporanKepegawaianService, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, LaporanDnpController, GetMapping (+34 more)

### Community 8 - "Penggajian Payroll Entities"
Cohesion: 0.12
Nodes (5): from(), SuppressWarnings, PendidikanRecord, Pendidikan, PendidikanMapper

### Community 9 - "Kepegawaian SK & SP"
Cohesion: 0.08
Nodes (11): GetMapping, ResponseEntity, from(), from(), LampiranSk, Override, LampiranProfil, PengalamanKerja (+3 more)

### Community 10 - "Enums & Constants"
Cohesion: 0.07
Nodes (28): ADR-010, AllowedFileTypeController, ApiError, ApplicationEventPublisher, AppWriteAuthFilter, ArchivePublishedEvent, BETWEEN, Patterns Mail Service (+20 more)

### Community 11 - "Master References"
Cohesion: 0.01
Nodes (152): Communities, Community 0 - ".getId()", Community 100 - "OrganisasiRepository.java", Community 101 - "JenisPelatihanRepository.java", Community 102 - "JenisKeahlianRepository.java", Community 103 - "RumahDinasRepository.java", Community 104 - "JenisKitasRepository.java", Community 105 - "GradeRepository.java" (+144 more)

### Community 12 - "Profil Biodata & Pendidikan"
Cohesion: 0.09
Nodes (4): GajiTunjanganAudRecord, Override, Record2, SuppressWarnings

### Community 13 - "Cuti Leave Module"
Cohesion: 0.06
Nodes (6): from(), GajiBatchRootRecord, Override, Record1, SuppressWarnings, GajiBatchRoot

### Community 14 - "Domain Context Docs"
Cohesion: 0.04
Nodes (87): AlatKerjaRepository, ApdRepository, DetailDasarGajiRepository, Master Delete Guard Claim Order, GradeRepository, JabatanRepository, JenisSpPostRequest, JenisSpRepository (+79 more)

### Community 15 - "Claim Order & ADRs"
Cohesion: 0.09
Nodes (9): from(), CutiKuotaRecord, Override, Record1, SuppressWarnings, CutiKuotaMapper, CutiKuota, CutiKuota (+1 more)

### Community 16 - "PagedRequest"
Cohesion: 0.04
Nodes (85): Direction, PagedRequest, Getter, JsonIgnore, Pageable, Setter, PagedRequest, CutiApprovalRequest (+77 more)

### Community 17 - "PengalamanKerjaAudRecord"
Cohesion: 0.05
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 19 - ".toQuery"
Cohesion: 0.23
Nodes (6): GradeJooqMapperTest, DSLContext, Field, Test, Test, SanksiJooqMapperTest

### Community 22 - "RecordMapper"
Cohesion: 0.05
Nodes (29): EGolonganDarah, JenjangPendidikanResponse, RecordMapper, JenjangPendidikanResponse, BiodataQuery, KartuIdentitasQuery, ProfilKeluargaQuery, PelatihanQuery (+21 more)

### Community 23 - "KartuIdentitasAudRecord"
Cohesion: 0.07
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 24 - "Cuti CQRS Rewrite — Claim Order & Checklists"
Cohesion: 0.14
Nodes (24): Acceptance, Acceptance (final modul), Cuti CQRS Rewrite — Claim Order & Checklists, FASE 0 — Pra-implementasi (setup beads), FASE 10 — Pengajuan Command (Keputusan #1, #6, #8, #9), FASE 11 — Approval Command state-machine (Keputusan #6), FASE 12 — Klaim Command + allocator klaim 1:1 (Keputusan #16, #10), FASE 13 — Controllers (Keputusan #13) (+16 more)

### Community 25 - "DasarGaji"
Cohesion: 0.13
Nodes (17): DetailDasarGajiPostRequest, DetailDasarGajiPostRequest, Data, JsonIgnore, Specification, DetailDasarGajiPutRequest, DetailDasarGaji, AllArgsConstructor (+9 more)

### Community 27 - "RiwayatSkRecord"
Cohesion: 0.05
Nodes (4): Override, Record1, SuppressWarnings, RiwayatSkRecord

### Community 28 - "Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring"
Cohesion: 0.29
Nodes (7): Acceptance ringkas per issue, Cara update checklist, Dependency map (ringkas), Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring, REF, WAVE 0 — Epic (gerbang, tidak dikerjakan langsung), WAVE 1 — Eksekusi paralel (2 issue, tidak saling blok)

### Community 30 - "BiodataRecord"
Cohesion: 0.05
Nodes (10): BiodataGolonganDarah, A, AB, B, O, SuppressWarnings, BiodataRecord, Override (+2 more)

### Community 31 - "SanksiSpRecord"
Cohesion: 0.12
Nodes (3): SuppressWarnings, SanksiSpRecord, SanksiMapper

### Community 33 - "RevinfoPath"
Cohesion: 0.06
Nodes (21): Override, Record1, SuppressWarnings, RevinfoRecord, Condition, Field, ForeignKey, Identity (+13 more)

### Community 35 - "LampiranSkAudRecord"
Cohesion: 0.05
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 36 - "StatistikPegawaiRecord"
Cohesion: 0.05
Nodes (19): Override, Record1, SuppressWarnings, StatistikPegawaiRecord, Condition, Field, Identity, Index (+11 more)

### Community 37 - "LampiranProfilRecord"
Cohesion: 0.05
Nodes (19): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+11 more)

### Community 38 - "PegawaiPath"
Cohesion: 0.10
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 39 - "VPegawaiRecord"
Cohesion: 0.06
Nodes (15): SuppressWarnings, VPegawaiRecord, Condition, Field, Name, Override, PlainSQL, Schema (+7 more)

### Community 40 - "CutiKuotaAudRecord"
Cohesion: 0.06
Nodes (20): CutiKuotaAud, CutiKuotaAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 41 - "DasarGajiAudRecord"
Cohesion: 0.09
Nodes (6): DasarGajiAudRecord, Override, Record2, SuppressWarnings, DasarGajiMapper, DasarGaji

### Community 42 - "KartuIdentitasRecord"
Cohesion: 0.08
Nodes (4): Override, Record1, SuppressWarnings, KartuIdentitasRecord

### Community 43 - "JabatanMiniResponse"
Cohesion: 0.09
Nodes (38): AlasanBerhentiResponse, CutiJenisMiniResponse, GolonganResponse, GradeMiniResponse, GradeResponse, JabatanMiniResponse, JenisSpMiniResponse, LocalDate (+30 more)

### Community 44 - "GajiPotonganTkkAudRecord"
Cohesion: 0.07
Nodes (20): GajiPotonganTkkAud, GajiPotonganTkkAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 45 - "CutiJenisAudRecord"
Cohesion: 0.06
Nodes (20): CutiJenisAud, CutiJenisAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 46 - "DetailDasarGajiAudRecord"
Cohesion: 0.06
Nodes (20): DetailDasarGajiAud, DetailDasarGajiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 47 - ".getId"
Cohesion: 0.02
Nodes (156): ConstraintViolationException, PostMapping, PutMapping, PostMapping, PutMapping, ResponseEntity, PostMapping, PreAuthorize (+148 more)

### Community 48 - "DasarGajiRecord"
Cohesion: 0.09
Nodes (4): DasarGajiRecord, Override, Record1, SuppressWarnings

### Community 49 - "GajiPendapatanNonPajakAudRecord"
Cohesion: 0.07
Nodes (20): GajiPendapatanNonPajakAud, GajiPendapatanNonPajakAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 50 - "RiwayatCutiAudRecord"
Cohesion: 0.07
Nodes (20): Override, Record2, SuppressWarnings, RiwayatCutiAudRecord, Condition, Field, ForeignKey, InverseForeignKey (+12 more)

### Community 51 - "CutiPegawai"
Cohesion: 0.09
Nodes (19): CutiJenisPath, CutiPegawai, CutiPegawaiPath, Condition, Field, ForeignKey, Identity, Index (+11 more)

### Community 52 - "Organisasi"
Cohesion: 0.06
Nodes (22): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+14 more)

### Community 53 - "RiwayatCutiRecord"
Cohesion: 0.06
Nodes (22): Override, Record1, SuppressWarnings, RiwayatCutiRecord, Condition, Field, ForeignKey, Identity (+14 more)

### Community 54 - "GajiParameterSettingAudRecord"
Cohesion: 0.07
Nodes (20): GajiParameterSettingAud, GajiParameterSettingAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 55 - "Jabatan"
Cohesion: 0.06
Nodes (22): Jabatan, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+14 more)

### Community 56 - "GajiPendapatanNonPajakRecord"
Cohesion: 0.08
Nodes (10): LevelResponse, GradeMiniResponse, GradeResponse, LevelResponse, JabatanJooqMapper, SuppressWarnings, SharedMappers, PegawaiDetailRecordMapper (+2 more)

### Community 57 - "Ringkasan Temuan"
Cohesion: 0.08
Nodes (26): ✅ Alive Selects (lengkap), Claim Order & Checklist, 🔵 Cross-Module DTO (Masih Dipakai — JANGAN Dihapus), [D1] Hapus Dead DTO — kepegawaian-0ox, [D2] Hapus Unused Import — kepegawaian-k29, [D3] Verifikasi Cross-Module — kepegawaian-5o6, [D4] Final Cleanup & Build — kepegawaian-aak, [D5] Cleanup Selects — Hapus Dead Field/Array + File — kepegawaian-aak (+18 more)

### Community 58 - "Analisis Project Kepegawaian"
Cohesion: 0.05
Nodes (38): APPROVED, 1. Pegawai (Data Utama Pegawai), 2. Profil (Data Pribadi), 3. Master Data (Referensi), 4. Cuti (Manajemen Cuti), 5. Kepegawaian (Administrasi Pegawai), 6. Penggajian (Payroll), Alur JWT + Appwrite (+30 more)

### Community 59 - "Biodata"
Cohesion: 0.13
Nodes (16): Biodata, Condition, Field, ForeignKey, Index, InverseForeignKey, Name, Override (+8 more)

### Community 60 - "Tables"
Cohesion: 0.09
Nodes (19): LevelPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+11 more)

### Community 61 - "RiwayatTerminasi"
Cohesion: 0.05
Nodes (38): JabatanPath, OrganisasiPath, PegawaiPath, Condition, Field, ForeignKey, Identity, Index (+30 more)

### Community 62 - "GajiParameterSettingRecord"
Cohesion: 0.17
Nodes (15): GajiParameterSetting, Condition, Field, Identity, Index, Name, Override, PlainSQL (+7 more)

### Community 63 - "GajiPhdpRecord"
Cohesion: 0.07
Nodes (19): GajiPhdp, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 64 - "GajiProfilAudRecord"
Cohesion: 0.07
Nodes (21): SELECT, GajiProfilAud, GajiProfilAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name (+13 more)

### Community 65 - "FlywaySchemaHistoryCopy1Record"
Cohesion: 0.07
Nodes (18): FlywaySchemaHistoryCopy1, Condition, Field, Index, Name, Override, PlainSQL, Schema (+10 more)

### Community 66 - "Master Record Refactor — Claim Order & Checklist"
Cohesion: 0.10
Nodes (24): Checklist, Column Set Arrays — ✅ SELESAI, Common Mistakes, Dependency Graph, E0: Foundation (kepegawaian-hkq) — ✅ SELESAI, E1: Flat Batch 1 (kepegawaian-5k9) — ✅ SELESAI, E2: Flat Batch 2 (kepegawaian-1xy) — ✅ SELESAI, E3: JenjangPendidikan (kepegawaian-1ws) — ✅ SELESAI (+16 more)

### Community 67 - "RiwayatSp"
Cohesion: 0.10
Nodes (24): SchemaImpl, DefaultSchema, Catalog, Override, SuppressWarnings, Table, Condition, Field (+16 more)

### Community 68 - "Grade"
Cohesion: 0.07
Nodes (22): Grade, GradePath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 69 - "GajiProfilRecord"
Cohesion: 0.11
Nodes (4): GajiProfilRecord, Override, Record1, SuppressWarnings

### Community 70 - "RiwayatKontrakRecord"
Cohesion: 0.05
Nodes (21): Override, Record1, SuppressWarnings, RiwayatKontrakRecord, Condition, Field, ForeignKey, Identity (+13 more)

### Community 71 - "RiwayatSk"
Cohesion: 0.14
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 72 - "Golongan"
Cohesion: 0.06
Nodes (27): ForeignKey, SuppressWarnings, UniqueKey, Keys, Golongan, GolonganPath, Condition, Field (+19 more)

### Community 73 - "Profil Record Refactor — Claim Order & Checklist"
Cohesion: 0.19
Nodes (21): Analisis, Aturan Penting (dari master-query-optimization-pattern.md), Checklist, Claim Order, File, P10: Final Verification, P1: Pendidikan, P2: Keahlian (+13 more)

### Community 74 - "SanksiSp"
Cohesion: 0.14
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 75 - "GajiBatchMaster"
Cohesion: 0.12
Nodes (20): GajiBatchMaster, GajiBatchMasterPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+12 more)

### Community 76 - "ProfilKeluarga"
Cohesion: 0.15
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 77 - "KartuIdentitas"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 78 - "RiwayatMutasi"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 79 - "AlasanBerhentiRecord"
Cohesion: 0.09
Nodes (20): AlasanBerhenti, AlasanBerhentiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+12 more)

### Community 80 - "RumahDinasRecord"
Cohesion: 0.09
Nodes (26): GajiProfilCommandService, GajiProfilController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+18 more)

### Community 81 - "JenisSpRecord"
Cohesion: 0.12
Nodes (4): Override, Record1, SuppressWarnings, JenisSpRecord

### Community 82 - "ApdRecord"
Cohesion: 0.07
Nodes (22): Apd, ApdPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 83 - "GajiTunjangan"
Cohesion: 0.15
Nodes (18): GajiTunjangan, GajiTunjanganPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 84 - "Keahlian"
Cohesion: 0.14
Nodes (18): JenisKeahlianPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 85 - "tables/Pelatihan.java"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 86 - "GajiPotonganTkk"
Cohesion: 0.13
Nodes (18): GajiPotonganTkk, GajiPotonganTkkPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 87 - "JenisSp"
Cohesion: 0.12
Nodes (19): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+11 more)

### Community 88 - "AlatKerjaRecord"
Cohesion: 0.08
Nodes (22): AlatKerja, AlatKerjaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 89 - "Pegawai Record Refactor — Claim Order & Checklist"
Cohesion: 0.15
Nodes (19): Analisis, Aturan Penting, Checklist, Controller Response Types, CustomResult Method Reference, File, File Impact Summary, G1: PegawaiResponse (+11 more)

### Community 90 - "Claim Order — Adopsi Pattern Publication ke Modul Master"
Cohesion: 0.11
Nodes (19): A. Klaim berurutan (master list), B. Wave structure (urutan eksekusi + verifikasi), C. Pre-flight checklist (jalankan sekali sebelum mulai), Claim Order — Adopsi Pattern Publication ke Modul Master, D.1 Pre-flight per modul, D.2 Child paging/sort checklist, D.3 Child write-flow checklist, D.4 Sub-resource khusus (Apd/AlatKerja) (+11 more)

### Community 91 - "GajiBatchRoot"
Cohesion: 0.12
Nodes (17): GajiBatchRoot, GajiBatchRootPath, Condition, Field, ForeignKey, Index, InverseForeignKey, Name (+9 more)

### Community 92 - "tables/GajiBatchRootLampiran.java"
Cohesion: 0.13
Nodes (20): GajiBatchRootLampiran, GajiBatchRootLampiranPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+12 more)

### Community 93 - "tables/GajiBatchRootErrorLogs.java"
Cohesion: 0.15
Nodes (18): GajiBatchRootErrorLogs, GajiBatchRootErrorLogsPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 94 - "tables/PengalamanKerja.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 95 - "Specification"
Cohesion: 0.04
Nodes (44): KeahlianPostRequest, AlasanBerhentiPostRequest, HariLiburPostRequest, Data, JsonIgnore, Specification, JenisKitasPostRequest, Data (+36 more)

### Community 96 - "tables/CutiApprovalChain.java"
Cohesion: 0.07
Nodes (25): CutiApprovalChain, CutiApprovalChainPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+17 more)

### Community 97 - "tables/GajiProfil.java"
Cohesion: 0.13
Nodes (18): GajiProfil, GajiProfilPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 98 - "tables/CutiKuota.java"
Cohesion: 0.13
Nodes (18): CutiKuota, CutiKuotaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 99 - "tables/CutiKlaimDetail.java"
Cohesion: 0.10
Nodes (22): CutiKlaimDetail, CutiKlaimDetailPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 100 - "tables/DetailDasarGaji.java"
Cohesion: 0.14
Nodes (18): DasarGajiPath, DetailDasarGaji, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 101 - "Page"
Cohesion: 0.17
Nodes (14): RequestMapping, RequiredArgsConstructor, RestController, Validator, ValidatorFactory, RiwayatMutasiController, DSLContext, Repository (+6 more)

### Community 102 - "JenisKitasRecord"
Cohesion: 0.07
Nodes (22): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+14 more)

### Community 103 - "Level"
Cohesion: 0.08
Nodes (21): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+13 more)

### Community 104 - "JenisPelatihanRecord"
Cohesion: 0.13
Nodes (4): Override, Record1, SuppressWarnings, JenisPelatihanRecord

### Community 105 - "JenisKeahlianRecord"
Cohesion: 0.13
Nodes (4): Override, Record1, SuppressWarnings, JenisKeahlianRecord

### Community 106 - "Decisions Cuti"
Cohesion: 0.02
Nodes (113): ADR-0021, ApprovalChain, ApprovalCutiCommand, CutiApprovalChainCustomRepositoryImpl, CutiApprovalChainIndexQuery, CutiApprovalChainRequest, CutiApprovalChainResponse, CutiApprovalChainService (+105 more)

### Community 107 - "NotFoundException"
Cohesion: 0.03
Nodes (70): ApdRow, Byte, CATEGORY, DefaultRecordMapper, Aturan keputusan, Consequences, Considered Options, 0025 Fetchinto Flat Jooqmapper Join Nested Master (+62 more)

### Community 108 - "tables/JenisKeahlian.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 109 - "tables/RumahDinas.java"
Cohesion: 0.07
Nodes (22): Override, Record1, SuppressWarnings, RumahDinasRecord, Condition, Field, ForeignKey, Identity (+14 more)

### Community 110 - "Profil CQRS — Pola Implementasi per Layer"
Cohesion: 0.12
Nodes (17): 1. DTO, 1a. Request tulis — <Agg>PostRequest / <Agg>PutRequest, 1b. Request baca — <Agg>Request, 1c. Response baca — <Agg>Response / <Agg>Query, 2. Mapper — final, private ctor, BUKAN @Component, 2a. Write mapper — <Agg>Mapper (dipakai CommandService), 2b. Read mapper Pola A (flat) — static mapToResponse(Record), 2c. Read mapper Pola B (implements RecordMapper) — dipakai profil (+9 more)

### Community 111 - "CutiApprovalAudRecord"
Cohesion: 0.09
Nodes (4): CutiApprovalAudRecord, Override, Record2, SuppressWarnings

### Community 112 - "GajiPhdpAudRecord"
Cohesion: 0.07
Nodes (20): GajiPhdpAud, GajiPhdpAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 113 - "RiwayatMutasiAud.java"
Cohesion: 0.16
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 114 - "JenjangPendidikanResponse"
Cohesion: 0.08
Nodes (30): Keluarga, ProfilKeluargaCommandService, ProfilKeluargaDetailQuery, ProfilKeluargaQueryRepository, DeleteMapping, GetMapping, Page, RequestMapping (+22 more)

### Community 115 - "CutiPegawaiAud.java"
Cohesion: 0.16
Nodes (16): CutiPegawaiAud, CutiPegawaiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 116 - "RiwayatSpAud.java"
Cohesion: 0.15
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 117 - "PelatihanAudRecord"
Cohesion: 0.05
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 118 - "Profesi"
Cohesion: 0.22
Nodes (8): Data, EqualsAndHashCode, ProfesiPutRequest, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, ProfesiAlatKerjaE2eTest

### Community 120 - "GajiKomponenAud.java"
Cohesion: 0.07
Nodes (20): GajiKomponenAud, GajiKomponenAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 122 - "Graph Report"
Cohesion: 0.03
Nodes (88): Graph Report, AuditRevisionEntity, BiodataRepository, BiodataService, CutiApprovalChainCustomRepository, CutiApprovalServiceImplTest, CutiKuotaService, GajiBatchRootService (+80 more)

### Community 123 - "LampiranSkRecord"
Cohesion: 0.06
Nodes (19): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+11 more)

### Community 124 - "UpdatableRecordImpl"
Cohesion: 0.09
Nodes (20): GajiBatchRootAud, GajiBatchRootAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 125 - "Organisasi"
Cohesion: 0.04
Nodes (73): GajiTunjanganCommandService, GajiTunjanganPostRequest, JabatanPostRequest, LevelRepository, Data, JsonIgnore, Specification, RiwayatKontrakPostRequest (+65 more)

### Community 126 - "KeahlianAud.java"
Cohesion: 0.19
Nodes (11): Condition, Field, Override, PlainSQL, Schema, Select, SQL, SuppressWarnings (+3 more)

### Community 127 - "LampiranProfilAudRecord"
Cohesion: 0.06
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 128 - "CutiApprovalRecord"
Cohesion: 0.06
Nodes (22): CutiApproval, CutiApprovalPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 129 - "EJenisLampiranProfil"
Cohesion: 0.10
Nodes (34): LampiranProfilPostRequest, Data, EqualsAndHashCode, KartuIdentitasLampiranPostRequest, Data, EqualsAndHashCode, KeahlianLampiranPostRequest, Data (+26 more)

### Community 130 - "Domain Docs"
Cohesion: 0.16
Nodes (11): DDLDatabase, Consequences, Considered Options, 0004 Jooq Codegen From Testcontainers, JOOQ codegen membaca schema dari Testcontainers MariaDB, ADRs, Domain Docs, File Structure (+3 more)

### Community 131 - "ISSUE 2 — kepegawaian-buc (Phase B-D)"
Cohesion: 0.14
Nodes (15): A1 — move LevelRepository, A2 — move JenjangPendidikanRepository, A3 — verify, Acceptance (all must pass), Guardrails (apply on BOTH issues), ISSUE 1 — kepegawaian-j5i (Phase A), ISSUE 2 — kepegawaian-buc (Phase B-D), Level CQRS Migration — Claim Order & Checklists (+7 more)

### Community 132 - "Checklist Detail per Issue"
Cohesion: 0.13
Nodes (15): 10. kepegawaian-6h2 — LevelServiceImpl tidak CQRS — ✅ SHIPPED (multi-commit), 11. kepegawaian-ytz — PegawaiServiceImpl wildcard+eksplisit — ✅ SHIPPED (commit 9f00059), 1. kepegawaian-9v9 — Status enum pakai ordinal()  ✅ SHIPPED (commit a20914f), 2. kepegawaian-g2j (real ID kepegawaian-0jo) — logAndBuildFailure bocor e.getMessage() — OPEN, claimed 2026-06-22, 3. kepegawaian-0fe (real ID kepegawaian-f5i) — File upload di dalam tx → orphan — OPEN, claimed 2026-06-22, 4. kepegawaian-7rk (real ID kepegawaian-jgm) — processPotonganTkk di dalam tx — OPEN, claimed 2026-06-22, 5. kepegawaian-u68 (real ID kepegawaian-9q7) — delete() tanpa @Transactional — OPEN, claimed 2026-06-22, 6. kepegawaian-qgp (real ID kepegawaian-hng) — registerSynchronization duplikasi — OPEN, claimed 2026-06-22 (+7 more)

### Community 133 - "LANGKAH KERJA"
Cohesion: 0.13
Nodes (15): 1 — Profesi (paling berisiko; kena ceiling 120 baris), 2 — Organisasi (self-ref), 3 — Jabatan (self-ref), 4 — JenisSp, 5 — Verifikasi, ⚠️ Ceiling — CODINGRULES §4 (max 120 baris), Guardrails, Konteks (baca dulu) (+7 more)

### Community 135 - "GajiBatchRootErrorLogsRecord"
Cohesion: 0.16
Nodes (4): GajiBatchRootErrorLogsRecord, Override, Record1, SuppressWarnings

### Community 136 - "Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists"
Cohesion: 0.15
Nodes (13): Aturan ISDELETED per aggregate (WAJIB benar), Claim order, Guardrails (semua issue), ISSUE 10 — kepegawaian-awf.10 GajiBatchMaster, ISSUE 11 — kepegawaian-awf.11 GajiBatchMasterProses, ISSUE 12 — kepegawaian-awf.12 GajiBatchRoot (4-file split), Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists, Prinsip modul (baca sekali di awal) (+5 more)

### Community 137 - "Organisasi Claim Order"
Cohesion: 0.15
Nodes (13): AuditConfig, Cara klaim & tutup (beads), Catatan per-issue, Claim Order — Epic kepegawaian-irt, Irt Claim Order, irt/1 — kepegawaian-9g0 (INDEPENDEN, mulai dulu), irt/2 — kepegawaian-j4a (INDEPENDEN, blok irt/3), irt/3 — kepegawaian-c2q (butuh irt/2) (+5 more)

### Community 138 - "JwtAuthFilter"
Cohesion: 0.10
Nodes (24): OncePerRequestFilter, DevAuthFilter, Component, FilterChain, HttpServletRequest, HttpServletResponse, Override, Component (+16 more)

### Community 139 - "Profil CQRS Cleanup — Claim Order & Checklists"
Cohesion: 0.10
Nodes (8): Data, ProfesiPostRequest, Override, Record1, SuppressWarnings, ProfesiRecord, Profesi, ProfesiMapper

### Community 140 - "Kepegawaian — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring"
Cohesion: 0.17
Nodes (12): Acceptance ringkas per issue, Cara update checklist, Dependency map (ringkas), Kepegawaian — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring, REF, WAVE 0 — Epic (gerbang, tidak dikerjakan langsung), WAVE 1 — Fondasi (3 issue paralel, file berbeda, tidak saling blok), WAVE 2 — Leaf CRUD (2 issue paralel) + akar SK Query (+4 more)

### Community 141 - "LANGKAH KERJA"
Cohesion: 0.17
Nodes (12): 1 — Tambah dua multiset ke pageQuery, 2 — Collapse DTO & kolom (hapus ProfesiQuery), 3 — Rapikan signature hulu, 4 — Verifikasi, Guardrails, Keputusan manager (hasil grilling), Konteks (baca dulu), LANGKAH KERJA (+4 more)

### Community 142 - "Claim Order — Drop CommonPageRequest → Rewrite CQRS/JOOQ 5 Modul Terakhir"
Cohesion: 0.17
Nodes (12): Aturan Wajib (jangan dilanggar), Checklist Pengerjaan (Master), Claim Order — Drop CommonPageRequest → Rewrite CQRS/JOOQ 5 Modul Terakhir, Domain cuti — epik kepegawaian-6bu.2, Domain kepegawaian — epik kepegawaian-6bu.3, Domain profil — epik kepegawaian-6bu.4, Domain system + users — epik kepegawaian-6bu.5, Per-Slice Checklist (template CQRS) (+4 more)

### Community 143 - "Checklist per Domain"
Cohesion: 0.17
Nodes (12): 1 · penggajian (kepegawaian-51j.1) ✅, 2 · master (kepegawaian-51j.2) — + bug 200→201 ✅, 3 · profil (kepegawaian-51j.3) ✅, 4 · cuti (kepegawaian-51j.4) ✅, 4 Kriteria, 5 · kepegawaian + laporan (kepegawaian-51j.5) ✅, 6 · pegawai + auth + users + system (kepegawaian-51j.6) ✅, Checklist per Domain (+4 more)

### Community 144 - ".getBiodata"
Cohesion: 0.08
Nodes (15): PatchMapping, from(), from(), from(), JenisKitas, BiodataAudRecord, SuppressWarnings, PegawaiMapper (+7 more)

### Community 145 - "FileUploadUtilImpl"
Cohesion: 0.11
Nodes (14): JooqCodegenTask, DefaultTask, Property, FileUploadUtilImpl, MultipartFile, Override, RequiredArgsConstructor, Service (+6 more)

### Community 146 - "ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ"
Cohesion: 0.08
Nodes (28): JenjangPendidikanMiniResponse, Data, JsonIgnore, Specification, ProfilKeluargaPostRequest, ProfilKeluargaResponse, EHubunganKeluarga, ANAK (+20 more)

### Community 147 - "Claim Order — Reformat Column Order V1baseline.sql"
Cohesion: 0.18
Nodes (11): Checklist, Claim Order — Reformat Column Order V1baseline.sql, Claim Order Baseline Column Order, Kolom Orphan (baseline ≠ entity), Pattern A — MasterBaseEntity (tabel master), Pattern B — IdsAbstract (tabel transaksional/audited), Pattern C — Biodata (standalone, PK=nik), Risk (+3 more)

### Community 148 - "Profil Rewrite — Claim Order & Monitoring"
Cohesion: 0.18
Nodes (11): Cara update checklist ini, Dependency map (ringkas), Issue terkait (bug), Keputusan desain yang dikunci (rujukan saat coding), Profil Rewrite — Claim Order & Monitoring, Referensi template (BACA DULU sebelum coding), WAVE 0 — Fondasi (1 issue, blokir semua write-side), WAVE 1 — Slice Referensi Pendidikan (2 issue, GERBANG) (+3 more)

### Community 150 - "Master Rewrite — Claim Order & Monitoring"
Cohesion: 0.18
Nodes (11): Apa yang disentuh tiap issue, Cara update checklist, Dependency map (ringkas), Master Rewrite — Claim Order & Monitoring, WAVE 0 — Akar (1 issue, blokir semua), WAVE 1 — Foundation paralel (5 issue, semua butuh F1), WAVE 2 — Lanjutan foundation (3 issue), WAVE 3 — Exemplar (1 issue, GERBANG) (+3 more)

### Community 151 - "ApiException"
Cohesion: 0.17
Nodes (8): ApiException, RuntimeException, ApiException, Getter, HttpStatus, BadRequestException, ConflictException, NotFoundException

### Community 152 - "PegawaiController"
Cohesion: 0.11
Nodes (20): AlatKerjaQuery, CutiPegawaiSelects, Consequences, Considered Options, 0030 Hapus Seeding Imperatif Setupmaster, Hapus jalur seeding imperatif setupMaster/, seeding data via Flyway, Catatan bukan-prioritas, Claim Order — Temuan Grilling Arsitektur (2026-07-09) (+12 more)

### Community 153 - "Worktree"
Cohesion: 0.22
Nodes (9): Worktree, Aturan, Buat ulang worktree legacy kalau terhapus, Catatan, Hapus worktree legacy kalau sudah tak dibutuhkan, Layout, Lihat daftar worktree, Perintah (+1 more)

### Community 154 - "Pegawai — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring"
Cohesion: 0.22
Nodes (9): Acceptance ringkas per issue, Cara update checklist, Dependency map (ringkas), Pegawai — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring, REF, WAVE 0 — Epic (gerbang, tidak dikerjakan langsung), WAVE 1 — Fondasi (2 issue paralel, file berbeda, tidak saling blok), WAVE 2 — Builder (3 issue paralel, file berbeda, tidak saling blok) (+1 more)

### Community 155 - "Decisions — Modul Master (CQRS Cleanup)"
Cohesion: 0.22
Nodes (9): §1 — Scope rewrite modul master, §2 — Enum-backed aggregate: no interface, rename ke QueryService, §3 — Write mapper: final class terpisah, bukan method di DTO, §4 — JOOQ mapping: fetchInto flat, JooqMapper join-nested, multiset one-to-many, §5 — CommandService return: entity JPA (bukan SavedStatus), §6 — FK attach: getReferenceById di CommandService, bukan di mapper, §7 — Revive-on-create: lewat Specification (tanpa @SQLRestriction di entity master), §8 — Delete parent: guard owned-child, JANGAN cascade (issue kepegawaian-15u) (+1 more)

### Community 156 - "Checklist Implementasi"
Cohesion: 0.67
Nodes (3): AGENTS.md Agent Config, CLAUDE.md Canonical Guidance, GitNexus Code Intelligence

### Community 157 - "OrganisasiQueryRepository.java"
Cohesion: 0.08
Nodes (30): OrganisasiIndexQuery, OrganisasiListResponse, OrganisasiQuery, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+22 more)

### Community 158 - "GajiBatchMasterProsesRecord"
Cohesion: 0.07
Nodes (25): GajiBatchMasterProsesJenisGaji, NONE, PEMASUKAN, POTONGAN, SuppressWarnings, GajiBatchMasterProses, Condition, Field (+17 more)

### Community 159 - "BiodataAudGolonganDarah"
Cohesion: 0.10
Nodes (25): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, LaporanKontrakController, GetMapping, RequestMapping (+17 more)

### Community 160 - "BiodataGolonganDarah.java"
Cohesion: 0.36
Nodes (8): getCatalog(), getLiteral(), getName(), getSchema(), Catalog, Override, Schema, lookupLiteral()

### Community 161 - "Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist"
Cohesion: 0.29
Nodes (7): Bentuk DTO Target, Claim Order (kerjakan berurutan — tiap task blok task berikutnya), Definition of Done, Keputusan Grilling, Konteks, Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist, Yang TIDAK Boleh Disentuh

### Community 163 - "0008 Fk Via Getreference On Write"
Cohesion: 0.29
Nodes (7): DataIntegrityViolationException, Attach FK relasi via getReferenceById, bukan findById, Consequences, Considered Options, 0008 Fk Via Getreference On Write, DuplicateResourceException, ResourceNotFoundException

### Community 164 - "PrefRole"
Cohesion: 0.16
Nodes (15): Condition, Field, Name, Override, PlainSQL, Schema, Select, SQL (+7 more)

### Community 165 - "GajiKomponenAudJenisGaji"
Cohesion: 0.22
Nodes (12): GajiKomponenAudJenisGaji, NONE, PEMASUKAN, POTONGAN, getCatalog(), getLiteral(), getSchema(), Catalog (+4 more)

### Community 166 - "PendidikanQueryService.java"
Cohesion: 0.05
Nodes (51): BiodataDetailRowMapper, BiodataRowMapper, Acceptance, Acceptance (Wave 1), Acceptance (Wave 2), Profil Cqrs Cleanup Claim Order, FASE 0 — Pra-implementasi (setup beads), FASE PU-1 — Buang interface ProfileUpdateService (decisions-cuti §11) (+43 more)

### Community 167 - "LampiranProfilController.java"
Cohesion: 0.16
Nodes (16): CutiApprovalAud, CutiApprovalAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 168 - "ADR-0017 — Claim Order & Monitoring"
Cohesion: 0.33
Nodes (6): ADR-0017 — Claim Order & Monitoring, Cara update checklist, Dependency map (ringkas), Issue khusus (pola beda — baca design issue penuh), WAVE 0 — Exemplar (1 issue, GERBANG), WAVE 1 — Replikasi (13 issue paralel, semua butuh s55)

### Community 169 - "Issue tracker: beads + GitHub"
Cohesion: 0.33
Nodes (6): beads conventions (default for task tracking), Issue Tracker, GitHub conventions (published issues / PRDs), Issue tracker: beads + GitHub, When a skill says "fetch the relevant ticket", When a skill says "publish to the issue tracker"

### Community 170 - "JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset"
Cohesion: 0.16
Nodes (16): GajiTunjanganAud, GajiTunjanganAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 171 - "GajiBatchRoot"
Cohesion: 0.10
Nodes (18): EProsesGaji, GajiBatchRootQueryService, ListResult, RequiredArgsConstructor, Data, EProsesGaji, FAILED, FINISHED (+10 more)

### Community 172 - "Level"
Cohesion: 0.04
Nodes (66): EStatusKerja, EStatusPegawai, GajiPotonganTkkCommandService, GajiPotonganTkkPostRequest, GajiPotonganTkkRepository, Component, ConfigurationProperties, Data (+58 more)

### Community 173 - "DefaultSchema"
Cohesion: 0.07
Nodes (20): CatalogImpl, List, Data, PegawaiBatchIdsRequest, DefaultCatalog, Override, Schema, SuppressWarnings (+12 more)

### Community 174 - "ProfesiController"
Cohesion: 0.09
Nodes (22): Consequences, Considered Options, 0001 Jpa Write Jooq Read Cqrs, Pemisahan jalur Command (JPA) dan Query (JOOQ), ProfesiCommandService, ProfesiQueryService, GetMapping, Page (+14 more)

### Community 175 - "Coding Rules"
Cohesion: 0.40
Nodes (5): Coding Rules, CODINGRULES, Git mv + Edit Workflow (HARD INVARIANT), Workflow, EnterPlanMode

### Community 176 - "CONTEXT-MAP — Kepegawaian"
Cohesion: 0.40
Nodes (5): Context Map, Cara Pakai, CONTEXT-MAP — Kepegawaian, Peta Sub-Context, Sub-Context Files

### Community 177 - "Claim Order — GajiBatchRootServiceImpl (Kafka)"
Cohesion: 0.40
Nodes (5): Catatan ketergantungan, Claim Order — GajiBatchRootServiceImpl (Kafka), Claim Order Gajibatchroot Kafka, Perintah claim, StringSerializer

### Community 179 - "Context — Relasi Antar Domain"
Cohesion: 0.40
Nodes (5): Arah Dependency Lintas-Modul, Context — Relasi Antar Domain, Relationships, Relasi, INSERT

### Community 180 - "0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success")"
Cohesion: 0.40
Nodes (5): 0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success"), Consequences, Considered Options, Context, Decision

### Community 181 - "0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail"
Cohesion: 0.11
Nodes (4): GajiParameterSettingRecord, Override, Record1, SuppressWarnings

### Community 182 - "0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null"
Cohesion: 0.15
Nodes (13): ApiResponse, Consequences, Considered Options, 0006 Layer First Package Layout, Tata letak paket: layer-first (bukan vertical slice), 0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null, Consequences, Considered Options (+5 more)

### Community 184 - "DetailDasarGajiCommandService.java"
Cohesion: 0.15
Nodes (12): DetailDasarGajiController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, RestController, DasarGajiMiniResponse, DetailDasarGajiResponse (+4 more)

### Community 185 - "DasarGajiQueryRepository.java"
Cohesion: 0.14
Nodes (16): DasarGajiIndexQuery, Data, EqualsAndHashCode, DasarGajiResponse, DasarGajiJooqMapper, DasarGajiQueryRepository, Condition, DSLContext (+8 more)

### Community 186 - "GajiProfilResponse"
Cohesion: 0.07
Nodes (29): GajiKomponenMiniProjection, GajiKomponenController, DeleteMapping, GetMapping, Page, PutMapping, RequestMapping, RequiredArgsConstructor (+21 more)

### Community 187 - "GajiPhdpResponse"
Cohesion: 0.15
Nodes (13): GajiPhdpResponse, GajiPhdpJooqMapper, GajiPhdpQueryRepository, Condition, DSLContext, Field, Page, Repository (+5 more)

### Community 188 - "SavedStatus"
Cohesion: 0.14
Nodes (9): PatchMapping, PostMapping, PreAuthorize, PutMapping, ResponseEntity, PegawaiPutRequest, Condition, Transactional (+1 more)

### Community 189 - "JenisKitasQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, JenisKitasController, Data (+14 more)

### Community 190 - "GolonganQueryRepository.java"
Cohesion: 0.10
Nodes (27): GolonganCommandService, GolonganIndexQuery, GolonganQuery, GolonganQueryRepository, GolonganQueryService, GolonganController, GetMapping, Page (+19 more)

### Community 191 - "JenisSpQueryRepository.java"
Cohesion: 0.05
Nodes (36): CriteriaBuilder, JenisSpQueryRepository, Root, SafeVarargs, SanksiRow, DeleteMapping, GetMapping, Page (+28 more)

### Community 192 - "RiwayatSpRecord"
Cohesion: 0.05
Nodes (4): Override, Record1, SuppressWarnings, RiwayatSpRecord

### Community 193 - "PrefRole"
Cohesion: 0.16
Nodes (12): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Override, Setter, Table, ToString (+4 more)

### Community 194 - "Context — Contoh Dialog & Ambiguitas Terflag"
Cohesion: 0.50
Nodes (4): Context — Contoh Dialog & Ambiguitas Terflag, Examples And Flags, Example Dialogue, Flagged Ambiguities

### Community 195 - "Context — Modul Master (Data Referensi)"
Cohesion: 0.50
Nodes (4): Aturan Bisnis Penting, Context — Modul Master (Data Referensi), Language Master, Glossary

### Community 196 - "APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone"
Cohesion: 0.13
Nodes (13): LampiranSkQueryRepository, LampiranSkQuery, HashedFileInfo, DSLContext, Repository, RequiredArgsConstructor, LampiranSkQueryRepository, RequiredArgsConstructor (+5 more)

### Community 197 - "Flyway sebagai sumber kebenaran schema"
Cohesion: 0.50
Nodes (4): Consequences, Considered Options, 0002 Flyway Schema Source Of Truth, Flyway sebagai sumber kebenaran schema

### Community 198 - "KepegawaianApplication"
Cohesion: 0.60
Nodes (3): EnableJpaRepositories, SpringBootApplication, KepegawaianApplication

### Community 201 - "KenaikanBerkalaRequest"
Cohesion: 0.14
Nodes (13): EFilterKenaikanBerkala, BULAN_INI, GTE_1, GTE_2, TAHUN_INI, EJenisKenaikanBerkala, SK_KENAIKAN_GAJI_BERKALA, SK_KENAIKAN_PANGKAT_GOLONGAN (+5 more)

### Community 202 - "DeletedResult"
Cohesion: 0.06
Nodes (34): DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping (+26 more)

### Community 203 - ".toEntity"
Cohesion: 0.05
Nodes (40): from(), from(), from(), from(), from(), from(), from(), from() (+32 more)

### Community 204 - "GajiBatchRootCommandService.java"
Cohesion: 0.11
Nodes (23): GajiBatchMasterPostRequest, Data, MultipartFile, GajiBatchRootLampiranRepository, GajiBatchRootRepository, Query, GajiBatchMasterCommandService, RequiredArgsConstructor (+15 more)

### Community 205 - "ProfilUpdateRecord"
Cohesion: 0.07
Nodes (19): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+11 more)

### Community 206 - "KepegawaianApplicationTests.java"
Cohesion: 0.60
Nodes (3): SpringBootTest, Test, KepegawaianApplicationTests

### Community 207 - "Kepegawaian — Master Context"
Cohesion: 0.67
Nodes (3): Context, Kepegawaian — Master Context, Lazy Read — Jangan Baca Semua Sekaligus

### Community 208 - "Context — Modul Cuti (Pengajuan & Approval Cuti)"
Cohesion: 0.67
Nodes (3): Context — Modul Cuti (Pengajuan & Approval Cuti), Language Cuti, Glossary

### Community 209 - "Profil Rewrite Claim Order"
Cohesion: 0.02
Nodes (133): ADR-0001, ADR-0005, ADR-0011, ADR-0013, ADR-0017, ADR-0019, ADR-0025, ADR-0026 (+125 more)

### Community 211 - "HariLiburQueryRepository.java"
Cohesion: 0.07
Nodes (26): HariLiburController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, HariLiburIndexQuery (+18 more)

### Community 218 - "AppwriteUser"
Cohesion: 0.15
Nodes (12): SimpleGrantedAuthority, Component, RequiredArgsConstructor, Slf4j, JwtTokenService, AppwriteUser, AllArgsConstructor, Data (+4 more)

### Community 219 - "CutiJenisQueryRepository.java"
Cohesion: 0.09
Nodes (29): CutiJenisCommandService, CutiJenisQueryRepository, CutiJenisRequest, CutiJenisResponse, CutiJenisController, GetMapping, Page, RequestMapping (+21 more)

### Community 220 - "GajiBatchMasterProsesResponse"
Cohesion: 0.12
Nodes (17): GajiBatchMasterProsesJooqMapper, GajiBatchMasterProsesIndexQuery, Data, EqualsAndHashCode, GajiBatchMasterProsesResponse, GajiBatchMasterProsesJooqMapper, GajiBatchMasterProsesQueryRepository, Condition (+9 more)

### Community 221 - "CutiKuotaDeductionResult"
Cohesion: 0.27
Nodes (6): CutiKuotaDeductionResult, Builder, Data, CutiKuotaDeductionAllocator, CutiKuotaDeductionAllocatorTest, Test

### Community 222 - "EApprovalCutiStatus"
Cohesion: 0.06
Nodes (65): CutiApprovalChainGenerator, CutiApprovalChainRepository, CutiApprovalPostRequest, CutiApprovalRepository, CutiKlaimDetailRepository, CutiKuotaAllocator, CutiKuotaRepository, CutiPegawai (+57 more)

### Community 223 - "ListResult"
Cohesion: 0.06
Nodes (36): GetMapping, GetMapping, GetMapping, GetMapping, GetMapping, ResponseEntity, Data, EqualsAndHashCode (+28 more)

### Community 224 - "RiwayatTerminasiAudRecord"
Cohesion: 0.06
Nodes (4): Override, Record2, SuppressWarnings, RiwayatTerminasiAudRecord

### Community 225 - "JenjangPendidikan"
Cohesion: 0.04
Nodes (74): ADR-0018, BiodataPostRequest, 0019 Profil Revive Archived Peek Native Query, Profil revive-on-create peeks the archived row via a native query, not a derived finder or JOOQ, JenisKitasRepository, KartuIdentitasPostRequest, KartuIdentitasRepository, LampiranProfilCommandService (+66 more)

### Community 226 - "GajiPendapatanNonPajakResponse"
Cohesion: 0.09
Nodes (27): GajiPendapatanNonPajakCommandService, GajiPendapatanNonPajakController, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+19 more)

### Community 227 - ".delete"
Cohesion: 0.35
Nodes (6): ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test, ProfesiCommandServiceTest

### Community 228 - "ErrorCode"
Cohesion: 0.33
Nodes (6): ErrorCode, DB_ERROR, DUPLICATE_BATCH, INTERNAL, UNKNOWN_BATCH, Getter

### Community 229 - "ProfileUpdate"
Cohesion: 0.07
Nodes (31): ProfileUpdate, Data, ProfilUpdateAcceptRequest, EProfileUpdateApproval, APPROVED, PENDING, REJECT, EProfileUpdateTable (+23 more)

### Community 230 - "AuthServiceImplTest.java"
Cohesion: 0.11
Nodes (21): LampiranSp, RiwayatSpPostRequest, Data, JsonIgnore, MultipartFile, Specification, RiwayatSpPostRequest, Data (+13 more)

### Community 231 - "AppwriteClient"
Cohesion: 0.11
Nodes (23): AppwriteUserPostRequest, MockRestServiceServer, RestClient, AppwriteClient, Component, HttpHeaders, PrefRole, RequiredArgsConstructor (+15 more)

### Community 232 - "RiwayatKeluarRecord"
Cohesion: 0.06
Nodes (4): Override, Record1, SuppressWarnings, RiwayatKeluarRecord

### Community 234 - "StatistikPegawai"
Cohesion: 0.22
Nodes (8): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString, StatistikPegawai

### Community 235 - "ProcessPotonganTkkImpl.java"
Cohesion: 0.11
Nodes (20): GajiBatchPotonganTkkRepository, ProcessPotonganTkk, GajiBatchPotonganTkk, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter (+12 more)

### Community 236 - "RiwayatKontrakController.java"
Cohesion: 0.13
Nodes (18): LevelQueryRepository, RequestMapping, RequiredArgsConstructor, RestController, LevelController, Data, EqualsAndHashCode, LevelIndexQuery (+10 more)

### Community 237 - "PageResult"
Cohesion: 0.04
Nodes (48): GetMapping, Page, Page, Page, GetMapping, Page, RequestMapping, RequiredArgsConstructor (+40 more)

### Community 238 - "GajiKomponen"
Cohesion: 0.04
Nodes (52): EJenisGaji, GajiKomponenPostRequest, GajiKomponenRepository, GajiProfilRepository, GajiBatchMasterProsesPostRequest, Data, JsonIgnore, Specification (+44 more)

### Community 239 - "GajiBatchRootLampiran"
Cohesion: 0.19
Nodes (11): GajiBatchRootLampiranMiniResponse, EJenisPotonganGaji, POTONGAN_TAMBAHAN, POTONGAN_TKK, GajiBatchRootLampiran, AllArgsConstructor, Entity, Getter (+3 more)

### Community 240 - ".toString"
Cohesion: 0.20
Nodes (8): PutMapping, PutMapping, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test, OrganisasiCommandServiceTest

### Community 241 - "ConflictException"
Cohesion: 0.03
Nodes (99): ADR-0014, ADR-0031, AlatKerjaCommandService, AlatKerjaController, AlatKerjaQueryService, ApdCommandService, ApdController, ApdQueryService (+91 more)

### Community 242 - "ProfilKeluargaAudRecord"
Cohesion: 0.06
Nodes (4): Override, Record2, SuppressWarnings, ProfilKeluargaAudRecord

### Community 243 - "HariLibur"
Cohesion: 0.13
Nodes (16): Data, JsonIgnore, Specification, EJenisLibur, CUTI_BERSAMA, LIBUR_NASIONAL, Getter, HariLibur (+8 more)

### Community 244 - "EStatusCuti"
Cohesion: 0.25
Nodes (8): EStatusCuti, APPROVED, CANCELLED, CONFIRMED, REJECTED, RETURNED, WAIT_APPROVAL, Getter

### Community 245 - "EJenisTunjangan"
Cohesion: 0.06
Nodes (38): GajiTunjanganJooqMapper, GajiTunjanganController, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Data (+30 more)

### Community 246 - "List"
Cohesion: 0.05
Nodes (52): KartuIdentitasMiniResponse, JenisKitasResponse, Data, PegawaiPatchProfil, BiodataDetail, BiodataMiniResponse, BiodataPatchRequest, Data (+44 more)

### Community 247 - "EJenisSk"
Cohesion: 0.03
Nodes (79): FileUploadUtil, LampiranSkAcceptRequest, LampiranSkQueryService, RiwayatSkPostRequest, Specification, SpecificationBuilder, RequestMapping, RequiredArgsConstructor (+71 more)

### Community 248 - "JenisKontrakController.java"
Cohesion: 0.05
Nodes (51): CustomResult, 1. Keputusan terkunci (berlaku untuk semua master), 2.1 Base paging — PageRequest (abstract), 2.2 Sort whitelist — SortParam, 2.3 Typed ID (opsional, ditunda), 2.4 Controller — write-flow, 2. Komponen pattern (dari kode Publication), 3. Resep adopsi per modul master (langkah generik) (+43 more)

### Community 249 - "RiwayatSkQuery"
Cohesion: 0.16
Nodes (17): EntityManager, ProfileUpdateApprovalService, RevInfoService, RequiredArgsConstructor, Service, Slf4j, ProfileUpdateBiodataApprovalService, RequiredArgsConstructor (+9 more)

### Community 250 - "CutiPegawaiAudRecord"
Cohesion: 0.07
Nodes (4): CutiPegawaiAudRecord, Override, Record2, SuppressWarnings

### Community 254 - "GajiBatchPotonganTkkRecord"
Cohesion: 0.17
Nodes (15): GajiBatchPotonganTkk, Condition, Field, Identity, Index, Name, Override, PlainSQL (+7 more)

### Community 255 - "PengalamanKerjaRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, PengalamanKerjaRecord

### Community 256 - "JabatanQueryRepository.java"
Cohesion: 0.16
Nodes (14): JabatanQueryRepository, JabatanController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+6 more)

### Community 257 - "BiodataPath"
Cohesion: 0.08
Nodes (21): BiodataPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+13 more)

### Community 258 - "KeahlianRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, KeahlianRecord

### Community 259 - "GajiBatchMasterResponse"
Cohesion: 0.10
Nodes (27): GajiBatchMasterCommandService, GajiBatchMasterJooqMapper, GajiBatchMasterPostRequest, GajiBatchMasterQueryRepository, GajiBatchMasterQueryService, GajiBatchMasterController, GetMapping, Page (+19 more)

### Community 260 - "GajiProfil"
Cohesion: 0.23
Nodes (10): GajiKomponenCommandService, GajiProfilPostRequest, GajiProfilPostRequest, Data, JsonIgnore, Specification, GajiProfilPutRequest, GajiProfilCommandService (+2 more)

### Community 261 - "JenisSp"
Cohesion: 0.14
Nodes (17): MasterBaseEntity, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, JenisSp (+9 more)

### Community 262 - "Pendidikan"
Cohesion: 0.15
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 263 - "WebSecurity.java"
Cohesion: 0.14
Nodes (21): ADMIN, ADR-0029, CorsConfigurationSource, DEV, 0016 Profile Conditional Auth, Profile-conditional authentication, Aturan Bisnis Penting, Context — Security (Autentikasi & Lingkungan) (+13 more)

### Community 264 - "PelatihanQueryService.java"
Cohesion: 0.06
Nodes (39): [ ] #4 — CUTOVER + hapus shim lama · kepegawaian-94u.3 (blocked by #3), Aturan wajib tiap langkah (CODINGRULES), Lampiranprofil Cqrs Claim Order, lampiranProfil CQRS — Claim Order & Checklist, Session close (setelah semua hijau), [x] #2 — READ side · kepegawaian-94u.1 (READY), [x] #3 — WRITE side · kepegawaian-94u.2 (blocked by #2), LampiranProfilQuery (+31 more)

### Community 265 - "SanksiQueryRepository.java"
Cohesion: 0.11
Nodes (24): SanksiQueryRepository, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, SanksiController (+16 more)

### Community 266 - ".between1JanAnd30Jun"
Cohesion: 0.17
Nodes (5): CutiApproveKlaimCutiService, RequiredArgsConstructor, Service, CutiKuotaAllocatorTest, Test

### Community 267 - "GajiKomponenRecord"
Cohesion: 0.10
Nodes (4): GajiKomponenRecord, Override, Record1, SuppressWarnings

### Community 268 - "Keys"
Cohesion: 0.10
Nodes (4): Override, Record1, SuppressWarnings, ProfilKeluargaRecord

### Community 269 - "tables/GajiPendapatanNonPajak.java"
Cohesion: 0.07
Nodes (22): GajiPendapatanNonPajak, GajiPendapatanNonPajakPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 270 - "tables/GajiKomponen.java"
Cohesion: 0.14
Nodes (18): GajiKomponen, GajiKomponenPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 271 - "ProfilUpdateController.java"
Cohesion: 0.08
Nodes (27): Profil Record Refactor Claim Order, PendidikanQueryRepository, ProfileUpdateQuery, ProfilUpdateAcceptRequest, ProfilUpdateDetail, GetMapping, Page, RequestMapping (+19 more)

### Community 272 - "LampiranRow"
Cohesion: 0.11
Nodes (9): BiodataCommandService, MimeTypesUtils, BiodataController, MultipartFile, Page, RequestMapping, RequiredArgsConstructor, RestController (+1 more)

### Community 273 - "JenjangPendidikanRecord"
Cohesion: 0.10
Nodes (7): from(), Override, Record1, SuppressWarnings, JenjangPendidikanRecord, JenjangPendidikan, JenjangPendidikanMapper

### Community 274 - "GitNexus — Code Intelligence"
Cohesion: 0.19
Nodes (11): Condition, Field, Override, PlainSQL, Schema, Select, SQL, SuppressWarnings (+3 more)

### Community 275 - "Keahlian"
Cohesion: 0.07
Nodes (28): EnumType, JenisKeahlianResponse, Data, JenisKeahlianResponse, Data, EqualsAndHashCode, Data, JsonIgnore (+20 more)

### Community 276 - "Knowledge — kepegawaian (PERUMDAMTS)"
Cohesion: 0.06
Nodes (32): 10. Issue Tracking, 11. Skills, 12. Commit Convention, 13. Pre-Ship Checklist, 14. Useful Links (auto-scraped by Freebuff), 1. Project Identity, 2. Modes of Operation, 3. Build & Run (+24 more)

### Community 277 - "JenisKeahlianQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, JenisKeahlianController, Data (+14 more)

### Community 278 - "RumahDinasQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, RumahDinasController, Data (+14 more)

### Community 279 - "TableImpl"
Cohesion: 0.14
Nodes (18): DasarGaji, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 280 - "tables/JenisPelatihan.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 281 - "PegawaiQueryService"
Cohesion: 0.09
Nodes (14): GetMapping, Page, RequestMapping, RequiredArgsConstructor, RestController, Validator, PegawaiController, RefMiniResponse (+6 more)

### Community 282 - "JenisPelatihanQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, JenisPelatihanController, Data (+14 more)

### Community 283 - "CutiJenis"
Cohesion: 0.07
Nodes (21): CutiJenis, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+13 more)

### Community 284 - "GradeRecord"
Cohesion: 0.14
Nodes (10): from(), GradePostRequest, Data, JsonIgnore, Specification, from(), GradeMapper, Grade (+2 more)

### Community 285 - "BiodataAud.java"
Cohesion: 0.08
Nodes (32): BiodataAudGolonganDarah, A, AB, B, O, getCatalog(), getLiteral(), getName() (+24 more)

### Community 286 - "DasarGajiAud.java"
Cohesion: 0.15
Nodes (16): DasarGajiAud, DasarGajiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 287 - "PegawaiAud.java"
Cohesion: 0.16
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 288 - "ProfilKeluargaAud.java"
Cohesion: 0.17
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 289 - "PengalamanKerjaCommandService.java"
Cohesion: 0.20
Nodes (12): GajiBatchMasterProsesCommandService, GajiBatchMasterProsesQueryService, GajiBatchMasterProsesController, DeleteMapping, GetMapping, Page, PostMapping, PreAuthorize (+4 more)

### Community 290 - "AlasanBerhentiQueryRepository.java"
Cohesion: 0.12
Nodes (21): AlasanBerhentiController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, AlasanBerhentiIndexQuery (+13 more)

### Community 291 - "RiwayatSkAud.java"
Cohesion: 0.06
Nodes (20): Override, Record2, SuppressWarnings, RiwayatSkAudRecord, Condition, Field, ForeignKey, InverseForeignKey (+12 more)

### Community 293 - "DetailDasarGajiRecord"
Cohesion: 0.10
Nodes (4): DetailDasarGajiRecord, Override, Record1, SuppressWarnings

### Community 294 - "GradeQuery"
Cohesion: 0.11
Nodes (22): GradeIndexQuery, GradeController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+14 more)

### Community 295 - "FileUploadUtil"
Cohesion: 0.06
Nodes (30): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, RiwayatSpController, CutiKuotaPegawaiResponse, Page (+22 more)

### Community 296 - "tables/HariLibur.java"
Cohesion: 0.08
Nodes (19): HariLibur, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 297 - "BiodataAudRecord"
Cohesion: 0.16
Nodes (5): GajiBatchPotonganTkkRecord, Override, Record1, SuppressWarnings, GajiBatchPotonganTkk

### Community 298 - "BiodataQueryService.java"
Cohesion: 0.09
Nodes (28): BiodataDetailQuery, BiodataQueryRepository, KartuIdentitasMultisetJooqMapper, GetMapping, BiodataDashboardResponse, BiodataDashboardQuery, DSLContext, Repository (+20 more)

### Community 299 - "CutiKuota"
Cohesion: 0.04
Nodes (50): CutiKuotaCommandService, CutiKuotaImportRequest, CutiKuotaPegawaiResponse, CutiKuotaPostRequest, CutiKuotaQueryRepository, CutiKuotaRequest, CutiKuotaResponse, CutiKuotaSisa (+42 more)

### Community 300 - "GajiKomponenAudRecord"
Cohesion: 0.18
Nodes (8): SanksiQuery, JenisSpSimple, SanksiJenisSpList, SanksiJooqMapper, Field, SanksiSelects, DSLContext, Field

### Community 301 - "DetailDasarGajiQueryRepository.java"
Cohesion: 0.17
Nodes (12): DetailDasarGajiNominal, DetailDasarGajiQueryRepository, Condition, DSLContext, Field, Page, Repository, RequiredArgsConstructor (+4 more)

### Community 302 - "GajiTunjanganRecord"
Cohesion: 0.11
Nodes (4): GajiTunjanganRecord, Override, Record1, SuppressWarnings

### Community 303 - "GajiParameterSettingCommandService.java"
Cohesion: 0.08
Nodes (27): GajiParameterSettingCommandService, GajiParameterSettingController, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+19 more)

### Community 304 - "LampiranProfilCommandService"
Cohesion: 0.21
Nodes (10): DSLContext, Field, Page, Repository, RequiredArgsConstructor, JenjangPendidikanQueryRepository, Page, RequiredArgsConstructor (+2 more)

### Community 305 - "KeahlianQueryService.java"
Cohesion: 0.09
Nodes (24): KeahlianDetailQuery, KeahlianJooqMapper, KeahlianQuery, KeahlianQueryRepository, LampiranRow, KeahlianDetail, LampiranRow, Override (+16 more)

### Community 306 - "IdsAbstract"
Cohesion: 0.22
Nodes (11): DasarGaji, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+3 more)

### Community 307 - "CutiJenisRecord"
Cohesion: 0.17
Nodes (12): #1 kepegawaian-33s — fix revive ADR-0005 (RISIKO TERTINGGI) — DONE, #2 kepegawaian-jow — kunci keunikan (BUTUH KEPUTUSAN, label needs-info) — DONE, #3 kepegawaian-5ft — hapus dead code (aman, mekanis) — DONE, #4 kepegawaian-9tf — test pengaman (murni tambah test) — DONE, 🔴 BUG SUDAH AKTIF — terverifikasi, bukan risiko masa depan, Cara klaim & tutup (beads), Checklist eksekusi per issue, Claim Order — Deepening Modul Organisasi (master) (+4 more)

### Community 309 - "CutiJenis"
Cohesion: 0.08
Nodes (28): BiodataSelects, CutiJenisJooqMapper, CutiJenisMapper, CutiJenisPostRequest, CutiJenisRepository, Profil Cqrs Implementation Patterns, PendidikanSelects, ProfileUpdateService (+20 more)

### Community 310 - "GajiPotonganTkkRecord"
Cohesion: 0.12
Nodes (4): GajiPotonganTkkRecord, Override, Record1, SuppressWarnings

### Community 311 - "HariLiburRecord"
Cohesion: 0.26
Nodes (8): SanksiPostRequest, Data, JsonIgnore, Specification, SanksiPostRequest, Data, EqualsAndHashCode, SanksiPutRequest

### Community 312 - "SpecificationBuilder"
Cohesion: 0.31
Nodes (9): AuthenticationEntryPoint, AuthenticationException, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Slf4j (+1 more)

### Community 313 - "GajiPhdpCommandService.java"
Cohesion: 0.09
Nodes (28): GajiPhdpCommandService, GajiPhdpPostRequest, GajiPhdpRepository, GajiPhdpController, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+20 more)

### Community 314 - "PengalamanKerjaQueryService.java"
Cohesion: 0.07
Nodes (31): PengalamanKerjaQueryRepository, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+23 more)

### Community 315 - "GajiBatchRootController.java"
Cohesion: 0.22
Nodes (8): Biodata, Golongan, Grade, Jabatan, KodePajak, Organisasi, PegawaiResponse, Profesi

### Community 316 - "ProfilKeluargaJooqMapperTest"
Cohesion: 0.21
Nodes (8): DSLContext, Field, Test, ProfilKeluargaJooqMapperTest, DSLContext, Field, Test, PendidikanJooqMapperTest

### Community 317 - "DasarGajiController.java"
Cohesion: 0.16
Nodes (15): DasarGajiPostRequest, DasarGajiRepository, DasarGajiController, Page, RequestMapping, RequiredArgsConstructor, RestController, DasarGajiPostRequest (+7 more)

### Community 318 - "BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`)"
Cohesion: 0.10
Nodes (20): 1. `GET /pegawai/{id}/mutasi-context`, 2. `GET /master/profesi/jabatan/{id}`, 3. Konfirmasi — snapshot nilai "Lama" (`*LamaId`), 4. `GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` — sudah ada, 2 hal perlu dikonfirmasi, 4a. Konfirmasi arti `masaKerja`, 4b. Response membocorkan entity JPA mentah, 5. Konteks — matriks visibilitas field (FYI, tidak butuh perubahan BE), BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`) (+12 more)

### Community 319 - "Master Query Optimization Pattern"
Cohesion: 0.10
Nodes (20): 1. Prinsip, 2. Lapisan Arsitektur, 3. Pola per Endpoint, 3a. List / Dropdown (GET /list), 3b. Index / Page (GET /), 3c. Detail (GET /{id}), 4. Aturan Penting, 4b. Kolom yang tidak dipakai DTO jangan di-select (+12 more)

### Community 320 - "JenjangPendidikanController.java"
Cohesion: 0.10
Nodes (24): JenjangPendidikanPostRequest, JenjangPendidikanRepository, Page, RequestMapping, RequiredArgsConstructor, RestController, JenjangPendidikanController, Data (+16 more)

### Community 321 - "AppwriteClientTest"
Cohesion: 0.23
Nodes (4): Override, getName(), AppwriteClientTest, Test

### Community 322 - "RiwayatKontrakQueryRepository.java"
Cohesion: 0.14
Nodes (17): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, RiwayatKontrakController, RiwayatKontrakQuery, DSLContext (+9 more)

### Community 323 - "PendidikanAud.java"
Cohesion: 0.18
Nodes (12): Condition, Field, ForeignKey, Override, PlainSQL, Schema, Select, SQL (+4 more)

### Community 324 - "ADR-0003"
Cohesion: 0.11
Nodes (20): ADR-0003, ADR-0004, Consequences, Considered Options, 0032 Squash Migration Baseline Bersih Dari Dump Db Existing, Konteks, Squash migration jadi baseline bersih, di-derive dari dump DB kepegawaian existing, Catatan risiko (+12 more)

### Community 325 - "IdsAbstract"
Cohesion: 0.03
Nodes (103): IdsAbstract, IdsAbstract, AllArgsConstructor, Audited, EntityListeners, Getter, MappedSuperclass, Override (+95 more)

### Community 326 - "BiodataDashboardQueryTest"
Cohesion: 0.32
Nodes (5): PendidikanDashboard, BiodataDashboardQueryTest, DSLContext, Field, Test

### Community 327 - "RiwayatTerminasiAud.java"
Cohesion: 0.15
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 328 - "CutiKuotaTemplateBuilder.java"
Cohesion: 0.06
Nodes (27): ByteArrayResource, Font, Row, ECutiPeriod, JAN_JUN, JUL_DES, JUN_JUL, NEXT_YEAR (+19 more)

### Community 329 - "Claim Order — Security: Dev Chain Validasi Bearer Token + Fallback DevAuth (ADR-0033)"
Cohesion: 0.11
Nodes (17): Consequences, Considered Options, Dev chain memvalidasi Bearer token, fallback Dev User hanya saat tanpa Bearer, Keputusan, Konteks, A. Klaim berurutan (master list), B. Semantik target (acceptance semua child), C. Pre-flight checklist (sekali sebelum mulai) (+9 more)

### Community 331 - "CutiApprovalChainRecord"
Cohesion: 0.36
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, RiwayatKontrakAudPath

### Community 332 - "GolonganRecord"
Cohesion: 0.36
Nodes (8): getCatalog(), getLiteral(), getName(), getSchema(), Catalog, Override, Schema, lookupLiteral()

### Community 333 - "📌 Issue Details"
Cohesion: 0.11
Nodes (18): 1a — kepegawaian-scn · Phase 1, 1b — kepegawaian-sqf · Phase 1, 1c — kepegawaian-39o · Phase 1, 2a — kepegawaian-hit · Phase 2, 2b — kepegawaian-rq2 · Phase 2, 3 — kepegawaian-llq · Phase 3, 4 — kepegawaian-y7u.1 · Phase 4, 4b — kepegawaian-y7u.2 · Phase 4 (+10 more)

### Community 334 - "Mail Service — Code Patterns (Verified Analysis)"
Cohesion: 0.11
Nodes (18): 0. How to read this document, 10. Confirmed pre-existing bugs (do NOT fix without a beads issue), 1. CQRS-lite: Command / Query split, 2. JOOQ read pattern — single-query pagination via window function, 3. Sqid opaque external IDs, 4. Pagination base classes, 4a. DIVERGENCE — two pagination response shapes, 5. Soft delete (+10 more)

### Community 335 - "LampiranProfil"
Cohesion: 0.14
Nodes (16): Data, JsonIgnore, Specification, LampiranProfilAcceptRequest, AllArgsConstructor, Audited, Entity, Getter (+8 more)

### Community 336 - "OpenApiConfig"
Cohesion: 0.28
Nodes (6): EnableWebMvc, GroupedOpenApi, OpenAPI, Bean, Configuration, OpenApiConfig

### Community 337 - "KartuIdentitasQueryService.java"
Cohesion: 0.08
Nodes (28): KartuIdentitasCommandService, KartuIdentitasQueryRepository, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+20 more)

### Community 338 - "Penggajian Cqrs Claim Order"
Cohesion: 0.08
Nodes (30): ADR-0024, Consequences, Considered Options, 0024 Gajibatchroot Kafka Diisolasi Ke Eventpublisher, Publikasi Kafka GajiBatchRoot diisolasi ke GajiBatchRootEventPublisher, dipublish after-commit, Batch pemrosesan gaji, Context — Modul Penggajian (Payroll & Batch Pemrosesan Gaji), Language Penggajian (+22 more)

### Community 339 - "JenisKitasPostRequest"
Cohesion: 0.29
Nodes (4): GradeQuery, GradeJooqMapper, GradeSelects, Field

### Community 340 - "JenisSpCommandServiceTest"
Cohesion: 0.15
Nodes (10): Override, Override, Result, PegawaiRingkasanMapper, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest (+2 more)

### Community 341 - ".build"
Cohesion: 0.09
Nodes (30): ConstraintViolation, 0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail, Consequences, Considered Options, Context, Decision, 0013 Symmetric Apiresponse Error Envelope, EntityNotFoundException (+22 more)

### Community 342 - "MimeTypesUtilsImpl"
Cohesion: 0.35
Nodes (3): Override, Service, MimeTypesUtilsImpl

### Community 343 - "StatusPegawaiController.java"
Cohesion: 0.23
Nodes (11): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, StatusPegawaiController, StatusPegawaiResponse, Service (+3 more)

### Community 344 - "CutiApprovalChain"
Cohesion: 0.06
Nodes (26): PegawaiMiniResponse, CutiApprovalMiniResponse, fromList(), PegawaiMiniResponse, EApprovalCutiStatus, APPROVED, CANCELED, CONFIRMED (+18 more)

### Community 346 - "GajiParameterSetting"
Cohesion: 0.11
Nodes (21): GajiParameterSettingPostRequest, GajiParameterSettingRepository, GajiParameterSettingPostRequest, Data, JsonIgnore, Specification, GajiParameterSettingPutRequest, GajiParameterSetting (+13 more)

### Community 348 - ".Jabatan"
Cohesion: 0.21
Nodes (5): CutiPegawaiJooqMapper, DSLContext, Field, Test, PegawaiSessionQueryRepositoryTest

### Community 349 - "GajiPendapatanNonPajak"
Cohesion: 0.12
Nodes (18): GajiPendapatanNonPajakPostRequest, GajiPendapatanNonPajakPostRequest, Data, JsonIgnore, Specification, GajiPendapatanNonPajakPutRequest, GajiPendapatanNonPajak, AllArgsConstructor (+10 more)

### Community 350 - "PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian"
Cohesion: 0.15
Nodes (13): Architectural Decisions, Further Notes, Implementation Decisions, Modul yang Di-test, Out of Scope, PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian, Prinsip Testing, Prior Art (+5 more)

### Community 351 - "LampiranProfilQueryService"
Cohesion: 0.40
Nodes (5): ADR-0020, Consequences, Considered Options, 0021 Pegawai Saga Atomik Dengan Sistem Eksternal, Saga tulis Pegawai membungkus pemanggilan Appwrite di dalam satu transaksi DB

### Community 352 - "MasterBaseEntity"
Cohesion: 0.20
Nodes (9): Grade, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete, Table (+1 more)

### Community 353 - "Serializable"
Cohesion: 0.12
Nodes (19): Serializable, GajiBatchRootErrorLogsResponse, PegawaiProfilUpdate, GajiBatchRoot, AllArgsConstructor, Entity, EntityListeners, Getter (+11 more)

### Community 354 - "RumahDinas"
Cohesion: 0.19
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+1 more)

### Community 355 - "MasterBaseEntity"
Cohesion: 0.12
Nodes (18): AllArgsConstructor, EntityListeners, Getter, MappedSuperclass, NoArgsConstructor, Override, Setter, SQLRestriction (+10 more)

### Community 356 - "CutiKlaimDetail"
Cohesion: 0.18
Nodes (9): CutiKlaimDetail, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString (+1 more)

### Community 357 - "DateHelper"
Cohesion: 0.40
Nodes (5): Consequences, Considered Options, Ekstraksi REST client Appwrite ke typed adapter AppwriteClient, Keputusan, Konteks

### Community 358 - ".delete_withChildSubJabatan_throwsConflict"
Cohesion: 0.29
Nodes (7): JdbcTemplate, JabatanCommandServiceTest, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test

### Community 359 - "Claim Order 2026 06 17 Analisis Bug"
Cohesion: 0.40
Nodes (3): SortField, Field, SortParam

### Community 360 - "AlasanBerhenti"
Cohesion: 0.21
Nodes (9): AlasanBerhenti, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete, Table (+1 more)

### Community 361 - "JenisKitas"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Override, Setter, SQLDelete, Table (+1 more)

### Community 362 - "Pelatihan"
Cohesion: 0.10
Nodes (22): JenisPelatihanRepository, PelatihanPostRequest, Data, PelatihanPostRequest, PelatihanPutRequest, AllArgsConstructor, Audited, Entity (+14 more)

### Community 363 - "DownloadPenggajian"
Cohesion: 0.27
Nodes (7): ResponseEntity, DownloadPenggajian, ByteArrayResource, Component, RequiredArgsConstructor, RestClient, Slf4j

### Community 364 - "0012 Jooq Codegen Via Generationtool Not Plugin"
Cohesion: 0.18
Nodes (11): ADR-0006, ADR-0015, Database, Consequences, Considered Options, 0012 Jooq Codegen Via Generationtool Not Plugin, JOOQ codegen dijalankan lewat GenerationTool di satu task imperatif, bukan plugin official, Keputusan (+3 more)

### Community 365 - "BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session"
Cohesion: 0.18
Nodes (10): 1. `GET /pegawai/{id}/session` — tambah field `statusPegawai`, 2. Konteks — bagaimana FE memakai field ini (FYI, tidak butuh perubahan BE), Alternatif yang dipertimbangkan (ditolak), BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session, Dampak, Definition of Done (BE), Kontak / referensi FE, Perubahan yang diminta (+2 more)

### Community 366 - "Modul yang Dibangun/Dimodifikasi"
Cohesion: 0.18
Nodes (11): M10: Penggajian Domain CQRS, M1: Flyway Infrastructure, M2: JOOQ Code Generation, M3: IdsAbstract Refactoring, M4: Entity Performance Hardening, M5: Master Domain CQRS (Pilot), M6: Profil Domain CQRS, M7: Pegawai Domain CQRS (+3 more)

### Community 368 - ".save"
Cohesion: 0.50
Nodes (4): Consequences, Considered Options, Hubungan dengan ADR-0008, Profesi.level tetap denormalisasi — sisi tulis memuat Jabatan penuh

### Community 369 - "JenisKeahlianPostRequest"
Cohesion: 0.83
Nodes (3): Data, EqualsAndHashCode, JenisSpPutRequest

### Community 370 - "JenisKeahlian"
Cohesion: 0.17
Nodes (12): Data, JsonIgnore, Specification, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter (+4 more)

### Community 371 - ".KeahlianAud"
Cohesion: 0.36
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, KeahlianAudPath

### Community 373 - ".handle"
Cohesion: 0.33
Nodes (8): AccessDeniedException, AccessDeniedHandler, DeniedHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor

### Community 374 - "Configuration"
Cohesion: 0.29
Nodes (7): Configuration, DefConfig, Configuration, Getter, Bean, Configuration, ThreadPoolConfig

### Community 375 - "RedisHelperTest"
Cohesion: 0.39
Nodes (7): DataRedisTest, GenericContainer, Import, StringRedisTemplate, Test, RedisHelperTest, Testcontainers

### Community 376 - "Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`"
Cohesion: 0.20
Nodes (9): 1. DTO — `PegawaiResponseSession.java`, 2. Repository — `PegawaiSessionQueryRepository.java`, 3. Verifikasi, Checklist Implementasi, Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`, Format di berbagai endpoint, Konteks & Keputusan Desain, Referensi File (+1 more)

### Community 378 - "Keputusan yang Disepakati"
Cohesion: 0.20
Nodes (10): 1. CQRS Split, 2. JOOQ Code Generation, 3. Flyway Strategy, 4. Service Layer Pattern, 5. Repository Structure, 6. Migration Priority, 7. Envers Three-Tier Audit, 8. Performance Improvements (+2 more)

### Community 379 - "AuthController.java"
Cohesion: 0.25
Nodes (11): AuthController, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestClient, RestController, Component (+3 more)

### Community 380 - "PelatihanController"
Cohesion: 0.08
Nodes (18): GetMapping, GetMapping, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+10 more)

### Community 382 - "Apd"
Cohesion: 0.22
Nodes (9): Apd, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete, Table (+1 more)

### Community 383 - "JenisPelatihan"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+1 more)

### Community 384 - ".PendidikanAud"
Cohesion: 0.47
Nodes (4): InverseForeignKey, Name, Table, PendidikanAudPath

### Community 385 - "GolonganWriteIT.java"
Cohesion: 0.33
Nodes (6): GolonganWriteIT, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Transactional

### Community 386 - "AuditConfig.java"
Cohesion: 0.39
Nodes (6): DateTimeProvider, EnableJpaAuditing, AuditConfig, AuditorAware, Bean, Configuration

### Community 387 - "0010 — Drop the @Version / version column from rewritten master entities"
Cohesion: 0.22
Nodes (9): 0010 — Drop the @Version / version column from rewritten master entities, Consequences, Context, Decision, 0010 Drop Version Column Master, Scope guard, ObjectOptimisticLockingFailureException, REV (+1 more)

### Community 388 - "Inventory: kepegawaian (Legacy) Schema Dump"
Cohesion: 0.22
Nodes (9): AUD Tables (42 → 30 setelah odb.2), Domain Tables (58), Dump, Inventory: kepegawaian (Legacy) Schema Dump, Notes, Orphan (dropped in odb.2 — master domain, no @Audited), Perbedaan dari Dump Sebelumnya (Salah), Tables Summary (+1 more)

### Community 389 - "Form Mutasi — Claim Order & Checklist"
Cohesion: 0.22
Nodes (8): 1. `form-mutasi: endpoint GET /pegawai/{id}/mutasi-context` (`kepegawaian-nil`), 2. `form-mutasi: endpoint GET /master/profesi/jabatan/{id}` (`kepegawaian-qly`), 3. Konfirmasi snapshot `*LamaId` & `masaKerja`, Catatan, Finalisasi, Form Mutasi — Claim Order & Checklist, P1 — Blocking FE (wajib dikerjakan), P2 — Konfirmasi

### Community 390 - "CQRS Migration Roadmap"
Cohesion: 0.22
Nodes (9): CQRS Migration Roadmap, Phase 1: Infrastructure & Hardening, Phase 2: Pilot Pattern (Tracer Bullet), Phase 3: Master Data Migration, Phase 4: Profil Data Migration, Phase 5: Core Pegawai, Phase 6: Kepegawaian (SK & Mutasi), Phase 7: Cuti & Penggajian (Transaction) (+1 more)

### Community 391 - "TestController.java"
Cohesion: 0.39
Nodes (7): Principal, GetMapping, PreAuthorize, RequestMapping, ResponseEntity, RestController, TestController

### Community 392 - "RedisConfig.java"
Cohesion: 0.42
Nodes (6): RedisConnectionFactory, RedisTemplate, Bean, Configuration, StringRedisTemplate, RedisConfig

### Community 393 - "Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration"
Cohesion: 0.32
Nodes (5): ADR-0002, Domain, Dokumentasi yang Dibuat, Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration, Next Steps

### Community 394 - "Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build"
Cohesion: 0.25
Nodes (8): ADR-0012, Consequences, Considered Options, 0015 Jooq Generated Sources Committed Manual Regen, Keputusan, Status, Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build, GenerationTool

### Community 395 - "context7"
Cohesion: 0.25
Nodes (7): headers, type, url, Authorization, mcp, context7, $schema

### Community 396 - "GajiBatchRootPostRequest"
Cohesion: 0.29
Nodes (5): GajiBatchRootPostRequest, Data, JsonIgnore, MultipartFile, GajiBatchRootMapper

### Community 398 - "ArchUnitTest.java"
Cohesion: 0.48
Nodes (5): AnalyzeClasses, ArchCondition, ArchRule, JavaMethod, ArchUnitTest

### Community 399 - "AuditAwareImpl"
Cohesion: 0.38
Nodes (5): AuditorAware, AuditAwareImpl, Component, Override, SuppressWarnings

### Community 400 - "0005 Revive On Create Soft Delete Unique"
Cohesion: 0.29
Nodes (7): Cara menemukan bangkai (yang disembunyikan @SQLRestriction), Consequences, Considered Options, 0005 Revive On Create Soft Delete Unique, Penegakan keunikan di bawah soft-delete, Revive-on-create untuk menegakkan UNIQUE di bawah soft-delete, UNIQUE

### Community 401 - "Graph Report - .  (2026-05-05)"
Cohesion: 0.29
Nodes (7): Community Hubs (Navigation), Corpus Check, God Nodes (most connected - your core abstractions), Graph Report - .  (2026-05-05), Knowledge Gaps, Summary, Surprising Connections (you probably didn't know these)

### Community 402 - "KafkaConfig.java"
Cohesion: 0.48
Nodes (5): NewTopic, Bean, Component, Slf4j, KafkaConfig

### Community 403 - "GajiKomponenJenisGaji.java"
Cohesion: 0.22
Nodes (12): GajiKomponenJenisGaji, NONE, PEMASUKAN, POTONGAN, getCatalog(), getLiteral(), getSchema(), Catalog (+4 more)

### Community 406 - "Test"
Cohesion: 0.36
Nodes (3): MinimalCutiRule, Test, MinimalCutiRuleTest

### Community 407 - "0018 Changedstatus Server Resolved By Role"
Cohesion: 0.33
Nodes (6): ADR-0016, CommandService, changedStatus is server-resolved by role, not sent by the client, 0018 Changedstatus Server Resolved By Role, PostRequest, PutRequest

### Community 408 - "JooqConfig.java"
Cohesion: 0.53
Nodes (4): DefaultConfigurationCustomizer, Bean, Configuration, JooqConfig

### Community 411 - ".restClient"
Cohesion: 0.53
Nodes (4): Bean, Component, RestClient, WebClientConfig

### Community 413 - "Context — Keputusan Rewrite: Modul Pegawai & Kepegawaian"
Cohesion: 0.40
Nodes (5): Context — Keputusan Rewrite: Modul Pegawai & Kepegawaian, Entity Mapping Convention: @Column(name) Revisi, Interface Cleanup Lintas-Modul, Keputusan Rewrite Modul Kepegawaian, Keputusan Rewrite Sisi-Tulis Pegawai

### Community 414 - "context7"
Cohesion: 0.40
Nodes (4): CONTEXT7_API_KEY, npx, context7, @upstash/context7-mcp

### Community 415 - "Perubahan Code yang Harus Dilakukan"
Cohesion: 0.40
Nodes (5): build.gradle, Entity Tier 1 (contoh Pegawai.java), Entity Tier 2 (contoh Golongan.java), IdsAbstract.java, Perubahan Code yang Harus Dilakukan

### Community 416 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 417 - "JabatanPutRequest"
Cohesion: 0.83
Nodes (3): JabatanPutRequest, Data, EqualsAndHashCode

### Community 418 - "OrganisasiPutRequest"
Cohesion: 0.83
Nodes (3): Data, EqualsAndHashCode, OrganisasiPutRequest

### Community 420 - "EReferensiPegawai"
Cohesion: 0.50
Nodes (3): EReferensiPegawai, BIODATA, PEGAWAI

### Community 422 - "GajiBatchRootRecord.java"
Cohesion: 0.67
Nodes (3): BiodataDashboardResponse changedStatus field, changedStatus server-resolved by role, ProfileUpdateService approval queue

### Community 436 - "CutiAllocationHelper.java"
Cohesion: 0.25
Nodes (5): CutiKuotaAllocationResult, Builder, Data, CutiKuotaAllocator, CutiAllocationHelper

## Knowledge Gaps
- **1225 isolated node(s):** `build-dev.sh script`, `copy.sh script`, `npx`, `@upstash/context7-mcp`, `CONTEXT7_API_KEY` (+1220 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **51 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `List` connect `DefaultSchema` to `Core Entities & Pagination`, `DTO Patterns & Builders`, `Relation Mappings & DSL`, `Domain Context Docs`, `PengalamanKerjaAudRecord`, `RecordMapper`, `KartuIdentitasAudRecord`, `LampiranSkAudRecord`, `StatistikPegawaiRecord`, `LampiranProfilRecord`, `PegawaiPath`, `CutiKuotaAudRecord`, `JabatanMiniResponse`, `GajiPotonganTkkAudRecord`, `CutiJenisAudRecord`, `DetailDasarGajiAudRecord`, `GajiPendapatanNonPajakAudRecord`, `RiwayatCutiAudRecord`, `CutiPegawai`, `Organisasi`, `RiwayatCutiRecord`, `GajiParameterSettingAudRecord`, `Jabatan`, `Biodata`, `Tables`, `RiwayatTerminasi`, `GajiParameterSettingRecord`, `GajiPhdpRecord`, `GajiProfilAudRecord`, `FlywaySchemaHistoryCopy1Record`, `RiwayatSp`, `Grade`, `RiwayatKontrakRecord`, `RiwayatSk`, `Golongan`, `SanksiSp`, `GajiBatchMaster`, `ProfilKeluarga`, `KartuIdentitas`, `RiwayatMutasi`, `AlasanBerhentiRecord`, `RumahDinasRecord`, `ApdRecord`, `GajiTunjangan`, `Keahlian`, `tables/Pelatihan.java`, `GajiPotonganTkk`, `JenisSp`, `AlatKerjaRecord`, `GajiBatchRoot`, `tables/GajiBatchRootLampiran.java`, `tables/GajiBatchRootErrorLogs.java`, `tables/PengalamanKerja.java`, `tables/CutiApprovalChain.java`, `tables/GajiProfil.java`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `tables/DetailDasarGaji.java`, `JenisKitasRecord`, `Level`, `Decisions Cuti`, `NotFoundException`, `tables/JenisKeahlian.java`, `tables/RumahDinas.java`, `GajiPhdpAudRecord`, `RiwayatMutasiAud.java`, `JenjangPendidikanResponse`, `CutiPegawaiAud.java`, `RiwayatSpAud.java`, `PelatihanAudRecord`, `GajiKomponenAud.java`, `LampiranSkRecord`, `UpdatableRecordImpl`, `Organisasi`, `KeahlianAud.java`, `LampiranProfilAudRecord`, `CutiApprovalRecord`, `JwtAuthFilter`, `OrganisasiQueryRepository.java`, `GajiBatchMasterProsesRecord`, `PendidikanQueryService.java`, `LampiranProfilController.java`, `JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset`, `DetailDasarGajiCommandService.java`, `DasarGajiQueryRepository.java`, `GajiProfilResponse`, `GajiPhdpResponse`, `JenisKitasQueryRepository.java`, `GolonganQueryRepository.java`, `JenisSpQueryRepository.java`, `PrefRole`, `APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone`, `GajiBatchRootCommandService.java`, `ProfilUpdateRecord`, `Profil Rewrite Claim Order`, `HariLiburQueryRepository.java`, `CutiJenisQueryRepository.java`, `GajiBatchMasterProsesResponse`, `EApprovalCutiStatus`, `ListResult`, `JenjangPendidikan`, `GajiPendapatanNonPajakResponse`, `.delete`, `ProfileUpdate`, `AppwriteClient`, `ProcessPotonganTkkImpl.java`, `RiwayatKontrakController.java`, `PageResult`, `GajiKomponen`, `GajiBatchRootLampiran`, `.toString`, `ConflictException`, `List`, `EJenisSk`, `JenisKontrakController.java`, `RiwayatSkQuery`, `GajiBatchPotonganTkkRecord`, `JabatanQueryRepository.java`, `BiodataPath`, `GajiBatchMasterResponse`, `JenisSp`, `Pendidikan`, `WebSecurity.java`, `PelatihanQueryService.java`, `SanksiQueryRepository.java`, `tables/GajiPendapatanNonPajak.java`, `tables/GajiKomponen.java`, `GitNexus — Code Intelligence`, `Keahlian`, `JenisKeahlianQueryRepository.java`, `RumahDinasQueryRepository.java`, `TableImpl`, `tables/JenisPelatihan.java`, `JenisPelatihanQueryRepository.java`, `CutiJenis`, `BiodataAud.java`, `DasarGajiAud.java`, `PegawaiAud.java`, `ProfilKeluargaAud.java`, `AlasanBerhentiQueryRepository.java`, `RiwayatSkAud.java`, `GradeQuery`, `FileUploadUtil`, `tables/HariLibur.java`, `BiodataQueryService.java`, `CutiKuota`, `DetailDasarGajiQueryRepository.java`, `GajiParameterSettingCommandService.java`, `LampiranProfilCommandService`, `KeahlianQueryService.java`, `PengalamanKerjaQueryService.java`, `DasarGajiController.java`, `JenjangPendidikanController.java`, `PendidikanAud.java`, `IdsAbstract`, `RiwayatTerminasiAud.java`, `CutiKuotaTemplateBuilder.java`, `OpenApiConfig`, `KartuIdentitasQueryService.java`, `JenisSpCommandServiceTest`, `.build`, `StatusPegawaiController.java`, `CutiApprovalChain`, `MasterBaseEntity`, `Serializable`, `CutiKlaimDetail`, `.delete_withChildSubJabatan_throwsConflict`?**
  _High betweenness centrality (0.175) - this node is a cross-community bridge._
- **Why does `LocalDate` connect `JabatanMiniResponse` to `Core Entities & Pagination`, `Relation Mappings & DSL`, `Domain Context Docs`, `Claim Order & ADRs`, `PagedRequest`, `RecordMapper`, `KartuIdentitasAudRecord`, `RiwayatSkRecord`, `BiodataRecord`, `PegawaiPath`, `VPegawaiRecord`, `CutiKuotaAudRecord`, `DasarGajiAudRecord`, `KartuIdentitasRecord`, `DasarGajiRecord`, `CutiPegawai`, `Biodata`, `RiwayatTerminasi`, `RiwayatSp`, `RiwayatKontrakRecord`, `RiwayatSk`, `ProfilKeluarga`, `KartuIdentitas`, `RiwayatMutasi`, `tables/Pelatihan.java`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `Decisions Cuti`, `RiwayatMutasiAud.java`, `CutiPegawaiAud.java`, `RiwayatSpAud.java`, `PelatihanAudRecord`, `Organisasi`, `ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ`, `Level`, `DasarGajiQueryRepository.java`, `RiwayatSpRecord`, `HariLiburQueryRepository.java`, `EApprovalCutiStatus`, `RiwayatTerminasiAudRecord`, `JenjangPendidikan`, `AuthServiceImplTest.java`, `RiwayatKeluarRecord`, `ProfilKeluargaAudRecord`, `HariLibur`, `List`, `EJenisSk`, `CutiPegawaiAudRecord`, `Keys`, `GitNexus — Code Intelligence`, `Keahlian`, `TableImpl`, `PegawaiQueryService`, `BiodataAud.java`, `DasarGajiAud.java`, `PegawaiAud.java`, `ProfilKeluargaAud.java`, `RiwayatSkAud.java`, `tables/HariLibur.java`, `BiodataQueryService.java`, `CutiKuota`, `KeahlianQueryService.java`, `IdsAbstract`, `DasarGajiController.java`, `IdsAbstract`, `BiodataDashboardQueryTest`, `RiwayatTerminasiAud.java`, `CutiKuotaTemplateBuilder.java`, `CutiApprovalChain`, `CutiKlaimDetail`, `Pelatihan`, `.createStyle`, `CutiPegawaiSelects`, `KafkaTemplate`, `PegawaiAudRecord.java`, `RiwayatMutasiAudRecord.java`, `RiwayatSpAudRecord.java`, `.key`, `.key`, `.key`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **Why does `Keys` connect `Golongan` to `Core Entities & Pagination`, `Penggajian Payroll Entities`, `Profil Biodata & Pendidikan`, `Cuti Leave Module`, `Claim Order & ADRs`, `PengalamanKerjaAudRecord`, `PegawaiAudRecord`, `GajiBatchMasterRecord`, `CutiPegawaiRecord`, `KartuIdentitasAudRecord`, `PendidikanAudRecord`, `RiwayatSkRecord`, `PelatihanRecord`, `BiodataRecord`, `SanksiSpRecord`, `RiwayatTerminasiRecord`, `RevinfoPath`, `KeahlianAudRecord`, `LampiranSkAudRecord`, `StatistikPegawaiRecord`, `LampiranProfilRecord`, `CutiKuotaAudRecord`, `DasarGajiAudRecord`, `KartuIdentitasRecord`, `GajiPotonganTkkAudRecord`, `CutiJenisAudRecord`, `DetailDasarGajiAudRecord`, `DasarGajiRecord`, `GajiPendapatanNonPajakAudRecord`, `RiwayatCutiAudRecord`, `Organisasi`, `RiwayatCutiRecord`, `GajiParameterSettingAudRecord`, `Jabatan`, `GajiPhdpRecord`, `GajiProfilAudRecord`, `FlywaySchemaHistoryCopy1Record`, `Grade`, `GajiProfilRecord`, `RiwayatKontrakRecord`, `AlasanBerhentiRecord`, `JenisSpRecord`, `ApdRecord`, `AlatKerjaRecord`, `tables/GajiBatchRootLampiran.java`, `tables/CutiApprovalChain.java`, `tables/CutiKlaimDetail.java`, `JenisKitasRecord`, `Level`, `JenisPelatihanRecord`, `JenisKeahlianRecord`, `tables/RumahDinas.java`, `CutiApprovalAudRecord`, `GajiPhdpAudRecord`, `PelatihanAudRecord`, `GajiKomponenAud.java`, `RiwayatKontrakAudRecord`, `LampiranSkRecord`, `UpdatableRecordImpl`, `LampiranProfilAudRecord`, `CutiApprovalRecord`, `RiwayatMutasiAudRecord`, `GajiBatchRootErrorLogsRecord`, `Profil CQRS Cleanup — Claim Order & Checklists`, `.getBiodata`, `GajiBatchMasterProsesRecord`, `Glossary`, `PrefRole`, `0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail`, `RiwayatSpAudRecord`, `RiwayatSpRecord`, `RiwayatMutasiRecord`, `ProfilUpdateRecord`, `RiwayatTerminasiAudRecord`, `RiwayatKeluarRecord`, `ProfilKeluargaAudRecord`, `CutiPegawaiAudRecord`, `PengalamanKerjaRecord`, `KeahlianRecord`, `GajiKomponenRecord`, `Keys`, `tables/GajiPendapatanNonPajak.java`, `JenjangPendidikanRecord`, `CutiJenis`, `RiwayatSkAud.java`, `DetailDasarGajiRecord`, `tables/HariLibur.java`, `BiodataAudRecord`, `GajiTunjanganRecord`, `GajiPotonganTkkRecord`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **What connects `build-dev.sh script`, `copy.sh script`, `npx` to the rest of the system?**
  _1225 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Core Entities & Pagination` be split into smaller, more focused modules?**
  _Cohesion score 0.02567739589016185 - nodes in this community are weakly interconnected._
- **Should `Many-to-Many & Base Entities` be split into smaller, more focused modules?**
  _Cohesion score 0.043509789702683106 - nodes in this community are weakly interconnected._
- **Should `List & Java Collections` be split into smaller, more focused modules?**
  _Cohesion score 0.0476582373185695 - nodes in this community are weakly interconnected._
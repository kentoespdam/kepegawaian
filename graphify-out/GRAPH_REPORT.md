# Graph Report - kepegawaian  (2026-08-10)

## Corpus Check
- 1231 files · ~374,528 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 14902 nodes · 43043 edges · 437 communities (379 shown, 58 thin omitted)
- Extraction: 81% EXTRACTED · 19% INFERRED · 0% AMBIGUOUS · INFERRED: 8159 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a3cb2f41`
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
- PegawaiRecord
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
- ProfesiSelects.java
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
- `AuthService` --references--> `Typed Controller Result`  [EXTRACTED]
  GRAPH_REPORT.md → docs/refactor/typed-controller-result.md
- `RiwayatSp` --references--> `Decisions Pegawai`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/decisions-pegawai.md
- `RiwayatSp` --references--> `Kepegawaian Cqrs Rewrite Claim Order`  [EXTRACTED]
  GRAPH_REPORT.md → docs/kepegawaian-cqrs-rewrite-claim-order.md
- `RiwayatSp` --references--> `Typed Controller Result`  [EXTRACTED]
  GRAPH_REPORT.md → docs/refactor/typed-controller-result.md
- `CutiKlaimDetail` --references--> `Decisions Cuti`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/decisions-cuti.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Biodata changedStatus Flow** — biodata_dashboard_response_changedstatus, changed_status_server_resolved, profile_update_service [INFERRED 0.85]

## Communities (437 total, 58 thin omitted)

### Community 0 - "Core Entities & Pagination"
Cohesion: 0.02
Nodes (160): AlatKerja, Apd, CutiApprovalChain, CutiJenis, CutiKlaimDetail, CutiKuota, Consequences, 0022 Label Snapshot Riwayat Findbyid (+152 more)

### Community 1 - "Many-to-Many & Base Entities"
Cohesion: 0.06
Nodes (18): from(), PegawaiPostRequest, SuppressWarnings, RiwayatSkAudRecord, RiwayatKontrak, RiwayatKontrakMapper, RiwayatSk, Condition (+10 more)

### Community 2 - "List & Java Collections"
Cohesion: 0.05
Nodes (34): PostMapping, ResponseEntity, JenisKitas, Level, GajiParameterSetting, Keahlian, Transactional, Transactional (+26 more)

### Community 3 - "DTO Patterns & Builders"
Cohesion: 0.08
Nodes (22): GetMapping, ResponseEntity, GetMapping, ResponseEntity, LaporanStatistikController, GetMapping, GetMapping, GetMapping (+14 more)

### Community 4 - "Validation & Error Handling"
Cohesion: 0.05
Nodes (20): SelectField, SortField, Field, SuppressWarnings, SortParam, from(), from(), from() (+12 more)

### Community 5 - "Relation Mappings & DSL"
Cohesion: 0.06
Nodes (20): from(), from(), from(), from(), from(), from(), from(), from() (+12 more)

### Community 6 - "Pegawai Join Queries"
Cohesion: 0.05
Nodes (51): Golongan, from(), from(), from(), from(), Golongan, AllArgsConstructor, Entity (+43 more)

### Community 7 - "Adapter & Config Mappers"
Cohesion: 0.07
Nodes (44): KenaikanBerkalaRequest, LaporanKepegawaianService, SingleResult, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+36 more)

### Community 8 - "Penggajian Payroll Entities"
Cohesion: 0.09
Nodes (7): from(), Override, Record1, SuppressWarnings, PendidikanRecord, Pendidikan, PendidikanMapper

### Community 9 - "Kepegawaian SK & SP"
Cohesion: 0.13
Nodes (8): GetMapping, from(), from(), GajiBatchRootLampiranRecord, Override, Record1, SuppressWarnings, Override

### Community 10 - "Enums & Constants"
Cohesion: 0.05
Nodes (46): ADR-010, AllowedFileTypeController, ApiError, ApiResponse, ApplicationEventPublisher, AppWriteAuthFilter, ArchivePublishedEvent, BETWEEN (+38 more)

### Community 11 - "Master References"
Cohesion: 0.01
Nodes (152): Communities, Community 0 - ".getId()", Community 100 - "OrganisasiRepository.java", Community 101 - "JenisPelatihanRepository.java", Community 102 - "JenisKeahlianRepository.java", Community 103 - "RumahDinasRepository.java", Community 104 - "JenisKitasRepository.java", Community 105 - "GradeRepository.java" (+144 more)

### Community 12 - "Profil Biodata & Pendidikan"
Cohesion: 0.07
Nodes (14): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+6 more)

### Community 13 - "Cuti Leave Module"
Cohesion: 0.06
Nodes (6): from(), GajiBatchRootRecord, Override, Record1, SuppressWarnings, GajiBatchRoot

### Community 14 - "Domain Context Docs"
Cohesion: 0.05
Nodes (58): Jabatan, JpaRepository, JpaSpecificationExecutor, PegawaiWriteback, ProfesiRepository, QueryByExampleExecutor, RevisionRepository, RiwayatSpRepository (+50 more)

### Community 15 - "Claim Order & ADRs"
Cohesion: 0.09
Nodes (7): from(), CutiKuotaRecord, SuppressWarnings, CutiKuotaMapper, CutiKuota, CutiKuota, Sheet

### Community 16 - "PagedRequest"
Cohesion: 0.04
Nodes (81): Direction, Pageable, PagedRequest, Getter, JsonIgnore, Pageable, Setter, PagedRequest (+73 more)

### Community 19 - ".toQuery"
Cohesion: 0.12
Nodes (14): SanksiQuery, JenisSpSimple, SanksiJenisSpList, SanksiJooqMapper, Field, SanksiSelects, GradeJooqMapperTest, DSLContext (+6 more)

### Community 22 - "RecordMapper"
Cohesion: 0.07
Nodes (21): PendidikanJooqMapper, RecordMapper, BiodataDetail, KartuIdentitasQuery, PendidikanQuery, PengalamanKerjaQuery, BiodataDetailJooqMapper, Override (+13 more)

### Community 23 - "KartuIdentitasAudRecord"
Cohesion: 0.08
Nodes (7): from(), Override, Record2, SuppressWarnings, KartuIdentitasAudRecord, KartuIdentitas, KartuIdentitasMapper

### Community 24 - "Cuti CQRS Rewrite — Claim Order & Checklists"
Cohesion: 0.14
Nodes (24): Acceptance, Acceptance (final modul), Cuti CQRS Rewrite — Claim Order & Checklists, FASE 0 — Pra-implementasi (setup beads), FASE 10 — Pengajuan Command (Keputusan #1, #6, #8, #9), FASE 11 — Approval Command state-machine (Keputusan #6), FASE 12 — Klaim Command + allocator klaim 1:1 (Keputusan #16, #10), FASE 13 — Controllers (Keputusan #13) (+16 more)

### Community 25 - "DasarGaji"
Cohesion: 0.04
Nodes (75): KontrakBootstrapPort, Pegawai, PegawaiPostRequest, RiwayatKontrakPostRequest, RiwayatSk, SkBootstrapPort, RequestMapping, RequiredArgsConstructor (+67 more)

### Community 27 - "RiwayatSkRecord"
Cohesion: 0.05
Nodes (4): Override, Record1, SuppressWarnings, RiwayatSkRecord

### Community 28 - "Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring"
Cohesion: 0.29
Nodes (7): Acceptance ringkas per issue, Cara update checklist, Dependency map (ringkas), Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring, REF, WAVE 0 — Epic (gerbang, tidak dikerjakan langsung), WAVE 1 — Eksekusi paralel (2 issue, tidak saling blok)

### Community 31 - "SanksiSpRecord"
Cohesion: 0.10
Nodes (5): Override, Record1, SuppressWarnings, SanksiSpRecord, SanksiMapper

### Community 33 - "RevinfoPath"
Cohesion: 0.08
Nodes (27): DasarGajiAudPath, GajiBatchRootAudPath, GajiPhdpAudPath, GajiTunjanganAudPath, Override, Record1, SuppressWarnings, RevinfoRecord (+19 more)

### Community 34 - "KeahlianAudRecord"
Cohesion: 0.06
Nodes (4): Override, Record2, SuppressWarnings, KeahlianAudRecord

### Community 35 - "LampiranSkAudRecord"
Cohesion: 0.06
Nodes (9): ForeignKey, InverseForeignKey, Name, Table, LampiranSkAudPath, Override, Record2, SuppressWarnings (+1 more)

### Community 37 - "LampiranProfilRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, LampiranProfilRecord

### Community 38 - "PegawaiPath"
Cohesion: 0.09
Nodes (12): PostMapping, ResponseEntity, CutiPengajuanController, PostMapping, PutMapping, RequestMapping, ResponseEntity, RestController (+4 more)

### Community 39 - "VPegawaiRecord"
Cohesion: 0.06
Nodes (15): SuppressWarnings, VPegawaiRecord, Condition, Field, Name, Override, PlainSQL, Schema (+7 more)

### Community 40 - "CutiKuotaAudRecord"
Cohesion: 0.05
Nodes (16): CutiKuotaAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 41 - "DasarGajiAudRecord"
Cohesion: 0.10
Nodes (6): DasarGajiAudRecord, Override, Record2, SuppressWarnings, DasarGajiMapper, DasarGaji

### Community 42 - "KartuIdentitasRecord"
Cohesion: 0.06
Nodes (16): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 43 - "JabatanMiniResponse"
Cohesion: 0.11
Nodes (33): AlasanBerhentiResponse, CutiJenisMiniResponse, GradeMiniResponse, JabatanMiniResponse, JenisSpMiniResponse, OrganisasiMiniResponse, SanksiMiniResponse, CutiJenisMiniResponse (+25 more)

### Community 44 - "GajiPotonganTkkAudRecord"
Cohesion: 0.09
Nodes (4): GajiPotonganTkkAudRecord, Override, Record2, SuppressWarnings

### Community 45 - "CutiJenisAudRecord"
Cohesion: 0.09
Nodes (4): CutiJenisAudRecord, Override, Record2, SuppressWarnings

### Community 46 - "DetailDasarGajiAudRecord"
Cohesion: 0.06
Nodes (20): DetailDasarGajiAud, DetailDasarGajiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 47 - ".getId"
Cohesion: 0.02
Nodes (130): ConstraintViolationException, PostMapping, PutMapping, ResponseEntity, PostMapping, PreAuthorize, PutMapping, ResponseEntity (+122 more)

### Community 48 - "DasarGajiRecord"
Cohesion: 0.09
Nodes (4): DasarGajiRecord, Override, Record1, SuppressWarnings

### Community 49 - "GajiPendapatanNonPajakAudRecord"
Cohesion: 0.08
Nodes (9): GajiPendapatanNonPajakAudPath, ForeignKey, InverseForeignKey, Name, Table, GajiPendapatanNonPajakAudRecord, Override, Record2 (+1 more)

### Community 50 - "RiwayatCutiAudRecord"
Cohesion: 0.16
Nodes (15): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+7 more)

### Community 51 - "CutiPegawai"
Cohesion: 0.02
Nodes (85): CutiApproval, CutiApprovalPath, Index, Schema, SuppressWarnings, TableField, CutiJenisPath, CutiPegawai (+77 more)

### Community 52 - "Organisasi"
Cohesion: 0.10
Nodes (4): Override, Record1, SuppressWarnings, OrganisasiRecord

### Community 53 - "RiwayatCutiRecord"
Cohesion: 0.15
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 54 - "GajiParameterSettingAudRecord"
Cohesion: 0.06
Nodes (16): GajiParameterSettingAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 55 - "Jabatan"
Cohesion: 0.06
Nodes (16): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 56 - "GajiPendapatanNonPajakRecord"
Cohesion: 0.10
Nodes (7): RumahDinasResponse, JabatanJooqMapper, SuppressWarnings, ProfesiJooqMapper, SharedMappers, PegawaiDetailRecordMapper, PegawaiDetailRefMapper

### Community 57 - "Ringkasan Temuan"
Cohesion: 0.08
Nodes (26): ✅ Alive Selects (lengkap), Claim Order & Checklist, 🔵 Cross-Module DTO (Masih Dipakai — JANGAN Dihapus), [D1] Hapus Dead DTO — kepegawaian-0ox, [D2] Hapus Unused Import — kepegawaian-k29, [D3] Verifikasi Cross-Module — kepegawaian-5o6, [D4] Final Cleanup & Build — kepegawaian-aak, [D5] Cleanup Selects — Hapus Dead Field/Array + File — kepegawaian-aak (+18 more)

### Community 58 - "Analisis Project Kepegawaian"
Cohesion: 0.08
Nodes (26): 1. Pegawai (Data Utama Pegawai), 2. Profil (Data Pribadi), 3. Master Data (Referensi), 4. Cuti (Manajemen Cuti), 5. Kepegawaian (Administrasi Pegawai), 6. Penggajian (Payroll), Alur JWT + Appwrite, Analisis Project Kepegawaian (+18 more)

### Community 60 - "Tables"
Cohesion: 0.12
Nodes (23): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Validator, ValidatorFactory (+15 more)

### Community 61 - "RiwayatTerminasi"
Cohesion: 0.16
Nodes (8): Condition, Field, Identity, Override, PlainSQL, Select, SQL, UniqueKey

### Community 62 - "GajiParameterSettingRecord"
Cohesion: 0.17
Nodes (15): GajiParameterSetting, Condition, Field, Identity, Index, Name, Override, PlainSQL (+7 more)

### Community 63 - "GajiPhdpRecord"
Cohesion: 0.07
Nodes (14): Condition, Field, Index, Name, Override, PlainSQL, Select, SQL (+6 more)

### Community 64 - "GajiProfilAudRecord"
Cohesion: 0.07
Nodes (20): GajiProfilAud, GajiProfilAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 65 - "FlywaySchemaHistoryCopy1Record"
Cohesion: 0.17
Nodes (14): FlywaySchemaHistoryCopy1, Condition, Field, Index, Name, Override, PlainSQL, Schema (+6 more)

### Community 66 - "Master Record Refactor — Claim Order & Checklist"
Cohesion: 0.10
Nodes (24): Checklist, Column Set Arrays — ✅ SELESAI, Common Mistakes, Dependency Graph, E0: Foundation (kepegawaian-hkq) — ✅ SELESAI, E1: Flat Batch 1 (kepegawaian-5k9) — ✅ SELESAI, E2: Flat Batch 2 (kepegawaian-1xy) — ✅ SELESAI, E3: JenjangPendidikan (kepegawaian-1ws) — ✅ SELESAI (+16 more)

### Community 67 - "RiwayatSp"
Cohesion: 0.19
Nodes (10): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+2 more)

### Community 68 - "Grade"
Cohesion: 0.07
Nodes (22): Grade, GradePath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 69 - "GajiProfilRecord"
Cohesion: 0.11
Nodes (4): GajiProfilRecord, Override, Record1, SuppressWarnings

### Community 70 - "RiwayatKontrakRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, RiwayatKontrakRecord

### Community 71 - "RiwayatSk"
Cohesion: 0.13
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 72 - "Golongan"
Cohesion: 0.14
Nodes (4): GolonganRecord, Override, Record1, SuppressWarnings

### Community 73 - "Profil Record Refactor — Claim Order & Checklist"
Cohesion: 0.19
Nodes (21): Analisis, Aturan Penting (dari master-query-optimization-pattern.md), Checklist, Claim Order, File, P10: Final Verification, P1: Pendidikan, P2: Keahlian (+13 more)

### Community 74 - "SanksiSp"
Cohesion: 0.08
Nodes (10): GajiKomponenAudJenisGaji, NONE, PEMASUKAN, POTONGAN, SuppressWarnings, lookupLiteral(), GajiKomponenAudRecord, Override (+2 more)

### Community 75 - "GajiBatchMaster"
Cohesion: 0.07
Nodes (36): GajiBatchMaster, GajiBatchMasterPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+28 more)

### Community 76 - "ProfilKeluarga"
Cohesion: 0.13
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 77 - "KartuIdentitas"
Cohesion: 0.15
Nodes (16): CutiJenisAud, CutiJenisAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 78 - "RiwayatMutasi"
Cohesion: 0.06
Nodes (36): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+28 more)

### Community 79 - "AlasanBerhentiRecord"
Cohesion: 0.14
Nodes (18): AlasanBerhenti, AlasanBerhentiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 81 - "JenisSpRecord"
Cohesion: 0.12
Nodes (4): Override, Record1, SuppressWarnings, JenisSpRecord

### Community 82 - "ApdRecord"
Cohesion: 0.07
Nodes (22): Apd, ApdPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 83 - "GajiTunjangan"
Cohesion: 0.07
Nodes (21): GajiTunjangan, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+13 more)

### Community 84 - "Keahlian"
Cohesion: 0.15
Nodes (16): GajiPotonganTkkAud, GajiPotonganTkkAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 85 - "tables/Pelatihan.java"
Cohesion: 0.16
Nodes (8): Condition, Field, Identity, Override, PlainSQL, Select, SQL, UniqueKey

### Community 86 - "GajiPotonganTkk"
Cohesion: 0.07
Nodes (16): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 87 - "JenisSp"
Cohesion: 0.06
Nodes (37): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+29 more)

### Community 88 - "AlatKerjaRecord"
Cohesion: 0.05
Nodes (40): AlatKerja, AlatKerjaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+32 more)

### Community 89 - "Pegawai Record Refactor — Claim Order & Checklist"
Cohesion: 0.15
Nodes (19): Analisis, Aturan Penting, Checklist, Controller Response Types, CustomResult Method Reference, File, File Impact Summary, G1: PegawaiResponse (+11 more)

### Community 90 - "Claim Order — Adopsi Pattern Publication ke Modul Master"
Cohesion: 0.11
Nodes (19): A. Klaim berurutan (master list), B. Wave structure (urutan eksekusi + verifikasi), C. Pre-flight checklist (jalankan sekali sebelum mulai), Claim Order — Adopsi Pattern Publication ke Modul Master, D.1 Pre-flight per modul, D.2 Child paging/sort checklist, D.3 Child write-flow checklist, D.4 Sub-resource khusus (Apd/AlatKerja) (+11 more)

### Community 91 - "GajiBatchRoot"
Cohesion: 0.04
Nodes (57): GajiBatchRoot, GajiBatchRootPath, Condition, Field, ForeignKey, Index, InverseForeignKey, Name (+49 more)

### Community 92 - "tables/GajiBatchRootLampiran.java"
Cohesion: 0.15
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 93 - "tables/GajiBatchRootErrorLogs.java"
Cohesion: 0.14
Nodes (15): PegawaiProperties, GetMapping, Page, PatchMapping, PostMapping, PreAuthorize, PutMapping, RequestMapping (+7 more)

### Community 94 - "tables/PengalamanKerja.java"
Cohesion: 0.15
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 95 - "Specification"
Cohesion: 0.04
Nodes (50): JenjangPendidikanPostRequest, SpecificationBuilder, Page, RequestMapping, RequiredArgsConstructor, RestController, JenjangPendidikanController, RequestMapping (+42 more)

### Community 96 - "tables/CutiApprovalChain.java"
Cohesion: 0.08
Nodes (22): CutiApprovalChain, CutiApprovalChainPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 97 - "tables/GajiProfil.java"
Cohesion: 0.15
Nodes (18): GajiProfil, GajiProfilPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 98 - "tables/CutiKuota.java"
Cohesion: 0.13
Nodes (18): CutiKuota, CutiKuotaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 99 - "tables/CutiKlaimDetail.java"
Cohesion: 0.10
Nodes (22): CutiKlaimDetail, CutiKlaimDetailPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 100 - "tables/DetailDasarGaji.java"
Cohesion: 0.15
Nodes (12): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+4 more)

### Community 101 - "Page"
Cohesion: 0.08
Nodes (26): Page, RequestMapping, RequiredArgsConstructor, RestController, Validator, ValidatorFactory, RiwayatMutasiController, from() (+18 more)

### Community 102 - "JenisKitasRecord"
Cohesion: 0.07
Nodes (23): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+15 more)

### Community 103 - "Level"
Cohesion: 0.04
Nodes (42): GajiPotonganTkkPath, GajiTunjanganPath, Golongan, GolonganPath, Condition, Field, ForeignKey, Identity (+34 more)

### Community 104 - "JenisPelatihanRecord"
Cohesion: 0.09
Nodes (8): ForeignKey, SuppressWarnings, UniqueKey, Keys, BiodataAudRecord, Override, Record2, SuppressWarnings

### Community 105 - "JenisKeahlianRecord"
Cohesion: 0.13
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 106 - "Decisions Cuti"
Cohesion: 0.03
Nodes (92): ApprovalChain, ApprovalCutiCommand, CutiApprovalChainCustomRepositoryImpl, CutiApprovalChainIndexQuery, CutiApprovalChainRequest, CutiApprovalChainResponse, CutiApprovalChainService, CutiApprovalChainServiceImpl (+84 more)

### Community 107 - "NotFoundException"
Cohesion: 0.04
Nodes (53): Byte, CATEGORY, DefaultRecordMapper, Aturan keputusan, Consequences, Considered Options, 0025 Fetchinto Flat Jooqmapper Join Nested Master, JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset (+45 more)

### Community 108 - "tables/JenisKeahlian.java"
Cohesion: 0.03
Nodes (74): Biodata, BiodataPath, Condition, Field, ForeignKey, Index, InverseForeignKey, Name (+66 more)

### Community 109 - "tables/RumahDinas.java"
Cohesion: 0.15
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 110 - "Profil CQRS — Pola Implementasi per Layer"
Cohesion: 0.12
Nodes (17): 1. DTO, 1a. Request tulis — <Agg>PostRequest / <Agg>PutRequest, 1b. Request baca — <Agg>Request, 1c. Response baca — <Agg>Response / <Agg>Query, 2. Mapper — final, private ctor, BUKAN @Component, 2a. Write mapper — <Agg>Mapper (dipakai CommandService), 2b. Read mapper Pola A (flat) — static mapToResponse(Record), 2c. Read mapper Pola B (implements RecordMapper) — dipakai profil (+9 more)

### Community 111 - "CutiApprovalAudRecord"
Cohesion: 0.06
Nodes (16): CutiApprovalAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 112 - "GajiPhdpAudRecord"
Cohesion: 0.10
Nodes (4): GajiPhdpAudRecord, Override, Record2, SuppressWarnings

### Community 113 - "RiwayatMutasiAud.java"
Cohesion: 0.17
Nodes (15): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+7 more)

### Community 114 - "JenjangPendidikanResponse"
Cohesion: 0.18
Nodes (9): GetMapping, ProfilKeluargaDetail, Override, SuppressWarnings, ProfilKeluargaDetailJooqMapper, DSLContext, Repository, RequiredArgsConstructor (+1 more)

### Community 115 - "CutiPegawaiAud.java"
Cohesion: 0.17
Nodes (11): CutiPegawaiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+3 more)

### Community 116 - "RiwayatSpAud.java"
Cohesion: 0.17
Nodes (11): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+3 more)

### Community 118 - "Profesi"
Cohesion: 0.06
Nodes (32): CustomResult, DeletedResult, SavedResult, CutiKuotaController, GetMapping, PostMapping, PutMapping, RequestMapping (+24 more)

### Community 119 - "PegawaiRecord"
Cohesion: 0.04
Nodes (12): from(), Override, Record1, SuppressWarnings, PegawaiRecord, Golongan, Jabatan, Organisasi (+4 more)

### Community 120 - "GajiKomponenAud.java"
Cohesion: 0.15
Nodes (16): GajiKomponenAud, GajiKomponenAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 122 - "Graph Report"
Cohesion: 0.03
Nodes (62): Graph Report, AuditRevisionEntity, BiodataService, CutiApprovalChainCustomRepository, CutiApprovalServiceImplTest, CutiKuotaService, DasarGajiRepository, DetailDasarGajiRepository (+54 more)

### Community 123 - "LampiranSkRecord"
Cohesion: 0.08
Nodes (4): Override, Record1, SuppressWarnings, LampiranSkRecord

### Community 124 - "UpdatableRecordImpl"
Cohesion: 0.09
Nodes (15): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+7 more)

### Community 125 - "Organisasi"
Cohesion: 0.03
Nodes (105): LampiranSp, MasterBaseEntity, Organisasi, RiwayatMutasiPostRequest, Data, EqualsAndHashCode, RiwayatMutasiPutRequest, from() (+97 more)

### Community 126 - "KeahlianAud.java"
Cohesion: 0.15
Nodes (12): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+4 more)

### Community 127 - "LampiranProfilAudRecord"
Cohesion: 0.07
Nodes (6): Override, Record2, SuppressWarnings, LampiranProfilAudRecord, LampiranSk, LampiranProfil

### Community 128 - "CutiApprovalRecord"
Cohesion: 0.06
Nodes (16): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 129 - "EJenisLampiranProfil"
Cohesion: 0.06
Nodes (51): LampiranProfilPostRequest, Data, EqualsAndHashCode, KartuIdentitasLampiranPostRequest, Data, EqualsAndHashCode, KeahlianLampiranPostRequest, Data (+43 more)

### Community 130 - "Domain Docs"
Cohesion: 0.04
Nodes (45): ADR-0001, ADR-0002, AuditRevisionListener, DDLDatabase, Consequences, 0003 Envers Scoped To Penggajian Kepegawaian, Envers dibatasi ke modul penggajian dan kepegawaian, Consequences (+37 more)

### Community 131 - "ISSUE 2 — kepegawaian-buc (Phase B-D)"
Cohesion: 0.14
Nodes (15): A1 — move LevelRepository, A2 — move JenjangPendidikanRepository, A3 — verify, Acceptance (all must pass), Guardrails (apply on BOTH issues), ISSUE 1 — kepegawaian-j5i (Phase A), ISSUE 2 — kepegawaian-buc (Phase B-D), Level CQRS Migration — Claim Order & Checklists (+7 more)

### Community 132 - "Checklist Detail per Issue"
Cohesion: 0.07
Nodes (30): COMPLETED, DataAccessException, 10. kepegawaian-6h2 — LevelServiceImpl tidak CQRS — ✅ SHIPPED (multi-commit), 11. kepegawaian-ytz — PegawaiServiceImpl wildcard+eksplisit — ✅ SHIPPED (commit 9f00059), 1. kepegawaian-9v9 — Status enum pakai ordinal()  ✅ SHIPPED (commit a20914f), 2. kepegawaian-g2j (real ID kepegawaian-0jo) — logAndBuildFailure bocor e.getMessage() — OPEN, claimed 2026-06-22, 3. kepegawaian-0fe (real ID kepegawaian-f5i) — File upload di dalam tx → orphan — OPEN, claimed 2026-06-22, 4. kepegawaian-7rk (real ID kepegawaian-jgm) — processPotonganTkk di dalam tx — OPEN, claimed 2026-06-22 (+22 more)

### Community 133 - "LANGKAH KERJA"
Cohesion: 0.13
Nodes (15): 1 — Profesi (paling berisiko; kena ceiling 120 baris), 2 — Organisasi (self-ref), 3 — Jabatan (self-ref), 4 — JenisSp, 5 — Verifikasi, ⚠️ Ceiling — CODINGRULES §4 (max 120 baris), Guardrails, Konteks (baca dulu) (+7 more)

### Community 134 - "RiwayatMutasiAudRecord"
Cohesion: 0.02
Nodes (17): from(), from(), from(), Override, Record2, SuppressWarnings, RiwayatMutasiAudRecord, Override (+9 more)

### Community 135 - "GajiBatchRootErrorLogsRecord"
Cohesion: 0.09
Nodes (4): Override, Record2, SuppressWarnings, RiwayatCutiAudRecord

### Community 136 - "Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists"
Cohesion: 0.15
Nodes (13): Aturan ISDELETED per aggregate (WAJIB benar), Claim order, Guardrails (semua issue), ISSUE 10 — kepegawaian-awf.10 GajiBatchMaster, ISSUE 11 — kepegawaian-awf.11 GajiBatchMasterProses, ISSUE 12 — kepegawaian-awf.12 GajiBatchRoot (4-file split), Penggajian CQRS/JOOQ Rewrite — Claim Order & Checklists, Prinsip modul (baca sekali di awal) (+5 more)

### Community 137 - "Organisasi Claim Order"
Cohesion: 0.15
Nodes (13): AuditConfig, Cara klaim & tutup (beads), Catatan per-issue, Claim Order — Epic kepegawaian-irt, Irt Claim Order, irt/1 — kepegawaian-9g0 (INDEPENDEN, mulai dulu), irt/2 — kepegawaian-j4a (INDEPENDEN, blok irt/3), irt/3 — kepegawaian-c2q (butuh irt/2) (+5 more)

### Community 138 - "JwtAuthFilter"
Cohesion: 0.14
Nodes (18): OncePerRequestFilter, DevAuthFilter, Component, FilterChain, HttpServletRequest, HttpServletResponse, Override, Profile (+10 more)

### Community 139 - "Profil CQRS Cleanup — Claim Order & Checklists"
Cohesion: 0.04
Nodes (33): CutiJenisPostRequest, from(), CutiJenisPostRequest, Data, JsonIgnore, Specification, CutiJenisPutRequest, from() (+25 more)

### Community 140 - "Kepegawaian — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring"
Cohesion: 0.06
Nodes (38): ADR-0022, ADR-0023, ApplicationEvent, Consequences, Considered Options, 0007 Concrete Services No Interface, Service sebagai kelas konkret, tanpa interface + Impl, Arah dependency lintas-modul disearahkan lewat DIP port (SkBootstrapPort) (+30 more)

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
Cohesion: 0.17
Nodes (7): from(), from(), from(), BiodataMapper, Biodata, ProfilKeluarga, ProfilKeluargaMapper

### Community 145 - "FileUploadUtilImpl"
Cohesion: 0.08
Nodes (15): JooqCodegenTask, DefaultTask, Property, FileUploadUtilImpl, MultipartFile, Override, RequiredArgsConstructor, Service (+7 more)

### Community 146 - "ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ"
Cohesion: 0.07
Nodes (31): ProfilKeluargaPostRequest, from(), JenjangPendidikanMiniResponse, Data, JsonIgnore, Specification, ProfilKeluargaPostRequest, ProfilKeluargaPutRequest (+23 more)

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
Cohesion: 0.10
Nodes (21): AlatKerjaQuery, CutiPegawaiSelects, Consequences, Considered Options, 0030 Hapus Seeding Imperatif Setupmaster, Hapus jalur seeding imperatif setupMaster/, seeding data via Flyway, Catatan bukan-prioritas, Claim Order — Temuan Grilling Arsitektur (2026-07-09) (+13 more)

### Community 153 - "Worktree"
Cohesion: 0.22
Nodes (9): Worktree, Aturan, Buat ulang worktree legacy kalau terhapus, Catatan, Hapus worktree legacy kalau sudah tak dibutuhkan, Layout, Lihat daftar worktree, Perintah (+1 more)

### Community 154 - "Pegawai — Rewrite CQRS (JPA-write / JOOQ-read) — Claim Order & Monitoring"
Cohesion: 0.07
Nodes (31): Coding Rules, ADR-0017, ADR-0020, ADR-0021, CODINGRULES, Git mv + Edit Workflow (HARD INVARIANT), Workflow, Baca read-model Pegawai membaca tabel lintas modul langsung lewat JOOQ (+23 more)

### Community 155 - "Decisions — Modul Master (CQRS Cleanup)"
Cohesion: 0.06
Nodes (36): ADR-0013, ADR-0019, ADR-0025, 0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null, Consequences, Considered Options, Context, Decision (+28 more)

### Community 156 - "Checklist Implementasi"
Cohesion: 0.67
Nodes (3): AGENTS.md Agent Config, CLAUDE.md Canonical Guidance, GitNexus Code Intelligence

### Community 157 - "OrganisasiQueryRepository.java"
Cohesion: 0.07
Nodes (31): OrganisasiCommandService, OrganisasiJooqMapper, OrganisasiListResponse, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+23 more)

### Community 158 - "GajiBatchMasterProsesRecord"
Cohesion: 0.08
Nodes (17): GajiBatchMasterProsesJenisGaji, NONE, PEMASUKAN, POTONGAN, getCatalog(), getLiteral(), getName(), getSchema() (+9 more)

### Community 159 - "BiodataAudGolonganDarah"
Cohesion: 0.10
Nodes (25): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, LaporanKontrakController, GetMapping, RequestMapping (+17 more)

### Community 160 - "BiodataGolonganDarah.java"
Cohesion: 0.36
Nodes (8): getCatalog(), getLiteral(), getName(), getSchema(), Catalog, Override, Schema, lookupLiteral()

### Community 161 - "Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist"
Cohesion: 0.11
Nodes (22): BiodataPostRequest, BiodataPostRequest, Data, JsonIgnore, Specification, BiodataPutRequest, EGolonganDarah, A (+14 more)

### Community 162 - "Glossary"
Cohesion: 0.12
Nodes (14): HttpHeaders, PrefRole, GetMapping, Page, PatchMapping, PostMapping, PreAuthorize, RequestMapping (+6 more)

### Community 163 - "0008 Fk Via Getreference On Write"
Cohesion: 0.15
Nodes (13): ADR-0008, DataIntegrityViolationException, Attach FK relasi via getReferenceById, bukan findById, Consequences, Considered Options, 0008 Fk Via Getreference On Write, Consequences, Considered Options (+5 more)

### Community 164 - "PrefRole"
Cohesion: 0.14
Nodes (17): Condition, Field, Name, Override, PlainSQL, Schema, Select, SQL (+9 more)

### Community 165 - "GajiKomponenAudJenisGaji"
Cohesion: 0.24
Nodes (9): getCatalog(), getLiteral(), getName(), getSchema(), Catalog, Override, Schema, ChangedStatusPlacementTest (+1 more)

### Community 166 - "PendidikanQueryService.java"
Cohesion: 0.04
Nodes (47): BiodataDetailRowMapper, BiodataRowMapper, Profil Cqrs Cleanup Claim Order, KartuIdentitasMultisetMapper, KartuIdentitasRowMapper, KeahlianRowMapper, Keluarga, LampiranProfilQuery (+39 more)

### Community 167 - "LampiranProfilController.java"
Cohesion: 0.10
Nodes (4): Override, Record1, SuppressWarnings, RiwayatCutiRecord

### Community 168 - "ADR-0017 — Claim Order & Monitoring"
Cohesion: 0.11
Nodes (25): ADR-0011, ADR-0017 — Claim Order & Monitoring, Cara update checklist, Dependency map (ringkas), Adr 0017 Claim Order, Issue khusus (pola beda — baca design issue penuh), WAVE 0 — Exemplar (1 issue, GERBANG), WAVE 1 — Replikasi (13 issue paralel, semua butuh s55) (+17 more)

### Community 169 - "Issue tracker: beads + GitHub"
Cohesion: 0.33
Nodes (6): beads conventions (default for task tracking), Issue Tracker, GitHub conventions (published issues / PRDs), Issue tracker: beads + GitHub, When a skill says "fetch the relevant ticket", When a skill says "publish to the issue tracker"

### Community 170 - "JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset"
Cohesion: 0.12
Nodes (5): from(), Override, Record1, SuppressWarnings, ProfilUpdateRecord

### Community 171 - "GajiBatchRoot"
Cohesion: 0.17
Nodes (15): GajiBatchMasterProses, Condition, Field, Identity, Index, Name, Override, PlainSQL (+7 more)

### Community 172 - "Level"
Cohesion: 0.04
Nodes (50): EnumType, EStatusKerja, EStatusPegawai, Component, ConfigurationProperties, Data, PegawaiProperties, Data (+42 more)

### Community 173 - "DefaultSchema"
Cohesion: 0.32
Nodes (5): CatalogImpl, DefaultCatalog, Override, Schema, SuppressWarnings

### Community 174 - "ProfesiController"
Cohesion: 0.10
Nodes (22): Consequences, Considered Options, 0001 Jpa Write Jooq Read Cqrs, Pemisahan jalur Command (JPA) dan Query (JOOQ), Profesi Apd Alatkerja Page Claim Order, ProfesiCommandService, ProfesiIndexQuery, ProfesiListResponse (+14 more)

### Community 175 - "Coding Rules"
Cohesion: 0.17
Nodes (15): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+7 more)

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
Cohesion: 0.17
Nodes (15): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+7 more)

### Community 181 - "0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail"
Cohesion: 0.11
Nodes (4): GajiParameterSettingRecord, Override, Record1, SuppressWarnings

### Community 182 - "0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null"
Cohesion: 0.11
Nodes (4): FlywaySchemaHistoryCopy1Record, Override, Record1, SuppressWarnings

### Community 183 - "RiwayatSpAudRecord"
Cohesion: 0.05
Nodes (4): Override, Record2, SuppressWarnings, RiwayatSpAudRecord

### Community 184 - "DetailDasarGajiCommandService.java"
Cohesion: 0.13
Nodes (21): CutiApprovalJooqMapper, CutiApprovalMiniResponse, CutiApprovalRequest, CutiApprovalController, Page, RequestMapping, RequiredArgsConstructor, RestController (+13 more)

### Community 185 - "DasarGajiQueryRepository.java"
Cohesion: 0.12
Nodes (17): GetMapping, DasarGajiIndexQuery, Data, EqualsAndHashCode, DasarGajiResponse, DasarGajiJooqMapper, DasarGajiQueryRepository, Condition (+9 more)

### Community 186 - "GajiProfilResponse"
Cohesion: 0.05
Nodes (46): GajiKomponenCommandService, GajiKomponenMiniProjection, GajiKomponenController, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor (+38 more)

### Community 187 - "GajiPhdpResponse"
Cohesion: 0.14
Nodes (16): GajiPhdpIndexQuery, Data, EqualsAndHashCode, GajiPhdpResponse, GajiPhdpJooqMapper, GajiPhdpQueryRepository, Condition, DSLContext (+8 more)

### Community 188 - "SavedStatus"
Cohesion: 0.16
Nodes (9): Data, JsonIgnore, MultipartFile, Specification, RiwayatSpPostRequest, Condition, Transactional, Transactional (+1 more)

### Community 189 - "JenisKitasQueryRepository.java"
Cohesion: 0.12
Nodes (20): Page, RequestMapping, RequiredArgsConstructor, RestController, JenisKitasController, Data, EqualsAndHashCode, JenisKitasIndexQuery (+12 more)

### Community 190 - "GolonganQueryRepository.java"
Cohesion: 0.10
Nodes (25): GolonganCommandService, GolonganIndexQuery, GolonganQuery, GolonganQueryRepository, GolonganQueryService, GolonganController, Page, RequestMapping (+17 more)

### Community 191 - "JenisSpQueryRepository.java"
Cohesion: 0.08
Nodes (27): JenisSpQueryRepository, SanksiRow, Page, RequestMapping, RequiredArgsConstructor, RestController, JenisSpController, Data (+19 more)

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
Nodes (17): CutiKuotaJooqMapper, CutiKuotaPegawaiResponse, CutiKuotaQueryRepository, CutiKuotaRequest, CutiKuotaResponse, CutiKuotaSisa, CutiKuotaResponse, CutiKuotaJooqMapper (+9 more)

### Community 197 - "Flyway sebagai sumber kebenaran schema"
Cohesion: 0.50
Nodes (4): Consequences, Considered Options, 0002 Flyway Schema Source Of Truth, Flyway sebagai sumber kebenaran schema

### Community 198 - "KepegawaianApplication"
Cohesion: 0.60
Nodes (3): EnableJpaRepositories, SpringBootApplication, KepegawaianApplication

### Community 199 - "RiwayatMutasiRecord"
Cohesion: 0.15
Nodes (15): GajiParameterSettingPostRequest, GajiParameterSettingController, DeleteMapping, PostMapping, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+7 more)

### Community 201 - "KenaikanBerkalaRequest"
Cohesion: 0.14
Nodes (13): EFilterKenaikanBerkala, BULAN_INI, GTE_1, GTE_2, TAHUN_INI, EJenisKenaikanBerkala, SK_KENAIKAN_GAJI_BERKALA, SK_KENAIKAN_PANGKAT_GOLONGAN (+5 more)

### Community 202 - "DeletedResult"
Cohesion: 0.03
Nodes (60): DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, PostMapping, PreAuthorize, ResponseEntity, DeleteMapping (+52 more)

### Community 203 - ".toEntity"
Cohesion: 0.11
Nodes (14): from(), from(), from(), from(), JenisSp, JenisSpMapper, RumahDinas, GajiBatchMasterProses (+6 more)

### Community 204 - "GajiBatchRootCommandService.java"
Cohesion: 0.09
Nodes (27): FileUploadUtil, GajiBatchMasterPostRequest, PrefRoleRepository, Specification, Page, PreAuthorize, RequestMapping, RequiredArgsConstructor (+19 more)

### Community 205 - "ProfilUpdateRecord"
Cohesion: 0.17
Nodes (15): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+7 more)

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
Cohesion: 0.06
Nodes (47): AlasanBerhentiRepository, ConflictException, Organisasi Claim Order, GajiPotonganTkkRepository, GolonganMapper, GolonganRepository, GradeRepository, JenisKeahlianRepository (+39 more)

### Community 211 - "HariLiburQueryRepository.java"
Cohesion: 0.07
Nodes (25): HariLiburController, Page, RequestMapping, RequiredArgsConstructor, RestController, HariLiburIndexQuery, Data, EqualsAndHashCode (+17 more)

### Community 218 - "AppwriteUser"
Cohesion: 0.10
Nodes (26): AppwriteUser, SimpleGrantedAuthority, AppwriteClient, Component, RequiredArgsConstructor, RestClient, Slf4j, Component (+18 more)

### Community 219 - "CutiJenisQueryRepository.java"
Cohesion: 0.07
Nodes (34): BiodataSelects, CutiJenisCommandService, CutiJenisJooqMapper, CutiJenisMapper, CutiJenisQueryRepository, CutiJenisRequest, CutiJenisResponse, Profil Cqrs Implementation Patterns (+26 more)

### Community 220 - "GajiBatchMasterProsesResponse"
Cohesion: 0.08
Nodes (34): Penggajian Cqrs Claim Order, ENDPOINT, GajiBatchMasterJooqMapper, GajiBatchMasterProsesCommandService, GajiBatchMasterProsesJooqMapper, GajiBatchMasterProsesQueryService, GajiBatchMasterProsesServiceImpl, GajiBatchMasterQueryService (+26 more)

### Community 221 - "CutiKuotaDeductionResult"
Cohesion: 0.27
Nodes (6): CutiKuotaDeductionResult, Builder, Data, CutiKuotaDeductionAllocator, CutiKuotaDeductionAllocatorTest, Test

### Community 222 - "EApprovalCutiStatus"
Cohesion: 0.03
Nodes (90): CutiApproval, CutiApprovalChainGenerator, CutiApprovalChainRepository, CutiApprovalPostRequest, CutiApprovalRepository, CutiKlaimDetailRepository, CutiKuotaUpdateByCutiService, CutiPegawai (+82 more)

### Community 223 - "ListResult"
Cohesion: 0.18
Nodes (14): GajiBatchRootIndexQuery, Data, EqualsAndHashCode, GajiBatchRootQueryRepository, Condition, DSLContext, Field, Page (+6 more)

### Community 225 - "JenjangPendidikan"
Cohesion: 0.05
Nodes (76): BiodataRepository, ChangedStatusResolver, JenisKitasRepository, JenjangPendidikan, JenjangPendidikanRepository, KartuIdentitasRepository, KeahlianPostRequest, KeahlianPutRequest (+68 more)

### Community 226 - "GajiPendapatanNonPajakResponse"
Cohesion: 0.10
Nodes (24): GajiPendapatanNonPajakCommandService, GajiPendapatanNonPajakController, Page, RequestMapping, RequiredArgsConstructor, RestController, GajiPendapatanNonPajakIndexQuery, Data (+16 more)

### Community 227 - ".delete"
Cohesion: 0.12
Nodes (4): AlasanBerhentiRecord, Override, Record1, SuppressWarnings

### Community 228 - "ErrorCode"
Cohesion: 0.33
Nodes (6): ErrorCode, DB_ERROR, DUPLICATE_BATCH, INTERNAL, UNKNOWN_BATCH, Getter

### Community 229 - "ProfileUpdate"
Cohesion: 0.09
Nodes (24): ProfileUpdate, ProfileUpdateApprovalService, RevInfoService, AllArgsConstructor, Builder, Entity, Getter, NoArgsConstructor (+16 more)

### Community 230 - "AuthServiceImplTest.java"
Cohesion: 0.20
Nodes (11): RiwayatSpPostRequest, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, RiwayatSpController (+3 more)

### Community 231 - "AppwriteClient"
Cohesion: 0.17
Nodes (14): AppwriteUserPostRequest, RestClient, Bean, Component, RestClient, WebClientConfig, AppwriteUserPostRequest, Builder (+6 more)

### Community 234 - "StatistikPegawai"
Cohesion: 0.22
Nodes (8): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString, StatistikPegawai

### Community 235 - "ProcessPotonganTkkImpl.java"
Cohesion: 0.07
Nodes (34): GajiBatchPotonganTkkRepository, ProcessPotonganTkk, GajiBatchPotonganTkk, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter (+26 more)

### Community 236 - "RiwayatKontrakController.java"
Cohesion: 0.16
Nodes (16): Page, PostMapping, PreAuthorize, PutMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+8 more)

### Community 237 - "PageResult"
Cohesion: 0.05
Nodes (30): RiwayatTerminasiQuery, GetMapping, GetMapping, Page, Page, GetMapping, GetMapping, Page (+22 more)

### Community 238 - "GajiKomponen"
Cohesion: 0.05
Nodes (37): EJenisGaji, Data, EqualsAndHashCode, GajiBatchMasterProsesPostRequest, Data, JsonIgnore, Specification, GajiKomponenPostRequest (+29 more)

### Community 239 - "GajiBatchRootLampiran"
Cohesion: 0.12
Nodes (4): Override, Record1, SuppressWarnings, RumahDinasRecord

### Community 240 - ".toString"
Cohesion: 0.04
Nodes (55): JabatanPostRequest, JdbcTemplate, OrganisasiPostRequest, ProfesiPostRequest, PutMapping, ResponseEntity, PutMapping, JabatanPostRequest (+47 more)

### Community 241 - "ConflictException"
Cohesion: 0.04
Nodes (56): ADR-0014, ADR-0031, AlatKerjaCommandService, AlatKerjaController, AlatKerjaQueryService, ApdCommandService, ApdController, ApdQueryService (+48 more)

### Community 242 - "ProfilKeluargaAudRecord"
Cohesion: 0.06
Nodes (4): Override, Record2, SuppressWarnings, ProfilKeluargaAudRecord

### Community 243 - "HariLibur"
Cohesion: 0.11
Nodes (15): EJenisLibur, CUTI_BERSAMA, LIBUR_NASIONAL, Getter, HariLibur, AllArgsConstructor, Entity, Getter (+7 more)

### Community 244 - "EStatusCuti"
Cohesion: 0.25
Nodes (8): EStatusCuti, APPROVED, CANCELLED, CONFIRMED, REJECTED, RETURNED, WAIT_APPROVAL, Getter

### Community 245 - "EJenisTunjangan"
Cohesion: 0.07
Nodes (39): GajiTunjanganCommandService, GajiTunjanganJooqMapper, GajiTunjanganPostRequest, GajiTunjanganController, GetMapping, Page, PostMapping, PutMapping (+31 more)

### Community 246 - "List"
Cohesion: 0.05
Nodes (47): EGolonganDarah, JenisKitasResponse, JenjangPendidikanResponse, KartuIdentitasMiniResponse, JenisKitasResponse, JenjangPendidikanResponse, Data, PegawaiPatchProfil (+39 more)

### Community 247 - "EJenisSk"
Cohesion: 0.04
Nodes (63): EJenisSk, LampiranSkAcceptRequest, LampiranSkCommandService, LampiranSkQueryRepository, LampiranSkQueryService, GetMapping, RequestMapping, RequiredArgsConstructor (+55 more)

### Community 248 - "JenisKontrakController.java"
Cohesion: 0.04
Nodes (50): EnumOption, JenisKontrakQueryService, ListResult, GetMapping, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity (+42 more)

### Community 249 - "RiwayatSkQuery"
Cohesion: 0.29
Nodes (5): EntityManager, RequiredArgsConstructor, Service, Slf4j, RevInfoService

### Community 250 - "CutiPegawaiAudRecord"
Cohesion: 0.07
Nodes (4): CutiPegawaiAudRecord, Override, Record2, SuppressWarnings

### Community 254 - "GajiBatchPotonganTkkRecord"
Cohesion: 0.09
Nodes (19): GajiBatchPotonganTkk, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 255 - "PengalamanKerjaRecord"
Cohesion: 0.07
Nodes (6): Override, Record1, SuppressWarnings, PengalamanKerjaRecord, PengalamanKerja, PengalamanKerjaMapper

### Community 256 - "JabatanQueryRepository.java"
Cohesion: 0.10
Nodes (25): JabatanJooqMapper, JabatanQueryRepository, JabatanController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+17 more)

### Community 257 - "BiodataPath"
Cohesion: 0.36
Nodes (6): Condition, Field, ForeignKey, InverseForeignKey, Name, Table

### Community 258 - "KeahlianRecord"
Cohesion: 0.05
Nodes (16): Condition, Field, ForeignKey, Identity, InverseForeignKey, Name, Override, PlainSQL (+8 more)

### Community 259 - "GajiBatchMasterResponse"
Cohesion: 0.16
Nodes (16): GajiBatchMasterCommandService, GajiBatchMasterQueryRepository, GajiBatchMasterController, GetMapping, Page, PreAuthorize, RequestMapping, RequiredArgsConstructor (+8 more)

### Community 260 - "GajiProfil"
Cohesion: 0.13
Nodes (19): GajiProfilCommandService, GajiProfilPostRequest, GajiProfilController, DeleteMapping, Page, PostMapping, RequestMapping, RequiredArgsConstructor (+11 more)

### Community 261 - "JenisSp"
Cohesion: 0.09
Nodes (25): SanksiPostRequest, from(), Data, JsonIgnore, Specification, SanksiPostRequest, Data, EqualsAndHashCode (+17 more)

### Community 262 - "Pendidikan"
Cohesion: 0.18
Nodes (7): GolonganResponse, LevelResponse, GradeResponse, LevelResponse, GajiPotonganTkkResponse, GajiPotonganTkkJooqMapper, GajiTunjanganJooqMapper

### Community 263 - "WebSecurity.java"
Cohesion: 0.07
Nodes (32): ADMIN, ADR-0007, ADR-0029, AppwriteClient, AuthService, AuthServiceImpl, CamelCaseToUnderscoresNamingStrategy, DEV (+24 more)

### Community 264 - "PelatihanQueryService.java"
Cohesion: 0.11
Nodes (19): PelatihanQueryRepository, PelatihanQuery, Override, PelatihanJooqMapper, DSLContext, Repository, RequiredArgsConstructor, PelatihanDetailQuery (+11 more)

### Community 265 - "SanksiQueryRepository.java"
Cohesion: 0.15
Nodes (16): Data, EqualsAndHashCode, JsonIgnore, Specification, SanksiIndexQuery, SanksiQuery, DSLContext, Field (+8 more)

### Community 266 - ".between1JanAnd30Jun"
Cohesion: 0.13
Nodes (9): CutiKuotaAllocator, CutiAllocationHelper, Override, Override, Override, Override, Override, CutiKuotaAllocatorTest (+1 more)

### Community 267 - "GajiKomponenRecord"
Cohesion: 0.08
Nodes (9): GajiKomponenJenisGaji, NONE, PEMASUKAN, POTONGAN, SuppressWarnings, GajiKomponenRecord, Override, Record1 (+1 more)

### Community 268 - "Keys"
Cohesion: 0.10
Nodes (4): Override, Record1, SuppressWarnings, ProfilKeluargaRecord

### Community 269 - "tables/GajiPendapatanNonPajak.java"
Cohesion: 0.10
Nodes (4): GajiPendapatanNonPajakRecord, Override, Record1, SuppressWarnings

### Community 270 - "tables/GajiKomponen.java"
Cohesion: 0.13
Nodes (18): GajiKomponen, GajiKomponenPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 271 - "ProfilUpdateController.java"
Cohesion: 0.09
Nodes (27): Profil Record Refactor Claim Order, PendidikanQueryRepository, ProfileUpdateQuery, ProfilUpdateAcceptRequest, ProfilUpdateDetail, GetMapping, Page, RequestMapping (+19 more)

### Community 272 - "LampiranRow"
Cohesion: 0.14
Nodes (14): BiodataCommandService, MimeTypesUtils, PegawaiIdNipam, PegawaiRepository, MultipartFile, Page, RequiredArgsConstructor, PegawaiIdNipam (+6 more)

### Community 273 - "JenjangPendidikanRecord"
Cohesion: 0.13
Nodes (4): Override, Record1, SuppressWarnings, JenjangPendidikanRecord

### Community 274 - "GitNexus — Code Intelligence"
Cohesion: 0.19
Nodes (7): Condition, Field, Override, PlainSQL, Select, SQL, UniqueKey

### Community 275 - "Keahlian"
Cohesion: 0.06
Nodes (32): JenisKeahlianResponse, from(), JenisKeahlianResponse, KeahlianQuery, KeahlianResponse, EKualifikasi, BAIK, CUKUP (+24 more)

### Community 276 - "Knowledge — kepegawaian (PERUMDAMTS)"
Cohesion: 0.06
Nodes (34): 10. Issue Tracking, 11. Skills, 12. Commit Convention, 13. Pre-Ship Checklist, 14. Useful Links (auto-scraped by Freebuff), 1. Project Identity, 2. Modes of Operation, 3. Build & Run (+26 more)

### Community 277 - "JenisKeahlianQueryRepository.java"
Cohesion: 0.12
Nodes (20): Page, RequestMapping, RequiredArgsConstructor, RestController, JenisKeahlianController, Data, EqualsAndHashCode, JenisKeahlianIndexQuery (+12 more)

### Community 278 - "RumahDinasQueryRepository.java"
Cohesion: 0.14
Nodes (15): Data, EqualsAndHashCode, RumahDinasIndexQuery, RumahDinasListResponse, RumahDinasQuery, DSLContext, Field, Page (+7 more)

### Community 279 - "TableImpl"
Cohesion: 0.13
Nodes (19): DasarGaji, DasarGajiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+11 more)

### Community 280 - "tables/JenisPelatihan.java"
Cohesion: 0.06
Nodes (28): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+20 more)

### Community 281 - "PegawaiQueryService"
Cohesion: 0.11
Nodes (19): RefMiniResponse, RefMiniResponse, PegawaiResponseMutasiContext, PegawaiResponseSession, PegawaiTableResponse, PegawaiTableRecordMapper, DSLContext, Repository (+11 more)

### Community 282 - "JenisPelatihanQueryRepository.java"
Cohesion: 0.12
Nodes (20): Page, RequestMapping, RequiredArgsConstructor, RestController, JenisPelatihanController, Data, EqualsAndHashCode, JenisPelatihanIndexQuery (+12 more)

### Community 283 - "CutiJenis"
Cohesion: 0.07
Nodes (21): CutiJenis, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+13 more)

### Community 284 - "GradeRecord"
Cohesion: 0.05
Nodes (40): GajiBatchRootController, PatchMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, Getter (+32 more)

### Community 285 - "BiodataAud.java"
Cohesion: 0.17
Nodes (11): BiodataAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+3 more)

### Community 286 - "DasarGajiAud.java"
Cohesion: 0.17
Nodes (15): DasarGajiAud, Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL (+7 more)

### Community 287 - "PegawaiAud.java"
Cohesion: 0.15
Nodes (12): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+4 more)

### Community 288 - "ProfilKeluargaAud.java"
Cohesion: 0.15
Nodes (12): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+4 more)

### Community 289 - "PengalamanKerjaCommandService.java"
Cohesion: 0.17
Nodes (11): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Select (+3 more)

### Community 290 - "AlasanBerhentiQueryRepository.java"
Cohesion: 0.13
Nodes (19): AlasanBerhentiController, Page, RequestMapping, RequiredArgsConstructor, RestController, AlasanBerhentiIndexQuery, Data, EqualsAndHashCode (+11 more)

### Community 291 - "RiwayatSkAud.java"
Cohesion: 0.13
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 293 - "DetailDasarGajiRecord"
Cohesion: 0.10
Nodes (4): DetailDasarGajiRecord, Override, Record1, SuppressWarnings

### Community 294 - "GradeQuery"
Cohesion: 0.11
Nodes (23): GradeQuery, GradeController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+15 more)

### Community 295 - "FileUploadUtil"
Cohesion: 0.02
Nodes (118): ADR-0005, CutiKuotaRepositoryTest, Level Cqrs Claim Order, Master Pattern Claim Order, Master Response Pattern Guide, Organisasi Publication Pattern Claim Order, DSLContext, Edit (+110 more)

### Community 296 - "tables/HariLibur.java"
Cohesion: 0.07
Nodes (15): Condition, Field, Identity, Index, Name, Override, PlainSQL, Select (+7 more)

### Community 298 - "BiodataQueryService.java"
Cohesion: 0.10
Nodes (27): BiodataDetailQuery, BiodataQueryRepository, KartuIdentitasMultisetJooqMapper, BiodataDashboardResponse, BiodataDashboardQuery, DSLContext, Repository, RequiredArgsConstructor (+19 more)

### Community 299 - "CutiKuota"
Cohesion: 0.11
Nodes (22): CutiKuotaPostRequest, CutiKuotaImportRequest, Data, JsonIgnore, MultipartFile, Specification, CutiKuotaPostRequest, Data (+14 more)

### Community 300 - "GajiKomponenAudRecord"
Cohesion: 0.18
Nodes (11): GajiPendapatanNonPajakAud, Condition, Field, Override, PlainSQL, Schema, Select, SQL (+3 more)

### Community 301 - "DetailDasarGajiQueryRepository.java"
Cohesion: 0.07
Nodes (30): DetailDasarGajiPostRequest, DetailDasarGajiController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, RestController, DasarGajiMiniResponse (+22 more)

### Community 302 - "GajiTunjanganRecord"
Cohesion: 0.18
Nodes (11): Condition, Field, Override, PlainSQL, Schema, Select, SQL, SuppressWarnings (+3 more)

### Community 303 - "GajiParameterSettingCommandService.java"
Cohesion: 0.13
Nodes (15): GajiParameterSettingListRequest, Data, GajiParameterSettingResponse, GajiParameterSettingJooqMapper, GajiParameterSettingQueryRepository, Condition, DSLContext, Field (+7 more)

### Community 304 - "LampiranProfilCommandService"
Cohesion: 0.16
Nodes (10): Condition, Field, Identity, Name, Override, PlainSQL, Select, SQL (+2 more)

### Community 305 - "KeahlianQueryService.java"
Cohesion: 0.06
Nodes (33): [ ] #4 — CUTOVER + hapus shim lama · kepegawaian-94u.3 (blocked by #3), Aturan wajib tiap langkah (CODINGRULES), Lampiranprofil Cqrs Claim Order, lampiranProfil CQRS — Claim Order & Checklist, Session close (setelah semua hijau), [x] #2 — READ side · kepegawaian-94u.1 (READY), [x] #3 — WRITE side · kepegawaian-94u.2 (blocked by #2), KeahlianDetailQuery (+25 more)

### Community 306 - "IdsAbstract"
Cohesion: 0.22
Nodes (5): CriteriaBuilder, Root, SafeVarargs, Specification, SpecificationBuilder

### Community 307 - "CutiJenisRecord"
Cohesion: 0.17
Nodes (12): #1 kepegawaian-33s — fix revive ADR-0005 (RISIKO TERTINGGI) — DONE, #2 kepegawaian-jow — kunci keunikan (BUTUH KEPUTUSAN, label needs-info) — DONE, #3 kepegawaian-5ft — hapus dead code (aman, mekanis) — DONE, #4 kepegawaian-9tf — test pengaman (murni tambah test) — DONE, 🔴 BUG SUDAH AKTIF — terverifikasi, bukan risiko masa depan, Cara klaim & tutup (beads), Checklist eksekusi per issue, Claim Order — Deepening Modul Organisasi (master) (+4 more)

### Community 309 - "CutiJenis"
Cohesion: 0.06
Nodes (38): AlatKerjaRepository, ApdRepository, CutiJenisRepository, Master Delete Guard Claim Order, GajiKomponenPostRequest, GajiKomponenRepository, GajiPendapatanNonPajakRepository, GajiProfilRepository (+30 more)

### Community 310 - "GajiPotonganTkkRecord"
Cohesion: 0.13
Nodes (17): ADR-0026, ADR-0027, ADR-0028, CommonPageRequest, Consequences, Considered Options, 0026 Cleanup Commonpagerequest Memicu Rewrite Cqrs, Penghapusan CommonPageRequest memicu rewrite CQRS/JOOQ 4 modul, bukan swap-superclass (+9 more)

### Community 311 - "HariLiburRecord"
Cohesion: 0.18
Nodes (10): ApdRow, ProfesiDetail, AlatKerjaRow, ApdRow, DSLContext, Repository, RequiredArgsConstructor, ProfesiDetailQuery (+2 more)

### Community 312 - "SpecificationBuilder"
Cohesion: 0.31
Nodes (9): AuthenticationEntryPoint, AuthenticationException, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor, Slf4j (+1 more)

### Community 313 - "GajiPhdpCommandService.java"
Cohesion: 0.09
Nodes (27): GajiPhdpCommandService, GajiPhdpPostRequest, GajiPhdpRepository, GajiPhdpController, Page, RequestMapping, RequiredArgsConstructor, RestController (+19 more)

### Community 314 - "PengalamanKerjaQueryService.java"
Cohesion: 0.10
Nodes (22): PengalamanKerjaQueryRepository, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+14 more)

### Community 315 - "GajiBatchRootController.java"
Cohesion: 0.18
Nodes (9): Biodata, Golongan, Grade, Jabatan, KodePajak, Organisasi, PegawaiResponse, Profesi (+1 more)

### Community 316 - "ProfilKeluargaJooqMapperTest"
Cohesion: 0.21
Nodes (8): DSLContext, Field, Test, ProfilKeluargaJooqMapperTest, DSLContext, Field, Test, PendidikanJooqMapperTest

### Community 317 - "DasarGajiController.java"
Cohesion: 0.17
Nodes (15): DasarGajiPostRequest, DasarGajiController, Page, RequestMapping, RequiredArgsConstructor, RestController, DasarGajiPostRequest, Data (+7 more)

### Community 318 - "BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`)"
Cohesion: 0.10
Nodes (20): 1. `GET /pegawai/{id}/mutasi-context`, 2. `GET /master/profesi/jabatan/{id}`, 3. Konfirmasi — snapshot nilai "Lama" (`*LamaId`), 4. `GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` — sudah ada, 2 hal perlu dikonfirmasi, 4a. Konfirmasi arti `masaKerja`, 4b. Response membocorkan entity JPA mentah, 5. Konteks — matriks visibilitas field (FYI, tidak butuh perubahan BE), BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`) (+12 more)

### Community 319 - "Master Query Optimization Pattern"
Cohesion: 0.10
Nodes (20): 1. Prinsip, 2. Lapisan Arsitektur, 3. Pola per Endpoint, 3a. List / Dropdown (GET /list), 3b. Index / Page (GET /), 3c. Detail (GET /{id}), 4. Aturan Penting, 4b. Kolom yang tidak dipakai DTO jangan di-select (+12 more)

### Community 320 - "JenjangPendidikanController.java"
Cohesion: 0.08
Nodes (26): PendidikanPostRequest, Data, JsonIgnore, Specification, PendidikanPostRequest, PendidikanPutRequest, AllArgsConstructor, Entity (+18 more)

### Community 321 - "AppwriteClientTest"
Cohesion: 0.17
Nodes (7): AppwriteProperties, MockRestServiceServer, Override, getName(), AppwriteClientTest, BeforeEach, Test

### Community 322 - "RiwayatKontrakQueryRepository.java"
Cohesion: 0.19
Nodes (12): GetMapping, RiwayatKontrakQuery, DSLContext, Field, Page, Repository, RequiredArgsConstructor, RiwayatKontrakQueryRepository (+4 more)

### Community 323 - "PendidikanAud.java"
Cohesion: 0.21
Nodes (7): Condition, Field, Override, PlainSQL, Select, SQL, UniqueKey

### Community 324 - "ADR-0003"
Cohesion: 0.04
Nodes (48): ADR-0003, ADR-0004, ADR-0018, Cara menemukan bangkai (yang disembunyikan @SQLRestriction), Consequences, Considered Options, 0005 Revive On Create Soft Delete Unique, Penegakan keunikan di bawah soft-delete (+40 more)

### Community 325 - "IdsAbstract"
Cohesion: 0.03
Nodes (92): IdsAbstract, IdsAbstract, AllArgsConstructor, Audited, EntityListeners, Getter, MappedSuperclass, Override (+84 more)

### Community 326 - "BiodataDashboardQueryTest"
Cohesion: 0.32
Nodes (5): PendidikanDashboard, BiodataDashboardQueryTest, DSLContext, Field, Test

### Community 327 - "RiwayatTerminasiAud.java"
Cohesion: 0.32
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, RiwayatTerminasiAudPath

### Community 328 - "CutiKuotaTemplateBuilder.java"
Cohesion: 0.08
Nodes (25): ByteArrayResource, Font, Row, ExcelHelper, ByteArrayResource, CellStyle, Workbook, CutiKuotaTemplateBuilder (+17 more)

### Community 329 - "Claim Order — Security: Dev Chain Validasi Bearer Token + Fallback DevAuth (ADR-0033)"
Cohesion: 0.10
Nodes (18): Consequences, Considered Options, Dev chain memvalidasi Bearer token, fallback Dev User hanya saat tanpa Bearer, Keputusan, Konteks, A. Klaim berurutan (master list), B. Semantik target (acceptance semua child), C. Pre-flight checklist (sekali sebelum mulai) (+10 more)

### Community 331 - "CutiApprovalChainRecord"
Cohesion: 0.36
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, RiwayatKontrakAudPath

### Community 332 - "GolonganRecord"
Cohesion: 0.19
Nodes (14): BiodataAudGolonganDarah, A, AB, B, O, getCatalog(), getLiteral(), getName() (+6 more)

### Community 333 - "📌 Issue Details"
Cohesion: 0.11
Nodes (18): 1a — kepegawaian-scn · Phase 1, 1b — kepegawaian-sqf · Phase 1, 1c — kepegawaian-39o · Phase 1, 2a — kepegawaian-hit · Phase 2, 2b — kepegawaian-rq2 · Phase 2, 3 — kepegawaian-llq · Phase 3, 4 — kepegawaian-y7u.1 · Phase 4, 4b — kepegawaian-y7u.2 · Phase 4 (+10 more)

### Community 334 - "Mail Service — Code Patterns (Verified Analysis)"
Cohesion: 0.11
Nodes (18): 0. How to read this document, 10. Confirmed pre-existing bugs (do NOT fix without a beads issue), 1. CQRS-lite: Command / Query split, 2. JOOQ read pattern — single-query pagination via window function, 3. Sqid opaque external IDs, 4. Pagination base classes, 4a. DIVERGENCE — two pagination response shapes, 5. Soft delete (+10 more)

### Community 335 - "LampiranProfil"
Cohesion: 0.22
Nodes (3): from(), JenjangPendidikan, JenjangPendidikanMapper

### Community 336 - "OpenApiConfig"
Cohesion: 0.06
Nodes (34): AuditorAware, Configuration, DateTimeProvider, DefaultConfigurationCustomizer, EnableJpaAuditing, EnableWebMvc, GroupedOpenApi, OpenAPI (+26 more)

### Community 337 - "KartuIdentitasQueryService.java"
Cohesion: 0.10
Nodes (22): KartuIdentitasCommandService, KartuIdentitasQueryRepository, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+14 more)

### Community 338 - "Penggajian Cqrs Claim Order"
Cohesion: 0.09
Nodes (23): ADR-0016, CommandService, changedStatus is server-resolved by role, not sent by the client, 0018 Changedstatus Server Resolved By Role, Consequences, Considered Options, 0024 Gajibatchroot Kafka Diisolasi Ke Eventpublisher, Publikasi Kafka GajiBatchRoot diisolasi ke GajiBatchRootEventPublisher, dipublish after-commit (+15 more)

### Community 339 - "JenisKitasPostRequest"
Cohesion: 0.27
Nodes (6): ForeignKey, InverseForeignKey, Name, Override, Table, UniqueKey

### Community 340 - "JenisSpCommandServiceTest"
Cohesion: 0.22
Nodes (11): GajiProfil, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+3 more)

### Community 341 - ".build"
Cohesion: 0.11
Nodes (21): ConstraintViolation, Errors, ExceptionHandler, HttpStatusCode, ResponseEntityExceptionHandler, RestControllerAdvice, ErrorResult, Data (+13 more)

### Community 342 - "MimeTypesUtilsImpl"
Cohesion: 0.35
Nodes (3): Override, Service, MimeTypesUtilsImpl

### Community 343 - "StatusPegawaiController.java"
Cohesion: 0.28
Nodes (6): Field, ForeignKey, InverseForeignKey, Name, Table, PengalamanKerjaAudPath

### Community 344 - "CutiApprovalChain"
Cohesion: 0.05
Nodes (42): DeleteResult, Context — Modul Pegawai (Catatan Kepegawaian Inti), Language Pegawai, Glossary, Pegawai Record Refactor Claim Order, Bentuk DTO Target, Claim Order (kerjakan berurutan — tiap task blok task berikutnya), Definition of Done (+34 more)

### Community 345 - "ProfileUpdateService"
Cohesion: 0.17
Nodes (12): APPROVED, Appwrite, Custom Properties, Database, Project Analysis, External Services, Integrasi External, Kafka & Redis (+4 more)

### Community 346 - "GajiParameterSetting"
Cohesion: 0.20
Nodes (11): GajiParameterSetting, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+3 more)

### Community 349 - "GajiPendapatanNonPajak"
Cohesion: 0.16
Nodes (10): GajiPendapatanNonPajakPostRequest, DeleteMapping, PostMapping, PutMapping, ResponseEntity, GajiPendapatanNonPajakPostRequest, Data, JsonIgnore (+2 more)

### Community 350 - "PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian"
Cohesion: 0.08
Nodes (24): Architectural Decisions, Further Notes, Implementation Decisions, M10: Penggajian Domain CQRS, M1: Flyway Infrastructure, M2: JOOQ Code Generation, M3: IdsAbstract Refactoring, M4: Entity Performance Hardening (+16 more)

### Community 351 - "LampiranProfilQueryService"
Cohesion: 0.18
Nodes (12): Acceptance, Acceptance (Wave 1), Acceptance (Wave 2), FASE 0 — Pra-implementasi (setup beads), FASE PU-1 — Buang interface ProfileUpdateService (decisions-cuti §11), FASE PU-2 — Migrasi read ProfileUpdate ke JOOQ + split (BLOCKED oleh PU-1), Guardrails (semua fase), Profil CQRS Cleanup — Claim Order & Checklists (+4 more)

### Community 352 - "MasterBaseEntity"
Cohesion: 0.24
Nodes (9): Data, EqualsAndHashCode, ProfileUpdateRequest, Data, ProfilUpdateAcceptRequest, EProfileUpdateApproval, APPROVED, PENDING (+1 more)

### Community 353 - "Serializable"
Cohesion: 0.05
Nodes (43): EProsesGaji, Serializable, Data, GajiBatchRootResponse, GajiBatchRootErrorLogsResponse, GajiBatchRootLampiranMiniResponse, EJenisPotonganGaji, POTONGAN_TAMBAHAN (+35 more)

### Community 354 - "RumahDinas"
Cohesion: 0.18
Nodes (10): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+2 more)

### Community 355 - "MasterBaseEntity"
Cohesion: 0.21
Nodes (11): AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, SQLRestriction (+3 more)

### Community 356 - "CutiKlaimDetail"
Cohesion: 0.18
Nodes (9): CutiKlaimDetail, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString (+1 more)

### Community 357 - "DateHelper"
Cohesion: 0.21
Nodes (7): Condition, Field, Override, PlainSQL, Select, SQL, UniqueKey

### Community 358 - ".delete_withChildSubJabatan_throwsConflict"
Cohesion: 0.32
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, PelatihanAudPath

### Community 359 - "Claim Order 2026 06 17 Analisis Bug"
Cohesion: 0.21
Nodes (7): Condition, Field, Override, PlainSQL, Select, SQL, UniqueKey

### Community 360 - "AlasanBerhenti"
Cohesion: 0.06
Nodes (41): RiwayatSkPostRequest, RiwayatTerminasiPostRequest, Data, EqualsAndHashCode, JsonIgnore, Specification, RiwayatMutasiPostRequest, AllArgsConstructor (+33 more)

### Community 361 - "JenisKitas"
Cohesion: 0.08
Nodes (28): KartuIdentitasPostRequest, from(), Data, JsonIgnore, Specification, KartuIdentitasPostRequest, KartuIdentitasPutRequest, AllArgsConstructor (+20 more)

### Community 362 - "Pelatihan"
Cohesion: 0.18
Nodes (11): 1. Keputusan terkunci (berlaku untuk semua master), 2.1 Base paging — PageRequest (abstract), 2.2 Sort whitelist — SortParam, 2.3 Typed ID (opsional, ditunda), 2.4 Controller — write-flow, 2. Komponen pattern (dari kode Publication), 3. Resep adopsi per modul master (langkah generik), 4. Checklist acceptance (salin per modul) (+3 more)

### Community 363 - "DownloadPenggajian"
Cohesion: 0.18
Nodes (11): Catatan lanjutan (BUKAN bagian ag3), Fix codegen (file: buildSrc/src/main/kotlin/JooqCodegenTask.kt), Guardrails, ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ, jOOQ Split-Brain Schema — Claim Order & Checklist, Pre-commit, Regenerate & verifikasi generated code, Root cause (sudah dipastikan manager — JANGAN diulang buta) (+3 more)

### Community 364 - "0012 Jooq Codegen Via Generationtool Not Plugin"
Cohesion: 0.11
Nodes (19): ADR-0006, ADR-0012, ADR-0015, Database, Consequences, Considered Options, 0012 Jooq Codegen Via Generationtool Not Plugin, JOOQ codegen dijalankan lewat GenerationTool di satu task imperatif, bukan plugin official (+11 more)

### Community 365 - "BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session"
Cohesion: 0.18
Nodes (10): 1. `GET /pegawai/{id}/session` — tambah field `statusPegawai`, 2. Konteks — bagaimana FE memakai field ini (FYI, tidak butuh perubahan BE), Alternatif yang dipertimbangkan (ditolak), BE Requirement — Riwayat Kontrak Kerja: tambah `statusPegawai` di Session, Dampak, Definition of Done (BE), Kontak / referensi FE, Perubahan yang diminta (+2 more)

### Community 366 - "Modul yang Dibangun/Dimodifikasi"
Cohesion: 0.29
Nodes (8): ProfilKeluargaRepository, Modifying, Query, Transactional, ProfilKeluargaRepository, RequiredArgsConstructor, Service, ProfilKeluargaLampiranCommandService

### Community 367 - "Prefs"
Cohesion: 0.24
Nodes (9): AllArgsConstructor, Getter, JsonIgnoreProperties, NoArgsConstructor, Setter, ToString, Prefs, Data (+1 more)

### Community 368 - ".save"
Cohesion: 0.20
Nodes (8): EProfileUpdateTable, BIODATA, KEAHLIAN, KELUARGA, PELATIHAN, PENDIDIKAN, PENGALAMAN_KERJA, RevisionType

### Community 369 - "JenisKeahlianPostRequest"
Cohesion: 0.25
Nodes (8): JenisSpPostRequest, Data, JsonIgnore, Specification, JenisSpPostRequest, Data, EqualsAndHashCode, JenisSpPutRequest

### Community 370 - "JenisKeahlian"
Cohesion: 0.36
Nodes (5): Data, JsonIgnore, Specification, JenisKeahlianPostRequest, JenisKeahlian

### Community 371 - ".KeahlianAud"
Cohesion: 0.24
Nodes (6): Condition, Override, PlainSQL, Select, SQL, UniqueKey

### Community 372 - ".RiwayatTerminasiAud"
Cohesion: 0.24
Nodes (8): GajiPotonganTkkCommandService, GajiPotonganTkkPostRequest, GajiPotonganTkkController, Page, RequestMapping, RequiredArgsConstructor, RestController, GajiPotonganTkkPutRequest

### Community 373 - ".handle"
Cohesion: 0.17
Nodes (17): AccessDeniedException, AccessDeniedHandler, CorsConfigurationSource, EnableMethodSecurity, EnableWebSecurity, HttpSecurity, DeniedHandler, Component (+9 more)

### Community 375 - "RedisHelperTest"
Cohesion: 0.39
Nodes (7): DataRedisTest, GenericContainer, Import, StringRedisTemplate, Test, RedisHelperTest, Testcontainers

### Community 376 - "Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`"
Cohesion: 0.20
Nodes (9): 1. DTO — `PegawaiResponseSession.java`, 2. Repository — `PegawaiSessionQueryRepository.java`, 3. Verifikasi, Checklist Implementasi, Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`, Format di berbagai endpoint, Konteks & Keputusan Desain, Referensi File (+1 more)

### Community 377 - ".createStyle"
Cohesion: 0.47
Nodes (4): ForeignKey, InverseForeignKey, Name, Table

### Community 378 - "Keputusan yang Disepakati"
Cohesion: 0.47
Nodes (4): ForeignKey, InverseForeignKey, Name, Table

### Community 379 - "AuthController.java"
Cohesion: 0.38
Nodes (7): AuthController, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestClient, RestController

### Community 380 - "PelatihanController"
Cohesion: 0.39
Nodes (6): Override, RequiredArgsConstructor, Service, Slf4j, Transactional, ProfileUpdateKeluargaApprovalService

### Community 381 - "RiwayatKeluar"
Cohesion: 0.44
Nodes (4): BiodataDetailJooqMapperTest, DSLContext, Field, Test

### Community 382 - "Apd"
Cohesion: 0.25
Nodes (6): BiodataGolonganDarah, A, AB, B, O, SuppressWarnings

### Community 383 - "JenisPelatihan"
Cohesion: 0.29
Nodes (5): Condition, Field, PlainSQL, Select, SQL

### Community 384 - ".PendidikanAud"
Cohesion: 0.32
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, PendidikanAudPath

### Community 385 - "GolonganWriteIT.java"
Cohesion: 0.23
Nodes (7): GolonganWriteIT, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test, Transactional

### Community 386 - "AuditConfig.java"
Cohesion: 0.29
Nodes (7): ADR-0024, Batch pemrosesan gaji, Context — Modul Penggajian (Payroll & Batch Pemrosesan Gaji), Language Penggajian, Enum & konsep alur, Glossary, Referensi & parameter penggajian (master)

### Community 387 - "0010 — Drop the @Version / version column from rewritten master entities"
Cohesion: 0.38
Nodes (4): PelatihanDetail, Override, SuppressWarnings, PelatihanDetailJooqMapper

### Community 388 - "Inventory: kepegawaian (Legacy) Schema Dump"
Cohesion: 0.53
Nodes (3): CustomResult, ResponseEntity, Slf4j

### Community 389 - "Form Mutasi — Claim Order & Checklist"
Cohesion: 0.22
Nodes (8): 1. `form-mutasi: endpoint GET /pegawai/{id}/mutasi-context` (`kepegawaian-nil`), 2. `form-mutasi: endpoint GET /master/profesi/jabatan/{id}` (`kepegawaian-qly`), 3. Konfirmasi snapshot `*LamaId` & `masaKerja`, Catatan, Finalisasi, Form Mutasi — Claim Order & Checklist, P1 — Blocking FE (wajib dikerjakan), P2 — Konfirmasi

### Community 390 - "CQRS Migration Roadmap"
Cohesion: 0.70
Nodes (4): AppwriteProperties, Component, ConfigurationProperties, Data

### Community 391 - "TestController.java"
Cohesion: 0.39
Nodes (7): Principal, GetMapping, PreAuthorize, RequestMapping, ResponseEntity, RestController, TestController

### Community 393 - "Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration"
Cohesion: 0.83
Nodes (3): GajiBatchMasterPostRequest, Data, MultipartFile

### Community 395 - "context7"
Cohesion: 0.25
Nodes (7): headers, type, url, Authorization, mcp, context7, $schema

### Community 396 - "GajiBatchRootPostRequest"
Cohesion: 0.46
Nodes (4): GajiBatchRootPostRequest, Data, JsonIgnore, MultipartFile

### Community 398 - "ArchUnitTest.java"
Cohesion: 0.48
Nodes (5): AnalyzeClasses, ArchCondition, ArchRule, JavaMethod, ArchUnitTest

### Community 401 - "Graph Report - .  (2026-05-05)"
Cohesion: 0.29
Nodes (7): Community Hubs (Navigation), Corpus Check, God Nodes (most connected - your core abstractions), Graph Report - .  (2026-05-05), Knowledge Gaps, Summary, Surprising Connections (you probably didn't know these)

### Community 402 - "KafkaConfig.java"
Cohesion: 0.48
Nodes (5): NewTopic, Bean, Component, Slf4j, KafkaConfig

### Community 403 - "GajiKomponenJenisGaji.java"
Cohesion: 0.39
Nodes (7): getCatalog(), getLiteral(), getSchema(), Catalog, Override, Schema, lookupLiteral()

### Community 406 - "Test"
Cohesion: 0.36
Nodes (3): MinimalCutiRule, Test, MinimalCutiRuleTest

### Community 414 - "context7"
Cohesion: 0.40
Nodes (4): CONTEXT7_API_KEY, npx, context7, @upstash/context7-mcp

### Community 416 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 420 - "EReferensiPegawai"
Cohesion: 0.50
Nodes (3): EReferensiPegawai, BIODATA, PEGAWAI

### Community 422 - "GajiBatchRootRecord.java"
Cohesion: 0.67
Nodes (3): BiodataDashboardResponse changedStatus field, changedStatus server-resolved by role, ProfileUpdateService approval queue

### Community 436 - "CutiAllocationHelper.java"
Cohesion: 0.09
Nodes (39): CutiKuotaAllocator, CutiKuotaImportRequest, CutiKuotaRepository, CutiPengajuanPostRequest, CutiPeriodHandler, MinimalCutiRule, CutiKuotaAllocationResult, Builder (+31 more)

## Knowledge Gaps
- **1227 isolated node(s):** `build-dev.sh script`, `copy.sh script`, `npx`, `@upstash/context7-mcp`, `CONTEXT7_API_KEY` (+1222 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **58 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `List` connect `Core Entities & Pagination` to `Domain Context Docs`, `RecordMapper`, `DasarGaji`, `JabatanMiniResponse`, `DetailDasarGajiAudRecord`, `RiwayatCutiAudRecord`, `CutiPegawai`, `RiwayatCutiRecord`, `Tables`, `GajiParameterSettingRecord`, `GajiProfilAudRecord`, `FlywaySchemaHistoryCopy1Record`, `Grade`, `RiwayatSk`, `GajiBatchMaster`, `ProfilKeluarga`, `KartuIdentitas`, `RiwayatMutasi`, `AlasanBerhentiRecord`, `ApdRecord`, `GajiTunjangan`, `Keahlian`, `JenisSp`, `AlatKerjaRecord`, `GajiBatchRoot`, `tables/GajiBatchRootLampiran.java`, `tables/GajiBatchRootErrorLogs.java`, `tables/PengalamanKerja.java`, `Specification`, `tables/CutiApprovalChain.java`, `tables/GajiProfil.java`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `JenisKitasRecord`, `Level`, `NotFoundException`, `tables/JenisKeahlian.java`, `tables/RumahDinas.java`, `RiwayatMutasiAud.java`, `JenjangPendidikanResponse`, `GajiKomponenAud.java`, `Graph Report`, `Organisasi`, `OrganisasiQueryRepository.java`, `Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist`, `Glossary`, `PendidikanQueryService.java`, `GajiBatchRoot`, `DefaultSchema`, `ProfesiController`, `Coding Rules`, `0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success")`, `DasarGajiQueryRepository.java`, `GajiProfilResponse`, `GajiPhdpResponse`, `JenisKitasQueryRepository.java`, `GolonganQueryRepository.java`, `JenisSpQueryRepository.java`, `PrefRole`, `APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone`, `GajiBatchRootCommandService.java`, `ProfilUpdateRecord`, `Profil Rewrite Claim Order`, `HariLiburQueryRepository.java`, `AppwriteUser`, `CutiJenisQueryRepository.java`, `GajiBatchMasterProsesResponse`, `EApprovalCutiStatus`, `ListResult`, `JenjangPendidikan`, `GajiPendapatanNonPajakResponse`, `ProfileUpdate`, `AppwriteClient`, `ProcessPotonganTkkImpl.java`, `RiwayatKontrakController.java`, `GajiKomponen`, `.toString`, `EJenisTunjangan`, `List`, `EJenisSk`, `JenisKontrakController.java`, `RiwayatSkQuery`, `JabatanQueryRepository.java`, `GajiBatchMasterResponse`, `JenisSp`, `PelatihanQueryService.java`, `SanksiQueryRepository.java`, `tables/GajiKomponen.java`, `LampiranRow`, `Keahlian`, `JenisKeahlianQueryRepository.java`, `RumahDinasQueryRepository.java`, `TableImpl`, `tables/JenisPelatihan.java`, `PegawaiQueryService`, `JenisPelatihanQueryRepository.java`, `CutiJenis`, `DasarGajiAud.java`, `AlasanBerhentiQueryRepository.java`, `GradeQuery`, `FileUploadUtil`, `BiodataQueryService.java`, `GajiKomponenAudRecord`, `DetailDasarGajiQueryRepository.java`, `GajiTunjanganRecord`, `GajiParameterSettingCommandService.java`, `KeahlianQueryService.java`, `IdsAbstract`, `CutiJenis`, `HariLiburRecord`, `PengalamanKerjaQueryService.java`, `DasarGajiController.java`, `JenjangPendidikanController.java`, `CutiKuotaTemplateBuilder.java`, `OpenApiConfig`, `KartuIdentitasQueryService.java`, `.build`, `CutiApprovalChain`, `Serializable`, `CutiKlaimDetail`, `JenisKitas`, `Prefs`, `.handle`, `PelatihanController`, `RiwayatKeluar`, `0010 — Drop the @Version / version column from rewritten master entities`, `Inventory: kepegawaian (Legacy) Schema Dump`, `BiodataDetailJooqMapperTest`, `AuditAwareImpl`, `0005 Revive On Create Soft Delete Unique`?**
  _High betweenness centrality (0.181) - this node is a cross-community bridge._
- **Why does `LocalDate` connect `Core Entities & Pagination` to `PelatihanQueryService.java`, `Domain Context Docs`, `PagedRequest`, `LampiranRow`, `ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ`, `RecordMapper`, `TableImpl`, `PegawaiQueryService`, `DasarGaji`, `DasarGajiAud.java`, `Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist`, `VPegawaiRecord`, `BiodataQueryService.java`, `CutiKuota`, `JabatanMiniResponse`, `Level`, `CutiPegawai`, `CutiAllocationHelper.java`, `DasarGajiQueryRepository.java`, `SavedStatus`, `DasarGajiController.java`, `APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone`, `IdsAbstract`, `BiodataDashboardQueryTest`, `RiwayatSk`, `CutiKuotaTemplateBuilder.java`, `ProfilKeluarga`, `RiwayatMutasi`, `HariLiburQueryRepository.java`, `CutiApprovalChain`, `EApprovalCutiStatus`, `Specification`, `MasterBaseEntity`, `JenjangPendidikan`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `CutiKlaimDetail`, `Page`, `AlasanBerhenti`, `JenisKitas`, `Decisions Cuti`, `tables/JenisKeahlian.java`, `PageResult`, `Modul yang Dibangun/Dimodifikasi`, `RiwayatMutasiAud.java`, `HariLibur`, `List`, `EJenisSk`, `Organisasi`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **Why does `Keys` connect `JenisPelatihanRecord` to `Core Entities & Pagination`, `Many-to-Many & Base Entities`, `Penggajian Payroll Entities`, `Kepegawaian SK & SP`, `Profil Biodata & Pendidikan`, `Cuti Leave Module`, `Claim Order & ADRs`, `PengalamanKerjaAudRecord`, `PegawaiAudRecord`, `GajiBatchMasterRecord`, `CutiPegawaiRecord`, `KartuIdentitasAudRecord`, `PendidikanAudRecord`, `RiwayatSkRecord`, `PelatihanRecord`, `BiodataRecord`, `SanksiSpRecord`, `RiwayatTerminasiRecord`, `RevinfoPath`, `KeahlianAudRecord`, `LampiranSkAudRecord`, `StatistikPegawaiRecord`, `LampiranProfilRecord`, `CutiKuotaAudRecord`, `DasarGajiAudRecord`, `KartuIdentitasRecord`, `GajiPotonganTkkAudRecord`, `CutiJenisAudRecord`, `DetailDasarGajiAudRecord`, `DasarGajiRecord`, `GajiPendapatanNonPajakAudRecord`, `Organisasi`, `GajiParameterSettingAudRecord`, `Jabatan`, `GajiPhdpRecord`, `GajiProfilAudRecord`, `Grade`, `GajiProfilRecord`, `RiwayatKontrakRecord`, `Golongan`, `SanksiSp`, `JenisSpRecord`, `ApdRecord`, `GajiTunjangan`, `GajiPotonganTkk`, `AlatKerjaRecord`, `GajiBatchRoot`, `tables/CutiApprovalChain.java`, `tables/CutiKlaimDetail.java`, `JenisKitasRecord`, `Level`, `tables/JenisKeahlian.java`, `CutiApprovalAudRecord`, `GajiPhdpAudRecord`, `PelatihanAudRecord`, `PegawaiRecord`, `RiwayatKontrakAudRecord`, `LampiranSkRecord`, `UpdatableRecordImpl`, `LampiranProfilAudRecord`, `CutiApprovalRecord`, `RiwayatMutasiAudRecord`, `GajiBatchRootErrorLogsRecord`, `Profil CQRS Cleanup — Claim Order & Checklists`, `GajiBatchMasterProsesRecord`, `PrefRole`, `LampiranProfilController.java`, `JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset`, `0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail`, `0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null`, `RiwayatSpAudRecord`, `RiwayatSpRecord`, `RiwayatTerminasiAudRecord`, `.delete`, `RiwayatKeluarRecord`, `GajiBatchRootLampiran`, `ProfilKeluargaAudRecord`, `CutiPegawaiAudRecord`, `GajiBatchPotonganTkkRecord`, `PengalamanKerjaRecord`, `KeahlianRecord`, `GajiKomponenRecord`, `Keys`, `tables/GajiPendapatanNonPajak.java`, `JenjangPendidikanRecord`, `tables/JenisPelatihan.java`, `CutiJenis`, `DetailDasarGajiRecord`, `tables/HariLibur.java`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `build-dev.sh script`, `copy.sh script`, `npx` to the rest of the system?**
  _1227 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Core Entities & Pagination` be split into smaller, more focused modules?**
  _Cohesion score 0.01737012338777303 - nodes in this community are weakly interconnected._
- **Should `Many-to-Many & Base Entities` be split into smaller, more focused modules?**
  _Cohesion score 0.05878725590955807 - nodes in this community are weakly interconnected._
- **Should `List & Java Collections` be split into smaller, more focused modules?**
  _Cohesion score 0.053141167775314115 - nodes in this community are weakly interconnected._
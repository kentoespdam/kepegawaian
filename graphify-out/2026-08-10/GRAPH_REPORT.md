# Graph Report - kepegawaian  (2026-08-10)

## Corpus Check
- 1233 files · ~373,817 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 14954 nodes · 43092 edges · 444 communities (399 shown, 45 thin omitted)
- Extraction: 81% EXTRACTED · 19% INFERRED · 0% AMBIGUOUS · INFERRED: 8272 edges (avg confidence: 0.8)
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
- Pattern Response/DTO Modul Master — Panduan Adopsi
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
- Claim Order — Analisis Bug GajiBatchRoot + Config + CQRS (2026-06-17)
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
- PelatihanCommandService.java
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
- GajiPhdp
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
- RiwayatTerminasiAudRecord.java
- ProfesiSelects.java
- BiodataSelects.java
- ProfilKeluargaSelects.java
- .key
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
- `Biodata PATCH changedStatus` --references--> `CLAUDE.md Canonical Guidance`  [INFERRED]
  docs/claim-order-biodata-patch-changedstatus.md → CLAUDE.md
- `Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration` --cites--> `ADR-0001`  [EXTRACTED]
  grill/2026-05-05_cqrs-jooq-flyway-migration.md → docs/profil-rewrite-claim-order.md
- `PRD: CQRS, JOOQ, and Flyway` --cites--> `ADR-0001`  [EXTRACTED]
  grill/PRD-cqrs-jooq-flyway.md → docs/profil-rewrite-claim-order.md
- `AuthService` --references--> `0029 Appwriteclient Typed Adapter`  [EXTRACTED]
  GRAPH_REPORT.md → docs/adr/0029-appwriteclient-typed-adapter.md
- `AuthService` --references--> `Decisions Pegawai`  [EXTRACTED]
  GRAPH_REPORT.md → docs/context/decisions-pegawai.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Biodata changedStatus Flow** — biodata_dashboard_response_changedstatus, changed_status_server_resolved, profile_update_service [INFERRED 0.85]

## Communities (444 total, 45 thin omitted)

### Community 0 - "Core Entities & Pagination"
Cohesion: 0.02
Nodes (171): ADR-0001, ADR-0005, ADR-0007, ADR-0008, ADR-0014, ADR-0018, ADR-0020, ADR-0022 (+163 more)

### Community 1 - "Many-to-Many & Base Entities"
Cohesion: 0.05
Nodes (43): RiwayatSkPutRequest, from(), getLastFromList(), PegawaiPostRequest, AllArgsConstructor, Entity, Getter, NoArgsConstructor (+35 more)

### Community 2 - "List & Java Collections"
Cohesion: 0.04
Nodes (61): PostMapping, PutMapping, ResponseEntity, PutMapping, PostMapping, PutMapping, ResponseEntity, PostMapping (+53 more)

### Community 3 - "DTO Patterns & Builders"
Cohesion: 0.10
Nodes (18): JabatanPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 4 - "Validation & Error Handling"
Cohesion: 0.03
Nodes (64): SelectField, SortField, Field, SuppressWarnings, SortParam, fromList(), HariLiburQuery, from() (+56 more)

### Community 5 - "Relation Mappings & DSL"
Cohesion: 0.04
Nodes (41): PostMapping, ResponseEntity, PostMapping, PutMapping, ResponseEntity, from(), CutiApprovalPostRequest, from() (+33 more)

### Community 6 - "Pegawai Join Queries"
Cohesion: 0.02
Nodes (77): from(), from(), from(), from(), from(), from(), from(), from() (+69 more)

### Community 7 - "Adapter & Config Mappers"
Cohesion: 0.03
Nodes (90): KenaikanBerkalaRequest, LaporanKepegawaianService, SingleResult, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+82 more)

### Community 8 - "Penggajian Payroll Entities"
Cohesion: 0.08
Nodes (10): from(), Override, Record1, SuppressWarnings, PendidikanRecord, LampiranSkMapper, Biodata, LampiranProfilMapper (+2 more)

### Community 9 - "Kepegawaian SK & SP"
Cohesion: 0.10
Nodes (11): PostMapping, from(), from(), GajiBatchRootLampiranRecord, SuppressWarnings, LampiranSk, Override, LampiranProfil (+3 more)

### Community 10 - "Enums & Constants"
Cohesion: 0.07
Nodes (28): ADR-010, AllowedFileTypeController, ApiError, ApplicationEventPublisher, AppWriteAuthFilter, ArchivePublishedEvent, BETWEEN, Patterns Mail Service (+20 more)

### Community 11 - "Master References"
Cohesion: 0.01
Nodes (152): Communities, Community 0 - ".getId()", Community 100 - "OrganisasiRepository.java", Community 101 - "JenisPelatihanRepository.java", Community 102 - "JenisKeahlianRepository.java", Community 103 - "RumahDinasRepository.java", Community 104 - "JenisKitasRepository.java", Community 105 - "GradeRepository.java" (+144 more)

### Community 12 - "Profil Biodata & Pendidikan"
Cohesion: 0.07
Nodes (20): GajiTunjanganAud, GajiTunjanganAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 13 - "Cuti Leave Module"
Cohesion: 0.07
Nodes (4): from(), GajiBatchRootRecord, SuppressWarnings, GajiBatchRoot

### Community 14 - "Domain Context Docs"
Cohesion: 0.04
Nodes (73): AlasanBerhentiRepository, JabatanPostRequest, JpaRepository, JpaSpecificationExecutor, QueryByExampleExecutor, RevisionRepository, RumahDinasRepository, SanksiRepository (+65 more)

### Community 15 - "Claim Order & ADRs"
Cohesion: 0.09
Nodes (10): from(), CutiKuotaRecord, Override, Record1, SuppressWarnings, CutiKuotaMapper, CutiKuota, Condition (+2 more)

### Community 16 - "PagedRequest"
Cohesion: 0.03
Nodes (96): Direction, PagedRequest, Getter, JsonIgnore, Pageable, Setter, PagedRequest, CutiApprovalRequest (+88 more)

### Community 17 - "PengalamanKerjaAudRecord"
Cohesion: 0.05
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 19 - ".toQuery"
Cohesion: 0.10
Nodes (18): GradeQuery, SanksiQuery, JenisSpSimple, SanksiJenisSpList, GradeJooqMapper, SanksiJooqMapper, GradeSelects, Field (+10 more)

### Community 20 - "GajiBatchMasterRecord"
Cohesion: 0.05
Nodes (3): from(), GajiBatchMasterRecord, SuppressWarnings

### Community 22 - "RecordMapper"
Cohesion: 0.10
Nodes (12): RecordMapper, KartuIdentitasQuery, BiodataDetailJooqMapper, Override, KartuIdentitasJooqMapper, Override, KartuIdentitasMultisetJooqMapper, PendidikanJooqMapper (+4 more)

### Community 23 - "KartuIdentitasAudRecord"
Cohesion: 0.07
Nodes (20): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+12 more)

### Community 24 - "Cuti CQRS Rewrite — Claim Order & Checklists"
Cohesion: 0.14
Nodes (24): Acceptance, Acceptance (final modul), Cuti CQRS Rewrite — Claim Order & Checklists, FASE 0 — Pra-implementasi (setup beads), FASE 10 — Pengajuan Command (Keputusan #1, #6, #8, #9), FASE 11 — Approval Command state-machine (Keputusan #6), FASE 12 — Klaim Command + allocator klaim 1:1 (Keputusan #16, #10), FASE 13 — Controllers (Keputusan #13) (+16 more)

### Community 25 - "DasarGaji"
Cohesion: 0.10
Nodes (22): DasarGaji, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+14 more)

### Community 28 - "Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring"
Cohesion: 0.29
Nodes (7): Acceptance ringkas per issue, Cara update checklist, Dependency map (ringkas), Organisasi — Adopsi Pattern Response Publication — Claim Order & Monitoring, REF, WAVE 0 — Epic (gerbang, tidak dikerjakan langsung), WAVE 1 — Eksekusi paralel (2 issue, tidak saling blok)

### Community 30 - "BiodataRecord"
Cohesion: 0.05
Nodes (10): BiodataGolonganDarah(), A, AB, B, O, SuppressWarnings, BiodataRecord, Override (+2 more)

### Community 31 - "SanksiSpRecord"
Cohesion: 0.10
Nodes (5): Override, Record1, SuppressWarnings, SanksiSpRecord, SanksiMapper

### Community 32 - "RiwayatTerminasiRecord"
Cohesion: 0.06
Nodes (4): Override, Record1, SuppressWarnings, RiwayatTerminasiRecord

### Community 33 - "RevinfoPath"
Cohesion: 0.07
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
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 39 - "VPegawaiRecord"
Cohesion: 0.06
Nodes (15): SuppressWarnings, VPegawaiRecord, Condition, Field, Name, Override, PlainSQL, Schema (+7 more)

### Community 40 - "CutiKuotaAudRecord"
Cohesion: 0.06
Nodes (20): CutiKuotaAud, CutiKuotaAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 41 - "DasarGajiAudRecord"
Cohesion: 0.09
Nodes (7): DasarGajiAudRecord, Override, Record2, SuppressWarnings, DasarGajiMapper, DasarGaji, Condition

### Community 42 - "KartuIdentitasRecord"
Cohesion: 0.08
Nodes (4): Override, Record1, SuppressWarnings, KartuIdentitasRecord

### Community 43 - "JabatanMiniResponse"
Cohesion: 0.03
Nodes (64): AlasanBerhentiResponse, ApdRow, GolonganResponse, GradeMiniResponse, JabatanMiniResponse, JenisSpMiniResponse, LevelQueryRepository, LevelResponse (+56 more)

### Community 44 - "GajiPotonganTkkAudRecord"
Cohesion: 0.06
Nodes (20): GajiPotonganTkkAud, GajiPotonganTkkAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 45 - "CutiJenisAudRecord"
Cohesion: 0.06
Nodes (20): CutiJenisAud, CutiJenisAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 46 - "DetailDasarGajiAudRecord"
Cohesion: 0.06
Nodes (20): DetailDasarGajiAud, DetailDasarGajiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 47 - ".getId"
Cohesion: 0.02
Nodes (135): ConstraintViolationException, PostMapping, PutMapping, PostMapping, PreAuthorize, ResponseEntity, PostMapping, PreAuthorize (+127 more)

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
Cohesion: 0.12
Nodes (17): CutiPegawai, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+9 more)

### Community 52 - "Organisasi"
Cohesion: 0.05
Nodes (25): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+17 more)

### Community 53 - "RiwayatCutiRecord"
Cohesion: 0.06
Nodes (22): Override, Record1, SuppressWarnings, RiwayatCutiRecord, Condition, Field, ForeignKey, Identity (+14 more)

### Community 54 - "GajiParameterSettingAudRecord"
Cohesion: 0.07
Nodes (20): GajiParameterSettingAud, GajiParameterSettingAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 55 - "Jabatan"
Cohesion: 0.05
Nodes (24): CutiPegawaiPath, Jabatan, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+16 more)

### Community 56 - "GajiPendapatanNonPajakRecord"
Cohesion: 0.10
Nodes (4): GajiPendapatanNonPajakRecord, Override, Record1, SuppressWarnings

### Community 57 - "Ringkasan Temuan"
Cohesion: 0.08
Nodes (26): ✅ Alive Selects (lengkap), Claim Order & Checklist, 🔵 Cross-Module DTO (Masih Dipakai — JANGAN Dihapus), [D1] Hapus Dead DTO — kepegawaian-0ox, [D2] Hapus Unused Import — kepegawaian-k29, [D3] Verifikasi Cross-Module — kepegawaian-5o6, [D4] Final Cleanup & Build — kepegawaian-aak, [D5] Cleanup Selects — Hapus Dead Field/Array + File — kepegawaian-aak (+18 more)

### Community 58 - "Analisis Project Kepegawaian"
Cohesion: 0.08
Nodes (26): 1. Pegawai (Data Utama Pegawai), 2. Profil (Data Pribadi), 3. Master Data (Referensi), 4. Cuti (Manajemen Cuti), 5. Kepegawaian (Administrasi Pegawai), 6. Penggajian (Payroll), Alur JWT + Appwrite, Analisis Project Kepegawaian (+18 more)

### Community 59 - "Biodata"
Cohesion: 0.13
Nodes (16): Biodata, Condition, Field, ForeignKey, Index, InverseForeignKey, Name, Override (+8 more)

### Community 60 - "Tables"
Cohesion: 0.06
Nodes (24): SuppressWarnings, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+16 more)

### Community 61 - "RiwayatTerminasi"
Cohesion: 0.10
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 62 - "GajiParameterSettingRecord"
Cohesion: 0.07
Nodes (19): GajiParameterSetting, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 63 - "GajiPhdpRecord"
Cohesion: 0.07
Nodes (19): GajiPhdp, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 64 - "GajiProfilAudRecord"
Cohesion: 0.07
Nodes (20): GajiProfilAud, GajiProfilAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 65 - "FlywaySchemaHistoryCopy1Record"
Cohesion: 0.07
Nodes (18): FlywaySchemaHistoryCopy1, Condition, Field, Index, Name, Override, PlainSQL, Schema (+10 more)

### Community 66 - "Master Record Refactor — Claim Order & Checklist"
Cohesion: 0.10
Nodes (24): Checklist, Column Set Arrays — ✅ SELESAI, Common Mistakes, Dependency Graph, E0: Foundation (kepegawaian-hkq) — ✅ SELESAI, E1: Flat Batch 1 (kepegawaian-5k9) — ✅ SELESAI, E2: Flat Batch 2 (kepegawaian-1xy) — ✅ SELESAI, E3: JenjangPendidikan (kepegawaian-1ws) — ✅ SELESAI (+16 more)

### Community 67 - "RiwayatSp"
Cohesion: 0.10
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 68 - "Grade"
Cohesion: 0.12
Nodes (18): Grade, GradePath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 69 - "GajiProfilRecord"
Cohesion: 0.11
Nodes (4): GajiProfilRecord, Override, Record1, SuppressWarnings

### Community 70 - "RiwayatKontrakRecord"
Cohesion: 0.05
Nodes (22): Override, Record1, SuppressWarnings, RiwayatKontrakRecord, Condition, Field, ForeignKey, Identity (+14 more)

### Community 71 - "RiwayatSk"
Cohesion: 0.12
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 72 - "Golongan"
Cohesion: 0.12
Nodes (18): Golongan, GolonganPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 73 - "Profil Record Refactor — Claim Order & Checklist"
Cohesion: 0.19
Nodes (21): Analisis, Aturan Penting (dari master-query-optimization-pattern.md), Checklist, Claim Order, File, P10: Final Verification, P1: Pendidikan, P2: Keahlian (+13 more)

### Community 74 - "SanksiSp"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 75 - "GajiBatchMaster"
Cohesion: 0.13
Nodes (18): GajiBatchMaster, GajiBatchMasterPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 76 - "ProfilKeluarga"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 77 - "KartuIdentitas"
Cohesion: 0.15
Nodes (17): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+9 more)

### Community 78 - "RiwayatMutasi"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 79 - "AlasanBerhentiRecord"
Cohesion: 0.08
Nodes (22): AlasanBerhenti, AlasanBerhentiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 80 - "RumahDinasRecord"
Cohesion: 0.12
Nodes (4): Override, Record1, SuppressWarnings, RumahDinasRecord

### Community 81 - "JenisSpRecord"
Cohesion: 0.12
Nodes (4): Override, Record1, SuppressWarnings, JenisSpRecord

### Community 82 - "ApdRecord"
Cohesion: 0.08
Nodes (22): Apd, ApdPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 83 - "GajiTunjangan"
Cohesion: 0.13
Nodes (18): GajiTunjangan, GajiTunjanganPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 84 - "Keahlian"
Cohesion: 0.14
Nodes (18): JenisKeahlianPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 85 - "tables/Pelatihan.java"
Cohesion: 0.14
Nodes (18): JenisPelatihanPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 86 - "GajiPotonganTkk"
Cohesion: 0.13
Nodes (18): GajiPotonganTkk, GajiPotonganTkkPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 87 - "JenisSp"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 88 - "AlatKerjaRecord"
Cohesion: 0.07
Nodes (22): AlatKerja, AlatKerjaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 89 - "Pegawai Record Refactor — Claim Order & Checklist"
Cohesion: 0.15
Nodes (19): Analisis, Aturan Penting, Checklist, Controller Response Types, CustomResult Method Reference, File, File Impact Summary, G1: PegawaiResponse (+11 more)

### Community 90 - "Claim Order — Adopsi Pattern Publication ke Modul Master"
Cohesion: 0.11
Nodes (19): A. Klaim berurutan (master list), B. Wave structure (urutan eksekusi + verifikasi), C. Pre-flight checklist (jalankan sekali sebelum mulai), Claim Order — Adopsi Pattern Publication ke Modul Master, D.1 Pre-flight per modul, D.2 Child paging/sort checklist, D.3 Child write-flow checklist, D.4 Sub-resource khusus (Apd/AlatKerja) (+11 more)

### Community 91 - "GajiBatchRoot"
Cohesion: 0.13
Nodes (17): GajiBatchRoot, GajiBatchRootPath, Condition, Field, ForeignKey, Index, InverseForeignKey, Name (+9 more)

### Community 92 - "tables/GajiBatchRootLampiran.java"
Cohesion: 0.15
Nodes (18): GajiBatchRootLampiran, GajiBatchRootLampiranPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 93 - "tables/GajiBatchRootErrorLogs.java"
Cohesion: 0.15
Nodes (18): GajiBatchRootErrorLogs, GajiBatchRootErrorLogsPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 94 - "tables/PengalamanKerja.java"
Cohesion: 0.15
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 95 - "Specification"
Cohesion: 0.03
Nodes (92): GajiPendapatanNonPajakPostRequest, GajiPendapatanNonPajakRepository, JenisPelatihanRepository, RiwayatSkPostRequest, Specification, SpecificationBuilder, Data, EqualsAndHashCode (+84 more)

### Community 96 - "tables/CutiApprovalChain.java"
Cohesion: 0.14
Nodes (18): CutiApprovalChain, CutiApprovalChainPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 97 - "tables/GajiProfil.java"
Cohesion: 0.14
Nodes (18): GajiKomponenPath, GajiProfil, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 98 - "tables/CutiKuota.java"
Cohesion: 0.13
Nodes (18): CutiKuota, CutiKuotaPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 99 - "tables/CutiKlaimDetail.java"
Cohesion: 0.10
Nodes (22): CutiKlaimDetail, CutiKlaimDetailPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+14 more)

### Community 100 - "tables/DetailDasarGaji.java"
Cohesion: 0.13
Nodes (18): DetailDasarGaji, DetailDasarGajiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 101 - "Page"
Cohesion: 0.03
Nodes (95): CustomResult, CutiApprovalJooqMapper, CutiApprovalMiniResponse, CutiKuotaJooqMapper, CutiKuotaPegawaiResponse, CutiKuotaRequest, CutiKuotaResponse, CutiKuotaSisa (+87 more)

### Community 102 - "JenisKitasRecord"
Cohesion: 0.07
Nodes (23): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+15 more)

### Community 103 - "Level"
Cohesion: 0.07
Nodes (22): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+14 more)

### Community 104 - "JenisPelatihanRecord"
Cohesion: 0.13
Nodes (4): Override, Record1, SuppressWarnings, JenisPelatihanRecord

### Community 105 - "JenisKeahlianRecord"
Cohesion: 0.13
Nodes (4): Override, Record1, SuppressWarnings, JenisKeahlianRecord

### Community 106 - "Decisions Cuti"
Cohesion: 0.03
Nodes (106): ADR-0021, ApprovalChain, ApprovalCutiCommand, CutiApprovalChainCustomRepositoryImpl, CutiApprovalChainIndexQuery, CutiApprovalChainRequest, CutiApprovalChainResponse, CutiApprovalChainService (+98 more)

### Community 107 - "NotFoundException"
Cohesion: 0.03
Nodes (88): ADR-0011, ADR-0025, BiodataSelects, Byte, CATEGORY, CutiJenisJooqMapper, CutiJenisMapper, CutiJenisRepository (+80 more)

### Community 108 - "tables/JenisKeahlian.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 109 - "tables/RumahDinas.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 110 - "Profil CQRS — Pola Implementasi per Layer"
Cohesion: 0.12
Nodes (17): 1. DTO, 1a. Request tulis — <Agg>PostRequest / <Agg>PutRequest, 1b. Request baca — <Agg>Request, 1c. Response baca — <Agg>Response / <Agg>Query, 2. Mapper — final, private ctor, BUKAN @Component, 2a. Write mapper — <Agg>Mapper (dipakai CommandService), 2b. Read mapper Pola A (flat) — static mapToResponse(Record), 2c. Read mapper Pola B (implements RecordMapper) — dipakai profil (+9 more)

### Community 111 - "CutiApprovalAudRecord"
Cohesion: 0.06
Nodes (20): CutiApprovalAud, CutiApprovalAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 112 - "GajiPhdpAudRecord"
Cohesion: 0.07
Nodes (20): GajiPhdpAud, GajiPhdpAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+12 more)

### Community 113 - "RiwayatMutasiAud.java"
Cohesion: 0.16
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 114 - "JenjangPendidikanResponse"
Cohesion: 0.04
Nodes (50): Keluarga, ProfilKeluargaCommandService, ProfilKeluargaDetailQuery, ProfilKeluargaQueryRepository, GetMapping, Page, PutMapping, RequestMapping (+42 more)

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
Cohesion: 0.04
Nodes (51): AlatKerjaRepository, ApdRepository, CutiKuotaCommandService, DeletedResult, Master Delete Guard Claim Order, GradeRepository, JabatanRepository, Kuota (+43 more)

### Community 119 - "PegawaiRecord"
Cohesion: 0.04
Nodes (4): Override, Record1, SuppressWarnings, PegawaiRecord

### Community 120 - "GajiKomponenAud.java"
Cohesion: 0.15
Nodes (16): GajiKomponenAud, GajiKomponenAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 121 - "RiwayatKontrakAudRecord"
Cohesion: 0.05
Nodes (20): Override, Record2, SuppressWarnings, RiwayatKontrakAudRecord, Condition, Field, ForeignKey, InverseForeignKey (+12 more)

### Community 122 - "Graph Report"
Cohesion: 0.03
Nodes (66): Graph Report, AuditRevisionEntity, BiodataService, CutiApprovalChainCustomRepository, CutiApprovalServiceImplTest, CutiKuotaService, GajiBatchRootService, GajiSk (+58 more)

### Community 123 - "LampiranSkRecord"
Cohesion: 0.05
Nodes (19): Condition, Field, Identity, Index, Name, Override, PlainSQL, Schema (+11 more)

### Community 124 - "UpdatableRecordImpl"
Cohesion: 0.07
Nodes (25): GajiBatchRootAud, GajiBatchRootAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+17 more)

### Community 125 - "Organisasi"
Cohesion: 0.05
Nodes (55): LampiranSp, OrganisasiPostRequest, Audited, SQLDelete, SQLRestriction, AllArgsConstructor, Audited, Entity (+47 more)

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
Cohesion: 0.06
Nodes (32): AuditConfig, Cara klaim & tutup (beads), Catatan per-issue, Claim Order — Epic kepegawaian-irt, Irt Claim Order, irt/1 — kepegawaian-9g0 (INDEPENDEN, mulai dulu), irt/2 — kepegawaian-j4a (INDEPENDEN, blok irt/3), irt/3 — kepegawaian-c2q (butuh irt/2) (+24 more)

### Community 138 - "JwtAuthFilter"
Cohesion: 0.15
Nodes (17): OncePerRequestFilter, DevAuthFilter, Component, FilterChain, HttpServletRequest, HttpServletResponse, Override, Component (+9 more)

### Community 139 - "Profil CQRS Cleanup — Claim Order & Checklists"
Cohesion: 0.18
Nodes (12): Acceptance, Acceptance (Wave 1), Acceptance (Wave 2), FASE 0 — Pra-implementasi (setup beads), FASE PU-1 — Buang interface ProfileUpdateService (decisions-cuti §11), FASE PU-2 — Migrasi read ProfileUpdate ke JOOQ + split (BLOCKED oleh PU-1), Guardrails (semua fase), Profil CQRS Cleanup — Claim Order & Checklists (+4 more)

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
Cohesion: 0.17
Nodes (8): from(), from(), from(), JenisKitas, BiodataMapper, Biodata, ProfilKeluarga, ProfilKeluargaMapper

### Community 145 - "FileUploadUtilImpl"
Cohesion: 0.08
Nodes (15): JooqCodegenTask, DefaultTask, Property, FileUploadUtilImpl, MultipartFile, Override, RequiredArgsConstructor, Service (+7 more)

### Community 146 - "ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ"
Cohesion: 0.18
Nodes (11): Catatan lanjutan (BUKAN bagian ag3), Fix codegen (file: buildSrc/src/main/kotlin/JooqCodegenTask.kt), Guardrails, ISSUE — kepegawaian-ag3 — Selaraskan schema jOOQ, jOOQ Split-Brain Schema — Claim Order & Checklist, Pre-commit, Regenerate & verifikasi generated code, Root cause (sudah dipastikan manager — JANGAN diulang buta) (+3 more)

### Community 147 - "Claim Order — Reformat Column Order V1baseline.sql"
Cohesion: 0.18
Nodes (11): Checklist, Claim Order — Reformat Column Order V1baseline.sql, Claim Order Baseline Column Order, Kolom Orphan (baseline ≠ entity), Pattern A — MasterBaseEntity (tabel master), Pattern B — IdsAbstract (tabel transaksional/audited), Pattern C — Biodata (standalone, PK=nik), Risk (+3 more)

### Community 148 - "Profil Rewrite — Claim Order & Monitoring"
Cohesion: 0.18
Nodes (11): Cara update checklist ini, Dependency map (ringkas), Issue terkait (bug), Keputusan desain yang dikunci (rujukan saat coding), Profil Rewrite — Claim Order & Monitoring, Referensi template (BACA DULU sebelum coding), WAVE 0 — Fondasi (1 issue, blokir semua write-side), WAVE 1 — Slice Referensi Pendidikan (2 issue, GERBANG) (+3 more)

### Community 149 - "Pattern Response/DTO Modul Master — Panduan Adopsi"
Cohesion: 0.18
Nodes (11): 1. Keputusan terkunci (berlaku untuk semua master), 2.1 Base paging — PageRequest (abstract), 2.2 Sort whitelist — SortParam, 2.3 Typed ID (opsional, ditunda), 2.4 Controller — write-flow, 2. Komponen pattern (dari kode Publication), 3. Resep adopsi per modul master (langkah generik), 4. Checklist acceptance (salin per modul) (+3 more)

### Community 150 - "Master Rewrite — Claim Order & Monitoring"
Cohesion: 0.18
Nodes (11): Apa yang disentuh tiap issue, Cara update checklist, Dependency map (ringkas), Master Rewrite — Claim Order & Monitoring, WAVE 0 — Akar (1 issue, blokir semua), WAVE 1 — Foundation paralel (5 issue, semua butuh F1), WAVE 2 — Lanjutan foundation (3 issue), WAVE 3 — Exemplar (1 issue, GERBANG) (+3 more)

### Community 151 - "ApiException"
Cohesion: 0.17
Nodes (8): ApiException, RuntimeException, ApiException, Getter, HttpStatus, BadRequestException, ConflictException, NotFoundException

### Community 152 - "PegawaiController"
Cohesion: 0.06
Nodes (39): AlatKerjaQuery, CutiPegawaiSelects, Consequences, Considered Options, 0030 Hapus Seeding Imperatif Setupmaster, Hapus jalur seeding imperatif setupMaster/, seeding data via Flyway, Catatan bukan-prioritas, Claim Order — Temuan Grilling Arsitektur (2026-07-09) (+31 more)

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
Cohesion: 0.07
Nodes (32): AGENTS.md Agent Config, BiodataDashboardResponse changedStatus field, changedStatus server-resolved by role, CLAUDE.md Canonical Guidance, Biodata Dashboard Endpoint, 1. DTO, 2. Repository — JOOQ Query, 3. Service (+24 more)

### Community 157 - "OrganisasiQueryRepository.java"
Cohesion: 0.06
Nodes (41): Master Pattern Claim Order, Edit, JenisKontrak, JenisMutasi, JenisSk, OrganisasiCommandService, OrganisasiIndexQuery, OrganisasiListResponse (+33 more)

### Community 158 - "GajiBatchMasterProsesRecord"
Cohesion: 0.06
Nodes (32): GajiBatchMasterProsesJenisGaji(), NONE, PEMASUKAN, POTONGAN, getCatalog(), getLiteral(), getName(), getSchema() (+24 more)

### Community 159 - "BiodataAudGolonganDarah"
Cohesion: 0.19
Nodes (14): BiodataAudGolonganDarah(), A, AB, B, O, getCatalog(), getLiteral(), getName() (+6 more)

### Community 160 - "BiodataGolonganDarah.java"
Cohesion: 0.36
Nodes (8): getCatalog(), getLiteral(), getName(), getSchema(), Catalog, Override, Schema, lookupLiteral()

### Community 161 - "Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist"
Cohesion: 0.29
Nodes (7): Bentuk DTO Target, Claim Order (kerjakan berurutan — tiap task blok task berikutnya), Definition of Done, Keputusan Grilling, Konteks, Optimasi GET /pegawai — DTO Tabel Ramping — Claim Order & Checklist, Yang TIDAK Boleh Disentuh

### Community 162 - "Glossary"
Cohesion: 0.29
Nodes (7): ADR-0024, Batch pemrosesan gaji, Context — Modul Penggajian (Payroll & Batch Pemrosesan Gaji), Language Penggajian, Enum & konsep alur, Glossary, Referensi & parameter penggajian (master)

### Community 163 - "0008 Fk Via Getreference On Write"
Cohesion: 0.29
Nodes (7): DataIntegrityViolationException, Attach FK relasi via getReferenceById, bukan findById, Consequences, Considered Options, 0008 Fk Via Getreference On Write, DuplicateResourceException, ResourceNotFoundException

### Community 164 - "PrefRole"
Cohesion: 0.13
Nodes (17): Condition, Field, Name, Override, PlainSQL, Schema, Select, SQL (+9 more)

### Community 165 - "GajiKomponenAudJenisGaji"
Cohesion: 0.22
Nodes (12): GajiKomponenAudJenisGaji(), NONE, PEMASUKAN, POTONGAN, getCatalog(), getLiteral(), getSchema(), Catalog (+4 more)

### Community 166 - "PendidikanQueryService.java"
Cohesion: 0.10
Nodes (24): PendidikanCommandService, PendidikanDetailQuery, PendidikanJooqMapper, PendidikanLampiranCommandService, Page, RequestMapping, RequiredArgsConstructor, RestController (+16 more)

### Community 167 - "LampiranProfilController.java"
Cohesion: 0.12
Nodes (17): [ ] #4 — CUTOVER + hapus shim lama · kepegawaian-94u.3 (blocked by #3), Aturan wajib tiap langkah (CODINGRULES), Lampiranprofil Cqrs Claim Order, lampiranProfil CQRS — Claim Order & Checklist, Session close (setelah semua hijau), [x] #2 — READ side · kepegawaian-94u.1 (READY), [x] #3 — WRITE side · kepegawaian-94u.2 (blocked by #2), LampiranProfilCommandService (+9 more)

### Community 168 - "ADR-0017 — Claim Order & Monitoring"
Cohesion: 0.33
Nodes (6): ADR-0017 — Claim Order & Monitoring, Cara update checklist, Dependency map (ringkas), Issue khusus (pola beda — baca design issue penuh), WAVE 0 — Exemplar (1 issue, GERBANG), WAVE 1 — Replikasi (13 issue paralel, semua butuh s55)

### Community 169 - "Issue tracker: beads + GitHub"
Cohesion: 0.33
Nodes (6): beads conventions (default for task tracking), Issue Tracker, GitHub conventions (published issues / PRDs), Issue tracker: beads + GitHub, When a skill says "fetch the relevant ticket", When a skill says "publish to the issue tracker"

### Community 170 - "JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset"
Cohesion: 0.33
Nodes (6): Aturan keputusan, Consequences, Considered Options, JOOQ mapping master: fetchInto flat, JooqMapper join-nested & multiset, Latar belakang, Pola multiset

### Community 171 - "GajiBatchRoot"
Cohesion: 0.07
Nodes (28): EProsesGaji, Data, EProsesGaji(), FAILED, FINISHED, PENDING, PROSES, WAIT_APPROVAL (+20 more)

### Community 172 - "Level"
Cohesion: 0.05
Nodes (52): GajiPotonganTkkCommandService, GajiPotonganTkkPostRequest, GajiPotonganTkkRepository, GajiPotonganTkkController, RequestMapping, RequiredArgsConstructor, RestController, GajiPotonganTkkPostRequest (+44 more)

### Community 173 - "DefaultSchema"
Cohesion: 0.14
Nodes (11): CatalogImpl, SchemaImpl, DefaultCatalog, Override, Schema, SuppressWarnings, DefaultSchema, Catalog (+3 more)

### Community 174 - "ProfesiController"
Cohesion: 0.11
Nodes (21): Consequences, Considered Options, 0001 Jpa Write Jooq Read Cqrs, Pemisahan jalur Command (JPA) dan Query (JOOQ), ProfesiCommandService, ProfesiQueryService, GetMapping, Page (+13 more)

### Community 175 - "Coding Rules"
Cohesion: 0.40
Nodes (5): Coding Rules, CODINGRULES, Git mv + Edit Workflow (HARD INVARIANT), Workflow, EnterPlanMode

### Community 176 - "CONTEXT-MAP — Kepegawaian"
Cohesion: 0.40
Nodes (5): Context Map, Cara Pakai, CONTEXT-MAP — Kepegawaian, Peta Sub-Context, Sub-Context Files

### Community 177 - "Claim Order — GajiBatchRootServiceImpl (Kafka)"
Cohesion: 0.40
Nodes (5): Catatan ketergantungan, Claim Order — GajiBatchRootServiceImpl (Kafka), Claim Order Gajibatchroot Kafka, Perintah claim, StringSerializer

### Community 178 - "Claim Order — Analisis Bug GajiBatchRoot + Config + CQRS (2026-06-17)"
Cohesion: 0.40
Nodes (5): Catatan Ketergantungan, Claim Order — Analisis Bug GajiBatchRoot + Config + CQRS (2026-06-17), Perintah Claim (real ID, 2026-06-22), Status Realisasi (2026-06-22), Urutan Claim

### Community 179 - "Context — Relasi Antar Domain"
Cohesion: 0.40
Nodes (5): Arah Dependency Lintas-Modul, Context — Relasi Antar Domain, Relationships, Relasi, INSERT

### Community 180 - "0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success")"
Cohesion: 0.40
Nodes (5): 0031 — Batch/workflow endpoints return SavedResult<String> ("{n} success" / "success"), Consequences, Considered Options, Context, Decision

### Community 181 - "0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail"
Cohesion: 0.40
Nodes (5): 0013 — Error path reuses the ApiResponse<T> envelope, not ProblemDetail, Consequences, Considered Options, Context, Decision

### Community 182 - "0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null"
Cohesion: 0.40
Nodes (5): 0014 — GET /master/x/{id} on a missing/soft-deleted row returns 404, not 200-null, Consequences, Considered Options, Context, Decision

### Community 184 - "DetailDasarGajiCommandService.java"
Cohesion: 0.11
Nodes (17): DetailDasarGajiPostRequest, DetailDasarGajiRepository, DetailDasarGajiController, DeleteMapping, GetMapping, Page, RequestMapping, RequiredArgsConstructor (+9 more)

### Community 185 - "DasarGajiQueryRepository.java"
Cohesion: 0.13
Nodes (16): GetMapping, DasarGajiIndexQuery, Data, EqualsAndHashCode, DasarGajiResponse, DasarGajiJooqMapper, DasarGajiQueryRepository, DSLContext (+8 more)

### Community 186 - "GajiProfilResponse"
Cohesion: 0.05
Nodes (48): GajiKomponenCommandService, GajiKomponenMiniProjection, GajiKomponenController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+40 more)

### Community 187 - "GajiPhdpResponse"
Cohesion: 0.13
Nodes (17): GetMapping, GajiPhdpIndexQuery, Data, EqualsAndHashCode, GajiPhdpResponse, GajiPhdpJooqMapper, GajiPhdpQueryRepository, Condition (+9 more)

### Community 188 - "SavedStatus"
Cohesion: 0.07
Nodes (28): PegawaiPatchGaji, PegawaiPatchProfil, PegawaiPostRequest, PegawaiPutRequest, Getter, Setter, SavedStatus, Data (+20 more)

### Community 189 - "JenisKitasQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, JenisKitasController, Data (+14 more)

### Community 190 - "GolonganQueryRepository.java"
Cohesion: 0.10
Nodes (27): GolonganCommandService, GolonganIndexQuery, GolonganQuery, GolonganQueryRepository, GolonganQueryService, GolonganController, GetMapping, Page (+19 more)

### Community 191 - "JenisSpQueryRepository.java"
Cohesion: 0.09
Nodes (24): JenisSpQueryRepository, SanksiRow, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+16 more)

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
Cohesion: 0.50
Nodes (4): APD & Alat Kerja: punya endpoint tulis sendiri, tapi tanpa endpoint baca standalone, Consequences, Considered Options, Model APD & Alat Kerja di rewrite

### Community 197 - "Flyway sebagai sumber kebenaran schema"
Cohesion: 0.50
Nodes (4): Consequences, Considered Options, 0002 Flyway Schema Source Of Truth, Flyway sebagai sumber kebenaran schema

### Community 198 - "KepegawaianApplication"
Cohesion: 0.60
Nodes (3): EnableJpaRepositories, SpringBootApplication, KepegawaianApplication

### Community 201 - "KenaikanBerkalaRequest"
Cohesion: 0.14
Nodes (13): EFilterKenaikanBerkala, BULAN_INI, GTE_1, GTE_2, TAHUN_INI, EJenisKenaikanBerkala(), SK_KENAIKAN_GAJI_BERKALA, SK_KENAIKAN_PANGKAT_GOLONGAN (+5 more)

### Community 202 - "DeletedResult"
Cohesion: 0.06
Nodes (26): DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping (+18 more)

### Community 203 - ".toEntity"
Cohesion: 0.11
Nodes (13): from(), from(), from(), from(), RumahDinas, RumahDinasMapper, GajiBatchMasterProsesMapper, GajiBatchMasterProses (+5 more)

### Community 204 - "GajiBatchRootCommandService.java"
Cohesion: 0.15
Nodes (15): GajiBatchMasterPostRequest, Data, MultipartFile, GajiBatchRootLampiranRepository, GajiBatchMasterCommandService, RequiredArgsConstructor, RestClient, Service (+7 more)

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
Cohesion: 0.04
Nodes (51): ADR-0013, ADR-0017, ADR-0019, ADR-0026, ADR-0027, ADR-0028, ApiResponse, APPROVED (+43 more)

### Community 211 - "HariLiburQueryRepository.java"
Cohesion: 0.07
Nodes (27): HariLiburController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, HariLiburIndexQuery (+19 more)

### Community 218 - "AppwriteUser"
Cohesion: 0.15
Nodes (12): SimpleGrantedAuthority, Component, RequiredArgsConstructor, Slf4j, JwtTokenService, AppwriteUser, AllArgsConstructor, Data (+4 more)

### Community 219 - "CutiJenisQueryRepository.java"
Cohesion: 0.08
Nodes (31): CutiJenisCommandService, CutiJenisQueryRepository, CutiJenisRequest, CutiJenisResponse, CutiJenisController, DeleteMapping, GetMapping, Page (+23 more)

### Community 220 - "GajiBatchMasterProsesResponse"
Cohesion: 0.09
Nodes (27): GajiBatchMasterProsesCommandService, GajiBatchMasterProsesJooqMapper, GajiBatchMasterProsesQueryService, GajiBatchMasterProsesController, DeleteMapping, GetMapping, Page, PostMapping (+19 more)

### Community 221 - "CutiKuotaDeductionResult"
Cohesion: 0.27
Nodes (6): CutiKuotaDeductionResult, Builder, Data, CutiKuotaDeductionAllocator, CutiKuotaDeductionAllocatorTest, Test

### Community 222 - "EApprovalCutiStatus"
Cohesion: 0.04
Nodes (105): CutiApprovalChainGenerator, CutiApprovalChainRepository, CutiApprovalPostRequest, CutiApprovalRepository, CutiKlaimDetailRepository, CutiKuotaAllocator, CutiKuotaQueryRepository, CutiKuotaRepository (+97 more)

### Community 223 - "ListResult"
Cohesion: 0.06
Nodes (19): GetMapping, Page, GetMapping, ResponseEntity, EnumOption, Data, EqualsAndHashCode, ListResult (+11 more)

### Community 225 - "JenjangPendidikan"
Cohesion: 0.07
Nodes (34): PendidikanPostRequest, PendidikanRepository, Data, JsonIgnore, Specification, PendidikanPostRequest, PendidikanPutRequest, AllArgsConstructor (+26 more)

### Community 226 - "GajiPendapatanNonPajakResponse"
Cohesion: 0.09
Nodes (26): GajiPendapatanNonPajakCommandService, GajiPendapatanNonPajakController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+18 more)

### Community 227 - ".delete"
Cohesion: 0.07
Nodes (22): DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping, DeleteMapping (+14 more)

### Community 228 - "ErrorCode"
Cohesion: 0.33
Nodes (6): ErrorCode(), DB_ERROR, DUPLICATE_BATCH, INTERNAL, UNKNOWN_BATCH, Getter

### Community 229 - "ProfileUpdate"
Cohesion: 0.06
Nodes (41): EntityManager, ProfileUpdate, ProfileUpdateApprovalService, Data, ProfilUpdateAcceptRequest, ProfilUpdateDetail, EProfileUpdateApproval, APPROVED (+33 more)

### Community 230 - "AuthServiceImplTest.java"
Cohesion: 0.29
Nodes (9): AppwriteUserPostRequest, AppwriteUserPostRequest, Builder, Data, AuthServiceImplTest, ActiveProfiles, RestClient, Slf4j (+1 more)

### Community 231 - "AppwriteClient"
Cohesion: 0.12
Nodes (20): AppwriteProperties, Consequences, Considered Options, 0029 Appwriteclient Typed Adapter, Ekstraksi REST client Appwrite ke typed adapter AppwriteClient, Keputusan, Konteks, RestClient (+12 more)

### Community 232 - "RiwayatKeluarRecord"
Cohesion: 0.06
Nodes (4): Override, Record1, SuppressWarnings, RiwayatKeluarRecord

### Community 234 - "StatistikPegawai"
Cohesion: 0.22
Nodes (8): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString, StatistikPegawai

### Community 235 - "ProcessPotonganTkkImpl.java"
Cohesion: 0.09
Nodes (27): GajiBatchPotonganTkkRepository, ProcessPotonganTkk, GajiBatchPotonganTkk, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter (+19 more)

### Community 236 - "RiwayatKontrakController.java"
Cohesion: 0.14
Nodes (14): RiwayatKontrakCommandService, RiwayatKontrakPostRequest, Data, JsonIgnore, Specification, RiwayatKontrakPostRequest, Data, EqualsAndHashCode (+6 more)

### Community 237 - "PageResult"
Cohesion: 0.02
Nodes (80): KartuIdentitasCommandService, LampiranProfilQuery, GetMapping, GetMapping, Page, GetMapping, GetMapping, Page (+72 more)

### Community 238 - "GajiKomponen"
Cohesion: 0.06
Nodes (40): EJenisGaji, GajiKomponenPostRequest, GajiKomponenRepository, Data, EqualsAndHashCode, GajiBatchMasterProsesPostRequest, Data, JsonIgnore (+32 more)

### Community 239 - "GajiBatchRootLampiran"
Cohesion: 0.20
Nodes (11): GajiBatchRootLampiranMiniResponse, EJenisPotonganGaji(), POTONGAN_TAMBAHAN, POTONGAN_TKK, GajiBatchRootLampiran, AllArgsConstructor, Entity, Getter (+3 more)

### Community 240 - ".toString"
Cohesion: 0.11
Nodes (13): PutMapping, PutMapping, Override, Override, Result, PegawaiRingkasanMapper, PegawaiTableRecordMapper, ActiveProfiles (+5 more)

### Community 241 - "ConflictException"
Cohesion: 0.03
Nodes (101): AppwriteClient, AppwriteUser, AuthService, BiodataCommandService, ConflictException, CutiKuotaImportRequest, CutiKuotaPostRequest, EStatusKerja (+93 more)

### Community 242 - "ProfilKeluargaAudRecord"
Cohesion: 0.06
Nodes (4): Override, Record2, SuppressWarnings, ProfilKeluargaAudRecord

### Community 243 - "HariLibur"
Cohesion: 0.18
Nodes (12): EJenisLibur(), CUTI_BERSAMA, LIBUR_NASIONAL, HariLibur, AllArgsConstructor, Entity, Getter, NoArgsConstructor (+4 more)

### Community 244 - "EStatusCuti"
Cohesion: 0.25
Nodes (8): EStatusCuti(), APPROVED, CANCELLED, CONFIRMED, REJECTED, RETURNED, WAIT_APPROVAL, Getter

### Community 245 - "EJenisTunjangan"
Cohesion: 0.07
Nodes (38): GajiTunjanganCommandService, GajiTunjanganPostRequest, GajiTunjanganController, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, GajiTunjanganPostRequest (+30 more)

### Community 246 - "List"
Cohesion: 0.02
Nodes (107): BiodataPostRequest, EGolonganDarah, EnumType, JenisKitasResponse, JenjangPendidikanResponse, KartuIdentitasMiniResponse, List, LocalDate (+99 more)

### Community 247 - "EJenisSk"
Cohesion: 0.05
Nodes (55): LampiranSkAcceptRequest, LampiranSkCommandService, LampiranSkQueryRepository, LampiranSkQueryService, RequestMapping, RequiredArgsConstructor, RestController, LampiranSkController (+47 more)

### Community 248 - "JenisKontrakController.java"
Cohesion: 0.07
Nodes (35): EnumOption, JenisKontrakQueryService, ListResult, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+27 more)

### Community 249 - "RiwayatSkQuery"
Cohesion: 0.08
Nodes (28): CutiKuotaController, GetMapping, RequestMapping, RequiredArgsConstructor, RestController, GetMapping, Page, RequestMapping (+20 more)

### Community 250 - "CutiPegawaiAudRecord"
Cohesion: 0.07
Nodes (4): CutiPegawaiAudRecord, Override, Record2, SuppressWarnings

### Community 254 - "GajiBatchPotonganTkkRecord"
Cohesion: 0.10
Nodes (19): GajiBatchPotonganTkk, Condition, Field, Identity, Index, Name, Override, PlainSQL (+11 more)

### Community 255 - "PengalamanKerjaRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, PengalamanKerjaRecord

### Community 256 - "JabatanQueryRepository.java"
Cohesion: 0.11
Nodes (24): JabatanQuery, JabatanController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+16 more)

### Community 257 - "BiodataPath"
Cohesion: 0.09
Nodes (19): BiodataPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+11 more)

### Community 258 - "KeahlianRecord"
Cohesion: 0.07
Nodes (4): Override, Record1, SuppressWarnings, KeahlianRecord

### Community 259 - "GajiBatchMasterResponse"
Cohesion: 0.10
Nodes (27): GajiBatchMasterCommandService, GajiBatchMasterPostRequest, GajiBatchMasterQueryRepository, GajiBatchMasterQueryService, GajiBatchMasterController, GetMapping, Page, PatchMapping (+19 more)

### Community 260 - "GajiProfil"
Cohesion: 0.09
Nodes (27): GajiProfilCommandService, GajiProfilPostRequest, GajiProfilRepository, GajiProfilController, Page, RequestMapping, RequiredArgsConstructor, RestController (+19 more)

### Community 261 - "JenisSp"
Cohesion: 0.08
Nodes (28): JenisSpRepository, SanksiPostRequest, from(), Data, JsonIgnore, Specification, SanksiPostRequest, Data (+20 more)

### Community 262 - "Pendidikan"
Cohesion: 0.13
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 263 - "WebSecurity.java"
Cohesion: 0.10
Nodes (30): ADMIN, ADR-0029, AuthenticationEntryPoint, AuthenticationException, CorsConfigurationSource, DEV, 0016 Profile Conditional Auth, Profile-conditional authentication (+22 more)

### Community 264 - "PelatihanQueryService.java"
Cohesion: 0.09
Nodes (23): PelatihanQueryRepository, PelatihanDetail, PelatihanQuery, Override, SuppressWarnings, PelatihanDetailJooqMapper, Override, PelatihanJooqMapper (+15 more)

### Community 265 - "SanksiQueryRepository.java"
Cohesion: 0.10
Nodes (24): SanksiQueryRepository, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, SanksiController (+16 more)

### Community 266 - ".between1JanAnd30Jun"
Cohesion: 0.17
Nodes (5): CutiApproveKlaimCutiService, RequiredArgsConstructor, Service, CutiKuotaAllocatorTest, Test

### Community 267 - "GajiKomponenRecord"
Cohesion: 0.08
Nodes (10): GajiKomponenJenisGaji(), NONE, PEMASUKAN, POTONGAN, SuppressWarnings, lookupLiteral(), GajiKomponenRecord, Override (+2 more)

### Community 268 - "Keys"
Cohesion: 0.08
Nodes (8): ForeignKey, SuppressWarnings, UniqueKey, Keys, Override, Record1, SuppressWarnings, ProfilKeluargaRecord

### Community 269 - "tables/GajiPendapatanNonPajak.java"
Cohesion: 0.13
Nodes (18): GajiPendapatanNonPajak, GajiPendapatanNonPajakPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 270 - "tables/GajiKomponen.java"
Cohesion: 0.13
Nodes (18): GajiKomponen, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name (+10 more)

### Community 271 - "ProfilUpdateController.java"
Cohesion: 0.09
Nodes (26): Profil Record Refactor Claim Order, PendidikanQueryRepository, ProfileUpdateQuery, ProfilUpdateAcceptRequest, ProfilUpdateDetail, RevInfoService, GetMapping, Page (+18 more)

### Community 272 - "LampiranRow"
Cohesion: 0.09
Nodes (18): LampiranRow, KartuIdentitasDetail, LampiranRow, PengalamanKerjaDetail, Override, SuppressWarnings, KartuIdentitasDetailJooqMapper, Override (+10 more)

### Community 273 - "JenjangPendidikanRecord"
Cohesion: 0.10
Nodes (7): from(), Override, Record1, SuppressWarnings, JenjangPendidikanRecord, JenjangPendidikan, JenjangPendidikanMapper

### Community 274 - "GitNexus — Code Intelligence"
Cohesion: 0.06
Nodes (29): Always Do, Beads Issue Tracker, CLI, GitNexus — Code Intelligence, Graphify — Knowledge Graph, Never Do, Outputs, Quick Reference (+21 more)

### Community 275 - "Keahlian"
Cohesion: 0.09
Nodes (21): JenisKeahlianResponse, KeahlianQuery, JenisKeahlianResponse, KeahlianResponse, EKualifikasi, BAIK, CUKUP, KURANG (+13 more)

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
Cohesion: 0.15
Nodes (19): DasarGaji, DasarGajiPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+11 more)

### Community 280 - "tables/JenisPelatihan.java"
Cohesion: 0.14
Nodes (18): Condition, Field, ForeignKey, Identity, Index, InverseForeignKey, Name, Override (+10 more)

### Community 281 - "PegawaiQueryService"
Cohesion: 0.12
Nodes (18): RefMiniResponse, RefMiniResponse, PegawaiResponseMutasiContext, PegawaiResponseSession, PegawaiTableResponse, DSLContext, Repository, RequiredArgsConstructor (+10 more)

### Community 282 - "JenisPelatihanQueryRepository.java"
Cohesion: 0.11
Nodes (22): GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, JenisPelatihanController, Data (+14 more)

### Community 283 - "CutiJenis"
Cohesion: 0.15
Nodes (18): CutiJenis, CutiJenisPath, Condition, Field, ForeignKey, Identity, Index, InverseForeignKey (+10 more)

### Community 284 - "GradeRecord"
Cohesion: 0.10
Nodes (7): from(), GradeRecord, Override, Record1, SuppressWarnings, GradeMapper, Grade

### Community 285 - "BiodataAud.java"
Cohesion: 0.15
Nodes (16): BiodataAud, BiodataAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 286 - "DasarGajiAud.java"
Cohesion: 0.15
Nodes (16): DasarGajiAud, DasarGajiAudPath, Condition, Field, ForeignKey, InverseForeignKey, Name, Override (+8 more)

### Community 287 - "PegawaiAud.java"
Cohesion: 0.15
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 288 - "ProfilKeluargaAud.java"
Cohesion: 0.15
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 289 - "PengalamanKerjaCommandService.java"
Cohesion: 0.11
Nodes (23): PengalamanKerjaPostRequest, PengalamanKerjaPutRequest, PengalamanKerjaRepository, Data, JsonIgnore, Specification, PengalamanKerjaPostRequest, PengalamanKerjaPutRequest (+15 more)

### Community 290 - "AlasanBerhentiQueryRepository.java"
Cohesion: 0.12
Nodes (21): AlasanBerhentiController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, AlasanBerhentiIndexQuery (+13 more)

### Community 291 - "RiwayatSkAud.java"
Cohesion: 0.16
Nodes (16): Condition, Field, ForeignKey, InverseForeignKey, Name, Override, PlainSQL, Schema (+8 more)

### Community 292 - "PelatihanCommandService.java"
Cohesion: 0.12
Nodes (19): BiodataRepository, KartuIdentitasPostRequest, PelatihanPostRequest, Data, JsonIgnore, Specification, KartuIdentitasPostRequest, KartuIdentitasPutRequest (+11 more)

### Community 293 - "DetailDasarGajiRecord"
Cohesion: 0.10
Nodes (4): DetailDasarGajiRecord, Override, Record1, SuppressWarnings

### Community 294 - "GradeQuery"
Cohesion: 0.13
Nodes (20): GradeIndexQuery, GradeController, GetMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+12 more)

### Community 295 - "FileUploadUtil"
Cohesion: 0.11
Nodes (16): DeleteMapping, GetMapping, Page, PreAuthorize, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController (+8 more)

### Community 296 - "tables/HariLibur.java"
Cohesion: 0.16
Nodes (15): HariLibur, Condition, Field, Identity, Index, Name, Override, PlainSQL (+7 more)

### Community 297 - "BiodataAudRecord"
Cohesion: 0.11
Nodes (4): BiodataAudRecord, Override, Record2, SuppressWarnings

### Community 298 - "BiodataQueryService.java"
Cohesion: 0.13
Nodes (20): BiodataDetailQuery, BiodataQueryRepository, BiodataDashboardQuery, DSLContext, Repository, RequiredArgsConstructor, BiodataQueryRepository, DSLContext (+12 more)

### Community 299 - "CutiKuota"
Cohesion: 0.11
Nodes (20): CutiKuotaImportRequest, Data, JsonIgnore, MultipartFile, Specification, CutiKuotaPostRequest, Data, JsonIgnore (+12 more)

### Community 300 - "GajiKomponenAudRecord"
Cohesion: 0.10
Nodes (4): GajiKomponenAudRecord, Override, Record2, SuppressWarnings

### Community 301 - "DetailDasarGajiQueryRepository.java"
Cohesion: 0.15
Nodes (15): DetailDasarGajiNominal, DetailDasarGajiQueryRepository, Condition, DSLContext, Field, Page, Repository, RequiredArgsConstructor (+7 more)

### Community 302 - "GajiTunjanganRecord"
Cohesion: 0.11
Nodes (4): GajiTunjanganRecord, Override, Record1, SuppressWarnings

### Community 303 - "GajiParameterSettingCommandService.java"
Cohesion: 0.15
Nodes (16): GajiParameterSettingCommandService, GajiParameterSettingPostRequest, GajiParameterSettingRepository, GajiParameterSettingController, DeleteMapping, Page, RequestMapping, RequiredArgsConstructor (+8 more)

### Community 304 - "LampiranProfilCommandService"
Cohesion: 0.13
Nodes (17): KartuIdentitasRepository, ProfilKeluargaRepository, RequiredArgsConstructor, Service, Transactional, KartuIdentitasLampiranCommandService, RequiredArgsConstructor, Service (+9 more)

### Community 305 - "KeahlianQueryService.java"
Cohesion: 0.12
Nodes (16): KeahlianDetailQuery, KeahlianQueryRepository, KeahlianDetail, Override, SuppressWarnings, KeahlianDetailJooqMapper, LampiranProfilJooqMapper, DSLContext (+8 more)

### Community 306 - "IdsAbstract"
Cohesion: 0.11
Nodes (20): IdsAbstract, AllArgsConstructor, Audited, EntityListeners, Getter, MappedSuperclass, Override, RequiredArgsConstructor (+12 more)

### Community 307 - "CutiJenisRecord"
Cohesion: 0.11
Nodes (4): CutiJenisRecord, Override, Record1, SuppressWarnings

### Community 309 - "CutiJenis"
Cohesion: 0.13
Nodes (16): CutiJenisPostRequest, CutiJenisPostRequest, Data, JsonIgnore, Specification, CutiJenisPutRequest, CutiJenis, AllArgsConstructor (+8 more)

### Community 310 - "GajiPotonganTkkRecord"
Cohesion: 0.12
Nodes (4): GajiPotonganTkkRecord, Override, Record1, SuppressWarnings

### Community 311 - "HariLiburRecord"
Cohesion: 0.12
Nodes (4): HariLiburRecord, Override, Record1, SuppressWarnings

### Community 312 - "SpecificationBuilder"
Cohesion: 0.17
Nodes (8): CriteriaBuilder, Root, SafeVarargs, JsonIgnore, Pegawai, Specification, Specification, SpecificationBuilder

### Community 313 - "GajiPhdpCommandService.java"
Cohesion: 0.15
Nodes (17): GajiPhdpCommandService, GajiPhdpPostRequest, GajiPhdpRepository, GajiPhdpController, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity (+9 more)

### Community 314 - "PengalamanKerjaQueryService.java"
Cohesion: 0.14
Nodes (15): PengalamanKerjaQueryRepository, PengalamanKerjaQuery, Override, PengalamanKerjaJooqMapper, DSLContext, Field, Page, Repository (+7 more)

### Community 315 - "GajiBatchRootController.java"
Cohesion: 0.14
Nodes (18): GajiBatchRootController, RequestMapping, RequiredArgsConstructor, RestController, GajiBatchRootIndexQuery, Data, EqualsAndHashCode, GajiBatchRootQueryRepository (+10 more)

### Community 316 - "ProfilKeluargaJooqMapperTest"
Cohesion: 0.21
Nodes (8): DSLContext, Field, Test, ProfilKeluargaJooqMapperTest, DSLContext, Field, Test, PendidikanJooqMapperTest

### Community 317 - "DasarGajiController.java"
Cohesion: 0.17
Nodes (15): DasarGajiPostRequest, DasarGajiRepository, DasarGajiController, Page, RequestMapping, RequiredArgsConstructor, RestController, DasarGajiPostRequest (+7 more)

### Community 318 - "BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`)"
Cohesion: 0.10
Nodes (20): 1. `GET /pegawai/{id}/mutasi-context`, 2. `GET /master/profesi/jabatan/{id}`, 3. Konfirmasi — snapshot nilai "Lama" (`*LamaId`), 4. `GET /penggajian/detail-dasar-gaji/{golonganId}/{masaKerja}` — sudah ada, 2 hal perlu dikonfirmasi, 4a. Konfirmasi arti `masaKerja`, 4b. Response membocorkan entity JPA mentah, 5. Konteks — matriks visibilitas field (FYI, tidak butuh perubahan BE), BE Requirement — Form Mutasi Pegawai (kondisional per `jenisMutasi`) (+12 more)

### Community 319 - "Master Query Optimization Pattern"
Cohesion: 0.10
Nodes (20): 1. Prinsip, 2. Lapisan Arsitektur, 3. Pola per Endpoint, 3a. List / Dropdown (GET /list), 3b. Index / Page (GET /), 3c. Detail (GET /{id}), 4. Aturan Penting, 4b. Kolom yang tidak dipakai DTO jangan di-select (+12 more)

### Community 320 - "JenjangPendidikanController.java"
Cohesion: 0.16
Nodes (15): JenjangPendidikanPostRequest, JenjangPendidikanRepository, Page, RequestMapping, RequiredArgsConstructor, RestController, JenjangPendidikanController, Data (+7 more)

### Community 321 - "AppwriteClientTest"
Cohesion: 0.21
Nodes (5): MockRestServiceServer, Override, getName(), AppwriteClientTest, Test

### Community 322 - "RiwayatKontrakQueryRepository.java"
Cohesion: 0.16
Nodes (15): RequestMapping, RequiredArgsConstructor, RestController, RiwayatKontrakController, RiwayatKontrakQuery, DSLContext, Field, Page (+7 more)

### Community 323 - "PendidikanAud.java"
Cohesion: 0.18
Nodes (12): Condition, Field, ForeignKey, Override, PlainSQL, Schema, Select, SQL (+4 more)

### Community 324 - "ADR-0003"
Cohesion: 0.11
Nodes (20): ADR-0003, ADR-0004, Consequences, Considered Options, 0032 Squash Migration Baseline Bersih Dari Dump Db Existing, Konteks, Squash migration jadi baseline bersih, di-derive dari dump DB kepegawaian existing, Catatan risiko (+12 more)

### Community 325 - "IdsAbstract"
Cohesion: 0.15
Nodes (18): IdsAbstract, Audited, Getter, MappedSuperclass, Setter, ToString, LampiranSp, AllArgsConstructor (+10 more)

### Community 326 - "BiodataDashboardQueryTest"
Cohesion: 0.22
Nodes (7): BiodataDashboardResponse, PendidikanDashboard, BiodataDashboardQueryTest, DSLContext, Field, Test, Test

### Community 327 - "RiwayatTerminasiAud.java"
Cohesion: 0.19
Nodes (11): Condition, Field, Override, PlainSQL, Schema, Select, SQL, SuppressWarnings (+3 more)

### Community 328 - "CutiKuotaTemplateBuilder.java"
Cohesion: 0.18
Nodes (12): ByteArrayResource, ByteArrayResource, Workbook, CutiKuotaTemplateBuilder, ByteArrayResource, CellStyle, RequiredArgsConstructor, ResponseEntity (+4 more)

### Community 329 - "Claim Order — Security: Dev Chain Validasi Bearer Token + Fallback DevAuth (ADR-0033)"
Cohesion: 0.11
Nodes (17): Consequences, Considered Options, Dev chain memvalidasi Bearer token, fallback Dev User hanya saat tanpa Bearer, Keputusan, Konteks, A. Klaim berurutan (master list), B. Semantik target (acceptance semua child), C. Pre-flight checklist (sekali sebelum mulai) (+9 more)

### Community 330 - "GlobalExceptionHandler.java"
Cohesion: 0.18
Nodes (12): ExceptionHandler, HttpStatusCode, MethodArgumentNotValidException, ResponseEntityExceptionHandler, RestControllerAdvice, GlobalExceptionHandler, HttpHeaders, Override (+4 more)

### Community 331 - "CutiApprovalChainRecord"
Cohesion: 0.15
Nodes (4): CutiApprovalChainRecord, Override, Record1, SuppressWarnings

### Community 332 - "GolonganRecord"
Cohesion: 0.15
Nodes (4): GolonganRecord, Override, Record1, SuppressWarnings

### Community 333 - "📌 Issue Details"
Cohesion: 0.11
Nodes (18): 1a — kepegawaian-scn · Phase 1, 1b — kepegawaian-sqf · Phase 1, 1c — kepegawaian-39o · Phase 1, 2a — kepegawaian-hit · Phase 2, 2b — kepegawaian-rq2 · Phase 2, 3 — kepegawaian-llq · Phase 3, 4 — kepegawaian-y7u.1 · Phase 4, 4b — kepegawaian-y7u.2 · Phase 4 (+10 more)

### Community 334 - "Mail Service — Code Patterns (Verified Analysis)"
Cohesion: 0.11
Nodes (18): 0. How to read this document, 10. Confirmed pre-existing bugs (do NOT fix without a beads issue), 1. CQRS-lite: Command / Query split, 2. JOOQ read pattern — single-query pagination via window function, 3. Sqid opaque external IDs, 4. Pagination base classes, 4a. DIVERGENCE — two pagination response shapes, 5. Soft delete (+10 more)

### Community 335 - "LampiranProfil"
Cohesion: 0.16
Nodes (15): Data, JsonIgnore, Specification, LampiranProfilAcceptRequest, AllArgsConstructor, Audited, Entity, Getter (+7 more)

### Community 336 - "OpenApiConfig"
Cohesion: 0.28
Nodes (6): EnableWebMvc, GroupedOpenApi, OpenAPI, Bean, Configuration, OpenApiConfig

### Community 337 - "KartuIdentitasQueryService.java"
Cohesion: 0.18
Nodes (12): KartuIdentitasQueryRepository, DSLContext, Field, Page, Repository, RequiredArgsConstructor, KartuIdentitasQueryRepository, Page (+4 more)

### Community 338 - "Penggajian Cqrs Claim Order"
Cohesion: 0.13
Nodes (16): Consequences, Considered Options, 0024 Gajibatchroot Kafka Diisolasi Ke Eventpublisher, Publikasi Kafka GajiBatchRoot diisolasi ke GajiBatchRootEventPublisher, dipublish after-commit, Penggajian Cqrs Claim Order, ENDPOINT, GajiBatchMasterJooqMapper, GajiBatchMasterProsesServiceImpl (+8 more)

### Community 339 - "JenisKitasPostRequest"
Cohesion: 0.18
Nodes (10): JenisKitasRepository, Data, JsonIgnore, Specification, JenisKitasPostRequest, JenisKitas, JenisKitasMapper, RequiredArgsConstructor (+2 more)

### Community 340 - "JenisSpCommandServiceTest"
Cohesion: 0.22
Nodes (10): JenisSpPostRequest, Data, EqualsAndHashCode, JenisSpPutRequest, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest (+2 more)

### Community 341 - ".build"
Cohesion: 0.25
Nodes (10): ConstraintViolation, Errors, ErrorResult, Data, EqualsAndHashCode, NoArgsConstructor, ResponseEntity, Data (+2 more)

### Community 342 - "MimeTypesUtilsImpl"
Cohesion: 0.31
Nodes (4): MimeTypesUtils, Override, Service, MimeTypesUtilsImpl

### Community 343 - "StatusPegawaiController.java"
Cohesion: 0.21
Nodes (11): GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, StatusPegawaiController, StatusPegawaiResponse, Service (+3 more)

### Community 344 - "CutiApprovalChain"
Cohesion: 0.18
Nodes (11): EReadWriteStatus, NONE, READ, WRITE, CutiApprovalChain, Entity, Getter, NoArgsConstructor (+3 more)

### Community 345 - "ProfileUpdateService"
Cohesion: 0.15
Nodes (12): EProfileUpdateTable, BIODATA, KEAHLIAN, KELUARGA, PELATIHAN, PENDIDIKAN, PENGALAMAN_KERJA, RequiredArgsConstructor (+4 more)

### Community 346 - "GajiParameterSetting"
Cohesion: 0.20
Nodes (11): GajiParameterSetting, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+3 more)

### Community 347 - "GajiPhdp"
Cohesion: 0.20
Nodes (11): GajiPhdp, AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete (+3 more)

### Community 348 - ".Jabatan"
Cohesion: 0.37
Nodes (4): DSLContext, Field, Test, PegawaiSessionQueryRepositoryTest

### Community 349 - "GajiPendapatanNonPajak"
Cohesion: 0.21
Nodes (12): GajiPendapatanNonPajak, AllArgsConstructor, Audited, Entity, EntityListeners, Getter, NoArgsConstructor, Setter (+4 more)

### Community 350 - "PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian"
Cohesion: 0.15
Nodes (13): Architectural Decisions, Further Notes, Implementation Decisions, Modul yang Di-test, Out of Scope, PRD: Penerapan CQRS, JOOQ, dan Flyway pada Kepegawaian, Prinsip Testing, Prior Art (+5 more)

### Community 351 - "LampiranProfilQueryService"
Cohesion: 0.24
Nodes (10): LampiranProfilRepository, DSLContext, Repository, RequiredArgsConstructor, LampiranProfilQueryRepository, LampiranProfilRepository, RequiredArgsConstructor, ResponseEntity (+2 more)

### Community 352 - "MasterBaseEntity"
Cohesion: 0.18
Nodes (11): MasterBaseEntity, AlatKerja, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete (+3 more)

### Community 353 - "Serializable"
Cohesion: 0.22
Nodes (9): Serializable, GajiBatchRootErrorLogsResponse, PegawaiProfilUpdate, GajiBatchRootErrorLogs, Entity, Getter, Setter, Table (+1 more)

### Community 354 - "RumahDinas"
Cohesion: 0.19
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+1 more)

### Community 355 - "MasterBaseEntity"
Cohesion: 0.23
Nodes (9): AllArgsConstructor, EntityListeners, Getter, MappedSuperclass, NoArgsConstructor, Override, Setter, SQLRestriction (+1 more)

### Community 356 - "CutiKlaimDetail"
Cohesion: 0.18
Nodes (9): CutiKlaimDetail, AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, Table, ToString (+1 more)

### Community 357 - "DateHelper"
Cohesion: 0.21
Nodes (4): DateHelper, DateHelperTest, Slf4j, Test

### Community 358 - ".delete_withChildSubJabatan_throwsConflict"
Cohesion: 0.32
Nodes (6): JabatanCommandServiceTest, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test

### Community 359 - "Claim Order 2026 06 17 Analisis Bug"
Cohesion: 0.17
Nodes (12): COMPLETED, DataAccessException, Claim Order 2026 06 17 Analisis Bug, Klaster A — sudah executed, DRAFT, ErrorCode, Integer, KafkaException (+4 more)

### Community 360 - "AlasanBerhenti"
Cohesion: 0.21
Nodes (9): AlasanBerhenti, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete, Table (+1 more)

### Community 361 - "JenisKitas"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Override, Setter, SQLDelete, Table (+1 more)

### Community 362 - "Pelatihan"
Cohesion: 0.21
Nodes (11): AllArgsConstructor, Audited, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, SQLRestriction (+3 more)

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

### Community 367 - "Prefs"
Cohesion: 0.24
Nodes (9): AllArgsConstructor, Getter, JsonIgnoreProperties, NoArgsConstructor, Setter, ToString, Prefs, Data (+1 more)

### Community 368 - ".save"
Cohesion: 0.35
Nodes (3): from(), CutiJenisMapper, CutiJenis

### Community 369 - "JenisKeahlianPostRequest"
Cohesion: 0.25
Nodes (6): Data, JsonIgnore, Specification, JenisKeahlianPostRequest, JenisKeahlian, JenisKeahlianMapper

### Community 370 - "JenisKeahlian"
Cohesion: 0.24
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+1 more)

### Community 371 - ".KeahlianAud"
Cohesion: 0.36
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, KeahlianAudPath

### Community 372 - ".RiwayatTerminasiAud"
Cohesion: 0.36
Nodes (5): ForeignKey, InverseForeignKey, Name, Table, RiwayatTerminasiAudPath

### Community 373 - ".handle"
Cohesion: 0.33
Nodes (8): AccessDeniedException, AccessDeniedHandler, DeniedHandler, Component, HttpServletRequest, HttpServletResponse, Override, RequiredArgsConstructor

### Community 374 - "Configuration"
Cohesion: 0.29
Nodes (7): Configuration, DefConfig, Configuration, Getter, Bean, Configuration, ThreadPoolConfig

### Community 375 - "RedisHelperTest"
Cohesion: 0.33
Nodes (7): DataRedisTest, GenericContainer, Import, StringRedisTemplate, Test, RedisHelperTest, Testcontainers

### Community 376 - "Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`"
Cohesion: 0.20
Nodes (9): 1. DTO — `PegawaiResponseSession.java`, 2. Repository — `PegawaiSessionQueryRepository.java`, 3. Verifikasi, Checklist Implementasi, Claim Order — `statusPegawai` di `GET /pegawai/{id}/session`, Format di berbagai endpoint, Konteks & Keputusan Desain, Referensi File (+1 more)

### Community 377 - ".createStyle"
Cohesion: 0.36
Nodes (4): Font, Row, ExcelHelper, CellStyle

### Community 378 - "Keputusan yang Disepakati"
Cohesion: 0.20
Nodes (10): 1. CQRS Split, 2. JOOQ Code Generation, 3. Flyway Strategy, 4. Service Layer Pattern, 5. Repository Structure, 6. Migration Priority, 7. Envers Three-Tier Audit, 8. Performance Improvements (+2 more)

### Community 379 - "AuthController.java"
Cohesion: 0.38
Nodes (7): AuthController, GetMapping, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestClient, RestController

### Community 380 - "PelatihanController"
Cohesion: 0.24
Nodes (7): DeleteMapping, Page, RequestMapping, RequiredArgsConstructor, ResponseEntity, RestController, PelatihanController

### Community 381 - "RiwayatKeluar"
Cohesion: 0.20
Nodes (10): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, SQLRestriction, Table (+2 more)

### Community 382 - "Apd"
Cohesion: 0.22
Nodes (9): Apd, AllArgsConstructor, Entity, Getter, RequiredArgsConstructor, Setter, SQLDelete, Table (+1 more)

### Community 383 - "JenisPelatihan"
Cohesion: 0.27
Nodes (9): AllArgsConstructor, Entity, Getter, NoArgsConstructor, Setter, SQLDelete, Table, ToString (+1 more)

### Community 384 - ".PendidikanAud"
Cohesion: 0.40
Nodes (4): InverseForeignKey, Name, Table, PendidikanAudPath

### Community 385 - "GolonganWriteIT.java"
Cohesion: 0.31
Nodes (7): GolonganWriteIT, ActiveProfiles, AfterEach, JdbcTemplate, SpringBootTest, Test, Transactional

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
Nodes (7): ADR-0002, Domain, Dokumentasi yang Dibuat, Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration, Next Steps, CQRS Migration Roadmap, PRD: CQRS, JOOQ, and Flyway

### Community 394 - "Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build"
Cohesion: 0.25
Nodes (8): ADR-0012, Consequences, Considered Options, 0015 Jooq Generated Sources Committed Manual Regen, Keputusan, Status, Sumber JOOQ ter-generate di-commit ke git & di-regen manual, bukan di-generate tiap build, GenerationTool

### Community 395 - "context7"
Cohesion: 0.25
Nodes (7): headers, type, url, Authorization, mcp, context7, $schema

### Community 396 - "GajiBatchRootPostRequest"
Cohesion: 0.46
Nodes (4): GajiBatchRootPostRequest, Data, JsonIgnore, MultipartFile

### Community 397 - "BiodataDetailJooqMapperTest"
Cohesion: 0.43
Nodes (4): BiodataDetailJooqMapperTest, DSLContext, Field, Test

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
Cohesion: 0.48
Nodes (6): getCatalog(), getLiteral(), getSchema(), Catalog, Override, Schema

### Community 407 - "0018 Changedstatus Server Resolved By Role"
Cohesion: 0.33
Nodes (6): ADR-0016, CommandService, changedStatus is server-resolved by role, not sent by the client, 0018 Changedstatus Server Resolved By Role, PostRequest, PutRequest

### Community 408 - "JooqConfig.java"
Cohesion: 0.53
Nodes (4): DefaultConfigurationCustomizer, Bean, Configuration, JooqConfig

### Community 409 - "KafkaTemplate"
Cohesion: 0.47
Nodes (4): KafkaTemplate, JajalKafkaTest, KafkaTemplate, SpringBootTest

### Community 410 - "SELECT"
Cohesion: 0.53
Nodes (5): SELECT, BiodataDetailQuery, DSLContext, Repository, RequiredArgsConstructor

### Community 411 - ".restClient"
Cohesion: 0.53
Nodes (4): Bean, Component, RestClient, WebClientConfig

### Community 412 - "GajiBatchRootEventPublisher"
Cohesion: 0.60
Nodes (5): GajiBatchRootEventPublisher, Component, KafkaTemplate, RequiredArgsConstructor, Slf4j

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

### Community 419 - "ProfesiPutRequest"
Cohesion: 0.83
Nodes (3): Data, EqualsAndHashCode, ProfesiPutRequest

### Community 420 - "EReferensiPegawai"
Cohesion: 0.50
Nodes (3): EReferensiPegawai, BIODATA, PEGAWAI

## Knowledge Gaps
- **1264 isolated node(s):** `build-dev.sh script`, `copy.sh script`, `npx`, `@upstash/context7-mcp`, `CONTEXT7_API_KEY` (+1259 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **45 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `List` connect `List` to `Core Entities & Pagination`, `DTO Patterns & Builders`, `Adapter & Config Mappers`, `Profil Biodata & Pendidikan`, `Domain Context Docs`, `PengalamanKerjaAudRecord`, `KartuIdentitasAudRecord`, `LampiranSkAudRecord`, `StatistikPegawaiRecord`, `LampiranProfilRecord`, `PegawaiPath`, `CutiKuotaAudRecord`, `JabatanMiniResponse`, `GajiPotonganTkkAudRecord`, `CutiJenisAudRecord`, `DetailDasarGajiAudRecord`, `GajiPendapatanNonPajakAudRecord`, `RiwayatCutiAudRecord`, `CutiPegawai`, `Organisasi`, `RiwayatCutiRecord`, `GajiParameterSettingAudRecord`, `Jabatan`, `Biodata`, `Tables`, `RiwayatTerminasi`, `GajiParameterSettingRecord`, `GajiPhdpRecord`, `GajiProfilAudRecord`, `FlywaySchemaHistoryCopy1Record`, `RiwayatSp`, `Grade`, `RiwayatKontrakRecord`, `RiwayatSk`, `Golongan`, `SanksiSp`, `GajiBatchMaster`, `ProfilKeluarga`, `KartuIdentitas`, `RiwayatMutasi`, `AlasanBerhentiRecord`, `ApdRecord`, `GajiTunjangan`, `Keahlian`, `tables/Pelatihan.java`, `GajiPotonganTkk`, `JenisSp`, `AlatKerjaRecord`, `GajiBatchRoot`, `tables/GajiBatchRootLampiran.java`, `tables/GajiBatchRootErrorLogs.java`, `tables/PengalamanKerja.java`, `tables/CutiApprovalChain.java`, `tables/GajiProfil.java`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `tables/DetailDasarGaji.java`, `Page`, `JenisKitasRecord`, `Level`, `NotFoundException`, `tables/JenisKeahlian.java`, `tables/RumahDinas.java`, `CutiApprovalAudRecord`, `GajiPhdpAudRecord`, `RiwayatMutasiAud.java`, `JenjangPendidikanResponse`, `CutiPegawaiAud.java`, `RiwayatSpAud.java`, `PelatihanAudRecord`, `Profesi`, `GajiKomponenAud.java`, `RiwayatKontrakAudRecord`, `Graph Report`, `LampiranSkRecord`, `UpdatableRecordImpl`, `Organisasi`, `KeahlianAud.java`, `LampiranProfilAudRecord`, `CutiApprovalRecord`, `PegawaiController`, `OrganisasiQueryRepository.java`, `GajiBatchMasterProsesRecord`, `PendidikanQueryService.java`, `DefaultSchema`, `DetailDasarGajiCommandService.java`, `DasarGajiQueryRepository.java`, `GajiProfilResponse`, `GajiPhdpResponse`, `SavedStatus`, `JenisKitasQueryRepository.java`, `GolonganQueryRepository.java`, `JenisSpQueryRepository.java`, `PrefRole`, `ProfilUpdateRecord`, `HariLiburQueryRepository.java`, `CutiJenisQueryRepository.java`, `GajiBatchMasterProsesResponse`, `EApprovalCutiStatus`, `ListResult`, `GajiPendapatanNonPajakResponse`, `.delete`, `ProfileUpdate`, `AuthServiceImplTest.java`, `AppwriteClient`, `ProcessPotonganTkkImpl.java`, `PageResult`, `.toString`, `ConflictException`, `EJenisTunjangan`, `EJenisSk`, `JenisKontrakController.java`, `RiwayatSkQuery`, `GajiBatchPotonganTkkRecord`, `JabatanQueryRepository.java`, `BiodataPath`, `GajiBatchMasterResponse`, `JenisSp`, `Pendidikan`, `WebSecurity.java`, `PelatihanQueryService.java`, `SanksiQueryRepository.java`, `tables/GajiPendapatanNonPajak.java`, `tables/GajiKomponen.java`, `LampiranRow`, `Keahlian`, `JenisKeahlianQueryRepository.java`, `RumahDinasQueryRepository.java`, `TableImpl`, `tables/JenisPelatihan.java`, `PegawaiQueryService`, `JenisPelatihanQueryRepository.java`, `CutiJenis`, `BiodataAud.java`, `DasarGajiAud.java`, `PegawaiAud.java`, `ProfilKeluargaAud.java`, `AlasanBerhentiQueryRepository.java`, `RiwayatSkAud.java`, `GradeQuery`, `tables/HariLibur.java`, `BiodataQueryService.java`, `DetailDasarGajiQueryRepository.java`, `KeahlianQueryService.java`, `SpecificationBuilder`, `PengalamanKerjaQueryService.java`, `GajiBatchRootController.java`, `DasarGajiController.java`, `JenjangPendidikanController.java`, `PendidikanAud.java`, `RiwayatTerminasiAud.java`, `CutiKuotaTemplateBuilder.java`, `OpenApiConfig`, `KartuIdentitasQueryService.java`, `JenisSpCommandServiceTest`, `.build`, `StatusPegawaiController.java`, `LampiranProfilQueryService`, `MasterBaseEntity`, `CutiKlaimDetail`, `.delete_withChildSubJabatan_throwsConflict`, `Prefs`, `BiodataSelects.java`, `ProfilKeluargaSelects.java`?**
  _High betweenness centrality (0.166) - this node is a cross-community bridge._
- **Why does `LocalDate` connect `List` to `Core Entities & Pagination`, `DTO Patterns & Builders`, `Validation & Error Handling`, `Relation Mappings & DSL`, `Domain Context Docs`, `PagedRequest`, `RecordMapper`, `KartuIdentitasAudRecord`, `DasarGaji`, `BiodataRecord`, `PegawaiPath`, `VPegawaiRecord`, `CutiKuotaAudRecord`, `DasarGajiAudRecord`, `JabatanMiniResponse`, `DasarGajiRecord`, `CutiPegawai`, `Biodata`, `RiwayatTerminasi`, `RiwayatSp`, `RiwayatKontrakRecord`, `RiwayatSk`, `ProfilKeluarga`, `KartuIdentitas`, `RiwayatMutasi`, `tables/Pelatihan.java`, `Specification`, `tables/CutiKuota.java`, `tables/CutiKlaimDetail.java`, `Page`, `Decisions Cuti`, `RiwayatMutasiAud.java`, `JenjangPendidikanResponse`, `CutiPegawaiAud.java`, `RiwayatSpAud.java`, `PelatihanAudRecord`, `RiwayatKontrakAudRecord`, `UpdatableRecordImpl`, `Organisasi`, `DasarGajiQueryRepository.java`, `SavedStatus`, `HariLiburQueryRepository.java`, `EApprovalCutiStatus`, `RiwayatKeluarRecord`, `RiwayatKontrakController.java`, `ConflictException`, `ProfilKeluargaAudRecord`, `HariLibur`, `EJenisSk`, `CutiPegawaiAudRecord`, `PelatihanQueryService.java`, `ProfilUpdateController.java`, `LampiranRow`, `TableImpl`, `PegawaiQueryService`, `BiodataAud.java`, `DasarGajiAud.java`, `PegawaiAud.java`, `ProfilKeluargaAud.java`, `RiwayatSkAud.java`, `PelatihanCommandService.java`, `tables/HariLibur.java`, `BiodataAudRecord`, `BiodataQueryService.java`, `CutiKuota`, `IdsAbstract`, `HariLiburRecord`, `DasarGajiController.java`, `BiodataDashboardQueryTest`, `RiwayatTerminasiAud.java`, `CutiKuotaTemplateBuilder.java`, `CutiKlaimDetail`, `DateHelper`, `Pelatihan`, `PegawaiAudRecord.java`, `RiwayatMutasiAudRecord.java`, `RiwayatSpAudRecord.java`, `RiwayatTerminasiAudRecord.java`?**
  _High betweenness centrality (0.048) - this node is a cross-community bridge._
- **Why does `Page` connect `Page` to `Core Entities & Pagination`, `JabatanQueryRepository.java`, `GajiBatchMasterResponse`, `GajiProfil`, `PelatihanQueryService.java`, `SanksiQueryRepository.java`, `Enums & Constants`, `ProfilUpdateController.java`, `JenisKeahlianQueryRepository.java`, `RumahDinasQueryRepository.java`, `PegawaiController`, `PegawaiQueryService`, `JenisPelatihanQueryRepository.java`, `OrganisasiQueryRepository.java`, `AlasanBerhentiQueryRepository.java`, `GradeQuery`, `FileUploadUtil`, `PendidikanQueryService.java`, `BiodataQueryService.java`, `JabatanMiniResponse`, `Level`, `DetailDasarGajiQueryRepository.java`, `ProfesiController`, `GajiParameterSettingCommandService.java`, `PengalamanKerjaQueryService.java`, `KeahlianQueryService.java`, `DetailDasarGajiCommandService.java`, `GajiPhdpCommandService.java`, `GajiProfilResponse`, `DasarGajiQueryRepository.java`, `GajiBatchRootController.java`, `JenisKitasQueryRepository.java`, `GolonganQueryRepository.java`, `JenisSpQueryRepository.java`, `JenjangPendidikanController.java`, `DasarGajiController.java`, `RiwayatKontrakQueryRepository.java`, `GajiPhdpResponse`, `KartuIdentitasQueryService.java`, `HariLiburQueryRepository.java`, `CutiJenisQueryRepository.java`, `GajiBatchMasterProsesResponse`, `GajiPendapatanNonPajakResponse`, `Decisions Cuti`, `NotFoundException`, `RiwayatKontrakController.java`, `PageResult`, `ConflictException`, `JenjangPendidikanResponse`, `EJenisTunjangan`, `RiwayatSkQuery`, `Graph Report`, `PelatihanController`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **What connects `build-dev.sh script`, `copy.sh script`, `npx` to the rest of the system?**
  _1264 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Core Entities & Pagination` be split into smaller, more focused modules?**
  _Cohesion score 0.023826789599088367 - nodes in this community are weakly interconnected._
- **Should `Many-to-Many & Base Entities` be split into smaller, more focused modules?**
  _Cohesion score 0.04980842911877394 - nodes in this community are weakly interconnected._
- **Should `List & Java Collections` be split into smaller, more focused modules?**
  _Cohesion score 0.0376020209871745 - nodes in this community are weakly interconnected._
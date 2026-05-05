# CQRS Migration Roadmap

Dokumen ini berisi urutan pengerjaan issue dari infrastruktur awal hingga pembersihan akhir. Pengerjaan harus mengikuti urutan ini karena adanya ketergantungan (dependency) antar issue.

## Phase 1: Infrastructure & Hardening
Fokus pada setup Flyway, JOOQ, dan pembersihan base class entity.

1.  **[Issue #35] Flyway Infrastructure**: Setup baseline migration dan matikan DDL-auto. (AFK)
2.  **[Issue #36] JOOQ Code Generation**: Gradle task untuk generate Java classes dari database. (AFK)
3.  **[Issue #37] IdsAbstract Refactor**: Hapus `@Audited` blanket dan field `changedStatus`. (AFK)
4.  **[Issue #38] Entity Hardening**: Konversi semua `FetchType.EAGER` ke `LAZY`. (AFK)

## Phase 2: Pilot Pattern (Tracer Bullet)
Menentukan pola implementasi CQRS yang akan diikuti oleh modul lain.

5.  **[Issue #39] CQRS Tracer Bullet: Golongan**: Implementasi end-to-end Command (JPA) & Query (JOOQ) untuk entity Golongan. (AFK)

## Phase 3: Master Data Migration
Migrasi seluruh tabel referensi menggunakan pola dari Phase 2.

6.  **[Issue #40] Master: Level, Grade, Profesi**. (AFK)
7.  **[Issue #41] Master: Organisasi + Jabatan** (Termasuk hierarki). (AFK)
8.  **[Issue #42] Master: JenjangPendidikan, JenisKeahlian, JenisPelatihan, JenisSp**. (AFK)
9.  **[Issue #43] Master: Sanksi, AlatKerja, Apd, AlasanBerhenti, RumahDinas, HariLibur, JenisKitas**. (AFK)
10. **[Issue #55] Cuti: CutiJenis + CutiKuota**. (AFK)
11. **[Issue #61] Penggajian: GajiParameterSetting + GajiPendapatanNonPajak**. (AFK)

## Phase 4: Profil Data Migration
Migrasi data personal pegawai.

12. **[Issue #44] Profil: Biodata (NIK PK) + KartuIdentitas**. (AFK)
13. **[Issue #45] Profil: Pendidikan, Pelatihan, Keahlian, PengalamanKerja**. (AFK)
14. **[Issue #46] Profil: ProfilKeluarga, LampiranProfil**. (AFK)
15. **[Issue #47] Profil: ProfileUpdate workflow** (Approval logic). (HITL)

## Phase 5: Core Pegawai
Migrasi entity utama sistem.

16. **[Issue #48] Pegawai QueryService**: JOOQ Projections dengan banyak JOIN. (AFK)
17. **[Issue #49] Pegawai CommandService**: Operasi tulis + GenericPegawaiService. (HITL)

## Phase 6: Kepegawaian (SK & Mutasi)
Modul dengan logic bisnis paling kompleks.

18. **[Issue #50] Kepegawaian: LampiranSk + RiwayatSp**. (AFK)
19. **[Issue #51] Kepegawaian: RiwayatSk + GenericSkService**. (HITL)
20. **[Issue #52] Kepegawaian: RiwayatMutasi + GenericMutasiService**. (HITL)
21. **[Issue #53] Kepegawaian: RiwayatKontrak + GenericKontrakService**. (HITL)
22. **[Issue #54] Kepegawaian: RiwayatTerminasi**. (HITL)

## Phase 7: Cuti & Penggajian (Transaction)
Operasi transaksional bulanan.

23. **[Issue #56] Cuti: CutiApprovalChain + CutiApproval**. (HITL)
24. **[Issue #57] Cuti: CutiPengajuan + Validate + Save**. (HITL)
25. **[Issue #58] Penggajian: GajiProfil + DasarGaji + DetailDasarGaji**. (AFK)
26. **[Issue #59] Penggajian: GajiKomponen, GajiTunjangan, Potongan, Phdp**. (AFK)
27. **[Issue #60] Penggajian: GajiBatch (Root, Master, Proses)** (Kafka integration). (HITL)

## Phase 8: Cleanup
Pembersihan akhir setelah migrasi selesai.

28. **[Issue #62] Cleanup: Drop unused _aud tables**. (AFK)

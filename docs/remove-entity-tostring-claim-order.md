# Claim Order — Remove @ToString from All Entities

**Issue:** kepegawaian-dkio
**Priority:** P1 (CRITICAL)
**Severity:** CRITICAL — @ToString on @Entity triggers lazy loads (LazyInitializationException, N+1)

---

## Context

CODING_RULES: "@ToString (unscoped — triggers lazy loads)" is FORBIDDEN on @Entity.

43 entities currently have `@ToString`. All have `@ManyToOne`/`@OneToMany` associations that will be lazily loaded when `toString()` is called.

**Approach:** Delete `@ToString` annotation from all entities. No replacement needed — entities don't need toString() in production code.

---

## Claim Order

### Step 1: Remove @ToString from all entities
- [x] Delete `@ToString` line from all 43 entity files
- [x] Remove unused `import lombok.ToString;` if present
- [x] Verify: `./gradlew clean compileJava`

### Step 2: Verify
- [x] `./gradlew test` — all green (unit tests pass; 44 IT failures pre-existing, need DB)
- [x] No test relies on entity toString()

---

## Entities (43 files)

| Package | Files |
|---------|-------|
| `profil/` | KartuIdentitas, ProfilKeluarga, ProfileUpdate, Pendidikan, Pelatihan, LampiranProfil, PengalamanKerja, Keahlian |
| `cuti/` | CutiKlaimDetail, CutiKuota |
| `system/` | PrefPermission |
| `penggajian/` | DasarGaji, GajiBatchMasterProses, DetailDasarGaji, GajiKomponen, GajiProfil, GajiTunjangan, GajiPhdp, GajiBatchMaster, GajiPendapatanNonPajak, GajiPotonganTkk, GajiParameterSetting, GajiBatchPotonganTkk, GajiBatchRootErrorLogs |
| `master/` | JenisPelatihan, AlasanBerhenti, Grade, AlatKerja, Apd, RumahDinas, HariLibur, Sanksi, JenjangPendidikan, JenisKeahlian |
| `kepegawaian/` | RiwayatKontrak, LampiranSp, StatistikPegawai, RiwayatSp, LampiranSk, RiwayatCuti, RiwayatSk, RiwayatTerminasi, RiwayatKeluar |

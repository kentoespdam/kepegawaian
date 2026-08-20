# Claim Order — Entity Layer Fixes

**Issue:** kepegawaian-ngwu
**Priority:** P1 (CRITICAL)
**Severity:** CRITICAL — lazy `toString()` triggers LazyInitializationException; `@NoArgsConstructor` without PROTECTED breaks encapsulation

---

## Context

Code review finding: entity `toString()` accesses lazy-loaded associations (`@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne(LAZY)`), causing:
- LazyInitializationException in logs/debugging
- N+1 queries
- Performance degradation

CODING_RULES violation: "@ToString.Exclude MUST exclude all @OneToMany, @ManyToOne, @ManyToMany, @OneToOne(LAZY) from toString."

Additionally, 12+ entities use plain `@NoArgsConstructor` without `AccessLevel.PROTECTED`, breaking encapsulation.

---

## Claim Order (Step-by-Step)

### Step 1: Audit ALL entities for lazy toString
- [ ] List every entity in `entities/` (cuti, pegawai, profil, master, kepegawaian, penggajian, system)
- [ ] For each entity, identify `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne(LAZY)` fields
- [ ] Check if any of these fields are NOT excluded from `toString()`
- [ ] Document findings

### Step 2: Fix @ToString.Exclude on all entities
- [ ] Add `@ToString.Exclude` to all lazy association fields
- [ ] Priority targets: `CutiPegawai`, `CutiApproval` (known lazy toString)
- [ ] Then audit remaining entities

### Step 3: Fix @NoArgsConstructor on all entities
- [ ] Change `@NoArgsConstructor` → `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- [ ] Ensure `@AllArgsConstructor` or `@Builder` is present for construction
- [ ] Verify no code breaks (test compilation)

### Step 4: Verify
- [ ] `./gradlew clean compileJava` — zero errors
- [ ] `./gradlew test` — all green
- [ ] No LazyInitializationException in test output

---

## Entities to Audit

| Package | Entities |
|---------|----------|
| `cuti/` | CutiPegawai, CutiApproval, CutiApprovalChain, CutiKuota, CutiJenis, CutiKlaimDetail |
| `pegawai/` | Pegawai, PegawaiProfilUpdate |
| `profil/` | Biodata, Pendidikan, Keluarga, ProfilKeluarga, KartuIdentitas, PengalamanKerja, Keahlian, Pelatihan, LampiranProfil, ProfileUpdate |
| `master/` | Golongan, Jabatan, Organisasi, HariLibur, AlatKerja, Apd, dll |
| `kepegawaian/` | RiwayatSk, RiwayatMutasi, dll |
| `penggajian/` | GajiBatchRoot |
| `system/` | PrefPermission |

---

## Dependencies

None — can be done first.

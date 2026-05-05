# Grilling Session: Kepegawaian CQRS + JOOQ + Flyway Migration

**Tanggal:** 2026-05-05
**Topik:** Review arsitektur migrasi dari SmartOffice, penerapan CQRS dengan JOOQ, dan Flyway untuk schema management.

---

## Keputusan yang Disepakati

### 1. CQRS Split

| Sisi | Teknologi | Tanggung Jawab |
|------|-----------|----------------|
| **Command** | JPA/Hibernate | Create, update, soft-delete. Mempertahankan Envers, AuditAware, `@SQLDelete`, `@Version` |
| **Query** | JOOQ | List, detail, search, pagination. Type-safe SQL, direct DTO projection |

> ADR: `docs/adr/0001-cqrs-jooq-query-jpa-command.md`

### 2. JOOQ Code Generation

- **Source:** Database live (konek langsung ke MariaDB saat generate)
- **Output:** Committed ke `src/main/java` package `id.perumdamts.kepegawaian.jooq`
- **Regenerate:** Manual via Gradle task saat schema berubah

### 3. Flyway Strategy

- **Baseline:** Generate full `V1__baseline.sql` dari schema production yang ada
- **Existing migration:** `V1_0_0__create_master.sql` dibuang (hanya 7 dari 60+ tabel)
- **Folder:** Rename `db/migrations/` → `db/migration/` (Flyway convention)
- **DDL_AUTO:** `none` di semua environment setelah Flyway aktif
- **Scope:** Schema only (DDL) — data migration dari SmartOffice sudah selesai

### 4. Service Layer Pattern

- **Concrete class** tanpa interface (ikuti mail-service)
- Controller inject **dua service** terpisah: `XxxCommandService` + `XxxQueryService`
- Naming: `PegawaiCommandService`, `PegawaiQueryService`

### 5. Repository Structure

```
repositories/{domain}/
├── jpa/
│   └── XxxRepository.java          ← extends JpaRepository (Command)
└── jooq/
    └── XxxQueryRepository.java      ← @Repository, inject DSLContext (Query)
```

Domain-first grouping, teknologi sebagai subdirectory.

### 6. Migration Priority

```
master → profil → pegawai → kepegawaian → cuti → penggajian → laporan
```

Mulai dari master (paling sederhana, zero risiko bisnis, foundation untuk domain lain).

### 7. Envers Three-Tier Audit

> ADR: `docs/adr/0002-envers-selective-three-tier-audit.md`

| Tier | Entity | Strategy |
|------|--------|----------|
| **1: Full Envers** | Pegawai, RiwayatSk, RiwayatMutasi, RiwayatKontrak, RiwayatTerminasi, RiwayatSp, CutiPegawai, GajiProfil | `@Audited` di class level |
| **2: Simple Audit** | Semua master, profil, penggajian detail | `created_at/by` + `updated_at/by` via AuditAware |
| **3: No Audit** | Pivot/junction tables, logs | Tidak ada |

### 8. Performance Improvements

- **FetchType:** Semua relasi diubah ke `LAZY` (EAGER tidak diperlukan karena reads via JOOQ)
- **`changedStatus`:** Dihapus dari `IdsAbstract` — approval menggunakan entity `ProfileUpdate`/`PegawaiProfilUpdate`
- **Envers:** Dari 60+ tabel audit → ~8-10 (Tier 1 saja)

### 9. DTO Strategy

- **JOOQ reads:** `fetchInto()` untuk simple queries, manual mapping untuk complex JOINs
- **MapStruct:** Belum digunakan sekarang, akan ditambahkan saat implementasi SQIDS
- **SQIDS:** Planned untuk masa depan (encoding ID seperti di mail-service)

---

## Perubahan Code yang Harus Dilakukan

### IdsAbstract.java

```diff
 @MappedSuperclass
 @EntityListeners(AuditingEntityListener.class)
-@Audited
 public abstract class IdsAbstract implements Serializable {
     // ... audit fields tetap ...
-    @Column(name = "changed_status", columnDefinition = "boolean default false")
-    private Boolean changedStatus;
 }
```

### build.gradle

```diff
-//  implementation 'org.flywaydb:flyway-core'
-//  implementation 'org.flywaydb:flyway-mysql'
+    implementation 'org.flywaydb:flyway-core'
+    implementation 'org.flywaydb:flyway-mysql'
```

### Entity Tier 1 (contoh Pegawai.java)

```diff
 @Entity
+@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
 public class Pegawai extends IdsAbstract {
-    @ManyToOne(fetch = FetchType.EAGER)
+    @ManyToOne(fetch = FetchType.LAZY)
     private Biodata biodata;
-    @ManyToOne(fetch = FetchType.EAGER)
+    @ManyToOne(fetch = FetchType.LAZY)
     private Organisasi organisasi;
-    @ManyToOne(fetch = FetchType.EAGER)
+    @ManyToOne(fetch = FetchType.LAZY)
     private Jabatan jabatan;
 }
```

### Entity Tier 2 (contoh Golongan.java)

```diff
 @Entity
-// Tidak ada @Audited — simple audit dari IdsAbstract sudah cukup
 public class Golongan extends IdsAbstract {
     // ... fields tetap sama ...
 }
```

---

## Dokumentasi yang Dibuat

| File | Isi |
|------|-----|
| `CONTEXT.md` | Domain glossary — semua term yang disepakati |
| `docs/adr/0001-cqrs-jooq-query-jpa-command.md` | CQRS: JOOQ Query + JPA Command |
| `docs/adr/0002-envers-selective-three-tier-audit.md` | Envers selektif three-tier |

---

## Next Steps

1. **Generate Flyway baseline** — dump schema dari DB production → `V1__baseline.sql`
2. **Enable Flyway** — uncomment dependency, rename folder, set config
3. **Setup JOOQ code-gen** — Gradle task, generate dari live DB, commit ke `src/main/java`
4. **Refactor IdsAbstract** — hapus `@Audited` dan `changedStatus`
5. **Migrate domain: master/** — split services, create JOOQ query repositories
6. **Iterate** — profil → pegawai → kepegawaian → cuti → penggajian → laporan

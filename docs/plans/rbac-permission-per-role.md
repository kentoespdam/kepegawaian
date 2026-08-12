# Plan: RBAC — Permission Granular per Role

**Issue**: `kepegawaian-9b6l` · **Priority**: P1  
**ADR**: [ADR-0037](../adr/0037-rbac-permission-per-role-didb-mariadb.md)  
**Context**: [language-security.md](../context/language-security.md)

---

## Ringkasan Arsitektur

```
Appwrite User
  └── prefs.roles: ["ADMIN", "HRD"]          ← disimpan di Appwrite

JwtAuthFilter (setiap request JWT masuk)
  ├── validasi Appwrite JWT → AppwriteUser
  ├── baca roles dari prefs.roles
  ├── load Set<Permission> dari MariaDB berdasarkan roles
  └── inject ROLE_xxx + ENTITY:ACTION ke GrantedAuthority Spring

Controller
  └── @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
```

**DevUser** (profile `development`): inject semua permission hardcoded — tidak query DB.

---

## Skema DB Baru

```sql
-- pref_permission: master daftar permission
CREATE TABLE pref_permission (
  name VARCHAR(50) NOT NULL PRIMARY KEY  -- format: "ENTITY:ACTION", mis. "CUTI:APPROVE"
);

-- pref_role_permission: join table Role ↔ Permission
CREATE TABLE pref_role_permission (
  role_id  VARCHAR(50) NOT NULL REFERENCES pref_role(id),
  perm_name VARCHAR(50) NOT NULL REFERENCES pref_permission(name),
  PRIMARY KEY (role_id, perm_name)
);
```

**Note**: `pref_role` sudah ada. Tidak ada perubahan struktur tabelnya.

---

## Claim Order Checklist

### Step 1 — Flyway Migration
- [x] Buat `src/main/resources/db/migration/V{next}__rbac_permission_tables.sql`
- [x] SQL: `CREATE TABLE pref_permission` + `CREATE TABLE pref_role_permission`
- [x] Jalankan `./gradlew flywayMigrate` untuk verifikasi

### Step 2 — Entity JPA
- [x] Buat `id.perumdamts.kepegawaian.entities.system.PrefPermission`
  ```java
  @Entity @Table(name = "pref_permission")
  public class PrefPermission {
      @Id String name; // "CUTI:APPROVE"
  }
  ```
- [x] Update `dto/appwrite/PrefRole.java` (sudah Entity JPA) — tambah relasi:
  ```java
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "pref_role_permission",
      joinColumns = @JoinColumn(name = "role_id"),
      inverseJoinColumns = @JoinColumn(name = "perm_name"))
  Set<PrefPermission> permissions;
  ```
- [x] **WAJIB** `gitnexus_impact({target: "PrefRole", direction: "upstream"})` sebelum edit

### Step 3 — Repository
- [x] Buat `repositories.PrefPermissionRepository` extends `JpaRepository<PrefPermission, String>`

### Step 4 — Permission Inflation di JwtAuthFilter
- [x] **WAJIB** `gitnexus_impact({target: "JwtAuthFilter", direction: "upstream"})` sebelum edit
- [x] Di `JwtAuthFilter`, setelah load `AppwriteUser`:
  1. Ambil roles dari `appwriteUser.getPrefs().getRoles()`
  2. Query `PrefRoleRepository.findAllById(roles)` → `List<PrefRole>`
  3. Collect semua `PrefPermission.name` dari semua roles (union)
  4. Inject ke `UsernamePasswordAuthenticationToken`:
     - `ROLE_xxx` dari roles (existing behavior)
     - `ENTITY:ACTION` dari permissions (baru)
- [x] Perhatikan max 120 lines/file — pecah ke helper `PermissionInflater` jika perlu

### Step 5 — DevAuthFilter (semua permission hardcoded)
- [x] **WAJIB** `gitnexus_impact({target: "DevAuthFilter", direction: "upstream"})` sebelum edit
- [x] Di DevAuthFilter, selain inject `ROLE_ADMIN + ROLE_SYSTEM`, tambah inject semua permission
- [x] Ambil list semua permission dari `PrefPermissionRepository.findAll()` atau hardcode enum

  > **Pilihan**: query DB (selalu fresh) vs hardcode list (no DB dependency di dev). Direkomendasikan: **hardcode constant list** semua permission yang diketahui — sederhana, no DB coupling, cukup untuk dev.

### Step 5b — Fix bug default roles (`kepegawaian-qp0m`)
- [x] **WAJIB** `gitnexus_impact({target: "createUserWithDefaultRoles", direction: "upstream"})` sebelum edit
- [x] [`AppwriteClient.java:79`](../../src/main/java/id/perumdamts/kepegawaian/config/appwrite/AppwriteClient.java) — ubah:
  ```diff
  - List<PrefRole> defaultRoles = List.of(new PrefRole("ADMIN"), new PrefRole("USER"));
  + List<PrefRole> defaultRoles = List.of(new PrefRole("USER"));
  ```
- [x] `bd close kepegawaian-qp0m`

### Step 6 — API Management Permission
- [x] Buat `controllers/system/PrefPermissionController`:
  - `GET /system/permissions` — list semua permission
  - `POST /system/roles/{roleId}/permissions/{permName}` — assign permission ke role
  - `DELETE /system/roles/{roleId}/permissions/{permName}` — revoke permission dari role
- [x] Proteksi semua endpoint dengan `@PreAuthorize("hasRole('SYSTEM')")`

### Step 7 — JOOQ Codegen
- [x] `./gradlew jooqCodegen` — regenerate JOOQ classes setelah schema change
- [x] Commit generated JOOQ files (ikuti convention repo)

### Step 8 — Build & Test
- [x] `./gradlew clean compileJava` — zero error
- [x] `./gradlew test` — all green
- [x] `gitnexus_detect_changes()` — verify scope sesuai
- [x] `npx gitnexus analyze` — refresh index
- [x] `/graphify --update` — update knowledge graph
- [x] `bd close kepegawaian-9b6l`
- [x] Commit: `feat: RBAC permission granular per Role (kepegawaian-9b6l)`
- [x] `git pull --rebase` → `bd dolt push` → `git push`

---

## Permission Catalogue (seed awal — isi setelah infrastruktur jadi)

| Permission | Deskripsi |
|------------|-----------|
| `MASTER:READ` | Baca referensi master |
| `MASTER:WRITE` | Buat/ubah data master |
| `MASTER:DELETE` | Hapus (soft) data master |
| `PEGAWAI:READ` | Baca data pegawai |
| `PEGAWAI:WRITE` | Buat/ubah data pegawai |
| `PEGAWAI:DELETE` | Hapus data pegawai |
| `KEPEGAWAIAN:READ` | Baca SK, mutasi, kontrak, SP |
| `KEPEGAWAIAN:WRITE` | Buat/ubah SK, mutasi, kontrak, SP |
| `KEPEGAWAIAN:DELETE` | Hapus SK, mutasi, kontrak, SP |
| `PROFIL:READ` | Baca profil |
| `PROFIL:UPDATE` | Update profil sendiri (self-service) |
| `PROFIL:APPROVE` | Approve/reject perubahan profil (HRD/ADMIN) |
| `CUTI:READ` | Baca data cuti |
| `CUTI:CREATE` | Ajukan cuti |
| `CUTI:APPROVE` | Approve/reject cuti |
| `PENGGAJIAN:READ` | Baca data penggajian |
| `PENGGAJIAN:WRITE` | Buat/ubah komponen gaji |
| `PENGGAJIAN:PROCESS` | Proses batch gaji |
| `SYSTEM:MANAGE_USER` | CRUD user Appwrite |
| `SYSTEM:MANAGE_ROLE` | CRUD role + assign permission |

---

## Dual Mode Enforcement (selama transisi)

Controller lama yang sudah pakai `hasRole('ADMIN')` **tidak perlu dimigrasi sekaligus**. Pola dual mode:

```java
// Contoh: sebelum (masih valid)
@PreAuthorize("hasRole('ADMIN')")

// Contoh: setelah migrasi per-modul (target akhir)
@PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")

// Atau full permission-based jika role-check sudah tidak diperlukan:
@PreAuthorize("hasAuthority('MASTER:DELETE')")
```

Migrasi per-modul dikerjakan di issue terpisah (tidak di scope issue ini).

---

## Referensi

- [ADR-0037](../adr/0037-rbac-permission-per-role-didb-mariadb.md) — keputusan arsitektur RBAC
- [ADR-0038](../adr/0038-profil-endpoint-split-admin-vs-selfservice.md) — endpoint profil split
- [`language-security.md`](../context/language-security.md) — glossary Permission, Role, Permission Inflation
- [`WebSecurity.java`](../../src/main/java/id/perumdamts/kepegawaian/config/WebSecurity.java)
- [`JwtAuthFilter.java`](../../src/main/java/id/perumdamts/kepegawaian/config/security/JwtAuthFilter.java)
- [`DevAuthFilter.java`](../../src/main/java/id/perumdamts/kepegawaian/config/security/DevAuthFilter.java)
- [`AppwriteClient.java`](../../src/main/java/id/perumdamts/kepegawaian/config/appwrite/AppwriteClient.java)

---

## Issue Terkait

| Issue | Judul | Blocker? |
|-------|-------|----------|
| `kepegawaian-9b6l` | **Issue ini** — RBAC infrastruktur | — |
| `kepegawaian-huis` | Pisah endpoint profil admin vs self-service | Blocked by issue ini |
| `kepegawaian-qp0m` | Bug: hardcoded `ADMIN` di `createUserWithDefaultRoles` — ganti ke `USER` saja | Independen, dikerjakan di Step 5b |

# FE Guide — Memahami Dual-Mode RBAC (Role + Permission)

> Dokumen **pembelajaran** untuk tim **Frontend**: menjelaskan apa itu "dual-mode RBAC" yang diimplementasikan di backend, kenapa polanya seperti itu, dan apa dampaknya pada cara FE membangun UI berbasis akses.

| Item | Nilai |
|------|-------|
| Branch | `rewrite/master-cqrs` |
| Tanggal | 2026-08-14 |
| Status | ✅ **LIVE 100%** — semua modul sudah dual-mode (bukan lagi sebagian) |
| ADR | [ADR-0037](../adr/0037-rbac-permission-per-role-didb-mariadb.md) — RBAC permission granular per role |
| Dokumen terkait | [FE-CONTRACT-profil-update-approval-rbac.md](./FE-CONTRACT-profil-update-approval-rbac.md) (kontrak API), [Plan RBAC](../plans/rbac-permission-per-role.md) |
| Issue utama | `kepegawaian-9b6l` (RBAC — ✅ closed) |

---

## 1. Latar Belakang — Kenapa Ada Dual Mode

Dulu setiap endpoint backend hanya dicek berdasarkan **role** (`hasRole('ADMIN')`). Masalahnya: role itu label kasar. Mau kasih akses "bisa approve cuti tapi tidak boleh hapus master"? Tidak bisa — harus buat role baru atau kasih semuanya.

Solusi: backend menambah lapisan **permission granular** (format `ENTITY:ACTION`, mis. `CUTI:APPROVE`), disimpan di MariaDB, dan diikat ke role. Tapi memigrasi puluhan controller sekaligus berisiko memutus akses user yang sudah berjalan.

**Dual mode** = pola transisi yang dipilih (ADR-0037):

```java
// Sebelum (cek role saja)
@PreAuthorize("hasRole('ADMIN')")

// Sesudah (dual mode: role ATAU permission)
@PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
```

Artinya: **izin lama tidak pernah dicabut** — permission hanya *menambah* jalur akses. Tidak ada endpoint yang menyempit selama migrasi. Sekarang seluruh modul sudah selesai di-migrasi (status 100% dual-mode), dan beberapa jalur sengaja *tidak* dual-mode lagi (lihat section 4).

---

## 2. Konsep Inti: Role vs Permission

| Istilah | Sumber | Contoh | Ciri |
|---------|--------|--------|------|
| **Role** | Appwrite (`prefs.roles`) | `ADMIN`, `HRD`, `USER`, `SYSTEM` | Label; satu user bisa punya banyak role |
| **Permission** | MariaDB (`pref_permission` + `pref_role_permission`) | `CUTI:APPROVE`, `MASTER:WRITE` | Unit akses granular; terikat ke role via join table |

**Aturan union**: permission user = **gabungan (union)** semua permission dari semua role-nya. Role tanpa permission di matriks → user tetap login, tapi tidak mendapat permission apa pun dari role itu.

### Bagaimana server memutuskan akses (di luar kendali FE)

```
Request JWT masuk
  └── JwtAuthFilter
        ├── validasi token Appwrite → identitas user
        ├── baca roles dari prefs.roles  (["ADMIN", "HRD"])
        ├── query permission dari MariaDB untuk role-role tsb
        └── inject ke Spring: ROLE_ADMIN, ROLE_HRD  +  CUTI:APPROVE, MASTER:WRITE, ...

Controller
  └── @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")
        → lolos bila salah satu authority di atas ada
```

> ⚠️ **Poin kunci untuk FE**: proses ini **sepenuhnya di server**. FE **tidak perlu dan tidak boleh** mengirim permission, role, atau header khusus (mis. `X-Role`) ke backend. Satu-satunya yang FE kirim adalah token. Backend yang memutuskan 200/401/403.

---

## 3. Pola Guard yang Dipakai Backend

Semua guard mengikuti satu dari tiga pola:

```java
// 1. Dual-mode (paling umum) — role lama ATAU permission baru
@PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')")

// 2. Dual-mode khusus modul system — role SYSTEM ATAU permission manajemen
@PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_USER'|'SYSTEM:MANAGE_ROLE')")

// 3. Login-only — TANPA guard permission, cukup sesi aktif
//    (tidak ada @PreAuthorize; ditangani WebSecurity anyRequest().authenticated())
```

Contoh nyata di kode:

| File | Guard |
|------|-------|
| `PelatihanController.java:23` (read profil) | `hasRole('ADMIN') or hasAuthority('PROFIL:READ')` |
| `AdminPendidikanController.java:20` (approval profil) | `hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')` |
| `CutiApprovalController.java:28` | `hasRole('ADMIN') or hasAuthority('CUTI:APPROVE')` |
| `GajiBatchRootController.java:49` (proses gaji) | `hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')` |
| `LaporanDnpController.java:21` | `hasRole('ADMIN') or hasAuthority('LAPORAN:READ')` |
| `PrefRoleController.java` (manajemen role) | `hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')` |

### Status migrasi per modul (semua ✅ selesai)

| Modul | Permission yang di-enforce |
|-------|----------------------------|
| master | `MASTER:WRITE`, `MASTER:DELETE` (read = login-only, lihat section 4) |
| pegawai | `PEGAWAI:READ`, `PEGAWAI:WRITE`, `PEGAWAI:DELETE` |
| kepegawaian | `KEPEGAWAIAN:READ`, `KEPEGAWAIAN:WRITE`, `KEPEGAWAIAN:DELETE` |
| profil | `PROFIL:READ`, `PROFIL:UPDATE`, `PROFIL:APPROVE` |
| cuti | `CUTI:READ`, `CUTI:WRITE`, `CUTI:APPROVE` |
| penggajian | `PENGGAJIAN:READ`, `PENGGAJIAN:WRITE`, `PENGGAJIAN:DELETE`, `PENGGAJIAN:PROCESS` |
| laporan | `LAPORAN:READ` |
| system | `SYSTEM:MANAGE_USER`, `SYSTEM:MANAGE_ROLE` |

---

## 4. Tiga "Tier" Akses — Penting untuk UX FE

Setelah migrasi selesai, akses di app ini terbagi tiga tier. FE **harus tahu tier mana** untuk setiap halaman agar tidak salah show/hide:

### Tier 1 — Dual-mode guard (mayoritas endpoint bisnis)

Akses = **punya `ROLE_ADMIN`** ATAU **memegang permission** (mis. `CUTI:READ`).

- ADMIN selalu lolos (guard punya cabang `hasRole('ADMIN')`).
- HRD dan role lain lolos bila role-nya ter-seed permission tsb (lihat matriks section 5).
- **Implikasi FE**: user non-ADMIN bisa saja punya akses (mis. HRD yang pegang `PROFIL:APPROVE`). Jangan sembunyikan fitur hanya karena "bukan ADMIN".

### Tier 2 — Login-only (cukup sesi aktif, TANPA permission)

Beberapa jalur sengaja **tidak** butuh permission — siapa pun yang login boleh akses:

| Jalur | Contoh endpoint |
|-------|-----------------|
| Read data master (referensi) | `GET /master/*` (jabatan, organisasi, golongan, jenis-*, dll.) — `MASTER:READ` dihapus dari katalog (V34) |
| Cuti self-service (create/update/klaim/batal) | `POST/PUT/DELETE /cuti/pengajuan/...` — `CUTI:CREATE` dihapus (V35); **plus ownership check** |
| Baca cuti milik sendiri | `GET /cuti/pengajuan/{pegawaiId}/pegawai`, `GET /cuti/pengajuan/{id}` |
| Referensi lintas modul | `GET /cuti/jenis*`, `GET /penggajian/tunjangan`, `GET /cuti/pengajuan/{tgl}/{tgl}/total-hari-kerja` |

**Implikasi FE**:
- Dropdown data referensi (jabatan, jenis cuti, dsb.) boleh ditampilkan untuk **semua user yang login** — jangan gate dengan permission.
- Form pengajuan cuti boleh tampil untuk semua pegawai; tapi server **ownership check**: non-ADMIN/HRD yang mengirim `pegawaiId` milik orang lain → **403** (dan read cuti orang lain → 404/tersembunyi, lihat section 6).

### Tier 3 — Permission-only (akses menyempit — perbaikan keamanan)

Beberapa jalur yang tadinya terbuka untuk semua user login kini **dipersempit**:

| Jalur | Guard | Dampak |
|-------|-------|--------|
| Antrian approval profil | `PROFIL:APPROVE` | User tanpa permission ini tidak bisa lagi melihat/menyetujui antrian (sebelumnya terbuka) |
| Manajemen role/permission/user | `SYSTEM:MANAGE_ROLE` / `SYSTEM:MANAGE_USER` | User biasa tidak bisa lagi membaca daftar role & permission (sebelumnya bocor) |

**Implikasi FE**: halaman ini hanya tampil untuk ADMIN / pemegang permission terkait.

> ⚠️ **Ringkasan cepat**: "boleh baca referensi" ≠ "boleh baca data pegawai". Referensi = login saja; data bisnis = dual-mode; approval/manajemen = permission-only.

---

## 5. Katalog Permission & Matriks Seed

Katalog final: **21 permission**, semuanya di-enforce ≥1 controller (0 zombie).

| Permission | Arti | ADMIN | HRD | USER |
|------------|------|:-----:|:---:|:----:|
| `MASTER:WRITE` / `MASTER:DELETE` | Kelola data master | ✅ | ✅ | — |
| `PEGAWAI:READ` | Baca data pegawai | ✅ | ✅ | ✅ |
| `PEGAWAI:WRITE` / `PEGAWAI:DELETE` | Kelola data pegawai | ✅ | ✅ | — |
| `KEPEGAWAIAN:READ` | Baca SK, mutasi, kontrak, SP | ✅ | ✅ | ✅ |
| `KEPEGAWAIAN:WRITE` / `KEPEGAWAIAN:DELETE` | Kelola SK, mutasi, kontrak, SP | ✅ | ✅ | — |
| `PROFIL:READ` | Baca profil | ✅ | ✅ | ✅ |
| `PROFIL:UPDATE` | Update profil sendiri (self-service) | ✅ | ✅ | ✅ |
| `PROFIL:APPROVE` | Approve antrian perubahan profil | ✅ | ✅ | — |
| `CUTI:READ` | Baca data cuti | ✅ | ✅ | ✅ |
| `CUTI:WRITE` | Kelola jenis/kuota cuti | ✅ | ✅ | — |
| `CUTI:APPROVE` | Approve/reject cuti | ✅ | ✅ | — |
| `PENGGAJIAN:READ` | Baca data penggajian | ✅ | ✅ | ✅ |
| `PENGGAJIAN:WRITE` / `PENGGAJIAN:DELETE` | Kelola komponen gaji | ✅ | — | — |
| `PENGGAJIAN:PROCESS` | Proses batch gaji | ✅ | — | — |
| `LAPORAN:READ` | Baca laporan | ✅ | ✅ | ✅ |
| `SYSTEM:MANAGE_USER` | CRUD user Appwrite | ✅ | — | — |
| `SYSTEM:MANAGE_ROLE` | CRUD role + assign permission | ✅ | — | — |

> ✅ **HRD kini punya akses yang sebelumnya tidak dimiliki**: write/delete master, write/delete pegawai, approve profil & cuti, kelola jenis/kuota cuti (`CUTI:WRITE`), dan baca laporan. Kalau FE sebelumnya menyembunyikan fitur admin untuk HRD, **sekarang harus ditampilkan**.
>
> ⚠️ Matriks ini **bisa berubah runtime** via API `POST/DELETE /system/roles/{roleId}/permissions/{permName}` (khusus `SYSTEM`). Jangan hardcode matriks di FE — ambil dari `GET /account/me` (section 7).

---

## 6. Perilaku Error yang Perlu Dikenali FE

| Kode | Arti | Kapan |
|------|------|-------|
| **401** | Tidak login / token invalid | Token hilang/kedaluwarsa |
| **403** | Login tapi tidak punya akses (guard gagal) | Dual-mode & permission-only guard menolak |
| **404** | Data tidak ada **atau** sengaja disamarkan | Ownership check profil/cuti: target milik orang lain di-respon 404 (bukan 403) agar tidak bocor info |

> ⚠️ **Jangan infer "boleh akses" dari 404.** Pada jalur ownership (Tier 2), 404 bisa berarti "data orang lain" — itu desain, bukan bug. FE cukup tampilkan "tidak ditemukan".

---

## 7. Membangun UI Berbasis Akses: `GET /account/me`

Endpoint ini sudah live dan merupakan **satu-satunya sumber kebenaran** untuk show/hide menu:

```http
GET /api/account/me
```

```json
{
  "status": 200, "statusText": "OK", "errors": [], "message": "Data Found",
  "data": {
    "id": "123",
    "name": "Budi Santoso",
    "roles": ["ADMIN", "HRD"],
    "permissions": ["MASTER:DELETE", "PEGAWAI:READ", "PROFIL:APPROVE"]
  },
  "timestamp": "2026-08-14 14:30:00"
}
```

- `roles` dan `permissions` ter-sort; `permissions` = union sesuai matriks DB saat itu.
- Butuh login (Bearer token; di dev pakai DevUser).

### Aturan emas FE

1. **Ambil akses dari `permissions` + `roles`, jangan hardcode per user.** Matriks berubah runtime — user yang sama bisa berubah aksesnya.
2. **Ingat dual-mode**: user dengan `roles` berisi `ADMIN` otomatis lolos semua guard dual-mode, **walau** list `permissions`-nya kosong. Logika show/hide harus menganggap ADMIN = punya semua akses Tier 1.
3. **UI hide hanyalah UX — keamanan di server.** Kalau UI salah sembunyi/tampil, yang terjadi paling parah adalah menu yang tidak relevan; backend tetap menolak akses yang tidak berhak.
4. **Login-only (Tier 2)**: jangan sembunyikan berdasarkan permission. Tampilkan untuk semua yang login.
5. **Permission-only (Tier 3)**: sembunyikan fitur approval/manajemen kecuali user punya permission terkait (atau role ADMIN/SYSTEM).

---

## 8. Checklist Praktis Tim FE

- [ ] Refactor logika akses ke helper berbasis `GET /account/me` (roles + permissions), bukan cek `roles.includes('ADMIN')` saja.
- [ ] Sembunyikan/tampilkan menu per permission (`CUTI:APPROVE`, `PROFIL:APPROVE`, `PENGGAJIAN:PROCESS`, `LAPORAN:READ`, `SYSTEM:MANAGE_*`, dst.).
- [ ] Tampilkan fitur admin untuk **HRD** yang sebelumnya disembunyikan (HRD = 14 permission termasuk master/pegawai write + approve).
- [ ] Pastikan dropdown data referensi (master, jenis cuti, dst.) **tidak** di-gate permission — cukup login.
- [ ] Form pengajuan cuti tampil untuk semua pegawai login; siapkan handling **403** (bukan miliknya) dan **404** (ownership read).
- [ ] Jangan kirim field/header `role` atau `permission` dari FE ke backend — server yang memutuskan.
- [ ] Regenerate tipe dari OpenAPI bila perlu; guard tidak mengubah bentuk envelope respons (section 6 di FE-CONTRACT).

---

## 9. FAQ

**Q: Apakah FE perlu mengirim permission saat memanggil API?**
Tidak. Semua inflasi permission dilakukan server dari token. FE cukup kirim Bearer token.

**Q: Kenapa user saya bisa akses padahal permission-nya tidak ada di `permissions`?**
Kemungkinan user itu `ADMIN` (dual-mode `hasRole('ADMIN')` selalu lolos) atau endpoint-nya login-only (Tier 2). Cek tier endpoint-nya.

**Q: Kenapa read master tidak butuh permission?**
Keputusan desain (review 2026-08-13): data referensi boleh dipakai siapa pun yang login, pola sama dengan `/account/me`. Write/delete master tetap butuh `MASTER:WRITE`/`MASTER:DELETE`.

**Q: Apa bedanya 403 dan 404 di jalur cuti/profil self-service?**
403 = bukan punyamu saat menulis (create/update/klaim/batal). 404 = bukan punyamu saat membaca — sengaja disamarkan agar tidak bisa "menebak" data pegawai lain.

**Q: Apakah ada endpoint yang aksesnya menyempit dibanding dulu?**
Ada dua yang disengaja: antrian approval profil (`PROFIL:APPROVE`) dan manajemen system (`SYSTEM:MANAGE_*`) — sebelumnya terbuka untuk semua user login, sekarang hanya untuk yang berhak. Selain itu tidak ada penyempitan (dual-mode menjamin).

**Q: Role `PENGGAJIAN` di seed lama tidak ada permission-nya, gimana?**
Benar — itu gap yang tercatat (role `PENGGAJIAN` seed V21 belum punya matriks; issue follow-up). Sampai ada keputusan, role itu tidak memberi permission apa pun.

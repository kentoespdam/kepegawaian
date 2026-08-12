# RBAC: Permission per Role disimpan di MariaDB, di-inflate saat JWT masuk

Role Appwrite User disimpan di `prefs.roles` (Appwrite). Sebelumnya role hanya dipakai sebagai `GrantedAuthority` (`ROLE_xxx`) langsung — akses endpoint dikontrol kasar lewat `hasRole`. Dengan bertambahnya kompleksitas akses antar modul (master, pegawai, kepegawaian, profil, cuti, penggajian, system), kita butuh permission granular `{ENTITY}:{ACTION}` yang bisa dikonfigurasi tanpa deploy ulang.

**Keputusan**: Permission disimpan di MariaDB dalam 3 tabel (`pref_role`, `pref_permission`, `pref_role_permission`). Saat JWT Appwrite divalidasi di `JwtAuthFilter`, sistem membaca roles dari `prefs`, lalu load permission set dari DB berdasarkan roles tersebut, dan menginjeksikan keduanya (`ROLE_xxx` + `ENTITY:ACTION`) ke `GrantedAuthority` Spring. Di-load per request — tidak di-cache, karena Role jarang berubah dan latency DB acceptable.

## Considered Options

- **Permission di Appwrite prefs** — ditolak: Appwrite prefs free-form string, tidak ada referential integrity; susah di-query dan di-manage via API internal.
- **Hardcoded Map<Role, Set<Permission>>** — ditolak: butuh deploy ulang untuk setiap perubahan permission matrix.
- **Cache Caffeine per role** — ditangguhkan: premature optimization; bisa ditambah belakangan jika profiling menunjukkan DB query permission menjadi bottleneck.

## Consequences

- Dual mode enforcement selama transisi: `hasRole('ADMIN') or hasAuthority('MASTER:DELETE')` — controller lama tidak perlu dimigrasi sekaligus.
- DevUser di profile `development` mendapat semua permission hardcoded (tidak query DB), supaya developer bisa test semua endpoint tanpa setup permission seed.
- Permission tidak bisa di-assign langsung ke user — hanya via Role. Union semantics: user dengan multiple roles mendapat gabungan semua permission dari semua role-nya.

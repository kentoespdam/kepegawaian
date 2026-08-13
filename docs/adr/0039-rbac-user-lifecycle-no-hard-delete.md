# Appwrite User tidak di-hard-delete — lifecycle mengikuti Pegawai (auto-disable)

Review RBAC (grill session, 2026-08-13) menemukan CRUD user tidak lengkap: tidak ada `DELETE /system/users`, dan `PegawaiCommandService.deleteById` menghapus data pegawai **tanpa** menyentuh Appwrite user → menyisakan user yatim yang masih bisa login. Opsi perbaikan yang tampak natural adalah menambah `DELETE /system/users/{id}` (hard delete user Appwrite) dan memanggilnya saat pegawai dihapus.

**Keputusan**: Appwrite User **tidak pernah di-hard-delete**. Lifecycle-nya mengikuti Pegawai:

- Tidak ada endpoint `DELETE /system/users`. Mutasi user yang tersedia: `PATCH /system/users/{id}/status` (block/unblock, status `true` = blocked = login nonaktif) dan `PATCH /system/users/pref/{id}` (set roles).
- Saat pegawai **terminasi** (`RiwayatTerminasiCommandService.save` → `statusKerja = BERHENTI_OR_KELUAR`) atau **di-hard-delete** (`PegawaiCommandService.deleteById`), Appwrite user yang bersangkutan di-**disable** (blocked) secara **best-effort** (gagal di-log, tidak menggagalkan transaksi utama).
- Re-aktivasi dimungkinkan manual via `PATCH /system/users/{id}/status` (mis. pegawai yang terminasi diaktifkan kembali).

## Considered Options

- **Hard delete user + endpoint `DELETE /system/users/{id}`** — ditolak: (1) identitas login adalah jejak audit yang harus bertahan; (2) hard delete menghancurkan relasi audit/prefs yang masih dirujuk; (3) "menghapus user" bukan kebutuhan nyata — kebutuhan nyata adalah **mencabut akses login**, dan itu sudah tercakup oleh status blocked yang ada. Pegawai "keluar" direpresentasikan oleh terminasi (`statusKerja`), bukan penghapusan.
- **Hard delete + re-create saat pegawai dibuat ulang** — ditolak: id Appwrite user = `pegawai.id`; re-create dengan id sama tidak bisa (id sudah dipakai) → butuh id baru → relasi rusak.

## Consequences

- `AppwriteClient` **tidak** mendapat method `deleteUser` — yang dipakai adalah `updateStatus` yang sudah ada.
- `PegawaiCommandService.deleteById` dan `RiwayatTerminasiCommandService.save` dipanggil lintas modul (profil, kepegawaian, penggajian tidak terpengaruh karena perubahan ini best-effort dan non-transaksional terhadap Appwrite).
- User yang di-disable tetap muncul di `GET /system/users` dengan `status: true` — FE harus menampilkan status, bukan menyembunyikan user.
- Glossary `language-security.md` mendapat istilah **Appwrite User** dengan aturan lifecycle ini.

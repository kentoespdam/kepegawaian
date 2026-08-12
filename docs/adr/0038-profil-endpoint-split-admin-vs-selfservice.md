# Endpoint profil split: `/admin/profil/{id}` vs `/profil` untuk routing changedStatus

`ChangedStatusResolver.requiresApproval()` menentukan apakah perubahan profil masuk ke approval queue berdasarkan role (`ADMIN`/`HRD` → tidak perlu, pegawai → perlu). Masalah muncul ketika ADMIN/HRD mengedit profilnya **sendiri** via halaman self-service: server tidak tahu apakah request datang "sebagai admin" atau "sebagai pegawai".

**Keputusan**: Pisahkan endpoint berdasarkan konteks aksi, bukan berdasarkan siapa yang memanggil:

- `PATCH /admin/profil/{id}` — diproteksi `hasAuthority('PROFIL:APPROVE')`, **tidak pernah** trigger changedStatus (langsung stable). Dipakai HRD/ADMIN mengedit profil siapapun.
- `PATCH /profil` — self-service, **selalu** trigger changedStatus=true (masuk approval queue). Dipakai pegawai mengedit profil sendiri.

**Bukan** solusi `X-Acting-As` header atau flag `asAdmin` di body — keduanya memungkinkan user biasa bypass changedStatus dengan memalsukan header/body.

## Consequences

- `ChangedStatusResolver` bisa disederhanakan: `/admin/profil` handler tidak perlu panggil resolver sama sekali.
- FE harus routing ke endpoint yang tepat berdasarkan halaman (halaman admin vs halaman pegawai), bukan berdasarkan role user.
- Pelanggaran: pegawai biasa yang hit `/admin/profil` akan dapat 403 karena tidak punya `PROFIL:APPROVE`.

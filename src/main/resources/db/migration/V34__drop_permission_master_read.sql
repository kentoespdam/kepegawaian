-- MASTER:READ dihapus dari katalog: read-path master (jabatan, organisasi, golongan, dst.)
-- adalah data referensi — boleh dibaca siapa pun yang punya sesi aktif (login-only, WebSecurity
-- anyRequest().authenticated()), seperti /account/me. Guard write/delete (MASTER:WRITE/DELETE) tetap.
-- Urutan: hapus baris join dulu (FK pref_role_permission.perm_name -> pref_permission.name tanpa CASCADE).

DELETE FROM `pref_role_permission` WHERE `perm_name` = 'MASTER:READ';
DELETE FROM `pref_permission` WHERE `name` = 'MASTER:READ';

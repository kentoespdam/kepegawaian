-- CUTI:CREATE dihapus dari katalog: pengajuan/ubah/klaim/batal cuti adalah self-service —
-- semua pegawai berhak, kini di-enforce ownership server-side (principal) bukan permission
-- (ADR-0038 pattern, CutiOwnershipService). Guard write cuti dicabut dari controller.
-- Urutan: hapus baris join dulu (FK pref_role_permission.perm_name -> pref_permission.name tanpa CASCADE).

DELETE FROM `pref_role_permission` WHERE `perm_name` = 'CUTI:CREATE';
DELETE FROM `pref_permission` WHERE `name` = 'CUTI:CREATE';

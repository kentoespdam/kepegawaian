-- ADR-0039 (review lanjutan): modul laporan butuh permission baru + role USER perlu
-- permission read/self-service supaya guard dual-mode read-path tidak menyempitkan akses.
--
-- USER (pegawai biasa, role default user baru) mendapat:
--   semua *:READ + PROFIL:UPDATE (self-service profil) + CUTI:CREATE (pengajuan cuti sendiri)
-- Sesuai FE contract: "CUTI:CREATE tetap milik pegawai (USER)".

INSERT INTO pref_permission (name) VALUES ('LAPORAN:READ'), ('CUTI:WRITE'), ('PENGGAJIAN:DELETE');

-- USER: read-path + self-service
INSERT INTO pref_role_permission (role_id, perm_name)
SELECT 'USER', name FROM pref_permission
WHERE name IN (
  'MASTER:READ', 'PEGAWAI:READ', 'PROFIL:READ', 'PROFIL:UPDATE',
  'KEPEGAWAIAN:READ', 'CUTI:READ', 'CUTI:CREATE',
  'PENGGAJIAN:READ', 'LAPORAN:READ'
);

-- ADMIN = semua permission (23); HRD operasional minus SYSTEM:*, CUTI:CREATE, PENGGAJIAN:WRITE/PROCESS/DELETE
-- CUTI:WRITE (kelola jenis & kuota cuti) untuk ADMIN + HRD; PENGGAJIAN:DELETE hanya ADMIN
-- (konsisten dgn PENGGAJIAN:WRITE/PROCESS yang tidak diberikan ke HRD)
INSERT INTO pref_role_permission (role_id, perm_name)
SELECT 'ADMIN', name FROM pref_permission WHERE name IN ('LAPORAN:READ', 'CUTI:WRITE', 'PENGGAJIAN:DELETE');

INSERT INTO pref_role_permission (role_id, perm_name)
SELECT 'HRD', name FROM pref_permission WHERE name IN ('LAPORAN:READ', 'CUTI:WRITE');

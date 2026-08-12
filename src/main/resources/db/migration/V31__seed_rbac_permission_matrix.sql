-- Seed: permission catalogue (20) + permission matrix (ADR-0037)
-- ADMIN = semua 20; HRD = operasional penuh minus SYSTEM:* (15).
-- Matrix diubah runtime via API: POST/DELETE /system/roles/{roleId}/permissions/{permName}

INSERT INTO pref_permission (name) VALUES
('MASTER:READ'), ('MASTER:WRITE'), ('MASTER:DELETE'),
('PEGAWAI:READ'), ('PEGAWAI:WRITE'), ('PEGAWAI:DELETE'),
('KEPEGAWAIAN:READ'), ('KEPEGAWAIAN:WRITE'), ('KEPEGAWAIAN:DELETE'),
('PROFIL:READ'), ('PROFIL:UPDATE'), ('PROFIL:APPROVE'),
('CUTI:READ'), ('CUTI:CREATE'), ('CUTI:APPROVE'),
('PENGGAJIAN:READ'), ('PENGGAJIAN:WRITE'), ('PENGGAJIAN:PROCESS'),
('SYSTEM:MANAGE_USER'), ('SYSTEM:MANAGE_ROLE');

INSERT INTO pref_role_permission (role_id, perm_name)
SELECT 'ADMIN', name FROM pref_permission;

INSERT INTO pref_role_permission (role_id, perm_name)
SELECT 'HRD', name FROM pref_permission
WHERE name NOT IN ('SYSTEM:MANAGE_USER', 'SYSTEM:MANAGE_ROLE', 'CUTI:CREATE', 'PENGGAJIAN:WRITE', 'PENGGAJIAN:PROCESS');

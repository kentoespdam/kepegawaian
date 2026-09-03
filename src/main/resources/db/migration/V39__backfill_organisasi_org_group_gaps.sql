-- kepegawaian-m04g follow-up: tutup celah backfill `org_group` yang tidak terjangkau V37.
-- V37 memetakan nama → group dari `docs/organization.sql` (match by nama). Dua celah:
--   1) UPDATE — id 44: seed V3 menamai 'SUB BAG HUMAS & PROTOKOL', sedangkan legacy
--      `organization` org_id 44 bernama 'SUB BAG HUMAS' (group '07.BAGIAN KESEKRETARIATAN')
--      → tidak match di V37, org_group masih ''.
--   2) INSERT — 'SUB BAG SEKRETARIAT DIREKSI & PROTOKOL' ada di legacy (org_id 76, group
--      '07.BAGIAN KESEKRETARIATAN') tapi tidak ikut di-seed V3 → record hilang.
--      id TIDAK dipin (biarkan AUTO_INCREMENT): id 76 bisa saja sudah terpakai record lain
--      di DB yang sudah berjalan (mis. data runtime/tes).

-- UPDATE gap #1: isi org_group record yang terlewat match-by-nama di V37.
UPDATE `organisasi`
SET `org_group` = '07.BAGIAN KESEKRETARIATAN',
    `updated_at` = NOW(),
    `updated_by` = 'SYSTEM'
WHERE `nama` = 'SUB BAG HUMAS & PROTOKOL'
  AND `parent_id` = 15;

-- INSERT gap #2: seed record yang hilang dari V3 sesuai legacy org_id 76
-- (kode '1.2.2.4' = saudara lanjutan di bawah BAG. KESEKRETARIATAN id 15).
-- Idempotent: hanya insert kalau (nama, parent_id) belum ada.
INSERT INTO `organisasi`
    (kode, parent_id, level_org, nama, short_name, category, org_group, is_deleted, created_at, created_by, updated_at, updated_by)
SELECT '1.2.2.4', 15, 5, 'SUB BAG SEKRETARIAT DIREKSI & PROTOKOL', NULL, NULL,
       '07.BAGIAN KESEKRETARIATAN', 0, NOW(), 'SYSTEM', NOW(), 'SYSTEM'
WHERE NOT EXISTS (
    SELECT 1 FROM `organisasi`
    WHERE `nama` = 'SUB BAG SEKRETARIAT DIREKSI & PROTOKOL'
      AND `parent_id` = 15
);
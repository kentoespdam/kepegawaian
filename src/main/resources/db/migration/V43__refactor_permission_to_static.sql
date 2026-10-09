-- Refactor permission to static enum (Phase 1)
-- Drop foreign key constraint referencing pref_permission
ALTER TABLE `pref_role_permission` DROP FOREIGN KEY `fk_rp_pm_pm_name`;

-- Drop master table pref_permission
DROP TABLE IF EXISTS `pref_permission`;

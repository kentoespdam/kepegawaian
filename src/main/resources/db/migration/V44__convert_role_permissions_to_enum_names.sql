-- Convert permission names in pref_role_permission from colon format (e.g., 'CUTI:APPROVE')
-- to Java enum constant format (e.g., 'CUTI_APPROVE') to match AppPermission enum.
UPDATE `pref_role_permission`
SET `perm_name` = REPLACE(`perm_name`, ':', '_')
WHERE `perm_name` LIKE '%:%';

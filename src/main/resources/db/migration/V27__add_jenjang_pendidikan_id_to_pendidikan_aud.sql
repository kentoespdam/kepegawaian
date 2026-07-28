-- V27__add_jenjang_pendidikan_id_to_pendidikan_aud.sql
-- Fix: Unknown column 'jenjang_pendidikan_id' in 'field list' when querying pendidikan_aud
-- The Pendidikan entity maps the FK as "jenjang_pendidikan_id" (via @JoinColumn),
-- but the audit table (pendidikan_aud) was still using the old column name "jenjang_id".
-- Keeping both columns for backward compatibility with existing audit data.

ALTER TABLE `pendidikan_aud`
  ADD COLUMN `jenjang_pendidikan_id` bigint(20) DEFAULT NULL;

-- Backfill existing audit rows that have the old column populated
UPDATE `pendidikan_aud`
SET `jenjang_pendidikan_id` = `jenjang_id`
WHERE `jenjang_pendidikan_id` IS NULL AND `jenjang_id` IS NOT NULL;

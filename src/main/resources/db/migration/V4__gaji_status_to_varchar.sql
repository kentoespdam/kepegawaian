-- Migrate gaji_batch_root.status from INT (ordinal) to VARCHAR(32) (enum name)
-- Idempotent: only update rows that still hold integer ordinals; skip rows already holding enum names.

UPDATE gaji_batch_root
SET status = CASE status
    WHEN '0' THEN 'PENDING'
    WHEN '1' THEN 'PROSES'
    WHEN '2' THEN 'WAIT_VERIFICATION_PHASE_1'
    WHEN '3' THEN 'WAIT_VERIFICATION_PHASE_2'
    WHEN '4' THEN 'WAIT_APPROVAL'
    WHEN '5' THEN 'FINISHED'
    WHEN '6' THEN 'FAILED'
    ELSE status
END
WHERE status IN ('0','1','2','3','4','5','6');

ALTER TABLE gaji_batch_root MODIFY status VARCHAR(32) NOT NULL;

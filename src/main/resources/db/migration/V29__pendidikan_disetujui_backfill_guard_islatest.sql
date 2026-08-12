-- ADR-0035: expose + auto-set disetujui (kolom sudah ada sejak V1), backfill data lama,
-- dan guard satu-true per biodata untuk is_latest via generated column + UNIQUE.

-- 1) Normalisasi is_latest: sisakan satu baris true per biodata (id terbesar), agar
--    ALTER (guard) tidak gagal pada data duplikat yang terlanjur ada.
UPDATE pendidikan p
INNER JOIN (
    SELECT biodata_id, MAX(id) AS keep_id
    FROM pendidikan
    WHERE is_latest = 1 AND is_deleted = 0
    GROUP BY biodata_id
    HAVING COUNT(*) > 1
) dup ON dup.biodata_id = p.biodata_id AND p.id <> dup.keep_id
SET p.is_latest = 0
WHERE p.is_latest = 1 AND p.is_deleted = 0;

-- 2) Backfill disetujui untuk baris stabil (bukan antrian approval). Baris pending
--    (changed_status = 1) tetap disetujui = 0.
UPDATE pendidikan
SET disetujui = 1,
    tanggal_disetujui = COALESCE(created_at, updated_at),
    disetujui_oleh = created_by
WHERE is_deleted = 0 AND changed_status = 0;

-- 3) Guard: generated column mencatat biodata_id hanya untuk baris AKTIF yang is_latest=1.
--    MySQL/MariaDB mengizinkan banyak NULL → UNIQUE ini memaksa ≤ 1 baris true per biodata.
--    Baris soft-deleted (is_deleted=1) menghasilkan NULL sehingga tidak memblokir write baru.
ALTER TABLE pendidikan
    ADD COLUMN is_latest_biodata VARCHAR(255)
        GENERATED ALWAYS AS (IF(is_latest = 1 AND is_deleted = 0, biodata_id, NULL)) STORED,
    ADD UNIQUE KEY uk_ddk_islatest_biodata (is_latest_biodata);

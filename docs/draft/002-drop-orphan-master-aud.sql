-- ======================================================================
-- odb.2: Drop 12 orphan master _AUD tables (per ADR-0003)
-- ======================================================================
-- These _AUD tables were manually created in V5_1_0 migration under
-- "Master module Audit tables" but NO master entity has @Audited.
-- Envers does not write to them, making them orphaned.
--
-- Per ADR-0003: Envers hanya untuk modul penggajian + kepegawaian.
-- Master data cukup kolom audit + soft-delete.
--
-- Note: cuti_jenis_aud is EXCLUDED from this drop because CutiJenis
-- entity HAS @Audited — it was mislabeled in V5_1_0's "master" section.
--
-- This script will be applied in odb.6 when assembling the baseline.
-- ======================================================================

DROP TABLE IF EXISTS alasan_berhenti_aud;
DROP TABLE IF EXISTS golongan_aud;
DROP TABLE IF EXISTS grade_aud;
DROP TABLE IF EXISTS hari_libur_aud;
DROP TABLE IF EXISTS jabatan_aud;
DROP TABLE IF EXISTS jenis_kitas_aud;
DROP TABLE IF EXISTS jenis_sp_aud;
DROP TABLE IF EXISTS level_aud;
DROP TABLE IF EXISTS organisasi_aud;
DROP TABLE IF EXISTS profesi_aud;
DROP TABLE IF EXISTS rumah_dinas_aud;
DROP TABLE IF EXISTS sanksi_sp_aud;

-- V5_0_7__add_changed_status_approval_tables.sql
-- Bugfix: changed_status was declared on the shared @MappedSuperclass
-- IdsAbstract, so Hibernate selected it for every inheriting entity — but
-- the column only ever existed on profil_keluarga. SELECTs against the other
-- profil tables (e.g. lampiran_profil) failed with
-- "Unknown column '..._.changed_status' in 'field list'".
-- The field has been moved off IdsAbstract onto the 6 approval entities that
-- actually use it. profil_keluarga + profil_keluarga_AUD already have the
-- column (V5_0_1) and are left untouched. This adds it to the other 5
-- approval tables and their Envers _AUD siblings.

ALTER TABLE keahlian         ADD COLUMN changed_status BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE pelatihan        ADD COLUMN changed_status BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE pengalaman_kerja ADD COLUMN changed_status BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE kartu_identitas  ADD COLUMN changed_status BIT(1) NOT NULL DEFAULT 0;
ALTER TABLE pendidikan       ADD COLUMN changed_status BIT(1) NOT NULL DEFAULT 0;

ALTER TABLE keahlian_AUD         ADD COLUMN changed_status BIT(1) DEFAULT NULL;
ALTER TABLE pelatihan_AUD        ADD COLUMN changed_status BIT(1) DEFAULT NULL;
ALTER TABLE pengalaman_kerja_AUD ADD COLUMN changed_status BIT(1) DEFAULT NULL;
ALTER TABLE kartu_identitas_AUD  ADD COLUMN changed_status BIT(1) DEFAULT NULL;
ALTER TABLE pendidikan_AUD       ADD COLUMN changed_status BIT(1) DEFAULT NULL;

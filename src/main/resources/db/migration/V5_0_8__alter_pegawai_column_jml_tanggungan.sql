-- V5_0_8__alter_pegawai_column_jml_tanggungan.sql
-- Rename jmlTanggungan → jml_tanggungan in pegawai and pegawai_AUD tables
-- for consistency with all other snake_case column names.

ALTER TABLE pegawai     CHANGE COLUMN jmlTanggungan jml_tanggungan INT DEFAULT NULL;
ALTER TABLE pegawai_AUD CHANGE COLUMN jmlTanggungan jml_tanggungan INT DEFAULT NULL;

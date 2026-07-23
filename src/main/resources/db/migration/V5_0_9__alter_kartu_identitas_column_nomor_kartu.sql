-- V5_0_8__alter_pegawai_column_jml_tanggungan.sql
-- Rename jmlTanggungan → jml_tanggungan in pegawai and pegawai_AUD tables
-- for consistency with all other snake_case column names.

ALTER TABLE kartu_identitas     CHANGE COLUMN nomor nomor_kartu varchar(50) DEFAULT NULL;
ALTER TABLE kartu_identitas_AUD CHANGE COLUMN nomor nomor_kartu varchar(50) DEFAULT NULL;

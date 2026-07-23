-- V5_0_9__alter_kartu_identitas_column_nomor_kartu.sql
-- Rename nomor → nomor_kartu in kartu_identitas and kartu_identitas_AUD tables
-- for consistency with all other snake_case column names.

ALTER TABLE kartu_identitas     CHANGE COLUMN nomor nomor_kartu varchar(50) DEFAULT NULL;
ALTER TABLE kartu_identitas_AUD CHANGE COLUMN nomor nomor_kartu varchar(50) DEFAULT NULL;

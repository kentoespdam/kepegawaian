ALTER TABLE biodata
    ADD COLUMN changed_status tinyint(1) DEFAULT 0;

ALTER TABLE biodata_aud
    ADD COLUMN changed_status tinyint(1) DEFAULT 0;

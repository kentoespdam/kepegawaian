-- V5_0_0__create_revinfo.sql
-- Envers revision tracking table. Spring Data Envers (envers-core on classpath)
-- requires REVINFO and per-entity _AUD tables. Both column names + types
-- match Envers default mappings (REV BIGINT, REVTSTMP BIGINT).

CREATE TABLE revinfo (
    REV BIGINT NOT NULL AUTO_INCREMENT,
    REVTSTMP BIGINT DEFAULT NULL,
    PRIMARY KEY (REV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

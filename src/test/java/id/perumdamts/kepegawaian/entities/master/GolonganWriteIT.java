package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.repositories.master.jpa.GolonganRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for ADR-0010/0003 master non-Envers migration.
 * Asserts: real MariaDB writes (no mocks), audit 'system' fallback (irt/1),
 * @SQLDelete + @SQLRestriction behaviour, no golongan_aud table.
 *
 * Anti-masking: must fail if any of irt/1/2/3 are reverted.
 */
@SpringBootTest
@ActiveProfiles("development")
class GolonganWriteIT {
    @Autowired
    private GolonganRepository repository;
    @Autowired
    private JdbcTemplate jdbc;

    private Long createdId;

    @AfterEach
    void cleanup() {
        if (createdId != null) {
            jdbc.update("DELETE FROM golongan WHERE id = ?", createdId);
            createdId = null;
        }
    }

    @Test
    void saveWritesRealRowAndAuditFallsBackToSystem() {
        Golongan g = new Golongan();
        g.setGolongan("IT-GOL");
        g.setPangkat("IT-Pangkat");
        Golongan saved = repository.saveAndFlush(g);
        createdId = saved.getId();

        assertNotNull(createdId, "DB-assigned id must be non-null");
        assertEquals("system", saved.getCreatedBy(),
                "irt/1: no SecurityContext in dev profile -> 'system' fallback");
        assertNotNull(saved.getCreatedAt(),
                "AuditingEntityListener must populate created_at before insert");
    }

    @Test
    void noEnversAuditTableForMaster() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = DATABASE() AND table_name LIKE '%golongan_aud%'",
                Integer.class);
        assertEquals(0, count == null ? 0 : count,
                "irt/3: master Golongan must not have Envers _aud table");
    }

    @Test
    void softDeletePreservesRowButHidesFromFind() {
        Golongan g = new Golongan();
        g.setGolongan("IT-DEL");
        g.setPangkat("IT-Pangkat-Del");
        Golongan saved = repository.saveAndFlush(g);
        createdId = saved.getId();

        repository.deleteById(createdId);
        repository.flush();

        Integer rowCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM golongan WHERE id = ?",
                Integer.class, createdId);
        assertEquals(1, rowCount == null ? 0 : rowCount,
                "@SQLDelete sets is_deleted=TRUE; row must remain in DB");

        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM golongan WHERE id = ?",
                Integer.class, createdId);
        assertEquals(1, isDeleted == null ? 0 : isDeleted,
                "@SQLDelete SQL must set is_deleted=1");

        Optional<Golongan> found = repository.findById(createdId);
        assertFalse(found.isPresent(),
                "@SQLRestriction('is_deleted = FALSE') must hide soft-deleted rows");
    }

    @Test
    @Transactional
    void createdByEqualsSystemWithoutAnyMockPrincipal() {
        // No @Mock, no SecurityContext setup -> proves irt/1 fallback path.
        Golongan g = new Golongan();
        g.setGolongan("IT-AUDIT");
        g.setPangkat("IT-Pangkat-Audit");
        Golongan saved = repository.saveAndFlush(g);
        createdId = saved.getId();

        assertTrue(saved.getCreatedBy() != null && !saved.getCreatedBy().isBlank(),
                "created_by must be populated by AuditingEntityListener");
        assertEquals("system", saved.getCreatedBy(),
                "dev profile + no principal -> AuditAwareImpl returns 'system'");
    }
}

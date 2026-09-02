package id.perumdamts.kepegawaian.entities.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchRootRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test: JPA must persist {@link GajiBatchRoot#status} as an INTEGER
 * column (matching the real MariaDB schema {@code status int(11)}), and read it
 * back as an {@link EProsesGaji} enum via ordinal mapping.
 *
 * <p>Uses H2 embedded database with {@code MODE=MySQL} to avoid needing a live
 * MariaDB instance. The {@code @Enumerated(EnumType.ORDINAL)} fix is verified
 * end-to-end: Hibernate DDL → INSERT → SELECT → enum mapping.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GajiBatchRootStatusIT {

    @Autowired
    private GajiBatchRootRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @PersistenceContext
    private EntityManager em;

    private String createdId;

    @AfterEach
    void cleanup() {
        if (createdId != null) {
            jdbc.update("DELETE FROM gaji_batch_root WHERE id = ?", createdId);
            createdId = null;
        }
    }

    @Test
    void saveAndLoad_withEachStatus_writesIntegerToDatabase() {
        for (EProsesGaji expectedStatus : EProsesGaji.values()) {
            // Arrange
            String batchId = "IT-" + expectedStatus.name() + "-001";
            GajiBatchRoot entity = new GajiBatchRoot();
            entity.setId(batchId);
            entity.setPeriode("202609");
            entity.setStatus(expectedStatus);
            entity.setIsDeleted(false);

            // Act — JPA persist
            GajiBatchRoot saved = repository.saveAndFlush(entity);
            createdId = batchId;

            // Verify DB column type is INTEGER
            Integer dbStatus = jdbc.queryForObject(
                    "SELECT status FROM gaji_batch_root WHERE id = ?",
                    Integer.class, batchId);
            assertNotNull(dbStatus, "status column must be non-null in DB for " + expectedStatus.name());
            assertEquals(expectedStatus.ordinal(), dbStatus,
                    "DB integer must match enum ordinal for " + expectedStatus.name());

            // Act — JPA load (clear L2 cache to force re-read from DB)
            em.clear();
            Optional<GajiBatchRoot> loaded = repository.findById(batchId);

            // Verify JPA reads back the correct enum
            assertTrue(loaded.isPresent(), "Entity must be loadable after save");
            assertEquals(expectedStatus, loaded.get().getStatus(),
                    "Loaded status must match original enum for " + expectedStatus.name());

            // Cleanup for next iteration
            repository.deleteById(batchId);
            repository.flush();
            createdId = null;
        }
    }

    @Test
    void save_persistsStatusAsInteger_notVarchar() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("IT-TYPE-CHECK-001");
        entity.setPeriode("202609");
        entity.setStatus(EProsesGaji.PENDING);
        entity.setIsDeleted(false);
        createdId = entity.getId();

        repository.saveAndFlush(entity);

        // Query INFORMATION_SCHEMA to verify column type is numeric, not VARCHAR
        String columnType = jdbc.queryForObject(
                "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'GAJI_BATCH_ROOT' AND COLUMN_NAME = 'STATUS'",
                String.class);

        assertNotNull(columnType, "status column must exist in schema");
        // H2 may report INT or INTEGER; both are numeric. Must NOT be VARCHAR/CHAR.
        assertTrue(
                columnType.toUpperCase().contains("INT"),
                "status column must be a numeric INTEGER type, but was: " + columnType);
        assertFalse(
                columnType.toUpperCase().contains("CHAR") || columnType.toUpperCase().contains("CLOB"),
                "status column must NOT be a string type, but was: " + columnType);
    }

    @Test
    void save_nullStatus_persistsNullAndLoadsNull() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("IT-NULL-STATUS-001");
        entity.setPeriode("202609");
        entity.setStatus(null);
        entity.setIsDeleted(false);
        createdId = entity.getId();

        repository.saveAndFlush(entity);
        em.clear();

        Optional<GajiBatchRoot> loaded = repository.findById(entity.getId());
        assertTrue(loaded.isPresent());
        assertNull(loaded.get().getStatus(), "Null status must survive save/load cycle");
    }

    @Test
    void updateStatus_fromPendinToFinished_roundTripsCorrectly() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("IT-UPDATE-STATUS-001");
        entity.setPeriode("202609");
        entity.setStatus(EProsesGaji.PENDING);
        entity.setIsDeleted(false);
        createdId = entity.getId();

        // Save initial status
        repository.saveAndFlush(entity);

        // Update status through the workflow
        em.clear();
        GajiBatchRoot loaded = repository.findById(entity.getId()).orElseThrow();
        loaded.setStatus(EProsesGaji.WAIT_VERIFICATION_PHASE_1);
        repository.saveAndFlush(loaded);

        em.clear();
        GajiBatchRoot reloaded = repository.findById(entity.getId()).orElseThrow();
        assertEquals(EProsesGaji.WAIT_VERIFICATION_PHASE_1, reloaded.getStatus(),
                "Status update must persist correctly");

        // Update again
        reloaded.setStatus(EProsesGaji.FINISHED);
        repository.saveAndFlush(reloaded);

        em.clear();
        GajiBatchRoot finalLoad = repository.findById(entity.getId()).orElseThrow();
        assertEquals(EProsesGaji.FINISHED, finalLoad.getStatus(),
                "Final status must be FINISHED after workflow completion");

        // Verify DB value
        Integer dbStatus = jdbc.queryForObject(
                "SELECT status FROM gaji_batch_root WHERE id = ?",
                Integer.class, entity.getId());
        assertEquals(EProsesGaji.FINISHED.ordinal(), dbStatus,
                "DB must store FINISHED as integer ordinal");
    }

    @Test
    void delete_setsIsDeletedTrue_viaSQLDelete() {
        GajiBatchRoot entity = new GajiBatchRoot();
        entity.setId("IT-SOFTDEL-001");
        entity.setPeriode("202609");
        entity.setStatus(EProsesGaji.PENDING);
        entity.setIsDeleted(false);
        createdId = entity.getId();

        repository.saveAndFlush(entity);

        // Soft delete via @SQLDelete
        repository.deleteById(entity.getId());
        repository.flush();

        // Row still exists in DB with is_deleted=true
        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM gaji_batch_root WHERE id = ?",
                Integer.class, entity.getId());
        assertEquals(1, isDeleted, "is_deleted must be 1 after @SQLDelete");

        // But findById hides it via @SQLRestriction
        Optional<GajiBatchRoot> found = repository.findById(entity.getId());
        assertFalse(found.isPresent(),
                "@SQLRestriction must hide soft-deleted rows from findById");
    }
}

package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ProfesiCommandService delete guard (kepegawaian-15u).
 * <p>
 * Verifies:
 * - DELETE with active APD child → ConflictException (409)
 * - DELETE with active AlatKerja child → ConflictException (409)
 * - DELETE without children → success (soft-delete)
 */
@SpringBootTest
@ActiveProfiles("development")
class ProfesiCommandServiceTest {
    private static final String PREFIX = "IT-DG-";

    @Autowired
    private ProfesiCommandService service;
    @Autowired
    private JdbcTemplate jdbc;

    private final List<Long> apdIds = new ArrayList<>();
    private final List<Long> alatKerjaIds = new ArrayList<>();
    private final List<Long> profesiIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (Long id : apdIds) {
            jdbc.update("DELETE FROM apd WHERE id = ?", id);
        }
        for (Long id : alatKerjaIds) {
            jdbc.update("DELETE FROM alat_kerja WHERE id = ?", id);
        }
        for (Long id : profesiIds) {
            jdbc.update("DELETE FROM profesi WHERE id = ?", id);
        }
        apdIds.clear();
        alatKerjaIds.clear();
        profesiIds.clear();
    }

    private static String uniqueNama(String suffix) {
        return PREFIX + suffix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ProfesiPostRequest req(String nama) {
        ProfesiPostRequest r = new ProfesiPostRequest();
        r.setOrganisasiId(2L);
        r.setJabatanId(6L);
        r.setGradeId(8L);
        r.setNama(nama);
        r.setDetail("Detail " + nama);
        r.setResiko("Rendah");
        return r;
    }

    private Long createProfesi(String nama) {
        Profesi saved = service.create(req(nama));
        profesiIds.add(saved.getId());
        return saved.getId();
    }

    @Test
    void delete_withApdChild_throwsConflict() {
        Long profesiId = createProfesi(uniqueNama("apd"));
        jdbc.update("INSERT INTO apd (profesi_id, nama, is_deleted, created_at) VALUES (?, ?, 0, NOW())",
                profesiId, "APD-Test");
        Long apdId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        assertNotNull(apdId);
        apdIds.add(apdId);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.delete(profesiId));
        assertTrue(ex.getMessage().contains("APD"),
                "message should mention APD/Alat Kerja, got: " + ex.getMessage());
    }

    @Test
    void delete_withAlatKerjaChild_throwsConflict() {
        Long profesiId = createProfesi(uniqueNama("ak"));
        jdbc.update("INSERT INTO alat_kerja (profesi_id, nama, is_deleted, created_at) VALUES (?, ?, 0, NOW())",
                profesiId, "AK-Test");
        Long akId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        assertNotNull(akId);
        alatKerjaIds.add(akId);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.delete(profesiId));
        assertTrue(ex.getMessage().contains("Alat Kerja"),
                "message should mention APD/Alat Kerja, got: " + ex.getMessage());
    }

    @Test
    void delete_withoutChildren_succeeds() {
        Long profesiId = createProfesi(uniqueNama("no-child"));

        boolean result = service.delete(profesiId);
        assertTrue(result);

        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM profesi WHERE id = ?", Integer.class, profesiId);
        assertEquals(1, isDeleted, "profesi must be soft-deleted");
    }
}

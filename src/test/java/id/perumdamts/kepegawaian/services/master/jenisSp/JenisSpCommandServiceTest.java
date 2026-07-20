package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
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
 * Integration test for JenisSpCommandService delete guard (kepegawaian-15u).
 * <p>
 * Verifies:
 * - DELETE with active Sanksi child → ConflictException (409)
 * - DELETE without children → success (soft-delete)
 */
@SpringBootTest
@ActiveProfiles("development")
class JenisSpCommandServiceTest {
    private static final String PREFIX = "IT-DG-";

    @Autowired
    private JenisSpCommandService service;
    @Autowired
    private JdbcTemplate jdbc;

    private final List<Long> sanksiIds = new ArrayList<>();
    private final List<Long> jenisSpIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (Long id : sanksiIds) {
            jdbc.update("DELETE FROM sanksi_sp WHERE id = ?", id);
        }
        for (Long id : jenisSpIds) {
            jdbc.update("DELETE FROM jenis_sp WHERE id = ?", id);
        }
        sanksiIds.clear();
        jenisSpIds.clear();
    }

    private static String uniqueKode() {
        // Max length is 10 chars per schema (VARCHAR(10)).
        return PREFIX + UUID.randomUUID().toString().substring(0, 4);
    }

    private static String uniqueNama(String suffix) {
        return PREFIX + suffix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private JenisSpPostRequest req(String kode, String nama) {
        JenisSpPostRequest r = new JenisSpPostRequest();
        r.setKode(kode);
        r.setNama(nama);
        return r;
    }

    private Long createJenisSp(String kode, String nama) {
        JenisSp saved = service.create(req(kode, nama));
        jenisSpIds.add(saved.getId());
        return saved.getId();
    }

    @Test
    void delete_withSanksiChild_throwsConflict() {
        String kode = uniqueKode();
        Long jenisSpId = createJenisSp(kode, uniqueNama("parent"));

        jdbc.update("INSERT INTO sanksi_sp (kode, keterangan, jenis_sp_id, is_deleted, created_at) VALUES (?, ?, ?, 0, NOW())",
                uniqueKode(), "Test sanksi", jenisSpId);
        Long sanksiId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        assertNotNull(sanksiId);
        sanksiIds.add(sanksiId);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.delete(jenisSpId));
        assertTrue(ex.getMessage().contains("sanksi"),
                "message should mention sanksi, got: " + ex.getMessage());
    }

    @Test
    void delete_withoutChildren_succeeds() {
        String kode = uniqueKode();
        Long id = createJenisSp(kode, uniqueNama("no-child"));

        boolean result = service.delete(id);
        assertTrue(result);

        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM jenis_sp WHERE id = ?", Integer.class, id);
        assertEquals(1, isDeleted, "jenis_sp must be soft-deleted");
    }
}

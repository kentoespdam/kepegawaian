package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
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
 * Integration test for JabatanCommandService delete guard (kepegawaian-15u).
 * <p>
 * Verifies:
 * - DELETE with active child (sub-jabatan) → ConflictException (409)
 * - DELETE without children → success (soft-delete)
 */
@SpringBootTest
@ActiveProfiles("development")
class JabatanCommandServiceTest {
    private static final String PREFIX = "IT-DG-";

    @Autowired
    private JabatanCommandService service;
    @Autowired
    private JdbcTemplate jdbc;

    private final List<Long> childJabatanIds = new ArrayList<>();
    private final List<Long> jabatanIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (Long id : childJabatanIds) {
            jdbc.update("DELETE FROM jabatan WHERE id = ?", id);
        }
        for (Long id : jabatanIds) {
            jdbc.update("DELETE FROM jabatan WHERE id = ?", id);
        }
        childJabatanIds.clear();
        jabatanIds.clear();
    }

    private static String uniqueKode() {
        return PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String uniqueNama(String suffix) {
        return PREFIX + suffix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private JabatanPostRequest req(String kode, String nama, Long parentId) {
        JabatanPostRequest r = new JabatanPostRequest();
        r.setKode(kode);
        r.setNama(nama);
        r.setParentId(parentId);
        r.setOrganisasiId(2L);
        r.setLevelId(7L);
        return r;
    }

    private Long createJabatan(String kode, String nama) {
        return createJabatan(kode, nama, null);
    }

    private Long createJabatan(String kode, String nama, Long parentId) {
        Jabatan saved = service.create(req(kode, nama, parentId));
        jabatanIds.add(saved.getId());
        return saved.getId();
    }

    @Test
    void delete_withChildSubJabatan_throwsConflict() {
        String kodeParent = uniqueKode();
        String namaParent = uniqueNama("parent");
        Long parentId = createJabatan(kodeParent, namaParent);

        String kodeChild = uniqueKode();
        String namaChild = uniqueNama("child");
        JabatanPostRequest childReq = req(kodeChild, namaChild, parentId);
        Jabatan child = service.create(childReq);
        childJabatanIds.add(child.getId());

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.delete(parentId));
        assertTrue(ex.getMessage().contains("sub-jabatan"),
                "message should mention sub-jabatan, got: " + ex.getMessage());
    }

    @Test
    void delete_withoutChildren_succeeds() {
        String kode = uniqueKode();
        String nama = uniqueNama("no-child");
        Long id = createJabatan(kode, nama);

        boolean result = service.delete(id);
        assertTrue(result);

        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM jabatan WHERE id = ?", Integer.class, id);
        assertEquals(1, isDeleted, "jabatan must be soft-deleted");
    }
}

package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for OrganisasiCommandService — locks revive/conflict/update
 * behaviour before {@code kepegawaian-33s} touches the revive seam.
 *
 * Anti-masking: must run against a real MariaDB so {@code @SQLRestriction}
 * (from {@code MasterBaseEntity}) is exercised — a mocked repository would
 * hide the live bug. Kode prefix {@code IT-9TF-} keeps rows isolated from
 * V3_0_1 seed (75 rows) and the 15 soft-deleted carcasses.
 */
@SpringBootTest
@ActiveProfiles("development")
class OrganisasiCommandServiceTest {
    private static final String KODE_PREFIX = "IT-9TF-";

    @Autowired
    private OrganisasiCommandService service;
    @Autowired
    private JdbcTemplate jdbc;

    private final List<Long> createdIds = new java.util.ArrayList<>();

    @AfterEach
    void cleanup() {
        // Hard-delete any test rows we created (bypassing @SQLDelete to be thorough).
        for (Long id : createdIds) {
            jdbc.update("DELETE FROM organisasi WHERE id = ?", id);
        }
        createdIds.clear();
    }

    private static String uniqueKode() {
        return KODE_PREFIX + UUID.randomUUID().toString().substring(0, 8);
    }

    private OrganisasiPostRequest req(String kode, String nama) {
        OrganisasiPostRequest r = new OrganisasiPostRequest();
        r.setKode(kode);
        r.setLevelOrganisasi(99);
        r.setNama(nama);
        r.setShortName("IT");
        r.setCategory("TEST");
        return r;
    }

    private Long createAndRemember(OrganisasiPostRequest r) {
        Organisasi saved = service.create(r);
        createdIds.add(saved.getId());
        return saved.getId();
    }

    /**
     * (a) create baru → entity tersimpan, isDeleted=false.
     */
    @Test
    void create_persistsNewRow_withIsDeletedFalse() {
        String kode = uniqueKode();
        OrganisasiPostRequest r = req(kode, "IT-9TF-a");

        Organisasi saved = service.create(r);
        createdIds.add(saved.getId());

        assertNotNull(saved.getId(), "id must be assigned");
        assertEquals(kode, saved.getKode());
        assertEquals("IT-9TF-a", saved.getNama());
        assertFalse(Boolean.TRUE.equals(saved.getIsDeleted()),
                "fresh create must have isDeleted=false");
    }

    /**
     * (b) create saat ada record AKTIF cocok-spec → ConflictException.
     * Spec = (kode, parent.id, levelOrg, nama). A second create with the same
     * tuple must conflict against the active row.
     */
    @Test
    void create_overActiveRecord_throwsConflict() {
        String kode = uniqueKode();
        OrganisasiPostRequest first = req(kode, "IT-9TF-b");
        createAndRemember(first);

        // Same spec → same unique tuple → active record present → Conflict.
        OrganisasiPostRequest dup = req(kode, "IT-9TF-b");

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.create(dup));
        assertNotNull(ex.getMessage());
    }

    /**
     * (c) create saat ada record TERHAPUS (carcass) cocok-spec → REVIVE.
     *
     * KNOWN BUG (terverifikasi 2026-06-18): @SQLRestriction("is_deleted = FALSE")
     * on MasterBaseEntity means repository.findOne(spec) NEVER sees the carcass.
     * The revive branch in OrganisasiCommandService.create() (line 25) is dead
     * code; the second create will fall through to insert and either:
     *  - succeed and produce a duplicate, or
     *  - fail with a DB unique-constraint violation (DataIntegrityViolation).
     *
     * This test asserts the DESIRED behaviour (idempotent revive). It is
     * annotated {@code @Disabled} because the desired behaviour requires
     * kepegawaian-33s to add a native carcass-finder. Enable when #33s lands.
     *
     * (c) in the claim-order checklist instructs: "tulis (c) meng-assert
     * perilaku DIINGINKAN, tandai @Disabled".
     */
    @Test
    @org.junit.jupiter.api.Disabled("blocked by kepegawaian-33s — revive seam not yet fixed")
    void create_overCarcass_revivesSameId() {
        String kode = uniqueKode();
        String namaAwal = "IT-9TF-c-initial";
        String namaRevive = "IT-9TF-c-revived";

        // Step 1: create a row.
        Organisasi created = service.create(req(kode, namaAwal));
        createdIds.add(created.getId());
        Long originalId = created.getId();

        // Step 2: soft-delete it (creates the carcass).
        service.delete(originalId);

        // Verify it's actually a carcass in the DB.
        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM organisasi WHERE id = ?", Integer.class, originalId);
        assertEquals(1, isDeleted, "row must be soft-deleted in DB");

        // Step 3: create with the same spec. DESIRED: revive the carcass.
        Organisasi revived = service.create(req(kode, namaRevive));

        assertEquals(originalId, revived.getId(), "revive must reuse carcass id");
        assertEquals(namaRevive, revived.getNama(), "field must be updated");
        assertFalse(Boolean.TRUE.equals(revived.getIsDeleted()),
                "revived row must have isDeleted=false");
    }

    /**
     * (d) update ke nilai yang bentrok dengan record LAIN → ConflictException.
     */
    @Test
    void update_intoAnotherActiveRecord_throwsConflict() {
        String kodeA = uniqueKode();
        String kodeB = uniqueKode();
        Long idA = createAndRemember(req(kodeA, "IT-9TF-d-A"));

        // Existing target B is active with its own spec.
        Long idB = createAndRemember(req(kodeB, "IT-9TF-d-B"));

        // Try to update A into B's spec.
        OrganisasiPostRequest collide = req(kodeB, "IT-9TF-d-B");

        ConflictException ex = assertThrows(ConflictException.class,
                () -> service.update(idA, collide));
        assertNotNull(ex.getMessage());
        // idA and idB untouched.
        assertNotEquals(idA, idB);
    }

    /**
     * (e) update ke diri sendiri (duplicate.id == id) → sukses, tidak Conflict.
     * Updating row A with its own spec must not be flagged as duplicate.
     */
    @Test
    void update_intoOwnSpec_succeeds() {
        String kode = uniqueKode();
        Long id = createAndRemember(req(kode, "IT-9TF-e"));

        // Same spec → duplicate exists, but its id == this id → allowed.
        OrganisasiPostRequest same = req(kode, "IT-9TF-e");
        Organisasi updated = service.update(id, same);

        assertEquals(id, updated.getId());
        assertEquals(kode, updated.getKode());
        assertEquals("IT-9TF-e", updated.getNama());
    }

    /**
     * (f) update id tidak ada → NotFoundException.
     */
    @Test
    void update_missingId_throwsNotFound() {
        // Pick an id that almost certainly doesn't exist.
        Long missing = 9_999_999_999L;
        // Sanity-check via DB (cheap): ensure no row with that id.
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM organisasi WHERE id = ?", Integer.class, missing);
        assertNotNull(count);
        assertEquals(0, count, "test fixture assumes id " + missing + " is unused");

        OrganisasiPostRequest r = req(uniqueKode(), "IT-9TF-f");

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.update(missing, r));
        assertNotNull(ex.getMessage());
    }

    /**
     * (g) delete → isDeleted=true (bukan hard delete).
     */
    @Test
    void delete_softDeletesRow() {
        String kode = uniqueKode();
        Long id = createAndRemember(req(kode, "IT-9TF-g"));

        service.delete(id);

        Integer isDeleted = jdbc.queryForObject(
                "SELECT is_deleted FROM organisasi WHERE id = ?", Integer.class, id);
        assertEquals(1, isDeleted, "delete must soft-delete (is_deleted=true), not hard-delete");

        // Row must still be present in the table.
        Integer rowCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM organisasi WHERE id = ?", Integer.class, id);
        assertEquals(1, rowCount, "row must still exist (soft delete, not hard delete)");
    }
}

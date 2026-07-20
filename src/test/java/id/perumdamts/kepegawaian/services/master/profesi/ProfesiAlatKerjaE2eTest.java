package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaCommandService;
import id.perumdamts.kepegawaian.services.master.apd.ApdCommandService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E regression test for the split-brain schema fix (kepegawaian-ag3).
 * <p>
 * Verifies that after creating AlatKerja/APD under a Profesi (nested write),
 * the Profesi detail endpoint returns them in alatKerjaList/apdList.
 * <p>
 * This exercises the FULL CQRS path:
 * <ol>
 *   <li><b>Write side</b> (JPA): ProfesiCommandService + AlatKerjaCommandService</li>
 *   <li><b>Read side</b> (JOOQ MULTISET): ProfesiQueryService / ProfesiDetailQuery</li>
 *   <li><b>Schema alignment</b>: both read and write target the same DB schema</li>
 * </ol>
 */
@SpringBootTest
@ActiveProfiles("development")
class ProfesiAlatKerjaE2eTest {

    private static final String PREFIX = "IT-E2E-";

    @Autowired
    private ProfesiCommandService profesiService;
    @Autowired
    private ProfesiQueryService profesiQuery;
    @Autowired
    private AlatKerjaCommandService alatKerjaService;
    @Autowired
    private ApdCommandService apdService;
    @Autowired
    private JdbcTemplate jdbc;

    private Long profesiId;

    @AfterEach
    void cleanup() {
        if (profesiId != null) {
            jdbc.update("DELETE FROM alat_kerja WHERE profesi_id = ?", profesiId);
            jdbc.update("DELETE FROM apd WHERE profesi_id = ?", profesiId);
            jdbc.update("DELETE FROM profesi WHERE id = ?", profesiId);
        }
    }

    private static String uniqueName(String suffix) {
        return PREFIX + suffix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ProfesiPostRequest profesiReq(String nama) {
        ProfesiPostRequest r = new ProfesiPostRequest();
        r.setOrganisasiId(2L);
        r.setJabatanId(6L);
        r.setGradeId(8L);
        r.setNama(nama);
        r.setDetail("E2E Detail " + nama);
        r.setResiko("E2E Resiko");
        return r;
    }

    @Test
    void alatKerjaCreatedUnderProfesiAppearsInDetail() {
        String profesiName = uniqueName("PROF");
        Profesi saved = profesiService.create(profesiReq(profesiName));
        profesiId = saved.getId();

        String akName = uniqueName("AK");
        alatKerjaService.create(profesiId, new AlatKerjaPostRequest(akName));

        ProfesiDetail detail = profesiQuery.getById(profesiId);
        assertNotNull(detail, "ProfesiDetail must not be null");
        assertNotNull(detail.alatKerjaList(), "alatKerjaList must not be null");
        assertFalse(detail.alatKerjaList().isEmpty(),
                "alatKerjaList must contain the newly created AlatKerja");
        assertTrue(detail.alatKerjaList().stream().anyMatch(ak -> ak.nama().equals(akName)),
                "alatKerjaList must contain AlatKerja with nama='" + akName + "'");
    }

    @Test
    void apdCreatedUnderProfesiAppearsInDetail() {
        String profesiName = uniqueName("PROF-APD");
        Profesi saved = profesiService.create(profesiReq(profesiName));
        profesiId = saved.getId();

        String apdName = uniqueName("APD");
        apdService.create(profesiId, new ApdPostRequest(apdName));

        ProfesiDetail detail = profesiQuery.getById(profesiId);
        assertNotNull(detail, "ProfesiDetail must not be null");
        assertNotNull(detail.apdList(), "apdList must not be null");
        assertFalse(detail.apdList().isEmpty(),
                "apdList must contain the newly created APD");
        assertTrue(detail.apdList().stream().anyMatch(a -> a.nama().equals(apdName)),
                "apdList must contain APD with nama='" + apdName + "'");
    }

    @Test
    void multipleChildrenAllReturnedInDetail() {
        String profesiName = uniqueName("PROF-MULTI");
        Profesi saved = profesiService.create(profesiReq(profesiName));
        profesiId = saved.getId();

        alatKerjaService.create(profesiId, new AlatKerjaPostRequest(uniqueName("AK1")));
        alatKerjaService.create(profesiId, new AlatKerjaPostRequest(uniqueName("AK2")));
        apdService.create(profesiId, new ApdPostRequest(uniqueName("APD1")));
        apdService.create(profesiId, new ApdPostRequest(uniqueName("APD2")));

        ProfesiDetail detail = profesiQuery.getById(profesiId);
        assertNotNull(detail, "ProfesiDetail must not be null");
        assertEquals(2, detail.alatKerjaList().size(),
                "alatKerjaList must contain 2 items");
        assertEquals(2, detail.apdList().size(),
                "apdList must contain 2 items");
    }
}

package id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test: GajiBatchRootMapper.toEntityPhase1 must set status to PENDING
 * as an ordinal-compatible EProsesGaji enum (not a String).
 */
class GajiBatchRootMapperTest {

    @Test
    void toEntityPhase1_setsStatusToPENDING() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");
        request.setDiProsesOleh("Operator");
        request.setJabatanPemroses("Kabag");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);

        assertNotNull(entity.getStatus(), "status must not be null");
        assertEquals(EProsesGaji.PENDING, entity.getStatus(),
                "Initial status must be PENDING");
    }

    @Test
    void toEntityPhase1_setsIdFromPeriode() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);

        assertEquals("202609-001", entity.getId(),
                "ID must be periode + '-001'");
    }

    @Test
    void toEntityPhase1_setsPeriodeFromTahunBulan() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);

        assertEquals("202609", entity.getPeriode(),
                "periode must be tahun + bulan");
    }

    @Test
    void toEntityPhase1_setsProcessorFields() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");
        request.setDiProsesOleh("Budi");
        request.setJabatanPemroses("Kabag Keuangan");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);

        assertEquals("Budi", entity.getDiProsesOleh());
        assertEquals("Kabag Keuangan", entity.getJabatanPemroses());
    }

    @Test
    void toEntityPhase1_statusIsEnumType_notString() {
        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        GajiBatchRoot entity = GajiBatchRootMapper.toEntityPhase1(request);

        assertInstanceOf(EProsesGaji.class, entity.getStatus(),
                "Status must be an EProsesGaji enum instance, not a String");
    }
}

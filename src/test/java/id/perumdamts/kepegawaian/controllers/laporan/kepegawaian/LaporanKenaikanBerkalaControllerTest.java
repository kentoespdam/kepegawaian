package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EJenisKenaikanBerkala;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.KenaikanBerkalaService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanKenaikanBerkalaControllerTest {

    private final KenaikanBerkalaService service = mock(KenaikanBerkalaService.class);
    private final LaporanKenaikanBerkalaController controller = new LaporanKenaikanBerkalaController(service);

    @Test
    void lapKenaikanBerkalaExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{17, 18, 19};
        when(service.exportExcel(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapKenaikanBerkalaExcel(
                EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapKenaikanBerkalaExcel_setsContentDispositionAttachment() {
        when(service.exportExcel(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapKenaikanBerkalaExcel(
                EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_kenaikan_berkala.xlsx"),
                "Filename must be 'lap_kenaikan_berkala.xlsx'");
    }

    @Test
    void lapKenaikanBerkalaExcel_setsContentType() {
        when(service.exportExcel(EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA))
                .thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapKenaikanBerkalaExcel(
                EFilterKenaikanBerkala.BULAN_INI, EJenisKenaikanBerkala.SK_KENAIKAN_GAJI_BERKALA);

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

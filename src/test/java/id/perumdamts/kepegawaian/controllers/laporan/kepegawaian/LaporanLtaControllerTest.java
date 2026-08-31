package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LtaService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanLtaControllerTest {

    private final LtaService service = mock(LtaService.class);
    private final LaporanLtaController controller = new LaporanLtaController(service);

    @Test
    void lapLtaExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{4, 5, 6};
        when(service.exportExcel(EFilterLta.BULAN_INI)).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapLtaExcel(EFilterLta.BULAN_INI);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapLtaExcel_setsContentDispositionAttachment() {
        when(service.exportExcel(EFilterLta.BULAN_INI)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapLtaExcel(EFilterLta.BULAN_INI);

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_lta.xlsx"), "Filename must be 'lap_lta.xlsx'");
    }

    @Test
    void lapLtaExcel_setsContentType() {
        when(service.exportExcel(EFilterLta.BULAN_INI)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapLtaExcel(EFilterLta.BULAN_INI);

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

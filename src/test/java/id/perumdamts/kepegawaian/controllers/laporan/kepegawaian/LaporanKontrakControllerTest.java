package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.KontrakService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanKontrakControllerTest {

    private final KontrakService service = mock(KontrakService.class);
    private final LaporanKontrakController controller = new LaporanKontrakController(service);

    @Test
    void lapKontrakExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{7, 8, 9};
        when(service.exportExcel(EFilterKontrak.AKTIF)).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapKontrakExcel(EFilterKontrak.AKTIF);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapKontrakExcel_setsContentDispositionAttachment() {
        when(service.exportExcel(EFilterKontrak.AKTIF)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapKontrakExcel(EFilterKontrak.AKTIF);

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_kontrak.xlsx"), "Filename must be 'lap_kontrak.xlsx'");
    }

    @Test
    void lapKontrakExcel_setsContentType() {
        when(service.exportExcel(EFilterKontrak.AKTIF)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapKontrakExcel(EFilterKontrak.AKTIF);

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

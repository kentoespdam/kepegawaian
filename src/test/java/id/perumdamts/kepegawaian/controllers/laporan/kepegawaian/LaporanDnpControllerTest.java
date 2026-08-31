package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.services.laporan.kepegawaian.DnpService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanDnpControllerTest {

    private final DnpService service = mock(DnpService.class);
    private final LaporanDnpController controller = new LaporanDnpController(service);

    @Test
    void lapDnpExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{1, 2, 3, 4, 5};
        when(service.exportExcel()).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapDnpExcel();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapDnpExcel_setsContentDispositionAttachment() {
        when(service.exportExcel()).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapDnpExcel();

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_dnp.xlsx"), "Filename must be 'lap_dnp.xlsx'");
    }

    @Test
    void lapDnpExcel_setsContentType() {
        when(service.exportExcel()).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapDnpExcel();

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.services.laporan.kepegawaian.DukService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanDukControllerTest {

    private final DukService service = mock(DukService.class);
    private final LaporanDukController controller = new LaporanDukController(service);

    @Test
    void lapDukExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{10, 20, 30};
        when(service.exportExcel()).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapDukExcel();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapDukExcel_setsContentDispositionAttachment() {
        when(service.exportExcel()).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapDukExcel();

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_duk.xlsx"), "Filename must be 'lap_duk.xlsx'");
    }

    @Test
    void lapDukExcel_setsContentType() {
        when(service.exportExcel()).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapDukExcel();

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

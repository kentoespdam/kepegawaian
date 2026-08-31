package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.services.laporan.kepegawaian.MutasiService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanMutasiControllerTest {

    private final MutasiService service = mock(MutasiService.class);
    private final LaporanMutasiController controller = new LaporanMutasiController(service);

    @Test
    void lapMutasiExcel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{11, 12, 13};
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        when(service.exportExcel(from, to, null)).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapMutasiExcel(from, to, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapMutasiExcel_setsContentDispositionAttachment() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        when(service.exportExcel(from, to, null)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapMutasiExcel(from, to, null);

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_mutasi.xlsx"), "Filename must be 'lap_mutasi.xlsx'");
    }

    @Test
    void lapMutasiExcel_setsContentType() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        when(service.exportExcel(from, to, null)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapMutasiExcel(from, to, null);

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

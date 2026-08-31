package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.services.laporan.kepegawaian.StatistikService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LaporanStatistikControllerTest {

    private final StatistikService service = mock(StatistikService.class);
    private final LaporanStatistikController controller = new LaporanStatistikController(service);

    @Test
    void lapStatistikPendidikan2Excel_returnsOkWithOctetStreamContentType() {
        byte[] fakeExcel = new byte[]{14, 15, 16};
        when(service.exportExcelPendidikan2(2024, 1)).thenReturn(new ByteArrayResource(fakeExcel));

        ResponseEntity<?> response = controller.lapStatistikPendidikan2Excel(2024, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fakeExcel, ((ByteArrayResource) response.getBody()).getByteArray());
    }

    @Test
    void lapStatistikPendidikan2Excel_setsContentDispositionAttachment() {
        when(service.exportExcelPendidikan2(2024, 1)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapStatistikPendidikan2Excel(2024, 1);

        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition, "Content-Disposition header must be set");
        assertTrue(disposition.contains("attachment"), "Content-Disposition must be 'attachment'");
        assertTrue(disposition.contains("lap_statistik_pendidikan.xlsx"),
                "Filename must be 'lap_statistik_pendidikan.xlsx'");
    }

    @Test
    void lapStatistikPendidikan2Excel_setsContentType() {
        when(service.exportExcelPendidikan2(2024, 1)).thenReturn(new ByteArrayResource(new byte[0]));

        ResponseEntity<?> response = controller.lapStatistikPendidikan2Excel(2024, 1);

        MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType, "Content-Type header must be set");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, contentType,
                "Content-Type must be application/octet-stream");
    }
}

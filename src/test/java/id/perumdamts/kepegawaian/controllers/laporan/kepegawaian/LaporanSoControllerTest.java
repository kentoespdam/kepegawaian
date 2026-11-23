package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.SoService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class LaporanSoControllerTest {

    private final SoService service = mock(SoService.class);
    private final LaporanSoController controller = new LaporanSoController(service);

    @Test
    void lapSoFlatReturnsListResult() {
        var item = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(service.fetchFlat(6)).thenReturn(List.of(item));

        ResponseEntity<ListResult<SoResponse>> response = controller.lapSo(6, "flat");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(service).fetchFlat(6);
        verify(service, never()).fetchTreeList(any());
    }

    @Test
    void lapSoTreeReturnsListResult() {
        var item = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(service.fetchTreeList(4)).thenReturn(List.of(item));

        ResponseEntity<ListResult<SoResponse>> response = controller.lapSo(4, "tree");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        verify(service).fetchTreeList(4);
        verify(service, never()).fetchFlat(any());
    }
}

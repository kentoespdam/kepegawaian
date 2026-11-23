package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.SoService;
import java.util.List;
import java.util.Map;
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

        ResponseEntity<?> response = controller.lapSo(6, "flat");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ListResult<?>);
        verify(service).fetchFlat(6);
        verify(service, never()).fetchTree(any());
    }

    @Test
    void lapSoTreeReturnsSingleResult() {
        var item = SoResponse.flat(1L, null, "DIR", "Direktur", 1, "Andi", "8903001", "Direksi", null, false);
        when(service.fetchTree(4)).thenReturn(Map.of("hierarchy", item));

        ResponseEntity<?> response = controller.lapSo(4, "tree");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof SingleResult<?>);
        verify(service).fetchTree(4);
        verify(service, never()).fetchFlat(any());
    }
}

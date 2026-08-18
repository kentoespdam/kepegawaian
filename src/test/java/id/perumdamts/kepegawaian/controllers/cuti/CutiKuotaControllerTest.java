package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPegawaiResponse;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaRequest;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaCommandService;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test {@link CutiKuotaController#index} — kontrak response index
 * (ADR-0040): envelope {@link PageResult} (bukan SingleResult), selalu 200,
 * page kosong → empty page ber-metadata, TIDAK pernah 404 "Data not found!".
 */
class CutiKuotaControllerTest {

    private final CutiKuotaQueryService queryService = mock(CutiKuotaQueryService.class);
    private final CutiKuotaCommandService commandService = mock(CutiKuotaCommandService.class);
    private final CutiKuotaController controller = new CutiKuotaController(queryService, commandService);

    @Test
    void index_wrapsInPageResult_andReturns200WithEmptyPage() {
        when(queryService.findIndex(any(CutiKuotaRequest.class)))
                .thenReturn(new CutiKuotaPegawaiResponse(Page.empty(), List.of()));

        ResponseEntity<PageResult<CutiKuotaPegawaiResponse>> response =
                controller.index(new CutiKuotaRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PageResult<CutiKuotaPegawaiResponse> body = response.getBody();
        assertNotNull(body, "envelope harus PageResult, bukan SingleResult");
        assertEquals(HttpStatus.OK, body.getStatusText());
        assertNotNull(body.getData(), "data tidak boleh null saat service mengembalikan response utuh");
        assertNotNull(body.getData().page());
        assertTrue(body.getData().page().getContent().isEmpty());
        assertTrue(body.getData().kuotaTahunSebelumnya().isEmpty());
    }

    @Test
    void index_neverReturns404_whenServiceReturnsNull() {
        when(queryService.findIndex(any(CutiKuotaRequest.class))).thenReturn(null);

        ResponseEntity<PageResult<CutiKuotaPegawaiResponse>> response =
                controller.index(new CutiKuotaRequest());

        // Regression: dulu CustomResult.any(null) → SingleResult 404 "Data not found!"
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PageResult<CutiKuotaPegawaiResponse> body = response.getBody();
        assertNotNull(body, "envelope harus PageResult, bukan SingleResult");
        assertEquals(HttpStatus.OK, body.getStatusText());
        assertNull(body.getData());
    }
}

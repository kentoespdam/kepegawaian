package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisListRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisMiniResponse;
import id.perumdamts.kepegawaian.services.cuti.jenis.CutiJenisCommandService;
import id.perumdamts.kepegawaian.services.cuti.jenis.CutiJenisQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression (2026-08-18): GET /cuti/jenis/list mengembalikan envelope
 * {@link ListResult} berisi {@link CutiJenisMiniResponse} {id,nama,parentId}
 * langsung — bukan {@code CutiJenisResponse} + parent nested.
 */
class CutiJenisControllerTest {

    private final CutiJenisQueryService queryService = mock(CutiJenisQueryService.class);
    private final CutiJenisCommandService commandService = mock(CutiJenisCommandService.class);
    private final CutiJenisController controller = new CutiJenisController(queryService, commandService);

    @Test
    void listReturnsListResultOfCutiJenisMiniResponse() {
        CutiJenisMiniResponse mini = new CutiJenisMiniResponse(2L, "Cuti Sakit", 1L);
        when(queryService.findList(any(CutiJenisListRequest.class))).thenReturn(List.of(mini));

        ResponseEntity<ListResult<CutiJenisMiniResponse>> response =
                controller.list(new CutiJenisListRequest());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ListResult<CutiJenisMiniResponse> body = response.getBody();
        assertNotNull(body, "envelope harus ListResult, bukan tipe response lain");
        assertNotNull(body.getData());
        assertEquals(1, body.getData().size());
        CutiJenisMiniResponse item = body.getData().getFirst();
        assertEquals(2L, item.id());
        assertEquals("Cuti Sakit", item.nama());
        assertEquals(1L, item.parentId(),
                "parentId harus ikut dikirim apa adanya (nilai riil dari service/repo)");
    }
}

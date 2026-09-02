package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.*;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootQueryService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootWorkflowCommandService;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Regression test: GajiBatchRootController CRUD endpoints must correctly handle
 * EProsesGaji status as ordinal (INTEGER) throughout the request/response cycle.
 */
class GajiBatchRootControllerTest {

    private final GajiBatchRootCommandService commandService = mock(GajiBatchRootCommandService.class);
    private final GajiBatchRootWorkflowCommandService workflowCommandService = mock(GajiBatchRootWorkflowCommandService.class);
    private final GajiBatchRootQueryService queryService = mock(GajiBatchRootQueryService.class);
    private final GajiBatchRootController controller = new GajiBatchRootController(commandService, workflowCommandService, queryService);

    // --- INDEX (GET) ---

    @Test
    void index_returnsListOfResponsesWithStatus() {
        GajiBatchRootResponse response = new GajiBatchRootResponse(
                "202609-001", "202609", EProsesGaji.PENDING,
                10, null, null, null,
                null, null, null,
                null, null, null,
                null, null, null,
                null, null, null
        );
        when(queryService.findAll(any(GajiBatchRootIndexQuery.class)))
                .thenReturn(List.of(response));

        ResponseEntity<ListResult<GajiBatchRootResponse>> result =
                controller.index(new GajiBatchRootIndexQuery());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getData().size());
        assertEquals(EProsesGaji.PENDING, result.getBody().getData().getFirst().status());
    }

    // --- BY PERIODE ---

    @Test
    void byPeriode_returnsMatchingBatches() {
        GajiBatchRootResponse response = new GajiBatchRootResponse(
                "202609-001", "202609", EProsesGaji.FINISHED,
                20, null, null, null,
                null, null, null,
                null, null, null,
                null, null, null,
                null, null, null
        );
        when(queryService.findAll(any(GajiBatchRootIndexQuery.class)))
                .thenReturn(List.of(response));

        ResponseEntity<ListResult<GajiBatchRootResponse>> result =
                controller.byPeriode("202609", EProsesGaji.FINISHED);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(EProsesGaji.FINISHED, result.getBody().getData().getFirst().status());
    }

    // --- CREATE (POST) ---

    @Test
    void create_returnsCreatedStatus() {
        when(commandService.save(any(GajiBatchRootPostRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, "1 success"));

        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        ResponseEntity<SavedResult<String>> result = controller.create(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("1 success", result.getBody().getData());
    }

    @Test
    void create_duplicatePeriod_returnsConflict() {
        when(commandService.save(any(GajiBatchRootPostRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.DUPLICATE, "Batch already exists"));

        GajiBatchRootPostRequest request = new GajiBatchRootPostRequest();
        request.setTahun("2026");
        request.setBulan("09");

        ResponseEntity<SavedResult<String>> result = controller.create(request);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
    }

    // --- WORKFLOW ENDPOINTS ---

    @Test
    void reprocess_pathIdMismatch_throwsBadRequest() {
        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("different-id");

        assertThrows(BadRequestException.class,
                () -> controller.reprocess("202609-001", request));
    }

    @Test
    void reprocess_returnsSuccess() {
        when(workflowCommandService.reprocess(any(GajiBatchRootProcessRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, "success"));

        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("202609-001");

        ResponseEntity<SavedResult<String>> result = controller.reprocess("202609-001", request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void verify1_pathIdMismatch_throwsBadRequest() {
        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("wrong-id");

        assertThrows(BadRequestException.class,
                () -> controller.verify1("202609-001", request));
    }

    @Test
    void verify1_returnsSuccess() {
        when(workflowCommandService.verify1(any(GajiBatchRootProcessRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, "success"));

        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("202609-001");

        ResponseEntity<SavedResult<String>> result = controller.verify1("202609-001", request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void verify2_pathIdMismatch_throwsBadRequest() {
        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("wrong-id");

        assertThrows(BadRequestException.class,
                () -> controller.verify2("202609-001", request));
    }

    @Test
    void verify2_returnsSuccess() {
        when(workflowCommandService.verify2(any(GajiBatchRootProcessRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, "success"));

        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("202609-001");

        ResponseEntity<SavedResult<String>> result = controller.verify2("202609-001", request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void accept_pathIdMismatch_throwsBadRequest() {
        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("wrong-id");

        assertThrows(BadRequestException.class,
                () -> controller.accept("202609-001", request));
    }

    @Test
    void accept_returnsSuccess() {
        when(workflowCommandService.accept(any(GajiBatchRootProcessRequest.class)))
                .thenReturn(SavedStatus.build(ESaveStatus.SUCCESS, "success"));

        GajiBatchRootProcessRequest request = new GajiBatchRootProcessRequest();
        request.setId("202609-001");

        ResponseEntity<SavedResult<String>> result = controller.accept("202609-001", request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    // --- DELETE ---

    @Test
    void delete_returnsOkWhenSuccess() {
        when(commandService.delete("202609-001")).thenReturn(true);

        ResponseEntity<DeletedResult> result = controller.delete("202609-001");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Data berhasil dihapus", result.getBody().getMessage());
    }

    @Test
    void delete_returnsBadRequestWhenFailed() {
        when(commandService.delete("202609-001")).thenReturn(false);

        ResponseEntity<DeletedResult> result = controller.delete("202609-001");

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}

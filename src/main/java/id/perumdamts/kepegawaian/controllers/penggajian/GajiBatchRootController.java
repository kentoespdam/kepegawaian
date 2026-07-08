package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootQueryService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootWorkflowCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/batch")
@RequiredArgsConstructor
public class GajiBatchRootController {
    private final GajiBatchRootCommandService commandService;
    private final GajiBatchRootWorkflowCommandService workflowCommandService;
    private final GajiBatchRootQueryService queryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ListResult<GajiBatchRootResponse>> index(@ParameterObject @Valid GajiBatchRootIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{periode}/periode/{status}/status")
    public ResponseEntity<ListResult<GajiBatchRootResponse>> byPeriode(
            @PathVariable String periode,
            @PathVariable EProsesGaji status) {
        GajiBatchRootIndexQuery request = new GajiBatchRootIndexQuery();
        request.setPeriode(periode);
        request.setStatus(status);
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Object>> create(@Valid @ModelAttribute GajiBatchRootPostRequest request) {
        return saveResult(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reprocess")
    public ResponseEntity<SavedResult<Object>> reprocess(@PathVariable String id,
                                          @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            return CustomResult.save(SavedStatus.build(ESaveStatus.FAILED, "Error Process"));
        return saveResult(workflowCommandService.reprocess(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/verify1")
    public ResponseEntity<SavedResult<Object>> verify1(@PathVariable String id,
                                        @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            return CustomResult.save(SavedStatus.build(ESaveStatus.FAILED, "Error Process"));
        return saveResult(workflowCommandService.verify1(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/verify2")
    public ResponseEntity<SavedResult<Object>> verify2(@PathVariable String id,
                                        @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            return CustomResult.save(SavedStatus.build(ESaveStatus.FAILED, "Error Process"));
        return saveResult(workflowCommandService.verify2(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/accept")
    public ResponseEntity<SavedResult<Object>> accept(@PathVariable String id,
                                       @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            return CustomResult.save(SavedStatus.build(ESaveStatus.FAILED, "Error Process"));
        return saveResult(workflowCommandService.accept(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable String id) {
        return CustomResult.delete(commandService.delete(id));
    }

    @SuppressWarnings("unchecked")
    private static ResponseEntity<SavedResult<Object>> saveResult(SavedStatus<?> status) {
        return CustomResult.save((SavedStatus<Object>) status);
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootQueryService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootWorkflowCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Penggajian — Gaji Batch Root")
@RestController
@RequestMapping("/penggajian/batch")
@RequiredArgsConstructor
public class GajiBatchRootController {
    private final GajiBatchRootCommandService commandService;
    private final GajiBatchRootWorkflowCommandService workflowCommandService;
    private final GajiBatchRootQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiBatchRootResponse>>> index(@ParameterObject @Valid GajiBatchRootIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "by periode")
    @GetMapping("/{periode}/periode/{status}/status")
    public ResponseEntity<PageResult<Page<GajiBatchRootResponse>>> byPeriode(
            @PathVariable String periode,
            @PathVariable EProsesGaji status) {
        GajiBatchRootIndexQuery request = new GajiBatchRootIndexQuery();
        request.setPeriode(periode);
        request.setStatus(status);
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "Buat data baru")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<String>> create(@Valid @ModelAttribute GajiBatchRootPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "reprocess")
    @PatchMapping("/{id}/reprocess")
    public ResponseEntity<SavedResult<String>> reprocess(@PathVariable String id,
                                          @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            throw new BadRequestException("Path id does not match request body id");
        return CustomResult.save(workflowCommandService.reprocess(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "verify1")
    @PatchMapping("/{id}/verify1")
    public ResponseEntity<SavedResult<String>> verify1(@PathVariable String id,
                                        @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            throw new BadRequestException("Path id does not match request body id");
        return CustomResult.save(workflowCommandService.verify1(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "verify2")
    @PatchMapping("/{id}/verify2")
    public ResponseEntity<SavedResult<String>> verify2(@PathVariable String id,
                                        @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            throw new BadRequestException("Path id does not match request body id");
        return CustomResult.save(workflowCommandService.verify2(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "accept")
    @PatchMapping("/{id}/accept")
    public ResponseEntity<SavedResult<String>> accept(@PathVariable String id,
                                       @Valid @RequestBody GajiBatchRootProcessRequest request) {
        if (!request.getId().equals(id))
            throw new BadRequestException("Path id does not match request body id");
        return CustomResult.save(workflowCommandService.accept(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable String id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

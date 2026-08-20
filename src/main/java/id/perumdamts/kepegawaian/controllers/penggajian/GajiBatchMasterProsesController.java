package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@Tag(name = "Penggajian — Gaji Batch Master Proses")
@RestController
@RequestMapping("/penggajian/batch/master/proses")
public class GajiBatchMasterProsesController {
    private final GajiBatchMasterProsesCommandService commandService;
    private final GajiBatchMasterProsesQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiBatchMasterProsesResponse>>> index(@ParameterObject @Valid GajiBatchMasterProsesIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "show")
    @GetMapping("/{masterId}/master_batch_id/{kode}/kode")
    public ResponseEntity<SingleResult<GajiBatchMasterProsesResponse>> show(@PathVariable Long masterId, @PathVariable String kode) {
        return CustomResult.any(queryService.findById(masterId).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "by batch master")
    @GetMapping("/{batchMasterId}/master")
    public ResponseEntity<ListResult<GajiBatchMasterProsesResponse>> byBatchMaster(@PathVariable Long batchMasterId) {
        return CustomResult.list(queryService.findByMasterId(batchMasterId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody GajiBatchMasterProsesPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "rollback")
    @DeleteMapping("/{rootBatchId}/rollback")
    public ResponseEntity<DeletedResult> rollback(@PathVariable String rootBatchId) {
        return CustomResult.delete(commandService.rollback(rootBatchId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:PROCESS')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

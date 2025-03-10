package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/penggajian/batch/master/proses")
public class GajiBatchMasterProsesController {
    private final GajiBatchMasterProsesService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiBatchMasterProsesRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{masterId}/master_batch_id/{kode}/kode")
    public ResponseEntity<?> show(@PathVariable Long masterId, @PathVariable String kode) {
        return CustomResult.any(service.findById(masterId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{batchMasterId}/master")
    public ResponseEntity<?> byBatchMaster(@PathVariable Long batchMasterId) {
        return CustomResult.list(service.findByMasterId(batchMasterId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody GajiBatchMasterProsesPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{rootBatchId}/rollback")
    public ResponseEntity<?> rollback(@PathVariable String rootBatchId) {
        return CustomResult.delete(service.rollback(rootBatchId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{masterId}/master_batch_id/{kode}/kode")
    public ResponseEntity<?> delete(@PathVariable Long masterId, @PathVariable String kode) {
        return CustomResult.delete(service.delete(masterId));
    }
}

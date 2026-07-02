package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/batch/master")
public class GajiBatchMasterController {
    private final GajiBatchMasterCommandService commandService;
    private final GajiBatchMasterQueryService queryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getGajiBatchMasterByPeriode(@Valid @ParameterObject GajiBatchMasterIndexQuery request, Errors errors) {
        if (errors.hasErrors())
            return ErrorResult.build(errors);
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGajiBatchMasterById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @GetMapping("/pegawai/{pegawaiId}")
    public ResponseEntity<?> getGajiBatchMasterByPegawaiId(@PathVariable Long pegawaiId, @ParameterObject GajiBatchMasterIndexQuery query) {
        return CustomResult.page(queryService.findByPegawaiId(pegawaiId, query));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download/table-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadTableGaji(@PathVariable String rootBatchId) {
        return queryService.downloadTableGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download/potongan-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadPotonganGaji(@PathVariable String rootBatchId) {
        return queryService.downloadPotonganGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "upload/{rootBatchId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPotonganTambahan(@PathVariable String rootBatchId, @Valid @ModelAttribute GajiBatchMasterPostRequest request, Errors errors) {
        if (errors.hasErrors())
            return ErrorResult.build(errors);
        return CustomResult.save(commandService.uploadPotonganTambahan(rootBatchId, request));
    }
}

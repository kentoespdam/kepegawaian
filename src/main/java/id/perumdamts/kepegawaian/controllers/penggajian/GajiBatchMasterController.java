package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterService;
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
    private final GajiBatchMasterService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getGajiBatchMasterByPeriode(@Valid @ParameterObject GajiBatchMasterRequest request, Errors errors) {
        if (errors.hasErrors())
            return ErrorResult.build(errors);
        return CustomResult.list(service.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download/table-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadTableGaji(@PathVariable String rootBatchId){
        return service.downloadTableGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download/potongan-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadPotonganGaji(@PathVariable String rootBatchId){
        return service.downloadPotonganGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(name = "upload/{rootBatchId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPotonganTambahan(@PathVariable String rootBatchId, @Valid @ModelAttribute GajiBatchMasterPostRequest request, Errors errors) {
        return CustomResult.save(service.uploadPotonganTambahan(rootBatchId, request));
    }


}

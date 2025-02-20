package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/batch/master")
public class GajiBatchMasterController {
    private final GajiBatchMasterService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getGajiBatchMaster(@Valid @ParameterObject GajiBatchMasterRequest request, Errors errors) {
        if (errors.hasErrors())
            return ErrorResult.build(errors);
        return CustomResult.page(service.findPage(request));
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
}

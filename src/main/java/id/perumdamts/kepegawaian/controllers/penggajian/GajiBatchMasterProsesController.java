package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/penggajian/batch/master/proses")
public class GajiBatchMasterProsesController {
    private final GajiBatchMasterProsesService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiBatchMasterProsesRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{batchMasterId}/master")
    public ResponseEntity<?> index(@PathVariable Long batchMasterId) {
        return CustomResult.list(service.findByMasterId(batchMasterId));
    }
}

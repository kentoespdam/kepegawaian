package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootProcessRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot.GajiBatchRootRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot.GajiBatchRootService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/batch")
@RequiredArgsConstructor
public class GajiBatchRootController {
    private final GajiBatchRootService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiBatchRootRequest request) {
        return CustomResult.any(service.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{periode}/periode/{status}/status")
    public ResponseEntity<?> byPeriode(@PathVariable String periode, @PathVariable EProsesGaji status) {
        GajiBatchRootRequest request = new GajiBatchRootRequest();
        request.setPeriode(periode);
        request.setGtStatus(status);
        return CustomResult.any(service.findAll(request));
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{id}/errors")
//    public ResponseEntity<?> errorLogs(@PathVariable String id) {
//        return CustomResult.list(service.findErrorLogs(id));
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@Valid @ModelAttribute GajiBatchRootPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reprocess")
    public ResponseEntity<?> reprocess(@PathVariable String id, @Valid @RequestBody GajiBatchRootProcessRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (!request.getId().equals(id)) return ErrorResult.build("Error Process");
        return CustomResult.save(service.reprocess(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/verify1")
    public ResponseEntity<?> verify1(@PathVariable String id, @Valid @RequestBody GajiBatchRootProcessRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (!request.getId().equals(id)) return ErrorResult.build("Error Process");
        return CustomResult.save(service.verify1(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/verify2")
    public ResponseEntity<?> verify2(@PathVariable String id, @Valid @RequestBody GajiBatchRootProcessRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (!request.getId().equals(id)) return ErrorResult.build("Error Process");
        return CustomResult.save(service.verify2(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable String id, @Valid @RequestBody GajiBatchRootProcessRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (!request.getId().equals(id)) return ErrorResult.build("Error Process");
        return CustomResult.save(service.accept(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return CustomResult.delete(service.delete(id));
    }
}

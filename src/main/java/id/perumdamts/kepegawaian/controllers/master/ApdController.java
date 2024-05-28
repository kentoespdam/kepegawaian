package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPutRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdRequest;
import id.perumdamts.kepegawaian.services.master.apd.ApdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/apd")
public class ApdController {
    private final ApdService apdService;

    @GetMapping
    public ResponseEntity<?> get(@ParameterObject ApdRequest request) {
        return CustomResult.any(apdService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(apdService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(apdService.findById(id));
    }

    @GetMapping("/{id}/profesi")
    public ResponseEntity<?> findByProfesi(@PathVariable Long id) {
        return CustomResult.any(apdService.findByProfesiId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ApdPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(apdService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ApdPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(apdService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(apdService.deleteById(id));
    }
}

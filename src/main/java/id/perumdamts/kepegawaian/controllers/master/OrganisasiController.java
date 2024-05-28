package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiRequest;
import id.perumdamts.kepegawaian.services.master.organisasi.OrganisasiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/organisasi")
public class OrganisasiController {
    private final OrganisasiService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject OrganisasiRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> search() {
        return CustomResult.list(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{id}/parent")
    public ResponseEntity<?> findByParentId(@PathVariable Long id) {
        return CustomResult.any(service.findByParentId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody OrganisasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@Valid @RequestBody List<OrganisasiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody OrganisasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(service.deleteById(id));
    }
}

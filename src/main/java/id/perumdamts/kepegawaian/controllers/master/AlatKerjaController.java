package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaRequest;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/alat_kerja")
public class AlatKerjaController {
    private final AlatKerjaService AlatKerjaService;

    @GetMapping
    public ResponseEntity<?> get(@ParameterObject AlatKerjaRequest request) {
        return CustomResult.any(AlatKerjaService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(AlatKerjaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(AlatKerjaService.findById(id));
    }

    @GetMapping("/{id}/profesi")
    public ResponseEntity<?> findByProfesi(@PathVariable Long id) {
        return CustomResult.any(AlatKerjaService.findByProfesiId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody AlatKerjaPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(AlatKerjaService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody AlatKerjaPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(AlatKerjaService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(AlatKerjaService.deleteById(id));
    }
}

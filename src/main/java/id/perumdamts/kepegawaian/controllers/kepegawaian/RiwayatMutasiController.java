package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.*;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/mutasi")
public class RiwayatMutasiController {
    private final RiwayatMutasiService service;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject RiwayatMutasiRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> findByPegawaiId(@PathVariable Long id, @ParameterObject RiwayatMutasiRequest request) {
        return CustomResult.any(service.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatMutasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);

        Set<ConstraintViolation<RiwayatMutasiPostRequest>> violations = validator.validate(request);

        if (Objects.nonNull(request.getGolonganId()))
            violations.addAll(validator.validate(request, MutasiGolongan.class));

        if (Objects.nonNull(request.getOrganisasiId()))
            violations.addAll(validator.validate(request, MutasiJabatan.class));

        if (!violations.isEmpty()) return ErrorResult.build(violations);

        return CustomResult.any(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatMutasiPutRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.any(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.any(service.delete(id));
    }
}

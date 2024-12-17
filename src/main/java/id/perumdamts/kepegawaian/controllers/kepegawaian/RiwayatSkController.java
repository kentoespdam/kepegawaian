package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.GajiSk;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
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
@RequestMapping("/kepegawaian/riwayat/sk")
public class RiwayatSkController {
    private final RiwayatSkService riwayatSkService;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject RiwayatSkRequest request) {
        return CustomResult.any(riwayatSkService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject RiwayatSkRequest request) {
        return CustomResult.list(riwayatSkService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(riwayatSkService.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> findByPegawaiId(@PathVariable Long id, @ParameterObject RiwayatSkRequest request) {
        return CustomResult.any(riwayatSkService.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatSkPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty())
                return ErrorResult.build(validate);
        }

        return CustomResult.save(riwayatSkService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatSkPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty())
                return ErrorResult.build(validate);
        }
        return CustomResult.save(riwayatSkService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(riwayatSkService.delete(id));
    }
}

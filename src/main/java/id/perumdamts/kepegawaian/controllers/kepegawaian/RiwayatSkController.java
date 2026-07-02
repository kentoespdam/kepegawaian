package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.GajiSk;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkQueryService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/sk")
public class RiwayatSkController {
    private final RiwayatSkCommandService commandService;
    private final RiwayatSkQueryService queryService;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @GetMapping
    public ResponseEntity<?> index(@Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> findByPegawaiId(@PathVariable Long id, @Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatSkPostRequest request) {
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty())
                return ErrorResult.build(validate);
        }

        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatSkPutRequest request) {
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty())
                return ErrorResult.build(validate);
        }
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        commandService.delete(id);
        return CustomResult.delete(true);
    }
}

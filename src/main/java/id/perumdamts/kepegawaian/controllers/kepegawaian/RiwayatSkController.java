package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.*;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkQueryService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PageResult<Page<RiwayatSkQuery>>> index(@Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<RiwayatSkQuery>> list(@Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatSkQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatSkQuery>>> findByPegawaiId(@PathVariable Long id, @Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody RiwayatSkPostRequest request) {
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty()) {
                throw new ConstraintViolationException(validate);
            }
        }

        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody RiwayatSkPutRequest request) {
        if (Objects.nonNull(request.getUpdateMaster()) && request.getUpdateMaster()) {
            Set<ConstraintViolation<RiwayatSkPostRequest>> validate = validator.validate(request, GajiSk.class);
            if (!validate.isEmpty()) {
                throw new ConstraintViolationException(validate);
            }
        }
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

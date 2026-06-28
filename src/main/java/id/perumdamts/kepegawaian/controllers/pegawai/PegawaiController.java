package id.perumdamts.kepegawaian.controllers.pegawai;

import id.perumdamts.kepegawaian.config.PegawaiProperties;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiCommandService;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiQueryService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pegawai")
public class PegawaiController {
    private final PegawaiQueryService queryService;
    private final PegawaiCommandService commandService;
    private final PegawaiProperties pegawaiProperties;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject PegawaiRequest request) {
        return CustomResult.any(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject PegawaiRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/{nipam}/nipam")
    public ResponseEntity<?> findByNipam(@PathVariable String nipam) {
        return CustomResult.any(queryService.findByNipam(nipam));
    }

    @GetMapping("/{id}/ringkasan")
    public ResponseEntity<?> findRingkasan(@PathVariable Long id) {
        return CustomResult.any(queryService.findRingkasan(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody PegawaiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);

        if (request.getStatusPegawai().equals(EStatusPegawai.PEGAWAI) && !pegawaiProperties.getExcludedJabatanIds().contains(request.getJabatanId())) {
            Set<ConstraintViolation<PegawaiPostRequest>> validate = validator.validate(request, PegawaiTetap.class);
            if (!validate.isEmpty()) return ErrorResult.build(validate);
        }
        return CustomResult.save(commandService.save(request));
    }

    @PostMapping("/batch-by-ids")
    public ResponseEntity<?> batchByIds(@Valid @RequestBody PegawaiBatchIdsRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.list(queryService.findByIds(request.getIds()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@Valid @RequestBody List<PegawaiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PegawaiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/gaji")
    public ResponseEntity<?> patchGaji(@PathVariable Long id, @Valid @RequestBody PegawaiPatchGaji request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.patchGaji(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/profil")
    public ResponseEntity<?> patchProfil(@PathVariable Long id, @Valid @RequestBody PegawaiPatchProfil request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (!id.equals(request.getId())) return ErrorResult.build("Unknown Pegawai");
        return CustomResult.save(commandService.patchProfil(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

package id.perumdamts.kepegawaian.controllers.pegawai;

import id.perumdamts.kepegawaian.config.PegawaiProperties;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.*;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiCommandService;
import id.perumdamts.kepegawaian.services.pegawai.pegawai.PegawaiQueryService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final Validator validator;

    @GetMapping
    public ResponseEntity<PageResult<Page<PegawaiTableResponse>>> index(@ParameterObject @Valid PegawaiRequest request) {
        return CustomResult.page(queryService.findTablePage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<PegawaiListResponse>> list(@ParameterObject @Valid PegawaiRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PegawaiResponseDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/{nipam}/nipam")
    public ResponseEntity<SingleResult<PegawaiResponse>> findByNipam(@PathVariable String nipam) {
        return CustomResult.any(queryService.findByNipam(nipam));
    }

    @GetMapping("/{id}/ringkasan")
    public ResponseEntity<SingleResult<PegawaiResponseRingkasan>> findRingkasan(@PathVariable Long id) {
        return CustomResult.any(queryService.findRingkasan(id));
    }

    @GetMapping("/{id}/session")
    public ResponseEntity<SingleResult<PegawaiResponseSession>> findSession(@PathVariable Long id) {
        return CustomResult.any(queryService.findSession(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody PegawaiPostRequest request) {
        if (request.getStatusPegawai().equals(EStatusPegawai.PEGAWAI)
                && !pegawaiProperties.getExcludedJabatanIds().contains(request.getJabatanId())) {
            Set<ConstraintViolation<PegawaiPostRequest>> violations = validator.validate(request, PegawaiTetap.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
        return CustomResult.save(commandService.save(request));
    }

    @PostMapping("/batch-by-ids")
    public ResponseEntity<ListResult<PegawaiListResponse>> batchByIds(@Valid @RequestBody PegawaiBatchIdsRequest request) {
        return CustomResult.list(queryService.findByIds(request.getIds()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<String>> saveBatch(@Valid @RequestBody List<PegawaiPostRequest> requests) {
        return CustomResult.save(commandService.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id,
                                                     @Valid @RequestBody PegawaiPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/gaji")
    public ResponseEntity<SavedResult<Long>> patchGaji(@PathVariable Long id,
                                                        @Valid @RequestBody PegawaiPatchGaji request) {
        return CustomResult.save(commandService.patchGaji(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/profil")
    public ResponseEntity<SavedResult<Long>> patchProfil(@PathVariable Long id,
                                                          @Valid @RequestBody PegawaiPatchProfil request) {
        if (!id.equals(request.getId())) {
            throw new IllegalArgumentException("Unknown Pegawai");
        }
        return CustomResult.save(commandService.patchProfil(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

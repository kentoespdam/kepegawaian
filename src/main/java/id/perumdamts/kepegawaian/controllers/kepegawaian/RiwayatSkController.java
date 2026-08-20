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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Kepegawaian — Riwayat Sk")
@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/sk")
public class RiwayatSkController {
    private final RiwayatSkCommandService commandService;
    private final RiwayatSkQueryService queryService;
    private final Validator validator;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<RiwayatSkQuery>>> index(@Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<RiwayatSkQuery>> list(@Valid @ParameterObject RiwayatSkListRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "detail")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatSkQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "find by pegawai id")
    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatSkQuery>>> findByPegawaiId(@PathVariable Long id, @Valid @ParameterObject RiwayatSkRequest request) {
        return CustomResult.page(queryService.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Simpan data baru")
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

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Perbarui data")
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

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

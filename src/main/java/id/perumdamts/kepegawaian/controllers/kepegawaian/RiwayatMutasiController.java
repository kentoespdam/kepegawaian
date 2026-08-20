package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiQueryService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Kepegawaian — Riwayat Mutasi")
@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/mutasi")
public class RiwayatMutasiController {
    private final RiwayatMutasiCommandService commandService;
    private final RiwayatMutasiQueryService queryService;
    private final Validator validator;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatMutasiQuery>>> index(@PathVariable Long id, @Valid @ParameterObject RiwayatMutasiRequest request) {
        request.setPegawaiId(id);
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "detail")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatMutasiQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody RiwayatMutasiPostRequest request) {
        if (request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GOLONGAN) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GAJI) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GAJI_BERKALA)
        ) {
            Set<ConstraintViolation<RiwayatMutasiPostRequest>> violations = validator.validate(request, MutasiGolongan.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }

        if (request.getJenisMutasi().equals(EJenisMutasi.MUTASI_JABATAN) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_LOKER)
        ) {
            Set<ConstraintViolation<RiwayatMutasiPostRequest>> violations = validator.validate(request, MutasiJabatan.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }

        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody RiwayatMutasiPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

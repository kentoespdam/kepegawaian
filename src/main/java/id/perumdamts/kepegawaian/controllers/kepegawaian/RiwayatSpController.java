package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Kepegawaian — Riwayat Sp")
@RestController
@RequestMapping("/kepegawaian/riwayat/sp")
@RequiredArgsConstructor
public class RiwayatSpController {
    private final RiwayatSpCommandService commandService;
    private final RiwayatSpQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatSpQuery>>> index(@PathVariable Long id, @Valid @ParameterObject RiwayatSpRequest request) {
        return CustomResult.page(queryService.pageQuery(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "detail")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatSpQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @Operation(summary = "Ambil file")
    @GetMapping("/{id}/file")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        return queryService.getFile(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Buat data baru")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Long>> create(@Valid @ModelAttribute RiwayatSpPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatSpPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

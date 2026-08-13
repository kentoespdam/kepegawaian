package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster.GajiBatchMasterQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/batch/master")
public class GajiBatchMasterController {
    private final GajiBatchMasterCommandService commandService;
    private final GajiBatchMasterQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping
    public ResponseEntity<ListResult<GajiBatchMasterResponse>> getGajiBatchMasterByPeriode(
            @Valid @ParameterObject GajiBatchMasterIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiBatchMasterResponse>> getGajiBatchMasterById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/pegawai/{pegawaiId}")
    public ResponseEntity<PageResult<Page<GajiBatchMasterResponse>>> getGajiBatchMasterByPegawaiId(
            @PathVariable Long pegawaiId,
            @ParameterObject @Valid GajiBatchMasterIndexQuery query) {
        return CustomResult.page(queryService.findByPegawaiId(pegawaiId, query));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/download/table-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadTableGaji(@PathVariable String rootBatchId) {
        return queryService.downloadTableGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/download/potongan-gaji/{rootBatchId}")
    public ResponseEntity<?> downloadPotonganGaji(@PathVariable String rootBatchId) {
        return queryService.downloadPotonganGaji(rootBatchId);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PatchMapping(value = "upload/{rootBatchId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<String>> uploadPotonganTambahan(
            @PathVariable String rootBatchId,
            @Valid @ModelAttribute GajiBatchMasterPostRequest request) {
        return CustomResult.save(commandService.uploadPotonganTambahan(rootBatchId, request));
    }
}

package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaCommandService;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/kuota")
@RequiredArgsConstructor
public class CutiKuotaController {
    private final CutiKuotaQueryService queryService;
    private final CutiKuotaCommandService commandService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:READ')")
    @GetMapping
    public ResponseEntity<PageResult<CutiKuotaPegawaiResponse>> index(@Valid @ParameterObject CutiKuotaRequest request) {
        return CustomResult.page(queryService.findIndex(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<CutiKuotaResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/{pegawaiId}/{tahun}/sisa")
    public ResponseEntity<SingleResult<CutiKuotaSisa>> showByPegawai(@PathVariable Long pegawaiId, @PathVariable Integer tahun) {
        return CustomResult.any(queryService.findByPegawai(pegawaiId, tahun));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:READ')")
    @GetMapping("/template")
    public ResponseEntity<?> template() {
        return commandService.exportTemplate();
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> store(@Valid @RequestBody CutiKuotaPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<String>> importData(@Valid @ModelAttribute CutiKuotaImportRequest request) {
        return CustomResult.save(commandService.importData(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody CutiKuotaPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

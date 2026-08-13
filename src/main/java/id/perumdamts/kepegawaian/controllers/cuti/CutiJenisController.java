package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisListRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisResponse;
import id.perumdamts.kepegawaian.services.cuti.jenis.CutiJenisCommandService;
import id.perumdamts.kepegawaian.services.cuti.jenis.CutiJenisQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/jenis")
@RequiredArgsConstructor
public class CutiJenisController {
    private final CutiJenisQueryService queryService;
    private final CutiJenisCommandService commandService;

    // cuti_jenis = data referensi (katalog jenis cuti) — login-only, seperti read master
    @GetMapping
    public ResponseEntity<PageResult<Page<CutiJenisResponse>>> index(@Valid @ParameterObject CutiJenisRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<CutiJenisResponse>> list(@Valid @ParameterObject CutiJenisListRequest request) {
        return CustomResult.list(queryService.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<CutiJenisResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody CutiJenisPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody CutiJenisPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CUTI:WRITE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

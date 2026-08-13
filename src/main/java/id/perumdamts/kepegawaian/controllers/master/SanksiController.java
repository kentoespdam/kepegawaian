package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.sanksi.*;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiCommandService;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/sanksi")
public class SanksiController {
    private final SanksiQueryService query;
    private final SanksiCommandService command;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<SanksiQuery>>> index(@ParameterObject @Valid SanksiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<SanksiQuery>> list() {
        return CustomResult.list(query.listQuery());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<SanksiQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/jenis-sp/{id}")
    public ResponseEntity<ListResult<SanksiJenisSpList>> findByJenisSpId(@PathVariable Long id) {
        return CustomResult.list(query.findJenisSpList(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody SanksiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody SanksiPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PatchMapping("/{id}/jenis-sp")
    public ResponseEntity<SavedResult<Long>> updateJenisSp(@PathVariable Long id, @RequestBody PatchSanksiJenisSpRequest request) {
        var entity = command.updateJenisSp(id, request.getJenisSpId());
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

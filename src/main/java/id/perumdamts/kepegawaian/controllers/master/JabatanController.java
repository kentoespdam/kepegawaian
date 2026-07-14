package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.jabatan.*;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanCommandService;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jabatan")
public class JabatanController {
    private final JabatanQueryService query;
    private final JabatanCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<JabatanQuery>>> index(@ParameterObject @Valid JabatanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<JabatanListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JabatanQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @GetMapping("/{id}/parent")
    public ResponseEntity<ListResult<JabatanQuery>> findByParentId(@PathVariable Long id) {
        return CustomResult.list(query.findByParentId(id));
    }

    @GetMapping("/organisasi/{id}")
    public ResponseEntity<ListResult<JabatanQuery>> findByOrganisasiId(@PathVariable Long id) {
        return CustomResult.list(query.findByOrganisasiId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JabatanPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JabatanPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

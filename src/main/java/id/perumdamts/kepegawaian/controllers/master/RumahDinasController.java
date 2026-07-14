package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasListResponse;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasPostRequest;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasQuery;
import id.perumdamts.kepegawaian.services.master.rumahDinas.RumahDinasCommandService;
import id.perumdamts.kepegawaian.services.master.rumahDinas.RumahDinasQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/rumah-dinas")
public class RumahDinasController {
    private final RumahDinasQueryService query;
    private final RumahDinasCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<RumahDinasQuery>>> index(@ParameterObject @Valid RumahDinasIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<RumahDinasListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RumahDinasQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody RumahDinasPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody RumahDinasPostRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

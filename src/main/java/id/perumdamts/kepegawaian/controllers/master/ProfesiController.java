package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.profesi.*;
import id.perumdamts.kepegawaian.services.master.profesi.ProfesiCommandService;
import id.perumdamts.kepegawaian.services.master.profesi.ProfesiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/profesi")
public class ProfesiController {
    private final ProfesiQueryService query;
    private final ProfesiCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<ProfesiDetail>>> index(@ParameterObject @Valid ProfesiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<ProfesiListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<ProfesiDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @GetMapping("/jabatan/{id}")
    public ResponseEntity<ListResult<ProfesiListResponse>> findByJabatanId(@PathVariable Long id) {
        return CustomResult.list(query.findByJabatanId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody ProfesiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id,
                                    @Valid @RequestBody ProfesiPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

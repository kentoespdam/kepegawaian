package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiListResponse;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiQuery;
import id.perumdamts.kepegawaian.services.master.alasanBerhenti.AlasanBerhentiCommandService;
import id.perumdamts.kepegawaian.services.master.alasanBerhenti.AlasanBerhentiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/alasan-berhenti")
public class AlasanBerhentiController {
    private final AlasanBerhentiQueryService query;
    private final AlasanBerhentiCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<AlasanBerhentiQuery>>> index(@ParameterObject @Valid AlasanBerhentiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<AlasanBerhentiListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<AlasanBerhentiQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody AlasanBerhentiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody AlasanBerhentiPostRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.grade.GradeIndexQuery;
import id.perumdamts.kepegawaian.dto.master.grade.GradeListResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.services.master.grade.GradeCommandService;
import id.perumdamts.kepegawaian.services.master.grade.GradeQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/grade")
public class GradeController {
    private final GradeQueryService query;
    private final GradeCommandService command;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<GradeQuery>>> index(@ParameterObject @Valid GradeIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<GradeListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GradeQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/level/{id}")
    public ResponseEntity<ListResult<GradeQuery>> findByLevelId(@PathVariable Long id) {
        return CustomResult.list(query.findByLevelId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody GradePostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GradePostRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

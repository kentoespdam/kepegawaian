package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.grade.GradeIndexQuery;
import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.services.master.grade.GradeCommandService;
import id.perumdamts.kepegawaian.services.master.grade.GradeQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/grade")
public class GradeController {
    private final GradeQueryService query;
    private final GradeCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GradeIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @GetMapping("/level/{id}")
    public ResponseEntity<?> findByLevelId(@PathVariable Long id) {
        return CustomResult.list(query.findByLevelId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody GradePostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GradePostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

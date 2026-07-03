package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.level.LevelIndexQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelPostRequest;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.services.master.level.LevelCommandService;
import id.perumdamts.kepegawaian.services.master.level.LevelQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/level")
public class LevelController {
    private final LevelQueryService query;
    private final LevelCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject @Valid LevelIndexQuery request) {
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody LevelPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        Level entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@Valid @RequestBody List<LevelPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        command.createBatch(requests);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody LevelPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        Level entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPutRequest;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanCommandService;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jabatan")
public class JabatanController {
    private final JabatanQueryService query;
    private final JabatanCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject JabatanIndexQuery request) {
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

    @GetMapping("/{id}/parent")
    public ResponseEntity<?> findByParentId(@PathVariable Long id) {
        return CustomResult.list(query.findByParentId(id));
    }

    @GetMapping("/organisasi/{id}")
    public ResponseEntity<?> findByOrganisasiId(@PathVariable Long id) {
        return CustomResult.list(query.findByOrganisasiId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody JabatanPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody JabatanPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

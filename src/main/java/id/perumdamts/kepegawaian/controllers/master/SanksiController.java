package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.sanksi.PatchSanksiJenisSpRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPostRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPutRequest;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiCommandService;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/sanksi")
public class SanksiController {
    private final SanksiQueryService query;
    private final SanksiCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject SanksiIndexQuery request) {
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

    @GetMapping("/jenis-sp/{id}")
    public ResponseEntity<?> findByJenisSpId(@PathVariable Long id) {
        return CustomResult.list(query.findByJenisSpId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody SanksiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SanksiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/jenis-sp")
    public ResponseEntity<?> updateJenisSp(@PathVariable Long id, @RequestBody PatchSanksiJenisSpRequest request) {
        var entity = command.updateJenisSp(id, request.getJenisSpId());
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

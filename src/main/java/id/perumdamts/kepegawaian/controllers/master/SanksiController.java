package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.master.sanksi.PatchSanksiJenisSpRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiJenisSpList;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPostRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPutRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
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

    @GetMapping
    public ResponseEntity<PageResult<Page<SanksiQuery>>> index(@ParameterObject @Valid SanksiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<SanksiQuery>> list() {
        return CustomResult.list(query.listQuery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<SanksiQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @GetMapping("/jenis-sp/{id}")
    public ResponseEntity<ListResult<SanksiJenisSpList>> findByJenisSpId(@PathVariable Long id) {
        return CustomResult.list(query.findJenisSpList(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody SanksiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody SanksiPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/jenis-sp")
    public ResponseEntity<SavedResult<Long>> updateJenisSp(@PathVariable Long id, @RequestBody PatchSanksiJenisSpRequest request) {
        var entity = command.updateJenisSp(id, request.getJenisSpId());
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

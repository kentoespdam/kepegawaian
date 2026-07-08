package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.entities.commons.MasterBaseEntity;
import id.perumdamts.kepegawaian.services.master.jenjangPendidikan.JenjangPendidikanCommandService;
import id.perumdamts.kepegawaian.services.master.jenjangPendidikan.JenjangPendidikanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenjang-pendidikan")
public class JenjangPendidikanController {
    private final JenjangPendidikanQueryService queryService;
    private final JenjangPendidikanCommandService commandService;

    @GetMapping
    public ResponseEntity<PageResult<Page<JenjangPendidikanResponse>>> index(@ParameterObject @Valid JenjangPendidikanIndexQuery request) {
        return CustomResult.page(queryService.pageQuery(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<JenjangPendidikanResponse>> list() {
        return CustomResult.list(queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JenjangPendidikanResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JenjangPendidikanPostRequest request) {
        var entity = commandService.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<List<Long>>> saveBatch(@Valid @RequestBody List<@Valid JenjangPendidikanPostRequest> requests) {
        var entities = commandService.saveBatch(requests);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entities.stream().map(MasterBaseEntity::getId).toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JenjangPendidikanPutRequest request) {
        var entity = commandService.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        commandService.delete(id);
        return CustomResult.delete(true);
    }
}

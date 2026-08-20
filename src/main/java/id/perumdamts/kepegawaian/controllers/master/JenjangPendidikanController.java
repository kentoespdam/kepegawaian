package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Jenjang Pendidikan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenjang-pendidikan")
public class JenjangPendidikanController {
    private final JenjangPendidikanQueryService queryService;
    private final JenjangPendidikanCommandService commandService;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<JenjangPendidikanResponse>>> index(@ParameterObject @Valid JenjangPendidikanIndexQuery request) {
        return CustomResult.page(queryService.pageQuery(request));
    }

    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<JenjangPendidikanResponse>> list() {
        return CustomResult.list(queryService.findAll());
    }

    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JenjangPendidikanResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JenjangPendidikanPostRequest request) {
        var entity = commandService.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "save batch")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<List<Long>>> saveBatch(@Valid @RequestBody List<@Valid JenjangPendidikanPostRequest> requests) {
        var entities = commandService.saveBatch(requests);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entities.stream().map(MasterBaseEntity::getId).toList()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JenjangPendidikanPutRequest request) {
        var entity = commandService.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @Operation(summary = "Hapus data berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

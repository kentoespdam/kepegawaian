package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.jabatan.*;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanCommandService;
import id.perumdamts.kepegawaian.services.master.jabatan.JabatanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Jabatan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jabatan")
public class JabatanController {
    private final JabatanQueryService query;
    private final JabatanCommandService command;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<JabatanQuery>>> index(@ParameterObject @Valid JabatanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<JabatanListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JabatanQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @Operation(summary = "find by parent id")
    @GetMapping("/{id}/parent")
    public ResponseEntity<ListResult<JabatanQuery>> findByParentId(@PathVariable Long id) {
        return CustomResult.list(query.findByParentId(id));
    }

    @Operation(summary = "find by organisasi id")
    @GetMapping("/organisasi/{id}")
    public ResponseEntity<ListResult<JabatanQuery>> findByOrganisasiId(@PathVariable Long id) {
        return CustomResult.list(query.findByOrganisasiId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JabatanPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JabatanPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @Operation(summary = "Hapus data berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.profesi.*;
import id.perumdamts.kepegawaian.services.master.profesi.ProfesiCommandService;
import id.perumdamts.kepegawaian.services.master.profesi.ProfesiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Profesi")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/profesi")
public class ProfesiController {
    private final ProfesiQueryService query;
    private final ProfesiCommandService command;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<ProfesiDetail>>> index(@ParameterObject @Valid ProfesiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<ProfesiListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<ProfesiDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @Operation(summary = "find by jabatan id")
    @GetMapping("/jabatan/{id}")
    public ResponseEntity<ListResult<ProfesiListResponse>> findByJabatanId(@PathVariable Long id) {
        return CustomResult.list(query.findByJabatanId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody ProfesiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id,
                                    @Valid @RequestBody ProfesiPutRequest request) {
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

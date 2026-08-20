package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.sanksi.*;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiCommandService;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Sanksi")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/sanksi")
public class SanksiController {
    private final SanksiQueryService query;
    private final SanksiCommandService command;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<SanksiQuery>>> index(@ParameterObject @Valid SanksiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<SanksiQuery>> list() {
        return CustomResult.list(query.listQuery());
    }

    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<SanksiQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @Operation(summary = "find by jenis sp id")
    @GetMapping("/jenis-sp/{id}")
    public ResponseEntity<ListResult<SanksiJenisSpList>> findByJenisSpId(@PathVariable Long id) {
        return CustomResult.list(query.findJenisSpList(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody SanksiPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody SanksiPutRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "update jenis sp")
    @PatchMapping("/{id}/jenis-sp")
    public ResponseEntity<SavedResult<Long>> updateJenisSp(@PathVariable Long id, @RequestBody PatchSanksiJenisSpRequest request) {
        var entity = command.updateJenisSp(id, request.getJenisSpId());
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @Operation(summary = "Hapus data berdasarkan ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

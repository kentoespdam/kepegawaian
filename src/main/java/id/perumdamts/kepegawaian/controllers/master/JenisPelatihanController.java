package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanQuery;
import id.perumdamts.kepegawaian.services.master.jenisPelatihan.JenisPelatihanCommandService;
import id.perumdamts.kepegawaian.services.master.jenisPelatihan.JenisPelatihanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Jenis Pelatihan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-pelatihan")
public class JenisPelatihanController {
    private final JenisPelatihanQueryService query;
    private final JenisPelatihanCommandService command;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<JenisPelatihanQuery>>> index(@ParameterObject @Valid JenisPelatihanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<JenisPelatihanQuery>> list() {
        return CustomResult.list(query.listQuery());
    }

    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JenisPelatihanQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JenisPelatihanPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JenisPelatihanPostRequest request) {
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

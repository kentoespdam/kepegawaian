package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanListResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-pelatihan")
public class JenisPelatihanController {
    private final JenisPelatihanQueryService query;
    private final JenisPelatihanCommandService command;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<JenisPelatihanQuery>>> index(@ParameterObject @Valid JenisPelatihanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<JenisPelatihanListResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<JenisPelatihanQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody JenisPelatihanPostRequest request) {
        var entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody JenisPelatihanPostRequest request) {
        var entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

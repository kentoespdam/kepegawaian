package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DasarGaji;
import id.perumdamts.kepegawaian.services.penggajian.dasarGaji.DasarGajiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.dasarGaji.DasarGajiQueryService;
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
@RequestMapping("/penggajian/dasar-gaji")
public class DasarGajiController {
    private final DasarGajiCommandService command;
    private final DasarGajiQueryService query;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<DasarGajiResponse>>> index(@ParameterObject @Valid DasarGajiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<DasarGajiResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<DasarGajiResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody DasarGajiPostRequest request) {
        DasarGaji entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<List<Long>>> createBatch(@Valid @RequestBody List<@Valid DasarGajiPostRequest> requests) {
        List<DasarGaji> entities = command.createBatch(requests);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entities.stream().map(DasarGaji::getId).toList()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody DasarGajiPutRequest request) {
        DasarGaji entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

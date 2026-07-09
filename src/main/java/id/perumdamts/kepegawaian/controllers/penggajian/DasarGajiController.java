package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import org.springframework.data.domain.Page;
import id.perumdamts.kepegawaian.services.penggajian.dasarGaji.DasarGajiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.dasarGaji.DasarGajiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/dasar-gaji")
public class DasarGajiController {
    private final DasarGajiCommandService commandService;
    private final DasarGajiQueryService queryService;

    @GetMapping
    public ResponseEntity<SingleResult<Page<DasarGajiResponse>>> get(@ParameterObject @Valid DasarGajiIndexQuery request) {
        return CustomResult.any(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<DasarGajiResponse>> list(@ParameterObject @Valid DasarGajiIndexQuery request) {
        return CustomResult.list(queryService.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<DasarGajiResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody DasarGajiPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<String>> batch(@Valid @RequestBody List<DasarGajiPostRequest> requests) {
        return CustomResult.save(commandService.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody DasarGajiPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

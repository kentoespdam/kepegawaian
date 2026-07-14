package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/sp")
@RequiredArgsConstructor
public class RiwayatSpController {
    private final RiwayatSpCommandService commandService;
    private final RiwayatSpQueryService queryService;

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatSpQuery>>> index(@PathVariable Long id, @Valid @ParameterObject RiwayatSpRequest request) {
        return CustomResult.page(queryService.pageQuery(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatSpQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.getById(id));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        return queryService.getFile(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @ModelAttribute RiwayatSpPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatSpPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

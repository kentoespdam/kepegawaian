package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSp.RiwayatSpRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSp.RiwayatSpQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/sp")
@RequiredArgsConstructor
public class RiwayatSpController {
    private final RiwayatSpCommandService commandService;
    private final RiwayatSpQueryService queryService;

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> index(@PathVariable Long id, @ParameterObject RiwayatSpRequest request) {
        return CustomResult.page(queryService.pageQuery(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.getById(id));
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        return queryService.getFile(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute RiwayatSpPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatSpPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        commandService.delete(id);
        return CustomResult.delete(true);
    }
}

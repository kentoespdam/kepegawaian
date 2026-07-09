package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaQuery;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaCommandService;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AlatKerjaController {
    private final AlatKerjaCommandService command;
    private final AlatKerjaQueryService query;

    @GetMapping("/master/alat-kerja/{id}")
    public ResponseEntity<SingleResult<AlatKerjaQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/master/alat-kerja")
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody AlatKerjaPostRequest request) {
        return CustomResult.save(command.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/master/alat-kerja/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody AlatKerjaPostRequest request) {
        return CustomResult.save(command.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/master/alat-kerja/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

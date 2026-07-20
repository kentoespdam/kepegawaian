package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AlatKerjaController {
    private final AlatKerjaCommandService command;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/master/profesi/{profesiId}/alat-kerja")
    public ResponseEntity<SavedResult<Long>> save(@PathVariable Long profesiId,
                                                   @Valid @RequestBody AlatKerjaPostRequest request) {
        return CustomResult.save(command.create(profesiId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/master/profesi/{profesiId}/alat-kerja/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long profesiId,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody AlatKerjaPostRequest request) {
        return CustomResult.save(command.update(id, profesiId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/master/profesi/{profesiId}/alat-kerja/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long profesiId,
                                                 @PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

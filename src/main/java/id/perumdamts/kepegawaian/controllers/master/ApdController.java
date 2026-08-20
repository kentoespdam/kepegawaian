package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.services.master.apd.ApdCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Tag(name = "Master Data — APD")
public class ApdController {
    private final ApdCommandService command;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PostMapping("/master/profesi/{profesiId}/apd")
    @Operation(summary = "Tambah APD baru")
    public ResponseEntity<SavedResult<Long>> save(@PathVariable Long profesiId,
                                                   @Valid @RequestBody ApdPostRequest request) {
        return CustomResult.save(command.create(profesiId, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:WRITE')")
    @PutMapping("/master/profesi/{profesiId}/apd/{id}")
    @Operation(summary = "Perbarui APD")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long profesiId,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody ApdPostRequest request) {
        return CustomResult.save(command.update(id, profesiId, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:DELETE')")
    @DeleteMapping("/master/profesi/{profesiId}/apd/{id}")
    @Operation(summary = "Hapus APD")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long profesiId,
                                                 @PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

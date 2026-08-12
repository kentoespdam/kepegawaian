package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanLampiranCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin context (ADR-0038): HRD/ADMIN mengelola data pendidikan — selalu stable
 * (changedStatus=false, tidak masuk approval queue).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/profil/pendidikan")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
public class AdminPendidikanController {
    private final PendidikanCommandService command;
    private final PendidikanLampiranCommandService lampiranCommand;

    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody PendidikanPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request, false)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PendidikanPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request, false)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id, false));
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<SavedResult<Long>> addLampiran(@Valid @ModelAttribute PendidikanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, lampiranCommand.addLampiran(request, false)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(lampiranCommand.deleteLampiran(id, false));
    }
}

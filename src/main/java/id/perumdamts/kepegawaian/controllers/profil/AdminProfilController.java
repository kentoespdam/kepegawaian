package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin edit profil (ADR-0038): PATCH /admin/profil/{id} — HRD/ADMIN mengedit
 * profil siapapun, TIDAK PERNAH trigger changedStatus (langsung stable).
 * Dual-mode: ADMIN tetap jalan via role, role lain dengan PROFIL:APPROVE ikut diizinkan.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/profil")
public class AdminProfilController {
    private final BiodataCommandService commandService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
    @PatchMapping("/{id}")
    public ResponseEntity<SavedResult<String>> patchBiodataAdmin(@PathVariable String id,
                                                                  @Valid @RequestBody BiodataPatchRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.patchBiodata(id, request, false)));
    }
}

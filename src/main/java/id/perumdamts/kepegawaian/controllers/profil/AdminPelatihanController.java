package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.*;
import id.perumdamts.kepegawaian.services.profil.pelatihan.PelatihanCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Admin context (ADR-0038): HRD/ADMIN mengelola data pelatihan — selalu stable.
 */
@Tag(name = "Admin — Admin Pelatihan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/profil/pelatihan")
@PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
public class AdminPelatihanController {
    private final PelatihanCommandService command;

    @Operation(summary = "Buat data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody PelatihanPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request, false)));
    }

    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PelatihanPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request, false)));
    }

    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id, false));
    }

    @Operation(summary = "add lampiran")
    @PostMapping(value = "/lampiran", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Long>> addLampiran(@Valid @ModelAttribute PelatihanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request, false)));
    }

    @Operation(summary = "Hapus lampiran")
    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(command.deleteLampiran(id, false));
    }
}

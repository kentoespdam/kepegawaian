package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequiredArgsConstructor
@Tag(name = "Profil Pegawai — Lampiran Profil")
@RestController
@RequestMapping("/profil/lampiran")
public class LampiranProfilController {
    private final LampiranProfilQueryService queryService;
    private final LampiranProfilCommandService commandService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "Ambil file")
    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisLampiranProfil jenis, @PathVariable Long id) {
        return queryService.getFileLampiranById(jenis, id);
    }

    // ADR-0036 §6: jalur approval lampiran lama (POST /profil/lampiran/accept) dihapus —
    // approval kini lewat antrian ProfileUpdate (PUT /profil/profil-update/{id}).
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id, true));
    }
}

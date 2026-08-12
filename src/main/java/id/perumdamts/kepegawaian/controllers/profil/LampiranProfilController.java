package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilCommandService;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profil/lampiran")
public class LampiranProfilController {
    private final LampiranProfilQueryService queryService;
    private final LampiranProfilCommandService commandService;

    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisLampiranProfil jenis, @PathVariable Long id) {
        return queryService.getFileLampiranById(jenis, id);
    }

    // ADR-0036 §6: jalur approval lampiran lama (POST /profil/lampiran/accept) dihapus —
    // approval kini lewat antrian ProfileUpdate (PUT /profil/profil-update/{id}).
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Self-service profil (ADR-0038): PATCH /profil — edit biodata milik sendiri,
 * SELALU masuk approval queue (changedStatus=true). NIK diambil dari principal,
 * bukan dari path/body, sehingga tidak bisa edit profil orang lain.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/profil")
public class SelfProfilController {
    private final BiodataCommandService commandService;
    private final PegawaiRepository pegawaiRepository;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @PatchMapping
    public ResponseEntity<SavedResult<String>> patchBiodataSelf(@Valid @RequestBody BiodataPatchRequest request) {
        AppwriteUser principal = (AppwriteUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // ponytail: DEV tidak punya pegawai riil — self-service tanpa akun tidak terdefinisi.
        // Test alur approval pakai Bearer token asli, atau pakai PATCH /admin/profil/{id}.
        if ("DEV".equals(principal.get$id())) {
            throw new NotFoundException("Self-service tidak tersedia untuk DEV — gunakan PATCH /admin/profil/{id}");
        }
        Pegawai pegawai = pegawaiRepository.findById(Long.valueOf(principal.get$id()))
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        String nik = pegawai.getBiodata().getNik();
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.patchBiodata(nik, request, true)));
    }
}

package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilAcceptRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profil/lampiran")
public class LampiranProfilController {
    private final LampiranProfilService service;

    @GetMapping("/file/{jenis}/{id}")
    public ResponseEntity<?> getFile(@PathVariable EJenisLampiranProfil jenis, @PathVariable Long id) {
        return service.getFileLampiranById(jenis, id);
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptLampiran(@Valid @RequestBody LampiranProfilAcceptRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        AppwriteUser appwriteUser = (AppwriteUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return CustomResult.save(service.acceptLampiran(request, appwriteUser.getName()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.deleteById(id));
    }
}

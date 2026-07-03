package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateAcceptRequest;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateQueryService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profil/profil-update")
@RequiredArgsConstructor
public class ProfilUpdateController {
    private final ProfileUpdateQueryService queryService;
    private final ProfileUpdateService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject @Valid ProfileUpdateRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> approval(@PathVariable Long id, @Valid @RequestBody ProfilUpdateAcceptRequest approval, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.approval(id, approval));
    }
}

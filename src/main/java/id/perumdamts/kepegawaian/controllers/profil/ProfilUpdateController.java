package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.PageResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateAcceptRequest;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateQuery;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateQueryService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profil/profil-update")
@RequiredArgsConstructor
public class ProfilUpdateController {
    private final ProfileUpdateQueryService queryService;
    private final ProfileUpdateService service;

    @GetMapping
    public ResponseEntity<PageResult<Page<ProfileUpdateQuery>>> index(@ParameterObject @Valid ProfileUpdateRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<ProfilUpdateDetail<?>>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<String>> approval(@PathVariable Long id, @Valid @RequestBody ProfilUpdateAcceptRequest approval) {
        return CustomResult.save(service.approval(id, approval));
    }
}

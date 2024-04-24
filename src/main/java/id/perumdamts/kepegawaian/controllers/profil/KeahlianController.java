package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianAcceptRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianRequest;
import id.perumdamts.kepegawaian.services.profil.keahlian.KeahlianService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/keahlian")
public class KeahlianController {
    private final KeahlianService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject KeahlianRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{nik}/biodata")
    public ResponseEntity<?> findByBiodataId(@PathVariable String nik) {
        return CustomResult.list(service.findByBiodataId(nik));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody KeahlianPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody KeahlianPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptKeahlian(@PathVariable Long id, @Valid @RequestBody KeahlianAcceptRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        AppwriteUser appwriteUser = (AppwriteUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return CustomResult.save(service.acceptKeahlian(id, request.getBiodataId(), appwriteUser.get$id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

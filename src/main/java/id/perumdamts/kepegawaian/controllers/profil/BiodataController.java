package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataRequest;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataService;
import id.perumdamts.kepegawaian.utils.MimeTypesUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/biodata")
public class BiodataController {
    private final BiodataService service;
    private final MimeTypesUtils mimeTypesUtils;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject BiodataRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return CustomResult.any(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody BiodataPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> saveBatch(@Valid @RequestBody List<BiodataPostRequest> requests, Errors errors) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody BiodataPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchBiodata(@PathVariable String id, @Valid @RequestBody BiodataPatchRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.patchBiodata(id, request));
    }

    @GetMapping("/{id}/foto-profil")
    public ResponseEntity<?> getFotoProfil(@PathVariable String id) {
        return service.findFotoProfil(id);
    }

    @PutMapping("/{id}/foto-profil")
    public ResponseEntity<?> updateFotoProfil(@PathVariable String id, @RequestParam("fotoProfil") MultipartFile fotoProfil) {
        String extension = mimeTypesUtils.getExtension(fotoProfil.getContentType());
        if (!mimeTypesUtils.isImage(extension))
            return ErrorResult.build("File must be an image");

        return CustomResult.save(service.updateFotoProfil(id, fotoProfil));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String id) {
        return CustomResult.delete(service.deleteById(id));
    }
}

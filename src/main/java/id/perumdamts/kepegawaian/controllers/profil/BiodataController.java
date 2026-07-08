package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPatchRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPostRequest;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataPutRequest;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataQueryService;
import id.perumdamts.kepegawaian.utils.MimeTypesUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/biodata")
public class BiodataController {
    private final BiodataQueryService queryService;
    private final BiodataCommandService commandService;
    private final MimeTypesUtils mimeTypesUtils;

    @GetMapping
    public ResponseEntity<?> index(@Valid @ParameterObject BiodataIndexQuery query) {
        return CustomResult.page(queryService.pageQuery(query));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject @Valid BiodataIndexQuery query) {
        return CustomResult.list(queryService.findAll(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        return CustomResult.any(queryService.getById(id));
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Object>> save(@Valid @RequestBody BiodataPostRequest request) {
        return (ResponseEntity<SavedResult<Object>>) (ResponseEntity<?>) CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.create(request)));
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Object>> update(@PathVariable String id, @Valid @RequestBody BiodataPutRequest request) {
        return (ResponseEntity<SavedResult<Object>>) (ResponseEntity<?>) CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<SavedResult<Object>> patchBiodata(@PathVariable String id, @Valid @RequestBody BiodataPatchRequest request) {
        return (ResponseEntity<SavedResult<Object>>) (ResponseEntity<?>) CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.patchBiodata(id, request)));
    }

    @GetMapping("/{id}/foto-profil")
    public ResponseEntity<?> getFotoProfil(@PathVariable String id) {
        return queryService.findFotoProfil(id);
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/foto-profil")
    public ResponseEntity<SavedResult<Object>> updateFotoProfil(@PathVariable String id, @RequestParam("fotoProfil") MultipartFile fotoProfil) {
        String extension = mimeTypesUtils.getExtension(fotoProfil.getContentType());
        if (!mimeTypesUtils.isImage(extension))
            throw new IllegalArgumentException("File must be an image");
        return (ResponseEntity<SavedResult<Object>>) (ResponseEntity<?>) CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.updateFotoProfil(id, fotoProfil)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable String id) {
        commandService.deleteById(id);
        return CustomResult.delete(Boolean.TRUE);
    }
}

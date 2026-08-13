package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.biodata.*;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataQueryService;
import id.perumdamts.kepegawaian.utils.MimeTypesUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
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

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<BiodataQuery>>> index(@Valid @ParameterObject BiodataIndexQuery query) {
        return CustomResult.page(queryService.pageQuery(query));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<BiodataQuery>> list() {
        return CustomResult.list(queryService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<BiodataDetail>> findById(@PathVariable String id) {
        return CustomResult.any(queryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
    @PostMapping
    public ResponseEntity<SavedResult<String>> save(@Valid @RequestBody BiodataPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<String>> update(@PathVariable String id, @Valid @RequestBody BiodataPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request, false)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<SingleResult<BiodataDashboardResponse>> getDashboard(@PathVariable String id) {
        return CustomResult.any(queryService.getDashboard(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @GetMapping("/{id}/foto-profil")
    public ResponseEntity<?> getFotoProfil(@PathVariable String id) {
        return queryService.findFotoProfil(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
    @PutMapping("/{id}/foto-profil")
    public ResponseEntity<SavedResult<String>> updateFotoProfil(@PathVariable String id, @RequestParam("fotoProfil") MultipartFile fotoProfil) {
        String extension = mimeTypesUtils.getExtension(fotoProfil.getContentType());
        if (!mimeTypesUtils.isImage(extension))
            throw new IllegalArgumentException("File must be an image");
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.updateFotoProfil(id, fotoProfil)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:APPROVE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable String id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.biodata.*;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataCommandService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataQueryService;
import id.perumdamts.kepegawaian.utils.MimeTypesUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/biodata")
public class BiodataController {
    private final BiodataQueryService queryService;
    private final BiodataCommandService commandService;
    private final MimeTypesUtils mimeTypesUtils;
    private final PegawaiRepository pegawaiRepository;

    @GetMapping
    public ResponseEntity<PageResult<Page<BiodataQuery>>> index(@Valid @ParameterObject BiodataIndexQuery query) {
        return CustomResult.page(queryService.pageQuery(query));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<BiodataQuery>> list() {
        return CustomResult.list(queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<BiodataDetail>> findById(@PathVariable String id) {
        return CustomResult.any(queryService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<String>> save(@Valid @RequestBody BiodataPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<String>> update(@PathVariable String id, @Valid @RequestBody BiodataPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SavedResult<String>> patchBiodata(@PathVariable String id, @Valid @RequestBody BiodataPatchRequest request) {
        AppwriteUser principal = (AppwriteUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var isAdmin = principal.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !"DEV".equals(principal.get$id())) {
            var pegawai = pegawaiRepository.findById(Long.valueOf(principal.get$id()))
                    .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
            String ownNik = pegawai.getBiodata().getNik();
            if (!ownNik.equals(id))
                throw new NotFoundException("Biodata not found");
        }
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.patchBiodata(id, request)));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<SingleResult<BiodataDashboardResponse>> getDashboard(@PathVariable String id) {
        return CustomResult.any(queryService.getDashboard(id));
    }

    @GetMapping("/{id}/foto-profil")
    public ResponseEntity<?> getFotoProfil(@PathVariable String id) {
        return queryService.findFotoProfil(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/foto-profil")
    public ResponseEntity<SavedResult<String>> updateFotoProfil(@PathVariable String id, @RequestParam("fotoProfil") MultipartFile fotoProfil) {
        String extension = mimeTypesUtils.getExtension(fotoProfil.getContentType());
        if (!mimeTypesUtils.isImage(extension))
            throw new IllegalArgumentException("File must be an image");
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.updateFotoProfil(id, fotoProfil)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable String id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

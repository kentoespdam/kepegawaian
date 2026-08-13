package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiProfil.GajiProfilCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiProfil.GajiProfilQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/profil")
@RequiredArgsConstructor
public class GajiProfilController {
    private final GajiProfilCommandService commandService;
    private final GajiProfilQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiProfilResponse>>> index(@ParameterObject @Valid GajiProfilIndexQuery request) {
        return CustomResult.page(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiProfilResponse>> list(@ParameterObject @Valid GajiProfilListRequest request) {
        return CustomResult.list(queryService.list(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiProfilResponse>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiProfilPostRequest request) {
        return CustomResult.save(commandService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiProfilPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

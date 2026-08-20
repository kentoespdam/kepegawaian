package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.*;
import id.perumdamts.kepegawaian.services.profil.pelatihan.PelatihanCommandService;
import id.perumdamts.kepegawaian.services.profil.pelatihan.PelatihanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Profil Pegawai — Pelatihan")
@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pelatihan")
public class PelatihanController {
    private final PelatihanQueryService query;
    private final PelatihanCommandService command;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<PelatihanQuery>>> index(@ParameterObject @Valid PelatihanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PelatihanDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "Simpan data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody PelatihanPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request, true)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PelatihanPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request, true)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id, true));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "Ambil lampiran")
    @GetMapping("/{id}/lampiran")
    public ResponseEntity<ListResult<LampiranProfilQuery>> getLampiran(@PathVariable Long id) {
        return CustomResult.list(query.getLampiran(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "Ambil lampiran by id")
    @GetMapping("/lampiran/{id}")
    public ResponseEntity<SingleResult<LampiranProfilQuery>> getLampiranById(@PathVariable Long id) {
        return CustomResult.any(query.getLampiranById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:READ')")
    @Operation(summary = "Ambil file lampiran by id")
    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return query.getFileLampiranById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "save lampiran")
    @PostMapping(value = "/lampiran", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SavedResult<Long>> saveLampiran(@Valid @ModelAttribute PelatihanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request, true)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFIL:UPDATE')")
    @Operation(summary = "Hapus lampiran")
    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(command.deleteLampiran(id, true));
    }
}

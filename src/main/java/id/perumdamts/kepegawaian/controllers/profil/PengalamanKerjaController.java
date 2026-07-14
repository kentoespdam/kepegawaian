package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.*;
import id.perumdamts.kepegawaian.services.profil.pengalamanKerja.PengalamanKerjaCommandService;
import id.perumdamts.kepegawaian.services.profil.pengalamanKerja.PengalamanKerjaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pengalaman-kerja")
public class PengalamanKerjaController {
    private final PengalamanKerjaQueryService query;
    private final PengalamanKerjaCommandService command;

    // READ

    @GetMapping
    public ResponseEntity<PageResult<Page<PengalamanKerjaQuery>>> index(@ParameterObject @Valid PengalamanKerjaIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PengalamanKerjaDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    // WRITE

    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody PengalamanKerjaPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PengalamanKerjaPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }

    // Lampiran

    @GetMapping("/lampiran/{id}/list")
    public ResponseEntity<ListResult<LampiranProfilQuery>> getLampiran(@PathVariable Long id) {
        return CustomResult.list(query.getLampiran(id));
    }

    @GetMapping("/lampiran/{id}/detail")
    public ResponseEntity<SingleResult<LampiranProfilQuery>> getLampiranById(@PathVariable Long id) {
        return CustomResult.any(query.getLampiranById(id));
    }

    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return query.getFileLampiranById(id);
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<SavedResult<Long>> saveLampiran(@Valid @ModelAttribute PengalamanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(command.deleteLampiran(id));
    }
}

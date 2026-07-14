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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pelatihan")
public class PelatihanController {
    private final PelatihanQueryService query;
    private final PelatihanCommandService command;

    @GetMapping
    public ResponseEntity<PageResult<Page<PelatihanQuery>>> index(@ParameterObject @Valid PelatihanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PelatihanDetail>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody PelatihanPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PelatihanPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }

    @GetMapping("/{id}/lampiran")
    public ResponseEntity<ListResult<LampiranProfilQuery>> getLampiran(@PathVariable Long id) {
        return CustomResult.list(query.getLampiran(id));
    }

    @GetMapping("/lampiran/{id}")
    public ResponseEntity<SingleResult<LampiranProfilQuery>> getLampiranById(@PathVariable Long id) {
        return CustomResult.any(query.getLampiranById(id));
    }

    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return query.getFileLampiranById(id);
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<SavedResult<Long>> saveLampiran(@Valid @ModelAttribute PelatihanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(command.deleteLampiran(id));
    }
}

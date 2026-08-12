package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilQuery;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanLampiranCommandService;
import id.perumdamts.kepegawaian.services.profil.pendidikan.PendidikanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pendidikan")
public class PendidikanController {
    private final PendidikanQueryService query;
    private final PendidikanCommandService command;
    private final PendidikanLampiranCommandService lampiranCommand;

    // READ

    @GetMapping
    public ResponseEntity<PageResult<Page<PendidikanQuery>>> index(@Valid @ParameterObject PendidikanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PendidikanQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    // WRITE

    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody PendidikanPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request, true)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody PendidikanPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request, true)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id, true));
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
    public ResponseEntity<SavedResult<Long>> saveLampiran(@Valid @ModelAttribute PendidikanLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, lampiranCommand.addLampiran(request, true)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(lampiranCommand.deleteLampiran(id, true));
    }
}

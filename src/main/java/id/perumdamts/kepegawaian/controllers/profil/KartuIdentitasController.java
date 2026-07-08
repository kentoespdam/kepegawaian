package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPostRequest;
import id.perumdamts.kepegawaian.dto.profil.kartuIdentitas.KartuIdentitasPutRequest;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasCommandService;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasLampiranCommandService;
import id.perumdamts.kepegawaian.services.profil.kartuIdentitas.KartuIdentitasQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/kartu-identitas")
public class KartuIdentitasController {
    private final KartuIdentitasQueryService query;
    private final KartuIdentitasCommandService command;
    private final KartuIdentitasLampiranCommandService lampiranCommand;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject @Valid KartuIdentitasIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody KartuIdentitasPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody KartuIdentitasPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }

    @GetMapping("/{id}/lampiran")
    public ResponseEntity<?> getLampiran(@PathVariable Long id) {
        return CustomResult.list(query.getLampiran(id));
    }

    @GetMapping("/lampiran/{id}")
    public ResponseEntity<?> getLampiranById(@PathVariable Long id) {
        return CustomResult.any(query.getLampiranById(id));
    }

    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return query.getFileLampiranById(id);
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<SavedResult<Long>> saveLampiran(@Valid @ModelAttribute KartuIdentitasLampiranPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, lampiranCommand.addLampiran(request)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<DeletedResult> deleteLampiran(@PathVariable Long id) {
        lampiranCommand.deleteLampiran(id);
        return CustomResult.delete(true);
    }
}

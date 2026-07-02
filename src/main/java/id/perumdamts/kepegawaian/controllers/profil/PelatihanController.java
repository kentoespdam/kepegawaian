package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.PelatihanPutRequest;
import id.perumdamts.kepegawaian.services.profil.pelatihan.PelatihanCommandService;
import id.perumdamts.kepegawaian.services.profil.pelatihan.PelatihanQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pelatihan")
public class PelatihanController {
    private final PelatihanQueryService query;
    private final PelatihanCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject PelatihanIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody PelatihanPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PelatihanPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
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
    public ResponseEntity<?> saveLampiran(@Valid @ModelAttribute PelatihanLampiranPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<?> deleteLampiran(@PathVariable Long id) {
        command.deleteLampiran(id);
        return CustomResult.delete(true);
    }
}

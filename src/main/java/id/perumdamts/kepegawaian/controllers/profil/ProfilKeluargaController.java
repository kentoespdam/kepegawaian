package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.services.profil.keluarga.ProfilKeluargaCommandService;
import id.perumdamts.kepegawaian.services.profil.keluarga.ProfilKeluargaLampiranCommandService;
import id.perumdamts.kepegawaian.services.profil.keluarga.ProfilKeluargaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/keluarga")
public class ProfilKeluargaController {
    private final ProfilKeluargaQueryService query;
    private final ProfilKeluargaCommandService command;
    private final ProfilKeluargaLampiranCommandService lampiranCommand;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject ProfilKeluargaIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ProfilKeluargaPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProfilKeluargaPutRequest request, Errors errors) {
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
    public ResponseEntity<?> saveLampiran(@Valid @ModelAttribute ProfilKeluargaLampiranPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, lampiranCommand.addLampiran(request)));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<?> deleteLampiran(@PathVariable Long id) {
        lampiranCommand.deleteLampiran(id);
        return CustomResult.delete(true);
    }
}

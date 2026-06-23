package id.perumdamts.kepegawaian.controllers.profil.pengalamanKerja;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaIndexQuery;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pengalamanKerja.PengalamanKerjaPutRequest;
import id.perumdamts.kepegawaian.services.profil.pengalamanKerja.PengalamanKerjaCommandService;
import id.perumdamts.kepegawaian.services.profil.pengalamanKerja.PengalamanKerjaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profil/pengalaman-kerja")
public class PengalamanKerjaController {
    private final PengalamanKerjaQueryService query;
    private final PengalamanKerjaCommandService command;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject PengalamanKerjaIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody PengalamanKerjaPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody PengalamanKerjaPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }

    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return command.getFileLampiranById(id);
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<?> saveLampiran(@Valid @ModelAttribute PengalamanKerjaLampiranPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, command.addLampiran(request)));
    }
}

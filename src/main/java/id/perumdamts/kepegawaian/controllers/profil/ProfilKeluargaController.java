package id.perumdamts.kepegawaian.controllers.profil;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaRequest;
import id.perumdamts.kepegawaian.services.profil.keluarga.ProfilKeluargaService;
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
    private final ProfilKeluargaService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject ProfilKeluargaRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{biodataId}/biodata")
    public ResponseEntity<?> findByNik(@PathVariable String biodataId, @ParameterObject ProfilKeluargaRequest request) {
        return CustomResult.any(service.findByBiodataId(biodataId, request));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ProfilKeluargaPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProfilKeluargaPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }

    // Lampiran
    @GetMapping("/lampiran/{id}/list")
    public ResponseEntity<?> getLampiran(@PathVariable Long id) {
        return CustomResult.any(service.getLampiran(id));
    }

    @GetMapping("/lampiran/{id}/detail")
    public ResponseEntity<?> getLampiranById(@PathVariable Long id) {
        return CustomResult.any(service.getLampiranById(id));
    }

    @GetMapping("/lampiran/{id}/file")
    public ResponseEntity<?> getFileLampiranById(@PathVariable Long id) {
        return service.getFileLampiranById(id);
    }

    @PostMapping(value = "/lampiran", consumes = "multipart/form-data")
    public ResponseEntity<?> saveLampiran(@Valid @ModelAttribute ProfilKeluargaLampiranPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.addLampiran(request));
    }

    @DeleteMapping("/lampiran/{id}")
    public ResponseEntity<?> deleteLampiran(@PathVariable Long id) {
        return CustomResult.delete(service.deleteLampiran(id));
    }
}

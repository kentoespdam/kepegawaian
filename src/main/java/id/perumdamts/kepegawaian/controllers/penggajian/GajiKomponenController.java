package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiKomponen.GajiKomponenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("penggajian/komponen")
@RequiredArgsConstructor
public class GajiKomponenController {
    private final GajiKomponenService service;

    @GetMapping("/{profilId}/kode")
    public ResponseEntity<?> listKode(@PathVariable Long profilId) {
        return CustomResult.page(service.findAllKode(profilId));
    }

    @GetMapping("/{profilId}/profil")
    public ResponseEntity<?> index(@PathVariable Long profilId, @ParameterObject GajiKomponenRequest request) {
        return CustomResult.page(service.findByProfil(profilId, request));
    }

    @GetMapping("/{profilId}/profil/urut")
    public ResponseEntity<?> indexUrut(@PathVariable Long profilId) {
        return CustomResult.any(service.findLastUrut(profilId));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findDetail(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody GajiKomponenPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiKomponenPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiProfil.GajiProfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/profil")
@RequiredArgsConstructor
public class GajiProfilController {
    private final GajiProfilService service;

    @GetMapping
    public ResponseEntity<?> index() {
        return CustomResult.page(service.findAll());
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiProfilPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiProfilPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.terminasi.RiwayatTerminasiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/terminasi")
@RequiredArgsConstructor
public class RiwayatTerminasiController {
    private final RiwayatTerminasiService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/calon-pensiun")
    public ResponseEntity<?> indexCalonPensiun(@ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(service.findPageCalonPensiun(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute RiwayatTerminasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatTerminasiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }
}

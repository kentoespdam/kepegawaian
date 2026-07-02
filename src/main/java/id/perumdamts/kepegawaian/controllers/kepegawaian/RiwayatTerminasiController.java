package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.terminasi.RiwayatTerminasiCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.terminasi.RiwayatTerminasiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/terminasi")
@RequiredArgsConstructor
public class RiwayatTerminasiController {
    private final RiwayatTerminasiCommandService commandService;
    private final RiwayatTerminasiQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@Valid @ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/calon-pensiun")
    public ResponseEntity<?> indexCalonPensiun(@Valid @ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(queryService.findPageCalonPensiun(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute RiwayatTerminasiPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatTerminasiPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }
}

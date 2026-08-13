package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.terminasi.RiwayatTerminasiRequest;
import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.services.kepegawaian.terminasi.RiwayatTerminasiCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.terminasi.RiwayatTerminasiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/terminasi")
@RequiredArgsConstructor
public class RiwayatTerminasiController {
    private final RiwayatTerminasiCommandService commandService;
    private final RiwayatTerminasiQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<RiwayatTerminasiQuery>>> index(@Valid @ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @GetMapping("/calon-pensiun")
    public ResponseEntity<PageResult<Page<PegawaiResponse>>> indexCalonPensiun(@Valid @ParameterObject RiwayatTerminasiRequest request) {
        return CustomResult.page(queryService.findPageCalonPensiun(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatTerminasiQuery>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @ModelAttribute RiwayatTerminasiPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @ModelAttribute RiwayatTerminasiPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }
}

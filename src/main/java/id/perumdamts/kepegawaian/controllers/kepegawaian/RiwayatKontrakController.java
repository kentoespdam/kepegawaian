package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/kontrak")
@RequiredArgsConstructor
public class RiwayatKontrakController {
    private final RiwayatKontrakCommandService commandService;
    private final RiwayatKontrakQueryService queryService;

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> index(@PathVariable Long id, @Valid @ParameterObject RiwayatKontrakRequest request) {
        request.setPegawaiId(id);
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatKontrakPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatKontrakPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        commandService.delete(id);
        return CustomResult.delete(true);
    }
}

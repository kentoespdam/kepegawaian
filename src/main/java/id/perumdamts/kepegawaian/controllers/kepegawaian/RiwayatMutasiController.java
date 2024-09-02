package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.RiwayatMutasiRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/mutasi")
public class RiwayatMutasiController {
    private final RiwayatMutasiService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject RiwayatMutasiRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> findByPegawaiId(@PathVariable Long id, @ParameterObject RiwayatMutasiRequest request) {
        return CustomResult.any(service.findByPegawaiId(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatMutasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.any(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatMutasiPutRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.any(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.any(service.delete(id));
    }
}

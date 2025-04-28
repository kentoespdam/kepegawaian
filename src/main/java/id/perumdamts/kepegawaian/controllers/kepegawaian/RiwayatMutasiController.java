package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.kepegawaian.mutasi.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.services.kepegawaian.mutasi.RiwayatMutasiService;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kepegawaian/riwayat/mutasi")
public class RiwayatMutasiController {
    private final RiwayatMutasiService service;
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> index(@PathVariable Long id, @ParameterObject RiwayatMutasiRequest request) {
        request.setPegawaiId(id);
        if (Objects.isNull(request.getSortBy()) || request.getSortBy().isEmpty()) {
            request.setSortBy("id");
            request.setSortDirection("DESC");
        }
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody RiwayatMutasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        if (request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GOLONGAN) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GAJI) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_GAJI_BERKALA)
        ) {
            Set<ConstraintViolation<RiwayatMutasiPostRequest>> violations = validator.validate(request, MutasiGolongan.class);
            if (!violations.isEmpty())
                return ErrorResult.build(violations);
        }

        if (request.getJenisMutasi().equals(EJenisMutasi.MUTASI_JABATAN) ||
                request.getJenisMutasi().equals(EJenisMutasi.MUTASI_LOKER)
        ) {
            Set<ConstraintViolation<RiwayatMutasiPostRequest>> violations = validator.validate(request, MutasiJabatan.class);
            if (!violations.isEmpty())
                return ErrorResult.build(violations);
        }

        return CustomResult.save(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RiwayatMutasiPutRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return ErrorResult.build(errors);
        }
        return CustomResult.save(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

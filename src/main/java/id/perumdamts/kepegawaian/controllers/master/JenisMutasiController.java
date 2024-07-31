package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiRequest;
import id.perumdamts.kepegawaian.services.master.jenisMutasi.JenisMutasiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-mutasi")
public class JenisMutasiController {
    private final JenisMutasiService jenisMutasi;

    @GetMapping
    public ResponseEntity<?> get(@ParameterObject JenisMutasiRequest request) {
        return CustomResult.any(jenisMutasi.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject JenisMutasiRequest request) {
        return CustomResult.list(jenisMutasi.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(jenisMutasi.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody JenisMutasiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(jenisMutasi.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> batch(@Valid @RequestBody List<JenisMutasiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(jenisMutasi.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody JenisMutasiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(jenisMutasi.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(jenisMutasi.deleteById(id));
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiRequest;
import id.perumdamts.kepegawaian.services.penggajian.dasarGaji.DasarGajiService;
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
@RequestMapping("/penggajian/dasar-gaji")
public class DasarGajiController {
    private final DasarGajiService service;

    @GetMapping
    public ResponseEntity<?> get(@ParameterObject DasarGajiRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject DasarGajiRequest request) {
        return CustomResult.list(service.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody DasarGajiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> batch(@Valid @RequestBody List<DasarGajiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody DasarGajiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(service.deleteById(id));
    }
}

package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.jenis.CutiJenisRequest;
import id.perumdamts.kepegawaian.services.cuti.jenis.CutiJenisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/jenis")
@RequiredArgsConstructor
public class CutiJenisController {
    private final CutiJenisService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject CutiJenisRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject CutiJenisRequest request) {
        return CustomResult.page(service.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CutiJenisPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CutiJenisPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

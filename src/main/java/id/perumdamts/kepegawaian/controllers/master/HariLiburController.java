package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPostRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburPutRequest;
import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburRequest;
import id.perumdamts.kepegawaian.services.master.hariLibur.HariLiburService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/master/hari-libur")
@RequiredArgsConstructor
public class HariLiburController {
    private final HariLiburService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject HariLiburRequest request) {
        return CustomResult.page(service.findPage(request));
    }
    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject HariLiburRequest request) {
        return CustomResult.page(service.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody HariLiburPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody HariLiburPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

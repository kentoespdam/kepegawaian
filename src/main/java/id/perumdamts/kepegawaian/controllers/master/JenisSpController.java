package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpRequest;
import id.perumdamts.kepegawaian.services.master.jenisSp.JenisSpService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-sp")
public class JenisSpController {
    private final JenisSpService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject JenisSpRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.findList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> store(@RequestBody JenisSpPostRequest request) {
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JenisSpPutRequest request) {
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }

}

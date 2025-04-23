package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPostRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPutRequest;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiRequest;
import id.perumdamts.kepegawaian.services.master.sanksi.SanksiService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/sanksi")
public class SanksiController {
    private final SanksiService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject SanksiRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.any(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> store(@RequestBody SanksiPostRequest request) {
        return CustomResult.any(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SanksiPutRequest request) {
        return CustomResult.any(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.any(service.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.services.master.apd.ApdCommandService;
import id.perumdamts.kepegawaian.services.master.apd.ApdQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApdController {
    private final ApdCommandService command;
    private final ApdQueryService query;

    @GetMapping("/master/apd/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping("/master/apd")
    public ResponseEntity<?> save(@RequestBody ApdPostRequest request) {
        return CustomResult.any(command.create(request));
    }

    @PutMapping("/master/apd/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ApdPostRequest request) {
        return CustomResult.any(command.update(id, request));
    }

    @DeleteMapping("/master/apd/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

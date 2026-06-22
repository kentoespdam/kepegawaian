package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.master.alatKerja.AlatKerjaPostRequest;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaCommandService;
import id.perumdamts.kepegawaian.services.master.alatKerja.AlatKerjaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AlatKerjaController {
    private final AlatKerjaCommandService command;
    private final AlatKerjaQueryService query;

    @GetMapping("/master/alat-kerja/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PostMapping("/master/alat-kerja")
    public ResponseEntity<?> save(@RequestBody AlatKerjaPostRequest request) {
        return CustomResult.any(command.create(request));
    }

    @PutMapping("/master/alat-kerja/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AlatKerjaPostRequest request) {
        return CustomResult.any(command.update(id, request));
    }

    @DeleteMapping("/master/alat-kerja/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        command.delete(id);
        return CustomResult.delete(true);
    }
}

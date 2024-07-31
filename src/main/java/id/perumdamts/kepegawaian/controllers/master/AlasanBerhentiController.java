package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPostRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiPutRequest;
import id.perumdamts.kepegawaian.dto.master.alasanBerhenti.AlasanBerhentiRequest;
import id.perumdamts.kepegawaian.services.master.alasanBerhenti.AlasanBerhentiService;
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
@RequestMapping("/master/alasan-berhenti")
public class AlasanBerhentiController {
    private final AlasanBerhentiService alasanBerhenti;

    @GetMapping
    public ResponseEntity<?> get(@ParameterObject AlasanBerhentiRequest request) {
        return CustomResult.any(alasanBerhenti.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(alasanBerhenti.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(alasanBerhenti.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody AlasanBerhentiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(alasanBerhenti.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> batch(@Valid @RequestBody List<AlasanBerhentiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(alasanBerhenti.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody AlasanBerhentiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(alasanBerhenti.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(alasanBerhenti.deleteById(id));
    }
}

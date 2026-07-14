package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.DeletedResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.master.apd.ApdPostRequest;
import id.perumdamts.kepegawaian.dto.master.apd.ApdQuery;
import id.perumdamts.kepegawaian.services.master.apd.ApdCommandService;
import id.perumdamts.kepegawaian.services.master.apd.ApdQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApdController {
    private final ApdCommandService command;
    private final ApdQueryService query;

    @GetMapping("/master/apd/{id}")
    public ResponseEntity<SingleResult<ApdQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/master/apd")
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody ApdPostRequest request) {
        return CustomResult.save(command.create(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/master/apd/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody ApdPostRequest request) {
        return CustomResult.save(command.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/master/apd/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

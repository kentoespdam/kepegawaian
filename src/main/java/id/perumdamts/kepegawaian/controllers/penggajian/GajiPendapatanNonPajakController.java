package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/pendapatan-non-pajak")
@RequiredArgsConstructor
public class GajiPendapatanNonPajakController {
    private final GajiPendapatanNonPajakCommandService commandService;
    private final GajiPendapatanNonPajakQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiPendapatanNonPajakIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject GajiPendapatanNonPajakIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiPendapatanNonPajakPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiPendapatanNonPajakPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/pendapatan-non-pajak")
@RequiredArgsConstructor
public class GajiPendapatanNonPajakController {
    private final GajiPendapatanNonPajakCommandService commandService;
    private final GajiPendapatanNonPajakQueryService queryService;

    @GetMapping
    public ResponseEntity<PageResult<Page<GajiPendapatanNonPajakResponse>>> index(@ParameterObject @Valid GajiPendapatanNonPajakIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiPendapatanNonPajakResponse>> list(@ParameterObject @Valid GajiPendapatanNonPajakIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiPendapatanNonPajakResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiPendapatanNonPajakPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiPendapatanNonPajakPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

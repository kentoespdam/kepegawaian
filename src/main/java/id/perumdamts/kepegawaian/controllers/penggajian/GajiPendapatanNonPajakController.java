package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakListRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/pendapatan-non-pajak")
@RequiredArgsConstructor
public class GajiPendapatanNonPajakController {
    private final GajiPendapatanNonPajakCommandService commandService;
    private final GajiPendapatanNonPajakQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiPendapatanNonPajakResponse>>> index(@ParameterObject @Valid GajiPendapatanNonPajakIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiPendapatanNonPajakResponse>> list(@ParameterObject @Valid GajiPendapatanNonPajakListRequest request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiPendapatanNonPajakResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiPendapatanNonPajakPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiPendapatanNonPajakPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

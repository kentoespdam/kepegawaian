package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.*;
import id.perumdamts.kepegawaian.services.penggajian.gajiKomponen.GajiKomponenCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiKomponen.GajiKomponenQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("penggajian/komponen")
@RequiredArgsConstructor
public class GajiKomponenController {
    private final GajiKomponenCommandService commandService;
    private final GajiKomponenQueryService queryService;

    @GetMapping("/{profilId}/kode")
    public ResponseEntity<ListResult<GajiKomponenMiniProjection>> listKode(@PathVariable Long profilId) {
        return CustomResult.list(queryService.findAllKode(profilId));
    }

    @GetMapping("/{profilId}/profil")
    public ResponseEntity<PageResult<Page<GajiKomponenResponse>>> index(@PathVariable Long profilId, @ParameterObject @Valid GajiKomponenIndexQuery request) {
        return CustomResult.page(queryService.findPage(profilId, request));
    }

    @GetMapping("/{profilId}/profil/urut")
    public ResponseEntity<SingleResult<Integer>> indexUrut(@PathVariable Long profilId) {
        return CustomResult.any(queryService.findLastUrut(profilId));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<SingleResult<GajiKomponenResponse>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody GajiKomponenPostRequest request) {
        return CustomResult.save(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiKomponenPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

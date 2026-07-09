package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import org.springframework.data.domain.Page;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/detail-dasar-gaji")
public class DetailDasarGajiController {
    private final DetailDasarGajiCommandService commandService;
    private final DetailDasarGajiQueryService queryService;

    @GetMapping
    public ResponseEntity<SingleResult<Page<DetailDasarGajiResponse>>> index(@ParameterObject @Valid DetailDasarGajiIndexQuery request) {
        return CustomResult.any(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<DetailDasarGajiResponse>> list(@ParameterObject @Valid DetailDasarGajiIndexQuery request) {
        return CustomResult.list(queryService.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<DetailDasarGajiResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @GetMapping("/{golonganId}/{masaKerja}")
    public ResponseEntity<SingleResult<DetailDasarGaji>> findByGolonganIdAndMasaKerja(@PathVariable Long golonganId, @PathVariable Integer masaKerja) {
        return CustomResult.any(commandService.findDetailDasarGajiByGolonganAndMasaKerja(golonganId, masaKerja));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody DetailDasarGajiPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<String>> batch(@Valid @RequestBody List<DetailDasarGajiPostRequest> requests) {
        return CustomResult.save(commandService.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody DetailDasarGajiPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

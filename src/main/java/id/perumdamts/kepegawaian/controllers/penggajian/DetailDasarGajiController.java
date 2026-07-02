package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiQueryService;
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
@RequestMapping("/penggajian/detail-dasar-gaji")
public class DetailDasarGajiController {
    private final DetailDasarGajiCommandService commandService;
    private final DetailDasarGajiQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject DetailDasarGajiIndexQuery request) {
        return CustomResult.any(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject DetailDasarGajiIndexQuery request) {
        return CustomResult.list(queryService.findList(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @GetMapping("/{golonganId}/{masaKerja}")
    public ResponseEntity<?> findByGolonganIdAndMasaKerja(@PathVariable Long golonganId, @PathVariable Integer masaKerja) {
        return CustomResult.any(commandService.findDetailDasarGajiByGolonganAndMasaKerja(golonganId, masaKerja));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody DetailDasarGajiPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.save(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public ResponseEntity<?> batch(@Valid @RequestBody List<DetailDasarGajiPostRequest> requests, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.saveBatch(requests));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody DetailDasarGajiPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        return CustomResult.delete(commandService.deleteById(id));
    }
}

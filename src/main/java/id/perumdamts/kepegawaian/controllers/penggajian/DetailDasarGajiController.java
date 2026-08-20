package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiNominal;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiCommandService;
import id.perumdamts.kepegawaian.services.penggajian.detailDasarGaji.DetailDasarGajiQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Penggajian — Detail Dasar Gaji")
@RestController
@RequiredArgsConstructor
@RequestMapping("/penggajian/detail-dasar-gaji")
public class DetailDasarGajiController {
    private final DetailDasarGajiCommandService command;
    private final DetailDasarGajiQueryService query;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<DetailDasarGajiResponse>>> index(@ParameterObject @Valid DetailDasarGajiIndexQuery request) {
        return CustomResult.page(query.pageQuery(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<DetailDasarGajiResponse>> list() {
        return CustomResult.list(query.listQuery());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "Detail data berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<DetailDasarGajiResponse>> findById(@PathVariable Long id) {
        return CustomResult.any(query.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @Operation(summary = "find by golongan id and masa kerja")
    @GetMapping("/{golonganId}/{masaKerja}")
    public ResponseEntity<SingleResult<DetailDasarGajiNominal>> findByGolonganIdAndMasaKerja(
            @PathVariable Long golonganId, @PathVariable Integer masaKerja) {
        return CustomResult.any(query.findNominalByGolonganAndMasaKerja(golonganId, masaKerja));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "Buat data baru")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody DetailDasarGajiPostRequest request) {
        DetailDasarGaji entity = command.create(request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "create batch")
    @PostMapping("/batch")
    public ResponseEntity<SavedResult<List<Long>>> createBatch(@Valid @RequestBody List<@Valid DetailDasarGajiPostRequest> requests) {
        List<DetailDasarGaji> entities = command.createBatch(requests);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS,
                entities.stream().map(DetailDasarGaji::getId).toList()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @Operation(summary = "Perbarui data")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody DetailDasarGajiPutRequest request) {
        DetailDasarGaji entity = command.update(id, request);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, entity.getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @Operation(summary = "Hapus data")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(command.delete(id));
    }
}

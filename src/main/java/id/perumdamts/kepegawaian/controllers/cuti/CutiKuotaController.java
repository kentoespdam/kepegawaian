package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.cuti.kuota.*;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaCommandService;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/kuota")
@RequiredArgsConstructor
public class CutiKuotaController {
    private final CutiKuotaQueryService queryService;
    private final CutiKuotaCommandService commandService;

    @GetMapping
    public ResponseEntity<SingleResult<CutiKuotaPegawaiResponse>> index(@Valid @ParameterObject CutiKuotaRequest request) {
        return CustomResult.any(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<CutiKuotaResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @GetMapping("/{pegawaiId}/{tahun}/sisa")
    public ResponseEntity<SingleResult<CutiKuotaSisa>> showByPegawai(@PathVariable Long pegawaiId, @PathVariable Integer tahun) {
        return CustomResult.any(queryService.findByPegawai(pegawaiId, tahun));
    }

    @GetMapping("/template")
    public ResponseEntity<?> template() {
        return commandService.exportTemplate();
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> store(@Valid @RequestBody CutiKuotaPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<SavedResult<String>> importData(@Valid @ModelAttribute CutiKuotaImportRequest request) {
        return CustomResult.save(commandService.importData(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody CutiKuotaPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaImportRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaRequest;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaService;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaTemplateBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/kuota")
@RequiredArgsConstructor
public class CutiKuotaController {
    private final CutiKuotaService service;
    private final CutiKuotaTemplateBuilder cutiKuotaTemplateBuilder;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject CutiKuotaRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/{pegawaiId}/{tahun}/sisa")
    public ResponseEntity<?> showByPegawai(@PathVariable Long pegawaiId, @PathVariable Integer tahun) {
        return CustomResult.any(service.findByPegawai(pegawaiId, tahun));
    }

    @GetMapping("/template")
    public ResponseEntity<?> template() {
        return cutiKuotaTemplateBuilder.build();
    }


    @PostMapping
    public ResponseEntity<?> store(@RequestBody CutiKuotaPostRequest request) {
        return CustomResult.save(service.save(request));
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<?> importData(@Valid @ModelAttribute CutiKuotaImportRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.importData(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CutiKuotaPutRequest request) {
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }

}

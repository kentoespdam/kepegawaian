package id.perumdamts.kepegawaian.controllers.cuti;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaImportRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaPutRequest;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaRequest;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaService;
import id.perumdamts.kepegawaian.services.cuti.kuota.CutiKuotaTemplateBuilder;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuti/kuota")
@RequiredArgsConstructor
public class CutiKuotaController {
    private final CutiKuotaService service;
    private final CutiKuotaTemplateBuilder cutiKuotaTemplateBuilder;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject CutiKuotaRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @GetMapping("/pegawai/{id}")
    public ResponseEntity<?> showByPegawai(@PathVariable Long id) {
        return CustomResult.list(service.findByPegawai(id));
    }

    @GetMapping("/template")
    public ResponseEntity<?> template() {
        return cutiKuotaTemplateBuilder.build();
    }


    @PostMapping
    public ResponseEntity<?> store(@RequestBody CutiKuotaPostRequest request) {
        return CustomResult.save(service.save(request));
    }

    @PostMapping("/import")
    public ResponseEntity<?> importData(@ModelAttribute CutiKuotaImportRequest request) {
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

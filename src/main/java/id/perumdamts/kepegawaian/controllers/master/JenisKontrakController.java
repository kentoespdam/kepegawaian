package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.services.master.jenisKontrak.JenisKontrakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-kontrak")
public class JenisKontrakController {
    private final JenisKontrakService service;

    @GetMapping
    public ResponseEntity<?> index() {
        return CustomResult.list(service.findAll());
    }
}

package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.services.master.jenisSk.JenisSkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-sk")
@SuppressWarnings("DuplicatedCode")
public class JenisSkController {
    private final JenisSkQueryService service;

    @GetMapping
    public ResponseEntity<?> index() {
        return CustomResult.list(service.findAll());
    }
}

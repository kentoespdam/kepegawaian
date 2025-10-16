package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/duk")
@RequiredArgsConstructor
public class LaporanDukController {
    private static final String BASE_PATH = "/duk";
    private final LaporanKepegawaianService service;

    @GetMapping()
    public ResponseEntity<?> lapDuk() {
        return CustomResult.any(service.getObject(UrlBuilder.build(BASE_PATH, "/")));
    }

    @GetMapping("/excel")
    public ResponseEntity<?> lapDukExcel() {
        return service.getExport(UrlBuilder.build(BASE_PATH, "excel"));
    }
}

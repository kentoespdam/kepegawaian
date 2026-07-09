package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/kontrak")
@RequiredArgsConstructor
public class LaporanKontrakController {
    private static final String BASE_PATH = "/kontrak";
    private final LaporanKepegawaianService service;

    @GetMapping
    public ResponseEntity<SingleResult<Object>> lapKontrak(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        return CustomResult.any(
                service.getObject(UrlBuilder.buildFilter(BASE_PATH, "/", filter)));
    }

    @GetMapping("/excel")
    public ResponseEntity<?> lapKontrakExcel(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        return service.getExport(UrlBuilder.buildFilter(BASE_PATH, "/excel", filter));
    }
}

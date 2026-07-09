package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/dnp")
@RequiredArgsConstructor
public class LaporanDnpController {
    private final String BASE_PATH = "/dnp";
    private final LaporanKepegawaianService service;

    @GetMapping()
    public ResponseEntity<SingleResult<Object>> lapDnp() {
        return CustomResult.any(service.getObject(UrlBuilder.build(BASE_PATH, "/")));
    }

    @GetMapping("/excel")
    public ResponseEntity<?> lapDnpExcel() {
        return service.getExport(UrlBuilder.build(BASE_PATH, "/excel"));
    }
}

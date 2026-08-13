package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/lepas_tanggungan_anak")
@RequiredArgsConstructor
@Slf4j
public class LaporanLtaController {
    private static final String BASE_PATH = "/lepas_tanggungan_anak";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping()
    public ResponseEntity<SingleResult<Object>> lapLta(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return CustomResult.any(service.getObject(UrlBuilder.buildFilter(BASE_PATH, "/", filter)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping("/count")
    public ResponseEntity<SingleResult<Object>> lapLtaCount(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return CustomResult.any(service.getObject(UrlBuilder.buildFilter(BASE_PATH, "/count", filter)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping("/excel")
    public ResponseEntity<?> lapLtaExcel(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return service.getExport(UrlBuilder.buildFilter(BASE_PATH, "/excel", filter));
    }
}

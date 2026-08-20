package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Laporan — Laporan Lta")
@RestController
@RequestMapping("/laporan/kepegawaian/lepas_tanggungan_anak")
@RequiredArgsConstructor
@Slf4j
public class LaporanLtaController {
    private static final String BASE_PATH = "/lepas_tanggungan_anak";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta")
    @GetMapping()
    public ResponseEntity<SingleResult<Object>> lapLta(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        var uri = UriComponentsBuilder.fromPath(BASE_PATH).path("/")
                .queryParam("filter", filter).toUriString();
        return CustomResult.any(service.getObject(uri));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta count")
    @GetMapping("/count")
    public ResponseEntity<SingleResult<Object>> lapLtaCount(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        var uri = UriComponentsBuilder.fromPath(BASE_PATH).path("/count")
                .queryParam("filter", filter).toUriString();
        return CustomResult.any(service.getObject(uri));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapLtaExcel(@RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        var uri = UriComponentsBuilder.fromPath(BASE_PATH).path("/excel")
                .queryParam("filter", filter).toUriString();
        return service.getExport(uri);
    }
}

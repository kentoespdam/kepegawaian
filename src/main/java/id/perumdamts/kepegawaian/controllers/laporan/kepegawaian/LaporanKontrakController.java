package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Laporan — Laporan Kontrak")
@RestController
@RequestMapping("/laporan/kepegawaian/kontrak")
@RequiredArgsConstructor
public class LaporanKontrakController {
    private static final String BASE_PATH = "/kontrak";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kontrak")
    @GetMapping
    public ResponseEntity<SingleResult<Object>> lapKontrak(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        var uri = UriComponentsBuilder.fromPath(BASE_PATH).path("/")
                .queryParam("filter", filter).toUriString();
        return CustomResult.any(service.getObject(uri));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kontrak excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapKontrakExcel(@RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        var uri = UriComponentsBuilder.fromPath(BASE_PATH).path("/excel")
                .queryParam("filter", filter).toUriString();
        return service.getExport(uri);
    }
}

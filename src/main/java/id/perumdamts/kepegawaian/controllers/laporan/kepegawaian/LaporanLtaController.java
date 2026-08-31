package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterLta;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaCountResponse;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.LtaResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LtaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Laporan — Laporan Lta")
@RestController
@RequestMapping("/laporan/kepegawaian/lepas_tanggungan_anak")
@RequiredArgsConstructor
public class LaporanLtaController {
    private final LtaService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta")
    @GetMapping()
    public ResponseEntity<SingleResult<List<LtaResponse>>> lapLta(
            @RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return CustomResult.any(service.fetch(filter));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta count")
    @GetMapping("/count")
    public ResponseEntity<SingleResult<LtaCountResponse>> lapLtaCount(
            @RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return CustomResult.any(service.count(filter));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap lta excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapLtaExcel(
            @RequestParam(required = false, defaultValue = "BULAN_INI") EFilterLta filter) {
        return ResponseEntity.ok(service.exportExcel(filter));
    }
}

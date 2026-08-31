package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKontrak;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KontrakResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.KontrakService;
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

@Tag(name = "Laporan — Laporan Kontrak")
@RestController
@RequestMapping("/laporan/kepegawaian/kontrak")
@RequiredArgsConstructor
public class LaporanKontrakController {
    private final KontrakService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kontrak")
    @GetMapping
    public ResponseEntity<SingleResult<List<KontrakResponse>>> lapKontrak(
            @RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        return CustomResult.any(service.fetch(filter));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kontrak excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapKontrakExcel(
            @RequestParam(required = false, defaultValue = "AKTIF") EFilterKontrak filter) {
        return ResponseEntity.ok(service.exportExcel(filter));
    }
}

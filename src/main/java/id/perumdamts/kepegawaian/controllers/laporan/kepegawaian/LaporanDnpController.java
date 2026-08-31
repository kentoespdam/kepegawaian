package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DnpResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.DnpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Laporan — Laporan Dnp")
@RestController
@RequestMapping("/laporan/kepegawaian/dnp")
@RequiredArgsConstructor
public class LaporanDnpController {
    private final DnpService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap dnp")
    @GetMapping()
    public ResponseEntity<SingleResult<List<DnpResponse>>> lapDnp() {
        return CustomResult.any(service.fetch());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap dnp excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapDnpExcel() {
        return ResponseEntity.ok(service.exportExcel());
    }
}

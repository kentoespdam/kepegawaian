package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EFilterKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.EJenisKenaikanBerkala;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.KenaikanBerkalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Laporan — Laporan Kenaikan Berkala")
@RestController
@RequestMapping("/laporan/kepegawaian/kenaikan_berkala")
@RequiredArgsConstructor
@Validated
public class LaporanKenaikanBerkalaController {
    private final KenaikanBerkalaService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kenaikan berkala")
    @GetMapping
    public ResponseEntity<SingleResult<List<KenaikanBerkalaResponse>>> lapKenaikanBerkala(
            @RequestParam(defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter,
            @RequestParam EJenisKenaikanBerkala jenisSk) {
        return CustomResult.any(service.fetch(filter, jenisSk));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kenaikan berkala count")
    @GetMapping("/count")
    public ResponseEntity<SingleResult<Long>> lapKenaikanBerkalaCount(
            @RequestParam(defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter,
            @RequestParam EJenisKenaikanBerkala jenisSk) {
        return CustomResult.any(service.count(filter, jenisSk));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap kenaikan berkala excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapKenaikanBerkalaExcel(
            @RequestParam(defaultValue = "BULAN_INI") EFilterKenaikanBerkala filter,
            @RequestParam EJenisKenaikanBerkala jenisSk) {
        var resource = service.exportExcel(filter, jenisSk);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lap_kenaikan_berkala.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

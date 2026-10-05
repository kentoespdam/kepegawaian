package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.DukResponse;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.DukService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Laporan — Laporan Duk")
@RestController
@RequestMapping("/laporan/kepegawaian/duk")
@RequiredArgsConstructor
public class LaporanDukController {
    private final DukService service;
    private final id.perumdamts.kepegawaian.services.laporan.kepegawaian.ReportJobService reportJobService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap duk")
    @GetMapping()
    public ResponseEntity<SingleResult<List<DukResponse>>> lapDuk() {
        return CustomResult.any(service.fetch());
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap duk excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapDukExcel() {
        var resource = service.exportExcel();
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lap_duk.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "Async export duk excel (Claim Order)")
    @org.springframework.web.bind.annotation.PostMapping("/export")
    public ResponseEntity<SingleResult<id.perumdamts.kepegawaian.dto.laporan.kepegawaian.ReportJobResponse>> exportDuk() {
        var res = reportJobService.submitJob("duk", "lap_duk_" + System.currentTimeMillis() + ".xlsx", () -> service.exportExcel().getByteArray());
        return CustomResult.any(res);
    }
}

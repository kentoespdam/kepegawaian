package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Laporan — Laporan Duk")
@RestController
@RequestMapping("/laporan/kepegawaian/duk")
@RequiredArgsConstructor
public class LaporanDukController {
    private static final String BASE_PATH = "/duk";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap duk")
    @GetMapping()
    public ResponseEntity<SingleResult<Object>> lapDuk() {
        return CustomResult.any(service.getObject(BASE_PATH + "/"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap duk excel")
    @GetMapping("/excel")
    public ResponseEntity<?> lapDukExcel() {
        return service.getExport(BASE_PATH + "/excel");
    }
}

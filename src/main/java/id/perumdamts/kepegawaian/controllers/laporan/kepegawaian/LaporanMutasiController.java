package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.MutasiResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisMutasi;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.MutasiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Laporan — Laporan Mutasi")
@RestController
@RequestMapping("/laporan/kepegawaian/mutasi")
@RequiredArgsConstructor
public class LaporanMutasiController {
    private final MutasiService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap mutasi")
    @GetMapping("/{from_date}/{to_date}")
    public ResponseEntity<SingleResult<List<MutasiResponse>>> lapMutasi(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from_date,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to_date,
            @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        return CustomResult.any(service.fetch(from_date, to_date, jenis_mutasi));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @Operation(summary = "lap mutasi excel")
    @GetMapping("/excel/{from_date}/{to_date}")
    public ResponseEntity<?> lapMutasiExcel(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from_date,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to_date,
            @RequestParam(required = false) EJenisMutasi jenis_mutasi) {
        var resource = service.exportExcel(from_date, to_date, jenis_mutasi);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lap_mutasi.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

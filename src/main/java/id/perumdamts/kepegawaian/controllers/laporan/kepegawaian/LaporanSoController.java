package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import id.perumdamts.kepegawaian.enums.AppPermission;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.SoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Laporan — Laporan So")
@RestController
@RequestMapping("/laporan/kepegawaian/so")
@RequiredArgsConstructor
public class LaporanSoController {
    private final SoService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('" + AppPermission.Authority.LAPORAN_READ + "') or hasAuthority('" + AppPermission.Authority.PEGAWAI_READ + "')")
    @Operation(summary = "lap so")
    @GetMapping
    public ResponseEntity<ListResult<SoResponse>> lapSo(
            @Parameter(description = "Maksimal level jabatan (opsional, default null/seluruh level hingga staf)", example = "7")
            @RequestParam(name = "maxLevel", required = false) Integer maxLevel,
            @Parameter(description = "Format output: 'flat' (d3-org-chart) atau 'tree'", example = "flat")
            @RequestParam(name = "format", defaultValue = "flat") String format
    ) {
        if ("tree".equalsIgnoreCase(format)) {
            return CustomResult.list(service.fetchTreeList(maxLevel));
        }
        return CustomResult.list(service.fetchFlat(maxLevel));
    }
}

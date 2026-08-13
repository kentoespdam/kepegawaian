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

@RestController
@RequestMapping("/laporan/kepegawaian/so")
@RequiredArgsConstructor
public class LaporanSoController {
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping()
    public ResponseEntity<SingleResult<Object>> lapSo() {
        return CustomResult.any(
                service.getObject("/so/"));
    }
}

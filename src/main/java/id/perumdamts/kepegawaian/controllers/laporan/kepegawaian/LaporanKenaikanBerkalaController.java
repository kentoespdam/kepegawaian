package id.perumdamts.kepegawaian.controllers.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.KenaikanBerkalaRequest;
import id.perumdamts.kepegawaian.helpers.UrlBuilder;
import id.perumdamts.kepegawaian.services.laporan.kepegawaian.LaporanKepegawaianService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/laporan/kepegawaian/kenaikan_berkala")
@RequiredArgsConstructor
@Validated
public class LaporanKenaikanBerkalaController {
    private static final String BASE_PATH = "/kenaikan_berkala";
    private final LaporanKepegawaianService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping
    public ResponseEntity<SingleResult<Object>> lapKenaikanBerkala(@ParameterObject KenaikanBerkalaRequest request) {
        return CustomResult.any(service.getObject(UrlBuilder.build(BASE_PATH, "/", request)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping("/count")
    public ResponseEntity<SingleResult<Object>> lapKenaikanBerkalaCount(@ParameterObject KenaikanBerkalaRequest request) {
        return CustomResult.any(service.getObject(UrlBuilder.build(BASE_PATH, "/count", request)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('LAPORAN:READ')")
    @GetMapping("/excel")
    public ResponseEntity<?> lapKenaikanBerkalaExcel(@ParameterObject KenaikanBerkalaRequest request) {
        return service.getExport(UrlBuilder.build(BASE_PATH, "/excel", request));
    }
}

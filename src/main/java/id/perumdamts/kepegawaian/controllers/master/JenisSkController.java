package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.services.master.jenisSk.JenisSkQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Jenis Sk")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/jenis-sk")
public class JenisSkController {
    private final JenisSkQueryService service;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<ListResult<EnumOption>> index() {
        return CustomResult.list(service.findAll());
    }
}

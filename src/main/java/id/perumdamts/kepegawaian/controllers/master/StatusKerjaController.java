package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.services.master.statusKerja.StatusKerjaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Status Kerja")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/status-kerja")
public class StatusKerjaController {
    private final StatusKerjaQueryService service;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping("/list")
    public ResponseEntity<ListResult<EnumOption>> index() {
        return CustomResult.list(service.findAll());
    }
}

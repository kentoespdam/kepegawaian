package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import id.perumdamts.kepegawaian.services.master.statusPegawai.StatusPegawaiQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Master Data — Status Pegawai")
@RestController
@RequiredArgsConstructor
@RequestMapping("/master/status-pegawai")
public class StatusPegawaiController {
    private final StatusPegawaiQueryService service;

    @Operation(summary = "List data dengan paginasi")
    @GetMapping("/list")
    public ResponseEntity<ListResult<StatusPegawaiResponse>> index() {
        return CustomResult.list(service.findAll());
    }
}

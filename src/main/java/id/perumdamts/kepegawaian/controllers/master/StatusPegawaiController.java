package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import id.perumdamts.kepegawaian.services.master.statusPegawai.StatusPegawaiQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/status-pegawai")
public class StatusPegawaiController {
    private final StatusPegawaiQueryService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<StatusPegawaiResponse>> index() {
        return CustomResult.list(service.findAll());
    }
}

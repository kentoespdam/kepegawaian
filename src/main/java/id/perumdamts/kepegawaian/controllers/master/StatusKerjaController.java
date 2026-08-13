package id.perumdamts.kepegawaian.controllers.master;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.EnumOption;
import id.perumdamts.kepegawaian.dto.commons.ListResult;
import id.perumdamts.kepegawaian.services.master.statusKerja.StatusKerjaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master/status-kerja")
public class StatusKerjaController {
    private final StatusKerjaQueryService service;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('MASTER:READ')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<EnumOption>> index() {
        return CustomResult.list(service.findAll());
    }
}

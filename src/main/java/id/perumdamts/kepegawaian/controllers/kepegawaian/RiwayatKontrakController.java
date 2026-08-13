package id.perumdamts.kepegawaian.controllers.kepegawaian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakQuery;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakRequest;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakCommandService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.RiwayatKontrakQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kepegawaian/riwayat/kontrak")
@RequiredArgsConstructor
public class RiwayatKontrakController {
    private final RiwayatKontrakCommandService commandService;
    private final RiwayatKontrakQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @GetMapping("/pegawai/{id}")
    public ResponseEntity<PageResult<Page<RiwayatKontrakQuery>>> index(@PathVariable Long id, @Valid @ParameterObject RiwayatKontrakRequest request) {
        request.setPegawaiId(id);
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<RiwayatKontrakQuery>> findById(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody RiwayatKontrakPostRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.save(request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody RiwayatKontrakPutRequest request) {
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, commandService.update(id, request).getId()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('KEPEGAWAIAN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk.GajiPotonganTkkCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk.GajiPotonganTkkQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/potongan-tkk")
@RequiredArgsConstructor
public class GajiPotonganTkkController {
    private final GajiPotonganTkkCommandService commandService;
    private final GajiPotonganTkkQueryService queryService;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping
    public ResponseEntity<PageResult<Page<GajiPotonganTkkResponse>>> index(@ParameterObject @Valid GajiPotonganTkkIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:READ')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiPotonganTkkResponse>> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PostMapping
    public ResponseEntity<SavedResult<Long>> save(@Valid @RequestBody GajiPotonganTkkPostRequest request) {
        return CustomResult.save(commandService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:WRITE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiPotonganTkkPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PENGGAJIAN:DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

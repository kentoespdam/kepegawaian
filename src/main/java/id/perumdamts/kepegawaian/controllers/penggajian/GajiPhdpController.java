package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiPhdp.GajiPhdpCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPhdp.GajiPhdpQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/phdp")
@RequiredArgsConstructor
public class GajiPhdpController {
    private final GajiPhdpCommandService commandService;
    private final GajiPhdpQueryService queryService;

    @GetMapping
    public ResponseEntity<PageResult<Page<GajiPhdpResponse>>> index(@ParameterObject @Valid GajiPhdpIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiPhdpResponse>> list() {
        return CustomResult.list(queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiPhdpResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiPhdpPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiPhdpPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

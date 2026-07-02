package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiPhdp.GajiPhdpCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPhdp.GajiPhdpQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/phdp")
@RequiredArgsConstructor
public class GajiPhdpController {
    private final GajiPhdpCommandService commandService;
    private final GajiPhdpQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiPhdpIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(queryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiPhdpPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiPhdpPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

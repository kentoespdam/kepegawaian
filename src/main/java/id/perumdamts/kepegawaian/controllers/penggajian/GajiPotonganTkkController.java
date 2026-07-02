package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk.GajiPotonganTkkCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiPotonganTkk.GajiPotonganTkkQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/potongan-tkk")
@RequiredArgsConstructor
public class GajiPotonganTkkController {
    private final GajiPotonganTkkCommandService commandService;
    private final GajiPotonganTkkQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiPotonganTkkIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody GajiPotonganTkkPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiPotonganTkkPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

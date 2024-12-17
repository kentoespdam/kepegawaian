package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingRequest;
import id.perumdamts.kepegawaian.services.penggajian.GajiParameterSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/parameter-setting")
@RequiredArgsConstructor
public class GajiParameterSettingController {
    private final GajiParameterSettingService service;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject GajiParameterSettingRequest request) {
        return CustomResult.page(service.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list() {
        return CustomResult.list(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiParameterSettingPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiParameterSettingPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(service.delete(id));
    }
}

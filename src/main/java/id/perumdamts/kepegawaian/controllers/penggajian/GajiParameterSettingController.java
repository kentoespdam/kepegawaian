package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting.GajiParameterSettingCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting.GajiParameterSettingQueryService;
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
    private final GajiParameterSettingCommandService commandService;
    private final GajiParameterSettingQueryService queryService;

    @GetMapping
    public ResponseEntity<?> index(@ParameterObject @Valid GajiParameterSettingIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(@ParameterObject @Valid GajiParameterSettingIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody GajiParameterSettingPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody GajiParameterSettingPutRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

package id.perumdamts.kepegawaian.controllers.penggajian;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting.GajiParameterSettingCommandService;
import id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting.GajiParameterSettingQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/penggajian/parameter-setting")
@RequiredArgsConstructor
public class GajiParameterSettingController {
    private final GajiParameterSettingCommandService commandService;
    private final GajiParameterSettingQueryService queryService;

    @GetMapping
    public ResponseEntity<PageResult<Page<GajiParameterSettingResponse>>> index(@ParameterObject @Valid GajiParameterSettingIndexQuery request) {
        return CustomResult.page(queryService.findPage(request));
    }

    @GetMapping("/list")
    public ResponseEntity<ListResult<GajiParameterSettingResponse>> list(@ParameterObject @Valid GajiParameterSettingIndexQuery request) {
        return CustomResult.list(queryService.findAll(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<GajiParameterSettingResponse>> show(@PathVariable Long id) {
        return CustomResult.any(queryService.findById(id).orElse(null));
    }

    @PostMapping
    public ResponseEntity<SavedResult<Long>> create(@Valid @RequestBody GajiParameterSettingPostRequest request) {
        return CustomResult.save(commandService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<Long>> update(@PathVariable Long id, @Valid @RequestBody GajiParameterSettingPutRequest request) {
        return CustomResult.save(commandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> delete(@PathVariable Long id) {
        return CustomResult.delete(commandService.delete(id));
    }
}

package id.perumdamts.kepegawaian.services.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GajiParameterSettingService {
    Page<GajiParameterSettingResponse> findPage(GajiParameterSettingRequest request);

    List<GajiParameterSettingResponse> findAll();

    GajiParameterSettingResponse findById(Long id);

    SavedStatus<?> save(GajiParameterSettingPostRequest request);

    SavedStatus<?> update(Long id, GajiParameterSettingPutRequest request);

    Boolean delete(Long id);
}

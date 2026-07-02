package id.perumdamts.kepegawaian.mapper.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;

public final class GajiParameterSettingMapper {
    private GajiParameterSettingMapper() {}

    public static GajiParameterSetting toEntity(GajiParameterSettingPostRequest request) {
        GajiParameterSetting entity = new GajiParameterSetting();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        return entity;
    }

    public static void updateEntity(GajiParameterSetting entity, GajiParameterSettingPutRequest request) {
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
    }
}

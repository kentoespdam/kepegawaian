package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;

public class GajiParameterSettingPutRequest extends GajiParameterSettingPostRequest {
    public static GajiParameterSetting toEntity(GajiParameterSetting entity, GajiParameterSettingPutRequest request) {
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        return entity;
    }
}

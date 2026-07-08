package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;

public record GajiParameterSettingResponse(
        Long id,
        String kode,
        Double nominal
) {
    public static GajiParameterSettingResponse from(GajiParameterSetting entity) {
        return new GajiParameterSettingResponse(entity.getId(), entity.getKode(), entity.getNominal());
    }
}

package id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import lombok.Data;

@Data
public class GajiParameterSettingResponse {
    private Long id;
    private String kode;
    private Double nominal;

    public static GajiParameterSettingResponse from(GajiParameterSetting entity) {
        GajiParameterSettingResponse response = new GajiParameterSettingResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        response.setNominal(entity.getNominal());
        return response;
    }
}

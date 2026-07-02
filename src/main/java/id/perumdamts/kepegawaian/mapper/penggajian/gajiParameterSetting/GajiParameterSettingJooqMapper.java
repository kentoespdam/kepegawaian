package id.perumdamts.kepegawaian.mapper.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiParameterSetting.GAJI_PARAMETER_SETTING;

public final class GajiParameterSettingJooqMapper {
    private GajiParameterSettingJooqMapper() {}

    public static GajiParameterSettingResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiParameterSettingResponse response = new GajiParameterSettingResponse();
        response.setId(record.get(GAJI_PARAMETER_SETTING.ID));
        response.setKode(record.get(GAJI_PARAMETER_SETTING.KODE));
        response.setNominal(record.get(GAJI_PARAMETER_SETTING.NOMINAL));
        return response;
    }
}

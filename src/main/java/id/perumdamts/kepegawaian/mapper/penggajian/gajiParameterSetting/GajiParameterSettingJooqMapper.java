package id.perumdamts.kepegawaian.mapper.penggajian.gajiParameterSetting;

import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiParameterSetting.GAJI_PARAMETER_SETTING;

public final class GajiParameterSettingJooqMapper {
    private GajiParameterSettingJooqMapper() {}

    public static GajiParameterSettingResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new GajiParameterSettingResponse(
                record.get(GAJI_PARAMETER_SETTING.ID),
                record.get(GAJI_PARAMETER_SETTING.KODE),
                record.get(GAJI_PARAMETER_SETTING.NOMINAL)
        );
    }
}

package id.perumdamts.kepegawaian.mapper.penggajian.gajiKpi;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiKpi.GAJI_KPI;

public final class GajiKpiJooqMapper {
    private GajiKpiJooqMapper() {}

    public static GajiKpiResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new GajiKpiResponse(
                record.get(GAJI_KPI.ID),
                record.get(GAJI_KPI.NIPAM),
                record.get(GAJI_KPI.PERIODE),
                record.get(GAJI_KPI.TUNKIN),
                record.get(GAJI_KPI.PPH21_TER)
        );
    }
}

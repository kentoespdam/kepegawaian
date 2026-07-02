package id.perumdamts.kepegawaian.mapper.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiPhdp.GAJI_PHDP;

public final class GajiPhdpJooqMapper {
    private GajiPhdpJooqMapper() {}

    public static GajiPhdpResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiPhdpResponse response = new GajiPhdpResponse();
        response.setId(record.get(GAJI_PHDP.ID));
        response.setUrut(record.get(GAJI_PHDP.URUT));
        response.setKondisi(record.get(GAJI_PHDP.KONDISI));
        response.setFormula(record.get(GAJI_PHDP.FORMULA));
        return response;
    }
}

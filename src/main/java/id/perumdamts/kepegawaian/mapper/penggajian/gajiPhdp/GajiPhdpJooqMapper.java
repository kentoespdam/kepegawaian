package id.perumdamts.kepegawaian.mapper.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiPhdp.GAJI_PHDP;

public final class GajiPhdpJooqMapper {
    private GajiPhdpJooqMapper() {}

    public static GajiPhdpResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new GajiPhdpResponse(
                record.get(GAJI_PHDP.ID),
                record.get(GAJI_PHDP.URUT),
                record.get(GAJI_PHDP.KONDISI),
                record.get(GAJI_PHDP.FORMULA)
        );
    }
}

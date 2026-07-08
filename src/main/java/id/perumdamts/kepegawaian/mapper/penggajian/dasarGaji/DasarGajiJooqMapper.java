package id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji;

import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.DasarGaji.DASAR_GAJI;

public final class DasarGajiJooqMapper {
    private DasarGajiJooqMapper() {}

    public static DasarGajiResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new DasarGajiResponse(
                record.get(DASAR_GAJI.ID),
                record.get(DASAR_GAJI.DESKRIPSI),
                record.get(DASAR_GAJI.TANGGAL_AWAL),
                record.get(DASAR_GAJI.TANGGAL_AKHIR),
                record.get(DASAR_GAJI.AKTIF)
        );
    }
}

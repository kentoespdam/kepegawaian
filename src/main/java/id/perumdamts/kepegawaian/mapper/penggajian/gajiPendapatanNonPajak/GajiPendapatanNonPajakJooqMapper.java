package id.perumdamts.kepegawaian.mapper.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;

public final class GajiPendapatanNonPajakJooqMapper {
    private GajiPendapatanNonPajakJooqMapper() {}

    public static GajiPendapatanNonPajakResponse mapToResponse(Record record) {
        if (record == null) return null;
        return new GajiPendapatanNonPajakResponse(
                record.get(GAJI_PENDAPATAN_NON_PAJAK.ID),
                record.get(GAJI_PENDAPATAN_NON_PAJAK.KODE),
                record.get(GAJI_PENDAPATAN_NON_PAJAK.NOMINAL),
                record.get(GAJI_PENDAPATAN_NON_PAJAK.NOTES)
        );
    }
}

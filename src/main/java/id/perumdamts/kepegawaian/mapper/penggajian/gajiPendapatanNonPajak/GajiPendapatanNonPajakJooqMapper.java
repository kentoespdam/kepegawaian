package id.perumdamts.kepegawaian.mapper.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;

public final class GajiPendapatanNonPajakJooqMapper {
    private GajiPendapatanNonPajakJooqMapper() {}

    public static GajiPendapatanNonPajakResponse mapToResponse(Record record) {
        if (record == null) return null;
        GajiPendapatanNonPajakResponse response = new GajiPendapatanNonPajakResponse();
        response.setId(record.get(GAJI_PENDAPATAN_NON_PAJAK.ID));
        response.setKode(record.get(GAJI_PENDAPATAN_NON_PAJAK.KODE));
        response.setNominal(record.get(GAJI_PENDAPATAN_NON_PAJAK.NOMINAL));
        response.setNotes(record.get(GAJI_PENDAPATAN_NON_PAJAK.NOTES));
        return response;
    }
}

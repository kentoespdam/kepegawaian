package id.perumdamts.kepegawaian.mapper.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;

public final class GajiPendapatanNonPajakMapper {
    private GajiPendapatanNonPajakMapper() {}

    public static GajiPendapatanNonPajak toEntity(GajiPendapatanNonPajakPostRequest request) {
        GajiPendapatanNonPajak entity = new GajiPendapatanNonPajak();
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        entity.setNotes(request.getNotes());
        return entity;
    }

    public static void updateEntity(GajiPendapatanNonPajak entity, GajiPendapatanNonPajakPutRequest request) {
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        entity.setNotes(request.getNotes());
    }
}

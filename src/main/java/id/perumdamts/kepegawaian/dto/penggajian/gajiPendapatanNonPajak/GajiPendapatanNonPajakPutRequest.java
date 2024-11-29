package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;

public class GajiPendapatanNonPajakPutRequest extends GajiPendapatanNonPajakPostRequest {
    public static GajiPendapatanNonPajak toEntity(GajiPendapatanNonPajak entity, GajiPendapatanNonPajakPutRequest request) {
        entity.setKode(request.getKode());
        entity.setNominal(request.getNominal());
        entity.setNotes(request.getNotes());
        return entity;
    }
}

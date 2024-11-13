package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import lombok.Data;

@Data
public class GajiPendapatanNonPajakResponse {
    private Long id;
    private String kode;
    private Double nominal;

    public static GajiPendapatanNonPajakResponse from(GajiPendapatanNonPajak entity) {
        GajiPendapatanNonPajakResponse response = new GajiPendapatanNonPajakResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        response.setNominal(entity.getNominal());
        return response;
    }
}
package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import lombok.Data;

import java.util.Objects;

@Data
public class GajiPendapatanNonPajakResponse {
    private Long id;
    private String kode;
    private Double nominal;
    private String notes;

    public static GajiPendapatanNonPajakResponse from(GajiPendapatanNonPajak entity) {
        if (Objects.isNull(entity)) return null;
        GajiPendapatanNonPajakResponse response = new GajiPendapatanNonPajakResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        response.setNominal(entity.getNominal());
        response.setNotes(entity.getNotes());
        return response;
    }
}

package id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;

import java.util.Objects;

public record GajiPendapatanNonPajakResponse(
        Long id,
        String kode,
        Double nominal,
        String notes
) {
    public static GajiPendapatanNonPajakResponse from(GajiPendapatanNonPajak entity) {
        if (Objects.isNull(entity)) return null;
        return new GajiPendapatanNonPajakResponse(
                entity.getId(),
                entity.getKode(),
                entity.getNominal(),
                entity.getNotes()
        );
    }
}

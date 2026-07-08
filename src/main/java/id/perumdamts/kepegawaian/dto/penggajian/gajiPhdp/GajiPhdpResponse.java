package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;

public record GajiPhdpResponse(
        Long id,
        Integer urut,
        String kondisi,
        String formula
) {
    public static GajiPhdpResponse from(GajiPhdp entity) {
        return new GajiPhdpResponse(entity.getId(), entity.getUrut(), entity.getKondisi(), entity.getFormula());
    }
}

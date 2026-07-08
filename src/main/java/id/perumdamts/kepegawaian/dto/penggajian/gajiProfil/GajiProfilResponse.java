package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

public record GajiProfilResponse(
        Long id,
        String nama
) {
    public static GajiProfilResponse from(GajiProfil entity) {
        return new GajiProfilResponse(entity.getId(), entity.getNama());
    }
}

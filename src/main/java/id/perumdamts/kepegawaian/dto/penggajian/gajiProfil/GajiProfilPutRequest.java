package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

public class GajiProfilPutRequest extends GajiProfilPostRequest {
    public static GajiProfil toEntity(GajiProfil entity, GajiProfilPostRequest request) {
        entity.setNama(request.getNama());
        return entity;
    }
}

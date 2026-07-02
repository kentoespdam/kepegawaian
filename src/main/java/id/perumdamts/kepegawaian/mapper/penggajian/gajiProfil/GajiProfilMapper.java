package id.perumdamts.kepegawaian.mapper.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;

public final class GajiProfilMapper {
    private GajiProfilMapper() {}

    public static GajiProfil toEntity(GajiProfilPostRequest request) {
        GajiProfil entity = new GajiProfil();
        entity.setNama(request.getNama());
        return entity;
    }

    public static void updateEntity(GajiProfil entity, GajiProfilPutRequest request) {
        entity.setNama(request.getNama());
    }
}

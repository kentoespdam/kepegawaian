package id.perumdamts.kepegawaian.dto.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import lombok.Data;

@Data
public class GajiProfilResponse {
    private Long id;
    private String nama;

    public static GajiProfilResponse from(GajiProfil entity) {
        GajiProfilResponse response = new GajiProfilResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }
}

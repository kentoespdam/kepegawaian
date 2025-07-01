package id.perumdamts.kepegawaian.dto.cuti.jenis;

import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import lombok.Data;

@Data
public class JenisCutiMiniResponse {
    private Long id;
    private String nama;

    public static JenisCutiMiniResponse from(CutiJenis entity) {
        JenisCutiMiniResponse response = new JenisCutiMiniResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }
}

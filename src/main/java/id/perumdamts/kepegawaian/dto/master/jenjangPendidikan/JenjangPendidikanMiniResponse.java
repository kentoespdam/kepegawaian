package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import lombok.Data;

@Data
public class JenjangPendidikanMiniResponse {
    private Long id;
    private String nama;

    public static JenjangPendidikanMiniResponse from(JenjangPendidikan entity) {
        JenjangPendidikanMiniResponse response = new JenjangPendidikanMiniResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }
}

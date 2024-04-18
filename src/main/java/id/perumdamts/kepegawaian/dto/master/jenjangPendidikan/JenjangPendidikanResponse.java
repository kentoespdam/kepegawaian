package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import lombok.Data;

import java.util.List;

@Data
public class JenjangPendidikanResponse {
    private Long id;
    private String nama;
    private Integer seq;

    public static JenjangPendidikanResponse from(JenjangPendidikan entity) {
        JenjangPendidikanResponse response = new JenjangPendidikanResponse();
        response.setId(entity.getId());
        response.setSeq(entity.getSeq());
        response.setNama(entity.getNama());
        return response;
    }

    public static List<JenjangPendidikanResponse> from(List<JenjangPendidikan> entities) {
        return entities.stream().map(JenjangPendidikanResponse::from).toList();
    }
}

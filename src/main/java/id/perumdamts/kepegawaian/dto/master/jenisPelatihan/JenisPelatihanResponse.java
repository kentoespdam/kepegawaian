package id.perumdamts.kepegawaian.dto.master.jenisPelatihan;

import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import lombok.Data;

import java.util.List;

@Data
public class JenisPelatihanResponse {
    private Long id;
    private String nama;

    public static JenisPelatihanResponse from(JenisPelatihan entity) {
        JenisPelatihanResponse response = new JenisPelatihanResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }

    public static List<JenisPelatihanResponse> from(List<JenisPelatihan> entities) {
        return entities.stream().map(JenisPelatihanResponse::from).toList();
    }
}

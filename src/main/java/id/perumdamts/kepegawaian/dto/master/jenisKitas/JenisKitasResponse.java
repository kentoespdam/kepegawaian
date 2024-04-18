package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import lombok.Data;

import java.util.List;

@Data
public class JenisKitasResponse {
    private Long id;
    private String nama;

    public static JenisKitasResponse from(JenisKitas entity) {
        JenisKitasResponse response = new JenisKitasResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }

    public static List<JenisKitasResponse> from(List<JenisKitas> entities) {
        return entities.stream().map(JenisKitasResponse::from).toList();
    }
}

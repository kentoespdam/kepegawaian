package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import lombok.Data;

import java.util.List;

@Data
public class JenisKeahlianResponse {
    private Long id;
    private String nama;

    public static JenisKeahlianResponse from(JenisKeahlian entity) {
        JenisKeahlianResponse response = new JenisKeahlianResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }

    public static List<JenisKeahlianResponse> from(List<JenisKeahlian> entities) {
        return entities.stream().map(JenisKeahlianResponse::from).toList();
    }
}

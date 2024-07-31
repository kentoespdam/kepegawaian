package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JenisSpResponse {
    private Long id;
    private String nama;
    private String notes;

    public static JenisSpResponse from(JenisSp entity) {
        JenisSpResponse response = new JenisSpResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }
}

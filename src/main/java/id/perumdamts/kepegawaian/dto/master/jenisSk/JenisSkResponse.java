package id.perumdamts.kepegawaian.dto.master.jenisSk;

import id.perumdamts.kepegawaian.entities.master.JenisSk;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JenisSkResponse {
    private Long id;
    private String nama;
    private String notes;

    public static JenisSkResponse from(JenisSk entity) {
        JenisSkResponse response = new JenisSkResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }
}

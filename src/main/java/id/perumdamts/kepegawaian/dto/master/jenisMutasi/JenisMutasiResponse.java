package id.perumdamts.kepegawaian.dto.master.jenisMutasi;

import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class JenisMutasiResponse {
    private Long id;
    private String nama;
    private String notes;

    public static JenisMutasiResponse from(JenisMutasi entity) {
        JenisMutasiResponse response = new JenisMutasiResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setNotes(entity.getNotes());
        return response;
    }
}

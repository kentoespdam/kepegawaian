package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import lombok.Data;

@Data
public class RumahDinasResponse {
    private Long id;
    private String nama;
    private Double nilai;

    public static RumahDinasResponse from(RumahDinas entity) {
        RumahDinasResponse response = new RumahDinasResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setNilai(entity.getNilai());
        return response;
    }
}

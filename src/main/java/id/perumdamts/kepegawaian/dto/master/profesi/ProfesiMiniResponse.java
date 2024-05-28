package id.perumdamts.kepegawaian.dto.master.profesi;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfesiMiniResponse {
    private Long id;
    private String nama;

    public static ProfesiMiniResponse from(Profesi profesi) {
        ProfesiMiniResponse response = new ProfesiMiniResponse();
        response.setId(profesi.getId());
        response.setNama(profesi.getNama());
        return response;
    }
}

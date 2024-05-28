package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.Apd;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ApdResponse {
    private Long id;
    private String nama;
    private ProfesiMiniResponse profesi;

    public static ApdResponse from(Apd entity) {
        ApdResponse response = new ApdResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setProfesi(ProfesiMiniResponse.from(entity.getProfesi()));
        return response;
    }
}

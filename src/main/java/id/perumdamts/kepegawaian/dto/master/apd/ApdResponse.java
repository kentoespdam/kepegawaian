package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.Apd;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ApdResponse {
    private Long id;
    private String name;
    private ProfesiMiniResponse profesi;

    public static ApdResponse from(Apd entity) {
        ApdResponse response = new ApdResponse();
        response.setId(entity.getId());
        response.setName(entity.getNama());
        response.setProfesi(ProfesiMiniResponse.from(entity.getProfesi()));
        return response;
    }
}

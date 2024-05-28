package id.perumdamts.kepegawaian.dto.master.alatKerja;

import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AlatKerjaResponse {
    private Long id;
    private String nama;
    private ProfesiMiniResponse profesi;

    public static AlatKerjaResponse from(AlatKerja entity) {
        AlatKerjaResponse response = new AlatKerjaResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        response.setProfesi(ProfesiMiniResponse.from(entity.getProfesi()));
        return response;
    }
}

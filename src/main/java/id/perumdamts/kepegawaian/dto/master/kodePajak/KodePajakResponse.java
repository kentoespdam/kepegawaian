package id.perumdamts.kepegawaian.dto.master.kodePajak;

import id.perumdamts.kepegawaian.entities.master.KodePajak;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class KodePajakResponse {
    private Long id;
    private String kode;
    private Double nominal;

    public static KodePajakResponse from(KodePajak entity) {
        KodePajakResponse response = new KodePajakResponse();
        response.setId(entity.getId());
        response.setKode(entity.getKode());
        response.setNominal(entity.getNominal());
        return response;
    }
}

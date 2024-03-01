package id.perumdamts.kepegawaian.dto.master.pangkat;

import id.perumdamts.kepegawaian.entities.master.Pangkat;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PangkatResponse {
    private Long id;
    private String nama;

    public static PangkatResponse from(Pangkat pangkat) {
        return new PangkatResponse(pangkat.getId(), pangkat.getNama());
    }
}

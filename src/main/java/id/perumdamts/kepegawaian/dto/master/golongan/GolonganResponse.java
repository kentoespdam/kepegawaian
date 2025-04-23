package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GolonganResponse {
    private Long id;
    private String golongan;
    private String pangkat;

    public static GolonganResponse from(Golongan golongan) {
        return golongan == null ? null : new GolonganResponse(golongan.getId(), golongan.getGolongan(), golongan.getPangkat());
    }
}

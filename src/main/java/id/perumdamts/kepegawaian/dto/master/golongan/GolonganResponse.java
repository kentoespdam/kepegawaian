package id.perumdamts.kepegawaian.dto.master.golongan;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GolonganResponse {
    private Long id;
    private String nama;

    public static GolonganResponse from(Golongan golongan) {
        return new GolonganResponse(golongan.getId(), golongan.getNama());
    }
}

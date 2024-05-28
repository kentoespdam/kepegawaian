package id.perumdamts.kepegawaian.dto.master.alatKerja;

import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class AlatKerjaMiniResponse {
    private Long id;
    private String nama;

    public static AlatKerjaMiniResponse from(AlatKerja entity) {
        AlatKerjaMiniResponse response = new AlatKerjaMiniResponse();
        response.setId(entity.getId());
        response.setNama(entity.getNama());
        return response;
    }

    public static List<AlatKerjaMiniResponse> from(List<AlatKerja> entities) {
        return entities.stream().map(AlatKerjaMiniResponse::from).toList();
    }
}

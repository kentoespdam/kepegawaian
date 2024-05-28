package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.entities.master.Apd;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ApdMiniResponse {
    private Long id;
    private String nama;

    public static ApdMiniResponse from(Apd apd) {
        ApdMiniResponse response = new ApdMiniResponse();
        response.setId(apd.getId());
        response.setNama(apd.getNama());
        return response;
    }

    public static List<ApdMiniResponse> from(List<Apd> list) {
        if (list.isEmpty()) return null;
        return list.stream().map(ApdMiniResponse::from).toList();
    }
}

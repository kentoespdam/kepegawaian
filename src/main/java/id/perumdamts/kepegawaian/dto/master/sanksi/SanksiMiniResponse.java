package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SanksiMiniResponse {
    private Long id;
    private String kode;
    private String keterangan;
    private Long jenisSpId;

    public static SanksiMiniResponse from(Sanksi sanksi) {
        Long jenisSpId = sanksi.getJenisSp() != null ? sanksi.getJenisSp().getId() : null;
        return new SanksiMiniResponse(sanksi.getId(), sanksi.getKode(), sanksi.getKeterangan(), jenisSpId);
    }
}

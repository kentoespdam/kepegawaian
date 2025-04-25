package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.Data;

@Data
public class SanksiMiniResponse {
    private Long id;
    private String kode;
    private String keterangan;
    private Long jenisSpId;

    public static SanksiMiniResponse from(Sanksi sanksi) {
        SanksiMiniResponse response = new SanksiMiniResponse();
        response.setId(sanksi.getId());
        response.setKode(sanksi.getKode());
        response.setKeterangan(sanksi.getKeterangan());
        if (sanksi.getJenisSp() != null)
            response.setJenisSpId(sanksi.getJenisSp().getId());
        return response;
    }
}

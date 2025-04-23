package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;

import java.util.List;

@Data
public class JenisSpMiniResponse {
    private Long id;
    private String kode;
    private String nama;
    private List<SanksiMiniResponse> sanksiSp;

    public static JenisSpMiniResponse from(JenisSp jenisSp) {
        JenisSpMiniResponse response = new JenisSpMiniResponse();
        response.setId(jenisSp.getId());
        response.setKode(jenisSp.getKode());
        response.setNama(jenisSp.getNama());
        response.setSanksiSp(jenisSp.getSanksiSp()
                .stream().map(SanksiMiniResponse::from)
                .toList());
        return response;
    }
}

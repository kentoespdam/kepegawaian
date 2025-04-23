package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JenisSpMiniResponse {
    private Long id;
    private String kode;
    private String nama;
    private Set<SanksiMiniResponse> sanksiSp;

    public static JenisSpMiniResponse from(JenisSp jenisSp) {
        JenisSpMiniResponse response = new JenisSpMiniResponse();
        response.setId(jenisSp.getId());
        response.setKode(jenisSp.getKode());
        response.setNama(jenisSp.getNama());
        response.setSanksiSp(jenisSp.getSanksiSp()
                .stream().map(SanksiMiniResponse::from)
                .collect(Collectors.toSet()));
        return response;
    }
}

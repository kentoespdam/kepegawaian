package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class JenisSpResponse {
    private Long id;
    private String kode;
    private String nama;
    private String deskripsi;
    private Integer masaBerlaku;
    private Set<SanksiResponse> sanksiSp;

    public static JenisSpResponse from(JenisSp jenisSp) {
        JenisSpResponse response = new JenisSpResponse();
        response.setId(jenisSp.getId());
        response.setKode(jenisSp.getKode());
        response.setNama(jenisSp.getNama());
        response.setSanksiSp(jenisSp.getSanksiSp()
                .stream().map(SanksiResponse::from)
                .collect(Collectors.toSet())
        );
        return response;
    }
}

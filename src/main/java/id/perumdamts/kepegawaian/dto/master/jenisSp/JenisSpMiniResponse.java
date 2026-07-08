package id.perumdamts.kepegawaian.dto.master.jenisSp;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiMiniResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;

import java.util.List;

public record JenisSpMiniResponse(Long id, String kode, String nama, List<SanksiMiniResponse> sanksiSp) {
    public static JenisSpMiniResponse from(JenisSp jenisSp) {
        if (jenisSp == null) return null;
        return new JenisSpMiniResponse(
                jenisSp.getId(),
                jenisSp.getKode(),
                jenisSp.getNama(),
                jenisSp.getSanksiSp()
                        .stream().map(SanksiMiniResponse::from)
                        .toList()
        );
    }
}

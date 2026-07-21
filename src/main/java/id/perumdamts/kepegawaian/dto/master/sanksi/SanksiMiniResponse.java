package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.Sanksi;

public record SanksiMiniResponse(
        Long id,
        String kode,
        String keterangan,
        Long jenisSpId
) {
    public static SanksiMiniResponse from(Sanksi sanksi) {
        Long jenisSpId = sanksi.getJenisSp() != null ? sanksi.getJenisSp().getId() : null;
        return new SanksiMiniResponse(sanksi.getId(), sanksi.getKode(), sanksi.getKeterangan(), jenisSpId);
    }

}

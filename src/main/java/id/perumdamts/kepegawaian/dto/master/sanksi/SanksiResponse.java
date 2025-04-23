package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.Data;

@Data
public class SanksiResponse {
    private Long id;
    private String kode;
    private String keterangan;
    private Boolean potTkk;
    private Integer jmlPotTkk;
    private Boolean isPendingPangkat;
    private Boolean isPendingGaji;
    private Boolean isTurunPangkat;
    private Boolean isTurunJabatan;
    private Boolean isSuspension;
    private Boolean isTerminateDh;
    private Boolean isTerminateTh;

    public static SanksiResponse from(Sanksi sanksi) {
        SanksiResponse response = new SanksiResponse();
        response.setId(sanksi.getId());
        response.setKode(sanksi.getKode());
        response.setKeterangan(sanksi.getKeterangan());
        response.setPotTkk(sanksi.getPotTkk());
        response.setJmlPotTkk(sanksi.getJmlPotTkk());
        response.setIsPendingPangkat(sanksi.getIsPendingPangkat());
        response.setIsPendingGaji(sanksi.getIsPendingGaji());
        response.setIsTurunPangkat(sanksi.getIsTurunPangkat());
        response.setIsTurunJabatan(sanksi.getIsTurunJabatan());
        response.setIsSuspension(sanksi.getIsSuspension());
        response.setIsTerminateDh(sanksi.getIsTerminateDh());
        response.setIsTerminateTh(sanksi.getIsTerminateTh());
        return response;
    }
}

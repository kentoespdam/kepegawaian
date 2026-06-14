package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiPostRequest;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;

import java.util.Objects;

public final class SanksiMapper {
    private SanksiMapper() {}

    public static Sanksi toEntity(SanksiPostRequest request, JenisSp jenisSp) {
        Sanksi entity = new Sanksi();
        entity.setKode(request.getKode());
        entity.setKeterangan(request.getKeterangan());
        if (Objects.nonNull(jenisSp))
            entity.setJenisSp(jenisSp);
        entity.setPotTkk(request.getPotTkk());
        entity.setJmlPotTkk(request.getJmlPotTkk());
        entity.setIsPendingPangkat(request.getIsPendingPangkat());
        entity.setIsPendingGaji(request.getIsPendingGaji());
        entity.setIsTurunPangkat(request.getIsTurunPangkat());
        entity.setIsTurunJabatan(request.getIsTurunJabatan());
        entity.setIsSuspension(request.getIsSuspension());
        entity.setIsTerminateDh(request.getIsTerminateDh());
        entity.setIsTerminateTh(request.getIsTerminateTh());
        return entity;
    }

    public static void updateEntity(Sanksi entity, SanksiPostRequest request, JenisSp jenisSp) {
        entity.setKode(request.getKode());
        entity.setKeterangan(request.getKeterangan());
        if (jenisSp != null)
            entity.setJenisSp(jenisSp);
        entity.setPotTkk(request.getPotTkk());
        entity.setJmlPotTkk(request.getJmlPotTkk());
        entity.setIsPendingPangkat(request.getIsPendingPangkat());
        entity.setIsPendingGaji(request.getIsPendingGaji());
        entity.setIsTurunPangkat(request.getIsTurunPangkat());
        entity.setIsTurunJabatan(request.getIsTurunJabatan());
        entity.setIsSuspension(request.getIsSuspension());
        entity.setIsTerminateDh(request.getIsTerminateDh());
        entity.setIsTerminateTh(request.getIsTerminateTh());
    }
}

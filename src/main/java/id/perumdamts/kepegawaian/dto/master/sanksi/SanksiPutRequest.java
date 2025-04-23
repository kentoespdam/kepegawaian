package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SanksiPutRequest extends SanksiPostRequest {
    public static Sanksi toEntity(Sanksi entity, SanksiPutRequest request, JenisSp jenisSp) {
        entity.setKode(request.getKode());
        entity.setKeterangan(request.getNama());
        entity.setJenisSp(jenisSp);
        entity.setPotTkk(request.getPotTkk());
        entity.setJmlPotTkk(request.getJmlPotTkk());
        entity.setIsPendingPangkat(request.getIsPendingPangkat());
        entity.setIsPendingGaji(request.getIsPendingGaji());
        entity.setIsTurunPangkat(request.getIsTurunPangkat());
        entity.setIsTurunJabatan(request.getIsTurunJabatan());
        entity.setIsTerminateDh(request.getIsTerminateDh());
        entity.setIsTerminateTh(request.getIsTerminateTh());
        return entity;
    }
}

package id.perumdamts.kepegawaian.dto.master.sanksi;

import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.entities.master.Sanksi;
import lombok.Data;

@Data
public class PatchSanksiJenisSpRequest {
    private Long id;
    private Long jenisSpId;

    public static Sanksi toEntity(Sanksi entity, JenisSp jenisSp) {
        entity.setJenisSp(jenisSp);
        return entity;
    }
}

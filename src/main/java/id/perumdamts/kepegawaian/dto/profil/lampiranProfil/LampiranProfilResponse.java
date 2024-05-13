package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import lombok.Data;

@Data
public class LampiranProfilResponse {
    private Long id;
    private EJenisLampiranProfil ref;
    private Long ref_id;
    private String fileName;

    public static LampiranProfilResponse from(LampiranProfil entity) {
        LampiranProfilResponse response = new LampiranProfilResponse();
        response.setId(entity.getId());
        response.setRef(entity.getRef());
        response.setRef_id(entity.getRefId());
        response.setFileName(entity.getFileName());
        return response;
    }
}

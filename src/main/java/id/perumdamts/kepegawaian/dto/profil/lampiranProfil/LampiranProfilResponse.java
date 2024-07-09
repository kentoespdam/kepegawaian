package id.perumdamts.kepegawaian.dto.profil.lampiranProfil;

import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.profil.LampiranProfil;
import lombok.Data;

@Data
public class LampiranProfilResponse {
    private Long id;
    private EJenisLampiranProfil ref;
    private Long refId;
    private String fileName;
    private String notes;

    public static LampiranProfilResponse from(LampiranProfil entity) {
        LampiranProfilResponse response = new LampiranProfilResponse();
        response.setId(entity.getId());
        response.setRef(entity.getRef());
        response.setRefId(entity.getRefId());
        response.setFileName(entity.getFileName());
        response.setNotes(entity.getNotes());
        return response;
    }
}

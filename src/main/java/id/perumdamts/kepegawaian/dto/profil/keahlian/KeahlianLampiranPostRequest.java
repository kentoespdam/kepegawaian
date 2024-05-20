package id.perumdamts.kepegawaian.dto.profil.keahlian;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeahlianLampiranPostRequest extends LampiranProfilPostRequest {
    private EJenisLampiranProfil ref = EJenisLampiranProfil.PROFIL_KEAHLIAN;
}

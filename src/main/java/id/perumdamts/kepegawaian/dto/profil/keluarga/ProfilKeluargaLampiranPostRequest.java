package id.perumdamts.kepegawaian.dto.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfilKeluargaLampiranPostRequest extends LampiranProfilPostRequest {
    private EJenisLampiranProfil ref = EJenisLampiranProfil.PROFIL_KELUARGA;
}

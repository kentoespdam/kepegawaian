package id.perumdamts.kepegawaian.dto.profil.kartuIdentitas;

import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KartuIdentitasLampiranPostRequest extends LampiranProfilPostRequest {
    private EJenisLampiranProfil ref= EJenisLampiranProfil.KARTU_IDENTITAS;
}

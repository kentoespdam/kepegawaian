package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ProfilUpdateAcceptRequest {
    @Enumerated(EnumType.ORDINAL)
    private EProfileUpdateApproval approval;
    private Long pegawaiId;
}

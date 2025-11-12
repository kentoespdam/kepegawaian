package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfilUpdateAcceptRequest {
    @NotNull(message = "approval is required")
    private EProfileUpdateApproval approval;
    @NotNull(message = "pegawai id is required")
    @Min(value = 1, message = "pegawai id is required")
    private Long pegawaiId;
}

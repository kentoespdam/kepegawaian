package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.dto.commons.PagedRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileUpdateRequest extends PagedRequest {
    private String nipam;
    private String nama;
    private LocalDate tanggalPengajuan;
    @Enumerated(EnumType.ORDINAL)
    private EProfileUpdateApproval approvalStatus = EProfileUpdateApproval.PENDING;
}

package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import jakarta.persistence.criteria.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileUpdateRequest extends CommonPageRequest {
    private String nipam;
    private String nama;
    private LocalDate tanggalPengajuan;
    private EProfileUpdateApproval approvalStatus = EProfileUpdateApproval.PENDING;

    public Specification<ProfileUpdate> getSpecification() {
        Specification<ProfileUpdate> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("nipam").as(String.class), "%" + nipam + "%");
        Specification<ProfileUpdate> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("nama").as(String.class), "%" + nama + "%");
        Specification<ProfileUpdate> tanggalPengajuanSpec = Objects.isNull(tanggalPengajuan) ? null :
                (root, query, criteriaBuilder) -> {
                    Expression<String> tglExp = criteriaBuilder.function("DATE_FORMAT", String.class, root.get("tanggalPengajuan").as(LocalDate.class), criteriaBuilder.literal("'%Y-%m-%d'"));
                    return criteriaBuilder.equal(root.get("tanggalPengajuan"), tglExp);
                };
        Specification<ProfileUpdate> approvalStatusSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("approvalStatus"), approvalStatus);

        return Specification.where(nipamSpec).and(namaSpec).and(tanggalPengajuanSpec).and(approvalStatusSpec);
    }
}

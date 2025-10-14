package id.perumdamts.kepegawaian.dto.profil.profileUpdate;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.criteria.Expression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileUpdateRequest extends CommonPageRequest {
    private String nipam;
    private String nama;
    private LocalDate tanggalPengajuan;
    @Enumerated(EnumType.ORDINAL)
    private EProfileUpdateApproval approvalStatus = EProfileUpdateApproval.PENDING;

    public Specification<ProfileUpdate> getSpecification() {
        SpecificationBuilder<ProfileUpdate> builder = SpecificationBuilder.<ProfileUpdate>of()
                .addLike(nipam, "nipam")
                .addLike(nama, "nama")
                .addEqual(approvalStatus, "approvalStatus");
        if (Objects.nonNull(tanggalPengajuan))
            builder.addCustom((root, cb) -> {
                Expression<LocalDate> tglExp = cb.function("DATE_FORMAT", LocalDate.class, root.get("reqDate"), cb.literal("'%Y-%m-%d'"));
                return cb.equal(root.get("reqDate"), tglExp);
            });
        return builder.build();
    }
}

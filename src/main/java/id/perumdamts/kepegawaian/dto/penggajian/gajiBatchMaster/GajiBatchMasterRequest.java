package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchMasterRequest extends CommonPageRequest {
    @NotEmpty(message = "is Required")
    private String periode;
    @NotNull(message = "is Required")
    @Enumerated(EnumType.STRING)
    private EProsesGaji status;
    private String nipam;
    private String nama;
    private Long organisasiId;

    @JsonIgnore
    public Specification<GajiBatchMaster> getSpecification() {
        Specification<GajiBatchMaster> batchIdSpec = Objects.isNull(periode) ? null :
                (root, query, cb) -> cb.equal(root.get("gajiBatchRoot").get("periode"), periode);
        Specification<GajiBatchMaster> statusSpec = Objects.isNull(status) ? null :
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("gajiBatchRoot").get("status"), status.value());
        Specification<GajiBatchMaster> nipamSpec = Objects.isNull(nipam) ? null :
                (root, query, cb) -> cb.equal(root.get("nipam"), nipam);
        Specification<GajiBatchMaster> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        Specification<GajiBatchMaster> organisasiIdSpec = Objects.isNull(organisasiId) ? null :
                (root, query, cb) -> cb.equal(root.get("organisasi").get("id"), organisasiId);
        return Specification.where(batchIdSpec).and(statusSpec).and(nipamSpec).and(namaSpec).and(organisasiIdSpec);
    }
}

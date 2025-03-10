package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchRootRequest extends CommonPageRequest {
    private String periode;
    private EProsesGaji status;
    private EProsesGaji ltStatus;
    private EProsesGaji gtStatus;

    public Specification<GajiBatchRoot> getSpecification() {
        Specification<GajiBatchRoot> periodeSpec = Objects.isNull(periode) ? null :
                (root, query, cb) -> cb.like(root.get("periode"), periode + "%");
        Specification<GajiBatchRoot> statusSpec = Objects.isNull(status) ? null :
                (root, query, cb) -> cb.equal(root.get("status"), status.value());
        Specification<GajiBatchRoot> ltStatusSpec = Objects.isNull(ltStatus) ? null :
                (root, query, cb) -> cb.lessThanOrEqualTo(root.get("status"), ltStatus.value());
        Specification<GajiBatchRoot> gtStatusSpec = Objects.isNull(gtStatus) ? null :
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("status"), gtStatus.value());
        return Specification.where(periodeSpec).and(statusSpec).and(ltStatusSpec).and(gtStatusSpec);
    }
}
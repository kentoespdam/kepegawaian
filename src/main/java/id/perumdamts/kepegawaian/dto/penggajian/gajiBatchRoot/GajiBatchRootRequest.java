package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchRootRequest extends CommonPageRequest {
    private String periode;
    @Enumerated(EnumType.ORDINAL)
    private EProsesGaji status;

    public Specification<GajiBatchRoot> getSpecification() {
        Specification<GajiBatchRoot> periodeSpec = Objects.isNull(periode) ? null :
                (root, query, cb) -> cb.equal(root.get("periode"), periode);
        Specification<GajiBatchRoot> statusSpec = Objects.isNull(status) ? null :
                (root, query, cb) -> cb.equal(root.get("status"), status);
        return Specification.where(periodeSpec).and(statusSpec);
    }
}

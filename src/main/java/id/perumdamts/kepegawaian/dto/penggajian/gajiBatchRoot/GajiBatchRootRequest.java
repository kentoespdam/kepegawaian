package id.perumdamts.kepegawaian.dto.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiBatchRootRequest extends CommonPageRequest {
    private String periode;
    private EProsesGaji status;
    private String ltStatus;
    private String gtStatus;

    public Specification<GajiBatchRoot> getSpecification() {
        SpecificationBuilder<GajiBatchRoot> builder = SpecificationBuilder.<GajiBatchRoot>of()
                .addLike(periode, "periode");

        if (Objects.nonNull(status))
            builder.addEqual(status, "status");
        if (Objects.nonNull(ltStatus))
            builder.addLessThanOrEqual(ltStatus, "status");
        if (Objects.nonNull(gtStatus))
            builder.addGreaterThanOrEqual(gtStatus, "status");
        return builder.build();
    }
}
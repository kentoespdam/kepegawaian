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
    private EProsesGaji ltStatus;
    private EProsesGaji gtStatus;

    public Specification<GajiBatchRoot> getSpecification() {
        SpecificationBuilder<GajiBatchRoot> builder = SpecificationBuilder.<GajiBatchRoot>of()
                .addLike(periode, "periode")
                .addEqual(status, "status");

        if (Objects.nonNull(ltStatus))
            builder.addLessThanOrEqual(ltStatus, "ltStatus");
        if (Objects.nonNull(gtStatus))
            builder.addGreaterThanOrEqual(gtStatus, "gtStatus");
        return builder.build();
    }
}
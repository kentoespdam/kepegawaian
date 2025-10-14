package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPhdpRequest extends CommonPageRequest {
    private String kondisi;

    @JsonIgnore
    public Specification<GajiPhdp> getSpecification() {
        return SpecificationBuilder.<GajiPhdp>of()
                .addEqual(kondisi, "kondisi")
                .build();
    }
}

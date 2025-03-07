package id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GajiPhdpRequest extends CommonPageRequest {
    private String kondisi;

    @JsonIgnore
    public Specification<GajiPhdp> getSpecification() {
        Specification<GajiPhdp> kondisiSpec = Objects.isNull(kondisi) ? null :
                (root, query, cb) -> cb.equal(root.get("kondisi"), kondisi);
        return Specification.where(kondisiSpec);
    }
}

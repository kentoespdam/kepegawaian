package id.perumdamts.kepegawaian.dto.master.rumahDinas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class RumahDinasRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<RumahDinas> getSpecification() {
        return Specification.where(
                (root, query, cb) ->
                        cb.like(root.get("nama"), "%" + nama + "%")
        );
    }
}

package id.perumdamts.kepegawaian.dto.master.golongan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GolonganRequest extends CommonPageRequest {
    private String golongan;
    private String pangkat;

    @JsonIgnore
    public Specification<Golongan> getSpecification() {
        Specification<Golongan> golonganSpec = Objects.isNull(golongan) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("golongan"), "%" + golongan + "%");
        Specification<Golongan> pangkatSpec = Objects.isNull(pangkat) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("pangkat"), "%" + pangkat + "%");

        return Specification.where(golonganSpec).and(pangkatSpec);
    }
}

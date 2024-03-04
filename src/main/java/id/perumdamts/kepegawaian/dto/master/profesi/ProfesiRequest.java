package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiRequest extends CommonPageRequest {
    private Long levelId;
    private String nama;

    @JsonIgnore
    public Specification<Profesi> getSpecification() {
        Specification<Profesi> levelSpec = Objects.isNull(levelId) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("level").get("id"), levelId);
        Specification<Profesi> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(levelSpec).and(namaSpec);
    }
}

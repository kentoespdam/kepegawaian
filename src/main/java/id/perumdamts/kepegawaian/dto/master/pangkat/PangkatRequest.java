package id.perumdamts.kepegawaian.dto.master.pangkat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Pangkat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class PangkatRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<Pangkat> getSpecification() {
        Specification<Pangkat> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("nama"), "%" + nama + "%");

        return Specification.where(namaSpec);
    }
}

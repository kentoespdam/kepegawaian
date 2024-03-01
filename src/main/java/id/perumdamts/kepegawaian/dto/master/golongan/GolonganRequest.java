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
    private String nama;

    @JsonIgnore
    public Specification<Golongan> getSpecification() {
        Specification<Golongan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("nama"), nama);

        return Specification.where(namaSpec);
    }
}

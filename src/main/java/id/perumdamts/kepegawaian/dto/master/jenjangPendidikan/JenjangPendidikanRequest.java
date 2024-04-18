package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenjangPendidikanRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenjangPendidikan> getSpecification() {
        Specification<JenjangPendidikan> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        return Specification.where(namaSpec);
    }
}

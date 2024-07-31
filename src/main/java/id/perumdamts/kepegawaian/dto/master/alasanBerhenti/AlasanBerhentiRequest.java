package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlasanBerhentiRequest extends CommonPageRequest {
    private String nama;
    private String notes;

    @JsonIgnore
    public Specification<AlasanBerhenti> getSpecification() {
        Specification<AlasanBerhenti> namaSpec = Objects.isNull(nama) ? null :
                (root, query, cb) -> cb.like(root.get("nama"), "%" + nama + "%");
        return Specification.where(namaSpec);
    }
}

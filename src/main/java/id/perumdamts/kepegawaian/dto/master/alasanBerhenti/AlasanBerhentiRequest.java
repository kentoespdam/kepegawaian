package id.perumdamts.kepegawaian.dto.master.alasanBerhenti;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlasanBerhentiRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<AlasanBerhenti> getSpecification() {
        return SpecificationBuilder.<AlasanBerhenti>of()
                .addLike(nama, "nama")
                .build();
    }
}

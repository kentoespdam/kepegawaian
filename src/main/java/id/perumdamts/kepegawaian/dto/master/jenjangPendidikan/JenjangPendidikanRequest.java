package id.perumdamts.kepegawaian.dto.master.jenjangPendidikan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenjangPendidikanRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenjangPendidikan> getSpecification() {
        return SpecificationBuilder.<JenjangPendidikan>of()
                .addLike(nama,"nama")
                .build();
    }
}

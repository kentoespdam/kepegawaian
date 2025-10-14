package id.perumdamts.kepegawaian.dto.master.golongan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GolonganRequest extends CommonPageRequest {
    private String golongan;
    private String pangkat;

    @JsonIgnore
    public Specification<Golongan> getSpecification() {
        return SpecificationBuilder.<Golongan>of()
                .addEqual(golongan,"golongan")
                .addEqual(pangkat,"pangkat")
                .build();
    }
}

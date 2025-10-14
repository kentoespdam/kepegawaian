package id.perumdamts.kepegawaian.dto.master.jenisKeahlian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class JenisKeahlianRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisKeahlian> getSpecification() {
        return SpecificationBuilder.<JenisKeahlian>of()
                .addLike(nama, "nama")
                .build();
    }
}

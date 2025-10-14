package id.perumdamts.kepegawaian.dto.master.jenisKitas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@Data
@EqualsAndHashCode(callSuper = true)
public class JenisKitasRequest extends CommonPageRequest {
    private String nama;

    @JsonIgnore
    public Specification<JenisKitas> getSpecification() {
        return SpecificationBuilder.<JenisKitas>of()
                .addLike(nama, "nama")
                .build();
    }
}

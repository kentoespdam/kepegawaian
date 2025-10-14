package id.perumdamts.kepegawaian.dto.master.alatKerja;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.AlatKerja;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlatKerjaRequest extends CommonPageRequest {
    private String nama;
    private Long profesiId;

    @JsonIgnore
    public Specification<AlatKerja> getSpecification() {
        return SpecificationBuilder.<AlatKerja>of()
                .addLike(nama, "nama")
                .addEqual(profesiId, "profesi", "id")
                .build();
    }
}

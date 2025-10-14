package id.perumdamts.kepegawaian.dto.master.apd;

import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Apd;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApdRequest extends CommonPageRequest {
    private String nama;
    private Long profesiId;

    public Specification<Apd> getSpecification() {
        return SpecificationBuilder.<Apd>of()
                .addEqual(nama, "nama")
                .addEqual(profesiId, "profesi", "id")
                .build();
    }
}

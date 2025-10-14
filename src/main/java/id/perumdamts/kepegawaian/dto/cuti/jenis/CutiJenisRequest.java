package id.perumdamts.kepegawaian.dto.cuti.jenis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class CutiJenisRequest extends CommonPageRequest {
    private Long parentId;
    private String nama;

    @JsonIgnore
    public Specification<CutiJenis> getSpecification() {
        return SpecificationBuilder.<CutiJenis>of()
                .addEqual(parentId, "parentId")
                .addLike(nama, "nama")
                .build();
    }
}

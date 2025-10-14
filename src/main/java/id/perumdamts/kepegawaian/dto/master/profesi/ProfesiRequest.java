package id.perumdamts.kepegawaian.dto.master.profesi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Profesi;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfesiRequest extends CommonPageRequest {
    private Long organisasiId;
    private Long jabatanId;
    private Long levelId;
    private Long gradeId;
    private String nama;

    @JsonIgnore
    public Specification<Profesi> getSpecification() {
        return SpecificationBuilder.<Profesi>of()
                .addEqual(organisasiId, "organisasi", "id")
                .addEqual(levelId, "level", "id")
                .addEqual(jabatanId, "jabatan", "id")
                .addEqual(gradeId, "grade", "id")
                .addLike(nama, "nama")
                .build();
    }
}

package id.perumdamts.kepegawaian.dto.master.grade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@EqualsAndHashCode(callSuper = true)
@Data
public class GradeIndexQuery extends CommonPageRequest {
    private Long levelId;
    private Integer grade;

    @JsonIgnore
    public Specification<Grade> getSpecification() {
        return SpecificationBuilder.<Grade>of()
                .addEqual(levelId, "level", "id")
                .addEqual(grade, "grade")
                .build();
    }
}

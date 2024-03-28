package id.perumdamts.kepegawaian.dto.master.grade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.dto.commons.CommonPageRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
public class GradeRequest extends CommonPageRequest {
    private Long levelId;
    private Integer grade;

    @JsonIgnore
    public Specification<Grade> getSpecification() {
        Specification<Grade> levelSpec = Objects.isNull(levelId) ? null : (root, query, cb) -> cb.equal(root.get("level").get("id"), levelId);
        Specification<Grade> gradeSpec = Objects.isNull(grade) ? null : (root, query, cb) -> cb.equal(root.get("grade"), grade);

        return Specification.where(levelSpec).and(gradeSpec);
    }
}

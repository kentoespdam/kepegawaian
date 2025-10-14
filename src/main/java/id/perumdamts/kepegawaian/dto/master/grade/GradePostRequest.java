package id.perumdamts.kepegawaian.dto.master.grade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.utils.SpecificationBuilder;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Data
public class GradePostRequest {
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @Min(value = 1, message = "Grade must be greater than 0")
    private Integer grade;
    @Min(value = 100000, message = "Tukin must be greater than 100.000")
    private Double tukin;

    @JsonIgnore
    public Specification<Grade> getSpecification() {
        return SpecificationBuilder.<Grade>of()
                .addEqual(levelId,"level","id")
                .addEqual(grade,"grade")
                .addEqual(tukin,"tukin")
                .build();
    }

    public static Grade toEntity(GradePostRequest request) {
        Grade entity = new Grade();
        entity.setLevel(new Level(request.getLevelId()));
        entity.setGrade(request.getGrade());
        entity.setTukin(request.getTukin());
        return entity;
    }

    public static Grade toEntity(GradePostRequest request, Long id) {
        Grade entity = new Grade(id);
        entity.setLevel(new Level(request.getLevelId()));
        entity.setGrade(request.getGrade());
        entity.setTukin(request.getTukin());
        return entity;
    }

    public static List<Grade> toEntities(List<GradePostRequest> requests) {
        return requests.stream().map(GradePostRequest::toEntity).toList();
    }
}

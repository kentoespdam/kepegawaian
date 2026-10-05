package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.entities.master.Grade;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GradePostRequest {
    @Min(value = 1, message = "Level ID must be greater than or equal to 1")
    private Long levelId;
    @Min(value = 1, message = "Grade must be greater than 0")
    private Integer grade;
    @Min(value = 100000, message = "Tukin must be greater than 100.000")
    private Double tukin;



}

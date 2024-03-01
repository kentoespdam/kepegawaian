package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GradeResponse {
    private Long id;
    private LevelResponse level;
    private Integer grade;
    private Double tukin;

    public static GradeResponse from(Grade grade) {
        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        LevelResponse levelResponse = LevelResponse.from(grade.getLevel());
        response.setLevel(levelResponse);
        response.setGrade(grade.getGrade());
        response.setTukin(grade.getTukin());
        return response;
    }
}

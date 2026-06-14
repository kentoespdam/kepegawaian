package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import lombok.Data;

@Data
public class GradeQuery {
    private Long id;
    private Long levelId;
    private LevelResponse level;
    private Integer grade;
    private Double tukin;
}

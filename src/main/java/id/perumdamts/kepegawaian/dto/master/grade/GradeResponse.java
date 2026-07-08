package id.perumdamts.kepegawaian.dto.master.grade;

import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.mapper.master.level.LevelMapper;

import java.util.Objects;

public record GradeResponse(
        Long id,
        LevelResponse level,
        Integer grade,
        Double tukin
) {
    public static GradeResponse from(Grade grade) {
        if (Objects.isNull(grade)) return null;
        return new GradeResponse(
                grade.getId(),
                LevelMapper.toResponse(grade.getLevel()),
                grade.getGrade(),
                grade.getTukin()
        );
    }
}

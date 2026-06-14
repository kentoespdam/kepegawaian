package id.perumdamts.kepegawaian.services.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradePostRequest;
import id.perumdamts.kepegawaian.entities.master.Grade;
import id.perumdamts.kepegawaian.entities.master.Level;

import java.util.Objects;

public final class GradeMapper {
    private GradeMapper() {}

    public static Grade toEntity(GradePostRequest request, Level level) {
        Grade entity = new Grade();
        if (Objects.nonNull(level))
            entity.setLevel(level);
        entity.setGrade(request.getGrade());
        entity.setTukin(request.getTukin());
        return entity;
    }

    public static void updateEntity(Grade entity, GradePostRequest request, Level level) {
        if (level != null)
            entity.setLevel(level);
        entity.setGrade(request.getGrade());
        entity.setTukin(request.getTukin());
    }
}

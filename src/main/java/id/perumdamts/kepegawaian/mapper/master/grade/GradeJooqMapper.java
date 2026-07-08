package id.perumdamts.kepegawaian.mapper.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.repositories.master.jooq.GradeSelects;
import org.jooq.Record;

public final class GradeJooqMapper {
    private GradeJooqMapper() {}

    public static GradeQuery toQuery(Record record) {
        return new GradeQuery(
            record.get(GradeSelects.ID),
            record.get(GradeSelects.GRADE_),
            record.get(GradeSelects.TUKIN),
            record.get(GradeSelects.LEVEL_ID) != null
                ? new LevelResponse(record.get(GradeSelects.LEVEL_ID), record.get(GradeSelects.LEVEL_NAMA))
                : null
        );
    }
}

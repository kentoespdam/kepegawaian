package id.perumdamts.kepegawaian.mapper.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import org.jooq.Record;

import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

public final class GradeJooqMapper {
    private GradeJooqMapper() {}

    public static GradeQuery mapToQuery(Record record) {
        GradeQuery query = new GradeQuery();
        query.setId(record.get(GRADE.ID));
        query.setLevelId(record.get(GRADE.LEVEL_ID.as("self_level_id")));
        query.setGrade(record.get(GRADE.GRADE_));
        query.setTukin(record.get(GRADE.TUKIN));

        Long levelId = record.get(LEVEL.ID.as("level_id"));
        if (levelId != null) {
            String levelNama = record.get(LEVEL.NAMA.as("level_nama"));
            query.setLevel(new LevelResponse(levelId, levelNama));
        }
        return query;
    }
}

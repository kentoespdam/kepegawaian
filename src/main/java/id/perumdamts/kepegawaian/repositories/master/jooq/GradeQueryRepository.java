package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.grade.GradeIndexQuery;
import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

@Repository
@RequiredArgsConstructor
public class GradeQueryRepository {
    private final DSLContext dsl;

    public Page<GradeQuery> pageQuery(GradeIndexQuery query) {
        var sortField = switch (query.getSortBy()) {
            case "grade" -> GRADE.GRADE_;
            case "tukin" -> GRADE.TUKIN;
            case "levelId" -> GRADE.LEVEL_ID;
            default -> GRADE.ID;
        };
        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();
        var count = dsl.selectCount()
                .from(GRADE)
                .where(GRADE.IS_DELETED.eq(false))
                .and(query.getLevelId() != null ? GRADE.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .and(query.getGrade() != null ? GRADE.GRADE_.eq(query.getGrade()) : DSL.noCondition())
                .fetchOne(0, Long.class);
        var data = dsl.select(
                        GRADE.ID,
                        GRADE.LEVEL_ID,
                        GRADE.GRADE_,
                        GRADE.TUKIN,
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.IS_DELETED.eq(false))
                .and(query.getLevelId() != null ? GRADE.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .and(query.getGrade() != null ? GRADE.GRADE_.eq(query.getGrade()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
    }

    public Optional<GradeQuery> getById(Long id) {
        return dsl.select(
                        GRADE.ID,
                        GRADE.LEVEL_ID,
                        GRADE.GRADE_,
                        GRADE.TUKIN,
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.ID.eq(id))
                .and(GRADE.IS_DELETED.eq(false))
                .fetchOptional(record -> toQuery(record.intoMap()));
    }

    public List<GradeQuery> listQuery() {
        return dsl.select(
                        GRADE.ID,
                        GRADE.LEVEL_ID,
                        GRADE.GRADE_,
                        GRADE.TUKIN,
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.IS_DELETED.eq(false))
                .orderBy(GRADE.GRADE_.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    public List<GradeQuery> findByLevelId(Long levelId) {
        return dsl.select(
                        GRADE.ID,
                        GRADE.LEVEL_ID,
                        GRADE.GRADE_,
                        GRADE.TUKIN,
                        LEVEL.ID.as("level_id"),
                        LEVEL.NAMA.as("level_nama"))
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.LEVEL_ID.eq(levelId))
                .and(GRADE.IS_DELETED.eq(false))
                .orderBy(GRADE.GRADE_.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    private GradeQuery toQuery(Map<String, Object> map) {
        var query = new GradeQuery();
        query.setId((Long) map.get("id"));
        query.setLevelId((Long) map.get("level_id"));
        query.setGrade((Integer) map.get("grade"));
        query.setTukin((Double) map.get("tukin"));
        if (map.get("level_id") != null) {
            var l = new LevelResponse((Long) map.get("level_id"), (String) map.get("level_nama"));
            query.setLevel(l);
        }
        return query;
    }
}

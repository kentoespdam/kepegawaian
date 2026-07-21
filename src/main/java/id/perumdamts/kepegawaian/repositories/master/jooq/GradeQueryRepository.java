package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.grade.GradeIndexQuery;
import id.perumdamts.kepegawaian.dto.master.grade.GradeListResponse;
import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import id.perumdamts.kepegawaian.mapper.master.grade.GradeJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
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
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GRADE.ID);
        var count = dsl.selectCount()
                .from(GRADE)
                .where(GRADE.IS_DELETED.eq(false))
                .and(query.getLevelId() != null ? GRADE.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .and(query.getGrade() != null ? GRADE.GRADE_.eq(query.getGrade()) : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GradeSelects.ID,
                        GradeSelects.GRADE_,
                        GradeSelects.TUKIN,
                        GradeSelects.LEVEL_ID,
                        GradeSelects.LEVEL_NAMA)
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.IS_DELETED.eq(false))
                .and(query.getLevelId() != null ? GRADE.LEVEL_ID.eq(query.getLevelId()) : DSL.noCondition())
                .and(query.getGrade() != null ? GRADE.GRADE_.eq(query.getGrade()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GradeJooqMapper::toQuery);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "grade", GRADE.GRADE_,
                "tukin", GRADE.TUKIN,
                "levelId", GRADE.LEVEL_ID
        );
    }

    public Optional<GradeQuery> getById(Long id) {
        return dsl.select(
                        GradeSelects.ID,
                        GradeSelects.GRADE_,
                        GradeSelects.TUKIN,
                        GradeSelects.LEVEL_ID,
                        GradeSelects.LEVEL_NAMA)
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.ID.eq(id))
                .and(GRADE.IS_DELETED.eq(false))
                .fetchOptional(GradeJooqMapper::toQuery);
    }

    public List<GradeListResponse> listQuery() {
        return dsl.select(GradeSelects.ID, GradeSelects.GRADE_, GRADE.LEVEL_ID.as("levelId"))
                .from(GRADE)
                .where(GRADE.IS_DELETED.eq(false))
                .orderBy(GRADE.GRADE_.asc())
                .fetchInto(GradeListResponse.class);
    }

    public List<GradeQuery> findByLevelId(Long levelId) {
        return dsl.select(
                        GradeSelects.ID,
                        GradeSelects.GRADE_,
                        GradeSelects.TUKIN,
                        GradeSelects.LEVEL_ID,
                        GradeSelects.LEVEL_NAMA)
                .from(GRADE)
                .leftJoin(LEVEL).on(GRADE.LEVEL_ID.eq(LEVEL.ID))
                .where(GRADE.LEVEL_ID.eq(levelId))
                .and(GRADE.IS_DELETED.eq(false))
                .orderBy(GRADE.GRADE_.asc())
                .fetch(GradeJooqMapper::toQuery);
    }

}

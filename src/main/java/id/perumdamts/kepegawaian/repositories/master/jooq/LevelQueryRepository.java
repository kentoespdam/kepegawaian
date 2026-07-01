package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.level.LevelIndexQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelQuery;
import id.perumdamts.kepegawaian.jooq.tables.Level;
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

@Repository
@RequiredArgsConstructor
public class LevelQueryRepository {
    private final DSLContext dsl;

    public Page<LevelQuery> pageQuery(LevelIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), Level.LEVEL.ID);

        var count = dsl.selectCount()
                .from(Level.LEVEL)
                .where(Level.LEVEL.IS_DELETED.eq(false))
                .and(query.getNama() != null ? Level.LEVEL.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(Level.LEVEL.ID, Level.LEVEL.NAMA)
                .from(Level.LEVEL)
                .where(Level.LEVEL.IS_DELETED.eq(false))
                .and(query.getNama() != null ? Level.LEVEL.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetchInto(LevelQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", Level.LEVEL.NAMA
        );
    }

    public Optional<LevelQuery> getById(Long id) {
        return dsl.select(Level.LEVEL.ID, Level.LEVEL.NAMA)
                .from(Level.LEVEL)
                .where(Level.LEVEL.ID.eq(id))
                .and(Level.LEVEL.IS_DELETED.eq(false))
                .fetchOptionalInto(LevelQuery.class);
    }

    public List<LevelQuery> listQuery() {
        return dsl.select(Level.LEVEL.ID, Level.LEVEL.NAMA)
                .from(Level.LEVEL)
                .where(Level.LEVEL.IS_DELETED.eq(false))
                .orderBy(Level.LEVEL.NAMA.asc())
                .fetchInto(LevelQuery.class);
    }
}

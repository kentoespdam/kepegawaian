package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.level.LevelIndexQuery;
import id.perumdamts.kepegawaian.dto.master.level.LevelQuery;
import id.perumdamts.kepegawaian.jooq.tables.Level;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LevelQueryRepository {
    private final DSLContext dsl;

    public Page<LevelQuery> pageQuery(LevelIndexQuery query) {
        var sortField = switch (query.getSortBy()) {
            case "nama" -> Level.LEVEL.NAMA;
            default -> Level.LEVEL.ID;
        };

        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();

        var count = dsl.selectCount()
                .from(Level.LEVEL)
                .where(Level.LEVEL.IS_DELETED.eq(false))
                .and(query.getNama() != null ? Level.LEVEL.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        var data = dsl.select(Level.LEVEL.ID, Level.LEVEL.NAMA)
                .from(Level.LEVEL)
                .where(Level.LEVEL.IS_DELETED.eq(false))
                .and(query.getNama() != null ? Level.LEVEL.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetchInto(LevelQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
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

package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.rumahDinas.RumahDinasQuery;
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

import static id.perumdamts.kepegawaian.jooq.tables.RumahDinas.RUMAH_DINAS;

@Repository
@RequiredArgsConstructor
public class RumahDinasQueryRepository {
    private final DSLContext dsl;

    public Page<RumahDinasQuery> pageQuery(RumahDinasIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), RUMAH_DINAS.ID);
        var count = dsl.selectCount()
                .from(RUMAH_DINAS)
                .where(RUMAH_DINAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? RUMAH_DINAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);
        var data = dsl.select(
                        RUMAH_DINAS.ID,
                        RUMAH_DINAS.NAMA,
                        RUMAH_DINAS.NILAI)
                .from(RUMAH_DINAS)
                .where(RUMAH_DINAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? RUMAH_DINAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", RUMAH_DINAS.NAMA,
                "nilai", RUMAH_DINAS.NILAI
        );
    }

    public Optional<RumahDinasQuery> getById(Long id) {
        return dsl.select(
                        RUMAH_DINAS.ID,
                        RUMAH_DINAS.NAMA,
                        RUMAH_DINAS.NILAI)
                .from(RUMAH_DINAS)
                .where(RUMAH_DINAS.ID.eq(id))
                .and(RUMAH_DINAS.IS_DELETED.eq(false))
                .fetchOptional(record -> toQuery(record.intoMap()));
    }

    public List<RumahDinasQuery> listQuery() {
        return dsl.select(
                        RUMAH_DINAS.ID,
                        RUMAH_DINAS.NAMA,
                        RUMAH_DINAS.NILAI)
                .from(RUMAH_DINAS)
                .where(RUMAH_DINAS.IS_DELETED.eq(false))
                .orderBy(RUMAH_DINAS.NAMA.asc())
                .fetch(record -> toQuery(record.intoMap()));
    }

    private RumahDinasQuery toQuery(Map<String, Object> map) {
        var query = new RumahDinasQuery();
        query.setId((Long) map.get("id"));
        query.setNama((String) map.get("nama"));
        Object nilai = map.get("nilai");
        if (nilai instanceof Number) {
            query.setNilai(((Number) nilai).doubleValue());
        }
        return query;
    }
}

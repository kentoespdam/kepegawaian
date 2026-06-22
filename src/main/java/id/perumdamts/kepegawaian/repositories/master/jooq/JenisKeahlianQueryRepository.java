package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianQuery;
import id.perumdamts.kepegawaian.jooq.tables.JenisKeahlian;
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
public class JenisKeahlianQueryRepository {
    private final DSLContext dsl;

    public Page<JenisKeahlianQuery> pageQuery(JenisKeahlianIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JenisKeahlian.JENIS_KEAHLIAN.ID);

        // Count query
        var count = dsl.selectCount()
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKeahlian.JENIS_KEAHLIAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOne(0, Long.class);

        // Data query
        var data = dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JenisKeahlian.JENIS_KEAHLIAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetchInto(JenisKeahlianQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", JenisKeahlian.JENIS_KEAHLIAN.NAMA
        );
    }

    public Optional<JenisKeahlianQuery> getById(Long id) {
        return dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.ID.eq(id))
                .and(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisKeahlianQuery.class);
    }

    public List<JenisKeahlianQuery> listQuery() {
        return dsl.select(JenisKeahlian.JENIS_KEAHLIAN.ID, JenisKeahlian.JENIS_KEAHLIAN.NAMA)
                .from(JenisKeahlian.JENIS_KEAHLIAN)
                .where(JenisKeahlian.JENIS_KEAHLIAN.IS_DELETED.eq(false))
                .orderBy(JenisKeahlian.JENIS_KEAHLIAN.NAMA.asc())
                .fetchInto(JenisKeahlianQuery.class);
    }
}

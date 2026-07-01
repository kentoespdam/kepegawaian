package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanQuery;
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

import static id.perumdamts.kepegawaian.jooq.tables.JenisPelatihan.JENIS_PELATIHAN;

@Repository
@RequiredArgsConstructor
public class JenisPelatihanQueryRepository {
    private final DSLContext dsl;

    public Page<JenisPelatihanQuery> pageQuery(JenisPelatihanIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JENIS_PELATIHAN.ID);

        var count = dsl.selectCount()
                .from(JENIS_PELATIHAN)
                .where(JENIS_PELATIHAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENIS_PELATIHAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(JENIS_PELATIHAN.ID, JENIS_PELATIHAN.NAMA)
                .from(JENIS_PELATIHAN)
                .where(JENIS_PELATIHAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENIS_PELATIHAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetchInto(JenisPelatihanQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<JenisPelatihanQuery> getById(Long id) {
        return dsl.select(JENIS_PELATIHAN.ID, JENIS_PELATIHAN.NAMA)
                .from(JENIS_PELATIHAN)
                .where(JENIS_PELATIHAN.ID.eq(id))
                .and(JENIS_PELATIHAN.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisPelatihanQuery.class);
    }

    public List<JenisPelatihanQuery> listQuery() {
        return dsl.select(JENIS_PELATIHAN.ID, JENIS_PELATIHAN.NAMA)
                .from(JENIS_PELATIHAN)
                .where(JENIS_PELATIHAN.IS_DELETED.eq(false))
                .orderBy(JENIS_PELATIHAN.NAMA.asc())
                .fetchInto(JenisPelatihanQuery.class);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", JENIS_PELATIHAN.NAMA
        );
    }
}

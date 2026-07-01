package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
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

import static id.perumdamts.kepegawaian.jooq.tables.JenjangPendidikan.JENJANG_PENDIDIKAN;

@Repository
@RequiredArgsConstructor
public class JenjangPendidikanQueryRepository {
    private final DSLContext dsl;

    public Page<JenjangPendidikanResponse> pageQuery(JenjangPendidikanIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JENJANG_PENDIDIKAN.ID);

        var count = dsl.selectCount()
                .from(JENJANG_PENDIDIKAN)
                .where(JENJANG_PENDIDIKAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENJANG_PENDIDIKAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(
                        JENJANG_PENDIDIKAN.ID,
                        JENJANG_PENDIDIKAN.NAMA,
                        JENJANG_PENDIDIKAN.SHORT_NAME,
                        JENJANG_PENDIDIKAN.SEQ,
                        JENJANG_PENDIDIKAN.IS_STATISTIK)
                .from(JENJANG_PENDIDIKAN)
                .where(JENJANG_PENDIDIKAN.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENJANG_PENDIDIKAN.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetchInto(JenjangPendidikanResponse.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<JenjangPendidikanResponse> getById(Long id) {
        return dsl.select(
                        JENJANG_PENDIDIKAN.ID,
                        JENJANG_PENDIDIKAN.NAMA,
                        JENJANG_PENDIDIKAN.SHORT_NAME,
                        JENJANG_PENDIDIKAN.SEQ,
                        JENJANG_PENDIDIKAN.IS_STATISTIK)
                .from(JENJANG_PENDIDIKAN)
                .where(JENJANG_PENDIDIKAN.ID.eq(id))
                .and(JENJANG_PENDIDIKAN.IS_DELETED.eq(false))
                .fetchOptionalInto(JenjangPendidikanResponse.class);
    }

    public List<JenjangPendidikanResponse> listQuery() {
        return dsl.select(
                        JENJANG_PENDIDIKAN.ID,
                        JENJANG_PENDIDIKAN.NAMA,
                        JENJANG_PENDIDIKAN.SHORT_NAME,
                        JENJANG_PENDIDIKAN.SEQ,
                        JENJANG_PENDIDIKAN.IS_STATISTIK)
                .from(JENJANG_PENDIDIKAN)
                .where(JENJANG_PENDIDIKAN.IS_DELETED.eq(false))
                .orderBy(JENJANG_PENDIDIKAN.SEQ.asc())
                .fetchInto(JenjangPendidikanResponse.class);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", JENJANG_PENDIDIKAN.NAMA,
                "seq", JENJANG_PENDIDIKAN.SEQ
        );
    }
}

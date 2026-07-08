package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasListResponse;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasQuery;
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

import static id.perumdamts.kepegawaian.jooq.tables.JenisKitas.JENIS_KITAS;

@Repository
@RequiredArgsConstructor
public class JenisKitasQueryRepository {
    private final DSLContext dsl;

    public Page<JenisKitasQuery> pageQuery(JenisKitasIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JENIS_KITAS.ID);

        var count = dsl.selectCount()
                .from(JENIS_KITAS)
                .where(JENIS_KITAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENIS_KITAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);

        var data = dsl.select(JENIS_KITAS.ID, JENIS_KITAS.NAMA)
                .from(JENIS_KITAS)
                .where(JENIS_KITAS.IS_DELETED.eq(false))
                .and(query.getNama() != null ? JENIS_KITAS.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetchInto(JenisKitasQuery.class);

        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<JenisKitasQuery> getById(Long id) {
        return dsl.select(JENIS_KITAS.ID, JENIS_KITAS.NAMA)
                .from(JENIS_KITAS)
                .where(JENIS_KITAS.ID.eq(id))
                .and(JENIS_KITAS.IS_DELETED.eq(false))
                .fetchOptionalInto(JenisKitasQuery.class);
    }

    public List<JenisKitasListResponse> listQuery() {
        return dsl.select(JENIS_KITAS.ID, JENIS_KITAS.NAMA)
                .from(JENIS_KITAS)
                .where(JENIS_KITAS.IS_DELETED.eq(false))
                .orderBy(JENIS_KITAS.NAMA.asc())
                .fetchInto(JenisKitasListResponse.class);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", JENIS_KITAS.NAMA
        );
    }
}

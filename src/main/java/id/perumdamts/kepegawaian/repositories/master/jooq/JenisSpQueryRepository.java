package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpIndexQuery;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpListResponse;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiRow;
import id.perumdamts.kepegawaian.mapper.master.jenisSp.JenisSpJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Repository
@RequiredArgsConstructor
public class JenisSpQueryRepository {
    private final DSLContext dsl;

    public Page<JenisSpQuery> pageQuery(JenisSpIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), JENIS_SP.ID);
        var count = dsl.selectCount()
                .from(JENIS_SP)
                .where(JENIS_SP.IS_DELETED.eq(false))
                .and(query.getKode() != null ? JENIS_SP.KODE.likeIgnoreCase("%" + query.getKode() + "%") : DSL.noCondition())
                .and(query.getNama() != null ? JENIS_SP.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        JENIS_SP.ID,
                        JENIS_SP.KODE,
                        JENIS_SP.NAMA)
                .select(
                        multiset(dsl.select(SANKSI_SP.ID, SANKSI_SP.KODE, SANKSI_SP.KETERANGAN)
                                .from(SANKSI_SP)
                                .where(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                                .and(SANKSI_SP.IS_DELETED.eq(false))
                                .orderBy(SANKSI_SP.KODE.asc()))
                                .as("sanksi_list")
                                .convertFrom(r -> r.map(mapping(SanksiRow::new))))
                .from(JENIS_SP)
                .where(JENIS_SP.IS_DELETED.eq(false))
                .and(query.getKode() != null ? JENIS_SP.KODE.likeIgnoreCase("%" + query.getKode() + "%") : DSL.noCondition())
                .and(query.getNama() != null ? JENIS_SP.NAMA.likeIgnoreCase("%" + query.getNama() + "%") : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(JenisSpJooqMapper::toQuery);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", JENIS_SP.KODE,
                "nama", JENIS_SP.NAMA
        );
    }

    public Optional<JenisSpQuery> getById(Long id) {
        return dsl.select(
                        JENIS_SP.ID,
                        JENIS_SP.KODE,
                        JENIS_SP.NAMA)
                .select(
                        multiset(dsl.select(SANKSI_SP.ID, SANKSI_SP.KODE, SANKSI_SP.KETERANGAN)
                                .from(SANKSI_SP)
                                .where(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                                .and(SANKSI_SP.IS_DELETED.eq(false))
                                .orderBy(SANKSI_SP.KODE.asc()))
                                .as("sanksi_list")
                                .convertFrom(r -> r.map(mapping(SanksiRow::new))))
                .from(JENIS_SP)
                .where(JENIS_SP.ID.eq(id))
                .and(JENIS_SP.IS_DELETED.eq(false))
                .fetchOptional(JenisSpJooqMapper::toQuery);
    }

    public List<JenisSpListResponse> listQuery() {
        return dsl.select(
                        JENIS_SP.ID,
                        JENIS_SP.KODE,
                        JENIS_SP.NAMA)
                .from(JENIS_SP)
                .where(JENIS_SP.IS_DELETED.eq(false))
                .orderBy(JENIS_SP.NAMA.asc())
                .fetchInto(JenisSpListResponse.class);
    }
}

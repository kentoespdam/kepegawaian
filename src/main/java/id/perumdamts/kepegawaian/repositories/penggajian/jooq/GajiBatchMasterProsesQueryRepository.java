package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMasterProses.GAJI_BATCH_MASTER_PROSES;

@Repository
@RequiredArgsConstructor
public class GajiBatchMasterProsesQueryRepository {
    private final DSLContext dsl;

    public Page<GajiBatchMasterProsesResponse> pageQuery(GajiBatchMasterProsesIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_MASTER_PROSES.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_BATCH_MASTER_PROSES)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.selectFrom(GAJI_BATCH_MASTER_PROSES)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiBatchMasterProsesJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiBatchMasterProsesResponse> listQuery(GajiBatchMasterProsesIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_MASTER_PROSES.ID);
        return dsl.selectFrom(GAJI_BATCH_MASTER_PROSES)
                .where(baseWhere(query))
                .orderBy(sortOrder)
                .fetch(GajiBatchMasterProsesJooqMapper::mapToResponse);
    }

    public List<GajiBatchMasterProsesResponse> findByMasterId(Long masterId) {
        return dsl.selectFrom(GAJI_BATCH_MASTER_PROSES)
                .where(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID.eq(masterId))
                .orderBy(GAJI_BATCH_MASTER_PROSES.URUT.asc(), GAJI_BATCH_MASTER_PROSES.ID.asc())
                .fetch(GajiBatchMasterProsesJooqMapper::mapToResponse);
    }

    public Optional<GajiBatchMasterProsesResponse> getById(Long id) {
        return dsl.selectFrom(GAJI_BATCH_MASTER_PROSES)
                .where(GAJI_BATCH_MASTER_PROSES.ID.eq(id))
                .fetchOptional(GajiBatchMasterProsesJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", GAJI_BATCH_MASTER_PROSES.ID,
                "urut", GAJI_BATCH_MASTER_PROSES.URUT,
                "nama", GAJI_BATCH_MASTER_PROSES.NAMA
        );
    }

    private Condition baseWhere(GajiBatchMasterProsesIndexQuery q) {
        Condition condition = DSL.noCondition();
        if (q.getBatchMasterId() != null) {
            condition = condition.and(GAJI_BATCH_MASTER_PROSES.BATCH_MASTER_ID.eq(q.getBatchMasterId()));
        }
        if (q.getJenisGaji() != null) {
            condition = condition.and(GAJI_BATCH_MASTER_PROSES.JENIS_GAJI.eq(
                    id.perumdamts.kepegawaian.jooq.enums.GajiBatchMasterProsesJenisGaji.valueOf(q.getJenisGaji().name())
            ));
        }
        if (q.getKode() != null && !q.getKode().isBlank()) {
            condition = condition.and(GAJI_BATCH_MASTER_PROSES.KODE.likeIgnoreCase("%" + q.getKode() + "%"));
        }
        return condition;
    }
}

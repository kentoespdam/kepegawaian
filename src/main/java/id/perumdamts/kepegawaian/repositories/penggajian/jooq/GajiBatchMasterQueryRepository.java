package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMaster.GajiBatchMasterResponse;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiBatchMaster.GajiBatchMasterJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchMaster.GAJI_BATCH_MASTER;
import static id.perumdamts.kepegawaian.jooq.tables.GajiBatchRoot.GAJI_BATCH_ROOT;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

@Repository
@RequiredArgsConstructor
public class GajiBatchMasterQueryRepository {
    private final DSLContext dsl;

    public Page<GajiBatchMasterResponse> pageQuery(GajiBatchMasterIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_MASTER.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_BATCH_MASTER)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_BATCH_MASTER.asterisk(),
                        ORGANISASI.KODE,
                        ORGANISASI.NAMA)
                .from(GAJI_BATCH_MASTER)
                .leftJoin(ORGANISASI).on(GAJI_BATCH_MASTER.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiBatchMasterJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiBatchMasterResponse> listQuery(GajiBatchMasterIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_MASTER.ID);
        return dsl.select(
                        GAJI_BATCH_MASTER.asterisk(),
                        ORGANISASI.KODE,
                        ORGANISASI.NAMA)
                .from(GAJI_BATCH_MASTER)
                .leftJoin(ORGANISASI).on(GAJI_BATCH_MASTER.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(baseWhere(query))
                .orderBy(sortOrder)
                .fetch(GajiBatchMasterJooqMapper::mapToResponse);
    }

    public Page<GajiBatchMasterResponse> findByPegawaiId(Long pegawaiId, GajiBatchMasterIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_BATCH_MASTER.ID);
        Condition where = baseWhere(query)
                .and(GAJI_BATCH_MASTER.PEGAWAI_ID.eq(pegawaiId))
                .and(GAJI_BATCH_ROOT.STATUS.ge(EProsesGaji.FINISHED.ordinal()));
        var count = dsl.selectCount()
                .from(GAJI_BATCH_MASTER)
                .join(GAJI_BATCH_ROOT).on(GAJI_BATCH_MASTER.BATCH_ROOT_ID.eq(GAJI_BATCH_ROOT.ID))
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_BATCH_MASTER.asterisk(),
                        ORGANISASI.KODE,
                        ORGANISASI.NAMA)
                .from(GAJI_BATCH_MASTER)
                .join(GAJI_BATCH_ROOT).on(GAJI_BATCH_MASTER.BATCH_ROOT_ID.eq(GAJI_BATCH_ROOT.ID))
                .leftJoin(ORGANISASI).on(GAJI_BATCH_MASTER.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiBatchMasterJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<GajiBatchMasterResponse> getById(Long id) {
        return dsl.select(
                        GAJI_BATCH_MASTER.asterisk(),
                        ORGANISASI.KODE,
                        ORGANISASI.NAMA)
                .from(GAJI_BATCH_MASTER)
                .leftJoin(ORGANISASI).on(GAJI_BATCH_MASTER.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(GAJI_BATCH_MASTER.ID.eq(id))
                .fetchOptional(GajiBatchMasterJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "id", GAJI_BATCH_MASTER.ID,
                "periode", GAJI_BATCH_MASTER.PERIODE
        );
    }

    private Condition baseWhere(GajiBatchMasterIndexQuery q) {
        Condition condition = GAJI_BATCH_MASTER.PERIODE.eq(q.getPeriode());
        if (q.getSearch() != null && !q.getSearch().isBlank()) {
            String escaped = q.getSearch().trim()
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
            String like = "%" + escaped + "%";
            condition = condition.and(
                    GAJI_BATCH_MASTER.NIPAM.likeIgnoreCase(like, '\\')
                            .or(GAJI_BATCH_MASTER.NAMA.likeIgnoreCase(like, '\\'))
            );
        }
        return condition;
    }
}

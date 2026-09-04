package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKpi.GajiKpiResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiKpi.GajiKpiJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.GajiKpi.GAJI_KPI;

@Repository
@RequiredArgsConstructor
public class GajiKpiQueryRepository {
    private final DSLContext dsl;

    public Page<GajiKpiResponse> pageQuery(GajiKpiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_KPI.ID);
        Condition where = baseWhere(query.getNipam(), query.getPeriode());
        var count = dsl.selectCount()
                .from(GAJI_KPI)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_KPI.ID,
                        GAJI_KPI.NIPAM,
                        GAJI_KPI.PERIODE,
                        GAJI_KPI.TUNKIN,
                        GAJI_KPI.PPH21_TER)
                .from(GAJI_KPI)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiKpiJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiKpiResponse> listQuery(GajiKpiListRequest query) {
        return dsl.select(
                        GAJI_KPI.ID,
                        GAJI_KPI.NIPAM,
                        GAJI_KPI.PERIODE,
                        GAJI_KPI.TUNKIN,
                        GAJI_KPI.PPH21_TER)
                .from(GAJI_KPI)
                .where(baseWhere(query.getNipam(), query.getPeriode()))
                .orderBy(GAJI_KPI.NIPAM.asc(), GAJI_KPI.PERIODE.asc())
                .fetch(GajiKpiJooqMapper::mapToResponse);
    }

    public Optional<GajiKpiResponse> getById(Long id) {
        return dsl.select(
                        GAJI_KPI.ID,
                        GAJI_KPI.NIPAM,
                        GAJI_KPI.PERIODE,
                        GAJI_KPI.TUNKIN,
                        GAJI_KPI.PPH21_TER)
                .from(GAJI_KPI)
                .where(GAJI_KPI.ID.eq(id))
                .and(GAJI_KPI.IS_DELETED.eq(false))
                .fetchOptional(GajiKpiJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nipam", GAJI_KPI.NIPAM,
                "periode", GAJI_KPI.PERIODE,
                "tunkin", GAJI_KPI.TUNKIN
        );
    }

    private Condition baseWhere(String nipam, String periode) {
        return GAJI_KPI.IS_DELETED.eq(false)
                .and(StringUtils.hasText(nipam) ? GAJI_KPI.NIPAM.likeIgnoreCase("%" + nipam + "%") : DSL.noCondition())
                .and(StringUtils.hasText(periode) ? GAJI_KPI.PERIODE.eq(periode) : DSL.noCondition());
    }
}

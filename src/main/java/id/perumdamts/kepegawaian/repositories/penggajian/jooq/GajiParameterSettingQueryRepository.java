package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiParameterSetting.GajiParameterSettingResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiParameterSetting.GajiParameterSettingJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiParameterSetting.GAJI_PARAMETER_SETTING;

@Repository
@RequiredArgsConstructor
public class GajiParameterSettingQueryRepository {
    private final DSLContext dsl;

    public Page<GajiParameterSettingResponse> pageQuery(GajiParameterSettingIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_PARAMETER_SETTING.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_PARAMETER_SETTING)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_PARAMETER_SETTING.ID,
                        GAJI_PARAMETER_SETTING.KODE,
                        GAJI_PARAMETER_SETTING.NOMINAL)
                .from(GAJI_PARAMETER_SETTING)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiParameterSettingJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiParameterSettingResponse> listQuery(GajiParameterSettingIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_PARAMETER_SETTING.KODE);
        return dsl.select(
                        GAJI_PARAMETER_SETTING.ID,
                        GAJI_PARAMETER_SETTING.KODE,
                        GAJI_PARAMETER_SETTING.NOMINAL)
                .from(GAJI_PARAMETER_SETTING)
                .where(baseWhere(query))
                .orderBy(sortOrder)
                .fetch(GajiParameterSettingJooqMapper::mapToResponse);
    }

    public Optional<GajiParameterSettingResponse> getById(Long id) {
        return dsl.select(
                        GAJI_PARAMETER_SETTING.ID,
                        GAJI_PARAMETER_SETTING.KODE,
                        GAJI_PARAMETER_SETTING.NOMINAL)
                .from(GAJI_PARAMETER_SETTING)
                .where(GAJI_PARAMETER_SETTING.ID.eq(id))
                .and(GAJI_PARAMETER_SETTING.IS_DELETED.eq(false))
                .fetchOptional(GajiParameterSettingJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", GAJI_PARAMETER_SETTING.KODE,
                "nominal", GAJI_PARAMETER_SETTING.NOMINAL
        );
    }

    private Condition baseWhere(GajiParameterSettingIndexQuery q) {
        return GAJI_PARAMETER_SETTING.IS_DELETED.eq(false)
                .and(q.getKode() != null && !q.getKode().isBlank() ? GAJI_PARAMETER_SETTING.KODE.likeIgnoreCase("%" + q.getKode() + "%") : DSL.noCondition());
    }
}

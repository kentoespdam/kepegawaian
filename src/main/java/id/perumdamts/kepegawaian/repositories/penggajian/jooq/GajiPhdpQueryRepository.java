package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPhdp.GajiPhdpResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPhdp.GajiPhdpJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiPhdp.GAJI_PHDP;

@Repository
@RequiredArgsConstructor
public class GajiPhdpQueryRepository {
    private final DSLContext dsl;

    public Page<GajiPhdpResponse> pageQuery(GajiPhdpIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_PHDP.URUT);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_PHDP)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_PHDP.ID,
                        GAJI_PHDP.URUT,
                        GAJI_PHDP.KONDISI,
                        GAJI_PHDP.FORMULA)
                .from(GAJI_PHDP)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiPhdpJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiPhdpResponse> listQuery() {
        return dsl.select(
                        GAJI_PHDP.ID,
                        GAJI_PHDP.URUT,
                        GAJI_PHDP.KONDISI,
                        GAJI_PHDP.FORMULA)
                .from(GAJI_PHDP)
                .where(GAJI_PHDP.IS_DELETED.eq(false))
                .orderBy(GAJI_PHDP.URUT.asc())
                .fetch(GajiPhdpJooqMapper::mapToResponse);
    }

    public Optional<GajiPhdpResponse> getById(Long id) {
        return dsl.select(
                        GAJI_PHDP.ID,
                        GAJI_PHDP.URUT,
                        GAJI_PHDP.KONDISI,
                        GAJI_PHDP.FORMULA)
                .from(GAJI_PHDP)
                .where(GAJI_PHDP.ID.eq(id))
                .and(GAJI_PHDP.IS_DELETED.eq(false))
                .fetchOptional(GajiPhdpJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "urut", GAJI_PHDP.URUT,
                "kondisi", GAJI_PHDP.KONDISI,
                "formula", GAJI_PHDP.FORMULA
        );
    }

    private Condition baseWhere(GajiPhdpIndexQuery q) {
        return GAJI_PHDP.IS_DELETED.eq(false)
                .and(q.getKondisi() != null && !q.getKondisi().isBlank() ? GAJI_PHDP.KONDISI.likeIgnoreCase("%" + q.getKondisi() + "%") : DSL.noCondition());
    }
}

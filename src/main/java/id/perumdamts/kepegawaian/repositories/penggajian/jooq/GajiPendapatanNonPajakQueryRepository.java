package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakListRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPendapatanNonPajak.GajiPendapatanNonPajakJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiPendapatanNonPajak.GAJI_PENDAPATAN_NON_PAJAK;

@Repository
@RequiredArgsConstructor
public class GajiPendapatanNonPajakQueryRepository {
    private final DSLContext dsl;

    public Page<GajiPendapatanNonPajakResponse> pageQuery(GajiPendapatanNonPajakIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_PENDAPATAN_NON_PAJAK.ID);
        Condition where = baseWhere(query.getKode());
        var count = dsl.selectCount()
                .from(GAJI_PENDAPATAN_NON_PAJAK)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_PENDAPATAN_NON_PAJAK.ID,
                        GAJI_PENDAPATAN_NON_PAJAK.KODE,
                        GAJI_PENDAPATAN_NON_PAJAK.NOMINAL,
                        GAJI_PENDAPATAN_NON_PAJAK.NOTES)
                .from(GAJI_PENDAPATAN_NON_PAJAK)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiPendapatanNonPajakJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<GajiPendapatanNonPajakResponse> listQuery(GajiPendapatanNonPajakListRequest query) {
        return dsl.select(
                        GAJI_PENDAPATAN_NON_PAJAK.ID,
                        GAJI_PENDAPATAN_NON_PAJAK.KODE,
                        GAJI_PENDAPATAN_NON_PAJAK.NOMINAL,
                        GAJI_PENDAPATAN_NON_PAJAK.NOTES)
                .from(GAJI_PENDAPATAN_NON_PAJAK)
                .where(baseWhere(query.getKode()))
                .orderBy(GAJI_PENDAPATAN_NON_PAJAK.KODE.asc())
                .fetch(GajiPendapatanNonPajakJooqMapper::mapToResponse);
    }

    public Optional<GajiPendapatanNonPajakResponse> getById(Long id) {
        return dsl.select(
                        GAJI_PENDAPATAN_NON_PAJAK.ID,
                        GAJI_PENDAPATAN_NON_PAJAK.KODE,
                        GAJI_PENDAPATAN_NON_PAJAK.NOMINAL,
                        GAJI_PENDAPATAN_NON_PAJAK.NOTES)
                .from(GAJI_PENDAPATAN_NON_PAJAK)
                .where(GAJI_PENDAPATAN_NON_PAJAK.ID.eq(id))
                .and(GAJI_PENDAPATAN_NON_PAJAK.IS_DELETED.eq(false))
                .fetchOptional(GajiPendapatanNonPajakJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", GAJI_PENDAPATAN_NON_PAJAK.KODE,
                "nominal", GAJI_PENDAPATAN_NON_PAJAK.NOMINAL
        );
    }

    private Condition baseWhere(String kode) {
        return GAJI_PENDAPATAN_NON_PAJAK.IS_DELETED.eq(false)
                .and(kode != null && !kode.isBlank() ? GAJI_PENDAPATAN_NON_PAJAK.KODE.likeIgnoreCase("%" + kode + "%") : DSL.noCondition());
    }
}

package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiTunjangan.GajiTunjanganResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiTunjangan.GajiTunjanganJooqMapper;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.GajiTunjangan.GAJI_TUNJANGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

@Repository
@RequiredArgsConstructor
public class GajiTunjanganQueryRepository {
    private final DSLContext dsl;

    public Page<GajiTunjanganResponse> pageQuery(EJenisTunjangan jenis, GajiTunjanganIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_TUNJANGAN.ID);
        Condition where = baseWhere(jenis, query);
        var count = dsl.selectCount()
                .from(GAJI_TUNJANGAN)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_TUNJANGAN.ID,
                        GAJI_TUNJANGAN.JENIS_TUNJANGAN,
                        GAJI_TUNJANGAN.LEVEL_ID,
                        GAJI_TUNJANGAN.GOLONGAN_ID,
                        GAJI_TUNJANGAN.NOMINAL,
                        LEVEL.NAMA,
                        GOLONGAN.GOLONGAN_,
                        GOLONGAN.PANGKAT)
                .from(GAJI_TUNJANGAN)
                .leftJoin(LEVEL).on(GAJI_TUNJANGAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(GAJI_TUNJANGAN.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiTunjanganJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<GajiTunjanganResponse> getByIdAndJenis(Long id, EJenisTunjangan jenis) {
        byte jenisByte = (byte) jenis.ordinal();
        return dsl.select(
                        GAJI_TUNJANGAN.ID,
                        GAJI_TUNJANGAN.JENIS_TUNJANGAN,
                        GAJI_TUNJANGAN.LEVEL_ID,
                        GAJI_TUNJANGAN.GOLONGAN_ID,
                        GAJI_TUNJANGAN.NOMINAL,
                        LEVEL.NAMA,
                        GOLONGAN.GOLONGAN_,
                        GOLONGAN.PANGKAT)
                .from(GAJI_TUNJANGAN)
                .leftJoin(LEVEL).on(GAJI_TUNJANGAN.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(GAJI_TUNJANGAN.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(GAJI_TUNJANGAN.ID.eq(id))
                .and(GAJI_TUNJANGAN.JENIS_TUNJANGAN.eq(jenisByte))
                .and(GAJI_TUNJANGAN.IS_DELETED.eq(false))
                .fetchOptional(GajiTunjanganJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "jenisTunjangan", GAJI_TUNJANGAN.JENIS_TUNJANGAN,
                "nominal", GAJI_TUNJANGAN.NOMINAL,
                "levelId", GAJI_TUNJANGAN.LEVEL_ID,
                "golonganId", GAJI_TUNJANGAN.GOLONGAN_ID
        );
    }

    private Condition baseWhere(EJenisTunjangan jenis, GajiTunjanganIndexQuery q) {
        byte jenisByte = (byte) jenis.ordinal();
        return GAJI_TUNJANGAN.IS_DELETED.eq(false)
                .and(GAJI_TUNJANGAN.JENIS_TUNJANGAN.eq(jenisByte))
                .and(q.getLevelId() != null ? GAJI_TUNJANGAN.LEVEL_ID.eq(q.getLevelId()) : DSL.noCondition())
                .and(q.getGolonganId() != null ? GAJI_TUNJANGAN.GOLONGAN_ID.eq(q.getGolonganId()) : DSL.noCondition());
    }
}

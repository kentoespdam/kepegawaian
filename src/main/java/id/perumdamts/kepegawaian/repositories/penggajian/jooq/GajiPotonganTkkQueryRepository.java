package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.gajiPotonganTkk.GajiPotonganTkkResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiPotonganTkk.GajiPotonganTkkJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.GajiPotonganTkk.GAJI_POTONGAN_TKK;
import static id.perumdamts.kepegawaian.jooq.tables.Golongan.GOLONGAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

@Repository
@RequiredArgsConstructor
public class GajiPotonganTkkQueryRepository {
    private final DSLContext dsl;

    public Page<GajiPotonganTkkResponse> pageQuery(GajiPotonganTkkIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_POTONGAN_TKK.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(GAJI_POTONGAN_TKK)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_POTONGAN_TKK.ID,
                        GAJI_POTONGAN_TKK.STATUS_PEGAWAI,
                        GAJI_POTONGAN_TKK.LEVEL_ID,
                        GAJI_POTONGAN_TKK.GOLONGAN_ID,
                        GAJI_POTONGAN_TKK.NOMINAL,
                        LEVEL.NAMA,
                        GOLONGAN.GOLONGAN_,
                        GOLONGAN.PANGKAT)
                .from(GAJI_POTONGAN_TKK)
                .leftJoin(LEVEL).on(GAJI_POTONGAN_TKK.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(GAJI_POTONGAN_TKK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(GajiPotonganTkkJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public Optional<GajiPotonganTkkResponse> getById(Long id) {
        return dsl.select(
                        GAJI_POTONGAN_TKK.ID,
                        GAJI_POTONGAN_TKK.STATUS_PEGAWAI,
                        GAJI_POTONGAN_TKK.LEVEL_ID,
                        GAJI_POTONGAN_TKK.GOLONGAN_ID,
                        GAJI_POTONGAN_TKK.NOMINAL,
                        LEVEL.NAMA,
                        GOLONGAN.GOLONGAN_,
                        GOLONGAN.PANGKAT)
                .from(GAJI_POTONGAN_TKK)
                .leftJoin(LEVEL).on(GAJI_POTONGAN_TKK.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GOLONGAN).on(GAJI_POTONGAN_TKK.GOLONGAN_ID.eq(GOLONGAN.ID))
                .where(GAJI_POTONGAN_TKK.ID.eq(id))
                .and(GAJI_POTONGAN_TKK.IS_DELETED.eq(false))
                .fetchOptional(GajiPotonganTkkJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "statusPegawai", GAJI_POTONGAN_TKK.STATUS_PEGAWAI,
                "nominal", GAJI_POTONGAN_TKK.NOMINAL,
                "levelId", GAJI_POTONGAN_TKK.LEVEL_ID,
                "golonganId", GAJI_POTONGAN_TKK.GOLONGAN_ID
        );
    }

    private Condition baseWhere(GajiPotonganTkkIndexQuery q) {
        Condition condition = GAJI_POTONGAN_TKK.IS_DELETED.eq(false);
        if (q.getStatusPegawai() != null) {
            byte statusByte = (byte) q.getStatusPegawai().ordinal();
            condition = condition.and(GAJI_POTONGAN_TKK.STATUS_PEGAWAI.eq(statusByte));
        }
        if (q.getLevelId() != null) {
            condition = condition.and(GAJI_POTONGAN_TKK.LEVEL_ID.eq(q.getLevelId()));
        }
        if (q.getGolonganId() != null) {
            condition = condition.and(GAJI_POTONGAN_TKK.GOLONGAN_ID.eq(q.getGolonganId()));
        }
        return condition;
    }
}

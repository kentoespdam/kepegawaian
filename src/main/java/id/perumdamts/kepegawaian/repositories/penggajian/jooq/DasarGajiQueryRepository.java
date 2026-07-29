package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.dasarGaji.DasarGajiResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.dasarGaji.DasarGajiJooqMapper;
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

import static id.perumdamts.kepegawaian.jooq.tables.DasarGaji.DASAR_GAJI;

@Repository
@RequiredArgsConstructor
public class DasarGajiQueryRepository {
    private final DSLContext dsl;

    public Page<DasarGajiResponse> pageQuery(DasarGajiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), DASAR_GAJI.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(DASAR_GAJI)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        DASAR_GAJI.ID,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DASAR_GAJI)
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(DasarGajiJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<DasarGajiResponse> listQuery() {
        return dsl.select(
                        DASAR_GAJI.ID,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DASAR_GAJI)
                .where(DASAR_GAJI.IS_DELETED.eq(false))
                .orderBy(DASAR_GAJI.ID.asc())
                .fetch(DasarGajiJooqMapper::mapToResponse);
    }

    public Optional<DasarGajiResponse> getById(Long id) {
        return dsl.select(
                        DASAR_GAJI.ID,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DASAR_GAJI)
                .where(DASAR_GAJI.ID.eq(id))
                .and(DASAR_GAJI.IS_DELETED.eq(false))
                .fetchOptional(DasarGajiJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "deskripsi", DASAR_GAJI.DESKRIPSI,
                "tanggalMulai", DASAR_GAJI.TANGGAL_AWAL,
                "tanggalAkhir", DASAR_GAJI.TANGGAL_AKHIR,
                "aktif", DASAR_GAJI.AKTIF
        );
    }

    private Condition baseWhere(DasarGajiIndexQuery q) {
        return DASAR_GAJI.IS_DELETED.eq(false)
                .and(q.getDeskripsi() != null ? DASAR_GAJI.DESKRIPSI.likeIgnoreCase("%" + q.getDeskripsi() + "%") : DSL.noCondition())
                .and(q.getTanggalAwal() != null ? DASAR_GAJI.TANGGAL_AWAL.ge(q.getTanggalAwal()) : DSL.noCondition())
                .and(q.getTanggalAkhir() != null ? DASAR_GAJI.TANGGAL_AKHIR.le(q.getTanggalAkhir()) : DSL.noCondition())
                .and(q.getAktif() != null ? DASAR_GAJI.AKTIF.eq(q.getAktif()) : DSL.noCondition());
    }
}

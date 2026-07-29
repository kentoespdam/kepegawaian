package id.perumdamts.kepegawaian.repositories.penggajian.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiIndexQuery;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiNominal;
import id.perumdamts.kepegawaian.dto.penggajian.detailDasarGaji.DetailDasarGajiResponse;
import id.perumdamts.kepegawaian.mapper.penggajian.detailDasarGaji.DetailDasarGajiJooqMapper;
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
import static id.perumdamts.kepegawaian.jooq.tables.DetailDasarGaji.DETAIL_DASAR_GAJI;

@Repository
@RequiredArgsConstructor
public class DetailDasarGajiQueryRepository {
    private final DSLContext dsl;

    public Page<DetailDasarGajiResponse> pageQuery(DetailDasarGajiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), DETAIL_DASAR_GAJI.ID);
        Condition where = baseWhere(query);
        var count = dsl.selectCount()
                .from(DETAIL_DASAR_GAJI)
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        DETAIL_DASAR_GAJI.ID,
                        DETAIL_DASAR_GAJI.DASAR_GAJI_ID,
                        DETAIL_DASAR_GAJI.MKG,
                        DETAIL_DASAR_GAJI.GOLONGAN_KODE,
                        DETAIL_DASAR_GAJI.NOMINAL,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DETAIL_DASAR_GAJI)
                .leftJoin(DASAR_GAJI).on(DETAIL_DASAR_GAJI.DASAR_GAJI_ID.eq(DASAR_GAJI.ID))
                .where(where)
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.getPageNumber() * query.getSizeOrDefault())
                .fetch(DetailDasarGajiJooqMapper::mapToResponse);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    public List<DetailDasarGajiResponse> listQuery(DetailDasarGajiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), DETAIL_DASAR_GAJI.ID);
        Condition where = baseWhere(query);
        return dsl.select(
                        DETAIL_DASAR_GAJI.ID,
                        DETAIL_DASAR_GAJI.DASAR_GAJI_ID,
                        DETAIL_DASAR_GAJI.MKG,
                        DETAIL_DASAR_GAJI.GOLONGAN_KODE,
                        DETAIL_DASAR_GAJI.NOMINAL,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DETAIL_DASAR_GAJI)
                .leftJoin(DASAR_GAJI).on(DETAIL_DASAR_GAJI.DASAR_GAJI_ID.eq(DASAR_GAJI.ID))
                .where(where)
                .orderBy(sortOrder)
                .fetch(DetailDasarGajiJooqMapper::mapToResponse);
    }

    public Optional<DetailDasarGajiResponse> getById(Long id) {
        return dsl.select(
                        DETAIL_DASAR_GAJI.ID,
                        DETAIL_DASAR_GAJI.DASAR_GAJI_ID,
                        DETAIL_DASAR_GAJI.MKG,
                        DETAIL_DASAR_GAJI.GOLONGAN_KODE,
                        DETAIL_DASAR_GAJI.NOMINAL,
                        DASAR_GAJI.DESKRIPSI,
                        DASAR_GAJI.TANGGAL_AWAL,
                        DASAR_GAJI.TANGGAL_AKHIR,
                        DASAR_GAJI.AKTIF)
                .from(DETAIL_DASAR_GAJI)
                .leftJoin(DASAR_GAJI).on(DETAIL_DASAR_GAJI.DASAR_GAJI_ID.eq(DASAR_GAJI.ID))
                .where(DETAIL_DASAR_GAJI.ID.eq(id))
                .and(DETAIL_DASAR_GAJI.IS_DELETED.eq(false))
                .fetchOptional(DetailDasarGajiJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "dasarGajiId", DETAIL_DASAR_GAJI.DASAR_GAJI_ID,
                "mkg", DETAIL_DASAR_GAJI.MKG,
                "golonganKode", DETAIL_DASAR_GAJI.GOLONGAN_KODE,
                "nominal", DETAIL_DASAR_GAJI.NOMINAL
        );
    }

    public Optional<DetailDasarGajiNominal> getNominalByGolonganAndMasaKerja(Integer golonganKode, Integer masaKerja) {
        return dsl.select(DETAIL_DASAR_GAJI.NOMINAL)
                .from(DETAIL_DASAR_GAJI)
                .where(DETAIL_DASAR_GAJI.GOLONGAN_KODE.eq(golonganKode))
                .and(DETAIL_DASAR_GAJI.MKG.eq(masaKerja))
                .and(DETAIL_DASAR_GAJI.IS_DELETED.eq(false))
                .fetchOptional(record -> new DetailDasarGajiNominal(record.get(DETAIL_DASAR_GAJI.NOMINAL)));
    }

    private Condition baseWhere(DetailDasarGajiIndexQuery q) {
        return DETAIL_DASAR_GAJI.IS_DELETED.eq(false)
                .and(q.getDasarGajiId() != null ? DETAIL_DASAR_GAJI.DASAR_GAJI_ID.eq(q.getDasarGajiId()) : DSL.noCondition())
                .and(q.getMkg() != null ? DETAIL_DASAR_GAJI.MKG.eq(q.getMkg()) : DSL.noCondition())
                .and(q.getGolonganKode() != null ? DETAIL_DASAR_GAJI.GOLONGAN_KODE.eq(q.getGolonganKode()) : DSL.noCondition())
                .and(q.getNominal() != null ? DETAIL_DASAR_GAJI.NOMINAL.eq(q.getNominal()) : DSL.noCondition());
    }
}

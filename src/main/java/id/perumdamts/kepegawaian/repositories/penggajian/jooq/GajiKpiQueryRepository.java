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

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.GajiKpi.GAJI_KPI;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;

@Repository
@RequiredArgsConstructor
public class GajiKpiQueryRepository {
    private final DSLContext dsl;

    public Page<GajiKpiResponse> pageQuery(GajiKpiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), GAJI_KPI.ID);
        Condition where = baseWhere(query.getSearch(), query.getPeriode(), query.getOrganisasiId());
        var count = dsl.selectCount()
                .from(GAJI_KPI)
                .leftJoin(PEGAWAI).on(GAJI_KPI.NIPAM.eq(PEGAWAI.NIPAM)
                        .and(PEGAWAI.IS_DELETED.isFalse()))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(where)
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        GAJI_KPI.ID,
                        GAJI_KPI.NIPAM,
                        GAJI_KPI.PERIODE,
                        GAJI_KPI.TUNKIN,
                        GAJI_KPI.PPH21_TER,
                        BIODATA.NAMA,
                        JABATAN.NAMA,
                        ORGANISASI.NAMA,
                        PEGAWAI.STATUS_PEGAWAI)
                .from(GAJI_KPI)
                .leftJoin(PEGAWAI).on(GAJI_KPI.NIPAM.eq(PEGAWAI.NIPAM)
                        .and(PEGAWAI.IS_DELETED.isFalse()))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
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
                        GAJI_KPI.PPH21_TER,
                        BIODATA.NAMA,
                        JABATAN.NAMA,
                        ORGANISASI.NAMA,
                        PEGAWAI.STATUS_PEGAWAI)
                .from(GAJI_KPI)
                .leftJoin(PEGAWAI).on(GAJI_KPI.NIPAM.eq(PEGAWAI.NIPAM)
                        .and(PEGAWAI.IS_DELETED.isFalse()))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(baseWhere(query.getSearch(), query.getPeriode(), query.getOrganisasiId()))
                .orderBy(BIODATA.NAMA.asc(), GAJI_KPI.PERIODE.asc())
                .fetch(GajiKpiJooqMapper::mapToResponse);
    }

    public Optional<GajiKpiResponse> getById(Long id) {
        return dsl.select(
                        GAJI_KPI.ID,
                        GAJI_KPI.NIPAM,
                        GAJI_KPI.PERIODE,
                        GAJI_KPI.TUNKIN,
                        GAJI_KPI.PPH21_TER,
                        BIODATA.NAMA,
                        JABATAN.NAMA,
                        ORGANISASI.NAMA,
                        PEGAWAI.STATUS_PEGAWAI)
                .from(GAJI_KPI)
                .leftJoin(PEGAWAI).on(GAJI_KPI.NIPAM.eq(PEGAWAI.NIPAM)
                        .and(PEGAWAI.IS_DELETED.isFalse()))
                .leftJoin(BIODATA).on(PEGAWAI.BIODATA_ID.eq(BIODATA.NIK))
                .leftJoin(JABATAN).on(PEGAWAI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(ORGANISASI).on(PEGAWAI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .where(GAJI_KPI.ID.eq(id))
                .fetchOptional(GajiKpiJooqMapper::mapToResponse);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "nama", BIODATA.NAMA,
                "nipam", GAJI_KPI.NIPAM,
                "id", GAJI_KPI.ID
        );
    }

    private Condition baseWhere(String search, String periode, Long organisasiId) {
        return (StringUtils.hasText(search)
                    ? GAJI_KPI.NIPAM.containsIgnoreCase(search).or(BIODATA.NAMA.containsIgnoreCase(search))
                    : DSL.noCondition())
                .and(StringUtils.hasText(periode) ? GAJI_KPI.PERIODE.eq(periode) : DSL.noCondition())
                .and(organisasiId != null ? PEGAWAI.ORGANISASI_ID.eq(organisasiId) : DSL.noCondition());
    }
}

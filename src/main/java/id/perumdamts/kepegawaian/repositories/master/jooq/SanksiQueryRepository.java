package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.commons.SortParam;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import id.perumdamts.kepegawaian.mapper.master.sanksi.SanksiJooqMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

@Repository
@RequiredArgsConstructor
public class SanksiQueryRepository {
    private final DSLContext dsl;

    public Page<SanksiQuery> pageQuery(SanksiIndexQuery query) {
        var sortOrder = SortParam.resolve(query.getSortBy(), query.getSortDirection(),
                allowedSorts(), SANKSI_SP.ID);
        var count = dsl.selectCount()
                .from(SANKSI_SP)
                .where(SANKSI_SP.IS_DELETED.eq(false))
                .and(query.getKode() != null ? SANKSI_SP.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getKeterangan() != null ? SANKSI_SP.KETERANGAN.likeIgnoreCase("%" + query.getKeterangan() + "%") : DSL.noCondition())
                .and(query.getJenisSpId() != null ? SANKSI_SP.JENIS_SP_ID.eq(query.getJenisSpId()) : DSL.noCondition())
                .fetchOptional(0, Long.class).orElse(0L);
        var data = dsl.select(
                        SANKSI_SP.ID,
                        SANKSI_SP.KODE,
                        SANKSI_SP.KETERANGAN,
                        SANKSI_SP.JENIS_SP_ID,
                        SANKSI_SP.POT_TKK,
                        SANKSI_SP.JML_POT_TKK,
                        SANKSI_SP.IS_PENDING_PANGKAT,
                        SANKSI_SP.IS_PENDING_GAJI,
                        SANKSI_SP.IS_TURUN_PANGKAT,
                        SANKSI_SP.IS_TURUN_JABATAN,
                        SANKSI_SP.IS_SUSPENSION,
                        SANKSI_SP.IS_TERMINATE_DH,
                        SANKSI_SP.IS_TERMINATE_TH,
                        JENIS_SP.ID.as("jenissp_id"),
                        JENIS_SP.KODE.as("jenissp_kode"),
                        JENIS_SP.NAMA.as("jenissp_nama"))
                .from(SANKSI_SP)
                .leftJoin(JENIS_SP).on(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .where(SANKSI_SP.IS_DELETED.eq(false))
                .and(query.getKode() != null ? SANKSI_SP.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getKeterangan() != null ? SANKSI_SP.KETERANGAN.likeIgnoreCase("%" + query.getKeterangan() + "%") : DSL.noCondition())
                .and(query.getJenisSpId() != null ? SANKSI_SP.JENIS_SP_ID.eq(query.getJenisSpId()) : DSL.noCondition())
                .orderBy(sortOrder)
                .limit(query.getSizeOrDefault())
                .offset(query.offset())
                .fetch(SanksiJooqMapper::mapToQuery);
        return new PageImpl<>(data, PageRequest.of(query.getPageNumber(), query.getSizeOrDefault()), count);
    }

    private static Map<String, Field<?>> allowedSorts() {
        return Map.of(
                "kode", SANKSI_SP.KODE,
                "keterangan", SANKSI_SP.KETERANGAN,
                "jenisSpId", SANKSI_SP.JENIS_SP_ID
        );
    }

    public Optional<SanksiQuery> getById(Long id) {
        return dsl.select(
                        SANKSI_SP.ID,
                        SANKSI_SP.KODE,
                        SANKSI_SP.KETERANGAN,
                        SANKSI_SP.JENIS_SP_ID,
                        SANKSI_SP.POT_TKK,
                        SANKSI_SP.JML_POT_TKK,
                        SANKSI_SP.IS_PENDING_PANGKAT,
                        SANKSI_SP.IS_PENDING_GAJI,
                        SANKSI_SP.IS_TURUN_PANGKAT,
                        SANKSI_SP.IS_TURUN_JABATAN,
                        SANKSI_SP.IS_SUSPENSION,
                        SANKSI_SP.IS_TERMINATE_DH,
                        SANKSI_SP.IS_TERMINATE_TH,
                        JENIS_SP.ID.as("jenissp_id"),
                        JENIS_SP.KODE.as("jenissp_kode"),
                        JENIS_SP.NAMA.as("jenissp_nama"))
                .from(SANKSI_SP)
                .leftJoin(JENIS_SP).on(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .where(SANKSI_SP.ID.eq(id))
                .and(SANKSI_SP.IS_DELETED.eq(false))
                .fetchOptional(SanksiJooqMapper::mapToQuery);
    }

    public List<SanksiQuery> listQuery() {
        return dsl.select(
                        SANKSI_SP.ID,
                        SANKSI_SP.KODE,
                        SANKSI_SP.KETERANGAN,
                        SANKSI_SP.JENIS_SP_ID,
                        SANKSI_SP.POT_TKK,
                        SANKSI_SP.JML_POT_TKK,
                        SANKSI_SP.IS_PENDING_PANGKAT,
                        SANKSI_SP.IS_PENDING_GAJI,
                        SANKSI_SP.IS_TURUN_PANGKAT,
                        SANKSI_SP.IS_TURUN_JABATAN,
                        SANKSI_SP.IS_SUSPENSION,
                        SANKSI_SP.IS_TERMINATE_DH,
                        SANKSI_SP.IS_TERMINATE_TH,
                        JENIS_SP.ID.as("jenissp_id"),
                        JENIS_SP.KODE.as("jenissp_kode"),
                        JENIS_SP.NAMA.as("jenissp_nama"))
                .from(SANKSI_SP)
                .leftJoin(JENIS_SP).on(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .where(SANKSI_SP.IS_DELETED.eq(false))
                .orderBy(SANKSI_SP.KODE.asc())
                .fetch(SanksiJooqMapper::mapToQuery);
    }

    public List<SanksiQuery> findByJenisSpId(Long jenisSpId) {
        return dsl.select(
                        SANKSI_SP.ID,
                        SANKSI_SP.KODE,
                        SANKSI_SP.KETERANGAN,
                        SANKSI_SP.JENIS_SP_ID,
                        SANKSI_SP.POT_TKK,
                        SANKSI_SP.JML_POT_TKK,
                        SANKSI_SP.IS_PENDING_PANGKAT,
                        SANKSI_SP.IS_PENDING_GAJI,
                        SANKSI_SP.IS_TURUN_PANGKAT,
                        SANKSI_SP.IS_TURUN_JABATAN,
                        SANKSI_SP.IS_SUSPENSION,
                        SANKSI_SP.IS_TERMINATE_DH,
                        SANKSI_SP.IS_TERMINATE_TH,
                        JENIS_SP.ID.as("jenissp_id"),
                        JENIS_SP.KODE.as("jenissp_kode"),
                        JENIS_SP.NAMA.as("jenissp_nama"))
                .from(SANKSI_SP)
                .leftJoin(JENIS_SP).on(SANKSI_SP.JENIS_SP_ID.eq(JENIS_SP.ID))
                .where(SANKSI_SP.JENIS_SP_ID.eq(jenisSpId))
                .and(SANKSI_SP.IS_DELETED.eq(false))
                .orderBy(SANKSI_SP.KODE.asc())
                .fetch(SanksiJooqMapper::mapToQuery);
    }

}

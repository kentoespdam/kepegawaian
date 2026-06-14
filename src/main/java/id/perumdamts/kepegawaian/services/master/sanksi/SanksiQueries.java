package id.perumdamts.kepegawaian.services.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpMiniResponse;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiIndexQuery;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.JenisSp.JENIS_SP;
import static id.perumdamts.kepegawaian.jooq.tables.SanksiSp.SANKSI_SP;

@Service
@RequiredArgsConstructor
public class SanksiQueries {
    private final DSLContext dsl;

    public Page<SanksiQuery> pageQuery(SanksiIndexQuery query) {
        var sortField = switch (query.getSortBy()) {
            case "kode" -> SANKSI_SP.KODE;
            case "keterangan" -> SANKSI_SP.KETERANGAN;
            case "jenisSpId" -> SANKSI_SP.JENIS_SP_ID;
            default -> SANKSI_SP.ID;
        };
        var sortOrder = "asc".equalsIgnoreCase(query.getSortDirection()) ? sortField.asc() : sortField.desc();
        var count = dsl.selectCount()
                .from(SANKSI_SP)
                .where(SANKSI_SP.IS_DELETED.eq(false))
                .and(query.getKode() != null ? SANKSI_SP.KODE.eq(query.getKode()) : DSL.noCondition())
                .and(query.getKeterangan() != null ? SANKSI_SP.KETERANGAN.likeIgnoreCase("%" + query.getKeterangan() + "%") : DSL.noCondition())
                .and(query.getJenisSpId() != null ? SANKSI_SP.JENIS_SP_ID.eq(query.getJenisSpId()) : DSL.noCondition())
                .fetchOne(0, Long.class);
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
                .limit(query.getSize())
                .offset(query.getPage() * query.getSize())
                .fetch(record -> toQuery(record.intoMap()));
        return new PageImpl<>(data, PageRequest.of(query.getPage(), query.getSize()), count);
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
                .fetchOptional(record -> toQuery(record.intoMap()));
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
                .fetch(record -> toQuery(record.intoMap()));
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
                .fetch(record -> toQuery(record.intoMap()));
    }

    private SanksiQuery toQuery(Map<String, Object> map) {
        var query = new SanksiQuery();
        query.setId((Long) map.get("id"));
        query.setKode((String) map.get("kode"));
        query.setKeterangan((String) map.get("keterangan"));
        query.setJenisSpId((Long) map.get("jenis_sp_id"));
        query.setPotTkk((Boolean) map.get("pot_tkk"));
        query.setJmlPotTkk((Integer) map.get("jml_pot_tkk"));
        query.setIsPendingPangkat((Boolean) map.get("is_pending_pangkat"));
        query.setIsPendingGaji((Boolean) map.get("is_pending_gaji"));
        query.setIsTurunPangkat((Boolean) map.get("is_turun_pangkat"));
        query.setIsTurunJabatan((Boolean) map.get("is_turun_jabatan"));
        query.setIsSuspension((Boolean) map.get("is_suspension"));
        query.setIsTerminateDh((Boolean) map.get("is_terminate_dh"));
        query.setIsTerminateTh((Boolean) map.get("is_terminate_th"));
        if (map.get("jenissp_id") != null) {
            var j = new JenisSpMiniResponse();
            j.setId((Long) map.get("jenissp_id"));
            j.setKode((String) map.get("jenissp_kode"));
            j.setNama((String) map.get("jenissp_nama"));
            query.setJenisSp(j);
        }
        return query;
    }
}

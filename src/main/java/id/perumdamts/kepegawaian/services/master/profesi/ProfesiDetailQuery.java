package id.perumdamts.kepegawaian.services.master.profesi;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.AlatKerjaRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ApdRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static id.perumdamts.kepegawaian.jooq.tables.AlatKerja.ALAT_KERJA;
import static id.perumdamts.kepegawaian.jooq.tables.Apd.APD;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;

@Service
@RequiredArgsConstructor
public class ProfesiDetailQuery {
    private final DSLContext dsl;

    public Optional<ProfesiDetail> getById(Long id) {
        return dsl.select(ProfesiSelects.PROFESI_COLUMNS)
                .select(
                        multiset(dsl.select(APD.ID, APD.NAMA)
                                .from(APD)
                                .where(APD.PROFESI_ID.eq(id))
                                .and(APD.IS_DELETED.eq(false))
                                .orderBy(APD.NAMA.asc()))
                                .as("apd_list")
                                .convertFrom(r -> r.map(mapping(ApdRow::new))),
                        multiset(dsl.select(ALAT_KERJA.ID, ALAT_KERJA.NAMA)
                                .from(ALAT_KERJA)
                                .where(ALAT_KERJA.PROFESI_ID.eq(id))
                                .and(ALAT_KERJA.IS_DELETED.eq(false))
                                .orderBy(ALAT_KERJA.NAMA.asc()))
                                .as("alat_kerja_list")
                                .convertFrom(r -> r.map(mapping(AlatKerjaRow::new))))
                .from(PROFESI)
                .leftJoin(ORGANISASI).on(PROFESI.ORGANISASI_ID.eq(ORGANISASI.ID))
                .leftJoin(JABATAN).on(PROFESI.JABATAN_ID.eq(JABATAN.ID))
                .leftJoin(LEVEL).on(PROFESI.LEVEL_ID.eq(LEVEL.ID))
                .leftJoin(GRADE).on(PROFESI.GRADE_ID.eq(GRADE.ID))
                .where(PROFESI.ID.eq(id))
                .and(PROFESI.IS_DELETED.eq(false))
                .fetchOptional(record -> toDetail(record.intoMap()));
    }

    @SuppressWarnings("unchecked")
    private ProfesiDetail toDetail(Map<String, Object> map) {
        var detail = new ProfesiDetail();
        detail.setId((Long) map.get("id"));
        detail.setOrganisasiId((Long) map.get("organisasi_id"));
        detail.setJabatanId((Long) map.get("jabatan_id"));
        detail.setLevelId((Long) map.get("level_id"));
        detail.setGradeId((Long) map.get("grade_id"));
        detail.setNama((String) map.get("nama"));
        detail.setDetail((String) map.get("detail"));
        detail.setResiko((String) map.get("resiko"));
        if (map.get("org_id") != null) {
            var o = new OrganisasiMiniResponse();
            o.setId((Long) map.get("org_id"));
            o.setKode((String) map.get("org_kode"));
            o.setNama((String) map.get("org_nama"));
            o.setShortName((String) map.get("org_short_name"));
            detail.setOrganisasi(o);
        }
        if (map.get("jabatan_id") != null) {
            var j = new JabatanMiniResponse();
            j.setId((Long) map.get("jabatan_id"));
            j.setKode((String) map.get("jabatan_kode"));
            j.setNama((String) map.get("jabatan_nama"));
            detail.setJabatan(j);
        }
        if (map.get("level_id") != null) {
            detail.setLevel(new LevelResponse((Long) map.get("level_id"), (String) map.get("level_nama")));
        }
        if (map.get("grade_id") != null) {
            var g = new GradeMiniResponse();
            g.setId((Long) map.get("grade_id"));
            g.setGrade((Integer) map.get("grade_grade"));
            g.setTukin((Double) map.get("grade_tukin"));
            detail.setGrade(g);
        }
        detail.setApdList((List<ApdRow>) map.get("apd_list"));
        detail.setAlatKerjaList((List<AlatKerjaRow>) map.get("alat_kerja_list"));
        return detail;
    }
}

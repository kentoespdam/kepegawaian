package id.perumdamts.kepegawaian.mapper.master.profesi;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.AlatKerjaRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ApdRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiQuery;

import java.util.List;
import java.util.Map;

public final class ProfesiJooqMapper {
    private ProfesiJooqMapper() {}

    public static ProfesiQuery toQuery(Map<String, Object> map) {
        var query = new ProfesiQuery();
        query.setId((Long) map.get("id"));
        query.setOrganisasiId((Long) map.get("organisasi_id"));
        query.setJabatanId((Long) map.get("self_jabatan_id"));
        query.setLevelId((Long) map.get("self_level_id"));
        query.setGradeId((Long) map.get("self_grade_id"));
        query.setNama((String) map.get("nama"));
        query.setDetail((String) map.get("detail"));
        query.setResiko((String) map.get("resiko"));
        if (map.get("org_id") != null) {
            var o = new OrganisasiMiniResponse();
            o.setId((Long) map.get("org_id"));
            o.setKode((String) map.get("org_kode"));
            o.setNama((String) map.get("org_nama"));
            o.setShortName((String) map.get("org_short_name"));
            query.setOrganisasi(o);
        }
        if (map.get("jabatan_id") != null) {
            var j = new JabatanMiniResponse();
            j.setId((Long) map.get("jabatan_id"));
            j.setKode((String) map.get("jabatan_kode"));
            j.setNama((String) map.get("jabatan_nama"));
            query.setJabatan(j);
        }
        if (map.get("level_id") != null) {
            query.setLevel(new LevelResponse((Long) map.get("level_id"), (String) map.get("level_nama")));
        }
        if (map.get("grade_id") != null) {
            var g = new GradeMiniResponse();
            g.setId((Long) map.get("grade_id"));
            g.setGrade((Integer) map.get("grade_grade"));
            g.setTukin((Double) map.get("grade_tukin"));
            query.setGrade(g);
        }
        return query;
    }

    @SuppressWarnings("unchecked")
    public static ProfesiDetail toDetail(Map<String, Object> map) {
        var detail = new ProfesiDetail();
        detail.setId((Long) map.get("id"));
        detail.setOrganisasiId((Long) map.get("organisasi_id"));
        detail.setJabatanId((Long) map.get("self_jabatan_id"));
        detail.setLevelId((Long) map.get("self_level_id"));
        detail.setGradeId((Long) map.get("self_grade_id"));
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

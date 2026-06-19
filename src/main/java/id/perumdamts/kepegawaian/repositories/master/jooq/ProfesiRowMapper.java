package id.perumdamts.kepegawaian.repositories.master.jooq;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiQuery;
import id.perumdamts.kepegawaian.jooq.tables.records.ProfesiRecord;
import org.jooq.Record;

import java.util.Map;

final class ProfesiRowMapper {
    private ProfesiRowMapper() {}

    static ProfesiQuery toQuery(Map<String, Object> map) {
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

    static Map<String, Object> intoMap(ProfesiRecord record) {
        return record.intoMap();
    }

    static Map<String, Object> intoMap(Record record) {
        return record.intoMap();
    }
}

package id.perumdamts.kepegawaian.mapper.master;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.repositories.master.jooq.SharedSelects;
import org.jooq.Record;

public final class SharedMappers {
    private SharedMappers() {}

    public static OrganisasiMiniResponse buildOrganisasi(Record record) {
        return new OrganisasiMiniResponse(
                record.get(SharedSelects.ORG_ID),
                record.get(SharedSelects.ORG_KODE),
                record.get(SharedSelects.ORG_NAMA),
                record.get(SharedSelects.ORG_SHORT_NAME));
    }

    public static JabatanMiniResponse buildJabatan(Record record) {
        return new JabatanMiniResponse(
                record.get(SharedSelects.JABATAN_ID),
                record.get(SharedSelects.JABATAN_KODE),
                null,
                record.get(SharedSelects.JABATAN_NAMA));
    }

    public static GradeMiniResponse buildGrade(Record record) {
        return new GradeMiniResponse(
                record.get(SharedSelects.GRADE_ID),
                record.get(SharedSelects.GRADE_GRADE),
                record.get(SharedSelects.GRADE_TUKIN));
    }

    public static LevelResponse buildLevel(Record record) {
        return new LevelResponse(
                record.get(SharedSelects.LEVEL_ID),
                record.get(SharedSelects.LEVEL_NAMA));
    }
}

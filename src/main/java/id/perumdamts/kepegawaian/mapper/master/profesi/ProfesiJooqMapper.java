package id.perumdamts.kepegawaian.mapper.master.profesi;

import id.perumdamts.kepegawaian.dto.master.grade.GradeMiniResponse;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanMiniResponse;
import id.perumdamts.kepegawaian.dto.master.level.LevelResponse;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiMiniResponse;
import id.perumdamts.kepegawaian.dto.master.profesi.AlatKerjaRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ApdRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiQuery;
import id.perumdamts.kepegawaian.repositories.master.jooq.ProfesiSelects;
import org.jooq.Record;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

public final class ProfesiJooqMapper {
    private ProfesiJooqMapper() {}

    /**
     * Typed Record mapper for ProfesiQuery — uses {@code record.get(Field)} instead
     * of {@code record.intoMap()} + raw casts for type safety.
     */
    public static ProfesiQuery toQuery(Record record) {
        return new ProfesiQuery(
                record.get(PROFESI.ID),
                record.get(PROFESI.NAMA),
                record.get(PROFESI.DETAIL),
                record.get(PROFESI.RESIKO),
                record.get(ProfesiSelects.ORG_ID) != null ? buildOrganisasi(record) : null,
                record.get(ProfesiSelects.JABATAN_ID_ALIAS) != null ? buildJabatan(record) : null,
                record.get(ProfesiSelects.LEVEL_ID_ALIAS) != null
                        ? new LevelResponse(record.get(ProfesiSelects.LEVEL_ID_ALIAS),
                        record.get(ProfesiSelects.LEVEL_NAMA))
                        : null,
                record.get(ProfesiSelects.GRADE_ID_ALIAS) != null ? buildGrade(record) : null
        );
    }

    /**
     * Typed Record mapper for ProfesiDetail — uses {@code record.get(Field)} instead
     * of {@code record.intoMap()} + raw casts for type safety.
     */
    @SuppressWarnings("unchecked")
    public static ProfesiDetail toDetail(Record record) {
        return new ProfesiDetail(
                record.get(PROFESI.ID),
                record.get(PROFESI.NAMA),
                record.get(PROFESI.DETAIL),
                record.get(PROFESI.RESIKO),
                record.get(ProfesiSelects.ORG_ID) != null ? buildOrganisasi(record) : null,
                record.get(ProfesiSelects.JABATAN_ID_ALIAS) != null ? buildJabatan(record) : null,
                record.get(ProfesiSelects.LEVEL_ID_ALIAS) != null
                        ? new LevelResponse(record.get(ProfesiSelects.LEVEL_ID_ALIAS),
                        record.get(ProfesiSelects.LEVEL_NAMA))
                        : null,
                record.get(ProfesiSelects.GRADE_ID_ALIAS) != null ? buildGrade(record) : null,
                (List<ApdRow>) record.get("apd_list"),
                (List<AlatKerjaRow>) record.get("alat_kerja_list")
        );
    }

    private static OrganisasiMiniResponse buildOrganisasi(Record record) {
        var o = new OrganisasiMiniResponse();
        o.setId(record.get(ProfesiSelects.ORG_ID));
        o.setKode(record.get(ProfesiSelects.ORG_KODE));
        o.setNama(record.get(ProfesiSelects.ORG_NAMA));
        o.setShortName(record.get(ProfesiSelects.ORG_SHORT_NAME));
        return o;
    }

    private static JabatanMiniResponse buildJabatan(Record record) {
        var j = new JabatanMiniResponse();
        j.setId(record.get(ProfesiSelects.JABATAN_ID_ALIAS));
        j.setKode(record.get(ProfesiSelects.JABATAN_KODE));
        j.setNama(record.get(ProfesiSelects.JABATAN_NAMA));
        return j;
    }

    private static GradeMiniResponse buildGrade(Record record) {
        var g = new GradeMiniResponse();
        g.setId(record.get(ProfesiSelects.GRADE_ID_ALIAS));
        g.setGrade(record.get(ProfesiSelects.GRADE_GRADE));
        g.setTukin(record.get(ProfesiSelects.GRADE_TUKIN));
        return g;
    }
}

package id.perumdamts.kepegawaian.mapper.master.profesi;

import id.perumdamts.kepegawaian.dto.master.profesi.AlatKerjaRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ApdRow;
import id.perumdamts.kepegawaian.dto.master.profesi.ProfesiDetail;
import id.perumdamts.kepegawaian.mapper.master.SharedMappers;
import id.perumdamts.kepegawaian.repositories.master.jooq.SharedSelects;
import org.jooq.Record;

import java.util.List;

import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

public final class ProfesiJooqMapper {
    private ProfesiJooqMapper() {}

    @SuppressWarnings("unchecked")
    public static ProfesiDetail toDetail(Record record) {
        return new ProfesiDetail(
                record.get(PROFESI.ID),
                record.get(PROFESI.NAMA),
                record.get(PROFESI.DETAIL),
                record.get(PROFESI.RESIKO),
                record.get(SharedSelects.ORG_ID) != null ? SharedMappers.buildOrganisasi(record) : null,
                record.get(SharedSelects.JABATAN_ID) != null ? SharedMappers.buildJabatan(record) : null,
                record.get(SharedSelects.LEVEL_ID) != null ? SharedMappers.buildLevel(record) : null,
                record.get(SharedSelects.GRADE_ID) != null ? SharedMappers.buildGrade(record) : null,
                (List<ApdRow>) record.get("apd_list"),
                (List<AlatKerjaRow>) record.get("alat_kerja_list")
        );
    }
}

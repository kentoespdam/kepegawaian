package id.perumdamts.kepegawaian.repositories.master.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

public final class ProfesiSelects {
    private ProfesiSelects() {}

    static final Field<?>[] PROFESI_QUERY_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            SharedSelects.ORG_ID,
            SharedSelects.ORG_KODE,
            SharedSelects.ORG_NAMA,
            SharedSelects.ORG_SHORT_NAME,
            SharedSelects.JABATAN_ID,
            SharedSelects.JABATAN_KODE,
            SharedSelects.JABATAN_NAMA,
            SharedSelects.LEVEL_ID,
            SharedSelects.LEVEL_NAMA,
            SharedSelects.GRADE_ID,
            SharedSelects.GRADE_GRADE,
            SharedSelects.GRADE_TUKIN
    };

    static final Field<?>[] PROFESI_DETAIL_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            SharedSelects.ORG_ID,
            SharedSelects.ORG_KODE,
            SharedSelects.ORG_NAMA,
            SharedSelects.ORG_SHORT_NAME,
            SharedSelects.JABATAN_ID,
            SharedSelects.JABATAN_KODE,
            SharedSelects.JABATAN_NAMA,
            SharedSelects.LEVEL_ID,
            SharedSelects.LEVEL_NAMA,
            SharedSelects.GRADE_ID,
            SharedSelects.GRADE_GRADE,
            SharedSelects.GRADE_TUKIN
    };
}

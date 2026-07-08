package id.perumdamts.kepegawaian.repositories.master.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

public final class ProfesiSelects {
    private ProfesiSelects() {}

    // Aliased fields for joined tables — public so mappers can use typed record.get(Field)
    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_KODE = ORGANISASI.KODE.as("org_kode");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");
    public static final Field<String> ORG_SHORT_NAME = ORGANISASI.SHORT_NAME.as("org_short_name");
    public static final Field<Long> JABATAN_ID_ALIAS = JABATAN.ID.as("jabatan_id");
    public static final Field<String> JABATAN_KODE = JABATAN.KODE.as("jabatan_kode");
    public static final Field<String> JABATAN_NAMA = JABATAN.NAMA.as("jabatan_nama");
    public static final Field<Long> LEVEL_ID_ALIAS = LEVEL.ID.as("level_id");
    public static final Field<String> LEVEL_NAMA = LEVEL.NAMA.as("level_nama");
    public static final Field<Long> GRADE_ID_ALIAS = GRADE.ID.as("grade_id");
    public static final Field<Integer> GRADE_GRADE = GRADE.GRADE_.as("grade_grade");
    public static final Field<Double> GRADE_TUKIN = GRADE.TUKIN.as("grade_tukin");

    // Self-referencing aliased fields — public for typed access in ProfesiJooqMapper
    public static final Field<Long> SELF_JABATAN_ID = PROFESI.JABATAN_ID.as("self_jabatan_id");
    public static final Field<Long> SELF_LEVEL_ID = PROFESI.LEVEL_ID.as("self_level_id");
    public static final Field<Long> SELF_GRADE_ID = PROFESI.GRADE_ID.as("self_grade_id");

    static final Field<?>[] PROFESI_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.ORGANISASI_ID,
            SELF_JABATAN_ID,
            SELF_LEVEL_ID,
            SELF_GRADE_ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            ORG_ID,
            ORG_KODE,
            ORG_NAMA,
            ORG_SHORT_NAME,
            JABATAN_ID_ALIAS,
            JABATAN_KODE,
            JABATAN_NAMA,
            LEVEL_ID_ALIAS,
            LEVEL_NAMA,
            GRADE_ID_ALIAS,
            GRADE_GRADE,
            GRADE_TUKIN
    };

    /**
     * Columns for ProfesiQuery — omits raw FK columns (organisasi_id, self_jabatan_id,
     * self_level_id, self_grade_id) that are not used by
     * {@link id.perumdamts.kepegawaian.mapper.master.profesi.ProfesiJooqMapper#toQuery(org.jooq.Record)}.
     */
    static final Field<?>[] PROFESI_QUERY_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            ORG_ID,
            ORG_KODE,
            ORG_NAMA,
            ORG_SHORT_NAME,
            JABATAN_ID_ALIAS,
            JABATAN_KODE,
            JABATAN_NAMA,
            LEVEL_ID_ALIAS,
            LEVEL_NAMA,
            GRADE_ID_ALIAS,
            GRADE_GRADE,
            GRADE_TUKIN
    };

    /**
     * Columns for ProfesiDetail query — omits self_* aliases that are not used
     * by {@link id.perumdamts.kepegawaian.mapper.master.profesi.ProfesiJooqMapper#toDetail(org.jooq.Record)}.
     */
    static final Field<?>[] PROFESI_DETAIL_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            ORG_ID,
            ORG_KODE,
            ORG_NAMA,
            ORG_SHORT_NAME,
            JABATAN_ID_ALIAS,
            JABATAN_KODE,
            JABATAN_NAMA,
            LEVEL_ID_ALIAS,
            LEVEL_NAMA,
            GRADE_ID_ALIAS,
            GRADE_GRADE,
            GRADE_TUKIN
    };
}

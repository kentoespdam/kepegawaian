package id.perumdamts.kepegawaian.repositories.master.jooq;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;

public final class SharedSelects {
    private SharedSelects() {}

    public static final Field<Long> ORG_ID = ORGANISASI.ID.as("org_id");
    public static final Field<String> ORG_KODE = ORGANISASI.KODE.as("org_kode");
    public static final Field<String> ORG_NAMA = ORGANISASI.NAMA.as("org_nama");
    public static final Field<String> ORG_SHORT_NAME = ORGANISASI.SHORT_NAME.as("org_short_name");

    public static final Field<Long> JABATAN_ID = JABATAN.ID.as("jabatan_id");
    public static final Field<String> JABATAN_KODE = JABATAN.KODE.as("jabatan_kode");
    public static final Field<String> JABATAN_NAMA = JABATAN.NAMA.as("jabatan_nama");

    public static final Field<Long> LEVEL_ID = LEVEL.ID.as("level_id");
    public static final Field<String> LEVEL_NAMA = LEVEL.NAMA.as("level_nama");

    public static final Field<Long> GRADE_ID = GRADE.ID.as("grade_id");
    public static final Field<Integer> GRADE_GRADE = GRADE.GRADE_.as("grade_grade");
    public static final Field<Double> GRADE_TUKIN = GRADE.TUKIN.as("grade_tukin");
}

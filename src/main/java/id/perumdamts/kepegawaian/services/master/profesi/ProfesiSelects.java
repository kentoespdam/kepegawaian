package id.perumdamts.kepegawaian.services.master.profesi;

import org.jooq.Field;

import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;
import static id.perumdamts.kepegawaian.jooq.tables.Organisasi.ORGANISASI;
import static id.perumdamts.kepegawaian.jooq.tables.Profesi.PROFESI;

final class ProfesiSelects {
    private ProfesiSelects() {}

    static final Field<?>[] PROFESI_COLUMNS = new Field[] {
            PROFESI.ID,
            PROFESI.ORGANISASI_ID,
            PROFESI.JABATAN_ID,
            PROFESI.LEVEL_ID,
            PROFESI.GRADE_ID,
            PROFESI.NAMA,
            PROFESI.DETAIL,
            PROFESI.RESIKO,
            ORGANISASI.ID.as("org_id"),
            ORGANISASI.KODE.as("org_kode"),
            ORGANISASI.NAMA.as("org_nama"),
            ORGANISASI.SHORT_NAME.as("org_short_name"),
            JABATAN.ID.as("jabatan_id"),
            JABATAN.KODE.as("jabatan_kode"),
            JABATAN.NAMA.as("jabatan_nama"),
            LEVEL.ID.as("level_id"),
            LEVEL.NAMA.as("level_nama"),
            GRADE.ID.as("grade_id"),
            GRADE.GRADE_.as("grade_grade"),
            GRADE.TUKIN.as("grade_tukin")
    };
}

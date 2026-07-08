package id.perumdamts.kepegawaian.repositories.master.jooq;

import org.jooq.Field;
import static id.perumdamts.kepegawaian.jooq.tables.Grade.GRADE;
import static id.perumdamts.kepegawaian.jooq.tables.Level.LEVEL;

public final class GradeSelects {
    private GradeSelects() {}

    public static final Field<Long> ID = GRADE.ID;
    public static final Field<Integer> GRADE_ = GRADE.GRADE_;
    public static final Field<Double> TUKIN = GRADE.TUKIN;

    public static final Field<Long> LEVEL_ID = LEVEL.ID.as("level_id");
    public static final Field<String> LEVEL_NAMA = LEVEL.NAMA.as("level_nama");

}

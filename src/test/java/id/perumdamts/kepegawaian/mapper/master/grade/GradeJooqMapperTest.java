package id.perumdamts.kepegawaian.mapper.master.grade;

import id.perumdamts.kepegawaian.dto.master.grade.GradeQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk {@link GradeJooqMapper#toQuery(Record)}.
 * Memvalidasi mapping dari JOOQ Record ke GradeQuery record termasuk null safety.
 */
class GradeJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<Integer> GRADE = DSL.field("grade", Integer.class);
    private static final Field<Double> TUKIN = DSL.field("tukin", Double.class);
    private static final Field<Long> LEVEL_ID = DSL.field("level_id", Long.class);
    private static final Field<String> LEVEL_NAMA = DSL.field("level_nama", String.class);

    private Record newRow() {
        return dsl.newRecord(ID, GRADE, TUKIN, LEVEL_ID, LEVEL_NAMA);
    }

    @Test
    void mapsAllFieldsWhenLevelPresent() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(GRADE, 5);
        row.set(TUKIN, 2_500_000.0);
        row.set(LEVEL_ID, 10L);
        row.set(LEVEL_NAMA, "Staff");

        GradeQuery q = GradeJooqMapper.toQuery(row);

        assertNotNull(q, "result must not be null");
        assertEquals(1L, q.id());
        assertEquals(5, q.grade());
        assertEquals(2_500_000.0, q.tukin(), 0.001);
        assertNotNull(q.level(), "level must not be null when level_id is present");
        assertEquals(10L, q.level().id());
        assertEquals("Staff", q.level().nama());
    }

    @Test
    void mapsNullLevelWhenLevelIdIsNull() {
        Record row = newRow();
        row.set(ID, 2L);
        row.set(GRADE, 3);
        row.set(TUKIN, 1_500_000.0);
        row.set(LEVEL_ID, (Long) null);
        row.set(LEVEL_NAMA, (String) null);

        GradeQuery q = GradeJooqMapper.toQuery(row);

        assertNotNull(q, "result must not be null");
        assertEquals(2L, q.id());
        assertEquals(3, q.grade());
        assertEquals(1_500_000.0, q.tukin(), 0.001);
        assertNull(q.level(), "level must be null when level_id is null");
    }

    @Test
    void mapsNullScalarsWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 3L);
        row.set(GRADE, (Integer) null);
        row.set(TUKIN, (Double) null);
        row.set(LEVEL_ID, (Long) null);
        row.set(LEVEL_NAMA, (String) null);

        GradeQuery q = assertDoesNotThrow(() -> GradeJooqMapper.toQuery(row),
                "must not throw on null scalar fields");

        assertEquals(3L, q.id());
        assertNull(q.grade(), "grade must be null");
        assertNull(q.tukin(), "tukin must be null");
        assertNull(q.level(), "level must be null");
    }

    @Test
    void preservesExactFieldValues() {
        Record row = newRow();
        row.set(ID, 99L);
        row.set(GRADE, 12);
        row.set(TUKIN, 5_000_000.0);
        row.set(LEVEL_ID, 7L);
        row.set(LEVEL_NAMA, "Kepala Divisi");

        GradeQuery q = GradeJooqMapper.toQuery(row);

        assertEquals(99L, q.id());
        assertEquals(12, q.grade());
        assertEquals(5_000_000.0, q.tukin(), 0.001);
        assertEquals(7L, q.level().id());
        assertEquals("Kepala Divisi", q.level().nama());
    }
}

package id.perumdamts.kepegawaian.mapper.master.hariLibur;

import id.perumdamts.kepegawaian.dto.master.hariLibur.HariLiburQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HariLiburJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<LocalDate> TANGGAL = DSL.field("tanggal", LocalDate.class);
    private static final Field<Byte> JENIS_LIBUR = DSL.field("jenis_libur", Byte.class);
    private static final Field<String> NOTES = DSL.field("notes", String.class);

    private Record newRow() {
        return dsl.newRecord(ID, TANGGAL, JENIS_LIBUR, NOTES);
    }

    @Test
    void mapsJenisLiburToEnumKey() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(TANGGAL, LocalDate.of(2026, 8, 17));
        row.set(JENIS_LIBUR, (byte) 0); // LIBUR_NASIONAL ordinal
        row.set(NOTES, "Hari Kemerdekaan");

        HariLiburQuery q = HariLiburJooqMapper.toQuery(row);

        assertNotNull(q);
        assertEquals(1L, q.id());
        assertEquals("LIBUR_NASIONAL", q.jenisLibur(), "should return enum key, not value");
        assertEquals("Hari Kemerdekaan", q.notes());
    }

    @Test
    void mapsCutiBersamaToEnumKey() {
        Record row = newRow();
        row.set(ID, 2L);
        row.set(TANGGAL, LocalDate.of(2026, 6, 1));
        row.set(JENIS_LIBUR, (byte) 1); // CUTI_BERSAMA ordinal
        row.set(NOTES, "Cuti bersama lebaran");

        HariLiburQuery q = HariLiburJooqMapper.toQuery(row);

        assertEquals("CUTI_BERSAMA", q.jenisLibur(), "should return enum key, not value");
    }

    @Test
    void mapsNullJenisLiburWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 3L);
        row.set(TANGGAL, LocalDate.of(2026, 1, 1));
        row.set(JENIS_LIBUR, (Byte) null);
        row.set(NOTES, null);

        HariLiburQuery q = assertDoesNotThrow(() -> HariLiburJooqMapper.toQuery(row));

        assertNotNull(q);
        assertNull(q.jenisLibur(), "null ordinal must produce null result");
    }
}

package id.perumdamts.kepegawaian.mapper.laporan.kepegawaian;

import id.perumdamts.kepegawaian.dto.laporan.kepegawaian.SoResponse;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static id.perumdamts.kepegawaian.jooq.tables.Biodata.BIODATA;
import static id.perumdamts.kepegawaian.jooq.tables.Jabatan.JABATAN;
import static id.perumdamts.kepegawaian.jooq.tables.Pegawai.PEGAWAI;
import static org.junit.jupiter.api.Assertions.*;

class SoRecordMapperTest {

    @Test
    void mapReadsAliasedFields() {
        // Simulate the aliased record that the query produces:
        // JABATAN.ID.as("key"), JABATAN.PARENT_ID.as("boss"), etc.
        var record = DSL.using(org.jooq.SQLDialect.MYSQL)
                .newRecord(
                        JABATAN.ID.as("key"),
                        DSL.coalesce(JABATAN.PARENT_ID, DSL.val(0L)).as("boss"),
                        JABATAN.LEVEL_ID.as("level"),
                        JABATAN.NAMA.as("jabatan"),
                        DSL.coalesce(BIODATA.NAMA, DSL.val("")).as("name"),
                        DSL.coalesce(PEGAWAI.NIPAM, DSL.val("")).as("nik")
                );

        record.set(JABATAN.ID.as("key"), 10L);
        record.set(DSL.coalesce(JABATAN.PARENT_ID, DSL.val(0L)).as("boss"), 5L);
        record.set(JABATAN.LEVEL_ID.as("level"), 3L);
        record.set(JABATAN.NAMA.as("jabatan"), "Manager");
        record.set(DSL.coalesce(BIODATA.NAMA, DSL.val("")).as("name"), "Budi");
        record.set(DSL.coalesce(PEGAWAI.NIPAM, DSL.val("")).as("nik"), "8903002");

        SoResponse result = SoRecordMapper.map(record);

        assertEquals(10L, result.key());
        assertEquals(5L, result.boss());
        assertEquals(3, result.level());
        assertEquals("Manager", result.jabatan());
        assertEquals("Budi", result.name());
        assertEquals("8903002", result.nik());
        assertNotNull(result.subordinates());
    }
}

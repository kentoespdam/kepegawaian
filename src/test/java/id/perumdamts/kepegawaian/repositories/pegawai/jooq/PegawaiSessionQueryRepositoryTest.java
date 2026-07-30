package id.perumdamts.kepegawaian.repositories.pegawai.jooq;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiResponseSession;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link PegawaiSessionQueryRepository#mapRow(org.jooq.Record)}.
 * Uses JOOQ mocking (DSL.using) — no database required.
 */
class PegawaiSessionQueryRepositoryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<String> NIPAM = DSL.field("nipam", String.class);
    private static final Field<String> NIK = DSL.field("nik", String.class);
    private static final Field<String> NAMA = DSL.field("nama", String.class);
    private static final Field<Byte> STATUS_PEGAWAI = DSL.field("status_pegawai", Byte.class);
    private static final Field<Long> JABATAN_ID = DSL.field("jabatan_id", Long.class);
    private static final Field<String> JABATAN_NAMA = DSL.field("jabatan_nama", String.class);
    private static final Field<Long> ORGANISASI_ID = DSL.field("organisasi_id", Long.class);
    private static final Field<String> ORGANISASI_NAMA = DSL.field("organisasi_nama", String.class);

    private Record newRow() {
        return dsl.newRecord(ID, NIPAM, NIK, NAMA, STATUS_PEGAWAI,
                JABATAN_ID, JABATAN_NAMA, ORGANISASI_ID, ORGANISASI_NAMA);
    }

    private PegawaiResponseSession map(Record r) {
        return PegawaiSessionQueryRepository.mapRow(r);
    }

    @Test
    void mapsFullRow() {
        Record row = newRow();
        row.set(ID, 1234L);
        row.set(NIPAM, "890300426");
        row.set(NIK, "3273012345678901");
        row.set(NAMA, "ABDUL AZIZ MIFTAHUDDIN, S.Kom.");
        row.set(STATUS_PEGAWAI, (byte) 0);       // KONTRAK
        row.set(JABATAN_ID, 22L);
        row.set(JABATAN_NAMA, "Supervisor Teknologi Informasi");
        row.set(ORGANISASI_ID, 7L);
        row.set(ORGANISASI_NAMA, "SUB BAG TEKNOLOGI INFORMASI");

        PegawaiResponseSession s = map(row);

        assertEquals(1234L, s.id());
        assertEquals("890300426", s.nipam());
        assertEquals("3273012345678901", s.nik());
        assertEquals("ABDUL AZIZ MIFTAHUDDIN, S.Kom.", s.nama());
        assertEquals("KONTRAK", s.statusPegawai());
        assertNotNull(s.jabatan());
        assertEquals(22L, s.jabatan().id());
        assertEquals("Supervisor Teknologi Informasi", s.jabatan().nama());
        assertNotNull(s.organisasi());
        assertEquals(7L, s.organisasi().id());
        assertEquals("SUB BAG TEKNOLOGI INFORMASI", s.organisasi().nama());
    }

    @Test
    void mapsNullStatusPegawai() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(NIPAM, "12345");
        row.set(STATUS_PEGAWAI, (Byte) null);
        row.set(NIK, "nik1");
        row.set(NAMA, "Test");

        PegawaiResponseSession s = map(row);

        assertNull(s.statusPegawai());
    }

    @Test
    void mapsAllEnumValues() {
        for (EStatusPegawai expected : EStatusPegawai.values()) {
            Record row = newRow();
            row.set(ID, (long) expected.ordinal());
            row.set(NIPAM, "nipam-" + expected.ordinal());
            row.set(NIK, "nik-" + expected.ordinal());
            row.set(NAMA, "Test " + expected.name());
            row.set(STATUS_PEGAWAI, (byte) expected.ordinal());

            PegawaiResponseSession s = map(row);

            assertEquals(expected.name(), s.statusPegawai(),
                    "statusPegawai must match .name() for " + expected);
        }
    }

    @Test
    void mapsNullJabatanToNull() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(NIPAM, "12345");
        row.set(NIK, "nik1");
        row.set(NAMA, "Test");
        row.set(JABATAN_ID, (Long) null);
        row.set(JABATAN_NAMA, (String) null);
        row.set(ORGANISASI_ID, 1L);
        row.set(ORGANISASI_NAMA, "Org");

        PegawaiResponseSession s = map(row);

        assertNull(s.jabatan());
        assertNotNull(s.organisasi());
    }

    @Test
    void mapsNullOrganisasiToNull() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(NIPAM, "12345");
        row.set(NIK, "nik1");
        row.set(NAMA, "Test");
        row.set(JABATAN_ID, 1L);
        row.set(JABATAN_NAMA, "Jabatan");
        row.set(ORGANISASI_ID, (Long) null);
        row.set(ORGANISASI_NAMA, (String) null);

        PegawaiResponseSession s = map(row);

        assertNotNull(s.jabatan());
        assertNull(s.organisasi());
    }
}

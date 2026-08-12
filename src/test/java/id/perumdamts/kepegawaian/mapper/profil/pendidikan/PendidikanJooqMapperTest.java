package id.perumdamts.kepegawaian.mapper.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression: GET /profil/pendidikan?biodataId={nik} returned 500 "defaultObj" when a
 * pendidikan row had NULL jenjang_id. Objects.requireNonNullElse(null, null) threw an NPE
 * whose internal param name "defaultObj" leaked into the response. The mapper must map such
 * rows null-safely. See bd kepegawaian-ouk.
 */
class PendidikanJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);
    private final PendidikanJooqMapper mapper = PendidikanJooqMapper.INSTANCE;

    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<String> BIODATA_ID = DSL.field("biodata_id", String.class);
    private static final Field<String> BIODATA_NIK = DSL.field("biodata_nik", String.class);
    private static final Field<String> BIODATA_NAMA = DSL.field("biodata_nama", String.class);
    private static final Field<Long> SELF_JENJANG_ID = DSL.field("self_jenjang_id", Long.class);
    private static final Field<Long> JENJANG_ID = DSL.field("jenjang_id", Long.class);
    private static final Field<String> JENJANG_NAMA = DSL.field("jenjang_nama", String.class);
    private static final Field<String> JENJANG_SHORT_NAME = DSL.field("jenjang_short_name", String.class);
    private static final Field<Integer> JENJANG_SEQ = DSL.field("jenjang_seq", Integer.class);
    private static final Field<Boolean> JENJANG_IS_STATISTIK = DSL.field("jenjang_is_statistik", Boolean.class);
    private static final Field<String> GELAR_DEPAN = DSL.field("gelar_depan", String.class);
    private static final Field<String> GELAR_BELAKANG = DSL.field("gelar_belakang", String.class);
    private static final Field<String> JURUSAN = DSL.field("jurusan", String.class);
    private static final Field<String> INSTITUSI = DSL.field("institusi", String.class);
    private static final Field<String> KOTA = DSL.field("kota", String.class);
    private static final Field<Integer> TAHUN_MASUK = DSL.field("tahun_masuk", Integer.class);
    private static final Field<Boolean> IS_LULUS = DSL.field("is_lulus", Boolean.class);
    private static final Field<Integer> TAHUN_LULUS = DSL.field("tahun_lulus", Integer.class);
    private static final Field<Double> GPA = DSL.field("gpa", Double.class);
    private static final Field<Byte> IS_LATEST = DSL.field("is_latest", Byte.class);
    private static final Field<Byte> DISETUJUI = DSL.field("disetujui", Byte.class);
    private static final Field<LocalDateTime> TANGGAL_PENGAJUAN = DSL.field("tanggal_pengajuan", LocalDateTime.class);
    private static final Field<LocalDateTime> TANGGAL_DISETUJUI = DSL.field("tanggal_disetujui", LocalDateTime.class);
    private static final Field<String> DISETUJUI_OLEH = DSL.field("disetujui_oleh", String.class);
    private static final Field<Byte> CHANGED_STATUS = DSL.field("changed_status", Byte.class);

    private Record newRow() {
        return dsl.newRecord(
                ID, BIODATA_ID, BIODATA_NIK, BIODATA_NAMA,
                SELF_JENJANG_ID, JENJANG_ID, JENJANG_NAMA, JENJANG_SHORT_NAME, JENJANG_SEQ, JENJANG_IS_STATISTIK,
                GELAR_DEPAN, GELAR_BELAKANG, JURUSAN, INSTITUSI, KOTA,
                TAHUN_MASUK, IS_LULUS, TAHUN_LULUS, GPA, IS_LATEST, DISETUJUI, TANGGAL_PENGAJUAN, TANGGAL_DISETUJUI, DISETUJUI_OLEH, CHANGED_STATUS);
    }

    @Test
    void mapsRowWithNullJenjangWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "1234567890");
        row.set(SELF_JENJANG_ID, (Long) null);
        row.set(JENJANG_ID, (Long) null);
        row.set(IS_LATEST, (byte) 1);

        PendidikanQuery q = assertDoesNotThrow(() -> mapper.map(row),
                "map() must not throw when both jenjang ids are null");
        assertNull(q.jenjangId(), "jenjangId must be null when row has no jenjang");
        assertNull(q.jenjangPendidikan(), "nested jenjang must be null when row has no jenjang");
        assertEquals(1L, q.id());
        assertEquals(Boolean.TRUE, q.isLatest());
    }

    @Test
    void mapsRowWithPopulatedJenjang() {
        Record row = newRow();
        row.set(ID, 2L);
        row.set(BIODATA_ID, "1234567890");
        row.set(SELF_JENJANG_ID, 7L);
        row.set(JENJANG_ID, 7L);
        row.set(JENJANG_NAMA, "Sarjana");
        row.set(JENJANG_SHORT_NAME, "S1");
        row.set(JENJANG_SEQ, 5);
        row.set(JENJANG_IS_STATISTIK, true);
        row.set(IS_LATEST, (byte) 0);

        PendidikanQuery q = mapper.map(row);
        assertEquals(7L, q.jenjangId());
        assertNotNull(q.jenjangPendidikan());
        assertEquals(7L, q.jenjangPendidikan().id());
        assertEquals("Sarjana", q.jenjangPendidikan().nama());
        assertEquals("S1", q.jenjangPendidikan().shortName());
        assertEquals(5, q.jenjangPendidikan().seq());
        assertEquals(Boolean.TRUE, q.jenjangPendidikan().isStatistik());
        assertEquals(Boolean.FALSE, q.isLatest());
    }

    @Test
    void mapsApprovalFieldsNullSafely() {
        Record row = newRow();
        row.set(ID, 4L);
        row.set(BIODATA_ID, "1234567890");

        PendidikanQuery q = mapper.map(row);
        assertEquals(Boolean.FALSE, q.disetujui(), "unset disetujui maps to false (pattern isLatest)");
        assertNull(q.tanggalPengajuan());
        assertNull(q.tanggalDisetujui());
        assertNull(q.disetujuiOleh());
    }

    @Test
    void mapsApprovalFieldsWhenSet() {
        LocalDateTime pengajuan = LocalDateTime.of(2026, 8, 12, 10, 0);
        LocalDateTime disetujui = LocalDateTime.of(2026, 8, 12, 11, 30);
        Record row = newRow();
        row.set(ID, 5L);
        row.set(BIODATA_ID, "1234567890");
        row.set(DISETUJUI, (byte) 1);
        row.set(TANGGAL_PENGAJUAN, pengajuan);
        row.set(TANGGAL_DISETUJUI, disetujui);
        row.set(DISETUJUI_OLEH, "user-42");

        PendidikanQuery q = mapper.map(row);
        assertEquals(Boolean.TRUE, q.disetujui());
        assertEquals(pengajuan, q.tanggalPengajuan());
        assertEquals(disetujui, q.tanggalDisetujui());
        assertEquals("user-42", q.disetujuiOleh());
    }

    @Test
    void fallsBackToJoinedJenjangIdWhenSelfNull() {
        Record row = newRow();
        row.set(ID, 3L);
        row.set(SELF_JENJANG_ID, (Long) null);
        row.set(JENJANG_ID, 9L);
        row.set(JENJANG_NAMA, "Magister");

        PendidikanQuery q = mapper.map(row);
        assertEquals(9L, q.jenjangId(), "must fall back to joined jenjang_id when self is null");
        assertNotNull(q.jenjangPendidikan());
    }
}

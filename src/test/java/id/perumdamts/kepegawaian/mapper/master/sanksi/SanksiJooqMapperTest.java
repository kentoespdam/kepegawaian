package id.perumdamts.kepegawaian.mapper.master.sanksi;

import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpSimple;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiJenisSpList;
import id.perumdamts.kepegawaian.dto.master.sanksi.SanksiQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test untuk {@link SanksiJooqMapper#toQuery(Record)}.
 * Memvalidasi mapping dari JOOQ Record ke SanksiQuery record termasuk null safety.
 */
class SanksiJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    // Sanksi fields
    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<String> KODE = DSL.field("kode", String.class);
    private static final Field<String> KETERANGAN = DSL.field("keterangan", String.class);
    private static final Field<Boolean> POT_TKK = DSL.field("pot_tkk", Boolean.class);
    private static final Field<Integer> JML_POT_TKK = DSL.field("jml_pot_tkk", Integer.class);
    private static final Field<Boolean> IS_PENDING_PANGKAT = DSL.field("is_pending_pangkat", Boolean.class);
    private static final Field<Boolean> IS_PENDING_GAJI = DSL.field("is_pending_gaji", Boolean.class);
    private static final Field<Boolean> IS_TURUN_PANGKAT = DSL.field("is_turun_pangkat", Boolean.class);
    private static final Field<Boolean> IS_TURUN_JABATAN = DSL.field("is_turun_jabatan", Boolean.class);
    private static final Field<Boolean> IS_SUSPENSION = DSL.field("is_suspension", Boolean.class);
    private static final Field<Boolean> IS_TERMINATE_DH = DSL.field("is_terminate_dh", Boolean.class);
    private static final Field<Boolean> IS_TERMINATE_TH = DSL.field("is_terminate_th", Boolean.class);

    // JenisSp fields (aliased)
    private static final Field<Long> JENIS_SP_ID = DSL.field("jenissp_id", Long.class);
    private static final Field<String> JENIS_SP_KODE = DSL.field("jenissp_kode", String.class);
    private static final Field<String> JENIS_SP_NAMA = DSL.field("jenissp_nama", String.class);

    private Record newRow() {
        return dsl.newRecord(
                ID, KODE, KETERANGAN, POT_TKK, JML_POT_TKK,
                IS_PENDING_PANGKAT, IS_PENDING_GAJI,
                IS_TURUN_PANGKAT, IS_TURUN_JABATAN,
                IS_SUSPENSION, IS_TERMINATE_DH, IS_TERMINATE_TH,
                JENIS_SP_ID, JENIS_SP_KODE, JENIS_SP_NAMA);
    }

    private void setBooleans(Record row, boolean value) {
        row.set(POT_TKK, value);
        row.set(IS_PENDING_PANGKAT, value);
        row.set(IS_PENDING_GAJI, value);
        row.set(IS_TURUN_PANGKAT, value);
        row.set(IS_TURUN_JABATAN, value);
        row.set(IS_SUSPENSION, value);
        row.set(IS_TERMINATE_DH, value);
        row.set(IS_TERMINATE_TH, value);
    }

    @Test
    void mapsAllFieldsWhenJenisSpPresent() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(KODE, "SP01");
        row.set(KETERANGAN, "Peringatan Tertulis");
        row.set(JML_POT_TKK, 5);
        setBooleans(row, true);
        row.set(JENIS_SP_ID, 2L);
        row.set(JENIS_SP_KODE, "JS01");
        row.set(JENIS_SP_NAMA, "Ringan");

        SanksiQuery q = SanksiJooqMapper.toQuery(row);

        assertNotNull(q, "result must not be null");
        assertEquals(1L, q.id());
        assertEquals("SP01", q.kode());
        assertEquals("Peringatan Tertulis", q.keterangan());
        assertEquals(5, q.jmlPotTkk());
        assertTrue(q.potTkk());
        assertTrue(q.isPendingPangkat());
        assertTrue(q.isPendingGaji());
        assertTrue(q.isTurunPangkat());
        assertTrue(q.isTurunJabatan());
        assertTrue(q.isSuspension());
        assertTrue(q.isTerminateDh());
        assertTrue(q.isTerminateTh());

        assertNotNull(q.jenisSp(), "jenisSp must not be null when jenisSp_id is present");
        assertEquals(2L, q.jenisSp().id());
        assertEquals("JS01", q.jenisSp().kode());
        assertEquals("Ringan", q.jenisSp().nama());
    }

    @Test
    void mapsNullJenisSpWhenJenisSpIdIsNull() {
        Record row = newRow();
        row.set(ID, 2L);
        row.set(KODE, "SP02");
        row.set(KETERANGAN, "Peringatan Keras");
        setBooleans(row, false);
        row.set(JENIS_SP_ID, (Long) null);
        row.set(JENIS_SP_KODE, (String) null);
        row.set(JENIS_SP_NAMA, (String) null);

        SanksiQuery q = SanksiJooqMapper.toQuery(row);

        assertNotNull(q, "result must not be null");
        assertEquals(2L, q.id());
        assertFalse(q.potTkk());
        assertNull(q.jenisSp(), "jenisSp must be null when jenisSp_id is null");
    }

    @Test
    void mapsAllBooleansFalse() {
        Record row = newRow();
        row.set(ID, 3L);
        row.set(KODE, "SP03");
        setBooleans(row, false);
        row.set(JENIS_SP_ID, (Long) null);

        SanksiQuery q = SanksiJooqMapper.toQuery(row);

        assertFalse(q.potTkk());
        assertFalse(q.isPendingPangkat());
        assertFalse(q.isPendingGaji());
        assertFalse(q.isTurunPangkat());
        assertFalse(q.isTurunJabatan());
        assertFalse(q.isSuspension());
        assertFalse(q.isTerminateDh());
        assertFalse(q.isTerminateTh());
    }

    @Test
    void mapsNullBooleansWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 4L);
        row.set(KODE, "SP04");
        row.set(POT_TKK, (Boolean) null);
        row.set(IS_PENDING_PANGKAT, (Boolean) null);
        row.set(IS_PENDING_GAJI, (Boolean) null);
        row.set(IS_TURUN_PANGKAT, (Boolean) null);
        row.set(IS_TURUN_JABATAN, (Boolean) null);
        row.set(IS_SUSPENSION, (Boolean) null);
        row.set(IS_TERMINATE_DH, (Boolean) null);
        row.set(IS_TERMINATE_TH, (Boolean) null);
        row.set(JENIS_SP_ID, (Long) null);

        SanksiQuery q = assertDoesNotThrow(() -> SanksiJooqMapper.toQuery(row),
                "must not throw on null boolean fields");

        assertNull(q.potTkk(), "potTkk must be null");
        assertNull(q.isPendingPangkat(), "isPendingPangkat must be null");
        assertNull(q.isSuspension(), "isSuspension must be null");
        assertNull(q.jmlPotTkk(), "jmlPotTkk must be null when not set");
        assertNull(q.jenisSp(), "jenisSp must be null");
    }

    @Test
    void mapsNullJmlPotTkkWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 5L);
        row.set(KODE, "SP05");
        row.set(JML_POT_TKK, (Integer) null);
        row.set(JENIS_SP_ID, (Long) null);

        SanksiQuery q = assertDoesNotThrow(() -> SanksiJooqMapper.toQuery(row),
                "must not throw on null jmlPotTkk");

        assertNull(q.jmlPotTkk(), "jmlPotTkk must be null");
    }

    // --- toJenisSpList ---

    @Test
    void toJenisSpListMapsNestedJenisSp() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(KODE, "SP01");
        row.set(KETERANGAN, "Peringatan Tertulis");
        row.set(JML_POT_TKK, 5);
        setBooleans(row, true);
        row.set(JENIS_SP_ID, 2L);
        row.set(JENIS_SP_KODE, "JS01");
        row.set(JENIS_SP_NAMA, "Ringan");

        SanksiJenisSpList q = SanksiJooqMapper.toJenisSpList(row);

        assertEquals(1L, q.id());
        assertEquals("SP01", q.kode());
        assertEquals("Peringatan Tertulis", q.keterangan());
        assertTrue(q.potTkk());
        assertEquals(5, q.jmlPotTkk());

        // Nested jenisSp object — tanpa sanksiSp circular reference
        assertNotNull(q.jenisSp(), "jenisSp must not be null");
        assertEquals(2L, q.jenisSp().id());
        assertEquals("JS01", q.jenisSp().kode());
        assertEquals("Ringan", q.jenisSp().nama());
        // Pastikan tidak ada field sanksiSp
        assertDoesNotThrow(() -> {
            var methods = JenisSpSimple.class.getDeclaredMethods();
            for (var m : methods) {
                assertFalse(m.getName().contains("sanksiSp"),
                        "JenisSpSimple must not have sanksiSp field");
            }
        });
    }

    @Test
    void toJenisSpListMapsNullJenisSp() {
        Record row = newRow();
        row.set(ID, 2L);
        row.set(KODE, "SP02");
        row.set(KETERANGAN, "Peringatan Keras");
        setBooleans(row, false);
        row.set(JENIS_SP_ID, (Long) null);
        row.set(JENIS_SP_KODE, (String) null);
        row.set(JENIS_SP_NAMA, (String) null);

        SanksiJenisSpList q = SanksiJooqMapper.toJenisSpList(row);

        assertNull(q.jenisSp(), "jenisSp must be null when jenisSp_id is null");
    }
}

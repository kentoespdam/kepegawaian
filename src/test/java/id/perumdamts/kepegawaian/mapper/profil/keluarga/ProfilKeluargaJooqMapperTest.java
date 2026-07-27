package id.perumdamts.kepegawaian.mapper.profil.keluarga;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link ProfilKeluargaJooqMapper#map(Record)}.
 * Uses JOOQ mocking (DSL.using) — no database required.
 * <p>
 * Regression: {@code Objects.requireNonNullElse(selfPendidikanId, pendidikanId)}
 * threw NPE("defaultObj") when both values were null — fixed with ternary
 * (kepegawaian-ws8 post-fix).
 */
class ProfilKeluargaJooqMapperTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<Long> ID = DSL.field("id", Long.class);
    private static final Field<String> BIODATA_ID = DSL.field("biodata_id", String.class);
    private static final Field<String> BIODATA_NIK = DSL.field("biodata_nik", String.class);
    private static final Field<String> BIODATA_NAMA = DSL.field("biodata_nama", String.class);
    private static final Field<String> NIK = DSL.field("nik", String.class);
    private static final Field<String> NAMA = DSL.field("nama", String.class);
    private static final Field<Byte> JENIS_KELAMIN = DSL.field("jenis_kelamin", Byte.class);
    private static final Field<Byte> AGAMA = DSL.field("agama", Byte.class);
    private static final Field<Byte> HUBUNGAN_KELUARGA = DSL.field("hubungan_keluarga", Byte.class);
    private static final Field<String> TEMPAT_LAHIR = DSL.field("tempat_lahir", String.class);
    private static final Field<java.time.LocalDate> TANGGAL_LAHIR = DSL.field("tanggal_lahir", java.time.LocalDate.class);
    private static final Field<Boolean> TANGGUNGAN = DSL.field("tanggungan", Boolean.class);
    private static final Field<Long> SELF_PENDIDIKAN_ID = DSL.field("self_pendidikan_id", Long.class);
    private static final Field<Long> PENDIDIKAN_ID = DSL.field("pendidikan_id", Long.class);
    private static final Field<String> PENDIDIKAN_NAMA = DSL.field("pendidikan_nama", String.class);
    private static final Field<String> PENDIDIKAN_SHORT_NAME = DSL.field("pendidikan_short_name", String.class);
    private static final Field<Integer> PENDIDIKAN_SEQ = DSL.field("pendidikan_seq", Integer.class);
    private static final Field<Boolean> PENDIDIKAN_IS_STATISTIK = DSL.field("pendidikan_is_statistik", Boolean.class);
    private static final Field<Byte> STATUS_PENDIDIKAN = DSL.field("status_pendidikan", Byte.class);
    private static final Field<Boolean> STATUS_KAWIN = DSL.field("status_kawin", Boolean.class);
    private static final Field<String> NOTES = DSL.field("notes", String.class);
    private static final Field<Integer> VERSION = DSL.field("version", Integer.class);
    private static final Field<Boolean> IS_DELETED = DSL.field("is_deleted", Boolean.class);
    private static final Field<Boolean> CHANGED_STATUS = DSL.field("changed_status", Boolean.class);

    private Record newRow() {
        return dsl.newRecord(ID, BIODATA_ID, BIODATA_NIK, BIODATA_NAMA, NIK, NAMA,
                JENIS_KELAMIN, AGAMA, HUBUNGAN_KELUARGA, TEMPAT_LAHIR, TANGGAL_LAHIR,
                TANGGUNGAN, SELF_PENDIDIKAN_ID, PENDIDIKAN_ID, PENDIDIKAN_NAMA,
                PENDIDIKAN_SHORT_NAME, PENDIDIKAN_SEQ, PENDIDIKAN_IS_STATISTIK,
                STATUS_PENDIDIKAN, STATUS_KAWIN, NOTES, VERSION, IS_DELETED, CHANGED_STATUS);
    }

    @Test
    void mapsBothPendidikanIdsNullWithoutThrowing() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Anggota Keluarga");
        row.set(SELF_PENDIDIKAN_ID, (Long) null);
        row.set(PENDIDIKAN_ID, (Long) null);

        assertDoesNotThrow(() -> ProfilKeluargaJooqMapper.INSTANCE.map(row),
                "map() must not throw when both pendidikan IDs are null");
    }

    @Test
    void mapsNullPendidikanIdsToNullPendidikanId() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Anggota Keluarga");
        row.set(SELF_PENDIDIKAN_ID, (Long) null);
        row.set(PENDIDIKAN_ID, (Long) null);

        ProfilKeluargaQuery result = ProfilKeluargaJooqMapper.INSTANCE.map(row);

        assertNull(result.pendidikanId(),
                "pendidikanId must be null when both self_pendidikan_id and pendidikan_id are null");
        assertNull(result.jenjangPendidikan(),
                "jenjangPendidikan must be null when pendidikan_id is null");
    }

    @Test
    void preferencesSelfPendidikanIdOverPendidikanId() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Anggota");
        row.set(SELF_PENDIDIKAN_ID, 10L);
        row.set(PENDIDIKAN_ID, 20L);
        row.set(PENDIDIKAN_NAMA, "SD");
        row.set(PENDIDIKAN_SHORT_NAME, "SD");
        row.set(PENDIDIKAN_SEQ, 1);
        row.set(PENDIDIKAN_IS_STATISTIK, true);

        ProfilKeluargaQuery result = ProfilKeluargaJooqMapper.INSTANCE.map(row);

        assertEquals(10L, result.pendidikanId(),
                "pendidikanId must prefer self_pendidikan_id over pendidikan_id");
        assertNotNull(result.jenjangPendidikan());
        assertEquals(20L, result.jenjangPendidikan().id(),
                "jenjangPendidikan should still reference pendidikan_id (the JENJANG_PENDIDIKAN join)");
    }

    @Test
    void fallsBackToPendidikanIdWhenSelfIsNull() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Anggota");
        row.set(SELF_PENDIDIKAN_ID, (Long) null);
        row.set(PENDIDIKAN_ID, 30L);
        row.set(PENDIDIKAN_NAMA, "SMA");
        row.set(PENDIDIKAN_SHORT_NAME, "SMA");
        row.set(PENDIDIKAN_SEQ, 3);
        row.set(PENDIDIKAN_IS_STATISTIK, false);

        ProfilKeluargaQuery result = ProfilKeluargaJooqMapper.INSTANCE.map(row);

        assertEquals(30L, result.pendidikanId(),
                "pendidikanId must fall back to pendidikan_id when self_pendidikan_id is null");
        assertNotNull(result.jenjangPendidikan());
        assertEquals("SMA", result.jenjangPendidikan().nama());
    }

    @Test
    void mapsNullEnumsToNull() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Anggota");
        row.set(JENIS_KELAMIN, (Byte) null);
        row.set(AGAMA, (Byte) null);
        row.set(HUBUNGAN_KELUARGA, (Byte) null);
        row.set(STATUS_PENDIDIKAN, (Byte) null);

        ProfilKeluargaQuery result = ProfilKeluargaJooqMapper.INSTANCE.map(row);

        assertNull(result.jenisKelamin());
        assertNull(result.agama());
        assertNull(result.hubunganKeluarga());
        assertNull(result.statusPendidikan());
    }

    @Test
    void mapsFullRow() {
        Record row = newRow();
        row.set(ID, 1L);
        row.set(BIODATA_ID, "BIODATA001");
        row.set(BIODATA_NIK, "BIODATA001");
        row.set(BIODATA_NAMA, "Bapak Budi");
        row.set(NIK, "NIK001");
        row.set(NAMA, "Ani");
        row.set(JENIS_KELAMIN, (byte) 1);             // PEREMPUAN
        row.set(AGAMA, (byte) 1);                      // ISLAM
        row.set(HUBUNGAN_KELUARGA, (byte) 1);          // ISTRI
        row.set(TEMPAT_LAHIR, "Jakarta");
        row.set(TANGGAL_LAHIR, java.time.LocalDate.of(1995, 5, 10));
        row.set(TANGGUNGAN, true);
        row.set(SELF_PENDIDIKAN_ID, (Long) null);
        row.set(PENDIDIKAN_ID, 30L);
        row.set(PENDIDIKAN_NAMA, "SMA");
        row.set(PENDIDIKAN_SHORT_NAME, "SMA");
        row.set(PENDIDIKAN_SEQ, 3);
        row.set(PENDIDIKAN_IS_STATISTIK, false);
        row.set(STATUS_PENDIDIKAN, (byte) 0);          // BELUM_SEKOLAH
        row.set(STATUS_KAWIN, true);
        row.set(NOTES, "Catatan");
        row.set(VERSION, 0);
        row.set(IS_DELETED, false);
        row.set(CHANGED_STATUS, false);

        ProfilKeluargaQuery result = ProfilKeluargaJooqMapper.INSTANCE.map(row);

        assertEquals(1L, result.id());
        assertEquals("BIODATA001", result.biodataId());
        assertEquals("BIODATA001", result.biodataNik());
        assertEquals("Bapak Budi", result.biodataNama());
        assertEquals("NIK001", result.nik());
        assertEquals("Ani", result.nama());
        assertEquals("PEREMPUAN", result.jenisKelamin());
        assertEquals("ISLAM", result.agama());
        assertEquals("ISTRI", result.hubunganKeluarga());
        assertEquals("Jakarta", result.tempatLahir());
        assertEquals(java.time.LocalDate.of(1995, 5, 10), result.tanggalLahir());
        assertTrue(result.tanggungan());
        assertEquals(30L, result.pendidikanId());
        assertNotNull(result.jenjangPendidikan());
        assertEquals("SMA", result.jenjangPendidikan().nama());
        assertEquals("BELUM_SEKOLAH", result.statusPendidikan());
        assertTrue(result.statusKawin());
        assertEquals("Catatan", result.notes());
        assertEquals(0, result.version());
        assertFalse(result.isDeleted());
        assertFalse(result.changedStatus());
    }
}

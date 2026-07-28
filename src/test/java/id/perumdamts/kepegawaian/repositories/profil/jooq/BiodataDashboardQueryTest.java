package id.perumdamts.kepegawaian.repositories.profil.jooq;

import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse;
import id.perumdamts.kepegawaian.dto.profil.biodata.BiodataDashboardResponse.PendidikanDashboard;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link BiodataDashboardQuery#mapRow(org.jooq.Record)}.
 * Uses JOOQ mocking (DSL.using) — no database required.
 */
class BiodataDashboardQueryTest {

    private final DSLContext dsl = DSL.using(SQLDialect.MARIADB);

    private static final Field<String> NIK = DSL.field("nik", String.class);
    private static final Field<String> NAMA = DSL.field("nama", String.class);
    private static final Field<Byte> JENIS_KELAMIN = DSL.field("jenis_kelamin", Byte.class);
    private static final Field<String> TEMPAT_LAHIR = DSL.field("tempat_lahir", String.class);
    private static final Field<LocalDate> TANGGAL_LAHIR = DSL.field("tanggal_lahir", LocalDate.class);
    private static final Field<Byte> AGAMA = DSL.field("agama", Byte.class);
    private static final Field<Byte> STATUS_KAWIN = DSL.field("status_kawin", Byte.class);
    private static final Field<String> ALAMAT = DSL.field("alamat", String.class);
    private static final Field<String> TELP = DSL.field("telp", String.class);
    private static final Field<String> EMAIL = DSL.field("email", String.class);
    private static final Field<String> KODE_PAJAK = DSL.field("kode", String.class);
    private static final Field<String> IBU_KANDUNG = DSL.field("ibu_kandung", String.class);
    private static final Field<String> TINGKAT = DSL.field("tingkat", String.class);
    private static final Field<String> JURUSAN = DSL.field("jurusan", String.class);
    private static final Field<String> INSTITUSI = DSL.field("institusi", String.class);
    private static final Field<Integer> TAHUN_LULUS = DSL.field("tahun_lulus", Integer.class);
    private static final Field<Boolean> CHANGED_STATUS = DSL.field("changed_status", Boolean.class);

    private Record newRow() {
        return dsl.newRecord(NIK, NAMA, JENIS_KELAMIN, TEMPAT_LAHIR, TANGGAL_LAHIR,
                AGAMA, STATUS_KAWIN, ALAMAT, TELP, EMAIL, KODE_PAJAK, IBU_KANDUNG,
                TINGKAT, JURUSAN, INSTITUSI, TAHUN_LULUS, CHANGED_STATUS);
    }

    private BiodataDashboardResponse map(Record r) {
        return BiodataDashboardQuery.mapRow(r);
    }

    @Test
    void mapsFullRow() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(NAMA, "Budi Santoso");
        row.set(JENIS_KELAMIN, (byte) 0);       // LAKI_LAKI
        row.set(TEMPAT_LAHIR, "Jakarta");
        row.set(TANGGAL_LAHIR, LocalDate.of(1990, 1, 15));
        row.set(AGAMA, (byte) 1);                 // ISLAM
        row.set(STATUS_KAWIN, (byte) 1);          // KAWIN
        row.set(ALAMAT, "Jl. Merdeka No.1");
        row.set(TELP, "08123456789");
        row.set(EMAIL, "budi@company.com");
        row.set(KODE_PAJAK, "TK0");
        row.set(IBU_KANDUNG, "Siti");
        row.set(TINGKAT, "Sarjana");
        row.set(JURUSAN, "Teknik Informatika");
        row.set(INSTITUSI, "Universitas Indonesia");
        row.set(TAHUN_LULUS, 2015);

        row.set(CHANGED_STATUS, true);

        BiodataDashboardResponse d = map(row);

        assertEquals("1234567890", d.nik());
        assertEquals("Budi Santoso", d.nama());
        assertEquals("Laki-Laki", d.jenisKelamin());
        assertEquals("Jakarta", d.tempatLahir());
        assertEquals(LocalDate.of(1990, 1, 15), d.tanggalLahir());
        assertEquals("ISLAM", d.agama());
        assertEquals("KAWIN", d.statusKawin());
        assertEquals("Jl. Merdeka No.1", d.alamat());
        assertEquals("08123456789", d.noTelp());
        assertEquals("budi@company.com", d.email());
        assertEquals("TK0", d.kodePajak());
        assertEquals("Siti", d.ibuKandung());
        assertTrue(d.changedStatus());

        assertNotNull(d.detailPendidikanTerakhir());
        assertEquals("Sarjana", d.detailPendidikanTerakhir().tingkat());
        assertEquals("Teknik Informatika", d.detailPendidikanTerakhir().jurusan());
        assertEquals("Universitas Indonesia", d.detailPendidikanTerakhir().institusi());
        assertEquals(2015, d.detailPendidikanTerakhir().tahunLulus());
    }

    @Test
    void mapsNullEnumsToNull() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(JENIS_KELAMIN, (Byte) null);
        row.set(AGAMA, (Byte) null);
        row.set(STATUS_KAWIN, (Byte) null);

        BiodataDashboardResponse d = map(row);

        assertNull(d.jenisKelamin());
        assertNull(d.agama());
        assertNull(d.statusKawin());
    }

    @Test
    void mapsPerempuanGender() {
        Record row = newRow();
        row.set(NIK, "0987654321");
        row.set(JENIS_KELAMIN, (byte) 1);   // PEREMPUAN

        BiodataDashboardResponse d = map(row);

        assertEquals("Perempuan", d.jenisKelamin());
    }

    @Test
    void mapsNullPendidikanToNull() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(TINGKAT, (String) null);
        row.set(JURUSAN, (String) null);
        row.set(INSTITUSI, (String) null);
        row.set(TAHUN_LULUS, (Integer) null);

        BiodataDashboardResponse d = map(row);

        assertNull(d.detailPendidikanTerakhir(),
                "detailPendidikanTerakhir must be null when all pendidikan fields are null");
    }

    @Test
    void mapsPartialPendidikan() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(TINGKAT, "SMA");
        row.set(JURUSAN, (String) null);
        row.set(INSTITUSI, (String) null);
        row.set(TAHUN_LULUS, (Integer) null);

        BiodataDashboardResponse d = map(row);

        assertNotNull(d.detailPendidikanTerakhir(),
                "detailPendidikanTerakhir must not be null when tingkat is present");
        assertEquals("SMA", d.detailPendidikanTerakhir().tingkat());
        assertNull(d.detailPendidikanTerakhir().jurusan());
        assertNull(d.detailPendidikanTerakhir().institusi());
        assertNull(d.detailPendidikanTerakhir().tahunLulus());
    }

    @Test
    void mapsNullKodePajak() {
        Record row = newRow();
        row.set(NIK, "1234567890");
        row.set(KODE_PAJAK, (String) null);

        BiodataDashboardResponse d = map(row);

        assertNull(d.kodePajak(),
                "kodePajak must be null when GAJI_PENDAPATAN_NON_PAJAK is LEFT JOINed and absent");
    }

    @Test
    void mapsRekapEnumOrdinalBoundaries() {
        Record row = newRow();
        row.set(NIK, "999");
        row.set(JENIS_KELAMIN, (byte) 0);       // LAKI_LAKI
        row.set(AGAMA, (byte) 0);                // TIDAK_TAHU (ordinal 0)
        row.set(STATUS_KAWIN, (byte) 0);         // BELUM_KAWIN

        BiodataDashboardResponse d = map(row);

        assertEquals("Laki-Laki", d.jenisKelamin());
        assertEquals("TIDAK_TAHU", d.agama());
        assertEquals("BELUM_KAWIN", d.statusKawin());
    }
}
